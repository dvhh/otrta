package com.nibdev.otrtav2.activities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.adapters.CodeSelectSpinnerAdapter;
import com.nibdev.otrtav2.view.adapters.ModelSelectSpinnerAdapter;
import com.nibdev.otrtav2.view.adapters.ScriptSelectSpinnerAdapter;
import com.nibdev.otrtav2.view.adapters.VendorSelectSpinnerAdapter;
import com.nibdev.otrtav2.widgets.SingleWidgetConfiguration;
import com.nibdev.otrtav2.widgets.WidgetSingleProvider;

public class ActivityWidgetSingleSettings extends FragmentActivity {

	//View
	private Spinner mSpnType;
	private Spinner mSpnSel2;
	private Spinner mSpnSel3;
	private Spinner mSpnSel4;
	private Button mBtOkay;

	private VendorSelectSpinnerAdapter mVendorAdapter;

	private List<String> mTypeNames = Arrays.asList(new String[]{"Code", "Script", "Layout", "All-off"});
	private List<Integer> mTypes = Arrays.asList(new Integer[]{SingleWidgetConfiguration.TYPE_CODE, SingleWidgetConfiguration.TYPE_SCRIPT, SingleWidgetConfiguration.TYPE_LAYOUTSTART, SingleWidgetConfiguration.TYPE_ALLOFF});


	//Service / DB Data
	private OTRTAService mService;
	private boolean mServiceBound;

	//Widget Data
	private int mAppWidgetId;
	private SharedPreferences mWidgetPrefs;
	private SingleWidgetConfiguration mWidgetConfig;

	private LastConfig mRestoreData;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widgetsingleconfig);
		
		setTitle("Config Widget");

		mBtOkay = (Button)findViewById(R.id.bt_ok);
		mSpnType = (Spinner)findViewById(R.id.spn_type);
		mSpnSel2 = (Spinner)findViewById(R.id.spn_select2);
		mSpnSel3 = (Spinner)findViewById(R.id.spn_select3);
		mSpnSel4 = (Spinner)findViewById(R.id.spn_select4);

		mBtOkay.setOnClickListener(mOnOkClickListener);
		mBtOkay.setEnabled(false);
		initLoadData();

	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent bindServiceIntent = new Intent(getApplicationContext(), OTRTAService.class);
		getApplicationContext().bindService(bindServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mServiceBound){
			getApplicationContext().unbindService(mConnection);
			mServiceBound = false;
		}
	}

	private void onServiceConnected(OTRTAService service){
		mServiceBound = true;
		mService = service;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				initView();
			}
		});

	}


	private void initLoadData(){
		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		//get shared preferences
		Gson gson = new Gson();
		mWidgetPrefs = getSharedPreferences(ActivityWidgetSingleSettings.class.getSimpleName(), Context.MODE_PRIVATE);

		//check if there is a config for this widget:
		if (mWidgetPrefs.contains("WIDGET_" + mAppWidgetId)){
			mWidgetConfig = gson.fromJson(mWidgetPrefs.getString("WIDGET_" + mAppWidgetId, ""), SingleWidgetConfiguration.class);
			if (mWidgetConfig.getConfigText() != null){
				mRestoreData = gson.fromJson(mWidgetConfig.getConfigText(), LastConfig.class);
			}
		}else{
			mWidgetConfig = new SingleWidgetConfiguration();
			if (mWidgetPrefs.contains("LAST_CONFIG")){
				mRestoreData = gson.fromJson(mWidgetPrefs.getString("LAST_CONFIG", ""), LastConfig.class);
			}
		}
		mWidgetConfig.setWidgetID(mAppWidgetId);
	}


	private void initView(){

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1_bigger, mTypeNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpnType.setAdapter(adapter);
		mSpnType.setOnItemSelectedListener(mOnTypeSelectedListener);

		mVendorAdapter = new VendorSelectSpinnerAdapter(mService.getLocalDb());
		mVendorAdapter.setTopString("Select a vendor");

		if (mRestoreData != null && mRestoreData.sel1Id > -1){
			mSpnType.setSelection(mRestoreData.sel1Id.intValue());
			mRestoreData.sel1Id = -1l;
		}else{
			mSpnType.setSelection(0);
		}

	}






	private OnItemSelectedListener mOnTypeSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			int type = mTypes.get(arg2);
			mWidgetConfig.setType(type);
			if (type == SingleWidgetConfiguration.TYPE_CODE){
				mSpnSel2.setVisibility(View.VISIBLE);
				mSpnSel3.setVisibility(View.VISIBLE);
				mSpnSel4.setVisibility(View.VISIBLE);

				mSpnSel2.setAdapter(mVendorAdapter);
				mSpnSel2.setOnItemSelectedListener(mOnVendorSelectedListener);
				//restore pos
				if (mRestoreData != null && mRestoreData.sel2Id > -1){
					for (int i = 0; i < mVendorAdapter.getCount(); i++){
						if (mVendorAdapter.getItemId(i) == mRestoreData.sel2Id){
							mSpnSel2.setSelection(i);
							break;
						}
					}
					mRestoreData.sel2Id = -1l;
				}
				mBtOkay.setEnabled(false);

			}else if (type == SingleWidgetConfiguration.TYPE_SCRIPT){
				mSpnSel2.setVisibility(View.VISIBLE);
				mSpnSel3.setVisibility(View.INVISIBLE);
				mSpnSel4.setVisibility(View.INVISIBLE);

				ScriptSelectSpinnerAdapter adapt = new ScriptSelectSpinnerAdapter(mService.getLocalDb());
				adapt.setTopString("Select a script");
				mSpnSel2.setAdapter(adapt);
				mSpnSel2.setOnItemSelectedListener(mOnScriptSelectedListener);

				mBtOkay.setEnabled(false);

			}else if (type == SingleWidgetConfiguration.TYPE_LAYOUTSTART){
				mSpnSel2.setVisibility(View.VISIBLE);
				mSpnSel3.setVisibility(View.INVISIBLE);
				mSpnSel4.setVisibility(View.INVISIBLE);

				mBtOkay.setEnabled(false);

			}else if (type == SingleWidgetConfiguration.TYPE_ALLOFF){
				mWidgetConfig.setTopText("");
				mWidgetConfig.setBottomText("All-off!");
				mWidgetConfig.setAllocationID(-1);

				mSpnSel2.setVisibility(View.INVISIBLE);
				mSpnSel3.setVisibility(View.INVISIBLE);
				mSpnSel4.setVisibility(View.INVISIBLE);

				mBtOkay.setEnabled(true);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}

	};

	private OnItemSelectedListener mOnVendorSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Long vendId = arg3;
			if (vendId > 0){
				ModelSelectSpinnerAdapter adapt = new ModelSelectSpinnerAdapter(mService.getLocalDb(), vendId.intValue());
				adapt.setTopString("Select a model");
				mSpnSel3.setAdapter(adapt);			
				mSpnSel3.setOnItemSelectedListener(mOnModelSelectedListener);
				mSpnSel3.setVisibility(View.VISIBLE);

				//restore pos
				if (mRestoreData != null && mRestoreData.sel3Id > -1){
					for (int i = 0; i < adapt.getCount(); i++){
						if (adapt.getItemId(i) == mRestoreData.sel3Id){
							mSpnSel3.setSelection(i);
							break;
						}
					}
					mRestoreData.sel3Id = -1l;
				}			
			}else{
				onNothingSelected(arg0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			mSpnSel3.setVisibility(View.INVISIBLE);
			mSpnSel4.setVisibility(View.INVISIBLE);
		}

	};

	private OnItemSelectedListener mOnModelSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Long modelId = arg3;
			if (modelId > 0){
				@SuppressWarnings("unchecked")
				Map<String, Object> modelData = (Map<String, Object>) arg0.getItemAtPosition(arg2);
				mWidgetConfig.setTopText((String) modelData.get(DBLocal.COLUMN_NAME));

				CodeSelectSpinnerAdapter adapt = new CodeSelectSpinnerAdapter(mService.getLocalDb(), modelId.intValue());
				adapt.setTopString("Select a code");
				mSpnSel4.setAdapter(adapt);
				mSpnSel4.setOnItemSelectedListener(mOnCodeSelectedListener);
				mSpnSel4.setVisibility(View.VISIBLE);
			}else{
				onNothingSelected(arg0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			mSpnSel4.setVisibility(View.INVISIBLE);
		}

	};


	private OnItemSelectedListener mOnCodeSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Long codeId = arg3;
			if (codeId > 0){
				@SuppressWarnings("unchecked")
				Map<String, Object> codeData = (Map<String, Object>) arg0.getItemAtPosition(arg2);
				mWidgetConfig.setBottomText((String) codeData.get("NAME"));

				mWidgetConfig.setAllocationID(codeId.intValue());
				mBtOkay.setEnabled(true);
			}else{
				onNothingSelected(arg0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			mWidgetConfig.setAllocationID(-1);
			mBtOkay.setEnabled(false);
		}

	};
	
	private OnItemSelectedListener mOnScriptSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Long scriptId = arg3;
			if (scriptId > 0){
				mWidgetConfig.setBottomText(mService.getLocalDb().getScriptNameById(scriptId.intValue()));
				mWidgetConfig.setAllocationID(scriptId.intValue());
				mBtOkay.setEnabled(true);
			}else{
				onNothingSelected(arg0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			mWidgetConfig.setAllocationID(-1);
			mBtOkay.setEnabled(false);
		}

	};





	private OnClickListener mOnOkClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//save settings in widget and last_config
			Gson gson = new Gson();
			String configString = gson.toJson(createLastConfig());
			mWidgetConfig.setConfigText(configString);

			mWidgetPrefs.edit().putString("WIDGET_" + mAppWidgetId, gson.toJson(mWidgetConfig)).commit();
			mWidgetPrefs.edit().putString("LAST_CONFIG", configString).commit();

			Context context = ActivityWidgetSingleSettings.this;
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			WidgetSingleProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}
	};

	private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			OTRTAService.IRSendBinder binder = (OTRTAService.IRSendBinder) service;
			OTRTAService otrtaservice = (OTRTAService) binder.getService();

			ActivityWidgetSingleSettings.this.onServiceConnected(otrtaservice);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceBound = false;
		}
	};


	private LastConfig createLastConfig(){
		LastConfig config = new LastConfig();
		config.sel1Id = ((Integer)mSpnType.getSelectedItemPosition()).longValue();
		if (mSpnType.getSelectedItemPosition() == 0){
			config.sel2Id = mSpnSel2.getAdapter().getItemId(mSpnSel2.getSelectedItemPosition());
			config.sel3Id = mSpnSel3.getAdapter().getItemId(mSpnSel3.getSelectedItemPosition());
		} else if (mSpnType.getSelectedItemPosition() <= 2){
			config.sel2Id = mSpnSel2.getAdapter().getItemId(mSpnSel2.getSelectedItemPosition());
		}
		return config;
	}

	private static class LastConfig{
		public Long sel1Id;
		public Long sel2Id;
		public Long sel3Id;

		public LastConfig(){
			sel1Id = -1l;
			sel2Id = -1l;
			sel3Id = -1l;
		}
	}


}
