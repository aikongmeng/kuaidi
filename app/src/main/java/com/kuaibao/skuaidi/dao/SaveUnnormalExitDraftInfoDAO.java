package com.kuaibao.skuaidi.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDBHelper;
import com.kuaibao.skuaidi.entry.SaveUnnormalExitDraftInfo;

/**
 * 用于保存和删除最新一条草稿箱内容【在非正常退出时保存|在正常退出以后删除该表】
 * 
 * @author 顾冬冬
 */
public class SaveUnnormalExitDraftInfoDAO {

	private static SQLiteDatabase db;
	private static String table_name = "save_unnormal_exit_draftinfo";

	/** 插入最新非正常退出时候的草稿数据【保存对象：短信草稿|云呼草稿】 **/
	public static synchronized void insertUnnormarlExitDraftInfo(SaveUnnormalExitDraftInfo info) {
		if (null != info) {
			db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
			ContentValues cv = infoToCV(info);

			db.insert(table_name, null, cv);
		}
	}

	/** 获取非正常退出时候保存的【短信草稿|云呼草稿】 **/
	public static synchronized SaveUnnormalExitDraftInfo getUnNormalExitDraftInfo(String from_data){
		SaveUnnormalExitDraftInfo info = null;
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getReadableDatabase();
		String whereClause = "from_data = ?";
		String[] whereArgs ={from_data};
		Cursor cs = db.query(table_name, null, whereClause, whereArgs, null, null, null);
		if(cs.getCount() > 0){
			while (cs.moveToNext()) {
				info = new SaveUnnormalExitDraftInfo();
				info.setDraft_id(cs.getString(cs.getColumnIndex("draft_id")));
				info.setDraft_no(cs.getString(cs.getColumnIndex("draft_no")));
				info.setDraft_orderNumber(cs.getString(cs.getColumnIndex("draft_orderNumber")));
				info.setDraft_phoneNumber(cs.getString(cs.getColumnIndex("draft_phoneNumber")));
				int draftPosition =Integer.parseInt( cs.getString(cs.getColumnIndex("draft_position")));// 将字符串转成整形
				info.setDraft_position(draftPosition);
				int draftPositionNo = Integer.parseInt(cs.getString(cs.getColumnIndex("draft_positionNo")));// 将字符串转成整形
				info.setDraft_positionNo(draftPositionNo);
				info.setFrom_data(cs.getString(cs.getColumnIndex("from_data")));
			}
		}
		return info;
	}

	/** 删除最新非正常退出时候的草稿数据【删除对象：短信草稿|云呼草稿】 **/
	public static synchronized void deleteUnnormalExitDraftInfo(String from_data) {
		db = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance()).getWritableDatabase();
		String whereClause = "from_data = ?";
		String[] whereArgs ={from_data};
		try {
			db.delete(table_name, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ContentValues infoToCV(SaveUnnormalExitDraftInfo info) {
		ContentValues cv = new ContentValues();
		cv.put("draft_id", info.getDraft_id());
		cv.put("from_data", info.getFrom_data());
		cv.put("draft_no", info.getDraft_no());
		cv.put("draft_phoneNumber", info.getDraft_phoneNumber());
		cv.put("draft_orderNumber", info.getDraft_orderNumber());
		cv.put("draft_position", info.getDraft_position());
		cv.put("draft_positionNo", info.getDraft_positionNo());
		return cv;
	}

}
