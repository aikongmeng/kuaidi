package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.TrachResultAdapter;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.entry.WuliuInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * @author 边道坚 运单跟踪
 */
public class TrackNoDetailActivity extends RxRetrofitBaseActivity {
	boolean flag;
	Context context;
	TextView track_no;
	TextView tv_title_des;
	ListView lv_result;
	TrachResultAdapter adapter;
	List<WuliuInfo> Wuliu;
	private WuliuInfo WuLiu;
	private boolean info_flag = false;// 从订单详情而来
	private boolean genzong = false;//从免费通知客户界面进来

	boolean deliver_flag = false;

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.NETWORK_FAILED:
				dismissProgressDialog();//TrackNoDetailActivity.this);
				UtilToolkit.showToast( "网络连接错误,请稍后重试!");
				break;
			case Constants.FIND_EXPRESS_GET_OK:
				if (msg.obj.toString() != null) {
					JsonXmlParser.parseFindExpress(handler,
							msg.obj.toString());
				}
				break;
			case Constants.FIND_EXPRESS_GET_FAID:
				dismissProgressDialog();//TrackNoDetailActivity.this);
				break;
			case Constants.FIND_EXPRESS_PAISE_FAID_TWO:
				dismissProgressDialog();//TrackNoDetailActivity.this);
				UtilToolkit.showToast((String)msg.obj);
				break;
			case Constants.FIND_EXPRESS_PAISE_OK:
				dismissProgressDialog();//TrackNoDetailActivity.this);
				WuLiu = (WuliuInfo) msg.obj;
				try {
					if (WuLiu.getIsException().equals("0")) {
						flag = false;
					} else if (WuLiu.getIsException().equals("1")) {
						flag = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				adapter = new TrachResultAdapter(context, handler, flag,
						WuLiu.getWuliuItems());
				lv_result.setAdapter(adapter);

				break;
			case Constants.FIND_EXPRESS_PAISE_FAID:
				dismissProgressDialog();//TrackNoDetailActivity.this);
				UtilToolkit.showToast( "获取信息失败");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.trach_result);
		context = this;
		getControl();
		getData();
	}

	private void getData() {
		String no = getIntent().getStringExtra("deliver_no");//通过intent传递过来的运单号
		String time = getIntent().getStringExtra("deliverno_time");
		info_flag = getIntent().getBooleanExtra("orderInfo", false);
		genzong = getIntent().getBooleanExtra("genzong", false);

		// 派件详情--title 跳转而来
		deliver_flag = getIntent().getBooleanExtra("deliver_flag", false);
		track_no.setText(no);

		KuaidiApi.findExpress(context, handler, no);

		showProgressDialog("");//TrackNoDetailActivity.this,"");
	}

	private void getControl() {
		lv_result = (ListView) findViewById(R.id.lv_trach_result);
		track_no = (TextView) findViewById(R.id.tv_result_num);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("运单跟踪");
	}

	public void back(View view) {
		if (deliver_flag) {
			finish();
		} else if (info_flag) {

			finish();
		}else if (genzong) {
			finish();
		}else {
//			Intent intent = new Intent(context, NotifyDetailActivity.class);
//			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (deliver_flag) {
				finish();
			} else if (info_flag) {
				finish();
			}else if (genzong) {
				finish();
			} else {
//				Intent intent = new Intent(context, NotifyDetailActivity.class);
//				startActivity(intent);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
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
