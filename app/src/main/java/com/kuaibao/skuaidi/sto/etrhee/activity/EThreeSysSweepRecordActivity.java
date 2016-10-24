package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.adapter.EThreeSysSweepRecordAdapter;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.PositiveButtonOnclickListener;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.ScanScope;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.E3ScanDeleteActivity;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * E3系统扫描记录
 *
 * @author xy
 */

public class EThreeSysSweepRecordActivity extends SkuaiDiBaseActivity implements OnClickListener, OnItemClickListener {
    private static final int REQUEST = 100;
    private Context context;
    private View back, more, ll_lanPice, ll_faPice, ll_piePice, ll_signedPice, ll_badPice, ll_daoPice, ll_rejectPice, ll_third_brunch, line_lanPice,
            line_faPice, line_piePice, line_signedPice, line_badPice, line_daoPice, line_rejectPice, line_third_brunch;
    private TextView tv_lanPice, tv_faPice, tv_piePice, tv_signedPice, tv_badPice, tv_rejectPice, tv_daoPice, tv_third_brunch,
            tv_sweepRecordContent;
    private TextView tv_count_lan, tv_count_fa, tv_count_pie, tv_count_dao, tv_count_signed, tv_count_bad,
            tv_count_reject, tv_count_third_brunch;
    private List<E3_order> list_lanPice = new ArrayList<>();
    private List<E3_order> list_faPice = new ArrayList<>();
    private List<E3_order> list_piePice = new ArrayList<>();
    private List<E3_order> list_signedPice = new ArrayList<>();
    private List<E3_order> list_badPice = new ArrayList<>();
    private List<E3_order> list_daoPice = new ArrayList<>();
    private List<E3_order> list_rejectPice = new ArrayList<>();// 退件列表
    private List<E3_order> list_thirdBrunch = new ArrayList<>();// 退件列表


    private ListView lv_ethreeinfos;
    private EThreeSysSweepRecordAdapter adapter;
    private PullToRefreshView pullToRefreshView;
    private int checkedIndex = 1;

    /**
     * 单号类型
     */
    private int type = 1;
    /**
     * 加载的页号
     */
    private int page_num = 1;
    /**
     * 每页加载的数据条数
     */
    private final int page_size = 20;
    private String dataTypeflag;
    private int color_b;
    private int color_c;
    private int color_d;
    private String dataType;

    private int todayCount_lan;// 当天扫描收件总数
    private int todayCount_fa;
    private int todayCount_dao;
    private int todayCount_pie;
    private int todayCount_signed;
    private int todayCount_bad;
    private int todayCount_reject;
    private int todayCount_third;
    public String courierNO = "";
    public String brand = "";
    private CourierReviewInfo reviewInfo;
    private final ArrayList<String> numberList = new ArrayList<>();
    private final ArrayList<String> statusList = new ArrayList<>();
    private static final int MAX_SIGNED_PIC = 3;
    /**
     * 包含图片的签收件
     */
    private final List<E3_order> picSignInfos = new ArrayList<>();

    /**
     * 可上传单号
     */
    private final List<E3_order> usualInfos = new ArrayList<>();


    /**
     * 是否包含图片签收
     */
    private boolean signed_pic = false;

    /***
     * 是否忽略重复
     */
    private boolean ignore = false;


    private Ringtone ringtone;// 拦截件语音提示
    private ScanScope ss;
    private TextView tv_scan_one, tv_scan_two;
    private LinearLayout ll_new_operate;
    private int checkCount_lan = 0;// 扫描记录 收件 选中的item数目
    private int checkCount_dao = 0;
    private int checkCount_pie = 0;
    private int checkCount_bad = 0;
    private final ArrayList<String> picPathList = new ArrayList<>();// 所有单号对应的图片地址

    private List<String> successList = new ArrayList<>();//上传成功的单号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        brand = SkuaidiSpf.getLoginUser().getExpressNo();
        reviewInfo = E3SysManager.getReviewInfo();
        courierNO = reviewInfo.getCourierJobNo();

        if ("zt".equals(brand)) {
            setContentView(R.layout.ethree_sys_wseep_record_layout1);
        } else {
            setContentView(R.layout.ethree_sys_wseep_record_layout);
        }
        color_b = SkuaidiSkinManager.getTextColor("main_color");
        color_c = getResources().getColor(R.color.text_hint);
        color_d = SkuaidiSkinManager.getTextColor("btn_gray1");
        Intent intent = getIntent();
        dataTypeflag = intent.getStringExtra("dataType");
        initView();
        if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录
            getDatas(1, 1, 20);
            setDatas();
            setListener();
            ss = SkuaidiSpf.getUserScanScope(context);
        } else {// 数据上传，上传提醒 入口
            setLocalData();
            setListener();
        }
    }


    /**
     * 从数据库查询本地数据
     */
    private void setLocalData() {

        list_lanPice = E3OrderDAO.queryOrderByType("收件", brand, courierNO);
        list_faPice = E3OrderDAO.queryOrderByType("发件", brand, courierNO);
        list_piePice = E3OrderDAO.queryOrderByType("派件", brand, courierNO);
        list_signedPice = E3OrderDAO.queryOrderByType("签收件", brand, courierNO);
        list_badPice = E3OrderDAO.queryOrderByType("问题件", brand, courierNO);
        list_daoPice = E3OrderDAO.queryOrderByType("到件", brand, courierNO);
        list_thirdBrunch = E3OrderDAO.queryOrderByType("第三方签收", brand, courierNO);
        list_rejectPice = null;

        if (list_lanPice != null && list_lanPice.size() != 0) {

            tv_count_lan.setVisibility(TextView.VISIBLE);
            tv_count_lan.setText("" + list_lanPice.size());
        }
        if (list_faPice != null && list_faPice.size() != 0) {

            tv_count_fa.setVisibility(TextView.VISIBLE);
            tv_count_fa.setText("" + list_faPice.size());
        }
        if (list_piePice != null && list_piePice.size() != 0) {

            tv_count_pie.setVisibility(TextView.VISIBLE);
            tv_count_pie.setText("" + list_piePice.size());
        }
        if (list_signedPice != null && list_signedPice.size() != 0) {

            tv_count_signed.setVisibility(TextView.VISIBLE);
            tv_count_signed.setText("" + list_signedPice.size());
        }
        if (list_badPice != null && list_badPice.size() != 0) {

            tv_count_bad.setVisibility(TextView.VISIBLE);
            tv_count_bad.setText("" + list_badPice.size());
        }
        if (list_daoPice != null && list_daoPice.size() != 0) {

            tv_count_dao.setVisibility(TextView.VISIBLE);
            tv_count_dao.setText("" + list_daoPice.size());
        }
        if (list_thirdBrunch != null && list_thirdBrunch.size() != 0) {

            tv_count_third_brunch.setVisibility(TextView.VISIBLE);
            tv_count_third_brunch.setText("" + list_thirdBrunch.size());
        }

        // 初始化页面显示顺序
        if (list_lanPice != null && list_lanPice.size() != 0) {
            tv_lanPice.setTextColor(color_b);
            line_lanPice.setBackgroundColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            line_daoPice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            tv_daoPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 1;
            getDataStatus(list_lanPice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_lanPice, "收件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_faPice != null && list_faPice.size() != 0) {
            tv_faPice.setTextColor(color_b);
            line_faPice.setBackgroundColor(color_b);

            line_daoPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_daoPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 6;
            getDataStatus(list_faPice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_faPice, "发件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_piePice != null && list_piePice.size() != 0) {
            tv_piePice.setTextColor(color_b);
            line_piePice.setBackgroundColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            line_daoPice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            tv_daoPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 2;
            getDataStatus(list_piePice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_piePice, "派件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_daoPice != null && list_daoPice.size() != 0) {
            tv_daoPice.setTextColor(color_b);
            line_daoPice.setBackgroundColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 5;
            getDataStatus(list_daoPice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_daoPice, "到件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_signedPice != null && list_signedPice.size() != 0) {
            tv_signedPice.setTextColor(color_b);
            line_signedPice.setBackgroundColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            line_daoPice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            tv_daoPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 3;
            getDataStatus(list_signedPice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_signedPice, "签收件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_badPice != null && list_badPice.size() != 0) {
            line_badPice.setBackgroundColor(color_b);
            tv_badPice.setTextColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            line_daoPice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            tv_daoPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 4;
            getDataStatus(list_badPice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_badPice, "问题件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_rejectPice != null && list_rejectPice.size() != 0) {
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_b);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            line_daoPice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            tv_daoPice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_c);

            type = 7;
            getDataStatus(list_rejectPice);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_rejectPice, "退件");
            lv_ethreeinfos.setAdapter(adapter);
        } else if (list_thirdBrunch != null && list_thirdBrunch.size() != 0) {
            if (tv_third_brunch != null)
                tv_third_brunch.setTextColor(color_b);
            if (line_third_brunch != null)
                line_third_brunch.setBackgroundColor(color_b);

            line_faPice.setBackgroundColor(color_d);
            line_lanPice.setBackgroundColor(color_d);
            line_piePice.setBackgroundColor(color_d);
            line_badPice.setBackgroundColor(color_d);
            line_daoPice.setBackgroundColor(color_d);
            line_signedPice.setBackgroundColor(color_d);
            if (line_rejectPice != null)
                line_rejectPice.setBackgroundColor(color_d);

            tv_faPice.setTextColor(color_c);
            tv_lanPice.setTextColor(color_c);
            tv_piePice.setTextColor(color_c);
            tv_badPice.setTextColor(color_c);
            tv_daoPice.setTextColor(color_c);
            tv_signedPice.setTextColor(color_c);
            if (tv_rejectPice != null)
                tv_rejectPice.setTextColor(color_c);

            type = 8;
            getDataStatus(list_thirdBrunch);
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_thirdBrunch, "第三方签收");
            lv_ethreeinfos.setAdapter(adapter);
        } else {
            adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_lanPice, "收件");
        }
        back.setOnClickListener(this);
        ll_lanPice.setOnClickListener(this);
        ll_faPice.setOnClickListener(this);
        ll_piePice.setOnClickListener(this);
        ll_signedPice.setOnClickListener(this);
        ll_badPice.setOnClickListener(this);
        ll_daoPice.setOnClickListener(this);
        if (ll_third_brunch != null)
            ll_third_brunch.setOnClickListener(this);
        more.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
    }

    private void initView() {


//        ll_bar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
////                ll_bar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                if (getResources().getDisplayMetrics().widthPixels > ll_bar.getWidth()) {
//                    for (int i = 0, j = ll_bar.getChildCount(); i < j; i++) {
//                        View view = ll_bar.getChildAt(i);
//                        view.setLayoutParams(new LinearLayout.LayoutParams(0, view.getLayoutParams().height));
//                        ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = 1;
//                        ll_bar.removeViewAt(i);
//                        ll_bar.addView(view,i);
//                    }
//                }
//                UtilToolkit.showToast("宽度：" + ll_bar.getWidth());
//                return true;
//            }
//        });
        back = findViewById(R.id.iv_title_back);
        more = findViewById(R.id.customer_manager_more);
        lv_ethreeinfos = (ListView) findViewById(R.id.lv_ethreeinfos);

        ll_lanPice = findViewById(R.id.ll_lanPice);
        ll_faPice = findViewById(R.id.ll_faPice);
        ll_piePice = findViewById(R.id.ll_piePice);
        ll_signedPice = findViewById(R.id.ll_signedPice);
        ll_badPice = findViewById(R.id.ll_badPice);
        ll_daoPice = findViewById(R.id.ll_daoPice);
        ll_rejectPice = findViewById(R.id.ll_rejectPice);
        ll_third_brunch = findViewById(R.id.ll_third_brunch);

        line_lanPice = findViewById(R.id.line_lanPice);
        line_faPice = findViewById(R.id.line_faPice);
        line_piePice = findViewById(R.id.line_piePice);
        line_signedPice = findViewById(R.id.line_signedPice);
        line_badPice = findViewById(R.id.line_badPice);
        line_daoPice = findViewById(R.id.line_daoPice);
        line_rejectPice = findViewById(R.id.line_rejectPice);
        line_third_brunch = findViewById(R.id.line_third_brunch);

        tv_lanPice = (TextView) findViewById(R.id.tv_lanPice);
        tv_faPice = (TextView) findViewById(R.id.tv_faPice);
        tv_piePice = (TextView) findViewById(R.id.tv_piePice);
        tv_signedPice = (TextView) findViewById(R.id.tv_signedPice);
        tv_badPice = (TextView) findViewById(R.id.tv_badPice);
        tv_daoPice = (TextView) findViewById(R.id.tv_daoPice);
        tv_rejectPice = (TextView) findViewById(R.id.tv_rejectPice);
        tv_third_brunch = (TextView) findViewById(R.id.tv_third_brunch);

        tv_count_lan = (TextView) findViewById(R.id.tv_count_lan);
        tv_count_fa = (TextView) findViewById(R.id.tv_count_fa);
        tv_count_pie = (TextView) findViewById(R.id.tv_count_pie);
        tv_count_dao = (TextView) findViewById(R.id.tv_count_dao);
        tv_count_signed = (TextView) findViewById(R.id.tv_count_signed);
        tv_count_bad = (TextView) findViewById(R.id.tv_count_bad);
        tv_count_reject = (TextView) findViewById(R.id.tv_count_reject);
        tv_count_third_brunch = (TextView) findViewById(R.id.tv_count_third_brunch);

        tv_sweepRecordContent = (TextView) findViewById(R.id.tv_sweep_record_content);
        LinearLayout ll_upload = (LinearLayout) findViewById(R.id.ll_upload);
        TextView tv_upload = (TextView) findViewById(R.id.tv_upload);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        tv_scan_one = (TextView) findViewById(R.id.tv_scan_one);
        tv_scan_two = (TextView) findViewById(R.id.tv_scan_two);
        ll_upload.setClickable(false);
        // 显示本地数据，禁用下拉，s上拉。
        if (!"sweepRecord".equals(dataTypeflag)) {
            pullToRefreshView.disableScroolUp();
            ll_upload.setClickable(true);
            tv_upload.setVisibility(TextView.VISIBLE);
            TextView title = (TextView) findViewById(R.id.tv_title_des);
            title.setText("数据上传");
            more.setVisibility(TextView.GONE);
            tv_sweepRecordContent.setVisibility(View.GONE);
        } else {
            ll_upload.setVisibility(View.GONE);
            tv_scan_one.setText("问题件(0)");
            tv_scan_two.setText("签收扫描(0)");
            LinearLayout ll_operate = (LinearLayout) findViewById(R.id.ll_operate);
            ll_operate.setVisibility(View.VISIBLE);
            ll_new_operate = (LinearLayout) findViewById(R.id.ll_new_operate);
        }

        tv_lanPice.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
        line_lanPice.setBackgroundColor(SkuaidiSkinManager.getTextColor("main_color"));
        if ("qf".equals(brand)) {
            ll_faPice.setVisibility(View.GONE);
            ll_daoPice.setVisibility(View.GONE);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            int width = display.getWidth() / 3;
            ll_lanPice.getLayoutParams().width = width;
            ll_piePice.getLayoutParams().width = width;
            ll_signedPice.getLayoutParams().width = width;
        } else if ("zt".equals(brand)) {
            ll_faPice.setVisibility(View.GONE);
            ll_daoPice.setVisibility(View.GONE);
        }

        if (E3SysManager.BRAND_ZT.equals(brand)) {
            final LinearLayout ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            ll_bar.measure(w, h);
            int height = ll_bar.getMeasuredHeight();
            int measuredWidth = ll_bar.getMeasuredWidth();

            if (getResources().getDisplayMetrics().widthPixels > measuredWidth) {
                for (int i = 0, j = ll_bar.getChildCount(); i < j; i++) {
                    View view = ll_bar.getChildAt(i);
                    LinearLayout.LayoutParams params = ((LinearLayout.LayoutParams) view.getLayoutParams());
                    params.weight = 1;
                    params.width = 0;
                    view.setLayoutParams(params);

                }
            }
        }

    }

    /**
     * 请求网络数据
     *
     * @param type      数据类型
     * @param page_num  页号
     * @param page_size 每一页的数量
     */
    private void getDatas(int type, int page_num, int page_size) {

        switch (type) {
            case 1:
                if (list_lanPice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 2:
                if (list_piePice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 3:
                if (list_signedPice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 4:
                if (list_badPice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 5:
                if (list_daoPice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 6:
                if (list_faPice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 7:
                if (list_rejectPice.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
            case 8:
                if (list_thirdBrunch.isEmpty())
                    showProgressDialog( "数据加载中....");
                break;
        }
        JSONObject data = new JSONObject();
        try {
            if ("qf".equals(brand)) {
                data.put("sname", E3SysManager.SCAN_QF_LIST);
            } else if ("sto".equals(brand)) {
                data.put("sname", E3SysManager.SCAN_E3_LIST);
            } else if ("zt".equals(brand)) {
                data.put("sname", E3SysManager.SCAN_ZT_LIST);
            }
            data.put("type", type);
            data.put("page_num", page_num);
            data.put("page_size", page_size);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 初始化 adapter
     */
    private void setDatas() {
        adapter = new EThreeSysSweepRecordAdapter(dataTypeflag, context, list_lanPice, "收件");
        lv_ethreeinfos.setAdapter(adapter);
    }

    /**
     * 控件设置监听器
     */
    private void setListener() {
        if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录
            back.setOnClickListener(this);
            ll_lanPice.setOnClickListener(this);
            ll_faPice.setOnClickListener(this);
            ll_piePice.setOnClickListener(this);
            ll_signedPice.setOnClickListener(this);
            ll_badPice.setOnClickListener(this);
            ll_daoPice.setOnClickListener(this);
            if (ll_third_brunch != null) {
                ll_third_brunch.setOnClickListener(this);
            }
            if (ll_rejectPice != null)
                ll_rejectPice.setOnClickListener(this);
            more.setOnClickListener(this);

            pullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

                @Override
                public void onFooterRefresh(PullToRefreshView view) {
                    isPullup = true;
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (Utility.isNetworkConnected()) {
                                page_num += 1;
                                getDatas(type, page_num, page_size);
                            }

                        }
                    }, 1000);

                }
            });
        }

        pullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                pullToRefreshView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isNetworkConnected()) {
                            if (!"sweepRecord".equals(dataTypeflag)) {// 数据上传
                                switch (type) {
                                    case 1:
                                        if (list_lanPice.size() != 0)
                                            getDataStatus(list_lanPice);
                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 2:
                                        if (list_piePice.size() != 0)
                                            getDataStatus(list_piePice);
                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 3:
                                        if (list_signedPice.size() != 0)
                                            getDataStatus(list_signedPice);
                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 4:
                                        if (list_badPice.size() != 0)
                                            getDataStatus(list_badPice);
                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 5:
                                        if (list_daoPice.size() != 0)
                                            getDataStatus(list_daoPice);
                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 6:
                                        if (list_faPice.size() != 0)
                                            getDataStatus(list_faPice);

                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 7:
                                        if (list_rejectPice.size() != 0)
                                            getDataStatus(list_rejectPice);

                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    case 8:
                                        if (list_thirdBrunch.size() != 0)
                                            getDataStatus(list_thirdBrunch);

                                        else
                                            pullToRefreshView.onHeaderRefreshComplete();
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                page_num = 1;
                                getDatas(type, page_num, page_size);
                            }

                        } else {
                            pullToRefreshView.onHeaderRefreshComplete();
                        }
                    }
                }, 1000);
            }
        });
        lv_ethreeinfos.setOnItemClickListener(this);

    }

    private boolean isPullup = false;// 上拉加载更多

    private void showFailDialog(String str) {
        SkuaidiDialogGrayStyle sdialog = new SkuaidiDialogGrayStyle(context);
        sdialog.setTitleGray("温馨提示");
        if ("404".equals(str)) {
            sdialog.setContentGray("请求失败，请重试！");
        } else {
            sdialog.setContentGray(str);
        }
        sdialog.isUseMiddleBtnStyle(true);
        sdialog.setMiddleButtonTextGray("我知道了");
        sdialog.showDialogGray(lv_ethreeinfos.getRootView());
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String data, String act) {
        dismissProgressDialog();
        JSONObject result = null;
        try {
            result = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == null) {
            pullToRefreshView.onFooterRefreshComplete();
            pullToRefreshView.onHeaderRefreshComplete();
            return;
        }

        if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {// 数据上传
            String orderType = "";
            if (adapter.getList() != null && adapter.getList().size() != 0)
                orderType = adapter.getList().get(0).getType();
            ResponseData responseData = JSON.parseObject(data, ResponseData.class);
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
                        for (E3_order info : adapter.getList()) {
                            if (info.getOrder_number().equals(error.getWaybillNo())) {
                                info.setError(true);
                                info.setErrorMsg(error.getReason());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                successList = uploadResutl.getSuccess();
            }


            if (signed_pic) {
                for (int i = 0; i < picSignInfos.size(); i++) {
                    E3_order order = picSignInfos.get(i);
                    if (successList != null && !successList.contains(order.getOrder_number()))
                        continue;

                    if (picPathList.size() == 1) {
                        E3SysManager.deletePic(order.getPicPath());
                    } else {
                        picPathList.remove(order.getPicPath());
                        if (!picPathList.contains(order.getPicPath())) {
                            // 上传成功，删除图片
                            E3SysManager.deletePic(order.getPicPath());
                        }
                    }
                    adapter.getList().remove(order);
                    usualInfos.remove(order);
                    E3OrderDAO.updateOrder(Collections.singletonList(order), brand, courierNO);
                    adapter.notifyDataSetChanged();
                    successList.remove(order.getOrder_number());
                }
                for (String number : successList) {
                    for (E3_order info:adapter.getList()){
                        if(info.getOrder_number().equals(number)){
                            usualInfos.remove(info);
                            adapter.getList().remove(info);
                            E3OrderDAO.updateOrder(Collections.singletonList(info), brand, courierNO);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                if (adapter.getUploadAbleList().size() > 0) {// 图片签收上传，一次最多包含三单
                    picSignInfos.clear();
                    uploadDatas(adapter.getUploadAbleList(), null);
                    return;
                }
            } else {
                try {
                    for (E3_order order : usualInfos) {
                        if (!successList.contains(order.getOrder_number())) continue;//失败的单号不处理

                        E3OrderDAO.updateOrder(Collections.singletonList(order), brand, courierNO);
                        if (usualInfos.size() != 0) {
                            adapter.getList().remove(order);
                        }
                    }
                    adapter.notifyDataSetChanged();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if (adapter.getList().size() == 0) {
                if ("收件".equals(orderType)) {
                    tv_count_lan.setVisibility(TextView.GONE);
                } else if ("发件".equals(orderType)) {
                    tv_count_fa.setVisibility(TextView.GONE);
                } else if ("到件".equals(orderType)) {
                    tv_count_dao.setVisibility(TextView.GONE);
                } else if ("派件".equals(orderType)) {
                    tv_count_pie.setVisibility(TextView.GONE);
                } else if ("签收件".equals(orderType)) {
                    tv_count_signed.setVisibility(TextView.GONE);
                } else if ("问题件".equals(orderType)) {
                    tv_count_bad.setVisibility(TextView.GONE);
                } else if ("退件".equals(orderType)) {
                    tv_count_reject.setVisibility(TextView.GONE);
                } else if ("第三方签收".equals(orderType)) {
                    tv_count_third_brunch.setVisibility(TextView.GONE);
                }
                UtilToolkit.showToast(result.optString("retStr"));
            } else {
                showFailDialog(responseData.getDesc());
            }

            dismissProgressDialog();
            UtilToolkit.showToast(result.optString("retStr"));
            return;
        } else if (E3SysManager.SCAN_TO_QF.equals(sname)) {
            ArrayList<E3_order> ordersInfos = new ArrayList<>();// 上传成功的单号
            try {
                String orderType;
                if (usualInfos == null || usualInfos.size() == 0) {
                    return;
                } else
                    orderType = usualInfos.get(0).getType();
                for (int i = usualInfos.size() - 1; i >= 0; i--) {
                    if (result.optJSONObject(usualInfos.get(i).getOrder_number()) == null) {// 其他异常
                        if ("fail".equals(result.optString("status"))) {
                            dismissProgressDialog();
                            showFailDialog(result.optString("desc"));
                            return;
                        }
                    }
                    E3_order ordersInfo;
                    String status = result.optJSONObject(usualInfos.get(i).getOrder_number()).optString("status");
                    if ("success".equals(status)) {

                        ordersInfo = usualInfos.get(i);
                        ordersInfos.add(ordersInfo);
                        ordersInfo.setIsUpload(1);
                        ordersInfo.setIsCache(0);
                        // 上传成功，清楚缓存
                        E3OrderDAO.deleteCacheOrders(new ArrayList<>(Collections.singletonList(ordersInfo)));
                        E3OrderDAO.addOrders(new ArrayList<>(Collections.singletonList(ordersInfo)), brand, courierNO);// 保存

                        usualInfos.remove(ordersInfo);
                        adapter.getList().remove(ordersInfo);
                    }
                }
                adapter.notifyDataSetChanged();
                if (usualInfos.size() == 0) {
                    UtilToolkit.showToast("提交成功！");
                    if ("收件".equals(orderType)) {
                        tv_count_lan.setVisibility(TextView.GONE);
                    } else if ("发件".equals(orderType)) {
                        tv_count_fa.setVisibility(TextView.GONE);
                    } else if ("到件".equals(orderType)) {
                        tv_count_dao.setVisibility(TextView.GONE);
                    } else if ("派件".equals(orderType)) {
                        tv_count_pie.setVisibility(TextView.GONE);
                    } else if ("签收件".equals(orderType)) {
                        tv_count_signed.setVisibility(TextView.GONE);
                    } else if ("问题件".equals(orderType)) {
                        tv_count_bad.setVisibility(TextView.GONE);
                    }

                } else if (ordersInfos.size() > 0 && usualInfos.size() > 0) {
//                    UtilToolkit.showToast("部分提交失败！");
                    showFailDialog("部分提交失败！");
                    if ("收件".equals(orderType)) {
                        tv_count_lan.setText(numberList.size() - adapter.getList().size() + "");
                    } else if ("发件".equals(orderType)) {
                        tv_count_fa.setText(numberList.size() - adapter.getList().size() + "");
                    } else if ("到件".equals(orderType)) {
                        tv_count_dao.setText(numberList.size() - adapter.getList().size() + "");
                    } else if ("派件".equals(orderType)) {
                        tv_count_pie.setText(numberList.size() - adapter.getList().size() + "");
                    } else if ("签收件".equals(orderType)) {
                        tv_count_signed.setText(numberList.size() - adapter.getList().size() + "");
                    } else if ("问题件".equals(orderType)) {
                        tv_count_bad.setText(numberList.size() - adapter.getList().size() + "");
                    }
                } else {
                    showFailDialog("提交失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismissProgressDialog();
            return;
        } else if (E3SysManager.EXPRESS_END_STATUS.equals(sname)) {
            if (!TextUtils.isEmpty(result.toString())) {

                if (numberList.size() == 1) {
                    try {
                        String status = result.optJSONObject("retArr").optJSONObject(numberList.get(0))
                                .optString("express_status");
                        statusList.add(status);
                    } catch (Exception e) {
                        e.printStackTrace();
                        pullToRefreshView.onHeaderRefreshComplete();
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
                            pullToRefreshView.onHeaderRefreshComplete();
                            return;
                        }

                    }
                }
                if (adapter.getList() != null) {
                    for (int i = 0; i < adapter.getList().size(); i++) {
                        try {
                            if (!TextUtils.isEmpty(statusList.get(i)) && !"null".equals(statusList.get(i)))
                                adapter.getList().get(i).setServerType(statusList.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }

                    }
                }
                adapter.notifyDataSetChanged();

            }
            pullToRefreshView.onHeaderRefreshComplete();
            dismissProgressDialog();
            return;
        }

        pullToRefreshView.onFooterRefreshComplete();
        pullToRefreshView.onHeaderRefreshComplete();

        int indexFlag = checkedIndex;
        if (type == 1) {
            checkedIndex = 1;
        } else if (type == 2) {
            checkedIndex = 3;
        } else if (type == 3) {
            checkedIndex = 5;
        } else if (type == 4) {
            checkedIndex = 6;
        } else if (type == 5) {
            checkedIndex = 4;
        } else if (type == 6) {
            checkedIndex = 2;
        } else if (type == 7) {
            checkedIndex = 7;
        } else if (type == 8) {
            checkedIndex = 8;
        }

        if (E3SysManager.SCAN_E3_LIST.equals(sname) || E3SysManager.SCAN_QF_LIST.equals(sname)
                || E3SysManager.SCAN_ZT_LIST.equals(sname)) {// 扫描记录

            List<E3_order> infos = new ArrayList<>();
            if (result.optJSONObject("retArr") == null) {
                dismissProgressDialog();
                return;
            }
            JSONArray array = result.optJSONObject("retArr").optJSONArray("scanList");
            if (array == null && checkedIndex == indexFlag) {
                dismissProgressDialog();
                return;
            } else {
                if (array != null && array.length() != 0) {
                    for (int i = 0; i < array.length(); i++) {
                        E3_order info = new E3_order();
                        JSONObject object = array.optJSONObject(i);
                        info.setScan_time(object.optString("create_time"));
                        if (type == 3) {
                            info.setType_extra(object.optString("sign_type"));
                            info.setType(object.optString("express_status"));
                        } else if (type == 4) {
                            info.setType_extra(object.optString("bad_waybill_type"));
                            info.setType(object.optString("express_status"));
                            info.setProblem_desc(object.optString("question_desc"));
                            info.setWayBillType_E3(object.optString("bad_waybill_code"));
                            info.setBad_waybill_status(object.optString("bad_waybill_status"));
                        } else if (type == 1) {
                            info.setType(object.optString("express_status"));
                            info.setType_extra(object.optString("operator_name"));
                        } else if (type == 2) {
                            info.setType(object.optString("express_status"));
                            info.setType_extra(object.optString("operator_name"));
                        } else if (type == 5) {
                            info.setType(object.optString("express_status"));
                            info.setType_extra(object.optString("operator_name"));
                        } else if (type == 6) {
                            info.setType(object.optString("express_status"));
                            info.setType_extra(object.optString("operator_name"));
                        } else if (type == 7) {
                            info.setType(object.optString("express_status"));
                        } else if (type == 8) {
                            info.setThirdBranch(object.optString("sign_type"));
                            info.setType(object.optString("express_status"));
                        }
                        info.setOrder_number(object.optString("waybill_no"));
                        infos.add(info);
                    }
                }

            }

            if (type == 1) {
                if (!isPullup) {// 下拉刷新
                    list_lanPice.clear();
                }
                list_lanPice.addAll(infos);
                todayCount_lan = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描收件：" + todayCount_lan);
            } else if (type == 2) {
                if (!isPullup) {
                    list_piePice.clear();
                }
                list_piePice.addAll(infos);
                todayCount_pie = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描派件：" + todayCount_pie);

            } else if (type == 3) {
                if (!isPullup) {
                    list_signedPice.clear();
                }
                list_signedPice.addAll(infos);
                todayCount_signed = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描签收件：" + todayCount_signed);
            } else if (type == 4) {
                if (!isPullup) {
                    list_badPice.clear();
                }
                list_badPice.addAll(infos);
                todayCount_bad = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描问题件：" + todayCount_bad);

            } else if (type == 5) {
                if (!isPullup) {
                    list_daoPice.clear();
                }
                list_daoPice.addAll(infos);
                todayCount_dao = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描到件：" + todayCount_dao);

            } else if (type == 6) {
                if (!isPullup) {
                    list_faPice.clear();
                }
                list_faPice.addAll(infos);
                todayCount_fa = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描发件：" + todayCount_fa);
            } else if (type == 7) {
                if (!isPullup) {
                    list_rejectPice.clear();
                }
                list_rejectPice.addAll(infos);
                todayCount_reject = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描退件：" + todayCount_reject);
            } else if (type == 8) {
                if (!isPullup) {
                    list_thirdBrunch.clear();
                }
                list_thirdBrunch.addAll(infos);
                todayCount_third = result.optJSONObject("retArr").optInt("todayCount");
                tv_sweepRecordContent.setText("今日共扫描第三方签收件：" + todayCount_third);
            }
            dismissProgressDialog();
            if (!isPullup) {// 下拉刷新
                adapter.shouldResetCheckedStatus = true;

            } else {
                isPullup = false;
            }
            adapter.notifyDataSetChanged();
            controlScanView(type);
        }
        dismissProgressDialog();
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        pullToRefreshView.onFooterRefreshComplete();
        pullToRefreshView.onHeaderRefreshComplete();
        if (E3SysManager.EXPRESS_END_STATUS.equals(sname)) {
            UtilToolkit.showToast(result);
        } else {
            showFailDialog(result);
        }
        if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_QF.equals(sname)
                || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onClick(View v) {
        pullToRefreshView.onHeaderRefreshComplete();// 切换tab时，下拉，上滑UI复原。
        pullToRefreshView.onFooterRefreshComplete();
        switch (v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.ll_lanPice:
                type = 1;
                adapter.notifyDataSetChanged(list_lanPice, "收件");
                getDataStatus(list_lanPice);
                UMShareManager.onEvent(context, "E3_sweep_record_lanPice", "E3", "E3：收件扫描记录");
                tv_lanPice.setTextColor(color_b);
                line_lanPice.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                line_daoPice.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                tv_daoPice.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录

                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描收件：0");
                    if (list_lanPice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描收件：" + todayCount_lan);// list不为空，不必请求网络数据
                    controlScanView(type);
                }

                break;
            case R.id.ll_faPice:
                type = 6;
                adapter.notifyDataSetChanged(list_faPice, "发件");
                getDataStatus(list_faPice);
                UMShareManager.onEvent(context, "E3_sweep_record_faPice", "E3", "E3：发件扫描记录");
                tv_faPice.setTextColor(color_b);
                line_faPice.setBackgroundColor(color_b);

                line_daoPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                if (line_third_brunch != null)
                    line_third_brunch.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_daoPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                if (tv_third_brunch != null)
                    tv_third_brunch.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录

                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描发件：0");
                    if (list_faPice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描发件：" + todayCount_fa);// list不为空，不必请求网络数据
                    controlScanView(type);
                }
                break;
            case R.id.ll_piePice:
                type = 2;
                adapter.notifyDataSetChanged(list_piePice, "派件");
                getDataStatus(list_piePice);
                UMShareManager.onEvent(context, "E3_sweep_record_piePice", "E3", "E3：派件扫描记录");
                tv_piePice.setTextColor(color_b);
                line_piePice.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                line_daoPice.setBackgroundColor(color_d);
                if (line_third_brunch != null)
                    line_third_brunch.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                tv_daoPice.setTextColor(color_c);
                if (tv_third_brunch != null)
                    tv_third_brunch.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录

                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描派件：0");
                    if (list_piePice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描派件：" + todayCount_pie);// list不为空，不必请求网络数据
                    controlScanView(type);
                }

                break;
            case R.id.ll_signedPice:
                type = 3;
                adapter.notifyDataSetChanged(list_signedPice, "签收件");
                getDataStatus(list_signedPice);
                UMShareManager.onEvent(context, "E3_sweep_record_signedPice", "E3", "E3：签收件扫描记录");
                tv_signedPice.setTextColor(color_b);
                line_signedPice.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                line_daoPice.setBackgroundColor(color_d);
                if (line_third_brunch != null)
                    line_third_brunch.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                tv_daoPice.setTextColor(color_c);
                if (tv_third_brunch != null)
                    tv_third_brunch.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录
                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描签收件：0");
                    if (list_signedPice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描签收件：" + todayCount_signed);// list不为空，不必请求网络数据
                    controlScanView(type);
                }
                break;
            case R.id.ll_badPice:
                type = 4;
                adapter.notifyDataSetChanged(list_badPice, "问题件");
                getDataStatus(list_badPice);
                UMShareManager.onEvent(context, "E3_sweep_record_badPice", "E3", "E3：问题件扫描记录");
                tv_badPice.setTextColor(color_b);
                line_badPice.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                line_daoPice.setBackgroundColor(color_d);
                if (line_third_brunch != null)
                    line_third_brunch.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_daoPice.setTextColor(color_c);
                if (tv_third_brunch != null)
                    tv_third_brunch.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录

                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描问题件：0");
                    if (list_badPice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描问题件：" + todayCount_bad);// list不为空，不必请求网络数据
                    controlScanView(type);
                }
                break;
            case R.id.ll_daoPice:
                type = 5;
                adapter.notifyDataSetChanged(list_daoPice, "到件");
                getDataStatus(list_daoPice);
                UMShareManager.onEvent(context, "E3_sweep_record_daoPice", "E3", "E3：到件扫描记录");
                tv_daoPice.setTextColor(color_b);
                line_daoPice.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                if (line_third_brunch != null)
                    line_third_brunch.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                if (tv_third_brunch != null)
                    tv_third_brunch.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录

                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描到件：0");
                    if (list_daoPice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描到件：" + todayCount_dao);// list不为空，不必请求网络数据
                    controlScanView(type);
                }
                break;
            case R.id.ll_rejectPice:
                type = 7;
                adapter.notifyDataSetChanged(list_rejectPice, "退件");
                getDataStatus(list_rejectPice);
                UMShareManager.onEvent(context, "E3_sweep_record_rejectPice", "E3", "E3：退件记录");
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_b);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                line_daoPice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                if (line_third_brunch != null)
                    line_third_brunch.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_daoPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                if (tv_third_brunch != null)
                    tv_third_brunch.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录

                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描退件：0");
                    if (list_rejectPice.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描退件：" + todayCount_reject);// list不为空，不必请求网络数据
                    controlScanView(type);
                }
                break;
            case R.id.ll_third_brunch:
                type = 8;
                adapter.notifyDataSetChanged(list_thirdBrunch, "第三方签收");
                getDataStatus(list_thirdBrunch);
                UMShareManager.onEvent(context, "E3_sweep_record_thirdBrunch", "E3", "E3：第三方签收扫描记录");
                tv_third_brunch.setTextColor(color_b);
                line_third_brunch.setBackgroundColor(color_b);

                line_faPice.setBackgroundColor(color_d);
                line_lanPice.setBackgroundColor(color_d);
                line_piePice.setBackgroundColor(color_d);
                line_badPice.setBackgroundColor(color_d);
                line_daoPice.setBackgroundColor(color_d);
                line_signedPice.setBackgroundColor(color_d);
                if (line_rejectPice != null)
                    line_rejectPice.setBackgroundColor(color_d);

                tv_faPice.setTextColor(color_c);
                tv_piePice.setTextColor(color_c);
                tv_lanPice.setTextColor(color_c);
                tv_badPice.setTextColor(color_c);
                tv_daoPice.setTextColor(color_c);
                tv_signedPice.setTextColor(color_c);
                if (tv_rejectPice != null)
                    tv_rejectPice.setTextColor(color_c);
                if ("sweepRecord".equals(dataTypeflag)) {// 扫描记录
                    page_num = 1;
                    tv_sweepRecordContent.setText("今日共扫描第三方签收件：0");
                    if (list_thirdBrunch.size() == 0)
                        getDatas(type, page_num, page_size);
                    else
                        tv_sweepRecordContent.setText("今日共扫描第三方签收件：" + todayCount_third);// list不为空，不必请求网络数据
                    controlScanView(type);
                }
                break;
            case R.id.customer_manager_more:

                String url = "http://m.kuaidihelp.com/tongji/list";
                String title = "扫描统计";
                loadWeb(url, title);
                break;

            default:
                break;
        }

    }

    /**
     * 上传按钮监听
     *
     * @param view 。。
     */
    public void upload(View view) {

//        if (adapter != null && adapter.hasRepetition) {
//            showAlertDialog(view);
//            return;
//        } else {
//            ignore = false;// 没有重复单号，不存在忽略重复问题
//        }
        if (!Utility.isNetworkConnected()) {// 无网络
            UtilToolkit.showToast("请检查网络设置！");
        } else {
            if (adapter != null)
                uploadDatas(adapter.getList(), null);
        }
    }

    /**
     * 上传数据
     */
    private void uploadDatas(List<E3_order> orders, String tempType) {

        if (orders.size() == 0) {
            UtilToolkit.showToast("请扫描后再来提交");
            dismissProgressDialog();
            return;
        }
        usualInfos.clear();
        if (ignore) {
            for (int i = 0; i < orders.size(); i++) {
                if (adapter.getRepeatList() != null && !adapter.getRepeatList().contains(orders.get(i))
                        && !usualInfos.contains(orders.get(i))) {
                    usualInfos.add(orders.get(i));
                }
            }
        } else {
            usualInfos.addAll(orders);
        }
        if (usualInfos.size() == 0) {
            dismissProgressDialog();
            UtilToolkit.showToast("没有可上传的数据了");
            return;
        }

            showProgressDialog( "正在上传请稍后...");

        JSONObject datas = new JSONObject();

        if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(dataType)) {
            picPathList.clear();
            for (E3_order order : orders) {
                picPathList.add(order.getPicPath());
            }
        }

        try {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            String only_code = Utility.getOnlyCode();
            String imei = tm.getDeviceId();
            if ("qf".equals(brand)) {
                datas.put("sname", E3SysManager.SCAN_TO_QF);
            } else if ("sto".equals(brand)) {
                datas.put("sname", E3SysManager.SCAN_TO_E3_V2);
            } else if ("zt".equals(brand)) {
                datas.put("sname", E3SysManager.SCAN_TO_ZT_V2);
            }
            if (TextUtils.isEmpty(tempType))
                datas.put("wayBillType", E3SysManager.typeMap.get(usualInfos.get(0).getType()));
            else
                datas.put("wayBillType", E3SysManager.typeToIDMap.get(tempType));
            datas.put("dev_id", only_code);
            datas.put("dev_imei", imei);
            datas.put("appVersion", Utility.getVersionCode());
            JSONArray wayBills = new JSONArray();
            JSONObject signPics = new JSONObject();// id:图片
            JSONObject picPath = new JSONObject();// id:图片路径
            String type = "";
            int count_pic = 0;
            int pic_no_diverse = 0;// 图片序号
            resetVariable();

            for (int i = 0; i < usualInfos.size(); i++) {
                dataType = usualInfos.get(i).getType();
                String site_code = E3SysManager.getSiteCode(orders.get(i).getSta_name(), dataType);
                JSONObject wayBill = new JSONObject();
                wayBill.put("waybillNo", usualInfos.get(i).getOrder_number());
                if (dataType.equals("到件")) {
                    wayBill.put("forward_station", site_code);
                } else if (dataType.equals("发件")) {
                    wayBill.put("next_station", site_code);
                }
                type = usualInfos.get(i).getType();

                if (E3SysManager.BRAND_QF.equals(brand)) {// 全峰
                    if ("签收件".equals(type)) {
                        if (!TextUtils.isEmpty(usualInfos.get(i).getPicPath())) {
                            wayBill.put("signPic",
                                    Utility.bitMapToString(Utility.getImage(usualInfos.get(i).getPicPath())));
                        } else {
                            wayBill.put("signType", usualInfos.get(i).getWayBillType_E3());
                        }
                    } else if ("问题件".equals(type)) {
                        CourierReviewInfo reviewInfo = E3SysManager.getReviewInfo();
                        String[] badDesc = usualInfos.get(i).getWayBillType_E3().split("\n");
                        String badSubject = badDesc[0];
                        String badType = badDesc[1];
                        wayBill.put("type", badSubject);// 未明确参数，先默认传"0"
                        wayBill.put("register_site", reviewInfo.getCourierLatticePoint());// 录入网点
                        wayBill.put("send_site", "unknown");// 寄件网点
                        wayBill.put("register_man", reviewInfo.getCourierName());// 录入人
                        wayBill.put("problem_cause", badType);// 问题件内容
                        wayBill.put("register_man_department", "unknown");// 问题件内容
                        wayBill.put("mobile", usualInfos.get(i).getPhone_number());
                    } else if ("收件".equals(type) || "派件".equals(type)) {// 发件不指定发件员
                        wayBill.put("operatorCode", usualInfos.get(i).getOperatorCode());
                    }
                } else if ("sto".equals(brand)) {// 申通

                    if ("收件".equals(type) || "派件".equals(type)) {
                        wayBill.put("operatorCode", usualInfos.get(i).getOperatorCode());
                    } else if ("签收件".equals(type)) {
                        if (!TextUtils.isEmpty(usualInfos.get(i).getPicPath())) {
                            wayBill.put("signPic",
                                    Utility.bitMapToString(Utility.getImage(usualInfos.get(i).getPicPath())));

                            count_pic++;
                            signed_pic = true;

                            if (count_pic > MAX_SIGNED_PIC && usualInfos.size() > MAX_SIGNED_PIC) {
                                break;
                            }
                        } else {
                            wayBill.put("signType", usualInfos.get(i).getWayBillType_E3());
                        }
                        picSignInfos.add(usualInfos.get(i));
                    } else if ("问题件".equals(type)) {
                        wayBill.put("badWayBillCode",
                                E3SysManager.getBadWaiBillTypeId(usualInfos.get(i).getWayBillType_E3()));
                        if (E3SysManager.getBadWaiBillTypeId(usualInfos.get(i).getWayBillType_E3()) == 0) {
                            wayBill.put("badWayBillType", usualInfos.get(i).getWayBillType_E3());
                        }
                        wayBill.put("mobile", usualInfos.get(i).getPhone_number());
                        wayBill.put("badWayBillDesc", usualInfos.get(i).getProblem_desc());
                        if (!Utility.isEmpty(usualInfos.get(i).getProblem_desc())) {
                            UMShareManager.onEvent(context, "problePice_liuyan_qrcode", "problePice_liuyan", "问题件申通留言");
                        }
                    }
                } else if ("zt".equals(brand)) {// 中通

                    if ("收件".equals(type) || "派件".equals(type)) {
                        wayBill.put("operatorCode", usualInfos.get(i).getOperatorCode());
                    } else if ("签收件".equals(type)) {
                        if (TextUtils.isEmpty(usualInfos.get(i).getPicPath())) {
                            wayBill.put("signType", usualInfos.get(i).getWayBillType_E3());
                        } else {

                            if (picSignInfos.size() == 0) {
                                pic_no_diverse++;
                                wayBill.put("signPic", pic_no_diverse);
                                signPics.put("" + pic_no_diverse,
                                        Utility.bitMapToString(Utility.getImage(usualInfos.get(i).getPicPath())));
                                picPath.put("" + pic_no_diverse, usualInfos.get(i).getPicPath());
                            } else {
                                boolean samePic = false;
                                Iterator<String> iterator = picPath.keys();
                                while (iterator.hasNext()) {
                                    String id = iterator.next();
                                    if (picPath.optString(id).equals(usualInfos.get(i).getPicPath())) {
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
                                                Utility.bitMapToString(Utility.getImage(usualInfos.get(i).getPicPath())));
                                        picPath.put("" + pic_no_diverse, usualInfos.get(i).getPicPath());
                                    }
                                }

                            }
                            if (!KuaiBaoStringUtilToolkit.isEmpty(usualInfos.get(i).getPicPath())) {
                                count_pic++;
                                signed_pic = true;
                            }
                            if (pic_no_diverse > MAX_SIGNED_PIC && usualInfos.size() > MAX_SIGNED_PIC) {
                                break;
                            }
                            if (TextUtils.isEmpty(wayBill.getString("signPic"))) {
                                UtilToolkit.showToast("获取图片失败");
                                continue;

                            }
                            picSignInfos.add(usualInfos.get(i));
                        }

                    } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(type)) {
                        wayBill.put("thirdBranch", usualInfos.get(i).getThirdBranch());
                        wayBill.put("thirdBranchId", usualInfos.get(i).getThirdBranchId());
                    } else if ("问题件".equals(type)) {
                        wayBill.put("question_desc", usualInfos.get(i).getProblem_desc());// 问题件内容
                        wayBill.put("badWayBillCode",
                                E3SysManager.getZTBadWaiBillTypeId(usualInfos.get(i).getWayBillType_E3()));
                        wayBill.put("mobile", usualInfos.get(i).getPhone_number());
                    }
                }
                wayBill.put("scan_time", usualInfos.get(i).getScan_time());
                JSONObject location = new JSONObject();
                location.put("latitude", usualInfos.get(i).getLatitude());
                location.put("longitude", usualInfos.get(i).getLongitude());
                wayBill.put("location", location);
                wayBill.put("weight", usualInfos.get(i).getOrder_weight());
                wayBill.put("resType", usualInfos.get(i).getResType());
                wayBills.put(wayBill);
            }
            if (wayBills.length() != 0) {
                datas.put("wayBillDatas", wayBills);
                datas.put("signPics", signPics);
                switch (type) {
                    case "问题件":
                        datas.put("sendSms", SkuaidiSpf.getAutoProblemNotify(courierNO) ? 1 : 0);
                        break;
                    case "签收件":
                        datas.put("sendSms", SkuaidiSpf.getAutoSignNotify(courierNO) ? 1 : 0);
                        break;
                }

            } else {
                dismissProgressDialog();
                resetVariable();
                return;
            }
            if ("收件".equals(type)) {
                UMShareManager.onEvent(context, "E3_scan_lanPice_confirm", "E3", "E3：收件扫描提交");
            } else if ("派件".equals(type)) {
                UMShareManager.onEvent(context, "E3_scan_piePice_confirm", "E3", "E3：派件扫描提交");
            } else if ("签收件".equals(type)) {
                UMShareManager.onEvent(context, "E3_scan_signedPice_confirm", "E3", "E3：签收件扫描提交");
            } else if ("问题件".equals(type)) {
                UMShareManager.onEvent(context, "E3_scan_badPice_confirm", "E3", "E3：问题件扫描提交");
            } else if ("到件".equals(type)) {
                UMShareManager.onEvent(context, "E3_scan_daoPice_confirm", "E3", "E3：到件扫描提交");
            } else if ("发件".equals(type)) {
                UMShareManager.onEvent(context, "E3_scan_faPice_confirm", "E3", "E3：发件扫描提交");
            }

            requestV2(datas);
//			httpInterfaceRequest(datas, false, INTERFACE_VERSION_NEW);
        } catch (NumberFormatException e) {
            UtilToolkit.showToast("单号格式异常！");
            dismissProgressDialog();
            e.printStackTrace();
        } catch (JSONException e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    /**
     * 获取单号最新状态
     *
     * @param list 运单数据
     */
    private void getDataStatus(List<E3_order> list) {
        if (list == null || list.size() == 0 || "sweepRecord".equals(dataTypeflag))// 扫描记录，不用获取单号状态
            return;
        numberList.clear();
        statusList.clear();
        JSONObject data = new JSONObject();
        String numbers = "";
        try {
            data.put("sname", E3SysManager.EXPRESS_END_STATUS);

            for (int i = 0; i < list.size(); i++) {
                numbers += list.get(i).getOrder_number() + ",";
                numberList.add(list.get(i).getOrder_number());
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("express_no", numbers);
            if ("sto".equals(brand)) {// 申通
                data.put("company", "sto");
            } else if ("qf".equals(brand)) {// 全峰)
                data.put("company", "qf");
            } else if ("zt".equals(brand)) {// 全峰)
                data.put("company", "zt");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * listView中删除item
     *
     * @param order 运单数据
     */
    public void delete(E3_order order) {
        if (order == null)
            return;
        try {
            int count = E3OrderDAO.deleteOrder(order);
            if (count == 0)
                return;

            if ("收件".equals(order.getType())) {
                int count_lan = Integer.parseInt(tv_count_lan.getText().toString()) - 1;
                if (count_lan > 0)
                    tv_count_lan.setText(count_lan + "");
                else
                    tv_count_lan.setVisibility(TextView.GONE);
            } else if ("发件".equals(order.getType())) {
                int count_fa = Integer.parseInt(tv_count_fa.getText().toString()) - 1;
                if (count_fa > 0)
                    tv_count_fa.setText(count_fa + "");
                else
                    tv_count_fa.setVisibility(TextView.GONE);
            } else if ("到件".equals(order.getType())) {
                int count_dao = Integer.parseInt(tv_count_dao.getText().toString()) - 1;
                if (count_dao > 0)
                    tv_count_dao.setText(count_dao + "");
                else
                    tv_count_dao.setVisibility(TextView.GONE);
            } else if ("派件".equals(order.getType())) {
                int cout_pie = Integer.parseInt(tv_count_pie.getText().toString()) - 1;
                if (cout_pie > 0)
                    tv_count_pie.setText(cout_pie + "");
                else
                    tv_count_pie.setVisibility(TextView.GONE);
            } else if ("签收件".equals(order.getType())) {
                int count_signed = Integer.parseInt(tv_count_signed.getText().toString()) - 1;
                if (count_signed > 0)
                    tv_count_signed.setText(count_signed + "");
                else
                    tv_count_signed.setVisibility(TextView.GONE);
            } else if ("问题件".equals(order.getType())) {
                int count_bad = Integer.parseInt(tv_count_bad.getText().toString()) - 1;
                if (count_bad > 0)
                    tv_count_bad.setText(count_bad + "");
                else
                    tv_count_bad.setVisibility(TextView.GONE);
            } else if ("第三方签收".equals(order.getType())) {
                int count_third = Integer.parseInt(tv_count_third_brunch.getText().toString()) - 1;
                if (count_third > 0)
                    tv_count_third_brunch.setText(count_third + "");
                else
                    tv_count_third_brunch.setVisibility(TextView.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Log.d("warn", "删除单号信息失败：" + order.toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
        intent.putExtra("express_no", brand);
        if (type == 1) {
            intent.putExtra("order_number", list_lanPice.get(position).getOrder_number());
        } else if (type == 2) {
            intent.putExtra("order_number", list_piePice.get(position).getOrder_number());
        } else if (type == 3) {
            intent.putExtra("order_number", list_signedPice.get(position).getOrder_number());
        } else if (type == 4) {
            intent.putExtra("order_number", list_badPice.get(position).getOrder_number());
        } else if (type == 5) {
            intent.putExtra("order_number", list_daoPice.get(position).getOrder_number());
        } else if (type == 6) {
            intent.putExtra("order_number", list_faPice.get(position).getOrder_number());
        } else if (type == 7) {
            intent.putExtra("order_number", list_rejectPice.get(position).getOrder_number());
        } else if (type == 8) {
            intent.putExtra("order_number", list_thirdBrunch.get(position).getOrder_number());
        }
        intent.setClass(context, CopyOfFindExpressResultActivity.class);
        startActivity(intent);
    }

    /**
     * 单号重复提醒
     *
     * @param view 。。
     */
    private void showAlertDialog(final View view) {

        final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);
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
                    ignore = true;
                    UMShareManager.onEvent(context, "sysSweepRecord_ignoreRepeat", "EThreeSysSweepRecordActivity",
                            "忽略重复");
                    uploadDatas(adapter.getList(), null);
                    if (!isFinishing())
                        dialog.dismiss();

                }
            });
            dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

                @Override
                public void onClick(View v) {
                    ignore = false;
                    uploadDatas(adapter.getList(), null);

                    if (!isFinishing())
                        dialog.dismiss();

                }
            });
            if (!isFinishing())
                dialog.showDialog();
        }

    }

    private void resetVariable() {
        signed_pic = false;
        picSignInfos.clear();
    }

    public void deleteIncorrect(View view) {
        if (adapter.getList() != null && adapter.getList().size() != 0) {
            UMShareManager.onEvent(context, "sysSweepRecord_deleteIncorrect", "EThreeSysSweepRecordActivity", "删除错扫");
            Intent intent = new Intent(context, E3ScanDeleteActivity.class);
            intent.putExtra("scanType", E3SysManager.IDToTypeMap.get(type));
            intent.putExtra("from", "EThreeSysSweepRecordActivity");
            intent.putExtra("isContinuous", true);
            startActivityForResult(intent, REQUEST);
        } else {
            UtilToolkit.showToast("请扫描后再操作");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        ArrayList<String> list = data.getStringArrayListExtra("numbersToDelete");
        for (int i = 0; i < list.size(); i++) {
            E3_order order = new E3_order();
            order.setCompany(brand);
            order.setOrder_number(list.get(i));
            order.setIsUpload(0);
            order.setType(E3SysManager.scanToTypeMap.get(E3SysManager.IDToTypeMap.get(type)));
            order.setCourier_job_no(courierNO);
            delete(order);
            for (int j = adapter.getList().size() - 1; j >= 0; j--) {
                if (list.get(i).equals(adapter.getList().get(j).getOrder_number())) {
                    adapter.getList().remove(j);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }

    }


    /**
     * 扫描记录，根据扫描类型和用户扫描权限控制底部扫描操作按钮的可见性
     *
     * @param dataType 数据类型
     */
    private void controlScanView(int dataType) {
        tv_scan_one.setVisibility(View.GONE);
        tv_scan_two.setVisibility(View.GONE);
        ll_new_operate.setVisibility(View.GONE);
        if (ss == null)
            return;
        switch (dataType) {
            case 1:
                if (ss.getFj() != null && ss.getFj().getAccess() == 1 && !E3SysManager.BRAND_QF.equals(brand)) {// 控制下一步操作按钮显示
                    tv_scan_one.setVisibility(View.VISIBLE);
                    ll_new_operate.setVisibility(View.VISIBLE);
                    if (checkCount_lan == 0) {
                        tv_scan_one.setEnabled(false);
                        //tv_scan_one.setBackgroundResource(R.drawable.shape_btn_gray1);
                    } else {
                        tv_scan_one.setEnabled(true);
                        //tv_scan_one.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                    }
                } else {
                    tv_scan_one.setVisibility(View.GONE);
                }
                tv_scan_one.setText("发件扫描(" + checkCount_lan + ")");
                break;
            case 2:
                if (ss.getWtj() != null && ss.getWtj().getAccess() == 1) {// 控制下一步操作按钮显示
                    tv_scan_one.setVisibility(View.VISIBLE);
                    ll_new_operate.setVisibility(View.VISIBLE);
                } else {
                    tv_scan_one.setVisibility(View.GONE);
                }
                tv_scan_one.setText("问题件(" + checkCount_pie + ")");
                if (ss.getQsj() != null && ss.getQsj().getAccess() == 1) {// 控制下一步操作按钮显示
                    tv_scan_two.setVisibility(View.VISIBLE);
                    ll_new_operate.setVisibility(View.VISIBLE);
                    if (checkCount_pie == 0) {
                        tv_scan_one.setEnabled(false);
                        tv_scan_two.setEnabled(false);
                    } else {
                        tv_scan_one.setEnabled(true);
                        tv_scan_two.setEnabled(true);
                    }
                } else {
                    tv_scan_two.setVisibility(View.GONE);
                }
                tv_scan_two.setText("签收扫描(" + checkCount_pie + ")");

                break;
            case 3:

                break;
            case 4:
                if (ss.getQsj() != null && ss.getQsj().getAccess() == 1) {// 控制下一步操作按钮显示
                    tv_scan_one.setVisibility(View.VISIBLE);
                    ll_new_operate.setVisibility(View.VISIBLE);
                    if (checkCount_bad == 0) {
                        tv_scan_one.setEnabled(false);
                    } else {
                        tv_scan_one.setEnabled(true);
                    }
                } else {
                    tv_scan_one.setVisibility(View.GONE);
                }
                tv_scan_one.setText("签收扫描(" + checkCount_bad + ")");

                break;
            case 5:
                if (ss.getPj() != null && ss.getPj().getAccess() == 1) {// 控制下一步操作按钮显示
                    tv_scan_one.setVisibility(View.VISIBLE);
                    ll_new_operate.setVisibility(View.VISIBLE);
                    if (checkCount_dao == 0) {
                        tv_scan_one.setEnabled(false);
                    } else {
                        tv_scan_one.setEnabled(true);
                    }
                } else {
                    tv_scan_one.setVisibility(View.GONE);
                }
                tv_scan_one.setText("派件扫描(" + checkCount_dao + ")");

                break;
            case 6:
                tv_scan_one.setVisibility(View.GONE);
                tv_scan_two.setVisibility(View.GONE);
                ll_new_operate.setVisibility(View.GONE);
                break;
            case 7:
                tv_scan_one.setVisibility(View.GONE);
                tv_scan_two.setVisibility(View.GONE);
                ll_new_operate.setVisibility(View.GONE);
            case 8:
                tv_scan_one.setVisibility(View.GONE);
                tv_scan_two.setVisibility(View.GONE);
                ll_new_operate.setVisibility(View.GONE);
                break;

            default:
                break;
        }

    }

    /**
     * 扫描记录中，扫描操作按钮是否可见
     *
     * @return 是否可见
     */
    public boolean scanViewVisible() {
        return tv_scan_one != null && tv_scan_two != null && (tv_scan_one.getVisibility() == View.VISIBLE || tv_scan_two.getVisibility() == View.VISIBLE);
    }

    /**
     * 扫描记录 点击列表中某一item时，控件页面底部扫描操作按钮上的已选择数量
     */
    public void setCheckedCount(int count) {
        String text = tv_scan_one.getText().toString();
        int start = text.indexOf('(');
        int end = text.indexOf(')');

        switch (type) {
            case 1:
                checkCount_lan = count;
                break;

            case 2:
                checkCount_pie = count;
                break;
            case 3:
                break;
            case 4:
                checkCount_bad = count;
                break;
            case 5:
                checkCount_dao = count;
                break;
            case 6:
                break;
        }
        String newText = "";
        try {
            newText = text.substring(0, start + 1) + count + text.substring(end, text.length());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tv_scan_one.getVisibility() == View.VISIBLE) {
            tv_scan_one.setText(newText);
            if (count == 0) {
                tv_scan_one.setEnabled(false);
            } else {
                tv_scan_one.setEnabled(true);
            }
        }
        if (tv_scan_two.getVisibility() == View.VISIBLE) {
            text = tv_scan_two.getText().toString();
            start = text.indexOf('(');
            end = text.indexOf(')');
            newText = text.substring(0, start + 1) + count + text.substring(end, text.length());
            tv_scan_two.setText(newText);
            if (count == 0) {
                tv_scan_two.setEnabled(false);
            } else {
                tv_scan_two.setEnabled(true);
            }
        }
    }

    public void onClickOne(View view) {
        Intent intent = new Intent(context, EthreeInfoScanActivity.class);
        String scanType = null;
        switch (type) {
            case 1:
                scanType = E3SysManager.SCAN_TYPE_FAPICE;
                break;
            case 2:
                scanType = E3SysManager.SCAN_TYPE_BADPICE;
                break;
            case 4:
                scanType = E3SysManager.SCAN_TYPE_SIGNEDPICE;
                break;
            case 5:
                scanType = E3SysManager.SCAN_TYPE_PIEPICE;
                break;
        }
        List<E3_order> mList = adapter.getCheckedList();
        List<E3_order> nList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        for (int i = 0, j = mList.size(); i < j; i++) {
            try {
                E3_order order = (E3_order) mList.get(i).clone();
                order.setScan_time(E3SysManager.getTimeBrandIndentify());
                if (!numberList.contains(order.getOrder_number())) {
                    nList.add(order);
                }
                numberList.add(order.getOrder_number());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        if (nList.size() == 0) {
            return;
        }
        intent.putExtra("scanType", scanType);
        intent.putExtra("e3WayBills", (Serializable) E3SysManager.orderToInfo(nList, 0, 0, scanType, "record"));
        startActivity(intent);
    }

    public void onClickTwo(View view) {
        Intent intent = new Intent(context, EthreeInfoScanActivity.class);
        String scanType = null;

        switch (type) {
            case 1:
                scanType = E3SysManager.SCAN_TYPE_FAPICE;
                break;
            case 2:
                scanType = E3SysManager.SCAN_TYPE_SIGNEDPICE;
                break;
            case 4:
                scanType = E3SysManager.SCAN_TYPE_SIGNEDPICE;
                break;
            case 5:
                scanType = E3SysManager.SCAN_TYPE_PIEPICE;
                break;
        }
        List<E3_order> mList = adapter.getCheckedList();
        List<E3_order> nList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();

        for (int i = 0, j = mList.size(); i < j; i++) {
            try {
                E3_order order = (E3_order) mList.get(i).clone();
                order.setScan_time(E3SysManager.getTimeBrandIndentify());
                if (!numberList.contains(order.getOrder_number())) {
                    nList.add(order);
                }
                numberList.add(order.getOrder_number());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        if (nList.size() == 0) {
            return;
        }
        intent.putExtra("scanType", scanType);
        intent.putExtra("e3WayBills", (Serializable) E3SysManager.orderToInfo(nList, 0, 0, scanType, "record"));
        startActivity(intent);
    }

    private void alert(String errorMsg) {
        final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(this, SkuaidiE3SysDialog.TYPE_COMMON, new View(this));
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
}