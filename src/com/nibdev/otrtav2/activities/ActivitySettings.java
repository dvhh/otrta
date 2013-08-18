package com.nibdev.otrtav2.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.fragments.FragmentSettings;

public class ActivitySettings extends Activity{
	
	private OTRTAService mService;
	private boolean mServiceBound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Settings");
		
		Intent i = new Intent(getApplicationContext(), OTRTAService.class);
		getApplicationContext().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mServiceBound){
			getApplicationContext().unbindService(mConnection);
			mServiceBound = false;
		}
	}

	private void onServiceConnected(OTRTAService service){
		mServiceBound = true;
		mService = service;
		FragmentSettings settFrag = new FragmentSettings();
		settFrag.setService(mService);
		getFragmentManager().beginTransaction().replace(android.R.id.content, settFrag).commit();
	}
	

	
	private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			OTRTAService.IRSendBinder binder = (OTRTAService.IRSendBinder) service;
			OTRTAService otrtaservice = (OTRTAService) binder.getService();

			ActivitySettings.this.onServiceConnected(otrtaservice);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceBound = false;
		}
	};
}
