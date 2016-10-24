
package com.kuaibao.skuaidi.activity.smsrecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.SmsRecordSearchActivity;
import com.kuaibao.skuaidi.activity.TimingSendSmsCancelActivity;
import com.kuaibao.skuaidi.activity.adapter.SmsRecordAdapter;
import com.kuaibao.skuaidi.activity.adapter.SmsRecordDraftAdapter;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.OldRecordsActivity;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.ItemOnClickListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.LayoutDismissListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP.PopDismissClickListener;
import com.kuaibao.skuaidi.activity.view.SkuaidiAlertDialogSendMsg;
import com.kuaibao.skuaidi.activity.view.SkuaidiAlertDialogSendMsg.BtnOnClickListener;
import com.kuaibao.skuaidi.activity.view.ToastCustom;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dao.RecordDraftBoxDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.DeliverNoHistory;
import com.kuaibao.skuaidi.entry.DraftBoxSmsInfo;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.DraftBoxUtility;
import com.kuaibao.skuaidi.util.SkuaidiSkinResourceManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 短信记录列表界面
 *顾冬冬
 */
public class SmsRecordActivity extends SkuaiDiBaseActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	public static Stack<Activity> activityStack;
	public static final int GET_DELIVERY_LIST_SUCCESS = 0x1001;
	public static final int GET_DELIVERY_LIST_FAIL = 0x1002;
	public static final int REQUEST_DRAFTBOX_TO_SENDSMS = 0X1004;
	public static final int REQUEST_SEARCH = 0x1003;// 搜索请求进入搜索界面
	public static final int REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY = 0x1005;// 进入详情界面
	public static final int RESULT_GETINTO_RECORD_DETAIL_ACTIVITY = 0X1006;// 从详情界面返回
	private static final int REQUEST_GET_TIMING_LIST_SUCCESS = 0X1007;// 请求定时发送列表成功
	private static final int REQUEST_MODIFY_OR_CANCEL_TIMING = 0x1008;
	private final int GET_NOSIGNED_INFO_SUCCESS = 0X1019;// 获取昨日未签收手机号信息成功

	private final int SEND_TYPE_SMS = 0X1010;
	/** 发短信 **/
	private final int SEND_TYPE_CLOUD = 0X1011;
	/** 发送云呼 **/
	public static final int REQUEST_MSG_MODEL = 0X1012;// 请求选择短信模板
	public static final int REQUEST_VOICE_MODEL = 0x1013;// 请求选择语音模板
	private final int AGAIN_SHOW_MODEL = 0x1015;// 重新显示主界面模板


	@BindView(R.id.ll_title) LinearLayout llTitle;// title
	@BindView(R.id.rlSearch) RelativeLayout rlSearch;// 搜索
	@BindView(R.id.iv_title_back) SkuaidiImageView ivTiteBack;// 返回按钮
	@BindView(R.id.smsRecordBox) TextView smsRecordBox;// 发件箱按钮
	@BindView(R.id.smsDraftBox) TextView smsDraftBox;// 草稿箱按钮
	@BindView(R.id.tvHint) TextView tvHint;// 提示（没有短信记录）
	@BindView(R.id.tv_more) SkuaidiImageView tvMore;// 筛选按钮
	@BindView(R.id.pull_refresh_view) PullToRefreshView pull;// 上拉下滑控件
	@BindView(R.id.lv_order) ListView lvOrder;// 列表
	@BindView(R.id.lv_draftBox) ListView lvDraftBox;// 草稿箱列表
	@BindView(R.id.send_count) TextView sendCount;// 今日发送消息详情（总发送量）
	@BindView(R.id.send_count2) TextView sendCount2;// 今日发送消息详情（失败量，回复量）
	@BindView(R.id.ll_send_count) ViewGroup llSendCount;// 底部通知

	private Context mContext = null;// 上下文
	private Activity mActivity = null;
	private Intent mIntent = null;
	private Message message = null;
	private SmsRecordAdapter adapter = null;
	private SmsRecordDraftAdapter draftAdapter = null;
	private RecordScreeningPop recordScreeningPop = null;
	private SelectConditionsListPOP selectListMenuPop = null;
	private SkuaidiDialogGrayStyle dialogGray = null;
	private List<SmsRecord> smsRecords = new ArrayList<>();
	private List<DraftBoxSmsInfo> draftBoxInfos = new ArrayList<>();
	private DeliverNoHistory deliverNoHistory = null;
	private ToastCustom toast = null;// 自定义吐司-显示在屏幕中央的大toast
	private int page_num = 1;// 列表页数
	private int total_page = 0;// 返回的总页数
	private String phone = "";// 按手机号码进行搜索
	private String query_number = "";// 编号和手机号码模糊搜索参数
	private String order_number = "";// 按单号进行搜索
	private String status = "";// 按状态进行搜索
	private String start_date = "";// 按开始时间进行搜索
	private String end_date = "";// 按结束时间进行搜索
	private String noread = "";

	private boolean isDraftBox = false;
	private String deleteId = "";// 保存定时发送删除的条目的ID-用于更新列表
	private Message msg = null;

	private String sendDesc = "";// 发送失败说明
	private String sendNoSignedTag = "";// 用来标记是一键发短信还是一键云呼
	private SkuaidiAlertDialogSendMsg alertDialog = null;

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_DELIVERY_LIST_FAIL:// 接收数据失败
				dismissProgressDialog();
				smsRecords.clear();
				adapter.notifyData(smsRecords);
				tvHint.setVisibility(View.VISIBLE);// 显示提示
				break;
			case GET_DELIVERY_LIST_SUCCESS:// 接收数据成功
				dismissProgressDialog();
				tvHint.setVisibility(View.GONE);// 隐藏提示
				if (page_num == 1) {
					smsRecords.clear();
					smsRecords = (List<SmsRecord>) msg.obj;
				} else {
					smsRecords.addAll((List<SmsRecord>) msg.obj);
				}
				adapter.notifyData(smsRecords);
				setRecordBox();
				if (isDraftBox)
					pull.setVisibility(View.GONE);
				else
					pull.setVisibility(View.VISIBLE);
				break;
			case Constants.GET_MESSAGE_COUNT_SUCCESS:// 获取发送短信数量
				deliverNoHistory = (DeliverNoHistory) msg.obj;
				String sentCount = deliverNoHistory.getSentCount();
				String replyCount = deliverNoHistory.getReplyCount();
				String failCount = deliverNoHistory.getFailCount();
				sendCount.setText(sentCount);
				sendCount2.setText("( 客户回复数：" + replyCount + "  失败：" + failCount + " )");
				break;
			case Constants.GET_SIGN_IN_STATUS_SUCCESS:
				int position = msg.arg1;// 获取点击条目的下标
				String getSingleValue = smsRecords.get(position).getSigned();// 获取列表集合里面被点击条目的签收标记
				if (getSingleValue.equals("0")) {
					smsRecords.get(position).setSigned("1");// 将未签收标记设置成签收状态
				} else {
					smsRecords.get(position).setSigned("0");
				}
				adapter.notifyDataSetChanged();
				break;
			case REQUEST_GET_TIMING_LIST_SUCCESS:
				// 获取定时发送列表成功
				@SuppressWarnings("unchecked")
				List<DraftBoxSmsInfo> infos = (List<DraftBoxSmsInfo>) msg.obj;
				if (draftBoxInfos.size() == 0) {
					draftBoxInfos.addAll(infos);
				} else {
					draftBoxInfos.addAll(0, infos);
				}
				draftAdapter.setData(draftBoxInfos);

				if (isDraftBox)
					setDraftBoxHintBg();
				break;
			case GET_NOSIGNED_INFO_SUCCESS:
				int needSendCount = msg.arg1;
				sendDesc = (String) msg.obj;
				if (needSendCount <= 0) {
					UtilToolkit.showToast(sendDesc);
					return;
				}
				if ("sms".equals(sendNoSignedTag)) {
					alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_SEND_MSG).builder().setCancelable(true);
					alertDialog.setSendInfo(sendDesc);
					alertDialog.setOnclickListener(new BtnOnClickListener() {

						@Override
						public void sendMsg(String modelID, String modelStatus) {
							if (!Utility.isEmpty(modelStatus) && "approved".equals(modelStatus)) {
								sendNoSigned(SEND_TYPE_SMS, modelID);
								showProgressDialog( "请稍候");
								alertDialog.dismiss();
							} else {
								UtilToolkit.showToast("请选择已审核的模板");
							}
						}

						@Override
						public void chooseModel() {
							mIntent = new Intent(mContext, ModelActivity.class);
							startActivityForResult(mIntent, REQUEST_MSG_MODEL);
						}

					});
					alertDialog.show();
				} else if ("ivr".equals(sendNoSignedTag)) {
					alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_SEND_VOICE).builder().setCancelable(true);
					alertDialog.setSendInfo(sendDesc);
					alertDialog.setOnclickListener(new BtnOnClickListener() {

						@Override
						public void sendMsg(String modelID, String modelStatus) {
							if ("1".equals(modelStatus)) {
								sendNoSigned(SEND_TYPE_CLOUD, modelID);
								showProgressDialog( "请稍候");
								alertDialog.dismiss();
							} else {
								UtilToolkit.showToast("请选择已审核模板");
							}
						}

						@Override
						public void chooseModel() {
							mIntent = new Intent(mContext, AddVoiceModelActivity.class);
							startActivityForResult(mIntent, REQUEST_VOICE_MODEL);
						}
					});
					alertDialog.show();
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
		setContentView(R.layout.sms_record_activity);
		mContext = this;
		mActivity = this;
		activityStack = new Stack<>();
		EventBus.getDefault().register(this);
		SkuaidiSpf.saveRecordChooseItem(mContext, 0);// 将筛选条目置下标置0
		SkuaidiSpf.saveConditionsListItem(mContext, 0);// 将选择器列表设置选中下标为0
		ButterKnife.bind(this);

		// 设置适配器
		setListAdapter();
		// 设置控件监听事件
		setListener();
		// 调接口-获取列表数据
		getDeliveryList(page_num, "", "", "", "", "", "", "0");
		// 获取定时发送列表
		getTimingList();

	}

	/**
	 * 设置列表适配器
	 */
	private void setListAdapter() {
		lvDraftBox.setOnItemClickListener(this);
		lvDraftBox.setOnItemLongClickListener(this);
		// ******************设置短信记录列表
		adapter = new SmsRecordAdapter(mContext, smsRecords, new SmsRecordAdapter.OnclickListener() {
			@Override
			public void itemClickEvent(View view, int position, SmsRecord smsRecord) {
				smsRecord.setCm_nr_flag(0);
				mIntent = new Intent(mContext, RecordDetailActivity.class);
				mIntent.putExtra("smsRecord", smsRecord);
				mIntent.putExtra("fromActivity", "smsRecordActivity");
				startActivityForResult(mIntent, REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY);
			}

			@Override
			public void updateSign(View view, int position, String informId) {
				if (!Utility.isNetworkConnected()) {// 无网络
					UtilToolkit.showToast("请设置网络");
				} else {// 有网络
					KuaidiApi.setSignInStatus(mHandler, informId, position);
				}
			}

		});
		lvOrder.setAdapter(adapter);

		// *********************获取draftBoxInfos集合数据
		getDraftBoxData();

		draftAdapter = new SmsRecordDraftAdapter(mContext, draftBoxInfos);
		lvDraftBox.setAdapter(draftAdapter);
	}

	/** 获取草稿箱数据 **/
	private void getDraftBoxData() {
		draftBoxInfos = RecordDraftBoxDAO.getDraftBoxInfo(SkuaidiSpf.getLoginUser().getPhoneNumber());
		List<String> deleteIds = new ArrayList<>();
		for (int i = 0; i < draftBoxInfos.size(); i++) {
			DraftBoxSmsInfo draftInfo = draftBoxInfos.get(i);
			String msgContent = draftInfo.getSmsContent();
			String msgNumberPhone = draftInfo.getPhoneNumber();
			String msgNumberOrder = draftInfo.getOrderNumber();
			// 【逗号连接的一串手机号码串中有没有号码】
			String[] phoneArr = DraftBoxUtility.strToArr(msgNumberPhone);
			String[] orderArr = DraftBoxUtility.strToArr(msgNumberOrder);
			boolean haveData = false;

			if (!Utility.isEmpty(phoneArr)) {
				for (int j = 0; j < phoneArr.length; j++) {
					if (!Utility.isEmpty(phoneArr[j].replaceAll(" ", "")) || !Utility.isEmpty(orderArr[j].replaceAll(" ", ""))) {
						haveData = true;// 证明该条存在手机号码或者单号
						break;
					}
				}
			}

			final String id = draftInfo.getId();
			if (Utility.isEmpty(msgContent.replaceAll(" ", "")) && !haveData) {
				deleteIds.add(id);
				RecordDraftBoxDAO.deleteDraft(id);
			}
		}
		 draftBoxInfos = RecordDraftBoxDAO.getDraftBoxInfo(SkuaidiSpf.getLoginUser().getPhoneNumber());
	}

	private void setDraftBoxHintBg() {
		if (draftBoxInfos.size() == 0) {
			lvDraftBox.setVisibility(View.GONE);
			tvHint.setText("没有记录");
			tvHint.setVisibility(View.VISIBLE);
		} else {
			lvDraftBox.setVisibility(View.VISIBLE);
			tvHint.setVisibility(View.GONE);
		}
	}

	private void setRecordBox() {
		if (smsRecords.size() == 0) {
			pull.setVisibility(View.GONE);
			tvHint.setText("没有短信记录");
			tvHint.setVisibility(View.VISIBLE);
		} else {
			pull.setVisibility(View.VISIBLE);
			tvHint.setVisibility(View.GONE);
		}
	}

	private void selectConditionsEvent() {
		if (recordScreeningPop != null) {
			recordScreeningPop.dismissPop();
			recordScreeningPop = null;
		}

		if (selectListMenuPop == null) {
			List<String> conditions = new ArrayList<>();
			conditions.add("筛选");
			conditions.add("短信发送统计");
			conditions.add("历史短信记录");
			//conditions.add("短信昨日未取件");

			selectListMenuPop = new SelectConditionsListPOP(mActivity, conditions, 0.4f, true, SelectConditionsListPOP.SHOW_RIGHT);
			// selectListMenuPop.setBackgroundResource(R.drawable.paopao5);
			selectListMenuPop.setItemOnclickListener(new SelectConditionsListPOP.ItemOnClickListener() {

				@Override
				public void itemOnClick(int position) {
					switch (position) {
					case 0:// 筛选
						eventScreen();
						break;
					case 1:// 短信发送统计
						String url = "http://m.kuaidihelp.com/statistical/smsWithIvr?mobile="+SkuaidiSpf.getLoginUser().getPhoneNumber()+"&type=sms";
						loadWeb(url,"");
						break;
					case 2://历史短信记录
						Intent intent2=new Intent(SmsRecordActivity.this, OldRecordsActivity.class);
						intent2.putExtra(OldRecordsActivity.OLD_RECORDS_NAME,OldRecordsActivity.SMS_OLD_RECORDS);
						startActivity(intent2);
						break;

					default:
						break;
					}
					selectListMenuPop.dismissPop();
					selectListMenuPop = null;
				}
			});

			// 设置点击空白区域的点击事件
			selectListMenuPop.setPopDismissClickListener(new PopDismissClickListener() {

				@Override
				public void onDismiss() {
					selectListMenuPop.dismissPop();
					selectListMenuPop = null;
				}
			});
			selectListMenuPop.showAsDropDown(tvMore, 20, 0);
		} else {
			selectListMenuPop.dismissPop();
			selectListMenuPop = null;
		}
	}

	/**
	 * 筛选功能
	 */
	private void eventScreen() {
		if (recordScreeningPop == null) {
			List<String> itemArr = new ArrayList<>();
			itemArr.add("全部");
			itemArr.add("已发送");
			itemArr.add("已收到");
			itemArr.add("已回复");
			itemArr.add("发送失败");
			itemArr.add("未读信息");
			itemArr.add("未取件");
			itemArr.add("已取件");
			recordScreeningPop = new RecordScreeningPop(mContext, llTitle, itemArr);
			recordScreeningPop.setNumColumns(4);
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
					page_num = 1;
					phone = "";
					start_date = "";
					end_date = "";
					order_number = "";
					query_number = "";
					noread = "";
					switch (position) {
					case 0:// 全部
						UMShareManager.onEvent(mContext, "sms_logs_all", "piePiece_notice", "短信记录:发送失败");
						status = null;
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
						break;
					case 1:// 已发送
						status = "send";
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
						break;
					case 2:// 已收到
						status = "receive";
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
						break;
					case 3:// 已回复
						status = "return";
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
						break;
					case 4:// 发送失败
						mIntent = new Intent(mContext, RecordSmsSendFailAndNoReceiveActivity.class);
						mIntent.putExtra("status","faild");
						startActivity(mIntent);
						break;
					case 5: // 【0：全部|1：未读|2：已读】
						status = "";
						noread = "1";
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);

						break;
					case 6: // 未取件
						mIntent = new Intent(mContext,RecordSmsSendFailAndNoReceiveActivity.class);
						mIntent.putExtra("status","notsigned");
						startActivity(mIntent);
						break;
					case 7: // 已取件
						status = "signed";
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
						break;
					default:
						break;
					}

					recordScreeningPop.dismissPop();
					recordScreeningPop = null;

				}
			});
			recordScreeningPop.showPop();
		} else {
			recordScreeningPop.dismissPop();
			recordScreeningPop = null;
		}
	}

	@OnClick({R.id.smsRecordBox,R.id.smsDraftBox,R.id.iv_title_back,R.id.tv_more,R.id.rlSearch})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.smsRecordBox:// 发件箱按钮
			isDraftBox = false;
			setRecordBox();
			tvMore.setVisibility(View.VISIBLE);
			rlSearch.setVisibility(View.VISIBLE);
			llSendCount.setVisibility(View.VISIBLE);
			lvDraftBox.setVisibility(View.GONE);
			pull.setVisibility(View.VISIBLE);
			smsRecordBox.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundLeft_white());
			smsDraftBox.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundRight());
			smsRecordBox.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
			smsDraftBox.setTextColor(getResources().getColor(R.color.white));
			smsRecordBox.setEnabled(false);
			smsDraftBox.setEnabled(true);
			break;
		case R.id.smsDraftBox:// 草稿箱按钮
			isDraftBox = true;
			setDraftBoxHintBg();
			tvMore.setVisibility(View.GONE);
			rlSearch.setVisibility(View.GONE);
			llSendCount.setVisibility(View.GONE);
			lvDraftBox.setVisibility(View.VISIBLE);
			pull.setVisibility(View.GONE);
			smsRecordBox.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundLeft());
			smsDraftBox.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundRight_white());
			smsRecordBox.setTextColor(getResources().getColor(R.color.white));
			smsDraftBox.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
			if (recordScreeningPop != null) {
				recordScreeningPop.dismissPop();
				recordScreeningPop = null;
			}
			smsRecordBox.setEnabled(true);
			smsDraftBox.setEnabled(false);
			break;
		case R.id.iv_title_back:
			if (recordScreeningPop != null) {
				recordScreeningPop.dismissPop();
				recordScreeningPop = null;
			}
			finish();
			break;
		case R.id.tv_more:// 筛选
			selectConditionsEvent();
			break;
		case R.id.rlSearch:// 搜索按钮
			mIntent = new Intent(mContext, SmsRecordSearchActivity.class);
			startActivityForResult(mIntent, REQUEST_SEARCH);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (recordScreeningPop != null) {
				recordScreeningPop.dismissPop();
				recordScreeningPop = null;
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		DraftBoxSmsInfo draftBoxInfo = draftBoxInfos.get(arg2);

		if (!Utility.isEmpty(draftBoxInfo.getTimingTag()) && draftBoxInfo.getTimingTag().equals("y")) {
			mIntent = new Intent(mContext, TimingSendSmsCancelActivity.class);
			mIntent.putExtra("draftBoxRecord", draftBoxInfo);
			startActivityForResult(mIntent, REQUEST_MODIFY_OR_CANCEL_TIMING);
		} else {
			mIntent = new Intent(mContext, SendMSGActivity.class);
			mIntent.putExtra("draftBoxRecord", draftBoxInfo);
			mIntent.putExtra("fromActivity", "draftbox");
			startActivityForResult(mIntent, REQUEST_DRAFTBOX_TO_SENDSMS);
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		final String timing_tag = draftBoxInfos.get(arg2).getTimingTag();
		dialogGray = new SkuaidiDialogGrayStyle(mContext);
		dialogGray.setTitleGray("删除提示");
		if (!Utility.isEmpty(timing_tag) && "y".equals(timing_tag)) {
			dialogGray.setContentGray("删除后将取消定时发送,\n确定要删除该条草稿？");
		} else {
			dialogGray.setContentGray("是否要删除这条草稿？");
		}
		dialogGray.setPositionButtonTextGray("是");
		dialogGray.setNegativeButtonTextGray("否");
		dialogGray.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

			@Override
			public void onClick(View v) {
				String id = draftBoxInfos.get(arg2).getId();
				if (!Utility.isEmpty(timing_tag) && "y".equals(timing_tag)) {// 如果长按的是定时发送的条目
					deleteId = id;
					deleteTiming(id);// 调用 接口删除定时发送条目
				} else {// 如果发送的草稿箱的条目
					RecordDraftBoxDAO.deleteDraft(id);
					draftBoxInfos.remove(arg2);
					draftAdapter.setData(draftBoxInfos);
					setDraftBoxHintBg();
				}
			}
		});

		dialogGray.showDialogGray(ivTiteBack);

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SEARCH && resultCode == REQUEST_SEARCH) {
			llTitle.setVisibility(View.VISIBLE);
			// setActivityAnimTranslateDown(ll_message_delivery, llTitle);
			getDeliveryList(1, "", "", "", "", "", "", "");
		} else if (requestCode == REQUEST_DRAFTBOX_TO_SENDSMS) {
			getDraftBoxData();// 重新删除不必要的数据并重新获取最新数据
			draftAdapter.setData(draftBoxInfos);
			// 获取定时发送列表
			getTimingList();
			setDraftBoxHintBg();

		} else if (requestCode == REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY && resultCode == RESULT_GETINTO_RECORD_DETAIL_ACTIVITY) {
			adapter.notifyDataSetChanged();
		} else if (requestCode == REQUEST_MODIFY_OR_CANCEL_TIMING) {
			getDraftBoxData();// 重新删除不必要的数据并重新获取最新数据
			draftAdapter.setData(draftBoxInfos);
			// 获取定时发送列表
			getTimingList();
			setDraftBoxHintBg();
		} else if (requestCode == REQUEST_MSG_MODEL) {
			Message msg = new Message();
			msg.what = AGAIN_SHOW_MODEL;
			mHandler.sendMessage(msg);

			alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_SEND_MSG).builder().setCancelable(true);
			alertDialog.setSendInfo(sendDesc);
			alertDialog.setOnclickListener(new BtnOnClickListener() {

				@Override
				public void sendMsg(String modelID, String modelStatus) {
					if (!Utility.isEmpty(modelStatus) && "approved".equals(modelStatus)) {
						sendNoSigned(SEND_TYPE_SMS, modelID);
						showProgressDialog( "请稍候");
						alertDialog.dismiss();
					} else {
						UtilToolkit.showToast("请选择已审核的模板");
					}
				}

				@Override
				public void chooseModel() {
					mIntent = new Intent(mContext, ModelActivity.class);
					startActivityForResult(mIntent, REQUEST_MSG_MODEL);
				}
			});
			alertDialog.show();

		} else if (requestCode == REQUEST_VOICE_MODEL) {
			alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_SEND_VOICE).builder().setCancelable(true);
			alertDialog.setSendInfo(sendDesc);
			alertDialog.setOnclickListener(new BtnOnClickListener() {

				@Override
				public void sendMsg(String modelID, String modelStatus) {
					if ("1".equals(modelStatus)) {
						sendNoSigned(SEND_TYPE_CLOUD, modelID);
						showProgressDialog( "请稍候");
						alertDialog.dismiss();
					} else {
						UtilToolkit.showToast("请选择已审核模板");
					}

				}

				@Override
				public void chooseModel() {
					mIntent = new Intent(mContext, AddVoiceModelActivity.class);
					startActivityForResult(mIntent, REQUEST_VOICE_MODEL);
				}
			});
			alertDialog.show();
		}
	}

	/**
	 * 设置activity界面上移动画
	 */
	private void setActivityAnimTranslateUp(View view, LinearLayout title) {
		AnimationSet animationSet = new AnimationSet(true);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		title.measure(w, h);
		int height = title.getMeasuredHeight();
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -height);
		translateAnimation.setDuration(500);
		animationSet.addAnimation(translateAnimation);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				llTitle.setVisibility(View.GONE);
				mIntent = new Intent(mContext, SmsRecordSearchActivity.class);
				startActivityForResult(mIntent, REQUEST_SEARCH);
			}
		});
		view.startAnimation(animationSet);
	}

	/**
	 * 设置activity界面下移动画
	 */
	private void setActivityAnimTranslateDown(View view, LinearLayout title) {
		AnimationSet animationSet = new AnimationSet(true);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		title.measure(w, h);
		int height = title.getMeasuredHeight();
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -height, 0);
		translateAnimation.setDuration(500);
		animationSet.setFillAfter(true);
		animationSet.addAnimation(translateAnimation);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		view.startAnimation(animationSet);
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		toast = new ToastCustom(mContext, 5, tvMore);
		// 下拉
		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				if (Utility.isNetworkConnected()) {
					page_num = 1;
					getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
					// 获取发送短信量
				} else {
					pull.onHeaderRefreshComplete();
					pull.onFooterRefreshComplete();
					UtilToolkit.showToast("无网络连接");
				}
				KuaidiApi.getSendMessageCount(mContext, mHandler);

			}
		});
		// 上拉
		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				if (Utility.isNetworkConnected()) {
					page_num = page_num + 1;
					if (page_num <= total_page) {
						getDeliveryList(page_num, phone, query_number, order_number, status, start_date, end_date, noread);
					} else {
						pull.onHeaderRefreshComplete();
						pull.onFooterRefreshComplete();
						UtilToolkit.showToast("已加载完全部数据");
					}
				} else {
					pull.onHeaderRefreshComplete();
					pull.onFooterRefreshComplete();
					UtilToolkit.showToast("无网络连接");
				}
				KuaidiApi.getSendMessageCount(mContext, mHandler);
			}
		});

	}

	// 接收eventbus传递过来的对象，此处用于更新列表小红点
	@Subscribe
	public void onEventMainThread(SmsRecord smsRecord) {

		for (int i = 0; i < adapter.getAdapterData().size(); i++) {
			SmsRecord smsRecord2 = adapter.getAdapterData().get(i);
			if (smsRecord2.getTopic_id().equals(smsRecord.getTopic_id())) {
				smsRecord2.setCm_nr_flag(0);
				smsRecord2.setSigned(smsRecord.getSigned());
				adapter.getAdapterData().set(i, smsRecord2);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		KuaidiApi.getSendMessageCount(mContext, mHandler);// 获取发送短信量
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		if (activityStack == null)
			return;
		// 退出时关闭 可能已经开启的SmsRecordSearchActivity
		for (Activity ac : activityStack) {
			ac.finish();
		}
		activityStack.clear();
		activityStack = null;

	}

	/**
	 * 调用短信记录列表接口方法
	 * @param noread
	 *            未读数【0：全部|1：未读|2：已读】
	 */
	private void getDeliveryList(int page_num, String phone, String query_number, String order_number, String status, String start_date,
			String end_date, String noread) {
		showProgressDialog( "数据加载中...");
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_user/get_delivery_list");
			data.put("role", "courier");
			data.put("page_size", Constants.PAGE_SIZE);
			data.put("page_num", page_num);
			data.put("user_phone", phone);
			data.put("query_number", query_number);
			data.put("order_number", order_number);
			data.put("status", status);
			data.put("start_date", start_date);
			data.put("end_date", end_date);
			data.put("noread", noread);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/** 获取定时发送列表 **/
	private void getTimingList() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_user/get_timing_list");
			data.put("role", "courier");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/** 删除定时发送 **/
	private void deleteTiming(String id) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_user/delete_timing");
			data.put("role", "courier");
			data.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/** 获取昨日未取件手机号信息（短信条数） **/
	private void getNoSignedSmsInfo(String send_type) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_nosigned/getsmsinfo");
			data.put("send_type", send_type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/** 给昨日未取件手机号发短信&云呼 **/
	private void sendNoSigned(int SendType, String modelId) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform_nosigned/send");
			data.put("type", "nosigned");
			if (SendType == SEND_TYPE_SMS) {
				data.put("send_type", "sms");
			} else if (SendType == SEND_TYPE_CLOUD) {
				data.put("send_type", "ivr");
			}
			data.put("template_id", modelId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
		dismissProgressDialog();
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
					total_page = result.getInt("total_page");
					List<SmsRecord> smsRecords = new ArrayList<>();
					if (total_page != 0) {
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
						}
						message.what = GET_DELIVERY_LIST_SUCCESS;
					} else {
						message.what = GET_DELIVERY_LIST_FAIL;
					}
					message.obj = smsRecords;
					mHandler.sendMessage(message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (!Utility.isEmpty(sname) && "inform_user/get_timing_list".equals(sname)) {// 获取定时发送列表
				try {
					JSONArray jsonArray = result.getJSONArray("data");
					if (null != jsonArray && jsonArray.length() > 0) {
						List<DraftBoxSmsInfo> infos = new ArrayList<>();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = (JSONObject) jsonArray.get(i);
							DraftBoxSmsInfo info = new DraftBoxSmsInfo();
							info.setId(obj.getString("id"));
							info.setTimingTag("y");
							info.setPhoneNumber(obj.getString("user_phone"));
							info.setSmsContent(obj.getString("template_content"));
							info.setCreateTime(obj.getString("create_time"));
							info.setSendTime(obj.getString("send_time"));
							info.setLastUpdateTime(obj.getString("last_update_time"));
							info.setModelTitle(obj.getString("title"));
							infos.add(info);
						}
						message.what = REQUEST_GET_TIMING_LIST_SUCCESS;
						message.obj = infos;
						mHandler.sendMessage(message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (!Utility.isEmpty(sname) && "inform_nosigned/getsmsinfo".equals(sname)) {
				int needSendCount = -1;
				String desc = "";
				try {
					needSendCount = result.getInt("needSendCount");
					desc = result.getString("msg");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.msg = new Message();
				this.msg.what = GET_NOSIGNED_INFO_SUCCESS;
				this.msg.obj = desc;
				this.msg.arg1 = needSendCount;
				mHandler.sendMessage(this.msg);
			}

		} else if (!Utility.isEmpty(sname) && "inform_user/delete_timing".equals(sname)) {
			if (!Utility.isEmpty(deleteId)) {
				for (int i = 0; i < draftBoxInfos.size(); i++) {
					if (draftBoxInfos.get(i).getId().equals(deleteId)) {
						draftBoxInfos.remove(i);
						break;
					}
				}
				toast.show(msg);
				draftAdapter.setData(draftBoxInfos);
				setDraftBoxHintBg();
			}
		}else if (!Utility.isEmpty(sname) && "inform_nosigned/send".equals(sname)) {
			if (!Utility.isEmpty(msg)) {
				UtilToolkit.showToast(msg);
			}
		} 
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
		if (!Utility.isEmpty(result)) {
			if (!Utility.isEmpty(sname) && "inform_user/delete_timing".equals(sname)) {
				toast.show(result);
			} else {
				UtilToolkit.showToast(result);
			}
		}
		if (!Utility.isEmpty(result))
			UtilToolkit.showToast(result);

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
	}

}
