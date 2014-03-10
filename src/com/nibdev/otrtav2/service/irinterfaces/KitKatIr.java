package com.nibdev.otrtav2.service.irinterfaces;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.ConsumerIrManager;

import com.nibdev.otrtav2.service.IrDataCompat;

@TargetApi(19)
public class KitKatIr extends IrFace {

	private ConsumerIrManager mKkIr;
	
	public KitKatIr(Context c) throws Exception{
		mKkIr = (ConsumerIrManager) c.getSystemService(Context.CONSUMER_IR_SERVICE);
		if (!mKkIr.hasIrEmitter()) throw new Exception("No KitKat IR Device");
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
		mKkIr.transmit(data.getFrequency(), data.getFrame());

	}

	@Override
	public void cancelCode() {	}

	@Override
	public void learnIRCmd(int timeout) {}

	@Override
	public void setOnCodeLearnedListener(OnCodeLearnedListener listener) {}

}
