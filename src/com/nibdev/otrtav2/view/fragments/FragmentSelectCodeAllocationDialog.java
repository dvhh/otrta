package com.nibdev.otrtav2.view.fragments;

import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nibdev.otrtav2.R;
import com.nibdev.otrtav2.model.database.DBLocal;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.adapters.CodeSelectSpinnerAdapter;
import com.nibdev.otrtav2.view.adapters.ModelSelectSpinnerAdapter;
import com.nibdev.otrtav2.view.adapters.VendorSelectSpinnerAdapter;

public class FragmentSelectCodeAllocationDialog extends DialogFragment {

	public interface OnSelectedCodeChangedListener{
		public void selectedCodeChanged(int codeAllocationId);
	}
	private OnSelectedCodeChangedListener mListener;
	public void setOnSelectedCodeChangedListener(OnSelectedCodeChangedListener listener){
		mListener = listener;
	}

	private Spinner mSpnVendor;
	private Spinner mSpnModel;
	private Spinner mSpnCodeType;


	//data fields
	private int mSelectedModel;
	private int mSelectedVendor;
	private int mSelectedCodeAllocation;

	private SharedPreferences mLastPrefs;
	private long mRestoringModel = -1;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_selectcode_dialog, null);

		mLastPrefs = getActivity().getSharedPreferences(FragmentSelectCodeAllocationDialog.class.getSimpleName(), Context.MODE_PRIVATE);

		mSpnVendor = (Spinner)v.findViewById(R.id.spn_vendor);
		mSpnModel = (Spinner)v.findViewById(R.id.spn_model);
		mSpnCodeType = (Spinner)v.findViewById(R.id.spn_codetype);

		mSpnVendor.setOnItemSelectedListener(mOnVendorSelectedListener);
		mSpnModel.setOnItemSelectedListener(mOnModelSelectedListener);
		mSpnCodeType.setOnItemSelectedListener(mOnCodeTypeSelectedListener);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select a Code");
		builder.setPositiveButton("Select", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.setView(v);

		mSpnVendor.setAdapter(new VendorSelectSpinnerAdapter(OTRTAService.getInstance().getLocalDb()));
		((VendorSelectSpinnerAdapter)mSpnVendor.getAdapter()).setTopString("Select a vendor");

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog)getDialog();
		d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(mOnSetClickListener);
		d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		loadDataFromArguemntsOrLastSession();
	}


	private View.OnClickListener mOnSetClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			saveDataForNextSession();
			if (mListener != null) mListener.selectedCodeChanged(mSelectedCodeAllocation);
			dismiss();
		}
	};
	private OnItemSelectedListener mOnCodeTypeSelectedListener = new OnItemSelectedListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (arg3 > -1){
				mSelectedCodeAllocation = ((Long) ((Map<String,Object>)arg0.getItemAtPosition(arg2)).get(DBLocal.COLUMN_ID)).intValue();
			}else{
				mSelectedCodeAllocation = -1;
			}
			AlertDialog d = (AlertDialog)getDialog();
			d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(mSelectedCodeAllocation > 0);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};

	private OnItemSelectedListener mOnModelSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mSelectedModel = (int)arg3;
			if (mSelectedModel > 0){
				mSpnCodeType.setAdapter(new CodeSelectSpinnerAdapter(OTRTAService.getInstance().getLocalDb(), mSelectedModel));
				((CodeSelectSpinnerAdapter)mSpnCodeType.getAdapter()).setTopString("Select a code");
			}else{
				mSpnCodeType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1));
				AlertDialog d = (AlertDialog)getDialog();
				d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}

	};

	private OnItemSelectedListener mOnVendorSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mSelectedVendor = (int)arg3;
			if (mSelectedVendor > 0){
				mSpnModel.setAdapter(new ModelSelectSpinnerAdapter(OTRTAService.getInstance().getLocalDb(), mSelectedVendor));
				((ModelSelectSpinnerAdapter)mSpnModel.getAdapter()).setTopString("Select a model");

				if (mRestoringModel > -1){
					for (int i = 0; i < mSpnModel.getAdapter().getCount(); i++){
						if (mSpnModel.getAdapter().getItemId(i) == mRestoringModel){
							mSpnModel.setSelection(i);
							break;
						}
					}
					mRestoringModel = -1;
				}


			}else{
				mSpnModel.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1));
				AlertDialog d = (AlertDialog)getDialog();
				d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}

		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}

	};

	private void loadDataFromArguemntsOrLastSession(){

		if (getArguments() != null){
			int caId = getArguments().getInt("CAID", -1);
			if (caId > -1){
				Long modId = (Long) OTRTAService.getInstance().getLocalDb().getModelFromCodeAllocationId(caId).get(DBLocal.COLUMN_ID);
				int vendId = OTRTAService.getInstance().getLocalDb().getVendorIdByModelId(modId.intValue());
				mRestoringModel = modId;
				for (int i = 0; i < mSpnVendor.getAdapter().getCount(); i++){
					if (mSpnVendor.getAdapter().getItemId(i) == vendId){
						mSpnVendor.setSelection(i);
						return;
					}
				}

			}
		}


		if (mLastPrefs.contains("VENDOR")){
			if (mLastPrefs.contains("MODEL")){
				mRestoringModel = mLastPrefs.getLong("MODEL", -1);
			}

			long savedVendor = mLastPrefs.getLong("VENDOR", 1);
			if (savedVendor > -1){
				for (int i = 0; i < mSpnVendor.getAdapter().getCount(); i++){
					if (mSpnVendor.getAdapter().getItemId(i) == savedVendor){
						mSpnVendor.setSelection(i);
						break;
					}
				}
			}
		}
	}

	private void saveDataForNextSession(){
		Editor e = mLastPrefs.edit();
		if (mSpnVendor.getSelectedItemPosition() > 0){
			long vendId = mSpnVendor.getSelectedItemId(); 
			e.putLong("VENDOR", vendId);
			if (mSpnModel.getSelectedItemPosition() > 0){
				long modId = mSpnModel.getSelectedItemId(); 
				e.putLong("MODEL", modId);
			}
			else{
				e.putLong("MODEL", -1);
			}
		}else{
			e.putLong("VENDOR", -1);
			e.putLong("MODEL", -1);
		}
		e.commit();
	}


}
