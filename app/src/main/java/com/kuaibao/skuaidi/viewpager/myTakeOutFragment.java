package com.kuaibao.skuaidi.viewpager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.OutSideDetailActivity;
import com.kuaibao.skuaidi.activity.adapter.MyTakeOutSideAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.fragment.SkuaidiBaseFragment;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的外单模块
 * 
 * @created 2015/1/5
 * @author 顾冬冬
 * @version V3.5.0
 */
public class myTakeOutFragment extends SkuaidiBaseFragment {

	private Context context;
	private View rootView = null;
	private PullToRefreshView pull;
	private ListView listView;
	private MyTakeOutSideAdapter mAdapter;
	private String latitude;// 经度
	private String longitude;// 纬度

	private List<LatestOutSide> latestOutSides;
	private int page = 1;
	private Intent mIntent;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.OUTSIDE_GET_SUCCESS:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				if (page == 1) {
					dismissProgressDialog();
					latestOutSides.clear();
					latestOutSides = (List<LatestOutSide>) msg.obj;
					mAdapter.notifyData(latestOutSides);
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
					dismissProgressDialog();
					mAdapter = new MyTakeOutSideAdapter(context, latestOutSides);
					listView.setAdapter(mAdapter);
				} else {
					UtilToolkit.showToast("已加载完全部数据");
				}
				break;
			case Constants.OUTSIDE_CODE_ISNOT_ZERO:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				break;
			case Constants.TIME_OUT_FAIL:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.my_take_out, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
		longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
		initView();
		initData();
		pullToRefresh();
		setListener();
	}

	/**
	 * 初始化界面控件
	 */
	private void initView() {
		pull = (PullToRefreshView) rootView
				.findViewById(R.id.pull_refresh_view);
		listView = (ListView) rootView.findViewById(R.id.lv_my_outside_block);
		latestOutSides = new ArrayList<LatestOutSide>();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		latestOutSides.clear();
		mAdapter = new MyTakeOutSideAdapter(context, latestOutSides);
		listView.setAdapter(mAdapter);

	}

	/**
	 * 加载刷新
	 */
	private void pullToRefresh() {
		// 下拉
		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				pull.postDelayed(new Runnable() {

					@Override
					public void run() {
						//System.out.println("进到这里来了~");
						if (Utility.isNetworkConnected() == false) {
							UtilToolkit.showToast("网络连接错误...");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							JSONObject data = (JSONObject) KuaidiApi
									.getOutSide(latitude,
											longitude, "get", "1", "", "", "1");
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
							JSONObject data = (JSONObject) KuaidiApi
									.getOutSide(latitude,
											longitude, "get", page + "", "",
											"", "1");
							httpInterfaceRequest(data, false,
									INTERFACE_VERSION_NEW);
						}
					}
				}, 1000);

			}
		});

	}

	private void setListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
//					UtilToolkit.showToast("gudddddd");
				} else {
					mIntent = new Intent(context, OutSideDetailActivity.class);
					mIntent.putExtra("position", position + 1);
					mIntent.putExtra("fromActivity", "myTakeOutFragment");
					SKuaidiApplication.getInstance().postMsg(
							"myTakeOutFragment", "OutSideDetailActivity",
							latestOutSides);
					startActivityForResult(mIntent, Constants.REQUEST_CODE);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE) {
			if (resultCode == Constants.RESULT_CODE) {
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication
						.getInstance().onReceiveMsg("OutSideDetailActivity",
								"myTakeOutFragment");
				String id = data.getStringExtra("outsideoutsideid");
				for (int i = 0; i < latestOutSides.size(); i++) {
					String outside_blockid = latestOutSides.get(i).getId();
					String status = latestOutSides.get(i).getState();
					if (outside_blockid.equals(id) && status.equals("pickup")) {// 如果存在这个ID并且该条中状态为等待则删除此条
						latestOutSides.remove(i);
						break;
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// 每次切换fragment时调用的方法
		if (isVisibleToUser) {
			if (Utility.isNetworkConnected() == false) {
				UtilToolkit.showToast("网络连接不可用，请设置网络");
			} else {
				latitude = SkuaidiSpf.getLatitudeOrLongitude(context)
						.getLatitude();
				longitude = SkuaidiSpf.getLatitudeOrLongitude(context)
						.getLongitude();
				page = 1;
				JSONObject data = (JSONObject) KuaidiApi.getOutSide(
						latitude, longitude, "get", "1", "", "", "1");
				httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
				showProgressDialog( "玩儿命为您加载...");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
		if (getUserVisibleHint()) {
			if (Utility.isNetworkConnected() == false) {

			} else {
				latitude = SkuaidiSpf.getLatitudeOrLongitude(context)
						.getLatitude();
				longitude = SkuaidiSpf.getLatitudeOrLongitude(context)
						.getLongitude();
				page = 1;
				JSONObject data = (JSONObject) KuaidiApi.getOutSide(
						latitude, longitude, "get", "1", "", "", "1");
				httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onRequestSucess(String sname, JSONObject result) {
		JsonXmlParser.parseOutSideList(context, handler, result);

	}

	@Override
	protected void onRequestFail(String sname, String message, String data_fail) {
		UtilToolkit.showToast(message);
		Message msg = new Message();
		msg.what = Constants.OUTSIDE_CODE_ISNOT_ZERO;// code 不为0
		handler.sendMessage(msg);
	}

	@Override
	protected void onRequestOldInterFaceFail(String sname, String msg,
			JSONObject result) {

	}

}
