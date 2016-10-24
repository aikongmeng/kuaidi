package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.circle.CircleExpressDetailActivity;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 顾冬冬
 * 
 */
public class LoadWebInformationActivity extends SkuaiDiBaseActivity {
	//private ProgressDialog pdWaitingMessage;
	private Context context;
	private TextView tv_title_des;

	private LinearLayout ll_menu;
	private TextView tv_share;// 分享
	private TextView tv_pinglun;// 评论
	private View view2;

	private String url;
	private String group_id="";//吐槽id
	private String description;// 描述
//	private String tucao_id;//吐槽id
	private String fid;//从首页传递过来的id
	private String resource;//跳转页面来源

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.loadwebinformation);
		fid = getIntent().getStringExtra("id");
		resource = getIntent().getStringExtra("resource");
		initView();
		getData();
		if (Utility.isEmpty(fid)) {
			setData();
		}
	}

	private void initView() {
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
		tv_share = (TextView) findViewById(R.id.tv_share);
		tv_pinglun = (TextView) findViewById(R.id.tv_pinglun);
		view2 = findViewById(R.id.view2);
		if (!Utility.isEmpty(fid)) {
			tv_pinglun.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
		}
		tv_title_des.setText("资讯中心");
		context = this;
		SkuaidiSpf.saveHotDot(context, false);
		getDialog();

		tv_share.setOnClickListener(new MyClickEvent());
		tv_pinglun.setOnClickListener(new MyClickEvent());
	}

	private void getData() {
		if(fid == null){
			group_id = getIntent().getStringExtra("group_id");
			description = getIntent().getStringExtra("title");
			if(getIntent().getStringExtra("loadType")==null){
				url = getIntent().getStringExtra("url");
			}else{
				url = getIntent().getStringExtra("url");
				group_id = getIntent().getStringExtra("tucao_id");
			}
		}else{
//			group_id = fid;
			JSONObject data = new JSONObject();
			try {
				data.put("sname", "news/getinfo");
				data.put("id", fid);
				data.put("type", "server");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		}
	}

	private void setData() {
		if (group_id.equals("0")) {
			tv_pinglun.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
		}
        //Log.i("iii", "********************setData()********************"+url);
//		tv_title_des.setText("资讯中心");
//		context = this;
//		SkuaidiSpf.saveHotDot(context, false);
//		getDialog();
		WebView webView = (WebView) findViewById(R.id.web_information);
		
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

			@Override
			public void onPageFinished(WebView view, String url) {
				dismissProgressDialog();//LoadWebInformationActivity.this);
				//pdWaitingMessage.dismiss();
			}
		});
		webView.loadUrl(url);
	}

	public void getDialog() {

		showProgressDialog("");//LoadWebInformationActivity.this,"");
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
	
	@Override
	public void onBackPressed() {
		if("splash".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}else if("circle".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("tabid", 1);
			startActivity(intent);
		}
		finish();
	}

	public void back(View view) {
		if("splash".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}else if("circle".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("tabid", 1);
			startActivity(intent);
		}
		finish();
	}

	class MyClickEvent implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_share:
				UMShareManager.onEvent(context, "informationCenter_share", "informationCenter", "资讯中心：分享");
				String title = "快递行业资讯";
				String shareText = description;
				String targetUrl = url+ "&cm_id="
						+ SkuaidiSpf.getLoginUser().getUserId() + "&phone=" +SkuaidiSpf.getLoginUser().getPhoneNumber()
						+"?from=groupmessage&isappinstalled=1";
				Map<String, String> shareTexts = new HashMap<String, String>();
				shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
				
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText+url+ "&cm_id="
						+ SkuaidiSpf.getLoginUser().getUserId());
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText+url+ "&cm_id="
						+ SkuaidiSpf.getLoginUser().getUserId());
				shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText+url+ "&cm_id="
						+ SkuaidiSpf.getLoginUser().getUserId());
				shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText+url+ "&cm_id="
						+ SkuaidiSpf.getLoginUser().getUserId());
				openShare(title, shareTexts, targetUrl,R.drawable.share_info);
				break;

			case R.id.tv_pinglun:
				UMShareManager.onEvent(context, "informationCenter_comment", "informationCenter", "资讯中心：评论");
				Intent mIntent = new Intent(context,CircleExpressDetailActivity.class);
				mIntent.putExtra("topic_id", group_id);
				startActivity(mIntent);
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		Log.i("iii","++++++++++++++"+json+"++++++++++++");
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if("news/getinfo".equals(sname) && null != result){
//			group_id = fid;
			url = result.optString("url");
			description = result.optString("title");
			setData();
			
		}
		
	}


	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		url = "";
		setData();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if(code.equals("7") && null != result){
			try {
				String desc = result.optString("desc");
				UtilToolkit.showToast(desc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
