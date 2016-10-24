package com.kuaibao.skuaidi.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.SMSDetailAdapter;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class SMSDetailActivity extends RxRetrofitBaseActivity implements View.OnClickListener {

	private TextView titile,titlePhone;
	private Button examineCus;
	private Message SMS;
	private List<Message> SMSes = new ArrayList<Message>();
	private ListView lvSMSDetail;
	private MyCustom cus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.sms_detail_layout);
		initView();
		getSmsInPhone();
		SMSDetailAdapter adapter = new SMSDetailAdapter(this, SMSes);
		lvSMSDetail.setAdapter(adapter);
		setViewOnclickListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initView() {
		SMS = (Message) getIntent().getSerializableExtra("SMS");
		String phoneNumber = SMS.getPersonPhoneNumber();
		String phoneNum = phoneNumber.length()>=11?phoneNumber.substring(phoneNumber.length()-11, phoneNumber.length()):phoneNumber;
		String cusName;
		titile = (TextView) findViewById(R.id.tv_title_des);
		titlePhone = (TextView) findViewById(R.id.tv_title_des_phone);
		examineCus = (Button) findViewById(R.id.bt_title_more);
		lvSMSDetail = (ListView) findViewById(R.id.lv_sms_detail);
		
		FinalDb db = FinalDb.create(this, "skuaidi.db");
		List<MyCustom> cuss = db.findAllByWhere(MyCustom.class, "phone like '%" + phoneNum + "%'");
		if (cuss!=null&&cuss.size() != 0) {
			examineCus.setText("查看客户");
			cus  = cuss.get(0);
			cusName = cus.getName();
		} else {
			examineCus.setText("添加客户");
			cusName = SMS.getPersonName();
			cus = new MyCustom();
			cus.setName(cusName);
			cus.setPhone(phoneNum);
		}
		titile.setText(cusName);
		titlePhone.setText(phoneNum);
		examineCus.setVisibility(View.VISIBLE);
	}

	private void getSmsInPhone() {
		final String SMS_URI_ALL = "content://sms/";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -365);
		try {
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"thread_id", "body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection,
					"thread_id = " + SMS.getThread_id(), null, "date asc");
			if (cur.moveToFirst()) {
				// int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");
				int thread_idColum = cur.getColumnIndex("thread_id");

				do {

					Message message = new Message();

					message.setPersonPhoneNumber(cur
							.getString(phoneNumberColumn));
					// name = cur.getString(nameColumn); 这样获取的联系人为空
					if (TextUtils.isEmpty(UtilToolkit
							.getCallerNameFromPhoneNumber(this,
									cur.getString(phoneNumberColumn)))) {
						message.setPersonName(cur.getString(phoneNumberColumn));
					} else {
						message.setPersonName(UtilToolkit
								.getCallerNameFromPhoneNumber(this,
										cur.getString(phoneNumberColumn)));
					}
					if (cur.getString(smsbodyColumn) == null) {
						message.setMessageContent("");
					} else {
						message.setMessageContent(cur.getString(smsbodyColumn));
					}
					message.setMessageDate(Long.parseLong(cur
							.getString(dateColumn)));
					message.setMessageType(cur.getInt(typeColumn));
					message.setThread_id(cur.getInt(thread_idColum));
					if (message.getMessageType() == 1
							|| message.getMessageType() == 2)
						SMSes.add(message);
				} while (cur.moveToNext());
			}
			cur.close();
			cur = null;
		} catch (SQLiteException ex) {
			//Log.e("SQLiteException in getSmsInPhone", ex.getMessage());
		}

	}

	public void back(View v) {
		finish();
	}

	private void setViewOnclickListener() {
		examineCus.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_title_more) {
			Intent intent = new Intent(this, MycustomAddActivity.class);
			Bundle bundle = new Bundle();
			if (examineCus.getText().equals("查看客户")) {
				
				intent.putExtra("type", "get");
				bundle.putSerializable("mycustom", cus);
				intent.putExtras(bundle);
				startActivity(intent);
				
			} else {
				
				intent.putExtra("type", "addFromMessageLogs");
				bundle.putSerializable("mycustom", cus);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}
}
