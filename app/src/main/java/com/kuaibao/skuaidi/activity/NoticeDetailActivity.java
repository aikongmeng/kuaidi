package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.NoticeInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.texthelp.TextToLink;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * 
 * 通知详情
 * 
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class NoticeDetailActivity extends SkuaiDiBaseActivity {
	private Context context;

	private static final int RESULT_CODE = 901;
	private TextView tv_branch, tv_time, tv_notice_content;
	private TextView tv_title_des;
	private NoticeInfo notice;

	private static String urlZZ = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";

	private String fid;// 从首页传递过来的id
	private String resource;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.NETWORK_FAILED:
				UtilToolkit.showToast( "网络连接错误,请稍后重试!");
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.notice_detail);
		context = this;
		resource = getIntent().getStringExtra("resource");
		getControl();
		setData();
		getData();
	}

	private void setData() {
		Intent intent = getIntent();
		fid = intent.getStringExtra("id");
		if (null == fid) {
			notice = (NoticeInfo) intent.getSerializableExtra("notice");
			tv_branch.setText(notice.getType());
		}
		tv_title_des.setText("通知中心");
	}

	private void getControl() {
		tv_branch = (TextView) findViewById(R.id.tv_branch);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_notice_content = (TextView) findViewById(R.id.tv_notice_content);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);

	}

	private void getData() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "notice/getinfo");
			if (null == fid) {
				data.put("cmn_id", notice.getNoticeId());
			} else if (!"".equals(fid)) {
				data.put("cmn_id", fid);
			}
			data.put("cm_id", SkuaidiSpf.getLoginUser().getUserId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	public void back(View view) {
		if ("splash".equals(resource)) {
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		} else if ("circle".equals(resource)) {
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("tabid", 1);
			startActivity(intent);
		} else if (null == fid) {
			Intent intent = new Intent();
			intent.putExtra("notice_id", notice.getNoticeId());
			intent.putExtra("read_state", 1);
			setResult(RESULT_CODE, intent);
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ("splash".equals(resource)) {
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
			} else if ("circle".equals(resource)) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("tabid", 1);
				startActivity(intent);
			} else if (null == fid) {
				Intent intent = new Intent();
				intent.putExtra("notice_id", notice.getNoticeId());
				intent.putExtra("read_state", 1);
				setResult(RESULT_CODE, intent);
			}
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject data = null;
		try {
			data = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		tv_branch.setText(data.optString("type"));
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
		String messagetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.optLong("create_time") * 1000);

		if (now.substring(0, 11).equals(messagetime.substring(0, 11))) {
			tv_time.setText("今天 " + messagetime.substring(11, 16));
		} else if (now.substring(0, 8).equals(messagetime.substring(0, 8))
				&& (Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(messagetime.substring(8, 10)) == 1)) {
			tv_time.setText("昨天 " + messagetime.substring(11, 16));
		} else {
			tv_time.setText(messagetime.substring(0, 16));
		}

		if (data.optInt("unread_count") > 0) {
			SkuaidiSpf.saveNoticeRedCircle(true);
		} else {
			SkuaidiSpf.saveNoticeRedCircle(false);
		}
		tv_notice_content.setText(Html.fromHtml(TextToLink.urlToLink(data.optString("content"))));
		tv_notice_content.setMovementMethod(LinkMovementMethod.getInstance());

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if (code.equals("7") && null != result) {
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
