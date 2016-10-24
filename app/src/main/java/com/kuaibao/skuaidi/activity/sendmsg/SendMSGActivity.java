package com.kuaibao.skuaidi.activity.sendmsg;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.activity.adapter.SendMSGAdapter;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.activity.view.NotifyBroadCast;
import com.kuaibao.skuaidi.activity.view.SelectSendTimePop;
import com.kuaibao.skuaidi.activity.view.SelectTimePop;
import com.kuaibao.skuaidi.activity.view.ShowTextPop;
import com.kuaibao.skuaidi.activity.view.SkuaidiAlertDialogSendMsg;
import com.kuaibao.skuaidi.activity.view.SkuaidiAlertDialogSendMsg.BtnOnClickListener;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.layout.SkuaidiRelativeLayout;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dao.RecordDraftBoxDAO;
import com.kuaibao.skuaidi.dao.SaveNoDAO;
import com.kuaibao.skuaidi.dao.SaveUnnormalExitDraftInfoDAO;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SendMsgMenuDialog;
import com.kuaibao.skuaidi.dialog.SendMsgMenuDialog.onClickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.SingleButtonOnclickListener;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.DraftBoxSmsInfo;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.entry.ReceiverInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.SaveNoEntry;
import com.kuaibao.skuaidi.entry.SaveUnnormalExitDraftInfo;
import com.kuaibao.skuaidi.json.entry.SendMSGParmeter;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeInterfaceActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.DraftBoxUtility;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.kuaibao.skuaidi.util.YYSBJsonParser;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 发短信界面
 * 顾冬冬
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SendMSGActivity extends SkuaiDiBaseActivity implements OnClickListener {

    private static final String SMS_SIGNED = "sms.signed";// 签收后，发送短信通知发件人
    public static final int REQUEST_ADD_PHONENUMBER = 0X1002;
    public static final int RESULT_ADD_PHONENUMBER = 0X1003;
    private final int GET_PHONENUMBER_SUCCESS = 0X1004;
    public static final int RESULT_ADD_PHONENUMBER_SEND = 0X1005;
    public static final int REQUEST_MSG_MODEL = 0X1006;// 请求选择短信模板
    public static final int REQUEST_VOICE_MODEL = 0x1007;// 请求选择语音模板
    private final int AGAIN_SHOW_MODEL = 0x1008;// 重新显示主界面模板
    private final int REQUEST_AUTO_CLOUD_CALL = 0X1009;// 请求语音模板-短信发送失败自动拨打云呼
    public static final int RESULT_AUTO_CLOUD_CALL = 0x1010;// 请求语音模板-短信发送失败自动拨打云呼
    /**
     * 发短信
     **/
    private final int SEND_TYPE_SMS = 0X1009;
    /**
     * 发送云呼
     **/
    private final int SEND_TYPE_CLOUD = 0X1011;
    private final int GET_NOSIGNED_INFO_SUCCESS = 0X1012;// 获取昨日未签收手机号信息成功
    private final int REFRESH_VIEW = 0x1015;// 刷新界面

    public static int SEND_MSG_LIST_COUNT = 200;

    public final static String SHOW_LIST_COUNT_BY_PHONE = "phone";
    public final static String SHOW_LIST_COUNT_BY_ORDER = "order";

    // 因为单号是多少位不确定、在添加模板那字数长度设置时有问题，
    private String ordernum = "#NON#";
    private String orderDH = "#DHDHDHDHDH#";
    private String model_url = "#SURLSURLSURLSURLS#";

    private Context mContext = null;
    private Intent mIntent = null;
    private SkuaidiDB skuaidiDB = null;
    private SendMSGAdapter adapter = null;
    private SkuaidiDialog dialog = null;
    private SelectSendTimePop spop = null;
    private SelectTimePop pop = null;
    private ShowTextPop showTextPop = null;
    private SkuaidiAlertDialogSendMsg alertDialog = null;
    private SendMsgMenuDialog menuDialog = null;
    private InputMethodManager imm = null;

    private NotifyBroadCast notifyBroadCast = null;// 广播通知弹窗
    private Message msg = null;

    @BindView(R.id.srlTitle2) SkuaidiRelativeLayout llTitle = null;// 标题栏
    @BindView(R.id.tv_title_des) TextView tv_title_des;
    @BindView(R.id.title_img) ImageView title_img;// 显示VIP图片
    @BindView(R.id.tv_more) SkuaidiTextView tvMore = null;// 发送按钮
    @BindView(R.id.et_notify_content) EditText etNotifyContent = null;// 输入短信模板
    @BindView(R.id.tvClearAll) TextView tvClearAll;// 清空按钮
    @BindView(R.id.tv_msgWordCount) TextView tvMsgWordCount = null;// 模板字数显示控件
    @BindView(R.id.send_total_down) TextView sendTotalDown = null;// 发送短信条数计费说明控件
    @BindView(R.id.lv_notify) ListView lvNotify = null;// 列表
    @BindView(R.id.switchText) TextView switchText = null;// 开关按钮上的文字提示
    @BindView(R.id.iv_MsgMenuIcon) ImageView ivMsgMenuIcon = null;// 菜单按钮
    @BindView(R.id.tvSetTimeSend) TextView tvSetTimeSend = null;// 展开收起按钮上的定时发送提示
    @BindView(R.id.llSendTiming) ViewGroup llSendTiming = null;// 用于关闭定时发送
    @BindView(R.id.llGunScan) ViewGroup llGunScan = null;// 用于巴枪扫描
    @BindView(R.id.llAutoCloudCall) ViewGroup llAutoCloudCall = null;// 展开收起按钮 上的自动发送云呼提示
    @BindView(R.id.hint_addModel) TextView hintAddModel;// 选择模板按钮提示
    @BindView(R.id.fl_options) FrameLayout rl_options;

    TextView tvSelectModel = null;// 显示选择的语音模板title->from dialog
    ImageView cbGunScan = null;// 巴枪扫描checkbox图片
    TextView tvSendTimeTag = null;// 定时发送文字标识


    private String modelTitle = "";// 保存模板标题
    private String modelContent = "";// 保存模板内容
    private String modelID = "";// 保存模板ID
    private String modelStatus = "";// 保存模板审核状态

    private boolean isOpen = false;// 联系人信息列表是否展开，false未展开
    private boolean cb_gunScanBol = false;// 巴枪扫描是否被选中
    private List<NotifyInfo2> infos = new ArrayList<>();// 联系人列表信息
    private List<NotifyInfo2> bestNewDatas = null;// 最终发送的数据【临时】
    private FinalDb finalDb;
    private long timeStamp = 0;// 时间戳变量
    private int clickIndex = -1;// 保存当前是从哪一条开始点击单号输入框的

    private List<NumberPhonePair> dispatchListData;
    // 手机列表比较是否存在相同手机号码标记
    private int i = -1;
    private int j = -1;
    private String sendNoSignedTag = "";// 用来标记是一键发短信还是一键云呼

    private String[] problemOrder = null;// 从问题件传过来的数据变量
    private boolean isProblemOrder = false;// 是否从问题件进入
    private boolean isUse = true;// 作为是否调用服务器提供广播消息接口依据
    private boolean isChooseCloudModel = false;// 是否选中了一条语音模板
    private String sendDesc = "";// 发送失败说明

    // 保存于本地的数据参数
    private SaveNoEntry saveNoEntry;
    private DraftBoxSmsInfo draftBoxInfo = null;
    private DraftBoxSmsInfo temporaryDraftInfo = null;
    private String fromActivity = "";// 保存从哪个界面而来
    private long curTime = System.currentTimeMillis();
    private String draft_id = "";// 保存的时间
    // 备份草稿箱进入到发短信界面时的数据-用于没有做数据修改时直接关闭界面时使用
    private String msgModelContent = "";// 备份短信内容
    private String msgModelId = "";// 备份短信ID
    private String msgNumber = "";// 备份编号
    private String msgNumberPhone = "";// 备份手机号码
    private String msgNumberOrder = "";// 备份单号
    // 备份模板内容
    private String msgLinShiContent = "";// 保存刚进入该界面时候的模板内容
    private String msgLinShiStatus = "";// 保存刚进入该界面时候的模板状态
    // 语音模板参数
    private String voice_ivid = "";// 对应语音模板唯一ID
    private String voice_title = "";// 语音模板标题

    // 语音识别相关
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();// 用HashMap存储听写结果
    private SpeechRecognizer mIat;// 语音听写对象
    private int speechCount = 0;// 为了使得语音识别能自动识别60s，标记关闭次数，当达到6次瑞关闭识别
    private String word = "";
    private String sentence = "";
    private MyHandler myHandler = null;
    private int distinguishMark = -1;// 识别条目【标记上一条识别的下标】
    private Thread mThread = null;

    // 保持屏幕常亮管理
    PowerManager powerManager = null;
    WakeLock wakeLock = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_VIEW:
                    // 收起展开按钮 上面显示提示-巴枪扫描提示
                    llGunScan.setVisibility(SkuaidiSpf.getGunScanStatus(mContext) ? View.VISIBLE : View.GONE);

                    // 收起展开按钮 上面显示提示-定时发送提示
                    llSendTiming.setVisibility(!SkuaidiSpf.getTimeSendMsg(mContext).isTimeSendCheckBoxIsSelect() ? View.GONE : View.VISIBLE);
                    tvSetTimeSend.setText(SkuaidiSpf.getTimeSendMsg(mContext).getTimeSendTimeString());// 显示定时的时间
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
                case GET_PHONENUMBER_SUCCESS:
                    @SuppressWarnings("unchecked")
                    List<ReceiverInfo> receiverInfos = (List<ReceiverInfo>) msg.obj;
                    for (int i = 0; i < receiverInfos.size(); i++) {
                        String rec_mobile = receiverInfos.get(i).getRec_mobile();// 返回的手机号码是连续的
                        autoJudgeMobilePhone2(i, rec_mobile);
                    }
                    adapter.setAdapterData(infos);
                    break;
                case Constants.SUCCESS:// 发送短信成功
//                    MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//                    EventBus.getDefault().post(m);
                    RecordDraftBoxDAO.deleteDraft(draft_id + "");
                    SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");
                    try {
                        SaveNoDAO.saveNo(saveNoEntry);
                        // 判断是否选中过巴枪扫描-选中则跳转到上传界面
                        if (cb_gunScanBol) {
                            if (null != infos && 0 != infos.size()) {
                                mIntent = new Intent(mContext, EThreeInterfaceActivity.class);
                                mIntent.putExtra("e3WayBills", (Serializable) infos);
                                startActivity(mIntent);
                            }
                        }
                        // 判断是否是上传问题件传过来的单号，在发送短信成功以后则跳转至业务首页
                        if (null != problemOrder && problemOrder.length != 0) {
                            mIntent = new Intent(mContext, MainActivity.class);
                            startActivity(mIntent);
                        }
                        if(!Utility.isEmpty(getIntent().getStringExtra("liuyanPhone"))){
                            mIntent = new Intent();
                            String messageContent = etNotifyContent.getText().toString();
                            if (messageContent.contains(ordernum)) { messageContent = messageContent.replaceAll(ordernum, infos.get(0).getExpressNo()); }
                            if (messageContent.contains(orderDH)) {  messageContent = messageContent.replaceAll(orderDH, infos.get(0).getExpress_number()); }
                            if (messageContent.contains(model_url)) { messageContent = messageContent.replaceAll(model_url, ""); }
                            mIntent.putExtra("messageContent", messageContent);
                            setResult(222, mIntent);
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case AGAIN_SHOW_MODEL:
                    showModel();
                    if (judgeIsEmptyForInfos()) { RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true)); }
                    break;
                default:
                    break;
            }
        }
    };

    @Subscribe
    public void onEvent(MessageEvent event) {
        switch (event.type) {
            case 0Xa1001:// 处理在选择模板界面数据发生变化时对本界面进行模板数据更新
                showModel();
                break;
            case 0xa1002:// 处理扫描单号后处理该界面显示数据
                List<NotifyInfo> notifyInfos = (List<NotifyInfo>) event.getIntent().getSerializableExtra("list");
                if (notifyInfos != null) {
                    for (int i = 0; i < notifyInfos.size(); i++) {
                        if (-1 != clickIndex) {
                            if (clickIndex + i < infos.size()) infos.get(clickIndex + i).setExpress_number(notifyInfos.get(i).getExpress_number());
                        } else {
                            if (i < SEND_MSG_LIST_COUNT) infos.get(i).setExpress_number(notifyInfos.get(i).getExpress_number());
                        }
                    }
                    adapter.setAdapterData(infos);
                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                } else {
                    return;
                }

                // 将运单号拼接起来
                String orderNum = "";
                for (int i = 0; i < notifyInfos.size(); i++) { orderNum = orderNum + notifyInfos.get(i).getExpress_number() + "|"; }
                if (orderNum.lastIndexOf("|") > 0) orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
                getPhoneNumberByOrderNo(orderNum);// 【接口】
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("infos", (Serializable) infos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        infos = (List<NotifyInfo2>) savedInstanceState.getSerializable("infos");
        adapter.setAdapterData(infos);
    }

    @SuppressWarnings({"static-access", "deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.sendmsgactivity);
        mContext = this;
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SEND_MSG_LIST_COUNT = 200;
        draft_id = curTime + "";// 本条草稿对应唯一ID【用时间戳】
        skuaidiDB = SkuaidiDB.getInstanse(mContext);
        finalDb = SKuaidiApplication.getInstance().getFinalDbCache();
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(mContext.getApplicationContext(), mInitListener);
        // 保持屏幕常亮设置
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        SkuaidiSpf.saveTimeSendMsg(mContext, false, 0, "");
        SkuaidiSpf.saveGunScanStatus(mContext, false);

        initView();
        monitorEvent();
        if (Utility.isEmpty(fromActivity)) {// 如果不是从草稿箱中而来
            showModel();
        }

        dispatchListData = (List<NumberPhonePair>) getIntent().getSerializableExtra("numberPhonePairs");// 从派件群发短信中获取数据

        setData();
        getData();

        getUnNormalExitModel();
    }

    /**
     * 获取非正常退出时候保存的草稿箱模板
     */
    private void getUnNormalExitModel() {
        SaveUnnormalExitDraftInfo saveDraftInfo = SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("sms");
        if (Utility.isEmpty(fromActivity) && !"draftbox".equals(fromActivity)) {// 如果是从草稿箱中而来
            if (null != saveDraftInfo && saveDraftInfo.getFrom_data().equals("sms")) {
                RecordDraftBoxDAO.deleteDraft(draft_id);// 删除掉当前刚创建的记录【删除条件：当匹配号码的过程中非正常退出|crash，然后重新进入该界面】

                draft_id = saveDraftInfo.getDraft_id();
                String number = saveDraftInfo.getDraft_no();
                String phoneNumber = saveDraftInfo.getDraft_phoneNumber();
                String orderNumber = saveDraftInfo.getDraft_orderNumber();
                String[] numberStr = DraftBoxUtility.strToArr(number);
                String[] phoneNumberStr = DraftBoxUtility.strToArr(phoneNumber);
                String[] orderNumberStr = DraftBoxUtility.strToArr(orderNumber);
                msgNumber = number;
                msgNumberPhone = phoneNumber;
                msgNumberOrder = orderNumber;

                for (int i = 0; i < infos.size(); i++) {
                    NotifyInfo2 notifyInfo2 = infos.get(i);
                    if (!Utility.isEmpty(numberStr) && i < numberStr.length) {
                        notifyInfo2.setExpressNo(!Utility.isEmpty(numberStr) ? numberStr[i].trim() : "");
                        notifyInfo2.setSender_mobile(!Utility.isEmpty(phoneNumberStr) ? phoneNumberStr[i].trim() : "");
                        notifyInfo2.setExpress_number(!Utility.isEmpty(orderNumberStr) ? orderNumberStr[i].trim() : "");
                    }
                }
                for (int j = 0; j < infos.size(); j++) {
                    NotifyInfo2 notInfo2 = infos.get(j);
                    String mobilePhone = notInfo2.getSender_mobile();
                    autoJudgeMobilePhone(j, mobilePhone);
                }
                adapter.setAdapterData(infos);
                adapter.setPhoneNumberCount(findLastPhoneNumberIndex(SHOW_LIST_COUNT_BY_PHONE));
                SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");
            }
        }
    }

    /**
     * saveNewestPhone:为异常退出时保存数据【仅手机号吗】
     * 顾冬冬
     */
    private void saveNewestPhone() {
        if (null == SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("sms")) {
            SaveUnnormalExitDraftInfo saveDraftInfo = new SaveUnnormalExitDraftInfo();
            saveDraftInfo.setDraft_id(draft_id);
            saveDraftInfo.setDraft_no(DraftBoxUtility.pinjieNumber(infos));
            saveDraftInfo.setDraft_orderNumber(DraftBoxUtility.pinjieOrderNumber(infos));
            saveDraftInfo.setDraft_phoneNumber(DraftBoxUtility.pinjiePhoneNumber(infos));
            saveDraftInfo.setFrom_data("sms");
            SaveUnnormalExitDraftInfoDAO.insertUnnormarlExitDraftInfo(saveDraftInfo);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SELECT_SEND_MORE && resultCode == Constants.SELECT_SEND_MORE) {
            showModel();
            if (judgeIsEmptyForInfos()) {
                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
            }
        } else if (requestCode == REQUEST_ADD_PHONENUMBER && resultCode == RESULT_ADD_PHONENUMBER_SEND) {
            infos = (List<NotifyInfo2>) data.getSerializableExtra("notifyinfos");
            getSearchPhoneNumber(infos);
            if (Utility.isNetworkConnected()) {
                sendMsg(tvMore, true);
            } else {
                mPopSendMsgOwnPhone(tvMore, "提示", "您没有连接网络，是否使用自己手机发送？", "确定", "取消");// 提示使用自己手机发送
            }
        } else if (requestCode == REQUEST_ADD_PHONENUMBER && resultCode == RESULT_ADD_PHONENUMBER) {
            infos = (List<NotifyInfo2>) data.getSerializableExtra("notifyinfos");
            getSearchPhoneNumber(infos);
            RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
            SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");
            saveNewestPhone();
        } else if (requestCode == Constants.REQUEST_NOTIFYDETAIL_CAPTURE) {// 扫描条码后的条码列表
            if (resultCode == Constants.RESULT_NOTIFYDETAIL_CAPTURE) {
                List<NotifyInfo2> notifyInfos = (List<NotifyInfo2>) data.getSerializableExtra("notifyInfo2");
                if (notifyInfos != null) {
                    for (int i = 0; i < notifyInfos.size(); i++) {
                        if (-1 != clickIndex) {
                            if (clickIndex + i < infos.size())
                                infos.get(clickIndex + i).setExpress_number(notifyInfos.get(i).getExpress_number());
                        } else {
                            if (i < SEND_MSG_LIST_COUNT)
                                infos.get(i).setExpress_number(notifyInfos.get(i).getExpress_number());
                        }
                    }
                    adapter.setAdapterData(infos);
                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                } else {
                    return;
                }

                // 将运单号拼接起来
                String orderNum = "";
                for (int i = 0; i < notifyInfos.size(); i++) {
                    orderNum = orderNum + notifyInfos.get(i).getExpress_number() + "|";
                }
                if (orderNum.lastIndexOf("|") > 0)
                    orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
                getPhoneNumberByOrderNo(orderNum);// 【接口】
            }
        }
        if (requestCode == REQUEST_MSG_MODEL) {
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
        } else if (requestCode == REQUEST_AUTO_CLOUD_CALL) {
            if (null != data) {
                isChooseCloudModel = data.getBooleanExtra("isChoose", false);
                getCloudVoiceModel();
                showChoosedModelStatus();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 已经选中了一条语音模板用于发送短信失败后云呼
     */
    private void showChoosedModelStatus() {
        if (isChooseCloudModel) {
            llAutoCloudCall.setVisibility(View.VISIBLE);
            tvSelectModel.setText(voice_title);
        }
    }

    /**
     * 取消发送短信失败后云呼语音
     */
    private void setCloseChoosedModelStatus() {
        isChooseCloudModel = false;
        llAutoCloudCall.setVisibility(View.GONE);
        initVoiceData();
        tvSelectModel.setText("选择语音模板");
    }

    private void initVoiceData() {
        voice_ivid = "";// 对应语音模板唯一ID
        voice_title = "";// 语音模板标题
    }

    /**
     * 获取语音模板被选中的那一条
     */
    private void getCloudVoiceModel() {
        List<CloudRecord> cloudRecords = skuaidiDB.getCloudRecordModels();// 取得所有录音模板
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
            // 获取模板参数
            voice_ivid = cRecord.getIvid();
            voice_title = cRecord.getTitle();
        }
    }

    private void getSearchPhoneNumber(List<NotifyInfo2> infos) {
        for (int i = 0; i < infos.size(); i++) {
            NotifyInfo2 notInfo2 = infos.get(i);
            String mobilePhone = notInfo2.getSender_mobile();
            autoJudgeMobilePhone(i, mobilePhone);
        }

        adapter.setAdapterData(infos);
        int index = 0;

        for (int i = infos.size() - 1; i >= 0; i--) {
            if (!Utility.isEmpty(infos.get(i).getSender_mobile())) {
                if (i == (infos.size() - 1)) {
                    index = SEND_MSG_LIST_COUNT;
                    isOpen = true;
                    switchText.setCompoundDrawablesWithIntrinsicBounds(Utility.getDrawable(mContext, R.drawable.all_close), null, null, null);
                    switchText.setText("收起");
                } else {
                    index = i + 2;
                }
                break;
            } else {
                index = 1;
            }
        }

        if (!isOpen) {// 如果没有展开全部列表
            adapter.setItemCount(index);
        }
    }

    /**
     * 自动判断手机号码是否符合规则，自动去掉不必要的标识字符
     **/
    private void autoJudgeMobilePhone(int i, String mobilePhone) {

        if (!Utility.isEmpty(infos.get(i).getSender_mobile()))// 测试要求存在手机号则不覆盖输入的手机号-此处留证
            return;
        if (Utility.isEmpty(mobilePhone)){
            infos.get(i).setSender_mobile("");
            return;
        }
        infos.get(i).setSender_mobile(formatPhoneNumber(mobilePhone));
    }

    /**
     * 格式化手机号码，去掉不必要的空格、下划线、和号码前缀（eg:86,+86,0086,17951）
     **/
    public static String formatPhoneNumber(String phoneNumber) {
        if (Utility.isEmpty(phoneNumber)) return "";
        phoneNumber = phoneNumber.replaceAll("[*]","x");
        phoneNumber = phoneNumber.replaceAll(" ", "");
        phoneNumber = phoneNumber.replaceAll("-", "");

        if (phoneNumber.indexOf("86") == 0) return phoneNumber.substring(2);
        if (phoneNumber.indexOf("+86") == 0) return phoneNumber.substring(3);
        if (phoneNumber.indexOf("0086") == 0) return phoneNumber.substring(4);
        if (phoneNumber.indexOf("17951") == 0) return phoneNumber.substring(5);
        return phoneNumber;
    }

    /**
     * 自动判断手机号码是否符合规则，自动去掉不必要的标识字符(用于输入单号自动获取手机号)
     **/
    private void autoJudgeMobilePhone2(int i, String mobilePhone) {
        if (clickIndex != -1) {
            if (clickIndex+i >= SEND_MSG_LIST_COUNT || !Utility.isEmpty(infos.get(clickIndex + i).getSender_mobile()))// 测试要求存在手机号则不覆盖输入的手机号-此处留证
                return;
            if (Utility.isEmpty(mobilePhone)){
                infos.get(clickIndex + i).setSender_mobile("");
                return;
            }
            infos.get(clickIndex + i).setSender_mobile(formatPhoneNumber(mobilePhone));
        } else {// 包括从e3中上传单号并发送短信传递过来的单号的自动获取手机号会执行此句代码
            if (Utility.isEmpty(mobilePhone)){
                infos.get(i).setSender_mobile("");
                return;
            }
            infos.get(i).setSender_mobile(formatPhoneNumber(mobilePhone));
        }
    }

    private void initView() {
        hintAddModel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sendmsg_add, 0, 0, 0);
        String title_other = getIntent().getStringExtra("title_desc");// 其他功能模块跳转过来的标题
        title_img.setVisibility(SkuaidiSpf.getClientIsVIP(mContext).equals("y") ? View.VISIBLE : View.GONE);// 如果是VIP显示VIP图标，否则隐藏

        if (TextUtils.isEmpty(title_other)) {
            tv_title_des.setText("发短信");
            tvMore.setText("发送");
        } else {
            tv_title_des.setText(title_other);
            tvMore.setText(getIntent().getStringExtra("action_name"));
            if ("签收短信通知".equals(getIntent().getStringExtra("title_desc"))) {
                rl_options.setVisibility(View.GONE);
            }
        }
        tvMore.setVisibility(View.VISIBLE);

        if (Utility.isEmpty(etNotifyContent.getText().toString()))
            tvMsgWordCount.setText("0/129");

        menuDialog = new SendMsgMenuDialog(mContext).builder(isProblemOrder).setCanceledOnTouchOutside(true).addClickListener(new onClickListener() {
            @Override
            public void timeSendMsg(TextView textView) {
                UMShareManager.onEvent(mContext, "SendMSG_sendTime", "SendMSG", "发短信:定时发送");
                SendMSGActivity.this.tvSendTimeTag = textView;
                pop.showPopupWindow(ivMsgMenuIcon);
            }

            @Override
            public void sendMsg() {
                UMShareManager.onEvent(mContext, "SendMSG_OneKeyMsg", "SendMSG", "发短信:一键发短信");
                sendNoSignedTag = "sms";
                getNoSignedSmsInfo(sendNoSignedTag);
                showProgressDialog( "请稍候");
            }

            @Override
            public void sendCloudCall() {
                UMShareManager.onEvent(mContext, "SendMSG_OneKeySendCloud", "SendMSG", "发短信:一键云呼");
                sendNoSignedTag = "ivr";
                getNoSignedSmsInfo(sendNoSignedTag);
                showProgressDialog( "请稍候");
            }

            @Override
            public void importPhoneNumber() {
                UMShareManager.onEvent(mContext, "SendMSG_ImportPhoneNumber", "SendMSG", "发短信:导入手机号");
                final SkuaidiDialog dialog = new SkuaidiDialog(mContext);
                dialog.setTitle("批量录入客户手机号");
                dialog.isUseBigEditText(true);
                dialog.setBigEditTextHint("手动输入或批量粘贴收件人手机号，并以“，”或换行分割，最多输入" + SEND_MSG_LIST_COUNT + "个号");
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
                        for (int i = 0; i < infos.size(); i++) {
                            if (!Utility.isEmpty(phoneList) && phoneList.size() > j) {
                                String mobilePhone = phoneList.get(j);
                                autoJudgeMobilePhone(i, mobilePhone);
                            } else {
                                infos.get(i).setSender_mobile("");
                            }
                            j++;
                        }
                        adapter.setAdapterData(infos);
                        adapter.setPhoneNumberCount(findLastPhoneNumberIndex(SHOW_LIST_COUNT_BY_PHONE));
                        dialog.setDismiss();
                        RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
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
            public void gunScanDesc() {
                UMShareManager.onEvent(mContext, "SendMSG_GunScanDesc", "SendMSG", "发短信:巴枪扫描说明");
                Intent mIntent3 = new Intent(mContext, WebViewActivity.class);
                mIntent3.putExtra("fromwhere", "gunscan");
                startActivity(mIntent3);
            }

            @Override
            public void gunScan(ImageView cb_gun_scan) {
                UMShareManager.onEvent(mContext, "SendMSG_GunScan", "SendMSG", "发短信:巴枪扫描");
                SendMSGActivity.this.cbGunScan = cb_gun_scan;

                if (!SkuaidiSpf.getGunScanStatus(mContext)) {
                    if (Utility.isNetworkConnected()) {
                        // 调用接口判断用户是否已审核-只有是申通用户才调用
                        getGunScanApprovalStatus();
                    } else {
                        UtilToolkit.showToast("请设置网络");
                    }
                } else {
                    SkuaidiSpf.saveGunScanStatus(mContext, false);
                    cb_gunScanBol = false;
                    timeStamp = 0;
                    cb_gun_scan.setBackgroundResource(R.drawable.icon_push_close);
                    msg = new Message();
                    msg.what = REFRESH_VIEW;
                    mHandler.sendMessage(msg);
                }

            }

            @Override
            public void autoCloudCall(View view) {// 发送短信失败自动云呼
                if (tvSelectModel == null) {
                    tvSelectModel = (TextView) view.findViewById(R.id.tvSelectModel);
                }
                mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                mIntent.putExtra("fromActivityType", "AutoCloudCall");
                startActivityForResult(mIntent, REQUEST_AUTO_CLOUD_CALL);

            }

            @Override
            public void settingPassword() {// 设置密码
                mIntent = new Intent(mContext, SettingTemplatePasswordTypeActivity.class);
                startActivity(mIntent);
            }
        });

    }

    private Timer timer;
    private void getData() {
        // 问题快件发送短信 ,或者签收短信通知
        problemOrder = getIntent().getStringArrayExtra("orderNumbers");

        if (null != problemOrder && 0 != problemOrder.length) {
            isProblemOrder = true;
            for (int i = 0; i < infos.size(); i++) {
                if (i < problemOrder.length) {
                    infos.get(i).setExpress_number(problemOrder[i]);
                }
            }

            // 将运单号拼接起来
            String orderNum = "";
            for (String problemOrders : problemOrder) {
                orderNum = orderNum + problemOrders + "|";
            }
            orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
            // 调用接口查找运单号是否有对应的手机号
            getPhoneNumberByOrderNo(orderNum);

            isUse = false;
        }

        // 派件列表群发短信带过来的单号

        if (!Utility.isEmpty(dispatchListData) && 0 != dispatchListData.size()){
            for (int i = 0; i < dispatchListData.size(); i++) {
                infos.get(i).setExpress_number(dispatchListData.get(i).getDh());
            }
            adapter.setMaxCount(SEND_MSG_LIST_COUNT);
            // 将运单号拼接起来
            String orderNum = "";
            for (NumberPhonePair npp : dispatchListData) {
                orderNum = orderNum + npp.getDh() + "|";
            }
            orderNum = orderNum.substring(0, orderNum.lastIndexOf("|"));
            // 调用接口查找运单号是否有对应的手机号
            getPhoneNumberByOrderNo(orderNum);
        }



        if ("签收短信通知".equals(getIntent().getStringExtra("title_desc"))) {
            adapter.setMaxCount(findLastPhoneNumberIndex(SHOW_LIST_COUNT_BY_ORDER));
            if (!SkuaidiSpf.getDialogShowen()) {
                dialog = new SkuaidiDialog(this);
                dialog.isUseSingleButton(true);
                dialog.isUseEditText(false);
                dialog.setSingleButtonTitle("确定");
                dialog.setContent("编辑需要发短信的手机号，快件被签收将短信通知该客户。签收通知短信暂时不会进短信记录。系统检测到快件被签收并且短信余额充足,才会将短信发出。");
                dialog.setTitle("快件签收通知发件人");
                dialog.showDialog();
                SkuaidiSpf.saveDialogShowen(true);
            }

        }
        if (isUse){
            getBroadCastNotify();// 获取广播通知
        }
    }

    private void showModel() {
        List<ReplyModel> models = skuaidiDB.getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN);// 模板集合
        if (null == models || 0 == models.size())
            return;

        ReplyModel model = new ReplyModel();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).isChoose()) {
                model = models.get(i);
                break;
            }
        }
        modelTitle = model.getTitle();// 获取模板标题
        modelContent = model.getModelContent();// 获取数据库中被选中的模板内容
        modelID = model.getTid();// 获取数据库中被选中的模板ID
        modelStatus = model.getState();// 获取数据库中被选中模板状态

        replayIcon();
        msgLinShiContent = modelContent;
        msgLinShiStatus = modelStatus;
        showModelContent();
    }

    /**
     * 替换图片字段
     **/
    private void replayIcon() {
        if (!Utility.isEmpty(modelContent) && 0 != modelContent.length()) {
            if (modelContent.contains("#NO#"))
                modelContent = modelContent.replaceAll("#NO#", ordernum);

            if (modelContent.contains("#DH#")) {
                modelContent = modelContent.replaceAll("#DH#", orderDH);
                if (modelContent.length() >= 129)
                    modelContent = modelContent.substring(0, 129);
            } else {
                if (modelContent.length() >= 129)
                    modelContent = modelContent.substring(0, 129);
            }
            if (modelContent.contains("#SURL#")) {
                modelContent = modelContent.replaceAll("#SURL#", model_url);
            }
        } else {
            modelContent = "";
        }
    }

    /**
     * 显示模板内容
     **/
    private void showModelContent() {
        TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(mContext);
        if (!Utility.isEmpty(modelContent) && 0 != modelContent.length()) {
            etNotifyContent.setText(mTextInsertImgParser.replace(modelContent));
            etNotifyContent.setSelection(etNotifyContent.getText().toString().length());
            setModelTitle(true);
        } else {
            etNotifyContent.setText("");
            setModelTitle(false);
        }
    }

    private void setModelTitle(boolean display) {
        if (display) {
            hintAddModel.setText(!Utility.isEmpty(modelTitle) ? modelTitle : "短信模板");
            hintAddModel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            hintAddModel.setTextColor(Utility.getColor(mContext, R.color.gray_1));
        } else {
            hintAddModel.setText(mContext.getResources().getString(R.string.send_msg_addmodel));// 请选择短信模板
            hintAddModel.setTextColor(Utility.getColor(mContext, R.color.default_green_2));
            hintAddModel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sendmsg_add, 0, 0, 0);
        }
    }

    /**
     * 设置数据;true:直接进入发送短信界面的时候设置的;false:从草稿箱传进来的原数据，用于在退出时还原数据
     */
    private DraftBoxSmsInfo setDraftBoxInfo(boolean newData) {
        if (newData) {
            DraftBoxSmsInfo info = new DraftBoxSmsInfo();
            info.setDraftSaveTime(curTime);
            info.setId(draft_id + "");
            if (null != adapter) {
                info.setNumber(DraftBoxUtility.pinjieNumber(adapter.getListData()));
                info.setPhoneNumber(DraftBoxUtility.pinjiePhoneNumber(adapter.getListData()));
                info.setOrderNumber(DraftBoxUtility.pinjieOrderNumber(adapter.getListData()));
            } else {
                info.setNumber(DraftBoxUtility.pinjieNumber(infos));
                info.setPhoneNumber(DraftBoxUtility.pinjiePhoneNumber(infos));
                info.setOrderNumber(DraftBoxUtility.pinjieOrderNumber(infos));
            }
            info.setUserPhoneNum(SkuaidiSpf.getLoginUser().getPhoneNumber());
            info.setSmsContent(etNotifyContent.getText().toString());
            info.setSmsStatus(modelStatus);
            info.setSmsId(modelID);
            info.setModelTitle(modelTitle);
            info.setNormal_exit_status(true);
            return info;
        } else {
            return temporaryDraftInfo;
        }
    }

    private void setData() {
        pop = new SelectTimePop(mContext, onClickListener);

        // 判断是否从派件列表界面群发短信而来
        if (!Utility.isEmpty(dispatchListData) && 0 != dispatchListData.size()) {
            SEND_MSG_LIST_COUNT = dispatchListData.size();
            isOpen = true;
            switchText.setText("收起");
            switchText.setCompoundDrawablesWithIntrinsicBounds(Utility.getDrawable(mContext, R.drawable.all_close), null, null, null);
        }


        String lastNoHead = "";// 最后一个编号的前两个字符
        int lastNo = 1;// 最后一个编号【数字】
        saveNoEntry = SaveNoDAO.getSaveNo(SaveNoDAO.NO_SMS);
        if (saveNoEntry != null) {
            lastNoHead = saveNoEntry.getSave_letter();// 获取保存的字母
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

        for (int i = 0; i < SEND_MSG_LIST_COUNT; i++) {
            NotifyInfo2 notifyInfo2 = new NotifyInfo2();
            if (lastNo > 99999) {
                lastNo = 1;
            }
            notifyInfo2.setExpressNo(lastNoHead + lastNo);
            lastNo++;
            infos.add(notifyInfo2);
        }
        if(!Utility.isEmpty(getIntent().getStringExtra("liuyanPhone"))){
            infos.get(0).setSender_mobile(getIntent().getStringExtra("liuyanPhone"));
        }

        // 编号，手机号码，单号 【列表适配器】
        adapter = new SendMSGAdapter(mContext, infos, new SendMSGAdapter.setButtonClick() {

            @Override
            public void modifyNo(View v, int position, List<NotifyInfo2> notifyInfo2s) {
                customNo(position, notifyInfo2s);
                stopDistinguish();
            }

            @Override
            public void addPhoneNumber(View v, int position) {
                stopDistinguish();
                Intent intent = new Intent(mContext, NotifySearchPhoneActivity.class);
                intent.putExtra("listsearchphone", true);
                intent.putExtra("listposition", position);
                intent.putExtra("notifyinfos", (Serializable) infos);
                // 用于草稿箱保存数据的时候使用的参数-此参数为对应草稿箱条目唯一ID
                intent.putExtra("draft_id", draft_id);
                if ("签收短信通知".equals(getIntent().getStringExtra("title_desc"))) {
                    intent.putExtra("from", "签收短信通知");
                }
                startActivityForResult(intent, REQUEST_ADD_PHONENUMBER);
            }

            @Override
            public void deletePhoneAndOrderNo(View v, int position, NotifyInfo2 notifyInfo2) {
                notifyInfo2.setSender_mobile("");
                notifyInfo2.setExpress_number("");
                adapter.notifyDataSetChanged();
                if (adapter.isShowAll())
                    adapter.setMaxCount(SEND_MSG_LIST_COUNT);
                else
                    adapter.setPhoneNumberCount(findLastPhoneNumberIndex(SHOW_LIST_COUNT_BY_PHONE));

                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
            }

            @Override
            public void addOrderNo(View v, int position, String expressNo, List<NotifyInfo2> infos) {
                stopDistinguish();
                clickIndex = position;// 保存当前点击条目下标
                Intent intent = new Intent(mContext, CaptureActivity.class);
                intent.putExtra("qrcodetype", Constants.TYPE_PAIJIAN);
                intent.putExtra("scanMaxSize", SEND_MSG_LIST_COUNT-position);
                intent.putExtra("isContinuous", true);
                intent.putExtra("inType", "notify_detail_adapter");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("listposition", position);// 点击的条目下标
                intent.putExtra("sendmsgInfos", (Serializable) infos);// 发送短信的列表集合
                startActivityForResult(intent, Constants.REQUEST_NOTIFYDETAIL_CAPTURE);
            }

            @Override
            public void playAudio(View v, int position) {
                if (!UtilityTime.isToday(mContext, SkuaidiSpf.getCurDate(mContext))) {
                    SkuaidiSpf.saveCurDate(mContext, UtilityTime.getDateTimeByMillisecond2(System.currentTimeMillis(), UtilityTime.YYYY_MM_DD));
                    UMShareManager.onEvent(mContext, "SendMSG_ClickEveryDay_Num", "SendMSG", "发短信:每日使用语音录入人数【个人当天只统计1次】");
                }

                if (distinguishMark != -1 && distinguishMark != position) {
                    startDistinguish();
                    infos.get(distinguishMark).setPlayVoiceAnim(false);
                    infos.get(position).setPlayVoiceAnim(true);
                    distinguishMark = position;
                } else if (distinguishMark == position) {
                    if (infos.get(position).isPlayVoiceAnim()) {
                        stopDistinguish();
                        infos.get(position).setPlayVoiceAnim(false);
                    } else {
                        startDistinguish();
                        infos.get(position).setPlayVoiceAnim(true);
                    }

                } else {// distinguishMark == -1
                    startDistinguish();
                    infos.get(position).setPlayVoiceAnim(true);
                    distinguishMark = position;
                }
                adapter.setAdapterData(infos);
            }

        }, new SendMSGAdapter.setButOnLongClick() {

            @Override
            public void showOrder(View v, int position, String orderStr) {
                showTextPop = new ShowTextPop(mContext, orderStr);
                showTextPop.showAsDropDown(v, 0, (-v.getHeight()) * 2);
            }
        });
        lvNotify.setAdapter(adapter);

        if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {// 如果是从草稿箱中而来
            if (null != draftBoxInfo) {
                String number = draftBoxInfo.getNumber();
                String phoneNumber = draftBoxInfo.getPhoneNumber();
                String orderNumber = draftBoxInfo.getOrderNumber();
                String[] numberStr = DraftBoxUtility.strToArr(number);
                String[] phoneNumberStr = DraftBoxUtility.strToArr(phoneNumber);
                String[] orderNumberStr = DraftBoxUtility.strToArr(orderNumber);
                msgNumber = number;
                msgNumberPhone = phoneNumber;
                msgNumberOrder = orderNumber;

                for (int i = 0; i < infos.size(); i++) {
                    NotifyInfo2 notifyInfo2 = infos.get(i);
                    if (!Utility.isEmpty(numberStr) && i < numberStr.length) {
                        notifyInfo2.setExpressNo(!Utility.isEmpty(numberStr) ? numberStr[i].trim() : "");
                        notifyInfo2.setSender_mobile(!Utility.isEmpty(phoneNumberStr) ? phoneNumberStr[i].trim() : "");
                        notifyInfo2.setExpress_number(!Utility.isEmpty(orderNumberStr) ? orderNumberStr[i].trim() : "");
                    }
                }
                adapter.setAdapterData(infos);
                adapter.setPhoneNumberCount(findLastPhoneNumberIndex(SHOW_LIST_COUNT_BY_PHONE));
            }
        }
        RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));

    }

    /**
     * 监听事件
     **/
    private void monitorEvent() {
        etNotifyContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String changeContent = etNotifyContent.getText().toString();// 取出当前文本框中的内容
                if (Utility.isEmpty(changeContent)) {
                    modelID = "";
                }
                int contentLength = changeContent.length();// 获取输入内容字数

                String pattern = "[a-zA-Z\u4e00-\u9fa5]";// 字母和中文的正则
                Pattern p = Pattern.compile(pattern);
                Matcher m, m2;
                m = p.matcher(msgLinShiContent);
                m2 = p.matcher(changeContent);

                String modelString = "";// 模板内容【全部是中文】
                String curString = "";// 当前修改过后的模板内容【全部是中文】
                while (m.find()) {
                    modelString = modelString + m.group();// 取出原先模板中全部中文
                }
                while (m2.find()) {
                    curString = curString + m2.group();// 取出修改后模板中全部中文
                }
                if (!Utility.isEmpty(changeContent)) {
                    if (!Utility.isEmpty(SkuaidiSpf.getClientIsVIP(mContext)) && SkuaidiSpf.getClientIsVIP(mContext).equals("y")) {// 如果是VIP用户
                        tvMsgWordCount.setVisibility(View.VISIBLE);
                        tvMsgWordCount.setText(contentLength + "/129");
                        tvClearAll.setTextColor(Utility.getColor(mContext, R.color.gray_2));
                        if (contentLength > 65) {// 文字大于65个按两条计费
                            sendTotalDown.setVisibility(View.VISIBLE);
                            sendTotalDown.setText("此短信按2条计费");
                        } else {
                            sendTotalDown.setVisibility(View.GONE);
                        }
                        modelContent = changeContent;
                        modelStatus = "";
                    } else {
                        if (msgLinShiContent.equals(changeContent)) {
                            modelStatus = msgLinShiStatus;
                        }
                        // 判断模板输入框中是不是选中的模板，否则不显示字数和计费条数
                        if (!Utility.isEmpty(msgLinShiContent) && modelString.equals(curString) && !Utility.isEmpty(modelStatus)
                                && "approved".equals(modelStatus)) {
                            tvMsgWordCount.setVisibility(View.VISIBLE);
                            tvMsgWordCount.setText(contentLength + "/129");
                            tvClearAll.setTextColor(Utility.getColor(mContext, R.color.gray_2));
                            if (contentLength > 65) {// 判断是否是按两条计费
                                sendTotalDown.setVisibility(View.VISIBLE);
                                sendTotalDown.setText("此短信按2条计费");
                            } else {
                                sendTotalDown.setVisibility(View.GONE);
                            }

                        } else {// 如果不是已经审核过的模板，隐藏字数显示，显示说明文案
                            modelContent = changeContent;
                            modelStatus = "";
                            tvMsgWordCount.setVisibility(View.GONE);
                            sendTotalDown.setVisibility(View.VISIBLE);

                            String str = "本条将用手机卡发，开通vip特权可直接发送";
                            SpannableStringBuilder style = new SpannableStringBuilder(str);
                            style.setSpan(new ForegroundColorSpan(Color.rgb(247, 71, 57)), 0, 11, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            style.setSpan(new ForegroundColorSpan(Color.rgb(12, 186, 160)), 11, 16, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            style.setSpan(new ForegroundColorSpan(Color.rgb(247, 71, 57)), 16, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            sendTotalDown.setText(style);
                            sendTotalDown.setTextSize(12f);
                        }
                    }
                } else {// 如果内容为空的话显示字数显示，隐藏说明文案
                    modelContent = "";
                    modelStatus = "";
                    tvMsgWordCount.setVisibility(View.VISIBLE);
                    tvMsgWordCount.setText("0/129");
                    sendTotalDown.setVisibility(View.GONE);
                    tvClearAll.setTextColor(Utility.getColor(mContext, R.color.gray_7));
                }
            }
        });

        draftBoxInfo = (DraftBoxSmsInfo) getIntent().getSerializableExtra("draftBoxRecord");
        temporaryDraftInfo = draftBoxInfo;
        fromActivity = getIntent().getStringExtra("fromActivity");
        if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {// 如果是从草稿箱中而来
            if (null != draftBoxInfo) {
                String number = draftBoxInfo.getNumber();
                String[] numberStr = DraftBoxUtility.strToArr(number);
                SEND_MSG_LIST_COUNT = numberStr.length;

                draft_id = draftBoxInfo.getId();
                modelContent = draftBoxInfo.getSmsContent();
                modelID = draftBoxInfo.getSmsId();
                modelTitle = draftBoxInfo.getModelTitle();
                modelStatus = draftBoxInfo.getSmsStatus();
                SkuaidiDB db = SkuaidiDB.getInstanse(mContext);
                boolean havemodel = db.isHaveModel(modelID);
                if (!havemodel) {// 模板已经不存在了
                    modelStatus = "reject";// 将状态设置为未通过审核
                }
                msgModelContent = modelContent;
                if (!Utility.isEmpty(msgModelContent) && msgModelContent.contains("#NO#"))
                    msgModelContent = msgModelContent.replaceAll("#NO#", "#NON#");
                msgModelId = modelID;
                // 下面四行顺序不能乱
                replayIcon();
                msgLinShiContent = modelContent;
                msgLinShiStatus = modelStatus;
                showModelContent();// 将模板内容显示到界面上
            }
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        if (spop != null)
            spop.dismiss();
        if (pop != null)
            pop.dismiss();
        if (showTextPop != null)
            showTextPop.dismiss();
        if (alertDialog != null)
            alertDialog.dismiss();
        if (notifyBroadCast != null) {
            notifyBroadCast.dismiss();
            if (notifyBroadCast.mTimer != null) {
                notifyBroadCast.mTimer.cancel();
                notifyBroadCast.mTimer = null;
            }
            if (notifyBroadCast.task != null) {
                notifyBroadCast.task.cancel();
                notifyBroadCast.task = null;
            }
            notifyBroadCast = null;
        }
        if (mIat != null) {
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.tvClearAll,
            R.id.llAutoCloudCall,
            R.id.llSendTiming,
            R.id.llGunScan,
            R.id.iv_title_back,
            R.id.tv_more,
            R.id.selectModel,
            R.id.switchList,
            R.id.iv_MsgMenuIcon})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:// 返回按钮
                finishContext();
                break;
            case R.id.tvClearAll:
                etNotifyContent.setText("");
                break;
            case R.id.llAutoCloudCall:// 取消自动云呼功能
                setCloseChoosedModelStatus();
                break;
            case R.id.llSendTiming:// 取消定时发送
                UMShareManager.onEvent(mContext, "SendMSG_CancelSendTime", "SendMSG", "发短信:取消发送时间");
                cancelTransmission();
                SkuaidiSpf.saveTimeSendMsg(mContext, false, 0, "");
                msg = new Message();
                msg.what = REFRESH_VIEW;
                mHandler.sendMessage(msg);
                break;
            case R.id.llGunScan:// 关闭巴枪扫描
                SkuaidiSpf.saveGunScanStatus(mContext, false);
                cb_gunScanBol = false;
                timeStamp = 0;
                cbGunScan.setBackgroundResource(R.drawable.icon_push_close);
                msg = new Message();
                msg.what = REFRESH_VIEW;
                mHandler.sendMessage(msg);

                break;
            case R.id.tv_more:// 发送按钮
                stopDistinguish();
                if ("签收短信通知".equals(getIntent().getStringExtra("title_desc"))) {
                    signSMS();
                } else {
                    if (Utility.isNetworkConnected()) {
                        //System.out.println("session_id:" + SkuaidiSpf.getLoginUser().getSession_id());
                        sendMsg(tvMore, true);
                    } else {
                        mPopSendMsgOwnPhone(tvMore, "提示", "您没有连接网络，是否使用自己手机发送？", "确定", "取消");// 提示使用自己手机发送
                    }
                }

                break;
            case R.id.selectModel:// 选择模板
                stopDistinguish();
                UMShareManager.onEvent(mContext, "SendMSG_change", "SendMSG", "发短信:更换短信模板");
                mIntent = new Intent(mContext, ModelActivity.class);
                if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {// 如果是从草稿箱中而来
                    mIntent.putExtra("from_activity", "draftBox");
                } else {
                    mIntent.putExtra("from_activity", "sendMore");
                }
                startActivityForResult(mIntent, Constants.SELECT_SEND_MORE);
                break;
            case R.id.switchList:// 是否展开列表
                if (isOpen) {
                    UMShareManager.onEvent(mContext, "SendMSG_ShouqiAll", "SendMSG", "发短信:收起全部");
                    switchText.setCompoundDrawablesWithIntrinsicBounds(Utility.getDrawable(mContext, R.drawable.all_open), null, null, null);
                    switchText.setText("展开");
                    isOpen = false;
                    if (null != adapter) {
                        adapter.setPhoneNumberCount(findLastPhoneNumberIndex(SHOW_LIST_COUNT_BY_PHONE));
                    }
                } else {
                    stopDistinguish();// 展开全部以后不允许识别
                    UMShareManager.onEvent(mContext, "SendMSG_ZhanKaiAll", "SendMSG", "发短信:展开全部");
                    switchText.setCompoundDrawablesWithIntrinsicBounds(Utility.getDrawable(mContext, R.drawable.all_close), null, null, null);
                    switchText.setText("收起");
                    isOpen = true;
                    if (null != adapter)
                        adapter.setMaxCount(SEND_MSG_LIST_COUNT);
                }
                break;
            case R.id.iv_MsgMenuIcon:// 菜单按钮
                stopDistinguish();
                menuDialog.show(isProblemOrder);

                break;
        }
    }

    /**
     * 获取列表中最后一条手机号或者单号所在列表下标
     *
     * @param need 单号或者手机号
     */
    private int findLastPhoneNumberIndex(String need) {
        int index = 1;
        boolean isExist = false;
        if (null != infos && 0 != infos.size()) {
            for (int i = infos.size() - 1; i >= 0; i--) {
                if (need.equals("phone")) {
                    String phone = infos.get(i).getSender_mobile();
                    if (!Utility.isEmpty(phone)) {
                        if (i == infos.size() - 1) {
                            index = SEND_MSG_LIST_COUNT;
                        } else {
                            index = i + 2;
                        }
                        isExist = true;
                    } else {
                        index = 1;
                    }
                } else if (need.equals("order")) {
                    String order = infos.get(i).getExpress_number();
                    if (!Utility.isEmpty(order)) {
                        if (i == infos.size() - 1) {
                            index = SEND_MSG_LIST_COUNT;
                        } else {
                            index = i + 2;
                        }
                        isExist = true;
                    } else {
                        index = 1;
                    }
                }

                if (isExist) {
                    break;
                }
            }
        }
        return index;
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 广播通知
                case R.id.tvBroadCast:
                    stopDistinguish();
                    loadWeb(Constants.URL_NOTIFY_SEND_MSG + SkuaidiSpf.getLoginUser().getPhoneNumber(), "");
                    break;
                // 选择好时间
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

    private void cancelTransmission() {
        tvSendTimeTag.setText("");
        timeStamp = 0;
    }

    private void timingTransmission() {
        timeStamp = pop.getTimeStamp();
        tvSendTimeTag.setText(pop.getSendTimeStr());// 设置checkbox旁边文本框显示选择的时间
        SkuaidiSpf.saveTimeSendMsg(mContext, true, timeStamp, pop.getSendTimeStr());
        pop.dismiss();
        msg = new Message();
        msg.what = REFRESH_VIEW;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义编号
     */
    private void customNo(final int position, final List<NotifyInfo2> notifyInfo2s) {
        UMShareManager.onEvent(mContext, "SendMSG_CustomNo", "SendMSG", "发短信:自定义编号");
        dialog = new SkuaidiDialog(mContext);
        dialog.setTitle("设置起始编号");
        dialog.isUseEditText(true);
        dialog.setPositionButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");
        dialog.showEditTextTermsArea(true);
        dialog.setEditTextContent(5);
        dialog.setSendSmsNoTerms(true);
        dialog.setDonotAutoDismiss(true);
        dialog.setEditText(notifyInfo2s.get(position).getExpressNo());
        dialog.setEditTextHint("最大99999，前两位支持输入字母");
        dialog.setPosionClickListener(new PositonButtonOnclickListener() {

            @Override
            public void onClick(View v) {
                if (!Utility.isEmpty(dialog.getEditTextContent())) {
                    if (!dialog.getEditTextContent().trim().isEmpty()) {
                        if (dialog.isInputContentFail()) {// 如果输入内容正确
                            String cusNumber = dialog.getEditTextContent();
                            if (dialog.isSelectEditTextTermsArea()) {// 只修改当前编号
                                notifyInfo2s.get(position).setExpressNo(cusNumber);
                                adapter.setAdapterData(notifyInfo2s);
                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                dialog.dismiss();
                            } else {// 修改以下所以编号

                                String firstWord = cusNumber.substring(0, 1);
                                Pattern p = Pattern.compile("[a-zA-Z]");
                                Matcher m, m2, m3;
                                if (cusNumber.length() >= 2) {// 编号长度>=2
                                    String secondWord = cusNumber.substring(1, 2);
                                    m = p.matcher(firstWord);
                                    m2 = p.matcher(secondWord);
                                    if (m.matches() && m2.matches()) {// 前面两个字符都是字母【当前两位为字母时】
                                        if (cusNumber.length() > 2) {// 编号长度>2
                                            try {
                                                int customNo = Integer.parseInt(cusNumber.substring(2));// 将输入编号转为整型
                                                if (customNo <= 999) {
                                                    for (int i = position; i < notifyInfo2s.size(); i++) {
                                                        NotifyInfo2 notifyInfo2 = notifyInfo2s.get(i);
                                                        if (customNo > 999) {
                                                            customNo = 1;
                                                        }
                                                        notifyInfo2.setExpressNo(cusNumber.substring(0, 2) + customNo);
                                                        customNo++;
                                                    }
                                                    adapter.setAdapterData(notifyInfo2s);
                                                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                    // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                    imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                                } else {
                                                    UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            dialog.dismiss();
                                        } else {// 编号长度==2&编号全为字母
                                            UtilToolkit.showToast("字母后面需输入数字");
                                        }
                                    } else if (m.matches()) {// 只有第一个字符是字母
                                        try {
                                            int customNo = Integer.parseInt(cusNumber.substring(1));// 将输入编号转为整型
                                            if (customNo <= 9999) {
                                                for (int i = position; i < notifyInfo2s.size(); i++) {
                                                    NotifyInfo2 notifyInfo2 = notifyInfo2s.get(i);
                                                    if (customNo > 9999) {
                                                        customNo = 1;
                                                    }
                                                    notifyInfo2.setExpressNo(cusNumber.substring(0, 1) + customNo);
                                                    customNo++;
                                                }
                                                adapter.setAdapterData(notifyInfo2s);
                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    } else {// 没有字母
                                        try {
                                            int customNo = Integer.parseInt(cusNumber);// 将输入编号转为整型
                                            if (customNo <= 99999) {
                                                for (int i = position; i < notifyInfo2s.size(); i++) {
                                                    NotifyInfo2 notifyInfo2 = notifyInfo2s.get(i);
                                                    if (customNo > 99999) {
                                                        customNo = 1;
                                                    }
                                                    notifyInfo2.setExpressNo(customNo + "");
                                                    customNo++;
                                                }
                                                adapter.setAdapterData(notifyInfo2s);
                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                } else {// 编号长度<2
                                    m3 = p.matcher(firstWord);
                                    if (m3.matches()) {// 编号长度==1&为字母
                                        UtilToolkit.showToast("字母后面需输入数字");
                                    } else {// 编号长度==1&为数字
                                        try {
                                            int customNo = Integer.parseInt(cusNumber);// 将输入编号转为整型
                                            if (customNo <= 99999) {
                                                for (int i = position; i < notifyInfo2s.size(); i++) {
                                                    NotifyInfo2 notifyInfo2 = notifyInfo2s.get(i);
                                                    if (customNo > 99999) {
                                                        customNo = 1;
                                                    }
                                                    notifyInfo2.setExpressNo(customNo + "");
                                                    customNo++;
                                                }
                                                adapter.setAdapterData(notifyInfo2s);
                                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                                // 2.调用hideSoftInputFromWindow方法隐藏软键盘
                                                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                                            } else {
                                                UtilToolkit.showToast("您输入的编号超出范围，请重新输入");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                    }
                                }
                            }

                        } else {
                            UtilToolkit.showToast("起始编号格式有误，请重新输入");
                        }
                    }
                }
                // 调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
            }
        });
        dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

            @Override
            public void onClick() {
                // 调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(dialog.getEditTextView().getWindowToken(), 0); // 强制隐藏键盘
                dialog.dismiss();
            }
        });
        dialog.showDialog();
        Utility.showKeyBoard((EditText) dialog.getEditTextView(), true);
    }

    /**
     * 使用自己手机号发送
     **/
    private void mPopSendMsgOwnPhone(View v, String title, String content, String positiveText, String negativeText) {
        dialog = new SkuaidiDialog(mContext);
        dialog.setTitle(title);
        dialog.setContent(content);
        dialog.isUseEditText(false);
        if (!Utility.isEmpty(positiveText)) {
            dialog.setPositionButtonTitle(positiveText);
        } else {
            dialog.setPositionButtonTitle("确认");
        }
        dialog.setNegativeButtonTitle(negativeText);
        dialog.setPosionClickListener(new PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                UMShareManager.onEvent(mContext, "sendMSG_byOwnPhone", "sendMSG", "发短信:使用自己手机群发短信");
                sendMsg(v, false);
            }
        });
        dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
            @Override
            public void onClick() {
                tvMore.setEnabled(true);
            }
        });
        dialog.showDialog();
    }

    // private int lastNumber = 0;// 保存最后一条带手机号的编号
    private boolean exist_orderNo = false;// 列表是否输入单号

    /**
     * 获取保存编号
     **/
    private SaveNoEntry getSaveNoEntry(String save_letter, int save_number) {
        SaveNoEntry saveNoEntry = new SaveNoEntry();
        saveNoEntry.setSave_from(SaveNoDAO.NO_SMS);
        saveNoEntry.setSaveTime(System.currentTimeMillis());
        saveNoEntry.setSave_userPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
        saveNoEntry.setSave_letter(save_letter);
        saveNoEntry.setSave_number(save_number);
        return saveNoEntry;
    }

    /**
     * 发送短信
     *
     * @param v
     * @param sendOnline true：在线发送 false :使用自己手机发送
     */
    private void sendMsg(View v, final boolean sendOnline) {

        // 多单发送
        if (etNotifyContent.getText().toString().trim().equals("")) {
            UtilToolkit.showToast("发送内容不能为空");
            tvMore.setEnabled(true);
            return;
        }

        if (null != infos && 0 != infos.size()) {
            int existInputPhoneNo = 0;// 最后一条输入了手机号的行的编号
            String send_mobile = "";// 从列表第一条开始遍历列表是否存在手机号
            // 通过循环找出列表中最后一条输入了手机号行的下标
            for (int j = infos.size() - 1; j >= 0; j--) {
                String mobile = infos.get(j).getSender_mobile();
                if (!Utility.isEmpty(mobile)) {
                    existInputPhoneNo = j;
                    String lastNumber = "";
                    if (j + 1 < infos.size()) {
                        lastNumber = infos.get(j + 1).getExpressNo();
                    } else {
                        lastNumber = infos.get(j).getExpressNo();
                    }

                    String firstChar = lastNumber.substring(0, 1);// 编号第一个字符
                    String regularExpression = "[a-zA-Z]";
                    Pattern p = Pattern.compile(regularExpression);
                    Matcher m, m2;
                    m = p.matcher(firstChar);
                    if (lastNumber.length() > 2) {
                        String secondChar = lastNumber.substring(1, 2);// 编号第二个字符
                        m2 = p.matcher(secondChar);
                        if (j + 1 < infos.size()) {
                            if (m.matches() && m2.matches()) {// 前两个字符都是字母
                                saveNoEntry = getSaveNoEntry(firstChar + secondChar, Integer.parseInt(lastNumber.substring(2)));
                            } else if (m.matches() && !m2.matches()) {// 只有第一个字符是字母
                                saveNoEntry = getSaveNoEntry(firstChar, Integer.parseInt(lastNumber.substring(1)));
                            } else if (!m.matches() && !m2.matches()) {// 全部是数字
                                saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber));
                            }
                        } else {
                            if (m.matches() && m2.matches()) {// 前两个字符都是字母
                                saveNoEntry = getSaveNoEntry(firstChar + secondChar, Integer.parseInt(lastNumber.substring(2)) + 1);
                            } else if (m.matches() && !m2.matches()) {// 只有第一个字符是字母
                                saveNoEntry = getSaveNoEntry(firstChar, Integer.parseInt(lastNumber.substring(1)) + 1);
                            } else if (!m.matches() && !m2.matches()) {// 全部是数字
                                saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber) + 1);
                            }
                        }

                    } else if (lastNumber.length() == 2) {
                        String secondChar = lastNumber.substring(1, 2);// 编号第二个字符
                        m2 = p.matcher(secondChar);
                        if (j + 1 < infos.size()) {
                            if (m.matches() && m2.matches()) {
                                saveNoEntry = getSaveNoEntry(firstChar + secondChar, 1);
                            } else if (m.matches() && !m2.matches()) {
                                saveNoEntry = getSaveNoEntry(firstChar, Integer.parseInt(lastNumber.substring(1)));
                            } else if (!m.matches() && !m2.matches()) {
                                saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber));
                            }
                        } else {
                            if (m.matches() && m2.matches()) {
                                saveNoEntry = getSaveNoEntry(firstChar + secondChar, 1);
                            } else if (m.matches() && !m2.matches()) {
                                saveNoEntry = getSaveNoEntry(firstChar, Integer.parseInt(lastNumber.substring(1)) + 1);
                            } else if (!m.matches() && !m2.matches()) {
                                saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber) + 1);
                            }
                        }

                    } else {
                        if (j + 1 < infos.size()) {
                            // 只有一个字符并且是字母
                            if (m.matches()) {
                                saveNoEntry = getSaveNoEntry(firstChar, 1);
                            } else {// 只有一个字符并且是数字
                                saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber));
                            }
                        } else {
                            // 只有一个字符并且是字母
                            if (m.matches()) {
                                saveNoEntry = getSaveNoEntry(firstChar, 1);
                            } else {// 只有一个字符并且是数字
                                saveNoEntry = getSaveNoEntry("", Integer.parseInt(lastNumber) + 1);
                            }
                        }
                    }
                    break;
                } else {
                    existInputPhoneNo = 0;
                }
            }
            for (int i = 0; i < infos.size(); i++) {
                send_mobile = infos.get(i).getSender_mobile();
                if (!Utility.isEmpty(send_mobile)) {
                    break;
                }
            }

            // 遍历查找列表中是否填写单号-在上传E3过程中会使用到这个状态(如果没有填写单号是不可以进入上传E3界面操作的)
            if (null != infos && 0 != infos.size()) {
                for (int i = 0; i < infos.size(); i++) {
                    if (!TextUtils.isEmpty(infos.get(i).getExpress_number())) {// 如果运存在单号
                        exist_orderNo = true;// 存在运单号
                        break;
                    }
                }
            }
            if (Utility.isEmpty(send_mobile)) {
                UtilToolkit.showToast("请填写手机号");
                tvMore.setEnabled(true);
            } else {
                bestNewDatas = new ArrayList<>();// 实例一个新集合，存放连续的信息
                NotifyInfo2 bestNewData;
                for (int i = 0; i <= existInputPhoneNo; i++) {
                    if (!Utility.isEmpty(infos.get(i).getSender_mobile())) {
                        bestNewData = infos.get(i);
                        String mobile = bestNewData.getSender_mobile();
                        // 判断字符串中首次出现+86出现的位置从0开始，没有返回-1
//                        if (mobile.substring(0, 1).equals("1") && mobile.length() == 11 && StringUtil.judgeStringEveryCharacterIsNumber(mobile)) {
                        char c = 'x';
                        if(mobile.substring(0, 1).equals("1") && mobile.length() == 11 && StringUtil.isPhoneString(mobile,c)){
                            bestNewDatas.add(bestNewData);
                        } else {
                            tvMore.setEnabled(true);
                            UtilToolkit.showToast("编号为" + bestNewData.getExpressNo() + "的手机号有误");
                            return;
                        }
                    }
                }

                if (checkWhetherNoInput(infos)){
                    tvMore.setEnabled(true);
                    SkuaidiDialog dialog = new SkuaidiDialog(mContext);
                    dialog.setTitle("温馨提示");
                    dialog.setContent("部分手机号\n未填写，是否继续发送？");
                    dialog.isUseEditText(false);
                    dialog.setPositionButtonTitle("继续发送");
                    dialog.setNegativeButtonTitle("取消");
                    dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                        @Override
                        public void onClick(View v) {
                            isExistTheSameMobile(v,sendOnline);
                        }
                    });
                    dialog.showDialog();
                }else{
                    isExistTheSameMobile(v,sendOnline);
                }


            }
        } else {
            UtilToolkit.showToast("请输入要发送的手机号");
            tvMore.setEnabled(true);
        }
    }

    private void isExistTheSameMobile(View v,final boolean sendOnline){
        // 判断列表里面是否存在相同的手机号
        boolean exist = getExistTheSameMobilePhone(bestNewDatas);
        if (exist) {
            tvMore.setEnabled(true);
            dialog = new SkuaidiDialog(mContext);
            dialog.isUseEditText(false);
            dialog.setTitle("提示");
//            dialog.setTitleSkinColor("main_color");
            dialog.setContent(
                    "编号：" + bestNewDatas.get(i).getExpressNo() + "  和编号：" + bestNewDatas.get(j).getExpressNo() + "  的手机号相同，是否返回修改？");
            dialog.setPositionButtonTitle("继续发送");
            dialog.setNegativeButtonTitle("返回修改");
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {// 继续发送

                @Override
                public void onClick(View v) {
                    if (sendOnline) {// 在线发送
                        sendMSGOnline(bestNewDatas);
                    } else {// 使用自己手机发送
                        sendMSGByOwn(bestNewDatas);
                    }
                }
            });
            dialog.showDialog();
        } else {
            if (sendOnline) {// 在线发送
                sendMSGOnline(bestNewDatas);
            } else {// 使用自己手机发送
                sendMSGByOwn(bestNewDatas);
            }
        }
    }

    /**检查是否有单号没有输入手机号
     * 至少有一条未输入手机号码：return true;
     * 全部单号都对应输入手机号码：return false;**/
    private boolean checkWhetherNoInput(List<NotifyInfo2> list){
        for (NotifyInfo2 npp : list){
            if (!Utility.isEmpty(npp.getExpress_number()) && Utility.isEmpty(npp.getSender_mobile())){
                return true;
            }
        }
        return false;
    }

//    private List<SendMSGParmeter> sendMSGParmeters = null;

    // 发短信- 将调用接口部分分离开
    private void sendMSGOnline(List<NotifyInfo2> bestNewDatas) {
        UMShareManager.onEvent(mContext, "sendMSG_byOnLine", "sendMSG", "发短信:在线发送短信");

        List<SendMSGParmeter> sendMSGParmeters = new ArrayList<>();
        for (int i = 0; i < bestNewDatas.size(); i++) {
            SendMSGParmeter sendMSGParmeter = new SendMSGParmeter();
            NotifyInfo2 notifyInfo = bestNewDatas.get(i);
            sendMSGParmeter.setBh(notifyInfo.getExpressNo());
            sendMSGParmeter.setDh(notifyInfo.getExpress_number());
            // 将号码中的x换成*
            String phone = notifyInfo.getSender_mobile().replaceAll("x","*");
            sendMSGParmeter.setUser_phone(phone);
            sendMSGParmeters.add(sendMSGParmeter);
        }
        Gson gson = new Gson();
        final String sendmsg_data_json = gson.toJson(sendMSGParmeters).toString();

        String pattern = "[a-zA-Z\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(pattern);
        Matcher m, m2;
        m = p.matcher(msgLinShiContent);
        m2 = p.matcher(etNotifyContent.getText().toString());

        String modelString = "";// 模板内容【全部是中文】
        String curString = "";// 当前修改过后的模板内容【全部是中文】
        while (m.find()) {
            modelString = modelString + m.group();
        }

        while (m2.find()) {
            curString = curString + m2.group();
        }

        // 如果该用户是发短信VIP用户
        if (!Utility.isEmpty(SkuaidiSpf.getClientIsVIP(mContext)) && SkuaidiSpf.getClientIsVIP(mContext).equals("y")) {
            scanOrderSendMsg(sendmsg_data_json);
        } else {
            // 判断现在使用的模板是否是已经审核过的
            if (!Utility.isEmpty(msgLinShiContent) && modelString.equals(curString) && !Utility.isEmpty(modelStatus)
                    && modelStatus.equals("approved")) {// 没有修改内容(使用的是审核通过的模板)且是审核通过的
                scanOrderSendMsg(sendmsg_data_json);
            } else {
                mPopSendMsgOwnPhone(tvMore, "提示", "您发送的短信未通过审核，是否通过自己手机发送？", "确定", "取消");// 提示使用自己手机发送
            }
        }
    }

    private void scanOrderSendMsg(final String sendmsg_data_json) {
        if (cb_gunScanBol && !exist_orderNo) {// 如果巴抢扫描按钮被选中以后但没有填入单号
            dialog = new SkuaidiDialog(mContext);
            dialog.setTitle("提示");
            dialog.setContent("您还没有填入单号，无法做巴枪扫描，是否继续发送短信？");
            dialog.isUseEditText(false);
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                @Override
                public void onClick(View v) {
                    cb_gunScanBol = false;
                    sendMsg(modelID, getSendMsgContent(), sendmsg_data_json, timeStamp, voice_ivid, SkuaidiSpf.getLoginUser().getExpressNo(), 1);// 【调用发送短信接口】
                    skuaidiDB.clearTableOrder();// 删除扫描后的单号列表
                    showProgressDialog( "短信发送中...");
                }
            });
            dialog.showDialog();
            tvMore.setEnabled(true);
        } else {
            sendMsg(modelID, getSendMsgContent(), sendmsg_data_json, timeStamp, voice_ivid, SkuaidiSpf.getLoginUser().getExpressNo(), 1);// 【调用发送短信接口】
            skuaidiDB.clearTableOrder();// 删除扫描后的单号列表
            if (!isFinishing())
                showProgressDialog( "短信发送中...");
        }
    }

    /**
     * getSendMsgContent:获取发送短信内容
     * 作者： 顾冬冬
     */
    private String getSendMsgContent() {
        String sendContent = etNotifyContent.getText().toString();
        if (sendContent.contains(ordernum)) {
            sendContent = sendContent.replaceAll(ordernum, "#NO#");
        }
        if (sendContent.contains(orderDH)) {
            sendContent = sendContent.replaceAll(orderDH, "#DH#");
        }
        if (sendContent.contains(model_url)) {
            sendContent = sendContent.replaceAll(model_url, "#SURL#");
        }
        return sendContent;
    }

    // 获取列表中是否在在相同的手机号
    private boolean getExistTheSameMobilePhone(List<NotifyInfo2> list) {
        boolean exist = false;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (!list.get(i).getSender_mobile().contains("*")
                        && !list.get(i).getSender_mobile().contains("x")
                        && list.get(i).getSender_mobile().equals(list.get(j).getSender_mobile())) {
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

    String sendMobile = "";// 保存要发送的手机号字符串

    /**
     * 使用自己手机发送
     **/
    private void sendMSGByOwn(List<NotifyInfo2> bestNewDatas) {
        String sendMsgInfo_original = etNotifyContent.getText().toString();// 发送短信内容
        final Map<String, String> splitJointStr = new HashMap<>();
        String sendMsgInfo;
        for (int i = 0; i < bestNewDatas.size(); i++) {
            sendMsgInfo = sendMsgInfo_original;
            NotifyInfo2 notifyInfo = bestNewDatas.get(i);

            if (sendMsgInfo_original.contains(ordernum)) {
                if (!Utility.isEmpty(notifyInfo.getExpressNo())) {
                    sendMsgInfo = sendMsgInfo.replaceAll(ordernum, notifyInfo.getExpressNo());// 替换编号
                } else {
                    sendMsgInfo = sendMsgInfo.replaceAll(orderDH, "");
                }
            }

            if (sendMsgInfo_original.contains(orderDH)) {
                if (!Utility.isEmpty(notifyInfo.getExpress_number())) {
                    sendMsgInfo = sendMsgInfo.replaceAll(orderDH, notifyInfo.getExpress_number());// 替换单号
                } else {
                    sendMsgInfo = sendMsgInfo.replaceAll(orderDH, "");
                }
            }

            if (sendMsgInfo_original.contains(model_url)) {
                sendMsgInfo = sendMsgInfo.replace(model_url, "");// 替换URL
            }
            splitJointStr.put(notifyInfo.getSender_mobile(), sendMsgInfo);
            if (sendMobile.equals("")) {
                sendMobile = notifyInfo.getSender_mobile();
            } else {
                sendMobile = sendMobile + ";" + notifyInfo.getSender_mobile();
            }
        }

        if (cb_gunScanBol && !exist_orderNo) {// 如果巴抢扫描按钮被选中以后但没有填入单号
            dialog = new SkuaidiDialog(mContext);
            dialog.isUseEditText(false);
            dialog.setTitle("提示");
            dialog.setContent("您还没有填入单号，无法做巴枪扫描，是否继续发送短信？");
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    cb_gunScanBol = false;
                    sendSMS(sendMobile, "", splitJointStr);// 使用自己的手机发送短信（使用手机在后台发送短信）
                }
            });
            dialog.showDialog();
        } else {
            sendSMS(sendMobile, "", splitJointStr);// 使用自己的手机发送短信（使用手机在后台发送短信）
        }
        // last_phone_no_byMyPhone =
        // Integer.parseInt(notifyInfo.getExpressNo());
        tvMore.setEnabled(true);
    }

    /**
     * 进入巴枪扫描界面
     */
    private void gunScan() {
        if (cb_gunScanBol) {
            cb_gunScanBol = false;
            cbGunScan.setBackgroundResource(R.drawable.icon_push_close);
        } else {
            cb_gunScanBol = true;
            cbGunScan.setBackgroundResource(R.drawable.icon_push_open);
            UtilToolkit.showToast("群发短信后可直接做巴枪上传");
            SkuaidiSpf.saveGunScanStatus(mContext, true);
        }
        msg = new Message();
        msg.what = REFRESH_VIEW;
        mHandler.sendMessage(msg);
    }

    /**
     * 判断列表中是否有数据
     **/
    private boolean judgeIsEmptyForInfos() {
        if (infos != null && infos.size() != 0) {
            NotifyInfo2 notifyInfo2;
            for (int i = 0; i < infos.size(); i++) {
                notifyInfo2 = infos.get(i);
                if (!Utility.isEmpty(notifyInfo2.getSender_mobile()) || !Utility.isEmpty(notifyInfo2.getExpress_number()))
                    return true;
            }
        }
        return false;
    }

    /**
     * 判断列表中是否有手机号码
     **/
    private boolean judgeIsEmptyForInforsByPhone() {
        if (null != infos && 0 != infos.size()) {
            NotifyInfo2 notifyInfo2;
            for (int i = 0; i < infos.size(); i++) {
                notifyInfo2 = infos.get(i);
                if (!Utility.isEmpty(notifyInfo2.getSender_mobile())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMsgModify() {
        String curTemplate = etNotifyContent.getText().toString();
        return !msgModelId.equals(modelID)
                || !msgModelContent.equals(curTemplate)
                || !msgNumber.equals(DraftBoxUtility.pinjieNumber(adapter.getListData()))
                || !msgNumberPhone.equals(DraftBoxUtility.pinjiePhoneNumber(adapter.getListData()))
                || !msgNumberOrder.equals(DraftBoxUtility.pinjieOrderNumber(adapter.getListData()));
    }

    private void finishContext() {
        stopDistinguish();
        // 列表中有数据
        if (judgeIsEmptyForInfos()) {
            // 从巴枪扫描界面【收件扫描】【派件扫描】功能传来的参数
            if (null != problemOrder && problemOrder.length > 0) {
                // 列表里在存在手机号码
                if (judgeIsEmptyForInforsByPhone()) {
                    if (null != dialog && dialog.isShowing()) {
                        dialog.dismiss();
                    } else {
                        dialog = new SkuaidiDialog(mContext);
                        dialog.setTitle("提示");
                        dialog.setContent("您确定要取消通知客户？");
                        dialog.isUseEditText(false);
                        dialog.setPositionButtonTitle("确定");
                        dialog.setNegativeButtonTitle("取消");
                        dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                            @Override
                            public void onClick(View v) {
                                RecordDraftBoxDAO.deleteDraft(draft_id);
                                finish();
                            }
                        });
                        dialog.showDialog();
                    }
                } else {
                    RecordDraftBoxDAO.deleteDraft(draft_id);
                    finish();
                }
            } else {
                // 如果是从草稿箱进来 的，并且没有对内容进行修改直接关闭界面
                if ((!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) && !isMsgModify()) {
                    finish();
                    // 如果是从草稿箱进来的，并且数据全部被删掉了，则删除该内容
                } else if ((!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) && "".equals(modelContent)
                        && !judgeIsEmptyForInfos()) {
                    RecordDraftBoxDAO.deleteDraft(draft_id);
                } else {
                    if (null != dialog && dialog.isShowing()) {
                        dialog.dismiss();
                    } else {
                        dialog = new SkuaidiDialog(mContext);
                        dialog.setTitle("离开提示");
                        dialog.isUseEditText(false);
                        if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {// 如果是从草稿箱中而来
                            dialog.setContent("短信内容已被修改，是否保存修改后的内容？");
                        } else {
                            dialog.setContent("是否将已编辑过的短信内容保存到草稿箱？");
                        }
                        dialog.setPositionButtonTitle("是");
                        dialog.setNegativeButtonTitle("否");
                        dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                            @Override
                            public void onClick(View v) {
                                SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");
                                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                                finish();
                            }
                        });
                        dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

                            @Override
                            public void onClick() {
                                if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {
                                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(false));
                                } else {
                                    RecordDraftBoxDAO.deleteDraft(draft_id);
                                    SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");
                                }
                                finish();
                            }
                        });
                        dialog.showDialog();
                    }
                }
            }
        } else {
            if (!Utility.isEmpty(fromActivity) && "draftbox".equals(fromActivity)) {
                if ("".equals(etNotifyContent.getText().toString().trim())) {
                    RecordDraftBoxDAO.deleteDraft(draft_id);
                } else {
                    RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(false));
                }
            } else {
                RecordDraftBoxDAO.deleteDraft(draft_id);
            }
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishContext();
            return true;
        }

        return false;
    }

    @Override
    protected void OnSMSSendSuccess() {
        RecordDraftBoxDAO.deleteDraft(draft_id + "");
        // 自己手机发送成功以后再保存发送成功以后的最后一个单号
        SaveNoDAO.saveNo(saveNoEntry);
        UtilToolkit.showToast("短信发送成功");
        SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");

        // 判断是否选中过巴枪扫描-选中则跳转到上传界面
        if (cb_gunScanBol) {
            if (infos != null && infos.size() != 0) {
                mIntent = new Intent(mContext, EThreeInterfaceActivity.class);
                mIntent.putExtra("e3WayBills", (Serializable) infos);
                startActivity(mIntent);
            }
        }
        // 判断是否是上传问题件传过来的单号，在发送短信成功以后则跳转至业务首页
        if (isProblemOrder) {
            mIntent = new Intent(mContext, MainActivity.class);
            startActivity(mIntent);
        }
        finish();
    }

    @Override
    protected void OnSMSSendFail() {
        super.OnSMSSendFail();
        tvMore.setEnabled(true);
        UtilToolkit.showToast("短信发送失败");
    }

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
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            UtilToolkit.showToast_Custom("听写失败,错误码：" + ret);
        } else {
            UtilToolkit.showToast_Custom("开始说话");
        }
        // UtilToolkit.showToast("建议在wifi下使用,在线语音识别会消耗流量");
    }

    /**
     * 初始化监听器。
     */
    private static InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            //Log.d(TAG, "初始化失败，错误码： " + code);
//			if (code != ErrorCode.SUCCESS) {
//				// UtilToolkit.showToast("初始化失败，错误码：" + code);
//			}
        }
    };


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
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//			 UtilToolkit.showToast("aaaa开始说话aaaa");
        }

        @Override
        public void onError(SpeechError error) {
            //Log.i("logerror", ""+error.getErrorCode());
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
//				stopDistinguish();
//				showTips(mContext);
            } else if (error.getErrorCode() == 20006) {// 这个判断码肯定是权限没打开
                stopDistinguish();
//				showTips(mContext);
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

//			if (isLast) {
//			}
        }

        @Override
        public void onVolumeChanged(final int volume, byte[] data) {

//			 showTip("当前正在说话，音量大小：" + volume);
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
//				String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
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
                    UtilToolkit.showToast_Custom("错误的手机号：" + sentence);
                    sentence = "";
                }
            } else if (word.length() > 12) {
                sentence = "";
                stopCountDown();// 停止倒计时线程
                UtilToolkit.showToast_Custom("错误的手机号：" + word);
            } else {
                shibieSuccess();// 11位手机号码识别成功
            }
        }
    }

    private void shibieSuccess() {
        if (word.length() == 11) {
            Pattern p = Pattern.compile("[1]\\d{10}");
            Matcher m = p.matcher(word);
            // 当找到了正确的手机号码
            while (m.find()) {
                infos.get(distinguishMark).setSender_mobile(word);
                adapter.setAdapterData(infos);
                setNextDistinguish();
                Utility.playMusicDing();
                UtilToolkit.showToast_Custom(word);
                RecordDraftBoxDAO.insertDraftInfo(setDraftBoxInfo(true));
                SaveUnnormalExitDraftInfoDAO.deleteUnnormalExitDraftInfo("sms");
                saveNewestPhone();
//				// 将界面数据保存到草稿箱
//				RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(infos, false));
            }
            getSearchPhoneNumber(infos);
            lvNotify.setSelection(showListLine());
        } else {
            UtilToolkit.showToast_Custom("错误的手机号：" + word);
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
        infos.get(distinguishMark).setPlayVoiceAnim(false);
        distinguishMark++;
        if (distinguishMark == infos.size()) {
            distinguishMark = infos.size() - 1;
            stopDistinguish();
        } else {
            infos.get(distinguishMark).setPlayVoiceAnim(true);
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
            infos.get(distinguishMark).setPlayVoiceAnim(false);
            adapter.setAdapterData(infos);
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
                    //Log.i("logi", time + "");
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
     * 调用 接口--调用通知，用于显示在底部通知栏中
     */
    private void getBroadCastNotify() {
        ApiWrapper apiWrapper = new ApiWrapper();
        Subscription subscription = apiWrapper.getBoardCastNotify("inform.broadcast","get","").subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
            @Override
            public void call(com.alibaba.fastjson.JSONObject jsonObject) {
                if (jsonObject != null){
                    if (jsonObject.getString("status").equals("success")){
                        final String content = jsonObject.getJSONObject("result").getJSONObject("retArr").getString("content");
                        notifyBroadCast = new NotifyBroadCast(mContext, 10, llTitle, content, onClickListener);
                        llTitle.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyBroadCast.show();
                            }
                        });
                    }
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    /**
     * 判断巴枪扫描审核状态
     */
    private void getGunScanApprovalStatus() {
        // 巴枪未审核
        JSONObject object = new JSONObject();
        try {
            object.put("sname", E3SysManager.SCAN_COUNTERMAN_VERIFY);
            object.put("act", "getinfo");
            object.put("new_verify", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
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

    /**
     * 获取昨日未取件手机号信息（短信条数）
     **/
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

    /**
     * 给昨日未取件手机号发短信&云呼
     **/
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

    /**
     * 免费发送短信
     **/
    private void sendMsg(String sms_tid, String sms_content, String batch_data, Long timeStamp, String ivr_tid, String brand, int use_wallet) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_user/run");
            data.put("role", "courier");
            if (!Utility.isEmpty(sms_tid)) {
                data.put("sms_tid", sms_tid);
            }
            data.put("sms_content", sms_content);
            data.put("batch_data", batch_data);
            data.put("send_time", timeStamp);
            data.put("ivr_tid", ivr_tid);// 发送短信失败自动云呼传入参数-云呼模板ID-不传不会发送云呼
            data.put("brand", brand);// brand:品牌
            data.put("use_wallet", use_wallet);// 传1直接使用发短信，不作通知【免费短信余额不足，是否使用自费金额发送？（4分/条）】
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String message, String json, String act) {
        dismissProgressDialog();
        tvMore.setEnabled(true);
        Message msg = null;
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (E3SysManager.SCAN_COUNTERMAN_VERIFY.equals(sname) && "getinfo".equals(act)) {// 巴枪审核
            //List<CourierReviewInfo> courierInfos = new ArrayList<>();
            if (Utility.isEmpty(result)) {
                UtilToolkit.showToast("获取消息失败");
                return;
            }
            JSONObject courier_infos = result.optJSONObject("retArr");
            int state = result.optInt("verified");
            if (courier_infos != null) {
                @SuppressWarnings("unchecked")
                Iterator<String> iterator = courier_infos.keys();
                int index = 0;
                while (iterator.hasNext()) {
                    if (index == 1) {
                        break;
                    }
                    String key = iterator.next();
                    try {
                        JSONObject info = courier_infos.getJSONObject(key);
//                        CourierReviewInfo reviewInfo = new CourierReviewInfo();
//                        reviewInfo.setCourierPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
//                        reviewInfo.setCourierJobNo(info.optString("counterman_code"));
//                        reviewInfo.setCourierName(info.optString("counterman_name"));
//                        reviewInfo.setCourierLatticePoint(info.optString("shop_name"));
//                        courierInfos.add(reviewInfo);
                        if (state == 1) {// 审核通过 verified审核状态 1：通过 0：未通过
                            info.put("isThroughAudit", 1);
                            UpdateReviewInfoUtil.updateCurrentReviewStatus(info.toString());
//                            for (int i = 0; i < courierInfos.size(); i++) {
//                                courierInfos.get(i).setIsThroughAudit(1);
//                                finalDb.save(courierInfos.get(i));
                            gunScan();// 进入巴枪扫描界面
                            //}
                        } else {
                            UtilToolkit.showToast("巴枪未认证，无法在群发短信里使用上传功能");
                            finalDb.deleteByWhere(CourierReviewInfo.class, "courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
                            UpdateReviewInfoUtil.updateCurrentReviewStatus("");
                        }
                        index++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                UtilToolkit.showToast("巴枪未认证，无法在群发短信里使用上传功能");
                finalDb.deleteByWhere(CourierReviewInfo.class, "courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
                UpdateReviewInfoUtil.updateCurrentReviewStatus("");
            }
        } else if (!Utility.isEmpty(sname) && "express.contacts".equals(sname)) {// 调用接口获取收件人信息
            List<ReceiverInfo> receiverInfo = JsonXmlParser.parseReceiverInfo(result);
            msg = new Message();
            msg.what = GET_PHONENUMBER_SUCCESS;
            msg.obj = receiverInfo;
        } else if (!Utility.isEmpty(sname) && "inform_nosigned/send".equals(sname)) {
            if (!Utility.isEmpty(message)) {
                UtilToolkit.showToast(message);
            }
        } else if (!Utility.isEmpty(sname) && "inform_nosigned/getsmsinfo".equals(sname)) {
            int needSendCount = -1;
            String desc = "";
            try {
                if (!Utility.isEmpty(result)) {
                    needSendCount = result.getInt("needSendCount");
                    desc = result.getString("msg");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg = new Message();
            msg.what = GET_NOSIGNED_INFO_SUCCESS;
            msg.obj = desc;
            msg.arg1 = needSendCount;
        } else if (!Utility.isEmpty(sname) && "inform_user/run".equals(sname)) {
            msg = new Message();
            msg.what = Constants.SUCCESS;
            if (!Utility.isEmpty(message)) {
                UtilToolkit.showToast(message);
            }
        } else if (SMS_SIGNED.equals(sname)) {
            dialog = new SkuaidiDialog(this);
            dialog.isUseSingleButton(true);
            dialog.isUseEditText(false);
            dialog.setSingleButtonTitle("知道了");
            dialog.setSingleClickListener(new SingleButtonOnclickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            });
            dialog.setContent("待单号状态变为已签收的时候，发件客户会收到这条签收短信。签收短信不会出现在短信记录中。");
            dialog.setTitle("提交成功");
            dialog.show();

        } else {// 发送短信成功
            msg = new Message();
            msg.what = Constants.SUCCESS;
            msg.obj = result;
            // msg.arg1 = lastNumber;
        }

        if (null != mHandler && null != msg) {
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String msg, String act, JSONObject data_fail) {
        dismissProgressDialog();
        tvMore.setEnabled(true);
        if (null != data_fail) {
            String confirm = data_fail.optString("confirm");
            switch (confirm) {
                case "recharge":
                    dialog = new SkuaidiDialog(mContext);
                    dialog.setTitle("余额不足");
                    dialog.setContent(msg);
                    dialog.isUseEditText(false);
                    dialog.setPositionButtonTitle("充值");
                    dialog.setNegativeButtonTitle("取消");
                    dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                        @Override
                        public void onClick(View v) {
                            mIntent = new Intent(mContext, TopUpActivity.class);
                            startActivity(mIntent);
                        }
                    });
                    dialog.showDialog();
                    break;
                case "template_disapprove":
                    dialog = new SkuaidiDialog(mContext);
                    dialog.setTitle("提示");
                    dialog.setContent(msg);
                    dialog.isUseEditText(false);
                    dialog.setPositionButtonTitle("确定");
                    dialog.setNegativeButtonTitle("取消");
                    dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                        @Override
                        public void onClick(View v) {
                            sendMSGByOwn(bestNewDatas);
                        }
                    });
                    dialog.showDialog();
                    break;
                default:
                    UtilToolkit.showToast(msg);
                    break;
            }
            return;
        }
        if (!Utility.isEmpty(msg)) {
            UtilToolkit.showToast(msg);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        dismissProgressDialog();
        tvMore.setEnabled(true);
        if ("7".equals(code) && result != null) {
            try {
                String desc = result.optString("desc");
                String recharge = result.optString("recharge");
                if (!Utility.isEmpty(recharge) && "y".equals(recharge)) {
                    dialog = new SkuaidiDialog(mContext);
                    dialog.setTitle("余额不足");
                    dialog.setContent(desc);
                    dialog.isUseEditText(false);
                    dialog.setPositionButtonTitle("充值");
                    dialog.setNegativeButtonTitle("取消");
                    dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                        @Override
                        public void onClick(View v) {
                            mIntent = new Intent(mContext, TopUpActivity.class);
                            startActivity(mIntent);
                        }
                    });
                    dialog.showDialog();
                } else {
                    UtilToolkit.showToast(desc);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("12".equals(code)) {
            UtilToolkit.showToast(msg);
        } else {
            UtilToolkit.showToast(msg);
        }
    }

    /**
     * 签收后，发送短信通知发件人
     */
    private void signSMS() {
        if (TextUtils.isEmpty(etNotifyContent.getText().toString().trim())) {
            UtilToolkit.showToast("短信内容不能为空");
            return;
        }
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < problemOrder.length; i++) {
                JSONObject orderInfo = new JSONObject();
                if (!TextUtils.isEmpty(infos.get(i).getSender_mobile())) {
                    orderInfo.put("waybillNo", infos.get(i).getExpress_number());
                    orderInfo.put("mobile", infos.get(i).getSender_mobile().replaceAll(" ", ""));
                    orderInfo.put("no", infos.get(i).getExpressNo().replaceAll(" ", ""));
                    array.put(orderInfo);
                }
            }
            if (array.length() == 0) {
                UtilToolkit.showToast("请输入手机号");
                return;
            }

            JSONObject data = new JSONObject();
            data.put("sname", SMS_SIGNED);
            data.put("signedDatas", array);
            data.put("smsContent", getSendMsgContent());
            data.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            UMShareManager.onEvent(SendMSGActivity.this, "send_msg_problem", "send_msg", "问题件短信通知");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
