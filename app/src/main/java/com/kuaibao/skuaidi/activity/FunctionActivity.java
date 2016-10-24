package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.VersionUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 顾冬冬 功能介绍
 */
public class FunctionActivity extends RxRetrofitBaseActivity {
	TextView tv_title_des;
	private SkuaidiImageView iv_title_back;// 返回按钮
	private Context context;
	private WebView webView;
	private static final String URL = "http://m.kuaidihelp.com/help/s_android?v=";

	private String branch = "";

	private TextView tv_function_1, tv_function_2, tv_function_3, tv_function_4, tv_function_5, tv_function_6,
			tv_function_7, tv_function_8;
	private ImageView img_function_1, img_function_2, img_function_3, img_function_4, img_function_5, img_function_6,
			img_function_7, img_function_8;
	private ImageView img_function_12, img_function_22, img_function_32, img_function_42, img_function_52,
			img_function_62, img_function_72, img_function_82;
	private LinearLayout ll_function_1, ll_function_2, ll_function_3, ll_function_4, ll_function_5, ll_function_6,
			ll_function_7, ll_function_8;
	private TextView tv_function_content_1, tv_function_content_2, tv_function_content_3, tv_function_content_4,
			tv_function_content_5, tv_function_content_6, tv_function_content_7, tv_function_content_8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.more_function);
		context = this;
		branch = SkuaidiSpf.getLoginUser().getExpressNo();

		webView = (WebView) findViewById(R.id.webview);

		findView();
		String url = URL + VersionUtil.getCurrentVersion(context).substring(1);
		//System.out.println(url);
		webShow(url);
	}

	// web
	private void webShow(String url) {
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
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);// 返回按钮
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("功能介绍");

		// // 部分
		// ll_function_1 = (LinearLayout) findViewById(R.id.ll_function_1);
		// ll_function_2 = (LinearLayout) findViewById(R.id.ll_function_2);
		// ll_function_3 = (LinearLayout) findViewById(R.id.ll_function_3);
		// ll_function_4 = (LinearLayout) findViewById(R.id.ll_function_4);
		// ll_function_5 = (LinearLayout) findViewById(R.id.ll_function_5);
		// ll_function_6 = (LinearLayout) findViewById(R.id.ll_function_6);
		// ll_function_7 = (LinearLayout) findViewById(R.id.ll_function_7);
		// ll_function_8 = (LinearLayout) findViewById(R.id.ll_function_8);
		//
		// // 标题
		// tv_function_1 = (TextView) findViewById(R.id.tv_function_1);
		// tv_function_2 = (TextView) findViewById(R.id.tv_function_2);
		// tv_function_3 = (TextView) findViewById(R.id.tv_function_3);
		// tv_function_4 = (TextView) findViewById(R.id.tv_function_4);
		// tv_function_5 = (TextView) findViewById(R.id.tv_function_5);
		// tv_function_6 = (TextView) findViewById(R.id.tv_function_6);
		// tv_function_7 = (TextView) findViewById(R.id.tv_function_7);
		// tv_function_8 = (TextView) findViewById(R.id.tv_function_8);
		//
		// // 说明
		// tv_function_content_1 = (TextView)
		// findViewById(R.id.tv_function_content_1);
		// tv_function_content_2 = (TextView)
		// findViewById(R.id.tv_function_content_2);
		// tv_function_content_3 = (TextView)
		// findViewById(R.id.tv_function_content_3);
		// tv_function_content_4 = (TextView)
		// findViewById(R.id.tv_function_content_4);
		// tv_function_content_5 = (TextView)
		// findViewById(R.id.tv_function_content_5);
		// tv_function_content_6 = (TextView)
		// findViewById(R.id.tv_function_content_6);
		// tv_function_content_7 = (TextView)
		// findViewById(R.id.tv_function_content_7);
		// tv_function_content_8 = (TextView)
		// findViewById(R.id.tv_function_content_8);
		//
		//
		// // 第一张图
		// img_function_1 = (ImageView) findViewById(R.id.img_function_1);
		// img_function_2 = (ImageView) findViewById(R.id.img_function_2);
		// img_function_3 = (ImageView) findViewById(R.id.img_function_3);
		// img_function_4 = (ImageView) findViewById(R.id.img_function_4);
		// img_function_5 = (ImageView) findViewById(R.id.img_function_5);
		// img_function_6 = (ImageView) findViewById(R.id.img_function_6);
		// img_function_7 = (ImageView) findViewById(R.id.img_function_7);
		// img_function_8 = (ImageView) findViewById(R.id.img_function_8);
		//
		// // 第二张图
		// img_function_12 = (ImageView) findViewById(R.id.img_function_12);
		// img_function_22 = (ImageView) findViewById(R.id.img_function_22);
		// img_function_32 = (ImageView) findViewById(R.id.img_function_32);
		// img_function_42 = (ImageView) findViewById(R.id.img_function_42);
		// img_function_52 = (ImageView) findViewById(R.id.img_function_52);
		// img_function_62 = (ImageView) findViewById(R.id.img_function_62);
		// img_function_72 = (ImageView) findViewById(R.id.img_function_72);
		// img_function_82 = (ImageView) findViewById(R.id.img_function_82);
		//
		// tv_function_1.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_2.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_3.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_4.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_5.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_6.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_7.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		// tv_function_8.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		//
		// if(branch.equals("sto")){
		// tv_function_1.setText(context.getResources().getString(R.string.sto_function_1));
		// tv_function_2.setText(context.getResources().getString(R.string.sto_function_2));
		// tv_function_3.setText(context.getResources().getString(R.string.sto_function_3));
		// tv_function_4.setText(context.getResources().getString(R.string.sto_function_4));
		// tv_function_5.setText(context.getResources().getString(R.string.sto_function_5));
		// tv_function_6.setText(context.getResources().getString(R.string.sto_function_6));
		// tv_function_7.setText(context.getResources().getString(R.string.sto_function_7));
		// tv_function_8.setText(context.getResources().getString(R.string.sto_function_8));
		//
		// tv_function_content_1.setText(context.getResources().getString(R.string.sto_function_content_1));
		// tv_function_content_2.setText(context.getResources().getString(R.string.sto_function_content_2));
		// tv_function_content_3.setText(context.getResources().getString(R.string.sto_function_content_3));
		// tv_function_content_4.setText(context.getResources().getString(R.string.sto_function_content_4));
		// tv_function_content_5.setText(context.getResources().getString(R.string.sto_function_content_5));
		// tv_function_content_6.setText(context.getResources().getString(R.string.sto_function_content_6));
		// tv_function_content_7.setText(context.getResources().getString(R.string.sto_function_content_7));
		// tv_function_content_8.setText(context.getResources().getString(R.string.sto_function_content_8));
		//
		// img_function_1.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic1"));
		// img_function_2.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic2"));
		// img_function_3.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic3"));
		// img_function_4.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic4"));
		// img_function_5.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic5"));
		// img_function_6.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic6"));
		// img_function_7.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic7"));
		// img_function_8.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic8"));
		//
		// img_function_12.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic1_2"));
		// img_function_22.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic2_2"));
		// img_function_32.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic3_2"));
		// img_function_42.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic4_2"));
		// img_function_52.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic5_2"));
		// img_function_62.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic6_2"));
		// img_function_72.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic7_2"));
		// img_function_82.setVisibility(View.GONE);
		// }else{
		// tv_function_1.setText(context.getResources().getString(R.string.function_1));
		// tv_function_2.setText(context.getResources().getString(R.string.function_2));
		// tv_function_3.setText(context.getResources().getString(R.string.function_3));
		// tv_function_4.setText(context.getResources().getString(R.string.function_4));
		// tv_function_5.setText(context.getResources().getString(R.string.function_5));
		// tv_function_6.setText(context.getResources().getString(R.string.function_6));
		// tv_function_7.setText(context.getResources().getString(R.string.function_7));
		// tv_function_8.setText(context.getResources().getString(R.string.function_8));
		//
		// tv_function_content_1.setText(context.getResources().getString(R.string.function_content_1));
		// tv_function_content_2.setText(context.getResources().getString(R.string.function_content_2));
		// tv_function_content_3.setText(context.getResources().getString(R.string.function_content_3));
		// tv_function_content_4.setText(context.getResources().getString(R.string.function_content_4));
		// tv_function_content_5.setText(context.getResources().getString(R.string.function_content_5));
		// tv_function_content_6.setText(context.getResources().getString(R.string.function_content_6));
		// tv_function_content_7.setText(context.getResources().getString(R.string.function_content_7));
		// tv_function_content_8.setText(context.getResources().getString(R.string.function_content_8));
		//
		// img_function_1.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic1"));
		// img_function_2.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic2"));
		// img_function_3.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic3"));
		// img_function_4.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic4"));
		// img_function_5.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic5"));
		// img_function_6.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic6"));
		// img_function_7.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic7"));
		//
		// img_function_12.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic1_2"));
		// img_function_22.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic2_2"));
		// img_function_32.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic3_2"));
		// img_function_42.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic4_2"));
		// img_function_52.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic5_2"));
		// img_function_62.setBackgroundResource(SkuaidiSkinManager.getSkinResId("gn_pic6_2"));
		// img_function_72.setVisibility(View.GONE);
		// ll_function_8.setVisibility(View.GONE);
		// }
		//
		//
		//
		iv_title_back.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back: // 返回
				finish();
				break;
			default:
				break;
			}
		}
	};

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
