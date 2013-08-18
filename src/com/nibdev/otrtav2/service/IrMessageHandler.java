package com.nibdev.otrtav2.service;

import android.os.Handler;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;
import com.nibdev.otrtav2.service.irinterfaces.IrFace.OnCodeLearnedListener;

public class IrMessageHandler extends Handler {

	
	
	public interface OnStartedListener{
		public void onStarted();
	}
	private OnStartedListener mStartedListener;
	public void setOnStartedListener(OnStartedListener listener){
		mStartedListener = listener;
	}
	
	
	public interface OnCancelFinished{
		public void onCanceled();
	}
	private OnCancelFinished mCancelFinishedListener;
	public void setOnCancelFinishedListener(OnCancelFinished listener){
		mCancelFinishedListener = listener;
	}

	//iface OnCodeLearned
	private OnCodeLearnedListener mCodeLearnedListener;
	public void setOnCodeLearnedListener(OnCodeLearnedListener listener){
		mCodeLearnedListener = listener;
	}


	@Override
	public void handleMessage(android.os.Message msg) {

		int messageType = msg.what;
		int errorId = msg.arg1;

		
		if (messageType == CIRControl.MSG_RET_CANCEL){
			if (mCancelFinishedListener != null) mCancelFinishedListener.onCanceled();
		}
		
		if (messageType == CIRControl.MSG_RET_STARTED){
			if (mStartedListener != null) mStartedListener.onStarted();

		}else if (messageType == CIRControl.MSG_RET_LEARN_IR){
			if (errorId == CIRControl.ERR_NONE){
				HtcIrData mLearnCode = (HtcIrData) msg.getData().getSerializable(CIRControl.KEY_CMD_RESULT);
				if (mCodeLearnedListener != null) mCodeLearnedListener.onCodeLearned(mLearnCode, "");
			}else{
				if (errorId == CIRControl.ERR_PULSE_ERROR){
					if (mCodeLearnedListener != null) mCodeLearnedListener.onCodeLearned(null, "PULSE ERROR");
				}else if (errorId == CIRControl.ERR_OUT_OF_FREQ){
					if (mCodeLearnedListener != null) mCodeLearnedListener.onCodeLearned(null, "OUT OF FREQUENCY");
				}else if (errorId == CIRControl.ERR_LEARNING_TIMEOUT){
					if (mCodeLearnedListener != null) mCodeLearnedListener.onCodeLearned(null, "Learning Timeout");
				}else{
					if (mCodeLearnedListener != null) mCodeLearnedListener.onCodeLearned(null, "Unknown Error Id: " + errorId);
				}
			}
		}
	};

}
