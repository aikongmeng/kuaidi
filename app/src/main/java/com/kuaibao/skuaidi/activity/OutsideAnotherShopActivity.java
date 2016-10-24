package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.GetAnotherShopListAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.entry.ShopInfo;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 其它店铺list
 * 
 * @author gudd
 * 
 */
public class OutsideAnotherShopActivity extends SkuaiDiBaseActivity {

	private Context context;
	private JSONObject data;
	private SkuaidiDialog dialog;
	private Intent intent;
	/** 适配器 **/
	private GetAnotherShopListAdapter adapter;
	// 变量
	private Object[] objects;
	private List<ShopInfo> shopInfos;
	private List<ShopInfo> shopInfos2;
	/** 店铺列表 **/
	private ShopInfo shopInfo;
	/** 总条数 **/
	private String total_records;
	/** 总页数 **/
	private String total_pages = "";
	/** 第几页 **/
	private String page_num;
	/** 加载数据的页数 **/
	private int page = 1;
	/** 当前页 **/
	private int cur_page = 1;

	private ImageView iv_title_back;// 返回按钮
	private TextView tv_title_des;// 标题

	private PullToRefreshView pull;
	private ListView lv_another_shop;// 列表

	/** save data variable **/
	private String latitude;// 纬度
	private String longitude;// 经度

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GET_SHOP_INFO_SUCCESS:// 获取其他店铺列表成功
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				dismissProgressDialog();
				if (page == 1) {
					shopInfos.clear();
					objects = (Object[]) msg.obj;
					shopInfos = (List<ShopInfo>) objects[0];
					total_records = (String) objects[1];
					total_pages = (String) objects[2];
					page_num = (String) objects[3];

					adapter = new GetAnotherShopListAdapter(context, shopInfos);
					lv_another_shop.setAdapter(adapter);
				} else {
					cur_page = page;// 得到当前页
					page = cur_page;// 将当前页设置到page中
					objects = (Object[]) msg.obj;
					shopInfos2 = (List<ShopInfo>) objects[0];
					total_records = (String) objects[1];
					total_pages = (String) objects[2];
					page_num = (String) objects[3];
					shopInfos.addAll(shopInfos2);
					adapter.notifyDataSetChanged();
				}

				break;
			case Constants.GET_SHOP_INFO_FAIL:// 获取其他店铺列表失败
				dismissProgressDialog();
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				break;
			case Constants.TIME_OUT_FAIL:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.another_shop_activity);
		context = this;
		initView();
		initData();
		pullToRefresh();

	}

	/** 初始化界面 **/
	private void initView() {
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("其他店铺");
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		lv_another_shop = (ListView) findViewById(R.id.lv_another_shop);
		iv_title_back.setOnClickListener(new onClickListener());
	}

	/** 初始化数据 **/
	private void initData() {
		shopInfos = new ArrayList<ShopInfo>();
		latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();// 纬度
		longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();// 经度
		if (Utility.isNetworkConnected() == false) {
			setNetWork();
		} else {
			// 从服务器加载数据
			page = 1;
			data = (JSONObject) KuaidiApi.getAnotherShopList(
					latitude, longitude, "1", "");
			httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
			showProgressDialog( "玩儿命帮您加载中...");
		}

		lv_another_shop.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent = new Intent(context, AddShopActivity.class);
				intent.putExtra("from", "anotherShopItem");
				intent.putExtra("position", position);
				SKuaidiApplication.getInstance().postMsg("AnotherShopActivity",
						"shopInfos", shopInfos);
				startActivityForResult(intent, Constants.REQUEST_CODE);
			}
		});

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
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
							setNetWork();
						} else {
							page = 1;
							data = (JSONObject) KuaidiApi.getAnotherShopList(
									latitude, longitude, "1",
									"");
							httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
							showProgressDialog( "玩儿命帮您加载中...");
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
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
							setNetWork();
						} else {
							page = page + 1;
							if(total_pages != null || !total_pages.equals("")){
								if (total_pages.equals(cur_page + "")) {
									pull.onFooterRefreshComplete();
									pull.onHeaderRefreshComplete();
									UtilToolkit.showToast("已为您翻越到最后一页咯");
								} else {
									data = (JSONObject) KuaidiApi
											.getAnotherShopList(latitude, longitude, page + "","");
									httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
								}
							}
						}
					}
				}, 1000);
			}
		});
	}

	class onClickListener implements android.view.View.OnClickListener {

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

	private void setNetWork() {
		dialog = new SkuaidiDialog(context);
		dialog.setTitle("提示");
		dialog.setContent("您没有连接网络，是否进行设置？");
		dialog.isUseEditText(false);
		dialog.setPositionButtonTitle("设置");
		dialog.setNegativeButtonTitle("取消");
		dialog.setPosionClickListener(new PositonButtonOnclickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS);
				startActivity(intent);
			}
		});
		dialog.showDialog();
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			JsonXmlParser.parseAnotherShopInfo(context, handler, result);
		}

	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		if(!result.equals("")){
			UtilToolkit.showToast(result);
		}
		Message msg = new Message();
		msg.what = Constants.TIME_OUT_FAIL;
		handler.sendMessage(msg);
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
