package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class CacheContactsDao {
	private static SQLiteDatabase db;
	private static String table_name = "cache_contacts";

	/**
	 * insertContact:插入新联系人【如果联系人存在即不作处理】 作者： 顾冬冬
	 * 
	 * @param myCustom
	 */
	public static synchronized void insertContact(MyCustom myCustom) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		if (!checkRepeat(myCustom)) {// 不存在该条
			ContentValues cv = infoToCV(myCustom);
			long id = db.insert(table_name, null, cv);
		}
	}

	/**
	 * getContact:获取所有缓存中的联系人 作者： 顾冬冬
	 * 
	 * @return
	 */
	public static synchronized List<MyCustom> getContact(String phone) {
		List<MyCustom> myCustoms = new ArrayList<MyCustom>();
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		String sql = "";
		if(!Utility.isEmpty(phone)){
			sql = "select * from cache_contacts where phone like'%"+phone+"%'";
		}else{
			sql = "select * from cache_contacts";
		}
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MyCustom myCustom = new MyCustom();
				myCustom.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
				myCustoms.add(myCustom);
			} while (cursor.moveToNext());
		}
		return myCustoms;
	}

	/**
	 * checkRepeat:检查表中是否存在该条号码 作者： 顾冬冬
	 * 
	 * @return
	 */
	private static boolean checkRepeat(MyCustom myCustom) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();

		String selection = "phone=?";
		String[] selectionArgs = { myCustom.getPhone() };
		Cursor cursor = db.query(table_name, null, selection, selectionArgs, null, null, null);
		return cursor.getCount() > 0;
	}

	public static boolean checkRepeat(String phone){
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();

		String selection = "phone=?";
		String[] selectionArgs = { phone };
		Cursor cursor = db.query(table_name, null, selection, selectionArgs, null, null, null);
		return cursor.getCount() > 0;
	}

	private static ContentValues infoToCV(MyCustom info) {
		ContentValues cv = new ContentValues();
		cv.put("phone", info.getPhone());
		// cv.put("name", info.getName());
		// cv.put("address", info.getAddress());
		// cv.put("note", info.getNote());
		// cv.put("time", info.getTime());
		return cv;
	}

}
