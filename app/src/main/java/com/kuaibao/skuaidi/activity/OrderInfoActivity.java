package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.OrderImAdapter;
import com.kuaibao.skuaidi.activity.adapter.OrderImAdapter.OnFuctionBTNClickListener;
import com.kuaibao.skuaidi.activity.adapter.OrderImAdapter.OnOrderCallback;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.bluetooth.printer.PrinterBase;
import com.kuaibao.skuaidi.bluetooth.printer.PrinterManager;
import com.kuaibao.skuaidi.business.order.OrderNotifyImgActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiOrderDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiOrderDialog.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.entry.OrderIm;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.SkuaidiTelPhoneManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.service.DownloadTask;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import HPRTAndroidSDK.HPRTPrinterHelper;

/**
 * 订单详情
 *
 */
public class OrderInfoActivity extends SkuaiDiBaseActivity implements OnFuctionBTNClickListener, OnClickListener {
	public static final String ACTION_SET_MONEY = "set_money";
	private static String GET_ORDER_CHARACTER = "partner/thermalPartner/getCharacters";
	private static String GET_ORDER_INFO_DETAIL = "order/getInfoByOrderNumber";
	private static String REFUSE_ORDER = "order/refuseOrder";
	private static String SET_EXPRESS_DELIVERY = "order/setWaybillNo";
	private static String ZT_SENDORDER = "AllThermalPaper/sendThermalPaper";
	private Context context;

	TextView tv_sender_phone, tv_order_time, tv_order_no, tv_sender_address, tv_sender_ps, tv_deliverno;
	// Button btn_deliverno;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
	private TextView tv_orderinfo_title, tv_send;
	private SkuaidiTextView tv_more;
	private Button btn_record_voice;
	private TextView btn_record_voice1;
	// private Button btn_fast_reply;
	private ListView lv_order_im;
	private RelativeLayout rl_orderinfo_layout;
	private Button btn_reply_customer, btn_reply_server;
	private TextView imb_send, tv_content_txt;
	private RelativeLayout rl_bottom, rl_bottom_reply, rl_bottom_txt, rl_bottom_yuyin, rl_order_bottom;
	private LinearLayout ll_send;
	private int sender = 0;
	private ImageButton imb_input_txt;
	private View view_line;
	private ImageView iv_huifu_jiantou, imv_phone_icon;
	//private ProgressDialog pdWaitingMessage;
	private View imb_input_voice;
	private PullToRefreshView order_pull_refresh;

	int choose = 1;//默认申通打回选择的原因
	private Order order;// 订单详情
	private String orderNo;// 订单号
	private String decodestr;// 运单号
	// String[] reply_list;
	private List<OrderIm> orderIms;
	private OrderImAdapter adapter;
	private String reply_txt;// 回复的文本内容
	private String thirdOrderNo;//第三方推送的订单号

	private int flag = 1;
	private int send = 1;
	// 订单对话框
	private ImageView iv_check;
	private int check_num = 0;
	private boolean is_check = false;
	private boolean select_mode = false;
	private SkuaidiDB skuaidiDb;
	private List<ReplyModel> models;

	// 录音相关
	private boolean islongclick = false;
	private String recorderPath;
	private File audioFile;
	private MediaRecorder mediaRecorder;
	private long start_time = 0;
	private long stop_time = 0;
	private long recorder_time = 0;
	private HPRTPrinterHelper hprtPrinter;
	private boolean flagIsRun;
	private int replyType;// 回复类型——回复客户、回复客服

	//打印电子面单相关
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	//	private List<BluetoothDevice> allDevices = new ArrayList<BluetoothDevice>();
	private PrinterBase myPrinter;
	private BluetoothDevice printer;

	private String bigChar = null;
	private boolean enterPrint;
	private UserInfo userInfo;

	public static final int USER_ROLE_CUSTOMER = 1;
	public static final int USER_ROLE_COURIER = 2;
	public static final int USER_ROLE_CUSSERVICE = 3;
	public static final int SESSION_TYPE_ALL = 0;
	public static final int SESSION_TYPE_COURIERANDCUSTOMER = 1;
	public static final int SESSION_TYPE_COURIERANDCUSSERVICE = 2;
	public static final HashMap<String, Integer> BACK_REASONS = new HashMap<String, Integer>();
	public static final ArrayList<String> BACK_REASONS_TEMP = new ArrayList<String>();
	static {
		BACK_REASONS.put("超规格", 142);
		BACK_REASONS.put("地址非本网点揽收范围", 143);
		BACK_REASONS.put("禁运品", 144);
		BACK_REASONS.put("客户不在家", 145);
		BACK_REASONS.put("客户取消订单", 146);
		BACK_REASONS.put("派送地超服务范围", 147);
		BACK_REASONS.put("取货地超服务范围", 148);
		BACK_REASONS.put("上门后用户不接受价格", 149);
		BACK_REASONS.put("虚假揽收地址", 150);
		BACK_REASONS.put("客户包装问题放弃投递", 151);
		BACK_REASONS.put("客户恶意下单", 152);
		BACK_REASONS.put("客户已更换投递方式", 153);
		BACK_REASONS.put("同行件", 154);
		BACK_REASONS.put("客户重复下单1", 155);
		BACK_REASONS.put("客户更改取件时间2", 156);
		for (String reason : BACK_REASONS.keySet()) {
			BACK_REASONS_TEMP.add(reason);
		}
	}
	private MyBroadcastReceiver receiver;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

				case Constants.NETWORK_FAILED:
					// order.setDeliverNo(decodestr);
					// order.setOrder_type("pickup");
					// SKuaidiApplication.getInstance().postMsg("OrderCenterActivity",
					// "backFill_num", order);
					// Utility.showToast(context, "网络连接错误,请稍后重试!");
					break;

				case Constants.TYPE_ORDER_ONE:
					order.setDeliverNo(decodestr);
					order.setOrder_type("pickup");
					newDB.updateOrderExpNO(order);
					adapter.setDeliverNo(decodestr);
					adapter.notifyDataSetChanged();
					SKuaidiApplication.getInstance().postMsg("OrderCenterActivity", "backFill_num", order);
					KuaidiApi.uploadDeliverNo(context, handler, orderNo, decodestr);
					break;
				case Constants.ADD_DELIVE_NO:

					int con = (Integer) msg.obj;
					order.setDeliverNo(decodestr);
					order.setOrder_type("pickup");
					newDB.updateOrderExpNO(order);
					adapter.setDeliverNo(decodestr);
					adapter.notifyDataSetChanged();
					SKuaidiApplication.getInstance().postMsg("OrderCenterActivity", "backFill_num", order);
					JSONObject object = new JSONObject();
					try {
						object.put("sname", SET_EXPRESS_DELIVERY);// 接口名称
						object.put("orderNumber", orderNo);// Android s端
						object.put("waybillNo", decodestr);// 运单号
						object.put("shipperInform", con);// 短信内容 1-发 0-不发
						httpInterfaceRequest(object, false, HttpHelper.SERVICE_V1);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// KuaidiApi.uploadDeliverNo(context, handler, orderNo,
					// decodestr);
					// order.setDeliverNo(decodestr);
					// newDB.updateOrderExpNO(order);
					//System.out.println("loc_order_id : " + order.getLoc_order_id());

					break;
				case Constants.MSG_UPDATE_RECORDER:
					// 收到定时器消息 根据声音等级获取对应的资源图片
					int recId = getRECImageByamp(getAmplitude());
					if (recId != 0) {
						pop_rec_iv.setImageResource(recId);
					}
					break;
				case Constants.PHONE_CALL:
					adapter.notifyDataSetChanged();
//				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
//						+ AllInterface.formatCall(msg.obj.toString())));
//				startActivity(intent);
					AcitivityTransUtil.showChooseTeleTypeDialog(OrderInfoActivity.this, "", msg.obj.toString(),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
					break;

				case Constants.GET_FAID:
					// order.setDeliverNo(decodestr);
					// order.setOrder_type("pickup");
					// SKuaidiApplication.getInstance().postMsg("OrderCenterActivity",
					// "backFill_num", order);
					// Utility.showToast(context, "对不起,网络发生异常!");
					break;

				case Constants.SUCCESS:
					// tv_deliverno.setText("运单号：" + decodestr);
					// tv_deliverno.setVisibility(View.VISIBLE);
					// btn_deliverno.setVisibility(View.GONE);
					// adapter.setDeliverNo(decodestr);
					// adapter.notifyDataSetChanged();
					//
					// SKuaidiApplication.getInstance().postMsg("OrderCenterActivity",
					// "backFill_num", order);
					break;

				case Constants.FAILED:
					// order.setDeliverNo(decodestr);
					// order.setOrder_type("pickup");
					// SKuaidiApplication.getInstance().postMsg("OrderCenterActivity",
					// "backFill_num", order);
					// Utility.showToast(context, "上传单号失败，请稍后重试");
					break;

				case Constants.ORDER_IM_DETAIL_PARSE_OK:
					//System.out.println("detail pase ok");
					dismissProgressDialog();//OrderInfoActivity.this);
					//pdWaitingMessage.dismiss();
					if (msg.arg1 == 1) {
						KLog.i("tag", msg.obj.toString());
						if (msg.obj != null) {
							order = (Order) msg.obj;
							// tv_orderinfo_title.setText(order.getPhone());
							order.setId(orderNo);
							orderIms.clear();
							if (order.getOrders() != null) {
								orderIms.addAll(order.getOrders());
							}

							adapter = new OrderImAdapter(OrderInfoActivity.this, context, handler, order, new BackReasonCallback(), orderIms,
									OrderInfoActivity.this);
							if (orderIms.size() > 4) {
								lv_order_im.setStackFromBottom(true);
							}
							lv_order_im.setAdapter(adapter);
						}
					} else {
						UtilToolkit.showToast( msg.obj.toString());
					}
					break;

				case Constants.ORDER_IM_DETAIL_PARSE_FAILD:
					dismissProgressDialog();//OrderInfoActivity.this);
					//pdWaitingMessage.dismiss();
					UtilToolkit.showToast( "获取消息异常");
					break;

				case Constants.ORDER_IM_ADD_GET_OK:

					JsonXmlParser.parseOrderImAdd(handler, msg.obj.toString(), msg.arg1);
					break;

				case Constants.ORDER_IM_ADD_GET_FAILD:
					UtilToolkit.showToast( "获取消息异常");
					break;

				case Constants.ORDER_IM_ADD_PARSE_OK:
					//System.out.println("orderIm add pase ok");
					if (msg.arg1 == 1) {
						// SkuaidiNewApi.synchroOrderData(context, handler);
						OrderIm orderIm = new OrderIm();
						orderIm.setSpeakId(userInfo.getUserId());
						orderIm.setSpeakRole(2);
						orderIm.setContentType(msg.arg2);
						if (orderIm.getContentType() == 1) {
							orderIm.setTxtContent(reply_txt);
							orderIm.setVoiceLength(0);
						} else if (orderIm.getContentType() == 3) {
							orderIm.setVoiceContent(recorderPath);
							orderIm.setVoiceLength((int) (recorder_time / 1000));
						}
						orderIm.setSpeakTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
						orderIm.setUserName(userInfo.getUserName());

						orderIms.add(orderIm);
						adapter.notifyDataSetChanged();
						if (orderIms.size() > 4) {
							lv_order_im.setStackFromBottom(true);
						}
					} else {
						UtilToolkit.showToast( "此订单用户为非注册用户，无法发送留言！");
					}
					break;

				case Constants.ORDER_IM_ADD_PARSE_FAILD:
					UtilToolkit.showToast( "消息发送失败!");
					break;

				case Constants.ZT_SEND_ORDER://中通录单
					SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
					dialog.setTitleGray("温馨提示");
					dialog.setTitleColor(R.color.title_bg);
					dialog.setContentGray("确认要录单到中通系统吗?");
					dialog.setPositionButtonTextGray("确认");
					dialog.setNegativeButtonTextGray("取消");
					dialog.setPositionButtonClickListenerGray(new SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray() {
						@Override
						public void onClick(View v) {
							UMShareManager.onEvent(context, "order_recordOrder_remind", "recordOrder_remind", "业务-订单-录单-温馨提示");
							showProgressDialog( "正在录单...");
							ztSendOrder();
						}
					});
					dialog.showDialogGray(lv_order_im.getRootView());
					break;
				case Constants.PRINT_ORDER:
					btAdapter = BluetoothAdapter.getDefaultAdapter();
					if(!btAdapter.isEnabled()){
						btAdapter.enable();
					}
					showProgressDialog( "连接中");
					printer = SKuaidiApplication.device;
					if(!Utility.isEmpty(printer) && BluetoothAdapter.STATE_OFF != btAdapter.getState()){
						myPrinter = PrinterManager.getPrinter(printer, OrderInfoActivity.this, handler);
					}else if(BluetoothAdapter.STATE_OFF == btAdapter.getState()){
						try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!Utility.isEmpty(myPrinter)){
						if(!myPrinter.isConnected()) {
							printerConnect();
						}else{
							getBigChar();
						}
					}else {
						btAdapter.startDiscovery();
						UtilToolkit.showToast( "蓝牙打印机连接中...\n连接成功将跳转到打印页面");
						// 注册用以接收到已搜索到的蓝牙设备的receiver
						IntentFilter mFilter = new IntentFilter();
						mFilter.addAction(BluetoothDevice.ACTION_FOUND);
						mFilter.addAction(BluetoothAdapter.	ACTION_DISCOVERY_FINISHED);
						registerReceiver(receiver, mFilter);
					}
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.orderinfo);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = this;
		Intent intent = getIntent();
		userInfo = SkuaidiSpf.getLoginUser();
		// order = (Order) intent.getSerializableExtra("order");
		orderNo = intent.getStringExtra("orderno");
		getControl();
		setData();
		addListener();
		initPopupWindow();

		select_mode = SkuaidiSpf.getHasHasSelectModel(context);
		receiver = new MyBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_SET_MONEY);
		registerReceiver(receiver, intentFilter);
	}

	private void addListener() {
		tv_more.setOnClickListener(this);
		btn_reply_customer.setOnClickListener(this);
		btn_reply_server.setOnClickListener(this);
		btn_record_voice.setOnClickListener(this);
		imb_input_txt.setOnClickListener(this);
		ll_send.setOnClickListener(this);
		tv_send.setOnClickListener(this);

		//下拉刷新
		order_pull_refresh.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
//				KuaidiApi.getOrderImDetail(context, handler, orderNo);
				getOrderDetail(orderNo);
			}
		});

		btn_record_voice1.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				islongclick = true;
				if (popupWindow != null) {
					popupWindow.showAtLocation(findViewById(R.id.rl_orderinfo_layout), Gravity.CENTER, 0, 0);
				}
				startRecorder();
				return false;
			}
		});
		btn_record_voice1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (islongclick == true) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (popupWindow != null) {
							// 关闭pop
							popupWindow.dismiss();
						}
						stopRecorder();// 停止录音
						if (audioFile.exists()) {
							String yuyincontent = StringUtil.recorderToString(audioFile.getAbsolutePath());
							//System.out.println("audioFile.getAbsolutePath() : " + audioFile.getAbsolutePath());

							// //System.out.println(recorder_time);
							KuaidiApi.getOrderImAdd(context, handler, orderNo, Constants.TYPE_YUYIN, yuyincontent,
									recorder_time / 1000, Constants.TYPE_REPLY_CUSTOMER);
						}
						// rl_order_bottom.setVisibility(View.GONE);
						// view_line.setVisibility(View.GONE);
						// rl_bottom_txt.setVisibility(View.GONE);
						islongclick = false;
					}
				}
				return false;
			}
		});

		ll_send.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv_huifu_jiantou.setBackgroundResource(SkuaidiSkinManager.getSkinResId("checked_up"));
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv_huifu_jiantou.setBackgroundResource(SkuaidiSkinManager.getSkinResId("checked_down"));
				}
				return false;
			}
		});

		tv_content_txt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (tv_content_txt.getText().toString().length() != 0) {
					ll_send.setVisibility(View.GONE);
					tv_send.setVisibility(View.VISIBLE);
					send = 2;
				} else {
					ll_send.setVisibility(View.VISIBLE);
					tv_send.setVisibility(View.GONE);
					send = 1;
				}
			}
		});

		// 粘贴
		tv_content_txt.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_more:
				if("打回".equals(tv_more.getText().toString())) {
					choose = 1;
					final CustomDialog.Builder builder = new CustomDialog.Builder(context);
					if ("zt".equals(userInfo.getExpressNo())) {
						View view = View.inflate(context, R.layout.dialog_order_rejected_zt, null);
						final EditText et_input_reason = (EditText) view.findViewById(R.id.et_input_reason);
						final TextView tv_attention = (TextView) view.findViewById(R.id.tv_attention);
						builder.setTitle("填写打回原因");
						builder.setContentView(view);
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (Utility.isEmpty(et_input_reason.getText().toString())) {
									tv_attention.setVisibility(View.VISIBLE);
								} else {
									refuseOrder(et_input_reason.getText().toString());
									dialog.dismiss();
								}
							}
						});
					} else if ("sto".equals(userInfo.getExpressNo())) {
						View view = View.inflate(context, R.layout.dialog_order_rejected_sto, null);
						final ImageView iv_out_range = (ImageView) view.findViewById(R.id.iv_out_range);
						final ImageView iv_change_time = (ImageView) view.findViewById(R.id.iv_change_time);
						final ImageView iv_cancel_order = (ImageView) view.findViewById(R.id.iv_cancel_order);
						final ImageView iv_other_reason = (ImageView) view.findViewById(R.id.iv_other_reason);
						final RelativeLayout rl_out_range = (RelativeLayout) view.findViewById(R.id.rl_out_range);
						final RelativeLayout rl_change_time = (RelativeLayout) view.findViewById(R.id.rl_change_time);
						final RelativeLayout rl_cancel_order = (RelativeLayout) view.findViewById(R.id.rl_cancel_order);
						final RelativeLayout rl_other_reason = (RelativeLayout) view.findViewById(R.id.rl_other_reason);
						builder.setTitle("选择打回原因");
						builder.setContentView(view);
						rl_out_range.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								iv_out_range.setImageResource(R.drawable.batch_add_checked);
								iv_change_time.setImageResource(R.drawable.select_edit_identity);
								iv_cancel_order.setImageResource(R.drawable.select_edit_identity);
								iv_other_reason.setImageResource(R.drawable.select_edit_identity);
								choose = 1;
							}
						});
						rl_change_time.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								iv_out_range.setImageResource(R.drawable.select_edit_identity);
								iv_change_time.setImageResource(R.drawable.batch_add_checked);
								iv_cancel_order.setImageResource(R.drawable.select_edit_identity);
								iv_other_reason.setImageResource(R.drawable.select_edit_identity);
								choose = 2;
							}
						});
						rl_cancel_order.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								iv_out_range.setImageResource(R.drawable.select_edit_identity);
								iv_change_time.setImageResource(R.drawable.select_edit_identity);
								iv_cancel_order.setImageResource(R.drawable.batch_add_checked);
								iv_other_reason.setImageResource(R.drawable.select_edit_identity);
								choose = 3;
							}
						});
						rl_other_reason.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								iv_out_range.setImageResource(R.drawable.select_edit_identity);
								iv_change_time.setImageResource(R.drawable.select_edit_identity);
								iv_cancel_order.setImageResource(R.drawable.select_edit_identity);
								iv_other_reason.setImageResource(R.drawable.batch_add_checked);
								choose = 4;
							}
						});
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								refuseOrder("" + choose);
								dialog.dismiss();
							}
						});
					}
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}else if("底单".equals(tv_more.getText().toString())){
					Intent intent = new Intent(context, OrderNotifyImgActivity.class);
					intent.putExtra("image", order.getCertificatePath());
					intent.putExtra("delivery", order.getDeliverNo());
					startActivity(intent);
				}
				break;
			case R.id.btn_reply_customer:
				flag = 1;
				// rl_bottom.setVisibility(View.VISIBLE);
				// rl_bottom_reply.setVisibility(View.GONE);
				rl_order_bottom.setVisibility(View.VISIBLE);
				view_line.setVisibility(View.VISIBLE);
				rl_bottom_txt.setVisibility(View.VISIBLE);
				rl_bottom_yuyin.setVisibility(View.GONE);
				btn_reply_customer.setTextColor(context.getResources().getColor(R.color.title_bg));
				btn_reply_server.setTextColor(context.getResources().getColor(R.color.text_point));
				break;
			case R.id.btn_reply_server:
				rl_order_bottom.setVisibility(View.VISIBLE);
				view_line.setVisibility(View.VISIBLE);
				rl_bottom_txt.setVisibility(View.VISIBLE);
				flag = 2;
				rl_bottom_yuyin.setVisibility(View.GONE);
				btn_reply_server.setTextColor(context.getResources().getColor(R.color.title_bg));
				btn_reply_customer.setTextColor(context.getResources().getColor(R.color.text_point));
				break;
			case R.id.btn_record_voice:
				rl_bottom_txt.setVisibility(View.GONE);
				rl_bottom_yuyin.setVisibility(View.VISIBLE);
				btn_record_voice.setVisibility(View.VISIBLE);
				break;
			case R.id.imb_input_txt:
				rl_bottom_txt.setVisibility(View.VISIBLE);
				rl_bottom_yuyin.setVisibility(View.GONE);
				// btn_record_voice.setVisibility(View.GONE);
				break;
			case R.id.ll_send:
				setPop(v);
				// rl_order_bottom.setVisibility(View.GONE);
				// view_line.setVisibility(View.GONE);
				// rl_bottom_txt.setVisibility(View.GONE);
				break;
			case R.id.tv_send:
				String content = tv_content_txt.getText().toString();
				UMShareManager.onEvent(context, "order_detail_reply", "order_detail", "订单详情：回复客户");
				if (flag == 1) {
					replyType = Constants.TYPE_REPLY_CUSTOMER;
					KuaidiApi.getOrderImAdd(context, handler, orderNo, 1, content, 0, replyType);
					// TODO 暂时调用两次接口、因为每次发送后都要对界面进行刷新
					new Handler().postDelayed(new Runnable() {
						public void run() {
							KuaidiApi.getOrderImDetail(context, handler, orderNo);
						}
					}, 200);
				} else if (flag == 2) {
					replyType = Constants.TYPE_REPLY_SERVER;
					KuaidiApi.getOrderImAdd(context, handler, orderNo, 1, content, 0, replyType);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							KuaidiApi.getOrderImDetail(context, handler, orderNo);
						}
					}, 200);

				}

				tv_content_txt.setText("");

				btn_reply_server.setTextColor(context.getResources().getColor(R.color.gray_bg));
				btn_reply_customer.setTextColor(context.getResources().getColor(R.color.gray_bg));
				ll_send.setVisibility(View.VISIBLE);
				// 隐藏软键盘
				Utility.hideSoftInput(context);

				break;
		}
	}

	private void getDedail() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "order.imDetail");
			data.put("user_id", userInfo.getUserId());
			data.put("user_role", USER_ROLE_COURIER);
			data.put("session_type", SESSION_TYPE_COURIERANDCUSTOMER);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	private void setData() {
		Intent intent = getIntent();
		flagIsRun = intent.getBooleanExtra("FlagIsRun", false);
		orderIms = new ArrayList<OrderIm>();
		showProgressDialog("");//OrderInfoActivity.this,"");
		getOrderDetail(orderNo);
	}

	private void getOrderDetail(String orderId){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", GET_ORDER_INFO_DETAIL);
			data.put("orderNumber", orderId);
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打回订单
	 *
	 * @param msg 打回原因
	 */
	private void refuseOrder(String msg) {
		showProgressDialog( "上传数据...");
		JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			data.put("sname", REFUSE_ORDER);
			data.put("courierId", userInfo.getUserId());
			data.put("brand", userInfo.getExpressNo());
			JSONObject obj = new JSONObject();
			obj.put("orderNo", order.getId());
			obj.put("thirdOrderNo", thirdOrderNo);
			obj.put("refuseMsg", msg);
			array.put(obj);
			data.put("orders", array);
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getControl() {
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		tv_more.setVisibility(View.GONE);
		iv_check = (ImageView) findViewById(R.id.iv_check);
		rl_right_question = (RelativeLayout) findViewById(R.id.rl_right_question);
		tv_intent_select = (TextView) findViewById(R.id.tv_intent_select);
		// imv_phone_icon = (ImageView) findViewById(R.id.iv_order_phone);
		// imv_phone_icon.setBackgroundResource(R.drawable.icon_phone_white);
		tv_orderinfo_title = (TextView) findViewById(R.id.tv_title_des);
		tv_orderinfo_title.setText("订单详情");
		tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
		// btn_fast_reply = (Button) findViewById(R.id.btn_fast_reply);
		lv_order_im = (ListView) findViewById(R.id.lv_order_im);
		btn_record_voice = (Button) findViewById(R.id.imb_input_voice);
		rl_orderinfo_layout = (RelativeLayout) findViewById(R.id.rl_orderinfo_layout);
		btn_reply_customer = (Button) findViewById(R.id.btn_reply_customer);
		btn_reply_server = (Button) findViewById(R.id.btn_reply_server);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		rl_bottom_reply = (RelativeLayout) findViewById(R.id.rl_bottom_reply);
		rl_bottom_reply.setVisibility(View.GONE);
		rl_bottom_txt = (RelativeLayout) findViewById(R.id.rl_bottom_txt);
		rl_order_bottom = (RelativeLayout) findViewById(R.id.rl_order_bottom);
		view_line = findViewById(R.id.view_line);
		imb_send = (TextView) findViewById(R.id.imb_send);
		iv_huifu_jiantou = (ImageView) findViewById(R.id.iv_huifu_jiantou);
		ll_send = (LinearLayout) findViewById(R.id.ll_send);
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_content_txt = (TextView) findViewById(R.id.tv_content_txt);
		rl_bottom_yuyin = (RelativeLayout) findViewById(R.id.rl_bottom_yuyin);
		btn_record_voice1 = (TextView) findViewById(R.id.btn_record_voice1);
		imb_input_voice = findViewById(R.id.imb_input_voice);
		imb_input_txt = (ImageButton) findViewById(R.id.imb_input_txt);
		order_pull_refresh = (PullToRefreshView) findViewById(R.id.order_pull_refresh);
		order_pull_refresh.disableScroolUp();
		// rl_express_gz = (RelativeLayout) findViewById(R.id.rl_express_gz);
		// tv_express_gz = (TextView) findViewById(R.id.tv_express_gz);

		rl_order_bottom.setVisibility(View.GONE);
		view_line.setVisibility(View.VISIBLE);
		rl_bottom_txt.setVisibility(View.VISIBLE);

		if ("sto".equals(userInfo.getExpressNo())) {
			ll_send.setBackgroundResource(R.drawable.sto_selector_send_bt);
			tv_send.setBackgroundResource(R.drawable.sto_selector_send_bt);
			btn_record_voice1.setBackgroundResource(R.drawable.sto_selector_send_bt);
			iv_huifu_jiantou.setBackgroundResource(R.drawable.sto_checked_down);
			imb_input_txt.setBackgroundResource(R.drawable.sto_selector_imb_txt);
			imb_input_voice.setBackgroundResource(R.drawable.sto_selector_imb_yuyin);
		}else{
			ll_send.setBackgroundResource(R.drawable.selector_send_bt);
			tv_send.setBackgroundResource(R.drawable.selector_send_bt);
			btn_record_voice1.setBackgroundResource(R.drawable.selector_send_bt);
			iv_huifu_jiantou.setBackgroundResource(R.drawable.checked_down);
			imb_input_txt.setBackgroundResource(R.drawable.selector_imb_txt);
			imb_input_voice.setBackgroundResource(R.drawable.selector_imb_yuyin);
		}

		imb_send.setTextColor(getResources().getColorStateList(SkuaidiSkinManager.getSkinResId("selector_green_white")));
		btn_record_voice1.setTextColor(getResources().getColorStateList(
				SkuaidiSkinManager.getSkinResId("selector_green_white")));
		tv_send.setTextColor(getResources().getColorStateList(SkuaidiSkinManager.getSkinResId("selector_green_white")));

		/*
		 * // 电话按钮 imv_phone_icon.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Uri uri = Uri.parse("tel:" +
		 * tv_phone_number.getText().toString()); Intent intent = new
		 * Intent(Intent.ACTION_CALL, uri); startActivity(intent);
		 * 
		 * UMShareManager.onEvent(context, "order_detail_phoneCall",
		 * "order_detail", "订单详情：打电话"); Message msg = new Message(); msg.what =
		 * Constants.PHONE_CALL; msg.obj = order.getPhone();
		 * 
		 * handler.sendMessage(msg);
		 * 
		 * } });
		 */
	}

	public void getQrCode(View view) {
		UMShareManager.onEvent(context, "order_detail_backfillNum", "order_detail", "订单详情：回填单号");
		Intent intent = new Intent(context, CaptureActivity.class);
		startActivityForResult(intent, Constants.REQUEST_QRCODE);
	}

	public void setPop(View v) {
		if (flag == 1) {
			Intent intent = new Intent(context, FastReplyActivity.class);
			replyType = Constants.TYPE_REPLY_CUSTOMER;
			intent.putExtra("replytype", replyType);
			startActivityForResult(intent, Constants.REQUEST_CHOOSE_FAST_REPLY);
		} else if (flag == 2) {
			Intent intent = new Intent(context, FastReplyActivity.class);
			replyType = Constants.TYPE_REPLY_SERVER;
			intent.putExtra("replytype", replyType);
			startActivityForResult(intent, Constants.REQUEST_CHOOSE_FAST_REPLY);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SkuaidiSpf.saveHasSelectModel(context, true);
		if (requestCode == Constants.REQUEST_QRCODE && resultCode == Constants.RESULT_QRCODE) {

			decodestr = data.getStringExtra("decodestr");
			Utility.ShowDialog(context, handler, "添加运单号", "运单号" + decodestr + "？", "确定", "取消", null,
					Constants.ADD_DELIVE_NO);
		} else if (requestCode == 1001 && resultCode == 1002) {

			// 从服务器读取用户存储的短信模板
/*			if (SkuaidiSpf.getHasHasSelectModel(context) == false) {

				JSONObject object = new JSONObject();
				try {
					object.put("sname", "inform.order.sender.template");
					object.put("act", "get");
					httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}*/

			// 判断是否打开**
			// 快件签收自动通知发件人

			// decodestr = data.getStringExtra("decodestr");
			orderDialog = new SkuaidiOrderDialog(context, onclickListener);
			orderDialog.setDiliverNO(decodestr);
			orderDialog.showAtLocation(tv_orderinfo_title, Gravity.CENTER, 0, 0);
			orderDialog.getIvCheck().setClickable(false);

			orderDialog.getIvCheck().setBackgroundResource(SkuaidiSkinManager.getSkinResId("check_select_surl"));
			sender = 1;
			orderDialog.getTvselect().setVisibility(View.GONE);
			orderDialog.getTv_content().setVisibility(View.VISIBLE);
			orderDialog.getTV_Select().setVisibility(View.GONE);

/*			if (SkuaidiSpf.getHasHasSelectModel(context) == true) {// 判断是否选择短信模板
				orderDialog.getIvCheck().setBackgroundResource(SkuaidiSkinManager.getSkinResId("check_select_surl"));
				sender = 1;
				orderDialog.getTvselect().setVisibility(View.GONE);
				orderDialog.getTv_content().setVisibility(View.VISIBLE);
				orderDialog.getTV_Select().setVisibility(View.GONE);
			} else {
				check_num = 1;
				sender = 0;
				orderDialog.getBtn_ok().setEnabled(false);
				orderDialog.getTv_content().setVisibility(View.GONE);
				orderDialog.getTvselect().setVisibility(View.VISIBLE);
				orderDialog.getTV_Select().setVisibility(View.VISIBLE);

			}*/

			orderDialog.getLeftView().setClickable(true);

			// 对话框确定按钮点击
			orderDialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

				@Override
				public void onClick() {
					UMShareManager.onEvent(context, "orderinfo_ok", "orderinfo", "回填单号");
					Message msg = new Message();

					msg.what = Constants.ADD_DELIVE_NO;
					msg.obj = sender;
					handler.sendMessage(msg);

				}
			});

			is_check = SkuaidiSpf.getHasNoticeAddressor(context);

		} else if (requestCode == Constants.REQUEST_CHOOSE_FAST_REPLY
				&& resultCode == Constants.RESULT_CHOOSE_FAST_REPLY) {
			reply_txt = data.getStringExtra("reply_content");
			KuaidiApi.getOrderImAdd(context, handler, orderNo, Constants.TYPE_TXT, reply_txt, 0, replyType);
			btn_reply_server.setTextColor(context.getResources().getColor(R.color.gray_bg));
			btn_reply_customer.setTextColor(context.getResources().getColor(R.color.gray_bg));
		}
		// 扫描完的popuwindow
		else if (requestCode == Constants.TYPE_ORDER_ONE && resultCode == Constants.TYPE_ORDER_ONE) {
			// 从服务器读取用户存储的短信模板

			decodestr = data.getStringExtra("decodestr");
			// 从服务器读取用户存储的短信模板
			if (SkuaidiSpf.getHasHasSelectModel(context) == false) {

				JSONObject object = new JSONObject();
				try {
					object.put("sname", "inform.order.sender.template");
					object.put("act", "get");
					httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			showDialog();
		}else if(requestCode == Constants.GET_TRANS_NUMBER && resultCode == 188){
			String trans_num = data.getStringExtra("trans_num");
			order.setDeliverNo(trans_num);
			adapter.notifyDataSetChanged();
		}
	}

	// orderdialog里面的点击事件
	private OnClickListener onclickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
				// 复选框
				case R.id.iv_check:
					check_num += 1;
					if (check_num % 2 == 1) {// 取消自动短信通知
						// 仅回填单号
						is_check = false;
						orderDialog.getIvCheck().setBackgroundResource(R.drawable.check_select_empty);
						orderDialog.getTV_Select().setVisibility(View.GONE);
						orderDialog.getTv_content().setVisibility(View.VISIBLE);
						orderDialog.getTvselect().setVisibility(View.GONE);
						orderDialog.getBtn_ok().setEnabled(true);
						skuaidiDb = SkuaidiDB.getInstanse(context);
						sender = 0;

					} else {// 选择自动短信通知

						is_check = true;
						orderDialog.getIvCheck()
								.setBackgroundResource(SkuaidiSkinManager.getSkinResId("check_select_surl"));
						if (SkuaidiSpf.getHasHasSelectModel(context) == true) {
							sender = 1;
							orderDialog.getTv_content().setVisibility(View.VISIBLE);
							orderDialog.getBtn_ok().setEnabled(true);
							orderDialog.getTvselect().setVisibility(View.GONE);
							orderDialog.getTV_Select().setVisibility(View.GONE);
						} else {

							orderDialog.getBtn_ok().setEnabled(false);
							orderDialog.getTV_Select().setVisibility(View.VISIBLE);
							// orderDialog.getBtn_ok().setBackground(SkuaidiSkinManager.getSkinResId(""));
							orderDialog.getTvselect().setVisibility(View.VISIBLE);
						}

					}

					break;

				// 立即设置模板
				case R.id.tv_intent_select:
					Intent intent = new Intent(context, SelectModeActivity.class);
					intent.putExtra("from_activity", "orderinfo_activity");
					startActivityForResult(intent, 1001);
					orderDialog.dismiss();
					break;

				default:
					break;
			}
		}
	};

	private void showDialog() {

		// decodestr = data.getStringExtra("decodestr");
		orderDialog = new SkuaidiOrderDialog(context, onclickListener);
		orderDialog.setDiliverNO(decodestr);
		orderDialog.showAtLocation(tv_orderinfo_title, Gravity.CENTER, 0, 0);
		orderDialog.getIvCheck().setClickable(false);

		// 判断是否打开**
		// 快件签收自动通知发件人
		is_check = SkuaidiSpf.getHasNoticeAddressor(context);
		if (is_check == true) {

			if (SkuaidiSpf.getHasHasSelectModel(context) == true) {// 判断是否选择短信模板
				sender = 1;
				orderDialog.getTvselect().setVisibility(View.GONE);
				orderDialog.getTv_content().setVisibility(View.VISIBLE);
				orderDialog.getTV_Select().setVisibility(View.GONE);
			} else {
				orderDialog.getBtn_ok().setEnabled(false);
				orderDialog.getTv_content().setVisibility(View.GONE);
				orderDialog.getTvselect().setVisibility(View.VISIBLE);
				orderDialog.getTV_Select().setVisibility(View.VISIBLE);

			}
			orderDialog.getIvCheck().setBackgroundResource(SkuaidiSkinManager.getSkinResId("check_select_surl"));

			orderDialog.getIvCheck().setClickable(true);

		} else {
			orderDialog.getIvCheck().setBackgroundResource(R.drawable.check_select_empty);
			// 开关关闭
			if (SkuaidiSpf.getHasHasSelectModel(context) == true) {

				// 判断是否选择短信模板
				orderDialog.getTV_Select().setVisibility(View.GONE);
				check_num = 1;
				sender = 0;
				orderDialog.getTv_content().setVisibility(View.VISIBLE);
				orderDialog.getIvCheck().setClickable(true);
			} else {
				check_num = 1;
				sender = 0;
				orderDialog.getTv_content().setVisibility(View.VISIBLE);
				orderDialog.getIvCheck().setClickable(true);

				orderDialog.getTV_Select().setVisibility(View.GONE);
			}
		}
		// 对话框确定按钮点击
		orderDialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

			@Override
			public void onClick() {
				UMShareManager.onEvent(context, "orderinfo_ok", "orderinfo", "回填单号");
				Message msg = new Message();

				msg.what = Constants.ADD_DELIVE_NO;
				msg.obj = sender;
				handler.sendMessage(msg);

			}
		});

	}

	// 跳转到 通知发件人的 静态界面
	public void getDetail(View v) {
		Intent intent = new Intent(context, NoticeAddressorActivity.class);
		startActivity(intent);

	}

	public void back(View view) {
		if (flagIsRun) {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("tabid", 0);
			startActivity(intent);
		}else{
			Intent intent = new Intent("back_to_main_with_tab");
			intent.putExtra("tabid", 0);
			setResult(100);
			sendBroadcast(intent);
		}
//		else {
//			Intent intent = new Intent(context, MainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra("tabid", 0);
//			startActivity(intent);
//		}
//		if (MainActivity.isActive) {
//			Intent intent = new Intent("back_to_main_with_tab");
//			if (OrderCenterActivity.fromWhere == 2) {
//				intent.putExtra("tabid", 0);
//			} else {
//				intent.putExtra("tabid", 0);
//			}
//
//			sendBroadcast(intent);
//		}
		finish();
	}

	@Override
	public void onBackPressed() {
		back(null);
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (adapter != null)
			adapter.stopPlayRecord();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		is_check = SkuaidiSpf.getHasNoticeAddressor(context);

		MobclickAgent.onResume(context);
		if (null != adapter) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onRestart() {
		enterPrint = false;
		InputMethodManager inputMethodManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(tv_content_txt.getWindowToken(), 0);
		getOrderDetail(order.getId());
		super.onRestart();
	}

	/**
	 * ------------------------------------录音处理以及视图功能--------------------------
	 **/
	/**
	 * 初始化popupwindow
	 */
	PopupWindow popupWindow;
	ImageView pop_rec_iv;

	public void initPopupWindow() {
		View view = getLayoutInflater().inflate(R.layout.pop_recorder_layout, null);
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pop_rec_iv = (ImageView) view.findViewById(R.id.pop_rec_iv);
		timer = new Timer();
	}

	public void startRecorder() {
		File file = new File(Constants.RECORDER_PATH);
		recorderPath = "rec_" + System.currentTimeMillis() + ".amr";
		audioFile = new File(Constants.RECORDER_PATH, recorderPath);
		if (!file.exists()) {
			file.mkdirs();// 创建储存目录
		}
		mediaRecorder = new MediaRecorder();
		// 麦克风
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 输出格式
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		// 输出文件
		mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
		// 编码格式
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

		try {
			// 创建文件
			audioFile.createNewFile();
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
			UtilToolkit.showToast("未知错误，无法录音！");
		}

		start_time = System.currentTimeMillis();
		// 根据声音的振幅来切换录音图片的效果
		timeTask = new MyTimer();
		// 每隔500毫秒执行一次定时器里的任务
		timer.schedule(timeTask, 0, 500);
	}

	/**
	 * 根据声音的振幅来切换录音图片的效果
	 */
	MyTimer timeTask;
	Timer timer;

	private SkuaidiOrderDialog orderDialog;

	private RelativeLayout rl_express_gz;

	private TextView tv_express_gz;

	private TextView tv_intent_select;

	private TextView tv_phone_number;

	private RelativeLayout rl_left_check;

	private RelativeLayout rl_right_question;

	class MyTimer extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = Constants.MSG_UPDATE_RECORDER;
			handler.sendMessage(message);
		}

	}

	/**
	 * 停止录音
	 */
	private void stopRecorder() {
		if (mediaRecorder != null) {
			try {
				mediaRecorder.stop();
			} catch (IllegalStateException e) {

				e.printStackTrace();
			}
			mediaRecorder.release();
			mediaRecorder = null;
			// toastInfo("录音完毕");
			stop_time = System.currentTimeMillis();

			recorder_time = stop_time - start_time;
			// 录音时间不得小于1s
			if (recorder_time < 1000) {
				UtilToolkit.showToast( "录音时间太短");
				if (audioFile.exists()) {
					audioFile.delete();
				}
			}

			if (timeTask != null) {
				timeTask.cancel();
			}
		}
	}

	// 使用声音等级显示动画效果
	public int getRECImageByamp(int soundsLevel) {
		// 取值范围为0~13
		switch (soundsLevel) {
			case 0:
				return R.drawable.record_animate_01s;
			case 1:
				return R.drawable.record_animate_02s;
			case 2:
				return R.drawable.record_animate_03s;
			case 3:
				return R.drawable.record_animate_04s;
			case 4:
				return R.drawable.record_animate_05s;
			case 5:
				return R.drawable.record_animate_06s;
			case 6:
				return R.drawable.record_animate_07s;
			case 7:
				return R.drawable.record_animate_08s;
			case 8:
				return R.drawable.record_animate_09s;
			case 9:
				return R.drawable.record_animate_10s;
			case 10:
				return R.drawable.record_animate_11s;
			case 11:
				return R.drawable.record_animate_12s;
			case 12:
				return R.drawable.record_animate_13s;
			case 13:
				return R.drawable.record_animate_14s;

		}
		return 0;
	}

	public int getAmplitude() {
		if (mediaRecorder != null) {
			int n = 14 * mediaRecorder.getMaxAmplitude() / 32768;
			return n;
		} else
			return 0;
	}

	private class BackReasonChoiceWindow {

		View parentView, btn_center, cancel, submit, splitLine, splitLine1, backReasonTitle;
		ListView backReason;
		Window window;
		BackReasonAdapter reasonAdapter;

		public BackReasonChoiceWindow() {
			window = new Window(context);
		}

		private class Window extends PopupWindow {
			public Window(Context context) {
				super(context);
				initView();
			}

			@SuppressWarnings("deprecation")
			private void initView() {
				parentView = getLayoutInflater().inflate(R.layout.back_reason_choice_layout, null);
				backReasonTitle = parentView.findViewById(R.id.tv_backreason_title);
				btn_center = parentView.findViewById(R.id.btn_center);
				cancel = parentView.findViewById(R.id.tv_cancel);
				submit = parentView.findViewById(R.id.tv_submit);
				splitLine = parentView.findViewById(R.id.v_splitline);
				splitLine1 = parentView.findViewById(R.id.v_splitline1);
				backReason = (ListView) parentView.findViewById(R.id.lv_backreson);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				int showHeight, showWidth;
				showWidth = showHeight = (int) (wm.getDefaultDisplay().getHeight() * 0.4);
				backReasonTitle.getLayoutParams().width = showWidth;
				backReasonTitle.setLayoutParams(backReasonTitle.getLayoutParams());
				backReason.getLayoutParams().width = showWidth;
				backReason.getLayoutParams().height = showHeight;
				backReason.setLayoutParams(backReason.getLayoutParams());
				btn_center.getLayoutParams().width = showWidth;
				btn_center.setLayoutParams(btn_center.getLayoutParams());
				splitLine.getLayoutParams().width = showWidth;
				splitLine.setLayoutParams(splitLine.getLayoutParams());
				splitLine1.getLayoutParams().width = showWidth;
				splitLine1.setLayoutParams(splitLine1.getLayoutParams());
				reasonAdapter = new BackReasonAdapter();
				backReason.setAdapter(reasonAdapter);
				setListener();
				setWidth(LayoutParams.MATCH_PARENT);
				setHeight(LayoutParams.MATCH_PARENT);
				setContentView(parentView);
				setBackgroundDrawable(new ColorDrawable(0xb0000000));
			}

			private void setListener() {
				parentView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						window.dismiss();
					}
				});
				cancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						window.dismiss();
					}
				});
				submit.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						int backReasonId = BACK_REASONS.get(reasonAdapter.getItem(reasonAdapter.getCheckedIndex()));
						orderBackRequest(backReasonId);
						window.dismiss();
					}
				});
			}

			private void orderBackRequest(int backReasonId) {
				JSONObject data = new JSONObject();
				try {
					data.put("sname", "pda.refuseOrder");
					data.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
					JSONObject pda = new JSONObject();
					CourierReviewInfo info = E3SysManager.getReviewInfo();
					pda.put("opOrgCode", info.getCourierJobNo().subSequence(0, 6));
					pda.put("oderNo", orderNo);
					pda.put("opUserCode", info.getCourierJobNo());
					pda.put("rollbackReason", backReasonId);
					pda.put("dev_imei", SkuaidiTelPhoneManager.getInstanse(context).getPhoneIME());
					pda.put("dev_id", Utility.getOnlyCode());
					data.put("pda", pda);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
			}
		}

		public void show() {
			window.showAtLocation(lv_order_im, Gravity.CENTER, 0, 0);
		}

	}

	private class BackReasonAdapter extends BaseAdapter {

		int checkedIndex = 0;

		@Override
		public int getCount() {
			return BACK_REASONS_TEMP.size();
		}

		@Override
		public String getItem(int position) {
			return BACK_REASONS_TEMP.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(R.layout.back_reason_item, null);
			TextView backReason = (TextView) convertView.findViewById(R.id.tv_back_reason);
			ImageView checkIcon = (ImageView) convertView.findViewById(R.id.iv_back_reason);
			if (position == checkedIndex) {
				checkIcon.setImageResource(R.drawable.select_edit_identity_hovery);
			}
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					checkedIndex = position;
					notifyDataSetChanged();
				}
			});
			backReason.setText(getItem(position));
			return convertView;
		}

		public int getCheckedIndex() {
			return checkedIndex;
		}

	}

	private class BackReasonCallback implements OnOrderCallback {

		@Override
		public void callback() {
			BackReasonChoiceWindow reasonChoiceWindow = new BackReasonChoiceWindow();
			reasonChoiceWindow.show();
		}

	}

	// 请求成功接口回调
	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(sname.equals(GET_ORDER_INFO_DETAIL)){
			KLog.json(json);
			order = new Order();
			order.setId(result.optString("orderNumber"));
			order.setOrder_state_cname(result.optString("status"));
			order.setSenderName(result.optString("senderName"));
			order.setSenderPhone(result.optString(StringUtil.isEmpty("senderPhone")));
			String send_province = StringUtil.isEmpty(result.optString("senderProvince"));
			String send_city = StringUtil.isEmpty(result.optString("senderCity"));
			String send_country = StringUtil.isEmpty(result.optString("senderArea"));
			String send_detail = StringUtil.isEmpty(result.optString("senderAddress"));
			order.setSenderProvince(send_province);
			order.setSenderCity(send_city);
			order.setSenderCountry(send_country);
			order.setSenderDetailAddress(send_detail);
			order.setSenderAddress(send_province + send_city + send_country + send_detail);
			order.setName(result.optString("receiveName"));
			order.setPhone(StringUtil.isEmpty(result.optString("receivePhone")));
			String receipt_province = StringUtil.isEmpty(result.optString("receiveProvince"));
			String receipt_city = StringUtil.isEmpty(result.optString("receiveCity"));
			String receipt_country = StringUtil.isEmpty(result.optString("receiveArea"));
			String receipt_detail = StringUtil.isEmpty(result.optString("receiveAddress"));
			order.setReceiptProvince(receipt_province);
			order.setReceiptCity(receipt_city);
			order.setReceiptCountry(receipt_country);
			order.setReceiptDetailAddress(receipt_detail);
			order.setAddress(receipt_province + receipt_city + receipt_country + receipt_detail);
			order.setArticleInfo(result.optString("goodsType"));
			order.setReal_pay(result.optString("realPay"));
			order.setNeed_pay(result.optString("receiptsUnderCustody"));
			order.setPrice(result.optString("price"));
			order.setCollection_amount(result.optString("collectionAmount"));
			order.setTime(result.optString("createTime"));
			order.setDeliverNo(result.optString("wayBillNo"));
			order.setPs(result.optString("remark"));
			order.setIs_send(result.optString("isSend"));
			order.setCharging_weight(result.optString("chargingWeight"));
//			JsonXmlParser.parseOrderImDetail(context, handler, json, order);
			order.setId(orderNo);
			order.setIsRealName(result.optString("isRealname"));
			order.setCertificatePath(result.optString("certificatePath"));
			order.setIsPrint(result.optString("isPrint"));
			thirdOrderNo = result.optString("thirdPartyOrderId");
			order.setCharacters(result.optString("characters"));
			if ("sto".equals(userInfo.getExpressNo()) && "1".equals(order.getOrder_state_cname())
					&& !Utility.isEmpty(thirdOrderNo)) {
				tv_more.setText("打回");
				tv_more.setVisibility(View.VISIBLE);
			}else if(!TextUtils.isEmpty(order.getCertificatePath())){
				tv_more.setText("底单");
				tv_more.setVisibility(View.VISIBLE);
			}else{
				tv_more.setVisibility(View.GONE);
			}
			dismissProgressDialog();//OrderInfoActivity.this);
			order_pull_refresh.onHeaderRefreshComplete();
			orderIms.clear();
			if (order.getOrders() != null) {
				orderIms.addAll(order.getOrders());
			}

			adapter = new OrderImAdapter(OrderInfoActivity.this, context, handler, order, new BackReasonCallback(), orderIms,
					OrderInfoActivity.this);
			if (orderIms.size() > 4) {
				lv_order_im.setStackFromBottom(true);
			}
			lv_order_im.setAdapter(adapter);
			return;
		}
		// 获取短信内容成功
		else if (sname.equals("inform.order.sender.template") && act.equals("get")) {
			String retStr = result.optString("retStr");
			if (retStr.equals("") || retStr == null) {
				SkuaidiSpf.saveHasSelectModel(context, false);
			} else {
				SkuaidiSpf.saveHasSelectModel(context, true);
			}

		}

		if (sname.equals("sms.send")) {
			UtilToolkit.showToast(message);
			return;
		} else if (GET_ORDER_CHARACTER.equals(sname)) {
			dismissProgressDialog();
			try {
				unregisterReceiver(receiver);
			} catch (Exception e) {
				e.getStackTrace();
			}
			Intent intent = new Intent(context, PrintBillActivity.class);
			intent.putExtra("order", order);
			intent.putExtra("device", printer);
			bigChar = json;
			intent.putExtra("bigChar", bigChar);
			((Activity) context).startActivityForResult(intent, Constants.GET_TRANS_NUMBER);
		} else if (REFUSE_ORDER.equals(sname)) {
			JSONArray array = null;
			try {
				array = new JSONArray(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(Utility.isEmpty(array) || array.length() == 0){
				dismissProgressDialog();
				UtilToolkit.showToast("打回失败！");
				return;
			}else if(array.optJSONObject(0).optBoolean("status")) {
				tv_more.setVisibility(View.GONE);
				getOrderDetail(orderNo);
				dismissProgressDialog();
				UtilToolkit.showToast("打回成功！");
			}else{
				dismissProgressDialog();
				UtilToolkit.showToast(array.optJSONObject(0).optString("msg"));
			}
		} else if (SET_EXPRESS_DELIVERY.equals(sname)) {
			tv_more.setVisibility(View.GONE);
		}else if(ZT_SENDORDER.equals(sname)){
			if(!TextUtils.isEmpty(result.optString("waybillNo"))){
				getOrderDetail(orderNo);
				dismissProgressDialog();
				UtilToolkit.showToast("录单成功！");
			}else{
				UtilToolkit.showToast("录单失败！");
				dismissProgressDialog();
			}
		}
		try {
			JSONObject response = result.getJSONObject("response");
			JSONObject body = response.getJSONObject("body");
			String status = body.get("status").toString();
			Message msg = new Message();

			if (status.equals("success")) {
				msg.what = Constants.SUCCESS;
			} else {
				msg.what = Constants.FAILED;
			}
			handler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		order_pull_refresh.onHeaderRefreshComplete();
		dismissProgressDialog();
		UtilToolkit.showToast(result);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if (code.equals("7") && null != result) {
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void fuction(View v) {
		if (v.getId() == R.id.tv_edit) {
			SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(this);
			dialog.setTitleGray("发短信通知客户填写信息");
			dialog.setTitleColor(R.color.title_bg);
			dialog.setContentGray("点击发送后将发送一条短信给\n发件人，完善收发信息");
			dialog.setPositionButtonTextGray("发送");
			dialog.setPositionButtonClickListenerGray(new DialogSendBTNListener());
			dialog.showDialogGray(v);
		}
	}

	private class DialogSendBTNListener implements SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray {
		@Override
		public void onClick(View v) {
			if (!Utility.isNetworkConnected()) {
				UtilToolkit.showToast("当前网络未连接");
				return;
			}
			sendSMS();
		}
	}

	private void sendSMS() {
		JSONObject object = new JSONObject();
		try {
			object.put("sname", "sms.send");
			object.put("order_number", order.getId());
			object.put("mobile", order.getSenderPhone());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(object, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			unregisterReceiver(receiver);
		}catch (Exception e) {
			KLog.i("tag","广播已经注销");
		}
		DownloadTask.stopPlayLocalVoice();
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_SET_MONEY.equals(action)) {
				String money = intent.getStringExtra("money");
				order.setReal_pay(money);
				adapter.notifyDataSetChanged();
			}else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				printer = device;

				KLog.i("tag", "**************"+device.getBluetoothClass().getMajorDeviceClass()+"**********");
				if(printer.getBluetoothClass().getMajorDeviceClass() ==1536) {
					String expressNo = userInfo.getExpressNo();
					if (!Utility.isEmpty(device) && !Utility.isEmpty(device.getName()) && device.getName().startsWith("QR")) {
						btAdapter.cancelDiscovery();
						myPrinter = PrinterManager.getPrinter(printer, null, null);
						SKuaidiApplication.device = printer;
						printerConnect();
					} else if (!Utility.isEmpty(device) && !Utility.isEmpty(device.getName()) && device.getName().startsWith("XT")) {
						btAdapter.cancelDiscovery();
						if ("sto".equals(expressNo)) {
							UtilToolkit.showToast("申通暂不支持芝柯打印机");
							dismissProgressDialog();
							try {
								unregisterReceiver(receiver);
							} catch (Exception e) {
								e.getStackTrace();
							}
							return;
						}
						myPrinter = PrinterManager.getPrinter(printer, null, null);
						SKuaidiApplication.device = printer;
						printerConnect();
					} else if (!Utility.isEmpty(device) && !Utility.isEmpty(device.getName()) && device.getName().startsWith("KM")) {
						btAdapter.cancelDiscovery();
						myPrinter = PrinterManager.getPrinter(printer, null, null);
						SKuaidiApplication.device = printer;
						printerConnect();
					}else if (!Utility.isEmpty(device) && !Utility.isEmpty(device.getName()) && device.getName().startsWith("L3")) {
						if ("sto".equals(expressNo)) {
							UtilToolkit.showToast("申通暂不支持万印和打印机");
							dismissProgressDialog();
							try {
								unregisterReceiver(receiver);
							} catch (Exception e) {
								e.getStackTrace();
							}
							return;
						}
						myPrinter = PrinterManager.getPrinter(printer, OrderInfoActivity.this, handler);
						SKuaidiApplication.device = printer;
						printerConnect();
					} else if (!Utility.isEmpty(device) && !Utility.isEmpty(device.getName()) && device.getName().startsWith("JLP")) {
						btAdapter.cancelDiscovery();
						if ("sto".equals(expressNo)) {
							UtilToolkit.showToast("申通暂不支持济强打印机");
							dismissProgressDialog();
							try {
								unregisterReceiver(receiver);
							} catch (Exception e) {
								e.getStackTrace();
							}
							return;
						}
						myPrinter = PrinterManager.getPrinter(printer, null, null);
						SKuaidiApplication.device = printer;
						printerConnect();
					}
				}
				// 搜索完成
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				if(Utility.isEmpty(printer) || Utility.isEmpty(myPrinter)){
					UtilToolkit.showToast( "没有发现蓝牙打印机设备");
					dismissProgressDialog();
				}else if(!Utility.isEmpty(myPrinter) && !myPrinter.isConnected()){
					printerConnect();
				}
//				unregisterReceiver(receiver);
			}
		}

	}

	/**
	 * 中通录单
	 */
	private void ztSendOrder(){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", ZT_SENDORDER);
			data.put("orderNumber", order.getId());
			boolean hasnotice = SkuaidiSpf.getHasNoticeAddressor(context);
			data.put("shipperInform", hasnotice ? "1" : "0");
			data.put("waybillNo", order.getDeliverNo());
			data.put("collectionAmount", order.getCollection_amount());
			data.put("chargingWeight", order.getCharging_weight());
			data.put("isSendRet", "1");
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取大字
	 */
	private void getBigChar(){
//		if("zt".equals(userInfo.getExpressNo())){
//			JSONObject data = new JSONObject();
//			try {
//				data.put("sname", GET_ORDER_CHARACTER);
//				data.put("zto_partner", SkuaidiSpf.getCusSimpleName());
//				data.put("zto_pass", SkuaidiSpf.getOrderPwd());
//				data.put("receiveprovince", order.getReceiptProvince());
//				data.put("receivecity", order.getReceiptCity());
//				data.put("receivearea", order.getReceiptCountry());
//				data.put("receiveraddress", order.getReceiptDetailAddress());
//				data.put("brand", "zt");
//				data.put("sendprovince", order.getSenderProvince());
//				data.put("sendcity", order.getSenderCity());
//				data.put("sendarea", order.getSenderCountry());
//				data.put("sendaddress", order.getSenderDetailAddress());
//				httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		Intent intent;
		if("sto".equals(userInfo.getExpressNo())){
			JSONObject data = new JSONObject();
			try {
				data.put("sname", GET_ORDER_CHARACTER);
				data.put("brand", "sto");
				data.put("shopName", SkuaidiSpf.getWangAddress());
				data.put("account", SkuaidiSpf.getCusSimpleName());
				data.put("cuspwd", SkuaidiSpf.getOrderPwd());
				data.put("receiveprovince", order.getReceiptProvince());
				data.put("receivecity", order.getReceiptCity());
				data.put("receivearea", order.getReceiptCountry());
				httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if("zt".equals(userInfo.getExpressNo())) {
			dismissProgressDialog();
			try {
				unregisterReceiver(receiver);
			} catch (Exception e) {
				e.getStackTrace();
			}
			intent = new Intent(context, PrintBillActivity.class);
			intent.putExtra("order", order);
			intent.putExtra("device", printer);
			((Activity) context).startActivityForResult(intent, Constants.GET_TRANS_NUMBER);
		}else{
			dismissProgressDialog();
			if (null != order) {
				bigChar = order.getReceiptProvince() + order.getReceiptCity() + order.getReceiptCountry();
			}
			intent = new Intent(context, PrintExpressBillActivity.class);
			intent.putExtra("order", order);
			intent.putExtra("device", printer);
			intent.putExtra("bigChar", bigChar);
			((Activity) context).startActivityForResult(intent, Constants.GET_TRANS_NUMBER);
		}
	}

	private void printerConnect(){

		myPrinter.connect(printer, new PrinterBase.ConnectedCallBack() {
			@Override
			public void connectedCallback() {
				btAdapter.cancelDiscovery();
				if(btAdapter.isEnabled() && !Utility.isEmpty(printer) && !Utility.isEmpty(myPrinter)
						&& myPrinter.isConnected()) {
					if (!enterPrint) {
						UtilToolkit.showToast("已成功连接打印机:" + printer.getName());
						enterPrint = true;
						getBigChar();
					}
				}else{
					myPrinter = null;
					SKuaidiApplication.device = null;
					dismissProgressDialog();
					UtilToolkit.showToast("打印机连接失败，请重试!");
				}
			}
		});
	}

}
