package com.kuaibao.skuaidi.activity.sendcloudcall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.NotifySearchPhoneActivity;
import com.kuaibao.skuaidi.activity.YunhuOutNumSettingActivity;
import com.kuaibao.skuaidi.activity.adapter.NotifyDetailSendYunhuAdapter;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.activity.view.NotifyBroadCast;
import com.kuaibao.skuaidi.activity.view.SelectSendTimePop;
import com.kuaibao.skuaidi.activity.view.SelectTimePop;
import com.kuaibao.skuaidi.activity.view.ShowTextPop;
import com.kuaibao.skuaidi.activity.view.SkuaidiAlertDialogSendMsg;
import com.kuaibao.skuaidi.activity.view.SkuaidiAlertDialogSendMsg.BtnOnClickListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.function.SkuaidiCommonFunctionContainer;
import com.kuaibao.skuaidi.common.layout.SkuaidiRelativeLayout;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dao.RecordDraftBoxCloudVoiceDAO;
import com.kuaibao.skuaidi.dao.SaveNoDAO;
import com.kuaibao.skuaidi.dao.SaveUnnormalExitDraftInfoDAO;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.NotifyDetailSendYunhuMenuDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.NegativeButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.CloudVoiceMsgDetailEntry;
import com.kuaibao.skuaidi.entry.DraftBoxCloudVoiceInfo;
import com.kuaibao.skuaidi.entry.MakeTelephoneNoEntry;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.entry.ReceiverInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.SaveNoEntry;
import com.kuaibao.skuaidi.entry.SaveUnnormalExitDraftInfo;
import com.kuaibao.skuaidi.json.entry.SendCloudVoiceParameter;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.DraftBoxUtility;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.kuaibao.skuaidi.util.YYSBJsonParser;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscription;
import rx.functions.Action1;

/**
 * @author 顾冬冬
 */
@SuppressLint("NewApi")
public class SendYunHuActivity extends SkuaiDiBaseActivity implements OnClickListener {

//    private final static int GET_BROADCAST_SUCCESS = 0x1001;
    public final static int ENTER_CHOOSE_MODEL = 0x1002;// 选择模板
    public final static int OUT_CHOOSE_MODEL = 0x1003;// 选择模板完成
    public final static int PLAYING_VOICE_ING = 0x1004;// 正在播放录音
    public final static int ENTER_CHOOSE_MOBILE = 0x1005;// 选择手机号
    public final static int OUT_CHOOSE_MOBILE = 0x1006;// 选择手机号完成
    public final static int SEND_VOICE_SUCCESS = 0x1007;// 发送成功
    public final static int OUT_CHOOSE_MOBILE_SEND = 0x1008;// 选择手机号后发送
    public final static int GET_NOSIGNED_INFO_SUCCESS = 0x1009;// 获取今天云呼失败信息及号码条数
    /**
     * 发短信
     **/
    private final int SEND_TYPE_SMS = 0X1011;
    /**
     * 发送云呼
     **/
    private final int SEND_TYPE_CLOUD = 0X1012;
    public static final int REQUEST_MSG_MODEL = 0X1013;// 请求选择短信模板
    public static final int REQUEST_VOICE_MODEL = 0x1014;// 请求选择语音模板
    public static final int AGAIN_SHOW_MODEL = 0x1015;// 重新再显示一遍模板
    private final int REQUEST_AUTO_SEND_MSG = 0X1016;// 云呼失败补发短信请求选择短信模板
    public static final int RESULT_AUTO_SEND_MSG = 0X1017;
    public static final int GET_PHONENUMBER_SUCCESS = 0x1008;// 通过单号获取手机号码成功
    public static int YUNHU_MAX_LIST_COUNT = 100;// 列表总长度

    private String VALID_DISPLAY_NUMBER = "ivr/validDisplayNumber2";// 去电显号设置获取状态接口

    private Subscription subscription;
    private SaveNoEntry saveNoEntry;
    private Context mContext;
    private Intent mIntent;
    private SkuaidiDB skuaidiDB;
    private NotifyDetailSendYunhuAdapter adapter;
    private SelectTimePop pop = null;
    private MyRunnable myRunnable = null;
    private Thread mThread = null;
    private MediaPlayer mPlayer = null;
    private List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries = new ArrayList<>();
    private NotifyDetailSendYunhuMenuDialog menuDialog = null;
    private SkuaidiAlertDialogSendMsg alertDialog = null;
    private SkuaidiCommonFunctionContainer commonFunction = null;

    private SkuaidiRelativeLayout srlTitle2 = null;// 标题栏
    private SkuaidiImageView iv_title_back;// 返回按钮
    //	private TextView tv_title_des;// 标题
    private TextView tv_more;// 发送

    // add models button
    private RelativeLayout btn_add_cloud_voice;// 新增云呼语音模板
    // show models area
    private LinearLayout ll_voice_model;// 模板显示区域
    private TextView tv_vocie_title;// 模板标题
    //	private LinearLayout ll_play_icon;// 播放录音按钮
    private ImageView iv_play_icon;// 播放录音按钮
    private TextView tv_rec_time;// 录音播放进度时长
    private ProgressBar voice_record_progressbar;// progressbar
    private TextView tv_rec_total_time;// 模板总时长

    private ListView list_mobile;// 显示手机号列表
    private ImageView iv_MsgMenuIcon = null;// 菜单按钮
    private ShowTextPop showTextPop = null;

    // 选中的模板信息
    private String voice_path_local = "";// 选中模板录音本地路径
    private String voice_path_service = "";// 选中模板录音对应服务器下载的路径
    private String voice_ivid = "";// 选中模板的ivid
    private String voice_name = "";// 选中模板的录音名称
    private int voice_length = 0;// 选中模板的语音时长
    private String voice_title = "";// 选中模板的标题
    //	private String last_no = "0";
    private int lastNo = 1;// 保存最后一条编号

    private boolean isPlaying = false;// 是否正在播放-false：未播放
    boolean isFind = false;// 用来标记是否有选择模板

    private String content = "";// 广播通知内容
    // 手机列表比较是否存在相同手机号码标记
    private int i = -1;
    private int j = -1;
    private String sendNoSignedTag = "";// 用来标记是一键发短信还是一键云呼
    private String sendDesc = "";// 发送失败说明

    // 云呼失败补发短信模板信息
    private String SmsModelID = "";// 短信模板ID
    private String SmsModelTitle = "";// 短信模板title

    private List<CloudRecord> cloudRecords = new ArrayList<>();
    long timeStamp;
    boolean isTimingTransmission;// 定时发送
    SelectSendTimePop sPop;
    View switchList, ll_yunhu_timing;
    TextView switchText, tv_yunhu_timing_flag;
    private ViewGroup llAutoSendMsg = null;// 自动补发短信提示框
    // 菜单中用来提示被发短信模板的title->from menu
    private TextView tvModelTitle = null;
    boolean isShowAll;
    NotifyBroadCast notifyBroadCast;
    private View sendMsgTitleView = null;

    // 关于草稿箱参数
    private long curTime = System.currentTimeMillis();
    private String draft_id = "";// 保存的时间
    private DraftBoxCloudVoiceInfo temporaryDraftInfo = null;
    private String fromActivity = "";// 进入该 界面来源

    // 语音识别相关
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();// 用HashMap存储听写结果
    private SpeechRecognizer mIat;// 语音听写对象
    //	private String mEngineType = SpeechConstant.TYPE_CLOUD;// 引擎类型
    private int ret = 0; // 函数调用返回值
    private int speechCount = 0;// 为了使得语音识别能自动识别60s，标记关闭次数，当达到6次瑞关闭识别
    private String word = "";
    private String sentence = "";
    private MyHandler myHandler = null;

    // 保持屏幕常亮管理
    PowerManager powerManager = null;
    WakeLock wakeLock = null;

    // 是否从派件界面传来的手机号
    private List<NumberPhonePair> dispatchListData;

    private int distinguishMark = -1;// 识别条目【标记上一条识别的下标】
    private int clickIndex = -1;// 保存当前是从哪一条开始点击单号输入框的

    private MakeTelephoneNoEntry makeTelephoneNoEntry;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AGAIN_SHOW_MODEL:
                    showModel();
                    break;
                case GET_NOSIGNED_INFO_SUCCESS:
                    int needSendCount = msg.arg1;
                    sendDesc = (String) msg.obj;
                    if (needSendCount <= 0) {
                        UtilToolkit.showToast(sendDesc);
                        return;
                    }
                    if ("sms".equals(sendNoSignedTag)) {
                        alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_VOICE_SEND_VOICE).builder().setCancelable(
                                true);
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
                        alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_VOICE_SEND_MSG).builder()
                                .setCancelable(true);
                        alertDialog.setSendInfo(sendDesc);
                        alertDialog.setOnclickListener(new BtnOnClickListener() {

                            @Override
                            public void sendMsg(String modelID, String modelStatus) {
                                if ("1".equals(modelStatus)) {
                                    sendNoSigned(SEND_TYPE_CLOUD, modelID);
                                    showProgressDialog( "请稍候");
                                    alertDialog.dismiss();
                                } else {
                                    UtilToolkit.showToast("请选择已审核的模板");
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
                case PLAYING_VOICE_ING:
                    int curProgress = msg.arg1;
                    int maxLength = msg.arg2;
                    voice_record_progressbar.setMax(maxLength);
                    if (curProgress == 0 || curProgress > maxLength) {
                        tv_rec_time.setText(Utility.formatTime(0));
                        voice_record_progressbar.setProgress(0);
                        iv_play_icon.setBackgroundResource(R.drawable.cloud_play_stop);
                    } else {
                        tv_rec_time.setText(Utility.formatTime(curProgress));
                        voice_record_progressbar.setProgress(curProgress);
                        iv_play_icon.setBackgroundResource(R.drawable.cloud_play_start);
                    }
                    break;
                case SEND_VOICE_SUCCESS:
//                    MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//                    EventBus.getDefault().post(m);
                    SaveNoDAO.saveNo(saveNoEntry);
                    // cache.setLastNo(Integer.parseInt(last_no));
                    // cache.setTodayTime_str(Utility.getSMSCurTime());
                    RecordDraftBoxCloudVoiceDAO.deleteDraftByID(draft_id);
                    SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("voice");
                    // 如果没有数据库或者数据库里面没有内容就要把内容保存进去这个数据库表里面
                /*
				 * if (fdb.findAll(CloudVoiceMobileNoEntry.class, "id = 1") ==
				 * null || fdb.findAll(CloudVoiceMobileNoEntry.class, "id = 1"
				 * ).size() == 0) { fdb.save(cache); } else { fdb.update(cache,
				 * "id = 1"); }
				 */
                    stopPlayRecord();
                    finish();
                    break;
                case GET_PHONENUMBER_SUCCESS:
                    List<ReceiverInfo> receiverInfos = (List<ReceiverInfo>) msg.obj;
                    for (int i = 0; i < receiverInfos.size(); i++) {
                        String rec_mobile = receiverInfos.get(i).getRec_mobile();// 返回的手机号码是连续的
                        autoJudgeMobilePhone2(i, rec_mobile);
                    }
                    adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressWarnings({"static-access", "deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.nofity_detail_send_yunhu_activity);
        mContext = this;
        EventBus.getDefault().register(this);
        YUNHU_MAX_LIST_COUNT = 100;
        draft_id = curTime + "";

        // 保持屏幕常亮设置
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        skuaidiDB = SkuaidiDB.getInstanse(mContext);
        //EventBus.getDefault().register(this);
        // fdb = SKuaidiApplication.getInstance().getFinalDbCache();

        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(mContext.getApplicationContext(), mInitListener);

        dispatchListData = (List<NumberPhonePair>) getIntent().getSerializableExtra("numberPhonePairs");// 从派件群发短信中获取数据

        findView();
        showModel();
        getBroadCast();/* 接收广播消息 */
        initData();
        initMenuView();

        getUnNormalExitModel();
    }

    /**
     * 获取非正常退出时候保存的草稿箱模板
     */
    private void getUnNormalExitModel() {

        SaveUnnormalExitDraftInfo saveDraftInfo = SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("voice");
        if (null != saveDraftInfo && saveDraftInfo.getFrom_data().equals("voice")) {
            RecordDraftBoxCloudVoiceDAO.deleteDraftByID(draft_id);// 删除掉当前刚创建的记录【删除条件：当匹配号码的过程中非正常退出|crash，然后重新进入该界面】
            draft_id = saveDraftInfo.getDraft_id();
            String number = saveDraftInfo.getDraft_no();
            String phoneNumber = saveDraftInfo.getDraft_phoneNumber();
            String orderNumber = saveDraftInfo.getDraft_orderNumber();
            String[] numberStr = DraftBoxUtility.strToArr(number);
            String[] phoneNumberStr = DraftBoxUtility.strToArr(phoneNumber);
            String[] orderNumberStr = DraftBoxUtility.strToArr(orderNumber);

            for (int i = 0; i < cloudVoiceMsgDetailEntries.size(); i++) {
                CloudVoiceMsgDetailEntry info = cloudVoiceMsgDetailEntries.get(i);
                info.setMobile(phoneNumberStr[i].trim());
                info.setMobile_no(numberStr[i].trim());
                info.setOrder_no(orderNumberStr[i].trim());
            }
            adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
            adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));
            SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("voice");
        }
    }

    private void initMenuView() {
        menuDialog = new NotifyDetailSendYunhuMenuDialog(mContext, new NotifyDetailSendYunhuMenuDialog.BtnOnClickListener() {

            @Override
            public void sendMsg() {
                UMShareManager.onEvent(mContext, "Cloud_SendMsgForFail", "CloudVoice", "云呼：给呼叫失败发短信");
                sendNoSignedTag = "sms";
                getNoSignedCloudInfo(sendNoSignedTag);
                showProgressDialog( "请稍候");
            }

            @Override
            public void sendCloudVoice() {
                UMShareManager.onEvent(mContext, "Cloud_SendVoiceCloudForFail", "CloudVoice", "云呼：给呼叫失败重新发起呼叫");
                sendNoSignedTag = "ivr";
                getNoSignedCloudInfo(sendNoSignedTag);
                showProgressDialog( "请稍候");
            }

            @Override
            public void sendTime(final View v) {// 定时发送

                if (isTimingTransmission) {
                    sPop = new SelectSendTimePop(mContext, new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (view.getId() == R.id.btn_deselect_sendTime) {// 取消定时发送
                                sPop.dismiss();
                                cancelTransmission();
                            } else if (view.getId() == R.id.btn_exchange_sendTime) {// 重置定时发送
                                sPop.dismiss();
                                pop = commonFunction.timingTransmission(mContext, iv_MsgMenuIcon, onClickListener);
                            }
                        }
                    });
                    sPop.showAtLocation(iv_MsgMenuIcon, Gravity.CENTER_VERTICAL, 0, 0);
                } else {
                    if (tv_sendTime_tag == null) {
                        tv_sendTime_tag = (TextView) v.findViewById(R.id.tv_sendTime_tag);
                    }
                    commonFunction = SkuaidiCommonFunctionContainer.getInstanse();
                    pop = commonFunction.timingTransmission(mContext, iv_MsgMenuIcon, onClickListener);
                }
            }

            @Override
            public void oneKeyImportPhone() {
                UMShareManager.onEvent(mContext, "Cloud_ImportPhoneNumber", "CloudVoice", "云呼:导入手机号");
                final SkuaidiDialog dialog = new SkuaidiDialog(mContext);
                dialog.setTitle("批量录入客户手机号");
                dialog.isUseBigEditText(true);
                dialog.setBigEditTextHint("手动输入或批量粘贴收件人手机号，并以“，”或换行分割，最多输入"+YUNHU_MAX_LIST_COUNT+"个号");
                dialog.setPositionButtonTitle("确认");
                dialog.setNegativeButtonTitle("取消");
                dialog.setDonotAutoDismiss(true);
                dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isEmpty(dialog.getBigEditTextContent())) {
                            UtilToolkit.showToast("请输入手机号");
                            return;
                        }

                        String phoneNoList = dialog.getBigEditTextContent();
                        Pattern mPattern = Pattern.compile("\\d+");
                        Matcher mMatcher = mPattern.matcher(phoneNoList);
                        List<String> phoneList = new ArrayList<>();
                        while (mMatcher.find()) {
                            phoneList.add(mMatcher.group());
                        }
                        int j = 0;
                        for (int i = 0; i < cloudVoiceMsgDetailEntries.size(); i++) {
                            if (!Utility.isEmpty(phoneList) && phoneList.size() > j) {
                                String mobilePhone = phoneList.get(j);
                                cloudVoiceMsgDetailEntries.get(i).setMobile(mobilePhone);
                            }
                            j++;
                        }
                        adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
                        adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));
                        dialog.setDismiss();
                    }
                });
                dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
                    @Override
                    public void onClick() {
                        dialog.setDismiss();
                    }
                });
                dialog.showDialog();
            }

            @Override
            public void yunhuOutPhoneNumSetting() {
                if (!Utility.isNetworkConnected()){
                    UtilToolkit.showToast("网络未连接或连接错误");
                    return;
                }
                Intent intent = new Intent(mContext, YunhuOutNumSettingActivity.class);
                if (null != makeTelephoneNoEntry) {
                    intent.putExtra("makeTelephoneNoPhone", makeTelephoneNoEntry);
                }
                mContext.startActivity(intent);
            }

            @Override
            public void autoSendMsg(View view) {
                sendMsgTitleView = view;
                if (tvModelTitle == null) {
                    tvModelTitle = (TextView) view.findViewById(R.id.tvModelTitle);
                }
                mIntent = new Intent(mContext, ModelActivity.class);
                mIntent.putExtra("from_activity", "autoSendMsg");
                startActivityForResult(mIntent, REQUEST_AUTO_SEND_MSG);
            }
        });
        menuDialog.builder().setCanceledOnTouchOutside(true);
        getYunhuSettingState();
    }

    private void findView() {
        srlTitle2 = (SkuaidiRelativeLayout) findViewById(R.id.srlTitle2);
        iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        TextView tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_more = (TextView) findViewById(R.id.tv_more);
        btn_add_cloud_voice = (RelativeLayout) findViewById(R.id.btn_add_cloud_voice);

        ll_voice_model = (LinearLayout) findViewById(R.id.ll_voice_model);// 模板显示区域
        tv_vocie_title = (TextView) findViewById(R.id.tv_vocie_title);// 模板标题
        LinearLayout ll_play_icon = (LinearLayout) findViewById(R.id.ll_play_icon);// 播放录音按钮
        iv_play_icon = (ImageView) findViewById(R.id.iv_play_icon);// 播放录音按钮
        tv_rec_time = (TextView) findViewById(R.id.tv_rec_time);// 录音播放进度时长
        voice_record_progressbar = (ProgressBar) findViewById(R.id.voice_record_progressbar);// progressbar
        tv_rec_total_time = (TextView) findViewById(R.id.tv_rec_total_time);// 模板总时长
        iv_MsgMenuIcon = (ImageView) findViewById(R.id.iv_MsgMenuIcon);
        switchText = (TextView) findViewById(R.id.switchText);
        tv_yunhu_timing_flag = (TextView) findViewById(R.id.tv_yunhu_timing_flag);
        switchList = findViewById(R.id.switchList);
        ll_yunhu_timing = findViewById(R.id.ll_yunhu_timing);
        list_mobile = (ListView) findViewById(R.id.list_mobile);
        llAutoSendMsg = (ViewGroup) findViewById(R.id.llAutoSendMsg);

        iv_title_back.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        btn_add_cloud_voice.setOnClickListener(this);
        ll_play_icon.setOnClickListener(this);
        ll_voice_model.setOnClickListener(this);
        iv_MsgMenuIcon.setOnClickListener(this);
        switchList.setOnClickListener(this);
        ll_yunhu_timing.setOnClickListener(this);
        llAutoSendMsg.setOnClickListener(this);

        tv_title_des.setText("云呼");
        tv_more.setText("发送");
    }

    private void initData() {
        if (!Utility.isEmpty(dispatchListData) && 0 != dispatchListData.size()){
            YUNHU_MAX_LIST_COUNT = dispatchListData.size();
            isShowAll = true;
            switchText.setText("收起");
            switchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_close, 0, 0, 0);
        }

        fromActivity = getIntent().getStringExtra("fromActivity");
        temporaryDraftInfo = (DraftBoxCloudVoiceInfo) getIntent().getSerializableExtra("draftBoxRecord");// 从草稿箱中传过来的数据
        // 如果是从草稿箱中进来 的
        if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {
            String number = temporaryDraftInfo.getNumber();
            String[] numberArr = DraftBoxUtility.strToArr(number);
            if (0 != numberArr.length){
                YUNHU_MAX_LIST_COUNT = numberArr.length;
            }
        }

        saveNoEntry = SaveNoDAO.getSaveNo(SaveNoDAO.NO_CLOUD);
        if (saveNoEntry != null) {
            // lastNoHead = saveNoEntry.getSave_letter();// 获取保存的字母
            lastNo = saveNoEntry.getSave_number();// 获取保存的数字
            if (Utility.isEmpty(lastNo)) {
                lastNo = 1;
            }

            String save_time = UtilityTime.getDateTimeByMillisecond(saveNoEntry.getSaveTime() / 1000, UtilityTime.YYYY_MM_DD);
            String cur_time = UtilityTime.getDateTimeByMillisecond(System.currentTimeMillis() / 1000, UtilityTime.YYYY_MM_DD);
            String save_time_day, cur_time_day;

            if (!Utility.isEmpty(save_time)) {
                save_time_day = save_time.substring(save_time.length() - 2, save_time.length());
                cur_time_day = cur_time.substring(cur_time.length() - 2, cur_time.length());
                if (!save_time_day.equals(cur_time_day)) {
                    lastNo = 1;
                }
            }
        }

        for (int i = 0; i < YUNHU_MAX_LIST_COUNT; i++) {
            CloudVoiceMsgDetailEntry cloudVoiceMsgDetailEntrie = new CloudVoiceMsgDetailEntry();
            if (lastNo > 99999) {
                lastNo = 1;
            }
            cloudVoiceMsgDetailEntrie.setMobile_no(lastNo + "");
            lastNo++;
            cloudVoiceMsgDetailEntries.add(cloudVoiceMsgDetailEntrie);
        }

        adapter = new NotifyDetailSendYunhuAdapter(mContext, cloudVoiceMsgDetailEntries, new NotifyDetailSendYunhuAdapter.ButtonOnclick() {
            @Override
            public void mobileBtn(View v, int position) {
                stopDistinguish();
                UMShareManager.onEvent(mContext, "Cloud_select_phoneNo", "CloudVoice", "云呼：选择手机号");
                stopPlayRecord();
                Intent intent = new Intent(mContext, NotifySearchPhoneActivity.class);
                intent.putExtra("listposition", position);
                intent.putExtra("message", (Serializable) adapter.getVoiceMsgDetail());
                intent.putExtra("fromActivity", "CloudVoice");
                intent.putExtra("draft_id", draft_id);
                SendYunHuActivity.this.startActivityForResult(intent, ENTER_CHOOSE_MOBILE);
            }

            @Override
            public void deleteMobile(View v, int position) {
                UMShareManager.onEvent(mContext, "Cloud_delete_phoneNo", "CloudVoice", "云呼：删除手机号");
                CloudVoiceMsgDetailEntry cloud = adapter.getVoiceMsgDetail().get(position);
                cloud.setMobile("");
                adapter.setVoiceMsgItemDetail(position, cloud);
                if (adapter.isShowAllItems())
                    adapter.showAllItem();
                else
                    adapter.showItems(getLastPhoneNumberIndex(adapter.getVoiceMsgDetail()));
                RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
            }

            @Override
            public void modidyNO(View v, int position, List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries) {
                customNo(v, position, cloudVoiceMsgDetailEntries);
                stopDistinguish();
            }

            @Override
            public void playAudio(View v, int position) {
//				if (!SkuaidiSpf.getAudioPermission()) {
////					SendMSGActivity sm = new SendMSGActivity();
////					sm.showTips(mContext);
//					return;
//				}
                if (!UtilityTime.isToday(mContext, SkuaidiSpf.getCurDate(mContext))) {
                    SkuaidiSpf.saveCurDate(mContext, UtilityTime.getDateTimeByMillisecond2(System.currentTimeMillis(), UtilityTime.YYYY_MM_DD));
                    UMShareManager.onEvent(mContext, "SendMSG_ClickEveryDay_Num", "SendMSG", "发短信:每日使用语音录入人数【个人当天只统计1次】");
                }
                if (distinguishMark != -1 && distinguishMark != position) {
                    startDistinguish();
                    cloudVoiceMsgDetailEntries.get(distinguishMark).setPlayVoiceAnim(false);
                    cloudVoiceMsgDetailEntries.get(position).setPlayVoiceAnim(true);
                    distinguishMark = position;
                } else if (distinguishMark == position) {
                    if (cloudVoiceMsgDetailEntries.get(position).isPlayVoiceAnim()) {
                        stopDistinguish();
                        cloudVoiceMsgDetailEntries.get(position).setPlayVoiceAnim(false);
                    } else {
                        startDistinguish();
                        cloudVoiceMsgDetailEntries.get(position).setPlayVoiceAnim(true);
                    }

                } else {// distinguishMark == -1
                    startDistinguish();
                    cloudVoiceMsgDetailEntries.get(position).setPlayVoiceAnim(true);
                    distinguishMark = position;
                }
                adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);

            }

            @Override
            public void inputOrderNo(View v, int position) {
                stopDistinguish();
                clickIndex = position;// 保存当前点击条目下标
                Intent intent = new Intent(mContext, CaptureActivity.class);
                intent.putExtra("qrcodetype", Constants.TYPE_PAIJIAN);
                intent.putExtra("scanMaxSize", YUNHU_MAX_LIST_COUNT-position);// 最大只能输入的单号条数
                intent.putExtra("isContinuous", true);
                intent.putExtra("from", "yunhu");// 用于告知扫码界面是从云呼功能进入的
                intent.putExtra("inType", "notify_detail_adapter");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("listposition", position);// 点击的条目下标
                intent.putExtra("sendmsgInfos", (Serializable) adapter.getVoiceMsgDetail());// 发送短信的列表集合
                startActivityForResult(intent, Constants.REQUEST_NOTIFYDETAIL_CAPTURE);
            }
        }, new NotifyDetailSendYunhuAdapter.ButtonOnLongClickListener() {

            @Override
            public void orderLongClick(View v, int position, String orderNo) {
                showTextPop = new ShowTextPop(mContext, orderNo);
                showTextPop.showAsDropDown(v, 0, (-v.getHeight()) * 2);
            }
        });
        list_mobile.setAdapter(adapter);
        adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));

        // 派件列表群发短信带过来的单号
        if (!Utility.isEmpty(dispatchListData) && 0 != dispatchListData.size()){
            for (int i = 0; i < dispatchListData.size(); i++) {
                cloudVoiceMsgDetailEntries.get(i).setOrder_no(dispatchListData.get(i).getDh());
            }
            adapter.setListCount(YUNHU_MAX_LIST_COUNT);
            adapter.showAllItem();
            // 将运单号拼接起来
            String orderNum = "";
            for (NumberPhonePair npp : dispatchListData) {
                orderNum = orderNum + npp.getDh() + "|";
            }
            orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
            // 调用接口查找运单号是否有对应的手机号
            getPhoneNumberByOrderNo(orderNum);
        }


        // 如果是从草稿箱中进来 的
        if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {

            if (temporaryDraftInfo != null) {
                draft_id = temporaryDraftInfo.getId();
                String phoneNumber = temporaryDraftInfo.getPhoneNumber();
                String number = temporaryDraftInfo.getNumber();
                String orderNumber = temporaryDraftInfo.getOrderNumber();
                String[] numberArr = null;
                String[] phoneNumberArr = null;
                String[] orderNumberArr = null;
                if (!Utility.isEmpty(number))
                    numberArr = DraftBoxUtility.strToArr(number);
                if (!Utility.isEmpty(phoneNumber))
                    phoneNumberArr = DraftBoxUtility.strToArr(phoneNumber);
                if (!Utility.isEmpty(orderNumber))
                    orderNumberArr = DraftBoxUtility.strToArr(orderNumber);
                for (int i = 0; i < cloudVoiceMsgDetailEntries.size(); i++) {
                    CloudVoiceMsgDetailEntry info = cloudVoiceMsgDetailEntries.get(i);
                    if (numberArr!=null)
                        info.setMobile_no(numberArr[i].trim());
                    if (phoneNumberArr!=null)
                        info.setMobile(phoneNumberArr[i].trim());
                    if (orderNumberArr!=null)
                        info.setOrder_no(orderNumberArr[i].trim());
                }
                adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
                adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));

                voice_ivid = temporaryDraftInfo.getModelId();
                if (!Utility.isEmpty(voice_ivid)) {
                    haveVoiceModel(voice_ivid);
                } else {
                    btn_add_cloud_voice.setVisibility(View.VISIBLE);
                    ll_voice_model.setVisibility(View.GONE);
                    UtilToolkit.showToast("请选择一个语音模板");
                }
            }
        }
        RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
    }

    /**
     * 显示和刷新模板功能
     */
    private void showModel() {
        btn_add_cloud_voice.setVisibility(View.VISIBLE);
        ll_voice_model.setVisibility(View.GONE);
        // 获取所有的模板并找出已经被选中过的
        cloudRecords = skuaidiDB.getCloudRecordModels();// 取得所有录音模板
        // 判断模板列表是否为空
        if (null == cloudRecords || 0 == cloudRecords.size())
            return;
        CloudRecord cRecord = null;
        // 循环遍历找出已经被选择过的那一条录音模板

        for (int i = 0; i < cloudRecords.size(); i++) {
            if (cloudRecords.get(i).isChoose()) {
                cRecord = cloudRecords.get(i);
                break;
            }
        }
        // 判断是否存在已经被选择的模板
        if (null != cRecord) {
            isFind = true;
            // 获取模板参数
            btn_add_cloud_voice.setVisibility(View.GONE);
            ll_voice_model.setVisibility(View.VISIBLE);

            voice_ivid = cRecord.getIvid();
            voice_length = cRecord.getVoiceLength();
            voice_path_local = cRecord.getPathLocal();
            voice_path_service = cRecord.getPathService();
            voice_title = cRecord.getTitle();
            voice_name = cRecord.getFileName();
            // 设置模板参数显示
            tv_vocie_title.setText(voice_title);
            String time_str = Utility.formatTime(voice_length);
            tv_rec_total_time.setText(time_str);
        }

    }

    /**
     * 获取保存编号
     **/
    private SaveNoEntry getSaveNoEntry(String save_letter, int save_number) {
        SaveNoEntry saveNoEntry = new SaveNoEntry();
        saveNoEntry.setSave_from(SaveNoDAO.NO_CLOUD_BACHSIGN);
        saveNoEntry.setSaveTime(System.currentTimeMillis());
        saveNoEntry.setSave_userPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
        saveNoEntry.setSave_letter(save_letter);
        saveNoEntry.setSave_number(save_number);
        return saveNoEntry;
    }

    /**
     * 自动判断手机号码是否符合规则，自动去掉不必要的标识字符(用于输入单号自动获取手机号)
     **/
    private void autoJudgeMobilePhone2(int i, String mobilePhone) {
        if (clickIndex != -1) {
            if (clickIndex+i >= YUNHU_MAX_LIST_COUNT || !Utility.isEmpty(cloudVoiceMsgDetailEntries.get(clickIndex + i).getMobile()))// 测试要求存在手机号则不覆盖输入的手机号-此处留证
                return;
            if (Utility.isEmpty(mobilePhone)){
                cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile("");
                return;
            }
            cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile(SendMSGActivity.formatPhoneNumber(mobilePhone));
        } else {// 包括从e3中上传单号并发送短信传递过来的单号的自动获取手机号会执行此句代码
            if (Utility.isEmpty(mobilePhone)){
                cloudVoiceMsgDetailEntries.get(i).setMobile("");
                return;
            }
            cloudVoiceMsgDetailEntries.get(i).setMobile(SendMSGActivity.formatPhoneNumber(mobilePhone));
        }
        /*
        if (clickIndex != -1) {
            if (!Utility.isEmpty(mobilePhone)) {
                if (Utility.isEmpty(cloudVoiceMsgDetailEntries.get(clickIndex + i).getMobile())) {
                    if ((mobilePhone.indexOf("86") == 0 && mobilePhone.indexOf("1", 2) == 2 && mobilePhone.substring(2).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile(mobilePhone.substring(2));
                    } else if ((mobilePhone.indexOf("+86") == 0 && mobilePhone.indexOf("1", 2) == 3 && mobilePhone.substring(3).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile(mobilePhone.substring(3));
                    } else if ((mobilePhone.indexOf("0086") == 0 && mobilePhone.indexOf("1", 2) == 4 && mobilePhone.substring(4).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile(mobilePhone.substring(4));
                    } else if ((mobilePhone.indexOf("17951") == 0 && mobilePhone.indexOf("1", 5) == 5 && mobilePhone.substring(5).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile(mobilePhone.substring(5));
                    } else {
                        cloudVoiceMsgDetailEntries.get(clickIndex + i).setMobile(mobilePhone);
                    }
                }
            }
        } else {// 包括从e3中上传单号并发送短信传递过来的单号的自动获取手机号会执行此句代码
            if (!Utility.isEmpty(mobilePhone)) {
                if (Utility.isEmpty(cloudVoiceMsgDetailEntries.get(i).getMobile())) {
                    if ((mobilePhone.indexOf("86") == 0 && mobilePhone.indexOf("1", 2) == 2 && mobilePhone.substring(2).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(i).setMobile(mobilePhone.substring(2));
                    } else if ((mobilePhone.indexOf("+86") == 0 && mobilePhone.indexOf("1", 2) == 3 && mobilePhone.substring(3).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(i).setMobile(mobilePhone.substring(3));
                    } else if ((mobilePhone.indexOf("0086") == 0 && mobilePhone.indexOf("1", 2) == 4 && mobilePhone.substring(4).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(i).setMobile(mobilePhone.substring(4));
                    } else if ((mobilePhone.indexOf("17951") == 0 && mobilePhone.indexOf("1", 5) == 5 && mobilePhone.substring(5).length() == 11)) {
                        cloudVoiceMsgDetailEntries.get(i).setMobile(mobilePhone.substring(5));
                    } else {
                        cloudVoiceMsgDetailEntries.get(i).setMobile(mobilePhone);
                    }
                }
            }
        }*/

    }

    /**
     * 数据库中是否有草稿箱中进来的这条模板，如果有则显示，如果没有则提示
     **/
    private void haveVoiceModel(String voice_ivid) {
        btn_add_cloud_voice.setVisibility(View.VISIBLE);
        ll_voice_model.setVisibility(View.GONE);
        // 获取所有的模板
        cloudRecords = skuaidiDB.getCloudRecordModels();// 取得所有录音模板
        // 判断模板列表是否为空
        if (null == cloudRecords || 0 == cloudRecords.size())
            return;
        CloudRecord cRecord = null;
        // 循环遍历找出草稿箱中的模板是哪一条
        for (int i = 0; i < cloudRecords.size(); i++) {
            if (cloudRecords.get(i).getIvid().equals(voice_ivid)) {
                cRecord = cloudRecords.get(i);
                break;
            }
        }
        // 判断是否存在已经被选择的模板
        if (null != cRecord) {
            isFind = true;
            // 获取模板参数
            btn_add_cloud_voice.setVisibility(View.GONE);
            ll_voice_model.setVisibility(View.VISIBLE);

            voice_length = cRecord.getVoiceLength();
            voice_path_local = cRecord.getPathLocal();
            voice_path_service = cRecord.getPathService();
            voice_title = cRecord.getTitle();
            voice_name = cRecord.getFileName();
            // 设置模板参数显示
            tv_vocie_title.setText(voice_title);
            String time_str = Utility.formatTime(voice_length);
            tv_rec_total_time.setText(time_str);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAutoSendMsg:// 关闭自动补发短信按钮
                llAutoSendMsg.setVisibility(View.GONE);
                cancelAutoSendMsgTishi();
                break;
            case R.id.iv_MsgMenuIcon:
                stopDistinguish();
                menuDialog.show();
                break;
            case R.id.iv_title_back:// 返回按钮
                finishActivity();

                break;
            case R.id.tv_more:// 发送按钮
                stopDistinguish();
                UMShareManager.onEvent(mContext, "Cloud_send_msg", "CloudVoice", "云呼：发送");
                sendVoiceMsg(v);// 发送语音电话

                break;
            case R.id.btn_add_cloud_voice:// 新增云呼语音
                stopDistinguish();
                UMShareManager.onEvent(mContext, "Cloud_select_model", "CloudVoice", "云呼：选择语音模板");
                mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                startActivityForResult(mIntent, ENTER_CHOOSE_MODEL);
                break;
            case R.id.ll_voice_model:// 显示模板区域（可点击进入选择模板）
                stopDistinguish();
                UMShareManager.onEvent(mContext, "Cloud_select_model", "CloudVoice", "云呼：选择语音模板");
                stopPlayRecord();
                mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                startActivityForResult(mIntent, ENTER_CHOOSE_MODEL);
                break;
            case R.id.ll_play_icon:// 模板语音播放按钮
                UMShareManager.onEvent(mContext, "Cloud_model_play", "CloudVoice", "云呼：模板语音播放按钮");
                if (isPlaying) {
                    stopPlayRecord();

                } else {
                    if (Utility.isEmpty(voice_path_local)) {
                        UtilToolkit.showToast("模板有误，请重新选择");
                        return;
                    }
                    File file = new File(voice_path_local);
                    if (file.exists()) {// 如果存在文件
                        myRunnable = new MyRunnable(voice_length);
                        mThread = new Thread(myRunnable);
                        mThread.start();
                        startPlayRecord(voice_path_local);
                    } else {
                        if (!Utility.getSDIsExist()) {
                            UtilToolkit.showToast("SD卡不存在");
                            return;
                        }
                        File voiceDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord");
                        if (!voiceDirectory.exists()) {
                            voiceDirectory.mkdirs();
                        }
                        FinalHttp fh = new FinalHttp();
                        fh.download(voice_path_service, Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/" + voice_name
                                + ".wav", new AjaxCallBack<File>() {
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                            }

                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                myRunnable = new MyRunnable(voice_length);
                                mThread = new Thread(myRunnable);
                                mThread.start();
                                startPlayRecord(voice_path_local);
                            }

                            @Override
                            public void onFailure(Throwable t, int errorNo, String strMsg) {
                                super.onFailure(t, errorNo, strMsg);
                                UtilToolkit.showToast("录音下载失败或已不存在该录音，请删除");
                            }
                        });
                    }

                }
                break;

            case R.id.switchList:
                if (isShowAll) {// 将收起设置为展开
                    int maxNum = getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries);
                    adapter.showItems(maxNum);
                    switchText.setText("展开");
                    switchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_open, 0, 0, 0);
                    isShowAll = false;

                } else {// 将展开设置为收起
                    adapter.showAllItem();
                    switchText.setText("收起");
                    switchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.all_close, 0, 0, 0);
                    isShowAll = true;
                }
                break;
            case R.id.ll_yunhu_timing:
                cancelTransmission();
                break;
            default:
                break;
        }
    }

    private void cancelTransmission() {
        tv_sendTime_tag.setText("定时发送");
        isTimingTransmission = false;
        timeStamp = 0;
        ll_yunhu_timing.setVisibility(View.GONE);
    }

    private void timingTransmission() {
        timeStamp = pop.getTimeStamp();
        tv_sendTime_tag.setText(pop.getSendTimeStr());// 设置checkbox旁边文本框显示选择的时间
        tv_yunhu_timing_flag.setText(pop.getSendTimeStr());
        ll_yunhu_timing.setVisibility(View.VISIBLE);
        isTimingTransmission = true;
        pop.dismiss();
    }

    TextView tv_sendTime_tag;
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvBroadCast:
                    stopDistinguish();
                    UMShareManager.onEvent(mContext, "Cloud_broadcast", "CloudVoice", "云呼：查看广播通知");
                    stopPlayRecord();
                    loadWeb(Constants.URL_NOTIFY_YUNHU + SkuaidiSpf.getLoginUser().getPhoneNumber(), "");
                    break;
                case R.id.ll_ok:
                    if (pop.isMoreThanTheCurrent10Minutes()) {
                        timingTransmission();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void sendVoiceMsg(View v) {
        if (!isFind) {
            UtilToolkit.showToast("请选择一个语音模板");
            return;
        }
        tv_more.setEnabled(false);

        List<CloudVoiceMsgDetailEntry> cvmne = new ArrayList<>();
        for (int i = 0; i < adapter.getVoiceMsgDetail().size(); i++) {
            CloudVoiceMsgDetailEntry cvmsDetailEntry;
            if (!Utility.isEmpty(adapter.getVoiceMsgDetail().get(i).getMobile())) {
                cvmsDetailEntry = adapter.getVoiceMsgDetail().get(i);
                String num = cvmsDetailEntry.getMobile();
                if (!Utility.isEmpty(num) && (num.length() >= 11 && num.length() <= 12) && StringUtil.judgeStringEveryCharacterIsNumber(num)) {// indexOf判断字符串中首次出现+86出现的位置从0开始，没有返回-1
                    cvmne.add(cvmsDetailEntry);
                } else {
                    tv_more.setEnabled(true);
                    UtilToolkit.showToast("编号为" + cvmsDetailEntry.getMobile_no() + "的手机号有误");
                    return;
                }
            }
        }

        boolean exist = getExistTheSameMobilePhone(cvmne);
        if (exist) {
            tv_more.setEnabled(true);
            SkuaidiDialog dialog = new SkuaidiDialog(mContext);
            dialog.setTitle("提示");
            dialog.setContent("编号：" + cvmne.get(i).getMobile_no() + "  和编号：" + cvmne.get(j).getMobile_no() + "  的手机号相同，是否返回修改？");
            dialog.isUseEditText(false);
            dialog.setPositionButtonTitle("继续发送");
            dialog.setNegativeButtonTitle("返回修改");
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                @Override
                public void onClick(View v) {
                    call();
                }
            });
            dialog.showDialog();
        } else {
            call();
        }
    }

    /**
     * 返回用户是否对数据进行过修改 true:没修改过
     **/
    private boolean isModify() {
        if (!temporaryDraftInfo.getModelId().equals(voice_ivid)) {
            return true;
        }
        if (!temporaryDraftInfo.getNumber().equals(DraftBoxUtility.pinjieNumberCloud(adapter.getVoiceMsgDetail()))) {
            return true;
        }
        if (!temporaryDraftInfo.getPhoneNumber().equals(DraftBoxUtility.pinjiePhoneNumberCloud(adapter.getVoiceMsgDetail()))) {
            return true;
        }
        if(!Utility.isEmpty(temporaryDraftInfo.getOrderNumber())) {// 如果草稿箱中的单号不为空
            if (!temporaryDraftInfo.getOrderNumber().equals(DraftBoxUtility.pinjieOrderNumberCloud(adapter.getVoiceMsgDetail())))
                return true;
        }else{// 草稿箱中的数据为空【这种情况一般出现在4.6.x升级到4.7以上版本会出现-4.7之前的版本云呼草稿中没有单号保存】
            String orderarr = DraftBoxUtility.pinjieOrderNumberCloud(adapter.getVoiceMsgDetail());
            if (orderarr.contains(","))
                orderarr = orderarr.replace(",","").replaceAll(" ","");
            if (!Utility.isEmpty(orderarr))
                return true;
        }
        return false;
    }

    SkuaidiDialogGrayStyle dialog;

    private void finishActivity() {
        stopDistinguish();
        // *********************************(判断列表中是否有数据)*********************(以下)
        boolean hasData = false;
        List<CloudVoiceMsgDetailEntry> list = adapter.getVoiceMsgDetail();
        CloudVoiceMsgDetailEntry cvmde;
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                cvmde = list.get(i);
                if (!Utility.isEmpty(cvmde.getMobile())) {
                    hasData = true;
                    break;
                }
            }
        }
        // *********************************(判断列表中是否有数据)*********************(以上)
        if (hasData) {// 列表中存在手机号码
            if (!Utility.isEmpty(fromActivity) && fromActivity.equals("draftbox") && !isModify()) {// 从草稿箱进来的，并且没有对内容进行修改
                stopPlayRecord();
                finish();
            } else {
                if (null != dialog) {
                    dialog.dismiss();
                    dialog = null;
                } else {
                    dialog = new SkuaidiDialogGrayStyle(mContext);
                    dialog.setTitleGray("离开提示");
                    if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {// 如果是从草稿箱中而来
                        dialog.setContentGray("云呼草稿已被修改，\n是否保存修改后的内容？");
                    } else {
                        dialog.setContentGray("是否将编辑过的云呼内容保存到草稿箱？");
                    }
                    dialog.setPositionButtonTextGray("是");
                    dialog.setNegativeButtonTextGray("否");
                    dialog.showDialogGray(iv_title_back);
                    dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {
                        @Override
                        public void onClick(View v) {
                            SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("voice");
                            RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
                            stopPlayRecord();
                            finish();
                        }
                    });
                    dialog.setNegativeButtonClickListenerGray(new NegativeButtonOnclickListenerGray() {

                        @Override
                        public void onClick() {

                            if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {
                                RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(false));
                            } else {
                                RecordDraftBoxCloudVoiceDAO.deleteDraftByID(draft_id);
                                SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("voice");
                            }
                            stopPlayRecord();
                            finish();
                        }
                    });
                }
            }

        } else {
            // 如果是从草稿箱中进来 的
            if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {
                // 如果没有选择模板
                if (Utility.isEmpty(voice_ivid)) {
                    RecordDraftBoxCloudVoiceDAO.deleteDraftByID(draft_id);
                } else {
                    RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(false));
                }
            } else {
                RecordDraftBoxCloudVoiceDAO.deleteDraftByID(draft_id);
            }
            stopPlayRecord();
            finish();

        }
    }

    /**
     * 自定义编号
     */
    private void customNo(View v, final int position, final List<CloudVoiceMsgDetailEntry> cloudDetailEntries) {
        UMShareManager.onEvent(mContext, "Cloud_CustomNo", "CloudVoice", "云呼：自定义编号");
        final SkuaidiDialog dialog = new SkuaidiDialog(mContext);
        dialog.setTitle("自定义起始编号");
        dialog.isUseEditText(true);
        dialog.setPositionButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");
        dialog.setEditTextInputTypeStyle(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);// 设置弹出键盘为数字键盘
        dialog.setEditTextContent(5);
        dialog.setEditTextHint("请输入起始编号，最大限度99999");
        dialog.setPosionClickListener(new PositonButtonOnclickListener() {

            @Override
            public void onClick(View v) {
                if (!dialog.getEditTextContent().trim().isEmpty()) {
                    try {
                        int customNo = Integer.parseInt(dialog.getEditTextContent());// 将输入编号转为整型
                        if (customNo <= 99999) {
                            for (int i = position; i < cloudDetailEntries.size(); i++) {
                                CloudVoiceMsgDetailEntry cloudDetailEntry = cloudDetailEntries.get(i);
                                if (customNo > 99999) {
                                    customNo = 1;
                                }
                                cloudDetailEntry.setMobile_no(customNo + "");
                                customNo++;
                            }
                            adapter.setVoiceMsgDetail(cloudDetailEntries);
                            RecordDraftBoxCloudVoiceDAO.updateDraftNumber(setDraftBoxNumberInfoCloud(adapter.getVoiceMsgDetail()));
                        } else {
                            UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        dialog.showDialog();
    }

    /**
     * (云呼记录)设置数据-用于更新草稿箱中条目的手机号码
     **/
    private DraftBoxCloudVoiceInfo setDraftBoxPhoneNumberInfoCloud(List<CloudVoiceMsgDetailEntry> cvme) {
        DraftBoxCloudVoiceInfo info = new DraftBoxCloudVoiceInfo();
        info.setSaveTime(System.currentTimeMillis());
        info.setId(draft_id + "");
        info.setPhoneNumber(DraftBoxUtility.pinjieNumberCloud(cvme));
        return info;
    }

    /**(云呼记录)设置数据-用于更新草稿箱中条目的编号**/
    private DraftBoxCloudVoiceInfo setDraftBoxNumberInfoCloud(List<CloudVoiceMsgDetailEntry> cvme){
        DraftBoxCloudVoiceInfo info = new DraftBoxCloudVoiceInfo();
        info.setSaveTime(System.currentTimeMillis());
        info.setId(draft_id + "");
        info.setNumber(DraftBoxUtility.pinjieNumberCloud(cvme));
        return info;
    }

    private void call() {
        List<CloudVoiceMsgDetailEntry> cvmne = adapter.getVoiceMsgDetail();

        List<SendCloudVoiceParameter> scvps = new ArrayList<>();
        for (int i = 0; i < cvmne.size(); i++) {
            if (!Utility.isEmpty(cvmne.get(i).getMobile())) {
                SendCloudVoiceParameter scvp = new SendCloudVoiceParameter();
                scvp.setNo(cvmne.get(i).getMobile_no());
                scvp.setPhone(cvmne.get(i).getMobile());
                scvp.setDh(cvmne.get(i).getOrder_no());
                if (timeStamp != 0) {
                    scvp.setSend_time(timeStamp);
                }
                scvps.add(scvp);
                String last_no = cvmne.get(i).getMobile_no();
                saveNoEntry = getSaveNoEntry("", Integer.parseInt(last_no) + 1);
            }
        }

        if (scvps.size() == 0) {
            UtilToolkit.showToast("请至少输入一个手机号");
            tv_more.setEnabled(true);
            return;
        }

        Gson gson = new Gson();
        String call_data_json = gson.toJson(scvps);

        cloudCall(voice_ivid, SmsModelID, call_data_json);

    }

    private boolean haveData() {
        List<CloudVoiceMsgDetailEntry> cvmne = adapter.getVoiceMsgDetail();
        for (int i = 0; i < cvmne.size(); i++) {
            if (!Utility.isEmpty(cvmne.get(i).getMobile())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取列表中是否在在相同的手机号
     */
    private boolean getExistTheSameMobilePhone(List<CloudVoiceMsgDetailEntry> cvmne) {
        boolean exist = false;
        for (int i = 0; i < cvmne.size() - 1; i++) {
            for (int j = i + 1; j < cvmne.size(); j++) {
                if (cvmne.get(i).getMobile().equals(cvmne.get(j).getMobile())) {
                    this.i = i;
                    this.j = j;
                    exist = true;
                    break;
                }
            }
            if (exist) {
                break;
            }
        }
        return exist;
    }

    class MyRunnable implements Runnable {
        int m_CurProgress = 0;// 当前进度
        int m_maxLength = 0;

        public MyRunnable(int maxLength) {
            super();
            this.m_maxLength = maxLength;
        }

        public void setRunnableStop() {
            m_CurProgress = m_maxLength + 2;
        }

        @Override
        public void run() {
            while (m_CurProgress <= m_maxLength) {
                Message msg = mHandler.obtainMessage();
                msg.what = PLAYING_VOICE_ING;
                m_CurProgress = m_CurProgress + 1;
                msg.arg1 = m_CurProgress;
                msg.arg2 = m_maxLength;
                msg.sendToTarget();
                try {
                    Thread.sleep(1000);// 每隔一秒执行一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (m_CurProgress > m_maxLength) {
                Message msg = mHandler.obtainMessage();
                msg.what = PLAYING_VOICE_ING;
                m_CurProgress = m_CurProgress + 1;
                msg.arg1 = m_CurProgress;
                msg.arg2 = m_maxLength;
                msg.sendToTarget();
                try {
                    Thread.sleep(1000);// 每隔一秒执行一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 开始播放录音
     * 顾冬冬
     */
    private void startPlayRecord(String path) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    mPlayer = null;
                }
            });
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放录音
     * 顾冬冬
     */
    private void stopPlayRecord() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
        }
        if (null != myRunnable) {
            myRunnable.setRunnableStop();
        }
        if (null != mThread) {
            mThread.interrupt();
            mThread = null;
            isPlaying = false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENTER_CHOOSE_MODEL && resultCode == OUT_CHOOSE_MODEL) {
            showModel();
            if (haveData()) {
                RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
            }
        } else if (requestCode == ENTER_CHOOSE_MOBILE && resultCode == OUT_CHOOSE_MOBILE) {
            cloudVoiceMsgDetailEntries = (List<CloudVoiceMsgDetailEntry>) data.getSerializableExtra("cloudMessage");
            adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
            adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));
        } else if (requestCode == ENTER_CHOOSE_MOBILE && resultCode == OUT_CHOOSE_MOBILE_SEND) {
            cloudVoiceMsgDetailEntries = (List<CloudVoiceMsgDetailEntry>) data.getSerializableExtra("cloudMessage");
            adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
            adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));
            sendVoiceMsg(tv_more);// 发送语音电话
        }
        if (requestCode == REQUEST_MSG_MODEL) {

            alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_VOICE_SEND_VOICE).builder().setCancelable(true);
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
            Message msg = new Message();
            msg.what = AGAIN_SHOW_MODEL;
            mHandler.sendMessage(msg);

            alertDialog = new SkuaidiAlertDialogSendMsg(mContext, SkuaidiAlertDialogSendMsg.FROM_VOICE_SEND_MSG).builder().setCancelable(true);
            alertDialog.setSendInfo(sendDesc);
            alertDialog.setOnclickListener(new BtnOnClickListener() {

                @Override
                public void sendMsg(String modelID, String modelStatus) {
                    if ("1".equals(modelStatus)) {
                        sendNoSigned(SEND_TYPE_CLOUD, modelID);
                        showProgressDialog( "请稍候");
                        alertDialog.dismiss();
                    } else {
                        UtilToolkit.showToast("请选择已审核的模板");
                    }
                }

                @Override
                public void chooseModel() {
                    mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                    startActivityForResult(mIntent, REQUEST_VOICE_MODEL);
                }
            });
            alertDialog.show();
        } else if (requestCode == REQUEST_AUTO_SEND_MSG && resultCode == RESULT_AUTO_SEND_MSG) {
            if (null != data) {
                boolean isChoose = data.getBooleanExtra("isChoose", false);// 云呼失败自动补发短信-是否已经选择过一个短信模板
                if (isChoose) {
                    getAutoSendMsgSmsModelData();
                    showAutoSendMsgTishi();
                }
            }
        } else if (requestCode == Constants.REQUEST_NOTIFYDETAIL_CAPTURE && resultCode == Constants.RESULT_NOTIFYDETAIL_CAPTURE) {
            List<CloudVoiceMsgDetailEntry> notifyInfos = (List<CloudVoiceMsgDetailEntry>) data.getSerializableExtra("cloudInfos");
            if (notifyInfos != null) {
                for (int i = 0; i < notifyInfos.size(); i++) {
                    if (-1 != clickIndex) {
                        if (clickIndex + i < cloudVoiceMsgDetailEntries.size()) {
                            cloudVoiceMsgDetailEntries.get(clickIndex + i).setOrder_no(notifyInfos.get(i).getOrder_no());
                        }
                    } else {
                        if (i < YUNHU_MAX_LIST_COUNT)
                            cloudVoiceMsgDetailEntries.get(i).setOrder_no(notifyInfos.get(i).getOrder_no());
                    }
                }
                adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
            } else {
                return;
            }

            // 将运单号拼接起来
            String orderNum = "";
            for (int i = 0; i < notifyInfos.size(); i++) {
                orderNum = orderNum + notifyInfos.get(i).getOrder_no() + "|";
            }
            if (orderNum.lastIndexOf("|") > 0)
                orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
            getPhoneNumberByOrderNo(orderNum);// 【接口】
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getAutoSendMsgSmsModelData() {
        List<ReplyModel> models = skuaidiDB.getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN);
        if (null == models || 0 == models.size())
            return;

        ReplyModel model = new ReplyModel();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).isChoose()) {
                model = models.get(i);
                break;
            }
        }
        SmsModelID = model.getTid();// 获取数据库中被选中的模板ID
        SmsModelTitle = model.getTitle();// 获取数据库中被选中的模板title
    }

    private void showAutoSendMsgTishi() {
        llAutoSendMsg.setVisibility(View.VISIBLE);
        if (tvModelTitle != null) {
            tvModelTitle.setText(SmsModelTitle);
        } else if (null != sendMsgTitleView) {
            tvModelTitle = (TextView) sendMsgTitleView.findViewById(R.id.tvModelTitle);
            tvModelTitle.setText(SmsModelTitle);
        }
    }

    private void cancelAutoSendMsgTishi() {
        llAutoSendMsg.setVisibility(View.GONE);
        tvModelTitle.setText("派件专用短信");
        SmsModelTitle = "";
        SmsModelID = "";
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

    /**
     * 设置数据;true:直接进入发送短信界面的时候设置的;false:从草稿箱传进来的原数据，用于在退出时还原数据
     */
    private DraftBoxCloudVoiceInfo setDraftBoxInfo(boolean newData) {
        if (newData) {
            DraftBoxCloudVoiceInfo info = new DraftBoxCloudVoiceInfo();
            info.setSaveTime(curTime);
            info.setId(draft_id + "");
            if (null != adapter) {
                info.setNumber(DraftBoxUtility.pinjieNumberCloud(adapter.getVoiceMsgDetail()));
                info.setPhoneNumber(DraftBoxUtility.pinjiePhoneNumberCloud(adapter.getVoiceMsgDetail()));
                info.setOrderNumber(DraftBoxUtility.pinjieOrderNumberCloud(adapter.getVoiceMsgDetail()));
            } else {
                info.setNumber(DraftBoxUtility.pinjieNumberCloud(cloudVoiceMsgDetailEntries));
                info.setPhoneNumber(DraftBoxUtility.pinjiePhoneNumberCloud(cloudVoiceMsgDetailEntries));
                info.setOrderNumber(DraftBoxUtility.pinjieOrderNumberCloud(cloudVoiceMsgDetailEntries));
            }
            info.setUserPhoneNum(SkuaidiSpf.getLoginUser().getPhoneNumber());
            info.setModelId(voice_ivid);
            info.setModelTitle(voice_title);
            return info;
        } else {
            return temporaryDraftInfo;
        }
    }

    /**
     * 获取列表中最后一条手机号码在列表中的位置,用于显示列表的条目数
     **/
    private int getLastPhoneNumberIndex(List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries) {
        boolean exist = false;
        int index = 1;
        if (cloudVoiceMsgDetailEntries != null && cloudVoiceMsgDetailEntries.size() != 0) {
            for (int i = cloudVoiceMsgDetailEntries.size() - 1; i >= 0; i--) {
                String cellNumber = cloudVoiceMsgDetailEntries.get(i).getMobile();
                if (!Utility.isEmpty(cellNumber)) {
                    if (i == cloudVoiceMsgDetailEntries.size() - 1) {
                        index = YUNHU_MAX_LIST_COUNT;
                    } else {
                        index = i + 2;
                    }
                    exist = true;
                } else {
                    index = 1;
                }
                if (exist) {
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 获取广播通知消息
     **/
    private void getBroadCast() {
        ApiWrapper apiWrapper = new ApiWrapper();
        subscription = apiWrapper.getBoardCastNotify("inform.broadcast","get","ivr").subscribe(new Action1<com.alibaba.fastjson.JSONObject>() {
            @Override
            public void call(com.alibaba.fastjson.JSONObject jsonObject) {
                if (jsonObject != null){
                    if (jsonObject.getString("status").equals("success")){
                        final String content = jsonObject.getJSONObject("result").getJSONObject("retArr").getString("content");
                        notifyBroadCast = new NotifyBroadCast(SendYunHuActivity.this, 10, srlTitle2, content, onClickListener);
                        srlTitle2.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyBroadCast.show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void getYunhuSettingState() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", VALID_DISPLAY_NUMBER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 发送云语音
     **/
    private void cloudCall(String ivid, String sms_tid, String call_data) {
        showProgressDialog( "请稍候...");
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr/ivrCall");
            data.put("ivid", ivid);
            data.put("sms_tid", sms_tid);
            // 云呼接收短信开关[y-代表允许发短信，n-代表不允许发短信]
            if (SkuaidiSpf.getWhetherCanReceiveMSG(mContext)) {
                data.put("enable_sms", "y");
            } else {
                data.put("enable_sms", "n");
            }
            data.put("call_data", call_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 获取昨日未取件手机号信息（短信条数）
     **/
    private void getNoSignedCloudInfo(String send_type) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_nosigned/getivrinfo");
            data.put("send_type", send_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 给昨日未取件手机号发短信&云呼
     **/
    private void sendNoSigned(int SendType, String modelId) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_nosigned/send");
            data.put("type", "failivr");
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

    /**
     * 通过单号自动获取手机号
     **/
    private void getPhoneNumberByOrderNo(String orderNum) {
        if (TextUtils.isEmpty(orderNum))
            return;
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("sname", "express.contacts");
            jsonData.put("no", orderNum);
            jsonData.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(jsonData, false, INTERFACE_VERSION_NEW);
    }

//	String settingState;


    /**
     * 开始识别
     **/
    private void startDistinguish() {
        if (wakeLock != null) {
            try {
                wakeLock.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 以下开始语音识别
        if (null != mIatResults && mIatResults.size() != 0) {
            mIatResults.clear();
        }
        setParam();
        // 不显示听写对话框
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            UtilToolkit.showToast_Custom("听写失败,错误码：" + ret);
        } else {
            UtilToolkit.showToast_Custom("开始说话");
        }
        // UtilToolkit.showToast("建议在wifi下使用,在线语音识别会消耗流量");
    }

    /**
     * 参数设置
     */
    public void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);// 清空参数
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);// 设置听写引擎
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");// 设置返回结果格式
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");// 设置语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");// 设置语言区域
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_EOS, "10000");// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，
        // 自动停止录音
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, "0");
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            //Log.d(TAG, "初始化失败，错误码： " + code);
            if (code != ErrorCode.SUCCESS) {
                UtilToolkit.showToast_Custom("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            // UtilToolkit.showToast("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            // UtilToolkit.showToast(error.getPlainDescription(true));
            // if(error.getErrorCode()==10118){
            // UtilToolkit.showToast("您的录音权限可能被禁，请到设置中打开");
            // }else
            if (error.getErrorCode() == 20001) {
                UtilToolkit.showToast("网络连接错误，请稍候再试");
                stopDistinguish();
            } else if (error.getErrorCode() == 10118) {
            } else if (error.getErrorCode() == 20006) {
                stopDistinguish();
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            // UtilToolkit.showToast("结束说话");

            if (speechCount == 6) {
                stopDistinguish();
                speechCount = 0;
            } else {
                speechCount++;
                startDistinguish();
            }

        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            //Log.d(TAG, results.getResultString());
            if (!isFinishing()) {
                printResult(results);
            }
        }

        @Override
        public void onVolumeChanged(final int volume, byte[] data) {
            // showTip("当前正在说话，音量大小：" + volume);
            // //Log.i("GUDDD", volume+"");
            // //Log.d(TAG, "返回音频数据："+data.length);

            // animHandler.postDelayed(new Runnable() {
            // @Override
            // public void run() {
            // updateDisplay(ivSpeech, volume);// 录音时修改动画
            // }
            // }, 50);

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                //Log.d(TAG, "session id =" + sid);
            }
        }
    };

    /**
     * 将解析出来的文字返回并显示
     **/
    private void printResult(RecognizerResult results) {
        String text = YYSBJsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        int size = mIatResults.keySet().size();
        int i = 0;

        for (String key : mIatResults.keySet()) {
            i++;
            if (i == size) {
                word = mIatResults.get(key);
            }

        }

        // //Log.i("guddlog","1: "+ word);
        if (NotifySearchPhoneActivity.judgeWordIsZeroToNine(word)) {
            //Log.i("logi", word);
            if (word.length() < 11) {
                if (!Utility.isEmpty(sentence)) {
                    stopCountDown();
                    sentence = sentence + word;
                    startCountDown();
                } else {
                    sentence = word;
                    startCountDown();
                }
                if (sentence.length() == 11) {
                    word = sentence;
                    sentence = "";
                    stopCountDown();// 停止倒计时线程
                    shibieSuccess();
                } else if (sentence.length() > 11) {
                    stopCountDown();// 停止倒计时线程
                    UtilToolkit.showToast_Custom("错误的手机号:" + sentence);
                    sentence = "";
                }
            } else if (word.length() > 12) {
                sentence = "";
                stopCountDown();// 停止倒计时线程
                UtilToolkit.showToast_Custom("错误的手机号:" + word);
            } else {
                shibieSuccess();// 11位手机号码识别成功
            }
        } else {
            // UtilToolkit.showToast("请说手机号码");
        }
    }

    // private VoiceIndentifyShowText voiceIndentifyShowText = null;

    private void shibieSuccess() {
        // //Log.i("guddlog", word);
        if (!Utility.isEmpty(word) && word.length() >= 11 && word.length() <= 13) {// 手机号在11位到12位之间
            String firstChar = word.substring(0, 1);// 获取手机号第一个数字
            String secoundChar = word.substring(0, 2);// 获取手机号前2个数字
            if ("0".equals(firstChar)) {
                if ("01".equals(secoundChar) || "02".equals(secoundChar)) {
                    if (word.length() == 11) {
                        cloudVoiceMsgDetailEntries.get(distinguishMark).setMobile(word);
                        adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
                        setNextDistinguish();
                        Utility.playMusicDing();
                        if (!isFinishing()) {
                            UtilToolkit.showToast_Custom(word);
                        }
                        RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
                    } else {
                        UtilToolkit.showToast_Custom("错误的手机号:" + word);
                    }
                } else {
                    if (word.length() == 11 || word.length() == 12) {
                        cloudVoiceMsgDetailEntries.get(distinguishMark).setMobile(word);
                        adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
                        setNextDistinguish();
                        Utility.playMusicDing();
                        if (!isFinishing()) {
                            UtilToolkit.showToast_Custom(word);
                        }
                        RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
                    } else {
                        UtilToolkit.showToast_Custom("错误的固话号码:" + word);
                    }
                }
            } else if ("1".equals(firstChar)) {
                if (word.length() == 11) {
                    cloudVoiceMsgDetailEntries.get(distinguishMark).setMobile(word);
                    adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
                    setNextDistinguish();
                    Utility.playMusicDing();
                    if (!isFinishing()) {
                        UtilToolkit.showToast_Custom(word);
                    }
                    RecordDraftBoxCloudVoiceDAO.insertDraftInfo(setDraftBoxInfo(true));
                } else {
                    UtilToolkit.showToast_Custom("错误的固话号码:" + word);
                }
            } else {
                UtilToolkit.showToast_Custom("错误的固话号码:" + word);
            }
            if (isShowAll) {// 当前显示的是收起【状态：列表全部展开】
                adapter.showAllItem();
            } else {// 当前显示的是展开【状态：列表未全部展开】
                adapter.setListCount(getLastPhoneNumberIndex(cloudVoiceMsgDetailEntries));
            }
            list_mobile.setSelection(showListLine());
        }
    }

    private int showListLine() {
        if (distinguishMark < 2) {
            return distinguishMark;
        } else {
            return distinguishMark - 1;
        }
    }

    /**
     * 设置下一个条目进行识别
     **/
    private void setNextDistinguish() {
        cloudVoiceMsgDetailEntries.get(distinguishMark).setPlayVoiceAnim(false);
        distinguishMark++;
        if (distinguishMark == cloudVoiceMsgDetailEntries.size()) {
            distinguishMark = cloudVoiceMsgDetailEntries.size() - 1;
            stopDistinguish();
        } else {
            cloudVoiceMsgDetailEntries.get(distinguishMark).setPlayVoiceAnim(true);
        }
    }

    /**
     * 开始倒计时
     **/
    private void startCountDown() {
        myHandler = new MyHandler();
        mThread = new Thread(myHandler);
        mThread.start();
    }

    /**
     * 停止线程
     **/
    private void stopCountDown() {
        if (myHandler != null) {
            myHandler.setThreadStop();
        }
        if (null != mThread) {
            mThread.interrupt();
            mThread = null;
        }
    }

    class MyHandler implements Runnable {
        boolean isTrue = true;
        int time = 5;

        public MyHandler() {
            super();
        }

        public void setThreadStop() {
            isTrue = false;
        }

        @Override
        public void run() {
            while (isTrue) {
                try {
                    Thread.sleep(1000);
                    time--;
                    if (time == 0) {
                        isTrue = false;
                        sentence = "";
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 停止识别
     **/
    private void stopDistinguish() {
        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mIat.stopListening();
        setStopPlayAudioAnim();
    }

    // 结束录音后关闭动画
    private void setStopPlayAudioAnim() {
        if (distinguishMark != -1) {
            cloudVoiceMsgDetailEntries.get(distinguishMark).setPlayVoiceAnim(false);
            adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String message, String json, String act) {
        tv_more.setEnabled(true);
        dismissProgressDialog();
        Message msg = null;
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ("ivr/ivrCall".equals(sname)) {
            UtilToolkit.showToast(message);

            msg = new Message();
            msg.what = SEND_VOICE_SUCCESS;
        } else if ("inform_nosigned/getivrinfo".equals(sname)) {
            int needSendCount = -1;
            String sendMsg = "";
            try {
                if (null == result) {
                    UtilToolkit.showToast("数据获取异常");
                    return;
                }
                needSendCount = result.getInt("needSendCount");
                sendMsg = result.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg = new Message();
            msg.what = GET_NOSIGNED_INFO_SUCCESS;
            msg.arg1 = needSendCount;
            msg.obj = sendMsg;
        } else if ("inform_nosigned/send".equals(sname)) {
            if (!Utility.isEmpty(message)) {
                UtilToolkit.showToast(message);
            }
        } else if (VALID_DISPLAY_NUMBER.equals(sname)) {// 返回去电显号开启状态
            makeTelephoneNoEntry = new MakeTelephoneNoEntry();
            if (null == result) {
                UtilToolkit.showToast("数据获取异常");
                return;
            }
            makeTelephoneNoEntry.setMake_telephone_no(result.optString("phone"));
            makeTelephoneNoEntry.setIn_use(result.optString("in_use"));
            makeTelephoneNoEntry.setNew_apply_phone(result.optString("new_apply_phone"));
            makeTelephoneNoEntry.setNew_apply_phone_state(result.optString("new_apply_sate"));
            String phone = makeTelephoneNoEntry.getMake_telephone_no();
            String in_use = makeTelephoneNoEntry.getIn_use();


            if (!Utility.isEmpty(makeTelephoneNoEntry.getMake_telephone_no()) && !Utility.isEmpty(in_use) && "y".equals(in_use)) {
                if ("y".equals(in_use)) {
                    menuDialog.modifyYuhuSettingState(phone);
                } else if ("n".equals(in_use)) {
                    menuDialog.modifyYuhuSettingState("未开启");
                }
            } else {
                menuDialog.modifyYuhuSettingState("未设置");
            }
        } else if (!Utility.isEmpty(sname) && "express.contacts".equals(sname)) {// 调用接口获取收件人信息
            List<ReceiverInfo> receiverInfo = JsonXmlParser.parseReceiverInfo(result);
            msg = new Message();
            msg.what = GET_PHONENUMBER_SUCCESS;
            msg.obj = receiverInfo;
        }

        if (null != mHandler && null != msg) {
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        tv_more.setEnabled(true);
        dismissProgressDialog();
        if (!Utility.isEmpty(result)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        if (!Utility.isEmpty(msg)) {
            UtilToolkit.showToast(msg);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getYunhuSettingState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (notifyBroadCast != null) {
            notifyBroadCast.dismiss();
            if(notifyBroadCast.mTimer!=null){
                notifyBroadCast.mTimer.cancel();
                notifyBroadCast.mTimer=null;
            }
            if(notifyBroadCast.task!=null){
                notifyBroadCast.task.cancel();
                notifyBroadCast.task=null;
            }
            notifyBroadCast=null;
        }
        if(mIat!=null){
            mIat.cancel();
            mIat.destroy();
            mIat=null;
        }
        super.onDestroy();
    }

    @Subscribe
    public void onEventMainThread(UpdateList event) {
        String msg = event.getMsg();
        if (!Utility.isEmpty(msg) && msg.equals("startDistinguish")) {
            startDistinguish();
        } else if (!Utility.isEmpty(msg) && msg.equals("stopDistinguish")) {
            stopDistinguish();
        }
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        switch (event.type) {
            case 0xa1003:
                List<NotifyInfo> notifyInfos = (List<NotifyInfo>) event.getIntent().getSerializableExtra("list");
                if (notifyInfos != null) {
                    for (int i = 0; i < notifyInfos.size(); i++) {
                        if (-1 != clickIndex) {
                            if (clickIndex + i < cloudVoiceMsgDetailEntries.size()) {
                                cloudVoiceMsgDetailEntries.get(clickIndex + i).setOrder_no(notifyInfos.get(i).getExpress_number());
                            }
                        } else {
                            if (i < YUNHU_MAX_LIST_COUNT)
                                cloudVoiceMsgDetailEntries.get(i).setOrder_no(notifyInfos.get(i).getExpress_number());
                        }
                    }
                    adapter.setVoiceMsgDetail(cloudVoiceMsgDetailEntries);

                    // 将运单号拼接起来
                    String orderNum = "";
                    for (int i = 0; i < notifyInfos.size(); i++) {
                        orderNum = orderNum + notifyInfos.get(i).getExpress_number() + "|";
                    }
                    if (orderNum.lastIndexOf("|") > 0)
                        orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
                    getPhoneNumberByOrderNo(orderNum);// 【接口】
                    break;
                }
        }
    }

    public class UpdateList {
        private String mMsg;

        public UpdateList(String msg) {
            mMsg = msg;
        }

        public String getMsg() {
            return mMsg;
        }
    }
}
