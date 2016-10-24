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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.AddShopActivity;
import com.kuaibao.skuaidi.activity.adapter.GetMyShopListAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.fragment.SkuaidiBaseFragment;
import com.kuaibao.skuaidi.entry.ShopInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的店铺模块
 * 
 * @created 2015/1/5
 * @author 顾冬冬
 * @version V3.5.0
 */
public class myShopFragment extends SkuaidiBaseFragment {
	private View rootView = null;
	private Intent intent = null;

	private Context context;
	private RelativeLayout rl_my_shop;// 我的店铺列表
	private RelativeLayout rl_add_shop;// 添加店铺
	private PullToRefreshView pull;
	private ListView listView;// 我的店铺列表
	private TextView tv_add_new_shop;// 添加店铺按钮

	private Object[] object;
	private String total_records;
	private String total_pages;
	private String page_num;

	private int page = 1;
	private int cur_page = 1;

	// 与适配器有关
	private GetMyShopListAdapter adapter;

	/** 店铺列表有关 **/
	private List<ShopInfo> shopInfos;
	private List<ShopInfo> shopInfos2;// 存放加载更多数据的时候的临时数据
	private ShopInfo shopInfo;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GET_SHOP_INFO_SUCCESS:// 获取店铺列表成功
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				rl_my_shop.setVisibility(View.VISIBLE);
				rl_add_shop.setVisibility(View.GONE);
				if (page == 1) {
					dismissProgressDialog();
					shopInfos.clear();
					object = (Object[]) msg.obj;
					shopInfos = (List<ShopInfo>) object[0];
					total_records = (String) object[1];
					total_pages = (String) object[2];
					page_num = (String) object[3];
					if (shopInfos.size() > 0) {
						listView.setVisibility(View.VISIBLE);
						rl_add_shop.setVisibility(View.GONE);
						adapter = new GetMyShopListAdapter(context, shopInfos);
						listView.setAdapter(adapter);
					} else {
						listView.setVisibility(View.GONE);
						rl_add_shop.setVisibility(View.VISIBLE);
					}
				} else {
					cur_page = page;// 得到当前页
					page = cur_page;// 将当前页设置到page中
					object = (Object[]) msg.obj;
					shopInfos2 = (List<ShopInfo>) object[0];
					//System.out.println("gudd  shopInfos2    "+ shopInfos2.size() + "        "+ shopInfos2.get(0));
					total_records = (String) object[1];
					total_pages = (String) object[2];
					page_num = (String) object[3];
					shopInfos.addAll(shopInfos2);
					adapter.notifyDataSetChanged();
				}
				break;
			case Constants.GET_SHOP_INFO_FAIL:// 未获得店铺列表数据
				dismissProgressDialog();
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				shopInfos.clear();
				rl_my_shop.setVisibility(View.GONE);
				rl_add_shop.setVisibility(View.VISIBLE);
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
		rootView = inflater
				.inflate(R.layout.my_shop_fragment, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();

		pullToRefresh();
		setEvent();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		rl_my_shop = (RelativeLayout) rootView.findViewById(R.id.rl_my_shop);
		rl_add_shop = (RelativeLayout) rootView.findViewById(R.id.rl_add_shop);
		pull = (PullToRefreshView) rootView
				.findViewById(R.id.pull_refresh_view);
		listView = (ListView) rootView.findViewById(R.id.lv_my_shop);
		tv_add_new_shop = (TextView) rootView
				.findViewById(R.id.tv_add_new_shop);

		tv_add_new_shop.setOnClickListener(new MyOnclickListener());

		shopInfos = new ArrayList<ShopInfo>();
		shopInfo = new ShopInfo();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		page = 1;
		// 调用接口-获取我的店铺列表
		JSONObject data = (JSONObject) KuaidiApi.getBusinessShopList(
				"", "1");
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
		showProgressDialog( "玩儿命为您加载中...请稍候");

	}

	/**
	 * 下拉刷新事件，上拉加载事件
	 */
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
									.getBusinessShopList("",
											"1");
							httpInterfaceRequest(data, false,
									INTERFACE_VERSION_NEW);
							page = 1;
							showProgressDialog(
									"拼命命为您刷新中...请稍候");
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
							UtilToolkit.showToast( "网络连接错误...");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							page = page + 1;
							if (total_pages.equals(cur_page + "")) {
								pull.onFooterRefreshComplete();
								pull.onHeaderRefreshComplete();
								UtilToolkit.showToast("已为您翻越到最后一页咯");
							} else {
								JSONObject data = (JSONObject) KuaidiApi
										.getBusinessShopList(
												"", page + "");
								httpInterfaceRequest(data, false,
										INTERFACE_VERSION_NEW);
								showProgressDialog(
										"玩儿命为您加载中...请稍候");
							}

						}
					}
				}, 1000);
			}
		});
	}

	/**
	 * 设置事件
	 */
	private void setEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent = new Intent(context, AddShopActivity.class);
				intent.putExtra("from", "myShopItem");
				intent.putExtra("position", position);
				SKuaidiApplication.getInstance().postMsg("AddShopActivity",
						"shopInfos", shopInfos);
				startActivityForResult(intent, Constants.REQUEST_CODE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE) {// 点击列表进入详情
			JSONObject data1 = null;
			if (resultCode == Constants.RESULT_CODE) {// 点击列表进入详情更新后返回
				page = 1;
				// 调用接口-获取我的店铺列表
				data1 = (JSONObject) KuaidiApi.getBusinessShopList(
						"", "1");
				showProgressDialog( "玩儿命帮您更新列表信息");
			} else if (resultCode == Constants.RESULT_CODE_2) {
				page = 1;
				// 调用接口-获取我的店铺列表
				data1 = (JSONObject) KuaidiApi.getBusinessShopList(
						"", "1");
				showProgressDialog( "玩儿命帮您更新列表信息");
			}
			httpInterfaceRequest(data1, false, INTERFACE_VERSION_NEW);
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
				page = 1;
				// 调用接口-获取我的店铺列表
				JSONObject data = (JSONObject) KuaidiApi.getBusinessShopList(
						"", "1");
				httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
				showProgressDialog( "玩儿命为您加载中...请稍候");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
		if (getUserVisibleHint()) {// 如果是当前页面
			if (Utility.isNetworkConnected() == false) {
			} else {
				page = 1;
				// 调用接口-获取我的店铺列表
				JSONObject data = (JSONObject) KuaidiApi.getBusinessShopList(
						"", "1");
				httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	class MyOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_add_new_shop:
				UMShareManager.onEvent(context, "myshop_add_new_shop", "myshop", "任务详情：抢任务");
				intent = new Intent(context, AddShopActivity.class);
				intent.putExtra("from", "myShopFragment");
				startActivityForResult(intent, Constants.REQUEST_CODE);
				break;

			default:
				break;
			}
		}

	}

	@Override
	protected void onRequestSucess(String sname, JSONObject result) {
		JsonXmlParser.parseShopInfo(context, handler, result);
	}

	@Override
	protected void onRequestFail(String sname, String message, String data_fail) {
		UtilToolkit.showToast(message);
		Message msg = new Message();
		msg.what = Constants.GET_SHOP_INFO_FAIL;// 获取店铺列表失败
		handler.sendMessage(msg);
	}

	@Override
	protected void onRequestOldInterFaceFail(String sname, String msg,
			JSONObject result) {

	}

}
