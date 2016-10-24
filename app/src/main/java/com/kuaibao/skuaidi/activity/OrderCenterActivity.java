package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ListOrderAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.IsGuid;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.kuaibao.skuaidi.R.id.tv_net_reload;

/**
 * @author 四大界面之 —— 订单
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class OrderCenterActivity extends SkuaiDiBaseActivity implements OnClickListener{

	private static final String ORDER_LIST = "order/getOrderList";
	private Context context;
	private ListView lv_order;
	// private TextView tv_select_num;
	private static final int FRESH_UI = 900;
	protected String user_phone = "";
	protected TextView tv_selete, tv_order_tag, tv_order_num, tv_create_order;
	protected ImageView iv_clickBack;
	protected TextView tv_title, tv_order_check, tv_history_order_check;
	protected LinearLayout ll_title;
	protected boolean isToday = false;

	private PopupWindow popupwindow;
	private RecordScreeningPop recordScreeningPop;
	private RelativeLayout rl_common_title,rl_create_order;
	private View view_blue_divider;
	private TextView tv_today_order, tv_history_order, tv_order_state;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();

	private int selectCount = 0;
	@SuppressWarnings("unused")
	private String deleteIds;

	private int presentPage = 1, pageSize = 15, total_page = 0;

	protected ProgressDialog pdWaitingMessage;

	private List<Order> orders;
	private ListOrderAdapter adapter;
	private PullToRefreshView pull;

	private static final String ORDER_STATE_ALL = "0";
	private static final String ORDER_STATE_UNFINISH = "1";
	private static final String ORDER_STATE_FINISH = "2";
	private static final String ORDER_STATE_PRINTED = "3";
	private static final String ORDER_STATE_REJECTED = "4";
	private static final String ORDER_STATE_BACK = "back";
	private static final int REQUEST_TODAY = 1;
	private static final int REQUEST_HISTORY = 0;
	private String statue = ORDER_STATE_ALL;
	private boolean isThroughAudit = false;

	public static int fromWhere = 1;
	private String pullType = "";
	protected MyCustom cus;
	private boolean isFoot = false;
	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case Constants.NETWORK_FAILED:
					pdWaitingMessage.dismiss();
					// Utility.showToast(context, "网络连接错误,请稍后重试!");
					pull.setVisibility(View.GONE);
					lv_order.setVisibility(View.GONE);

					break;
				case Constants.DELETE_TO_FINISH:
					selectCount = msg.arg1;
					// tv_select_num.setText("已选择" + selectCount + "个");
					break;
				case Constants.SUCCESS:
					adapter.hide_checkbox();
					getData();
					adapter.notifyDataSetChanged(orders);
					break;

				case Constants.PHONE_CALL:
					// rl_delete_title.setVisibility(View.GONE);
					view_blue_divider.setVisibility(View.GONE);
					rl_common_title.setVisibility(View.VISIBLE);
					adapter.hide_checkbox();
					adapter.notifyDataSetChanged(orders);
					AcitivityTransUtil.showChooseTeleTypeDialog(OrderCenterActivity.this, "", msg.obj.toString(),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
					break;

				case FRESH_UI:
					adapter.notifyDataSetChanged(orders);
					break;

				default:
					break;
			}
		}
	};
	private UserInfo mUserInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.message_order);
		context = this;
		mUserInfo=SkuaidiSpf.getLoginUser();
		if ("sto".equals(mUserInfo.getExpressNo())) {
			isThroughAudit = (E3SysManager.getReviewInfoNew()!=null);
		}
		getControl();
		// getData();
		setListener();
	}

	private void getControl() {

		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		lv_order = (ListView) findViewById(R.id.lv_order);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setVisibility(View.GONE);
		ll_title = (LinearLayout) findViewById(R.id.ll_title);
		ll_title.setVisibility(View.VISIBLE);
		// tv_select_num = (TextView) findViewById(R.id.tv_select_num);

		iv_clickBack = (ImageView) findViewById(R.id.click_back);
		iv_clickBack.setVisibility(View.VISIBLE);

		tv_selete = (TextView) findViewById(R.id.tv_selete);
		// rl_delete_title = (RelativeLayout)
		// findViewById(R.id.rl_delete_title);
		rl_common_title = (RelativeLayout) findViewById(R.id.rl_common_title);
		view_blue_divider = findViewById(R.id.view_blue_divider);
		tv_today_order = (TextView) findViewById(R.id.tv_today_order);
		tv_history_order = (TextView) findViewById(R.id.tv_histroy_order);
		tv_order_state = (TextView) findViewById(R.id.tv_order_state);
		tv_order_tag = (TextView) findViewById(R.id.tv_order_tag);
		tv_order_num = (TextView) findViewById(R.id.tv_order_num);
		rl_create_order = (RelativeLayout) findViewById(R.id.rl_create_order);
		tv_create_order = (TextView) findViewById(R.id.tv_create_order);

		if (fromWhere == 1) {
//			order_top_center.setVisibility(View.VISIBLE);
//			int b = SkuaidiSkinManager.getTextColor("main_color");
//			tv_order_check.setTextColor(b);
//			line_order_check.setBackgroundColor(b);
//			int c = getResources().getColor(R.color.gray_4);
//			int text = getResources().getColor(R.color.gray_3);
//			line_history_order_check.setBackgroundColor(c);
//			tv_history_order_check.setTextColor(text);
			tv_today_order.setEnabled(false);
			tv_today_order.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
			tv_history_order.setEnabled(true);
			tv_history_order.setTextColor(getResources().getColor(R.color.white));

			isToday = true;
		}
//		else {
//			order_top_center.setVisibility(View.GONE);
//		}
		orders = new ArrayList<Order>();
		adapter = new ListOrderAdapter(context, handler, orders);
		lv_order.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

//		addGuid();
	}

	// 判断activity是否引导过
	private void addGuid() {

		order_list_meng = (RelativeLayout) findViewById(R.id.order_list_meng);
		if (IsGuid.activityIsGuided(context, this.getClass().getName())) {
			order_list_meng.setVisibility(View.GONE);
			return;
		} else {
			order_list_meng.setVisibility(View.VISIBLE);
		}

		order_list_meng.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				order_list_meng.setVisibility(View.GONE);
				IsGuid.setIsGuided(getApplicationContext(), OrderCenterActivity.this.getClass().getName());

			}
		});
	}

	private void getData() {
		getOrderList();
	}

	private void setListener() {
		tv_selete.setOnClickListener(this);
		iv_clickBack.setOnClickListener(this);
		tv_today_order.setOnClickListener(this);
		tv_history_order.setOnClickListener(this);
		rl_create_order.setOnClickListener(this);
		lv_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UMShareManager.onEvent(context, "order_center_itemDetail", "order_center", "订单：订单详情");
				// rl_delete_title.setVisibility(View.GONE);
				view_blue_divider.setVisibility(View.GONE);
				rl_common_title.setVisibility(View.VISIBLE);
				adapter.hide_checkbox();
				adapter.notifyDataSetChanged(orders);
				Intent intent2 = new Intent(context, OrderInfoActivity.class);
				Order mOrder = orders.get(position);
				intent2.putExtra("orderno", mOrder.getId());
				intent2.putExtra("FlagIsRun", false);
				intent2.putExtra("Type", mOrder.getType());
				startActivityForResult(intent2, 100);
				if (mOrder.getIsread() > 0) {
					clearRedCircle(mOrder.getId());
				}
			}
		});


		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				UMShareManager.onEvent(context, "order_center_onHeaderRefresh", "order_center", "订单：下拉刷新");
				pullType = "onHeaderRefresh";
				isFoot = false;
				pull.postDelayed(new Runnable() {

					@Override
					public void run() {
						presentPage = 1;
						if (Utility.isNetworkConnected() == true) {
							getOrderList();
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							UtilToolkit.showToast("网络未连接");
						}
					}
				}, 1000);
			}
		});
		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				UMShareManager.onEvent(context, "order_center_onFooterRefresh", "order_center", "订单：上拉刷新");
				pullType = "onFooterRefresh";
				isFoot = true;
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (total_page == 1) {
							UtilToolkit.showToast( "已加载全部数据");
							pull.onFooterRefreshComplete();
							pull.onHeaderRefreshComplete();
						} else {
							// pageSize += 15;
							presentPage += 1;
							if (Utility.isNetworkConnected() == true) {
								getOrderList();
								pull.onFooterRefreshComplete();
								pull.onHeaderRefreshComplete();
							} else {
								UtilToolkit.showToast("网络未连接");
							}
						}

					}
				}, 1000);

			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private void clearRedCircle(String order_number) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "order_sync_im_unread");
			data.put("order_numbers", order_number);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_OLD);
	}

	protected void getOrderList() {

		tv_order_state.setVisibility(View.GONE);
		JSONObject object = new JSONObject();
		try {
			object.put("sname", ORDER_LIST);
			object.put("pageSize", pageSize);
			object.put("pageNum", presentPage);
			object.put("status", statue);
			object.put("isToday", isToday ? REQUEST_TODAY : REQUEST_HISTORY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (Utility.isNetworkConnected()) {
			showProgressDialog( "数据加载中...");
		}else{
			tv_order_state.setVisibility(View.VISIBLE);
			tv_order_state.setText("网络好像不太顺畅哦~");
		}
		httpInterfaceRequest(object, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
		adapter.notifyDataSetChanged(orders);
		// if(!isThroughAudit&&SkuaidiUserManager.getInstanse().getExpressNo().equals("sto")){
		// showThroughAuditDialog();
		// }
		MobclickAgent.onResume(context);
	}

	@Override
	protected void onDestroy() {
		if ((recordScreeningPop != null && recordScreeningPop.isShowing())) {
			recordScreeningPop.dismissPop();
		}
		SkuaidiSpf.saveRecordChooseItem(context, 0);// 将筛选条目置下标置0
		super.onDestroy();
	}

	private RelativeLayout order_list_meng;

	// 第二次后退键退出程序
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (ORDER_LIST.equals(sname)) {
			tv_order_num.setText(result.optString("total_count"));
			JSONArray orderArray = result.optJSONArray("list");
			List<Order> tempOrder  = new ArrayList<Order>();
			if (orderArray != null) {
				for (int i = 0; i < orderArray.length(); i++) {
					JSONObject object = orderArray.optJSONObject(i);
					Order order = new Order();
					order.setId(object.optString("orderNumber"));
					order.setTime(object.optString("updateTime"));
					order.setSenderPhone(object.optString("senderPhone"));
					order.setSenderName(object.optString("senderName"));
					order.setSenderProvince(object.optString("senderProvince"));
					order.setSenderCity(object.optString("senderCity"));
					order.setSenderCountry(object.optString("senderArea"));
					order.setSenderDetailAddress(object.optString("senderAddress"));
					order.setOrder_type(object.optString("status"));
					order.setIsPrint(object.optString("isPrint"));
					order.setIsread(object.optInt("isRead"));
					order.setInform_sender_when_sign(object.optString("informStatus"));
					tempOrder.add(order);
				}
			}
			if (isFoot) {
				orders.addAll(tempOrder);
			} else {
				orders.clear();
				orders.addAll(tempOrder);
			}
			Message message = new Message();
			message.what = FRESH_UI;
			handler.sendMessage(message);
		} else if (sname.equals("order_sync_im_unread")) {

		}
		dismissProgressDialog();
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		if("0".equals(code) && !isFoot) {
			tv_order_state.setVisibility(View.VISIBLE);
			orders.clear();
			adapter.notifyDataSetChanged(orders);
			if(isToday) {
				tv_order_state.setText("暂无今日订单");
			}else{
				tv_order_state.setText("暂无历史订单");
			}
		}else{
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		dismissProgressDialog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == 100) {
			isFoot = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case tv_net_reload:
				getData();
				break;
			case R.id.tv_selete:
				if ((recordScreeningPop != null && recordScreeningPop.isShowing())) {
					recordScreeningPop.dismissPop();
					return;
				}
				final List<String> items = new ArrayList<String>();
				items.add("全部");
				items.add("未完成");
				items.add("已完成");
				items.add("已打印");
				if("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
					items.add("已打回");
				}
				recordScreeningPop = new RecordScreeningPop(context, v , items);
				recordScreeningPop.setLayoutDismissListener(new RecordScreeningPop.LayoutDismissListener() {

					@Override
					public void onDismiss() {
						recordScreeningPop.dismissPop();
						recordScreeningPop = null;
					}
				});
				recordScreeningPop.setItemOnclickListener(new RecordScreeningPop.ItemOnClickListener() {
					@Override
					public void itemOnClick(int position) {
						switch (items.get(position)){
							case "全部":
								UMShareManager.onEvent(context, "order_center_all", "order_center", "订单：全部");
								statue = ORDER_STATE_ALL;
								isFoot = false;
								getData();
								break;
							case "未完成":
								UMShareManager.onEvent(context, "order_center_unfinished", "order_center", "订单：未完成");
								statue = ORDER_STATE_UNFINISH;
								isFoot = false;
								getOrderList();
								break;
							case "已完成":
								UMShareManager.onEvent(context, "order_center_finished", "order_center", "订单：已完成");
								statue = ORDER_STATE_FINISH;
								isFoot = false;
								getOrderList();
								break;
							case "已打印":
								statue = ORDER_STATE_PRINTED;
								isFoot = false;
								getOrderList();
								break;
							case "已打回":
								statue = ORDER_STATE_REJECTED;
								isFoot = false;
								getOrderList();
								break;
							default:
								break;
						}
						recordScreeningPop.dismissPop();
						recordScreeningPop = null;
					}
				});
				recordScreeningPop.showPop();
				break;
			case R.id.click_back:
				finish();
				break;
			case R.id.tv_today_order:
				UMShareManager.onEvent(context, "order_center_today", "order_center", "订单：今日订单");
//				int b = SkuaidiSkinManager.getTextColor("main_color");
//				tv_order_check.setTextColor(b);
//				line_order_check.setBackgroundColor(b);
//				int c = getResources().getColor(R.color.gray_4);
//				int t = getResources().getColor(R.color.gray_3);
//				line_history_order_check.setBackgroundColor(c);
//				tv_history_order_check.setTextColor(t);
				if (fromWhere == 2) {
					user_phone = cus.getPhone();
				}
				tv_order_tag.setText("今日订单：");
				tv_order_num.setText("0");
				tv_today_order.setEnabled(false);
				tv_today_order.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
				tv_history_order.setEnabled(true);
				tv_history_order.setTextColor(getResources().getColor(R.color.white));
				presentPage = 1;
				pageSize = 15;
				isToday = true;
				isFoot = false;
				statue = ORDER_STATE_ALL;
				orders.clear();
				SkuaidiSpf.saveRecordChooseItem(context, 0);
				adapter.notifyDataSetChanged();
				getOrderList();
				break;
			case R.id.tv_histroy_order:
				UMShareManager.onEvent(context, "order_center_history", "order_center", "订单：历史订单");
//				int b = getResources().getColor(R.color.gray_3);
//				int p = getResources().getColor(R.color.gray_4);
//				tv_order_check.setTextColor(b);
//				line_order_check.setBackgroundColor(b);
//				int c = SkuaidiSkinManager.getTextColor("main_color");
//				line_history_order_check.setBackgroundColor(c);
//				tv_history_order_check.setTextColor(c);
				if (fromWhere == 2) {
					user_phone = cus.getPhone();
				}
				tv_order_tag.setText("历史订单（一周内）：");
				tv_order_num.setText("0");
				tv_history_order.setEnabled(false);
				tv_history_order.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
				tv_today_order.setEnabled(true);
				tv_today_order.setTextColor(getResources().getColor(R.color.white));
				presentPage = 1;
				pageSize = 15;
				isToday = false;
				isFoot = false;
				statue = ORDER_STATE_ALL;
				orders.clear();
				SkuaidiSpf.saveRecordChooseItem(context, 0);
				adapter.notifyDataSetChanged();
				getOrderList();
				break;
			case R.id.rl_create_order:
				Intent intnt = new Intent(this, CreateNewOrderActivity.class);
				startActivity(intnt);
				break;
		}
	}
}
