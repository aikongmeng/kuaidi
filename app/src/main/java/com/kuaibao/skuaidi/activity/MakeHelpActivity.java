package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * @author 蒋健
 * 
 */
public class MakeHelpActivity extends SkuaiDiBaseActivity {
	private TextView tv_title_des;
	private WebView webView;
	private String comeFrom = "";
	private SkuaidiImageView iv_title_back;// 返回按钮
	private LinearLayout ll_state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.makehelp);
		findView();

		String url = getIntent().getStringExtra("url");
		webView.loadUrl(url);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
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

	private void findView() {
		iv_title_back = (SkuaidiImageView) findViewById(R.id.back);// 返回按钮
		iv_title_back.setOnClickListener(onClickListener);
		comeFrom = getIntent().getStringExtra("comeFrom");
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		ll_state = (LinearLayout) findViewById(R.id.ll_state);
		ll_state.setVisibility(View.GONE);
		tv_title_des.setVisibility(View.VISIBLE);
		if(comeFrom.equals("moreSetting")){
			tv_title_des.setText("使用帮助");
		}else if(comeFrom.equals("circleExpress")){
			tv_title_des.setText("在线客服");
		}
		webView = (WebView) findViewById(R.id.web_makehelp);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back: // 返回
				webviewCanGoBack();
				break;
			default:
				break;
			}
		}
	};
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			webviewCanGoBack();
		}
		return true;
	}

	// 网页是否可能继续返回上一页，可以则返回，否则关闭界面
	private boolean webviewCanGoBack() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			finish();
		}
		return true;
	}



}
