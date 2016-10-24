package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.SignAgingAdapter;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.model.SignAging;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HH 签收时效
 */
public class SignAgingActivity extends SkuaiDiBaseActivity implements OnClickListener{

    public static final String LATEST_FIVE_DAY = "latest_five_day";
    public static final String YESTERDAY = "yesterday";
    public static final String TODAY = "today";
    private TextView tv_title_des;
    private TextView tv_count_latest_five_day;
    private TextView tv_count_today;
    private TextView tv_count_yesterday;
    private ImageView iv_sync_yesterday;
    private ImageView iv_sync_today;
    private ImageView iv_sync_latest_five_day;

    private Intent intent;
    private Context context;
    private String operator_code;
    private String mobile;
    private String getType = "";
    public static String TIME_CHANGED_ACTION = "com.kuaibao.skuaidi.activity.action.TIME_CHANGED_ACTION";
    private SkuaidiSpf spf;
    private SkuaidiNewDB newDB;
    private Map<String, String> map = new HashMap<String, String>();
    private ListView sign_list_today;
    private ListView sign_list_yesterday;
    private ListView sign_list_latest_five_day;
    private List<SignAging> datas = new ArrayList<SignAging>();
    private boolean refresh;
    private ImageView iv_hide_content;
    protected FinalDb finalDb;
    protected String company = "";
    protected CourierReviewInfo reviewInfo;

    String start_time = null;
    String end_time = null;

    private enum dataPeriod {today, yesterday, latest_five_day}

    private String period = dataPeriod.today.toString();

    private LinearLayout ll_sync_today;
    private LinearLayout ll_sync_yesterday;
    private LinearLayout ll_sync_latest_five_day;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.signaging_activity);
        context = this;
        getControl();
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        // GetServiceDatas();
        initDatas();
    }

    private void getControl() {
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("有派无签");
        sign_list_today = (ListView) findViewById(R.id.lv_sign_aging_toady);
        sign_list_yesterday = (ListView) findViewById(R.id.lv_sign_aging_yesterday);
        sign_list_latest_five_day = (ListView) findViewById(R.id.lv_sign_aging_latest_five_day);

        tv_count_latest_five_day = (TextView) findViewById(R.id.tv_count_latest_five_day);
        tv_count_today = (TextView) findViewById(R.id.tv_count_today);
        tv_count_yesterday = (TextView) findViewById(R.id.tv_count_yesterday);
        iv_sync_yesterday = (ImageView) findViewById(R.id.iv_sync_yesterday);
        iv_sync_today = (ImageView) findViewById(R.id.iv_sync_today);
        iv_sync_latest_five_day = (ImageView) findViewById(R.id.iv_sync_latest_five_day);
        ll_sync_today = (LinearLayout) findViewById(R.id.ll_sync_today);
        ll_sync_yesterday = (LinearLayout) findViewById(R.id.ll_sync_yesterday);
        ll_sync_latest_five_day = (LinearLayout) findViewById(R.id.ll_sync_latest_five_day);

        ll_sync_today.setOnClickListener(this);
        ll_sync_yesterday.setOnClickListener(this);
        ll_sync_latest_five_day.setOnClickListener(this);
    }

    // 调接口 获取数据
//    private void GetServiceDatas() {
//        showProgressDialog("");//SignAgingActivity.this,"查询中...");
//        List<CourierReviewInfo> list = new ArrayList<>();
//        // 获取当前快递员的工号
//        finalDb = BackUpService.getfinalDb();
//        list = finalDb.findAllByWhere(CourierReviewInfo.class, "courierPhone = '"
//                + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
//        if (list != null && list.size() != 0) {
//            reviewInfo = list.get(0);
//            operator_code = reviewInfo.getCourierJobNo();
//            mobile = reviewInfo.getCourierPhone();
//        }
//        JSONObject object = new JSONObject();
//        try {
//
//            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
//                object.put("brand", "sto");
//            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
//                object.put("brand", "zt");
//            }
//            object.put("sname", E3SysManager.SCAN_E3_UNSIGNED);
//            object.put("getType", "");
//            object.put("mobile", mobile);
//            object.put("operator_code", operator_code);
//
//            httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

    public void getNosign(String dateType) {
        showProgressDialog("");//SignAgingActivity.this,"查询中...");
        JSONObject object = new JSONObject();
        getType = "unsigned";
        try {
            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                object.put("brand", "sto");
            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                object.put("brand", "zt");
            }
            setDateRange(dateType);
            object.put("sname", E3SysManager.SCAN_E3_UNSIGNED);
            object.put("getType", getType);
            object.put("mobile", mobile);
            object.put("operator_code", operator_code);
            object.put("start_time",start_time);
            object.put("end_time",end_time);

            httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void setListener() {
////		iv_hide_content.setOnClickListener(new MyOnclickLisner());
//        pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
//
//            @Override
//            public void onHeaderRefresh(PullToRefreshView view) {
//                boolean tag = false;
//                pull.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (Utility.isNetworkConnected() == true) {
//                            GetServiceDatas();
//                        }
//
//                    }
//                }, 1000);
//
//            }
//        });
//    }

    private void initDatas() {
        map = SkuaidiSpf.getSingAging(context);
        String deliver = map.get("deliver");
        String unsigned = map.get("unsigned");
        String problem = map.get("problem");
        if (!TextUtils.isEmpty(deliver)||!TextUtils.isEmpty(unsigned)||!TextUtils.isEmpty(problem)) {
            SignAging sign = new SignAging();
            sign.setNo_sign(unsigned);
            sign.setQuestion_sign(problem);
            sign.setTotal_sign(deliver);
            datas.add(sign);
            SignAgingAdapter adapter = new SignAgingAdapter(context, datas);
            adapter.setDate(TODAY);
            sign_list_today.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            sign_list_today.setVisibility(View.GONE);
            // GetServiceDatas();
        }
        //List<CourierReviewInfo> list = new ArrayList<>();
        // 获取当前快递员的工号
        finalDb = SKuaidiApplication.getInstance().getFinalDbCache();
        CourierReviewInfo  courierReviewInfo=E3SysManager.getReviewInfo();
        //list = finalDb.findAllByWhere(CourierReviewInfo.class, "courierPhone = '"
//                + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
        if (courierReviewInfo != null ) {
            reviewInfo = courierReviewInfo;
            operator_code = reviewInfo.getCourierJobNo();
            mobile = reviewInfo.getCourierPhone();
        }
    }

    public void getQuestion(String dateType) {
        showProgressDialog("");//SignAgingActivity.this,"查询中...");
        JSONObject object = new JSONObject();
        getType = "problem";
        try {
            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                object.put("brand", "sto");
            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                object.put("brand", "zt");
            }
            setDateRange(dateType);

            object.put("sname", E3SysManager.SCAN_E3_UNSIGNED);
            object.put("getType", getType);
            object.put("mobile", mobile);
            object.put("operator_code", operator_code);
            object.put("start_time",start_time);
            object.put("end_time",end_time);
            httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     *  根据日期类型设置查询起止时间
     * @param dateType 日期类型： 今天，昨天，前五天
     */
    private void setDateRange(String dateType) {
        if(TODAY.equals(dateType)){
            start_time = "";
            end_time = "";
        }else if(YESTERDAY.equals(dateType)){
            start_time = DateHelper.getAppointDate(-1, "yyyy-MM-dd");
            end_time = "";
        }else if(LATEST_FIVE_DAY.equals(dateType)){
            start_time = DateHelper.getAppointDate(-6, "yyyy-MM-dd");
            end_time = DateHelper.getAppointDate(-1, "yyyy-MM-dd");
        }
    }

    public void getDatas(String dataType) {
        showProgressDialog("");//SignAgingActivity.this,"查询中...");
        JSONObject object = new JSONObject();
        try {
            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
                object.put("brand", "sto");
            } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                object.put("brand", "zt");
            }
            setDateRange(dataType);
            object.put("sname", E3SysManager.SCAN_E3_UNSIGNED);
            object.put("getType", getType);
            object.put("mobile", mobile);
            object.put("operator_code", operator_code);
            object.put("start_time", start_time);
            object.put("end_time", end_time);
            httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 返回图标
    public void back(View v) {
        finish();
    }

    @SuppressWarnings("static-access")
    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        dismissProgressDialog();//SignAgingActivity.this);
        animation.cancel();
        animation.reset();
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == null) {
            return;
        }
        if (sname.equals(E3SysManager.SCAN_E3_UNSIGNED) && getType.equals("unsigned")) {
            getType = "";
            List<NotifyInfo> infos = new ArrayList<NotifyInfo>();
            try {
                JSONArray datas = result.getJSONArray("retArr");
                for (int i = 0; i < datas.length(); i++) {
                    NotifyInfo noti = new NotifyInfo();
                    JSONObject object = datas.getJSONObject(i);
                    noti.setExpress_number(object.optString("waybill_no"));
                    noti.setWayBillTypeForE3(object.optString("bad_waybill_type"));
                    noti.setStatus(object.optString("type"));
                    noti.setScanTime(E3SysManager.getTimeBrandIndentify());
                    infos.add(noti);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (infos.size() != 0) {
                SKuaidiApplication.getInstance().postMsg("NoSingActivity", "NoSing", infos);
                intent = new Intent(context, NoSignActivity.class);
                intent.putExtra("flag", "unsign");
                startActivity(intent);
            } else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
            }
        } else if (sname.equals(E3SysManager.SCAN_E3_UNSIGNED) && getType.equals("problem")) {
            getType = "";
            List<NotifyInfo> infos = new ArrayList<>();
            try {
                JSONArray datas = result.getJSONArray("retArr");
                for (int i = 0; i < datas.length(); i++) {
                    NotifyInfo noti = new NotifyInfo();
                    JSONObject object = datas.getJSONObject(i);
                    noti.setExpress_number(object.optString("waybill_no"));
                    noti.setQuestion_detail(object.optString("bad_waybill_type"));
                    noti.setStatus(object.optString("type"));
                    noti.setScanTime(E3SysManager.getTimeBrandIndentify());
                    infos.add(noti);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (infos.size() != 0) {

                SKuaidiApplication.getInstance().postMsg("NoSingActivity", "NoSing", infos);

                intent = new Intent(context, NoSignActivity.class);
                intent.putExtra("flag", "question");
                startActivity(intent);
            } else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
            }
        } else if (sname.equals(E3SysManager.SCAN_E3_UNSIGNED)) {
            try {
                datas.clear();
                JSONObject object = result.getJSONObject("retArr");
                int unsigned_num = object.optInt("unsigned");
                int problem_num = object.optInt("problem");
                int deliver_num = object.optInt("deliver");
                if (unsigned_num > 0||problem_num>0||deliver_num>0) {
                    SignAging sign = new SignAging();
                    sign.setNo_sign(String.valueOf(unsigned_num));
                    sign.setQuestion_sign(String.valueOf(problem_num));
                    sign.setTotal_sign(String.valueOf(deliver_num));
                    datas.add(sign);
                }

                SignAgingAdapter adapter = new SignAgingAdapter(context, datas);
                switch (dataPeriod.valueOf(period)) {
                    case today:
                        if (datas.size() > 0) {
                            adapter.setDate(TODAY);
                            sign_list_today.setAdapter(adapter);
                            sign_list_today.setVisibility(View.VISIBLE);
                            String text_count = String.format(getResources().getString(R.string.today_count), deliver_num+"");
                            tv_count_today.setText(text_count);
                        } else {
                            sign_list_today.setVisibility(View.GONE);
                        }
                        break;
                    case yesterday:
                        if (datas.size() > 0) {
                            adapter.setDate(YESTERDAY);
                            sign_list_yesterday.setAdapter(adapter);
                            sign_list_yesterday.setVisibility(View.VISIBLE);
                            String text_count = String.format(getResources().getString(R.string.yesterday_count), deliver_num+"");
                            tv_count_yesterday.setText(text_count);
                        } else {
                            sign_list_yesterday.setVisibility(View.GONE);
                        }
                        break;
                    case latest_five_day:
                        if (datas.size() > 0) {
                            adapter.setDate(LATEST_FIVE_DAY);
                            sign_list_latest_five_day.setAdapter(adapter);
                            sign_list_latest_five_day.setVisibility(View.VISIBLE);
                            String text_count = String.format(getResources().getString(R.string.latest_five_day_count), deliver_num+"");
                            tv_count_latest_five_day.setText(text_count);
                        } else {
                            sign_list_latest_five_day.setVisibility(View.GONE);
                        }
                        break;
                }
                adapter.notifyDataSetChanged();
                // 把数据保存在本地
                spf.saveSingAging(context, String.valueOf(deliver_num), String.valueOf(unsigned_num), String.valueOf(problem_num));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();//SignAgingActivity.this);
        animation.cancel();
        animation.reset();
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        dismissProgressDialog();//SignAgingActivity.this);
        if (Utility.isNetworkConnected() == true) {
            if (code.equals("7") && null != result) {
                try {
                    String desc = result.optString("desc");
                    UtilToolkit.showToast(desc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        String dateType = null;

        switch (v.getId()) {

            case R.id.ll_sync_today:
                iv_sync_today.startAnimation(animation);

                iv_sync_yesterday.clearAnimation();
                iv_sync_latest_five_day.clearAnimation();
                period = dataPeriod.today.toString();
                dateType=TODAY;
                break;
            case R.id.ll_sync_yesterday:
                iv_sync_yesterday.startAnimation(animation);

                iv_sync_today.clearAnimation();
                iv_sync_latest_five_day.clearAnimation();
                UMShareManager.onEvent(context, "singnAging_yesterday_qrcode", "singnAging_yesterday", "有派无签:查询昨日");
                period = dataPeriod.yesterday.toString();
                dateType=YESTERDAY;
                break;
            case R.id.ll_sync_latest_five_day:
                iv_sync_latest_five_day.startAnimation(animation);

                iv_sync_today.clearAnimation();
                iv_sync_yesterday.clearAnimation();
                UMShareManager.onEvent(context, "singnAging_fivedays_select", "singnAging_fivedays", "有派无签:查询前五日");
                period = dataPeriod.latest_five_day.toString();
                dateType=LATEST_FIVE_DAY;
                break;
            default:
                break;
        }
        getDatas(dateType);
    }
}
