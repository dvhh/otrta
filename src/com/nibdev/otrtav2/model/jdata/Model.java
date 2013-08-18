package com.nibdev.otrtav2.model.jdata;

import org.json.JSONException;
import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class Model {
	private int mVendorID;
	private int mID;
	private int mDeviceTypeID;
	private String mName;

	public Model(String name, int id, int vendorId, int deviceTypeId){
		mName = name;
		mID = id;
		mVendorID = vendorId;
		mDeviceTypeID = deviceTypeId;
	}

	public int getID(){
		return mID;
	}
	
	public void setID(int id){
		mID = id;
	}

	public int getVendorID(){
		return mVendorID;
	}
	
	public void setVendorID(int vendorId) {
		mVendorID = vendorId;		
	}
	
	public int getDeviceTypeID(){
		return mDeviceTypeID;
	}

	public String getName(){
		return mName;
	}

	public static Model parseJSON(JSONObject obj){
		try {
			return new Model(obj.getString("Name"), obj.getInt("ID"), obj.getInt("VendorID"), obj.getInt("DeviceTypeID"));
		} catch (JSONException e) {
			ActivityMain.handleException(e, true);
			return null;
		}
	}

	public JSONObject toJSON(){
		try{
			JSONObject jobj = new JSONObject();
			jobj.put("Name", mName);
			jobj.put("ID", mID);
			jobj.put("VendorID", mVendorID);
			jobj.put("DeviceTypeID", mDeviceTypeID);
			return jobj;
		}catch (JSONException e){
			ActivityMain.handleException(e, true);
			return null;
		}
	}

	@Override
	public String toString() {
		return mName;
	}
	

}