package com.nibdev.otrtav2.model.jdata;

import org.json.JSONException;
import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class Vendor {
	private String mName;
	private int mID;

	public Vendor(String name, int id){
		mName = name;
		mID = id;
	}


	public int getID(){
		return mID;
	}
	
	public void setID(int id){
		mID = id;
	}

	public String getName(){
		return mName;
	}




	public static Vendor parseJSON(JSONObject obj){
		try{
			return new Vendor(obj.getString("Name"), obj.getInt("ID"));
		}catch (JSONException ex){
			ActivityMain.handleException(ex, true);
			return null;
		}

	}


	public JSONObject toJSON(){
		try{
			JSONObject jobj = new JSONObject();
			jobj.put("Name", mName);
			jobj.put("ID", mID);
			return jobj;
		}catch (JSONException ex){
			ActivityMain.handleException(ex, true);
			return null;
		}

	}
	
	@Override
	public String toString() {
		return mName;
	}

}