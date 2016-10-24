package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

public class SMSOperationActivity extends RxRetrofitBaseActivity {
	TextView sender,smsContent,smsReceiveDate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.sms_operation_layout);
		getViewInParent();
		setData();
	}
	private void getViewInParent(){
		sender = (TextView) findViewById(R.id.sender);
		smsContent = (TextView) findViewById(R.id.sms_content);
		smsReceiveDate = (TextView) findViewById(R.id.sms_receive_date);
	}
	private void setData(){
		String[] smsData = getIntent().getStringArrayExtra("smsData");
		sender.setText(sender.getText().toString()+smsData[0]);
		smsContent.setText(smsContent.getText().toString()+smsData[1]);
		smsReceiveDate.setText(smsReceiveDate.getText().toString()+smsData[2]);
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
}
