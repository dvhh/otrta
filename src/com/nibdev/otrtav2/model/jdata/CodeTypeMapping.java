package com.nibdev.otrtav2.model.jdata;

import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class CodeTypeMapping {
	private int mID;
	private int mDeviceTypeID;
	private int mCodeTypeID;

	public CodeTypeMapping(int id, int codeTypeId, int deviceTypeId){
		mID = id;
		mDeviceTypeID = deviceTypeId;
		mCodeTypeID = codeTypeId;
	}
	
	public int getID(){
		return mID;
	}

	public int getDeviceTypeID() {
		return mDeviceTypeID;
	}

	public int getCodeTypeID() {
		return mCodeTypeID;
	}
	
	public static CodeTypeMapping parseJSON(JSONObject obj){
		try{
			return new CodeTypeMapping(obj.getInt("ID"), obj.getInt("CodeTypeID"), obj.getInt("DeviceTypeID"));
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}
	
	
}
