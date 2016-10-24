package com.kuaibao.skuaidi.activity.make.realname;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.activity.OrderInfoActivity;
import com.kuaibao.skuaidi.activity.ShowImageActivity;
import com.kuaibao.skuaidi.activity.adapter.ExpressCollectionAdapter;
import com.kuaibao.skuaidi.activity.make.realname.AsyncTask.IDCardImgAsyncTask;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.CollectionDetail;
import com.kuaibao.skuaidi.entry.CollectionDetail.Express_List;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.RealNameInfo;
import com.kuaibao.skuaidi.entry.ScanScope;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.readidcard.PreviewActivity;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.yunmai.android.vo.IDCard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 收款记录详情页面-显示在线收款详情
 *
 * @author wangqiang
 */
public class CollectionDetailActivity extends SkuaiDiBaseActivity
        implements ExpressCollectionAdapter.Delete, OnGlobalLayoutListener, OnClickListener {
    private static final int REQUEST_CODE_STARTACTIVITY = 0x1002;
    // 收件扫描后，更新单号是否做过收件扫描的状态
    private static final String UPLOAD_EXPRESS_UPDATE = "upload_express.update";
    private static final String SCAN_ACCESS_GET = "scan.access.get";
    // 超过三天的收款 不能添加单号
    private static final int _3 = 3;
    // 获取收款详情
    private static final String UPLOAD_EXPRESS = "upload_express";
    /**
     * 删除收款后上传的单号
     */
    private static final String UPLOAD_EXPRESS_DEL = "upload_express.del";
    // 添加单号
    private TextView  tv_money;
    private LinearLayout ll_add_number_btn;
    // 付款时间
    private TextView tv_payment_Time;
    // 账户名
    private TextView tv_account_name;
    // 认证信息
    private TextView tv_instruction;
    // 标题
    private TextView title;
    // 采集实名信息按钮
    // private TextView realNameInfo;
    @BindView(R.id.btn_cj_realname) Button btn_cj_realname;
    // 保存身份证图片路径
    private String capturePath = null;

    // 收件扫描
    private TextView tv_upload_lanpie;
    private RelativeLayout ll_account_name;
    // 添加单号，listview 下方
    private RelativeLayout ll_add_number;

    private ListView lv_number;
    private ExpressCollectionAdapter mAdapter;
    private Context context;
    private List<NotifyInfo> mList;
    private int position;
    //
    private String number_tobeDelete = "";
    private String money;
    private String time;
    private String buyer_methor;
    private String buyer_openid;
    // 收款订单号
    private String order_number;
    // 是否有扫收件权限
    private boolean canLanPice = false;
    // 有未上传的快递单号
    private boolean uploadable = false;

    View layout_title;
    RelativeLayout rl_money;
    LinearLayout ll_acount;
    int height;
    private boolean outoftime = false;

    private String event = "";
    private File tempFile;
    private String fileName = "";
    private Intent mIntent;
//    private CollectRealNameInfoDialogMenu dialogMenu;// 实名认证信息菜单
    private Bitmap picBitmap = null;
    private RealNameInfo realNameInfoObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        context = this;
        setContentView(R.layout.activity_collection_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {

        layout_title = findViewById(R.id.layout_title);
        rl_money = (RelativeLayout) findViewById(R.id.rl_money);
        ll_acount = (LinearLayout) findViewById(R.id.ll_acount);
        tv_upload_lanpie = (TextView) findViewById(R.id.tv_upload_lanpie);
        lv_number = (ListView) findViewById(R.id.lv_number);
        // realNameInfo = (TextView) findViewById(R.id.realNameInfo);

        ViewTreeObserver vto = layout_title.getViewTreeObserver();
        ViewTreeObserver vto1 = rl_money.getViewTreeObserver();
        ViewTreeObserver vto2 = ll_acount.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(this);
        vto1.addOnGlobalLayoutListener(this);
        vto2.addOnGlobalLayoutListener(this);

        btn_cj_realname.setOnClickListener(this);

        title = ((TextView) findViewById(R.id.tv_title_des));
        title.setText("收款详情");
        ll_account_name = (RelativeLayout) findViewById(R.id.ll_account_name);

        tv_payment_Time = (TextView) findViewById(R.id.tv_payment_Time);
        tv_account_name = (TextView) findViewById(R.id.tv_account_name);
        tv_instruction = (TextView) findViewById(R.id.tv_instruction);

        ll_add_number_btn = (LinearLayout) findViewById(R.id.ll_add_number_btn);
        tv_money = (TextView) findViewById(R.id.tv_money);
        ll_add_number_btn.setVisibility(View.GONE);

        ll_add_number = (RelativeLayout) findViewById(R.id.ll_add_number);
        ll_add_number_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startCaptureActivity();
            }
        });

        ll_add_number.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startCaptureActivity();
            }

        });

        event = getIntent().getStringExtra("event");
        // 如果是记账入口
        if ("jizhang".equals(event)) {
            title.setText("记账详情");
        }

        initData();

    }

    @SuppressWarnings("unchecked")
    private void initData() {
        order_number = getIntent().getStringExtra("order_number");
        getCollectionDetail();
        money = getIntent().getStringExtra("money");
        tv_money.setText("+" + money);
        if (mList == null || mList.size() == 0) {
            ll_add_number.setVisibility(View.GONE);
        } else {
            mAdapter = new ExpressCollectionAdapter(mList, context, this);
            lv_number.setVisibility(View.VISIBLE);
            lv_number.setAdapter(mAdapter);
            ll_add_number.setVisibility(View.VISIBLE);
        }
        getScanVerify();

    }

    private void startCaptureActivity() {
        Intent mIntent = new Intent(context, CaptureActivity.class);
        mIntent.putExtra("isContinuous", false);
        mIntent.putExtra("money", money);
        mIntent.putExtra("order_number", order_number);
        mIntent.putExtra("qrcodetype", Constants.TYPE_COLLECTION);
        mIntent.putExtra("from", "CollectionDetailActivity");
        startActivityForResult(mIntent, 100);
    }

    public void upload(View view) {
        if (!isFinishing())
            showProgressDialog( "正在上传请稍后...");
        JSONObject datas = new JSONObject();
        try {

            if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                datas.put("sname", E3SysManager.SCAN_TO_QF);
            } else if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                datas.put("sname", E3SysManager.SCAN_TO_E3);
            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                datas.put("sname", E3SysManager.SCAN_TO_ZT);
                datas.put("forceIntercept", 0);
            }
            datas.put("wayBillType", E3SysManager.typeToIDMap.get("扫收件"));
            datas.put("dev_id", Utility.getOnlyCode());
            datas.put("dev_imei", ((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId());

            JSONArray wayBills = new JSONArray();
            CourierReviewInfo reviewInfo = null;
            try {
                reviewInfo = E3SysManager.getReviewInfo();
//				reviewInfo = BackUpService.getfinalDb()
//						.findAllByWhere(CourierReviewInfo.class, "courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'").get(0);
            } catch (Exception e) {
                UtilToolkit.showToast("获取个人信息失败，请重新登陆！");
                e.printStackTrace();
                dismissProgressDialog();
                return;
            }
            for (int i = 0; i < mList.size(); i++) {
                JSONObject wayBill = new JSONObject();
                NotifyInfo notifyInfo = mList.get(i);
                if (notifyInfo.getIsUpload() == 1) {
                    continue;
                }
                wayBill.put("waybillNo", notifyInfo.getExpress_number());
                wayBill.put("scan_time", notifyInfo.getScanTime());
                if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 全峰
                    wayBill.put("operatorCode", notifyInfo.getCourierJobNO() == null ? reviewInfo.getCourierJobNo() : notifyInfo.getCourierJobNO());
                } else if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                    wayBill.put("operatorCode", notifyInfo.getCourierJobNO() == null ? reviewInfo.getCourierJobNo() : notifyInfo.getCourierJobNO());
                } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 中通
                    wayBill.put("operatorCode", notifyInfo.getCourierJobNO() == null ? reviewInfo.getCourierJobNo() : notifyInfo.getCourierJobNO());
                }

                JSONObject location = new JSONObject();
                location.put("latitude", notifyInfo.getLatitude());
                location.put("longitude", notifyInfo.getLongitude());
                wayBill.put("location", location);
                wayBills.put(wayBill);
            }
            if (wayBills.length() != 0) {
                datas.put("wayBillDatas", wayBills);
            } else {
                dismissProgressDialog();
                UtilToolkit.showToast("没有未上传的单号");
                return;
            }
            UMShareManager.onEvent(context, "E3_scan_lanPice_confirm", "E3", "E3：收件扫描提交");

            httpInterfaceRequest(datas, false, INTERFACE_VERSION_NEW);
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
     * 删除收款扫描的快递单号
     */
    private void deleteCollectionExpress(int index) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", UPLOAD_EXPRESS_DEL);
            data.put("order_number", order_number);
            data.put("express_number", mList.get(index).getExpress_number());
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            position = index;
            number_tobeDelete = mList.get(index).getExpress_number();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCollectionDetail() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", UPLOAD_EXPRESS);
            data.put("order_number", order_number);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateIsUploadStatus() {
        JSONObject data = new JSONObject();
        String numbers = "";
        try {
            data.put("sname", UPLOAD_EXPRESS_UPDATE);
            data.put("order_number", order_number);
            if (mList == null || mList.size() == 0)
                return;
            for (int i = 0; i < mList.size(); i++) {
                numbers += mList.get(i).getExpress_number() + ",";
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("express_number", numbers);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private CollectionDetail parseCollectionRecords(String detail) {
        CollectionDetail cd = null;
        Gson gson = new Gson();
        try {
            cd = gson.fromJson(detail, CollectionDetail.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return cd;

    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent){
        if (messageEvent.type == Constants.EVENT_BUS_TYPE_1007){
            tv_instruction.setVisibility(View.VISIBLE);
            tv_instruction.setText("已采集实名信息");
            btn_cj_realname.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 101) {
            if (data == null)
                return;
            List<NotifyInfo> list = (List<NotifyInfo>) data.getSerializableExtra("express_list");
            if (mList == null || mList.size() == 0) {
                mList = new ArrayList<NotifyInfo>();
                mList.addAll(list);
                mAdapter = new ExpressCollectionAdapter(mList, context, this);
                lv_number.setVisibility(View.VISIBLE);
                lv_number.setAdapter(mAdapter);
            } else {
                for (int i = 0, j = list.size(); i < j; i++) {
                    boolean contains = false;
                    for (int k = 0, l = mList.size(); k < l; k++) {
                        if (mList.get(k).getExpress_number().equals(list.get(i).getExpress_number())) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        mList.add(list.get(i));
                    }

                }

            }
            tv_upload_lanpie.setVisibility(View.VISIBLE);
            ll_add_number.setVisibility(View.VISIBLE);
            ll_add_number_btn.setVisibility(View.GONE);
            notifyDataAndViewChanged();
        }else if (resultCode == Constants.RESULT_GETREALNAME_SUCCESS) {
            tv_instruction.setVisibility(View.VISIBLE);
            tv_instruction.setText("已采集实名信息");
            btn_cj_realname.setVisibility(View.GONE);
        } else if (requestCode == Constants.CAMREA_IDENTIFY_IDCARD && resultCode == RESULT_OK) {
            if (null != data) {
                IDCard idCard = (IDCard) data.getSerializableExtra("Data");
                String cardFolder;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    cardFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/pic/realname";
                } else {
                    cardFolder = this.getFilesDir().getAbsolutePath();
                }
                File outImgFile = new File(cardFolder, "idCard.jpg");
                KLog.i("TAG", "fdl--idCard =" + idCard.toString());
                IDCardImgAsyncTask mTask = new IDCardImgAsyncTask(CollectionDetailActivity.this,idCard, outImgFile);
                mTask.setRecordContent(getRecordContent());
                mTask.execute();
            }

        }
    }

    private Map<String,String> getRecordContent(){
        Map<String,String> recordContent = new HashMap<>();
        recordContent.put("money",money);
        recordContent.put("time",time);
        recordContent.put("buyer_methor",buyer_methor);
        recordContent.put("buyer_openid",buyer_openid);
        String order = "";
        for (int i = 0; i < mList.size(); i++) {
            order = order + mList.get(i).getExpress_number() + ",";
        }
        if (!Utility.isEmpty(order) && order.length() > 1) {
            order = order.substring(0, order.length() - 1);
        }
        recordContent.put("order",order);
        recordContent.put("order_number",order_number);
        return recordContent;
    }

    private void submitIDCard(String pic) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "IDCard/getAllInfo");
            data.put("filename", pic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    private void getScanVerify() {
        JSONObject object = new JSONObject();
        String company = SkuaidiSpf.getLoginUser().getExpressNo();
        try {
            if ("qf".equals(company)) {
                object.put("sname", E3SysManager.SCAN_QF_VERIFY);
            } else if ("sto".equals(company)) {
                object.put("sname", E3SysManager.SCAN_COUNTERMAN_VERIFY);
            } else if ("zt".equals(company)) {
                object.put("sname", E3SysManager.SCAN_ZT_VERIFY);
            }
            object.put("act", "getinfo");
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            String only_code = Utility.getOnlyCode();
            object.put("dev_imei", imei);
            object.put("dev_id", only_code);
            object.put("new_verify", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (MakeCollectionsActivity.activitys != null) {// 订单收款扫单号，
            for (Activity activity : MakeCollectionsActivity.activitys) {
                activity.finish();
            }
            Intent intent = new Intent(OrderInfoActivity.ACTION_SET_MONEY);
            intent.putExtra("money", money);
            sendBroadcast(intent);
            if (MakeCollectionsActivity.activitys != null)
                MakeCollectionsActivity.activitys.clear();
        }

        super.onBackPressed();

    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (UPLOAD_EXPRESS_DEL.equals(sname)) {
            try {
                if (number_tobeDelete.equals(mList.get(position).getExpress_number())) {
                    mList.remove(mList.get(position));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mList.size() == 0 && !outoftime) {
                ll_add_number_btn.setVisibility(View.VISIBLE);
                ll_add_number.setVisibility(View.GONE);
                tv_upload_lanpie.setVisibility(View.GONE);
            }
            notifyDataAndViewChanged();
        } else if (UPLOAD_EXPRESS.equals(sname)) {
            CollectionDetail mDetail = parseCollectionRecords(result.toString());
            if (mDetail != null) {
                tv_payment_Time.setText(mDetail.getPay_time());
                time = mDetail.getPay_time();
                buyer_methor = mDetail.getReal_info().getBuyer_method();
                buyer_openid = mDetail.getReal_info().getBuyer_openid();
                if (mDetail.getReal_info() != null) {
                    if (!TextUtils.isEmpty(mDetail.getReal_info().getNickname())) {// 真实姓名
                        tv_account_name.setText(mDetail.getReal_info().getNickname());
                    }
                    if (!TextUtils.isEmpty(mDetail.getReal_info().getInstruction())) {// 是否已采集提示文字
                        tv_instruction.setText(mDetail.getReal_info().getInstruction());
                    }
                    if (!Utility.isEmpty(mDetail.getReal_info().getStatus()) && mDetail.getReal_info().getStatus().equals("T")) {// 已通过实名认证
                        tv_instruction.setText("已采集实名信息");
                        btn_cj_realname.setVisibility(View.GONE);
                        tv_instruction.setVisibility(View.VISIBLE);
                    } else {
                        btn_cj_realname.setVisibility(View.VISIBLE);
                        tv_instruction.setVisibility(View.GONE);
                    }

                }

                String pay_time = mDetail.getPay_time();
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
                try {
                    if (!TextUtils.isEmpty(pay_time) && now.substring(0, 8).equals(pay_time.substring(0, 8))
                            && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(pay_time.substring(8, 10)) > _3) {
                        // 超过三天的收款 不能添加单号
                        outoftime = true;
                        tv_upload_lanpie.setVisibility(View.GONE);
                        ll_add_number_btn.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                tv_payment_Time.setText(mDetail.getPay_time());
                tv_money.setText("+" + mDetail.getMoney());

                List<Express_List> el = mDetail.getExpress_list();
                if (mList == null) {
                    mList = new ArrayList<NotifyInfo>();
                }
                for (Express_List exl : el) {
                    exl.getExpress_number();
                    NotifyInfo info = new NotifyInfo();
                    info.setExpress_number(exl.getExpress_number());

                    info.setIsUpload(exl.getIs_upload());
                    if (exl.getIs_upload() == 0) {
                        uploadable = true;
                    }
                    mList.add(info);
                }
                setVisibilityOfUpload();
                //System.out.println("是否有未上传");

                if (mList.size() > 0) {
                    mAdapter = new ExpressCollectionAdapter(mList, context, this);
                    lv_number.setAdapter(mAdapter);
                    ll_add_number.setVisibility(View.VISIBLE);
                    ll_add_number_btn.setVisibility(View.GONE);
                    lv_number.setVisibility(View.VISIBLE);
                    setHightOfListView();
                } else {
                    if (!outoftime)
                        ll_add_number_btn.setVisibility(View.VISIBLE);
                }

                if (!Utility.isEmpty(mDetail.getIs_add()) && mDetail.getIs_add().equals("0")) {
                    ll_add_number_btn.setVisibility(View.GONE);
                    ll_account_name.setVisibility(View.GONE);
                }

            }
        } else if (SCAN_ACCESS_GET.equals(sname)) {
            try {
                JSONObject jb = result.optJSONObject("retArr");
                if (jb != null) {
                    Gson gson = new Gson();
                    ScanScope ss = gson.fromJson(jb.toString(), ScanScope.class);
                    SkuaidiSpf.saveUserScanScope(context, ss);
                    canLanPice = !(ss.getSj() == null || ss.getSj().getAccess() == 0);
                    setVisibilityOfUpload();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else if (E3SysManager.SCAN_TO_QF.equals(sname) || E3SysManager.SCAN_TO_E3.equals(sname) || E3SysManager.SCAN_TO_ZT.equals(sname)) {
            dismissProgressDialog();
            UtilToolkit.showToast(result.optString("retStr"));
            for (NotifyInfo info : mList) {
                info.setIsUpload(1);
            }

            updateIsUploadStatus();
            mAdapter.notifyDataSetChanged();
        } else if (UPLOAD_EXPRESS_UPDATE.equals(sname)) {

        } else if (E3SysManager.SCAN_QF_VERIFY.equals(sname) || E3SysManager.SCAN_COUNTERMAN_VERIFY.equals(sname)
                || E3SysManager.SCAN_ZT_VERIFY.equals(sname)) {

            int state = result.optInt("verified");
            if (state == 0) {
                canLanPice = false;
            } else if (state == 1) {// 已通过审核
//				db = BackUpService.getfinalDb();
//				List<CourierReviewInfo> infos = db.findAllByWhere(CourierReviewInfo.class,
//						"courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
                //if (infos == null || infos.size() == 0 || infos.get(0).getIsThroughAudit() != 1) {// 但是数据库不不存在用户信息，解析保存
                //ArrayList<CourierReviewInfo> courierInfos = new ArrayList<CourierReviewInfo>();
                String retStr = result.optString("retStr");
                JSONObject courier_infos = result.optJSONObject("retArr");

                if (KuaiBaoStringUtilToolkit.isEmpty(retStr)) {
                    Iterator<String> iterator = courier_infos.keys();
                    int index = 0;
                    while (iterator.hasNext()) {
                        if (index == 1) {
                            break;
                        }
                        String key = iterator.next();
                        JSONObject info = courier_infos.optJSONObject(key);
                        if (info == null)
                            continue;
                        try {
                            info.put("isThroughAudit", 1);
                            UpdateReviewInfoUtil.updateCurrentReviewStatus(info.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        index++;
//							CourierReviewInfo reviewInfo = new CourierReviewInfo();
//							reviewInfo.setCourierPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
//							reviewInfo.setCourierJobNo(info.optString("counterman_code"));
//							reviewInfo.setCourierName(info.optString("counterman_name"));
//							reviewInfo.setCourierLatticePoint(info.optString("shop_name"));
//							reviewInfo.setCourierLatticePointId(info.optString("branch_id"));
//							courierInfos.add(reviewInfo);
                    }
//						for (int i = 0; i < courierInfos.size(); i++) {
//							courierInfos.get(i).setIsThroughAudit(1);
//							db.save(courierInfos.get(i));
//						}

                }
                //}
                canLanPice = true;
            }
            setVisibilityOfUpload();
        } else if (sname.equals("IDCard/getAllInfo")) {
            String order = "";
            for (int i = 0; i < mList.size(); i++) {
                order = order + mList.get(i).getExpress_number() + ",";
            }
            if (!Utility.isEmpty(order) && order.length() > 1) {
                order = order.substring(0, order.length() - 1);
            }
            realNameInfoObj = new RealNameInfo();
            try {
                realNameInfoObj.setName(result.getString("name"));
                realNameInfoObj.setSex(result.getString("sex"));
                realNameInfoObj.setNation(result.getString("nation"));
                realNameInfoObj.setBorn(result.getString("born"));
                realNameInfoObj.setAddress(result.getString("address"));
                realNameInfoObj.setIdno(result.getString("idno"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIntent = new Intent(context, ShowImageActivity.class);
            mIntent.putExtra("money", money);
            mIntent.putExtra("time", time);
            mIntent.putExtra("buyer_methor", buyer_methor);
            mIntent.putExtra("buyer_openid", buyer_openid);
            mIntent.putExtra("order", order);
            mIntent.putExtra("order_number", order_number);
            mIntent.putExtra("realNameInfo", realNameInfoObj);
            startActivityForResult(mIntent, REQUEST_CODE_STARTACTIVITY);
        }
    }

    private void notifyDataAndViewChanged() {
        mAdapter.notifyDataSetChanged();
        setHightOfListView();
    }

    /**
     * 设置收件扫描按钮的可见性
     */
    private void setVisibilityOfUpload() {
        // 有收件权限，并且有未上传的单号
        if (uploadable && canLanPice && !outoftime) {
            tv_upload_lanpie.setVisibility(View.VISIBLE);
        } else {
            tv_upload_lanpie.setVisibility(View.GONE);
        }
    }

    /**
     * 根据ListView 数据条数，设置ListView 的高度
     */
    private void setHightOfListView() {
        int contentHeight = lv_number.getCount() * Utility.dip2px(context, 44);
        if (contentHeight >= height) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            lv_number.setLayoutParams(params);
        } else {
            LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lv_number.setLayoutParams(p);
        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (UPLOAD_EXPRESS_DEL.equals(sname)) {
            UtilToolkit.showToast(result);
        } else if (UPLOAD_EXPRESS.equals(sname)) {
            UtilToolkit.showToast(result);
        } else if (E3SysManager.SCAN_TO_QF.equals(sname) || E3SysManager.SCAN_TO_E3.equals(sname) || E3SysManager.SCAN_TO_ZT.equals(sname)) {
            dismissProgressDialog();
            UtilToolkit.showToast(result);
        } else if (UPLOAD_EXPRESS_UPDATE.equals(sname)) {
            UtilToolkit.showToast(result);
        } else if (E3SysManager.SCAN_COUNTERMAN_VERIFY.equals(sname) || E3SysManager.SCAN_QF_VERIFY.equals(sname)
                || (sname.equals(E3SysManager.SCAN_ZT_VERIFY))) {
            if (!result.equals(HttpHelper.FAILD_INFO) && !result.equals(HttpHelper.TIME_OUT)) {
                canLanPice = false;
                setVisibilityOfUpload();
            }
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void dedete(int index) {
        deleteCollectionExpress(index);
    }

    @Override
    public void onGlobalLayout() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels - layout_title.getHeight() - rl_money.getHeight() - ll_acount.getHeight() - Utility.dip2px(context, 19)
                - Utility.dip2px(context, 44) - Utility.dip2px(context, 30) - 100;
        setHightOfListView();
    }

    @OnClick({R.id.btn_cj_realname})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cj_realname:// 采集实名信息按钮
                if (null == mList || 0 == mList.size()) {
                    UtilToolkit.showToast("请先添加单号");
                } else {
                    cameraIdentifyIdCard();
                }
                break;

            default:
                break;
        }

    }

    //相机识别身份证
    private void cameraIdentifyIdCard() {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("type", false);
        intent.putExtra("box", true);
        startActivityForResult(intent, Constants.CAMREA_IDENTIFY_IDCARD);
    }

}
