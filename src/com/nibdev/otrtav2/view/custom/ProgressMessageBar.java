package com.nibdev.otrtav2.view.custom;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.database.DBRemote;
import com.nibdev.otrtav2.model.database.DBRemote.SyncStatusCallbacks;

public class ProgressMessageBar implements SyncStatusCallbacks {

	private View mContainer;
	private TextView mTextView;
	private ProgressBar mPb;
	private ImageView mButton;


	public ProgressMessageBar(Activity activity) {
		ViewGroup contianer = (ViewGroup) activity.findViewById(android.R.id.content);
		View v = activity.getLayoutInflater().inflate(R.layout.messagebar_syncprogress, contianer);
		init(v);
		DBRemote.getInstance().addSyncListener(this);
	}

	private void init(View v) {
		mContainer = v.findViewById(R.id.ll_mb_syncprogress);
		mContainer.setVisibility(View.GONE);
		mTextView = (TextView) v.findViewById(R.id.tv_status);
		mPb = (ProgressBar) v.findViewById(R.id.pb_progress);

		mButton = (ImageView) v.findViewById(R.id.iv_cancel);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBRemote.getInstance().cancelFullSyncThread();
			}
		});
		
	}

	private void show(){
		mContainer.setVisibility(View.VISIBLE);
	}

	private void hide(int delay){
		mContainer.postDelayed(new Runnable() {
			@Override
			public void run() {
				mContainer.setVisibility(View.GONE);
			}
		}, delay);

	}


	public void onStop(){
		DBRemote.getInstance().removeSyncListener(this);
		((ViewGroup)mContainer.getParent()).removeView(mContainer);
	}

	@Override
	public void onSyncStatusUpdate(RemoteSyncState state, int total, int progress, String info) {
		if (state == RemoteSyncState.STARTED){
			mTextView.setText("Started..");
			show();
		}else if (state == RemoteSyncState.RUNNING){
			mTextView.setText(info);
			if (total < 0){
				mPb.setMax(100);
				mPb.setProgress(0);
				mPb.setIndeterminate(true);
			}else{
				mPb.setIndeterminate(false);
				mPb.setMax(total);
				mPb.setProgress(progress);
			}
			show();

		}else if (state == RemoteSyncState.FINISHED){
			mTextView.setText("Finished");
			mPb.setMax(100);
			mPb.setProgress(0);
			mPb.setIndeterminate(true);
			hide(1000);
		}else if (state == RemoteSyncState.ERROR){

			mPb.setIndeterminate(true);
			mPb.setMax(100);
			mPb.setProgress(100);
			hide(5000);
		}
	}

}
