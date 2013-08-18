package com.nibdev.otrtav2.model;

import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class NQ {
	private NQ(){}


	//Views
	/***
	 * (T)View.findViewById(id)
	 * @param v
	 * @param id
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T v(View v, int id, Class<T> type){
		return (T)v.findViewById(id);
	}

	//Preferences
	/***
	 * (T)PreferenceFragment.findPreference(prefString)
	 * @param pf
	 * @param prefString
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Preference> T pref(PreferenceFragment pf, int prefString, Class<T> type){
		return (T)pf.findPreference(pf.getString(prefString));
	}

	public static int etPrefToInt(EditTextPreference et, int min, int max, int def) {
		String sval = et.getText();
		try{
			Long lval = Long.valueOf(sval);
			lval = Math.min(lval, max);
			lval = Math.max(lval, min);
			return lval.intValue();
		}catch (Exception ex){
			return def;
		}
	}
	
	public static float etPrefToFloat(EditTextPreference et, float min, float max, float def) {
		String sval = et.getText();
		try{
			Float fval = Float.valueOf(sval);
			fval = Math.min(fval, max);
			fval = Math.max(fval, min);
			return fval.floatValue();
		}catch (Exception ex){
			return def;
		}
	}




	//Sleep, Threading

	/***
	 * Thread.sleep in try-catch
	 * @param milis
	 */
	public static void safeSleep(int milis){
		try{
			Thread.sleep(milis);
		}catch (InterruptedException ie) {}
	}

	/***
	 * Thread.join(timeout) in try-catch
	 * @param t
	 * @param timeout
	 */
	public static void safeJoin(Thread t, long timeout) {
		if (t == null) return;
		try{
			t.join(timeout);
		}catch (InterruptedException ie){}
	}

	/**
	 * ThreadPoolExeuctor.join(timeout) in try-catch
	 * @param executor
	 * @param timeout
	 */
	public static void safeAwaitTermination(ThreadPoolExecutor executor, long timeout) {
		try{
			executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		}catch (InterruptedException ie){}

	}

	public static View baseAdaptViewInit(View convertView, ViewGroup parent, int layoutId) {
		View v = convertView;
		if (convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			v = inflater.inflate(layoutId, null);
		}
		return v;
	}

	//UI Threading
	private static Handler mLooperHandler;
	public static void onUiThread(Runnable runnable) {
		if (mLooperHandler == null){
			mLooperHandler = new Handler(Looper.getMainLooper());
		}
		mLooperHandler.post(runnable);
	}

	public static void onUiThread(View v, Runnable runnable) {
		v.post(runnable);
	}

	public static void toastOnUiThread(final String message, final int length){
		onUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(Statics.RefContext, message, length).show();				
			}
		});
	}

	// String format
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}


	public static void safeTryAquire(Semaphore waiter, int timeout, TimeUnit unit) {
		try {
			waiter.tryAcquire(timeout, unit);
		} catch (InterruptedException e) {}		
	}


}
