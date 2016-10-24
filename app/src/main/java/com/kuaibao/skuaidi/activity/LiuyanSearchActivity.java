package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.LiuyanListAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.ItemOnClickListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.LayoutDismissListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiButton;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.SkuaidiPopAboutCheckList;
import com.kuaibao.skuaidi.dialog.SkuaidiPopAboutCheckList.ItemOnclickListener;
import com.kuaibao.skuaidi.entry.MessageList;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 留言列表
 * 
 * @author wq
 */
@SuppressLint("HandlerLeak")
public class LiuyanSearchActivity extends SkuaiDiBaseActivity implements OnDateSetListener, OnClickListener {
	private static final String EDITTEXT_DIGITS = "0123456789-,\nabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String URL_CREATE_MESSAGE = "http://dev.liuyan.kuaidihelp.com/m/msg/add";
	private static final String SNAME_LIUYAN_LIST = "liuyan.topic_query";
	private Context context;
	protected TextView tv_orderinfo_title;
	protected SkuaidiImageView tv_filter;
	private View view_blue_divider;
	private TextView tv_start_time, tv_end_time;

	protected TextView reload;
	protected View not_net;

	protected TextView tv_title;
	protected View liuyan_top_center;

	protected String startTime = "";
	protected String endTime = "";
	protected String user_phone = "";// 用于筛选的手机号
	protected int select_type = 1;// 搜索未读标记 0：已读 1：未读

	//protected ProgressDialog pdWaitingMessage;

	private ListView listview;
	protected PullToRefreshView pull;
	private LiuyanListAdapter adapter;

	private List<MessageList> messages;

	protected int presentPage = 1, pageSize = 15, totalPages = 0, time = 1;

	protected int state;
	protected String from = "";// 界面可重用，空字符串标识从消息模块进入，

	private int pageNum;
	private String company;
	private Button btn_cancel;
	private boolean isAddmore = false;
	private SkuaidiPopAboutCheckList popAboutCheckList;
	private RecordScreeningPop recordScreeningPop = null;
	protected static final int REQUEST_CODE = 100;
	private int index_clickedItem = 0;
	private String readStatus = "all";
	private ViewGroup llSmsRecord = null;// 列表区域
	private LinearLayout ll_title_sto;
	private LinearLayout ll_title_other;

	private View line1 = null;
	private ListView lvSmsRecord = null;// 列表
	private EditText etInputNo = null;// 输入框
	private TextView tvSearch = null;// 取消按钮
	private ViewGroup back = null;// 返回
	private ViewGroup select = null;// 选择筛选条件按钮
	private TextView selectConditions = null;// 显示筛选条件方案控件
	private SelectConditionsListPOP selectPop = null;
	private String queryNumber = "";
	// 搜索类型 1:手机号，2：运单号
	private int search_type = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.list_liuyan_search);
		context = this;
		company = SkuaidiSpf.getLoginUser().getExpressNo();
		getControl();
		setListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	protected void setListener() {

		tv_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((recordScreeningPop != null && recordScreeningPop.isShowing())) {
					recordScreeningPop.dismissPop();
				}
				if ((popAboutCheckList == null || !popAboutCheckList.isShowing())) {
					final List<String> titleList = new ArrayList<String>();
					titleList.add("筛选");
					titleList.add("发起内部留言");
					popAboutCheckList = new SkuaidiPopAboutCheckList(context, v, titleList);
					popAboutCheckList.setItemOnclickListener(new ItemOnclickListener() {

						@Override
						public void onClick(int position) {
							if (position == titleList.indexOf("筛选")) {
								showFilterWindow(ll_title_sto);
							} else if (position == titleList.indexOf("发起内部留言")) {
								loadWeb(URL_CREATE_MESSAGE + "?cm_id=" + SkuaidiSpf.getLoginUser().getUserId(),
										"发起内部留言");
							}
						}

					});
					popAboutCheckList.showPop();
				} else if (popAboutCheckList != null && popAboutCheckList.isShowing()) {
					popAboutCheckList.dismiss();
				}

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UMShareManager.onEvent(LiuyanSearchActivity.this, "message_center_messageDitail", "message_center",
						"留言详情");

				Intent intent = new Intent(LiuyanSearchActivity.this, LiuyanDetailActivity.class);
				MessageList messageList = adapter.getItem(position);
				intent.putExtra("m_id", messageList.getM_id());
				intent.putExtra("m_type", messageList.getM_type());
				intent.putExtra("post_timestramp", messageList.getPost_timestramp());
				intent.putExtra("waybill_no", messageList.getWaybill_no());
				if (!"record".equals(messageList.getTopic_cate())) {
					intent.putExtra("mix_content", messageList.getMix_content());
				}
				intent.putExtra("user_phone", messageList.getUser_phone());
				intent.putExtra("post_username", messageList.getPost_username());
				intent.putExtra("messageList", messageList);
				index_clickedItem = position;
				startActivityForResult(intent, REQUEST_CODE);

			}
		});

		// 下拉
		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				pull.postDelayed(new Runnable() {
					@Override
					public void run() {
						pageNum = 1;
						String number = etInputNo.getText().toString().trim();
						isAddmore = false;
						requestDatas(SNAME_LIUYAN_LIST, 1, 20, number, false);
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
						pageNum++;
						isAddmore = true;
						String number = etInputNo.getText().toString().trim();
						requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, false);
					}
				}, 1000);
			}
		});
	}

	/**
	 * 显示筛选菜单
	 */
	private void showFilterWindow(View view) {
		if (recordScreeningPop == null) {
			// tv_filter.setText("收起");
			final List<String> itemArr = new ArrayList<String>();
			itemArr.add("全部");
			itemArr.add("未读");
			itemArr.add("已读");
			recordScreeningPop = new RecordScreeningPop(context, view, itemArr);
			recordScreeningPop.setLayoutDismissListener(new LayoutDismissListener() {

				@Override
				public void onDismiss() {
					recordScreeningPop.dismissPop();
					recordScreeningPop = null;
				}
			});
			recordScreeningPop.setItemOnclickListener(new ItemOnClickListener() {

				@Override
				public void itemOnClick(int position) {
					String number = etInputNo.getText().toString().trim();
					switch (position) {
					case 0:// 全部
						readStatus = "all";
						requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, true);
						UMShareManager.onEvent(context, "liuyan_list_all", "liuyan_list", "留言列表:全部");
						break;
					case 1:// 未读
						readStatus = "unread";
						requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, true);
						UMShareManager.onEvent(context, "liuyan_list_unread", "liuyan_list", "留言列表:未读");

						break;
					case 2:// 已读
						readStatus = "readed";
						requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, true);
						UMShareManager.onEvent(context, "liuyan_list_read", "liuyan_list", "留言列表:已读");
						break;
					default:
						break;
					}

					// tv_more.setText("筛选");
					recordScreeningPop.dismissPop();
					recordScreeningPop = null;

				}
			});
			recordScreeningPop.showPop();
		} else {
			// tv_more.setText("筛选");
			recordScreeningPop.showPop();
		}
	}

	private void getControl() {
		line1 = findViewById(R.id.line1);
		line1.setVisibility(View.GONE);
		lvSmsRecord = (ListView) findViewById(R.id.lvSmsRecord);
		etInputNo = (EditText) findViewById(R.id.etInputNo);

		etInputNo.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				/* 判断是否是“GO”键 */
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					/* 隐藏软键盘 */
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					queryNumber = etInputNo.getText().toString();
					if (TextUtils.isEmpty(queryNumber)) {
						return false;
					}
					pageNum = 1;
					requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, queryNumber, true);
					return true;
				}
				return false;
			}
		});

		pageNum = 1;
//		requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, queryNumber, false);

		tvSearch = (TextView) findViewById(R.id.tvSearch);
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		llSmsRecord = (ViewGroup) findViewById(R.id.llSmsRecord);
		back = (ViewGroup) findViewById(R.id.back);
		select = (ViewGroup) findViewById(R.id.select);
		selectConditions = (TextView) findViewById(R.id.selectConditions);

		select.setOnClickListener(this);
		tvSearch.setOnClickListener(this);

		llSmsRecord = (ViewGroup) findViewById(R.id.llSmsRecord);
		ll_title_sto = (LinearLayout) findViewById(R.id.title_sto);
		ll_title_other = (LinearLayout) findViewById(R.id.title_other);

		tv_orderinfo_title = (TextView) findViewById(R.id.tv_title_des);
		view_blue_divider = findViewById(R.id.view_blue_divider);
		tv_filter = (SkuaidiImageView) findViewById(R.id.tv_more);
		tv_filter.setVisibility(View.VISIBLE);
		not_net = findViewById(R.id.exception_nonet);
		reload = (TextView) findViewById(R.id.tv_net_reload);
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		listview = (ListView) findViewById(R.id.lv_exception_list);

		messages = new ArrayList<MessageList>();
		adapter = new LiuyanListAdapter(context, messages);

		tv_orderinfo_title.setText("留言");
		etInputNo.setHint("输入运单号搜索");

		// 非申通账号没有发起内部留言
		if (!"sto".equals(company)) {
			ll_title_other.setVisibility(View.VISIBLE);
			ll_title_sto.setVisibility(View.GONE);

			TextView tv_title_des = (TextView) ll_title_other.findViewById(R.id.tv_title_des);
			tv_title_des.setText("留言");
			SkuaidiButton bt_title_more = (SkuaidiButton) findViewById(R.id.bt_title_more);
			bt_title_more.setText("筛选");
			bt_title_more.setVisibility(View.VISIBLE);
			bt_title_more.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showFilterWindow(ll_title_other);
				}
			});
		} else {
			TextView tv_title_des = (TextView) ll_title_sto.findViewById(R.id.tv_title_des);
			tv_title_des.setText("留言");
		}

		listview.setAdapter(adapter);
		ll_title_other.setVisibility(View.GONE);
		ll_title_sto.setVisibility(View.GONE);

	}

	/**
	 * 返回
	 */
	public void back(View view) {
//		if (getIntent().getStringExtra("fromto") != null
//				&& getIntent().getStringExtra("fromto").equals("deliverdetailactivity")) {
//			// Intent intent = new Intent(context,
//			// NotifyDetailActivity.class);
//			// setResult(RESULT_OK, intent);
//			finish();
//		} else {
//			Intent intent = new Intent(context, MainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra("tabid", 0);
//			startActivity(intent);
//		}
		finish();

	}


	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
		dismissProgressDialog();//LiuyanSearchActivity.this);
		//pdWaitingMessage.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	public void delete(View view) {
		adapter.show_checkbox();
		// rl_delete_title.setVisibility(View.VISIBLE);
		view_blue_divider.setVisibility(View.VISIBLE);
		// rl_common_title.setVisibility(View.GONE);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

		if (tv_start_time.hasFocusable()) {
			if (monthOfYear < 9 && dayOfMonth < 10) {
				tv_start_time.setText(Integer.toString(year) + "-" + "0" + Integer.toString(monthOfYear + 1) + "-"
						+ "0" + Integer.toString(dayOfMonth));

			} else if (monthOfYear < 9) {
				tv_start_time.setText(Integer.toString(year) + "-" + "0" + Integer.toString(monthOfYear + 1) + "-"
						+ Integer.toString(dayOfMonth));
			} else if (dayOfMonth < 10) {
				tv_start_time.setText(Integer.toString(year) + "-" + Integer.toString(monthOfYear + 1) + "-" + "0"
						+ Integer.toString(dayOfMonth));
			} else {
				tv_start_time.setText(Integer.toString(year) + "-" + Integer.toString(monthOfYear + 1) + "-"
						+ Integer.toString(dayOfMonth));
			}
			tv_start_time.setTextColor(Color.rgb(0, 0, 0));
			tv_end_time.setTextColor(Color.rgb(0, 0, 0));

		}
		if (tv_end_time.hasFocusable()) {
			if (monthOfYear < 9 && dayOfMonth < 10) {
				tv_end_time.setText(Integer.toString(year) + "-" + "0" + Integer.toString(monthOfYear + 1) + "-" + "0"
						+ Integer.toString(dayOfMonth));
			} else if (monthOfYear < 9) {
				tv_end_time.setText(Integer.toString(year) + "-" + "0" + Integer.toString(monthOfYear + 1) + "-"
						+ Integer.toString(dayOfMonth));
			} else if (dayOfMonth < 10) {
				tv_end_time.setText(Integer.toString(year) + "-" + Integer.toString(monthOfYear + 1) + "-" + "0"
						+ Integer.toString(dayOfMonth));
			} else {
				tv_end_time.setText(Integer.toString(year) + "-" + Integer.toString(monthOfYear + 1) + "-"
						+ Integer.toString(dayOfMonth));
			}
			tv_start_time.setTextColor(Color.rgb(0, 0, 0));
			tv_end_time.setTextColor(Color.rgb(0, 0, 0));
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
		if (SNAME_LIUYAN_LIST.equals(sname)) {
			if (result != null) {
				JSONArray array = result.optJSONArray("list");
				ArrayList<MessageList> messages1 = parseMsgFromJson(array.toString());
				if (isAddmore) {
					messages.addAll(messages1);
					if (messages.size() != 0) {
						llSmsRecord.setVisibility(View.VISIBLE);
						adapter.notifyDataSetChanged(messages);
					} else {
						llSmsRecord.setVisibility(View.GONE);
					}
				} else {
					messages = messages1;
					if (messages.size() != 0) {
						llSmsRecord.setVisibility(View.VISIBLE);

					} else {
						llSmsRecord.setVisibility(View.GONE);
					}

					adapter.notifyDataSetChanged(messages1);
				}

			} else {
				llSmsRecord.setVisibility(View.VISIBLE);
			}
			pull.onFooterRefreshComplete();
			pull.onHeaderRefreshComplete();
		}
		dismissProgressDialog();
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (SNAME_LIUYAN_LIST.equals(sname)) {
			UtilToolkit.showToast( result);
		}
		llSmsRecord.setVisibility(View.VISIBLE);
		pull.onFooterRefreshComplete();
		pull.onHeaderRefreshComplete();
		dismissProgressDialog();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	private void requestDatas(String sname, int pageNum, int pageSize, String number, boolean showProgress) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", sname);
			data.put("page_num", pageNum);
			data.put("page_size", pageSize);
			data.put("status", readStatus);
			data.put("search_keyword", number);
			data.put("search_type", search_type);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		if (showProgress)
			showProgressDialog( "数据加载中...");

	}

	private ArrayList<MessageList> parseMsgFromJson(String jsonData) {
		Gson gson = new Gson();
		ArrayList<MessageList> list = new ArrayList<MessageList>();
		try {
			list = gson.fromJson(jsonData, new TypeToken<List<MessageList>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 取消按钮
	 * 
	 * @param view
	 */
	public void cancel(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); //
		// 强制隐藏键盘
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (REQUEST_CODE == requestCode && resultCode == 100) {
			MessageList messageList = messages.get(index_clickedItem);
			messageList.setTotal_unread(0);
			String last_reply = data.getStringExtra("last_reply");
			if (!TextUtils.isEmpty(last_reply)) {
				messageList.setLast_reply(last_reply);
			}

			String[] dataAndTime = null;
			try {
				dataAndTime = data.getStringExtra("post_time").split(" ");
			} catch (Exception e) {
				e.printStackTrace();
				//Log.w("iii", "post_time格式异常");
				List<MessageList> mss = adapter.getList();
				if (index_clickedItem < mss.size() - 1) {
					mss.remove(index_clickedItem);
					mss.add(index_clickedItem, messageList);
				} else {
					mss.remove(index_clickedItem);
					mss.add(messageList);
				}

				adapter.notifyDataSetChanged(mss);
				return;
			}
			if (dataAndTime != null && dataAndTime.length >= 2 && !TextUtils.isEmpty(dataAndTime[0])
					&& !TextUtils.isEmpty(dataAndTime[1])) {
				messageList.setUpdate_date(dataAndTime[0]);
				messageList.setUpdate_time(dataAndTime[1]);
			}
			messages.remove(index_clickedItem);
			messages.add(0, messageList);
			adapter.notifyDataSetChanged(messages);
		} else if (resultCode == 110) {
			// 从发起内部留言的webview页面返回过来
			requestDatas(SNAME_LIUYAN_LIST, 1, 20, "", true);
		}
	}

	@Override
	public void finish() {
		if ((recordScreeningPop != null && recordScreeningPop.isShowing())) {
			recordScreeningPop.dismissPop();
		}
		if ((popAboutCheckList != null && popAboutCheckList.isShowing())) {
			popAboutCheckList.dismiss();
		}

		SkuaidiSpf.saveRecordChooseItem(context, 0);// 将筛选条目置下标置0
		super.finish();
	}

	@Override
	protected void loadWeb(String url, String title) {
		Intent intent = new Intent(context, WebLoadView.class);
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(url);
		parameters.add(title);
		intent.putStringArrayListExtra("parameters", parameters);
		startActivityForResult(intent, REQUEST_CODE);
	}

	public static void ShowKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);

	}

	public static void KeyBoard(final EditText txtSearchKey, final String status) {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager m = (InputMethodManager) txtSearchKey.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				if (status.equals("open")) {
					m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
				} else {
					m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
				}
			}
		}, 300);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select:// selectConditions
			selectConditionsEvent();
			break;
		case R.id.tvSearch:
			queryNumber = etInputNo.getText().toString();
			if (TextUtils.isEmpty(queryNumber)) {
				return;
			}
			pageNum = 1;
			requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, queryNumber, true);
			InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
			break;
		default:
			break;
		}

	}

	private void selectConditionsEvent() {
		if (selectPop == null) {
			List<String> conditions = new ArrayList<String>();
			conditions.add("运单号");
			conditions.add("手机号");

			selectPop = new SelectConditionsListPOP(this, conditions, 0.3f, false,0);
			selectPop.setItemOnclickListener(new SelectConditionsListPOP.ItemOnClickListener() {

				@Override
				public void itemOnClick(int position) {
					switch (position) {
					case 0:// 单号
						selectConditions.setText("运单号");
						etInputNo.setHint("请输入运单号搜索");
						search_type = 2;
						break;
					case 1:// 手机号
						selectConditions.setText("手机号");
						etInputNo.setHint("输入手机号/手机尾号搜索");
						search_type = 1;
						break;

					default:
						etInputNo.setHintTextColor(getResources().getColor(R.color.gray_7));
						break;
					}
					selectPop.dismissPop();
					selectPop = null;
				}
			});
			// 设置点击空白区域的点击事件
			selectPop.setPopDismissClickListener(new SelectConditionsListPOP.PopDismissClickListener() {

				@Override
				public void onDismiss() {
					selectPop.dismissPop();
					selectPop = null;
				}
			});
			selectPop.showAsDropDown(select, -20, 0);
		} else {
			selectPop.dismissPop();
			selectPop = null;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(selectPop!=null&&selectPop.isShowing()){
			selectPop.dismissPop();
			selectPop = null;
		}
	}
}
