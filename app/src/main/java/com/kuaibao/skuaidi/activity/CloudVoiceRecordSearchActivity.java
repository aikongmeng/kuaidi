package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.CloudVoiceRecordSearchAdapter;
import com.kuaibao.skuaidi.activity.adapter.CloudVoiceRecordSearchAdapter.ButtonClickListener;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP.ItemOnClickListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP.PopDismissClickListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CloudVoiceRecordSearchActivity extends SkuaiDiBaseActivity implements OnClickListener, OnItemClickListener {
	
	private final static int UPDATE_SIGNED_STATUS = 0x1007;
	
	private Activity mActivity = null;
	private Message msg = null;
	private CloudVoiceRecordSearchAdapter adapter = null;
	private SelectConditionsListPOP selectPop = null;
	private InputMethodManager imm = null;// 键盘管理类

	// 接口调用返回的结果
	private int total_page = 0;
	private int pageNum = 1;
	private int pageSize = 15;
	
	private int updatePosition = -1;

	private String phone = "";
	private String start_time = "";
	private String end_time = "";
	private String bh = "";
	private String status = "";
	private boolean isPhoneNumber = true;

	private String queryNum = null;// 输入框中输入的内容
	private List<CloudVoiceRecordEntry> cvre = new ArrayList<>();

	private ListView lvSmsRecord = null;
	private EditText etInputNo = null;
	private TextView tvSearch = null;
	private PullToRefreshView pull = null;
	private ViewGroup back = null;
	private ViewGroup llSmsRecord = null;
	private ViewGroup select = null;
	private TextView selectConditions= null;
	private TextView ll_no_data ;
	private boolean canClick = true;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CloudVoiceRecordActivity.GET_VOICE_LIST_SUCCESS:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				ll_no_data.setVisibility(View.GONE);
				if (pageNum == 1) {
					imm.hideSoftInputFromWindow(etInputNo.getWindowToken(), 0); // 强制隐藏键盘
					cvre.clear();
					cvre = (List<CloudVoiceRecordEntry>) msg.obj;
				} else {
					cvre.addAll((List<CloudVoiceRecordEntry>) msg.obj);
				}
				adapter.notifyList(cvre);

				if (null != cvre && cvre.size() != 0) {// 如果加载到数据则显示列表，否则不显示
					llSmsRecord.setVisibility(View.VISIBLE);
				} else {
					llSmsRecord.setVisibility(View.GONE);
				}
				break;
			case CloudVoiceRecordActivity.GET_VOICE_LIST_FAIL:
				cvre.clear();
				adapter.notifyList(cvre);
				llSmsRecord.setVisibility(View.GONE);
				ll_no_data.setVisibility(View.VISIBLE);
				break;
			case UPDATE_SIGNED_STATUS:
				adapter.modifySignedStatus(updatePosition);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.sms_record_search_activity);
		mActivity = this;
		SkuaidiSpf.saveRecordChooseItem(mActivity, 0);
		CloudVoiceRecordActivity.activityStack.add(this);
		initView();
		setListener();

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		lvSmsRecord = (ListView) findViewById(R.id.lvSmsRecord);
		etInputNo = (EditText) findViewById(R.id.etInputNo);
		tvSearch = (TextView) findViewById(R.id.tvSearch);
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		llSmsRecord = (ViewGroup) findViewById(R.id.llSmsRecord);
		back = (ViewGroup) findViewById(R.id.back);
		select = (ViewGroup) findViewById(R.id.select);
		selectConditions = (TextView) findViewById(R.id.selectConditions);
		ll_no_data = (TextView) findViewById(R.id.ll_no_data);
		etInputNo.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_VARIATION_NORMAL);
		
		back.setOnClickListener(this);
		select.setOnClickListener(this);
		lvSmsRecord.setOnItemClickListener(this);
		tvSearch.setOnClickListener(this);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	}

	private void setListener() {

		// 监听listview滚动
		lvSmsRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
			/**
			 * ListView的状态改变时触发
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
						imm.hideSoftInputFromWindow(etInputNo.getWindowToken(), 0); // 强制隐藏键盘
						break;
					default:
						break;
				}
			}

			/**
			 * 正在滚动 firstVisibleItem第一个Item的位置 visibleItemCount 可见的Item的数量
			 * totalItemCount item的总数
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		adapter = new CloudVoiceRecordSearchAdapter(mActivity, mHandler, cvre, new ButtonClickListener() {

			@Override
			public void call(View v, int position, String callNumber) {
				if (Utility.isEmpty(callNumber)) {
					UtilToolkit.showToast("本条记录没有联系号码");
				} else {
					// 跳转到拨打电话界面
					// Intent intent = new
					// Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+callNumber));
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// startActivity(intent);
					// 直接拨打电话
//					Intent mIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
//					startActivity(mIntent);
					AcitivityTransUtil.showChooseTeleTypeDialog(CloudVoiceRecordSearchActivity.this, "", callNumber,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
				}
			}

			@Override
			public void updateSignedStatus(View v, int position, String cid) {
				updatePosition = position;
				updateSigned(cid);
			}
		});
		lvSmsRecord.setAdapter(adapter);

		etInputNo.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (!canClick)
					return true;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 点击的是搜索按钮
					queryNum = etInputNo.getText().toString();
					if(!isPhoneNumber){
						getList(1, phone, "", "", "", queryNum, "");
					}else{
						getList(1, queryNum, "", "", "", "", "");
					}
					return true;
				} else {
					return false;
				}
			}
		});
		etInputNo.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DEL){
					cvre.clear();
					adapter.notifyList(cvre);
				}
				return false;
			}
		});
		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {

				pull.postDelayed(new Runnable() {
					@Override
					public void run() {
						// 拉取数据
						if (Utility.isNetworkConnected()) {
							pageNum = 1;
							if(!isPhoneNumber){
								getList(1, phone, "", "", "", queryNum, "");
							}else{
								getList(1, queryNum, "", "", "", "", "");
							}
						} else {
							pull.onHeaderRefreshComplete();
							UtilToolkit.showToast("无网络连接");
						}
					}
				}, 1000);
			}
		});

		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (Utility.isNetworkConnected()) {
							pageNum = pageNum + 1;
							if (pageNum <= total_page) {
								if(!isPhoneNumber){
									getList(1, phone, "", "", "", queryNum, "");
								}else{
									getList(1, queryNum, "", "", "", "", "");
								}
							} else {
								UtilToolkit.showToast("已加载完全部数据");
								pull.onFooterRefreshComplete();
							}
						} else {
							pull.onFooterRefreshComplete();
							UtilToolkit.showToast("无网络连接");
						}
					}
				}, 1000);
			}
		});

		// 打开软键盘
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500); // 在一秒后打开
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvSearch:
			if (!canClick)
				return;
			queryNum = etInputNo.getText().toString();
			if(!isPhoneNumber){
				getList(1, phone, "", "", "", queryNum, "");
			}else{
				getList(1, queryNum, "", "", "", "", "");
			}
			break;
		case R.id.back:
			setResult(CloudVoiceRecordActivity.RESULT_SEARCH);
			finish();
			break;
		case R.id.select://selectConditions
			selectConditionsEvent();
			break;
		default:
			break;
		}

	}
	
	private void selectConditionsEvent() {
		if (selectPop == null) {
			List<String> conditions = new ArrayList<>();
			conditions.add("手机号");
			conditions.add("编号");

			selectPop = new SelectConditionsListPOP(mActivity, conditions,0.3f, false,0);
			selectPop.setItemOnclickListener(new ItemOnClickListener() {

				@Override
				public void itemOnClick(int position) {
					switch (position) {
					case 0:// 手机号
						UMShareManager.onEvent(mActivity,"CloudVoiceRecordSearch_by_phone","CloudVoiceSmsRecordSearch","云呼记录搜索：手机号码搜索");
						selectConditions.setText("手机号");
						etInputNo.setHint("输入手机号/手机尾号搜索");
						isPhoneNumber = true;
						break;
					case 1:// 编号
						UMShareManager.onEvent(mActivity,"CloudVoiceSmsRecordSearch_by_no","CloudVoiceSmsRecordSearch","云呼记录搜索：手机编号搜索");
						selectConditions.setText("编号");
						etInputNo.setHint("请输入编号搜索");
						isPhoneNumber = false;
						break;
					default:
						etInputNo.setHintTextColor(Utility.getColor(mActivity,R.color.gray_7));
						break;
					}
					selectPop.dismissPop();
					selectPop = null;
				}
			});
			// 设置点击空白区域的点击事件
			selectPop.setPopDismissClickListener(new PopDismissClickListener() {

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			setResult(CloudVoiceRecordActivity.RESULT_SEARCH);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && !isOutOfBounds(this, event)) {
			setResult(CloudVoiceRecordActivity.RESULT_SEARCH);
			finish();
			return true;
		}
		return super.onTouchEvent(event);
	}

	private boolean isOutOfBounds(Activity context, MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int slop = ViewConfiguration.get(mActivity).getScaledWindowTouchSlop();
		final View decorView = context.getWindow().getDecorView();
		return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
	}

	/**
	 * 调用云呼语音列表接口
	 * getList
	 * @param pageNum 页数
	 * @param phone 按手机号
	 * @param start_time 开始时间
	 * @param end_time 结束时间
	 * @param bh 编号
	 * @param status 接听状态
	 * void 返回类型
	 * 顾冬冬
	 */
	private void getList(int pageNum, String phone, String queryNum, String start_time, String end_time, String bh, String status) {
		canClick = false;
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "ivr.call.list");
			data.put("page_num", pageNum);
			data.put("page_size", pageSize);
			data.put("call_number", phone);
			data.put("query_number", queryNum);
			data.put("start_time", start_time);
			data.put("end_time", end_time);
			data.put("bh", bh);
			data.put("status", status);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
	}
	
	/**
	 * 更新取件状态接口
	 */
	private void updateSigned(String cid) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "ivr/updateSign");
			data.put("id", cid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}
	@Override
	protected void onRequestSuccess(String sname, String message, String json1, String act) {
		canClick = true;
		JSONObject result = null;
		try {
			result = new JSONObject(json1);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if ("ivr.call.list".equals(sname) && null != result) {
			List<CloudVoiceRecordEntry> cvre = new ArrayList<>();
			try {
				JSONObject json = result.getJSONObject("retArr");
				total_page = json.optInt("total_page");// 总页数
				msg = new Message();
				if (total_page != 0) {

					JSONArray jArr = json.optJSONArray("data");
					if (null != jArr && 0 != jArr.length()) {
						for (int i = 0; i < jArr.length(); i++) {
							JSONObject jObj = jArr.optJSONObject(i);
							CloudVoiceRecordEntry cvRecordEntry = new CloudVoiceRecordEntry();
							cvRecordEntry.setCid(jObj.optString("cid"));
							cvRecordEntry.setTopic_id(jObj.optString("topic_id"));
							cvRecordEntry.setBh(jObj.optString("bh"));
							cvRecordEntry.setVoice_title(jObj.optString("voice_title"));
							cvRecordEntry.setVoice_name(jObj.optString("voice_name"));
							cvRecordEntry.setVoice_path(jObj.optString("voice_path"));
							cvRecordEntry.setFee_mins(jObj.optString("fee_mins"));
							cvRecordEntry.setCall_number(jObj.optString("call_number"));
							cvRecordEntry.setUser_input_key(jObj.optString("user_input_key"));
							cvRecordEntry.setCall_duration(jObj.optInt("call_duration"));
							cvRecordEntry.setStatus(jObj.optString("status"));
							cvRecordEntry.setStatus_msg(jObj.optString("status_msg"));
							cvRecordEntry.setCreate_time(jObj.optString("create_time"));
							cvRecordEntry.setSigned(jObj.optInt("signed"));
							cvRecordEntry.setNoreadFlag(jObj.optInt("noread_flag"));
							cvRecordEntry.setLastMsgContent(jObj.optString("last_msg_content"));
							cvRecordEntry.setLastMsgContentType(jObj.optString("last_msg_content_type"));
							cvRecordEntry.setLastMsgTime(jObj.optString("last_msg_time"));
							cvre.add(cvRecordEntry);
						}
					}
					msg.what = CloudVoiceRecordActivity.GET_VOICE_LIST_SUCCESS;
				} else {
					msg.what = CloudVoiceRecordActivity.GET_VOICE_LIST_FAIL;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			msg.obj = cvre;
			mHandler.sendMessage(msg);
		}else if ("ivr/updateSign".equals(sname)) {
			msg = new Message();
			msg.what = UPDATE_SIGNED_STATUS;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		canClick = true;
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		canClick = true;
		if (!Utility.isEmpty(msg)) {
			UtilToolkit.showToast(msg);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CloudVoiceRecordEntry entry = cvre.get(position);
		if (Integer.parseInt(entry.getTopic_id()) == 0) {
			UtilToolkit.showToast("无可查看的详情内容");
			return;
		}
		cvre.get(position).setNoreadFlag(0);
		Intent intent = new Intent(mActivity, RecordDetailActivity.class);
		intent.putExtra("fromActivity", "cloudVoiceRecordActivity");
		intent.putExtra("cloudEntry", cvre.get(position));
		startActivity(intent);
		EventBus.getDefault().post(new CloudVoiceRecordEntry(entry));
		adapter.notifyDataSetChanged();

	}

}
