package com.kuaibao.skuaidi.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.base.db.SkuaidiBaseDBHelper;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

@SuppressLint("NewApi")
public class SkuaidiNewDBHelper extends SkuaidiBaseDBHelper {
	private static final String DB_PATH = "skuaidiNewDB";
	private static final String BASEDBSQLFILENAME = "skuadi_newDB.sql";
	private SQLiteDatabase database = null;
	private static SkuaidiNewDBHelper dbHelper;

	private static final String DB_NAME = "newSkuaidi.db";
	private static final int version = 22;

	private SkuaidiNewDBHelper(Context context) {
		super(context.getApplicationContext(), DB_NAME, version, DB_PATH, BASEDBSQLFILENAME);
	}

	public synchronized static SkuaidiNewDBHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new SkuaidiNewDBHelper(context.getApplicationContext());
		}
		return dbHelper;
	}

	public synchronized SQLiteDatabase openDatabase() {
		database = this.getWritableDatabase();
		return database;
	}
	
	public synchronized SQLiteDatabase openDatabaseRead(){
		database = this.getReadableDatabase();
		return database;
	}

	public synchronized void closeDatabase() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
		//清空客户管理的同步时间，这样数据库升级后，会重新全量同步客户管理的数据。（尤其是数据库版本从18到19时，客户数据表增加了一个user_id字段)
		if(oldVersion == 18 && newVersion == 19){
			SkuaidiSpf.setCustomerLastSyncTime(SkuaidiSpf.getLoginUser().getUserId(), "");
		}
	}
}
