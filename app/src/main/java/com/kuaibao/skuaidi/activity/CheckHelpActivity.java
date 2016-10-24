package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 蒋健
 * 
 */
public class CheckHelpActivity extends RxRetrofitBaseActivity {

	private WebView mWebView;
	private View nofind;
	private TextView tv_title_des;
	private Context context;

	private String mUrl;
	private String url = Constants.CHECK_KUAIDI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.check_help);
		context = this;
		getControl();

	}

	private void getControl() {
		mWebView = (WebView) findViewById(R.id.wb_view);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("审核帮助");
		nofind = findViewById(R.id.check_network);

		if (!Utility.isNetworkConnected()) {
			nofind.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);

		} else {
			initWebView();
			getIntentData();
		}
	}

	private void getIntentData() {
		mUrl = getIntent().getStringExtra("web_url");
		if (TextUtils.isEmpty(mUrl)) {
			finish();
		}
		mWebView.loadUrl(mUrl);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollbarOverlay(true);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		// 用新的浏览器打开网页
		WebView webView = new WebView(this);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		mWebView.requestFocus();
	}

	public void back(View view) {
		finish();
	}

	// 重新加载
	public void inviteColleague(View v) {
		getIntentData();
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
