package com.nibdev.otrtav2.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.service.IrDataCompat;
import com.nibdev.otrtav2.service.OTRTAService;

public class ActivityAllOff extends FragmentActivity {

	private TextView mTvStatus;
	private ProgressBar mPbSend;
	private Button mBtCancel;

	private LoadCodesTask mLoadCodeTask;
	private SendCodesTask mSendCodeTask;
	private boolean mServiceBound;
	private OTRTAService mService;
	private DuplicateFilter mDataFilter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alloff);

		setTitle("All-off!");

		mTvStatus = (TextView)findViewById(R.id.tv_status);
		mPbSend = (ProgressBar)findViewById(R.id.pb_sending);
		mPbSend.setKeepScreenOn(true);
		mBtCancel = (Button)findViewById(R.id.bt_cancel);
		mBtCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Intent bindServiceIntent = new Intent(getApplicationContext(), OTRTAService.class);
		getApplicationContext().bindService(bindServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}



	private void onServiceConnected(OTRTAService service){
		mServiceBound = true;
		mService = service;
		mLoadCodeTask = new LoadCodesTask();
		mLoadCodeTask.execute();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mLoadCodeTask != null){
			mLoadCodeTask.cancel(false);
		}
		if (mSendCodeTask != null){
			mSendCodeTask.cancel(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mServiceBound) {
			getApplicationContext().unbindService(mConnection);
			mServiceBound = false;
		}
	}


	private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			OTRTAService.IRSendBinder binder = (OTRTAService.IRSendBinder) service;
			OTRTAService otrtaservice = (OTRTAService) binder.getService();

			ActivityAllOff.this.onServiceConnected(otrtaservice);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceBound = false;
		}
	};


	private class SendCodesTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected void onPreExecute() {
			mPbSend.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mTvStatus.setText(String.format("sending %d / %d ..", values[1], values[0]));
			mPbSend.setMax(values[0]);
			mPbSend.setProgress(values[1]);
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			float sleepFactor = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(ActivityAllOff.this).getString(getString(R.string.pref_alloff_delayfactor), "1"));
						
			for (int i = 0; i < mDataFilter.getFiltered().size(); i++){
				if (isCancelled()) return null;

				int sleepTimeOrig = mDataFilter.getFiltered().get(i).sleepTime();
				//calculating sleep time with pref Filter
				int newSleep = Math.round(((float)sleepTimeOrig)*sleepFactor);
				mService.getIrSender().sendCodeAndSleep(mDataFilter.getFiltered().get(i), newSleep);
				publishProgress(mDataFilter.getFiltered().size(), i);

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ActivityAllOff.this.finish();
		}
	}




	private class LoadCodesTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			mPbSend.setVisibility(View.INVISIBLE);
			mTvStatus.setText("Loading..");
		}

		@Override
		protected Void doInBackground(Void... params) {
			//load filters from prefs
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivityAllOff.this);
			try{
				String framediff = prefs.getString(getString(R.string.pref_alloff_framediff), "20");
				String freqdiff = prefs.getString(getString(R.string.pref_alloff_freqdiff), "200");
				IrDataCompat.FRAME_COMP = Integer.parseInt(framediff);
				IrDataCompat.FREQ_COMP = Integer.parseInt(freqdiff);
			}catch (Exception ex){}

			//load & filter data
			mDataFilter = new DuplicateFilter(mService.getLocalDb().getCodesByCodeTypeId(1));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!isCancelled()){
				mSendCodeTask = new SendCodesTask();
				mSendCodeTask.execute();
			}
		}
	}


	public static class DuplicateFilter{

		private List<IrDataCompat> mFilteredCodes;

		public DuplicateFilter(List<Code> codes){
			mFilteredCodes = new ArrayList<IrDataCompat>();
			for (Code c : codes){
				IrDataCompat irData = c.getData();
				if (!mFilteredCodes.contains(irData)){
					mFilteredCodes.add(irData);
				}
			}
		}

		public List<IrDataCompat> getFiltered() {
			return mFilteredCodes;
		}
	}
}
