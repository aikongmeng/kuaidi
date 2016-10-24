package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 创建订单页面
 * 
 * @author xy
 * 
 */
public class AddOrderActivity extends RxRetrofitBaseActivity {

	private EditText cusName, cusNeed, cusCallNumber, cusAdress;
	private TextView titleContent,history_order_count;
	private Button click_save;
	private MyCustom cus;
	private View bottomView;
	private boolean isAdd = false;
	private String callNum,name,need,adress;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.add_order_layout);
		getViewInParent();
		setDataToView();
		getHistoryOrderCount();
		//setViewOnclickListener();
	}

	private void getViewInParent() {
		titleContent = (TextView) findViewById(R.id.tv_title_des);
		click_save = (Button) findViewById(R.id.bt_title_more);
		click_save.setVisibility(View.VISIBLE);
		cusName = (EditText) findViewById(R.id.xy_et_custom_name);
		cusNeed = (EditText) findViewById(R.id.xy_et_custom_need);
		cusCallNumber = (EditText) findViewById(R.id.xy_et_custom_callnumber);
		cusAdress = (EditText) findViewById(R.id.xy_et_custom_address);
		bottomView = findViewById(R.id.addorder_bottom_view);
		history_order_count = (TextView) findViewById(R.id.history_order_count);
	}

	private void setDataToView() {
		cus = (MyCustom) getIntent().getSerializableExtra("cus");
		titleContent.setText("创建订单");
		click_save.setText("保存");
		if (!TextUtils.isEmpty(cus.getName())) {
			cusName.setText(cus.getName());
		}
		cusCallNumber.setText(cus.getPhone());
		cusCallNumber.setFocusable(false);
		if (!TextUtils.isEmpty(cus.getAddress())) {
			cusAdress.setText(cus.getAddress());
		}

	}

//	private void setViewOnclickListener() {
//		click_save.setOnClickListener(this);
//		bottomView.setOnClickListener(this);
//	}

	// &&getIntent().getIntExtra("flag", 0)!=1
//	public void back(View v) {
//		if (PhoneOperationWindowManager.getPhoneOperationView() != null
//				&& SKuaidiApplication.windowFrom.equals("window")
//				&& SKuaidiApplication.flag != 1) {
//			PhoneOperationWindowManager.getPhoneOperationView().setVisibility(
//					View.VISIBLE);
//			SKuaidiApplication.getInstance().postMsg("phoneListener", "status",
//					2);
//		}
//		finish();
//	}

//	@Override
//	public void onClick(View v) {
//		if (v.getId() == R.id.bt_title_more) {
//			if (TextUtils.isEmpty(cusAdress.getText().toString().trim())) {
//				UtilToolkit.showToast("请输入取件地址");
//				return;
//			}
//			//离线添加订单
//			if(Utility.isNetworkConnected()==false){
//				if(isAdd == false){
//					callNum = cusCallNumber.getText().toString();
//					name = cusName.getText().toString();
//					need = cusNeed.getText().toString();
//					adress = cusAdress.getText().toString();
//				}else{
//					if(callNum.equals(cusCallNumber.getText().toString())&&name.equals(cusName.getText().toString())&&need.equals(cusNeed.getText().toString())&&adress.equals(cusAdress.getText().toString())){
//						UtilToolkit.showToast("您已保存过此条订单");
//						return;
//					}
//				}
//
//				callNum = cusCallNumber.getText().toString();
//				name = cusName.getText().toString();
//				need = cusNeed.getText().toString();
//				adress = cusAdress.getText().toString();
//				Order orderModel = new Order();
//				orderModel.setPhone(callNum);
//				orderModel.setPs(need);
//				orderModel.setName(name);
//				orderModel.setAddress(adress);
//				orderModel.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//				orderModel.setIsread(1);
//				newDB.insertOrder(orderModel);
//				UtilToolkit.showToast("保存成功");
//				isAdd = true;
//				finish();
//				return;
//			}
//			final JSONObject data = new JSONObject();
//
//			try {
//				data.put("pname", "androids");
//				data.put("from_channel", "android-cm");
//				data.put("sname", "order.cmadd2cm");
//				// data.put("counterman_mobile",
//				// SkuaidiSpf.getLoginUser(AddOrderActivity.this)
//				// .getPhoneNumber());
//				data.put("user_name", cusCallNumber.getText().toString());
//				data.put("send_user_mobile", cusCallNumber.getText().toString());
//				if (!TextUtils.isEmpty(cusName.getText().toString())) {
//					data.put("send_user", cusName.getText().toString());
//				} else {
//					data.put("send_user", "");
//				}
//				// data.put("send_address_id", "0");
//				if (!TextUtils.isEmpty(cusNeed.getText().toString())) {
//					data.put("note", cusNeed.getText().toString());
//				} else {
//					data.put("note", "");
//				}
//
//				data.put("send_address_detail", cusAdress.getText().toString());
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			showProgressDialog("");//AddOrderActivity.this,"");
//			HttpHelper httpHelper = new HttpHelper(
//					new OnResultListener() {
//
//						@Override
//						public void onSuccess(String result, String sname) {
//							dismissProgressDialog();//AddOrderActivity.this);
//							//Log.i("result", result);
//							try {
//								JSONObject json = new JSONObject(result);
//								String code = json.getString("code");
//								String msg = json.getString("msg");
//								if (code.equals("0")) {
//									UtilToolkit.showToast(
//											"保存成功");
//									if (SkuaidiSpf
//											.isVerified(AddOrderActivity.this) == 0) {
//										SkuaidiSpf.setAddOrderFlag(
//												AddOrderActivity.this, true);
//									}
//									if (PhoneOperationWindowManager
//											.getPhoneOperationView() != null
//											&& SKuaidiApplication.windowFrom
//													.equals("window")
//											&& SKuaidiApplication.flag != 1) {
//										PhoneOperationWindowManager
//												.getPhoneOperationView()
//												.setVisibility(View.VISIBLE);
//										SKuaidiApplication.getInstance()
//												.postMsg("phoneListener",
//														"status", 2);
//									}
//									//SkuaidiNewApi.synchroOrderData(AddOrderActivity.this, handler);
//									finish();
//								} else {
//									if (msg.equals("账号未登录")) {
//										UtilToolkit
//												.showToast("您的登陆session已失效,请重新登录后再添加!");
//									} else if(msg.equals("账号登陆已失效")){
//										SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//
//											@Override
//											public void todo() {
//												HttpHelper httpHelper = new HttpHelper(
//														new OnResultListener() {
//
//															@Override
//															public void onSuccess(String result, String sname) {
//																dismissProgressDialog();//AddOrderActivity.this);
//																//Log.i("result", result);
//																try {
//																	JSONObject json = new JSONObject(result);
//																	String code = json.getString("code");
//																	String msg = json.getString("msg");
//																	if (code.equals("0")) {
//																		UtilToolkit.showToast(
//																				"保存成功");
//																		if (SkuaidiSpf
//																				.isVerified(AddOrderActivity.this) == 0) {
//																			SkuaidiSpf.setAddOrderFlag(
//																					AddOrderActivity.this, true);
//																		}
//																		if (PhoneOperationWindowManager
//																				.getPhoneOperationView() != null
//																				&& SKuaidiApplication.windowFrom
//																						.equals("window")
//																				&& SKuaidiApplication.flag != 1) {
//																			PhoneOperationWindowManager
//																					.getPhoneOperationView()
//																					.setVisibility(View.VISIBLE);
//																			SKuaidiApplication.getInstance()
//																					.postMsg("phoneListener",
//																							"status", 2);
//																		}
//																		finish();
//																	} else {
//																		UtilToolkit.showToast(msg);
//																	}
//																} catch (JSONException e) {
//																	e.printStackTrace();
//																	UtilToolkit.showToast( "保存失败");
//																}
//															}
//
//															@Override
//															public void onFail(String result, JSONObject data_fail, String code) {
//																dismissProgressDialog();//AddOrderActivity.this);
//																UtilToolkit.showToast( result);
//															}
//														},handler);
//												Map<String, String> head = new HashMap<String, String>();
//												head.put("session_id",
//														SkuaidiSpf.getLoginUser()
//																.getSession_id());
//												httpHelper.getPart(data, head);
//											}
//
//											@Override
//											public void faild() {
//
//											}
//										});
//									}else{
//
//									}
//								}
//							} catch (JSONException e) {
//								e.printStackTrace();
//								UtilToolkit.showToast( "保存失败");
//							}
//						}
//
//						@Override
//						public void onFail(String result, JSONObject data_fail, String code) {
//							dismissProgressDialog();//AddOrderActivity.this);
//							UtilToolkit.showToast( result);
//						}
//					},handler);
//			Map<String, String> head = new HashMap<String, String>();
//			head.put("session_id",
//					SkuaidiSpf.getLoginUser()
//							.getSession_id());
//			httpHelper.getPart(data, head);
//		}else if(v.getId() == R.id.addorder_bottom_view){
//			if(hisToryOrderCount == 0){
//				UtilToolkit.showToast("此客户没有订单记录");
//				return;
//			}
//			Intent intent = new Intent(this, HistoryOrderActivity.class);
//			intent.putExtra("cus", cus);
//			intent.putExtra("fromAddOrder", "addOrder");
//			startActivity(intent);
//		}
//	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				&& PhoneOperationWindowManager.getPhoneOperationView() != null
//				&& SKuaidiApplication.windowFrom.equals("window")
//				&& SKuaidiApplication.flag != 1) {
//			PhoneOperationWindowManager.getPhoneOperationView().setVisibility(
//					View.VISIBLE);
//			SKuaidiApplication.getInstance().postMsg("phoneListener", "status",
//					2);
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	private int hisToryOrderCount = 0;
	public void getHistoryOrderCount(){
		hisToryOrderCount = newDB.selectOrderCountByUserPhone(cus.getPhone());
		history_order_count.setText("历史订单 （"+hisToryOrderCount+"）");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
