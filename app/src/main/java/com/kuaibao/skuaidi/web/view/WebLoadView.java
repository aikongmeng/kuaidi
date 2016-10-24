package com.kuaibao.skuaidi.web.view;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.service.AlarmReceiver;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Map;

/**
 * WEB页通用加载器
 *
 * @author xy
 *
 */
public class WebLoadView extends RxRetrofitBaseActivity {
	private static final int RESULT_CODE_WEBVIEW = 110;
	private final String BUTTON_LEFT = "button_left";
	private final String BUTTON_MIDDLE = "button_middle";
	private final String BUTTON_RIGHT = "button_right";

	public static final String SHOUKUAN_ONLINE_URL = "http://m.kuaidihelp.com/shoukuan/list";//在线收款记录统计url
	private final String SHOUKUAN_ALL_URL = "http://m.kuaidihelp.com/shoukuan/list?trans_type=offline";//全部收款统计url

	private TextView tv_title_des;
	private WebView webView;
	private ArrayList<String> parameters;
	private Context context;
	ValueCallback<Uri> mUploadMessage;
	private TextView btn_left, btn_middle, btn_right;
	private LinearLayout ll_state;
	private SkuaidiTextView tv_more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.makehelp);
		context = this;
		final int apiLevel = Build.VERSION.SDK_INT;
		String version_release = Build.VERSION.RELEASE;
		parameters = getIntent().getStringArrayListExtra("parameters");
		findView();
		initData();
		loadWeb();

		setListener();

	}

	private void findView() {
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		ll_state = (LinearLayout) findViewById(R.id.ll_state);
		webView = (WebView) findViewById(R.id.web_makehelp);
		btn_left = (TextView) findViewById(R.id.button_left);
		btn_middle = (TextView) findViewById(R.id.button_middle);
		btn_right = (TextView) findViewById(R.id.button_right);
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		if("收款统计".equals(parameters.get(1))){
			ll_state.setVisibility(View.VISIBLE);
			tv_title_des.setVisibility(View.GONE);
			btn_left.setText(" 收   款 ");
			btn_middle.setVisibility(View.GONE);
			btn_right.setText("在线收款");
			tv_more.setVisibility(View.GONE);
		}else{
			ll_state.setVisibility(View.GONE);
			tv_title_des.setVisibility(View.VISIBLE);
		}

	}

	private void initData() {
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		CookieSyncManager.createInstance(this);
		CookieManager manager = CookieManager.getInstance();
		manager.setAcceptCookie(true);
		manager.removeAllCookie();
		Map<String, String> cookies = Utility.getSession_id(this);
		try {
			for (String key : cookies.keySet()) {
				manager.setCookie(parameters.get(0), key + "=" + cookies.get(key));
			}
			CookieSyncManager.getInstance().sync();
			tv_title_des.setText(parameters.get(1));
			webView.loadUrl(parameters.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		webView.addJavascriptInterface(this, "client");
	}


	private void setListener(){
		btn_left.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				changeButtonBG(btn_left, BUTTON_LEFT);
				webView.clearCache(true);
				webView.clearHistory();
				webView.loadUrl(SHOUKUAN_ALL_URL);
			}
		});
		btn_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeButtonBG(btn_right, BUTTON_RIGHT);
				webView.clearCache(true);
				webView.clearHistory();
				webView.loadUrl(SHOUKUAN_ONLINE_URL);
			}
		});
	}


	private void changeButtonBG(TextView button_textview, String button_name) {

		btn_left.setBackgroundResource(R.drawable.shape_default_green_radius_left);
		btn_right.setBackgroundResource(R.drawable.shape_default_green_radius_right);
		btn_left.setTextColor(Utility.getColor(context,R.color.white));
		btn_right.setTextColor(Utility.getColor(context,R.color.white));
		button_textview.setTextColor(Utility.getColor(context,R.color.default_green));
		if (button_name.equals(BUTTON_LEFT)) {
			btn_left.setBackgroundResource(R.drawable.shape_radius_btn_left_white2);
		} else if (button_name.equals(BUTTON_RIGHT)) {
			btn_right.setBackgroundResource(R.drawable.shape_radius_btn_right_white2);
		}
	}

	private void loadWeb() {
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.substring(0, 4).equals("tel:")) {
					return true;
				} else if ("kdy://showScanViewController".equals(url)) {
					scanExpressNumber();
					return true;
				}
				CookieManager manager = CookieManager.getInstance();
				manager.setAcceptCookie(true);
				manager.removeAllCookie();
				Map<String, String> cookies = Utility.getSession_id(context);
				for (String key : cookies.keySet()) {
					manager.setCookie(parameters.get(0), key + "=" + cookies.get(key));
				}
				CookieSyncManager.getInstance().sync();
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				tv_title_des.setText(title);
			}


		});
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

	@Override
	protected void onDestroy() {
		if (webView != null) {
			webView.getSettings().setJavaScriptEnabled(false);
			if(webView.getVisibility()==View.VISIBLE){
				webView.setVisibility(View.GONE);
			}
			ViewGroup parent = (ViewGroup) webView.getParent();
			if (parent != null) {
				parent.removeView(webView);
			}
			webView.removeAllViews();
			webView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back(null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void back(View view) {
//		if (webView.canGoBack()) {
//			webView.goBack();
//		} else {
		Intent intent = getIntent();
		String from = intent.getStringExtra("from");
		if ("alarmDialog".equals(from)) {
			Intent alarmIntent = new Intent(context, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
			try {
				sender.send();
			} catch (CanceledException e) {
				e.printStackTrace();
				//Log.d("ii", e.getLocalizedMessage());
			}
		}
		finish();
//		}

	}

	@JavascriptInterface
	public void back() {
		setResult(RESULT_CODE_WEBVIEW);
		finish();
	}

	/**
	 * 发起留言扫单号
	 */
	public void scanExpressNumber() {
		Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra("qrcodetype", Constants.TYPE_CREATE_LIUYAN);
		intent.putExtra("isContinuous", false);
		startActivityForResult(intent, 101);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101 && resultCode == 102) {
			final String number = data.getStringExtra("decodestr");
			webView.post(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:scanExpressNo('" + number + "')");
				}
			});
		} else if (requestCode == 1001) {
			if (null == mUploadMessage)
				return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;

		}
	}

}
