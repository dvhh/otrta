package com.nibdev.otrtav2.service.irinterfaces;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;

import com.htc.htcircontrol.HtcIrData;
import com.nibdev.otrtav2.model.NQ;
import com.nibdev.otrtav2.service.IrDataCompat;
import com.nibdev.otrtav2.view.custom.SendGlow;

public abstract class IrFace {

	protected Vibrator mVibrator;


	protected void init() {

	}


	public void sendCode(IrDataCompat data, boolean repeat){
		SendGlow.sGlowOnce();
		mVibrator.vibrate(50);
		ifSendCode(data, repeat);
	}

	public void sendCodeAndSleep(IrDataCompat data, int sleepMs){
		SendGlow.sGlowOnce();
		ifSendCode(data, false);
		NQ.safeSleep(sleepMs);
	}

	public void onDestroy(){

	}


	public interface OnCodeLearnedListener{
		public void onCodeLearned(HtcIrData data, String message);
	}
	public abstract boolean isRunning();
	public abstract boolean canLearn();
	protected abstract void ifSendCode(IrDataCompat data, boolean repeat);
	public abstract void cancelCode();
	public abstract void learnIRCmd(int timeout);
	public abstract void setOnCodeLearnedListener(OnCodeLearnedListener listener);


	public static IrFace create(Context c) {

		IrFace face = null;

		//HTC
		if (face == null){
			try{
				face = new HtcInfrared(c);
			}catch (Exception ex){}
		}

		//Samsung
		if (face == null){
			try{
				face = new SamsungInfrared(c);
			}catch (Exception ex){}
		}
		
		//LG
		if (face == null){
			try{
				face = new LgInfrared(c);
			}catch (Exception ex){}
		}

		//Universal (KitKat++)
		if (face == null){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
				try{
					face = new KitKatIr(c);
				}catch (Exception ex){}
			}
		}

		if (face != null){
			face.mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);			
		}

		return face;

	}

}
