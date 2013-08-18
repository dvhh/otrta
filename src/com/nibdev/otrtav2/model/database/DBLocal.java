package com.nibdev.otrtav2.model.database;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;

import com.nibdev.otrtav2.model.jdata.Code;
import com.nibdev.otrtav2.model.jdata.CodeAllocation;
import com.nibdev.otrtav2.model.jdata.CodeType;
import com.nibdev.otrtav2.model.jdata.CodeTypeMapping;
import com.nibdev.otrtav2.model.jdata.DeviceType;
import com.nibdev.otrtav2.model.jdata.FSInfoResponse;
import com.nibdev.otrtav2.model.jdata.Model;
import com.nibdev.otrtav2.model.jdata.Vendor;
import com.nibdev.otrtav2.model.scripts.Script;
import com.nibdev.otrtav2.model.tools.GsonData;
import com.nibdev.otrtav2.model.tools.LowerStringComparator;
import com.nibdev.otrtav2.view.fragments.FragmentLearn.SaveUploadData;


public class DBLocal{

	//callbacks
	public interface OnNewLocalDataListener{
		public void onNewData();
	}
	private static HashSet<OnNewLocalDataListener> mListeners = new HashSet<DBLocal.OnNewLocalDataListener>();;
	public void addOnNewLocalDataListener(OnNewLocalDataListener listener){
		mListeners.add(listener);
	}
	public void removeOnNewLocalDataListener(OnNewLocalDataListener listener){
		mListeners.remove(listener);
	}
	private void notifyListeners(){
		//always run on UI Thread
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				for (OnNewLocalDataListener listener : mListeners){
					if (listener != null){
						listener.onNewData();
					}
				}
			}
		});
	}
	public void notifyYourListeners() {
		notifyListeners();		
	}

	private SQLiteDatabase mDb;
	private DBLocalHelper mHelper;
	private LowerStringComparator mLowerStringComparator = new LowerStringComparator();

	public DBLocal(DBLocalHelper helper){
		//load all entry when db constructs
		mHelper = helper;
		mDb = mHelper.getWritableDatabase();
	}



	public void onDestroy() {
		mDb.close();
		mHelper.close();
		mListeners.clear();
	}

	public List<Script> getAllScripts(){
		Cursor c = mDb.query(TABLE_SCRIPTS, null, null, null, null, null, null);

		int iID = c.getColumnIndex(COLUMN_ID);
		int iData = c.getColumnIndex(COLUMN_SCRIPTDATA);

		List<Script> scripts = new ArrayList<Script>(c.getCount());
		while (c.moveToNext()){
			Script s = GsonData.GSONToObject(Script.class, c.getString(iData));
			s.setId(c.getInt(iID));
			scripts.add(s);
		}
		c.close();
		return scripts;
	}

	public TreeMap<String, Script> getSortedScripts() {
		List<Script> allScripts = getAllScripts();
		TreeMap<String, Script> sortedMap = new TreeMap<String, Script>(mLowerStringComparator);
		for (Script s : allScripts){
			if (sortedMap.containsKey(s.getName())){
				String name = s.getName();
				while (sortedMap.containsKey(name)){
					name += " copy";
				}
				s.setName(name);
				saveUpdateScript(s);
			}
			sortedMap.put(s.getName(), s);
		}
		return sortedMap;
	}

	public Script getScriptById(int scriptId) {
		Cursor c = mDb.query(TABLE_SCRIPTS, null, COLUMN_ID + " =?", new String[]{"" + scriptId}, null, null, null);

		int iID = c.getColumnIndex(COLUMN_ID);
		int iData = c.getColumnIndex(COLUMN_SCRIPTDATA);

		c.moveToFirst();
		Script s = GsonData.GSONToObject(Script.class, c.getString(iData));
		s.setId(c.getInt(iID));
		c.close();

		return s;
	}

	public String getScriptNameById(int scriptId) {
		Script s = getScriptById(scriptId);
		return s.getName();
	}

	public void deleteScript(Script s) {
		mDb.delete(TABLE_SCRIPTS, MessageFormat.format("{0} = ?", COLUMN_ID), new String[]{Integer.toString(s.getId())});
	}

	public void saveUpdateScript(Script s){
		if (s.getId() == -1){
			// next id = highest id + 1
			Cursor c = mDb.query(TABLE_SCRIPTS, new String[]{COLUMN_ID}, null, null, null, null, COLUMN_ID + " DESC", "1");
			if (c.getCount() == 0){
				s.setId(1);
			}else{
				c.moveToFirst();
				s.setId(c.getInt(c.getColumnIndex(COLUMN_ID)) + 1);
			}
			c.close();
		}
		ContentValues values = new ContentValues();
		values.put(COLUMN_SCRIPTDATA, GsonData.ObjectToGSON(s));
		values.put(COLUMN_ID, s.getId());

		long dbid = mDb.insertWithOnConflict(TABLE_SCRIPTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		dbid = dbid * 1;
	}

	
	public List<Map<String, Object>> getAllButtons(){
		
		Cursor c = mDb.query(TABLE_BUTTONS, null, null, null, null, null, COLUMN_NAME);
		int iId = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		int iAlloc = c.getColumnIndex(COLUMN_CODEALLOCATIONID);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(COLUMN_ID, c.getInt(iId));
			data.put(COLUMN_NAME, c.getString(iName));
			data.put(COLUMN_CODEALLOCATIONID, c.getInt(iAlloc));
			dataList.add(data);
			
		}
		c.close();
		
		return dataList;
	}

	public int getButtonAllocation(String button){
		int alloc = -1;
		Cursor c = mDb.query(TABLE_BUTTONS, null, COLUMN_NAME + " LIKE '" + button + "'", null, null, null, null);
		if (c.moveToFirst()){
			alloc = c.getInt(c.getColumnIndex(COLUMN_CODEALLOCATIONID));
		}
		c.close();
		return alloc;
	}

	public void saveButtonAllocation(String button, int allocation){
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, button);
		cv.put(COLUMN_CODEALLOCATIONID, allocation);

		int existing = getButtonAllocation(button);
		if (existing > 0){
			int rowAffectCount = mDb.updateWithOnConflict(TABLE_BUTTONS, cv, COLUMN_NAME + " LIKE '" + button + "'", null, SQLiteDatabase.CONFLICT_REPLACE);
			int p = 3; p = 0 + p;
		}else{
			long insertAffectCount = mDb.insert(TABLE_BUTTONS, null, cv);
			int p = 3; p = 0 + p;
		}
		
	}



	public int getHighestID_SQL(String tableName){
		int hId = 0;
		Cursor c = mDb.query(tableName, new String[]{COLUMN_ID}, null, null, null, null, COLUMN_ID + " desc", "1");
		if (c.moveToFirst()){
			hId = c.getInt(0);
		}
		c.close();
		return hId;
	}

	public int getLowestID_SQL(String tableName){
		int lId = 0;
		Cursor c = mDb.query(tableName, new String[]{COLUMN_ID}, null, null, null, null, COLUMN_ID, "1");
		if (c.moveToFirst()){
			lId = c.getInt(0);
		}
		c.close();
		return lId;
	}

	public int getCount_SQL(String tableName){
		return getCount_SQL(tableName, null, null);
	}

	public int getCount_SQL(String tableName, String selection, String[] selectionArgs){
		int count = (int)DatabaseUtils.queryNumEntries(mDb, tableName, selection, selectionArgs);
		return count;
	}


	public SparseArray<Set<Integer>> getCodeTypeMappings() {
		Cursor c = mDb.query(TABLE_CODETYPEMAPPINGS, new String[]{COLUMN_DEVTYPEID, COLUMN_CODETYPEID}, null, null, null, null, null);
		int iDevTypeID = c.getColumnIndex(COLUMN_DEVTYPEID);
		int iCodeTypeID = c.getColumnIndex(COLUMN_CODETYPEID);
		SparseArray<Set<Integer>> codeTypeMappings = new SparseArray<Set<Integer>>(c.getCount());
		while (c.moveToNext()){
			int dtId = c.getInt(iDevTypeID);
			int ctId = c.getInt(iCodeTypeID);
			if (codeTypeMappings.indexOfKey(dtId) < 0){
				codeTypeMappings.put(dtId, new HashSet<Integer>());
			}
			codeTypeMappings.get(dtId).add(ctId);
		}
		c.close();
		return codeTypeMappings;
	}

	public int getDeviceTypeForModelId(int modelId) {
		Cursor c = mDb.query(TABLE_MODELS, null, COLUMN_ID + " =?", new String[]{"" + modelId}, null, null, null);
		int iDevTypeID = c.getColumnIndex(COLUMN_DEVTYPEID);
		int devtypId = -1;
		if (c.moveToFirst()){
			devtypId = c.getInt(iDevTypeID);
		}
		c.close();
		return devtypId;
	}


	public TreeMap<String, Integer> getSortedVendors() {
		Cursor c = mDb.query(TABLE_VENDORS, null, null, null, null, null, COLUMN_NAME);
		TreeMap<String, Integer> vendors = new TreeMap<String, Integer>(mLowerStringComparator);
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		while (c.moveToNext()){
			vendors.put(c.getString(iName), c.getInt(iID));
		}
		c.close();
		return vendors;
	}

	public TreeMap<String, Integer> getSortedDeviceTypes() {
		Cursor c = mDb.query(TABLE_DEVICETYPES, null, null, null, null, null, COLUMN_NAME + " COLLATE NOCASE");
		TreeMap<String, Integer> devtypes = new TreeMap<String, Integer>();
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		while (c.moveToNext()){
			devtypes.put(c.getString(iName), c.getInt(iID));
		}
		c.close();
		return devtypes;
	}

	public TreeMap<String, Integer> getSortedCodeTypes() {
		Cursor c = mDb.query(TABLE_CODETYPES, null, null, null, null, null, COLUMN_NAME + " COLLATE NOCASE");
		TreeMap<String, Integer> codetypes = new TreeMap<String, Integer>();
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		while (c.moveToNext()){
			codetypes.put(c.getString(iName), c.getInt(iID));
		}
		c.close();
		return codetypes;
	}

	public TreeMap<String, Integer> getSortedModels() {
		Cursor c = mDb.query(TABLE_MODELS, null, null, null, null, null, COLUMN_NAME + " COLLATE NOCASE");
		TreeMap<String, Integer> models = new TreeMap<String, Integer>();
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		while (c.moveToNext()){
			models.put(c.getString(iName), c.getInt(iID));
		}
		c.close();
		return models;
	}


	public List<Map<String, Object>> getVendorsWithModelCounts(){
		Cursor c = mDb.query(TABLE_VENDORS, new String[]{COLUMN_ID, COLUMN_NAME, QUERY_VENDOR_MODELCOUNT}, null, null, null, null, COLUMN_NAME + " COLLATE NOCASE");
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		int iModCount = c.getColumnIndex(COLUMN_MODELCOUNT);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()){
			Map<String, Object> vendMap = new HashMap<String, Object>(3);
			vendMap.put(COLUMN_ID, c.getLong(iID));
			vendMap.put(COLUMN_NAME, c.getString(iName));
			vendMap.put(COLUMN_MODELCOUNT, c.getInt(iModCount));
			dataList.add(vendMap);
		}
		c.close();

		return dataList;
	}

	public List<Map<String, Object>> getVendors(){
		Cursor c = mDb.query(TABLE_VENDORS, null, null, null, null, null, COLUMN_NAME + " COLLATE NOCASE");
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()){
			Map<String, Object> vendMap = new HashMap<String, Object>(3);
			vendMap.put(COLUMN_ID, c.getLong(iID));
			vendMap.put(COLUMN_NAME, c.getString(iName));
			dataList.add(vendMap);
		}
		c.close();

		return dataList;
	}

	public int getVendorIdByModelId(int modelId) {
		Cursor c = mDb.query(TABLE_MODELS, null, COLUMN_ID + " =?", new String[]{"" + modelId}, null, null, null);
		int iVendID = c.getColumnIndex(COLUMN_VENID);
		int vendorId = -1;
		if (c.moveToFirst()){
			vendorId = c.getInt(iVendID);
		}
		c.close();
		return vendorId;
	}

	public List<Map<String, Object>> getCodeAllocationsForModelId(int modelId){
		Cursor c = mDb.query(TABLE_CODEALLOCATIONS, null, COLUMN_MODELID + "= ?",  new String[]{"" + modelId}, null, null, null);
		int iID = c.getColumnIndex(COLUMN_ID);
		int iModelId = c.getColumnIndex(COLUMN_MODELID);
		int iCodeId = c.getColumnIndex(COLUMN_CODEID);
		int iCodeTypeId = c.getColumnIndex(COLUMN_CODETYPEID);


		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()){
			Map<String, Object> caMap = new HashMap<String, Object>(3);
			caMap.put(COLUMN_ID, c.getLong(iID));
			caMap.put(COLUMN_MODELID, c.getInt(iModelId));
			caMap.put(COLUMN_CODEID, c.getInt(iCodeId));
			caMap.put(COLUMN_CODETYPEID, c.getInt(iCodeTypeId));

			dataList.add(caMap);
		}
		c.close();

		return dataList;
	}

	public List<Map<String, Object>> getModels(String selection, String[] selectionArgs) {
		Cursor c = mDb.query(TABLE_MODELS, null, selection, selectionArgs, null, null, COLUMN_NAME + " COLLATE NOCASE");
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		int iVendId = c.getColumnIndex(COLUMN_VENID);
		int iDevTypeId = c.getColumnIndex(COLUMN_DEVTYPEID);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()){
			Map<String, Object> modmap = new HashMap<String, Object>(3);
			modmap.put(COLUMN_ID, c.getLong(iID));
			modmap.put(COLUMN_NAME, c.getString(iName));
			modmap.put(COLUMN_VENID, c.getInt(iVendId));
			modmap.put(COLUMN_DEVTYPEID, c.getInt(iDevTypeId));
			dataList.add(modmap);
		}
		c.close();

		return dataList;
	}

	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, List<Map<String, Object>>> getModelsWithDeviceTypedAndCodeCount(int vendorId){
		List<Map<String, Object>> models = getModels(COLUMN_VENID + "= ?", new String[]{"" + vendorId});
		HashMap<Integer, List<Map<String, Object>>> data = new HashMap<Integer, List<Map<String,Object>>>();
		for (Map<String, Object> model : models){
			model.put(COLUMN_CODECOUNT, getCount_SQL(TABLE_CODEALLOCATIONS, COLUMN_MODELID + "= ?", new String[]{"" + model.get(COLUMN_ID)}));
			int devTypeId = (Integer) model.get(COLUMN_DEVTYPEID);
			if (!data.containsKey(devTypeId)){
				data.put(devTypeId, new ArrayList<Map<String, Object>>());
			}
			data.get(devTypeId).add(model);
		}
		return data;
	}

	public String getModelNameAndCodeTypeNameByCodeAllocationId(int value) {
		int modelId = 0;
		int codeTypeId = 0;
		Cursor c = mDb.query(TABLE_CODEALLOCATIONS, new String[]{COLUMN_MODELID, COLUMN_CODETYPEID}, COLUMN_ID + " =?", new String[]{"" + value}, null, null, null);
		if (c.moveToFirst()){
			modelId = c.getInt(0);
			codeTypeId = c.getInt(1);
		}
		c.close();

		String modelName = "";
		String ctName = "";

		if (modelId > 0){
			c = mDb.query(TABLE_MODELS, new String[]{COLUMN_NAME}, COLUMN_ID + " =?", new String[]{"" + modelId}, null, null, null);
			if (c.moveToFirst()){
				modelName = c.getString(0);
			}
			c.close();
		}

		if (codeTypeId > 0){
			c = mDb.query(TABLE_CODETYPES, new String[]{COLUMN_NAME}, COLUMN_ID + " =?", new String[]{"" + codeTypeId}, null, null, null);
			if (c.moveToFirst()){
				ctName = c.getString(0);
			}
			c.close();
		}
		return modelName + "\n" + ctName;
	}

	public Map<String, Object> getModelFromCodeAllocationId(int caId) {
		int modelId = -1;
		Cursor c = mDb.query(TABLE_CODEALLOCATIONS, new String[]{COLUMN_MODELID}, COLUMN_ID + " =?", new String[]{"" + caId}, null, null, null);
		if (c.moveToFirst()){
			modelId = c.getInt(0);
		}
		c.close();

		return getModels(COLUMN_ID + " =?", new String[]{"" + modelId}).get(0);
	}




	public SparseArray<String> getDeviceTypes(){
		Cursor c = mDb.query(TABLE_DEVICETYPES, null, null, null, null, null, null);
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		SparseArray<String> data = new SparseArray<String>(c.getCount());
		while (c.moveToNext()){
			data.put(c.getInt(iID), c.getString(iName));
		}
		c.close();

		return data;
	}

	public SparseArray<String> getCodeTypes(){
		Cursor c = mDb.query(TABLE_CODETYPES, null, null, null, null, null, null);
		int iID = c.getColumnIndex(COLUMN_ID);
		int iName = c.getColumnIndex(COLUMN_NAME);
		SparseArray<String> data = new SparseArray<String>(c.getCount());
		while (c.moveToNext()){
			data.put(c.getInt(iID), c.getString(iName));
		}
		c.close();

		return data;
	}


	public Code getCodeById(int codeId) {
		Cursor c = mDb.query(TABLE_CODES, null, COLUMN_ID +"= ?", new String[]{codeId + ""}, null, null, null);
		int iID = c.getColumnIndex(COLUMN_ID);
		int iData = c.getColumnIndex(COLUMN_CODEDATA);
		if (c.getCount() > 0){
			c.moveToFirst();
			Code retCode = new Code(c.getInt(iID), c.getString(iData));
			c.close();
			retCode.getData();
			return retCode;
		}else{
			return null;
		}
	}

	public Code getCodeByCodeAllocationId(int codeAllocationId) {
		Cursor c = mDb.query(TABLE_CODEALLOCATIONS, new String[]{COLUMN_CODEID}, COLUMN_ID +"= ?", new String[]{codeAllocationId + ""}, null, null, null);
		int iCodeId = c.getColumnIndex(COLUMN_CODEID);
		int codeId = -1;
		if (c.moveToFirst()){
			codeId = c.getInt(iCodeId);
		}
		c.close();
		if (codeId > -1){
			return getCodeById(codeId);
		}
		return null;
	}

	public List<Code> getCodesByCodeTypeId(int codeTypeId) {
		Cursor c = mDb.query(TABLE_CODEALLOCATIONS, new String[]{COLUMN_CODEID}, COLUMN_CODETYPEID +"= ?", new String[]{codeTypeId + ""}, null, null, null);
		int iCodeId = c.getColumnIndex(COLUMN_CODEID);
		List<Integer> codeIds = new ArrayList<Integer>(c.getCount());
		while (c.moveToNext()){
			codeIds.add(c.getInt(iCodeId));
		}
		c.close();

		String inString = "(" + TextUtils.join(",", codeIds) + ")";
		c = mDb.query(TABLE_CODES, null, COLUMN_ID + " in " + inString, null, null, null, null);
		int iID = c.getColumnIndex(COLUMN_ID);
		int iData = c.getColumnIndex(COLUMN_CODEDATA);
		List<Code> retCodes = new ArrayList<Code>(c.getCount());
		while (c.moveToNext()){
			retCodes.add(new Code(c.getInt(iID), c.getString(iData)));
		}
		c.close();
		return retCodes;
	}



	public void deleteGaps(FSInfoResponse infoResp){
		int[][][] tableGaps = new int [][][]{infoResp.getCodeAllocationDeleteGaps(), infoResp.getCodeDeleteGaps(), infoResp.getCodeTypeDeleteGaps(), infoResp.getDeviceTypeDeleteGaps(), infoResp.getModelDeleteGaps(), infoResp.getVendorDeleteGaps(), infoResp.getCodeTypeMappingDeleteGaps()}; 
		String[] tableNames = new String[]{TABLE_CODEALLOCATIONS, TABLE_CODES, TABLE_CODETYPES, TABLE_DEVICETYPES, TABLE_MODELS, TABLE_VENDORS, TABLE_CODETYPEMAPPINGS}; 
		mDb.beginTransaction();
		try {
			for (int i = 0; i < tableNames.length; i++){
				String tname = tableNames[i];
				int[][] gaps = tableGaps[i];
				if (gaps != null){
					for (int[] gaprange : gaps){
						if (gaprange != null && gaprange[0]+gaprange[1] > 0){
							int deletedCount = mDb.delete(tname, COLUMN_ID + " >= ? AND " + COLUMN_ID + " <= ?" , new String[]{Integer.toString(gaprange[0]), Integer.toString(gaprange[1])});
							deletedCount = deletedCount + deletedCount;
						}
					}
				}
			}
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
	}

	public int deleteCodeAllocation(int deleteId) {
		int delCount = mDb.delete(TABLE_CODEALLOCATIONS, COLUMN_ID + " = ?", new String[]{"" + deleteId});
		return delCount;
	}



	public void addVendors(List<Vendor> vendors){

		mDb.beginTransaction();
		try{
			for (Vendor remoteVend : vendors){
				ContentValues newLocalVendor = new ContentValues();
				newLocalVendor.put(COLUMN_ID, remoteVend.getID());
				newLocalVendor.put(COLUMN_NAME, remoteVend.getName());
				mDb.insertWithOnConflict(TABLE_VENDORS, null, newLocalVendor, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}

	public void addModels(List<Model> models){

		mDb.beginTransaction();
		try{
			for (Model remoteModel : models){
				ContentValues newLocalModel = new ContentValues();
				newLocalModel.put(COLUMN_ID, remoteModel.getID());
				newLocalModel.put(COLUMN_VENID, remoteModel.getVendorID());
				newLocalModel.put(COLUMN_DEVTYPEID, remoteModel.getDeviceTypeID());
				newLocalModel.put(COLUMN_NAME, remoteModel.getName());
				mDb.insertWithOnConflict(TABLE_MODELS, null, newLocalModel, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}

	public void addDeviceTypes(List<DeviceType> devtypes){

		mDb.beginTransaction();
		try{
			for (DeviceType remoteDevType : devtypes){
				ContentValues newLocalDevType = new ContentValues();
				newLocalDevType.put(COLUMN_ID, remoteDevType.getID());
				newLocalDevType.put(COLUMN_NAME, remoteDevType.getName());
				mDb.insertWithOnConflict(TABLE_DEVICETYPES, null, newLocalDevType, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}

	public void addCodeTypes(List<CodeType> codetypes){

		mDb.beginTransaction();
		try{
			for (CodeType remoteDevType : codetypes){
				ContentValues newLocalCodeType = new ContentValues();
				newLocalCodeType.put(COLUMN_ID, remoteDevType.getID());
				newLocalCodeType.put(COLUMN_NAME, remoteDevType.getName());
				mDb.insertWithOnConflict(TABLE_CODETYPES, null, newLocalCodeType, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}

	public void addCodes(List<Code> codes){

		mDb.beginTransaction();
		try{
			for (Code remoteCode : codes){
				ContentValues newLocalCode = new ContentValues();
				newLocalCode.put(COLUMN_ID, remoteCode.getID());
				newLocalCode.put(COLUMN_CODEDATA, remoteCode.getDataAsString());
				mDb.insertWithOnConflict(TABLE_CODES, null, newLocalCode, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}

	public void addCodeAllocations(List<CodeAllocation> codeAllocs) {
		mDb.beginTransaction();
		try{
			for (CodeAllocation remoteCodeAlloc : codeAllocs){
				ContentValues newLocalCodeAlloc = new ContentValues();
				newLocalCodeAlloc.put(COLUMN_ID, remoteCodeAlloc.getID());
				newLocalCodeAlloc.put(COLUMN_CODEID, remoteCodeAlloc.getCodeID());
				newLocalCodeAlloc.put(COLUMN_CODETYPEID, remoteCodeAlloc.getCodeTypeID());
				newLocalCodeAlloc.put(COLUMN_MODELID, remoteCodeAlloc.getModelID());
				mDb.insertWithOnConflict(TABLE_CODEALLOCATIONS, null, newLocalCodeAlloc, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}

	public void addCodeTypeMappings(List<CodeTypeMapping> codeTypeMappings) {
		mDb.beginTransaction();
		try{
			for (CodeTypeMapping remoteMapping : codeTypeMappings){
				ContentValues newLocalCodeAlloc = new ContentValues();
				newLocalCodeAlloc.put(COLUMN_ID, remoteMapping.getID());
				newLocalCodeAlloc.put(COLUMN_DEVTYPEID, remoteMapping.getDeviceTypeID());
				newLocalCodeAlloc.put(COLUMN_CODETYPEID, remoteMapping.getCodeTypeID());
				mDb.insertWithOnConflict(TABLE_CODETYPEMAPPINGS, null, newLocalCodeAlloc, SQLiteDatabase.CONFLICT_REPLACE);
			}
			mDb.setTransactionSuccessful();
		}catch (Exception ex){
			ex.printStackTrace();
		}finally{
			mDb.endTransaction();
		}
	}


	public void checkUploadResult(SaveUploadData uploadedData) {
		//CheckInsert Vendor

		Vendor v = uploadedData.vend;
		if (DatabaseUtils.queryNumEntries(mDb, TABLE_VENDORS, MessageFormat.format("{0} = {1,number,#}", COLUMN_ID, v.getID())) == 0){
			ContentValues cv = new ContentValues(1);
			cv.put(COLUMN_ID, v.getID());
			cv.put(COLUMN_NAME, v.getName());
			mDb.insert(TABLE_VENDORS, null, cv);

		}

		//CheckInsert Model
		Model m = uploadedData.mod;
		if (DatabaseUtils.queryNumEntries(mDb, TABLE_MODELS, MessageFormat.format("{0} = {1,number,#}", COLUMN_ID, m.getID())) == 0){
			ContentValues cv = new ContentValues(1);
			cv.put(COLUMN_ID, m.getID());
			cv.put(COLUMN_NAME, m.getName());
			cv.put(COLUMN_VENID, m.getVendorID());
			cv.put(COLUMN_DEVTYPEID, m.getDeviceTypeID());
			mDb.insert(TABLE_MODELS, null, cv);

		}

		//CheckInsert CodeType
		//NotYet

		//CheckInsert DeviceType
		//NotYed


		//CheckInsert Code
		Code c = uploadedData.codedata;
		if (DatabaseUtils.queryNumEntries(mDb, TABLE_CODES, MessageFormat.format("{0} = {1,number,#}", COLUMN_ID, c.getID())) == 0){
			ContentValues cv = new ContentValues(1);
			cv.put(COLUMN_ID, c.getID());
			cv.put(COLUMN_CODEDATA, c.getDataAsString());
			mDb.insert(TABLE_CODES, null, cv);
		}

		//CheckInsert CodeAllocation
		CodeAllocation ca = uploadedData.ca;
		if (DatabaseUtils.queryNumEntries(mDb, TABLE_CODEALLOCATIONS, MessageFormat.format("{0} = {1,number,#}", COLUMN_ID, ca.getID())) == 0){
			ContentValues cv = new ContentValues(1);
			cv.put(COLUMN_ID, ca.getID());
			cv.put(COLUMN_CODEID, ca.getCodeID());
			cv.put(COLUMN_MODELID, ca.getModelID());
			cv.put(COLUMN_CODETYPEID, ca.getCodeTypeID());
			mDb.insert(TABLE_CODEALLOCATIONS, null, cv);
		}

		notifyListeners();


	}




	public void purge() {
		mDb.delete(TABLE_CODEALLOCATIONS, null, null);
		mDb.delete(TABLE_CODES, null, null);
		mDb.delete(TABLE_CODETYPEMAPPINGS, null, null);
		mDb.delete(TABLE_CODETYPES, null, null);
		mDb.delete(TABLE_DEVICETYPES, null, null);
		mDb.delete(TABLE_MODELS, null, null);
		mDb.delete(TABLE_VENDORS, null, null);

	}




	//Table Manufacters
	public static final String TABLE_VENDORS = "vendors";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String TVENDORCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} text not null);", TABLE_VENDORS, COLUMN_ID, COLUMN_NAME);

	//Table Models
	public static final String TABLE_MODELS = "models";
	//public static final String COLUMN_ID = "id";
	public static final String COLUMN_VENID = "vendor_id";
	public static final String COLUMN_DEVTYPEID = "devicetype_id";
	//public static final String COLUMN_NAME = "name";
	public static final String TMODELSCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} integer not null, {3} integer not null, {4} text not null);", TABLE_MODELS, COLUMN_ID, COLUMN_VENID, COLUMN_DEVTYPEID, COLUMN_NAME);

	//Table CodeTypes
	public static final String TABLE_DEVICETYPES = "devicetypes";
	//public static final String COLUMN_ID = "id";
	//public static final String COLUMN_NAME = "name";
	public static final String TDEVICETYPESCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} text not null);", TABLE_DEVICETYPES, COLUMN_ID, COLUMN_NAME);

	//Table CodeTypes
	public static final String TABLE_CODETYPES = "codetypes";
	//public static final String COLUMN_ID = "id";
	//public static final String COLUMN_NAME = "name";
	public static final String TCODETYPESCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} text not null);", TABLE_CODETYPES, COLUMN_ID, COLUMN_NAME);

	//Table Codes
	public static final String TABLE_CODES = "codes";
	//public static final String COLUMN_ID = "id";
	public static final String COLUMN_CODEDATA = "codedata";
	public static final String TCODESCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} text not null);", TABLE_CODES, COLUMN_ID, COLUMN_CODEDATA);

	//Table CodeAllocations
	public static final String TABLE_CODEALLOCATIONS = "codeallocations";
	//public static final String COLUMN_ID = "id";
	public static final String COLUMN_MODELID = "model_id";
	public static final String COLUMN_CODEID = "code_id";
	public static final String COLUMN_CODETYPEID = "codetype_id";
	public static final String TCODEALLOCATIONSCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} integer not null, {3} integer not null, {4} integer not null);", TABLE_CODEALLOCATIONS, COLUMN_ID, COLUMN_MODELID, COLUMN_CODEID, COLUMN_CODETYPEID);

	//Table CodeAllocations
	public static final String TABLE_CODETYPEMAPPINGS = "codetypemappings";
	//public static final String COLUMN_ID = "id";
	//public static final String COLUMN_DEVTYPEID = "devicetype_id";
	//public static final String COLUMN_CODETYPEID = "codetype_id";
	public static final String TCODETYPEMAPPINGSCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} integer not null, {3} integer not null);", TABLE_CODETYPEMAPPINGS, COLUMN_ID, COLUMN_DEVTYPEID, COLUMN_CODETYPEID);


	//LOCALONLYTABLES
	//
	//	//Table UserLayouts
	//	public static final String TABLE_USERLAYOUTS = "userlayouts";
	//	//public static final String COLUMN_ID = "id";
	//	public static final String COLUMN_LAYOUTDATA = "layoutdata";
	//	public static final String TUSERLAYOUTSSCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} text not null);", TABLE_USERLAYOUTS, COLUMN_ID, COLUMN_LAYOUTDATA);

	//Table Scripts
	public static final String TABLE_SCRIPTS = "scripts";
	//public static final String COLUMN_ID = "id";
	public static final String COLUMN_SCRIPTDATA = "scriptdata";
	public static final String TSCRIPTSCREATE = MessageFormat.format("create table {0} ({1} integer primary key not null, {2} text not null);", TABLE_SCRIPTS, COLUMN_ID, COLUMN_SCRIPTDATA);

	//Table Button Text Mappings
	public static final String TABLE_BUTTONS = "buttons";
	//public static final String COLUMN_ID = "id";
	//public static final String COLUMN_NAME = "name";
	public static final String COLUMN_CODEALLOCATIONID = "codealloc_id";
	public static final String TBUTTONMAPPINGCREATE = String.format("create table %s (%s integer primary key not null, %s text not null, %s integer not null);", TABLE_BUTTONS, COLUMN_ID, COLUMN_NAME, COLUMN_CODEALLOCATIONID);


	//	//SPECIAL COLUMNS
	public static final String COLUMN_MODELCOUNT = "modelcount";
	public static final String QUERY_VENDOR_MODELCOUNT = String.format("(SELECT COUNT(*) FROM %s WHERE %s.%s = %s.%s)'%s'", TABLE_MODELS, TABLE_MODELS, COLUMN_VENID, TABLE_VENDORS, COLUMN_ID, COLUMN_MODELCOUNT); 
	//
	public static final String COLUMN_CODECOUNT = "codecount";
	//	public static final String QUERY_MODEL_CODEOUNT = String.format("(SELECT COUNT(*) FROM %s WHERE %s.%s = %s.%s)'%s'", TABLE_CODEALLOCATIONS, TABLE_CODEALLOCATIONS, COLUMN_MODELID, TABLE_MODELS, COLUMN_ID, COLUMN_CODECOUNT);
	//






}
