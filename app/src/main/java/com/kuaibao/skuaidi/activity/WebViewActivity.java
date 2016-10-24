package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * 绑定百度账号
 * 
 * @author 顾冬冬
 * 
 */
public class WebViewActivity extends SkuaiDiBaseActivity {

	private Activity activity;
	private Context context;
	
	private ViewGroup llReaded = null;// 我已阅读按钮
	
	private WebChromeClient wcc = null;
	private TextView tv_title_des;// 标题
	private WebView web_login_baidu;// web
	private RelativeLayout rl_iagree;
	private ImageView iv_checkbox;// 选择按钮
	private TextView tv_iagree_ok;// 同意并下一步
	private boolean isIAgreeTX = false;// 是否已经阅读提现说明
	private boolean isIAgreeCZ = false;// 是否已经阅读充值说明
	private String url;
	private String fromwhere;// 来源哪个点击
	private SkuaidiImageView iv_title_back;// 返回按钮
	private Map<String, String> pay_Params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.my_account_login_baidu_activity);
		activity = this;
		context = this;
		initView();
		initData();
		webView();
	}

	// 初始化控件
	private void initView() {
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);// 返回按钮
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		web_login_baidu = (WebView) findViewById(R.id.web);
		rl_iagree = (RelativeLayout) findViewById(R.id.rl_iagree);
		iv_checkbox = (ImageView) findViewById(R.id.iv_checkbox);
		tv_iagree_ok = (TextView) findViewById(R.id.tv_iagree_ok);
		llReaded = (ViewGroup)findViewById(R.id.llReaded);

		llReaded.setOnClickListener(new MyOnclickListener());
		tv_iagree_ok.setOnClickListener(new MyOnclickListener());
		iv_title_back.setOnClickListener(new MyOnclickListener());
	}

	// 初始化数据
	@SuppressWarnings("unchecked")
	private void initData() {
		rl_iagree.setVisibility(View.VISIBLE);
		iv_checkbox.setImageResource(R.drawable.select_edit_identity);
		fromwhere = getIntent().getStringExtra("fromwhere");
		isIAgreeTX = SkuaidiSpf.getIagree_tixian(context);
		isIAgreeCZ = SkuaidiSpf.getIagree_chongzhi(context);
		switch (fromwhere){
			case "weighing_explanation":
				tv_title_des.setText("重量录入说明");
				url = Constants.WEIGHING_EXP;
				rl_iagree.setVisibility(View.GONE);
				break;
			case "tixian":
				tv_title_des.setText("提现说明");
				url = Constants.FAQ_PROBLEM_CASH;
				break;
			case "chongzhi":
				if (!isIAgreeCZ) {
					tv_title_des.setText("充值说明");
					url = Constants.MY_ACCOUNT_CHONGZHI_DESC;
				} else {
					Intent intent = new Intent(context, TopUpActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			case "moreActivity":
				tv_title_des.setText("快递员APP网页版");
				rl_iagree.setVisibility(View.GONE);
				break;
			case "preview_myshop":
				tv_title_des.setText("预览店铺");
				url = getIntent().getStringExtra("express_preview_myshop_url");
				rl_iagree.setVisibility(View.GONE);
				break;
			case "gunscan":
				tv_title_des.setText("巴枪扫描");
				url = Constants.GUN_SCAN_DESC;
				rl_iagree.setVisibility(View.GONE);
				break;
			case "baidupay":
				tv_title_des.setText("百度充值");
				pay_Params = (Map<String, String>) getIntent().getSerializableExtra("urlParams");
				url = Constants.POST_PAY_URL;
				rl_iagree.setVisibility(View.GONE);
				break;
			case "alisan":
				tv_title_des.setText("支付宝扫码");
				pay_Params = (Map<String, String>) getIntent().getSerializableExtra("urlParams");
				url = Constants.POST_PAY_URL;
				rl_iagree.setVisibility(View.GONE);
				break;
			case "receive_money":
				tv_title_des.setText("在线收款");
				url = Constants.RECEIVE_MONEY_FOR_BUSINESS + SkuaidiSpf.getLoginUser().getUserId();
				rl_iagree.setVisibility(View.GONE);
				break;
			case "receive_money_byOrder":
				tv_title_des.setText("在线收款");
				String orderNo = getIntent().getStringExtra("orderNo");
				url = Constants.RECEIVE_MONEY_FOR_ORDERDETAIL + orderNo;
				rl_iagree.setVisibility(View.GONE);
				break;
			case "MakeCollectionsActivity":
				url = getIntent().getStringExtra("url");
				tv_title_des.setText(getIntent().getStringExtra("title"));
				rl_iagree.setVisibility(View.GONE);
				break;
			case "cash_baidu":
				url = Constants.BIND_BAIDU_ACCOUNT + SkuaidiSpf.getLoginUser().getPhoneNumber();
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
			case "cash_alipay":
				url = getIntent().getStringExtra("url");
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				 break;
			case "disclaimer":
				url = Constants.DISCLAIMER;
				rl_iagree.setVisibility(View.GONE);
				tv_title_des.setText("免责声明");
				break;
			case "descAll":
				url = Constants.DESC_BALANCE_CASH_COMSUMPATION;
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
			case "problemTopUp":
				url = Constants.MY_ACCOUNT_CHONGZHI_DESC;
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
			case "problemConSumpation":
				url = Constants.FAQ_PROBLEM_COMSUMPATION;
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
			case "problemCash":// 常见问题：提现问题
				url = Constants.FAQ_PROBLEM_CASH;
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
			case "help":
				url = Constants.MY_ACCOUNT_CHONGZHI_DESC;
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
			case "cancellation":
				url = Constants.CANCELLATION_APP+SkuaidiSpf.getLoginUser().getPhoneNumber();
				rl_iagree.setVisibility(View.GONE);
				setWebviewTitle();
				break;
		}
	}
	
	/**
	 * @Title: setWebviewTitle 
	 * @Description: 获取url的title设置在标题上
	 * @param 
	 * @return void
	 */
	private void setWebviewTitle(){
		wcc = new WebChromeClient() {  
            @Override  
            public void onReceivedTitle(WebView view, String title) {  
                super.onReceivedTitle(view, title);  
                tv_title_des.setText(title);  
            }  
  
        }; 
	}

	// 显示网页
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private void webView() {
		if (fromwhere.equals("baidupay") || fromwhere.equals("alisan")) {
			StringBuffer sb = null;
			if (null != pay_Params) {
				Iterator<String> it = pay_Params.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = pay_Params.get(key);
					if (null == sb) {
						sb = new StringBuffer();
					} else {
						sb.append("&");
					}
					sb.append(key);
					//System.out.println("gudd key " + key);
					sb.append("=");
					sb.append(value);
				}
			}
			String postData = sb.toString();
			web_login_baidu.postUrl(url, EncodingUtils.getBytes(postData, "base64"));
		} else if (fromwhere.equals("moreActivity")) {
			String urlurl = "http://kdy.kuaidihelp.com/inform/home";
			String session = SkuaidiSpf.getLoginUser().getSession_id();
			String session_id="";
			if(!TextUtils.isEmpty(session)){
				 session_id = session.substring(0, session.trim().length() - 1);
			}

			String cookies = "session_id=" + session_id;
			CookieSyncManager.createInstance(context);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			cookieManager.removeAllCookie();// 移除
			cookieManager.setCookie(urlurl, cookies.toString());

			// //Log.i("GUDDD", "快递员主页设置的cookie：url:" + url +
			// "                           "
			// + cookieManager.getCookie(url));
			CookieSyncManager.getInstance().sync();
			web_login_baidu.loadUrl(urlurl);
		} else {
			web_login_baidu.loadUrl(url);
		}
		WebSettings webSettings = web_login_baidu.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);// 设置支持javascript脚本
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
		webSettings.setSupportZoom(false);// 支持缩放
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
		web_login_baidu.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setTitle("Loading...");
				activity.setProgress(progress * 100);
				if (progress == 100)
					activity.setTitle(R.string.app_name);
			}
		});

		web_login_baidu.setWebChromeClient(wcc);
		web_login_baidu.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) { // Handle
																												// the
																												// error
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.substring(0, 4).equals("tel:")) {// 如果字符串从第一个字符到第4个字符是“tel：”说明是拨打电话手机号
					final String telPhone = url.substring(4, url.length());// 获取"tel:"字符串后面的手机所有字符
					SkuaidiDialog dialog = new SkuaidiDialog(context);
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
//							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telPhone));
//							startActivity(intent);
							AcitivityTransUtil.showChooseTeleTypeDialog(WebViewActivity.this, "", telPhone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
						}
					});
				} else if (url.substring(0, 4).equals("sms:")) {// 如果字符串第一个字符到第4个字符是“sms:”说明是发送短信的手机号
					final String smsPhone = url.substring(4, url.length());// 获取"sms:"字符串后面的手机所有字符
					// 发送短信
					Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + smsPhone));
					context.startActivity(sendIntent);
				} else {
					if(url.contains("http://m.kuaidihelp.com/order/result?user_type=wduser_id")){
//						MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//						EventBus.getDefault().post(m);
					}
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
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web_login_baidu.canGoBack()) {
			web_login_baidu.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
			// 返回按钮
			case R.id.iv_title_back:
				setResult(CashActivity.RESULT_BIND_PAYTYPE);
				finish();
				break;

			// 下一步
			case R.id.tv_iagree_ok:
				if (fromwhere.equals("tixian")) {
					// 账户存在
					if (!isIAgreeTX) {// 没有阅读
						UtilToolkit.showToast("请阅读提现说明");
					} else {// 进入提现界面
						Intent intent = new Intent(context, CashActivity.class);
						startActivity(intent);
						finish();
					}
				} else if (fromwhere.equals("chongzhi")) {
					if (!isIAgreeCZ) {
						UtilToolkit.showToast("请阅读充值说明");
					} else {
						Intent intent = new Intent(context, TopUpActivity.class);
						startActivity(intent);
						finish();
					}
				}

				break;
			// 选择已阅读按钮
			case R.id.llReaded:
				if (fromwhere.equals("tixian")) {
					if (!SkuaidiSpf.getIagree_tixian(context)) {
						iv_checkbox.setImageResource(R.drawable.batch_add_checked);// 被选中
						SkuaidiSpf.saveIagree_tixian(context, true);
						isIAgreeTX = SkuaidiSpf.getIagree_tixian(context);
					} else {
						iv_checkbox.setImageResource(R.drawable.select_edit_identity);// 取消选中
						SkuaidiSpf.saveIagree_tixian(context, false);
						isIAgreeTX = SkuaidiSpf.getIagree_tixian(context);
					}
				} else if (fromwhere.equals("chongzhi")) {
					if (!SkuaidiSpf.getIagree_chongzhi(context)) {
						iv_checkbox.setImageResource(R.drawable.batch_add_checked);// 被选中
						SkuaidiSpf.saveIagree_chongzhi(context, true);
						isIAgreeCZ = SkuaidiSpf.getIagree_chongzhi(context);
					} else {
						iv_checkbox.setImageResource(R.drawable.select_edit_identity);// 取消选中
						SkuaidiSpf.saveIagree_chongzhi(context, false);
						isIAgreeCZ = SkuaidiSpf.getIagree_chongzhi(context);
					}
				}
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
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (Utility.isNetworkConnected()) {
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
