package com.kuaibao.skuaidi.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.MessageLogsAdapter;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * 短信列表
 * @author xy
 *
 */

public class MessageLogsActivity extends RxRetrofitBaseActivity implements OnItemClickListener{
	
	private List<Message> messageLogs = new ArrayList<Message>();
	private ListView lv;
	private TextView tv_title;
	private MessageLogsAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.message_log_layout);
		getViewInParent();
		getSmsInPhone();
		adapter = new MessageLogsAdapter(this, messageLogs);
		lv.setAdapter(adapter);
		setListener();
	}
	
	private void getViewInParent(){
		lv = (ListView) findViewById(R.id.messageLogs);
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		tv_title.setText("短信导入");
	}
	
	/**
	 * 获取手机内所以短消息
	 */
	private void getSmsInPhone() {
		final String SMS_URI_ALL = "content://sms/";
		/*
		 * final String SMS_URI_INBOX = "content://sms/inbox"; final String
		 * SMS_URI_SEND = "content://sms/sent"; final String SMS_URI_DRAFT =
		 * "content://sms/draft";
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -31);
		try {
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] { "_id", "address", "person","thread_id",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection , "date >= " + calendar.getTimeInMillis()+") group by (address" ,null , "date desc");
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
					if(TextUtils.isEmpty(UtilToolkit.getCallerNameFromPhoneNumber(this,cur.getString(phoneNumberColumn)))){
						message.setPersonName(cur.getString(phoneNumberColumn));
					}else{
						message.setPersonName(UtilToolkit.getCallerNameFromPhoneNumber(this,cur.getString(phoneNumberColumn)));
					}
					if(cur.getString(smsbodyColumn)==null){
						message.setMessageContent("");
					}else{
						message.setMessageContent(cur.getString(smsbodyColumn));
					}
					message.setMessageDate(Long.parseLong(cur.getString(dateColumn)));
					message.setMessageType(cur.getInt(typeColumn));
					message.setThread_id(cur.getInt(thread_idColum));
//					if (typeId == 1) {
//						type = "接收";
//					} else if (typeId == 2) {
//						type = "发送";
//					} else {
//						type = "草稿";
//					}
					if(cur.getString(phoneNumberColumn).length()>=11&&cur.getString(phoneNumberColumn).length()<=14)
						messageLogs.add(message);
				} while (cur.moveToNext());
			}
			cur.close();
			cur = null;
		} catch (SQLiteException ex) {
			//Log.e("SQLiteException in getSmsInPhone", ex.getMessage());
		}

	}

	public void back(View v){
		finish();
	}
	
	private void setListener(){
		lv.setOnItemClickListener(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(SKuaidiApplication.getInstance().onReceiveMsg("CallLogsActivity", "addSuccess")!=null&&Integer.parseInt(SKuaidiApplication.getInstance().onReceiveMsg("CallLogsActivity", "addSuccess").toString())==1){
			adapter.notifyDataSetChanged();
			SKuaidiApplication.getInstance().postMsg("CallLogsActivity", "addSuccess",0);
		}
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Message msg = (Message) adapter.getItem(arg2);
		Intent intent = new Intent(this, SMSDetailActivity.class);
		intent.putExtra("SMS", msg);
		startActivity(intent);
	}
}
