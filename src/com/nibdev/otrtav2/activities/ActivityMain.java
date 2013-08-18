package com.nibdev.otrtav2.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.model.Statics;
import com.nibdev.otrtav2.model.database.DBLocalHelper;
import com.nibdev.otrtav2.model.database.DBRemote;
import com.nibdev.otrtav2.model.tools.ServiceData;
import com.nibdev.otrtav2.model.tools.Zip;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.adapters.DrawerAdapter;
import com.nibdev.otrtav2.view.custom.ProgressMessageBar;
import com.nibdev.otrtav2.view.fragments.FragmentDB2DeviceTypeList;
import com.nibdev.otrtav2.view.fragments.FragmentLearn;
import com.nibdev.otrtav2.view.fragments.FragmentScriptManager;
import com.nibdev.otrtav2.view.fragments.FragmentUserLayoutManager;
import com.nibdev.otrtav2.view.fragments.FragmentVendorGrid;

public class ActivityMain extends FragmentActivity {

	private boolean mServiceBound;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerAdapter mDrawerAdapter;
	private ProgressMessageBar mSyncProgressBar;
	private boolean mInitDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkCopyMyDb();
		checkUnzipDb2();
		initDrawer();
		Statics.RefContext = getApplicationContext();

		Intent bindServiceIntent = new Intent(getApplicationContext(), OTRTAService.class);
		getApplicationContext().startService(bindServiceIntent);
		getApplicationContext().bindService(bindServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}



	private void checkCopyMyDb() {
		File localDbFile = getDatabasePath(DBLocalHelper.DB_NAME);
		if (localDbFile.exists()) return;

		File localDbPath = new File(localDbFile.getParent());
		if (!localDbPath.exists()) localDbPath.mkdirs();

		try {
			OutputStream os = new FileOutputStream(localDbFile);
			InputStream is = getResources().openRawResource(R.raw.otrta);
			byte[] buffer = new byte[1024];
			int c = 0;

			while ((c = is.read(buffer)) > 0){
				os.write(buffer, 0, c);
			}
			os.flush();
			os.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkUnzipDb2(){
		File savePath = getDatabasePath("db2.sqldb");
		if (savePath.exists()) return;
		Zip.unzipRaw(this, R.raw.db2, savePath.getParentFile(), true);
	}


	private void onServiceConnected(OTRTAService otrtaservice){
		mServiceBound = true;
		
		
		//check Ir blaster
		if (otrtaservice.getIrSender() == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Error");
			builder.setMessage("Sorry, not a supported device, no IR hardware found");
			builder.setCancelable(false);
			builder.setPositiveButton(":(", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog d = builder.create();
			d.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					ActivityMain.this.finish();
				}
			});
			d.show();
			return;
		}


		if (!mInitDone){

			if (!OTRTAService.getInstance().getIrSender().canLearn()){
				mDrawerAdapter.disableLearn();
			}

			mInitDone = true;
			getSupportFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentVendorGrid()).commit();
			if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_startup_sync), false)){
				DBRemote.getInstance().startFullSyncThread();
			}

			if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_startup_updatecheck), false)){
				updateCheck();
			}
		}



	}

	private void initDrawer(){
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList = (ListView)findViewById(R.id.left_drawer);
		mDrawerList.setOnItemClickListener(mOnNavigationClickListener);
		mDrawerAdapter = new DrawerAdapter();
		mDrawerList.setAdapter(mDrawerAdapter);
		mDrawerToggle = new ActionBarDrawerToggle(
				ActivityMain.this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.action_search,
				R.string.action_sync){

			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public void onStart() {
		super.onStart();
		mSyncProgressBar = new ProgressMessageBar(this);

	}

	@Override
	public void onStop() {
		super.onStop();
		mSyncProgressBar.onStop();
		DBRemote.getInstance().cancelFullSyncThread();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mServiceBound) {
			getApplicationContext().unbindService(mConnection);
			mServiceBound = false;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public interface OnBackKeyListener{
		boolean handlesBackKey();
	}
	private OnBackKeyListener mBackKeyListener;
	public void setOnBackKeyListener(OnBackKeyListener listener){
		mBackKeyListener = listener;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mDrawerList)){
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		}
		if (mBackKeyListener != null){
			if (mBackKeyListener.handlesBackKey()){
				return;
			}
		}
		super.onBackPressed();
	}


	private OnItemClickListener mOnNavigationClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			if (mDrawerAdapter.getSelectedPosition() == arg2) return;

			int actionId = ((Long)arg3).intValue();
			if (actionId == DrawerAdapter.ACTION_DATABASE){
				getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getSupportFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentVendorGrid()).commit();
				mDrawerAdapter.setSelectedPosition(arg2);

			}else if (actionId == DrawerAdapter.ACTION_SETTINGS){
				Intent iSett = new Intent(ActivityMain.this, ActivitySettings.class);
				startActivityForResult(iSett, 999);

			}else if (actionId == DrawerAdapter.ACTION_LEARN){
				getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getSupportFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentLearn()).commit();
				mDrawerAdapter.setSelectedPosition(arg2);

			} else if (actionId == DrawerAdapter.ACTION_ALLOFF){
				Intent i = new Intent(ActivityMain.this, ActivityAllOff.class);
				startActivity(i);

			}else if (actionId == DrawerAdapter.ACTION_SCRIPTS){
				getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getSupportFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentScriptManager()).commit();
				mDrawerAdapter.setSelectedPosition(arg2);

			}else if (actionId == DrawerAdapter.ACTION_LAYOUTS){
				getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getSupportFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentUserLayoutManager()).commit();
				mDrawerAdapter.setSelectedPosition(arg2);

			}else if (actionId == DrawerAdapter.ACTION_DB2BROWSER){
				getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				getSupportFragmentManager().beginTransaction().replace(R.id.frame_contet, new FragmentDB2DeviceTypeList()).commit();
				mDrawerAdapter.setSelectedPosition(arg2);

			}

			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}; 

	private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			OTRTAService.IRSendBinder binder = (OTRTAService.IRSendBinder) service;
			OTRTAService otrtaservice = (OTRTAService) binder.getService();
			ActivityMain.this.onServiceConnected(otrtaservice);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceBound = false;
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 999){
			finish();
		}
	};


	public static void handleException(Exception ex, boolean b) {
		ex.printStackTrace();
	}


	private void updateCheck(){
		Thread ucheck = new Thread(){
			@Override
			public void run() {

				try {
					if (!DBRemote.getInstance().checkConnection()) {
						return;
					}

					String ans = ServiceData.HttpGetAnswer(DBRemote.URL_GETAPPVERSION);
					JSONObject j = new JSONObject(ans);
					int vi = j.getInt("CVCV2");
					if (vi > 0) {
						PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
						int versionNumber = pinfo.versionCode;
						if (vi > versionNumber) {
							NQ.onUiThread(new Runnable() {
								@Override
								public void run() {
									showUpdateInfo();
								}
							});
						}
					}

				} catch (Exception ex) {}
			}
		};
		ucheck.start();
	}

	private void showUpdateInfo() {
		final AlertDialog ad = new AlertDialog.Builder(this).create();

		ad.setTitle("Update available");
		ad.setMessage("Download new version from XDA");
		ad.setButton(AlertDialog.BUTTON_POSITIVE, "Go to XDA", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/showthread.php?t=2271113")));
			}
		});
		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",	new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ad.show();
	}




}
