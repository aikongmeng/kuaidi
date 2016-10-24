package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.SaveNoEntry;
import com.kuaibao.skuaidi.util.Utility;

/**
 * 类名: SaveNoDao 保存编号工具【此】 
 * 方法: TODO  <br/> 
 * 原理: TODO  <br/> 
 * 时间: 2016-1-14 下午3:47:32 <br/> 
 * 
 * 作者： 顾冬冬 
 * 版本：
 */
public class SaveNoDAO {

	private static SQLiteDatabase db;
	private static String table_name = "save_no";
	public static final String NO_SMS = "sms";
	public static final String NO_CLOUD = "cloud";
	public static final String NO_SMS_BACHSIGN = "sms_back_sign";
	public static final String NO_CLOUD_BACHSIGN = "cloud_back_sign";
	
	public static synchronized void saveNo(SaveNoEntry saveNoEntry){
		if(saveNoEntry == null){
			return ;
		}
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		ContentValues cv = objectToCV(saveNoEntry);
		if(isExistSaveNo(saveNoEntry)){
			String whereClause = "save_from=?";
			String[] whereArgs ={saveNoEntry.getSave_from()};
			int db_update_id = db.update(table_name, cv, whereClause, whereArgs);
			//Log.i("gudd_db", db_update_id+"");
		}else{
			long db_insert_id = db.insert(table_name, null, cv);
			//Log.i("gudd_db", db_insert_id+"");
		}
	}
	
	/**
	 * getSaveNo:获取保存的编号
	 * 
	 * 作者： 顾冬冬
	 * @param save_from【来源->短信：sms|云呼：cloud】
	 */
	public static synchronized SaveNoEntry getSaveNo(String save_from){
		if(Utility.isEmpty(save_from)){
			return null;
		}
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		String selection = "save_from = ?";
		String[] selectionArgs = {save_from};
		Cursor cursor = db.query(table_name, null, selection, selectionArgs, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			SaveNoEntry saveNoEntry = new SaveNoEntry();
			saveNoEntry.setSave_from(cursor.getString(cursor.getColumnIndex("save_from")));
			saveNoEntry.setSave_letter(cursor.getString(cursor.getColumnIndex("save_letter")));
			saveNoEntry.setSave_number(cursor.getInt(cursor.getColumnIndex("save_number")));
			saveNoEntry.setSaveTime(Long.parseLong(cursor.getString(cursor.getColumnIndex("save_time"))));
			saveNoEntry.setSave_userPhone(cursor.getString(cursor.getColumnIndex("save_userPhone")));
			return saveNoEntry;
		}
		return null;
	}
	
	/**判断是否保存了数据**/
	public static synchronized boolean isExistSaveNo(SaveNoEntry saveNoEntry){
		if(saveNoEntry == null){
			return false;
		}
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		String selection = "save_from=?";
		String selectionArgs[] = {saveNoEntry.getSave_from()};
		Cursor cursor = db.query(table_name, null, selection, selectionArgs, null, null, null);
		return cursor.getCount() > 0;
	}
	
	private static ContentValues objectToCV(SaveNoEntry saveNoEntry){
		ContentValues cv = new ContentValues();
		cv.put("save_from", saveNoEntry.getSave_from());
		cv.put("save_time", saveNoEntry.getSaveTime());
		cv.put("save_letter", saveNoEntry.getSave_letter());
		cv.put("save_number", saveNoEntry.getSave_number());
		cv.put("save_userPhone", saveNoEntry.getSave_userPhone());
		return cv;
	}
	
	
	
}
