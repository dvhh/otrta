package com.nibdev.otrtav2.service.irinterfaces;

import java.lang.reflect.Method;

import android.content.Context;

import com.nibdev.otrtav2.service.IrDataCompat;

public class SamsungInfrared extends IrFace {

	private Object mIrdaService;
	private Method mWriteMethod;

	public SamsungInfrared(Context c) throws NoSuchMethodException{
		super();
		mIrdaService = c.getSystemService("irda");
		Class<? extends Object> irclass = mIrdaService.getClass();
		Class<?> p[] = { String.class };
		mWriteMethod = irclass.getMethod("write_irsend", p);
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
		try {
			mWriteMethod.invoke(mIrdaService, fromCompat(data));
		} catch (Exception ex){}
	}

	@Override
	public void cancelCode() {
		// TODO Auto-generated method stub

	}



	@Override
	public void learnIRCmd(int timeout) {}

	@Override
	public void setOnCodeLearnedListener(OnCodeLearnedListener listener) {}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private String fromCompat(IrDataCompat data){
		StringBuilder sb = new StringBuilder();
		sb.append(data.getFrequency() + ",");
		for (int rc = 0; rc < data.getRepeatCount(); rc++){
			for (int i : data.getFrame()){
				sb.append(i + ",");
			}
		}
		return sb.toString();
	}





}
