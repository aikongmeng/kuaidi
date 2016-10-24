package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.SmsRecordSearchAdapter;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.activity.smsrecord.SmsRecordActivity;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP.ItemOnClickListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP.PopDismissClickListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SmsRecordSearchActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private Activity mActivity = null;
	private Message message = null;
	private Intent mIntent = null;
	private View line1 = null;
	private List<SmsRecord> smsRecords = new ArrayList<>();
	private SmsRecordSearchAdapter adapter = null;
	private InputMethodManager imm = null;// 键盘管理类
	private SelectConditionsListPOP selectPop = null;

	private ListView lvSmsRecord = null;// 列表
	private EditText etInputNo = null;// 输入框
	private TextView tvSearch = null;// 取消按钮
	private ViewGroup llSmsRecord = null;// 列表区域
	private ViewGroup back = null;// 返回
	private ViewGroup select = null;// 选择筛选条件按钮
	private TextView selectConditions = null;// 显示筛选条件方案控件
	private TextView ll_no_data;// 无结果展示控件

	private PullToRefreshView pull = null;

	private int searchFlag = 0;
	private String queryNumber = "";
	private int pageNum = 1, totalPage = 1;
	private String inform_ID = "";
	private int updatePosition = -1;// 更新收件状态时候点击按钮的下标
	private boolean canClick = true;// 搜索是否可以点击

	Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.DELIVERY_LIST_GET_FAILED:
				pull.onFooterRefreshComplete();
				pull.onHeaderRefreshComplete();
				break;
			case SmsRecordActivity.GET_DELIVERY_LIST_FAIL:// 没有接收到数据
				smsRecords.clear();
				adapter.notifyData(smsRecords);
				llSmsRecord.setVisibility(View.GONE);
				ll_no_data.setVisibility(View.VISIBLE);
				break;
			case SmsRecordActivity.GET_DELIVERY_LIST_SUCCESS:// 接收数据成功
				ll_no_data.setVisibility(View.GONE);
				if (pageNum == 1) {
					imm.hideSoftInputFromWindow(etInputNo.getWindowToken(), 0); // 强制隐藏键盘
					smsRecords.clear();
					smsRecords = (List<SmsRecord>) msg.obj;
				} else {
					smsRecords.addAll((List<SmsRecord>) msg.obj);
				}
				adapter.notifyData(smsRecords);

				if (null != smsRecords && smsRecords.size() != 0) {// 如果加载到数据则显示列表，否则不显示
					llSmsRecord.setVisibility(View.VISIBLE);
				} else {
					llSmsRecord.setVisibility(View.GONE);
				}

				break;
			case Constants.GET_SIGN_IN_STATUS_SUCCESS:
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
		mActivity = this;
		SmsRecordActivity.activityStack.add(this);
		setContentView(R.layout.sms_record_search_activity);
		SkuaidiSpf.saveRecordChooseItem(mActivity, 0);
		initView();
		setAdapter();
		initListener();
	}

	private void setAdapter() {
		adapter = new SmsRecordSearchAdapter(mActivity, smsRecords, new SmsRecordSearchAdapter.OnclickListener() {
			@Override
			public void itemClickEvent(View view, int position, SmsRecord smsRecord) {
				smsRecord.setCm_nr_flag(0);
				adapter.getAdapterData().set(position, smsRecord);
				mIntent = new Intent(mActivity, RecordDetailActivity.class);
				mIntent.putExtra("fromActivity", "smsRecordSearchActivity");
				mIntent.putExtra("smsRecord", smsRecord);
				startActivityForResult(mIntent, SmsRecordActivity.REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY);
				EventBus.getDefault().post(new SmsRecord(smsRecord));
			}

			@Override
			public void updateSign(View view, int position, String informId) {
				updatePosition = position;
				inform_ID = informId;
				if (!Utility.isNetworkConnected()) {// 无网络
					UtilToolkit.showToast("请设置网络");
				} else {// 有网络
					KuaidiApi.setSignInStatus(mHandler, informId, position);
				}
			}

		});
		lvSmsRecord.setAdapter(adapter);

	}

	private void initView() {
		line1 = findViewById(R.id.line1);
		lvSmsRecord = (ListView) findViewById(R.id.lvSmsRecord);
		etInputNo = (EditText) findViewById(R.id.etInputNo);
		tvSearch = (TextView) findViewById(R.id.tvSearch);
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		llSmsRecord = (ViewGroup) findViewById(R.id.llSmsRecord);
		back = (ViewGroup) findViewById(R.id.back);
		select = (ViewGroup) findViewById(R.id.select);
		selectConditions = (TextView) findViewById(R.id.selectConditions);
		ll_no_data = (TextView) findViewById(R.id.ll_no_data);

		line1.setVisibility(View.GONE);
		etInputNo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

		back.setOnClickListener(this);
		select.setOnClickListener(this);
		tvSearch.setOnClickListener(this);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	}

	private void initListener() {
		// 监听listview滚动
		lvSmsRecord.setOnScrollListener(new OnScrollListener() {
			/**
			 * ListView的状态改变时触发
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

					break;
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
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

		etInputNo.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 点击的是搜索按钮
					if (!canClick) {
						return true;
					}
					queryNumber = etInputNo.getText().toString();
					pageNum = 1;
					switch (searchFlag){
						case 0:
							UMShareManager.onEvent(mActivity,"SmsRecordSearch_by_phone","SmsRecordSearch","短信记录搜索：手机号码搜索");
							getDeliveryList(pageNum, queryNumber, "", "", "", "", "", "");// 手机号搜索
							break;
						case 1:
							UMShareManager.onEvent(mActivity,"SmsRecordSearch_by_no","SmsRecordSearch","短信记录搜索：手机编号搜索");
							getDeliveryList(pageNum, "", queryNumber, "", "", "", "", "");// 编号搜索
							break;
						case 2:
							UMShareManager.onEvent(mActivity,"SmsRecordSearch_by_orderNo","SmsRecordSearch","短信记录搜索：手机运单号搜索");
							getDeliveryList(pageNum,"","","",queryNumber,"","","");// 单号搜索
							break;
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
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					smsRecords.clear();
					adapter.notifyData(smsRecords);
				}
				return false;
			}
		});

		etInputNo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// etInputNo.removeTextChangedListener(this);//解除文字改变事件
				// etInputNo.setText(s.toString().toUpperCase());//转换
				// etInputNo.setSelection(s.toString().length());//重新设置光标位置
				// etInputNo.addTextChangedListener(this);//重新绑

				// etInputNo.removeTextChangedListener(this);
				// final String s1 = etInputNo.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (searchFlag == 1) {
					final String temp = s.toString();
					if (!Utility.isEmpty(temp)) {

						String tem = temp.substring(temp.length() - 1, temp.length());
						char[] temC = tem.toCharArray();
						int mid = temC[0];

						// 判断如果是小写的字母的换，就转换
						if (mid >= 97 && mid <= 122) {// 小写字母
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									// 小写转大写
									etInputNo.setText(temp.toUpperCase());
									etInputNo.setSelection(temp.toString().length());// 重新设置光标位置
								}
							}, 300);
						}
					}
				}
			}
		});

		// 以上四个点击事件，控制扫条码和确定按钮的变化

		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {

				pull.postDelayed(new Runnable() {
					@Override
					public void run() {
						pageNum = 1;
						// 拉取数据
						if (Utility.isNetworkConnected()) {
							switch (searchFlag){
								case 0:
									getDeliveryList(pageNum, queryNumber, "", "", "", "", "", "");// 手机号搜索
									break;
								case 1:
									getDeliveryList(pageNum, "", queryNumber, "", "", "", "", "");// 编号搜索
									break;
								case 2:
									getDeliveryList(pageNum,"","","",queryNumber,"","","");// 单号搜索
									break;
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
						if (Utility.isNetworkConnected() == true) {
							pageNum += 1;
							if (pageNum <= totalPage) {
								switch (searchFlag){
									case 0:
										getDeliveryList(pageNum, queryNumber, "", "", "", "", "", "");// 手机号搜索
										break;
									case 1:
										getDeliveryList(pageNum, "", queryNumber, "", "", "", "", "");// 编号搜索
										break;
									case 2:
										getDeliveryList(pageNum,"","","",queryNumber,"","","");// 单号搜索
										break;
								}
							} else {
								pull.onFooterRefreshComplete();
								UtilToolkit.showToast( "已加载全部数据");
							}
						} else {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SmsRecordActivity.REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY
				&& resultCode == SmsRecordActivity.RESULT_GETINTO_RECORD_DETAIL_ACTIVITY) {
			adapter.notifyDataSetChanged();
		}
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
		case R.id.back:
			setResult(SmsRecordActivity.REQUEST_SEARCH);
			finish();
			break;
		case R.id.select:// selectConditions
			selectConditionsEvent();
			break;
		case R.id.tvSearch:
			if (!canClick) {
				return;
			}
			queryNumber = etInputNo.getText().toString();
			pageNum = 1;
			switch (searchFlag){
				case 0:
					getDeliveryList(pageNum, queryNumber, "", "", "", "", "", "");// 手机号搜索
					break;
				case 1:
					getDeliveryList(pageNum, "", queryNumber, "", "", "", "", "");// 编号搜索
					break;
				case 2:
					getDeliveryList(pageNum,"","","",queryNumber,"","","");// 单号搜索
					break;
			}
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
			conditions.add("单号");

			selectPop = new SelectConditionsListPOP(mActivity, conditions, 0.3f, false,0);
			selectPop.setItemOnclickListener(new ItemOnClickListener() {

				@Override
				public void itemOnClick(int position) {
					final String digits;
					switch (position) {
					case 0:// 手机号
						selectConditions.setText("手机号");
						etInputNo.setHint("输入手机号/手机尾号搜索");
						etInputNo.setText("");
						etInputNo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
						etInputNo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });// 设置输入框最多只能输入5个字符
						searchFlag = 0;
						break;
					case 1:// 编号
						selectConditions.setText("编号");
						etInputNo.setHint("请输入编号搜索");
						etInputNo.setText("");
						etInputNo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });// 设置输入框最多只能输入5个字符
						digits = "abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
						etInputNo.setKeyListener(new DigitsKeyListener() {
							@Override
							public int getInputType() {
								return InputType.TYPE_TEXT_VARIATION_PASSWORD;
							}

							@Override
							protected char[] getAcceptedChars() {
								char[] data = digits.toCharArray();
								return data;
							}

						});
						searchFlag = 1;
						break;
					case 2:
						selectConditions.setText("运单号");
						etInputNo.setHint("输入单号搜索");
						etInputNo.setText("");
						digits = "abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
						etInputNo.setKeyListener(new DigitsKeyListener() {
							@Override
							public int getInputType() {
								return InputType.TYPE_TEXT_VARIATION_PASSWORD;
							}

							@Override
							protected char[] getAcceptedChars() {
								char[] data = digits.toCharArray();
								return data;
							}
						});
						searchFlag = 2;
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
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && !isOutOfBounds(this, event)) {
			setResult(SmsRecordActivity.REQUEST_SEARCH);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(SmsRecordActivity.REQUEST_SEARCH);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 *调用短信记录列表接口方法
	 */
	private void getDeliveryList(int page_num, String phone, String order_number, String query_number, String dh, String status, String start_date,
			String end_date) {
		KLog.i("kb","点击一下");
		canClick = false;
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_user/get_delivery_list");
			data.put("role", "courier");
			data.put("page_size", Constants.PAGE_SIZE);
			data.put("page_num", page_num);
			data.put("user_phone", phone);
			data.put("query_number", query_number);
			data.put("order_number", order_number);
			data.put("dh", dh);
			data.put("status", status);
			data.put("start_date", start_date);
			data.put("end_date", end_date);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
		canClick = true;
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (null != result) {
			message = new Message();
			if (!Utility.isEmpty(sname) && sname.equals("inform_user/get_delivery_list")) {// 获取记录列表接口
				try {
					totalPage = result.getInt("total_page");
					List<SmsRecord> smsRecords = new ArrayList<>();
					if (totalPage != 0) {
						JSONArray desc = result.getJSONArray("desc");
						for (int i = 0; i < desc.length(); i++) {
							JSONObject object = (JSONObject) desc.get(i);
							SmsRecord smsRecord = new SmsRecord();
							smsRecord.setInform_id(object.getString("inform_id"));
							smsRecord.setTopic_id(object.getString("topic_id"));
							smsRecord.setExpress_number(object.getString("express_number"));
							smsRecord.setDh(object.getString("dh"));
							// smsRecord.setBrand(object.getString("brand"));
							// smsRecord.setShop_name(object.getString("shop_name"));
							// smsRecord.setCm_name(object.getString("cm_name"));
							smsRecord.setUser_phone(object.getString("user_phone"));
							smsRecord.setContent(object.getString("content"));
							smsRecord.setLast_update_time(object.getLong("last_update_time"));
							smsRecord.setStatus(object.getString("status"));
							smsRecord.setSigned(object.getString("signed"));
							smsRecord.setLast_msg_content(object.getString("last_msg_content"));
							smsRecord.setLast_msg_content_type(object.getString("last_msg_content_type"));
							smsRecord.setLast_msg_time(object.getString("last_msg_time"));
							// smsRecord.setUser_nr_flag(object.getString("user_nr_flag"));
							smsRecord.setCm_nr_flag(object.getInt("cm_nr_flag"));
							// smsRecord.setShop_nr_flag(object.getString("shop_nr_flag"));
							smsRecords.add(smsRecord);
							message.what = SmsRecordActivity.GET_DELIVERY_LIST_SUCCESS;
						}
					} else {
						message.what = SmsRecordActivity.GET_DELIVERY_LIST_FAIL;
					}
					message.obj = smsRecords;
					mHandler.sendMessage(message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
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

}
