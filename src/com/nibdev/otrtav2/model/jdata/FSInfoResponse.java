package com.nibdev.otrtav2.model.jdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FSInfoResponse{

	private int[][] vendorDeleteGaps;
	private int[][] modelDeleteGaps;
	private int[][] deviceTypeDeleteGaps;
	private int[][] codeTypeDeleteGaps;
	private int[][] codeAllocationDeleteGaps;
	private int[][] codeDeleteGaps;
	private int[][] codeTypeMappingDeleteGaps;


	private int[][] newVendors;
	private int[][] newModels;
	private int[][] newDeviceTypes;
	private int[][] newCodeTypes;
	private int[][] newCodeAllocations;
	private int[][] newCodes;
	private int[][] newCodeTypeMappings;



	public int[][] getVendorDeleteGaps() {
		return vendorDeleteGaps;
	}
	public int[][] getModelDeleteGaps() {
		return modelDeleteGaps;
	}
	public int[][] getDeviceTypeDeleteGaps() {
		return deviceTypeDeleteGaps;
	}
	public int[][] getCodeTypeDeleteGaps() {
		return codeTypeDeleteGaps;
	}
	public int[][] getCodeAllocationDeleteGaps() {
		return codeAllocationDeleteGaps;
	}
	public int[][] getCodeDeleteGaps() {
		return codeDeleteGaps;
	}
	public int[][] getCodeTypeMappingDeleteGaps() {
		return codeTypeMappingDeleteGaps;
	}
	public int[][] getNewVendors() {
		return newVendors;
	}
	public int[][] getNewModels() {
		return newModels;
	}
	public int[][] getNewDeviceTypes() {
		return newDeviceTypes;
	}
	public int[][] getNewCodeTypes() {
		return newCodeTypes;
	}
	public int[][] getNewCodeAllocations() {
		return newCodeAllocations;
	}
	public int[][] getNewCodes() {
		return newCodes;
	}
	public int[][] getNewCodeTypeMappings() {
		return newCodeTypeMappings;
	}
	

	public static FSInfoResponse parseJSON(JSONObject obj){
		FSInfoResponse newFSIR = new FSInfoResponse();
		try {

			JSONArray jarr = obj.getJSONArray("vendorDeleteGaps");
			if (jarr != null){
				newFSIR.vendorDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.vendorDeleteGaps[i] = new int[2];
						newFSIR.vendorDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.vendorDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}


			jarr = obj.getJSONArray("modelDeleteGaps");
			if (jarr != null){
				newFSIR.modelDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.modelDeleteGaps[i] = new int[2];
						newFSIR.modelDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.modelDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}


			jarr = obj.getJSONArray("deviceTypeDeleteGaps");
			if (jarr != null){
				newFSIR.deviceTypeDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.deviceTypeDeleteGaps[i] = new int[2];
						newFSIR.deviceTypeDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.deviceTypeDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}


			jarr = obj.getJSONArray("codeTypeDeleteGaps");
			if (jarr != null){
				newFSIR.codeTypeDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.codeTypeDeleteGaps[i] = new int[2];
						newFSIR.codeTypeDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.codeTypeDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}


			jarr = obj.getJSONArray("codeAllocationDeleteGaps");
			if (jarr != null){
				newFSIR.codeAllocationDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.codeAllocationDeleteGaps[i] = new int[2];
						newFSIR.codeAllocationDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.codeAllocationDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}


			jarr = obj.getJSONArray("codeDeleteGaps");
			if (jarr != null){
				newFSIR.codeDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.codeDeleteGaps[i] = new int[2];
						newFSIR.codeDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.codeDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}

			jarr = obj.getJSONArray("codeTypeMappingDeleteGaps");
			if (jarr != null){
				newFSIR.codeTypeMappingDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.codeTypeMappingDeleteGaps[i] = new int[2];
						newFSIR.codeTypeMappingDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.codeTypeMappingDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}

			// new 

			jarr = obj.getJSONArray("newVendors");
			if (jarr != null){
				newFSIR.newVendors = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newVendors[i] = new int[2];
						newFSIR.newVendors[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newVendors[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}
			jarr = obj.getJSONArray("newModels");
			if (jarr != null){
				newFSIR.newModels = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newModels[i] = new int[2];
						newFSIR.newModels[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newModels[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}
			jarr = obj.getJSONArray("newDeviceTypes");
			if (jarr != null){
				newFSIR.newDeviceTypes = new int[jarr.length()][];

				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newDeviceTypes[i] = new int[2];
						newFSIR.newDeviceTypes[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newDeviceTypes[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}
			jarr = obj.getJSONArray("newCodeTypes");
			if (jarr != null){
				newFSIR.newCodeTypes = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newCodeTypes[i] = new int[2];
						newFSIR.newCodeTypes[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newCodeTypes[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}
			jarr = obj.getJSONArray("newCodeTypeMappings");
			if (jarr != null){
				newFSIR.newCodeTypeMappings = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newCodeTypeMappings[i] = new int[2];
						newFSIR.newCodeTypeMappings[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newCodeTypeMappings[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}
			jarr = obj.getJSONArray("newCodeAllocations");
			if (jarr != null){
				newFSIR.newCodeAllocations = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newCodeAllocations[i] = new int[2];
						newFSIR.newCodeAllocations[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newCodeAllocations[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}
			jarr = obj.getJSONArray("newCodes");
			if (jarr != null){
				newFSIR.newCodes = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					Class<? extends Object> entryClass = jarr.get(0).getClass();
					if (entryClass == JSONObject.class){
						newFSIR.newCodes[i] = new int[2];
						newFSIR.newCodes[i][0] = jarr.getJSONObject(i).getInt("start");
						newFSIR.newCodes[i][1] = jarr.getJSONObject(i).getInt("stop");
					}
				}
			}

			return newFSIR;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 

		return null;
	}





}