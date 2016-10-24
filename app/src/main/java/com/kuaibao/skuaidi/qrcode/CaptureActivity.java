package com.kuaibao.skuaidi.qrcode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.FindExpressHistoryActivity;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.activity.adapter.ContinuousScanAdapter;
import com.kuaibao.skuaidi.activity.adapter.ContinuousScanAdapter.PicViewClickListener;
import com.kuaibao.skuaidi.activity.make.realname.CollectionDetailActivity;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.scan_mobile.GunScanActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.view.ViewfinderView;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.activity.DispatchSearchActivity;
import com.kuaibao.skuaidi.entry.CloudVoiceMsgDetailEntry;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.litepal.entry.Number;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.camera.CameraManager;
import com.kuaibao.skuaidi.qrcode.decoding.CaptureActivityHandler;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeShowScanWaybillPicActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.MyLocationManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 派件拍照界面.
 *
 * @author kb38
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CaptureActivity extends SkuaiDiBaseActivity implements Callback {
    /**
     * 是否播放重复提示音，只有添加进扫描列表，并且需要做重复检查，并且检查发现重复的情况需要提示
     **/
    public boolean playRepeatedTone = false;
    private int MAX_COUNT = 100;// 最多可扫描单号条数
    /**
     * 收款后上传单号
     */
    private static final String UPLOAD_EXPRESS_ADD = "upload_express.add";
    protected static final String EDITTEXT_DIGITS = ",abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789-";
    Context context;
    public static final int TACKE_PIC_RESPONSE_CODE = 702;
    protected CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;

    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private TextView tv_scan;
    protected TextView tv_cap_finish, tv_flashlight1;
    TextView tv_flashlight_top;
    protected ListView mListview;
    private TextView tv_flashlight;
    protected boolean isContinuous = true;
    private RelativeLayout more_scan;
    protected RelativeLayout rl_input1, rl_input2, rl_input3, rl_input_bluetooth;
    private ImageView ivflashlight, iv_scan;
    private boolean light = false;
    private int orderNum = 0;
    protected CourierReviewInfo courierReviewInfo;
    private MyBoradCastReceiver myBoradCastReceiver;
    private boolean dialog_show = false;
    private String scanType = null;

    private Map<String, Boolean> wayBillsCache = new HashMap<>();
    private Ringtone ringtone;
    private boolean isLightOn = false;// 闪光灯是否打开
    ContinuousScanAdapter mAdapter;

    NotifyInfo scan;
    protected List<NotifyInfo> mList;

    String deliverNO;
    SkuaidiDB skuaidiDb;
    String code;
    String expressFirmName, express;
    String inType = "";
    public String courierNO = "";
    public String company = "";

    private int captureType = 0;
    // 条形码扫描
    private static final int INTERFACE_VERSION_NEW = 2;
    /**
     * 扫完即拍
     */
    protected boolean pic_signed = false;// 签收扫描，扫描一次拍一次
    /**
     * 删除错扫
     */
    protected boolean e3ScanDelete = false;
    // 收款扫单右上角的完成按钮
    SkuaidiTextView tv_finish;

    private NotifyInfo toUploadInfo;
    private boolean uploading = false;// 正在上传数据
    private MyLocationManager mLocationManager;
    private String from;

    /**
     * 已经上传成功的数据
     */
    protected ArrayList<NotifyInfo> uploadedInfos = new ArrayList<>();

    public static final HashMap<String, String> scanToTypeMap = new HashMap<>();

    static {
        scanToTypeMap.put("扫收件", "收件");
        scanToTypeMap.put("扫发件", "发件");
        scanToTypeMap.put("扫到件", "到件");
        scanToTypeMap.put("扫派件", "派件");
        scanToTypeMap.put("扫签收", "签收件");
        scanToTypeMap.put("问题件", "问题件");
    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SCAN_DEL:// 删除扫描的单号-通过删除按钮删除
                    skuaidiDb.deleteOrder(msg.obj.toString());
                    wayBillsCache.remove(msg.obj.toString());
                    DataSupport.deleteAll(Number.class,"orderNo=?",msg.obj.toString());
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getCount() == 0) {
                        setFinishViewVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
    private boolean isRequesting = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        skuaidiDb = SkuaidiDB.getInstanse(context);
        Connector.getDatabase();
        Intent intent = getIntent();
        scanType = intent.getStringExtra("scanType");
        captureType = intent.getIntExtra("qrcodetype", 0);
        MAX_COUNT = getIntent().getIntExtra("scanMaxSize", MAX_COUNT);// 获取最大可以输入的单号条数
        from = intent.getStringExtra("from");
        isContinuous = intent.getBooleanExtra("isContinuous", false);
        inType = intent.getStringExtra("inType");

        courierReviewInfo = E3SysManager.getReviewInfo();
        courierNO = courierReviewInfo.getCourierJobNo();//快递员工号
        company = SkuaidiSpf.getLoginUser().getExpressNo();// 公司代号

        e3ScanDelete = false;
        if (!TextUtils.isEmpty(scanType)) {// 巴枪扫描
            // 如果是申通的扫签收
            if ((E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) || E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) && !"qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                setContentView(R.layout.activity_signed_scan);
                if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                    findViewById(R.id.rl_input2).setVisibility(View.GONE);
                }
            } else {
                setContentView(R.layout.activity_send_scan);
            }
        } else {
            if (captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {// 实名登记界面
                setContentView(R.layout.activity_collection_add_express_no);
                tv_finish = ((SkuaidiTextView) findViewById(R.id.tv_more));
                tv_finish.setVisibility(View.GONE);
                tv_finish.setText("完成");
            } else if (captureType == Constants.TYPE_ORDER_ONE || captureType == Constants.TYPE_PAIJIAN || captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER || captureType == Constants.TYPE_DISPATCH) {
                setContentView(R.layout.activity_send_scan);
            } else {
                setContentView(R.layout.main);
            }
        }
        mList = new ArrayList<>();
        initRepeatedTone();// 初始化铃声管理
        findView();
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 初始化 CameraManager
        CameraManager.init(getApplication(), "CaptureActivity");

        if (captureType == Constants.TYPE_PAIJIAN
                || captureType == Constants.TYPE_PAIJIAN_ONE
                || captureType == Constants.TYPE_FIND_EXPRESS
                || captureType == Constants.TYPE_ORDER_ONE && !isContinuous
                || scanType != null
                || captureType == Constants.TYPE_COLLECTION
                || captureType == Constants.TYPE_KEEP_ACCOUNTS
                || captureType == Constants.TYPE_CREATE_LIUYAN
                || captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER
                || captureType == Constants.TYPE_DISPATCH
                || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST// 线下现金收款
                ) {
            more_scan.setVisibility(View.VISIBLE);
            if (captureType == Constants.TYPE_PAIJIAN
                    || captureType == Constants.TYPE_PAIJIAN_ONE
                    || captureType == Constants.TYPE_ORDER_ONE
                    || captureType == Constants.TYPE_KEEP_ACCOUNTS
                    || captureType == Constants.TYPE_CREATE_LIUYAN
                    || captureType == Constants.TYPE_DISPATCH) {// 如果是单条进入
                if (rl_input1 != null)
                    rl_input1.setVisibility(View.GONE);// 隐藏多条扫描按钮
                if (captureType == Constants.TYPE_DISPATCH && rl_input3 != null)
                    rl_input3.setVisibility(View.GONE);
            }
        } else {
            if (captureType == Constants.TYPE_DELIVER) {
                findViewById(R.id.rl_input1).setVisibility(View.GONE);
                findViewById(R.id.rl_input3).setVisibility(View.GONE);
            } else {
                more_scan.setVisibility(View.GONE);
            }
        }
        registerReceiver();
        hasSurface = false;
        if (!TextUtils.isEmpty(scanType)) {
            requestGps();
        }
    }

    private void findView() {
        rl_input_bluetooth = (RelativeLayout) findViewById(R.id.rl_input_bluetooth);// 扫描枪输入按钮
        if (rl_input_bluetooth != null){
            if (captureType != Constants.TYPE_PAIJIAN && Utility.isEmpty(scanType)){
                rl_input_bluetooth.setVisibility(View.GONE);
            }else{
                rl_input_bluetooth.setVisibility(View.VISIBLE);
            }
        }
        rl_input1 = (RelativeLayout) findViewById(R.id.rl_input1);
        rl_input2 = (RelativeLayout) findViewById(R.id.rl_input2);
        rl_input3 = (RelativeLayout) findViewById(R.id.rl_input3);
        tv_scan = (TextView) findViewById(R.id.tv_scan);
        ivflashlight = (ImageView) findViewById(R.id.ivflashlight);
        tv_flashlight1 = (TextView) findViewById(R.id.tv_flashlight1);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        tv_flashlight_top = (TextView) findViewById(R.id.tv_flashlight_top);
        tv_flashlight = (TextView) findViewById(R.id.tv_flashlight);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        mListview = (ListView) findViewById(R.id.uid_listview);
        more_scan = (RelativeLayout) findViewById(R.id.more_scan);
        tv_cap_finish = (TextView) findViewById(R.id.tv_cap_finish);

        ViewTreeObserver vto = mListview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mListview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                mListview.getLayoutParams().height = display.getHeight() / 2 - findViewById(R.id.ll_tips1).getLayoutParams().height;
                mListview.setLayoutParams(mListview.getLayoutParams());
            }
        });
        // 非E3入口进入时功能
        if (captureType == Constants.TYPE_PAIJIAN
                || captureType == Constants.TYPE_PAIJIAN_ONE
                || captureType == Constants.TYPE_FIND_EXPRESS
                || captureType == Constants.TYPE_ORDER_ONE
                || captureType == Constants.TYPE_COLLECTION
                || captureType == Constants.TYPE_CREATE_LIUYAN
                || captureType == Constants.TYPE_KEEP_ACCOUNTS
                || captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER//浙江地区实名登记入口
                || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST// 线下现金收款
                || captureType == Constants.TYPE_DISPATCH) {
            if (isContinuous) {
                if (tv_scan != null)
                    tv_scan.setText("关闭批量扫描");
                if (iv_scan != null && captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {
                    iv_scan.setBackgroundResource(R.drawable.more_scan_press);
                } else {
                    if (iv_scan != null)
                        iv_scan.setBackgroundResource(R.drawable.more_scan_hover);
                }
                // 以下判断针对记账扫码功能
                if (iv_scan != null && captureType == Constants.TYPE_KEEP_ACCOUNTS && "keep_accounts".equals(from)) {
                    iv_scan.setBackgroundResource(R.drawable.more_scan_press);
                } else {
                    if (iv_scan != null)
                        iv_scan.setBackgroundResource(R.drawable.more_scan_hover);
                }
            } else {
                if (iv_scan != null && captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {
                    iv_scan.setBackgroundResource(R.drawable.more_scan_normal);
                } else {
                    if (iv_scan != null)
                        iv_scan.setBackgroundResource(R.drawable.more_scan);
                }
                if (tv_scan != null)
                    tv_scan.setText("打开批量扫描");

                // 以下判断针对记账扫码功能
                if (iv_scan != null && captureType == Constants.TYPE_KEEP_ACCOUNTS && "keep_accounts".equals(from)) {
                    iv_scan.setBackgroundResource(R.drawable.more_scan_normal);
                } else {
                    if (iv_scan != null) {
                        iv_scan.setBackgroundResource(R.drawable.more_scan);
                    }
                }
            }
        }
        getNumbers();// 操作litepal.db
        scan = new NotifyInfo();
        if (captureType == Constants.TYPE_PAIJIAN
                || captureType == Constants.TYPE_PAIJIAN_ONE
                || captureType == Constants.TYPE_FIND_EXPRESS
                || captureType == Constants.TYPE_ORDER_ONE
                || captureType == Constants.TYPE_COLLECTION
                || captureType == Constants.TYPE_CREATE_LIUYAN
                || captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER//浙江地区实名登记入口
                || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST// 线下现金收款
                || captureType == Constants.TYPE_KEEP_ACCOUNTS) {

            mAdapter = new ContinuousScanAdapter(context, mhandler, mList, captureType, from);
        } else {
            mAdapter = new ContinuousScanAdapter(context, mhandler, mList, scanType);
            if (null != scanType) {
                if (scanType.equals(E3SysManager.SCAN_TYPE_SIGNEDPICE)) {
                    mAdapter.setPicViewClickListener(new PicViewClickListener() {

                        @Override
                        public void onClick(View v, int position) {
                            NotifyInfo info = mList.get(position);
                            Intent intent = new Intent(context, EThreeShowScanWaybillPicActivity.class);
                            intent.putExtra("wayBillNo", info.getExpress_number());
                            intent.putExtra("picPath", info.getPicPath());
                            context.startActivity(intent);
                        }
                    });
                }
            }
        }
        mListview.setAdapter(mAdapter);
        notifyDataChangedByPosition();
        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType) && "zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            photographSwitch(findViewById(R.id.btn_shutter));
        }

    }

    // 是否需要从litepal.db中获取扫单过程中保存的数据
    private void getNumbers(){
        if (captureType == Constants.TYPE_PAIJIAN){
            List<Number> numbers = getNumberList();
            for (int i = 0;i<numbers.size();i++){
                wayBillsCache.put(numbers.get(i).getOrderNo(),true);
                NotifyInfo info = new NotifyInfo();
                info.setExpress_number(numbers.get(i).getOrderNo());
                mList.add(info);
            }
        }
    }

    // 从litepal.db中获取扫单过程中保存的单号数据
    private List<Number> getNumberList(){
        return DataSupport.findAll(Number.class);
    }

    public void bluetoothInput(View view){
        UMShareManager.onEvent(this,"CaptureActivity_gun_scan","CaptureActivity","条码扫描：扫描枪输入");
        Intent i = new Intent(context, GunScanActivity.class);
        int position = getIntent().getIntExtra("listposition",-1);
        i.putExtra("listposition",position+1);
        i.putExtra("list", (Serializable) mList);
        List<String> no = new ArrayList<>();
        if (null != getIntent().getStringExtra("from") && getIntent().getStringExtra("from").equals("yunhu")){
            List<CloudVoiceMsgDetailEntry> c = (List<CloudVoiceMsgDetailEntry>) getIntent().getSerializableExtra("sendmsgInfos");
            for (int j = position;j<c.size();j++){
                no.add(c.get(j).getMobile_no());
            }
            i.putExtra("no", (Serializable) no);
        }else{
            List<NotifyInfo2> n = (List<NotifyInfo2>) getIntent().getSerializableExtra("sendmsgInfos");
            for (int j = position;j<n.size();j++){
                no.add(n.get(j).getExpressNo());
            }
            i.putExtra("no", (Serializable) no);
        }
        i.putExtra("from",getIntent().getStringExtra("from"));
        i.putExtra("inputMaxCount",MAX_COUNT);
//        startActivityForResult(i,10001);
        startActivity(i);
    }


    /**
     * 完成按钮
     *
     * @param view
     */
    public void finish(View view) {
        if (uploading) {
            UtilToolkit.showToast("正在上传数据，请稍后重试");
        } else {
            scanFinish();
        }
    }

    /**
     * 完成处理
     */
    public void scanFinish() {
        Intent intent;
        if (captureType == Constants.TYPE_FIND_EXPRESS) {
            intent = new Intent(CaptureActivity.this, FindExpressHistoryActivity.class);
            intent.putExtra("moreScan", true);
            intent.putExtra("express_list", (Serializable) mList);
            startActivity(intent);
        } else {
            if (captureType == Constants.TYPE_PAIJIAN) {
                String from = getIntent().getStringExtra("from");
                if (!Utility.isEmpty(from) && "yunhu".equals(from)) {// 发送云呼界面列表
                    List<CloudVoiceMsgDetailEntry> cloudInfos = new ArrayList<>();
                    for (int i = 0; i < mList.size(); i++) {
                        CloudVoiceMsgDetailEntry cloudinfo = new CloudVoiceMsgDetailEntry();
                        cloudinfo.setMobile(mList.get(i).getSender_mobile());
                        cloudinfo.setOrder_no(mList.get(i).getExpress_number());
                        cloudInfos.add(cloudinfo);
                    }
                    intent = new Intent();
                    intent.putExtra("cloudInfos", (Serializable) cloudInfos);
                } else {
                    List<NotifyInfo2> info2s = new ArrayList<>();
                    for (int i = 0; i < mList.size(); i++) {
                        NotifyInfo2 info2 = new NotifyInfo2();
                        info2.setExpress_number(mList.get(i).getExpress_number());
                        info2.setSender_mobile(mList.get(i).getSender_mobile());
                        info2.setSender_name(mList.get(i).getSender_name());
                        info2s.add(info2);
                    }
                    intent = new Intent();
                    intent.putExtra("notifyInfo2", (Serializable) info2s);
                }
                setResult(Constants.RESULT_NOTIFYDETAIL_CAPTURE, intent);
                // 删除扫描的单号
                DataSupport.deleteAll(Number.class);
                // 返回到订单
            } else if (captureType == Constants.TYPE_ORDER_ONE) {
                intent = new Intent();
                if (getIntent().hasExtra("isSto")) {
                    for (int i = 0; i < mList.size(); i++) {
                        if (i == 0) {
                            deliverNO = mList.get(i).getExpress_number();
                        } else {
                            deliverNO = deliverNO + "," + mList.get(i).getExpress_number();
                        }
                    }
                }
                intent.putExtra("diliverNo", deliverNO);
                setResult(Constants.TYPE_ORDER_ONE);
            } else if (captureType == Constants.TYPE_COLLECTION) {
                addCollectionExpress();
                return;
            } else if (captureType == Constants.TYPE_CREATE_LIUYAN) {
                intent = new Intent();
                intent.putExtra("decodestr", deliverNO);
                setResult(102, intent);
            } else if (captureType == Constants.TYPE_KEEP_ACCOUNTS) {// 针对记账扫码功能
                intent = new Intent();
                intent.putExtra("express_list", (Serializable) mList);
                setResult(Constants.TYPE_SCAN_ORDER_RESULT, intent);
            } else {
                intent = new Intent();
                intent.putExtra("express_list", (Serializable) mList);
                setResult(RESULT_OK, intent);
            }
        }
        finish();
    }

    /**
     * 收款上传快递单号【接口】
     */
    public void addCollectionExpress() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", UPLOAD_EXPRESS_ADD);
            data.put("order_number", getIntent().getStringExtra("order_number"));
            String numbers = "";
            for (int i = 0, j = mList.size(); i < j; i++) {
                numbers += mList.get(i).getExpress_number() + ",";
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("express_number", numbers);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连续扫描按钮
     *
     * @param view
     */
    public void Continuous(View view) {
        // 非E3入口进入时功能
        if (captureType == Constants.TYPE_PAIJIAN
                || captureType == Constants.TYPE_PAIJIAN_ONE
                || captureType == Constants.TYPE_FIND_EXPRESS
                || captureType == Constants.TYPE_ORDER_ONE
                || captureType == Constants.TYPE_COLLECTION
                || captureType == Constants.TYPE_CREATE_LIUYAN
                || captureType == Constants.TYPE_KEEP_ACCOUNTS
                || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST )// 线下现金收款
         {
            if (isContinuous) {
                if (tv_scan != null && iv_scan != null) {
                    tv_scan.setText("打开批量扫描");
                    iv_scan.setBackgroundResource(R.drawable.more_scan);
                }
                isContinuous = false;
            } else {
                if (tv_scan != null && iv_scan != null) {
                    tv_scan.setText("关闭批量扫描");
                    iv_scan.setBackgroundResource(R.drawable.more_scan_hover);
                }
                isContinuous = true;
            }
        }
        // 数据清空
        mList.clear();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
        skuaidiDb.clearTableOrder();
    }

    /**
     * 批量导入派件
     */
    public void importWaybill(View view) {
        UMShareManager.onEvent(context, "importWaybill", "E3", "E3：导入未签单号");
        if (!Utility.isNetworkConnected()) {// 无网络
            UtilToolkit.showToast("请检查网络设置！");
            return;
        }
        if (isRequesting) {
            UtilToolkit.showToast("正在获取快件信息，请勿重复请求");
            return;
        }
        isRequesting = true;
        JSONObject object = new JSONObject();
        try {
            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                object.put("sname", E3SysManager.SCAN_E3_UNSIGNED);
                object.put("brand", "sto");
            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                object.put("sname", E3SysManager.SCAN_E3_UNSIGNED);
                object.put("brand", "zt");
            }
            object.put("getType", "unsigned");
            object.put("mobile", courierReviewInfo.getCourierPhone());
            object.put("operator_code", courierReviewInfo.getCourierJobNo());
            httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
        } catch (JSONException e) {
            isRequesting = false;
            e.printStackTrace();
        }

    }

    /**
     * 闪光灯开关
     *
     * @param view
     */
    public void open(View view) {
        try {
            CameraManager.get().flash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 打开闪光灯点击变为白色、再点为原来的黑色
        if (!light) {
            if (tv_flashlight != null)
                tv_flashlight.setText("关闭闪光灯");
            if (tv_flashlight1 != null)
                tv_flashlight1.setText("关闭闪光灯");
            if (captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.icon_flash_collection_closed);
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_flash_collection_closed, 0, 0);
            } else {
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sao_icon_shanguang_hover, 0, 0);
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.sao_icon_shanguang_hover);

            }
            // 以下判断针对记账扫码功能
            if (captureType == Constants.TYPE_KEEP_ACCOUNTS && "keep_accunts".equals(from)) {
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.icon_flash_collection_closed);
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_flash_collection_closed, 0, 0);
            } else {
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sao_icon_shanguang_hover, 0, 0);
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.sao_icon_shanguang_hover);
            }

            light = true;
        } else {
            if (tv_flashlight != null)
                tv_flashlight.setText("打开闪光灯");
            if (tv_flashlight1 != null)
                tv_flashlight1.setText("打开闪光灯");
            if (captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_flash_collection, 0, 0);
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.icon_flash_collection);
            } else {
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sao_icon_shanguang, 0, 0);
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.sao_icon_shanguang);
            }

            // 以下判断针对记账扫码功能
            if (captureType == Constants.TYPE_KEEP_ACCOUNTS && "keep_accunts".equals(from)) {
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.icon_flash_collection_closed);
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_flash_collection_closed,
                            0, 0);
            } else {
                if (tv_flashlight != null)
                    tv_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sao_icon_shanguang, 0, 0);
                if (ivflashlight != null)
                    ivflashlight.setBackgroundResource(R.drawable.sao_icon_shanguang);
            }

            light = false;
        }
    }

    /**
     * 派件、工具 内的手动输入
     *
     * @param view
     */
    public void manualInput(View view) {
        if (CameraManager.get() != null) {
            CameraManager.get().stopPreview();
        }
        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        if (!Utility.isEmpty(inType) && inType.equals("notify_detail_adapter")
                || captureType == Constants.TYPE_ORDER_ONE && getIntent().hasExtra("isSto")
                || captureType == Constants.TYPE_COLLECTION && isContinuous
                || captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER// 浙江地区实名登记入口
                || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST && isContinuous// 线下现金收款
         ) {
            dialog.setTitle("批量录入客户运单号");
            if (!"MI NOTE Pro".equals(android.os.Build.MODEL) && "22".equals(android.os.Build.VERSION.SDK)) {
                dialog.setBigEditTextKeyLisenter(EDITTEXT_DIGITS);
            }
            dialog.isUseBigEditText(true);
            dialog.setBigEditTextHint("手动输入或批量粘贴收件人运单号，并以“，”或换行分割，最大限度" + MAX_COUNT + "个号码");
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setDonotAutoDismiss(true);// 设置所有按钮不自动隐藏
            dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    UMShareManager.onEvent(context, "CaptureActivity", "Camara", "扫描单号批量导入");
                    String expressNoStr = dialog.getBigEditTextContent();
                    String regulation = "[-A-Za-z0-9]+";
                    Pattern mPattern = Pattern.compile(regulation);
                    Matcher mMatcher = mPattern.matcher(expressNoStr);
                    List<String> phoneList = new ArrayList<>();// 保存输入的手机号码【变量】
                    while (mMatcher.find()) {
                        phoneList.add(mMatcher.group());// group(i):返回当前查找而获得的与指定的组匹配的子串内容
                    }
                    for (int i = phoneList.size() - 1; i >= MAX_COUNT; i--) {
                        phoneList.remove(i);
                    }
                    if ((mAdapter.getCount() + phoneList.size()) <= MAX_COUNT) {// 如果还没有满指定的最大条数【发短信中200，云呼中100】
                        setFinishViewVisibility(View.VISIBLE);
                        if (captureType == Constants.TYPE_PAIJIAN
                                || captureType == Constants.TYPE_COLLECTION
                                || captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER// 浙江地区实名登记入口
                                || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST// 线下现金收款
                                || captureType == Constants.TYPE_KEEP_ACCOUNTS) { // 从派件部分扫描二维码-多条派送(连续扫描)或者收款扫单
                            for (int i = 0; i < phoneList.size(); i++) {
                                if (phoneList.get(i).length() < 8) {
                                    UtilToolkit.showToast("第" + (i + 1) + "条单号不正确");
                                    return;
                                }
                            }
                            for (int i = 0; i < phoneList.size(); i++) {
                                addScanWayBill(phoneList.get(i), true, null);// 添加到列表中
                            }
                            dialog.showSoftInput(false);
                            dialog.setDismiss();
                        }

                        if (isContinuous) {// 如果是连续扫描
                            dialog.showSoftInput(false);
                            dialog.setDismiss();
                        } else {
                            scanFinish();
                        }
                    } else {
                        UtilToolkit.showToast("最多可扫" + MAX_COUNT + "单，请删除" + (phoneList.size() + mAdapter.getCount() - MAX_COUNT) + "条单号");
                    }
                }

            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
                @Override
                public void onClick() {
                    dialog.showSoftInput(false);
                    dialog.setDismiss();
                }
            });

        } else {// 小输入框
            dialog.setTitle("手动输入");
            dialog.isUseEditText(true);
            dialog.setEditTextHint("请输入运单号");
            dialog.setEditTextInputTypeStyle(InputType.TYPE_CLASS_NUMBER);
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setDonotAutoDismiss(true);
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    deliverNO = dialog.getEditTextContent();
                    // SkuaidiSpf.saveOrderNumbers(context, deliverNO);

                    if (deliverNO.length() >= 8) {
                        if (isContinuous) {
                            if (mAdapter.getCount() < MAX_COUNT) {
                                setFinishViewVisibility(View.VISIBLE);
                                addScanWayBill(deliverNO, true, null);
                            } else {
                                UtilToolkit.showToast("最多可扫" + MAX_COUNT + "单");
                            }
                        } else {

                            if (captureType == Constants.TYPE_FIND_EXPRESS) {
                                Intent intent = new Intent();
                                expressFirmName = SkuaidiSpf.getLoginUser().getExpressFirm();
                                express = SkuaidiSpf.getLoginUser().getExpressNo();
                                intent.setClass(context, CopyOfFindExpressResultActivity.class);
                                intent.putExtra("expressfirmName", expressFirmName);
                                intent.putExtra("express_no", express);
                                intent.putExtra("order_number", deliverNO);
                                startActivity(intent);
                                finish();
                            } else if (captureType == Constants.TYPE_PAIJIAN) { // 从派件部分扫描二维码-多条派送(连续扫描)
                                List<NotifyInfo2> info2s = new ArrayList<>();
                                NotifyInfo2 info2 = new NotifyInfo2();
                                info2.setExpress_number(deliverNO);
                                info2s.add(info2);
                                SKuaidiApplication.getInstance().postMsg("CaptureActivity", "ToNotifyDetailActivity",
                                        info2s);
                                setResult(Constants.RESULT_NOTIFYDETAIL_CAPTURE);
                                finish();
                            } else if (captureType == Constants.TYPE_PAIJIAN_ONE) { // 从派件部分扫描二维码-单条派送(扫描一条)
                                Intent intent = new Intent();
                                intent.putExtra("decodestr", deliverNO);
                                setResult(Constants.TYPE_PAIJIAN_ONE, intent);
                                finish();
                                // 回填单号
                            } else if (captureType == Constants.TYPE_ORDER_ONE) {
                                Intent intent = new Intent();
                                intent.putExtra("decodestr", deliverNO);
                                setResult(Constants.TYPE_ORDER_ONE, intent);
                                InputMethodManager inputMethodManager = (InputMethodManager) context
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                finish();
                            } else if (captureType == Constants.TYPE_CREATE_LIUYAN) {
                                Intent intent = new Intent();
                                intent.putExtra("decodestr", deliverNO);
                                setResult(102, intent);
                                finish();
                            } else if (captureType == Constants.TYPE_COLLECTION) {
                                addScanWayBill(deliverNO, true, null);
                                scanFinish();
                            } else if(captureType == Constants.TYPE_KEEP_ACCOUNTS){
                                Intent intent = new Intent();
                                intent.putExtra("express_no", deliverNO);
                                setResult(Constants.TYPE_SCAN_ORDER_RESULT, intent);
                                finish();
                            } else if(captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST){
                                addScanWayBill(deliverNO,true,null);
                                scanFinish();
                            }else {
                                Intent intent = new Intent();
                                intent.setClass(context, SendMSGActivity.class);
                                startActivity(intent);
                            }
                        }
                        dialog.showSoftInput(false);
                        dialog.dismiss();
                    } else {
                        UtilToolkit.showToast("单号位数不对！");
                    }
                }
            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
                @Override
                public void onClick() {
                    dialog.showSoftInput(false);
                    dialog.dismiss();
                }
            });


        }
        dialog.showDialog();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (CameraManager.get() != null) {
                    CameraManager.get().startPreview();
                    restartPreviewAndDecode();
                }
            }
        });
    }

    /**
     * 订单 自主审核内的手动输入
     *
     * @param view
     */

    public void mannulQrcode(View view) {
        manualInput(view);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            return;
        }
        if (handler == null) {
            try {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            if (holder != null) {
                try {
                    initCamera(holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        //Log.i(TAG, "surfaceDestroyed");
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    String cacheCode;

    private void notifyDataChangedByPosition(){
        if (mAdapter.getOrderNo() != -1) {
            mAdapter.notifyDataSetChanged();
        } else {
            if (null != getIntent().getStringExtra("from") && getIntent().getStringExtra("from").equals("yunhu"))
                mAdapter.notifyDataSetChangedYunhu(getIntent().getIntExtra("listposition", -1),
                        getIntent().getStringExtra("from"), (List<CloudVoiceMsgDetailEntry>) getIntent().getSerializableExtra("sendmsgInfos"));
            else
                mAdapter.notifyDataSetChanged(getIntent().getIntExtra("listposition", -1), (List<NotifyInfo2>) getIntent().getSerializableExtra("sendmsgInfos"));
        }
    }

    /**
     * 将单号添加到列表中
     *
     * @param code
     */
    @SuppressWarnings("deprecation")
    protected void addScanWayBill(String code, boolean isFromManualInput, Bitmap barcode) {
        if (wayBillsCache.get(code) == null) {
            if (captureType == Constants.TYPE_PAIJIAN) {
                Number number = new Number();
                number.setOrderNo(code);
                number.saveThrows();
            }
            NotifyInfo info = new NotifyInfo();
            info.setScanTime(E3SysManager.getTimeBrandIndentify());
            info.setExpress_number(code);
            info.setRemarks("");
            info.setStatus("");
            if ("扫派件".equals(scanType) || "扫收件".equals(scanType) || "扫发件".equals(scanType)) {
                info.setCourierJobNO(courierReviewInfo.getCourierJobNo());
                info.setWayBillTypeForE3(courierReviewInfo.getCourierName());
            }
            mList.add(info);
            List<NotifyInfo> toGetStatusList = new ArrayList<>();// 最新状态查询
            toGetStatusList.add(info);
            // 巴枪扫描
            if (!TextUtils.isEmpty(scanType)) {
                getEndStatus(toGetStatusList);
            }

            wayBillsCache.put(code, true);
            cacheCode = code;

            if (!isFromManualInput)// 手动输入不播放提示音
                playBeepSoundAndVibrate();

            if (scanType != null) {
                mAdapter.notifyDataSetChanged();
            } else {
                notifyDataChangedByPosition();
            }
            if (pic_signed) {
                CameraManager.get().closeDriver();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, EThreeCameraActivity.class);
                List<NotifyInfo> wayBills = new ArrayList<>();
                wayBills.add(info);
                intent.putExtra("isFromScan", true);
                intent.putExtra("wayBills", (Serializable) wayBills);
                startActivity(intent);
            }
            cacheScanData(Arrays.asList(info));
        } else {
            if (isFromManualInput) {
                UtilToolkit.showToast("重复扫描");
                playRepeatedTone();
            } else {
                orderNum += 1;
                if (orderNum == 10) {
                    orderNum = 0;
                    UtilToolkit.showToast("重复扫描");
                    playRepeatedTone();
                }
            }
        }
    }

    /**
     * 手动输入结果处理
     *
     * @param codes
     * @param isFromManualInput
     */
    protected void addScanWayBill(List<String> codes, List<String> incorrectNumberList, boolean isFromManualInput) {
        List<NotifyInfo> wayBills = new ArrayList<>();
        List<NotifyInfo> toGetStatusList = new ArrayList<>();// 最新状态查询
        for (int i = 0; i < codes.size(); i++) {
            if (wayBillsCache.get(codes.get(i)) == null) {
                NotifyInfo info = new NotifyInfo();
                info.setScanTime(E3SysManager.getTimeBrandIndentify());
                info.setExpress_number(codes.get(i));
                info.setRemarks("");
                info.setStatus("");
                if ("扫派件".equals(scanType) || "扫收件".equals(scanType) || "扫发件".equals(scanType)) {
                    info.setCourierJobNO(courierReviewInfo.getCourierJobNo());
                    info.setWayBillTypeForE3(courierReviewInfo.getCourierName());
                }
                mList.add(info);
                wayBills.add(info);
                toGetStatusList.add(info);
                wayBillsCache.put(codes.get(i), true);
                cacheCode = codes.get(i);
                if (scanType != null) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (mAdapter.getOrderNo() != -1) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyDataSetChanged(getIntent().getIntExtra("listposition", -1),
                                (List<NotifyInfo2>) getIntent().getSerializableExtra("sendmsgInfos"));
                    }
                }
            }
        }
        cacheScanData(mList);
        if (!TextUtils.isEmpty(scanType)) {

            getEndStatus(toGetStatusList);
        }
        if (pic_signed && incorrectNumberList.size() == 0) {
            CameraManager.get().closeDriver();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(context, EThreeCameraActivity.class);
            intent.putExtra("wayBills", (Serializable) wayBills);
            intent.putExtra("isFromScan", true);
            startActivity(intent);
        }
    }

    /**
     * 从服务器获单号最新状态
     */
    @SuppressWarnings("unchecked")
    protected void getEndStatus(List<NotifyInfo> infos) {

        JSONObject data = new JSONObject();
        String numbers = "";
        try {
            data.put("sname", E3SysManager.EXPRESS_END_STATUS);
            if (infos == null || infos.size() == 0)
                return;
            for (int i = 0; i < infos.size(); i++) {
                numbers += infos.get(i).getExpress_number() + ",";
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("express_no", numbers);
            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                data.put("company", "sto");
            } else if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 全峰)
                data.put("company", "qf");
            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                data.put("company", "zt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 条码扫描结果处理
     */

    public void handleDecode(final Result obj, Bitmap barcode) {
        code = obj.getText();// 获取到扫描的条形码
        if (code.length() >= 8) {
            Intent intent = new Intent();
            intent.putExtra("decodestr", code);

            if (!E3SysManager.isValidWaybillNo(code)) {
                playRepeatedTone();
                UtilToolkit.showToast("非" + E3SysManager.brandMap.get(company) + "条码");
                restartPreviewAndDecode();
                return;
            }

            if (isContinuous || captureType == Constants.TYPE_ORDER_ONE && getIntent().hasExtra("isSto")) {
                //System.out.println("连续扫描");
                useContinuous();
                if (mAdapter.getCount() < MAX_COUNT) {
                    addScanWayBill(code, false, barcode);
                } else {
                    if (!dialog_show) {
                        dialog_show = true;
                        if (captureType != Constants.TYPE_FIND_EXPRESS) {
                            SkuaidiDialog dialog = new SkuaidiDialog(this);
                            dialog.setTitle("温馨提示");
                            if (captureType == Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER) {// 浙江实名认证界面使用，当扫描了最大数单号以后提交
                                dialog.setContent("你已扫描" + MAX_COUNT + "个单号，是否要提交？");
                                dialog.setPositionButtonTitle("提交");
                            } else {
                                dialog.setContent("你已扫描" + MAX_COUNT + "单，可以先发送出去~\n客户等着你呐\n" + "~~" + "\\" + "(^o^)/" + "~~");
                                dialog.setPositionButtonTitle("发送");
                            }

                            dialog.isUseEditText(false);
                            dialog.setNegativeButtonTitle("取消");

                            dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                                @Override
                                public void onClick(View v) {
                                    scanFinish();
                                }
                            });
                            dialog.showDialog();
                        } else {
                            SkuaidiDialog dialog = new SkuaidiDialog(this);
                            dialog.setTitle("温馨提示");
                            dialog.setContent("主人，作为摄像头的我已经\n扛不住了，能不能查先？\n" + "~~" + "\\" + "(^o^)/" + "~~");
                            dialog.isUseEditText(false);
                            dialog.setPositionButtonTitle("进入查询");
                            dialog.setNegativeButtonTitle("取消");

                            dialog.setPosionClickListener(new PositonButtonOnclickListener() {
                                @Override
                                public void onClick(View v) {
                                    scanFinish();
                                }
                            });
                        }
                    }
                }
            } else {
                playBeepSoundAndVibrate();
                if (captureType == 0) {// 把二维码扫描结果返回到上个界面
                    setResult(Constants.RESULT_QRCODE, intent);
                } else if (captureType == Constants.TYPE_PAIJIAN) { // 从派件部分扫描二维码-多条派送(连续扫描)
                    List<NotifyInfo2> info2s = new ArrayList<>();
                    NotifyInfo2 info2 = new NotifyInfo2();
                    info2.setExpress_number(code);
                    info2s.add(info2);
                    SKuaidiApplication.getInstance().postMsg("CaptureActivity", "ToNotifyDetailActivity", info2s);
                    setResult(Constants.RESULT_NOTIFYDETAIL_CAPTURE);
                } else if (captureType == Constants.TYPE_PAIJIAN_ONE) { // 从派件部分扫描二维码-单条派送(扫描一条)
                    setResult(Constants.TYPE_PAIJIAN_ONE, intent);
                } else if (captureType == Constants.TYPE_ORDER_ONE) {
                    setResult(Constants.TYPE_ORDER_ONE, intent);
                } else if (captureType == Constants.TYPE_COLLECT) { // 自主审核 揽件扫描
                    setResult(Constants.TYPE_COLLECT, intent);
                } else if (captureType == Constants.TYPE_DELIVER) {// 自主审核 派件扫描
                    setResult(Constants.TYPE_DELIVER, intent);
                } else if (captureType == Constants.TYPE_CREATE_LIUYAN) {
                    setResult(102, intent);
                } else if (captureType == Constants.TYPE_FIND_EXPRESS) { // 工具内的查快递
                    expressFirmName = SkuaidiSpf.getLoginUser().getExpressFirm();
                    express = SkuaidiSpf.getLoginUser().getExpressNo();
                    intent.setClass(context, CopyOfFindExpressResultActivity.class);
                    intent.putExtra("expressfirmName", expressFirmName);
                    intent.putExtra("express_no", express);
                    intent.putExtra("order_number", obj.getText().toString());
                    startActivity(intent);
                } else if (captureType == Constants.TYPE_COLLECTION || captureType == Constants.TYPE_KEEP_ACCOUNTS) {
                    addScanWayBill(code, false, barcode);
                    scanFinish();
                    return;
                } else if (Constants.TYPE_DISPATCH == captureType) {
                    intent.setClass(this, DispatchSearchActivity.class);
                    intent.putExtra("listType", getIntent().getStringExtra("listType"));
                    intent.putExtra("timeType", getIntent().getStringExtra("timeType"));
                    intent.putExtra(DispatchSearchActivity.NUMBERTOSEARCH, code);
                    startActivity(intent);
                } else if (Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER == captureType || captureType == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST) {
                    addScanWayBill(code, false, barcode);
                    scanFinish();
                }
                finish();
            }
        } else {
            UtilToolkit.showToast("单号位数不对");
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    /**
     * 扫描单号重复提示铃声初始化
     */
    private void initRepeatedTone() {
        Uri soundURI = UtilToolkit.getResourceUri(context, R.raw.scan_required);
        ringtone = RingtoneManager.getRingtone(context, soundURI);
        if (ringtone != null)
            ringtone.setStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * 扫描单号重复提示铃声播放
     */
    public void playRepeatedTone() {
        if (ringtone != null) ringtone.play();
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    protected void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 返回按钮点击事件
     *
     * @param view
     */
    public void back(View view) {
        skuaidiDb.clearTableOrder();// 删除保存在数据库中扫描后的单号
        if (captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {
            showCollectionDialog(view);
        } else {
            showDialog(view);
        }
    }

    /**
     * 放弃收款后扫描，返回确认
     *
     * @param v
     */
    private void showCollectionDialog(View v) {
        if (mList != null && mList.size() != 0 && mList.size() != uploadedInfos.size()) {
            dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, v);
            if (!dialog.isShowing()) {
                dialog.setTitle("离开提示");
                dialog.setCommonContent("是否保存运单号？");
                dialog.setPositiveButtonTitle("是");
                dialog.setNegativeButtonTitle("否");
                dialog.setNegativeClickListener(new SkuaidiE3SysDialog.NegativeButtonOnclickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();

                    }
                });
                dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

                    @Override
                    public void onClick() {
                        dialog.dismiss();
                        UMShareManager.onEvent(context, "E3_giveUp_scanConfirm", "E3", "E3：放弃上传扫描单号");
                        E3OrderDAO.deleteCacheOrders(infoToOrder(mList, 0, 1));
                        addCollectionExpress();

                    }
                });
                if (!isFinishing())
                    dialog.showDialog();
            } else {
                if (!isFinishing())
                    dialog.dismiss();
            }
        } else if (mList != null && mList.size() == uploadedInfos.size() && mList.size() != 0) {
            E3SysManager.deletePicsByNotifyInfo(mList);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
    }

    @Override
    protected void onDestroy() {
        CameraManager.get().closeDriver();
        super.onDestroy();
        unregisterReceiver();
        if (mLocationManager != null)
            mLocationManager.removeUpdates();
        if (MakeCollectionsActivity.activitys != null)
            MakeCollectionsActivity.activitys.remove(this);
    }

    @Override
    protected void onStop() {
        CameraManager.get().closeDriver();
        super.onStop();
    }

    protected int getScanCount() {
        return mAdapter.getCount();
    }

    protected void restartPreviewAndDecode() {
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    /**
     * 设置连续扫描
     */
    protected void useContinuous() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);

        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
        setFinishViewVisibility(View.VISIBLE);
    }

    /**
     * 扫描后的动画
     *
     * @param v
     */
    public void startAnimation(View v) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(500);
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
        v.startAnimation(animationSet);
    }

    public void drawResultBitMap(Bitmap barcode) {
        viewfinderView.drawResultBitmap(barcode);
    }

    SkuaidiE3SysDialog dialog;

    /**
     * 放弃扫描，返回确认
     *
     * @param v
     */
    private void showDialog(View v) {
        if (mList != null && mList.size() != 0 && mList.size() != uploadedInfos.size()) {
            dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, v);
            if (!dialog.isShowing()) {

                dialog.setTitle("放弃扫描");
                dialog.setCommonContent("你将放弃已经扫描的单号!\n确认放弃？");
                dialog.setPositiveButtonTitle("确认");
                dialog.setNegativeButtonTitle("取消");
                dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

                    @Override
                    public void onClick() {
                        dialog.dismiss();
                        UMShareManager.onEvent(context, "E3_giveUp_scanConfirm", "E3", "E3：放弃上传扫描单号");
                        finish();
                        // 删除图片
                        E3SysManager.deletePicsByNotifyInfo(mList);
                        E3OrderDAO.deleteCacheOrders(infoToOrder(mList, 0, 1));
                        // 删除扫描的单号
                        DataSupport.deleteAll(Number.class);
                    }
                });
                if (!isFinishing())
                    dialog.showDialog();
            } else {
                if (!isFinishing())
                    dialog.dismiss();
            }
        } else if (mList != null && mList.size() == uploadedInfos.size() && mList.size() != 0) {
            E3SysManager.deletePicsByNotifyInfo(mList);
            finish();
        } else {
            finish();
        }
    }

    /**
     * 手机back键点击事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog(findViewById(R.id.iv_title_back));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (E3SysManager.EXPRESS_END_STATUS.equals(sname)) {
            if (!Utility.isEmpty(result) && !TextUtils.isEmpty(result.toString())) {

                if (mList.size() == 1) {
                    try {
                        String status = result.optJSONObject("retArr").optJSONObject(mList.get(0).getExpress_number())
                                .optString("express_status");
                        if (!TextUtils.isEmpty(status) && !"null".equals(status))
                            mList.get(0).setStatus(status);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                } else {
                    for (int i = 0; i < mList.size(); i++) {
                        try {
                            String status = "";
                            JSONObject jso = result.optJSONObject("retArr").optJSONObject(
                                    mList.get(i).getExpress_number());
                            if (jso != null) {
                                status = jso.optString("express_status");
                                if (!TextUtils.isEmpty(status) && !"null".equals(status))
                                    mList.get(i).setStatus(status);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                    }
                }
                if (captureType != Constants.TYPE_PAIJIAN
                        && captureType != Constants.TYPE_PAIJIAN_ONE
                        && captureType != Constants.TYPE_FIND_EXPRESS
                        || captureType != Constants.TYPE_COLLECTION
                        || captureType != Constants.TYPE_CREATE_LIUYAN) {
                    if (!e3ScanDelete)
                        mAdapter.setNotCheckRepeat(false);
                }
                playRepeatedTone = true;//列表每添加一次单号，可能有一次重复提示
                mAdapter.notifyDataSetChanged();

            }

        } else if (sname.equals(E3SysManager.SCAN_E3_UNSIGNED)) {
            final List<NotifyInfo> importList = new ArrayList<NotifyInfo>();
            try {
                if (result != null) {
                    JSONArray datas = result.getJSONArray("retArr");
                    for (int i = 0; i < datas.length(); i++) {
                        NotifyInfo noti = new NotifyInfo();
                        JSONObject object = datas.getJSONObject(i);
                        noti.setExpress_number(object.optString("waybill_no"));
                        noti.setWayBillTypeForE3(object.optString("bad_waybill_type"));
                        noti.setStatus(object.optString("type"));
                        noti.setScanTime(E3SysManager.getTimeBrandIndentify());
                        importList.add(noti);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (importList.size() != 0) {
                SkuaidiDialog skuaidiDialog = new SkuaidiDialog(CaptureActivity.this);
                skuaidiDialog.setTitle("温馨提示");
                skuaidiDialog.setContent("你将导入未签单号.\n未签收数据可能由于延时,快递公司服务器故障等导致不准,请务必确认确实是可签收的单号");
                skuaidiDialog.isUseEditText(false);
                skuaidiDialog.setPositionButtonTitle("继续操作");
                skuaidiDialog.setNegativeButtonTitle("取消");
                skuaidiDialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EthreeInfoScanActivity.class);
                        intent.putExtra("scanType", scanType);
                        intent.putExtra("isImport", true);
                        EthreeInfoScanActivity.importList = importList;
                        startActivity(intent);
//                        EthreeInfoScanActivity.actionStart(context,scanType,false,false,true,null,null);
                        finish();
                    }
                });
                skuaidiDialog.showDialog();
            } else {
                ToastHelper.makeText(CaptureActivity.this, "没有单号可以导入", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "无当日有派无签单号", Toast.LENGTH_LONG).show();
            }
            isRequesting = false;
        } else if (E3SysManager.SCAN_TO_ZT.equals(sname) || E3SysManager.SCAN_TO_QF.equals(sname)
                || E3SysManager.SCAN_TO_E3.equals(sname)) {
            uploading = false;
            if (E3SysManager.SCAN_TO_ZT.equals(sname)) {
                if (Utility.isEmpty(result))
                    return;
                JSONArray arr = result.optJSONArray("retArr");
                if (arr != null && arr.length() != 0) {// 单个上传，拦截件列表也最多只有一个
                    try {
                        if (toUploadInfo.getExpress_number().equals(arr.get(0).toString().trim())) {
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                }
            }

            // 上传成功，清楚缓存
            E3OrderDAO.deleteCacheOrders(infoToOrder(Arrays.asList(toUploadInfo), 0, 1));
            E3OrderDAO.addOrders(infoToOrder(Arrays.asList(toUploadInfo), 1, 0), company, courierNO);
            // 上传成功，删除图片
            // E3SysManager.deletePic(toUploadInfo.getPicPath());
            uploadedInfos.add(toUploadInfo);
            for (int i = 0; i < mList.size(); i++) {
                if (toUploadInfo.getExpress_number().equals(mList.get(i).getExpress_number())) {
                    mList.get(i).setIsUpload(1);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }

        } else if (UPLOAD_EXPRESS_ADD.equals(sname)) {
            if ("CollectionDetailActivity".equals(from)) {
                Intent intent = new Intent();
                intent.putExtra("express_list", (Serializable) mList);
                intent.putExtra("order_number", getIntent().getStringExtra("order_number"));
                setResult(101, intent);
            } else {
                Intent intent = new Intent(this, CollectionDetailActivity.class);
                intent.putExtra("express_list", (Serializable) mList);
                intent.putExtra("order_number", getIntent().getStringExtra("order_number"));
                intent.putExtra("money", getIntent().getStringExtra("money"));
                startActivity(intent);
            }
            finish();
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (E3SysManager.SCAN_E3_UNSIGNED.equals(sname)) {
            isRequesting = false;
            UtilToolkit.showToast(result);
        } else if (E3SysManager.SCAN_TO_ZT.equals(sname) || E3SysManager.SCAN_TO_QF.equals(sname)
                || E3SysManager.SCAN_TO_E3.equals(sname)) {
            uploading = false;
        } else if (UPLOAD_EXPRESS_ADD.equals(sname)) {
            UtilToolkit.showToast(result);

            if ("CollectionDetailActivity".equals(from)) {
                setResult(101);
            } else {
                if (MakeCollectionsActivity.activitys != null) {// 订单收款扫单号，
                    for (Activity activity : MakeCollectionsActivity.activitys) {
                        activity.finish();
                    }
                    if (MakeCollectionsActivity.activitys != null)
                        MakeCollectionsActivity.activitys.clear();
                    Intent intent = new Intent("collection");
                    intent.putExtra("decodestr", mList.get(0).getExpress_number());// 一次只能扫一单
                } else {
                    Intent intent = new Intent(this, CollectionDetailActivity.class);
                    intent.putExtra("express_list", (Serializable) mList);
                    intent.putExtra("order_number", getIntent().getStringExtra("order_number"));
                    intent.putExtra("money", getIntent().getStringExtra("money"));
                    startActivity(intent);
                }

            }
            finish();

        }

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    /**
     * 闪光灯开关
     *
     * @param view
     */
    public void openFlashLight(View view) {
        try {
            CameraManager.get().flash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Drawable leftDrawable;
        if (!isLightOn) {
            leftDrawable = context.getResources().getDrawable(R.drawable.icon_sign_flashlight_opened);
            isLightOn = true;
            tv_flashlight_top.setText("关闭闪光灯");
        } else {
            leftDrawable = context.getResources().getDrawable(R.drawable.icon_sign_flashlight_closed);
            isLightOn = false;
            tv_flashlight_top.setText("打开闪光灯");
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        tv_flashlight_top.setCompoundDrawables(leftDrawable, null, null, null);
    }

    /**
     * 拍照签收开关
     *
     * @param view
     */
    public void photographSwitch(View view) {
        if (pic_signed) {
            pic_signed = false;
            ((ImageView) view).setImageResource(R.drawable.icon_pic_signed_default);
        } else {
            ((ImageView) view).setImageResource(R.drawable.icon_pic_signed_checked);
            pic_signed = true;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case TACKE_PIC_RESPONSE_CODE:
                List<NotifyInfo> picWayBills = (List<NotifyInfo>) data.getSerializableExtra("picWayBills");
                for (int i = 0; i < picWayBills.size(); i++) {
                    for (int j = 0; j < mList.size(); j++) {
                        if (picWayBills.get(i).getExpress_number().equals(mList.get(j).getExpress_number())) {
                            mList.get(j).setPicPath(picWayBills.get(i).getPicPath());
                            break;
                        }
                    }

                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    public static final String ACTION_TAKEPIC = "com.skuaidi.scan.takeSigned";
    private IntentFilter filter;

    private void registerReceiver() {
        myBoradCastReceiver = new MyBoradCastReceiver();
        filter = new IntentFilter(ACTION_TAKEPIC);
        registerReceiver(myBoradCastReceiver, filter);

    }

    private void unregisterReceiver() {
        unregisterReceiver(myBoradCastReceiver);
        myBoradCastReceiver = null;
        filter = null;
    }

    /**
     * @param notifyInfos isupload 是否已经上传过
     * @return
     */
    protected ArrayList<E3_order> infoToOrder(List<NotifyInfo> notifyInfos, int isUpload, int isCache) {
        ArrayList<E3_order> orders = new ArrayList<E3_order>();
        for (int i = 0; i < notifyInfos.size(); i++) {

            E3_order order = new E3_order();
            order.setOrder_number(notifyInfos.get(i).getExpress_number());
            if ("扫收件".equals(scanType)) {
                order.setType("收件");
            } else if ("扫派件".equals(scanType)) {
                order.setType("派件");
            } else if ("扫到件".equals(scanType)) {
                order.setType("到件");
            } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                order.setType("签收件");
                if (!TextUtils.isEmpty(notifyInfos.get(i).getPicPath())) {
                    order.setPicPath(notifyInfos.get(i).getPicPath());
                    order.setWayBillType_E3("图片签收");
                    order.setType_extra("图片签收");// 两个字段表示同一意思
                } else {
                    order.setWayBillType_E3(notifyInfos.get(i).getWayBillTypeForE3());
                    order.setType_extra(notifyInfos.get(i).getWayBillTypeForE3());
                }
            } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                order.setType("第三方签收");
                //签收营业厅
                order.setWayBillType_E3(notifyInfos.get(i).getWayBillTypeForE3());
                order.setType_extra(notifyInfos.get(i).getWayBillTypeForE3());

            } else if ("问题件".equals(scanType)) {
                order.setType("问题件");
            } else if ("扫发件".equals(scanType)) {// 发件不指定操作员
                order.setType("发件");
            }

            order.setScan_time(notifyInfos.get(i).getScanTime());
            order.setCompany(company);
            order.setCourier_job_no(courierNO);
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            order.setLatitude(notifyInfos.get(i).getLatitude());
            order.setLongitude(notifyInfos.get(i).getLongitude());
            orders.add(order);

        }
        return orders;
    }

    private class MyBoradCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_TAKEPIC)) {
                //Log.i(TAG, "skuaidi Scan result");
                List<NotifyInfo> picWayBills = (List<NotifyInfo>) intent.getSerializableExtra("picWayBills");
                for (int i = 0; i < picWayBills.size(); i++) {
                    for (int j = 0; j < mList.size(); j++) {
                        if (picWayBills.get(i).getExpress_number().equals(mList.get(j).getExpress_number())) {
                            mList.get(j).setPicPath(picWayBills.get(i).getPicPath());
                            cacheScanData(Arrays.asList(mList.get(j)));
                            if ((mAdapter.getRepeatList() == null || !mAdapter.getRepeatList().contains(mList.get(j)))
                                    && SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
                                JSONObject datas = new JSONObject();
                                JSONArray wayBills = new JSONArray();
                                JSONObject wayBill = new JSONObject();
                                try {
                                    wayBill.put("waybillNo", mList.get(j).getExpress_number());

                                    if (!TextUtils.isEmpty(mList.get(j).getPicPath())) {
                                        wayBill.put("signPic", Utility.bitMapToString(Utility.getImage(mList.get(j)
                                                .getPicPath())));
                                    } else {
                                        continue;
                                    }
                                    JSONObject location = new JSONObject();
                                    location.put("latitude", mList.get(j).getLatitude());
                                    location.put("longitude", mList.get(j).getLongitude());
                                    wayBill.put("location", location);
                                    wayBill.put("scan_time", mList.get(j).getScanTime());
                                    wayBills.put(wayBill);

                                    if (wayBills.length() != 0) {
                                        datas.put("wayBillDatas", wayBills);
                                    } else {
                                        continue;
                                    }
                                    datas.put("sname", E3SysManager.getScanName());
                                    datas.put("appVersion", Utility.getVersionCode());
                                    datas.put("forceIntercept", 0);
                                    datas.put("wayBillType", E3SysManager.typeToIDMap.get(scanType));
                                    datas.put("dev_id", Utility.getOnlyCode());
                                    TelephonyManager tm = (TelephonyManager) context
                                            .getSystemService(TELEPHONY_SERVICE);
                                    String imei = tm.getDeviceId();
                                    datas.put("dev_imei", imei);
                                    toUploadInfo = mList.get(j);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                                httpInterfaceRequest(datas, false, INTERFACE_VERSION_NEW);
                                uploading = true;
                            }
                            break;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 缓存扫描的单号
     */
    protected void cacheScanData(List<NotifyInfo> mList) {
        if (e3ScanDelete || mList == null || mList.size() == 0 || TextUtils.isEmpty(scanType))
            return;
        if (mLocationManager == null) {
            mLocationManager = new MyLocationManager(locationHandler);
        }
        mLocationManager.setExpressNumber(mList.get(0).getExpress_number());
        Location location = mLocationManager.getLocation(CaptureActivity.this);
        if (location != null) {
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            if (mList.size() == 1) {

                if (location != null) {
                    mList.get(0).setLatitude(latitude);
                    mList.get(0).setLongitude(longitude);
                }
            } else {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setLatitude(latitude);
                    mList.get(i).setLongitude(longitude);
                }

            }
        }

        ArrayList<E3_order> orders = infoToOrder(mList, 0, 1);
        for (E3_order order : orders) {

            E3OrderDAO.cacheOrder(order, company, courierNO);
        }
    }

    /**
     * 控制“完成”按钮的显示和隐藏
     */
    protected void setFinishViewVisibility(int visibility) {
        if (captureType == Constants.TYPE_COLLECTION && !"CollectionDetailActivity".equals(from)) {

            if (tv_finish != null) {
                tv_finish.setVisibility(visibility);
            }

        } else {
            tv_cap_finish.setVisibility(visibility);
        }

    }

    /**
     *
     */
    private void requestGps() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isNetworkEnabled && !isGPSEnabled && !SkuaidiSpf.getOpenLocationNotify()) {
            final SkuaidiDialog dialog = new SkuaidiDialog(context);
            dialog.setTitle("温馨提示");
            dialog.isUseEditText(false);
            dialog.setContent("巴枪扫描需要访问您的地理位置，是否允许访问？");
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setDonotAutoDismiss(true);// 设置所有按钮不自动隐藏
            dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    openGPS();
                    dialog.setDismiss();
                }
            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
                @Override
                public void onClick() {
                    SkuaidiSpf.setOpenLocationNotify(true);
                    dialog.setDismiss();
                }
            });

            dialog.showDialog();
        }
    }

    /**
     * 引导用户打开GPS
     */
    public void openGPS() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private Handler locationHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1000 * 60 * 2) {
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getExpress_number().equals(msg.getData().getString("expressNumber"))) {
                        Location location = (Location) msg.obj;
                        mList.get(i).setLatitude(String.valueOf(location.getLatitude()));
                        mList.get(i).setLongitude(String.valueOf(location.getLongitude()));
                        ArrayList<E3_order> orders = infoToOrder(Arrays.asList(mList.get(i)), 0, 1);
                        if (orders != null && orders.size() != 0)
                            E3OrderDAO.updateCacheOrder(orders.get(0), company, courierNO);
                    }
                }

            }

        }

    };
}