package com.nibdev.otrtav2.model.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.nibdev.otrtav2.activities.ActivityMain;
import com.nibdev.otrtav2.model.Statics;
import com.nibdev.otrtav2.model.database.DBRemote.SyncStatusCallbacks.RemoteSyncState;
import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.model.jdata.CodeAllocation;
import com.nibdev.otrtav2.model.jdata.CodeType;
import com.nibdev.otrtav2.model.jdata.CodeTypeMapping;
import com.nibdev.otrtav2.model.jdata.DeviceType;
import com.nibdev.otrtav2.model.jdata.FSInfoRequest;
import com.nibdev.otrtav2.model.jdata.FSInfoResponse;
import com.nibdev.otrtav2.model.jdata.Model;
import com.nibdev.otrtav2.model.jdata.Vendor;
import com.nibdev.otrtav2.model.tools.ServiceData;
import com.nibdev.otrtav2.service.OTRTAService;

public class DBRemote {

	private static DBRemote _instance;
	public static DBRemote getInstance(){
		if (_instance == null) _instance = new DBRemote();
		return _instance;
	}


	//Callbacks
	public interface SyncStatusCallbacks{
		public enum RemoteSyncState{STARTED, RUNNING,FINISHED,ERROR}
		public void onSyncStatusUpdate(RemoteSyncState state, int total, int progress, String info);
	}
	private HashSet<SyncStatusCallbacks> mSyncListeners;
	public void addSyncListener(SyncStatusCallbacks listener){
		mSyncListeners.add(listener);
	}
	public void removeSyncListener(SyncStatusCallbacks listener){
		mSyncListeners.remove(listener);
	}
	private void updateSyncStatus(final RemoteSyncState state, final int total, final int progress, final String info){
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				for (SyncStatusCallbacks listener : mSyncListeners){
					if (listener != null){
						listener.onSyncStatusUpdate(state, total, progress, info);
					}
				}
			}
		});

	}	


	private FullSyncThread mSyncThread;

	private DBRemote(){
		mSyncListeners = new HashSet<DBRemote.SyncStatusCallbacks>();
	}


	public void startFullSyncThread(){
		if (mSyncThread == null || !mSyncThread.isAlive()){
			mSyncThread = new FullSyncThread();
			mSyncThread.start();
		}
	}

	public void cancelFullSyncThread(){
		if (mSyncThread != null && mSyncThread.isAlive()){
			mSyncThread.requestCancel();
			try {
				mSyncThread.join();
			} catch (InterruptedException egal) {}
			updateSyncStatus(RemoteSyncState.FINISHED, 0, 0, "Aborted");
		}
	}



	private class FullSyncThread extends Thread {
		private boolean _cancelRequest = false;
		public void requestCancel(){
			_cancelRequest = true;
		}

		@Override
		public void run() {
			try{

				updateSyncStatus(RemoteSyncState.STARTED, 0, 0, "Checking connection..");

				//online check!
				if (!checkConnection()){
					updateSyncStatus(RemoteSyncState.ERROR, 0, 0, "No internet connection");
					return;
				}

				//update info: Searching for updates..
				updateSyncStatus(RemoteSyncState.RUNNING, -1, 0, "Searching updates..");


				if (_cancelRequest) return;

				//Info Request:
				FSInfoRequest fsir = new FSInfoRequest();

				DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
				fsir.setModelMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_MODELS));
				fsir.setModelMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_MODELS));
				fsir.setModelCount(dbLocal.getCount_SQL(DBLocal.TABLE_MODELS));

				fsir.setVendorMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_VENDORS));
				fsir.setVendorMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_VENDORS));
				fsir.setVendorCount(dbLocal.getCount_SQL(DBLocal.TABLE_VENDORS));

				fsir.setCodeTypeMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_CODETYPES));
				fsir.setCodeTypeMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_CODETYPES));
				fsir.setCodeTypeCount(dbLocal.getCount_SQL(DBLocal.TABLE_CODETYPES));

				fsir.setDeviceTypeMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_DEVICETYPES));
				fsir.setDeviceTypeMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_DEVICETYPES));
				fsir.setDeviceTypeCount(dbLocal.getCount_SQL(DBLocal.TABLE_DEVICETYPES));

				fsir.setCodeAllocationMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_CODEALLOCATIONS));
				fsir.setCodeAllocationMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_CODEALLOCATIONS));
				fsir.setCodeAllocationCount(dbLocal.getCount_SQL(DBLocal.TABLE_CODEALLOCATIONS));

				fsir.setCodeMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_CODES));
				fsir.setCodeMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_CODES));
				fsir.setCodeCount(dbLocal.getCount_SQL(DBLocal.TABLE_CODES));

				fsir.setCodeTypeMappingMinID(dbLocal.getLowestID_SQL(DBLocal.TABLE_CODETYPEMAPPINGS));
				fsir.setCodeTypeMappingMaxID(dbLocal.getHighestID_SQL(DBLocal.TABLE_CODETYPEMAPPINGS));
				fsir.setCodeTypeMappingCount(dbLocal.getCount_SQL(DBLocal.TABLE_CODETYPEMAPPINGS));

				//InfoResponse
				JSONObject jFsInfo = ServiceData.HttpPostAnswerJsonObject(URL_GETFSINFO, fsir.toJSON());
				FSInfoResponse fsResp = FSInfoResponse.parseJSON(jFsInfo);


				//Del gaps
				dbLocal.deleteGaps(fsResp);


				//Gen IDs
				List<Integer> newVendors = createIDList(fsResp.getNewVendors());
				List<Integer> newModels = createIDList(fsResp.getNewModels());
				List<Integer> newDeviceTypes = createIDList(fsResp.getNewDeviceTypes());
				List<Integer> newCodeTypes = createIDList(fsResp.getNewCodeTypes());
				List<Integer> newCodes = createIDList(fsResp.getNewCodes());
				List<Integer> newCodeAllocations = createIDList(fsResp.getNewCodeAllocations());
				List<Integer> newCodeTypeMappings = createIDList(fsResp.getNewCodeTypeMappings());


				int totalCount = newVendors.size() + newModels.size() + newDeviceTypes.size() + newCodeTypes.size() + newCodeAllocations.size() + newCodeTypeMappings.size() + newCodes.size();
				int processedCount = 0;

				//dl new Vendors
				int dlStep = 100;
				while (newVendors.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newVendors.subList(0, Math.min(dlStep, newVendors.size()));
					downloadNewVendors(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				//dl new Models
				dlStep = 100;
				while (newModels.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newModels.subList(0, Math.min(dlStep, newModels.size()));
					downloadNewModels(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				//dl new CodeTypes
				dlStep = 100;
				while (newCodeTypes.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newCodeTypes.subList(0, Math.min(dlStep, newCodeTypes.size()));
					downloadNewCodeTypes(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				//dl new DeviceTypes
				dlStep = 100;
				while (newDeviceTypes.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newDeviceTypes.subList(0, Math.min(dlStep, newDeviceTypes.size()));
					downloadNewDeviceTypes(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				//dl new Codes
				dlStep = 50;
				while (newCodes.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newCodes.subList(0, Math.min(dlStep, newCodes.size()));
					downloadNewCodes(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				//dl new CodeAllocations
				dlStep = 100;
				while (newCodeAllocations.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newCodeAllocations.subList(0, Math.min(dlStep, newCodeAllocations.size()));
					downloadNewCodeAllocations(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				//dl new CodeTypeMappings
				dlStep = 100;
				while (newCodeTypeMappings.size() > 0){
					if (_cancelRequest) return;
					List<Integer> toDl = newCodeTypeMappings.subList(0, Math.min(dlStep, newCodeTypeMappings.size()));
					downloadNewCodeTypeMappings(toDl);
					processedCount += toDl.size();
					toDl.clear();
					updateSyncStatus(RemoteSyncState.RUNNING, totalCount, processedCount, "Downloading..");
				}

				updateSyncStatus(RemoteSyncState.FINISHED, 0, 0, "");


				if (totalCount > 0 ){
					dbLocal.notifyYourListeners();
				}

			}catch (Exception ex){
				ActivityMain.handleException(ex, true);
				updateSyncStatus(RemoteSyncState.ERROR, 0, 0, "Error");
			}
		}
	}


	public boolean checkConnection(){

		ConnectivityManager cm = (ConnectivityManager)Statics.RefContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();

		//NetowrkInfo null or not connected -> no connectivy
		if (ni == null || !ni.isConnected()) return false;

		//now wake up service
		try{
			String ans = ServiceData.HttpGetAnswer(URL_GETSERVICESTATE);
			ans = ans.replace("\"", "").trim();
			return ans.equals("1");
		}catch (Exception ex){
			return false;
		}
	}


	public String getVersionInfo(){
		try{
			String ans = ServiceData.HttpGetAnswer(URL_GETAPPVERSION);
			return ans;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}


	private void downloadNewVendors(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<Vendor> newVends = new ArrayList<Vendor>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETVENDORS, jarr);
		for (JSONObject obi : jobjs){
			newVends.add(Vendor.parseJSON(obi));
		}
		dbLocal.addVendors(newVends);
	}

	private void downloadNewModels(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<Model> newMods = new ArrayList<Model>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETMODELS, jarr);
		for (JSONObject obi : jobjs){
			newMods.add(Model.parseJSON(obi));
		}
		dbLocal.addModels(newMods);
	}

	private void downloadNewDeviceTypes(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<DeviceType> newDevTypes = new ArrayList<DeviceType>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETDEVICETYPES, jarr);
		for (JSONObject obi : jobjs){
			newDevTypes.add(DeviceType.parseJSON(obi));
		}
		dbLocal.addDeviceTypes(newDevTypes);
	}

	private void downloadNewCodeTypes(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<CodeType> newCodeTypes = new ArrayList<CodeType>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETCODETYPES, jarr);
		for (JSONObject obi : jobjs){
			newCodeTypes.add(CodeType.parseJSON(obi));
		}
		dbLocal.addCodeTypes(newCodeTypes);
	}

	private void downloadNewCodes(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<Code> newCodes = new ArrayList<Code>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETCODES, jarr);
		for (JSONObject obi : jobjs){
			newCodes.add(Code.parseJSON(obi));
		}
		dbLocal.addCodes(newCodes);
	}

	private void downloadNewCodeAllocations(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<CodeAllocation> newCodeAllocs = new ArrayList<CodeAllocation>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETCODEALLOCATIONS, jarr);
		for (JSONObject obi : jobjs){
			newCodeAllocs.add(CodeAllocation.parseJSON(obi));
		}
		dbLocal.addCodeAllocations(newCodeAllocs);
	}

	private void downloadNewCodeTypeMappings(List<Integer> ids) throws ClientProtocolException, IOException, JSONException, HttpException{
		DBLocal dbLocal = OTRTAService.getInstance().getLocalDb();
		JSONArray jarr = new JSONArray(ids);
		List<CodeTypeMapping> newCodeTypeMappings = new ArrayList<CodeTypeMapping>();
		List<JSONObject> jobjs = ServiceData.HttpPostAnswer(URL_GETCODETYPEMAPPIMGS, jarr);
		for (JSONObject obi : jobjs){
			newCodeTypeMappings.add(CodeTypeMapping.parseJSON(obi));
		}
		dbLocal.addCodeTypeMappings(newCodeTypeMappings);
	}


	public int uploadNewModel(Model m){
		try{
			String ans = ServiceData.HttpPostAnswer(URL_ADDMODEL, m.toJSON());
			ans =  ans.replace("\"", "").trim();
			int id = Integer.parseInt(ans);
			return id;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return 0;	
	}

	public int uploadNewVendor(Vendor v){
		try{
			String ans = ServiceData.HttpPostAnswer(URL_ADDVENDOR, v.toJSON());
			ans =  ans.replace("\"", "").trim();
			int id = Integer.parseInt(ans);
			return id;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return 0;	
	}

	public int uploadNewCodeAllocation(CodeAllocation ca){
		try{
			JSONObject caJObj = ca.toJSON();
			caJObj.put("XSN", Build.SERIAL);
			String ans = ServiceData.HttpPostAnswer(URL_ADDCODEALLOCATION, caJObj);
			ans =  ans.replace("\"", "").trim();
			int id = Integer.parseInt(ans);
			return id;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return 0;	
	}

	public int uploadNewCode(Code c){
		try{
			String ans = ServiceData.HttpPostAnswer(URL_ADDCODE, c.toJSON());
			ans =  ans.replace("\"", "").trim();
			int id = Integer.parseInt(ans);
			return id;
		}catch (Exception ex){
			ActivityMain.handleException(ex, true);
		}
		return 0;	
	}



	private List<Integer> createIDList(int[][] startStop){
		Set<Integer> ids = new TreeSet<Integer>();

		boolean nothing = (startStop == null || startStop.length == 0 || startStop[0] == null);
		if (nothing) return new ArrayList<Integer>(ids);

		for (int[] range : startStop){
			for (int i = 0; i < (range[1] - range[0]) + 1; i++){
				ids.add(range[0] + i);
			}			
		}	
		return new ArrayList<Integer>(ids);
	}




	private static final String URL_BASE = "http://otrta.nibdev.com/otrtaservice.php/";

	private static final String URL_GETSERVICESTATE = URL_BASE + "servicestate";



	private static final String URL_ADDVENDOR = URL_BASE + "addvendor";
	private static final String URL_ADDMODEL = URL_BASE + "addmodel";
	private static final String URL_ADDCODEALLOCATION = URL_BASE + "addcodeallocation";
	private static final String URL_ADDCODE = URL_BASE + "addcode";

	private static final String URL_GETFSINFO = URL_BASE + "fastsync/ireq";

	private static final String URL_GETVENDORS = URL_BASE + "vendors";
	private static final String URL_GETDEVICETYPES = URL_BASE + "devicetypes";
	private static final String URL_GETMODELS = URL_BASE + "models";
	private static final String URL_GETCODETYPES = URL_BASE + "codetypes";
	private static final String URL_GETCODES = URL_BASE + "codes";
	private static final String URL_GETCODEALLOCATIONS = URL_BASE + "codeallocations";
	private static final String URL_GETCODETYPEMAPPIMGS = URL_BASE + "codetypemappings";

	public static final String URL_ADDEXCEPTION = URL_BASE + "addexception";
	public static final String URL_CHECKDELETE = URL_BASE + "checkdelete";
	public static final String URL_DODELETE = URL_BASE + "dodelete";
	public static final String URL_GETDBINFO= URL_BASE + "dbinfo";
	public static final String URL_GETAPPVERSION = URL_BASE + "appversion";

}
