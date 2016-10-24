package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 蒋健，罗娜 链接页面
 */
public class LoadWebActivity extends RxRetrofitBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		WebView webView = new WebView(this);
		if (getIntent().getStringExtra("type") == null) {
			String sina = getIntent().getStringExtra("sina");
			if (sina == null) {
				webView.loadUrl("http://www.kuaidihelp.com");
			} else {
				webView.loadUrl("http://weibo.com/kuaidihelp");
			}
		}else {
			String type = getIntent().getStringExtra("type");
			if (type.equals("sto")) {
				webView.loadUrl("http://q1.sto.cn/chaxun");
			} else if (type.equals("ems")) {
				webView.loadUrl("http://www.ems.com.cn/");
			} else if (type.equals("sf")) {
				webView.loadUrl("http://www.sf-express.com/");
			} else if (type.equals("yt")) {
				webView.loadUrl("http://www.yto.net.cn/");
			} else if (type.equals("yd")) {
				webView.loadUrl("http://www.yundaex.com");
			} else if (type.equals("zt")) {
				webView.loadUrl("http://www.zto.cn/");
			} else if (type.equals("post")) {
				webView.loadUrl("http://yjcx.chinapost.com.cn/");
			} else if (type.equals("tt")) {
				webView.loadUrl("http://www.ttkdex.com/");
			} else if (type.equals("qf")) {
				webView.loadUrl("http://www.qfkd.com.cn/");
			} else if (type.equals("ht")) {
				webView.loadUrl("http://www.800bestex.com/");
			} else if (type.equals("dp")) {
				webView.loadUrl("http://www.deppon.com/");
			} else if (type.equals("zjs")) {
				webView.loadUrl("http://www.zjs.com.cn/");
			} else if (type.equals("rfd")) {
				webView.loadUrl("http://www.rufengda.com/");
			} else if (type.equals("gt")) {
				webView.loadUrl("http://www.gto365.com/");
			} else if (type.equals("jd")) {
				webView.loadUrl("http://jd-ex.com/");
			} else if (type.equals("kj")) {
				webView.loadUrl("http://www.fastexpress.com.cn/");
			} else if (type.equals("lb")) {
				webView.loadUrl("http://www.lbex.com.cn/");
			} else if (type.equals("lht")) {
				webView.loadUrl("http://www.lhtex.com.cn/index.asp");
			} else if (type.equals("nd")) {
				webView.loadUrl("http://www.nd56.com");
			} else if (type.equals("qy")) {
				webView.loadUrl("http://www.unitop-apex.com/");
			} else if (type.equals("qrt")) {
				webView.loadUrl("http://www.at-express.com/");
			} else if (type.equals("se")) {
				webView.loadUrl("http://www.sure56.com/");
			} else if (type.equals("ups")) {
				webView.loadUrl("http://www.ups.com/cn");
			} else if (type.equals("ys")) {
				webView.loadUrl("http://www.uc56.com/");
			} else if (type.equals("dhl")) {
				webView.loadUrl("http://www.cn.dhl.com/zh.html");
			}
		}
		// 设置点击后新打开的链接也在WebView中打开
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
		setContentView(webView);
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
}
