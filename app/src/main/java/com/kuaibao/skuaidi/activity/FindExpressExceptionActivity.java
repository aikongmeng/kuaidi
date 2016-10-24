package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 使用C端代码，业务员端是否需要展示及如何展示需要对口设计 异常处理
 */
public class FindExpressExceptionActivity extends RxRetrofitBaseActivity {

	private ImageView iv_call;
	private TextView tv_exception_reason;
	private TextView tv_exception_message;
	private TextView tv_name;
	private TextView tv_findexpress_exception_call;
	private TextView tv_modesecond;
	private TextView tv_title_des;
	private String order_number;
	private String express;
	private String name;
	private String type;
	private String reason;
	private String exception_id;
	private String message;
	private String home_shop_id;
	private String customer_service_phone;
	private String submit_type;
	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.findexpress_exception);

		getControl();

		setData();

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

	public void getControl() {
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		iv_call = (ImageView) findViewById(R.id.iv_findexpress_exception_call);
		tv_modesecond = (TextView) findViewById(R.id.tv_findexpress_exception_modesecond);
		tv_exception_reason = (TextView) findViewById(R.id.tv_exception_reason);
		tv_exception_message = (TextView) findViewById(R.id.tv_exception_message);
		tv_name = (TextView) findViewById(R.id.tv_findexpress_exception_firmname);
		tv_findexpress_exception_call = (TextView) findViewById(R.id.tv_findexpress_exception_call);
		tv_title_des=(TextView) findViewById(R.id.tv_title_des);
		express = getIntent().getStringExtra("express");
		order_number = getIntent().getStringExtra("order_number");
		name = getIntent().getStringExtra("name");
		type = getIntent().getStringExtra("type");
		reason = getIntent().getStringExtra("reason");
		exception_id = getIntent().getStringExtra("exception_id");
		message = getIntent().getStringExtra("message");
		home_shop_id = getIntent().getStringExtra("home_shop_id");
		customer_service_phone = getIntent().getStringExtra(
				"customer_service_phone");
		tv_title_des.setText("异常处理");
	}

	public void setData() {
		if (reason != null && !reason.equals("null") && !reason.equals("")) {
			tv_exception_reason.setText("异常原因：" + reason);
		}
		if (message != null && !message.equals("null")) {
			tv_exception_message.setText(message);
		}
		tv_name.setText(name);
		tv_findexpress_exception_call.setText(customer_service_phone);
		submit_type = "留言";
		tv_modesecond.setText("直接联系网点");
	}

	public void back(View view) {
		finish();
	}

	public void call(View view) {
//		Intent intent = new Intent("android.intent.action.CALL",
//				Uri.parse("tel:" + AllInterface.formatCall(customer_service_phone)));
//		startActivity(intent);
		AcitivityTransUtil.showChooseTeleTypeDialog(FindExpressExceptionActivity.this, "", customer_service_phone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
	}
}
