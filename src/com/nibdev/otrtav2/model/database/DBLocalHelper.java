package com.nibdev.otrtav2.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBLocalHelper extends SQLiteOpenHelper{

	public static final String DB_NAME = "otrta.sqldb";
	private static final int DB_VERSION = 2;


	public DBLocalHelper(Context c){
		super(c, DB_NAME, null, DB_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		createAllTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2){
			db.execSQL(DBLocal.TBUTTONMAPPINGCREATE);
		}
	}

	private void createAllTables(SQLiteDatabase db){
		db.execSQL(DBLocal.TVENDORCREATE);
		db.execSQL(DBLocal.TMODELSCREATE);
		db.execSQL(DBLocal.TCODETYPESCREATE);
		db.execSQL(DBLocal.TCODESCREATE);
		db.execSQL(DBLocal.TCODEALLOCATIONSCREATE);
		db.execSQL(DBLocal.TDEVICETYPESCREATE);
		db.execSQL(DBLocal.TCODETYPEMAPPINGSCREATE);

		db.execSQL(DBLocal.TSCRIPTSCREATE);
		
		db.execSQL(DBLocal.TBUTTONMAPPINGCREATE);
	}
	
	public void purgeNotUserTables(){
		
	}

}
