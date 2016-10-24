package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.CloudVoiceRecordAdapter;
import com.kuaibao.skuaidi.activity.adapter.CloudVoiceRecordDraftAdapter;
import com.kuaibao.skuaidi.activity.cloudcallrecord.RecordCloudCallSendFailAndNoReceiveActivity;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.OldRecordsActivity;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
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
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dao.RecordDraftBoxCloudVoiceDAO;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.CloudRecord;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.entry.DraftBoxCloudVoiceInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
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

/**
 * 顾冬冬
 * 云呼记录/云呼草稿箱
 */
public class CloudVoiceRecordActivity extends SkuaiDiBaseActivity implements OnClickListener, OnItemClickListener,
        OnItemLongClickListener {
    public static Stack<Activity> activityStack;
    public final static int GET_VOICE_LIST_SUCCESS = 0X1001;
    private final static int GET_VOICE_SEND_COUNT_SUCCESS = 0X1002;
    private final static int GET_MODELS_SUCCESS = 0x1003;
    private final static int REQUEST_SEARCH = 0x1004;
    public final static int RESULT_SEARCH = 0X1005;
    public final static int GET_VOICE_LIST_FAIL = 0x1006;
    private final static int UPDATE_SIGNED_STATUS = 0x1007;
    private final static int REQUEST_DRAFTBOX_TOSEND = 0X1008;
    private static final int REQUEST_GET_TIMING_LIST_SUCCESS = 0X1009;// 请求定时发送列表成功
    public final static int GET_NOSIGNED_INFO_SUCCESS = 0x10010;// 获取今天云呼失败信息及号码条数
    /**
     * 发短信
     **/
    private final int SEND_TYPE_SMS = 0X1011;
    /**
     * 发送云呼
     **/
    private final int SEND_TYPE_CLOUD = 0X1012;

    private Context mContext = null;
    private SkuaidiDB skuaidiDB = null;
    private Intent mIntent = null;
    private ToastCustom toast = null;// 自定义吐司-显示在屏幕中央的大toast
    private MediaPlayer mPlayer = null;
    private PullToRefreshView pull = null;
    private CloudVoiceRecordAdapter adapter = null;
    private RecordScreeningPop recordScreeningPop = null;
    private List<CloudVoiceRecordEntry> cvre = new ArrayList<>();
    private List<CloudRecord> cloudRecords = null;
    private CloudVoiceRecordDraftAdapter draftAdapter = null;// 草稿箱适配器
    private List<DraftBoxCloudVoiceInfo> draftBoxInfos = new ArrayList<>();
    boolean isSearching;
    private SelectConditionsListPOP selectListMenuPop = null;
    private Activity mActivity = null;

    private ViewGroup llCloudVoiceRecord = null;// 云呼列表记录界面
    private ListView listDraftBox = null;// 草稿箱列表
    private TextView cloudVoiceRecord = null;// 云呼记录按钮
    private TextView cloudVoiceDraftBox = null;// 草稿箱按钮
    private LinearLayout ll_title;// title区域
    private SkuaidiImageView tv_more;// 筛选
    private ListView lv_cloud_list;
    private TextView send_count;// 显示今日呼叫数
    private TextView recive_count;// 客户接听数
    private TextView tvHint = null;// 提示-是否有记录提示

    private String phone = "";
    private String start_time = "";
    private String end_time = "";
    private String bh = "";
    private String status = "";
    private String signed = "";// 参数n:未取件 y:已取件
    private String read = "";// 参数n:未读 y:已读
    private String deleteId = "";// 保存定时发送删除的条目的ID-用于更新列表

    private int pageNum = 1;
    private int total_page = 1;
    private int updatePosition = -1;
    private boolean isDraftBox = false;// 是否是草稿箱界面
//    private String sendNoSignedTag = "";// 用来标记是一键发短信还是一键云呼
    private String sendDesc = "";// 发送失败说明
    private SkuaidiAlertDialogSendMsg alertDialog = null;


    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
//			case NotifyDetailSendYunhuActivity.AGAIN_SHOW_MODEL:
////				showModel();
//				break;
                /*case GET_NOSIGNED_INFO_SUCCESS:
                    int needSendCount = msg.arg1;
                    sendDesc = (String) msg.obj;
                    if (needSendCount <= 0) {
                        UtilToolkit.showToast(sendDesc);
                        return;
                    }
                    if ("sms".equals(sendNoSignedTag)) {
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
                                startActivityForResult(mIntent, NotifyDetailSendYunhuActivity.REQUEST_MSG_MODEL);
                            }

                        });
                        alertDialog.show();
                    } else if ("ivr".equals(sendNoSignedTag)) {
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
                                    UtilToolkit.showToast("请选择已审核模板");
                                }
                            }

                            @Override
                            public void chooseModel() {
                                mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                                startActivityForResult(mIntent, NotifyDetailSendYunhuActivity.REQUEST_VOICE_MODEL);
                            }
                        });
                        alertDialog.show();
                    }
                    break;*/
                case GET_VOICE_LIST_SUCCESS:
                    pull.onFooterRefreshComplete();
                    pull.onHeaderRefreshComplete();

                    if (pageNum == 1) {
                        cvre.clear();
                        cvre = (List<CloudVoiceRecordEntry>) msg.obj;
                    } else {
                        cvre.addAll((List<CloudVoiceRecordEntry>) msg.obj);
                    }
                    adapter.notifyList(cvre);
                    break;
                case GET_VOICE_SEND_COUNT_SUCCESS:
                    send_count.setText("" + msg.arg1);
                    recive_count.setText("( 客户接听：" + msg.arg2 + ")");
                    break;
                case GET_MODELS_SUCCESS:
                    adapter.notifyListPlayIcon();
                    break;
                case UPDATE_SIGNED_STATUS:
                    adapter.modifySignedStatus(updatePosition);
                    break;
                case REQUEST_GET_TIMING_LIST_SUCCESS:
                    List<DraftBoxCloudVoiceInfo> infos = (List<DraftBoxCloudVoiceInfo>) msg.obj;
                    if (draftBoxInfos.size() == 0) {
                        draftBoxInfos.addAll(infos);
                    } else {
                        draftBoxInfos.addAll(0, infos);
                    }
                    draftAdapter.setData(draftBoxInfos);
                    setBgHint();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.cloud_voice_record_activity);
        mContext = this;
        mActivity = this;
        activityStack = new Stack<>();
        EventBus.getDefault().register(this);
        SkuaidiSpf.saveRecordChooseItem(mContext, 0);// 将筛选条目置下标置0
        SkuaidiSpf.saveConditionsListItem(mContext, 0);// 将选择器列表设置选中下标为0
        skuaidiDB = SkuaidiDB.getInstanse(mContext);

        findView();
        setOnListener();

        adapter = new CloudVoiceRecordAdapter(mContext, mHandler, cvre, new CloudVoiceRecordAdapter.ButtonClickListener() {

            @Override
            public void call(View v, int position, String callNumber) {
                if (Utility.isEmpty(callNumber)) {
                    UtilToolkit.showToast("本条记录没有联系号码");
                } else {
                    // 跳转到拨打电话界面
                    // Intent intent = new
                    // Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+callNumber));
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // startActivity(intent);
                    // 直接拨打电话
//					Intent mIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));
//					startActivity(mIntent);
                    AcitivityTransUtil.showChooseTeleTypeDialog(CloudVoiceRecordActivity.this, "", callNumber,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
                }
            }

            @Override
            public void updateSignedStatus(View v, int position, String cid) {
                updatePosition = position;
                updateSigned(cid);
            }
        });
        lv_cloud_list.setAdapter(adapter);
        getData();
        // addGuid();

        // *********************获取draftBoxInfos集合数据
        getDraftBoxData();
        // *********************设置草稿箱中的数据到列表中
        draftAdapter = new CloudVoiceRecordDraftAdapter(mContext, draftBoxInfos);
        listDraftBox.setAdapter(draftAdapter);
        getTimingList();
        toast = new ToastCustom(mContext, 5, tv_more);
    }

    private void getDraftBoxData() {
        draftBoxInfos = RecordDraftBoxCloudVoiceDAO.getDraftBoxInfo(SkuaidiSpf.getLoginUser().getPhoneNumber());
        List<String> deleteIds = new ArrayList<>();
        for (int i = 0; i < draftBoxInfos.size(); i++) {
            DraftBoxCloudVoiceInfo draftInfo = draftBoxInfos.get(i);
            String modelTitle = draftInfo.getModelTitle();
            String msgNumberPhone = draftInfo.getPhoneNumber();
            String[] numArr = DraftBoxUtility.strToArr(msgNumberPhone);

            final String id = draftInfo.getId();
            boolean haveData = false;
            for (int j = 0;j<numArr.length;j++){
                if (!Utility.isEmpty(numArr[i].replace(" ",""))){
                    haveData = true;
                    break;
                }
            }
            if (Utility.isEmpty(modelTitle) && !haveData) {
                deleteIds.add(id);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        RecordDraftBoxCloudVoiceDAO.deleteDraftByID(id);
                    }
                }).start();
            }
        }

        for (int i = 0; i < deleteIds.size(); i++) {
            for (int j = 0; j < draftBoxInfos.size(); j++) {
                if (deleteIds.get(i).equals(draftBoxInfos.get(j).getId())) {
                    draftBoxInfos.remove(j);
                }
            }
        }

    }

    private void setBgHint() {
        if (!isDraftBox) {
            if (cvre.size() == 0) {
                pull.setVisibility(View.GONE);
                tvHint.setText("没有云呼记录");
                tvHint.setVisibility(View.VISIBLE);
            } else {
                pull.setVisibility(View.VISIBLE);
                tvHint.setVisibility(View.GONE);
            }
        } else {
            if (draftBoxInfos.size() == 0) {
                listDraftBox.setVisibility(View.GONE);
                tvHint.setText("没有记录");
                tvHint.setVisibility(View.VISIBLE);
            } else {
                listDraftBox.setVisibility(View.VISIBLE);
                tvHint.setVisibility(View.GONE);
            }
        }

    }

    // 接收eventbus传递过来的对象，此处用于更新列表小红点
    @Subscribe
    public void onEventMainThread(CloudVoiceRecordEntry cvre) {
        for (int i = 0; i < adapter.getListDetail().size(); i++) {
            CloudVoiceRecordEntry cvre2 = adapter.getListDetail().get(i);
            if (cvre.getTopic_id().equals(cvre2.getTopic_id())) {
                cvre2.setNoreadFlag(0);
                cvre2.setSigned(cvre.getSigned());
                adapter.getListDetail().set(i, cvre2);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void stopPlayRecord() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void findView() {
        cloudVoiceRecord = (TextView) findViewById(R.id.smsRecordBox);
        cloudVoiceDraftBox = (TextView) findViewById(R.id.smsDraftBox);
        llCloudVoiceRecord = (ViewGroup) findViewById(R.id.llCloudVoiceRecord);
        listDraftBox = (ListView) findViewById(R.id.lvCloudVoiceDraftBox);
//        parentView = findViewById(R.id.cloud_record_layout);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        SkuaidiImageView iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        tv_more = (SkuaidiImageView) findViewById(R.id.tv_more);
        pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        lv_cloud_list = (ListView) findViewById(R.id.lv_cloud_list);
        send_count = (TextView) findViewById(R.id.send_count);
        recive_count = (TextView) findViewById(R.id.recive_count);
        ViewGroup rl_search = (ViewGroup) findViewById(R.id.rl_search);
        tvHint = (TextView) findViewById(R.id.tvHint);

        // ViewTouchDelegate.expandViewTouchDelegate(searchCloudRecord, 0, 0,
        // 400, 400);

        cloudVoiceRecord.setOnClickListener(this);
        cloudVoiceDraftBox.setOnClickListener(this);
        iv_title_back.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        tv_more.setVisibility(View.VISIBLE);
//		tv_more.setText("筛选");
    }

    private void getData() {
        cloudRecords = skuaidiDB.getCloudRecordModels();
        getModels();
        getList(1, phone, "", start_time, end_time, bh, status, signed, read);
        getCallCount();
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
        EventBus.getDefault().unregister(this);
        if (activityStack == null)
            return;
        // 退出时关闭 可能已经开启的CloudVoiceRecordSearchActivity
        for (Activity ac : activityStack) {
            ac.finish();
        }
        activityStack.clear();
        activityStack = null;
    }

    boolean isSearch;
    int searchPageNum, searchTotal_page;
    String queryNum;

    private void setOnListener() {
        lv_cloud_list.setOnItemClickListener(this);
        listDraftBox.setOnItemClickListener(this);
        listDraftBox.setOnItemLongClickListener(this);

        // 以上四个点击事件，控制扫条码和确定按钮的变化

        pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

            @Override
            public void onHeaderRefresh(PullToRefreshView view) {

                pull.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 拉取数据
                        if (Utility.isNetworkConnected()) {
                            if (isSearching) {
                                searchPageNum = 1;
                                getList(pageNum, phone, queryNum, "", "", "", "", "", "");
                            } else {
                                pageNum = 1;
                                getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);
                            }

                            getCallCount();
                        } else {
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
                        if (Utility.isNetworkConnected()) {
                            if (isSearching) {
                                searchPageNum += 1;
                                if (searchPageNum <= searchTotal_page) {
                                    getList(pageNum, phone, queryNum, "", "", "", "", "", "");
                                } else {
                                    pull.onFooterRefreshComplete();
                                    UtilToolkit.showToast("已加载全部数据");
                                }
                            } else {
                                pageNum += 1;
                                if (pageNum <= total_page) {
                                    getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);
                                } else {
                                    pull.onFooterRefreshComplete();
                                    UtilToolkit.showToast("已加载全部数据");
                                }
                            }

                            getCallCount();
                        } else {
                            UtilToolkit.showToast("无网络连接");
                        }
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smsRecordBox:// 云呼记录列表按钮
                isDraftBox = false;
                setBgHint();
                tv_more.setVisibility(View.VISIBLE);
                pull.setVisibility(View.VISIBLE);
                llCloudVoiceRecord.setVisibility(View.VISIBLE);
                listDraftBox.setVisibility(View.GONE);
                cloudVoiceRecord.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundLeft_white());
                cloudVoiceDraftBox.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundRight());
                cloudVoiceRecord.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
                cloudVoiceDraftBox.setTextColor(Utility.getColor(mContext,R.color.white));
                cloudVoiceRecord.setEnabled(false);
                cloudVoiceDraftBox.setEnabled(true);
                break;
            case R.id.smsDraftBox:// 草稿箱列表按钮
                isDraftBox = true;
                setBgHint();
                pull.setVisibility(View.GONE);
                llCloudVoiceRecord.setVisibility(View.GONE);
                listDraftBox.setVisibility(View.VISIBLE);
                cloudVoiceRecord.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundLeft());
                cloudVoiceDraftBox.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundRight_white());
                cloudVoiceRecord.setTextColor(Utility.getColor(mContext,R.color.white));
                cloudVoiceDraftBox.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
                if (recordScreeningPop != null) {
                    recordScreeningPop.dismissPop();
                    recordScreeningPop = null;
                }
                cloudVoiceRecord.setEnabled(true);
                cloudVoiceDraftBox.setEnabled(false);
                break;
            case R.id.iv_title_back:// 返回
                SkuaidiSpf.setDeliverFilterMenuIndex(mContext, 0, 0);
                stopPlayRecord();
                finish();
                if (recordScreeningPop != null) {
                    recordScreeningPop.dismissPop();
                    recordScreeningPop = null;
                }
                break;
            case R.id.tv_more:// 筛选

                moreMenu();
                break;

            case R.id.rl_search:
                mIntent = new Intent(mContext, CloudVoiceRecordSearchActivity.class);
                startActivityForResult(mIntent, REQUEST_SEARCH);
                // setActivityAnimTranslateUp(parentView, ll_title);
                break;
            default:
                break;
        }
    }

    /**
     * 更多菜单功能
     **/
    private void moreMenu() {
        if (recordScreeningPop != null) {
            recordScreeningPop.dismissPop();
            recordScreeningPop = null;
        }

        if (selectListMenuPop == null) {
            List<String> conditions = new ArrayList<>();
            conditions.add("筛选");
            conditions.add("云呼发送统计");
            conditions.add("历史云呼记录");
            //conditions.add("短信今日未接");

            selectListMenuPop = new SelectConditionsListPOP(mActivity, conditions, 0.4f, true, SelectConditionsListPOP.SHOW_RIGHT);
            // selectListMenuPop.setBackgroundResource(R.drawable.paopao5);
            selectListMenuPop.setItemOnclickListener(new SelectConditionsListPOP.ItemOnClickListener() {

                @Override
                public void itemOnClick(int position) {
                    Intent _intent;
                    switch (position) {
                        case 0:// 筛选
                            eventScreen();
                            break;
                        case 1:// 云呼发送统计
//                            Intent intent = new Intent(CloudVoiceRecordActivity.this, CountRecordsActivity.class);
//                            intent.putExtra(CountRecordsActivity.RECORD_TYPE_NAME, CountRecordsActivity.YUNHU_RECORD);
//                            startActivity(intent);
                            String url = "http://m.kuaidihelp.com/statistical/smsWithIvr?mobile="+SkuaidiSpf.getLoginUser().getPhoneNumber()+"&type=ivr";
                            loadWeb(url,"");
                            break;
                        case 2:// 历史云呼记录
                            Intent intent2 = new Intent(CloudVoiceRecordActivity.this, OldRecordsActivity.class);
                            intent2.putExtra(OldRecordsActivity.OLD_RECORDS_NAME, OldRecordsActivity.YUNHU_OLD_RECORDS);
                            startActivity(intent2);
                            break;
//					case 2:// 云呼昨日未取件
////						UMShareManager.onEvent(mContext, "smsRecord_OneKeySendCloud", "SmsRecord", "短信记录:一键云呼");
////						sendNoSignedTag = "ivr";
////						getNoSignedSmsInfo(sendNoSignedTag);
////						showProgressDialog( "请稍候");
//						sendNoSignedTag = "ivr";
//						getNoSignedCloudInfo(sendNoSignedTag);
//						showProgressDialog( "请稍候");
//						break;
//					case 3:// 短信昨日未取件
//
//						sendNoSignedTag = "sms";
//						getNoSignedCloudInfo(sendNoSignedTag);
//						showProgressDialog( "请稍候");
////						UMShareManager.onEvent(mContext, "smsRecord_OneKeyMsg", "SmsRecord", "短信记录:一键发短信");
////						sendNoSignedTag = "sms";
////						getNoSignedSmsInfo(sendNoSignedTag);
////						showProgressDialog( "请稍候");
//						break;

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
            selectListMenuPop.showAsDropDown(tv_more, 20, 0);
        } else {
            selectListMenuPop.dismissPop();
            selectListMenuPop = null;
        }
    }


    /**
     * 筛选
     **/
    private void eventScreen() {
        if (recordScreeningPop == null) {
            List<String> itemArr = new ArrayList<>();
            itemArr.add("全部");
            itemArr.add("呼叫成功");
            itemArr.add("呼叫失败");
            itemArr.add("未取件");
            itemArr.add("未读信息");

            recordScreeningPop = new RecordScreeningPop(mContext, ll_title, itemArr);
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
                    pageNum = 1;
                    phone = "";
                    start_time = "";
                    end_time = "";
                    bh = "";
                    signed = "";
                    read = "";
                    status = null;
                    switch (position) {
                        case 0:// 全部
                            UMShareManager.onEvent(mContext, "sms_logs_all", "piePiece_notice", "短信记录:发送失败");
                            getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);
                            break;
                        case 1:// 呼叫成功
                            status = "2";
                            getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);
                            break;
                        case 2:// 呼叫失败
//                            status = "1";
//                            getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);

                            mIntent = new Intent(mContext, RecordCloudCallSendFailAndNoReceiveActivity.class);
                            mIntent.putExtra("status","faild");
                            startActivity(mIntent);

                            break;
                        case 3:// 未取件
//                            signed = "n";
//                            getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);
                            mIntent = new Intent(mContext, RecordCloudCallSendFailAndNoReceiveActivity.class);
                            mIntent.putExtra("status","nosigned");
                            startActivity(mIntent);
                            break;
                        case 4:// 未读信息
                            read = "n";
                            getList(pageNum, phone, "", start_time, end_time, bh, status, signed, read);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SEARCH && resultCode == RESULT_SEARCH) {
            ll_title.setVisibility(View.VISIBLE);
            // setActivityAnimTranslateDown(parentView, ll_title);
        } else if (requestCode == REQUEST_DRAFTBOX_TOSEND) {
            getDraftBoxData();// 重新获取数据库中的数据
            draftAdapter.setData(draftBoxInfos);
            setBgHint();
            // 获取定时发送列表
            getTimingList();
            setBgHint();
        } else if (requestCode == SendYunHuActivity.REQUEST_MSG_MODEL) {

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
                    startActivityForResult(mIntent, SendYunHuActivity.REQUEST_MSG_MODEL);
                }
            });
            alertDialog.show();

        } else if (requestCode == SendYunHuActivity.REQUEST_VOICE_MODEL) {
//			Message msg = new Message();
//			msg.what = NotifyDetailSendYunhuActivity.AGAIN_SHOW_MODEL;
//			mHandler.sendMessage(msg);

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
                        UtilToolkit.showToast("请选择已审核模板");
                    }
                }

                @Override
                public void chooseModel() {
                    mIntent = new Intent(mContext, AddVoiceModelActivity.class);
                    startActivityForResult(mIntent, SendYunHuActivity.REQUEST_VOICE_MODEL);
                }
            });
            alertDialog.show();
        }
    }

    /**
     * @param cid
     * 更新取件状态接口
     * 顾冬冬
     */
    private void updateSigned(String cid) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr/updateSign");
            data.put("id", cid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * @param pageNum 页数
     * @param phone 按手机号
     * @param start_time 开始时间
     * @param end_time 结束时间
     * @param bh 编号
     * @param status 接听状态
     * @param signed n:未签收 y:已签收 ，不传此参数则为所有
     * @param read   n:未签收 y:已签收 ，不传此参数则为所有
     * 调用云呼语音列表接口
     * author: 顾冬冬
     */
    private void getList(int pageNum, String phone, String queryNum, String start_time, String end_time, String bh, String status, String signed,
                         String read) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr.call.list");
            data.put("page_num", pageNum);
            data.put("page_size", Constants.PAGE_SIZE);
            data.put("call_number", phone);
            data.put("query_number", queryNum);
            data.put("start_time", start_time);
            data.put("end_time", end_time);
            data.put("bh", bh);
            data.put("status", status);
            data.put("signed", signed);
            data.put("read", read);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 获取模板列表
     **/
    private void getModels() {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "getlist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 今日云呼统计接口
     **/
    private void getCallCount() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivrcount.get");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 【接口】获取定时发送列表
     **/
    private void getTimingList() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr/get_timing_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    /**
     * 【接口】 删除定时发送
     **/
    private void deleteTiming(String id) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr/delete_timing");
            data.put("id", id);
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

    List<CloudVoiceRecordEntry> tempList = new ArrayList<>();

    @Override
    protected void onRequestSuccess(String sname, String message, String json1, String act) {
        dismissProgressDialog();
        JSONObject result = null;
        Message msg ;
        try {
            result = new JSONObject(json1);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if ("ivr.call.list".equals(sname) && null != result) {
            List<CloudVoiceRecordEntry> cvre = new ArrayList<>();
            //
            try {
                JSONObject json = result.getJSONObject("retArr");
                if (isSearch) {
                    searchTotal_page = json.optInt("total_page");
                } else {
                    total_page = json.optInt("total_page");// 总页数
                }

                JSONArray jArr = json.optJSONArray("data");
                if (null != jArr && 0 != jArr.length()) {
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.optJSONObject(i);
                        CloudVoiceRecordEntry cvRecordEntry = new CloudVoiceRecordEntry();
                        cvRecordEntry.setCid(jObj.optString("cid"));
                        cvRecordEntry.setTopic_id(jObj.optString("topic_id"));
                        cvRecordEntry.setBh(jObj.optString("bh"));
                        cvRecordEntry.setDh(jObj.optString("dh"));
                        cvRecordEntry.setVoice_title(jObj.optString("voice_title"));
                        cvRecordEntry.setVoice_name(jObj.optString("voice_name"));
                        cvRecordEntry.setVoice_path(jObj.optString("voice_path"));
                        cvRecordEntry.setFee_mins(jObj.optString("fee_mins"));
                        cvRecordEntry.setCall_number(jObj.optString("call_number"));
                        cvRecordEntry.setUser_input_key(jObj.optString("user_input_key"));
                        cvRecordEntry.setCall_duration(jObj.optInt("call_duration"));
                        cvRecordEntry.setStatus(jObj.optString("status"));
                        cvRecordEntry.setStatus_msg(jObj.optString("status_msg"));
                        cvRecordEntry.setCreate_time(jObj.optString("create_time"));
                        cvRecordEntry.setSigned(jObj.optInt("signed"));
                        cvRecordEntry.setNoreadFlag(jObj.optInt("noread_flag"));
                        cvRecordEntry.setLastMsgContent(jObj.optString("last_msg_content"));
                        cvRecordEntry.setLastMsgContentType(jObj.optString("last_msg_content_type"));
                        cvRecordEntry.setLastMsgTime(jObj.optString("last_msg_time"));
                        cvRecordEntry.setCall_time(jObj.optString("call_time"));
                        cvre.add(cvRecordEntry);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg = new Message();
            msg.what = GET_VOICE_LIST_SUCCESS;
            msg.obj = cvre;
            mHandler.sendMessage(msg);
        } else if ("ivrcount.get".equals(sname) && null != result) {
            int call_count = 0;
            int answer_count = 0;
            try {
                JSONObject retArr = result.getJSONObject("retArr");
                call_count = retArr.getInt("callCount");
                answer_count = retArr.getInt("answerCount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg = new Message();
            msg.what = GET_VOICE_SEND_COUNT_SUCCESS;
            msg.arg1 = call_count;
            msg.arg2 = answer_count;
            mHandler.sendMessage(msg);
        } else if ("ivr.voice".equals(sname) && "getlist".equals(act)) {
            List<CloudRecord> cRecords = new ArrayList<>();
            try {
                if (null == result){
                    UtilToolkit.showToast("获取数据失败");
                    return;
                }
                JSONArray array = result.optJSONArray("retArr");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObject = (JSONObject) array.get(i);
                    CloudRecord cRecord = new CloudRecord();
                    cRecord.setCreateTime(jObject.optString("create_time"));// 语音创建时间
                    cRecord.setTitle(jObject.optString("title"));// 语音标题
                    cRecord.setIvid(jObject.optString("ivid"));// 语音对应服务器ID
                    cRecord.setFileName(jObject.optString("file_name"));// 语音名称
                    cRecord.setExamineStatus(jObject.optString("state"));// 语音审核状态
                    cRecord.setVoiceLength((int) Double.parseDouble(jObject.optString("voice_length")));// 语音的时长
                    String path = jObject.optString("path");
                    String pathService = "http://upload.kuaidihelp.com" + path.substring(path.indexOf("/", 2));// 音频下载路径
                    cRecord.setPathService(pathService);// 保存音频下载路径
                    cRecord.setPathLocal(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/cRecord/"
                            + jObject.optString("file_name") + ".wav");
                    cRecord.setChoose(false);
                    cRecords.add(cRecord);
                }
                skuaidiDB.insertCloudRecord(cRecords);

            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < cloudRecords.size(); i++) {
                for (int j = 0; j < cRecords.size(); j++) {
                    if (null != cloudRecords.get(i)) {
                        //Log.i("gudd", "  " + cloudRecords.get(i).getIvid() + "     " + cRecords.get(j).getIvid());
                        if (cloudRecords.get(i).getIvid().equals(cRecords.get(j).getIvid())) {
                            break;
                        }
                        if (j == cRecords.size() - 1) {
                            skuaidiDB.deleteCloudByivid(cloudRecords.get(i).getIvid());// 删除本地对应服务器上没有的模板
                        }
                    }
                }
            }
            msg = new Message();
            msg.what = GET_MODELS_SUCCESS;
            mHandler.sendMessage(msg);
        } else if ("ivr/updateSign".equals(sname)) {
            msg = new Message();
            msg.what = UPDATE_SIGNED_STATUS;
            mHandler.sendMessage(msg);
        } else if ("ivr/get_timing_list".equals(sname)) {
            try {
                if (null == result){
                    UtilToolkit.showToast("获取数据失败");
                    return;
                }
                JSONArray jsonArray = result.getJSONArray("data");
                if (null != jsonArray && jsonArray.length() > 0) {
                    List<DraftBoxCloudVoiceInfo> infos = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = (JSONObject) jsonArray.get(i);
                        DraftBoxCloudVoiceInfo info = new DraftBoxCloudVoiceInfo();
                        info.setId(obj.getString("id"));
                        info.setTimingTag("y");
                        info.setModelId(obj.getString("ivid"));
                        info.setPhoneNumber(obj.getString("user_phone"));
                        info.setModelTitle(obj.getString("title"));
                        info.setCreateTime(obj.getString("create_time"));
                        info.setSendTime(obj.getString("send_time"));
                        info.setLastUpdateTime(obj.getString("last_update_time"));
                        infos.add(info);
                    }
                    msg = new Message();
                    msg.what = REQUEST_GET_TIMING_LIST_SUCCESS;// 请求定时发送列表成功
                    msg.obj = infos;
                    mHandler.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (!Utility.isEmpty(sname) && "ivr/delete_timing".equals(sname)) {
            if (!Utility.isEmpty(deleteId)) {
                for (int i = 0; i < draftBoxInfos.size(); i++) {
                    if (draftBoxInfos.get(i).getId().equals(deleteId)) {
                        draftBoxInfos.remove(i);
                        break;
                    }
                }
                toast.show(message);
                draftAdapter.setData(draftBoxInfos);
                setBgHint();
            }
        } else if ("inform_nosigned/getivrinfo".equals(sname)) {
            int needSendCount = -1;
            String sendMsg = "";
            try {
                if (null == result){
                    UtilToolkit.showToast("获取数据失败");
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
            mHandler.sendMessage(msg);
        } else if ("inform_nosigned/send".equals(sname)) {
            if (!Utility.isEmpty(message)) {
                UtilToolkit.showToast(message);
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        pull.onHeaderRefreshComplete();
        pull.onFooterRefreshComplete();
        dismissProgressDialog();
        if (!Utility.isEmpty(result)) {
            if (!Utility.isEmpty(sname) && "ivr/delete_timing".equals(sname)) {
                toast.show(result);
            } else {
                UtilToolkit.showToast(result);
            }
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SkuaidiSpf.setDeliverFilterMenuIndex(mContext, 0, 0);
            stopPlayRecord();
            if (recordScreeningPop != null) {
                recordScreeningPop.dismissPop();
                recordScreeningPop = null;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_cloud_list:// 云呼记录列表控件
                CloudVoiceRecordEntry entry = cvre.get(position);
                if (Integer.parseInt(entry.getTopic_id()) == 0) {
                    UtilToolkit.showToast("无可查看的详情内容");
                    return;
                }
                if (isSearching) {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).getTopic_id().equals(entry.getTopic_id())) {
                            tempList.get(i).setNoreadFlag(0);
                            break;
                        }
                    }
                }
                cvre.get(position).setNoreadFlag(0);
                Intent intent = new Intent(mContext, RecordDetailActivity.class);
                intent.putExtra("fromActivity", "cloudVoiceRecordActivity");
                intent.putExtra("cloudEntry", cvre.get(position));
                startActivity(intent);
                adapter.notifyDataSetChanged();
                break;
            case R.id.lvCloudVoiceDraftBox:// 草稿箱列表控件
                DraftBoxCloudVoiceInfo draftBoxInfo = draftBoxInfos.get(position);
                if (!Utility.isEmpty(draftBoxInfo.getTimingTag()) && draftBoxInfo.getTimingTag().equals("y")) {
                    mIntent = new Intent(mContext, TimingSendCloudCancelActivity.class);
                    mIntent.putExtra("draftBoxRecord", draftBoxInfo);
                    startActivityForResult(mIntent, REQUEST_DRAFTBOX_TOSEND);
                } else {
                    mIntent = new Intent(mContext, SendYunHuActivity.class);
                    mIntent.putExtra("draftBoxRecord", draftBoxInfo);
                    mIntent.putExtra("fromActivity", "draftbox");
                    startActivityForResult(mIntent, REQUEST_DRAFTBOX_TOSEND);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        switch (parent.getId()) {
            case R.id.lvCloudVoiceDraftBox:// 草稿箱列表控件
                final String timing_tag = draftBoxInfos.get(position).getTimingTag();
                SkuaidiDialogGrayStyle dialogGray = new SkuaidiDialogGrayStyle(mContext);
                dialogGray.setTitleGray("删除提示");
                if (!Utility.isEmpty(timing_tag) && "y".equals(timing_tag)) {
                    dialogGray.setContentGray("删除后将取消定时发送,\n确定要删除该条草稿？");
                } else {
                    dialogGray.setContentGray("是否要删除这条云呼草稿");
                }
                dialogGray.setPositionButtonTextGray("是");
                dialogGray.setNegativeButtonTextGray("否");
                dialogGray.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

                    @Override
                    public void onClick(View v) {
                        String id = draftBoxInfos.get(position).getId();
                        if (!Utility.isEmpty(timing_tag) && "y".equals(timing_tag)) {
                            deleteId = id;
                            deleteTiming(id);
                        } else {
                            DraftBoxCloudVoiceInfo draftBoxInfo = draftBoxInfos.get(position);
                            String draft_id = draftBoxInfo.getId();
                            RecordDraftBoxCloudVoiceDAO.deleteDraftByID(draft_id);
                            draftBoxInfos = RecordDraftBoxCloudVoiceDAO.getDraftBoxInfo(SkuaidiSpf.getLoginUser().getPhoneNumber());
                            draftAdapter.setData(draftBoxInfos);
                            setBgHint();
                        }
                    }
                });
                dialogGray.showDialogGray(listDraftBox);

                break;

            default:
                break;
        }
        return true;
    }

}
