package com.nibdev.otrtav2.service.irinterfaces;

import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.service.IrDataCompat;
import com.nibdev.otrtav2.service.IrMessageHandler;

public class HtcInfrared extends IrFace {

	private IrMessageHandler mHandler;
	private CIRControl mCirControl;

	//repeat
	private UUID mLongCodeUUID;
	private Thread mRepeatInitThread;

	public HtcInfrared(Context c) throws Exception{
		super();
		try{
			mHandler = new IrMessageHandler();
			mCirControl = new CIRControl(c, mHandler);
			mCirControl.start();			
		} catch (NoClassDefFoundError ncde){
			throw new Exception("No HTC Device");
		}
		super.init();

	}

	@Override
	public boolean isRunning() {
		return mCirControl.isStarted();
	}

	@Override
	public boolean canLearn() {
		return true;
	}

	@Override
	protected void ifSendCode(IrDataCompat data, boolean repeat) {
		//cancel repetive code if is running
		cancelCode();

		//create code data
		HtcIrData htcdata = new HtcIrData(data.getRepeatCount(), data.getFrequency(), data.getFrame());

		if (!repeat){
			//just send out
			mCirControl.transmitIRCmd(htcdata, true);
		}else{
			startRepeatingCode(htcdata);
		}




	}

	private void startRepeatingCode(final HtcIrData data){
		mRepeatInitThread = new Thread(){
			@Override
			public void run() {
				try{
					//single send
					//Log.i(HtcInfrared.class.getSimpleName(), "repeat send started");
					mCirControl.transmitIRCmd(data, true);

					//wait 750ms
					Thread.sleep(750);

					//send repeated, vibrate again
					mVibrator.vibrate(75);
					Log.i(HtcInfrared.class.getSimpleName(), "long repeat sent");
					data.setRepeatCount(255);
					mLongCodeUUID = mCirControl.transmitIRCmd(data, false);
					
				}catch (InterruptedException ie){
					Log.i(HtcInfrared.class.getSimpleName(), "repeat send interruppted");
				}
			}
		};
		mRepeatInitThread.start();

	}

	
	@Override
	public void cancelCode() {

		//if repeat init thread is running -> cancel
		if (mRepeatInitThread != null && mRepeatInitThread.isAlive()){
			mRepeatInitThread.interrupt();
			NQ.safeJoin(mRepeatInitThread, 1000);
		}

		//if repeat uuid is set, cancel
		if (mLongCodeUUID != null){
			//Log.i(HtcInfrared.class.getSimpleName(), "repeat send canceled");
			mCirControl.cancelCommand();
			mCirControl.discardCommand(mLongCodeUUID);
			mLongCodeUUID = null;
		}
	}



	@Override
	public void learnIRCmd(int timeout) {
		mCirControl.learnIRCmd(timeout);
	}

	@Override
	public void setOnCodeLearnedListener(OnCodeLearnedListener listener) {
		mHandler.setOnCodeLearnedListener(listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.setOnCodeLearnedListener(null);
		mCirControl.stop();
	}

}
