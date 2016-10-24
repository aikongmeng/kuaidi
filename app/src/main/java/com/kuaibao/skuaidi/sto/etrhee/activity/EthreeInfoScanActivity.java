package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.NoSignActivity;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter;
import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter.DelItemListener;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.view.GetPhotoTypePop;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.EditTextMaxLengthListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.PositiveButtonOnclickListener;
import com.kuaibao.skuaidi.dispatch.activity.DispatchActivity;
import com.kuaibao.skuaidi.dispatch.adapter.ZTSignAdapter;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.dispatch.bean.ZTSignType;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.E3ScanActivity;
import com.kuaibao.skuaidi.qrcode.SpecialEquipmentScanActivity;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.bean.BusinessHall;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.ContextOfBatchOperations;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.DataManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.adapter.BusinessHallAdapter;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.MeasureUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.ViewTouchDelegate;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Subscription;
import rx.functions.Action1;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.kuaibao.skuaidi.dispatch.activity.DispatchActivity.GET_ADDRESS;

public class EthreeInfoScanActivity extends SpecialEquipmentScanActivity implements OnItemClickListener,
        OnClickListener, EthreeInfoScanAdapter.HideKeyboard {

    public static final String ZT_THIRD_PARTY_BRANCH_GET_BRANCH = "parter.zt.ThirdPartyBranch.GetBranch";
    public static List<NotifyInfo> importList;//扫码页面，导入未签单号

    private static final String BRAND_QF = "qf";
    private static final String BRAND_STO = "sto";
    private static final String BRAND_ZT = "zt";
    private TextView tv_title;
    private TextView tv_upload;
    private EditText edt_scanInput;
    private TextView tv_type, tvOperate;
    private TextView tv_count;//单号数量
    private ListView lv;
    private LinearLayout ll_sto_weight_notify;//sto 提示录入重量布局
    private View ll_operType, rl_bottom_center;
    // 全选按钮
    private ImageView iv;
    private Button bt;
    private EthreeInfoScanAdapter adapter;
    private boolean selected = false;
    public String scanType;
    private List<NotifyInfo> list;
    private Context context;
    public static final int TACKE_PIC_REQUEST_CODE = 701;
    public static final int TACKE_PIC_RESPONSE_CODE = 702;
    public static final int ADD_WEIGHT_RESPONSE_CODE = 100;
    private String imei;
    private final ArrayList<String> numberList = new ArrayList<>();
    private final ArrayList<String> statusList = new ArrayList<>();

    /**
     * 上传同时发短信
     */
    private boolean sendSMS = false;

    /**
     * 上传同时云呼
     */
    private boolean sendYunSMS = false;
    private List<NotifyInfo> nextsite_list;
    private List<NotifyInfo> upsite_list;
    private Map<String, String> upmap;
    private Map<String, String> nextmap;
    public String courierNO = "";
    public CourierReviewInfo reviewInfo;
    private NotifyInfo notifyInfo;
    private static final int MAX_SIGNED_PIC = 3;//图片签收上传，一次最多包含三张互不相同的照片
    /**
     * 包含图片的签收件
     */
    private final List<NotifyInfo> picSignInfos = new ArrayList<>();
    /**
     * qf 错误单号
     */
    public List<NotifyInfo> unUsualInfos = new ArrayList<>();
    /**
     * 可上传单号
     */
    private final List<NotifyInfo> usualInfos = new ArrayList<>();
    /**
     * 中通退件
     */
//    private List<NotifyInfo> interceptInfos = new ArrayList<NotifyInfo>();

    /**
     * 已经签收
     */
    private final List<NotifyInfo> signedList = new ArrayList<>();

    /**
     * 是否包含图片签收
     */
    private boolean signed_pic = false;

    /***
     * 是否忽略重复
     */
    private boolean ignore = false;

    private final ArrayList<String> numbers_qf = new ArrayList<>();

    private GetPhotoTypePop mPopupWindow;
    /**
     * 是否是扫描枪扫描模式
     */
    private boolean byScanner = false;

    private int forceIntercept = 0;// 中通：是否检查拦截件 0，检查 1，不检查

    private Ringtone ringtone;// 拦截件语音提示
    private Ringtone repeatedRingtone;// 拦截件语音提示
    private boolean ringtone_initialized = false;// 语音提示初始化
    private boolean signCheck = false;// 是否已经检查签收状态
    private final ArrayList<String> picPathList = new ArrayList<>();// 所有单号对应的图片地址
    private List<String> allSuccessList = new ArrayList<>();//所有上传成功的单号

    private List<String> successList = new ArrayList<>();//每次上传成功的单号

    private List<BusinessHall> hallList;

    /***
     * 是否自动发短信  ，针对签收件和问题件
     */
    private int autoSendSms = 0;

    public UserInfo mUserInfo;
    List<DispatchActivity.AddressInfo> addressList;

    private String tagMessage = "";
    private int tagCount = 0;
    private String brand;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        EventBus.getDefault().register(this);
        scanType = getIntent().getStringExtra("scanType");
        byScanner = getIntent().getBooleanExtra("byScanner", false);
        mUserInfo = SkuaidiSpf.getLoginUser();
        brand = mUserInfo.getExpressNo();
        if ((E3SysManager.SCAN_TYPE_BADPICE.equals(scanType) || E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType) || E3SysManager.SCAN_TYPE_PIEPICE
                .equals(scanType))) {// 申通问题件新增发短信按钮
            setContentView(R.layout.ethreeinfoscan_bad);
            showBanchWeight = false;
        } else {
            setContentView(R.layout.ethreeinfoscan);
            showBanchWeight = true;
        }
        reviewInfo = E3SysManager.getReviewInfo();
        courierNO = reviewInfo.getCourierJobNo();
        switch (scanType) {
            case E3SysManager.SCAN_TYPE_BADPICE:
                autoSendSms = SkuaidiSpf.getAutoProblemNotify(courierNO) ? 1 : 0;
                break;
            case E3SysManager.SCAN_TYPE_SIGNEDPICE:
                autoSendSms = SkuaidiSpf.getAutoSignNotify(courierNO) ? 1 : 0;
                break;
        }
        if (getIntent().hasExtra("isSpecialEquipment") && getIntent().getBooleanExtra("isSpecialEquipment", false)) {
            list = new ArrayList<>();
        } else if (getIntent().getBooleanExtra("isImport", false)) {
            list = importList;
        } else {
            if ("NoSignActivity".equals(getIntent().getStringExtra("from"))) {
                if (NoSignActivity.selectedOrders != null) {
                    list = NoSignActivity.selectedOrders;
                    NoSignActivity.selectedOrders = null;
                } else {
                    list = (List<NotifyInfo>) getIntent().getSerializableExtra("e3WayBills");
                }

            } else {
                list = (List<NotifyInfo>) getIntent().getSerializableExtra("e3WayBills");

            }
            getScanRecordsDatas(list);
        }
        getControl();
        showDatas();
        setIME();
        String shop_id = mUserInfo.getIndexShopId();
        // 下一站
        if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
            nextmap = SkuaidiSpf.getNextstation(context);
            // 拿本地缓存，然后调接口
            if (nextmap != null && nextmap.size() > 0) {
                Set<String> keySet = nextmap.keySet();
                nextsite_list = new ArrayList<>();
                for (String key : keySet) {
                    notifyInfo = new NotifyInfo();
                    String value = nextmap.get(key);
                    notifyInfo.setStation_no(key);
                    notifyInfo.setStation_name(value);
                    nextsite_list.add(notifyInfo);
                }
                // 发给界面做显示
                SKuaidiApplication.getInstance().postMsg("type_manager", "next_site", nextsite_list);
            }
            // 有网情况调接口，更新本地数据
            if (Utility.isNetworkConnected()) {

                JSONObject object = new JSONObject();
                try {
                    object.put("sname", "prev_next_site");
                    object.put("type", "1");
                    object.put("index_shop_id", shop_id);
                    httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // 上一站
            // 界面一进来 先读本地缓存，然后调接口
        } else if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
            upmap = SkuaidiSpf.getUpstation(context);
            if (upmap != null && upmap.size() > 0) {

                Set<String> keySet = upmap.keySet();
                upsite_list = new ArrayList<>();
                for (String key : keySet) {
                    notifyInfo = new NotifyInfo();
                    String value = upmap.get(key);
                    notifyInfo.setStation_no(key);
                    notifyInfo.setStation_name(value);
                    upsite_list.add(notifyInfo);
                }
                SKuaidiApplication.getInstance().postMsg("type_manager", "up_site", upsite_list);
            }
            // 有网情况调接口，更新本地数据
            if (Utility.isNetworkConnected()) {
                JSONObject object = new JSONObject();
                try {
                    object.put("sname", "prev_next_site");
                    object.put("type", "2");
                    object.put("index_shop_id", shop_id);
                    httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        allselect(null);
        hallList = SkuaidiSpf.getZTBusinessHall();
        if (hallList == null || hallList.size() == 0 || SkuaidiSpf.getQueryBusinessHallTime() - System.currentTimeMillis() > 1000 * 60 * 60 * 24) {
            getBusinessHall();

        }

        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
            List<String> noList = new ArrayList<>();
            for (NotifyInfo info : list) {
                noList.add(info.getExpress_number());
            }
            getAddressByWaybillNo(noList);
            if (BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                if (SkuaidiSpf.getZTSignTypes() == null || SkuaidiSpf.getZTSignTypes().size() == 0 || SkuaidiSpf.getQueryZTsignsTime() - System.currentTimeMillis() > 1000 * 60 * 60 * 24) {
                    getSignType();
                } else {
                    ztSignTypes = SkuaidiSpf.getZTSignTypes();
                }
            }
        }

        if (getIntent().hasExtra("tagType") && !TextUtils.isEmpty(getIntent().getStringExtra("tagType")) && getIntent().getIntExtra("tagCount", 0) != 0) {
            tagCount = getIntent().getIntExtra("tagCount", 0);
            if ("pay".equals(getIntent().getStringExtra("tagType"))) {
                tagMessage = "有" + tagCount + "个货到付款件，是否继续签收？";
            } else {
                tagMessage = "有" + tagCount + "个拦截件，是否继续签收？";
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
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialog != null)
            dialog.dismiss();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

    /**
     * @param context
     * @param scanType           扫描类型
     * @param byScanner          是否是扫描枪扫描的
     * @param isSpecialEquipment 是否是特殊设备
     * @param isImport           是否是导入未签收
     * @param e3WayBills         数据
     * @param from               从哪个界面跳转过来
     */
    public static void actionStart(Context context, String scanType, boolean byScanner, boolean isSpecialEquipment, boolean isImport, String e3WayBills, String from) {
        Intent intent = new Intent(context, EthreeInfoScanActivity.class);
        intent.putExtra("scanType", scanType);
        intent.putExtra("byScanner", byScanner);
        intent.putExtra("isSpecialEquipment", isSpecialEquipment);
        intent.putExtra("isImport", isImport);
        intent.putExtra("e3WayBills", e3WayBills);
        intent.putExtra("from", from);
        context.startActivity(intent);
    }


    public boolean isShowBanchWeight() {
        return showBanchWeight;
    }

    public void setShowBanchWeight(boolean showBanchWeight) {
        this.showBanchWeight = showBanchWeight;
    }

    private boolean showBanchWeight = true;

    /**
     * 从服务器获单号最新状态
     */
    @SuppressWarnings("unchecked")
    private void getScanRecordsDatas(List<NotifyInfo> infos) {

        JSONObject data = new JSONObject();
        list = infos;
        String numbers = "";
        try {
            data.put("sname", "express.end.status");
            if (list == null || list.size() == 0)
                return;
            numberList.clear();
            for (int i = 0; i < list.size(); i++) {
                numbers += list.get(i).getExpress_number() + ",";
                numberList.add(list.get(i).getExpress_number());
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("express_no", numbers);
            data.put("company", mUserInfo.getExpressNo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 生成一个唯一标识only_code 用来判断一个用户1分钟内最多扫描5单
     */
    private void setIME() {
        // 设备唯一imei
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        only_code = Utility.getOnlyCode();

    }

    /**
     * 初始化控件
     */
    private void getControl() {
        if (byScanner) {
            initBeepSound();
            initRepeatedTone();
        }
        edt_scanInput = (EditText) findViewById(R.id.edt_scanInput);
        edt_scanInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    return;
                if (s.charAt(s.length() - 1) == '\n' && s.length() > 7) {
                    String number = s.subSequence(0, s.length() - 1).toString();
                    if (!E3SysManager.isValidWaybillNo(number)) {
                        UtilToolkit.showToast("非" + E3SysManager.brandMap.get(brand) + "条码");
                        playRepeatedTone();
                        s.clear();
                        return;
                    }
                    NotifyInfo info = new NotifyInfo();
                    info.setExpress_number(number);
                    if (!numberList.contains(number)) {
                        numberList.add(number);
                        playBeepTone();
                        info.setScanTime(E3SysManager.getTimeBrandIndentify());
                        if (E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)
                                || E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                                || E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
                            info.setCourierJobNO(courierNO);
                            info.setWayBillTypeForE3(reviewInfo.getCourierName());
                        }
                        list.add(info);
                        adapter.notifyDataSetChanged();
                        lv.smoothScrollToPosition(lv.getCount() - 1);
                    } else {
                        UtilToolkit.showToast("单号重复！");
                        playRepeatedTone();
                    }
                    s.clear();

                } else if (s.toString().contains("\n") && s.charAt(s.length() - 1) != '\n' || s.length() > 13) {
                    s.clear();
                }
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title_des);
        tv_type = (TextView) findViewById(R.id.tv_ethreeinfoscan_type);
        tvOperate = (TextView) findViewById(R.id.tv_lanPice_operate);
        lv = (ListView) findViewById(R.id.lv_ethreeinfoscan);
        iv = (ImageView) findViewById(R.id.iv_ethreeinfoscan);
        bt = (Button) findViewById(R.id.bt_ethreeinfoscan);
        ll_operType = findViewById(R.id.ll_operType);
        rl_bottom_center = findViewById(R.id.rl_bottom_center);
        ll_sto_weight_notify = (LinearLayout) findViewById(R.id.ll_sto_weight_notify);
        ImageView btn_save = (ImageView) findViewById(R.id.btn_upload);
        ImageView btn_upload = (ImageView) findViewById(R.id.btn_upload_1);

        TextView tv_save = (TextView) findViewById(R.id.tv_save);
        tv_upload = (TextView) findViewById(R.id.tv_upload);

        tv_count = (TextView) findViewById(R.id.tv_count);

        tv_upload.setTextColor(SkuaidiSkinManager.getTextColor("default_green_2"));
        tv_save.setTextColor(SkuaidiSkinManager.getTextColor("default_green_2"));
        btn_save.setImageResource(R.drawable.bg_save);
        btn_upload.setImageResource(R.drawable.bg_upload);

        if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType) || E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)
                || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {
            LinearLayout ll_uploadAndSend = (LinearLayout) findViewById(R.id.ll_uploadAndSend);
            TextView tv_upload_mess = (TextView) findViewById(R.id.tv_upload_mess);
            tv_upload_mess.setTextColor(SkuaidiSkinManager.getTextColor("default_green_2"));
            ImageView btn_upload_mess = (ImageView) findViewById(R.id.btn_upload_mess);

            tv_upload_mess.setTextColor(getResources().getColor(R.color.default_green_2));

            btn_upload_mess.setBackgroundResource(R.drawable.icon_upload_notify);
//            btn_upload.setBackgroundResource(R.drawable.icon_question_upload_other);
//            btn_save.setBackgroundResource(R.drawable.icon_question_save_other);
            if ("EThreeInterfaceActivity".equals(getIntent().getStringExtra("from"))) {
                ll_uploadAndSend.setVisibility(View.GONE);
            }
        }

        ViewTouchDelegate.expandViewTouchDelegate(iv, 10, 10, 10, 10);// 扩大点击区域

        if (byScanner && !SkuaidiSpf.getBluetoothHintShowen()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    SkuaidiDialog dialog = new SkuaidiDialog(context);
                    dialog.setTitle("扫描枪输入");
                    dialog.isUseSingleButton(true);
                    dialog.setSingleButtonTitle("知道了");
                    dialog.isUseEditText(false);
                    dialog.setContent("支持市面上常见的扫描枪，包括蓝牙无线扫描枪、普通无线扫描枪、有线扫描枪。具体请查看巴枪扫描-设置-使用帮助。");
                    dialog.show();

                }
            }, 100);
            SkuaidiSpf.setBluetoothHintShowen(true);
        }

    }

    /**
     * listView 加载并显示数据
     */
    @SuppressWarnings("unchecked")
    private void showDatas() {

        if (E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {
            tv_type.setText("派件员");
            bt.setText("批量选派件员");
            bt.setVisibility(View.VISIBLE);
            rl_bottom_center.setVisibility(View.VISIBLE);
            tv_title.setText("派件扫描");
            ll_operType.setVisibility(View.VISIBLE);

        } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {// 上一站，下一站
            // 仅对申通。
            if (BRAND_STO.equals(mUserInfo.getExpressNo())) {
                tv_title.setText("发件扫描");
                tv_type.setText("设置下一站");
                rl_bottom_center.setVisibility(View.VISIBLE);
                bt.setText("批量设置下一站");
            }
        } else if ("扫到件".equals(scanType)) {
            if (BRAND_STO.equals(mUserInfo.getExpressNo())) {
                tv_title.setText("到件扫描");
                tv_type.setText("设置上一站");
                rl_bottom_center.setVisibility(View.VISIBLE);
                bt.setText("批量设置上一站");
            }
        } else if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)) {
            tv_type.setText("收件员");
            bt.setText("收件操作");
            bt.setVisibility(View.VISIBLE);
            rl_bottom_center.setVisibility(View.VISIBLE);
            tv_title.setText("收件扫描");
            ll_operType.setVisibility(View.VISIBLE);
            if (showBanchWeight && BRAND_STO.equals(mUserInfo.getExpressNo())) {
                tvOperate.setText("物品类别");
                ll_sto_weight_notify.setVisibility(View.VISIBLE);
                ll_sto_weight_notify.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EthreeInfoScanActivity.this, WebViewActivity.class);
                        intent.putExtra("fromwhere", "weighing_explanation");
                        startActivity(intent);
                    }
                });
            }
        } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
            tv_type.setText("签收人");

            if (BRAND_STO.equals(mUserInfo.getExpressNo()))// 申通包含签收件举证
                bt.setText("签收件操作");
            else
                bt.setText("批量输入签收人");
            bt.setVisibility(View.VISIBLE);
            ll_operType.setVisibility(View.VISIBLE);
            rl_bottom_center.setVisibility(View.VISIBLE);
            tv_title.setText("签收扫描");

        } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {

            tv_type.setText("营业厅");

            if (BRAND_STO.equals(mUserInfo.getExpressNo()))// 申通包含签收件举证
                bt.setText("签收件操作");
            else
                bt.setText("批量选择营业厅");
            bt.setVisibility(View.VISIBLE);
            ll_operType.setVisibility(View.VISIBLE);
            rl_bottom_center.setVisibility(View.VISIBLE);
            tv_title.setText("第三方签收");
        } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
            tv_type.setText("问题类型");
            bt.setVisibility(View.VISIBLE);
            if (BRAND_STO.equals(mUserInfo.getExpressNo()))// 申通包含问题件举证
                bt.setText("问题件操作");
            else
                bt.setText("批量输入问题件类型");
            ll_operType.setVisibility(View.VISIBLE);
            rl_bottom_center.setVisibility(View.VISIBLE);
            tv_title.setText(scanType);
        }
        if (list == null) {
            list = new ArrayList<>();
        }

        adapter = new EthreeInfoScanAdapter(this, list, scanType, new EthreeInfoScanAdapter.CheckCallBack() {

            @Override
            public void checkStatus(boolean isAllCheck) {
                if (isAllCheck) {
                    selected = true;
                    iv.setImageResource(R.drawable.batch_add_checked);
                } else {
                    selected = false;
                    iv.setImageResource(R.drawable.select_edit_identity);
                }
            }
        }, editTextMaxLengthListener);
        adapter.setDelItemListener(new DelItemListener() {
            @Override
            public void isDelete(NotifyInfo info) {
                removeItem(info);
            }
        });
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        if (showBanchWeight && BRAND_STO.equals(mUserInfo.getExpressNo())) {
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(EthreeInfoScanActivity.this);
                    builder.setMessage("删除该条记录?");
                    builder.setTitle("温馨提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            removeItem(adapter.getItem(position));
                            adapter.removeItem(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });
        }
//        controlAllSelecteView();
    }

    public void removeItem(NotifyInfo info) {
        for (int i = numberList.size() - 1; i >= 0; i--) {
            if (numberList.get(i).equals(info.getExpress_number())) {
                numberList.remove(i);
                break;
            }
        }
        for (int j = statusList.size() - 1; j >= 0; j--) {
            if (statusList.get(j).equals(info.getStatus())) {
                statusList.remove(j);
                break;
            }
        }

        if (numberList.size() == 0) {
            allselect(null);
        }
    }

//    private void controlAllSelecteView() {
//        if (list != null) {
//            boolean s = true;
//            for (int i = 0, j = list.size(); i < j; i++) {
//
//                if (!list.get(i).isChecked()) {
//                    s = false;
//                }
//            }
//            if (s) {
//                iv.setImageResource(R.drawable.batch_add_checked);
//                selected = true;
//            }
//
//        }
//    }

    /**
     * 保存单号信息
     */

    //public boolean isGoBanchWeight=false;
    public void save(final View view) {
        final List<NotifyInfo> array = adapter.getList();
        if (array.size() == 0) {
            UtilToolkit.showToast("请扫描后再操作");
            return;
        }

//        if(BRAND_STO.equals(company) && E3SysManager.SCAN_TYPE_LANPICE.equals(scanType) && !isGoBanchWeight){
//            showAlertNoWeightDialog();
//            return;
//        }

        //不做“重复”提醒了
//        if (adapter.hasRepetition) {
//            showAlertDialog(view, array, "save");
//            return;
//        }

        saveData(array);

    }

    private void showAlertNoWeightDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("请录入重量！请在当前页面\"收件操作\"中录入");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * @param view   。。
     * @param array  运单数据
     * @param action 操作类型
     */
    private void showAlertDialog(final View view, final List<NotifyInfo> array, final String action) {

        dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);
        if (!dialog.isShowing()) {

            if ("save".equals(action)) {
                dialog.setTitle("保存提醒");
                dialog.setCommonContent("是否保存重复的单号？");
                dialog.setNegativeButtonTitle("取消");
                dialog.setPositiveButtonTitle("继续保存");
            } else if ("upload".equals(action)) {
                dialog.setTitle("上传提醒");
                String hint = "您的扫描列表中有重复单号";
                SpannableStringBuilder sb = new SpannableStringBuilder(hint);
                sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_green_four)),
                        hint.indexOf("重复"), hint.indexOf("重复") + "重复".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dialog.setCommonContent(sb);
                dialog.setPositiveButtonTitle("仅上传不重复");
                dialog.setNegativeButtonTitle("全部上传");
            }

            dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

                @Override
                public void onClick() {
                    if (!isFinishing())
                        dialog.dismiss();
                    if ("save".equals(action)) {
                        saveData(array);
                    } else if ("upload".equals(action)) {
                        ignore = true;
                        uploadDatas(array, null);
                    }
                }
            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    if (!isFinishing())
                        dialog.dismiss();
                    if ("upload".equals(action)) {
                        ignore = false;
                        uploadDatas(array, null);
                    }
                }
            });
            if (!isFinishing())
                dialog.showDialog();
        }

    }


    /**
     * @param view  。。
     * @param array 运单数据
     */
    private void showAlertDialogSendUpload(final View view, final List<NotifyInfo> array) {

        dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);
        if (!dialog.isShowing()) {
            dialog.setTitle("上传提醒");
            String hint = "您的扫描列表中有重复单号";
            SpannableStringBuilder sb = new SpannableStringBuilder(hint);
            sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_green_four)),
                    hint.indexOf("重复"), hint.indexOf("重复") + "重复".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dialog.setCommonContent(sb);
            dialog.setPositiveButtonTitle("仅上传不重复");
            dialog.setNegativeButtonTitle("全部上传");

            dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

                @Override
                public void onClick() {
                    if (!isFinishing())
                        dialog.dismiss();
                    ignore = true;
                    uploadDatas(array, null);

                }
            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    if (!isFinishing())
                        dialog.dismiss();
                    ignore = false;
                    uploadDatas(array, null);

                }
            });
            if (!isFinishing())
                dialog.showDialog();
        }

    }


    /**
     * @param array 运单数据
     */
    private void saveData(List<NotifyInfo> array) {
        if (!signCheck && isSigned(array)) {
            showSignDialog(tv_upload, array, "save");
            signCheck = false;
            return;
        }
        signCheck = false;
        DataManager dm = new DataManager(scanType);
        // 判断数据是否完整
        if (!dm.isComplete(lv, array)) {
            return;
        }
        List<NotifyInfo> infos = new ArrayList<>();
        for (NotifyInfo info : array) {
            //签收，问题件已签收件不能保存上传
//            if (TextUtils.isEmpty(info.getStatus()) || !info.getStatus().contains("签收")) {
//                infos.add(info);
//            }
            if (!(!TextUtils.isEmpty(info.getStatus()) && info.getStatus().contains("签收") && (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)))) {
                infos.add(info);
            }
        }
        if (infos.size() == 0) {
            UtilToolkit.showToast("没有可保存的数据");
        } else {
            ArrayList<E3_order> orders = infoToOrder(infos, 0, 0);
            for (E3_order order : orders) {
                E3OrderDAO.addOrder(order, mUserInfo.getExpressNo(), courierNO);
            }
            array.remove(infos);
            E3OrderDAO.deleteCacheOrders(orders);
            adapter.getList().removeAll(infos);
        }
        adapter.notifyDataSetChanged();
        if (adapter.getList() != null && adapter.getList().size() == 0) {
            if ("EThreeInterfaceActivity".equals(getIntent().getStringExtra("from"))) {
                if (EThreeInterfaceActivity.activityList.size() > 0) {
                    for (Activity activity : EThreeInterfaceActivity.activityList) {
                        activity.finish();
                    }
                    EThreeInterfaceActivity.activityList.clear();
                }
            }
            if (SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
                Intent intent = new Intent(context, BackgroundUploadService.class);
                startService(intent);
            }
            finish();
        }
        // 删除缓存

    }

    /**
     * @param notifyInfos 运单数据
     * @param isUpload    是否已经上传过
     * @param isCache     是否是缓存
     * @return ArrayList<E3_order> 可保存的数据集
     */
    public ArrayList<E3_order> infoToOrder(List<NotifyInfo> notifyInfos, int isUpload, int isCache) {
        ArrayList<E3_order> orders = new ArrayList<>();
        for (int i = 0; i < notifyInfos.size(); i++) {

            E3_order order = new E3_order();
            NotifyInfo info = notifyInfos.get(i);
            order.setOrder_number(info.getExpress_number());
            order.setType_extra(info.getWayBillTypeForE3());
            order.setFirmname(mUserInfo.getExpressNo());
            order.setOrder_weight(info.getWeight());
            order.setResType(info.getResType());
            if ("扫收件".equals(scanType)) {
                order.setType("收件");
                order.setOperatorCode(TextUtils.isEmpty(info.getCourierJobNO()) ? reviewInfo
                        .getCourierJobNo() : info.getCourierJobNO());
            } else if ("扫派件".equals(scanType)) {
                order.setType("派件");
                order.setOperatorCode(TextUtils.isEmpty(info.getCourierJobNO()) ? reviewInfo
                        .getCourierJobNo() : info.getCourierJobNO());
            } else if ("扫到件".equals(scanType)) {
                order.setType("到件");
                order.setSta_name(info.getStation_name());
            } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                order.setType("签收件");
                if (!TextUtils.isEmpty(info.getPicPath())) {
                    order.setPicPath(info.getPicPath());
                    order.setWayBillType_E3("图片签收");
                    order.setType_extra("图片签收");// 两个字段表示同一意思
                } else {
                    order.setWayBillType_E3(info.getWayBillTypeForE3());
                    order.setType_extra(info.getWayBillTypeForE3());
                }
            } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                order.setType("第三方签收");
                //签收营业厅
                order.setThirdBranch(info.getThirdBranch());
                order.setThirdBranchId(info.getThirdBranchId());
                //签收营业厅
            } else if ("问题件".equals(scanType)) {
                order.setType("问题件");
                order.setWayBillType_E3(info.getWayBillTypeForE3());
                order.setProblem_desc(info.getProblem_desc());
                order.setPhone_number(info.getPhone_number());
            } else if ("扫发件".equals(scanType)) {// 发件不指定操作员
                order.setType("发件");
                order.setSta_name(info.getStation_name());
            }
            order.setScan_time(info.getScanTime());
            order.setCompany(mUserInfo.getExpressNo());
            order.setCourier_job_no(courierNO);
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            order.setLatitude(info.getLatitude());
            order.setLongitude(info.getLongitude());
            orders.add(order);

        }
        return orders;
    }

    /**
     * 上传订单同时发短信
     *
     * @param view 。。
     */
    public void uploadAndSend(View view) {
        List<NotifyInfo> array = new ArrayList<>();
        array.addAll(adapter.getList());
        if (isIncomplete(array)) {
            return;
        }
        mPopupWindow = new GetPhotoTypePop(context, "upload", this);
        mPopupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
        mPopupWindow.isOutsideTouchCancleable(true);
    }

//    @Subscribe
//    public void onRecieveMessage(String message){
//        if("pleaseGoBanchWeigh".equals(message)){
//            isGoBanchWeight=false;
//        }
//    }

    /**
     * 上传按钮监听
     *
     * @param view 。。
     */
    public void upload(View view) {
        if (!Utility.isNetworkConnected()) {// 无网络
            UtilToolkit.showToast("请检查网络设置！");
            return;
        }

        final List<NotifyInfo> array = new ArrayList<>();
        array.addAll(adapter.getList());

        if (array.size() == 0) {
            UtilToolkit.showToast("请扫描后再来提交！");
            return;
        }

//        if(BRAND_STO.equals(company) && E3SysManager.SCAN_TYPE_LANPICE.equals(scanType) && !isGoBanchWeight){
//            showAlertNoWeightDialog();
//            return;
//        }

        usualInfos.clear();
        //不做“重复”提醒了
//        if (adapter.hasRepetition) {
//            showAlertDialog(view, array, "upload");
//            return;
//        } else {
//            ignore = false;// 没有重复单号，不存在忽略重复问题
//        }
        if (!TextUtils.isEmpty(tagMessage)) {
            com.kuaibao.skuaidi.dialog.CustomDialog.Builder builder = new com.kuaibao.skuaidi.dialog.CustomDialog.Builder(this);
            builder.setMessage(tagMessage).setTitle("温馨提示").setPositiveButton("继续签收", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    uploadDatas(array, null);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
            return;
        }

        uploadDatas(array, null);
    }

    private boolean isSigned(List<NotifyInfo> list) {
        boolean isSigned = false;
        signedList.clear();
        //所有扫描类型，如果状态是已签收不允许上传。
        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
            for (int i = 0, j = list.size(); i < j; i++) {
                if (!TextUtils.isEmpty(list.get(i).getStatus()) && list.get(i).getStatus().contains("签收")) {
                    signedList.add(list.get(i));
                    isSigned = true;
                }
            }
        }
        return isSigned;
    }

    /**
     * 上传数据
     *
     * @param array    。。
     * @param tempType 中通退件
     */

    private void uploadDatas(List<NotifyInfo> array, String tempType) {
        if (!signCheck && isSigned(array)) {
            showSignDialog(tv_upload, array, "upload");
            signCheck = false;
            return;
        }
        signCheck = false;
        if (isIncomplete(array)) {
            dismissProgressDialog();
            return;
        }

        if (!isFinishing())
            showProgressDialog("正在上传请稍后...");
        JSONObject datas = new JSONObject();
        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
            picPathList.clear();
            for (NotifyInfo info : list) {
                picPathList.add(info.getPicPath());
            }
        }

        try {
            datas.put("sname", E3SysManager.getScanNameV2());
            datas.put("appVersion", Utility.getVersionCode());
            if (TextUtils.isEmpty(tempType))
                datas.put("wayBillType", E3SysManager.typeToIDMap.get(scanType));
            else
                datas.put("wayBillType", E3SysManager.typeToIDMap.get(tempType));
            datas.put("dev_id", only_code);
            datas.put("dev_imei", imei);
            if (BRAND_ZT.equals(mUserInfo.getExpressNo()))
                datas.put("forceIntercept", forceIntercept);

            JSONArray wayBills = new JSONArray();
            JSONObject signPics = new JSONObject();// id:图片
            JSONObject picPath = new JSONObject();// id:图片路径

            resetVariable();
            int count_pic = 0;
            int pic_no_diverse = 0;// 图片序号
            for (int i = 0; i < usualInfos.size(); i++) {
                JSONObject wayBill = new JSONObject();
                NotifyInfo notifyInfo = usualInfos.get(i);
                //已经签收件不能做签收件和问题件上传
                if (!TextUtils.isEmpty(notifyInfo.getStatus()) && notifyInfo.getStatus().contains("签收") && (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_BADPICE.equals(scanType))) {
                    continue;
                }
                wayBill.put("waybillNo", notifyInfo.getExpress_number());
                wayBill.put("scan_time", notifyInfo.getScanTime());
                if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
                    // 站点编号
                    wayBill.put("forward_station", getSiteCode(usualInfos.get(i).getStation_name()));
                } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
                    // 站点编号
                    wayBill.put("next_station", getSiteCode(usualInfos.get(i).getStation_name()));
                }

                if (BRAND_QF.equals(brand)) {// 全峰
                    if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                        if (!TextUtils.isEmpty(notifyInfo.getPicPath())) {
                            wayBill.put("signPic", Utility.bitMapToString(Utility.getImage(notifyInfo.getPicPath())));
                        } else {
                            wayBill.put("signType", notifyInfo.getWayBillTypeForE3());
                        }
                        wayBill.put("delivery_status", "签收");// 运单状态详细说明
                        wayBill.put("sign_man", "unknow");// 签收人

                    } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                        String[] badDesc = notifyInfo.getWayBillTypeForE3().split("\n");
                        String badSubject = badDesc[0];
                        String badType = badDesc[1];
                        wayBill.put("type", badSubject);// 问题件subject
                        wayBill.put("register_site", reviewInfo.getCourierLatticePoint());// 录入网点
                        wayBill.put("send_site", "unknow");// 寄件网点
                        wayBill.put("scan_site", reviewInfo.getCourierLatticePoint());// 扫描网点
                        wayBill.put("register_man", reviewInfo.getCourierName());// 录入人
                        wayBill.put("problem_cause", badType);// 问题件具体类型
                        wayBill.put("register_man_department", "unknow");
                        wayBill.put("mobile", notifyInfo.getPhone_number());

                    } else if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                            || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {// 发件不指定发件员
                        wayBill.put("operatorCode",
                                TextUtils.isEmpty(notifyInfo.getCourierJobNO()) ? reviewInfo.getCourierJobNo()
                                        : notifyInfo.getCourierJobNO());
                    }

                } else if (BRAND_STO.equals(mUserInfo.getExpressNo())) {// 申通
                    if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                            || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {// 发件不指定发件员
                        wayBill.put("operatorCode",
                                TextUtils.isEmpty(notifyInfo.getCourierJobNO()) ? reviewInfo.getCourierJobNo()
                                        : notifyInfo.getCourierJobNO());
                    } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                        if (!TextUtils.isEmpty(notifyInfo.getPicPath())) {
                            wayBill.put("signPic", Utility.bitMapToString(Utility.getImage(notifyInfo.getPicPath())));
                            if (!KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getPicPath())) {
                                count_pic++;
                                signed_pic = true;
                            }
                            if (count_pic > MAX_SIGNED_PIC && usualInfos.size() > MAX_SIGNED_PIC) {
                                break;
                            }
                        } else {
                            wayBill.put("signType", notifyInfo.getWayBillTypeForE3());
                        }
                        picSignInfos.add(notifyInfo);
                    } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                        wayBill.put("badWayBillCode", E3SysManager.getBadWaiBillTypeId(notifyInfo.getWayBillTypeForE3()));
                        if (E3SysManager.getBadWaiBillTypeId(notifyInfo.getWayBillTypeForE3()) == 0) {
                            wayBill.put("badWayBillType", notifyInfo.getWayBillTypeForE3());
                        }
                        wayBill.put("mobile", notifyInfo.getPhone_number());
                        wayBill.put("badWayBillDesc", notifyInfo.getProblem_desc());
                    }
                    if (!Utility.isEmpty(notifyInfo.getProblem_desc())) {
                        UMShareManager.onEvent(context, "problePice_liuyan_qrcode", "problePice_liuyan", "问题件申通留言");
                    }
                } else if (BRAND_ZT.equals(mUserInfo.getExpressNo())) {// 中通

                    if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                        if (TextUtils.isEmpty(notifyInfo.getPicPath())) {
                            wayBill.put("signType", notifyInfo.getWayBillTypeForE3());
                        } else {

                            if (picSignInfos.size() == 0) {
                                pic_no_diverse++;
                                wayBill.put("signPic", pic_no_diverse);
                                signPics.put("" + pic_no_diverse,
                                        Utility.bitMapToString(Utility.getImage(notifyInfo.getPicPath())));
                                picPath.put("" + pic_no_diverse, notifyInfo.getPicPath());

                            } else {
                                boolean samePic = false;
                                Iterator<String> iterator = picPath.keys();
                                while (iterator.hasNext()) {
                                    String id = iterator.next();
                                    if (picPath.optString(id).equals(notifyInfo.getPicPath())) {
                                        wayBill.put("signPic", id);
                                        samePic = true;
                                        break;
                                    }
                                }
                                if (!samePic) {
                                    pic_no_diverse++;
                                    if (pic_no_diverse <= MAX_SIGNED_PIC) {
                                        wayBill.put("signPic", pic_no_diverse);
                                        signPics.put("" + pic_no_diverse,
                                                Utility.bitMapToString(Utility.getImage(notifyInfo.getPicPath())));
                                        picPath.put("" + pic_no_diverse, notifyInfo.getPicPath());
                                    }
                                }

                            }

                            if (!KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getPicPath())) {
                                count_pic++;
                                signed_pic = true;
                            }
                            if (pic_no_diverse > MAX_SIGNED_PIC && usualInfos.size() > MAX_SIGNED_PIC) {
                                break;
                            }
                            picSignInfos.add(notifyInfo);
                        }

                    } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                        wayBill.put("thirdBranch", notifyInfo.getThirdBranch());
                        wayBill.put("thirdBranchId", notifyInfo.getThirdBranchId());
                    } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                        wayBill.put("question_desc", notifyInfo.getProblem_desc());// 问题件内容
                        wayBill.put("badWayBillCode",
                                E3SysManager.getZTBadWaiBillTypeId(notifyInfo.getWayBillTypeForE3()));
                        wayBill.put("mobile", notifyInfo.getPhone_number());
                    } else if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                            || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {// 发件不指定发件员
                        wayBill.put("operatorCode",
                                TextUtils.isEmpty(notifyInfo.getCourierJobNO()) ? reviewInfo.getCourierJobNo()
                                        : notifyInfo.getCourierJobNO());
                    }

                }
                JSONObject location = new JSONObject();
                location.put("latitude", notifyInfo.getLatitude());
                location.put("longitude", notifyInfo.getLongitude());
                wayBill.put("location", location);
                wayBill.put("weight", notifyInfo.getWeight());
                wayBill.put("resType", notifyInfo.getResType());
                wayBills.put(wayBill);
            }
            if (wayBills.length() != 0) {
                datas.put("wayBillDatas", wayBills);
                datas.put("signPics", signPics);
                if (!sendSMS) {
                    datas.put("sendSms", autoSendSms);
                }
            } else {
                dismissProgressDialog();
                UtilToolkit.showToast("没有可上传的数据了");
                resetVariable();
                return;
            }
            if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_lanPice_confirm", "E3", "E3：收件扫描提交");
            } else if (E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_piePice_confirm", "E3", "E3：派件扫描提交");
            } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_signedPice_confirm", "E3", "E3：签收件扫描提交");
            } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_badPice_confirm", "E3", "E3：问题件扫描提交");
            } else if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_daoPice_confirm", "E3", "E3：到件扫描提交");
            } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_daoPice_confirm", "E3", "E3：到件扫描提交");
            } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                UMShareManager.onEvent(context, "E3_scan_thirdPice_confirm", "E3", "E3：第三方签收扫描提交");
            }
            requestV2(datas);

        } catch (NumberFormatException e) {
            UtilToolkit.showToast("单号格式异常！");
            dismissProgressDialog();
            e.printStackTrace();
        } catch (JSONException e) {
            UtilToolkit.showToast("未知异常！");
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    /**
     * 检查数据信息是否完整
     *
     * @param array 。。
     */
    private boolean isIncomplete(List<NotifyInfo> array) {
        if (array == null || array.size() == 0) {
            UtilToolkit.showToast("请扫描后再来提交");
            signCheck = false;
            return true;
        }
        usualInfos.clear();
        if (ignore) {
            for (int i = 0; i < array.size(); i++) {
                if (adapter.getRepeatList() != null && !adapter.getRepeatList().contains(array.get(i))
                        && !usualInfos.contains(array.get(i))) {
                    usualInfos.add(array.get(i));
                }
            }
        } else {
            usualInfos.addAll(array);
        }
        if (usualInfos.size() == 0) {
            if (!signed_pic)
                UtilToolkit.showToast("没有可上传的数据了");
            else
                dismissProgressDialog();
            signCheck = false;
            return true;
        }
        signCheck = false;
        if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
            for (int i = 0; i < usualInfos.size(); i++) {
                if (KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getWayBillTypeForE3())) {
                    UtilToolkit.showToast("请选择问题类型");
                    lv.smoothScrollToPosition(i);
                    return true;
                }
            }
        } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
            for (int i = 0; i < usualInfos.size(); i++) {

                if (BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                    if (KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getPicPath()) && TextUtils.isEmpty(usualInfos.get(i).getWayBillTypeForE3())) {
                        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                            UtilToolkit.showToast("请选择签收人");
                        }
                        lv.smoothScrollToPosition(i);
                        return true;
                    }
                } else if (BRAND_QF.equals(mUserInfo.getExpressNo())) {
                    if (KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getWayBillTypeForE3())) {// 全峰只有选择签收人，不支持拍照签收
                        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                            UtilToolkit.showToast("请选择签收人");
                        }
                        lv.smoothScrollToPosition(i);
                        return true;
                    }
                } else {
                    if (KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getWayBillTypeForE3())
                            && KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getPicPath())) {
                        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                            UtilToolkit.showToast("请选择签收人");
                        }
                        lv.smoothScrollToPosition(i);
                        return true;
                    }
                }

            }
        } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
            for (int i = 0; i < usualInfos.size(); i++) {

                if (BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                    if (KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getThirdBranch())) {// 中通第三方签收 营业点
                        if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                            UtilToolkit.showToast("请选择营业厅");
                        }
                        lv.smoothScrollToPosition(i);
                        return true;
                    }
                }
            }
        } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
            for (int i = 0; i < usualInfos.size(); i++) {
                if (KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getStation_name())) {
                    UtilToolkit.showToast("请选择下一站");
                    lv.smoothScrollToPosition(i);
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * 全选按钮监听
     *
     * @param view 。。
     */
    public void allselect(View view) {
        List<NotifyInfo> array = adapter.getList();
        if (!selected && array != null && array.size() != 0) {
            selected = true;
            iv.setImageResource(R.drawable.batch_add_checked);
            for (int i = 0; i < array.size(); i++) {
                array.get(i).setChecked(true);
            }
            adapter.setCheckCount(array.size());
            adapter.notifyDataSetChanged();
            lv.smoothScrollToPosition(array.size() - 1);
        } else {
            selected = false;
            iv.setImageResource(R.drawable.select_edit_identity);
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    array.get(i).setChecked(false);
                }
            }
            adapter.setCheckCount(0);
            adapter.notifyDataSetChanged();
        }

    }

    private EditText editText;

    /**
     * 批量选择
     *
     * @param view 。。
     */
    public void allsettype(View view) {

        if (adapter.getCheckedList().size() == 0) {
            UtilToolkit.showToast("请勾选您要修改的单号");
            return;

        }
        if (BRAND_ZT.equals(mUserInfo.getExpressNo()) && "扫签收".equals(scanType)) {
//            allselect(null);
//                Intent intent = new Intent(context, EThreeCameraActivity.class);
//                intent.putExtra("wayBills", (Serializable) adapter.getList());
//                startActivityForResult(intent, TACKE_PIC_REQUEST_CODE);
            showSignTypeDialog(adapter.getCheckedList());
            return;
        } else {
            ContextOfBatchOperations operations = new ContextOfBatchOperations(context, adapter, scanType, iv,
                    editTextMaxLengthListener, nextsite_list, upsite_list, view);
            operations.doAction();
        }

    }

    @Override
    public void onBackPressed() {
        back(findViewById(R.id.iv_title_back));
    }

    /**
     * title左边回退按钮点击事件
     */
    public void back(View view) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }
        showDialog(view);
    }

    private SkuaidiE3SysDialog dialog;
    private String only_code;

    /**
     * 显示提醒对话框
     *
     * @param v 。。
     */
    private void showDialog(View v) {
        dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, v);
        if (list != null && list.size() != 0) {
            if (!dialog.isShowing()) {

                dialog.setTitle("放弃巴枪扫描上传");
                dialog.setCommonContent("你将放弃上传已经扫描的单号!\n确认放弃？");
                dialog.setPositiveButtonTitle("确认");
                dialog.setNegativeButtonTitle("取消");
                dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

                    @Override
                    public void onClick() {
                        UMShareManager.onEvent(context, "E3_giveUp_scanConfirm", "E3", "E3：放弃上传扫描单号");
                        // 删除图片
                        E3SysManager.deletePicsByNotifyInfo(picSignInfos);
                        // 清楚缓存
                        E3OrderDAO.deleteCacheOrders(infoToOrder(adapter.getList(), 1, 0));
                        finish();
                    }
                });
                if (!isFinishing())
                    dialog.showDialog();
            } else {
                if (!isFinishing())
                    dialog.dismiss();
            }
        } else {
            if (dialog != null) {
                dialog.dismiss();
            }
            finish();
        }
    }

    /**
     * 显示已经签收提醒对话框
     *
     * @param v 。。
     */
    private void showSignDialog(final View v, final List<NotifyInfo> array, final String action) {
        dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, v);
        if (!dialog.isShowing()) {
            String numbers = "";
            for (NotifyInfo info : signedList) {
                numbers += info.getExpress_number() + ",";
            }
            numbers = numbers.subSequence(0, numbers.length() - 1).toString();
            dialog.setTitle("已签收提醒");
            if ("save".equals(action)) {
                dialog.setCommonContent("以下单号:" + numbers + "已签收，继续保存将不包含这些单号");
                dialog.setPositiveButtonTitle("继续保存");
            } else if ("upload".equals(action)) {
                dialog.setCommonContent("以下单号:" + numbers + "已签收，继续上传将不包含这些单号");
                dialog.setPositiveButtonTitle("继续上传");
                //删除已签收的单号
//                for (NotifyInfo info : signedList) {
//                    E3SysManager.deletePic(info.getPicPath());
//                }
//                adapter.getList().removeAll(signedList);
//                E3OrderDAO.deleteCacheOrders(infoToOrder(signedList, 1, 1));
            }
            dialog.setNegativeButtonTitle("返回查看");
            dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {
                @Override
                public void onClick() {
                    if (!isFinishing())
                        dialog.dismiss();
                    signCheck = true;
                    //删除已签收的单号
                    array.removeAll(signedList);
                    adapter.getList().removeAll(signedList);
                    adapter.notifyDataSetChanged();
                    E3OrderDAO.deleteCacheOrders(infoToOrder(signedList, 1, 1));
                    if ("save".equals(action)) {
                        saveData(array);
                    } else if ("upload".equals(action)) {
                        uploadDatas(array, null);
                    }
                    signedList.clear();
                }
            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    if (!isFinishing())
                        dialog.dismiss();
                    signCheck = false;
                    signedList.clear();
                }
            });
            if (!isFinishing())
                dialog.showDialog();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (resultCode == TACKE_PIC_RESPONSE_CODE) {
            List<NotifyInfo> picWayBills = (List<NotifyInfo>) data.getSerializableExtra("picWayBills");
            for (int i = 0; i < picWayBills.size(); i++) {
                for (int j = 0; j < adapter.getList().size(); j++) {
                    if (picWayBills.get(i).getExpress_number().equals(adapter.getList().get(j).getExpress_number())) {
                        adapter.getList().get(j).setPicPath(picWayBills.get(i).getPicPath());
                        cacheData(adapter.getList().get(j));
                        break;
                    }
                }
            }
            adapter.setCheckCount(0);
            adapter.notifyDataSetChanged();
        } else if (requestCode == ADD_WEIGHT_RESPONSE_CODE) {
            if (resultCode == RESULT_OK) {
                List<NotifyInfo> checkList = (List<NotifyInfo>) data.getSerializableExtra("dataList");
                for (int i = 0, j = adapter.getCheckedList().size(); i < j; i++) {
                    adapter.getCheckedList().get(i).setWeight(checkList.get(i).getWeight());
                    adapter.getCheckedList().get(i).setResType(checkList.get(i).getResType());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, final String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (sname) {
            case "prev_next_site":
                // 上一站
                if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
                    NotifyInfo noti;
                    upsite_list = new ArrayList<>();
                    Map<String, String> up_map = new HashMap<>();
                    try {
                        JSONArray arry = result.getJSONArray("retArr");
                        for (int i = 0; i < arry.length(); i++) {
                            noti = new NotifyInfo();
                            JSONObject object = arry.getJSONObject(i);
                            noti.setStation_name(object.optString("site_code"));
                            noti.setStation_no(object.optString("site_name"));
                            upsite_list.add(noti);
                            up_map.put(object.optString("site_name"), object.optString("site_code"));
                        } // 把站点缓存在本地spf中，以map集合的形式储存
                        SkuaidiSpf.SaveUpStation(context, up_map);
                        SKuaidiApplication.getInstance().postMsg("type_manager", "up_site", upsite_list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {// 下一站
                    NotifyInfo noti;
                    nextsite_list = new ArrayList<>();
                    Map<String, String> next_map = new HashMap<>();
                    try {
                        JSONArray arry = result.getJSONArray("retArr");
                        for (int i = 0; i < arry.length(); i++) {

                            noti = new NotifyInfo();
                            JSONObject object = arry.getJSONObject(i);
                            noti.setStation_name(object.optString("site_code"));
                            noti.setStation_no(object.optString("site_name"));
                            nextsite_list.add(noti);
                            next_map.put(object.optString("site_name"), object.optString("site_code"));
                        }
                        // 把站点缓存在本地spf中，以map集合的形式储存
                        SkuaidiSpf.SaveNextStation(context, next_map);
                        SKuaidiApplication.getInstance().postMsg("type_manager", "next_site", nextsite_list);
                        adapter.notifyTypes();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case E3SysManager.SCAN_TO_E3_V2:
            case E3SysManager.SCAN_TO_ZT_V2:
                ResponseData responseData = JSON.parseObject(json, ResponseData.class);
                if (responseData.getCode() != 0) {
                    dismissProgressDialog();//this);
                    alert(responseData.getDesc());
                    return;
                }
                UploadResutl uploadResutl = JSON.parseObject(responseData.getResult(), UploadResutl.class);
                if (uploadResutl != null) {
                    List<UploadResutl.ErrorBean> errorBeanList = uploadResutl.getError();
                    if (errorBeanList != null && errorBeanList.size() != 0) {
                        for (UploadResutl.ErrorBean error : errorBeanList) {
                            for (NotifyInfo info : adapter.getList()) {
                                if (info.getExpress_number().equals(error.getWaybillNo())) {
                                    info.setError(true);
                                    info.setErrorMsg(error.getReason());
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    successList = uploadResutl.getSuccess();
                    allSuccessList.addAll(successList);
                }
                if (signed_pic) {// 图片签收
                    for (int i = 0; i < picSignInfos.size(); i++) {
                        NotifyInfo notifyInfo = picSignInfos.get(i);
                        if (successList != null && !successList.contains(notifyInfo.getExpress_number()))
                            continue;//失败的单号不处理

                        if (picPathList.size() == 1) {
                            E3SysManager.deletePic(notifyInfo.getPicPath());
                        } else {
                            picPathList.remove(notifyInfo.getPicPath());
                            if (!picPathList.contains(notifyInfo.getPicPath())) {
                                // 上传成功，删除图片
                                E3SysManager.deletePic(notifyInfo.getPicPath());
                            }
                        }
                        // 上传成功，清楚缓存
                        E3OrderDAO.deleteCacheOrders(infoToOrder(Collections.singletonList(notifyInfo), 1, 1));
                        E3OrderDAO.addOrders(infoToOrder(Collections.singletonList(notifyInfo), 1, 0), mUserInfo.getExpressNo(), courierNO);

                        usualInfos.remove(notifyInfo);
                        adapter.getList().remove(notifyInfo);
                        successList.remove(notifyInfo.getExpress_number());
                    }
                    for (String number : successList) {
                        for (NotifyInfo info : adapter.getList()) {
                            if (info.getExpress_number().equals(number)) {
                                usualInfos.remove(info);
                                adapter.getList().remove(info);
                                E3OrderDAO.deleteCacheOrders(infoToOrder(Collections.singletonList(info), 1, 1));
                                E3OrderDAO.addOrders(infoToOrder(Collections.singletonList(info), 1, 0), mUserInfo.getExpressNo(), courierNO);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

//                if (adapter.getCount() > 0 && interceptInfos.size() == 0) {
                    if (adapter.getCheckedUploadAbleList().size() > 0) {
                        picSignInfos.clear();
                        uploadDatas(adapter.getCheckedUploadAbleList(), null);
                        return;// 这里返回是为了不执行取消progressDialog 的逻辑
                    }
                } else {

                    for (NotifyInfo info : usualInfos) {
                        if (!successList.contains(info.getExpress_number())) continue;//失败的单号不处理

                        // 上传成功后保存在数据库，当天去重检查用。
                        E3OrderDAO.addOrders(infoToOrder(Collections.singletonList(info), 1, 0), mUserInfo.getExpressNo(), courierNO);
                        E3OrderDAO.deleteCacheOrders(infoToOrder(Collections.singletonList(info), 1, 1));

                        adapter.getList().remove(info);
                    }
                    adapter.notifyDataSetChanged();

                }
                if ("EThreeInterfaceActivity".equals(getIntent().getStringExtra("from"))) {
                    if (EThreeInterfaceActivity.activityList.size() > 0) {
                        for (Activity activity : EThreeInterfaceActivity.activityList) {
                            activity.finish();
                        }
                        EThreeInterfaceActivity.activityList.clear();
                    }

                }

                handleNotify();
                if (adapter.getList().size() == 0) {
                    UtilToolkit.showToast("上传成功!");
                    finish();
                } else {
                    SkuaidiDialogGrayStyle sdialog = new SkuaidiDialogGrayStyle(context);
                    sdialog.setTitleGray("温馨提示");
                    sdialog.setContentGray(responseData.getDesc());
                    sdialog.isUseMiddleBtnStyle(true);
                    sdialog.setMiddleButtonTextGray("我知道了");
                    sdialog.showDialogGray(lv.getRootView());
                }


                break;
            case E3SysManager.SCAN_TO_QF:
                ArrayList<NotifyInfo> ordersInfos = new ArrayList<>();// 上传成功的单号
                try {
                    for (int i = usualInfos.size() - 1; i >= 0; i--) {
                        if (result.optJSONObject(usualInfos.get(i).getExpress_number()) == null) {// 其他异常
                            if ("fail".equals(result.optString("status"))) {
                                UtilToolkit.showToast(result.optString("desc"));
                                dismissProgressDialog();
                                return;
                            }
                        }
                        NotifyInfo ordersInfo;
                        String status = result.optJSONObject(usualInfos.get(i).getExpress_number()).optString("status");
                        if ("success".equals(status)) {
                            ordersInfo = usualInfos.get(i);
                            ordersInfos.add(ordersInfo);
                            // 上传成功，清楚缓存
                            E3OrderDAO.deleteCacheOrders(infoToOrder(Collections.singletonList(ordersInfo), 1, 1));
                            E3OrderDAO.addOrders(infoToOrder(Collections.singletonList(ordersInfo), 1, 0), mUserInfo.getExpressNo(), courierNO);// 保存
                            numbers_qf.add(ordersInfo.getExpress_number());
                            usualInfos.remove(ordersInfo);
                            adapter.getList().remove(ordersInfo);

                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (usualInfos.size() == 0) {
                        UtilToolkit.showToast("提交成功！");
                        if (sendSMS) {
                            sendSMS = false;
                            Intent intent = new Intent(context, SendMSGActivity.class);
                            String[] array = new String[numbers_qf.size()];
                            intent.putExtra("orderNumbers", numbers_qf.toArray(array));
                            startActivity(intent);
                        } else if (sendYunSMS) {
                            sendYunSMS = false;
                            Intent intent = new Intent(context, SendYunHuActivity.class);
                            String[] array = new String[numbers_qf.size()];
                            intent.putExtra("orderNumbers", numbers_qf.toArray(array));
                            startActivity(intent);
                        }
                        if (adapter.getList().size() == 0)
                            finish();
                    } else if (ordersInfos.size() > 0 && usualInfos.size() > 0) {
                        UtilToolkit.showToast("部分提交失败！");

                    } else {
                        UtilToolkit.showToast("提交失败！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "e3user.get":
                try {
                    JSONObject data = result.optJSONObject("retArr");
                    dialog.setCourierName(data.optString("cm_name"));
                    dialog.setCourierLatticepoint(data.optString("shop_name"));
                    String cm_code = data.optString("cm_code");
                    int length = cm_code.length();
                    if (length >= 4) {
                        String code_to_show = cm_code.substring(length - 4, length);// 只显示工号后4位
                        editText.setText(code_to_show);
                        editText.setSelection(code_to_show.length());
                    }
                    dialog.setCourierNum(cm_code);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "qfuser.get":
                try {
                    JSONObject data = result.optJSONObject("retArr");
                    dialog.setCourierName(data.optString("cm_name"));
                    dialog.setCourierLatticepoint(data.optString("shop_name"));
                    String cm_code = data.optString("cm_code");
                    dialog.setCourierNum(cm_code);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "ztuser.get":
                try {
                    JSONObject data = result.optJSONObject("retArr");
                    dialog.setCourierName(data.optString("cm_name"));
                    dialog.setCourierLatticepoint(data.optString("shop_name"));
                    String cm_code = data.optString("cm_code");
                    dialog.setCourierNum(cm_code);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log.w("iii", e.getMessage());
                }

                break;
            case "express.end.status":
                if (!TextUtils.isEmpty(result.toString())) {

                    if (numberList.size() == 1) {
                        try {
                            String status = result.optJSONObject("retArr").optJSONObject(numberList.get(0))
                                    .optString("express_status");
                            statusList.add(status);
                        } catch (Exception e) {
                            e.printStackTrace();
                            dismissProgressDialog();
                            return;
                        }

                    } else {
                        for (int i = 0; i < numberList.size(); i++) {
                            try {
                                String status = "";
                                JSONObject jso = result.optJSONObject("retArr").optJSONObject(numberList.get(i));
                                if (jso != null) {
                                    status = jso.optString("express_status");
                                }

                                statusList.add(status);
                            } catch (Exception e) {
                                e.printStackTrace();
                                dismissProgressDialog();
                                return;
                            }

                        }
                    }
                    if (list != null) {
                        for (int i = 0; i < statusList.size(); i++) {
                            try {
                                if (!TextUtils.isEmpty(statusList.get(i)) && !"null".equals(statusList.get(i)))
                                    list.get(i).setStatus(statusList.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    adapter.notifyDataSetChanged();

                }
                break;
            case ZT_THIRD_PARTY_BRANCH_GET_BRANCH:
                hallList = JSON.parseArray(json, BusinessHall.class);
                if (hallList != null && hallList.size() != 0) {
                    SkuaidiSpf.setZTBusinessHall(hallList);
                }
                break;
            case GET_ADDRESS:
                new AsyncTask<Void, Void, Void>() {
                    String jsonString;

                    @Override
                    protected void onPreExecute() {
                        jsonString = json;
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        addressList = JSON.parseArray(jsonString, DispatchActivity.AddressInfo.class);
                        if (addressList == null) return null;
                        try {
                            for (DispatchActivity.AddressInfo address : addressList) {
                                List<NotifyInfo> mList = adapter.getList();
                                for (NotifyInfo info : mList) {
                                    if (address.getWaybillNo().equals(info.getExpress_number())) {
                                        if (address.getInfo() != null && !TextUtils.isEmpty(address.getInfo().getName())) {
                                            info.setWayBillTypeForE3(address.getInfo().getName());
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                        }
                        return null;

                    }

                    protected void onPostExecute(Void result) {
                        adapter.notifyDataSetChanged();
                    }

                }.execute();

        }
        dismissProgressDialog();
    }

    /**
     * 上传成功后发短信或者云呼
     */
    private void handleNotify() {
        if (sendSMS || sendYunSMS) {// 同时发短信
            String[] numbers = new String[allSuccessList.size()];
            for (int i = 0; i < allSuccessList.size(); i++) {
                numbers[i] = allSuccessList.get(i);
            }
            Intent intent = new Intent();
            if (sendSMS) {
                sendSMS = false;
                intent = new Intent(context, SendMSGActivity.class);
            } else if (sendYunSMS) {// 同时云呼
                sendYunSMS = false;
                intent = new Intent(context, SendYunHuActivity.class);
            }
            intent.putExtra("orderNumbers", numbers);
            startActivity(intent);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        // 接口请求失败，拿上一站的本地缓存
        if (sname.equals("prev_next_site")) {
            if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
                upmap = SkuaidiSpf.getUpstation(context);
                Set<String> keySet = upmap.keySet();
                upsite_list = new ArrayList<>();
                for (String key : keySet) {
                    notifyInfo = new NotifyInfo();
                    String value = upmap.get(key);
                    notifyInfo.setStation_no(key);
                    notifyInfo.setStation_name(value);
                    upsite_list.add(notifyInfo);
                }
                SKuaidiApplication.getInstance().postMsg("type_manager", "up_site", upsite_list);
            } else {
                nextmap = SkuaidiSpf.getNextstation(context);
                // 服务器没获取到数据
                // 拿本地缓存
                Set<String> keySet = nextmap.keySet();
                nextsite_list = new ArrayList<>();
                for (String key : keySet) {
                    notifyInfo = new NotifyInfo();
                    String value = nextmap.get(key);
                    notifyInfo.setStation_no(key);
                    notifyInfo.setStation_name(value);
                    nextsite_list.add(notifyInfo);
                }
                // 发给界面做显示
                SKuaidiApplication.getInstance().postMsg("type_manager", "next_site", nextsite_list);
            }
        }
        if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_QF.equals(sname)
                || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            if (E3SysManager.SCAN_TO_E3_V2.equals(sname) && sendSMS)
                sendSMS = false;
            if (E3SysManager.SCAN_TO_E3_V2.equals(sname) && sendYunSMS)
                sendYunSMS = false;
            UtilToolkit.showToast(result);
        } else if (sname.contains("user.get")) {
            dialog.setCourierNum("");
            dialog.setCourierName("");
            dialog.setCourierLatticepoint("");// 全部置空
            dialog.setDesignatedPersonnelHint(result);
            if (!isFinishing())
                dialog.showDialog();
        }
        dismissProgressDialog();

    }

    /**
     * 忽略错误单号提示框
     */
    private void showIgnoreDialog() {
        dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, new View(context));
        dialog.setTitle("上传提醒");
        String hint = "您的扫描列表中有错误单号";
        SpannableStringBuilder ss = new SpannableStringBuilder(hint);
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red_f74739)), hint.indexOf("错误"),
                hint.indexOf("错误") + "错误".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialog.setCommonContent(ss);
        dialog.setPositiveButtonTitle("仅上传正确");
        dialog.setNegativeButtonTitle("取消");
        dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

            @Override
            public void onClick() {
                List<NotifyInfo> infos = new ArrayList<>();
                infos.addAll(usualInfos);
                uploadDatas(infos, null);
                dialog.dismiss();
            }
        });
        if (!isFinishing())
            dialog.showDialog();
    }

    /**
     * 中通，包含拦截件提示框
     */
    private void showInterceptDialog(String retInterceptMsg, final List<NotifyInfo> datas) {
        playInterceptTone();// 播放提示音
        dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, new View(context));
        dialog.setTitle("快件拦截提醒");
        dialog.setCommonContent(retInterceptMsg);
        dialog.setPositiveButtonTitle("退件上传");
        String type;
        if (scanType.contains("扫")) {
            type = scanType.substring(1);
        } else {
            type = scanType;
        }
        dialog.setNegativeButtonTitle("继续上传" + type);
        dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

            @Override
            public void onClick() {// 上传退件
                List<NotifyInfo> infos = new ArrayList<>();
                infos.addAll(datas);
//                interceptInfos.clear();
                uploadDatas(infos, "退件");
                dialog.dismiss();
            }
        });
        dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {// 继续上传

            @Override
            public void onClick(View v) {
                forceIntercept = 1;// 不检查退件
                List<NotifyInfo> infos = new ArrayList<>();
                infos.addAll(datas);
//                interceptInfos.clear();
                uploadDatas(infos, null);
                dialog.dismiss();
            }
        });
        if (!isFinishing())
            dialog.showDialog();
    }

    private final EditTextMaxLengthListener editTextMaxLengthListener = new EditTextMaxLengthListener() {

        @Override
        public void onEditTextMaxLength(SkuaidiE3SysDialog e3SysDialog, EditText edit, String content) {
            dialog = e3SysDialog;
            editText = edit;
            JSONObject data = new JSONObject();
            try {
                if (BRAND_STO.equals(mUserInfo.getExpressNo())) {
                    data.put("sname", "e3user.get");
                } else if (BRAND_QF.equals(mUserInfo.getExpressNo())) {
                    data.put("sname", "qfuser.get");
                } else if (BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                    data.put("sname", "ztuser.get");
                }
                data.put("empcode", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
        }
    };

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("expressfirmName", mUserInfo.getExpressFirm());
        intent.putExtra("express_no", mUserInfo.getExpressNo());
        intent.putExtra("order_number", list.get(position).getExpress_number());
        intent.setClass(context, CopyOfFindExpressResultActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onScanResult(String code) {
        KLog.i("kb", "code:--->" + code);
//        if (!(code.length() > 11 && code.length() < 14) && BRAND_STO.equals(mUserInfo.getExpressNo())) {
//            UtilToolkit.showToast("非申通条码");
//            return;
//        }
        if (E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
            if (!E3SysManager.isSTOWaybillNo(code)) {
                playErrorSounds(0);
                UtilToolkit.showToast("非申通条码");

                return;
            }
        } else if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) {
            if (!E3SysManager.isZTWaybillNo(code)) {
                playErrorSounds(0);
                UtilToolkit.showToast("非中通条码");
                return;
            }
        }
        if (numberList.size() == E3ScanActivity.MAX_SCAN_COUNT) {
            UtilToolkit.showToast("亲，一次最多只能扫描" + E3ScanActivity.MAX_SCAN_COUNT + "条哦^_^");

            return;
        }
        for (int i = 0; i < numberList.size(); i++) {
            if (numberList.get(i).equals(code)) {
                UtilToolkit.showToast("重复扫描");
                playErrorSounds(0);
                return;
            }
        }
        playSounds(0);
        NotifyInfo info = new NotifyInfo();
        info.setExpress_number(code);
        info.setRemarks("");
        info.setStatus("");
        info.setScanTime(E3SysManager.getTimeBrandIndentify());
        switch (scanType) {
            case E3SysManager.SCAN_TYPE_LANPICE: // 收件
                info.setCourierJobNO(reviewInfo.getCourierJobNo());
                info.setWayBillTypeForE3(reviewInfo.getCourierName());
                break;
            case E3SysManager.SCAN_TYPE_FAPICE: // 发件
                info.setCourierJobNO(reviewInfo.getCourierJobNo());
                info.setWayBillTypeForE3(reviewInfo.getCourierName());
                break;
            case E3SysManager.SCAN_TYPE_DAOPICE: // 到件

                break;
            case E3SysManager.SCAN_TYPE_PIEPICE: // 派件
                info.setCourierJobNO(reviewInfo.getCourierJobNo());
                info.setWayBillTypeForE3(reviewInfo.getCourierName());
                break;
            case E3SysManager.SCAN_TYPE_BADPICE: // 问题件

                break;
            case E3SysManager.SCAN_TYPE_SIGNEDPICE: // 签收件

                break;
        }
        list.add(info);
        getScanRecordsDatas(list);
        adapter.notifyDataSetChanged();
        lv.smoothScrollToPosition(adapter.getCount() - 1);
    }

    /**
     * 传入一个站点名称，返回一个站点编号
     *
     * @param key_str 站点名
     * @return 站点编号
     */

    private String getSiteCode(String key_str) {
        nextmap = new HashMap<>();
        String site_code = "";
        if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
            nextmap = SkuaidiSpf.getUpstation(context);
        } else {
            nextmap = SkuaidiSpf.getNextstation(context);
        }
        Set<String> keySet = nextmap.keySet();
        for (String key : keySet) {
            if (key.equals(key_str)) {
                site_code = nextmap.get(key);
            }
        }
        return site_code;
    }

    private void resetVariable() {
        signed_pic = false;
        picSignInfos.clear();
    }

    /**
     * 缓存单号
     *
     * @param info 运单
     */
    public void cacheData(NotifyInfo info) {
        List<NotifyInfo> infos = new ArrayList<>();
        infos.add(info);
        ArrayList<E3_order> orders = infoToOrder(infos, 0, 1);
        E3OrderDAO.cacheOrders(orders, mUserInfo.getExpressNo(), courierNO);
    }

    @Override
    public void onClick(View v) {
        allSuccessList.clear();
        switch (v.getId()) {
            case R.id.btn_paizhao://上传同时发短信
                UMShareManager.onEvent(context, "upload_send_sms_qrcode", "upload_send_sms", "问题件:上传同时发短信");
                sendYunSMS = true;
                upload(v);
                mPopupWindow.dismiss();
                break;
            case R.id.btn_xiangce://上传同时云呼
                UMShareManager.onEvent(context, "upload_call_qrcode", "upload_call", "问题件:上传同时云呼");
                sendSMS = true;
                upload(v);
                mPopupWindow.dismiss();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    public void hideKeyboard() {
        edt_scanInput.clearFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(edt_scanInput.getWindowToken(), 0);
    }


    /**
     * 播放拦截件提示音
     */
    private void playInterceptTone() {
        ringtone.play();
    }

    /**
     * 扫单提示铃声初始化
     */
    private void initBeepSound() {
        Uri soundURI = UtilToolkit.getResourceUri(context.getApplicationContext(), R.raw.beep);
        ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), soundURI);
        if (ringtone != null) {
            ringtone.setStreamType(AudioManager.STREAM_MUSIC);
            ringtone_initialized = true;
        }
    }

    /**
     * 播放扫单提示音
     */
    private void playBeepTone() {
        if (ringtone != null)
            ringtone.play();
    }

    /**
     * 扫描单号重复提示铃声初始化
     */
    private void initRepeatedTone() {
        Uri soundURI = UtilToolkit.getResourceUri(context.getApplicationContext(), R.raw.scan_required);
        repeatedRingtone = RingtoneManager.getRingtone(context.getApplicationContext(), soundURI);
        if (repeatedRingtone != null)
            repeatedRingtone.setStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * 扫描单号重复提示铃声播放
     */
    private void playRepeatedTone() {
        repeatedRingtone.play();
    }

    private void alert(String errorMsg) {
        dialog = new SkuaidiE3SysDialog(this, SkuaidiE3SysDialog.TYPE_COMMON, new View(this));
        dialog.setTitle("上传提醒");
        dialog.setCommonContent(errorMsg);
        dialog.setSingleButtonTitle("确定");
        dialog.isUseSingleButton(true);
        dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        if (!isFinishing())
            dialog.showDialog();
    }

    /**
     * 显示数量统计
     *
     * @param count
     */
    public void showCount(int count) {
        tv_count.setText(String.format(getResources().getString(R.string.waybill_count), count));
    }

    @Subscribe
    public void onEvent(final List<NotifyInfo> infoList) {
        switch (scanType) {
            case E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY:
                showBusinessHallDialog(infoList);
                break;
            case E3SysManager.SCAN_TYPE_SIGNEDPICE:
                showSignTypeDialog(infoList);
                break;
        }

    }

    private int checkedPosition = -1;

    /**
     * 中通选签收人
     *
     * @param infoList
     */
    private void showSignTypeDialog(final List<NotifyInfo> infoList) {
        CustomDialog customDialog = null;
        CustomDialog.Builder dialogBuilder = new CustomDialog.Builder(context);
        View layoutView = LayoutInflater.from(context).inflate(R.layout.layout_zt_sign_type, null);
        final ExpandableStickyListHeadersListView listView = (ExpandableStickyListHeadersListView) layoutView.findViewById(R.id.listView);

        final ZTSignAdapter signAdapter = new ZTSignAdapter(context, ztSignTypes);
        listView.setAdapter(signAdapter);
        int checkPosition = signAdapter.getDataList().indexOf(SkuaidiSpf.getLatestSignTypeZT(courierNO));
        for (int i = 0, j = signAdapter.getTitleList().size(); i < j; i++) {
            listView.collapse(i);
        }
        if (checkPosition != -1) {
            listView.setItemChecked(checkPosition, true);
            listView.expand(signAdapter.getHeaderId(checkPosition));
            listView.smoothScrollToPosition(checkPosition);
        }
        if (!TextUtils.isEmpty(infoList.get(0).getWayBillTypeForE3())) {
            listView.getWrappedList().performItemClick(null, signAdapter.getSignTypes().indexOf(infoList.get(0).getWayBillTypeForE3()), signAdapter.getSignTypes().indexOf(infoList.get(0).getWayBillTypeForE3()));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkedPosition == listView.getCheckedItemPosition()) {
                    listView.setItemChecked(-1, true);
                    checkedPosition = -1;
                } else {
                    checkedPosition = position;

                }
            }
        });
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (listView.isHeaderCollapsed(headerId)) {
                    listView.expand(headerId);
                } else {
                    listView.collapse(headerId);
                }
                for (int i = 0, j = signAdapter.getTitleList().size(); i < j; i++) {
                    if (i != headerId) {
                        listView.collapse(i);
                    }
                }
                CheckBox cb = (CheckBox) header.findViewById(R.id.cb_check);
                cb.toggle();
            }
        });


        customDialog = dialogBuilder.setContentView(layoutView)
                .setTitle("选签收人")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确认", null)

                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (listView.getCheckedItemPosition() == -1) {
                            dialog.dismiss();
                            return;
                        }
                        String signType = signAdapter.getSignTypes().get(listView.getCheckedItemPosition());
                        SkuaidiSpf.saveLatestSignTypeZT(courierNO, signAdapter.getDataList().get(listView.getCheckedItemPosition()));
                        for (NotifyInfo info : infoList) {
//                            int index = adapter.getList().indexOf(info);
//                            adapter.getList().get(index).setWayBillTypeForE3(signType);
//                            //已经拍照的，删除照片
//                            if(!TextUtils.isEmpty(adapter.getList().get(index).getPicPath())){
//                                E3SysManager.deletePic(notifyInfo.getPicPath());
//                                adapter.getList().get(index).setPicPath( null);
//                            }

                            info.setWayBillTypeForE3(signType);

                            //已经拍照的，删除照片
                            boolean shouldDelete = true;
                            for (NotifyInfo info1 : adapter.getList()) {
                                if (!info1.getExpress_number().equals(info.getExpress_number()) && !TextUtils.isEmpty(info1.getPicPath()) && info1.getPicPath().equals(info.getPicPath())) {
                                    shouldDelete = false;
                                    break;
                                }
                            }
                            if (!TextUtils.isEmpty(info.getPicPath()) && shouldDelete) {
                                E3SysManager.deletePic(info.getPicPath());
                            }
                            info.setPicPath(null);
                        }

                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).create();
        customDialog.show();
        final CustomDialog finalCustomDialog = customDialog;
        ImageView ivCamerca = (ImageView) layoutView.findViewById(R.id.iv_camera);

        ivCamerca.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EThreeCameraActivity.class);
                intent.putExtra("wayBills", (Serializable) infoList);
                EthreeInfoScanActivity.this.startActivityForResult(intent,
                        EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                if (finalCustomDialog != null)
                    finalCustomDialog.dismiss();
            }
        });

    }

    /**
     * 中通第三方签收，选签收网点
     *
     * @param infoList
     */
    private void showBusinessHallDialog(final List<NotifyInfo> infoList) {
        if (hallList == null || hallList.size() == 0) {
            UtilToolkit.showToast("没有营业厅信息");
            return;
        }
        final BusinessHallAdapter hallAdapter = new BusinessHallAdapter(hallList);
        CustomDialog.Builder dialogBuilder = new CustomDialog.Builder(context);
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置布局宽高
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MeasureUtil.dip2px(context, 300)));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(this, R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin), getResources().getDimensionPixelSize(R.dimen.recyle_divider_rightmargin))
                .build());  //添加分割线
        recyclerView.setAdapter(hallAdapter);
        dialogBuilder.setContentView(recyclerView)
                .setTitle("选营业厅")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确认", null)

                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BusinessHall hall = hallAdapter.getCheckedHall();
                        if (hall == null) return;
                        for (NotifyInfo info : infoList) {
                            int index = adapter.getList().indexOf(info);
                            adapter.getList().get(index).setThirdBranch(hall.getName());
                            adapter.getList().get(index).setThirdBranchId(hall.getNo());
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void getBusinessHall() {
        if (!E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) return;
        JSONObject data = new JSONObject();
        try {
            data.put("sname", ZT_THIRD_PARTY_BRANCH_GET_BRANCH);

            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过单号获派件地址
     *
     * @param waybillNo 快递单号
     */
    private void getAddressByWaybillNo(List<String> waybillNo) {
        JSONObject data = new JSONObject();
        try {
            String numbers = "";
            for (int i = 0, j = waybillNo.size(); i < j; i++) {
                numbers += waybillNo.get(i) + ",";
            }
            if (TextUtils.isEmpty(numbers)) {
                return;
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("sname", GET_ADDRESS);
            data.put("appVersion", Utility.getVersionCode());
            data.put("waybillNo", numbers);//单号
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取中通签收类型
     */
    private List<ZTSignType> ztSignTypes;

    private void getSignType() {
        ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.getSignType().doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                int code = -1;
                String msg = "";
                if (throwable instanceof RetrofitUtil.APIException) {
                    RetrofitUtil.APIException exception = (RetrofitUtil.APIException) throwable;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                    code = exception.code;
                    msg = exception.msg;
                }
            }
        }).subscribe(newSubscriber(new Action1<List<ZTSignType>>() {
                                       @Override
                                       public void call(List<ZTSignType> ztSignTypes) {
                                           if (ztSignTypes != null && ztSignTypes.size() != 0) {
                                               EthreeInfoScanActivity.this.ztSignTypes = ztSignTypes;
                                               SkuaidiSpf.setZTSignTypes(ztSignTypes);
                                           }
                                       }
                                   }
        ));
        mCompositeSubscription.add(subscription);


    }
}
