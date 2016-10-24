package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.umeng.analytics.MobclickAgent;

import cn.feng.skin.manager.base.BaseActivity;

/**
 * @author 罗娜 工具
 */
public class ToolActivity extends BaseActivity {
	Context context;
	private TextView tv_title_des;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);

		setContentView(R.layout.tabhost_tool);
		findView();
		context = this;
	}

	private void findView() {
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("工具");
	}

	/**
	 * @param view
	 *            点击进入超快查询界面
	 */
	public void overareaQuery(View view) {
		Intent intent = new Intent(context, OverareaQueryActivity.class);
		startActivity(intent);
	}

	/**
	 * @param view
	 *            点击进入快递查询界面
	 */
	public void findExpress(View view) {
		Intent intent = new Intent(context, FindExpressActivity.class);
		startActivity(intent);
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

	public void back(View view) {
		finish();
	}

}
