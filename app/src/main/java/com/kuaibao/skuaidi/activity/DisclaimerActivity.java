package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 小费的提示声明
 * 
 */
public class DisclaimerActivity extends RxRetrofitBaseActivity {
	Context context;
	TextView tv_title_des;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.point_state);
		context = this;
		findView();
	}

	private void findView() {
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("提示声明");
	}

	public void back(View view) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

}
