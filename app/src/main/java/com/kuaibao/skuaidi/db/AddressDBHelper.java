package com.kuaibao.skuaidi.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class AddressDBHelper {
	String dbpath = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/com.kuaibao.skuaidi/databases";
	SQLiteDatabase database = null;
	
	public SQLiteDatabase openDatabase() {
		database = SQLiteDatabase.openDatabase(dbpath + "/address3.db", null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return database;
	}
	
	public void closeDatabase(){
		if (this.database != null) { 
			database.close();
		}
	}
}
