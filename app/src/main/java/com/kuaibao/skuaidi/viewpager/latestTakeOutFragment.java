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
import com.kuaibao.skuaidi.activity.OutSideDetailActivity;
import com.kuaibao.skuaidi.activity.adapter.LatestTakeOutAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.fragment.SkuaidiBaseFragment;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最新外单模块
 * 
 * @created 2015/1/5
 * @author 顾冬冬
 * @version V3.5.0
 */
public class latestTakeOutFragment extends SkuaidiBaseFragment {

	private int page = 1;

	private View rootView = null;
	private RelativeLayout rl_least_outside;//列表模块
	private ListView mListview;
	private Context context;
	private String latitude;// 经度
	private String longitude;// 纬度
	
	// 邀请朋友相关
	private RelativeLayout rl_invite_friends; // 邀请朋友页面
	private TextView tv_invite_friends_btn;// 邀请朋友按钮 

	private List<LatestOutSide> latestOutSides;
	private LatestTakeOutAdapter mAdapter;
	private PullToRefreshView pull;
	private Intent mIntent;

	public int LATEST_REQUEST_CODE = 1;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.OUTSIDE_GET_SUCCESS:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				rl_least_outside.setVisibility(View.VISIBLE);
				rl_invite_friends.setVisibility(View.GONE);
				if (page == 1) {
					// if ((Boolean) SKuaidiApplication.getInstance()
					// .onReceiveMsg("pagerFragment", "isShowProgress")) {
					dismissProgressDialog();
					// SKuaidiApplication.getInstance().postMsg(
					// "pagerFragment", "isShowProgress", false);
					// }
					latestOutSides.clear();
					latestOutSides = (List<LatestOutSide>) msg.obj;
					mAdapter = new LatestTakeOutAdapter(context, latestOutSides);
					mListview.setAdapter(mAdapter);
				} else {
					latestOutSides.addAll((List<LatestOutSide>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				break;
			case Constants.OUTSIDE_GET_DATAISNULL:// 未获得到数据
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				rl_least_outside.setVisibility(View.GONE);
				rl_invite_friends.setVisibility(View.VISIBLE);
				latestOutSides.clear();
				if (page == 1) {
					dismissProgressDialog();
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
		rootView = inflater.inflate(R.layout.best_take_out, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
		longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
		pullToRefresh();
		ListenerEvent();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LATEST_REQUEST_CODE) {
			if (resultCode == 2) {
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication.getInstance().onReceiveMsg("OutSideDetailActivity","latestTakeOutFragment");
				String id = data.getStringExtra("outsideoutsideid");
				for (int i = 0; i < latestOutSides.size(); i++) {
					String outside_blockid = latestOutSides.get(i).getId();
					String status = latestOutSides.get(i).getState();
					if (outside_blockid.equals(id) && status.equals("wait")) {// 如果存在这个ID并且该条中状态为等待则删除此条
						latestOutSides.remove(i);
						break;
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void ListenerEvent() {
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mIntent = new Intent(context, OutSideDetailActivity.class);
				mIntent.putExtra("position", position);
				mIntent.putExtra("fromActivity", "latestTakeOutFragment");
				SKuaidiApplication.getInstance().postMsg("LastTakeOutFragment","OutSideDetailActivity", latestOutSides);
				startActivityForResult(mIntent, LATEST_REQUEST_CODE);
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
							UtilToolkit.showToast("网络连接错误...");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
							longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
							JSONObject data = (JSONObject)KuaidiApi.getOutSide(latitude,
									longitude, "get", "", "", "", "");
							httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
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
							UtilToolkit.showToast( "网络连接错误...");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
							longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
							page = page + 1;
							JSONObject data = (JSONObject)KuaidiApi.getOutSide(latitude,
									longitude, "get", page + "", "", "", "");
							httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
						}
					}
				}, 1000);

			}
		});

	}

	private void initView() {
		rl_least_outside = (RelativeLayout) rootView.findViewById(R.id.rl_least_outside);
		mListview = (ListView) rootView.findViewById(R.id.lv_best);
		
		rl_invite_friends = (RelativeLayout) rootView.findViewById(R.id.rl_invite_friends);
		tv_invite_friends_btn = (TextView) rootView.findViewById(R.id.tv_invite_friends_btn);
		
		pull = (PullToRefreshView) rootView
				.findViewById(R.id.pull_refresh_view);
		
		tv_invite_friends_btn.setOnClickListener(new MyOnclickListener());//设置邀请朋友点击事件
		latestOutSides = new ArrayList<LatestOutSide>();
	}

	Boolean isShowProgress;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (Utility.isNetworkConnected() == false) {
				UtilToolkit.showToast("网络连接不可用，请设置网络");
			} else {
				latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
				longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
				page = 1;
				JSONObject data = (JSONObject)KuaidiApi.getOutSide(latitude, longitude,
						"get", "", "", "", "");
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
				// showDialog();
			} else {
				latitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude();
				longitude = SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude();
				page = 1;
				JSONObject data = (JSONObject)KuaidiApi.getOutSide(latitude, longitude,
						"get", "", "", "", "");
				httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
			}

		}
	}
	
	/**分享相关**/
	private String title;
	private String shareText;
	private String targetUrl;
	private Map<String, String> shareTexts;
	
	class MyOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_invite_friends_btn:
				UMShareManager.onEvent(context, "leastTakeOut_inviteFriends", "leastTakeOut", "最新任务：邀请客户");
				title = "快递小哥送外卖";
				shareText = "亲，我是一名勤快又善良的快递小哥！如需跑腿买东西，请召唤我，火箭速度，金牌服务！随时恭候您哟！";
				targetUrl = "http://ckd.so/1";
				shareTexts = new HashMap<String, String>();
				shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText+targetUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText+targetUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText+targetUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText+targetUrl);
				UMShareManager.openShare(getActivity(),title, shareTexts, targetUrl,R.drawable.share_software);
				break;

			default:
				break;
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
