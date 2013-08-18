package com.nibdev.otrtav2.model.jdata;

import org.json.JSONObject;

import com.htc.htcircontrol.HtcIrData;
import com.nibdev.otrtav2.activities.ActivityMain;
import com.nibdev.otrtav2.model.tools.GsonData;
import com.nibdev.otrtav2.service.IrDataCompat;

public class Code {
	
	private int mID;
	private IrDataCompat mData;
	private String mDataString;
		
	
	public Code(int id, String b64DeflateData){
		mID = id;
		mDataString = b64DeflateData;
	}

	public int getID(){
		return mID;
	}
	
	public void setID(int codeId) {
		mID = codeId;
	}
	
	
	public IrDataCompat getData(){
		if (mData == null){
			mData = IrDataCompat.fromJson(mDataString);
		}
		return mData;
	}
	
	public String getDataAsString(){
		return mDataString;
	}
	
		
	public JSONObject toJSON(){
		try{
			JSONObject jobj = new JSONObject();
			jobj.put("ID", mID);
			jobj.put("Data", getDataAsString());
			return jobj;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}
	
	public static Code parseJSON(JSONObject jobj){
		try{
			return new Code(jobj.getInt("ID"), jobj.getString("Data"));
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}
	
	
	public static Code fromHTCData(int id, HtcIrData data){
		return new Code(id, GsonData.ObjectToGSON(data));
	}
	

}
