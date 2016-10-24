package com.kuaibao.skuaidi.asynctask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.util.SmsContent;

import java.util.ArrayList;

/**
 * 获取手机短信列表
 * @author 顾冬冬
 *
 */
public class E3GetSMSTask extends AsyncTask<Object, Integer, Object> {

	private Context mContext = null;
	private Handler mHandler = null;
	private Uri uri = null;
	private ArrayList<Message> messages = new ArrayList<Message>();
	
	public E3GetSMSTask(Context context,Handler handler){
		this.mContext = context;
		this.mHandler = handler;
		uri = Uri.parse("content://mms-sms/conversations");
	}
	
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		android.os.Message msg = mHandler.obtainMessage();
		msg.what = 0x10001;
		msg.obj = messages;
		mHandler.sendMessage(msg);
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		SmsContent sc = new SmsContent(mContext, uri);
		messages=sc.getSmsInfo();
		return messages;
	}

}
