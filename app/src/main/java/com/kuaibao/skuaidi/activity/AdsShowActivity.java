package com.kuaibao.skuaidi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

public class AdsShowActivity extends RxRetrofitBaseActivity {
	
	private WebView webView;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ads_show);
		webView = (WebView) findViewById(R.id.ad_web);
		url  = getIntent().getStringExtra("url");
		//Log.i("iii", "url:"+url);
		webView.setWebViewClient(new WebViewClient());
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.loadUrl(url);
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setAppCacheEnabled(true);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		return true;
	}
}
