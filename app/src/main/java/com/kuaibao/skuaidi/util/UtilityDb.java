package com.kuaibao.skuaidi.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UtilityDb {
	/**
	 * 检查db中指定表是否存在
	 * @param db
	 * @param tableName 表名
	 * @return
	 */
	public static boolean checkTableExists(SQLiteDatabase db,String tableName){
		boolean result = false;
		Cursor cursor = null;
		try {
			String sql = "select sql from sqlite_master where type='table' and tbl_name=?";
			cursor = db.rawQuery(sql, new String[] { tableName });
			if (cursor.moveToNext()) {
				String createSql = cursor.getString(0);
				if(true == Utility.isEmpty(createSql)){
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}
	
	/**
	 * 检查db表中某列是否存在
	 * 
	 * @param db
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return
	 */
	public static boolean checkColumnExists(SQLiteDatabase db, String tableName,
			String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			String sql = "select sql from sqlite_master where type='table' and tbl_name=?";
			cursor = db.rawQuery(sql, new String[] { tableName });
			if (cursor.moveToNext()) {
				String createSql = cursor.getString(0);
				if (!Utility.isEmpty(createSql)) {
					if (createSql.contains(columnName)) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}
	
	/**
	 * 判断表是否存在
	 * 
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static boolean tabbleIsExist(SQLiteDatabase db, String tableName) {
		boolean result = false;
		if (Utility.isEmpty(tableName)) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
