package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @description 短信VIP特权界面
 * @author 顾冬冬
 * 
 */
public class VIPPrivilegeActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private SkuaidiImageView iv_title_back;// 返回
	private TextView tv_title_des;// 标题
	private SkuaidiTextView tv_more;// 更多
	private Button immediate_open;// 立即开通
	private TextView vip_cancelVip;// 关闭VIP功能按钮
	private Intent mIntent;
	private Activity mActivity;

	private String vipStatus = "";// 是否开通VIP状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.vip_privilege_activity);
		mActivity = this;
		EventBus.getDefault().register(this);
		initView();

	}

	private void initView() {
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		immediate_open = (Button) findViewById(R.id.immediate_open);
		vip_cancelVip = (TextView) findViewById(R.id.vip_cancelVip);

		tv_title_des.setText("短信VIP特权");
		tv_more.setText("开通协议");

		iv_title_back.setOnClickListener(this);
		immediate_open.setOnClickListener(this);
		tv_more.setOnClickListener(this);
		vip_cancelVip.setOnClickListener(this);

		vipStatus = SkuaidiSpf.getClientIsVIP(mActivity);
		if (!Utility.isEmpty(vipStatus) && vipStatus.equals("y")) {
			immediate_open.setBackgroundResource(R.drawable.shape_btn_gray1);
			immediate_open.setText("已开通");
			immediate_open.setEnabled(false);
			vip_cancelVip.setVisibility(View.VISIBLE);
		} else {
			immediate_open.setBackgroundResource(R.drawable.selector_base_green_qianse1);
			vip_cancelVip.setVisibility(View.GONE);
			immediate_open.setEnabled(true);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	// 接收eventbus传递过来的对象，用于处理此处VIP更新状态
	@Subscribe
	public void onEventMainThread(VIPStatus vipStatus) {
		if (!Utility.isEmpty(vipStatus.getWhetherOpen()) && vipStatus.getWhetherOpen().equals("y")) {
			SkuaidiSpf.saveClientIsVIP(mActivity, "y");// 保存用户是否是VIP用户状态【VIP特权可直接发送短信】
		}else{
			SkuaidiSpf.saveClientIsVIP(mActivity, "n");
		}
		String status = SkuaidiSpf.getClientIsVIP(mActivity);
		if (!Utility.isEmpty(status) && status.equals("y")) {
			immediate_open.setBackgroundResource(R.drawable.shape_btn_gray1);
			immediate_open.setText("已开通");
			immediate_open.setEnabled(false);
			vip_cancelVip.setVisibility(View.VISIBLE);
		} else {
			immediate_open.setBackgroundResource(R.drawable.selector_base_green_qianse1);
			vip_cancelVip.setVisibility(View.GONE);
			immediate_open.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.immediate_open:
			mIntent = new Intent(mActivity, VipPrerogativeWebViewActivity.class);
			mIntent.putExtra("url", Constants.VIP_PRIVILEGE_FOR_SMS + "?courier=" + SkuaidiSpf.getLoginUser().getUserId());
			startActivity(mIntent);
			break;
		case R.id.tv_more:
			mIntent = new Intent(mActivity, VipPrerogativeWebViewActivity.class);
			mIntent.putExtra("url", Constants.VIP_PRIVILEGE_FOR_SMS);
			startActivity(mIntent);
			break;
		case R.id.vip_cancelVip:
			closeVipDialog();
			break;
		default:
			break;
		}
	}
	
	// 关闭VIP特权
	private void closeVipDialog(){
		SkuaidiDialog dialog = new SkuaidiDialog(mActivity);
		dialog.setTitle("关闭提示");
		dialog.setContent("关闭后将不再享受短信VIP特权\n确定要关闭该特权？");
		dialog.isUseEditText(false);
		dialog.setPositionButtonTitle("关闭");
		dialog.setNegativeButtonTitle("取消");
		dialog.setPosionClickListener(new PositonButtonOnclickListener() {
			
			@Override
			public void onClick(View v) {
				closeVip();
			}
		});
		dialog.showDialog();
	}

	// 关闭VIP【接口】
	private void closeVip() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "user/closeVip");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		JSONObject json = null;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sname.equals("user/closeVip")) {
//			MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//			EventBus.getDefault().post(m);
			String data = json.optString("data");
			if (!Utility.isEmpty(data))
				ToastHelper.makeText(mActivity, data, ToastHelper.LENGTH_LONG).setAnimation(R.style.popUpWindowEnterExit).show();
			SkuaidiSpf.saveClientIsVIP(mActivity, "");// 设置没有开通vip
			Message mesg = new Message();
			mesg.what = 1;
			mHandler.sendMessage(mesg);
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (!Utility.isEmpty(result)) {
			ToastHelper.makeText(mActivity, result, ToastHelper.LENGTH_LONG).setAnimation(R.style.popUpWindowEnterExit).show();
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// vip关闭成功
				vipStatus = SkuaidiSpf.getClientIsVIP(mActivity);
				if (!Utility.isEmpty(vipStatus) && vipStatus.equals("y")) {
					immediate_open.setBackgroundResource(R.drawable.shape_btn_gray1);
					immediate_open.setText("已开通");
					immediate_open.setEnabled(false);
					vip_cancelVip.setVisibility(View.VISIBLE);
				} else {
					immediate_open.setBackgroundResource(R.drawable.selector_base_green_qianse1);
					immediate_open.setText("立即开通");
					vip_cancelVip.setVisibility(View.GONE);
					immediate_open.setEnabled(true);
				}
				break;

			default:
				break;
			}
		}
	};
	
	public class VIPStatus{
		private VIPStatus vipStatus;
		private String whetherOpen = "";// 是否已经打开
		public VIPStatus getVipStatus() {
			return vipStatus;
		}
		public void setVipStatus(VIPStatus vipStatus) {
			this.vipStatus = vipStatus;
		}
		public String getWhetherOpen() {
			return whetherOpen;
		}
		public void setWhetherOpen(String whetherOpen) {
			this.whetherOpen = whetherOpen;
		}
		
	}

}
