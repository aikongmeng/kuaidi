package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ClassName: VipPrerogativeWebViewActivity <br/>
 * date: 2016年3月15日 上午10:19:08 <br/>
 *
 * @author 顾冬冬
 */
public class VipPrerogativeWebViewActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private Activity mActivity;
	private Intent mIntent;
	private WebView mWebView;
	private SkuaidiImageView iv_title_back;
	private TextView tv_title_des;
	private SkuaidiDialog dialog;

	private WebChromeClient wcc;

	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.start_page_activity);
		mActivity = this;
		initView();

		url = getIntent().getStringExtra("url");

		setWebViewTitle();
		webView();
	}

	private void initView() {
		mWebView = (WebView) findViewById(R.id.webview);
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);

		iv_title_back.setOnClickListener(this);
	}

	/**
	 * setWebViewTitle:设置标题
	 *
	 * @author 顾冬冬
	 */
	private void setWebViewTitle() {
		wcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				tv_title_des.setText(title);
			}
		};
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void webView() {
		mWebView.loadUrl(url);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);// 支持javascript脚本
		webSettings.setAllowFileAccess(true);// 允许访问文件
		webSettings.setBuiltInZoomControls(false);// 设置显示缩放按钮
		webSettings.setSupportZoom(false);// 不支持缩放
		webSettings.setGeolocationEnabled(true);
		webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
		/**
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
		 * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
		 */
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSettings.setUseWideViewPort(true);

		// 启用数据库
		webSettings.setDatabaseEnabled(true);
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();

		// 启用地理定位
		webSettings.setGeolocationEnabled(true);
		// 设置定位的数据库路径
		webSettings.setGeolocationDatabasePath(dir);
		// 最重要的方法，一定要设置，这就是地图出不来的主要原因
		webSettings.setDomStorageEnabled(true);
		/**
		 * 如果需要监视加载进度的，需要创建一个自己的WebChromeClient类，
		 * 并重载方法onProgressChanged，再webview.setWebChromeClient(new
		 * MyWebChromeClient())即可
		 */
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				mActivity.setTitle("Loading...");
				mActivity.setProgress(progress * 100);
				if (progress == 100)
					mActivity.setTitle(R.string.app_name);
			}
		});

		mWebView.setWebChromeClient(wcc);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) { // Handle
																												// the
																												// error
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.substring(0, 4).equals("tel:")) {// 如果字符串从第一个字符到第4个字符是“tel：”说明是拨打电话手机号
					final String telPhone = url.substring(4, url.length());// 获取"tel:"字符串后面的手机所有字符
					SkuaidiDialog dialog = new SkuaidiDialog(mActivity);
					dialog.setTitle("提示");
					dialog.setContent("你确认要向" + telPhone + "拨打电话吗？");
					dialog.setPositionButtonTitle("取消");
					dialog.setNegativeButtonTitle("确认");
					dialog.isUseEditText(false);
					dialog.showDialog();
					dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
						@Override
						public void onClick() {
							// 拨打电话
							// Intent intent = new Intent(Intent.ACTION_CALL,
							// Uri.parse("tel:" + telPhone));
							// startActivity(intent);
							AcitivityTransUtil.showChooseTeleTypeDialog(mActivity, "", telPhone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
						}
					});
				} else if (url.substring(0, 4).equals("sms:")) {// 如果字符串第一个字符到第4个字符是“sms:”说明是发送短信的手机号
					final String smsPhone = url.substring(4, url.length());// 获取"sms:"字符串后面的手机所有字符
					// 发送短信
					Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + smsPhone));
					mActivity.startActivity(sendIntent);
				} else if (url.equals("go://open")) {// 同意协议并开通
					userApplyVip();
					showProgressDialog("");//mActivity, "请稍等...");
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});

	}

	/**
	 * 如果不做任何处理，浏览网页，点击系统“Back”键，整个Browser会调用finish()而结束自身， 如果希望浏览的网
	 * 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

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

	/**
	 * userApplyVip:申请VIP
	 *
	 * @author 顾冬冬
	 */
	private void userApplyVip() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "user/applyVip");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		dismissProgressDialog();//mActivity);
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (null != result) {
			if (sname.equals("user/applyVip")) {
//				MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//				EventBus.getDefault().post(m);
				ToastHelper.makeText(mActivity, result.optString("data"), ToastHelper.LENGTH_LONG).setAnimation(R.style.popUpWindowEnterExit).show();
				VIPPrivilegeActivity.VIPStatus vipstatus = new VIPPrivilegeActivity().new VIPStatus();
				vipstatus.setWhetherOpen("y");
				EventBus.getDefault().post(vipstatus);
				finish();
			}
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();//mActivity);
		if (sname.equals("user/applyVip")) {
			if (!Utility.isEmpty(code) && code.equals("10001")) {
				dialog = new SkuaidiDialog(mActivity);
				dialog.setTitle("提示");
				dialog.isUseEditText(false);
				dialog.setContent(result);
				dialog.setPositionButtonTitle("充值");
				dialog.setNegativeButtonTitle("取消");
				dialog.setPosionClickListener(new PositonButtonOnclickListener() {

					@Override
					public void onClick(View v) {
						mIntent = new Intent(mActivity, TopUpActivity.class);
						startActivity(mIntent);
					}
				});
				dialog.showDialog();
			}else{
				UtilToolkit.showToast(result);
			}
		}else{
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
