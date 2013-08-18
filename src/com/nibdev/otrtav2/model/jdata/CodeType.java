package com.nibdev.otrtav2.model.jdata;

import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class CodeType {
	private String mName;
	private int mID;

	public CodeType(String name, int id){
		mName = name;
		mID = id;
	}
	
	public int getID(){
		return mID;
	}
	
	public String getName(){
		return mName;
	}
	

	public static CodeType parseJSON(JSONObject obj){
		try{
			return new CodeType(obj.getString("Name"), obj.getInt("ID"));
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