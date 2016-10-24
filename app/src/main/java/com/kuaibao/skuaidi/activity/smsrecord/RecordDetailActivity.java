package com.kuaibao.skuaidi.activity.smsrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.adapter.RecordDetailAdapter;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.entry.RecordDetail;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.json.entry.SendCloudVoiceParameter;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.service.DownloadTask;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 顾冬冬
 */
public class RecordDetailActivity extends SkuaiDiBaseActivity implements OnClickListener {

    public static final String INFORM_USER_GET_TOPIC_DETAIL = "inform_user/get_topic_detail";
    private static final int REQUEST_CHOOSEMODEL = 0X1001;
    private static final int GET_TOPIC_DETAIL_SUCCESS = 0X1002;// 获取对话成功
    private static final int SEND_CONTENT_SUCCESS = 0X1003;// 发送消息成功
    public static final int RESULT_COME_FROM_VOICEMODELACTIVITY = 0x1004;// 从语音模板界面返回成功
    private static final int CLOUD_VOICE_SEND_SUCCESS = 0X1005;// 云呼语音发送成功

    private Context mContext = null;
    private RecordDetailAdapter adapter = null;
    private Message mesg = null;
    private InputMethodManager imm = null;
    private SkuaidiDB skuaidiDB;
    private List<RecordDetail> recordDetails = new ArrayList<>();

    private TextView tv_title_des = null;// 标题
    private ListView lvRecordList = null;// 列表
    private EditText etInputText = null;// 输入内容框
    private Button btnSend = null;// 发送按钮
    private ImageView ivSelectModel;// 选择语音模板按钮
    private PullToRefreshView pull = null;// 刷新listview

    // ****************参数变量*******************
    private String fromActivity = "";// 来自于哪个界面
    // private String fromActivity = "";// 数据来源界面
    private String topicId = "";// 留言ID
    private String source = "";// 参数返回结果-inform_user-派件通知， ivr-云呼通知
    private String phoneNumber = "";// 手机号
    private String orderNo = "";// 运单编号
    private String expressNumber = "";// 运单号
    private int page_num = 1;
    // 获取聊天数据
    private String state = "";// 状态
    private int total_page = 1;// 总页数
    private String brand = "";// 品牌
    private String cm_name = "";// 业务员姓名
    private String dh = "";// 单号
    private String userPhone = "";// 用户手机号
    private String signedTime = "";// 签收时间
    // 发送消息成功
    private String message_id = "";// 具体会话ID
    private String topic_id = "";// 主题ID
    // 语音模板
    private String voice_ivid = "";
    private int voice_length = 0;
    private String voice_title = "";// 语音标题
    private String voice_path_local = "";
    private String voice_path_service = "";

    private View lineView;
    private RelativeLayout rl_Bottom;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            RecordDetail recordDetail;
            switch (msg.what) {
                case CLOUD_VOICE_SEND_SUCCESS:
                    recordDetail = new RecordDetail();
                    recordDetail.setSpeaker_role("counterman");
                    recordDetail.setSpeaker_id(SkuaidiSpf.getLoginUser().getUserId());
                    recordDetail.setSpeaker_phone(SkuaidiSpf.getLoginUser().getPhoneNumber());
                    recordDetail.setContent_type(Constants.TYPE_CLOUD_VOICE);
                    recordDetail.setContent_title(voice_title);
                    recordDetail.setContent_path(voice_path_service);
                    recordDetail.setVoice_length(voice_length);
                    recordDetail.setSpeak_time(System.currentTimeMillis());
                    recordDetails.add(recordDetail);
                    adapter.setRecordDetail(recordDetails);
                    lvRecordList.setSelection(adapter.getCount());
                    break;
                case SEND_CONTENT_SUCCESS:
                    recordDetail = new RecordDetail();
                    recordDetail.setMessage_id(message_id);
                    recordDetail.setSpeaker_role("counterman");
                    recordDetail.setSpeaker_id(SkuaidiSpf.getLoginUser().getUserId());
                    recordDetail.setSpeaker_phone(SkuaidiSpf.getLoginUser().getPhoneNumber());
                    recordDetail.setContent_type(Constants.TYPE_TXT);
                    recordDetail.setContent(etInputText.getText().toString());
                    recordDetail.setVoice_length(0);
                    recordDetail.setSpeak_time(System.currentTimeMillis());
                    recordDetails.add(recordDetail);
                    adapter.setRecordDetail(recordDetails);
                    lvRecordList.setSelection(adapter.getCount());
                    etInputText.setText("");

                    break;
                case GET_TOPIC_DETAIL_SUCCESS:
                    pull.onHeaderRefreshComplete();
                    pull.onFooterRefreshComplete();
                    recordDetails = (List<RecordDetail>) msg.obj;

                    if (!Utility.isEmpty(getIntent().getStringExtra("topic_id"))) {// 这句是用来判断是否是从推送界面而来调用调用接口
                        adapter.setData(cm_name, dh, userPhone,signedTime);
                    } else {
                        adapter.setData(cm_name, "", "",signedTime);
                    }
                    tv_title_des.setText(!Utility.isEmpty(orderNo) ? "编号：" + orderNo : "编号有误");

                    if (page_num == 1) {
                        adapter.setRecordDetail(recordDetails);
                        if (recordDetails.size() != 0) {
                            lvRecordList.setSelection(recordDetails.size() - 1);
                        }
                    } else {
                        List<RecordDetail> recordDetailsBackUp = adapter.getRecordDetail();
                        recordDetailsBackUp.addAll(0, recordDetails);
                        adapter.setRecordDetail(recordDetailsBackUp);
                        lvRecordList.setSelection(recordDetails.size() - 1);
                    }
                    adapter.setExpressNumber(expressNumber,phoneNumber);
                    lvRecordList.setSelection(adapter.getCount());

                    if (!Utility.isEmpty(userPhone))
                        ivSelectModel.setVisibility(userPhone.contains("*") ? View.GONE : View.VISIBLE);
                    else
                        ivSelectModel.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.record_detail_activity);
        mContext = this;
        initView();
        getData();
        setAdapter();
        setListener();
        if ("oldRecordListActivity".equals(fromActivity)) {
            getMessageDetail(page_num);
        } else {
            getTopicDetail(page_num);
        }
        skuaidiDB = SkuaidiDB.getInstanse(mContext);
        setBottomVisible();
    }

    private void setBottomVisible() {
        if ("oldRecordListActivity".equals(fromActivity)) {
            lineView.setVisibility(View.GONE);
            rl_Bottom.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSEMODEL && resultCode == RESULT_COME_FROM_VOICEMODELACTIVITY) {// 如果从语音模板界面返回过来

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
            boolean isExist = false;
            // 判断是否存在已经被选择的模板
            if (null != cRecord) {
                isExist = true;
                // 获取模板参数
                voice_ivid = cRecord.getIvid();
                voice_length = cRecord.getVoiceLength();
                voice_title = cRecord.getTitle();
                voice_path_local = cRecord.getPathLocal();
                voice_path_service = cRecord.getPathService();
            }
            if (isExist) {
                List<SendCloudVoiceParameter> scvps = new ArrayList<>();
                SendCloudVoiceParameter scvp = new SendCloudVoiceParameter();
                scvp.setNo(orderNo);
                scvp.setPhone(phoneNumber);
                scvps.add(scvp);

                String call_data_json = new Gson().toJson(scvps).toString();
                call(voice_ivid, call_data_json, topicId);
            } else {
                UtilToolkit.showToast("请重新选择模板");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        SkuaidiImageView iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        lvRecordList = (ListView) findViewById(R.id.lvRecordList);
        etInputText = (EditText) findViewById(R.id.etInputText);
        btnSend = (Button) findViewById(R.id.btnSend);
        ivSelectModel = (ImageView) findViewById(R.id.ivSelectModel);

        lineView = findViewById(R.id.bottom_line);
        rl_Bottom = (RelativeLayout) findViewById(R.id.rl_bottom_parent);

        iv_title_back.setOnClickListener(this);
        ivSelectModel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        etInputText.setFocusable(true);
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (!Utility.isEmpty(getIntent().getStringExtra("topic_id"))) {
            topicId = getIntent().getStringExtra("topic_id");
        } else {
            fromActivity = getIntent().getStringExtra("fromActivity");
            if ("smsRecordActivity".equals(fromActivity) || "smsRecordSearchActivity".equals(fromActivity) || "oldRecordListActivity".equals(fromActivity)) {

                SmsRecord smsRecord = (SmsRecord) getIntent().getSerializableExtra("smsRecord");
                phoneNumber = smsRecord.getUser_phone();// 手机号
                orderNo = smsRecord.getExpress_number();// 编号
                expressNumber = smsRecord.getDh();// 单号
                topicId = smsRecord.getTopic_id();// 留言id
                cm_name = smsRecord.getCm_name();
            } else if ("cloudVoiceRecordActivity".equals(fromActivity)) {

                CloudVoiceRecordEntry crRecordEntry = (CloudVoiceRecordEntry) getIntent().getSerializableExtra("cloudEntry");
                phoneNumber = crRecordEntry.getCall_number();// 手机号
                orderNo = crRecordEntry.getBh();// 编号
                topicId = crRecordEntry.getTopic_id();// 主键
            }
            tv_title_des.setText(TextUtils.isEmpty(orderNo) ? "编号有误" : "编号：" + orderNo);
            if ("smsRecordActivity".equals(fromActivity) || phoneNumber.contains("*")){
                ivSelectModel.setVisibility(!Utility.isEmpty(phoneNumber) ? View.VISIBLE : View.GONE);// 显示|隐藏选择语音模板按钮
            }else{
                rl_Bottom.setVisibility(!Utility.isEmpty(phoneNumber) ? View.VISIBLE : View.GONE);// 显示|隐藏发送文本区域
            }
        }
    }

    /**
     * 设置监听
     */
    private void setListener() {
        // 下拉
        pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                if (Utility.isNetworkConnected()) {
                    page_num = 1;
                    //if (page_num <= total_page) {
                    if (fromActivity.equals("oldRecordListActivity")) {
                        getMessageDetail(page_num);
                    } else {
                        getTopicDetail(page_num);
                    }
                } else {
                    pull.onHeaderRefreshComplete();
                    pull.onFooterRefreshComplete();
                    UtilToolkit.showToast("无网络连接");
                }
            }
        });
        // 上拉
        pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

            @Override
            public void onFooterRefresh(PullToRefreshView view) {

                if (Utility.isNetworkConnected()) {
                    page_num = page_num + 1;
                    if (page_num <= total_page) {
                        if (fromActivity.equals("oldRecordListActivity")) {
                            getMessageDetail(page_num);
                        } else {
                            getTopicDetail(page_num);
                        }
                    } else {
                        pull.onHeaderRefreshComplete();
                        pull.onFooterRefreshComplete();
                    }
                } else {
                    pull.onHeaderRefreshComplete();
                    pull.onFooterRefreshComplete();
                    UtilToolkit.showToast("无网络连接");
                }
            }
        });

        // 监听listview滚动
        lvRecordList.setOnScrollListener(new OnScrollListener() {
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
                        imm.hideSoftInputFromWindow(etInputText.getWindowToken(), 0); // 强制隐藏键盘
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

    private void setAdapter() {
        adapter = new RecordDetailAdapter(mContext, phoneNumber, expressNumber,signedTime, recordDetails, new RecordDetailAdapter.OnButtonClickListener() {

            @Override
            public void flowExpressNo(String expressNo) {// 运单跟踪
                DownloadTask.stopPlayLocalVoice();
                Intent intent = new Intent();
                intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
                intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
                intent.putExtra("order_number", expressNo);
                intent.setClass(mContext, CopyOfFindExpressResultActivity.class);
                startActivity(intent);
            }

            @Override
            public void call(String mobilePhone) {// 　拨打电话
                DownloadTask.stopPlayLocalVoice();
                AcitivityTransUtil.showChooseTeleTypeDialog(RecordDetailActivity.this, "", mobilePhone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
            }
        });
        lvRecordList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Intent mIntent;
        switch (v.getId()) {
            case R.id.iv_title_back:// 返回按钮
                DownloadTask.stopPlayLocalVoice();
                if (!Utility.isEmpty(fromActivity) && fromActivity.equals("smsRecordActivity")) {
                    setResult(SmsRecordActivity.RESULT_GETINTO_RECORD_DETAIL_ACTIVITY);
                    finish();
                } else if (!Utility.isEmpty(fromActivity) && fromActivity.equals("smsRecordSearchActivity")) {
                    setResult(SmsRecordActivity.RESULT_GETINTO_RECORD_DETAIL_ACTIVITY);
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.ivSelectModel:// 选择模板按钮
                DownloadTask.stopPlayLocalVoice();
                mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                mIntent.putExtra("fromActivityType", "smsRecordDetailActivity");
                startActivityForResult(mIntent, REQUEST_CHOOSEMODEL);
                break;
            case R.id.btnSend:// 发送按钮
                DownloadTask.stopPlayLocalVoice();
                String sendContent = etInputText.getText().toString();

                if (Utility.isNetworkConnected()) {

                    if (!sendContent.trim().equals("")) {
                        createTalk(topicId, Constants.TYPE_TXT, sendContent, "");
                        btnSend.setEnabled(false);
                    } else {
                        UtilToolkit.showToast("请输入内容");
                    }
                } else {
                    UtilToolkit.showToast("网络连接错误");
                }
                break;
            default:
                break;
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
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DownloadTask.stopPlayLocalVoice();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取列表详情
     */
    private void getTopicDetail(int page_num) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname",  INFORM_USER_GET_TOPIC_DETAIL);
            data.put("topic_id", topicId);
            data.put("order_by", "desc");// 调用倒序
            data.put("role", "courier");
            data.put("page_num", page_num);
            data.put("page_size", Constants.PAGE_SIZE);
//			data.put("page_size", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }


    private void getMessageDetail(int page_num) {
        final ApiWrapper apiWrapper = new ApiWrapper();
        Subscription mSubscription = apiWrapper.getSMSDetail(topicId, page_num).subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject recordSMSDetail) {
                        total_page = recordSMSDetail.getIntValue("page_count");// 总页数
                        List<RecordDetail> recordDetails = new ArrayList<>();
                        com.alibaba.fastjson.JSONArray array = recordSMSDetail.getJSONArray("list");
                        if (null != array) {
                            for (int i = 0; i < array.size(); i++) {
                                com.alibaba.fastjson.JSONObject itemData = (com.alibaba.fastjson.JSONObject) array.get(i);
                                RecordDetail recordDetail = new RecordDetail();
                                recordDetail.setMessage_id(itemData.getString("message_id"));
                                recordDetail.setSpeaker_role(itemData.getString("speaker_role"));
                                recordDetail.setSpeaker_id(itemData.getString("speaker_id"));
                                recordDetail.setSpeaker_phone(itemData.getString("speaker_phone"));
                                recordDetail.setContent_type(itemData.getIntValue("content_type"));
                                String content_type = "" + itemData.getIntValue("content_type");
                                if (!Utility.isEmpty(content_type) && content_type.equals("1")) {// 纯文本
                                    recordDetail.setContent(itemData.getString("content"));
                                } else if (!Utility.isEmpty(content_type) && content_type.equals("3")) {// 客户回复语音需要填写插入地址
                                    recordDetail.setContent(itemData.getString("content"));
                                } else if (!Utility.isEmpty(content_type) && content_type.equals("6")) {// 快递员发送云呼语音
                                    recordDetail.setIvr_status(itemData.getString("ivr_status"));
                                    recordDetail.setIvr_status_msg(itemData.getString("ivr_status_msg"));
                                    recordDetail.setIvr_user_input(itemData.getString("ivr_user_input"));
                                    com.alibaba.fastjson.JSONObject content = itemData.getJSONObject("content");
                                    recordDetail.setContent_title(content.getString("title"));
                                    recordDetail.setContent_path(content.getString("path"));
                                } else if (!Utility.isEmpty(content_type) && content_type.equals("7")) {// 云呼过程中客户按按键以后录音，返回给快递员的录音
                                    recordDetail.setContent(itemData.getString("content"));
                                }
                                recordDetail.setVoice_length(itemData.getIntValue("voice_length"));
                                recordDetail.setSpeak_time(itemData.getLongValue("speak_time") * 1000);
                                recordDetails.add(recordDetail);
                            }
                        }
                        KLog.i("kb", "record detail size:--->" + recordDetails.size());
                        Message message = new Message();
                        message.what = GET_TOPIC_DETAIL_SUCCESS;
                        message.obj = recordDetails;
                        mHandler.sendMessage(message);
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }

    /**
     * @param topic_id     主题ID
     * @param content_type 发送内容类型1 - 文字 2 - 图片 3 - 语音 4-文字加单张图片 5-录音(文件名)'
     * @param content      文本
     * @param voice_length 语音时长
     * @return void
     * @Title: createTalk
     * @Description:追加聊天消息
     */
    private void createTalk(String topic_id, int content_type, String content, String voice_length) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_user/create_topic");
            data.put("role", "courier");
            data.put("topic_id", topic_id);
            data.put("content", content);
            data.put("content_type", content_type);
            data.put("voice_length", voice_length);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 发送云语音
     **/
    private void call(String ivid, String mobiles, String topicid) {
        showProgressDialog( "请稍候...");
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr/ivrCall");
            data.put("ivid", ivid);
            data.put("topic_id", topicid);
            data.put("call_data", mobiles);
            data.put("manually_call", "y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        pull.onFooterRefreshComplete();
        pull.onHeaderRefreshComplete();
        btnSend.setEnabled(true);
        dismissProgressDialog();
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != result) {
            if (sname.equals("inform_user/get_topic_detail")) {
                try {
                    state = result.getString("state");// 状态
                    total_page = result.getInt("total_page");// 总页数
                    brand = result.getString("brand");// 品牌
                    cm_name = result.getString("cm_name");// 业务员姓名
                    dh = result.getString("dh");// 单号
                    expressNumber = dh;
                    userPhone = result.getString("user_phone");// 用户手机号
                    phoneNumber = userPhone;
                    orderNo = result.getString("express_number");// 运单编号
                    signedTime = result.optString("signed_time");// 签收时间
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<RecordDetail> recordDetails = new ArrayList<>();
                if (!Utility.isEmpty(state) && state.equals("success")) {
                    try {
                        JSONArray array = result.getJSONArray("desc");
                        if (null != array && array.length() != 0) {

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject itemData = (JSONObject) array.get(i);
                                RecordDetail recordDetail = new RecordDetail();
                                recordDetail.setMessage_id(itemData.optString("message_id"));
                                recordDetail.setSpeaker_role(itemData.optString("speaker_role"));
                                recordDetail.setSpeaker_id(itemData.optString("speaker_id"));
                                recordDetail.setSpeaker_phone(itemData.optString("speaker_phone"));
                                recordDetail.setContent_type(itemData.optInt("content_type"));
                                String content_type = itemData.optString("content_type");
                                if (!Utility.isEmpty(content_type) && content_type.equals("1")) {// 纯文本
                                    recordDetail.setContent(itemData.optString("content"));
                                } else if (!Utility.isEmpty(content_type) && content_type.equals("3")) {// 客户回复语音需要填写插入地址
                                    recordDetail.setContent(itemData.optString("content"));
                                } else if (!Utility.isEmpty(content_type) && content_type.equals("6")) {// 快递员发送云呼语音
                                    recordDetail.setIvr_status(itemData.getString("ivr_status"));
                                    recordDetail.setIvr_status_msg(itemData.getString("ivr_status_msg"));
                                    recordDetail.setIvr_user_input(itemData.getString("ivr_user_input"));
                                    JSONObject content = itemData.optJSONObject("content");
                                    recordDetail.setContent_title(content.optString("title"));
                                    recordDetail.setContent_path(content.optString("path"));
                                } else if (!Utility.isEmpty(content_type) && content_type.equals("7")) {// 云呼过程中客户按按键以后录音，返回给快递员的录音
                                    recordDetail.setContent(itemData.optString("content"));
                                }
                                recordDetail.setVoice_length(itemData.optInt("voice_length"));
                                recordDetail.setSpeak_time(itemData.optLong("speak_time") * 1000);
                                recordDetails.add(recordDetail);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mesg = new Message();
                mesg.what = GET_TOPIC_DETAIL_SUCCESS;
                mesg.obj = recordDetails;
            } else if (sname.equals("inform_user/create_topic")) {
//                MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//                EventBus.getDefault().post(m);
                try {
                    String sendStatus = result.getString("state");
                    message_id = result.getString("message_id");
                    topic_id = result.getString("topic_id");
                    if (sendStatus.equals("success")) {
                        mesg = new Message();
                        mesg.what = SEND_CONTENT_SUCCESS;
                        mesg.obj = sendStatus;
                    }
                    if (!Utility.isEmpty(msg)) {
                        UtilToolkit.showToast(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (sname.equals("ivr/ivrCall")) {
//            MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//            EventBus.getDefault().post(m);

            UtilToolkit.showToast(msg);
            mesg = new Message();
            mesg.what = CLOUD_VOICE_SEND_SUCCESS;
        }
        mHandler.sendMessage(mesg);

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        btnSend.setEnabled(true);
        if(code.equals("10000")){
            if (result.contains("余额不足")){
                SkuaidiDialog dialog = new SkuaidiDialog(this);
                dialog.setTitle("余额不足");
                dialog.setContent("余额不足，请充值。");
                dialog.isUseEditText(false);
                dialog.setPositionButtonTitle("充值");
                dialog.setNegativeButtonTitle("取消");
                dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(mContext, TopUpActivity.class);
                        startActivity(mIntent);
                    }
                });
                dialog.showDialog();
            } else{
                if (!Utility.isEmpty(result)) {
                    UtilToolkit.showToast(result);
                }
            }
        }else{
            if (!Utility.isEmpty(result)) {
                UtilToolkit.showToast(result);
            }
        }




    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        dismissProgressDialog();
        btnSend.setEnabled(true);
        if (!Utility.isEmpty(msg)) {
            UtilToolkit.showToast(msg);
        }
    }

}
