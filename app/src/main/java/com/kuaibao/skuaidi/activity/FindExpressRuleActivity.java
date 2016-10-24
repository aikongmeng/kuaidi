package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FindExpressRuleAdapter;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.AllInterface;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class FindExpressRuleActivity extends RxRetrofitBaseActivity {
	private EditText et_order_number;
	private ListView lv;
	private String express;
	private String orderNumber;
	private TextView tv_find;
	private InputMethodManager manager;
	private List<String> list;
	private View view;
	private FindExpressRuleAdapter adapter;
	private String lockexpress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);

		setContentView(R.layout.findexpress_rule);

		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		lockexpress = sp.getString("lockexpress", "");

		getControl();

		setListener();

		setDate();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		lockexpress = sp.getString("lockexpress", "");
		if (!sp.getString("rule_express", "").equals("")) {
			express = sp.getString("rule_express", "");
			orderNumber = et_order_number.getText().toString();
			Editor editor = sp.edit();
			editor.putString("rule_express", "");
			editor.commit();
			findexpress();
		} else {
			String str = et_order_number.getText().toString();
			int length = str.length();
			String[] express_no = new String[] {};
			if (length >= 8 && length <= 18) {
				express_no = AllInterface.getExpressNoForRule(str);
				list = new ArrayList<String>();
				if (!lockexpress.equals("")) {
					int j = lockexpress.split("-").length;
					String lockexpress_str = lockexpress;
					for (int i = 0; i < j; i++) {
						list.add(lockexpress_str.substring(0,
								lockexpress_str.indexOf("-")));
						lockexpress_str = lockexpress_str
								.substring(lockexpress_str.indexOf("-") + 1);
					}
				}
				for (int i = 0; i < express_no.length; i++) {
					if (list.size() == 3) {
						break;
					}
					if (!list.contains(express_no[i])) {
						list.add(express_no[i]);
					}
				}
				list.add("选择其他快递公司");
				view.setVisibility(View.VISIBLE);
				adapter = new FindExpressRuleAdapter(
						FindExpressRuleActivity.this, list, str, true);
				lv.setAdapter(adapter);
			}
		}
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void getControl() {
		tv_find = (TextView) findViewById(R.id.tv_findexpress_rule_find);
		et_order_number = (EditText) findViewById(R.id.et_findexpressrule_order_number);
		lv = (ListView) findViewById(R.id.lv_findexpress_rule);
		view = findViewById(R.id.view_findexpress_rule);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView tv_ordernumber = (TextView) arg1
						.findViewById(R.id.tv_findexpress_rule_item_remark);
				if (tv_ordernumber.getText().toString().equals("选择其他快递公司")) {
					Intent intent = new Intent();
					intent.putExtra("type", "rule");
					intent.setClass(FindExpressRuleActivity.this,
							ExpressFirmActivity.class);
					startActivity(intent);
				} else {
					ImageView iv = (ImageView) arg1
							.findViewById(R.id.iv_findexpress_rule_item);
					String str = iv.getTag().toString();
					express = str.substring(0, str.indexOf("_"));
					orderNumber = str.substring(str.indexOf("_") + 1);
					findexpress();
				}
			}

		});

		lv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					manager.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});

		tv_find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				express = "";
				if (et_order_number.length() >= 8) {
					if (list != null && list.size() > 1) {
						express = list.get(0);
					}
				}
				orderNumber = et_order_number.getText().toString();
				findexpress();
			}
		});

		tv_find.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					tv_find.setTextColor(getResources().getColor(
							R.color.text_gray));
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					tv_find.setTextColor(getResources().getColor(R.color.text_black));
				}
				return false;
			}
		});

		et_order_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = et_order_number.getText().toString();
				int length = str.length();
				String[] express_no = new String[] {};
				if (length < 8) {
					list = new ArrayList<String>();
					adapter = new FindExpressRuleAdapter(
							FindExpressRuleActivity.this, list, str, true);
					view.setVisibility(View.GONE);
					lv.setAdapter(adapter);
				}
				if (length >= 8 && length <= 18) {
					express_no = AllInterface.getExpressNoForRule(str);
					list = new ArrayList<String>();
					if (!lockexpress.equals("")) {
						int j = lockexpress.split("-").length;
						String lockexpress_str = lockexpress;
						for (int i = 0; i < j; i++) {
							list.add(lockexpress_str.substring(0,
									lockexpress_str.indexOf("-")));
							lockexpress_str = lockexpress_str
									.substring(lockexpress_str.indexOf("-") + 1);
						}
					}
					for (int i = 0; i < express_no.length; i++) {
						if (list.size() == 3) {
							break;
						}
						if (!list.contains(express_no[i])) {
							list.add(express_no[i]);
						}
					}
					list.add("选择其他快递公司");
					adapter = new FindExpressRuleAdapter(
							FindExpressRuleActivity.this, list, str, true);
					view.setVisibility(View.VISIBLE);
					lv.setAdapter(adapter);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});

		et_order_number.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					express = "";
					if (et_order_number.length() >= 8) {
						if (list.size() > 1) {
							express = list.get(0);
						}
					}
					orderNumber = et_order_number.getText().toString();
					findexpress();
				}
				return false;
			}

		});
	}

	public void setDate() {
		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String decodestr = sp.getString("decodestr", "");
		if (decodestr != "") {
			et_order_number.setText(decodestr);
			Editor editor = sp.edit();
			editor.putString("decodestr", "");
			editor.commit();
		}
	}

	// 查询快递点击事件
	public void findexpress() {
		ConnectivityManager manager = (ConnectivityManager) getApplication()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (manager == null || networkInfo == null
				|| !networkInfo.isAvailable()) {
			UtilToolkit.showToast( "网络连接错误,请稍后重试!");
		} else {
			if (express.equals("")) {
				// 快递公司未选择
				UtilToolkit.showToast( "请输入正确运单号!");
			} else if (orderNumber.equals("")) {
				// 运单号未填写
				UtilToolkit.showToast( "请输入运单号!");
			} else if (!express.equals("") && !orderNumber.equals("")) {
				Intent intent = new Intent();
				intent.putExtra("express_no", express);
//				intent.putExtra("order_number", orderNumber);
				intent.putExtra("deliver_no", orderNumber);
//				intent.setClass(this, FindExpressResultActivity.class);//跳转到单号查询界面（之前微快递的查询界面）
				intent.setClass(this, TrackNoDetailActivity.class);//跳转到单号查询界面
				startActivity(intent);
				finish();
			}
		}
	}
}
