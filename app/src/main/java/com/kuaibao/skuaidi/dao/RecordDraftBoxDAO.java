package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.DraftBoxSmsInfo;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RecordDraftBoxDAO
 * @Description: 短信草稿箱数据库访问对象
 * @author 顾冬冬
 * @date 2015-10-21 下午4:09:44
 */
public class RecordDraftBoxDAO {

	private static SQLiteDatabase db;
	private static String draft_box = "draft_box";

	/**
	 * @Title: insertDraftInfo
	 * @Description: 插入或更新草稿
	 * @param @param info
	 * @param @param draft_id
	 * @param @return
	 * @return boolean
	 */
	public static synchronized boolean insertDraftInfo(DraftBoxSmsInfo info) {
		if (null == info) {
			return false;
		} else {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftToValues(info);
			try {
				if (existDraft(info.getId())) {
					String whereClause = "draft_id = ?";
					String[] whereArgs = { info.getId() };
					long id = db.update(draft_box, cv, whereClause, whereArgs);
					//Log.i("logi", "sql update id="+id);
				} else {
					long id=db.insert(draft_box, null, cv);
					//Log.i("logi", "sql insert id="+id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
	
	/**
	 * 更新数据库中手机号码
	 * @param info
	 * @return
	 */
	public static synchronized boolean updateDraftPhoneNumber(DraftBoxSmsInfo info){
		if(null == info){
			return false;
		}else{
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftPhoneNumberToValues(info);
			try {
				String whereClause = "draft_id = ?";
				String[] whereArgs = { info.getId() };
				long id = db.update(draft_box, cv, whereClause, whereArgs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
	
	/**
	 * 更新数据库中的订单号
	 * @param info
	 * @return
	 */
	public static synchronized boolean updateDraftOrderNumber(DraftBoxSmsInfo info){
		if(null == info){
			return false;
		}else{
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftOrderNumberToValues(info);
			try {
				String whereClause = "draft_id = ?";
				String[] whereArgs = { info.getId() };
				long id = db.update(draft_box, cv, whereClause, whereArgs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	/**
	 * @Title: getDraftBoxInfo
	 * @Description: 获取草稿箱信息
	 * @param @return
	 * @return DraftBoxInfo
	 */
	public static synchronized List<DraftBoxSmsInfo> getDraftBoxInfo() {
		List<DraftBoxSmsInfo> draftBoxInfos = new ArrayList<DraftBoxSmsInfo>();
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
			String draftBy = "draft_save_time DESC";
			Cursor cursor = db.query(draft_box, null, null, null, null, null, draftBy);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					DraftBoxSmsInfo draftBoxInfo = new DraftBoxSmsInfo();
					draftBoxInfo.setId(cursor.getString(cursor.getColumnIndex("draft_id")));
					draftBoxInfo.setDraftSaveTime(Long.parseLong(cursor.getString(cursor.getColumnIndex("draft_save_time"))));
					if (cursor.getString(cursor.getColumnIndex("isgunscan")).equals("0")) {
						draftBoxInfo.setGunScan(true);
					} else if (cursor.getString(cursor.getColumnIndex("isgunscan")).equals("1")) {
						draftBoxInfo.setGunScan(false);
					}
					draftBoxInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
					draftBoxInfo.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
					draftBoxInfo.setOrderNumber(cursor.getString(cursor.getColumnIndex("order_number")));
					draftBoxInfo.setUserPhoneNum(cursor.getString(cursor.getColumnIndex("user_phoneNum")));
					long sendTiming = 0L;// 定时发送时间（可以不设置，不设置则没有记录）
					if (!Utility.isEmpty(cursor.getString(cursor.getColumnIndex("send_timing")))) {
						try {
							sendTiming = Long.parseLong(cursor.getString(cursor.getColumnIndex("send_timing")));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					draftBoxInfo.setSendTiming(sendTiming);
					draftBoxInfo.setSmsContent(cursor.getString(cursor.getColumnIndex("sms_content")));
					draftBoxInfo.setModelTitle(cursor.getString(cursor.getColumnIndex("sms_content_title")));
					draftBoxInfo.setSmsId(cursor.getString(cursor.getColumnIndex("sms_id")));
					draftBoxInfo.setSmsStatus(cursor.getString(cursor.getColumnIndex("sms_status")));
					draftBoxInfos.add(draftBoxInfo);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return draftBoxInfos;
	}
	
	/**
	 * @Title: getDraftBoxInfo
	 * @Description: 获取草稿箱信息
	 * @param userPhone
	 * @return DraftBoxInfo
	 */
	public static synchronized List<DraftBoxSmsInfo> getDraftBoxInfo(String userPhone) {
		List<DraftBoxSmsInfo> draftBoxInfos = new ArrayList<DraftBoxSmsInfo>();
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
			String draftBy = "draft_save_time DESC";
			Cursor cursor = db.query(draft_box, null, "user_phoneNum=?", new String[]{userPhone}, null, null, draftBy);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					DraftBoxSmsInfo draftBoxInfo = new DraftBoxSmsInfo();
					draftBoxInfo.setId(cursor.getString(cursor.getColumnIndex("draft_id")));
					draftBoxInfo.setDraftSaveTime(Long.parseLong(cursor.getString(cursor.getColumnIndex("draft_save_time"))));
					if (cursor.getString(cursor.getColumnIndex("isgunscan")).equals("0")) {
						draftBoxInfo.setGunScan(true);
					} else if (cursor.getString(cursor.getColumnIndex("isgunscan")).equals("1")) {
						draftBoxInfo.setGunScan(false);
					}
					draftBoxInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
					draftBoxInfo.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
					draftBoxInfo.setOrderNumber(cursor.getString(cursor.getColumnIndex("order_number")));
					draftBoxInfo.setUserPhoneNum(cursor.getString(cursor.getColumnIndex("user_phoneNum")));
					long sendTiming = 0L;// 定时发送时间（可以不设置，不设置则没有记录）
					if (!Utility.isEmpty(cursor.getString(cursor.getColumnIndex("send_timing")))) {
						try {
							sendTiming = Long.parseLong(cursor.getString(cursor.getColumnIndex("send_timing")));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					draftBoxInfo.setSendTiming(sendTiming);
					draftBoxInfo.setSmsContent(cursor.getString(cursor.getColumnIndex("sms_content")));
					draftBoxInfo.setModelTitle(cursor.getString(cursor.getColumnIndex("sms_content_title")));
					draftBoxInfo.setSmsId(cursor.getString(cursor.getColumnIndex("sms_id")));
					draftBoxInfo.setSmsStatus(cursor.getString(cursor.getColumnIndex("sms_status")));
					draftBoxInfos.add(draftBoxInfo);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return draftBoxInfos;
	}

	
	
	/**
	 * @Title: deleteDraft
	 * @Description:删除指定草稿
	 * @param
	 * @return void
	 */
	public static synchronized void deleteDraft(String draft_id) {
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			String whereClause = "draft_id=?";
			String[] whereArgs = { draft_id };
			db.delete(draft_box, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Title: deleteAllDraft
	 * @Description:删除对应用户所有的草稿
	 * @param userPhone
	 */
	public static synchronized void deleteALLDraft(String userPhone){
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.delete(draft_box, "user_phoneNum=?", new String[]{userPhone});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @Title: deleteAllDraft 
	 * @Description:删除所有的草稿 
	 * @param 
	 * @return void
	 */
	public static synchronized void deleteAllDraft(){
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			db.delete(draft_box, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: existDraft
	 * @Description: 是否存在该条草稿记录-true:存在
	 * @param @param saveTime
	 * @param @return
	 * @return boolean
	 */
	public static synchronized boolean existDraft(String draft_id) {
		if (Utility.isEmpty(draft_id))
			return false;
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String sql = "select * from draft_box where draft_id ='" + draft_id + "'";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor != null && cursor.getCount() > 0;

	}

	private static ContentValues draftToValues(DraftBoxSmsInfo info) {
		ContentValues cv = new ContentValues();
		if(!existDraft(info.getId())){
			cv.put("draft_id", info.getId());
		}
		cv.put("draft_save_time", info.getDraftSaveTime());
		cv.put("sms_content", info.getSmsContent());
		cv.put("sms_content_title",info.getModelTitle());
		cv.put("sms_status", info.getSmsStatus());
		cv.put("sms_id", info.getSmsId());
		cv.put("send_timing", info.getSendTiming());
		cv.put("number", info.getNumber());
		cv.put("phone_number", info.getPhoneNumber());
		cv.put("order_number", info.getOrderNumber());
		cv.put("user_phoneNum", info.getUserPhoneNum());
		cv.put("isgunscan", info.isGunScan());
		cv.put("normal_exit_status", info.isNormal_exit_status());
		return cv;
	}
	
	/** 用于更新手机号码 **/
	private static ContentValues draftPhoneNumberToValues(DraftBoxSmsInfo info){
		ContentValues cv = new ContentValues();
		cv.put("draft_id", info.getId());
		cv.put("phone_number", info.getPhoneNumber());
		cv.put("draft_save_time", info.getDraftSaveTime());
		cv.put("normal_exit_status", info.isNormal_exit_status());
		return cv;
	}
		
	/** 用于更新运单号 **/
	private static ContentValues draftOrderNumberToValues(DraftBoxSmsInfo info){
		ContentValues cv = new ContentValues();
		cv.put("draft_id", info.getId());
		cv.put("order_number", info.getOrderNumber());
		cv.put("draft_save_time", info.getDraftSaveTime());
		return cv;
	}

}
