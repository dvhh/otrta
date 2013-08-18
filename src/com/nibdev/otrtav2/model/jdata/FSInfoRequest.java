package com.nibdev.otrtav2.model.jdata;

import org.json.JSONObject;

import com.nibdev.otrtav2.activities.ActivityMain;

public class FSInfoRequest{
	private int vendorMinID = 0;
	private int vendorMaxID = 0;
	private int vendorCount = 0;
	
	private int modelMinID = 0;
	private int modelMaxID = 0;
	private int modelCount = 0;
	
	private int codeTypeMinID = 0;
	private int codeTypeMaxID = 0;
	private int codeTypeCount = 0;
	
	private int deviceTypeMinID = 0;
	private int deviceTypeMaxID = 0;
	private int deviceTypeCount = 0;
	
	private int codeAllocationMinID = 0;
	private int codeAllocationMaxID = 0;
	private int codeAllocationCount = 0;
	
	private int codeMinID = 0;
	private int codeMaxID = 0;
	private int codeCount = 0;
	
	private int codeTypeMappingMinID = 0;
	private int codeTypeMappingMaxID = 0;
	private int codeTypeMappingCount = 0;
	
	public JSONObject toJSON(){
		try{
			JSONObject jobj = new JSONObject();
			jobj.put("vendorMinID", vendorMinID);
			jobj.put("vendorMaxID", vendorMaxID);
			jobj.put("vendorCount", vendorCount);
			
			jobj.put("modelMinID", modelMinID);
			jobj.put("modelMaxID", modelMaxID);
			jobj.put("modelCount", modelCount);
			
			jobj.put("codeTypeMinID", codeTypeMinID);
			jobj.put("codeTypeMaxID", codeTypeMaxID);
			jobj.put("codeTypeCount", codeTypeCount);
			
			jobj.put("deviceTypeMinID", deviceTypeMinID);
			jobj.put("deviceTypeMaxID", deviceTypeMaxID);
			jobj.put("deviceTypeCount", deviceTypeCount);
			
			jobj.put("codeAllocationMinID", codeAllocationMinID);
			jobj.put("codeAllocationMaxID", codeAllocationMaxID);
			jobj.put("codeAllocationCount", codeAllocationCount);
			
			jobj.put("codeMinID", codeMinID);
			jobj.put("codeMaxID", codeMaxID);
			jobj.put("codeCount", codeCount);
			
			jobj.put("codeTypeMappingMinID", codeTypeMappingMinID);
			jobj.put("codeTypeMappingMaxID", codeTypeMappingMaxID);
			jobj.put("codeTypeMappingCount", codeTypeMappingCount);
			
			return jobj;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return null;
	}
	
	
	
	
	
	
	
	public int getVendorMinID() {
		return vendorMinID;
	}
	public void setVendorMinID(int vendorMinID) {
		this.vendorMinID = vendorMinID;
	}
	public int getVendorMaxID() {
		return vendorMaxID;
	}
	public void setVendorMaxID(int vendorMaxID) {
		this.vendorMaxID = vendorMaxID;
	}
	public int getVendorCount() {
		return vendorCount;
	}
	public void setVendorCount(int vendorCount) {
		this.vendorCount = vendorCount;
	}
	public int getModelMinID() {
		return modelMinID;
	}
	public void setModelMinID(int modelMinID) {
		this.modelMinID = modelMinID;
	}
	public int getModelMaxID() {
		return modelMaxID;
	}
	public void setModelMaxID(int modelMaxID) {
		this.modelMaxID = modelMaxID;
	}
	public int getModelCount() {
		return modelCount;
	}
	public void setModelCount(int modelCount) {
		this.modelCount = modelCount;
	}
	public int getCodeTypeMinID() {
		return codeTypeMinID;
	}
	public void setCodeTypeMinID(int codeTypeMinID) {
		this.codeTypeMinID = codeTypeMinID;
	}
	public int getCodeTypeMaxID() {
		return codeTypeMaxID;
	}
	public void setCodeTypeMaxID(int codeTypeMaxID) {
		this.codeTypeMaxID = codeTypeMaxID;
	}
	public int getCodeTypeCount() {
		return codeTypeCount;
	}
	public void setCodeTypeCount(int codeTypeCount) {
		this.codeTypeCount = codeTypeCount;
	}
	public int getDeviceTypeMinID() {
		return deviceTypeMinID;
	}
	public void setDeviceTypeMinID(int deviceTypeMinID) {
		this.deviceTypeMinID = deviceTypeMinID;
	}
	public int getDeviceTypeMaxID() {
		return deviceTypeMaxID;
	}
	public void setDeviceTypeMaxID(int deviceTypeMaxID) {
		this.deviceTypeMaxID = deviceTypeMaxID;
	}
	public int getDeviceTypeCount() {
		return deviceTypeCount;
	}
	public void setDeviceTypeCount(int deviceTypeCount) {
		this.deviceTypeCount = deviceTypeCount;
	}
	public int getCodeAllocationMinID() {
		return codeAllocationMinID;
	}
	public void setCodeAllocationMinID(int codeAllocationMinID) {
		this.codeAllocationMinID = codeAllocationMinID;
	}
	public int getCodeAllocationMaxID() {
		return codeAllocationMaxID;
	}
	public void setCodeAllocationMaxID(int codeAllocationMaxID) {
		this.codeAllocationMaxID = codeAllocationMaxID;
	}
	public int getCodeAllocationCount() {
		return codeAllocationCount;
	}
	public void setCodeAllocationCount(int codeAllocationCount) {
		this.codeAllocationCount = codeAllocationCount;
	}
	public int getCodeMinID() {
		return codeMinID;
	}
	public void setCodeMinID(int codeMinID) {
		this.codeMinID = codeMinID;
	}
	public int getCodeMaxID() {
		return codeMaxID;
	}
	public void setCodeMaxID(int codeMaxID) {
		this.codeMaxID = codeMaxID;
	}
	public int getCodeCount() {
		return codeCount;
	}
	public void setCodeCount(int codeCount) {
		this.codeCount = codeCount;
	}
	public int getCodeTypeMappingMinID() {
		return codeTypeMappingMinID;
	}
	public void setCodeTypeMappingMinID(int codeTypeMappingMinID) {
		this.codeTypeMappingMinID = codeTypeMappingMinID;
	}
	public int getCodeTypeMappingMaxID() {
		return codeTypeMappingMaxID;
	}
	public void setCodeTypeMappingMaxID(int codeTypeMappingMaxID) {
		this.codeTypeMappingMaxID = codeTypeMappingMaxID;
	}
	public int getCodeTypeMappingCount() {
		return codeTypeMappingCount;
	}
	public void setCodeTypeMappingCount(int codeTypeMappingCount) {
		this.codeTypeMappingCount = codeTypeMappingCount;
	}
	
	
}