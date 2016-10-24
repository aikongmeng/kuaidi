package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * 预览我的店铺
 * 
 * @author gudd
 * 
 */
public class PreviewShopActivity extends SkuaiDiBaseActivity {

	private Context context;
	// title 部分
	private ImageView left_title_back_image;// 返回按钮
	private TextView middle_title_des_text;// 标题
	private RelativeLayout right_title;// 右侧按钮
	private TextView right_more_text;// 右侧按钮文字
	// WebView
	private WebView preview_my_shop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.preview_shop_activity);
		context = this;
		
		initView();
	}

	//初始化界面
	private void initView() {
		left_title_back_image = (ImageView) findViewById(R.id.left_title_back_image);
		middle_title_des_text = (TextView) findViewById(R.id.middle_title_des_text);
		right_title = (RelativeLayout) findViewById(R.id.right_title);
		right_more_text = (TextView) findViewById(R.id.right_more_text);
		preview_my_shop = (WebView) findViewById(R.id.preview_my_shop);
		
		left_title_back_image.setOnClickListener(onClickListener);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_title_back_image:
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

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
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

}
