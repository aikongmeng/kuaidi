package com.kuaibao.skuaidi.asynctask;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 获取短信聊天详情
 * 
 * @author 顾冬冬
 * 
 */
public class E3GetSMSDetailTask extends AsyncTask<Object, Integer, Object> {

	private Context mContext = null;
	private Handler mHandler = null;
	private Message SMS;
	private List<Message> SMSes = new ArrayList<Message>();

	public E3GetSMSDetailTask(Context context, Handler handler, Message SMS) {
		this.mContext = context;
		this.mHandler = handler;
		this.SMS = SMS;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		android.os.Message msg = mHandler.obtainMessage();
		msg.what = 0x10002;
		msg.obj = SMSes;
		mHandler.sendMessage(msg);
	}

	@Override
	protected Object doInBackground(Object... params) {
		final String SMS_URI_ALL = "content://sms/";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -365);
		try {

			ContentResolver cr = mContext.getContentResolver();
			String[] projection = new String[] { "_id", "address", "person", "thread_id", "body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, "thread_id = ? and address = ?",
					new String[] { String.valueOf(SMS.getThread_id()), SMS.getPersonPhoneNumber() }, "date asc");
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
					if (TextUtils.isEmpty(UtilToolkit.getCallerNameFromPhoneNumber(mContext,
							cur.getString(phoneNumberColumn)))) {
						message.setPersonName(cur.getString(phoneNumberColumn));
					} else {
						message.setPersonName(UtilToolkit.getCallerNameFromPhoneNumber(mContext,
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
		}
		return SMSes;
	}
}
