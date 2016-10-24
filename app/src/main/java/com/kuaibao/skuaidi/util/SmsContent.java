package com.kuaibao.skuaidi.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.activity.view.GetSMSProgressBar;
import com.kuaibao.skuaidi.entry.SmsInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * class name：SmsChoose<BR>
 * class description：获取手机中的各种短信信息<BR>
 * PS： 需要权限 <uses-permission android:name="android.permission.READ_SMS" /><BR>
 * Date:2012-3-19<BR>
 * 
 * @version 1.00
 * @author CODYY)peijiangping
 */
public class SmsContent {
	private Context context;
	private Handler mHandler;
	private Uri uri;
	private List<SmsInfo> infos;
	private GetSMSProgressBar getSMSProgressBar = null;

	public SmsContent(Context context, Uri uri) {
		infos = new ArrayList<SmsInfo>();
		this.context = context;
		this.uri = uri;
		getSMSProgressBar = new GetSMSProgressBar(context);
	}

	/**
	 * 获取手机内所有短消息
	 */
	public ArrayList<Message> getSmsInfo() {
		final String SMS_URI_ALL = "content://sms/";
		/*
		 * final String SMS_URI_INBOX = "content://sms/inbox"; final String
		 * SMS_URI_SEND = "content://sms/sent"; final String SMS_URI_DRAFT =
		 * "content://sms/draft";
		 */

		ArrayList<Message> messageLogs = new ArrayList<Message>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -100);
		try {
			ContentResolver cr = context.getContentResolver();
			String[] projection = new String[] { "_id", "address", "person", "thread_id", "body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, "date >= ? ) GROUP BY (address",
					new String[]{String.valueOf(calendar.getTimeInMillis())}, "date desc");
			if (cur.moveToFirst()) {
				// int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				int thread_idColum = cur.getColumnIndex("thread_id");

				do {

					Message message = new Message();

					message.setPersonPhoneNumber(cur.getString(phoneNumberColumn));
					// name = cur.getString(nameColumn); 这样获取的联系人为空
					if (TextUtils.isEmpty(UtilToolkit.getCallerNameFromPhoneNumber(context,
							cur.getString(phoneNumberColumn)))) {
						message.setPersonName(cur.getString(phoneNumberColumn));
					} else {
						message.setPersonName(UtilToolkit.getCallerNameFromPhoneNumber(context,
								cur.getString(phoneNumberColumn)));
					}
					if (cur.getString(smsbodyColumn) == null) {
						message.setMessageContent("");
					} else {
						message.setMessageContent(cur.getString(smsbodyColumn));
					}
					message.setMessageDate(Long.parseLong(cur.getString(dateColumn)));
					message.setMessageType(cur.getInt(typeColumn));
					message.setThread_id(cur.getInt(thread_idColum));
					// if (typeId == 1) {
					// type = "接收";
					// } else if (typeId == 2) {
					// type = "发送";
					// } else {
					// type = "草稿";
					// }
					if (!Utility.isEmpty(cur.getString(phoneNumberColumn))
							&& cur.getString(phoneNumberColumn).length() >= 11
							&& cur.getString(phoneNumberColumn).length() <= 16)
						messageLogs.add(message);
				} while (cur.moveToNext());
			}
			cur.close();
			cur = null;
		} catch (SQLiteException ex) {
			//Log.e("SQLiteException in getSmsInPhone", ex.getMessage());
			return null;
		}
		return messageLogs;
	}

	/**
	 * 获取同一号码的对话内容
	 */
	private List<Message> getSmsConversation(int thread_id) {
		final String SMS_URI_ALL = "content://sms/";

		ArrayList<Message> SMSes = new ArrayList<Message>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -365);
		try {
			ContentResolver cr = context.getContentResolver();
			String[] projection = new String[] { "_id", "address", "person", "thread_id", "body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, "thread_id = " + thread_id, null, "date asc");
			if (cur.moveToFirst()) {
				// int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				int thread_idColum = cur.getColumnIndex("thread_id");

				do {

					Message message = new Message();

					message.setPersonPhoneNumber(cur.getString(phoneNumberColumn));
					// name = cur.getString(nameColumn); 这样获取的联系人为空
					if (TextUtils.isEmpty(UtilToolkit.getCallerNameFromPhoneNumber(context,
							cur.getString(phoneNumberColumn)))) {
						message.setPersonName(cur.getString(phoneNumberColumn));
					} else {
						message.setPersonName(UtilToolkit.getCallerNameFromPhoneNumber(context,
								cur.getString(phoneNumberColumn)));
					}
					if (cur.getString(smsbodyColumn) == null) {
						message.setMessageContent("");
					} else {
						message.setMessageContent(cur.getString(smsbodyColumn));
					}
					message.setMessageDate(Long.parseLong(cur.getString(dateColumn)));
					message.setMessageType(cur.getInt(typeColumn));
					message.setThread_id(cur.getInt(thread_idColum));
					if (message.getMessageType() == 1 || message.getMessageType() == 2)
						SMSes.add(message);
				} while (cur.moveToNext());
			}
			cur.close();
			cur = null;
		} catch (SQLiteException ex) {
			//Log.e("SQLiteException in getSmsInPhone", ex.getMessage());
			return null;
		}
		return SMSes;
	}
}
