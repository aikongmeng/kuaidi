package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CommonWebViewActivity;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.CreateNewOrderActivity;
import com.kuaibao.skuaidi.activity.HuodongDescriptionActivity;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.activity.OrderInfoActivity;
import com.kuaibao.skuaidi.activity.SavePrintPermissionActivity;
import com.kuaibao.skuaidi.activity.UpdateAddressorActivity;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.order.RecordOrderActivity;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.entry.OrderIm;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.service.DownloadTask;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.BarcodeUtils;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.ExpressFirm;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.socks.library.KLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gen.greendao.bean.ICallLog;
import gen.greendao.dao.ICallLogDao;

import static com.kuaibao.skuaidi.R.id.iv_get_realInfo;
import static com.kuaibao.skuaidi.R.id.iv_save_order;
import static com.kuaibao.skuaidi.R.id.iv_sender_call;
import static com.kuaibao.skuaidi.R.id.ll_operation;
import static com.kuaibao.skuaidi.R.id.rl_get_realInfo;
import static com.kuaibao.skuaidi.R.id.rl_money_amont;
import static com.kuaibao.skuaidi.R.id.rl_note_info;
import static com.kuaibao.skuaidi.R.id.rl_save_order;
import static com.kuaibao.skuaidi.R.id.rl_thing;
import static com.kuaibao.skuaidi.R.id.rl_weight;

/**
 * @author xy 订单详情
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class OrderImAdapter extends BaseAdapter {
//	private static String GET_ORDER_CHARACTER = "parter/sto/thermalPaper/getCharacters";
	private OnFuctionBTNClickListener mFuctionBTNClickListener;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
	private OnOrderCallback callback;
	int click_num = 0;
	private Activity mActivity;
	Context context;
	Handler handler;
	Order order;
	List<OrderIm> orderIms;
	OrderIm orderIm;
	LayoutInflater inflater;
	final int TYPE_ORDER_INFO = 0;
	final int TYPE_ORDER_IM = 1;
	private PopupWindow mPopup;
	SelectConditionsListPOP pop;
	private MediaPlayer mp;
	private boolean isPlaying = false;
	@SuppressWarnings("unused")
	private ClipboardManager mClipboard = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private SimpleDateFormat durationTimeFormat = new SimpleDateFormat("HH:mm:ss");

	private PopupWindow mPopupWindow;
	private BluetoothAdapter btAdapter;

	public OrderImAdapter(Activity mActivity, Context context, Handler handler, Order order2, OnOrderCallback callback, List<OrderIm> orderIms, OnFuctionBTNClickListener fuctionBTNClickListener) {
		super();
		this.mActivity = mActivity;
		this.context = context;
		this.handler = handler;
		this.order = order2;
		this.callback = callback;
		this.orderIms = orderIms;
		mFuctionBTNClickListener = fuctionBTNClickListener;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return orderIms.size() + 1;
	}

	public void setDeliverNo(String deliverNo) {
		order.setDeliverNo(deliverNo);
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return order;
		} else {
			return orderIms.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_ORDER_INFO;
		} else {
			return TYPE_ORDER_IM;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private long time;
	// private ViewHolderFirst holderFirst;
	private Timer timer;
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					time = time + 1;
					((TextView) msg.obj).setText(format.format(time * 1000 - 28800000));
					break;

				default:
					break;
			}
		}
	};

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		final ViewHolderFirst holderFirst = new ViewHolderFirst();
		int type = getItemViewType(position);

		// if (convertView == null) {
		switch (type) {
			case TYPE_ORDER_INFO:
				// holderFirst = new ViewHolderFirst();
				convertView = inflater.inflate(R.layout.listitem_orderim_first, parent, false);
				holderFirst.recording_center_view_order = convertView.findViewById(R.id.recording_center_view_order);
				holderFirst.playorpause_order = (TextView) convertView.findViewById(R.id.playorpause_order);
				holderFirst.recording_date_order = (TextView) convertView.findViewById(R.id.recording_date_order);
				holderFirst.recording_palying_duration_order = (TextView) convertView.findViewById(R.id.recording_palying_duration_order);

				holderFirst.rl_save_order = convertView.findViewById(rl_save_order);
				holderFirst.tv_save_order = (TextView) convertView.findViewById(R.id.tv_save_order);
				holderFirst.iv_save_order = convertView.findViewById(iv_save_order);
				holderFirst.view_devider_sorder = convertView.findViewById(R.id.view_devider_sorder);
				holderFirst.rl_get_realInfo = convertView.findViewById(rl_get_realInfo);
				holderFirst.tv_get_realInfo = (TextView) convertView.findViewById(R.id.tv_get_realInfo);
				holderFirst.view_devider_realInfo = convertView.findViewById(R.id.view_devider_sorder);
				holderFirst.iv_get_realInfo = convertView.findViewById(iv_get_realInfo);
				holderFirst.ll_print_express_bill = convertView.findViewById(R.id.ll_print_express_bill);
				holderFirst.ll_edit_orderNo = convertView.findViewById(R.id.ll_edit_orderNo);
				holderFirst.tv_cash_num = (TextView) convertView.findViewById(R.id.tv_cash_num);
				holderFirst.ll_shou_qrcode = convertView.findViewById(R.id.ll_shou_qrcode);
				holderFirst.rl_order_back = convertView.findViewById(R.id.rl_express_back);

				holderFirst.tv_edit = (TextView) convertView.findViewById(R.id.tv_edit);

				holderFirst.tv_sender_name = (TextView) convertView.findViewById(R.id.tv_sender_name);
				holderFirst.tv_sender_address = (TextView) convertView.findViewById(R.id.tv_sender_address);
				holderFirst.tv_sender_phone = (TextView) convertView.findViewById(R.id.tv_sender_phone);
				holderFirst.iv_sender_call = convertView.findViewById(iv_sender_call);
				holderFirst.rl_sender_phone = convertView.findViewById(R.id.rl_sender_phone);
				holderFirst.view_line_below1 = convertView.findViewById(R.id.view_line_below1);
				holderFirst.rl_more = convertView.findViewById(R.id.rl_more);

				holderFirst.tv_shou_name = (TextView) convertView.findViewById(R.id.tv_shou_name);
				holderFirst.tv_shou_address = (TextView) convertView.findViewById(R.id.tv_shou_address);
				holderFirst.tv_shou_phone = (TextView) convertView.findViewById(R.id.tv_shou_phone);
				holderFirst.iv_shou_call = convertView.findViewById(R.id.iv_shou_call);
				holderFirst.rl_shou_pice_content = convertView.findViewById(R.id.rl_shou_pice_content);
				holderFirst.rl_gone = convertView.findViewById(R.id.rl_gone);

				holderFirst.rl_weight = convertView.findViewById(rl_weight);
				holderFirst.tv_weight = (TextView) convertView.findViewById(R.id.tv_weight);
				holderFirst.rl_money_amont = convertView.findViewById(rl_money_amont);
				holderFirst.tv_money_num = (TextView) convertView.findViewById(R.id.tv_money_num);
				holderFirst.rl_note_info = convertView.findViewById(rl_note_info);

				holderFirst.ll_fajian_date = convertView.findViewById(R.id.rl_fajian_date);
				holderFirst.tv_fajian_date = (TextView) convertView.findViewById(R.id.tv_fajian_date);
				holderFirst.rl_thing = convertView.findViewById(rl_thing);
				holderFirst.tv_thing = (TextView) convertView.findViewById(R.id.tv_thing);

				holderFirst.ll_operation = convertView.findViewById(ll_operation);
				holderFirst.tv_shou_qrcode = (TextView) convertView.findViewById(R.id.tv_shou_qrcode);
				holderFirst.iv_help_and_response = convertView.findViewById(R.id.iv_help_and_response);
				holderFirst.tv_input_order_num = (TextView) convertView.findViewById(R.id.tv_input_order_num);
				holderFirst.tv_now_version = (TextView) convertView.findViewById(R.id.tv_now_version);
				holderFirst.tv_print_status = (TextView) convertView.findViewById(R.id.tv_print_status);
				holderFirst.tv_order_use_introduce = (TextView) convertView.findViewById(R.id.tv_order_use_introduce);

//			holderFirst.rl_input_info = convertView.findViewById(R.id.rl_input_info);
				convertView.setTag(holderFirst);
				break;

			case TYPE_ORDER_IM:
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.listitem_exception_detail, parent, false);
				//暂时隐藏
				convertView.setVisibility(View.GONE);
				holder.rl_receive = (RelativeLayout) convertView.findViewById(R.id.rl_receive);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time_receive);
				holder.imv_header_receive = (ImageView) convertView.findViewById(R.id.imv_header_receive);
				holder.rl_content_bg_receive = (RelativeLayout) convertView.findViewById(R.id.rl_content_bg_receive);
				holder.tv_content_receive = (TextView) convertView.findViewById(R.id.tv_content_receive);
				holder.imv_voice_content_receive = (ImageView) convertView.findViewById(R.id.imv_voice_content_receive);
				holder.tv_record_time_receive = (TextView) convertView.findViewById(R.id.tv_record_time_receive);
				holder.tv_user_info = (TextView) convertView.findViewById(R.id.tv_user_info);

				holder.rl_send = (RelativeLayout) convertView.findViewById(R.id.rl_send);
				holder.imv_header_send = (ImageView) convertView.findViewById(R.id.imv_header_send);
				holder.rl_content_bg_send = (RelativeLayout) convertView.findViewById(R.id.rl_content_bg_send);
				holder.tv_content_send = (TextView) convertView.findViewById(R.id.tv_content_send);
				holder.imv_voice_content_send = (ImageView) convertView.findViewById(R.id.imv_voice_content_send);
				holder.tv_record_time_send = (TextView) convertView.findViewById(R.id.tv_record_time_send);

				convertView.setTag(holder);
				break;
		}

		/*
		 * } else { switch (type) { case TYPE_ORDER_INFO: holderFirst =
		 * (ViewHolderFirst) convertView.getTag(); break;
		 *
		 * case TYPE_ORDER_IM: holder = (ViewHolder) convertView.getTag();
		 * break; }
		 *
		 * }
		 */

		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
		switch (type) {
			case TYPE_ORDER_INFO:

				holderFirst.tv_shou_name.setText(StringUtil.isEmpty(order.getName()));
				holderFirst.tv_shou_address.setText(StringUtil.isEmpty(order.getAddress()));
				holderFirst.tv_shou_phone.setText(StringUtil.isEmpty(order.getPhone()));

				holderFirst.tv_weight.setText(StringUtil.isEmpty(order.getCharging_weight())+"kg");
				holderFirst.tv_money_num.setText(StringUtil.isEmpty(order.getCollection_amount())+"元");

				holderFirst.tv_sender_name.setText(StringUtil.isEmpty(order.getSenderName()));
				holderFirst.tv_sender_address.setText(StringUtil.isEmpty(order.getSenderAddress()));
				holderFirst.tv_sender_phone.setText(StringUtil.isEmpty(order.getSenderPhone()));
				holderFirst.tv_fajian_date.setText(StringUtil.isEmpty(order.getTime()));
				holderFirst.tv_thing.setText(StringUtil.isEmpty(order.getArticleInfo()));
				holderFirst.tv_now_version.setText(StringUtil.isEmpty(order.getDeliverNo()));
				if(Utility.isEmpty(order.getSenderPhone())){
					holderFirst.iv_sender_call.setVisibility(View.GONE);
				}else{
					holderFirst.iv_sender_call.setVisibility(View.VISIBLE);
				}
				if(Utility.isEmpty(order.getPhone())){
					holderFirst.iv_shou_call.setVisibility(View.GONE);
				}else{
					holderFirst.iv_shou_call.setVisibility(View.VISIBLE);
				}
				final String expressNo =SkuaidiSpf.getLoginUser().getExpressNo();
				if ("sto".equals(expressNo) || "zt".equals(expressNo) || "yt".equals(expressNo)
						|| "yd".equals(expressNo) || "qf".equals(expressNo) || "ht".equals(expressNo)) {//申通、中通、圆通、韵达、全峰、汇通
					holderFirst.ll_print_express_bill.setVisibility(View.VISIBLE);
					holderFirst.tv_order_use_introduce.setVisibility(View.VISIBLE);
				}else{
					holderFirst.ll_print_express_bill.setVisibility(View.GONE);
					holderFirst.tv_order_use_introduce.setVisibility(View.GONE);
				}

				if("zt".equals(expressNo)){
					holderFirst.rl_money_amont.setVisibility(View.VISIBLE);
					holderFirst.rl_save_order.setVisibility(View.VISIBLE);
					holderFirst.view_devider_sorder.setVisibility(View.VISIBLE);
					holderFirst.rl_get_realInfo.setVisibility(View.VISIBLE);
					holderFirst.view_devider_realInfo.setVisibility(View.VISIBLE);
				}else if("sto".equals(expressNo)){
					holderFirst.rl_get_realInfo.setVisibility(View.VISIBLE);
					holderFirst.view_devider_realInfo.setVisibility(View.VISIBLE);
				}else{
					holderFirst.rl_get_realInfo.setVisibility(View.GONE);
					holderFirst.view_devider_realInfo.setVisibility(View.GONE);
				}
				if("1".equals(order.getIs_send())){
					holderFirst.tv_edit.setVisibility(View.GONE);
					Drawable left_img = context.getResources().getDrawable(R.drawable.order_ocm_accomplish);
					left_img.setBounds(0, 0, left_img.getMinimumWidth(), left_img.getMinimumHeight());
					holderFirst.tv_save_order.setCompoundDrawables(left_img, null, null, null);
					holderFirst.tv_save_order.setText("完成录单");
					holderFirst.iv_save_order.setVisibility(View.GONE);
					holderFirst.rl_save_order.setEnabled(false);
				}else{
					holderFirst.tv_edit.setVisibility(View.VISIBLE);
					holderFirst.tv_save_order.setText("录单");
					holderFirst.iv_save_order.setVisibility(View.VISIBLE);
					holderFirst.rl_save_order.setEnabled(true);
				}
				if("1".equals(order.getIsRealName())){
					holderFirst.tv_get_realInfo.setText("实名信息已采集");
					Drawable left_img = context.getResources().getDrawable(R.drawable.order_autonym_accomplish);
					left_img.setBounds(0, 0, left_img.getMinimumWidth(), left_img.getMinimumHeight());
					holderFirst.tv_get_realInfo.setCompoundDrawables(left_img, null, null, null);
					holderFirst.iv_get_realInfo.setVisibility(View.GONE);
					holderFirst.rl_get_realInfo.setEnabled(false);
				}
				if("1".equals(order.getIsPrint())){
					holderFirst.tv_print_status.setText("已打印");
				}

				if("rejected".equals(order.getOrder_state_cname())){
					holderFirst.ll_operation.setVisibility(View.GONE);
					holderFirst.tv_order_use_introduce.setVisibility(View.GONE);
					holderFirst.tv_edit.setTextColor(context.getResources().getColor(R.color.gray_3));
					holderFirst.tv_edit.setEnabled(false);

					holderFirst.rl_shou_pice_content.setVisibility(View.VISIBLE);
					holderFirst.ll_fajian_date.setVisibility(View.VISIBLE);
					holderFirst.rl_money_amont.setVisibility("zt".equals(expressNo)?View.VISIBLE:View.GONE);
					holderFirst.view_line_below1.setVisibility(View.VISIBLE);
					holderFirst.rl_more.setVisibility(View.GONE);
					holderFirst.rl_note_info.setVisibility(View.VISIBLE);
					if(!TextUtils.isEmpty(order.getArticleInfo())) {
						holderFirst.rl_thing.setVisibility(View.VISIBLE);
					}else{
						holderFirst.rl_thing.setVisibility(View.GONE);
					}
					if(!TextUtils.isEmpty(order.getCharging_weight())) {
						holderFirst.rl_weight.setVisibility(View.VISIBLE);
					}else{
						holderFirst.rl_weight.setVisibility(View.GONE);
					}
				}

				/**
				 * 展开
				 */
				holderFirst.rl_more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						holderFirst.rl_shou_pice_content.setVisibility(View.VISIBLE);
						holderFirst.ll_fajian_date.setVisibility(View.VISIBLE);
						holderFirst.rl_weight.setVisibility(View.VISIBLE);
						holderFirst.rl_money_amont.setVisibility("zt".equals(expressNo)?View.VISIBLE:View.GONE);
						holderFirst.view_line_below1.setVisibility(View.VISIBLE);
						holderFirst.rl_more.setVisibility(View.GONE);
						holderFirst.rl_note_info.setVisibility(View.VISIBLE);
						if(!TextUtils.isEmpty(order.getArticleInfo())) {
							holderFirst.rl_thing.setVisibility(View.VISIBLE);
						}else{
							holderFirst.rl_thing.setVisibility(View.GONE);
						}
						if(!TextUtils.isEmpty(order.getCharging_weight())) {
							holderFirst.rl_weight.setVisibility(View.VISIBLE);
						}else{
							holderFirst.rl_weight.setVisibility(View.GONE);
						}
					}
				});

				/**
				 * 收缩
				 */
				holderFirst.rl_gone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						holderFirst.rl_shou_pice_content.setVisibility(View.GONE);
						holderFirst.view_line_below1.setVisibility(View.GONE);
						holderFirst.ll_fajian_date.setVisibility(View.GONE);
						holderFirst.rl_weight.setVisibility(View.VISIBLE);
						holderFirst.rl_money_amont.setVisibility(View.GONE);
						holderFirst.rl_more.setVisibility(View.VISIBLE);
						holderFirst.rl_note_info.setVisibility(View.GONE);
						holderFirst.rl_thing.setVisibility(View.GONE);
					}
				});

				/**
				 * 编辑收发信息
				 */
				holderFirst.tv_edit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, CreateNewOrderActivity.class);
						intent.putExtra("order", order);
						context.startActivity(intent);
					}
				});

				if(!Utility.isEmpty(order.getNeed_pay()) && Float.parseFloat(order.getNeed_pay()) > 0){
					holderFirst.tv_shou_qrcode.setText("完成支付");
					holderFirst.tv_cash_num.setText(order.getPrice()+"元");
					holderFirst.iv_help_and_response.setVisibility(View.GONE);
					Drawable left = context.getResources().getDrawable(R.drawable.icon_shou_qrcode_finish);
					left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
					holderFirst.tv_shou_qrcode.setCompoundDrawables(left, null, null, null);
				} else {
					holderFirst.iv_help_and_response.setVisibility(View.VISIBLE);
					holderFirst.ll_shou_qrcode.setOnClickListener(new OnClickListener() {// 收款二维码

						@Override
						public void onClick(View v) {
							UMShareManager.onEvent(context, "order_detail_qrcode", "order_detail", "订单详情：收款");

							if (order.getId() != null && !order.getId().equals("") && !order.getId().equals("null")) {
								newDB.markUpdateOrder(order);
								Intent intent = new Intent(context, MakeCollectionsActivity.class);
								intent.putExtra("tag", "order");
								intent.putExtra("orderID", order.getId());
								((OrderInfoActivity) context).startActivityForResult(intent, 50);
							} else {
								UtilToolkit.showToast("请联网后在订单列表页下拉刷新，再尝试");
							}
						}
					});
				}

				if (Utility.isEmpty(order.getDeliverNo())) {
					holderFirst.ll_edit_orderNo.setOnClickListener(new OnClickListener() {// 回填单号

						@Override
						public void onClick(View v) {
							// 跳转到拍照扫描界面
							Intent intent = new Intent(context, CaptureActivity.class);
							intent.putExtra("qrcodetype", Constants.TYPE_ORDER_ONE);
							if (order.getExpress_type() == 2) {
								intent.putExtra("isSto", true);
							}
							((Activity) context).startActivityForResult(intent, Constants.TYPE_ORDER_ONE);
						}
					});
				} else {
					holderFirst.tv_input_order_num.setText("运单跟踪" );
					holderFirst.tv_now_version.setText(StringUtil.isEmpty(order.getDeliverNo()));
					Drawable left = context.getResources().getDrawable(R.drawable.icon_ordernum_tracking);
					left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
					holderFirst.tv_input_order_num.setCompoundDrawables(left, null, null, null);
					holderFirst.ll_edit_orderNo.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							UMShareManager.onEvent(context, "order_detail_waybillTracking", "order_detail", "订单详情：运单跟踪");
							Intent intent = new Intent();
							intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
							intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
							intent.putExtra("order_number", order.getDeliverNo());
							intent.setClass(context, CopyOfFindExpressResultActivity.class);
							context.startActivity(intent);
						}
					});
				}

				/**
				 * 录单
				 */
				holderFirst.rl_save_order.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						UMShareManager.onEvent(context, "order_detail_recordOrder", "order_recordOrder", "业务-订单-录单");
						if(TextUtils.isEmpty(holderFirst.tv_now_version.getText()) || notCompleted()) {
							Intent intent = new Intent(context, RecordOrderActivity.class);
							intent.putExtra("recordOrder", order);
							context.startActivity(intent);
						}else{
							Message msg = Message.obtain();
							msg.what = Constants.ZT_SEND_ORDER;
							handler.sendMessage(msg);
						}
					}
				});

				/**
				 * 采集实名信息
				 */
				holderFirst.rl_get_realInfo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						UMShareManager.onEvent(context, "order_detail_realInfo", "order_realInfo", "业务-订单-实名采集");
						UserInfo userInfo = SkuaidiSpf.getLoginUser();
						String url;
						if (!TextUtils.isEmpty(userInfo.getArea()) && userInfo.getArea().contains("浙江")) {
							url = "http://m.kuaidihelp.com/realname/zjSto?mobile=" + userInfo.getPhoneNumber() + "&operateType=1&orderId=" + order.getId();
						} else {
							url = "http://m.kuaidihelp.com/realname/senderInfo?mobile=" + userInfo.getPhoneNumber() + "&operateType=1&orderId=" + order.getId();
						}
						Intent intent = new Intent(context, CommonWebViewActivity.class);
						ArrayList<String> parameters = new ArrayList<>();
						parameters.add(url);
						intent.putStringArrayListExtra("parameter", parameters);
						context.startActivity(intent);
					}
				});

				/**
				 * 打印电子面单
				 */
				holderFirst.ll_print_express_bill.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						UMShareManager.onEvent(context, "print_click_qrcode", "print_click", "订单详情：打印电子面单");

						String expressNo = SkuaidiSpf.getLoginUser().getExpressNo();
						if("sto".equals(expressNo)&& Utility.isEmpty(SkuaidiSpf.getOrderPwd())){
							Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.print_explain_icon);
							SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
							dialog.setTitleGray("电子面单权限验证");
							dialog.setTitleColor(R.color.title_bg);
							SpannableStringBuilder spannableString =  new SpannableStringBuilder("你暂无电子面单使用权限，可\n点击“立即验证”进行验证，\n或查阅？电子面单申请及使用\n查看申请流程。");
							ImageSpan imageSpan =  new ImageSpan(context, bitmap, ImageSpan.ALIGN_BASELINE);
							// 用ImageSpan对象替换字符
							spannableString.setSpan(imageSpan, 31 , 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							spannableString.setSpan(new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(R.dimen.dialog_gray_content_size)),31, 41,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							dialog.setContentGray(spannableString);
							dialog.setPositionButtonTextGray("立即验证");
							dialog.setNegativeButtonTextGray("我再看看");
							dialog.setPositionButtonClickListenerGray(new SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray(){

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(context, SavePrintPermissionActivity.class);
									context.startActivity(intent);
									return;
								}
							});
							dialog.showDialogGray(holderFirst.ll_print_express_bill.getRootView());
						}else if(notCompleted()){
							if(TextUtils.isEmpty(order.getDeliverNo())){
								SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
								dialog.setTitleGray("温馨提示");
								dialog.setTitleColor(R.color.title_bg);
								dialog.setContentGray("收发信息不完整，请补充完整");
								dialog.setPositionButtonTextGray("完善信息");
								dialog.setNegativeButtonTextGray("我再看看");
								dialog.setPositionButtonClickListenerGray(new SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray() {

									@Override
									public void onClick(View v) {
//									showPopUp(holderFirst.tv_edit);
										Intent intent = new Intent(context, UpdateAddressorActivity.class);
										intent.putExtra("update", order);
										context.startActivity(intent);
									}
								});
								dialog.showDialogGray(holderFirst.ll_print_express_bill.getRootView());
							}else {
								UtilToolkit.showToast("订单信息不完整，无法进行打印");
								return;
							}
						}else if("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
							if(isEnablePrint(order.getDeliverNo())){
								Message msg = new Message();
								msg.what = Constants.PRINT_ORDER;
								handler.sendMessage(msg);
							}else{
								Utility.showFailDialog(context, "非电子面单单号不允许作为电子面单打印", holderFirst.ll_print_express_bill.getRootView());
							}

						}else if("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
							KLog.i("tag", SkuaidiSpf.getZTOrderExpressNOreg());
//							if(!TextUtils.isEmpty(order.getDeliverNo()) && Utility.isEmpty(SkuaidiSpf.getZTOrderExpressNOreg())){
//								Utility.showFailDialog(context, "中通电子面单校验规则获取失败", holderFirst.ll_print_express_bill.getRootView());
//								return;
//							}
							if (TextUtils.isEmpty(SkuaidiSpf.getZTOrderExpressNOreg()) || TextUtils.isEmpty(order.getDeliverNo())
									|| (!Utility.isEmpty(SkuaidiSpf.getZTOrderExpressNOreg()) && isZtEnablePrint(order.getDeliverNo()))) {
								Message msg = new Message();
								msg.what = Constants.PRINT_ORDER;
								handler.sendMessage(msg);
							} else {
								Utility.showFailDialog(context, "非电子面单单号不允许作为电子面单打印", holderFirst.ll_print_express_bill.getRootView());
							}
						}else {
							Message msg = new Message();
							msg.what = Constants.PRINT_ORDER;
							handler.sendMessage(msg);
						}

//					Intent intent = new Intent(context, PrintExpressBillActivity.class);

					}
				});

				if (order.getExpress_type() == 2 && E3SysManager.getReviewInfoNew()!=null) {
					holderFirst.rl_order_back.setVisibility(View.VISIBLE);
				} else {
					holderFirst.rl_order_back.setVisibility(View.GONE);
				}

				holderFirst.rl_order_back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.callback();
						}
					}
				});



				/**
				 * 发件人去电
				 */
				holderFirst.iv_sender_call.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(order.getSenderPhone())) {
							UtilToolkit.showToast("此条订单没有发件人电话号码，无法拨号");
							return;
						}
						AcitivityTransUtil.showChooseTeleTypeDialog((Activity)context, "", order.getSenderPhone(),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
					}
				});

				/**
				 * 收件人去电
				 */
				holderFirst.iv_shou_call.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(order.getPhone())) {
							UtilToolkit.showToast("此条订单没有收件人电话号码，无法拨号");
							return;
						}
						AcitivityTransUtil.showChooseTeleTypeDialog((Activity)context, "", order.getSenderPhone(),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
					}
				});

				/**
				 * 电子免单申请及使用说明
				 */
				holderFirst.tv_order_use_introduce.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String expressNo = SkuaidiSpf.getLoginUser().getExpressNo();
						Intent intent = new Intent(context, WebLoadView.class);
						ArrayList<String> parameters = new ArrayList<String>();
						if("sto".equals(expressNo)) {
							parameters.add("http://m.kuaidihelp.com/help/ele_paper_exp?brand=sto");
						}else if("zt".equals(expressNo)){
							parameters.add("http://m.kuaidihelp.com/help/ele_paper_exp?brand=zt");
						}
						parameters.add("电子面单申请及使用");
						intent.putStringArrayListExtra("parameters", parameters);
						context.startActivity(intent);
					}
				});

//			holderFirst.rl_input_info.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (mFuctionBTNClickListener != null) {
//						mFuctionBTNClickListener.fuction(v);
//					}
//				}
//			});

//				List<CallRecordingMp3> mp3s = SKuaidiApplication.getInstance().getFinalDbCache().findAllByWhere(CallRecordingMp3.class, "orderNum = '" + order.getId() + "'");
				ICallLogDao iCallLogDao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
				QueryBuilder<ICallLog> qb = iCallLogDao.queryBuilder();
				qb.where(ICallLogDao.Properties.MasterPhone.eq(SkuaidiSpf.getLoginUser().getPhoneNumber()),ICallLogDao.Properties.OrderNumber.eq(order.getId()))
						.orderDesc(ICallLogDao.Properties.CallDate);
				List<ICallLog> callogs=qb.build().list();
				//System.out.println("mp3s size : " + mp3s.size());
				if (callogs != null && callogs.size() > 0) {
					final ICallLog callog = callogs.get(0);
					holderFirst.recording_center_view_order.setVisibility(View.VISIBLE);
					holderFirst.recording_date_order.setText(dateFormat.format(new Date(callog.getCallDate())));
					holderFirst.playorpause_order.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
					holderFirst.recording_palying_duration_order.setText(durationTimeFormat.format(callog.getCallDurationTime() - 28800000));
					holderFirst.playorpause_order.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							try {
								if (isPlaying == false) {
									mp = new MediaPlayer();
									mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer arg0) {
											isPlaying = false;
											time = 0;
											mp = null;
											timer.cancel();
											holderFirst.recording_palying_duration_order.setText(durationTimeFormat.format(callog.getCallDurationTime() - 28800000));
											holderFirst.playorpause_order.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));

										}
									});
									mp.reset();
									mp.setDataSource(callog.getRecordingFilePath());
									mp.prepare();
									mp.start();
									timer = new Timer(true);
									timer.schedule(new TimerTask() {

										@Override
										public void run() {
											Message msg = new Message();
											msg.what = 1;
											msg.obj = holderFirst.recording_palying_duration_order;
											mHandler.sendMessage(msg);
										}
									}, 1000, 1000);
									holderFirst.playorpause_order.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_stop_small"));
									isPlaying = true;
								} else {
									mp.pause();
									timer.cancel();
									time = 0;
									mp.stop();
									mp.release();
									mp = null;
									holderFirst.playorpause_order.setBackgroundResource(SkuaidiSkinManager.getSkinResId("record_play_small"));
									isPlaying = false;
									holderFirst.recording_palying_duration_order.setText(durationTimeFormat.format(callog.getCallDurationTime() - 28800000));
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					holderFirst.recording_center_view_order.setVisibility(View.GONE);
				}

				break;

			case TYPE_ORDER_IM:
				orderIm = (OrderIm) getItem(position);
//			holder.rl_content_bg_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_send_message"));
				if (now.substring(0, 10).equals(orderIm.getSpeakTime().substring(0, 10))) {
					holder.tv_time.setText("今天 " + orderIm.getSpeakTime().substring(10, 16));

				} else if (now.substring(0, 8).equals(orderIm.getSpeakTime().substring(0, 8))
						&& (Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(orderIm.getSpeakTime().substring(8, 10)) == 1)) {
					holder.tv_time.setText("昨天 " + orderIm.getSpeakTime().substring(10));
				} else {

					holder.tv_time.setText(orderIm.getSpeakTime().substring(5, 16));
				}

				if (orderIm.getSpeakRole() != 2) {
					holder.rl_send.setVisibility(View.GONE);
					holder.rl_receive.setVisibility(View.VISIBLE);

					if (orderIm.getSpeakRole() == 1) {// c用户
						holder.imv_header_receive.setBackgroundResource(R.drawable.icon_yonghu);
					}
					holder.tv_user_info.setText(orderIm.getUserName());
					if (orderIm.getContentType() == Constants.TYPE_TXT) {
						holder.tv_content_receive.setVisibility(View.VISIBLE);
						holder.imv_voice_content_receive.setVisibility(View.GONE);
						holder.imv_voice_content_receive.setTag(orderIm.getVoiceContent());
						holder.tv_record_time_receive.setVisibility(View.GONE);
						holder.tv_content_receive.setText(orderIm.getTxtContent());
						holder.rl_content_bg_receive.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});
					} else if (orderIm.getContentType() == Constants.TYPE_YUYIN) {
						holder.tv_content_receive.setVisibility(View.GONE);
						holder.imv_voice_content_receive.setVisibility(View.VISIBLE);
						holder.tv_record_time_receive.setVisibility(View.VISIBLE);
						holder.tv_record_time_receive.setText(orderIm.getVoiceLength() + "\"");
						DownloadTask.downloadFile(handler, Constants.URL_RECORDER_ORDER_ROOT, Constants.RECORDER_PATH, orderIm.getVoiceContent());
						holder.rl_content_bg_receive.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								DownloadTask.playLocalVoice(((OrderIm) getItem(position)).getVoiceContent());
								//System.out.println("LocalVoice path : " + ((OrderIm) getItem(position)).getVoiceContent());
							}
						});
					}
				} else {
					holder.rl_receive.setVisibility(View.GONE);
					holder.rl_send.setVisibility(View.VISIBLE);

					holder.imv_header_send.setBackgroundResource(ExpressFirm.getExpressHeadIcon(SkuaidiSpf.getLoginUser().getExpressNo()));
					if (orderIm.getContentType() == Constants.TYPE_TXT) {
						holder.tv_content_send.setVisibility(View.VISIBLE);
						holder.imv_voice_content_send.setVisibility(View.GONE);
						holder.imv_voice_content_send.setTag(orderIm.getVoiceContent());
						holder.tv_record_time_send.setVisibility(View.GONE);
						holder.tv_content_send.setText(orderIm.getTxtContent());
						holder.tv_content_send.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});
					} else if (orderIm.getContentType() == Constants.TYPE_YUYIN) {
						holder.tv_content_send.setVisibility(View.GONE);
						holder.imv_voice_content_send.setVisibility(View.VISIBLE);
						holder.rl_content_bg_send.setTag(orderIm.getVoiceContent());
						holder.tv_record_time_send.setVisibility(View.VISIBLE);
						holder.tv_record_time_send.setText(orderIm.getVoiceLength() + "\"");
						DownloadTask.downloadFile(handler, Constants.URL_RECORDER_ORDER_ROOT, Constants.RECORDER_PATH, orderIm.getVoiceContent());
						holder.rl_content_bg_send.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								DownloadTask.playLocalVoice(((OrderIm) getItem(position)).getVoiceContent());
								//System.out.println("LocalVoice path : " + ((OrderIm) getItem(position)).getVoiceContent());
							}
						});
					}
				}

				break;
		}
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Utility.isEmpty(pop) && pop.isShowing()){
					pop.dismissPop();
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		RelativeLayout rl_receive;
		TextView tv_time;
		ImageView imv_header_receive;
		RelativeLayout rl_content_bg_receive;
		TextView tv_content_receive;
		ImageView imv_voice_content_receive;
		TextView tv_record_time_receive;
		TextView tv_user_info;

		RelativeLayout rl_send;
		ImageView imv_header_send;
		RelativeLayout rl_content_bg_send;
		TextView tv_content_send;
		ImageView imv_voice_content_send;
		TextView tv_record_time_send;

	}

	class ViewHolderFirst {
		TextView playorpause_order, recording_palying_duration_order, tv_shou_qrcode, recording_date_order, recording_duration_order, tv_input_order_num, tv_save_order, tv_get_realInfo,
				 tv_print_status;

		TextView tv_edit, tv_shou_name, tv_shou_phone_tag, tv_cash_num, tv_now_version, tv_shou_phone, tv_shou_address_tag, tv_shou_address, tv_sender_phone, tv_sender_address, tv_sender_name, tv_fajian_date,
				tv_weight, tv_money_num, tv_thing, tv_order_use_introduce;

		View recording_center_view_order, el_phone, ll_print_express_bill, ll_edit_orderNo, ll_shou_qrcode, rl_sender_phone, view_line_below1, rl_more, rl_gone, rl_shou_pice_content,iv_help_and_response, iv_shou_call,
				iv_sender_call, ll_fajian_date, rl_order_back, rl_input_info, rl_note_info, rl_thing, rl_weight, rl_money_amont, ll_operation, rl_save_order,iv_save_order, rl_get_realInfo, iv_get_realInfo, view_devider_realInfo
				,view_devider_sorder;
	}

	public void setNotify(String order) {
		notifyDataSetChanged();
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.iv_close:
					mPopupWindow.dismiss();
					break;
				// 支付、金额及补贴活动说明
				case R.id.tv_activity_description:
					Intent intent = new Intent(context, HuodongDescriptionActivity.class);
					intent.putExtra("url", Constants.ACTIVITY_EXPLAIN);
					context.startActivity(intent);
					break;
				default:
					break;
			}

		}
	};

	public void stopPlayRecord() {
		if (mp != null) {
			mp.pause();
			mp.stop();
			mp.release();
			mp = null;
		}
	}

	public interface OnOrderCallback {
		void callback();
	}

	public void setOnFuctionBTNClickListener(OnFuctionBTNClickListener fuctionBTNClickListener) {
		mFuctionBTNClickListener = fuctionBTNClickListener;
	}

	public interface OnFuctionBTNClickListener {
		void fuction(View v);
	}


	private boolean isEnablePrint(String deliveryNo){
		if(Utility.isEmpty(deliveryNo)){
			return true;
		}else if(deliveryNo.length() == 12 && deliveryNo.startsWith("22") && !deliveryNo.startsWith("227") && !deliveryNo.startsWith("229")){
			return true;
		}else if(deliveryNo.length() == 13 && deliveryNo.startsWith("33")){
			return true;
		}else if(deliveryNo.startsWith("118")){
			return true;
		}else if(deliveryNo.length() == 13 && deliveryNo.startsWith("55")){
			return true;
		}
		return false;
	}

	private boolean isZtEnablePrint(String deliveryNo){
		if(Utility.isEmpty(deliveryNo)){
			return true;
		}else{
			Pattern pattern = Pattern.compile(SkuaidiSpf.getZTOrderExpressNOreg());
			Matcher matcher = pattern.matcher(deliveryNo);
			return matcher.find();
		}
	}

	private boolean notCompleted(){
		return(Utility.isEmpty(order.getSenderName()) || Utility.isEmpty(order.getSenderPhone())
				|| Utility.isEmpty(order.getSenderProvince()) || Utility.isEmpty(order.getSenderCity())
				|| Utility.isEmpty(order.getSenderCountry()) || Utility.isEmpty(order.getSenderDetailAddress())
				|| Utility.isEmpty(order.getName()) || Utility.isEmpty(order.getPhone()) || Utility.isEmpty(order.getReceiptProvince())
				|| Utility.isEmpty(order.getReceiptCity()) || Utility.isEmpty(order.getReceiptCountry())
				|| Utility.isEmpty(order.getReceiptDetailAddress()));
	}

	/**
	 * 二维码填写
	 * @param v
	 */
	private void qrCodeInput(View v){
		ShowQrcodePopwindow popwindow = new ShowQrcodePopwindow();
		popwindow.show(v);
	}


	/**
	 * 二维码展示window
	 * @author xy
	 *
	 */
	private class ShowQrcodePopwindow{
		private View convertView;
		private ImageView qrCodeView;
		private Window window;

		public ShowQrcodePopwindow(){
			window = new Window();
		}

		private class Window extends PopupWindow{

			public Window(){
				super(context);
				initView();
				initDatas();
				setWidth(WindowManager.LayoutParams.MATCH_PARENT);
				setHeight(WindowManager.LayoutParams.MATCH_PARENT);
				setBackgroundDrawable(new ColorDrawable(0xb0000000));
				setContentView(convertView);
			}

			private void initView(){
				convertView = LayoutInflater.from(context).inflate(R.layout.show_qrcode_window_layout, null);
				qrCodeView = (ImageView) convertView.findViewById(R.id.iv_qrcode);
			}

			private void initDatas(){
				Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
				int size = (int) (DisplayUtil.getScreenMetrics().x/1.5);
				String url = "http://m.kuaidihelp.com/wduser/order_info_update?mb="+SkuaidiSpf.getLoginUser().getPhoneNumber()+"&order_number="+order.getId();
				qrCodeView.setImageBitmap(BarcodeUtils.createQRImage(url, size, size, logo));
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
			}
		}
		public void show(View v){
			window.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
	}

}
