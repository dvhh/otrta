package com.nibdev.otrtav2.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.nibdev.otrtav2.model.scripts.Script;
import com.nibdev.otrtav2.service.OTRTAService;
import com.nibdev.otrtav2.view.adapters.ScriptSelectSpinnerAdapter;

public class FragmentSelectScriptDialog extends DialogFragment {

	public interface OnSelectedScriptChangedListener{
		public void selectedScriptChanged(Script s);
	}
	private OnSelectedScriptChangedListener mListener;
	public void setOnSelectedScriptChangedListener(OnSelectedScriptChangedListener listener){
		mListener = listener;
	}
	
	private int mSelectedScriptId;
	private Spinner mSpnSkript;
	
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mSpnSkript = new Spinner(getActivity());
		mSpnSkript.setOnItemSelectedListener(mOnScriptSelectedListener);
				
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select a Script");
		builder.setPositiveButton("Select", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		builder.setView(mSpnSkript);
		
		mSpnSkript.setAdapter(new ScriptSelectSpinnerAdapter(OTRTAService.getInstance().getLocalDb()));
		((ScriptSelectSpinnerAdapter)mSpnSkript.getAdapter()).setTopString("Select a script");
		return builder.create();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog)getDialog();
		d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(mOnSetClickListener);
		d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
	}
	
	private View.OnClickListener mOnSetClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Script s = new Script();
			s.setId(mSelectedScriptId);
			if (mListener != null) mListener.selectedScriptChanged(s);
			dismiss();
		}
	};
	
	private OnItemSelectedListener mOnScriptSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mSelectedScriptId = ((Long)arg3).intValue();
			AlertDialog d = (AlertDialog)getDialog();
			d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(mSelectedScriptId > 0);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	
	
}
