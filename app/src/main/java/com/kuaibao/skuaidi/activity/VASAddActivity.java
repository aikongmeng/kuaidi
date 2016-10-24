package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: VASAddActivity
 * @Description: 添加增值业务
 * @author 顾冬冬
 * @date 2015-11-24 下午4:06:05
 */
public class VASAddActivity extends RxRetrofitBaseActivity implements OnClickListener {

	public static int DELIVERY = 0X1001;
	public static int DELIVERY_CALL = 0X1002;
	public static int DELIVERY_TIMING = 0x1003;

	private Context mContext = null;
	private Intent mIntent = null;

	// title 部分
	private SkuaidiImageView back = null;
	private TextView title = null;
	// body 部分
	private ViewGroup delivery = null;// 送货上门服务
	private ViewGroup delivery_call = null;// 送货上门前联系
	private ViewGroup delivery_timing = null;// 定时送货上门
	private ViewGroup cailai = null;// 财来活动

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.vas_add_activity);
		mContext = this;

		initview();
	}

	private void initview() {
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		title = (TextView) findViewById(R.id.tv_title_des);

		delivery = (ViewGroup) findViewById(R.id.delivery);
		delivery_call = (ViewGroup) findViewById(R.id.delivery_call);
		delivery_timing = (ViewGroup) findViewById(R.id.delivery_timing);
		cailai = (ViewGroup) findViewById(R.id.cailai);

		back.setOnClickListener(this);
		delivery.setOnClickListener(this);
		delivery_call.setOnClickListener(this);
		delivery_timing.setOnClickListener(this);
		cailai.setOnClickListener(this);

		title.setText("添加增值业务");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.iv_title_back:
			setResult(VASActivity.VAS_ADD_ACTIVITY_FINISH);
			finish();
			break;
		// 送货上门
		case R.id.delivery:
			mIntent = null;
			mIntent = new Intent(mContext, VASAddDetailActivity.class);
			mIntent.putExtra("fromEvent", DELIVERY);
			startActivity(mIntent);
			break;
		// 送货上门前联系
		case R.id.delivery_call:
			mIntent = null;
			mIntent = new Intent(mContext, VASAddDetailActivity.class);
			mIntent.putExtra("fromEvent", DELIVERY_CALL);
			startActivity(mIntent);
			break;
		// 指定时间送货上门
		case R.id.delivery_timing:
			mIntent = null;
			mIntent = new Intent(mContext, VASAddDetailActivity.class);
			mIntent.putExtra("fromEvent", DELIVERY_TIMING);
			startActivity(mIntent);
			break;
		// 财来网活动
		case R.id.cailai:
			mIntent = null;
			mIntent = new Intent(mContext, VASCaiLaiActivity.class);
			startActivity(mIntent);
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


}
