package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FinishOutSideBlockAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 顾冬冬
 * @created 2015.1.7
 */
public class FinishOutSideBlockActivity extends SkuaiDiBaseActivity {

	private Context context;
	private Intent intent;
	private PullToRefreshView pull;
	private ListView listView;
	private ImageView iv_title_back;// 返回按钮
	private TextView tv_title_des;// title 标题
	
	private RelativeLayout rl_finish_outside;
	private RelativeLayout rl_tishi;

	private FinishOutSideBlockAdapter mAdapter;

	private List<LatestOutSide> latestOutSides;
	private String latitude;// 经度
	private String longitude;// 纬度

	private int page = 1;

	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.OUTSIDE_GET_SUCCESS:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				rl_finish_outside.setVisibility(View.VISIBLE);
				rl_tishi.setVisibility(View.GONE);
				if (page == 1) {
					if ((Boolean) SKuaidiApplication.getInstance()
							.onReceiveMsg("pagerFragment", "isShowProgress")) {
						dismissProgressDialog();
						SKuaidiApplication.getInstance().postMsg(
								"pagerFragment", "isShowProgress", false);
					}
					latestOutSides.clear();
					latestOutSides = (List<LatestOutSide>) msg.obj;
					mAdapter = new FinishOutSideBlockAdapter(context,
							latestOutSides);
					listView.setAdapter(mAdapter);
				} else {
					latestOutSides.addAll((List<LatestOutSide>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				break;
			case Constants.OUTSIDE_GET_DATAISNULL:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				if (page == 1) {
					rl_finish_outside.setVisibility(View.GONE);
					rl_tishi.setVisibility(View.VISIBLE);
					UtilToolkit.showToast("您还没有完成的订单，快去接单吧");
				} else {
					UtilToolkit.showToast("已加载完全部数据");
				}
				break;
			case Constants.TIME_OUT_FAIL:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				break;
			case Constants.OUTSIDE_CODE_ISNOT_ZERO:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				rl_finish_outside.setVisibility(View.GONE);
				rl_tishi.setVisibility(View.VISIBLE);
				dismissProgressDialog();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.finish_outside_block_activity);
		context = this;
		initView();
		pullToRefresh();
		initData();
		ListenerEvent();
	}

	private void initView() {
		rl_finish_outside = (RelativeLayout) findViewById(R.id.rl_finish_outside);
		rl_tishi = (RelativeLayout) findViewById(R.id.rl_tishi);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		listView = (ListView) findViewById(R.id.lv_finish_outside_block);
		latestOutSides = new ArrayList<LatestOutSide>();
		iv_title_back.setOnClickListener(new myOnClickListener());

		tv_title_des.setText("已完成任务");
	}

	private void pullToRefresh() {
		// 下拉
		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				pull.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (Utility.isNetworkConnected() == false) {
							UtilToolkit.showToast( "网络连接错误...");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							JSONObject data = (JSONObject) KuaidiApi
									.getOutSide(latitude,
											longitude, "get", "", "", "1", "");
							httpInterfaceRequest(data, false,
									INTERFACE_VERSION_NEW);
							page = 1;
						}
					}
				}, 1000);
			}
		});

		// 上拉
		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (Utility.isNetworkConnected() == false) {
							UtilToolkit.showToast("网络连接错误...");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							page = page + 1;
							JSONObject data = (JSONObject) KuaidiApi.getOutSide(latitude,longitude, "get", page + "", "","1", "");
							httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
						}
					}
				}, 1000);

			}
		});

	}

	private void initData() {
		Boolean isShowProgress = (Boolean) SKuaidiApplication.getInstance().onReceiveMsg("pagerFragment", "isShowProgress");
		if (isShowProgress == null || !isShowProgress) {
			showProgressDialog( "玩儿命为您加载...");
			SKuaidiApplication.getInstance().postMsg("pagerFragment","isShowProgress", true);
		}
		latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
		longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
		page = 1;
		JSONObject data = (JSONObject) KuaidiApi.getOutSide(latitude, longitude, "get", "", "", "1", "");
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
	}

	private void ListenerEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent = new Intent(context, OutSideDetailActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("fromActivity", "FinishOutSideBlockActivity");
				SKuaidiApplication.getInstance().postMsg(
						"FinishOutSideBlockActivity", "OutSideDetailActivity",
						latestOutSides);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(result!=null){
			JsonXmlParser.parseOutSideList(context, handler, result);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname,String message, String act, JSONObject data_fail) {
		if(!message.equals("")){
			UtilToolkit.showToast(message);
			Message msg = new Message();
			msg.what = Constants.OUTSIDE_CODE_ISNOT_ZERO;// code 不为0
			handler.sendMessage(msg);
		}
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
