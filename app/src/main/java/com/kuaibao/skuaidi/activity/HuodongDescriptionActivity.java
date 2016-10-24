package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

public class HuodongDescriptionActivity extends SkuaiDiBaseActivity {

	private TextView tv_title_des;// 标题
	private ImageView iv_title_back;// 返回
	private WebView web;
	private RelativeLayout rl_iagree;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.my_account_login_baidu_activity);
		initView();
		webView();
	}

	// 初始化控件
	private void initView() {
		url = getIntent().getStringExtra("url");
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		rl_iagree = (RelativeLayout) findViewById(R.id.rl_iagree);
		rl_iagree.setVisibility(View.GONE);
		tv_title_des.setText("支付及补贴活动说明");
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(new MyOnclickListener());
		web = (WebView) findViewById(R.id.web);
	}

	// 显示网页
	private void webView() {
		web.loadUrl(url);
		WebSettings webSettings = web.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
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

	class MyOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;

			default:
				break;
			}

		}

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if(code.equals("7") && null != result){
			try {
				String desc = result.optString("desc");
				UtilToolkit.showToast(desc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
