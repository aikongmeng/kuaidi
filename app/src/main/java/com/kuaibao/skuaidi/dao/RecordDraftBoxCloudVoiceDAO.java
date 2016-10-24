package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.DraftBoxCloudVoiceInfo;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 云呼草稿箱数据库访问对象
 */
public class RecordDraftBoxCloudVoiceDAO {
	private static SQLiteDatabase db;
	private static String draft_box_voice = "draft_box_cloud_voice";
	
	/**
	 * 添加或更新草稿
	 */
	public static synchronized boolean insertDraftInfo(DraftBoxCloudVoiceInfo info){
		if(null == info){
			return false;
		}else{
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftToValues(info);
			try {
				if(existDraft(info.getId())){// 数据库中保存了
					String whereClause = "draft_id=?";
					String[] whereArgs = {info.getId()};
					int id =db.update(draft_box_voice, cv, whereClause, whereArgs);
					//Log.i("logi", "draft voice sql update id="+id);
				}else{// 数据库中没有保存过
					long id =db.insert(draft_box_voice, null, cv);
					//Log.i("logi", "draft voice sql insert id="+id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
	
	/**
	 * 获取指定账户云呼记录草稿信息
	 */
	public static synchronized List<DraftBoxCloudVoiceInfo> getDraftBoxInfo(String user_phone){
		List<DraftBoxCloudVoiceInfo> info = new ArrayList<>();
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
			String queryBy = "draft_save_time DESC";
			String selection = "user_phoneNum=?";
			String[] selectionArgs = {user_phone};
			Cursor cursor = db.query(draft_box_voice, null, selection, selectionArgs, null, null, queryBy);
			if(cursor.getCount() >0){
				while (cursor.moveToNext()) {
					DraftBoxCloudVoiceInfo draftBoxCloudVoiceInfo = new DraftBoxCloudVoiceInfo();
					draftBoxCloudVoiceInfo.setId(cursor.getString(cursor.getColumnIndex("draft_id")));
					draftBoxCloudVoiceInfo.setModelId(cursor.getString(cursor.getColumnIndex("model_id")));
					draftBoxCloudVoiceInfo.setModelTitle(cursor.getString(cursor.getColumnIndex("model_title")));
					draftBoxCloudVoiceInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
					draftBoxCloudVoiceInfo.setSaveTime(cursor.getLong(cursor.getColumnIndex("draft_save_time")));
					draftBoxCloudVoiceInfo.setUserPhoneNum(cursor.getString(cursor.getColumnIndex("user_phoneNum")));
					draftBoxCloudVoiceInfo.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
					draftBoxCloudVoiceInfo.setOrderNumber(cursor.getString(cursor.getColumnIndex("order_number")));
					info.add(draftBoxCloudVoiceInfo);
				}
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	/**
	 * 删除指定的草稿内容——通过草稿ID
	 */
	public static synchronized void deleteDraftByID(String draft_id){
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			String whereClause = "draft_id=?";
			String[] whereArgs = {draft_id};
			db.delete(draft_box_voice, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新数据库中手机号码
	 */
	public static synchronized boolean updateDraftPhoneNumber(DraftBoxCloudVoiceInfo info){
		if(null == info){
			return false;
		}else{
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftPhoneNumberToValues(info);
			try {
				String whereClause = "draft_id = ?";
				String[] whereArgs = { info.getId() };
				long id = db.update(draft_box_voice, cv, whereClause, whereArgs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
	/**
	 * 更新数据库中编号
	 */
	public static synchronized boolean updateDraftNumber(DraftBoxCloudVoiceInfo info){
		if(null == info){
			return false;
		}else{
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftNumberToValue(info);
			try {
				String whereClause = "draft_id = ?";
				String[] whereArgs = { info.getId() };
				long id = db.update(draft_box_voice, cv, whereClause, whereArgs);
				//Log.i("logid", id+"");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	/**更新数据库中的单号**/
	public static synchronized boolean updateDraftOrderNumber(DraftBoxCloudVoiceInfo info){
		if (null == info){
			return false;
		}else{
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = draftOrderNumberToValue(info);
			try{
				String whereClause = "draft_id=?";
				String[] whereArgs = {info.getId()};
				long id = db.update(draft_box_voice,cv,whereClause,whereArgs);
			}catch(Exception e){
				e.printStackTrace();
			}
			return true;
		}
	}
	/**
	 * 根据不同用户的手机号进行删除
	 */
	public static synchronized void deleteDraftByUserPhone(String userPhone){
		try {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			String whereClause = "user_phoneNum=?";
			String[] whereArgs = {userPhone};
			db.delete(draft_box_voice, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ContentValues draftToValues(DraftBoxCloudVoiceInfo info) {
		ContentValues cv = new ContentValues();
		if(!existDraft(info.getId())){
			cv.put("draft_id", info.getId());
		}
		cv.put("draft_save_time", info.getSaveTime());
		cv.put("phone_number", info.getPhoneNumber());
		cv.put("order_number",info.getOrderNumber());
		cv.put("number", info.getNumber());
		cv.put("model_id", info.getModelId());
		cv.put("model_title", info.getModelTitle());
		cv.put("user_phoneNum", info.getUserPhoneNum());
		return cv;
	}
	
	/**
	 * 是否存在该条草稿记录-true:存在
	 */
	public static synchronized boolean existDraft(String draft_id) {
		if (Utility.isEmpty(draft_id))
			return false;
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String sql = "select * from draft_box_cloud_voice where draft_id ='" + draft_id + "'";
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor != null && cursor.getCount() > 0;

	}
	/** 用于更新手机号码 **/
	private static ContentValues draftPhoneNumberToValues(DraftBoxCloudVoiceInfo info){
		ContentValues cv = new ContentValues();
		cv.put("phone_number", info.getPhoneNumber());
		cv.put("draft_save_time", info.getSaveTime());
		return cv;
	}
	/** 用于更新编号 **/
	private static ContentValues draftNumberToValue(DraftBoxCloudVoiceInfo info){
		ContentValues cv = new ContentValues();
		cv.put("number", info.getNumber());
		cv.put("draft_save_time", info.getSaveTime());
		return cv;
	}
	/** 用于更新单号 **/
	private static ContentValues draftOrderNumberToValue(DraftBoxCloudVoiceInfo info){
		ContentValues cv = new ContentValues();
		cv.put("order_number",info.getOrderNumber());
		cv.put("draft_save_time",info.getSaveTime());
		return cv;
	}
}


