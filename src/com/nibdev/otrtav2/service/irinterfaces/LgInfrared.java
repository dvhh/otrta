package com.nibdev.otrtav2.service.irinterfaces;

import android.content.Context;

import com.lge.hardware.IRBlaster.IRBlaster;
import com.lge.hardware.IRBlaster.IRBlasterCallback;
import com.nibdev.otrtav2.service.IrDataCompat;

public class LgInfrared extends IrFace {
	
	private IRBlaster mIR;
	IRBlasterCallback irBlasterCallback;
	
	public LgInfrared(Context c) throws Exception{
		irBlasterCallback = new IRBlasterCallback() {
			
			@Override
			public void newDeviceId(int arg0) { }
			
			@Override
			public void learnIRCompleted(int arg0) { }
			
			@Override
			public void IRBlasterReady() { }
		};
		this.mIR = IRBlaster.getIRBlaster(c, irBlasterCallback);
		if(this.mIR == null) throw new Exception("No LG IR Device");
		if (!IRBlaster.isSdkSupported(c)) throw new Exception("No LG IR Device");
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
		this.mIR.sendIRPattern(data.getFrequency(), toCompIR(data));
	}

	@Override
	public void cancelCode() { }

	@Override
	public void learnIRCmd(int timeout) { }

	@Override
	public void setOnCodeLearnedListener(OnCodeLearnedListener listener) { }
	
	private int[] toCompIR(IrDataCompat data){
		int inUs = 1000000/data.getFrequency();
		int[] frame = data.getFrame();
		int[] arrayOfInt = new int[-1 + frame.length];
		for (int j = 1; j < frame.length; j++)
			arrayOfInt[(j - 1)] = frame[(j - 1)] * inUs;

		return arrayOfInt;
	}
	
}
