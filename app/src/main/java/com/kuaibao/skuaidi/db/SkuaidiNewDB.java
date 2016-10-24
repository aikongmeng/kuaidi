package com.kuaibao.skuaidi.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.customer.entity.Tags;
import com.kuaibao.skuaidi.entry.MessageInfo;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import net.tsz.afinal.exception.DbException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * * @author xy
 * 
 * @since 2014-10-17 11:10
 * 
 */

@SuppressLint("SimpleDateFormat")
public class SkuaidiNewDB {

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SkuaidiNewDBHelper dbHelper;
	private static SkuaidiNewDB single = null;

	private SQLiteDatabase db;

	private SkuaidiNewDB() {

	}

	public static SkuaidiNewDB getInstance() {
		if (single == null) {
			synchronized (SkuaidiNewDB.class) {
				if (single == null) {
					dbHelper = SkuaidiNewDBHelper.getInstance(SKuaidiApplication.getInstance());
					single = new SkuaidiNewDB();
				}
			}

		}
		return single;
	}



	/**
	 * 查询留言列表
	 * 
	 * @return List<MessageInfo>
	 */
	@SuppressWarnings("deprecation")
	public synchronized List<MessageInfo> selectAllMessage() {
		List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
		db = dbHelper.openDatabase();
		Cursor cursor = db.query("user_message_list", null, null, null, null, null, "speak_time desc");
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setLyId(cursor.getString(cursor.getColumnIndex("lyid")));
				messageInfo.setTargetId(cursor.getString(cursor.getColumnIndex("target_id")));
				messageInfo.setOrderNo(cursor.getString(cursor.getColumnIndex("exp_no")));
				messageInfo.setMessageType(cursor.getInt(cursor.getColumnIndex("liuyan_type")));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				if (messageInfo.getContentType() == 1) {
					messageInfo.setTxtContent(content);
				} else if (messageInfo.getContentType() == 2) {
					messageInfo.setImgContent(content);
				} else if (messageInfo.getContentType() == 3) {
					messageInfo.setVoiceContent(content);
				} else if (messageInfo.getContentType() == 4) {
					messageInfo.setTxtimgContent(content);
				} else if (messageInfo.getContentType() == 5) {
					messageInfo.setTxtContent(content);
				}
				try {
					messageInfo.setTime(format.parse(cursor.getString(cursor.getColumnIndex("speak_time"))).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				messageInfo.setCache_id(cursor.getInt(cursor.getColumnIndex("id")));
				messageInfo.setSpeakerId(cursor.getString(cursor.getColumnIndex("speaker_id")));
				messageInfo.setSpeakRole(cursor.getInt(cursor.getColumnIndex("speaker_role")));
				messageInfo.setNotRead(Integer.parseInt(cursor.getString(cursor.getColumnIndex("nrCount"))));
				messageInfo.setTip(cursor.getString(cursor.getColumnIndex("tip")));
				messageInfo.setPhone_num(cursor.getString(cursor.getColumnIndex("user_phone")));
				messageInfo.setUserName(cursor.getString(cursor.getColumnIndex("user_phone")));
				messageInfos.add(messageInfo);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;

		return messageInfos;
	}

	/**
	 * 修改留言列表某条留言信息
	 * 
	 */
	public synchronized void modifyMessage(MessageInfo info) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("liuyan_type", info.getMessageType());
		values.put("content", info.getContent());
		values.put("content_type", info.getContentType());
		values.put("speak_time", format.format(new Date(info.getTime())));
		values.put("speaker_role", info.getSpeakRole());
		if (info.getCm_nr_flag() > 0) {
			values.put("nrCount", info.getCm_nr_flag());
		}
		values.put("tip", info.getTip());
		String phone = info.getCuser_mobile();
		if (phone == null) {
			phone = info.getPhone_num();
		}
		// String args[] = { phone, info.getCache_id()+""};
		// db.update("user_message_list", values,
		// "user_phone = ? and exp_no = ?",
		// args);

		db.update("user_message_list", values, "id = " + info.getCache_id(), null);

	}


	public synchronized MessageInfo isMessageAdded(MessageInfo info) {
		db = dbHelper.openDatabase();

		String sql = "select * from user_message_list where lyid = '" + info.getLyId() + "'";

		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();

		if (count != 0) {
			cursor.moveToFirst();
			do {
				info.setCache_id(cursor.getInt(cursor.getColumnIndex("id")));
			} while (cursor.moveToNext());
			info.setUserName(info.getPhone_num());
		} else {
			info.setCache_id(-1);
		}

		cursor.close();
		cursor = null;
		return info;
	}

	/**
	 * 单个添加留言信息
	 * 
	 */
	public synchronized void insertMessage(MessageInfo info) {
		MessageInfo info1 = isMessageAdded(info);
		if (info1.getCache_id() >= 0) {
			modifyMessage(info1);
		} else {
			db = dbHelper.openDatabase();
			ContentValues values = new ContentValues();
			values.put("lyid", TextUtils.isEmpty(info.getLyId()) ? "" : info.getLyId());

			values.put("target_id", TextUtils.isEmpty(info.getLyId()) ? "" : info.getLyId());

			values.put("exp_no", isStringNull(info.getOrderNo()));
			values.put("liuyan_type", info.getMessageType());
			values.put("content", info.getContent());
			values.put("content_type", info.getContentType());
			values.put("speak_time", format.format(new Date(info.getTime())));
			values.put("speaker_id", info.getSpeakerId());
			values.put("speaker_role", info.getSpeakRole());
			values.put("nrCount", info.getCm_nr_flag());
			values.put("isReadStateUpload", info.getIsReadStateUpload());
			values.put("tip", info.getTip());
			values.put("user_phone", info.getCuser_mobile());
			db.insert("user_message_list", null, values);

		}
	}

	/**
	 * 添加留言(详情)
	 * 
	 * @param messageInfos
	 */
	public synchronized void insertMessageDetail(List<MessageInfo> messageInfos) {
		db = dbHelper.openDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < messageInfos.size(); i++) {
				ContentValues values = new ContentValues();
				MessageInfo info = messageInfos.get(i);
				values.put("content_type", info.getContentType());
				if (info.getContentType() == 1) {
					values.put("content", info.getTxtContent());
				} else if (info.getContentType() == 2) {
					values.put("content", info.getImgContent());
				} else if (info.getContentType() == 3) {
					values.put("content", info.getVoiceContent());
					values.put("voice_length", info.getVoiceLength());
					values.put("voice_path", Constants.RECORDER_PATH + info.getVoiceContent());
				} else if (info.getContentType() == 4) {
					values.put("content", info.getTxtimgContent());
					if (info.getTxtimgContent().indexOf("#%#") != -1) {
						values.put("pic_path", Constants.PICTURE_PATH + "liuyan/"
								+ info.getTxtimgContent().split("#%#")[1]);
					} else {
						values.put("pic_path", Constants.PICTURE_PATH + "liuyan/" + info.getTxtimgContent());
					}

				} else if (info.getContentType() == 5) {
					values.put("content", info.getTxtContent());
				}
				values.put("cache_id", info.getCache_id());
				values.put("lyid", isStringNull(info.getLyId()));
				values.put("ldid", isStringNull(info.getLdId()));
				values.put("exp_no", isStringNull(info.getOrderNo()));
				values.put("tip", isStringNull(info.getTip()));
				values.put("speaker_role", info.getSpeakRole());
				values.put("speak_time", format.format(new Date(info.getTime())));
				values.put("user_name", info.getUserName());
				db.insert("message_detail_list", null, values);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			db.endTransaction();
		}

	}

// TODO: 2016/6/17  查询未读订单信息与账号无关吗？
	public synchronized boolean isHaveUnreadOrder() {
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select id from user_order_list where isRead <> 0", null);
		if (cursor.getCount() > 0) {
			cursor.close();
			cursor = null;
			return true;
		}
		return false;
	}

	/**
	 * 添加订单到本地数据库
	 * 
	 * @param orderModel
	 */
	public synchronized void insertOrder(Order orderModel) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("order_id", isStringNull(orderModel.getId()));
		values.put("order_type", isStringNull(orderModel.getOrder_type()));
		values.put("exp_no", isStringNull(orderModel.getDeliverNo()));
		values.put("user_name", isStringNull(orderModel.getName()));
		values.put("user_phone", isStringNull(orderModel.getPhone()));
		values.put("address", isStringNull(orderModel.getAddress()));
		values.put("place_order_time", isStringNull(orderModel.getTime()));
		values.put("ps", isStringNull(orderModel.getPs()));
		values.put("newIm", orderModel.getNewIm());
		values.put("isRead", orderModel.getIsread());
		values.put("type", isStringNull(orderModel.getType()));
		values.put("isReadStateUpload", orderModel.getIsReadStateUpload());
		values.put("order_state_cname", isStringNull(orderModel.getOrder_state_cname()));
		values.put("real_pay", isStringNull(orderModel.getReal_pay()));
		values.put("inform_sender_when_sign", isStringNull(orderModel.getInform_sender_when_sign()));
		db.insert("user_order_list", null, values);

	}

	public synchronized void updateOrderId(Order order, String id) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("order_id", order.getId());
		values.put("is_need_sync", 0);
		values.put("order_type", order.getOrder_type());
		values.put("isRead", order.getIsread());
		values.put("isReadStateUpload", order.getIsReadStateUpload());
		values.put("real_pay", order.getReal_pay());
		values.put("inform_sender_when_sign", order.getInform_sender_when_sign());
		if (id.contains("s")) {
			db.update("user_order_list", values, " order_id = " + order.getId(), null);
		} else {
			db.update("user_order_list", values, " id = " + order.getLoc_order_id(), null);
		}

	}
	/**
	 * 获取用户的订单数量
	 * 
	 * @param user_phone
	 * @return
	 */
	public synchronized int selectOrderCountByUserPhone(String user_phone) {
		db = dbHelper.openDatabase();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		long time = new Date().getTime() + 1000 * 60 * 60 * 24;
		Date date = new Date(time);
		String newEndTime = dateFormat.format(date);
		String startTime = dateFormat.format(new Date(new Date().getTime() - 1000 * 60 * 60 * 24));
		Cursor cursor = db.rawQuery("select id from user_order_list where user_phone = '" + user_phone
				+ "' and place_order_time between '" + startTime + "' and '" + newEndTime + "'", null);
		int count = cursor.getCount();
		cursor.close();
		cursor = null;
		return count;
	}

	/**
	 * 回填订单运单号
	 * 
	 * @param order
	 */

	public synchronized void updateOrderExpNO(Order order) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("exp_no", order.getDeliverNo());
		values.put("order_type", order.getOrder_type());
		values.put("is_need_sync", 1);
		db.update("user_order_list", values, "id = " + order.getLoc_order_id(), null);

	}

	public synchronized void markUpdateOrder(Order order) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("is_need_sync", 1);
		db.update("user_order_list", values, "id = " + order.getLoc_order_id(), null);
	}

	/**
	 * 修改订单信息 收件地址 address 收件人姓名 name
	 */
	public synchronized void updateOrderInfo(Order order) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("exp_no", order.getDeliverNo());
		values.put("is_need_sync", 1);
		values.put("order_type", order.getOrder_type());
		values.put("user_name", order.getName());
		values.put("address", order.getAddress());
		values.put("user_phone", order.getPhone());
		values.put("senderName", order.getSenderName());
		values.put("senderAddress", order.getSenderAddress());
		values.put("senderPhone", order.getSenderPhone());
		values.put("articleInfo", order.getArticleInfo());
		values.put("addressHead", order.getAddressHead());
		db.update("user_order_list", values, "id = " + order.getLoc_order_id(), null);

	}

	public synchronized void addOrderInfo(Order order){
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("exp_no", order.getDeliverNo());
		values.put("is_need_sync", 1);
		values.put("order_type", order.getOrder_type());
		values.put("user_name", order.getName());
		values.put("address", order.getAddress());
		values.put("user_phone", order.getPhone());
		values.put("senderName", order.getSenderName());
		values.put("senderAddress", order.getSenderAddress());
		values.put("senderPhone", order.getSenderPhone());
		values.put("articleInfo", order.getArticleInfo());
		values.put("addressHead", order.getAddressHead());
		db.insert("user_order_list",null, values);
	}

	/**
	 * 查询所有待同步到服务器的客户
	 * 
	 * @return
	 */
	public synchronized List<MyCustom> selectAllToSyncCustomer() {
		List<MyCustom> customs = new ArrayList<MyCustom>();
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select * from customer_list where customer_id = ? and user_id = ?",
				new String[]{"", SkuaidiSpf.getLoginUser().getUserId()});
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MyCustom custom = new MyCustom();
				custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
				custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
				custom.setAddress(cursor.getString(cursor.getColumnIndex("customer_address")));
				custom.setName(cursor.getString(cursor.getColumnIndex("customer_name")));
				custom.setNote(cursor.getString(cursor.getColumnIndex("customer_remark")));
				custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
				custom.setTel(cursor.getString(cursor.getColumnIndex("customer_tel")));
				custom.setTime(cursor.getString(cursor.getColumnIndex("customer_addTime")));
				custom.setGroup(cursor.getInt(cursor.getColumnIndex("customerGroup")));
				customs.add(custom);
			} while (cursor.moveToNext());
		}

		return customs;
	}

	/**
	 * 查询所有客户
	 * 
	 * @return
	 */
	public synchronized List<MyCustom> selectAllCustomer() {
		List<MyCustom> customs = new ArrayList<MyCustom>();
		try{
			db = dbHelper.openDatabase();
			Cursor cursor = db.rawQuery("select * from customer_list where user_id = ?", new String[]{SkuaidiSpf.getLoginUser().getUserId()});
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					MyCustom custom = new MyCustom();
					custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
					custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
					custom.setAddress(cursor.getString(cursor.getColumnIndex("customer_address")));
					custom.setName(cursor.getString(cursor.getColumnIndex("customer_name")));
					custom.setNote(cursor.getString(cursor.getColumnIndex("customer_remark")));
					custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
					custom.setTime(cursor.getString(cursor.getColumnIndex("customer_addTime")));
					custom.setGroup(cursor.getInt(cursor.getColumnIndex("customerGroup")));
					String tags = cursor.getString(cursor.getColumnIndex("tags"));
					List<Tags> lsTag = new ArrayList<Tags>();
					if(!TextUtils.isEmpty(tags)){
						String[] strs = tags.split(";");
						for(int i = 0 ; i < strs.length; i++){
							String[] tag = strs[i].split("=");
							Tags tg = new Tags();
							tg.setType(tag[0]);
							tg.setDesc(tag[1]);
							lsTag.add(tg);
						}
					}
					custom.setTags(lsTag);
					//custom.setFormattedNumber(MAsyncTask.getNameNum(cursor.getString(cursor.getColumnIndex("customer_name"))));
					customs.add(custom);
				} while (cursor.moveToNext());
			}
			cursor.close();
			cursor = null;
		}catch (Exception e){
			KLog.e("kb",e.getMessage());
		}
		return customs;
	}
	
	/**
	 * 根据手机号搜索列表中存在的条目
	 * @return
	 */
	public synchronized List<MyCustom> selectFuzzySearchCustomer(String phoneNum){
		List<MyCustom> customs = new ArrayList<MyCustom>();
		db = dbHelper.openDatabase();
		String sql = "";
		if(!Utility.isEmpty(phoneNum)){
			sql = "select * from customer_list where customer_phone like '%"+phoneNum+"%' and user_id = ?";
		}else {
			sql = "select * from customer_list where user_id = ?";
		}
		Cursor cursor = db.rawQuery(sql, new String[]{SkuaidiSpf.getLoginUser().getUserId()});
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			do {
				MyCustom custom = new MyCustom();
				custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
				custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
				custom.setAddress(cursor.getString(cursor.getColumnIndex("customer_address")));
				custom.setName(cursor.getString(cursor.getColumnIndex("customer_name")));
				custom.setNote(cursor.getString(cursor.getColumnIndex("customer_remark")));
				custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
				custom.setTime(cursor.getString(cursor.getColumnIndex("customer_addTime")));
				custom.setGroup(cursor.getInt(cursor.getColumnIndex("customerGroup")));
				customs.add(custom);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return customs;
	}

	/**
	 * 查询所有已删除需要同步服务器的客户
	 * 
	 * @return
	 */
	public synchronized List<MyCustom> selectAllDeletedToSyncCustomer() {
		List<MyCustom> customs = new ArrayList<MyCustom>();
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select * from customer_tobedeleted_id_list where user_id = ?", new String[]{SkuaidiSpf.getLoginUser().getUserId()});
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MyCustom custom = new MyCustom();
				custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
				custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
				custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
				customs.add(custom);
			} while (cursor.moveToNext());
		}

		return customs;
	}

	/**
	 * 查询所有已修改需要同步服务器的客户
	 * 
	 * @return
	 */
	public synchronized List<MyCustom> selectAllModifyToSyncCustomer() {
		List<MyCustom> customs = new ArrayList<MyCustom>();
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select * from customer_list where is_need_update = 1 and user_id = ?",
				new String[]{SkuaidiSpf.getLoginUser().getUserId()});
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MyCustom custom = new MyCustom();
				custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
				custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
				custom.setAddress(cursor.getString(cursor.getColumnIndex("customer_address")));
				custom.setName(cursor.getString(cursor.getColumnIndex("customer_name")));
				custom.setNote(cursor.getString(cursor.getColumnIndex("customer_remark")));
				custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
				custom.setTel(cursor.getString(cursor.getColumnIndex("customer_tel")));
				custom.setTime(cursor.getString(cursor.getColumnIndex("customer_addTime")));
				customs.add(custom);
			} while (cursor.moveToNext());
		}

		return customs;
	}

	public synchronized void insertCustomers_v2(List<MyCustom> customs) {
		if(null == customs || customs.size() == 0){
			return;
		}
		db = dbHelper.openDatabase();
			db.beginTransaction();
			try {
				for (MyCustom myCustom : customs) {
					ContentValues values = new ContentValues();
					values.put("customer_id", isStringNull(myCustom.getId()));
					values.put("customer_phone", isStringNull(myCustom.getTel()));
					values.put("customer_name", isStringNull(myCustom.getName()));
					values.put("customer_address", isStringNull(myCustom.getAddress()));
					values.put("customer_remark", isStringNull(myCustom.getNote()));
					values.put("customer_addTime", isStringNull(myCustom.getTime()));
					values.put("is_need_update", 0);
					values.put("user_id", SkuaidiSpf.getLoginUser().getUserId());
					values.put("customer_tel", isStringNull(myCustom.getTel()));
					StringBuffer buffer = new StringBuffer();
					List<Tags> tagList = myCustom.getTags();
					if(null == tagList || tagList.size() == 0){
						values.put("tags", "");
						db.insert("customer_list", null, values);
						continue;
					}
					for(Tags tags : tagList){
						String tag =Utility.mergeString(tags.getType(), tags.getDesc(), "=");
						buffer.append(tag).append(";");
					}
					if(buffer.toString().endsWith(";")){
						buffer.deleteCharAt(buffer.toString().length()-1);
					}
					values.put("tags", buffer.toString());
					db.insert("customer_list", null, values);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				db.endTransaction();
			}
	}

	/**
	 * 根据电话号码判断是否为客户（ customer_phone like '%"+customer_phone+"%'"）
	 * 
	 * @param customer_phone
	 * @return
	 */
	public synchronized List<MyCustom> selectCustomerByPhone(String customer_phone) {
		List<MyCustom> customs = new ArrayList<MyCustom>();
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select * from customer_list where customer_phone like '%" + customer_phone + "%'"
				+ "and user_id = '"+ SkuaidiSpf.getLoginUser().getUserId()+"'",null);
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					MyCustom custom = new MyCustom();
					custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
					custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
					custom.setAddress(cursor.getString(cursor.getColumnIndex("customer_address")));
					custom.setName(cursor.getString(cursor.getColumnIndex("customer_name")));
					custom.setNote(cursor.getString(cursor.getColumnIndex("customer_remark")));
					custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
					custom.setTime(cursor.getString(cursor.getColumnIndex("customer_addTime")));
					custom.setGroup(cursor.getInt(cursor.getColumnIndex("customerGroup")));
					customs.add(custom);
				} while (cursor.moveToNext());
			}
		} catch (IllegalStateException e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			try {
				cursor.close();
			} catch (Exception e2) {

			}
		}
		return customs;
	}

	public synchronized  MyCustom selectCustomerByPhoneNum(String phone) {
		MyCustom custom = new MyCustom();
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select * from customer_list where customer_phone = ? and user_id = ?",
				new String[]{phone, SkuaidiSpf.getLoginUser().getUserId()});
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
					custom.set_index(cursor.getInt(cursor.getColumnIndex("id")));
					custom.setId(cursor.getString(cursor.getColumnIndex("customer_id")));
					custom.setAddress(cursor.getString(cursor.getColumnIndex("customer_address")));
					custom.setName(cursor.getString(cursor.getColumnIndex("customer_name")));
					custom.setNote(cursor.getString(cursor.getColumnIndex("customer_remark")));
					custom.setPhone(cursor.getString(cursor.getColumnIndex("customer_phone")));
					custom.setTime(cursor.getString(cursor.getColumnIndex("customer_addTime")));
					custom.setGroup(cursor.getInt(cursor.getColumnIndex("customerGroup")));
			}
		} catch (IllegalStateException e) {
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			try {
				cursor.close();
			} catch (Exception e2) {

			}
		}
		return custom;
	}
	
	/**
	 * existCustomer:根据电话号码判断是否存在该条手机号码  
	 * 作者： 顾冬冬
	 * @param customer_phone
	 * @return
	 */
	public synchronized boolean existCustomer(String customer_phone){
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select * from customer_list where customer_phone = ? and user_id = ?",
				new String[]{customer_phone, SkuaidiSpf.getLoginUser().getUserId()});
		return cursor.getCount() > 0;
	}

	public synchronized String queryCustomer(int _index) {
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select customer_id ,customer_phone from customer_list where id = "+ _index+ " and user_id = ?",
				new String[]{SkuaidiSpf.getLoginUser().getUserId()});
		String id = "";
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				id = cursor.getString(cursor.getColumnIndex("customer_id"));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return id;
	}

	public synchronized String[] selectCustomerByPhone1(int _index) {
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select customer_id ,customer_phone from customer_list where id = " +_index+ " and user_id = ?",
				new String[]{SkuaidiSpf.getLoginUser().getUserId()});
		String customer_id = "";
		String customer_phone = "";
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				customer_id = cursor.getString(cursor.getColumnIndex("customer_id"));
				customer_phone = cursor.getString(cursor.getColumnIndex("customer_phone"));
			} while (cursor.moveToNext());
		}

		return new String[] { customer_id, customer_phone };
	}

	/**
	 * 清空客户表
	 */
	public synchronized void deleteAllCustomer() {
		db = dbHelper.openDatabase();
		db.delete("customer_list where user_id = '"+ SkuaidiSpf.getLoginUser().getUserId()+ "'", null, null);

	}

	/**
	 * 删除客户
	 * 
	 * @param _index
	 */
	public synchronized void deleteCustomer(int _index) {
		String[] s = selectCustomerByPhone1(_index);
		db = dbHelper.openDatabase();
		int i = db.delete("customer_list where id = " + _index, null, null);
		//System.out.println("iii:"+i);
		if (!KuaiBaoStringUtilToolkit.isEmpty(s[0])) {
			ContentValues values = new ContentValues();
			values.put("customer_id", s[0]);
			values.put("customer_phone", s[1]);
			values.put("user_id", SkuaidiSpf.getLoginUser().getUserId());
			db.insert("customer_tobedeleted_id_list", null, values);
		}

	}

	public synchronized void deleteCustomer(String customer_id) {
		db = dbHelper.openDatabase();
		db.delete("customer_list where customer_id = '" + customer_id + "'", null, null);

	}
	
	public synchronized void deleteSynCustomerByPhone(String phone) {
		MyCustom s = selectCustomerByPhoneNum(phone);
		db = dbHelper.openDatabase();
		db.delete("customer_list where customer_phone = '" + phone + "' and user_id = '"+ SkuaidiSpf.getLoginUser().getUserId() + "'", null, null);
		if (!KuaiBaoStringUtilToolkit.isEmpty(s.getName())) {
			ContentValues values = new ContentValues();
			values.put("customer_id", s.getId());
			values.put("customer_phone", s.getPhone());
			values.put("user_id", SkuaidiSpf.getLoginUser().getUserId());
			db.insert("customer_tobedeleted_id_list", null, values);
		}
	}
	
	
	public synchronized void deleteCustomerByPhone(String phone) {
		db = dbHelper.openDatabase();
		db.delete("customer_list where customer_phone = '" + phone + "' and user_id = '"+ SkuaidiSpf.getLoginUser().getUserId() + "'", null, null);
	}

	/**
	 * 清空待还未和服务器同步的已删除的数据
	 */
	public synchronized void emptyToAddTableCache() {
		db = dbHelper.openDatabase();
		db.execSQL("DELETE FROM 'customer_tobedeleted_id_list'");
		db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'customer_tobedeleted_id_list'");

	}

	public synchronized void addBanedRecorderCustomer(MyCustom myCustom) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("customerGroup", myCustom.getGroup());
		db.update("customer_list", values, "id = " + myCustom.get_index(), null);
	}

	public synchronized void removeBanedRecorderCustomer(MyCustom myCustom) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("customerGroup", myCustom.getGroup());
		db.update("customer_list", values, "id = " + myCustom.get_index(), null);
	}


	/**
	 * 修改客户
	 * 
	 * @param custom
	 * @return
	 */
	public synchronized int modifyCustomer(MyCustom custom, int is_need_update) {
		int result = 0;
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("customer_phone", custom.getTel());
		values.put("customer_tel", isStringNull(custom.getTel()));
		values.put("customer_name", custom.getName());
		values.put("customer_address", isStringNull(custom.getAddress()));
		values.put("customer_remark", isStringNull(custom.getNote()));
		values.put("is_need_update", is_need_update);
		StringBuffer buffer = new StringBuffer();
		List<Tags> tagList = custom.getTags();
		if(tagList != null && tagList.size() > 0) {
			for (Tags tags : tagList) {
				String tag = Utility.mergeString(tags.getType(), tags.getDesc(), "=");
				buffer.append(tag).append(";");
			}
			if (buffer.toString().endsWith(";")) {
				buffer.deleteCharAt(buffer.toString().length() - 1);
			}
		}
		values.put("tags", buffer.toString());
		result = db.update("customer_list", values, "customer_id = " + custom.getId(), null);

		return result;
	}

	
	public synchronized int modifyCustomerById(MyCustom custom, int is_need_update) {
			int result = 0;
			db = dbHelper.openDatabase();
			ContentValues values = new ContentValues();
			values.put("customer_phone", custom.getPhone());
			values.put("customer_tel", custom.getPhone());
			values.put("customer_name", custom.getName());
			values.put("customer_address", isStringNull(custom.getAddress()));
			values.put("customer_remark", isStringNull(custom.getNote()));
			values.put("is_need_update", is_need_update);
			StringBuffer buffer = new StringBuffer();
			List<Tags> tagList = custom.getTags();
			if(tagList != null && tagList.size() > 0) {
				for (Tags tags : tagList) {
					String tag = Utility.mergeString(tags.getType(), tags.getDesc(), "=");
					buffer.append(tag).append(";");
				}
				if (buffer.toString().endsWith(";")) {
					buffer.deleteCharAt(buffer.toString().length() - 1);
				}
			}
			values.put("tags", buffer.toString());
			result = db.update("customer_list", values, "id = " + custom.get_index() + " and user_id = '"+ SkuaidiSpf.getLoginUser().getUserId() + "'", null);
		
		return result;
	}
	/**
	 * 
	 * @param customer_id
	 * @return
	 */
	public synchronized boolean isHaveCustomer(String customer_id) {
		db = dbHelper.openDatabaseRead();
		boolean bol  = false;
		try {
			Cursor cursor = db.rawQuery("select id from customer_list where customer_id = ? and user_id = ?",
					new String[]{customer_id, SkuaidiSpf.getLoginUser().getUserId()});
			if (cursor.getCount() > 0) {
				bol= true;
			}
			cursor.close();
		} catch (DbException e) {
			e.printStackTrace();
			return bol;
		}
		return bol;
	}

	public synchronized boolean isHaveCustomer1(String customer_phone) {
		db = dbHelper.openDatabaseRead();
		boolean bol = false;
		try {
			Cursor cursor = db.rawQuery("select id from customer_list where customer_phone like '%" + customer_phone + "%'" + " and user_id = ?",
					new String[]{SkuaidiSpf.getLoginUser().getUserId()});
			int count = cursor.getCount();
			if (count > 0) {
				bol= true;
			}
			cursor.close();
		} catch (DbException e) {
			e.printStackTrace();
			return bol;
		}
		
		return bol;
	}

	public synchronized void insertCustomer(MyCustom myCustom) {
		boolean isHave = isHaveCustomer1(myCustom.getPhone());
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("customer_id", isStringNull(myCustom.getId()));
		values.put("customer_phone", isStringNull(myCustom.getTel()));
		values.put("customer_name", isStringNull(myCustom.getName()));
		values.put("customer_address", isStringNull(myCustom.getAddress()));
		values.put("customer_remark", isStringNull(myCustom.getNote()));
		values.put("customer_addTime", isStringNull(myCustom.getTime()));
		values.put("is_need_update", 0);
		values.put("user_id", SkuaidiSpf.getLoginUser().getUserId());
		values.put("customer_tel", isStringNull(myCustom.getTel()));
		StringBuffer buffer = new StringBuffer();
		List<Tags> tagList = myCustom.getTags();
		if(tagList != null && tagList.size() > 0) {
			for (Tags tags : tagList) {
				String tag = Utility.mergeString(tags.getType(), tags.getDesc(), "=");
				buffer.append(tag).append(";");
			}
			if (buffer.toString().endsWith(";")) {
				buffer.deleteCharAt(buffer.toString().length() - 1);
			}
		}
		values.put("tags", buffer.toString());
		if (isHave) {
			db.update("customer_list", values, "customer_phone = '" + myCustom.getPhone() + "' and user_id = '" + SkuaidiSpf.getLoginUser().getUserId() + "'", null);
		} else {
			db.insert("customer_list", null, values);
		}
	}

	public synchronized int queryCustomerMaxId() {
		db = dbHelper.openDatabase();
		Cursor cursor = db.rawQuery("select Max(id) from customer_list where user_id = ?",
				new String[]{SkuaidiSpf.getLoginUser().getUserId()});
		int maxId = 0;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				maxId = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return maxId;
	}

	public synchronized List<E3Type> queryAllE3SignedType(String company) {
		List<E3Type> types = new ArrayList<E3Type>();
		db = dbHelper.openDatabase();
		Cursor cursor = db.query("E3signedTypes", null, "company = ?", new String[] { company }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				E3Type signedType = new E3Type();
				signedType.setId(cursor.getInt(cursor.getColumnIndex("id")));
				signedType.setType(cursor.getString(cursor.getColumnIndex("signedType")));
				signedType.setCompany(cursor.getString(cursor.getColumnIndex("company")));
				types.add(signedType);
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return types;
	}

	public synchronized void delE3SignedTypeById(int id) {
		db = dbHelper.openDatabase();
		String delSql = "delete from E3signedTypes where id = " + id;
		db.execSQL(delSql);
	}

	public synchronized E3Type addE3SignedType(E3Type type) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("signedType", type.getType());
		values.put("company", type.getCompany());
		db.insert("E3signedTypes", null, values);
		Cursor cursor = db.rawQuery("select * from E3signedTypes where id = (select max(id) from E3signedTypes)", null);
		cursor.moveToFirst();
		type.setId(cursor.getInt(cursor.getColumnIndex("id")));
		cursor.close();
		cursor = null;
		return type;
	}

	public synchronized void addOrder(Order order) {
		db = dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("user_name", isStringNull(order.getName()));
		values.put("user_mobile", isStringNull(order.getPhone()));
		values.put("user_address", isStringNull(order.getAddress()));
		values.put("note", isStringNull(order.getPs()));
		values.put("voice_name", isStringNull(order.getVoice_name()));
		db.insert("order_cache", null, values);
	}

	private String isStringNull(String str) {
		return KuaiBaoStringUtilToolkit.isEmpty(str) ? "" : str;
	}

}
