package com.kuaibao.skuaidi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 蒋健
 * 
 */
public class AuditTypeActivity extends RxRetrofitBaseActivity {

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.TYPE_AUDIT_SUCCESS:
				String status_result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(status_result);
					JSONObject response = json.getJSONObject("response");
					JSONObject body = response.getJSONObject("body");

					JSONObject desc = body.getJSONObject("desc");
					String status = desc.getString("status");
					String reject_reason = desc.getString("reject_reason");
					ll_audit1.setVisibility(View.VISIBLE);
					if (status.equals("apply")) {
						ll_auditing.setVisibility(View.VISIBLE);
					} else if (status.equals("pass")) {
						ll_audit_success.setVisibility(View.VISIBLE);
					} else if (status.equals("reject")) {
						ll_audit_fail.setVisibility(View.VISIBLE);
						tv_audit_fail.setText("原因:" + reject_reason);
					} else {
						ll_audit_do.setVisibility(View.VISIBLE);
					}
					ll_audit2.setVisibility(View.VISIBLE);
					ll_audit_help.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	};
	private LinearLayout ll_audit1;
	private LinearLayout ll_audit_do;
	private LinearLayout ll_auditing;
	private LinearLayout ll_audit_success;
	private LinearLayout ll_audit_fail;
	private TextView tv_audit_fail;
	private LinearLayout ll_audit2;
	private LinearLayout ll_audit_help;
	private TextView tv_title_des;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.audittype);

		getControl();

		// 审核状态
		KuaidiApi.getAuditstatus(this, handler);
	}

	public void getControl() {
		ll_audit1 = (LinearLayout) findViewById(R.id.ll_audit1);
		ll_audit_do = (LinearLayout) findViewById(R.id.ll_audit_do);
		ll_auditing = (LinearLayout) findViewById(R.id.ll_auditing);
		ll_audit_success = (LinearLayout) findViewById(R.id.ll_audit_success);
		ll_audit_fail = (LinearLayout) findViewById(R.id.ll_audit_fail);
		tv_audit_fail = (TextView) findViewById(R.id.tv_audit_fail);
		ll_audit2 = (LinearLayout) findViewById(R.id.ll_audit2);
		ll_audit_help = (LinearLayout) findViewById(R.id.ll_audit_help);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("审核方式");
	}

	public void audit_do(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SelfAuditActivity.class);
		startActivity(intent);
	}

	public void submit_again(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SelfAuditActivity.class);
		startActivity(intent);
	}

	public void audit_help(View view) {
		Intent intent = new Intent();
		intent.putExtra("web_url", Constants.CHECK_HELP);
		intent.setClass(this, CheckHelpActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void back(View view) {
		finish();
	}
}
