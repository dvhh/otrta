package com.nibdev.otrtav2.view.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.htc.htcircontrol.HtcIrData;
import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.activities.ActivityMain;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.model.database.DBRemote;
import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.model.jdata.CodeAllocation;
import com.nibdev.otrtav2.model.jdata.Model;
import com.nibdev.otrtav2.model.jdata.Vendor;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.service.irinterfaces.IrFace.OnCodeLearnedListener;
import com.nibdev.otrtav2.view.adapters.AutoCompleteArrayAdapter;
import com.nibdev.otrtav2.view.custom.HoloProgress;

public class FragmentLearn extends Fragment implements OnCodeLearnedListener {



	//Data
	private HtcIrData mLearnedCode;

	//Tasks
	private LearnCountdownTask mCountdownTask;
	private UploadDataTask mUploadTask;

	//Views
	private LinearLayout mLlUpload;
	private CheckBox mCbAutolearn;
	private HoloProgress mHoloProgress;
	private TextView mTvStatus;
	private Button mBtTest;
	private Button mBtLearn;
	private Button mBtUpload;
	private AutoCompleteTextView mActvVendor;
	private AutoCompleteTextView mActvModel;
	private Spinner mSpnDeviceType;
	private Spinner mSpnCodeType;
	private PopupWindow mPopup;
	private Drawable mErrorDrawable;
	private ProgressDialog mUploadProgressDialog;


	//DB Data
	private TreeMap<String, Integer> mVendorNames;
	private TreeMap<String, Integer> mDeviceTypeNames;
	private TreeMap<String, Integer> mCodeTypeNames;
	private TreeMap<String, Integer> mModelNames;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_learn, null);

		mLlUpload = (LinearLayout)v.findViewById(R.id.ll_upload);
		mHoloProgress = (HoloProgress)v.findViewById(R.id.hpb_learn);
		mTvStatus = (TextView)v.findViewById(R.id.tv_status);
		mCbAutolearn = (CheckBox)v.findViewById(R.id.cb_autolearn);
		mBtTest = (Button)v.findViewById(R.id.bt_test);
		mBtLearn = (Button)v.findViewById(R.id.bt_learn);
		mBtUpload = (Button)v.findViewById(R.id.bt_upload);
		mActvVendor = (AutoCompleteTextView)v.findViewById(R.id.actv_vendor);
		mActvModel = (AutoCompleteTextView)v.findViewById(R.id.actv_model);
		mSpnDeviceType = (Spinner)v.findViewById(R.id.spn_devtype);
		mSpnCodeType = (Spinner)v.findViewById(R.id.spn_codetype);

		initViews();

		return v;

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mUploadProgressDialog != null) mUploadProgressDialog.dismiss();
		OTRTAService.getInstance().getIrSender().setOnCodeLearnedListener(null);
	}


	private void initViews(){
		//hide uploadfields, progress,
		mLlUpload.setVisibility(View.INVISIBLE);
		mHoloProgress.setVisibility(View.INVISIBLE);

		//set empty status
		mTvStatus.setText("");

		//disable test & upload button
		mBtTest.setEnabled(false);
		mBtUpload.setEnabled(false);

		//set listeners
		mBtTest.setOnClickListener(mOnButtonListener);
		mBtLearn.setOnClickListener(mOnButtonListener);
		mBtUpload.setOnClickListener(mOnButtonListener);

		mActvVendor.addTextChangedListener(mOnNonEmptyTextWachter);
		mActvModel.addTextChangedListener(mOnNonEmptyTextWachter);

		mActvVendor.setOnItemClickListener(mOnVendorClickListener);
		mActvModel.setOnItemClickListener(mOnModelClickListener);
		mSpnDeviceType.setOnItemSelectedListener(mOnDeviceTypeClickListener);

		mErrorDrawable = getResources().getDrawable(R.drawable.indicator_input_error);
		int w = mErrorDrawable.getIntrinsicWidth();
		int h = mErrorDrawable.getIntrinsicHeight();
		mErrorDrawable.setBounds(0, 0, w, h);

		if (mDeviceTypeNames == null || mVendorNames == null || mDeviceTypeNames == null || mCodeTypeNames == null){
			LoadDatabaseTask ldt = new LoadDatabaseTask();
			ldt.execute();
		}else{
			mActvModel.setAdapter(new AutoCompleteArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(mModelNames.keySet())));
			mActvVendor.setAdapter(new AutoCompleteArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(mVendorNames.keySet())));
			mSpnCodeType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>(mCodeTypeNames.keySet())));	
			mSpnDeviceType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>(mDeviceTypeNames.keySet())));
			loadDataFromLastSession();
		}
	}


	private void startLearning() {
		mLearnedCode = null;

		mBtLearn.setEnabled(false);
		mBtTest.setEnabled(false);
		mBtUpload.setEnabled(false);
		mTvStatus.setText("");
		mTvStatus.setBackgroundColor(0x00000000);

		mLlUpload.setVisibility(View.INVISIBLE);

		OTRTAService.getInstance().getIrSender().setOnCodeLearnedListener(this);
		int learnTimeout = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getResources().getString(R.string.pref_learn_timeout), "10"));
		OTRTAService.getInstance().getIrSender().learnIRCmd(learnTimeout);
		mCountdownTask = new LearnCountdownTask();
		mCountdownTask.execute(learnTimeout);
	}

	private void checkStartUpload() {
		//check Vendor + Model != ""
		if (mActvVendor.getText().toString().isEmpty()){
			showEmptyStringError(mActvVendor, "Please specifiy a Vendor");
			return;
		}
		if (mActvModel.getText().toString().isEmpty()){
			showEmptyStringError(mActvModel, "Please specifiy a Model");
			return;
		}

		//save last selected model / venodr / devtype / keytype in prefsettings
		SaveUploadData sud = createDataFromInputfields();
		saveDataForNextSession(sud);

		//hide buttons during upload
		mBtLearn.setEnabled(false);
		mBtTest.setEnabled(false);
		mBtUpload.setEnabled(false);

		//start upload task
		mUploadTask = new UploadDataTask();
		mUploadTask.execute(sud);
	}

	private SaveUploadData createDataFromInputfields(){
		SaveUploadData sud = new SaveUploadData();

		int dtId = mDeviceTypeNames.get(new ArrayList<String>(mDeviceTypeNames.keySet()).get(mSpnDeviceType.getSelectedItemPosition()));

		String ctName = (String) mSpnCodeType.getSelectedItem();
		int ctId = mCodeTypeNames.get(ctName);

		Vendor v = new Vendor(mActvVendor.getText().toString(), 0);
		Model m = new Model(mActvModel.getText().toString(), 0, 0, dtId);

		sud.vend = v;
		sud.mod = m;
		sud.dtId = dtId;
		sud.ctId = ctId;
		sud.codedata = Code.fromHTCData(0, mLearnedCode);
		sud.ca = new CodeAllocation(0, sud.codedata.getID(), m.getID(), ctId);
		return sud;
	}


	@Override
	public void onCodeLearned(HtcIrData data, String message) {
		OTRTAService.getInstance().getIrSender().setOnCodeLearnedListener(null);
		mBtLearn.setEnabled(true);
		mHoloProgress.setVisibility(View.INVISIBLE);

		if (mCountdownTask != null){
			try{
				mCountdownTask.cancel(false);
			}catch (Exception ignore) {}
		}

		if (data == null){
			mTvStatus.setText(message);
			mTvStatus.setBackgroundColor(0xA0FF0000);
			if (mCbAutolearn.isChecked()){
				startLearning();							
			}else{

			}
		}else{
			mLearnedCode = data;
			switchToUploadView();
		}

	}


	private OnClickListener mOnButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int btId = v.getId();

			if (btId == R.id.bt_learn){
				startLearning();

			}else if (btId == R.id.bt_test){
				if (mLearnedCode != null){
					OTRTAService.getInstance().getIrSender().sendCode(Code.fromHTCData(0, mLearnedCode).getData(), false);
				}

			}else if (btId == R.id.bt_upload){
				checkStartUpload();
			}
		}

	};


	private TextWatcher mOnNonEmptyTextWachter = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() > 0){
				if (mPopup != null) mPopup.dismiss();
				mActvModel.setCompoundDrawables(null, null, null, null);
				mActvVendor.setCompoundDrawables(null, null, null, null);
			}
		}
	};

	private OnItemClickListener mOnVendorClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			hideKeyboard(mActvVendor);
			mActvVendor.clearFocus();

			//clear model Text
			mActvModel.setText("");
		}
	};

	private OnItemClickListener mOnModelClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			hideKeyboard(mActvModel);
			mActvModel.clearFocus();

			//set Vendor to selected vendor
			int modelId = mModelNames.get(arg0.getItemAtPosition(arg2));
			int venId = OTRTAService.getInstance().getLocalDb().getVendorIdByModelId(modelId);
			int vendNameIndex = new ArrayList<Integer>(mVendorNames.values()).indexOf(venId);
			mActvVendor.setText(new ArrayList<String>(mVendorNames.keySet()).get(vendNameIndex));

			//set DevType to selected 
			int devTypeId = OTRTAService.getInstance().getLocalDb().getDeviceTypeForModelId(modelId);
			if (devTypeId > 0){
				mSpnDeviceType.setSelection(new ArrayList<Integer>(mDeviceTypeNames.values()).indexOf((Integer)devTypeId));
			}

		}
	};

	private OnItemSelectedListener mOnDeviceTypeClickListener = new OnItemSelectedListener() {
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			int devTypeId = mDeviceTypeNames.get(arg0.getItemAtPosition(arg2));

			//get CodeTypeIDs for this Device
			Set<Integer> codeTypeIds = OTRTAService.getInstance().getLocalDb().getCodeTypeMappings().get(devTypeId);
			if (codeTypeIds == null){
				mSpnCodeType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item));
			}else{
				//arraylist with all codetypes
				List<Integer> allCodeTypeIds = new ArrayList<Integer>(mCodeTypeNames.values());
				List<String> allCodeTypeNames = new ArrayList<String>(mCodeTypeNames.keySet());

				TreeMap<String, Integer> mFilteredCodeTypes = new TreeMap<String, Integer>();
				for (int oneCtId : allCodeTypeIds){
					if (codeTypeIds.contains(oneCtId)){
						mFilteredCodeTypes.put(allCodeTypeNames.get(allCodeTypeIds.indexOf(oneCtId)), oneCtId);
					}
				}
				mSpnCodeType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>(mFilteredCodeTypes.keySet())));
			}
		}
	};

	private void switchToUploadView() {
		mTvStatus.setText("Learning successful");
		mTvStatus.setBackgroundColor(0x00000000);
		mLlUpload.setVisibility(View.VISIBLE);
		mBtTest.setEnabled(true);
		mBtUpload.setEnabled(true);
	}


	private void hideKeyboard(View v){
		InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}


	private void showEmptyStringError(TextView onView, String text){
		onView.setCompoundDrawables(null, null, mErrorDrawable, null);
		TextView tv = new TextView(getActivity());
		tv.setText(text);
		tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int h = tv.getMeasuredHeight();
		int w = tv.getMeasuredWidth();
		tv.setBackgroundColor(Color.RED);

		if (mPopup != null) mPopup.dismiss();
		mPopup=  new PopupWindow(tv);
		mPopup = new PopupWindow(tv, w, h);
		mPopup.showAsDropDown(onView);
	}




	private void loadDataFromLastSession(){
		SharedPreferences prefs = getActivity().getSharedPreferences(FragmentLearn.class.getSimpleName(), Context.MODE_PRIVATE);
		if (prefs.contains("VENDOR")){
			String savedVendor = prefs.getString("VENDOR", "");
			if (!savedVendor.isEmpty()){
				mActvVendor.setText(savedVendor);
			}
		}

		if (prefs.contains("MODEL")){
			String savedModel = prefs.getString("MODEL", "");
			if (!savedModel.isEmpty()){
				mActvModel.setText(savedModel);
			}
		}

		if (prefs.contains("DEVICETYPE")){
			String savedDevType = prefs.getString("DEVICETYPE", "");
			if (!savedDevType.isEmpty()){
				ArrayList<String> keyList = new ArrayList<String>(mDeviceTypeNames.keySet());
				int position = Math.max(0, keyList.indexOf(savedDevType));
				mSpnDeviceType.setSelection(position);
			}
		}

	}

	private void saveDataForNextSession(SaveUploadData sud){
		Editor e = getActivity().getSharedPreferences(FragmentLearn.class.getSimpleName(), Context.MODE_PRIVATE).edit();
		e.putString("VENDOR", sud.vend.getName());
		e.putString("MODEL", sud.mod.getName());
		e.putString("DEVICETYPE", OTRTAService.getInstance().getLocalDb().getDeviceTypes().get(sud.dtId));
		e.putString("CODETYPE",OTRTAService.getInstance().getLocalDb().getCodeTypes().get(sud.ctId));
		e.commit();
	}



	private class LoadDatabaseTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			DBLocal localDb = OTRTAService.getInstance().getLocalDb();
			mVendorNames = localDb.getSortedVendors();
			mDeviceTypeNames = localDb.getSortedDeviceTypes();			
			mCodeTypeNames = localDb.getSortedCodeTypes();
			mModelNames = localDb.getSortedModels();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mActvModel.setAdapter(new AutoCompleteArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(mModelNames.keySet())));
			mActvVendor.setAdapter(new AutoCompleteArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(mVendorNames.keySet())));
			mSpnCodeType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>(mCodeTypeNames.keySet())));	
			mSpnDeviceType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>(mDeviceTypeNames.keySet())));
			loadDataFromLastSession();
		}
	}


	private class LearnCountdownTask extends AsyncTask<Integer, Float, Void>{
		private int timeOut;
		private long started;

		@Override
		protected void onPreExecute() {
			mHoloProgress.setVisibility(View.VISIBLE);
			mHoloProgress.invalidate();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			timeOut = (params[0] * 967);
			started = System.currentTimeMillis();

			long currentTime = started;
			while (currentTime < (started + timeOut) && !isCancelled()){
				float degreeFact = 1f- ((currentTime - started) / (float)timeOut);
				publishProgress(degreeFact);
				currentTime = System.currentTimeMillis();
				try{
					Thread.sleep(33);
				}catch (Exception unhandled){}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			float fact = values[0];
			mHoloProgress.setProgress(fact);
			int hsvColor = android.graphics.Color.HSVToColor(new float[]{(float)fact*120f,1f,1f});
			mHoloProgress.changeProgressColor(hsvColor);
		}

	}

	private class UploadDataTask extends AsyncTask<SaveUploadData, Void, String>{

		private SaveUploadData uploadedData;

		@Override
		protected void onPreExecute() {
			mUploadProgressDialog = new ProgressDialog(getActivity());
			mUploadProgressDialog.setCancelable(false);
			mUploadProgressDialog.setCanceledOnTouchOutside(false);
			mUploadProgressDialog.setIndeterminate(true);
			mUploadProgressDialog.setMessage("Uploading..");
			mUploadProgressDialog.show();
		}

		@Override
		protected String doInBackground(SaveUploadData... params) {
			SaveUploadData data = params[0];
			DBRemote rdb = DBRemote.getInstance();

			if (!rdb.checkConnection()){
				return "No connection";
			}

			if (data == null){
				return "No recorded data";
			}	

			try{

				//vendor id
				int vendorId = data.vend.getID();
				if (vendorId == 0){
					//create Vendor in Database
					vendorId = rdb.uploadNewVendor(data.vend);
					if (vendorId <= 0){
						return "Upload failed";
					}else{
						data.vend.setID(vendorId);
						data.mod.setVendorID(vendorId);
					}
				}


				//model id
				int modelId = data.mod.getID();
				if (modelId == 0){
					//create model in Database
					modelId = rdb.uploadNewModel(data.mod);
					if (modelId <= 0){
						return "Upload failed";
					}else{
						data.mod.setID(modelId);
						data.ca.setModelID(modelId);
					}
				}

				//code
				int codeId = data.codedata.getID();
				if (codeId == 0){
					codeId = rdb.uploadNewCode(data.codedata);
					if (codeId <= 0){
						return "Upload failed";
					}else{
						data.codedata.setID(codeId);
						data.ca.setCodeID(codeId);
					}
				}

				//allocation
				int caId = data.ca.getID();
				if (caId == 0){
					caId = rdb.uploadNewCodeAllocation(data.ca);
					if (caId <= 0){
						return "Upload failed";
					}else{
						data.ca.setID(caId);
					}
				}

				uploadedData = data;

				return null;

			}catch (Exception ex){
				ActivityMain.handleException(ex, true);
				return "Unknown Error";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			//save data to localDB!
			if (mUploadProgressDialog != null) mUploadProgressDialog.dismiss();

			//Ok:
			if (result == null && uploadedData != null){
				//save to local db
				OTRTAService.getInstance().getLocalDb().checkUploadResult(uploadedData);
				mLlUpload.setVisibility(View.INVISIBLE);
				mTvStatus.setText("Upload okay");
				mBtLearn.setEnabled(true);

			}else{
				mTvStatus.setText(result);
				mTvStatus.setBackgroundColor(0xA0FF0000);
				mBtUpload.setEnabled(true);
				mBtTest.setEnabled(true);
				mBtLearn.setEnabled(true);
			}

		}
	}

	public static class SaveUploadData{
		public Vendor vend;
		public Model mod;
		public Code codedata;
		public CodeAllocation ca;
		public int ctId;
		public int dtId;
	}



}
