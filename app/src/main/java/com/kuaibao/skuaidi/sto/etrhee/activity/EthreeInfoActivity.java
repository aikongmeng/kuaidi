package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class EthreeInfoActivity extends SkuaiDiBaseActivity {
	private TextView tv_title;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.ethreeinfo);

		getControl();

		setListener();

		setData();
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

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
		tv_title = (TextView) findViewById(R.id.tv_title_des);
	}

	public void setListener() {

	}

	public void setData() {
		tv_title.setText(R.string.e3_info);
		final SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		String e3_name = sp.getString("user_e3_name", "");
		String e3_brandname = sp.getString("user_e3_brandname", "");
		if (!e3_name.equals("")) {
			((TextView)findViewById(R.id.tv_ethreeinfo_name)).setText(e3_name);
			((TextView)findViewById(R.id.tv_ethreeinfo_brandname)).setText(e3_brandname);
		}
		JSONObject data = new JSONObject();
		try {
			data.put("pname", "androids");
			data.put("sname", "express.authentication");
			data.put("id", SkuaidiSpf.getLoginUser().getUserId());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		showProgressDialog("");//EthreeInfoActivity.this,"");
		HttpHelper httpHelper = new HttpHelper(new OnResultListener() {

			@Override
			public void onSuccess(String result, String sname) {
				dismissProgressDialog();//EthreeInfoActivity.this);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if (code.equals("0")) {
						String data = json.getString("data");
						String status = "reject";
						if (data.equals("unaudited")) {
							// 未审核
							findViewById(R.id.bt_ethreeinfo_audit)
									.setVisibility(View.VISIBLE);
						} else if (data.equals("apply")) {
							// 审核中
							findViewById(R.id.bt_ethreeinfo_audit)
									.setVisibility(View.GONE);
							findViewById(R.id.ll_ethreeinfo_auditing)
									.setVisibility(View.VISIBLE);
						} else if (data.equals("pass")) {
							// 审核通过
							status = "pass";
							findViewById(R.id.bt_ethreeinfo_audit)
									.setVisibility(View.GONE);
							findViewById(R.id.ll_ethreeinfo_audited)
									.setVisibility(View.VISIBLE);
						} else if (data.equals("reject")) {
							// 审核失败
							findViewById(R.id.bt_ethreeinfo_audit)
									.setVisibility(View.GONE);
							findViewById(R.id.ll_ethreeinfo_auditfail)
									.setVisibility(View.VISIBLE);
						}
						sp.edit().putString("user_e3_status", status).commit();
					} else {
						UtilToolkit.showToast("获取审核状态失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(String result, JSONObject data_fail, String code) {
				dismissProgressDialog();//EthreeInfoActivity.this);
			}
		},handler);
		httpHelper.getPart(data);
	}

//	public void audit(View view) {
//		Intent intent = new Intent();
//		intent.setClass(this, EthreeInfoAuditActivity.class);
//		startActivity(intent);
//	}

	public void back(View view) {
		finish();
	}
}
