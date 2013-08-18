package com.nibdev.otrtav2.model.jdata;

import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class CodeAllocation {
	private int mID;
	private int mModelID;
	private int mCodeID;
	private int mCodeTypeID;
	

	public CodeAllocation(int id, int codeID, int modelID, int codeTypeID){
		mID = id;
		mCodeID = codeID;
		mModelID = modelID;
		mCodeTypeID = codeTypeID;
	}
	
	public int getID(){
		return mID;
	}
	
	public void setID(int caId) {
		mID = caId;
	}
	
	public int getModelID(){
		return mModelID;
	}
	
	public void setModelID(int id){
		mModelID = id;
	}
	
	public int getCodeID(){
		return mCodeID;
	}
	
	public void setCodeID(int id){
		mCodeID = id;
	}
	
	public int getCodeTypeID(){
		return mCodeTypeID;
	}
	
	public void setCodeTypeID(int id){
		mCodeTypeID = id;
	}
	
	
	public static CodeAllocation parseJSON(JSONObject obj){
		try{
			return new CodeAllocation(obj.getInt("ID"), obj.getInt("CodeID"), obj.getInt("ModelID"), obj.getInt("CodeTypeID"));
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}

	public JSONObject toJSON(){
		try{
			JSONObject jobj = new JSONObject();
			jobj.put("ID", mID);
			jobj.put("ModelID", mModelID);
			jobj.put("CodeID", mCodeID);
			jobj.put("CodeTypeID", mCodeTypeID);
			return jobj;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}



}
