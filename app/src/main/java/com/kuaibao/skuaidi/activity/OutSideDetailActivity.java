package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.activity.adapter.CircleExpressItemImageAdapter;
import com.kuaibao.skuaidi.activity.view.CustomBaseGridView;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.NegativeButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
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
 * 外单详情页
 * 
 * @author gudd
 * 
 */
public class OutSideDetailActivity extends SkuaiDiBaseActivity {

	private Context context;
	private ScrollView scrollView;
	private RelativeLayout rl_send_thing;// 送货地址模块
	private RelativeLayout rl_send_receive_distance;// 发货地址与收货地址之间的距离
	private RelativeLayout rl_distance;// 距离自己的距离长度
	private RelativeLayout rl_finish;// 完成状态模块
	private ImageView iv_title_back;// 返回按钮
	private ImageView iv_phone_icon;// 拨打电话按钮
	private TextView tv_send_tag;// 发货
	private TextView tv_receive;// 收货
	private TextView tv_send;// 需求
	private TextView tv_phone;// 显示手机号
	private TextView tv_income;// 跑腿收入
	private TextView tv_immediately;// 立即抢单按钮
	private TextView tv_send_address;// 发货地址
	private TextView tv_send_receive_distance;// 发货地址与收货地址之间距离控件
	private TextView tv_receipt_address;// 收货地址
	private TextView tv_need;// 需求内容
	private TextView tv_money;// 垫付金额
	private TextView tv_whether_advance;// 是否需要垫付说明
	private TextView tv_distance;// 距离
	private TextView tv_time;// 时间
	private TextView tv_peisong_explain;// 配送说明
	private TextView tv_paotui_explain;// 跑腿费说明
	private CustomBaseGridView gridView;
	private ImageView only_one_image;// 只有一张图片的时候显示
	private RelativeLayout rl_show_image;// 图片显示模块

	private String outSideBlockID;// 外单ID
	private String customer_mobile;// 客户手机号
	private String income;// 跑腿收入
	private String send_add;// 发货地址
	private String receipt_add;// 收货地址
	private String demand;// 需求
	private String pic;// 图片
	private String advance_money;// 垫付金额
	private String advance_status;// 是否需要垫付状态（y/n）
	private String send_receive_distance;// 收货与发货之间的距离
	private String distance;// 距离
	private String time;// 时间
	private String[] peisong_explain = null;// 配送说明
	private String[] run_errands_fee_explain = null;// 跑腿费说明
	private String peisong_str = "";// 配送说明
	private String paotui_str = "";// 跑腿说明
	private List<LatestOutSide> latestOutSides;
	private List<LatestOutSide> latestOutSides2;
	private LatestOutSide latestOutSide;
	private int position;
	private String fromActivity;

	private ArrayList<String> imageUrls;
	private ArrayList<String> imageUrlsbig;

	private Intent mIntent;
	// private SkuaidiDialog dialog;
	private SkuaidiDialogGrayStyle dialog_grayStyle;
	private String deal;

	// 推送相关
	private String weirenwu_id;
	private final int PUSH_INFORMATION_SUCCESS = 198;
	private final int PUSH_INFORMATION_FAIL = 199;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.ROB_OUTSIDE_SUCCESS:// 抢单成功
				dismissProgressDialog();
				UtilToolkit.showToast("成功抢到一单");
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication.getInstance().onReceiveMsg("LastTakeOutFragment","OutSideDetailActivity");
				latestOutSides.get(position).setState("wait");
				SKuaidiApplication.getInstance().postMsg("OutSideDetailActivity", "latestTakeOutFragment",latestOutSides);
				mIntent = new Intent();
				mIntent.putExtra("outsideoutsideid", outSideBlockID);
				setResult(2, mIntent);
				finish();
				break;
			case Constants.ROB_OUTSIDE_FAIL:// 抢单失败
				dismissProgressDialog();
				UtilToolkit.showToast("您没有抢到哦...");
				break;
			case Constants.RELEASE_OUTSIDE_SUCCESS:// 释放外单成功
				dismissProgressDialog();
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication.getInstance().onReceiveMsg("myTakeOutFragment","OutSideDetailActivity");
				latestOutSides.get(position).setState("pickup");
				SKuaidiApplication.getInstance().postMsg(
						"OutSideDetailActivity", "myTakeOutFragment",
						latestOutSides);
				mIntent = new Intent();
				mIntent.putExtra("outsideoutsideid", outSideBlockID);
				setResult(Constants.RESULT_CODE, mIntent);
				finish();
				break;
			case Constants.RELEASE_OUTSIDE_FAIL:// 释放外单失败
				UtilToolkit.showToast("放弃失败");
				break;
			case Constants.OUTSIDE_GET_SUCCESS:// 获取外单详情成功
				dismissProgressDialog();
				latestOutSides2 = (List<LatestOutSide>) msg.obj;
				latestOutSide = new LatestOutSide();
				latestOutSide = latestOutSides2.get(0);
				getAndSetData();// 获取并设置数据
				break;
			case Constants.OUTSIDE_GET_DATAISNULL:// 未获取到数据
				 UtilToolkit.showToast("数据获取失败");
				break;
			case PUSH_INFORMATION_SUCCESS:
				dismissProgressDialog();
				UtilToolkit.showToast("成功抢到一单");
				finish();
				break;
			case PUSH_INFORMATION_FAIL:
				dismissProgressDialog();
				UtilToolkit.showToast("您没有抢到哦...");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.outside_block_takeaway_detail);
		context = this;
		initView();
		initData();
		setListener();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tv_send_tag = (TextView) findViewById(R.id.tv_send_tag);
		tv_receive = (TextView) findViewById(R.id.tv_receive);
		tv_send = (TextView) findViewById(R.id.tv_send);
		// 设置不同公司区默认时候的背景
		tv_send_tag.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
		tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
		tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_whitebg_colorworld"));
		tv_send.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		rl_send_thing = (RelativeLayout) findViewById(R.id.rl_send_thing);
		rl_send_receive_distance = (RelativeLayout) findViewById(R.id.rl_send_receive_distance);
		rl_distance = (RelativeLayout) findViewById(R.id.rl_distance);
		rl_finish = (RelativeLayout) findViewById(R.id.rl_finish);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_phone_icon = (ImageView) findViewById(R.id.iv_phone_icon);
		// 设置不同公司区默认时候的背景
		iv_phone_icon.setBackgroundResource(SkuaidiSkinManager.getSkinResId("outside_block_call_phone_icon"));
		
		
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_income = (TextView) findViewById(R.id.tv_income);
		tv_immediately = (TextView) findViewById(R.id.tv_immediately);
		tv_send_receive_distance = (TextView) findViewById(R.id.tv_send_receive_distance);
		tv_send_address = (TextView) findViewById(R.id.tv_send_address);
		tv_receipt_address = (TextView) findViewById(R.id.tv_receipt_address);
		tv_need = (TextView) findViewById(R.id.tv_need);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_whether_advance = (TextView) findViewById(R.id.tv_whether_advance);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_peisong_explain = (TextView) findViewById(R.id.tv_peisong_explain);
		tv_paotui_explain = (TextView) findViewById(R.id.tv_paotui_explain);
		gridView = (CustomBaseGridView) findViewById(R.id.show_image);
		only_one_image = (ImageView) findViewById(R.id.only_one_image);
		rl_show_image = (RelativeLayout) findViewById(R.id.rl_show_image);

		iv_phone_icon.setOnClickListener(new MyOnClickListener());
		iv_title_back.setOnClickListener(new MyOnClickListener());
		tv_immediately.setOnClickListener(new MyOnClickListener());
	}

	/**
	 * 初始化界面数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		if (getIntent().getStringExtra("weirenwu_id") != null) {
			weirenwu_id = getIntent().getStringExtra("weirenwu_id");
			JSONObject data = new JSONObject();
			deal = "get";
			try {
				data.put("sname", "weirenwu_s");
				data.put("pname", "androids");
				data.put("id", weirenwu_id);
				data.put("deal", deal);
				data.put("lat", SkuaidiSpf.getLatitudeOrLongitude(context)
						.getLatitude());
				data.put("lng", SkuaidiSpf.getLatitudeOrLongitude(context)
						.getLongitude());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
			showProgressDialog( "拼命为您加载中...");
		} else {

			// 接收数据
			fromActivity = getIntent().getStringExtra("fromActivity");
			if (fromActivity.equals("latestTakeOutFragment")) {
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication.getInstance().onReceiveMsg("LastTakeOutFragment","OutSideDetailActivity");
				position = getIntent().getIntExtra("position", -1);
			} else if (fromActivity.equals("myTakeOutFragment")) {
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication.getInstance().onReceiveMsg("myTakeOutFragment","OutSideDetailActivity");
				position = getIntent().getIntExtra("position", -1);
				position = position - 2;
			} else if (fromActivity.equals("FinishOutSideBlockActivity")) {
				latestOutSides = (List<LatestOutSide>) SKuaidiApplication.getInstance().onReceiveMsg("FinishOutSideBlockActivity","OutSideDetailActivity");
				position = getIntent().getIntExtra("position", -1);
				tv_immediately.setVisibility(View.GONE);// 隐藏立即抢单按钮
				rl_finish.setVisibility(View.VISIBLE);// 显示已完成提示
			}
			latestOutSide = new LatestOutSide();
			latestOutSide = latestOutSides.get(position);
			// 设置按钮背景
			if (fromActivity.equals("latestTakeOutFragment")) {
				tv_immediately
						.setBackgroundResource(R.drawable.selector_btn_red);
			} else if (fromActivity.equals("myTakeOutFragment")) {
				tv_immediately.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_btn_big_title_bg"));
//				tv_immediately.setBackgroundResource(context.getResources().getDrawable(R.drawable.sto_selector_btn_orange_radius));
				tv_immediately.setText("放弃任务");
			}
			getAndSetData();// 获取并设置数据
		}
	}

	private void getAndSetData() {
		// 获取数据
		outSideBlockID = latestOutSide.getId();
		customer_mobile = latestOutSide.getUser_mobile();
		income = latestOutSide.getPay();// 收入金额
		pic = latestOutSide.getPic();// 获取图片
		send_add = latestOutSide.getSend();
		receipt_add = latestOutSide.getReceive();
		demand = latestOutSide.getMission();
		advance_money = latestOutSide.getReward();// 垫付金额
		advance_status = latestOutSide.getPay_first();// 是否需要垫付
		distance = latestOutSide.getDistance();// 距离
		send_receive_distance = latestOutSide.getSend_receive_distance();// 收货与发货之间的距离
		time = latestOutSide.getLimit_time();
		peisong_explain = latestOutSide.getPay_explain();// 配送说明
		run_errands_fee_explain = latestOutSide.getDelivery_explain();// 跑腿费说明

		tv_phone.setText(customer_mobile);
		tv_income.setText(income);// 设置跑腿收入金额
		if (!send_add.equals("")) {
			if (!send_add.equals("null")) {
				tv_send_address.setText(send_add);
			} else {
				tv_send_address.setText("老板太坏了，连发货地址都不给我...");
				tv_send_address.setTextColor(context.getResources().getColor(
						color.gray_3));
			}
			if (!send_receive_distance.equals("")
					&& !send_receive_distance.equals("0")) {
				rl_send_receive_distance.setVisibility(View.VISIBLE);
				tv_send_receive_distance.setText("相距"
						+ Utility.formatDistance(send_receive_distance));
			} else if (!distance.equals("") && !distance.equals("0")) {
				rl_send_receive_distance.setVisibility(View.VISIBLE);
				tv_send_receive_distance.setText("相距"
						+ Utility.formatDistance(distance));
			}
		} else {
			rl_send_thing.setVisibility(View.GONE);
			rl_send_receive_distance.setVisibility(View.GONE);
		}
		tv_receipt_address.setText(receipt_add);
		tv_need.setText(demand);
		if (!advance_money.equals("")) {
			tv_money.setText(advance_money);// 设置需要垫付的金额
		} else {
			tv_money.setVisibility(View.GONE);
			tv_whether_advance.setVisibility(View.GONE);
		}
		if (advance_status.equals("y")) {
			tv_whether_advance.setVisibility(View.VISIBLE);
		} else {
			tv_whether_advance.setVisibility(View.GONE);
		}
		if (!distance.equals("")) {
			tv_distance.setText("距您" + Utility.formatDistance(distance));
		} else {
			rl_distance.setVisibility(View.GONE);
		}

		if (getIntent().getStringExtra("weirenwu_id") != null) {
			tv_time.setText(Utility.CalculationSurplusTime(time));// 设置时间-还剩xxx时间
		} else {
			if (fromActivity.equals("latestTakeOutFragment")) {
				tv_time.setText(Utility.CalculationSurplusTime(time));// 设置时间-还剩xxx时间
			} else if (fromActivity.equals("myTakeOutFragment")) {
				tv_time.setText(Utility.CalculationTime(time));// 设置时间-xxx天xx时之前
			} else if (fromActivity.equals("FinishOutSideBlockActivity")) {
				tv_time.setText(Utility.CalculationTime(time));// 设置时间-同上
			}
		}

		for (int i = 0; i < peisong_explain.length; i++) {
			peisong_str = peisong_str + (peisong_explain[i] + "\n");
		}

		for (int i = 0; i < run_errands_fee_explain.length; i++) {
			paotui_str = paotui_str + (run_errands_fee_explain[i] + "\n");
		}

		if (!peisong_str.equals("") && peisong_str.length() >= 1) {
			tv_paotui_explain.setText(peisong_str.substring(0,
					peisong_str.length() - 1));
			tv_peisong_explain.setText(paotui_str.substring(0,
					paotui_str.length() - 1));
		} else {
			tv_paotui_explain.setText("暂无说明");
			tv_peisong_explain.setText("暂无说明");
		}

		imageUrls = new ArrayList<String>();
		imageUrlsbig = new ArrayList<String>();
		// 设置需求图片
		if (pic != null && !pic.equals("") && pic.contains("$%#") == true) {
			rl_show_image.setVisibility(View.VISIBLE);
			String[] arraysmall = pic.split("\\$%#");
			for (int i = 1; i < arraysmall.length; i++) {
				imageUrls.add(Constants.URL_OUTSIDE_IMG + "thumb."
						+ arraysmall[i]);
				imageUrlsbig.add(Constants.URL_OUTSIDE_IMG + arraysmall[i]);
			}
			if (arraysmall.length == 2) {
				gridView.setVisibility(View.GONE);
				only_one_image.setVisibility(View.VISIBLE);
//				DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//						.cacheInMemory(true).cacheOnDisk(true)
//						.bitmapConfig(Config.RGB_565).build();
//				ImageLoader.getInstance().displayImage(imageUrls.get(0),
//						only_one_image, imageOptions);
				GlideUtil.GlideUrlToImg(OutSideDetailActivity.this,imageUrls.get(0), only_one_image);
			} else {
				gridView.setVisibility(View.VISIBLE);
				only_one_image.setVisibility(View.GONE);
				gridView.setAdapter(new CircleExpressItemImageAdapter(context,
						imageUrls));// 将小图url的集合放到adapter里面（此处需要加入完整的url）
			}

		} else {
			rl_show_image.setVisibility(View.GONE);
		}
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:// 返回按钮
				finish();
				break;

			case R.id.tv_immediately:// 立即抢单按钮-释放外单按钮
				if (Utility.isNetworkConnected() == false) {
					UtilToolkit.showToast("请连接网络...");
				} else {
					if (getIntent().getStringExtra("weirenwu_id") != null) {
						UMShareManager.onEvent(context, "outsideDetail_rob_list", "outsideTheBlock", "任务详情：抢任务");
						deal = "pickup";
						JSONObject data = (JSONObject) KuaidiApi
								.robOutsideBlock(deal,
										outSideBlockID);
						httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
						showProgressDialog( "拼命为您抢单中...");
					} else {
						if (fromActivity.equals("latestTakeOutFragment")) {// 立即抢单
							UMShareManager.onEvent(context, "outsideDetail_rob_list", "outsideTheBlock", "任务详情：抢任务");
							deal = "pickup";
							JSONObject data = (JSONObject) KuaidiApi
									.robOutsideBlock(deal,
											outSideBlockID);
							httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
							showProgressDialog( "拼命为您抢单中...");
						} else if (fromActivity.equals("myTakeOutFragment")) {// 释放外单
							UMShareManager.onEvent(context, "outsideDetail_abandon_mission ", "outsideTheBlock", "任务详情：放弃任务");
							dialog_grayStyle = new SkuaidiDialogGrayStyle(
									context);
							dialog_grayStyle.setTitleGray("放弃任务");
							dialog_grayStyle
									.setContentGray("我抢错啦~~~（*+﹏+*）~~~\n把机会让给别的快递员吧~！");
							dialog_grayStyle.setPositionButtonTextGray("确定");
							dialog_grayStyle.setNegativeButtonTextGray("取消");
							dialog_grayStyle
									.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

										@Override
										public void onClick(View v) {
											deal = "wait";
											JSONObject data = (JSONObject) KuaidiApi
													.robOutsideBlock(
															deal,
															outSideBlockID);
											httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
											showProgressDialog(
													"正在放弃任务...");
											tv_immediately.setEnabled(true);
										}
									});
							dialog_grayStyle
									.setNegativeButtonClickListenerGray(new NegativeButtonOnclickListenerGray() {

										@Override
										public void onClick() {
											tv_immediately.setEnabled(true);

										}
									});
							dialog_grayStyle.showDialogGray(tv_immediately);
						}
					}

					tv_immediately.setEnabled(true);
				}
				break;
			case R.id.iv_phone_icon:
				dialog_grayStyle = new SkuaidiDialogGrayStyle(context);
				dialog_grayStyle.setTitleGray("提示");
				dialog_grayStyle.setContentGray("您是否要联系该客户？");
				dialog_grayStyle.setPositionButtonTextGray("确定");
				dialog_grayStyle.setNegativeButtonTextGray("取消");
				dialog_grayStyle
						.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

							@Override
							public void onClick(View v) {
								UMShareManager.onEvent(context, "outsideDetail_call_customer", "outsideTheBlock", "任务详情：拨打电话");
								String tel = tv_phone.getText().toString();
//								Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + tel));
//								startActivity(intent);
								AcitivityTransUtil.showChooseTeleTypeDialog(OutSideDetailActivity.this, "", tel,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
							}
						});
				dialog_grayStyle.showDialogGray(iv_phone_icon);
				break;
			default:
				break;
			}
		}

	}

	private void setListener() {

		// 点击回帖九宫格，查看大图
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				imageBrower(position, imageUrlsbig);// 此处需要完整的url（而这里只拿到了照片名）
			}
		});
		// 只有一张图片的时候点击imageview查看大图
		only_one_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imageBrower(position, imageUrlsbig);// 此处需要完整的url（而这里只拿到了照片名）
			}
		});

	}

	/**
	 * 打开图片查看器
	 * 
	 * @param position
	 * @param urls2
	 */
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		context.startActivity(intent);
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

	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(result!=null){
			Message msg = new Message();
			if (getIntent().getStringExtra("weirenwu_id") != null
					&& deal.equals("get")) {
				JsonXmlParser.parseOutSideList2(context, handler, result);
			} else {
				try {
					String data = result.getString("retStr");
					if (data.equals("true")) {// 抢单成功
						if (getIntent().getStringExtra("weirenwu_id") != null
								&& deal.equals("pickup")) {
							msg.what = PUSH_INFORMATION_SUCCESS;
						} else {
							if (fromActivity.equals("latestTakeOutFragment")) {
								msg.what = Constants.ROB_OUTSIDE_SUCCESS;
							} else if (fromActivity.equals("myTakeOutFragment")) {
								msg.what = Constants.RELEASE_OUTSIDE_SUCCESS;
							}
						}
					} else {
						if (getIntent().getStringExtra("weirenwu_id") != null
								&& deal.equals("pickup")) {
							msg.what = PUSH_INFORMATION_FAIL;
						} else {
							if (fromActivity.equals("latestTakeOutFragment")) {
								msg.what = Constants.ROB_OUTSIDE_FAIL;
							} else if (fromActivity.equals("myTakeOutFragment")) {
								msg.what = Constants.RELEASE_OUTSIDE_FAIL;
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			handler.sendMessage(msg);
			tv_immediately.setEnabled(true);
		}
	}
	
	@Override
	protected void onRequestFail(String code, String sname,String message, String act, JSONObject data_fail) {
		if(!message.equals("")){
			UtilToolkit.showToast(message);
		}
		dismissProgressDialog();
		tv_immediately.setEnabled(true);
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
