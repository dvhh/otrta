package com.nibdev.otrtav2.service;

import java.util.Arrays;

import com.nibdev.otrtav2.model.tools.GsonData;

public class IrDataCompat{
	private int[] frame;
	private int frequency;
	private int period;
	private int periodTolerance;
	private int repeatCount;

	private IrDataCompat(){}

	public static int FREQ_COMP = 200;
	public static int FRAME_COMP = 20;

	public static IrDataCompat fromJson(String jstr){
		IrDataCompat dat = GsonData.GSONToObject(IrDataCompat.class, jstr);
		if (dat.frame.length % 2 != 0){
			dat.frame = Arrays.copyOf(dat.frame, dat.frame.length + 1);
			dat.frame[dat.frame.length -1] = 0x00;
		}
		return dat;
	}

	public int getRepeatCount() { 
		return repeatCount;
	} 
	public void setRepeatCount(int rc) { 
		repeatCount = rc;
	} 
	public int getFrequency() {
		return frequency;
	} 
	public void setFrequency(int freq) {
		frequency = freq;
	} 
	public int[] getFrame() { 
		return frame;
	} 
	public void setFrame(int[] frame) { 
		this.frame = frame;
	} 
	public int getPeriod() { 
		return period;
	} 
	public void setPeriod(int period) {
		this.period = period; 
	} 
	public int getPeriodTolerance() { 
		return periodTolerance;
	}

	public int sleepTime(){
		int rc = getRepeatCount();
		float msFact = 1000f / (float)getFrequency();
		long all = 0;
		for (int i : getFrame()){
			all += i;
		}
		all *= rc;
		int sleepTime = Math.round(msFact * all);
		return sleepTime;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + frequency;
		result = prime * result + Arrays.hashCode(frame);
		result = prime * result + repeatCount;

		return result;
	}

	@Override
	public boolean equals(Object obj) {

		IrDataCompat other = (IrDataCompat)obj;

		
		if (repeatCount != other.repeatCount){
			return false;
		}

		
		if (frame.length != other.frame.length){
			return false;
		}

		
		if (Math.abs(frequency - other.frequency) > FREQ_COMP){
			return false;
		}

		
		for (int i = 0; i < frame.length; i++){
			long frameDiff = Math.abs(frame[i] - other.frame[i]);
			if (frameDiff > FRAME_COMP){
				return false;
			}
		}

		return true;
	} 



}
