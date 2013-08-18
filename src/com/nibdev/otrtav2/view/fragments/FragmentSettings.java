package com.nibdev.otrtav2.view.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.activities.ActivityAllOff.DuplicateFilter;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.database.DBLocalHelper;
import com.nibdev.otrtav2.model.database.DBRemote;
import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.service.IrDataCompat;
import com.nibdev.otrtav2.service.OTRTAService;


public class FragmentSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	private OTRTAService mService;

	private EditTextPreference mEtFrameCompare;
	private EditTextPreference mEtFreqCompare;
	private EditTextPreference mEtDelayFactor;
	
	private CalculateFilterTask mFilterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		mEtFrameCompare = NQ.pref(this, R.string.pref_alloff_framediff, EditTextPreference.class);
		mEtFreqCompare = NQ.pref(this, R.string.pref_alloff_freqdiff, EditTextPreference.class);
		mEtDelayFactor = NQ.pref(this, R.string.pref_alloff_delayfactor, EditTextPreference.class);
		
		setSummaryMinMaxLearnTimeout();
		setSummaryFrameCompare();
		setSummaryFreqCompare();
		setSummaryDelayFactor();

		Preference btPurge = (Preference)findPreference(getString(R.string.pref_db_purge));
		btPurge.setOnPreferenceClickListener(mOnDbPurgeClickListener);

		Preference btExport = (Preference)findPreference(getString(R.string.pref_db_export));
		btExport.setOnPreferenceClickListener(mOnDbExportClickListener);

		Preference btImport = (Preference)findPreference(getString(R.string.pref_db_import));
		btImport.setOnPreferenceClickListener(mOnDbImportClickListener);
	}


	public void setService(OTRTAService service){
		mService = service;
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(getString(R.string.pref_learn_timeout))){
			setSummaryMinMaxLearnTimeout();

		}else if (key.equals(getString(R.string.pref_alloff_framediff))){
			setSummaryFrameCompare();
			mFilterTask = new CalculateFilterTask();
			mFilterTask.execute();

		}else if (key.equals(getString(R.string.pref_alloff_freqdiff))){
			setSummaryFreqCompare();
			mFilterTask = new CalculateFilterTask();
			mFilterTask.execute();
			
		}else if (key.equals(getString(R.string.pref_alloff_delayfactor))){
			setSummaryDelayFactor();
		}
		
	}

	private void setSummaryMinMaxLearnTimeout(){
		EditTextPreference learnTimeoutPref= (EditTextPreference)findPreference(getString(R.string.pref_learn_timeout));
		int timeOut;

		try{
			timeOut = Integer.parseInt(learnTimeoutPref.getText());
		}catch (Exception ex){
			timeOut = 5;
		}

		timeOut = Math.max(timeOut, 1);
		timeOut = Math.min(timeOut, 60);
		learnTimeoutPref.setText(Integer.toString(timeOut));

		String summary = MessageFormat.format("Seconds: {0,number,#}", timeOut);
		learnTimeoutPref.setSummary(summary);
	}

	private void setSummaryFreqCompare(){
		int currentFreqCompare = NQ.etPrefToInt(mEtFreqCompare, 0, 1000, 200);
		String summary = getString(R.string.pref_alloff_freqdiff_summary);
		summary = summary + " [" + currentFreqCompare + "]";
		mEtFreqCompare.setText(Integer.toString(currentFreqCompare));
		mEtFreqCompare.setSummary(summary);
	}

	private void setSummaryFrameCompare(){
		int currentFrameCompare = NQ.etPrefToInt(mEtFrameCompare, 0, 100, 20);
		String summary = getString(R.string.pref_alloff_framediff_summary);
		summary = summary + " [" + currentFrameCompare + "]";
		mEtFrameCompare.setText(Integer.toString(currentFrameCompare));
		mEtFrameCompare.setSummary(summary);
	}
	
	private void setSummaryDelayFactor(){
		float currentFactor = NQ.etPrefToFloat(mEtDelayFactor, 0.1f, 1f, 1f);
		String summary = getString(R.string.pref_alloff_delayfactor_summary);
		summary = summary + " [" + currentFactor + "]";
		mEtDelayFactor.setText(Float.toString(currentFactor));
		mEtDelayFactor.setSummary(summary);
	}

	private OnPreferenceClickListener mOnDbPurgeClickListener = new OnPreferenceClickListener(){
		@Override
		public boolean onPreferenceClick(Preference arg0) { 
			//pruge local data, maybe aks user before, dont start a new sync.
			mService.getLocalDb().purge();
			Toast.makeText(getActivity(), "all data purged", Toast.LENGTH_SHORT).show();
			return true;
		}

	};

	private OnPreferenceClickListener mOnDbExportClickListener = new OnPreferenceClickListener(){
		@Override
		public boolean onPreferenceClick(Preference arg0) { 
			try {
				DBRemote.getInstance().cancelFullSyncThread();

				File dbFile = getActivity().getDatabasePath(DBLocalHelper.DB_NAME);
				if (dbFile.exists()){
					File extDir = Environment.getExternalStorageDirectory();
					File extDb = new File(extDir, dbFile.getName());
					if (extDb.exists()) extDb.delete();

					FileInputStream fis = new FileInputStream(dbFile);
					FileOutputStream fos = new FileOutputStream(new File(extDir, dbFile.getName()));

					byte[] buffer = new byte[1024*64];
					int count = 0;
					while ((count = fis.read(buffer)) > 0){
						fos.write(buffer, 0, count);
					}
					fos.flush();
					fos.close();
					fis.close();

					Toast.makeText(getActivity(), "Export okay", Toast.LENGTH_SHORT).show();

				}
			} catch (Exception e) {
				Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
			}	

			return true;
		}

	};

	private OnPreferenceClickListener mOnDbImportClickListener = new OnPreferenceClickListener(){
		@Override
		public boolean onPreferenceClick(Preference arg0) { 
			try{
				DBRemote.getInstance().cancelFullSyncThread();

				File dbFile = getActivity().getDatabasePath(DBLocalHelper.DB_NAME);
				File extDir = Environment.getExternalStorageDirectory();
				File extDb = new File(extDir, dbFile.getName());
				if (extDb.exists()){
					DBLocal dbl = OTRTAService.getInstance().getLocalDb();
					dbl.onDestroy();

					FileInputStream fis = new FileInputStream(extDb);
					FileOutputStream fos = new FileOutputStream(dbFile);

					byte[] buffer = new byte[1024*64];
					int count = 0;
					while ((count = fis.read(buffer)) > 0){
						fos.write(buffer, 0, count);
					}
					fos.flush();
					fos.close();
					fis.close();


					getActivity().setResult(999);
					getActivity().finish();



				}else{
					Toast.makeText(getActivity(), "No file to import found", Toast.LENGTH_SHORT).show();
				}



			}catch (Exception e) {
				Toast.makeText(getActivity(), "Import failed", Toast.LENGTH_SHORT).show();
			}	

			return true;
		}

	};



	private class CalculateFilterTask extends AsyncTask<Void, Void, Integer[]>{
		private ProgressDialog mCalcProg;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mCalcProg = new ProgressDialog(getActivity());
			mCalcProg.setTitle("Filter");
			mCalcProg.setMessage("Calculation filtered size...");
			mCalcProg.setCancelable(false);
			mCalcProg.show();
		}
		
		@Override
		protected Integer[] doInBackground(Void... params) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			try{
				String framediff = prefs.getString(getString(R.string.pref_alloff_framediff), "20");
				String freqdiff = prefs.getString(getString(R.string.pref_alloff_freqdiff), "200");
				IrDataCompat.FRAME_COMP = Integer.parseInt(framediff);
				IrDataCompat.FREQ_COMP = Integer.parseInt(freqdiff);

			}catch (Exception ex){}

			List<Code> allCodes = mService.getLocalDb().getCodesByCodeTypeId(1);
			int origSize = allCodes.size();
			int newSize = new DuplicateFilter(allCodes).getFiltered().size();
			Integer[] vals = new Integer[]{origSize, newSize};
			
			return vals;
		}
		
		@Override
		protected void onPostExecute(Integer[] result) {
			super.onPostExecute(result);
			mCalcProg.dismiss();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Filtering finished");
			builder.setMessage("Original Size: " + result[0].toString() + "\r\nFiltered Size: " + result[1]);
			builder.setCancelable(true);
			builder.setPositiveButton("Ok", null);
			
			builder.create().show();
			

		}

	}


}
