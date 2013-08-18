package com.nibdev.otrtav2.view.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class StringInputDialog {


	private StringInputDialog() {}


	private EditText mInputField;
	private Context mContext;


	public interface OnTextChanged{
		void textChanged(String text);
	}
	private OnTextChanged mListener;


	public static AlertDialog getStringInputDialog(Context c, String inText, String title, String posButtonText, String hint, int inputType, OnTextChanged listener){
		StringInputDialog sid = new StringInputDialog();
		sid.mContext = c;
		sid.mListener = listener;
		sid.mInputField = new EditText(sid.mContext);
		sid.mInputField.setText(inText);
		sid.mInputField.setHint(hint);
		sid.mInputField.setMaxLines(1);
		sid.mInputField.setSingleLine();
		sid.mInputField.setSingleLine(true);
		sid.mInputField.setInputType(inputType);

		AlertDialog.Builder bd = new AlertDialog.Builder(c);
		bd.setView(sid.mInputField);
		bd.setTitle(title);
		bd.setPositiveButton(posButtonText, sid.mOnClickListener);
		AlertDialog ad = bd.create();
		ad.setOnShowListener(sid.mOnShowListener);
		ad.setOnDismissListener(sid.mOnDismissListener);
		return ad;

	}
	
	private OnDismissListener mOnDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mInputField.getWindowToken(),0); 
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mInputField.getWindowToken(),0); 
			String newText = mInputField.getText().toString();
			if (mListener != null) mListener.textChanged(newText);
		}
	};

	private OnShowListener mOnShowListener = new OnShowListener() {
		@Override
		public void onShow(DialogInterface dialog) {
			mInputField.requestFocus();
			mInputField.setSelection(mInputField.getText().length());
			InputMethodManager keyboard = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.showSoftInput(mInputField, 0);
		}
	};




}
