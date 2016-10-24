package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;


/**
 * 分享成功后跳转的页面 
 * @author xy
 *
 */
public class ShareCallbackActivity extends SkuaiDiBaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.share_callback_activity_layout);
		initView();
	}
	
	private void initView(){
		TextView tv7 = (TextView) findViewById(R.id.share_callback_tvSeven);
		SpannableStringBuilder style = new SpannableStringBuilder("50元/每新注册一个！（系统自动奖励，红包隔天发送，一经发现作弊，立即取消红包资格）");
		style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.share_callback_red_text)),0,11,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tv7.setText(style);
	}
	
	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

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
	public void shareISee(View v){
		finish();
	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		
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
