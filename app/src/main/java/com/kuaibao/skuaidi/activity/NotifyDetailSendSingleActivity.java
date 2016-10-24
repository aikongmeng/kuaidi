package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.activity.adapter.NotifyShowPhoneAdapter;
import com.kuaibao.skuaidi.activity.model.SendSMSPhoneAboutExpressNoCache;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.customer.MyCustomManageActivity;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.ReceiverInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.PinyinComparator;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NotifyDetailSendSingleActivity extends SkuaiDiBaseActivity {

	private Context context;// 上下文
	private SkuaidiDialog dialog;
	// title 部分**********************
	private ImageView iv_title_back;// 返回按钮
	private TextView tv_title_des;// 标题
	private Button bt_title_more;// 发送
	// body 部分***********************
	private TextView tv_tag;// 编号
	private EditText et_phon_num;// 手机号码输入框
	private ImageView imv_notify_close;// 手机号输入框删除手机号按钮
	private ImageView ib_getPhone;// 进入客户管理界面
	private ListView lv_show_phonenumber;// 筛选的联系人列表
	private RelativeLayout replacementModel;// 更换模板点击区域

	private ImageView iv_model_add;// 短信模板按钮中-图片
	private ImageView iv_boardcast;
	private TextView tv_model;// 短信模板按钮中-文字

	private EditText et_notify_content;// 短信输入框
	private TextView tv_send_message_count_down;// 显示发送短信字数
	private TextView send_total_down;// 显示发送短信条数
	private RelativeLayout rl_scan_order_frame;// 扫描单号按钮
	private TextView tv_show_order;// 显示单号区域
	private ImageView imv_expressDH_close;// 删除单号按钮
	private RelativeLayout rl_forensics;// 拍照取证按钮
	private ImageView iv_forensics;// 拍照取证显示照片区
	// 发送短信部分************
	private String user_wallet = "1";// 1-同意使用可提现金额消费短信;0-不同意使用可提现金额消费短信
	// 编号显示部分************
	private FinalDb db;
	List<SendSMSPhoneAboutExpressNoCache> caches = new ArrayList<SendSMSPhoneAboutExpressNoCache>();
	SendSMSPhoneAboutExpressNoCache cache = new SendSMSPhoneAboutExpressNoCache();
	// 联系人部分**************
	private List<MyCustom> cuss;// 我的联系人列表
	private List<MyCustom> list_cuss=new ArrayList<>();// 我的联系人列表
	private NotifyShowPhoneAdapter phoneAdapter;// 联系人列表adapter

	private String changeContent;
	private String choosePhoneNumber;// 筛选后列表中选择的手机号码
	// 因为单号是多少位不确定、在添加模板那字数长度设置时有问题，
	private String ordernum = "#NO#";
	private String orderDH = "#DHDHDHDHDH#";
	private String model_url = "#SURLSURLSURLSURLS#";

	// 与底部通知相关
	private String broad_cast = "";// 用于接收广播内容
	private String smsFeeCount = "";// 自己充值的费用还可以发送短信的条数
	private String smsFreeCount = "";// 免费短信条数
	private String smsGiveCount = "";// 赠送短信条数

	// 临时变量
	private boolean isReciveData = false;// 　代表是接收到了单号所对应的手机号
	private String reciveNo = "";// 保存接收到的手机号

	private boolean getBroadCastSuccess = false;
	private boolean getSmsCountSuccess = false;
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_BROADCAST_SUCCESS:// 获取广播消息成功
				broad_cast = msg.obj.toString();// 接收到的通知内容
				break;

			case Constants.SUCCESS:
				try {
					int lastPhoneNo = msg.arg1;// 最后一个发送手机的编号
					JSONObject json = (JSONObject) msg.obj;
					String code = json.getString("code");
					JSONObject data = json.optJSONObject("data");
					String status = data.getString("status");
					String desc = data.getString("desc");
					if (status.equals("success")) {
						cache.setLastNo(lastPhoneNo);// 保存最后一个手机号编号
						cache.setTodayTime_str(Utility.getSMSCurTime());// 保存系统当前时间
						if (db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1") == null
								|| db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1").size() == 0) {
							db.save(cache);
						} else {
							db.update(cache, "id = 1");
						}
						UtilToolkit.showToast(desc);// 提示接口返回信息
						finish();// 关闭当前界面
					} else {
						dismissProgressDialog();
						bt_title_more.setEnabled(true);
						UtilToolkit.showToast(desc);// 提示接口返回信息
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case GET_PHONE_SUCCESS:// 获取手机号成功
				@SuppressWarnings("unchecked")
				List<ReceiverInfo> receiverInfos = (List<ReceiverInfo>) msg.obj;
				ReceiverInfo receiverInfo = receiverInfos.get(0);
				String receiverPhone = receiverInfo.getRec_mobile();
				if (Utility.isEmpty(receiverPhone)) {
					isReciveData = false;
				}
				if (!Utility.isEmpty(receiverInfo)) {
					reciveNo = receiverInfo.getRec_mobile();
					et_phon_num.setText(receiverInfo.getRec_mobile());
					et_phon_num.setSelection(reciveNo.length());

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
		setContentView(R.layout.notify_detail_send_single_activity);

		context = this;
		db = SKuaidiApplication.getInstance().getFinalDbCache();

		findView();
		initView();
		// showModel();
		getData();

		setListener();
		SkuaidiSpf.removeContent(context);// 删除保存过的临时编写的模板
	}

	// 查找控件
	private void findView() {
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);// 返回按钮
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);// 标题
		bt_title_more = (Button) findViewById(R.id.bt_title_more);// 发送按钮

		tv_tag = (TextView) findViewById(R.id.tv_tag);// 编号
		et_phon_num = (EditText) findViewById(R.id.et_phon_num);// 联系人手机号输入框
		imv_notify_close = (ImageView) findViewById(R.id.imv_notify_close);// 手机号码输入框删除号码按钮
		ib_getPhone = (ImageView) findViewById(R.id.ib_getPhone);// 进入客户管理界面按钮
		lv_show_phonenumber = (ListView) findViewById(R.id.lv_show_phonenumber);// 筛选的联系人列表
		replacementModel = (RelativeLayout) findViewById(R.id.replacementModel);// 更换模板点击区域
		et_notify_content = (EditText) findViewById(R.id.et_notify_content);// 输入短信编辑框

		iv_model_add = (ImageView) findViewById(R.id.iv_model_add);
		tv_model = (TextView) findViewById(R.id.tv_model);

		tv_send_message_count_down = (TextView) findViewById(R.id.tv_send_message_count_down);
		send_total_down = (TextView) findViewById(R.id.send_total_down);
		rl_scan_order_frame = (RelativeLayout) findViewById(R.id.rl_scan_order_frame);
		tv_show_order = (TextView) findViewById(R.id.tv_show_order);
		imv_expressDH_close = (ImageView) findViewById(R.id.imv_expressDH_close);
		rl_forensics = (RelativeLayout) findViewById(R.id.rl_forensics);
		iv_forensics = (ImageView) findViewById(R.id.iv_forensics);
//		iv_boardcast = (ImageView) findViewById(R.id.iv_boardcast);
//		iv_boardcast.setImageResource(SkuaidiSkinManager.getSkinResId("notify_broadcast_icon"));
		ib_getPhone.setImageResource(SkuaidiSkinManager.getSkinResId("address_list"));

		iv_title_back.setOnClickListener(onClickListener);// 返回按钮点击
		bt_title_more.setOnClickListener(onClickListener);// 发送按钮点击
		imv_notify_close.setOnClickListener(onClickListener); // 删除手机号按钮
		ib_getPhone.setOnClickListener(onClickListener);// 进入客户管理界面
		replacementModel.setOnClickListener(onClickListener);// 选择短信模板按钮
		rl_scan_order_frame.setOnClickListener(onClickListener);// 点击进入单号扫描
		imv_expressDH_close.setOnClickListener(onClickListener);// 删除扫描后显示的单号
		rl_forensics.setOnClickListener(onClickListener);// 点击拍照取证按钮
		iv_forensics.setOnClickListener(onClickListener);// 拍照取证的图片显示区
	}

	// 初始化界面
	private void initView() {
		tv_title_des.setText("免费发短信");
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setText("发送");

		iv_model_add.setBackgroundResource(SkuaidiSkinManager.getSkinResId("send_msg_select_models_icon"));

		if (et_notify_content.getText().toString().length() == 0) {
			tv_send_message_count_down.setText("0/129");
		}
		et_notify_content.setHint("请选择已审核的模板");
		// Utility.setEditTextHintSize(et_notify_content,
		// "自己编写的短信内容不可以发送！\n请选择短信模板发送！", 20);// 设置hint字体大小
	}

	// 获取数据
	private void getData() {
		// 获取联系人列表
		getContactList();
		// 设置编号
		if (db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id=1") == null
				|| db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1").size() == 0) {
			cache.setLastNo(0);
			caches.add(cache);
			db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1").addAll(caches);
			cache = caches.get(0);
		} else {
			caches.addAll(db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1"));
			cache = caches.get(0);
		}
		tv_tag.setText("编号 " + (cache.getLastNo() + 1));

		getBottomNotify();// 调用接口获取底部通知栏显示信息
	}

	private void getContactList(){
		showProgressDialog("正在加载联系人...");
		list_cuss.clear();
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
				List<MyCustom> customs = UtilToolkit.filledData(newDB.selectAllCustomer());
				Collections.sort(customs, new PinyinComparator());
				list_cuss=customs;
				return null;
			}
			protected void onPostExecute(Void result) {
				// UtilToolkit.showToast("通讯录整理完毕");
				dismissProgressDialog();
				phoneAdapter = new NotifyShowPhoneAdapter(context, list_cuss);
				if (!phoneAdapter.isEmpty()) {
					lv_show_phonenumber.setAdapter(phoneAdapter);
				}
			}
		}.execute();
	}

	/**
	 * 设置监听事件
	 */
	private void setListener() {
		cuss = new ArrayList<MyCustom>();// 初始化我的客户列表


		// 编写短信内容时更新编写字数
		et_notify_content.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				changeContent = et_notify_content.getText().toString();
				int contentLength = changeContent.length();
				if (!changeContent.equals("")) {
					if (null != finalContent && finalContent.equals(changeContent) && reviewStatus != null
							&& reviewStatus.equals("approved")) {// 判断模板输入框中是不是选中的模板，否则不显示字数和计费条数
						tv_send_message_count_down.setVisibility(View.VISIBLE);
						tv_send_message_count_down.setText(contentLength + "/129");
						if (changeContent.length() > 65) {
							send_total_down.setVisibility(View.VISIBLE);
							send_total_down.setText("此短信按2条计费");
						} else {
							send_total_down.setVisibility(View.GONE);
						}
						SkuaidiSpf.saveContent(context, et_notify_content.getText().toString());// 保存发送的内容
						SkuaidiSpf.saveContent(context, changeContent);
					} else {
						tv_send_message_count_down.setVisibility(View.GONE);
						send_total_down.setVisibility(View.VISIBLE);
						send_total_down.setText("本条将用手机卡发，选择已审核模板用APP发");
						send_total_down.setTextSize(12f);
					}
				} else {
					tv_send_message_count_down.setVisibility(View.VISIBLE);
					tv_send_message_count_down.setText("0/129");
					send_total_down.setVisibility(View.GONE);
				}
			}
		});

		// 联系人手机号码输入框内容变化时事件
		et_phon_num.addTextChangedListener(new TextWatcher() {
			// 筛选手机号
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (!Utility.isEmpty(reciveNo) && !reciveNo.equals(et_phon_num.getText().toString())) {
					isReciveData = false;
				}
				if (isReciveData == false) {
					cuss.clear();
					if (!et_phon_num.getText().toString().equals("")) {
						for (int i = 0; i < list_cuss.size(); i++) {
							String phone = list_cuss.get(i).getPhone();
							if (phone.indexOf(et_phon_num.getText().toString()) != -1) {
								cuss.add(list_cuss.get(i));
							}
						}
						if (cuss.size() == 0) {
							lv_show_phonenumber.setVisibility(View.GONE);
						} else {
							lv_show_phonenumber.setVisibility(View.VISIBLE);
							phoneAdapter.freshData(cuss);
						}
					} else {
						imv_notify_close.setVisibility(View.GONE);
						lv_show_phonenumber.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!et_phon_num.getText().toString().equals("")) {
					et_phon_num.setTextColor(context.getResources().getColor(color.text_black));
					imv_notify_close.setVisibility(View.VISIBLE);
					String phone = arg0.toString();
					if (phone.contains("1")) {// 如果手机号码中包含“1”
						int index = phone.indexOf("1", 0);
						if (phone.length() == index + 11) {// 判断手机号码是否是从1开始往后有11位
							et_phon_num.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));// 设置手机号码为绿色
						}
					}
				} else {
					imv_notify_close.setVisibility(View.GONE);// 隐藏删除手机号码按钮
				}
			}
		});

		// 联系人列表item点击事件
		lv_show_phonenumber.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				choosePhoneNumber = cuss.get(arg2).getPhone();
				et_phon_num.setText(choosePhoneNumber);// 将选中的手机号放到手机号输入框 中
				et_phon_num.setSelection(choosePhoneNumber.length());// 光标设置到文本最后
				lv_show_phonenumber.setVisibility(View.GONE);// 隐藏联系人列表
			}
		});
		// 扫描单号文本框内容变化
		tv_show_order.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String expressDHStr = tv_show_order.getText().toString();
				if (!expressDHStr.equals("")) {
					imv_expressDH_close.setVisibility(View.VISIBLE);// 显示删除按钮
				} else {
					imv_expressDH_close.setVisibility(View.GONE);// 隐藏删除按钮
				}
			}
		});

	}

	private List<ReplyModel> models;
	SkuaidiDB skuaidiDb;
	private String modelContentSpf;// 从数据库中获取的短信内容（模板）
	private String modelTid;// 模板id
	private String reviewStatus;// 审核状态
	private String contentSpf_zhuan;
	private String finalContent;// 最终显示文本

	private void showModel() {
		skuaidiDb = SkuaidiDB.getInstanse(context);// 实例化数据库类
		models = skuaidiDb.getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN);
		if (models == null || models.size() == 0) {
			return;
		}
		ReplyModel model = new ReplyModel();

		for (int i = 0; i < models.size(); i++) {
			if (models.get(i).isChoose() == true) {
				model = models.get(i);
				break;
			}
		}
		modelContentSpf = model.getModelContent();// 从数据库中找到已经被选中的模板内容
		modelTid = model.getTid();// 从数据库中获得模板ID

		if (null != modelContentSpf && !"".equals(modelContentSpf)) {
			if (modelContentSpf.contains("#DH#")) {
				contentSpf_zhuan = modelContentSpf.replaceAll("#DH#", orderDH);
				if (contentSpf_zhuan.length() >= 129) {
					contentSpf_zhuan = contentSpf_zhuan.substring(0, 129);
				}
			} else {
				contentSpf_zhuan = modelContentSpf;
				if (contentSpf_zhuan.length() >= 129) {
					contentSpf_zhuan = contentSpf_zhuan.substring(0, 129);
				}
			}
			if (contentSpf_zhuan.contains("#SURL#")) {
				finalContent = contentSpf_zhuan.replaceAll("#SURL#", model_url);
			} else {
				finalContent = contentSpf_zhuan;
			}
		} else {
			finalContent = "";
		}

		reviewStatus = model.getState(); // 从数据库中找到被选中的模板的状态
		TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(context);
		if (null != finalContent && !"".equals(finalContent)) {// 如果从数据库中取出了被选中的模板
			et_notify_content.setText(mTextInsertImgParser.replace(finalContent));
			// et_notify_content.setSelection(finalContent.length());//
			// 光标设置到文本最后
		} else {
			et_notify_content.setText("");
		}
	}

	private Intent mIntent;
	private String fileName;
	// 按钮点击
	private OnClickListener onClickListener = new OnClickListener() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:// 返回按钮
				if (!et_phon_num.getText().toString().trim().equals("")) {
					dialog = new SkuaidiDialog(context);
					dialog.setTitle("警告");
					dialog.setContent("您确认要退出吗？");
					dialog.setPositionButtonTitle("确定");
					dialog.setNegativeButtonTitle("取消");
					dialog.isUseEditText(false);
					dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					});
					dialog.showDialog();
				} else {
					finish();
				}
				break;
			case R.id.bt_title_more:// 发送按钮
				if (Utility.isNetworkConnected()) {
					sendMessage();
				} else {
					mPopSendMsgOwnPhone(bt_title_more, "提示", "您没有连接网络，是否使用自己手机发送？", "确定", "取消");// 提示使用自己手机发送
				}
				break;
			case R.id.imv_notify_close:// 手机号码输入框删除手机号码按钮
				et_phon_num.setText("");
				imv_notify_close.setVisibility(View.GONE);
				break;
			case R.id.ib_getPhone:// 进入客户管理界面
				UMShareManager.onEvent(context, "NotifyDetailSingle_get_into_custom", "sms", "单条发短信:进入客户管理界面");
				mIntent = new Intent(context, MyCustomManageActivity.class);
				mIntent.putExtra("single", true);
				mIntent.putExtra("loadType", MyCustomManageActivity.LOAD_TYPE_HIDETOP);
				startActivityForResult(mIntent, 121);
				break;
			case R.id.replacementModel:// 选择短信模板
				UMShareManager.onEvent(context, "NotifyDetailSingle_choose_model", "SMS", "单条发短信:更换短信模板");
				mIntent = new Intent(context, ModelActivity.class);
				startActivity(mIntent);
				break;
			case R.id.rl_scan_order_frame:// 进入单号扫描按钮
				UMShareManager.onEvent(context, "NotifyDetailSingle_scan_orderno", "SMS", "单条发短信:扫描单号");
				mIntent = new Intent(context, CaptureActivity.class);
				mIntent.putExtra("qrcodetype", Constants.TYPE_PAIJIAN_ONE);
				mIntent.putExtra("inType", "paijian_just_one");
				mIntent.putExtra("isContinuous", false);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivityForResult(mIntent, Constants.TYPE_PAIJIAN_ONE);
				break;
			case R.id.imv_expressDH_close:// 删除扫描的单号
				tv_show_order.setText("");
				break;
			case R.id.rl_forensics:
				UMShareManager.onEvent(context, "SMS_take_photos", "SMS", "SMS:拍照取证");
				// 验证手机是否存在SD卡
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
					fileName = "IMG_KuaiBao" + format.format(new Date());
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
							.getExternalStorageDirectory().getPath() + "/skuaidi/pic/", fileName + ".jpg")));
					startActivityForResult(intent, Constants.PHOTOHRAPH);
				} else {
					UtilToolkit.showToast("未找到存储卡");
				}
				break;
			case R.id.iv_forensics:
				mIntent = new Intent(context, NotifyImageShowActivity.class);
				mIntent.putExtra("from", "notifyDetailActivity_iv_forensics");
				SKuaidiApplication.getInstance().postMsg("NotifyDetailActivity", "bitmap_show", bitmap_show);
				startActivity(mIntent);
				break;
//			case R.id.ll_notify:
//				UMShareManager.onEvent(context, "NotifyDetailSingle_get_into_broadcast", "SMS", "单条发短信:查看广播通知");
//				mIntent = new Intent(context, NotifyShowBroadCastInfoActivity.class);
//				mIntent.putExtra("broadcast", broad_cast);
//				startActivity(mIntent);
//				break;
			default:
				break;
			}

		}
	};

	private String imagePath = "";// 拍照取证的图片路径
	private Bitmap bitmap_show;// 拍照取证图片的bitmap

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 121) {
			if (resultCode == RESULT_OK) {
				String phone = data.getStringExtra("phone");// 得到到客户管理界面选中的手机号
				et_phon_num.setText(phone);
				et_phon_num.setSelection(phone.length());// 将光标设置到最后
				lv_show_phonenumber.setVisibility(View.GONE);// 隐藏筛选列表
			}
		}
		// 单条扫描后的运单号填充到显示框中
		if (requestCode == Constants.TYPE_PAIJIAN_ONE) {
			isReciveData = true;
			String orderDH = "";
			if (resultCode == Constants.TYPE_PAIJIAN_ONE) {
				orderDH = data.getStringExtra("decodestr");// 得到扫描后的运单号
				tv_show_order.setText(orderDH);// 显示扫描后单号
			}
			// 如果未填写过手机号就调用接口获取手机号
			if (et_phon_num.getText().toString().trim().equals("")) {
				JSONObject jsonData = new JSONObject();
				try {
					jsonData.put("sname", "express.contacts");
					jsonData.put("no", orderDH);
					jsonData.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				httpInterfaceRequest(jsonData, false, INTERFACE_VERSION_NEW);
			}
		}
		// 拍照取证照片返回
		if (requestCode == Constants.PHOTOHRAPH) {
			imagePath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/" + fileName + ".jpg";
			if (!TextUtils.isEmpty(imagePath)&&Utility.getImage(imagePath, 800f, 480f, 40) != null) {
				int degree = BitmapUtil.readPictureDegree(imagePath);
				Bitmap bitmap = Utility.getImage(imagePath, 800f, 480f, 40);
				bitmap_show = BitmapUtil.rotaingImageView(degree, bitmap);
			}
			if (bitmap_show != null) {
				iv_forensics.setImageBitmap(bitmap_show);
			} else {
				iv_forensics.setImageResource(R.drawable.camera_icon);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 发送短信（调用接口）
	 */
	private String bitmapstr = "";// 图片转成字符串

	public void sendMessage() {

		bt_title_more.setEnabled(false);
		if (bitmap_show != null) {
			bitmapstr = Utility.bitMapToString(bitmap_show);// bitmap 转换成字符流
		}
		// 一单发送
		UMShareManager.onEvent(context, "SMS_singleSMS_send", "SMS", "SMS:单发短信");
		if (!et_notify_content.getText().toString().trim().equals("") && !et_phon_num.getText().toString().equals("")) {
			if (et_phon_num.getText().toString().trim().length() == 11
					|| (et_phon_num.getText().toString().indexOf("+86") != -1 && et_phon_num.getText().toString()
							.length() == 14)
					|| (et_phon_num.getText().toString().indexOf("0086") != -1 && et_phon_num.getText().toString()
							.length() == 15)
					|| (et_phon_num.getText().toString().indexOf("86") != -1 && et_phon_num.getText().toString()
							.length() == 13)
					|| (et_phon_num.getText().toString().indexOf("17951") != -1 && et_phon_num.getText().toString()
							.length() == 16)) {
				if (et_notify_content.getText().toString().length() <= 129) {
					// 判断现在使用的模板是否是已经审核过的，
					if (et_notify_content.getText().toString().equals(finalContent) && reviewStatus.equals("approved")) {// 没有修改内容(使用的是审核通过的模板)且是审核通过的
						JSONObject data = null;
						try {
							data = (JSONObject) KuaidiApi.freeSendMessageSingle(SkuaidiSpf
									.getLoginUser().getExpressNo(), cache.getLastNo() + 1, et_phon_num.getText()
									.toString(), tv_show_order.getText().toString(), bitmapstr, user_wallet, Utility
									.getVersionCode(), modelTid, 0);
							showProgressDialog( "请求发送短信中...");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						httpInterfaceRequest(data, false, INTERFACE_VERSION_OLD);
					} else {
						mPopSendMsgOwnPhone(bt_title_more, "提示", "您发送的短信未通过审核，是否通过自己手机发送？", "确定", "取消");// 提示使用自己手机发送
					}
				} else {
					bt_title_more.setEnabled(true);
					UtilToolkit.showToast( "发送内容不得超过129个字");
				}
			} else {
				bt_title_more.setEnabled(true);
				UtilToolkit.showToast( "手机号有误，请重新输入");
			}
		} else {
			bt_title_more.setEnabled(true);
			UtilToolkit.showToast( "发送内容和手机号码不得为空");
		}
	}

	/**
	 * 
	 * @param v
	 * @param title
	 * @param content
	 * @param positiveText
	 * @param negativeText
	 */
	private void mPopSendMsgOwnPhone(View v, String title, String content, String positiveText, String negativeText) {
		dialog = new SkuaidiDialog(context);
		dialog.setTitle(title);
		dialog.setContent(content);
		dialog.setPositionButtonTitle(positiveText);
		dialog.setNegativeButtonTitle(negativeText);
		dialog.isUseEditText(false);
		dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
			@Override
			public void onClick(View v) {
				sendMsgByOwnPhone();
			}
		});
		dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
			@Override
			public void onClick() {
				bt_title_more.setEnabled(true);
			}
		});
		dialog.showDialog();
	}

	/**
	 * 通过自己的手机发送短信方法（用于使用自己手机发送）
	 */
	private void sendMsgByOwnPhone() {
		String sendMsgInfo = et_notify_content.getText().toString();
		String replaceNO = sendMsgInfo.replaceAll(ordernum, (cache.getLastNo() + 1) + "");// 替换编号
		String replaceDH = "";// 替换单号
		if (replaceNO.contains(orderDH)) {
			replaceDH = replaceNO.replaceAll(orderDH, tv_show_order.getText().toString());
		} else {
			replaceDH = replaceNO.replaceAll(orderDH, "");
		}
		if (replaceDH.contains(model_url)) {
			replaceDH = replaceDH.replace(model_url, "");// 替换URL
		}
		sendSMS(et_phon_num.getText().toString(), replaceDH, null);// 使用自己的手机发送短信（使用手机在后台发送短信）
	}

	/**
	 * 调用 接口--调用通知，用于显示在底部通知栏中
	 */
	private void getBottomNotify() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "inform.broadcast");
			data.put("action", "get");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!et_phon_num.getText().toString().trim().equals("")) {
				dialog = new SkuaidiDialog(context);
				dialog.setTitle("警告");
				dialog.setContent("您确认要退出吗？");
				dialog.isUseEditText(false);
				dialog.setPositionButtonTitle("确定");
				dialog.setNegativeButtonTitle("取消");
				dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
				dialog.showDialog();
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		showModel();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// showModel();
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
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void OnSMSSendSuccess() {
		UtilToolkit.showToast("短信发送成功");
		cache.setLastNo(cache.getLastNo() + 1);// 保存最后一个手机号编号
		cache.setTodayTime_str(Utility.getSMSCurTime());// 保存系统当前时间
		if (db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1") == null
				|| db.findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1").size() == 0) {
			db.save(cache);
		} else {
			db.update(cache, "id = 1");
		}
		finish();
	}

	@Override
	protected void OnSMSSendFail() {
		super.OnSMSSendFail();
		bt_title_more.setEnabled(true);
		UtilToolkit.showToast("短信发送失败");
	}

	private static final int GET_BROADCAST_SUCCESS = 678;// 接收广播消息成功
	private final int GET_PHONE_SUCCESS = 679;// 获取手机号成功

	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			Message msg = new Message();
			if (sname != null && sname.equals("inform.broadcast")) {
				try {
					JSONObject retArr = result.getJSONObject("retArr");
					String content = retArr.getString("content");
					msg.what = GET_BROADCAST_SUCCESS;
					msg.obj = content;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (sname != null && sname.equals("express.contacts")) {// 调用接口获取收件人信息
				List<ReceiverInfo> receiverInfo = JsonXmlParser.parseReceiverInfo(result);
				msg.what = GET_PHONE_SUCCESS;
				msg.obj = receiverInfo;
			} else if (sname != null && sname.equals("sms.count")) {
				try {
					JSONObject retArr = result.getJSONObject("retArr");
					smsFeeCount = retArr.getString("FeeSmsCount");
					smsFreeCount = retArr.getString("FreeSmsCount");
					smsGiveCount = retArr.getString("GiveSmsCount");
					getSmsCountSuccess = true;
					msg.what = 1001;
				} catch (JSONException e) {
					// TODO: handle exception
				}
			} else {// 短信发送成功
				msg.what = Constants.SUCCESS;
				msg.obj = result;
				msg.arg1 = cache.getLastNo() + 1;
			}
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		bt_title_more.setEnabled(true);
		if (sname != null && !sname.equals("")) {
			if (result != null && !result.equals("")) {
				UtilToolkit.showToast(result);
			}
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		dismissProgressDialog();
		bt_title_more.setEnabled(true);
		if (Utility.isNetworkConnected() == true) {
			if (result != null) {
				try {
					String desc = result.getString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			UtilToolkit.showToast("网络连接失败");
		}
	}
}
