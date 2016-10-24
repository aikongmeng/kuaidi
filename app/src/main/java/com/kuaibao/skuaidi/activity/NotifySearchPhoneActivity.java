package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.sunflower.FlowerCollector;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.activity.adapter.NotifyShowPhoneAdapter;
import com.kuaibao.skuaidi.activity.scan_mobile.tesseract.ui.TesseractMobileActivity;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.view.NotifySearchPhoneShowTishi;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dao.CacheContactsDao;
import com.kuaibao.skuaidi.dao.RecordDraftBoxCloudVoiceDAO;
import com.kuaibao.skuaidi.dao.RecordDraftBoxDAO;
import com.kuaibao.skuaidi.dao.SaveUnnormalExitDraftInfoDAO;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.entry.CloudVoiceMsgDetailEntry;
import com.kuaibao.skuaidi.entry.DraftBoxCloudVoiceInfo;
import com.kuaibao.skuaidi.entry.DraftBoxSmsInfo;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.entry.SaveUnnormalExitDraftInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.DraftBoxUtility;
import com.kuaibao.skuaidi.util.PinyinComparator;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.kuaibao.skuaidi.util.YYSBJsonParser;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 顾冬冬
 * NotifySearchPhoneActivity
 * 免费派件通知多单发送界面联系人模糊搜索界面
 * 2015-11-30 下午7:22:51
 */
public class NotifySearchPhoneActivity extends RxRetrofitBaseActivity {
    @BindView(R.id.tvSend)
    SkuaidiTextView tvSend;// 发送按钮
    @BindView(R.id.next)
    TextView next;
    @BindView(R.id.iv_title_back)
    ImageView iv_title_back;// 返回按钮
    @BindView(R.id.imv_notify_close)
    ImageView imv_notify_close;// 清除按钮
    @BindView(R.id.tv_notify_num)
    TextView tv_notify_num;// 显示条目编号
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;// 显示单号
    @BindView(R.id.et_phone_number)
    EditText et_phone_number;// 输入单号
    @BindView(R.id.lv_show_phonenumber)
    ListView lv_show_phonenumber;// 联系人列表
    @BindView(R.id.ll_scan_btn)
    LinearLayout ll_scan_btn;
    @BindView(R.id.ll_VoiceDistinguishAnim)
    LinearLayout ll_VoiceDistinguishAnim;// 语音识别动画模块
    @BindView(R.id.ll_ScreeningContactList)
    LinearLayout ll_ScreeningContactList;// 筛选联系人列表模块
    @BindView(R.id.voice_recordinglight_1)
    ImageView mRecordLight_1 = null;// 圈
    @BindView(R.id.voice_recordinglight_2)
    ImageView mRecordLight_2 = null;
    @BindView(R.id.voice_recordinglight_3)
    ImageView mRecordLight_3 = null;
    @BindView(R.id.tvTiShi)
    TextView tvTiShi;// 语音识别文字提示
    @BindView(R.id.tvPhoneDesc)
    TextView tvPhoneDesc;// 语音识别出来的文字
    @BindView(R.id.ivSpeech)
    ImageView ivSpeech;// 识别动画图片


    private final int START_ACTIVITY_REQUEST_CODE = 0X10001;

    private String noOfTitle = "编号：%1$s";

    private InputMethodManager imm = null;
    private SkuaidiDialog dialog = null;
    public static boolean activityIsDestroy = false;// 标记activity是否被销毁

    private Animation mRecordLight_1_Animation = null;
    private Animation mRecordLight_2_Animation = null;
    private Animation mRecordLight_3_Animation = null;

    private boolean is_selector = false;

    private NotifyShowPhoneAdapter adapter;
    private List<MyCustom> list_cuss;// 我的联系人列表
    public Context context;
    private String itemPhone;// 存放点击当前item的手机号
    private int position = -1;// 单号列表下标
    private int initValueCloud = 0;// 云呼手机号个数初始值
    private int initValueMessage = 0;// 短信手机号个数初始值

    private List<NotifyInfo2> messages;// 存放接收过来的message数据
    private List<CloudVoiceMsgDetailEntry> cloudVoiceMsgDetailEntries;// 存放接收过来的编号和手机号信息
    private String order_number;// 显示到title上的运单号
    private int positionNo;// 显示到title上的条目数
    private String mobilePhoneNum;// 显示到编辑框中的手机号
    Map<String, String> map;
    private String fromActivity = "";// 来源哪个界面
    private boolean isNext = false;
    // 语音识别参数*************************************
    private SpeechRecognizer mIat;// 语音听写对象
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();// 用HashMap存储听写结果
    private int speechCount = 0;// 为了使得语音识别能自动识别60s，标记关闭次数，当达到6次瑞关闭识别
    private String word = "";
    private String sentence = "";
    private MyHandler myHandler = null;
    private Thread mThread = null;

    // ********关于草稿箱**************
    private String draft_id = "";// 数据库中对应草稿箱条目的唯一ID
    private boolean isOk = false;

    private boolean isChanged = false;
    private boolean isSetText = false;

    private int maxCount = 0;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_search_phone);
        context = this;
        ButterKnife.bind(NotifySearchPhoneActivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getControl();
        getData();
        myListener();
        setData();
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context.getApplicationContext(), mInitListener);

        if (savedInstanceState != null) {
            if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
                cloudVoiceMsgDetailEntries = (List<CloudVoiceMsgDetailEntry>) savedInstanceState.getSerializable("cloudVoiceMsgDetailEntries");
                RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
            } else {
                messages = (List<NotifyInfo2>) savedInstanceState.getSerializable("messages");
                draft_id = savedInstanceState.getString("draft_id");
                position = savedInstanceState.getInt("position", position);
                positionNo = savedInstanceState.getInt("positionNo", positionNo);
                tv_notify_num.setText(String.valueOf(positionNo));
                tv_title_des.setText(String.format(noOfTitle, messages.get(position).getExpressNo()));
            }
        }
    }

    // 初始化
    private void getControl() {
        tv_notify_num.setBackgroundResource(SkuaidiSpf.getLoginUser().getExpressNo().equals("sto") ? R.drawable.shape_circle_background3 : R.drawable.shape_circle_background2);
        if ("签收短信通知".equals(getIntent().getStringExtra("from"))) {
            tvSend.setVisibility(View.GONE);
            iv_title_back.setVisibility(View.VISIBLE);
        }
        activityIsDestroy = false;

        // 设置hint字体大小
        SpannableString ss = new SpannableString("手机号变绿直接输入下个手机号");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        et_phone_number.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失

        // edittext 编辑完成后点击键盘上的返回键触发事件
        et_phone_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /* 判断是否是“next”键 */
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                        cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                        RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                        et_phone_number.setText("");
                        if (position == cloudVoiceMsgDetailEntries.size() - 1) {
                            UtilToolkit.showToast_Custom("当前已经是最后一条!");
                        } else {
                            position = position + 1;
                            tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                            et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                            et_phone_number.setSelection(et_phone_number.getText().length());
                            tv_notify_num.setText(String.valueOf(position + 1));
                        }
                    } else {
                        messages.get(position).setSender_mobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                        RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                        et_phone_number.setText("");
                        if (position == messages.size() - 1) {
                            UtilToolkit.showToast_Custom("当前已经是最后一条!");
                        } else {
                            position = position + 1;
                            tv_title_des.setText(String.format(noOfTitle, messages.get(position).getExpressNo()));
                            et_phone_number.setText(messages.get(position).getSender_mobile());
                            et_phone_number.setSelection(et_phone_number.getText().length());
                            tv_notify_num.setText(String.valueOf(position + 1));
                        }
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private void getContactList(){
        if (list_cuss != null && list_cuss.size() != 0) {
            list_cuss.clear();
        }
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
                List<MyCustom> customs =  UtilToolkit.filledData(newDB.selectFuzzySearchCustomer(""));
                Collections.sort(customs, new PinyinComparator());
                list_cuss=customs;
                list_cuss.addAll(CacheContactsDao.getContact(""));
                return null;
            }
            protected void onPostExecute(Void result) {
                // UtilToolkit.showToast("通讯录整理完毕");
                dismissProgressDialog();
                adapter.notifyDataSetChanged();
                if (!adapter.isEmpty()) {
                    lv_show_phonenumber.setAdapter(adapter);
                }
            }
        }.execute();
    }


    @SuppressWarnings("unchecked")
    private void getData() {
        list_cuss = new ArrayList<>();
        adapter = new NotifyShowPhoneAdapter(context, list_cuss);// 实例化适配器
        getContactList();

        position = getIntent().getIntExtra("listposition", -1);
        fromActivity = getIntent().getStringExtra("fromActivity");

        if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
            cloudVoiceMsgDetailEntries = (List<CloudVoiceMsgDetailEntry>) getIntent().getSerializableExtra("message");
            maxCount = cloudVoiceMsgDetailEntries.size();// 列表条目数作为扫描界面最大输入条数
            draft_id = getIntent().getStringExtra("draft_id");
            if (null != cloudVoiceMsgDetailEntries) {
                for (int i = 0; i < cloudVoiceMsgDetailEntries.size() + 1; i++) {
                    if (i == position) {
                        order_number = cloudVoiceMsgDetailEntries.get(i).getMobile_no();
                        mobilePhoneNum = cloudVoiceMsgDetailEntries.get(i).getMobile();
                        positionNo = i + 1;
                    }
                }
                for (int j = 0; j < cloudVoiceMsgDetailEntries.size(); j++) {
                    if (!Utility.isEmpty(cloudVoiceMsgDetailEntries.get(j).getMobile())) {
                        initValueCloud++;
                    }
                }
            }
        } else {
            messages = (List<NotifyInfo2>) getIntent().getSerializableExtra("notifyinfos");
            maxCount = messages.size();
            draft_id = getIntent().getStringExtra("draft_id");
            if (messages != null) {
                for (int i = 0; i < messages.size() + 1; i++) {
                    if (i == position) {
                        order_number = messages.get(i).getExpressNo();
                        mobilePhoneNum = messages.get(i).getSender_mobile();
                        positionNo = i + 1;
                    }
                }
                for (int j = 0; j < messages.size(); j++) {
                    if (!Utility.isEmpty(messages.get(j).getSender_mobile())) {
                        initValueMessage++;
                    }
                }
            }
        }
    }

    // 设置监听事件
    private void myListener() {

        // 手机号输入框内容变化监听
        et_phone_number.addTextChangedListener(new TextWatcher() {
            boolean isToNext = true;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (isChanged)
                    return;
                isChanged = true;
                String lastNo = et_phone_number.getText().toString();// 每输入一个字符时都取得输入进去的号码
                if (!TextUtils.isEmpty(lastNo) && lastNo.contains("*")) {
                    lastNo = lastNo.replaceAll("[*]", "x");
                    et_phone_number.setText(lastNo);
                    et_phone_number.setSelection(et_phone_number.getText().toString().length());
                }
                isChanged = false;
                String inputPhone = et_phone_number.getText().toString();// 获取当前输入框中的手机号码
                if (list_cuss != null && list_cuss.size() != 0)
                    list_cuss.clear();// 每次变化时都清空临时联系人列表
                List<MyCustom> customs =  UtilToolkit.filledData(SkuaidiNewDB.getInstance().selectFuzzySearchCustomer(inputPhone));
                Collections.sort(customs, new PinyinComparator());
                list_cuss = customs;
                list_cuss.addAll(CacheContactsDao.getContact(inputPhone));
                adapter.freshData(list_cuss);
                et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));// 设置输入号码字体为黑色

                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
                    if (!Utility.isEmpty(lastNo) && lastNo.length() >= 11 && lastNo.length() <= 13) {// 手机号在11位到12位之间
                        String firstChar = lastNo.substring(0, 1);// 获取手机号第一个数字
                        String secoundChar = lastNo.substring(0, 2);// 获取手机号前2个数字

                        if ("0".equals(firstChar)) {
                            if ("01".equals(secoundChar) || "02".equals(secoundChar)) {
                                if (lastNo.length() == 11) {
                                    cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                                    if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                        insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                        insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                    }
                                    et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                                } else if (lastNo.length() == 12) {
                                    et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                                    if (position != cloudVoiceMsgDetailEntries.size() - 1) {
                                        position = position + 1;
                                        tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                        if (cloudVoiceMsgDetailEntries.get(position).getMobile() != null
                                                && !cloudVoiceMsgDetailEntries.get(position).getMobile().trim().equals("")) {
                                            et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                        } else {
                                            if (lastNo.length() == 12) {
                                                et_phone_number.setText(lastNo.substring(11));
                                            }
                                        }
                                        et_phone_number.setSelection(et_phone_number.getText().length());// 设置光标
                                        tv_notify_num.setText(String.valueOf(position + 1));// 设置显示第几条
                                    }
                                }
                            } else {
                                if (lastNo.length() == 11 || lastNo.length() == 12) {
                                    cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                                    if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                        insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                        insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                    }
                                    et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                                } else if (lastNo.length() == 13) {
                                    et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                                    if (position != cloudVoiceMsgDetailEntries.size() - 1) {
                                        position = position + 1;
                                        tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                        if (cloudVoiceMsgDetailEntries.get(position).getMobile() != null
                                                && !cloudVoiceMsgDetailEntries.get(position).getMobile().trim().equals("")) {
                                            et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                        } else {
                                            if (lastNo.length() == 13) {
                                                et_phone_number.setText(lastNo.substring(12));
                                            }
                                        }
                                        et_phone_number.setSelection(et_phone_number.getText().length());// 设置光标
                                        tv_notify_num.setText(String.valueOf(position + 1));// 设置显示第几条
                                    }
                                }

                            }
                        } else if ("1".equals(firstChar)) {
                            if (lastNo.length() == 11) {
                                cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                                RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                                if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                    insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                    insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                }
                                et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                            } else {
                                if (position != cloudVoiceMsgDetailEntries.size() - 1) {
                                    position = position + 1;
                                    tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                    if (cloudVoiceMsgDetailEntries.get(position).getMobile() != null
                                            && !cloudVoiceMsgDetailEntries.get(position).getMobile().trim().equals("")) {
                                        et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                    } else {
                                        if (lastNo.length() == 12) {
                                            et_phone_number.setText(lastNo.substring(11));// *************
                                        }
                                    }
                                    et_phone_number.setSelection(et_phone_number.getText().length());// 设置光标
                                    tv_notify_num.setText(String.valueOf(position + 1));// 设置显示第几条
                                }
                            }
                        } else {
                            et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                        }
                    } else {// 手机号为小于11位或大于12位
                        et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                    }
                } else {
                    if (lastNo.contains("1")) {
                        int index = lastNo.indexOf("1", 0);// 找到第一个“1”字所在的下标
                        // 如果手机号的长度等于从第一个“1”往后+12位
                        if (lastNo.length() == index + 12) {
                            if (position != messages.size() - 1) {
                                if (lastNo.substring(11).equals("1")){
                                    position = position + 1;
                                    tv_title_des.setText(String.format(noOfTitle, messages.get(position).getExpressNo()));
                                    if (!Utility.isEmpty(messages.get(position).getSender_mobile())) {
                                        et_phone_number.setText(messages.get(position).getSender_mobile());
                                    } else {
                                        if (lastNo.length() == index + 12) {
                                            et_phone_number.setText(lastNo.substring(index + 11));
                                        }
                                    }
                                    et_phone_number.setSelection(et_phone_number.getText().length());
                                    tv_notify_num.setText(String.valueOf(position + 1));
                                }else{// 如果第12位数字非“1”,则不自动设置下一条手机号
                                    if (isSetText)
                                        return;
                                    isSetText = true;
                                    et_phone_number.setText(lastNo);
                                    et_phone_number.setSelection(et_phone_number.getText().length());
                                    isSetText = false;
                                }
                            }
                        } else if (lastNo.length() == index + 11) {
                            messages.get(position).setSender_mobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                            RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                            if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                            }
                            et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String context_str = et_phone_number.getText().toString();

                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
                    if (!Utility.isEmpty(context_str) && context_str.length() >= 11 && context_str.length() <= 13) {// 手机号在11位到12位之间
                        String firstChar = context_str.substring(0, 1);// 获取手机号第一个数字
                        String secoundChar = context_str.substring(0, 2);// 获取手机号前2个数字
                        if ("0".equals(firstChar)) {
                            if ("01".equals(secoundChar) || "02".equals(secoundChar)) {
                                if (context_str.length() == 11) {
                                    isToNext = true;
                                    String phone = context_str.substring(0, 11);
                                    cloudVoiceMsgDetailEntries.get(position).setMobile(phone);
                                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                                    if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                        insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                        insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                    }
                                } else {
                                    isToNext = false;
                                }
                            } else {
                                if (context_str.length() == 12) {
                                    isToNext = true;
                                    String phone = context_str.substring(0, 12);
                                    cloudVoiceMsgDetailEntries.get(position).setMobile(phone);
                                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                                    if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                        insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                        insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                    }
                                } else {
                                    isToNext = false;
                                }
                            }

                        } else if ("1".equals(firstChar)) {
                            if (context_str.length() == 11) {
                                isToNext = true;
                                String phone = context_str.substring(0, 11);
                                if (position != cloudVoiceMsgDetailEntries.size()) {
                                    cloudVoiceMsgDetailEntries.get(position).setMobile(phone);
                                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                                    if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                        insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                        insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                    }
                                }
                            } else {
                                isToNext = false;
                            }
                        } else {
                            isToNext = false;
                        }
                    } else {
                        isToNext = false;
                    }
                } else {
                    if (position != messages.size()) {

                        if (context_str.contains("1")) {
                            int index = context_str.indexOf("1", 0);
                            if (context_str.length() == index + 11) {
                                isToNext = true;
                                String phone = context_str.substring(0, index + 11);
                                messages.get(position).setSender_mobile(phone.replaceAll(" ", ""));
                                RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                                if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                    insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                    insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                }
                            } else {
                                isToNext = false;
                            }
                        } else {
                            isToNext = false;
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (!et_phone_number.getText().toString().equals("")) {
                    imv_notify_close.setVisibility(View.VISIBLE);
                } else {
                    imv_notify_close.setVisibility(View.GONE);
                }
                if (!isToNext) {
                    return;
                }
                if (is_selector) {
                    String context_str = et_phone_number.getText().toString();

                    if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                        if (!Utility.isEmpty(context_str) && context_str.length() >= 11 && context_str.length() <= 13) {// 手机号在11位到12位之间
                            String firstChar = context_str.substring(0, 1);// 获取手机号第一个数字
                            String secoundChar = context_str.substring(0, 2);// 获取手机号前2个数字

                            if ("0".equals(firstChar)) {
                                if ("01".equals(secoundChar) || "02".equals(secoundChar)) {
                                    if (context_str.length() == 11) {
                                        et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                                        if (position == cloudVoiceMsgDetailEntries.size() - 1) {
                                            et_phone_number.setText("");
                                            position = position + 1;
                                            tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                            et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                            et_phone_number.setSelection(et_phone_number.getText().length());
                                            tv_notify_num.setText(String.valueOf(position + 1));
                                            if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                                insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                                insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                            }
                                        } else {
                                            UtilToolkit.showToast_Custom("手机号码已满");
                                        }
                                    } else {
                                        et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                                    }
                                } else {
                                    if (context_str.length() == 12) {
                                        et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                                        if (position != cloudVoiceMsgDetailEntries.size() - 1) {
                                            et_phone_number.setText("");
                                            position = position + 1;
                                            tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                            et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                            et_phone_number.setSelection(et_phone_number.getText().length());
                                            tv_notify_num.setText(String.valueOf(position + 1));
                                            if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                                insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                                insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                            }
                                        } else {
                                            UtilToolkit.showToast_Custom("手机号码已满");
                                        }
                                    } else {
                                        et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                                    }
                                }
                            } else if ("1".equals(firstChar)) {
                                if (context_str.length() == 11) {
                                    et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                                    if (position != cloudVoiceMsgDetailEntries.size() - 1) {
                                        et_phone_number.setText("");
                                        position = position + 1;
                                        if (position != cloudVoiceMsgDetailEntries.size()) {
                                            tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                            et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                            et_phone_number.setSelection(et_phone_number.getText().length());
                                            tv_notify_num.setText(String.valueOf(position + 1));
                                        }
                                        if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                            insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                            insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                        }
                                    } else {
                                        UtilToolkit.showToast_Custom("手机号码已满");
                                    }
                                } else {
                                    et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                                }
                            } else {
                                et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                            }
                        }
                    } else {
                        if (context_str.contains("1")) {
                            int index = context_str.indexOf("1", 0);
                            if (context_str.length() == index + 11) {
                                et_phone_number.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
                                if (position != messages.size() - 1) {
                                    et_phone_number.setText("");
                                    position = position + 1;
                                    tv_title_des.setText(String.format(noOfTitle, messages.get(position).getExpressNo()));
                                    et_phone_number.setText(messages.get(position).getSender_mobile());
                                    et_phone_number.setSelection(et_phone_number.getText().length());
                                    tv_notify_num.setText(String.valueOf(position + 1));
                                    if (isPhone(et_phone_number.getText().toString().replaceAll(" ", ""))) {
                                        insertCacheContacts(et_phone_number.getText().toString().replaceAll(" ", ""));
                                        insertCacheList(et_phone_number.getText().toString().replaceAll(" ",""));
                                    }
                                } else {
                                    UtilToolkit.showToast_Custom("手机号码已满");
                                }

                            } else {
                                et_phone_number.setTextColor(Utility.getColor(context, color.gray_1));
                            }
                        }
                    }
                    is_selector = false;
                }
            }
        });

        // 列表item点击
        lv_show_phonenumber.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                is_selector = true;
                itemPhone = list_cuss.get(arg2).getPhone();
                et_phone_number.setText(itemPhone);
                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
                    cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                } else {
                    messages.get(position).setSender_mobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                    RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                }
                String content = et_phone_number.getText().toString();
                et_phone_number.setSelection(content.length());
            }
        });
        // 监听listview滚动
        lv_show_phonenumber.setOnScrollListener(new OnScrollListener() {
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
                        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_phone_number.getWindowToken(), 0); // 强制隐藏键盘
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
    }

    /**
     * insertCacheContacts:将手机号码插入到缓存中【如果存在不插入】 作者： 顾冬冬
     *
     * @param phone 手机号
     */
    private void insertCacheContacts(String phone) {
        if (!Utility.isEmpty(phone)) {
            phone = phone.replaceAll(" ", "");
            // 如果不存在手机号码则插入到缓存中
            if (!SkuaidiNewDB.getInstance().existCustomer(phone)) {
                MyCustom myCustom = new MyCustom();
                myCustom.setPhone(phone);
                CacheContactsDao.insertContact(myCustom);
            }
        }
    }

    private void insertCacheList(String phone){
        if (!Utility.isEmpty(phone)){
            phone = phone.replaceAll(" ","");
            if (!SkuaidiNewDB.getInstance().existCustomer(phone) && !CacheContactsDao.checkRepeat(phone)) {
                MyCustom myCustom = new MyCustom();
                myCustom.setPhone(phone);
                list_cuss.add(myCustom);
            }
        }
    }

    /**
     * 判断是否是一个正确的手机号码
     *
     * @param inputPhone 输入的手机号码
     */
    public boolean isPhone(String inputPhone) {
        Pattern r = Pattern.compile(context.getResources().getString(R.string.mobile_pattern));
        Matcher m = r.matcher(inputPhone);
        return m.matches();
    }

    // 设置数据【此方法放于myListener()后面，文本框中文本变以后界面刷新需要按这样顺序执行】
    private void setData() {
        tv_notify_num.setVisibility(View.VISIBLE);
        tv_notify_num.setText(String.valueOf(positionNo));// 设置编号
        tv_title_des.setText(String.format(noOfTitle, order_number));// 设置编号
        et_phone_number.setText(mobilePhoneNum);// 设置手机号
    }

    /**
     * 开始识别
     **/
    private void startDistinguish() {
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

        ll_scan_btn.setVisibility(View.GONE);
        ll_VoiceDistinguishAnim.setVisibility(View.VISIBLE);
        ll_ScreeningContactList.setVisibility(View.GONE);
//        tvTiShi.setText(getResources().getString(R.string.use_wifi));
//        tvPhoneDesc.setText("在线语音识别会消耗流量");
        showControl();
        mRecord_State = RECORD_ING;
        startRecordLightAnimation();// 开始动画效果
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_phone_number.getWindowToken(), 0); // 强制隐藏键盘
    }

    /**
     * 停止识别
     **/
    private void stopDistinguish() {
        ll_scan_btn.setVisibility(View.VISIBLE);
        ll_VoiceDistinguishAnim.setVisibility(View.GONE);
        ll_ScreeningContactList.setVisibility(View.VISIBLE);
        mIat.stopListening();
        mRecord_State = RECORD_ED;
        stopRecordLightAnimation();// 停止动画效果
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);// 如果输入法在窗口上已经显示，则隐藏，反之则显示
    }

    private String TAG = "gudd";

    @OnClick({R.id.tv_voice_distinguish, R.id.tv_scan_phone, R.id.ivCloseAnim, R.id.tvSend, R.id.next, R.id.iv_title_back, R.id.tvMore, R.id.imv_notify_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_voice_distinguish:// 语音输入
                if (!UtilityTime.isToday(context, SkuaidiSpf.getCurDate(context))) {
                    SkuaidiSpf.saveCurDate(context, UtilityTime.getDateTimeByMillisecond2(System.currentTimeMillis(), UtilityTime.YYYY_MM_DD));
                    UMShareManager.onEvent(context, "SendMSG_ClickEveryDay_Num", "SendMSG", "发短信:每日使用语音录入人数【个人当天只统计1次】");
                }
                UMShareManager.onEvent(context, "ScreePhone_voiceSpeech", "NotifySearchPhone", "筛选手机号：开始语音识别");
                startDistinguish();
                break;
            case R.id.tv_scan_phone:// 扫描手机号
                Intent mIntent = new Intent(this, TesseractMobileActivity.class);
                mIntent.putExtra("scanMaxCount", maxCount);
                startActivityForResult(mIntent, START_ACTIVITY_REQUEST_CODE);
                UMShareManager.onEvent(context, "ScreePhone_scanMobile", "NotifySearchPhone", "筛选手机号：扫描识别手机号");
                break;
            case R.id.ivCloseAnim:// 关闭录音动画
                UMShareManager.onEvent(context, "ScreePhone_closeSpeechAnim", "NotifySearchPhone", "筛选手机号：关闭录音动画");
                stopDistinguish();
                speechCount = 0;
                break;
            case R.id.tvSend:// 发送按钮
                UMShareManager.onEvent(context, "ScreePhone_sendMsg", "NotifySearchPhone", "筛选手机号：发送功能");
                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                    try {
                        cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                        RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("cloudMessage", (Serializable) cloudVoiceMsgDetailEntries);
                    setResult(SendYunHuActivity.OUT_CHOOSE_MOBILE_SEND, intent);
                    finish();
                } else {
                    try {
                        messages.get(position).setSender_mobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                        RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                    } catch (Exception e) {
                        //Log.i(TAG, "exception-gudd  in NotifySeacherActivity 点击完成按钮事件中");
                    }
                    Intent intent = new Intent();
                    intent.putExtra("notifyinfos", (Serializable) messages);
                    setResult(SendMSGActivity.RESULT_ADD_PHONENUMBER_SEND, intent);
                    finish();
                }
                finish();
                break;
            case R.id.next:// 下一条按钮
                if (!isNext && !Utility.isEmpty(et_phone_number.getText().toString())) {
                    // 提示
                    NotifySearchPhoneShowTishi searchPhoneShowTishi = new NotifySearchPhoneShowTishi(context, 5, next);
                    searchPhoneShowTishi.show();
                    isNext = true;
                }
                UMShareManager.onEvent(context, "ScreePhone_nextPhone", "NotifySearchPhone", "筛选手机号：下一条");
                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                    if (position != cloudVoiceMsgDetailEntries.size()) {
                        if (position != cloudVoiceMsgDetailEntries.size() - 1) {
                            cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                            RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                            et_phone_number.setText("");
                            position = position + 1;
                            if (position != cloudVoiceMsgDetailEntries.size()) {
                                tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                                et_phone_number.setText(cloudVoiceMsgDetailEntries.get(position).getMobile());
                                et_phone_number.setSelection(et_phone_number.getText().length());
                                tv_notify_num.setText(String.valueOf(position + 1));
                            }
                        } else {
                            UtilToolkit.showToast_Custom("手机号码已满");
                        }
                    } else {
                        UtilToolkit.showToast_Custom("手机号码已满");
                    }
                } else {
                    if (position != messages.size()) {
                        if (position != messages.size() - 1) {
                            messages.get(position).setSender_mobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                            RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                            et_phone_number.setText("");
                            position = position + 1;
                            tv_title_des.setText(String.format(noOfTitle, messages.get(position).getExpressNo()));
                            et_phone_number.setText(messages.get(position).getSender_mobile());
                            et_phone_number.setSelection(et_phone_number.getText().length());
                            tv_notify_num.setText(String.valueOf(position + 1));
                        } else {
                            UtilToolkit.showToast_Custom("手机号码已满");
                        }
                    } else {
                        UtilToolkit.showToast_Custom("手机号码已满");
                    }
                }

                break;
            case R.id.iv_title_back:// 返回按钮点击事件
                if ("签收短信通知".equals(getIntent().getStringExtra("from"))) {
                    int messagePhoneCount = 0;
                    if (null != messages && 0 != messages.size()) {
                        for (int i = 0; i < messages.size(); i++) {
                            if (!Utility.isEmpty(messages.get(i).getSender_mobile())) {
                                messagePhoneCount++;
                            }
                        }
                    }
                    if (initValueMessage != messagePhoneCount) {
                        finishActivity();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.tvMore:// 完成按钮点击事件
                UMShareManager.onEvent(context, "ScreePhone_ok", "NotifySearchPhone", "筛选手机号：完成");
                isOk = true;// 操作是完成操作
                Intent intent = new Intent();
                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                    try {
                        cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                        RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("cloudMessage", (Serializable) cloudVoiceMsgDetailEntries);
                    setResult(SendYunHuActivity.OUT_CHOOSE_MOBILE, intent);
                    finish();
                } else {
                    try {
                        RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                    } catch (Exception e) {
                        //Log.i(TAG, "exception-gudd  in NotifySeacherActivity 点击完成按钮事件中");
                    }
                    intent.putExtra("notifyinfos", (Serializable) messages);
                    setResult(SendMSGActivity.RESULT_ADD_PHONENUMBER, intent);
                    finish();
                }
                finish();
                break;
            case R.id.imv_notify_close:// 清除手机号按钮事件
                UMShareManager.onEvent(context, "ScreePhone_cleanEditPhone", "NotifySearchPhone", "筛选手机号：清除编辑手机号");
                et_phone_number.setText("");
                if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
                    if (position == cloudVoiceMsgDetailEntries.size()) {
                        cloudVoiceMsgDetailEntries.get(position - 1).setMobile("");
                    } else {
                        cloudVoiceMsgDetailEntries.get(position).setMobile("");
                    }
                    RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                } else {
                    if (position == messages.size()) {
                        messages.get(position - 1).setSender_mobile("");
                    } else {
                        messages.get(position).setSender_mobile("");
                    }
                    RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                }

                break;
        }
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
//			SendMSGActivity sendMSGActivity = new SendMSGActivity();
            switch (error.getErrorCode()) {
                case 20001:
                    UtilToolkit.showToast("网络连接错误，请稍候再试");
                    stopDistinguish();
                    break;
                case 10118:
                    break;
                case 20006:
                    stopDistinguish();
                    break;
                default:
                    break;
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
            printResult(results);
        }

        @Override
        public void onVolumeChanged(final int volume, byte[] data) {
            // showTip("当前正在说话，音量大小：" + volume);
            // //Log.i("GUDDD", volume+"");
            // //Log.d(TAG, "返回音频数据："+data.length);

            animHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateDisplay(ivSpeech, volume);// 录音时修改动画
                }
            }, 50);

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
//            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //Log.d(TAG, "session id =" + sid);
//            }
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
            // //Log.i("GUDDD", mIatResults.get(key));
            i++;
            if (i == size) {
                word = mIatResults.get(key);
            }
            /*
             * //Log.i("GUDDD", mIatResults.get(key));
			 * resultBuffer.append(mIatResults.get(key));
			 */
        }
        // TODO 此处处理返回过来的文字
        // String word = resultBuffer.toString();

        if (judgeWordIsZeroToNine(word)) {
            //Log.i("logi", word);
            if (word.length() < 11) {
                if (!Utility.isEmpty(sentence)) {
                    stopCountDown();
                    sentence = sentence + word;
                    //Log.i("logi", "2:     " + sentence);
                    startCountDown();
                } else {
                    sentence = word;
                    //Log.i("logi", "1:     " + sentence);
                    startCountDown();
                }
                if (sentence.length() == 11) {
                    word = sentence;
                    //Log.i("logi", "3:     " + sentence + "    word:   " + word);
                    sentence = "";
                    stopCountDown();// 停止倒计时线程
                    shibieSuccess();
                } else if (sentence.length() > 11) {
                    stopCountDown();// 停止倒计时线程
                    showControl();
                    tvTiShi.setText("错误的手机号");
                    tvPhoneDesc.setText(sentence);
                    sentence = "";
                }
            } else if (word.length() > 12) {
                showControl();
                sentence = "";
                stopCountDown();// 停止倒计时线程
                tvTiShi.setText("错误的手机号");
                tvPhoneDesc.setText(word);
            } else {
                shibieSuccess();// 11位手机号码识别成功
            }
        } else {
            showControl();
            tvTiShi.setText("抱歉，没听清");
            tvPhoneDesc.setText("请说话大声些或换一个安静的环境重新试试");
        }
    }

    // 设置云呼列表的数据更新和草稿箱的保存功能
    private void setEditContent() {
        if (!Utility.isEmpty(et_phone_number.getText().toString())) {
            if (position == cloudVoiceMsgDetailEntries.size()) {
                UtilToolkit.showToast_Custom("手机号码已满");
                return;
            }
            position = position + 1;
            if (position != cloudVoiceMsgDetailEntries.size()) {
                positionNo++;
                tv_notify_num.setText(String.valueOf(positionNo));// 设置序号-非编号
                tv_title_des.setText(String.format(noOfTitle, cloudVoiceMsgDetailEntries.get(position).getMobile_no()));
                et_phone_number.setText(word);
                et_phone_number.setSelection(et_phone_number.getText().length());
                cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                UtilToolkit.showToast_Custom(word);
                hideControl();
                Utility.playMusicDing();
            } else {
                UtilToolkit.showToast_Custom("手机号码已满");
            }
        } else {
            et_phone_number.setText(word);
            et_phone_number.setSelection(et_phone_number.getText().length());
            if (position == cloudVoiceMsgDetailEntries.size()) {
                position = position - 1;
            }
            cloudVoiceMsgDetailEntries.get(position).setMobile(et_phone_number.getText().toString().replaceAll(" ", ""));
            RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
            UtilToolkit.showToast_Custom(word);

            hideControl();
            Utility.playMusicDing();
        }
    }

    private void shibieSuccess() {
        UMShareManager.onEvent(context, "ScreePhone_speechCount", "NotifySearchPhone", "筛选手机号：语音识别成功");
        if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
            if (!Utility.isEmpty(word) && word.length() >= 11 && word.length() <= 13) {// 手机号在11位到12位之间
                String firstChar = word.substring(0, 1);// 获取手机号第一个数字
                String secoundChar = word.substring(0, 2);// 获取手机号前2个数字
                if ("0".equals(firstChar)) {
                    if ("01".equals(secoundChar) || "02".equals(secoundChar)) {
                        if (word.length() == 11) {
                            setEditContent();
                        } else {
                            showControl();
                            tvTiShi.setText("错误的固话号码");
                            tvPhoneDesc.setText(word);
                        }
                    } else {
                        if (word.length() == 11 || word.length() == 12) {
                            setEditContent();
                        } else {
                            showControl();
                            tvTiShi.setText("错误的固话号码");
                            tvPhoneDesc.setText(word);
                        }
                    }
                } else if ("1".equals(firstChar)) {
                    if (word.length() == 11) {
                        setEditContent();
                    } else {
                        showControl();
                        tvTiShi.setText("错误的手机号");
                        tvPhoneDesc.setText(word);
                    }
                } else {
                    showControl();
                    tvTiShi.setText("错误的手机号");
                    tvPhoneDesc.setText(word);
                }
            }
        } else {// 发短信界手机号码-必须要11位长度
            if (word.length() == 11) {
                Pattern p = Pattern.compile("[1]\\d{10}");
                Matcher m = p.matcher(word);
                // 当找到了正确的手机号码
                while (m.find()) {
                    if (!Utility.isEmpty(et_phone_number.getText().toString())) {
                        if (position == messages.size()) {
                            UtilToolkit.showToast_Custom("手机号码已满");
                        } else {
                            position = position + 1;
                            if (position != messages.size()) {
                                positionNo++;
                                tv_notify_num.setText(String.valueOf(positionNo));// 设置序号-非编号
                                tv_title_des.setText(String.format(noOfTitle, messages.get(position).getExpressNo()));
                                et_phone_number.setText(m.group());
                                et_phone_number.setSelection(et_phone_number.getText().length());
                                messages.get(position).setSender_mobile(m.group().replaceAll(" ", ""));
                                RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                                UtilToolkit.showToast_Custom(m.group());
                                hideControl();
                                Utility.playMusicDing();
                            } else {
                                UtilToolkit.showToast_Custom("手机号码已满");
                            }
                        }
                    } else {
                        et_phone_number.setText(m.group());
                        et_phone_number.setSelection(et_phone_number.getText().length());
                        if (position == messages.size()) {
                            position = position - 1;
                        }
                        messages.get(position).setSender_mobile(et_phone_number.getText().toString().replaceAll(" ", ""));
                        RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                        UtilToolkit.showToast_Custom(m.group());
                        hideControl();
                        Utility.playMusicDing();
                    }
                }
            } else {
                showControl();
                tvTiShi.setText("错误的手机号");
                tvPhoneDesc.setText(word);
            }
        }
    }

    /**
     * @Title: countDown
     * @Description: 倒计时
     * @param countDown
     *            倒计时时间长度
     * @param InterruptedException
     * @return void
     */
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

    private void hideControl() {

        tvTiShi.setVisibility(View.GONE);
        tvPhoneDesc.setVisibility(View.GONE);
    }

    private void showControl() {
        tvTiShi.setVisibility(View.VISIBLE);
        tvPhoneDesc.setVisibility(View.VISIBLE);
    }

    /**
     * 判断文字是否全是数字
     **/
    public static boolean judgeWordIsZeroToNine(String word) {
        if (!Utility.isEmpty(word)) {
            char[] c = word.toCharArray();
            int t = 1;
            for (char cc : c) {
                if (cc >= '0' && cc <= '9') {
                    t = 1;
                } else {
                    t = 0;
                    break;
                }
            }
            return t == 1;
        } else {
            return false;
        }
    }

    /**
     * 参数设置
     **/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data.getStringExtra("phone") != null) {
                        String phone = data.getStringExtra("phone");
                        et_phone_number.setText(phone);
                    } else {
                        et_phone_number.setText("");
                    }
                }
                break;
            case START_ACTIVITY_REQUEST_CODE:
                Intent intent = new Intent();
                if (resultCode == RESULT_OK) {
                    List<NotifyInfo2> list = (List<NotifyInfo2>) data.getSerializableExtra("mobile_list");
                    for (int i = 0; i < list.size(); i++) {
                        if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                            if (position < cloudVoiceMsgDetailEntries.size()) {
                                cloudVoiceMsgDetailEntries.get(position).setMobile(list.get(i).getSender_mobile());
                                RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
                            }
                        } else {
                            if (position < messages.size()) {
                                messages.get(position).setSender_mobile(list.get(i).getSender_mobile());
                                RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, true));
                            }
                        }
                        position++;
                    }
                    if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                        intent.putExtra("cloudMessage", (Serializable) cloudVoiceMsgDetailEntries);
                        setResult(SendYunHuActivity.OUT_CHOOSE_MOBILE, intent);
                    } else {
                        intent.putExtra("notifyinfos", (Serializable) messages);
                        setResult(SendMSGActivity.RESULT_ADD_PHONENUMBER, intent);
                    }
                    finish();
                }
                break;
        }


    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
        // 光标放到文本框 最后面
        if (!Utility.isEmpty(et_phone_number.getText())) {
            et_phone_number.setSelection(et_phone_number.getText().length());
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(this);
        super.onPause();
        MobclickAgent.onPause(this);
        if (isOk)// 如果点击的是完成按钮 ，则不执行以下操作
            return;

        if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
            if (null == SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("voice")) {
                SaveUnnormalExitDraftInfo info = new SaveUnnormalExitDraftInfo();
                info.setDraft_id(draft_id);
                info.setDraft_no(DraftBoxUtility.pinjieNumberCloud(cloudVoiceMsgDetailEntries));
                info.setDraft_phoneNumber(DraftBoxUtility.pinjiePhoneNumberCloud(cloudVoiceMsgDetailEntries));
                info.setDraft_orderNumber(DraftBoxUtility.pinjieOrderNumberCloud(cloudVoiceMsgDetailEntries));
                info.setDraft_position(position);
                info.setDraft_positionNo(positionNo);
                info.setFrom_data("voice");
                SaveUnnormalExitDraftInfoDAO.insertUnnormarlExitDraftInfo(info);
            }
        } else {
            if (null == SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("sms")) {
                // 将界面数据保存到一个临时的数据库中-防止程序crash的时候数据消失
                SaveUnnormalExitDraftInfo saveDraftInfo = new SaveUnnormalExitDraftInfo();
                saveDraftInfo.setDraft_id(draft_id);
                saveDraftInfo.setDraft_no(DraftBoxUtility.pinjieNumber(messages));
                saveDraftInfo.setDraft_orderNumber(DraftBoxUtility.pinjieOrderNumber(messages));
                saveDraftInfo.setDraft_phoneNumber(DraftBoxUtility.pinjiePhoneNumber(messages));
                saveDraftInfo.setDraft_position(position);
                saveDraftInfo.setDraft_positionNo(positionNo);
                saveDraftInfo.setFrom_data("sms");
                SaveUnnormalExitDraftInfoDAO.insertUnnormarlExitDraftInfo(saveDraftInfo);
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (mIat != null) {
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }
        activityIsDestroy = true;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mBundle = new Bundle();
        if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {// 从云呼界面进来的
            RecordDraftBoxCloudVoiceDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfoCloud(cloudVoiceMsgDetailEntries));
            if (null == SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("voice")) {
                SaveUnnormalExitDraftInfo info = new SaveUnnormalExitDraftInfo();
                info.setDraft_id(draft_id);
                info.setDraft_no(DraftBoxUtility.pinjieNumberCloud(cloudVoiceMsgDetailEntries));
                info.setDraft_phoneNumber(DraftBoxUtility.pinjiePhoneNumberCloud(cloudVoiceMsgDetailEntries));
                info.setDraft_orderNumber(DraftBoxUtility.pinjieOrderNumberCloud(cloudVoiceMsgDetailEntries));
                info.setDraft_position(position);
                info.setDraft_positionNo(positionNo);
                info.setFrom_data("voice");
                SaveUnnormalExitDraftInfoDAO.insertUnnormarlExitDraftInfo(info);
            }
            // 将界面数据保存到bundle中-在页面关闭重新oncreate的时候用来还原
            mBundle.putString("draft_id", draft_id);
            mBundle.putInt("position", position);
            mBundle.putInt("positionNo", positionNo);
            mBundle.putSerializable("cloudVoiceMsgDetailEntries", (Serializable) cloudVoiceMsgDetailEntries);
        } else {
            // 将界面数据保存到草稿箱
            RecordDraftBoxDAO.updateDraftPhoneNumber(setDraftBoxPhoneNumberInfo(messages, false));
            // 将界面数据保存到一个临时的数据库中-防止程序crash的时候数据消失
            if (null == SaveUnnormalExitDraftInfoDAO.getUnNormalExitDraftInfo("sms")) {
                SaveUnnormalExitDraftInfo saveDraftInfo = new SaveUnnormalExitDraftInfo();
                saveDraftInfo.setDraft_id(draft_id);
                saveDraftInfo.setDraft_no(DraftBoxUtility.pinjieNumber(messages));
                saveDraftInfo.setDraft_orderNumber(DraftBoxUtility.pinjieOrderNumber(messages));
                saveDraftInfo.setDraft_phoneNumber(DraftBoxUtility.pinjiePhoneNumber(messages));
                saveDraftInfo.setDraft_position(position);
                saveDraftInfo.setDraft_positionNo(positionNo);
                saveDraftInfo.setFrom_data("sms");
                SaveUnnormalExitDraftInfoDAO.insertUnnormarlExitDraftInfo(saveDraftInfo);
            }
            // 将界面数据保存到bundle中-在页面关闭重新oncreate的时候用来还原
            mBundle.putString("draft_id", draft_id);
            mBundle.putInt("position", position);
            mBundle.putInt("positionNo", positionNo);
            mBundle.putSerializable("messages", (Serializable) messages);
        }
        outState.putAll(mBundle);
    }

    private static final int RECORD_ING = 1; // 正在录音
    private static final int RECORD_ED = 2; // 完成录音
    private int mRecord_State = 0; // 录音的状态
    /**
     * 用来控制动画效果
     */
    Handler mRecordLightHandler = new Handler() {

        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_1.setVisibility(View.VISIBLE);
                        mRecordLight_1_Animation = AnimationUtils.loadAnimation(context, R.anim.voice_anim);
                        mRecordLight_1.setAnimation(mRecordLight_1_Animation);
                        mRecordLight_1_Animation.startNow();
                    }
                    break;

                case 1:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_2.setVisibility(View.VISIBLE);
                        mRecordLight_2_Animation = AnimationUtils.loadAnimation(context, R.anim.voice_anim);
                        mRecordLight_2.setAnimation(mRecordLight_2_Animation);
                        mRecordLight_2_Animation.startNow();
                    }
                    break;
                case 2:
                    if (mRecord_State == RECORD_ING) {
                        mRecordLight_3.setVisibility(View.VISIBLE);
                        mRecordLight_3_Animation = AnimationUtils.loadAnimation(context, R.anim.voice_anim);
                        mRecordLight_3.setAnimation(mRecordLight_3_Animation);
                        mRecordLight_3_Animation.startNow();
                    }
                    break;
                case 3:
                    if (mRecordLight_1_Animation != null) {
                        mRecordLight_1.clearAnimation();
                        mRecordLight_1_Animation.cancel();
                        mRecordLight_1.setVisibility(View.GONE);

                    }
                    if (mRecordLight_2_Animation != null) {
                        mRecordLight_2.clearAnimation();
                        mRecordLight_2_Animation.cancel();
                        mRecordLight_2.setVisibility(View.GONE);
                    }
                    if (mRecordLight_3_Animation != null) {
                        mRecordLight_3.clearAnimation();
                        mRecordLight_3_Animation.cancel();
                        mRecordLight_3.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };

    Handler animHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private void updateDisplay(ImageView view, int signalEMA) {

        switch (signalEMA) {
            case 0:
            case 1:
            case 2:
            case 3:
                view.setImageResource(R.drawable.record_status_bg1);
                break;
            case 4:
            case 5:
                view.setImageResource(R.drawable.record_status_bg2);
                break;
            case 6:
            case 7:
                view.setImageResource(R.drawable.record_status_bg3);
                break;
            case 8:
            case 9:
                view.setImageResource(R.drawable.record_status_bg4);
                break;
            case 10:
            case 11:
                view.setImageResource(R.drawable.record_status_bg5);
                break;
            case 12:
            case 13:
                view.setImageResource(R.drawable.record_status_bg6);
                break;
            case 14:
            case 15:
                view.setImageResource(R.drawable.record_status_bg7);
                break;
            case 16:
            case 17:
                view.setImageResource(R.drawable.record_status_bg8);
                break;
            case 18:
            case 19:
                view.setImageResource(R.drawable.record_status_bg9);
                break;
            case 20:
            case 21:
                view.setImageResource(R.drawable.record_status_bg11);
                break;
            case 22:
            case 23:
                view.setImageResource(R.drawable.record_status_bg12);
                break;
            case 24:
            case 25:
                view.setImageResource(R.drawable.record_status_bg13);
                break;
            case 26:
            case 27:
                view.setImageResource(R.drawable.record_status_bg14);
                break;
            case 28:
            case 29:
                view.setImageResource(R.drawable.record_status_bg15);
                break;
            default:
                view.setImageResource(R.drawable.record_status_bg15);
                break;
        }
    }

    /**
     * 开始动画效果
     */
    private void startRecordLightAnimation() {
        mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
        mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
        mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
    }

    /**
     * 停止动画效果
     */
    private void stopRecordLightAnimation() {
        mRecordLightHandler.sendEmptyMessage(3);
    }

    private void finishActivity() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        } else {
            dialog = new SkuaidiDialog(context);
            dialog.setTitle("警告");
            dialog.setContent("退出后输入的数据将清空\n您确认要退出吗？");
            dialog.isUseEditText(false);
            dialog.setPositionButtonTitle("退出");
            dialog.setNegativeButtonTitle("取消");
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dialog.showDialog();
        }

    }

    /**
     * (短信记录)设置数据-用于更新草稿箱中条目的手机号码
     *
     * @param isNormalExitStatus 是否是正常保存
     */
    public DraftBoxSmsInfo setDraftBoxPhoneNumberInfo(List<NotifyInfo2> messages, boolean isNormalExitStatus) {
        DraftBoxSmsInfo info = new DraftBoxSmsInfo();
        info.setDraftSaveTime(System.currentTimeMillis());
        info.setId(draft_id + "");
        info.setNormal_exit_status(isNormalExitStatus);
        info.setPhoneNumber(DraftBoxUtility.pinjiePhoneNumber(messages));
        return info;
    }

    /**
     * (云呼记录)设置数据-用于更新草稿箱中条目的手机号码
     **/
    private DraftBoxCloudVoiceInfo setDraftBoxPhoneNumberInfoCloud(List<CloudVoiceMsgDetailEntry> cvme) {
        DraftBoxCloudVoiceInfo info = new DraftBoxCloudVoiceInfo();
        info.setSaveTime(System.currentTimeMillis());
        info.setId(draft_id + "");
        info.setPhoneNumber(DraftBoxUtility.pinjiePhoneNumberCloud(cvme));
        return info;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int cloudPhoneCount = 0;
            int messagePhoneCount = 0;
            if (!Utility.isEmpty(fromActivity) && "CloudVoice".equals(fromActivity)) {
                if (null != cloudVoiceMsgDetailEntries && 0 != cloudVoiceMsgDetailEntries.size()) {
                    for (int i = 0; i < cloudVoiceMsgDetailEntries.size(); i++) {
                        if (!Utility.isEmpty(cloudVoiceMsgDetailEntries.get(i).getMobile())) {
                            cloudPhoneCount++;
                        }
                    }
                }
                if (initValueCloud != cloudPhoneCount) {
                    finishActivity();
                    return true;
                }
            } else {
                if (null != messages && 0 != messages.size()) {
                    for (int i = 0; i < messages.size(); i++) {
                        if (!Utility.isEmpty(messages.get(i).getSender_mobile())) {
                            messagePhoneCount++;
                        }
                    }
                }
                if (initValueMessage != messagePhoneCount) {
                    finishActivity();
                    return true;
                }
            }
            activityIsDestroy = true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
