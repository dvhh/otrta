package com.nibdev.otrtav2.service.irinterfaces;

import android.widget.Toast;

import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.service.IrDataCompat;

public class IrDummy extends IrFace {

	public IrDummy(){
		super();
		super.init();
	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public boolean canLearn() {
		return false;
	}

	@Override
	protected void ifSendCode(IrDataCompat data, boolean repeat) {
		if (repeat){
			NQ.toastOnUiThread("repeat started", Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void cancelCode() {
		NQ.toastOnUiThread("code canceled", Toast.LENGTH_SHORT);		
	}


	@Override
	public void learnIRCmd(int timeout) {

	}

	@Override
	public void setOnCodeLearnedListener(OnCodeLearnedListener listener) {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}







}
