package com.nibdev.otrtav2.model.jdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FastSyncInfoResponse{

	private int[][] vendorDeleteGaps;
	private int[][] modelDeleteGaps;
	private int[][] deviceTypeDeleteGaps;
	private int[][] codeTypeDeleteGaps;
	private int[][] codeAllocationDeleteGaps;
	private int[][] codeDeleteGaps;
	private int fastsyncCount;


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
	public int getFastsyncCount() {
		return fastsyncCount;
	}





	public static FastSyncInfoResponse parseJSON(JSONObject obj){
		FastSyncInfoResponse newFSIR = new FastSyncInfoResponse();
		try {

			newFSIR.fastsyncCount = obj.getInt("fastsyncCount");


			JSONArray jarr = obj.getJSONArray("vendorDeleteGaps");
			if (jarr != null){
				newFSIR.vendorDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					newFSIR.vendorDeleteGaps[i] = new int[2];
					newFSIR.vendorDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
					newFSIR.vendorDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
				}
			}


			jarr = obj.getJSONArray("modelDeleteGaps");
			if (jarr != null){
				newFSIR.modelDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					newFSIR.modelDeleteGaps[i] = new int[2];
					newFSIR.modelDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
					newFSIR.modelDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
				}
			}


			jarr = obj.getJSONArray("deviceTypeDeleteGaps");
			if (jarr != null){
				newFSIR.deviceTypeDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					newFSIR.deviceTypeDeleteGaps[i] = new int[2];
					newFSIR.deviceTypeDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
					newFSIR.deviceTypeDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
				}
			}


			jarr = obj.getJSONArray("codeTypeDeleteGaps");
			if (jarr != null){
				newFSIR.codeTypeDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					newFSIR.codeTypeDeleteGaps[i] = new int[2];
					newFSIR.codeTypeDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
					newFSIR.codeTypeDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
				}
			}


			jarr = obj.getJSONArray("codeAllocationDeleteGaps");
			if (jarr != null){
				newFSIR.codeAllocationDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					newFSIR.codeAllocationDeleteGaps[i] = new int[2];
					newFSIR.codeAllocationDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
					newFSIR.codeAllocationDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
				}
			}


			jarr = obj.getJSONArray("codeDeleteGaps");
			if (jarr != null){
				newFSIR.codeDeleteGaps = new int[jarr.length()][];
				for (int i = 0; i < jarr.length(); i++){
					newFSIR.codeDeleteGaps[i] = new int[2];
					newFSIR.codeDeleteGaps[i][0] = jarr.getJSONObject(i).getInt("start");
					newFSIR.codeDeleteGaps[i][1] = jarr.getJSONObject(i).getInt("stop");
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