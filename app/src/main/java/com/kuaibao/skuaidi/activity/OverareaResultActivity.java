package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.RotateLoadView;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * @author 罗娜 超派查询结果（查询条件包含区域和详细地址）
 */
public class OverareaResultActivity extends SkuaiDiBaseActivity {
	private Context context;

	private ImageView imv_overarea_result;
	private TextView tv_detail, tv_wait_result, tv_overarea_result, tv_result_tips;
	private RotateLoadView pro_waitingresult;
	private TextView tv_title_des;

	private String area_id, address, detail;

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.NETWORK_FAILED:
				UtilToolkit.showToast( "网络连接错误,请稍后重试!");
				break;
			case Constants.OVERAREA_GET_OK:
				// //System.out.println(msg.obj.toString());
				JsonXmlParser.parseOverarea(handler, msg.obj.toString());
				break;

			case Constants.OVERAREA_GET_FAILD:
				UtilToolkit.showToast( "对不起,网络发生异常!");
				break;

			case Constants.OVERAREA_PARSE_OK:

				findOk();
				if (msg.obj.toString().equals("deliver")) {
					imv_overarea_result.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_xiaolian));
					tv_overarea_result.setText("可以派送哟！");
				} else if (msg.obj.toString().equals("deliver")) {
					imv_overarea_result.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_kulian));
					tv_overarea_result.setText("不可派送哟！");
				}
				break;

			case Constants.OVERAREA_PARSE_FAILD:
				UtilToolkit.showToast( "获取超派信息有误");
				break;

			default:
				break;
			}
		}

		private void findOk() {
			tv_wait_result.setVisibility(View.GONE);
			imv_overarea_result.setVisibility(View.VISIBLE);
			tv_overarea_result.setVisibility(View.VISIBLE);
			pro_waitingresult.setVisibility(View.GONE);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);

		setContentView(R.layout.overarea_result);

		context = this;

		getControl();
		getData();
	}

	private void getControl() {
		imv_overarea_result = (ImageView) findViewById(R.id.imv_overarea_result);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_wait_result = (TextView) findViewById(R.id.tv_wait_result);
		tv_overarea_result = (TextView) findViewById(R.id.tv_overarea_result);
		pro_waitingresult = (RotateLoadView) findViewById(R.id.progress_waitingresult);
		pro_waitingresult.show();
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("查询结果");
	}

	private void getData() {
		Intent intent = getIntent();
		area_id = intent.getStringExtra("area_id");
		address = intent.getStringExtra("address");
		detail = intent.getStringExtra("detail");

		tv_detail.setText(detail);
		KuaidiApi.getOverarea(context, handler, area_id, address, SkuaidiSpf.getLoginUser().getExpressNo());
	}

	/**
	 * @param view
	 *            返回
	 */
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

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
