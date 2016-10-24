package com.kuaibao.skuaidi.activity;


import android.os.Bundle;
import android.view.View;

import com.kuaibao.skuaidi.entry.MyCustom;
import com.umeng.analytics.MobclickAgent;

public class HistoryOrderActivity extends OrderCenterActivity implements
		View.OnClickListener {

	private MyCustom cus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		OrderCenterActivity.fromWhere = 2;
		getCus();
		user_phone = cus.getPhone();
		super.cus = cus;
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		initView();
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
		ll_title.setVisibility(View.GONE);
		tv_title.setVisibility(View.VISIBLE);
		tv_title.setText("历史订单");
		iv_clickBack.setVisibility(View.VISIBLE);
		tv_selete.setText("创建订单");
		tv_selete.setVisibility(View.GONE);
//		if(getIntent().getStringExtra("fromAddOrder")!=null){
//			tv_selete.setVisibility(View.GONE);
//		}
	}

	public void back(View view) {
//		if (PhoneOperationWindowManager.getPhoneOperationView() != null
//				&& SKuaidiApplication.windowFrom.equals("window")) {
//			PhoneOperationWindowManager.getPhoneOperationView().setVisibility(
//					View.VISIBLE);
//			SKuaidiApplication.getInstance().postMsg("phoneListener", "status",
//					2);
//		}
		finish();
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				&& PhoneOperationWindowManager.getPhoneOperationView() != null
//				&& SKuaidiApplication.windowFrom.equals("window")) {
//			PhoneOperationWindowManager.getPhoneOperationView().setVisibility(
//					View.VISIBLE);
//			SKuaidiApplication.getInstance().postMsg("phoneListener", "status",
//					2);
//			finish();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	private void getCus() {
		cus = (MyCustom) getIntent().getSerializableExtra("cus");
	}

	private void setViewOnclickListener() {
		tv_selete.setOnClickListener(this);
	}

//	@Override
//	public void onClick(View v) {
//		if (v.getId() == R.id.tv_selete) {
//			Intent intent = new Intent(this, AddOrderActivity.class);
//			intent.putExtra("cus", cus);
//			if (PhoneOperationWindowManager.getPhoneOperationView() != null
//					&& SKuaidiApplication.windowFrom.equals("window")) {
//				SKuaidiApplication.flag = 1;
//			}
//			startActivity(intent);
//		}
//	}
}
