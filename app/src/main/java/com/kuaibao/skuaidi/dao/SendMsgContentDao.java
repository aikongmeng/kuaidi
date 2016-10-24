package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.SendMsgContentEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送短信界面保存数据
 * 
 * @author 顾冬冬
 */
public class SendMsgContentDao {

	private static SQLiteDatabase db;
	private static String table_name = "send_msg_content";

	/** 插入新数据 **/
	public static synchronized void insertSendMsgData(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		ContentValues cv = infoToCV(sendMsgContentEntry);
		db.insert(table_name, null, cv);
	}

	/** 根据ID更新全部数据 **/
	public static synchronized void updateSendMsgData(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		ContentValues cv = infoToCV(sendMsgContentEntry);
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sendMsgContentEntry.getSms_id() };
		db.update(table_name, cv, whereClause, whereArgs);
	}

	/** 根据ID更新短信模板内容 **/
	public static synchronized void updateModel(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sendMsgContentEntry.getSms_id() };
		ContentValues cv = new ContentValues();
		cv.put("model_id", sendMsgContentEntry.getModel_id());
		cv.put("model_content", sendMsgContentEntry.getModel_content());
		cv.put("model_status", sendMsgContentEntry.getModel_status());
		db.update(table_name, cv, whereClause, whereArgs);
	}

	/** 根据ID更新发送时间 **/
	public static synchronized void updateSendTiming(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sendMsgContentEntry.getSms_id() };
		ContentValues cv = new ContentValues();
		cv.put("send_timing", sendMsgContentEntry.getSend_timing());
		db.update(table_name, cv, whereClause, whereArgs);
	}

	/** 更新自动发送语音模板id **/
	public static synchronized void updateAutoSendVoiceModelId(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sendMsgContentEntry.getSms_id() };
		ContentValues cv = new ContentValues();
		cv.put("auto_send_voice_model_id", sendMsgContentEntry.getAuto_send_voice_model_id());
		db.update(table_name, cv, whereClause, whereArgs);
	}

	/** 更新同步巴枪扫描状态 **/
	public static synchronized void updateSynchronizeGunScanStatus(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sendMsgContentEntry.getSms_id() };
		ContentValues cv = new ContentValues();
		cv.put("synchronize_gun_scan_status", sendMsgContentEntry.isSynchronize_gun_scan_status());
		db.update(table_name, cv, whereClause, whereArgs);
	}

	/** 更新是否是插入状态 **/
	public static synchronized void updateIsCrashStatus(SendMsgContentEntry sendMsgContentEntry) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sendMsgContentEntry.getSms_id() };
		ContentValues cv = new ContentValues();
		cv.put("iscrash_status", sendMsgContentEntry.isIscrash_status());
		db.update(table_name, cv, whereClause, whereArgs);
	}

	/** 删除指定信息 **/
	public static synchronized void deleteSendMsg(String sms_id) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "sms_id = ?";
		String[] whereArgs = { sms_id };
		db.delete(table_name, whereClause, whereArgs);
	}

	/** 删除所有信息 **/
	public static synchronized void deleteSendMsgAll() {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		db.delete(table_name, null, null);
	}

	/** 获取指定信息 **/
	public static synchronized SendMsgContentEntry getSendMsg(String sms_id) {
		SendMsgContentEntry sendEntry = new SendMsgContentEntry();
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		String selection = "sms_id = ?";
		String[] selectionArgs = { sms_id };
		Cursor cursor = db.query(table_name, null, selection, selectionArgs, null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				sendEntry.setSms_id(cursor.getString(cursor.getColumnIndex("sms_id")));
				sendEntry.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
				sendEntry.setModel_id(cursor.getString(cursor.getColumnIndex("model_id")));
				sendEntry.setModel_content(cursor.getString(cursor.getColumnIndex("model_content")));
				sendEntry.setModel_status(cursor.getString(cursor.getColumnIndex("model_status")));
				sendEntry.setSend_timing(Long.parseLong(cursor.getString(cursor.getColumnIndex("send_timing"))));
				sendEntry.setSave_time(Long.parseLong(cursor.getString(cursor.getColumnIndex("save_time"))));
				sendEntry.setAuto_send_voice_model_id(cursor.getString(cursor.getColumnIndex("auto_send_voice_model_id")));
				if (cursor.getString(cursor.getColumnIndex("synchronize_gun_scan_status")).equals("0")) {
					sendEntry.setSynchronize_gun_scan_status(false);
				} else {
					sendEntry.setSynchronize_gun_scan_status(true);
				}
				if (cursor.getString(cursor.getColumnIndex("iscrash_status")).equals("0")) {
					sendEntry.setIscrash_status(false);
				} else {
					sendEntry.setIscrash_status(true);
				}
			}
		}
		return sendEntry;
	}

	/** 获取所有信息 **/
	public static synchronized List<SendMsgContentEntry> getSendMsgAll() {
		List<SendMsgContentEntry> sendMsgs = new ArrayList<SendMsgContentEntry>();
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		String orderBy = "save_time desc";
		Cursor cursor = db.query(table_name, null, null, null, null, null, orderBy);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				SendMsgContentEntry sendEntry = new SendMsgContentEntry();
				sendEntry.setSms_id(cursor.getString(cursor.getColumnIndex("sms_id")));
				sendEntry.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
				sendEntry.setModel_id(cursor.getString(cursor.getColumnIndex("model_id")));
				sendEntry.setModel_content(cursor.getString(cursor.getColumnIndex("model_content")));
				sendEntry.setModel_status(cursor.getString(cursor.getColumnIndex("model_status")));
				sendEntry.setSend_timing(Long.parseLong(cursor.getString(cursor.getColumnIndex("send_timing"))));
				sendEntry.setSave_time(Long.parseLong(cursor.getString(cursor.getColumnIndex("save_time"))));
				sendEntry.setAuto_send_voice_model_id(cursor.getString(cursor.getColumnIndex("auto_send_voice_model_id")));
				if (cursor.getString(cursor.getColumnIndex("synchronize_gun_scan_status")).equals("0")) {
					sendEntry.setSynchronize_gun_scan_status(false);
				} else {
					sendEntry.setSynchronize_gun_scan_status(true);
				}
				if (cursor.getString(cursor.getColumnIndex("iscrash_status")).equals("0")) {
					sendEntry.setIscrash_status(false);
				} else {
					sendEntry.setIscrash_status(true);
				}
				sendMsgs.add(sendEntry);
			} while (cursor.moveToNext());
		}
		return sendMsgs;
	}

	private static ContentValues infoToCV(SendMsgContentEntry sendMsgContentEntry) {
		ContentValues cv = new ContentValues();
		cv.put("sms_id", sendMsgContentEntry.getSms_id());
		cv.put("user_id", sendMsgContentEntry.getUser_id());
		cv.put("model_id", sendMsgContentEntry.getModel_id());
		cv.put("model_content", sendMsgContentEntry.getModel_content());
		cv.put("model_status", sendMsgContentEntry.getModel_status());
		cv.put("send_timing", sendMsgContentEntry.getSend_timing());
		cv.put("save_time", sendMsgContentEntry.getSave_time());
		cv.put("auto_send_voice_model_id", sendMsgContentEntry.getAuto_send_voice_model_id());
		cv.put("synchronize_gun_scan_status", sendMsgContentEntry.isSynchronize_gun_scan_status());
		cv.put("iscrash_status", sendMsgContentEntry.isIscrash_status());
		return cv;
	}

}
