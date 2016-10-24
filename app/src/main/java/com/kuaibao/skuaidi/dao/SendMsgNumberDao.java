package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.SendMsgNumberEntry;

public class SendMsgNumberDao {

	private static SQLiteDatabase db;
	private static String table_name = "send_msg_number";

	/** 插入新数据 **/
	public static synchronized void insertSendMsgData(SendMsgNumberEntry info) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		ContentValues cv = infoToCV(info);
		db.insert(table_name, null, cv);
	}

	/** 根据ID更新编号 **/
	public static synchronized void updateNo(String sms_id, String _id, String no) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ? and _id = ?";
		String[] whereArgs = { sms_id, _id };
		ContentValues cv = new ContentValues();
		cv.put("no", no);
		db.update(table_name, cv, whereClause, whereArgs);

	}

	private static ContentValues infoToCV(SendMsgNumberEntry info) {
		ContentValues cv = new ContentValues();
		cv.put("sms_id", info.getSms_id());
		cv.put("_id", info.get_id());
		cv.put("user_id", info.getUser_id());
		cv.put("no", info.getNo());
		cv.put("phone_number", info.getPhone_number());
		cv.put("order_number", info.getOrder_number());
		return cv;
	}

}
