package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 蒋健
 * 
 */
public class LoadQrcodeCardActivity extends RxRetrofitBaseActivity {
	private TextView tv_title_des;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.loadqrcodecard);
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

	private void findView() {
		webView = (WebView) findViewById(R.id.web_qrcodecard);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("二维码信息");

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
