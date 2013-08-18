package com.nibdev.otrtav2.model.jdata;

import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class DeviceType {
	private String mName;
	private int mID;

	public DeviceType(String name, int id){
		mName = name;
		mID = id;
	}
	
	public int getID(){
		return mID;
	}
	
	public String getName(){
		return mName;
	}
	

	public static DeviceType parseJSON(JSONObject obj){
		try{
			return new DeviceType(obj.getString("Name"), obj.getInt("ID"));
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}


	public JSONObject toJSON(){
		try{
			JSONObject jobj = new JSONObject();
			jobj.put("Name", mName);
			jobj.put("ID", mID);
			return jobj;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return mName;
	}

}