package com.nibdev.otrtav2.model.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB2 extends SQLiteOpenHelper {

	public static final String DB_NAME = "db2.sqldb";
	private static final int DB_VERSION = 1;


	private SQLiteDatabase mDb;

	public DB2(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

	public void onDestroy() {
		mDb.close();
		close();
	}

	public List<Map<String, Object>> getSortedVendorsForDeviceType(int devtype) {
		Cursor c = mDb.query(TABLE_ALLOCATIONS, new String[]{COLUMN_VENDORID}, COLUMN_DEVICETYPE + "=?", new String[]{"" + devtype}, null, null, null);
		int iVendId = c.getColumnIndex(COLUMN_VENDORID);
		TreeMap<String, Map<String, Object>> allData = new TreeMap<String, Map<String, Object>>();
		HashSet<Integer> found = new HashSet<Integer>();
		while(c.moveToNext()){
			int vid = c.getInt(iVendId);
			if (!found.contains(vid)){
				Map<String, Object> vdata = new HashMap<String, Object>();
				vdata.put(COLUMN_VENDORID, vid);
				String name = getVendorNameByVendorId(vid);
				vdata.put(COLUMN_NAME, getVendorNameByVendorId(vid));
				allData.put(name, vdata);
				found.add(vid);
			}
		}
		c.close();
		return new ArrayList<Map<String, Object>>(allData.values());
	}


	public String getVendorNameByVendorId(int vendId){
		Cursor c = mDb.query(TABLE_VENDORS, new String[]{COLUMN_NAME}, COLUMN_ID + "=?", new String[]{"" + vendId}, null, null, null);
		c.moveToFirst();
		String name = c.getString(0);
		c.close();
		return name;
	}

	

	public List<Map<String, Object>> getCodeSetsForVendor(int vendorId, int devicetypeid) {
		Cursor c = mDb.query(TABLE_ALLOCATIONS, new String[]{COLUMN_CODESETID}, COLUMN_VENDORID + "=? AND " + COLUMN_DEVICETYPE + "=?", new String[]{"" + vendorId, "" + devicetypeid}, null, null, null);
		int iCodeSetId = c.getColumnIndex(COLUMN_CODESETID);
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>(c.getCount());
		while (c.moveToNext()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(COLUMN_CODESETID, c.getInt(iCodeSetId));
			data.add(map);
		}
		c.close();
		return data;
	}
	
	
	public List<Map<String, Object>> getCodesForCodeSetId(int codesetId) {
		Cursor c = mDb.query(TABLE_CODES, null, COLUMN_CODESETID + "=?", new String[]{"" + codesetId}, null, null, null);
		int iData = c.getColumnIndex(COLUMN_CODEDATA);
		int iCodeTypeId = c.getColumnIndex(COLUMN_CODETYPEID);
		TreeMap<String, Map<String, Object>> data = new TreeMap<String, Map<String,Object>>();
		while (c.moveToNext()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(COLUMN_CODEDATA, c.getString(iData));
			int ctid = c.getInt(iCodeTypeId);
			String codename = getCodeNameForCodeType(ctid);
			map.put(COLUMN_NAME, codename);
			data.put(codename, map);
		}
		c.close();
		return new ArrayList<Map<String, Object>>(data.values());
	}
	
	public String getCodeNameForCodeType(int codetypeId){
		Cursor c = mDb.query(TABLE_CODETYPES, new String[]{COLUMN_NAME}, COLUMN_ID + "=?", new String[]{"" + codetypeId}, null, null, null);
		c.moveToFirst();
		String name = c.getString(0);
		c.close();
		return name;
	}




	//allocs
	public static final String TABLE_ALLOCATIONS = "allocations";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_VENDORID = "vendor_id";
	public static final String COLUMN_DEVICETYPE = "devicetype";
	public static final String COLUMN_CODESETID = "codeset_id";


	//codetypes
	public static final String TABLE_CODETYPES = "codetypes";
	//public static final String COLUMN_ID = "_id";
	//public static final String COLUMN_NAME = "NAME";
	
	//codes
	public static final String TABLE_CODES = "codes";
	//public static final String COLUMN_ID = "_id";
	//public static final String COLUMN_CODESETID = "codeset_id";
	public static final String COLUMN_CODEDATA = "codedata";
	public static final String COLUMN_CODETYPEID = "codetype_id";

	
	//vendors
	public static final String TABLE_VENDORS = "vendors";
	//public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "NAME";

	

}
