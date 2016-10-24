package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

/**
 * 
 * @author a16 通知发件人的 静态界面
 */
public class NoticeAddressorActivity extends RxRetrofitBaseActivity {

	private WebView wv_order_notice;
	private ImageView iv_title_back;
	private TextView tv_title_des;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		//进度条效果
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.notice_addressor);
		getControl();
		final Activity activity = this;
		wv_order_notice.loadUrl("http://m.kuaidihelp.com/help/telfjr");
		
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		wv_order_notice.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}

		});

		wv_order_notice.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				activity.setProgress(newProgress * 1000);
			}
		});

		wv_order_notice.setWebViewClient(new WebViewClient() {
			//网页打开失败
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});

		tv_title_des.setText("通知发件人");
		// 返回图标
		iv_title_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
	}

	private void getControl() {
		wv_order_notice = (WebView) findViewById(R.id.wv_order_notice);
		
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
	}

}
