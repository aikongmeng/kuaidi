package com.kuaibao.skuaidi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.make.realname.CollectionDetailOfflineActivity;
import com.kuaibao.skuaidi.activity.make.realname.CollectionRecordActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.zxing.ui.PayScanQrcodeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * @author 顾冬冬
 * 收款
 */
public class MakeCollectionsActivity extends SkuaiDiBaseActivity {

    @BindView(R.id.jz) RelativeLayout jz;// 记账
    @BindView(R.id.icon_jz) ImageView iconJz;
    @BindView(R.id.title_name) TextView titleName;
    @BindView(R.id.duijieCountry) TextView duijieCountry;// 对接国家邮政总局
    @BindView(R.id.title_msg) TextView titleMsg;
    @BindView(R.id.tv_title_des) TextView tv_title_des;
    @BindView(R.id.tv_more) TextView tvMore;// 寄递记录
    @BindView(R.id.tv_notify) TextView tv_notify;// 收款说明
    @BindView(R.id.et_scan_money) EditText et_scan_money;// 金额输入框
    @BindView(R.id.title_notify) LinearLayout title_notify;

    /**
     * 收款说明
     */
    private static final String PAYMENT_EXPLAIN_DISPLAY = "payment_explain.display";

    private Context mContext = null;

    protected List<NotifyInfo> mList;

    public static Stack<Activity> activitys;
    private String url;
    private String orderID;
    private boolean validAmount = false;// 有效的收款金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.make_collections_activity);
        ButterKnife.bind(this);
        mContext = this;
        activitys = new Stack<>();
        if ("order".equals(getIntent().getStringExtra("tag"))) {// 订单收款
            activitys.add(this);
            orderID = getIntent().getStringExtra("orderID");
        }
        initView();

        initListener();
        httpMakeCollectionsDesc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        activitys.clear();
        super.onDestroy();
    }

    /**初始化控件
     */
    private void initView() {
        if (!Utility.isEmpty(SkuaidiSpf.getLoginUser().getArea()) && SkuaidiSpf.getLoginUser().getArea().contains("浙江")){// 浙江地区
            if (!Utility.isEmpty(SkuaidiSpf.getLoginUser().getExpressNo()) && ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo()) || "zt".equals(SkuaidiSpf.getLoginUser().getExpressNo()))){// 中通|申通【使用浙江地区H5网页】
                iconJz.setBackgroundResource(R.drawable.btn_zj);
                titleName.setText("浙江地区实名登记");
                titleMsg.setText("记账并登记实名寄递信息");
            }else{// 非中通|申通【使用原生实名登记】
                iconJz.setBackgroundResource(R.drawable.btn_jz);
                titleName.setText("实名登记");
                titleMsg.setText("记账并登记实名寄递信息");
            }

        }else{// 非浙江地区
            iconJz.setBackgroundResource(R.drawable.btn_jz);
            titleName.setText("实名登记");
            titleMsg.setText("记账并登记实名寄递信息");

            if (!Utility.isEmpty(SkuaidiSpf.getLoginUser().getExpressNo()) &&"sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())){// 申通【使用全国H5网页】
                duijieCountry.setVisibility(View.VISIBLE);
            }else{// 非申通【使用原生实名登记】
                duijieCountry.setVisibility(View.GONE);
            }
        }
        tv_title_des.setText("收款");
        tvMore.setText("收款记录");
        tvMore.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        // *************控件edittext只能输入到小数点后两位**************
        et_scan_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        et_scan_money.setText(s);
                        et_scan_money.setSelection(s.length());
                    }
                }
                if (s.toString().trim().equals(".")) {
                    s = "0" + s;
                    et_scan_money.setText(s);
                    et_scan_money.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_scan_money.setText(s.subSequence(0, 1));
                        et_scan_money.setSelection(1);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String pay_money = et_scan_money.getText().toString();
                if (TextUtils.isEmpty(pay_money)) {
                    validAmount = false;
                } else {
                    if (pay_money.substring(pay_money.length() - 1, pay_money.length()).equals(".")) {
                        pay_money = pay_money.substring(0, pay_money.length() - 1);
                    }
                    validAmount = !"".equals(pay_money) && !"0".equals(pay_money) && !"0.0".equals(pay_money)
                            && !"0.00".equals(pay_money);
                }
            }
        });
    }

    private boolean isMoreThan10000(String money) {
        if (Utility.isEmpty(money)) {
            UtilToolkit.showToast("请输入收款金额");
            return true;
        }
        if (Double.parseDouble(money) > 10000) {
            UtilToolkit.showToast("您输入的金额大于10000元");
            return true;
        }
        return false;
    }

    /**
     * 收款说明
     */
    private void httpMakeCollectionsDesc() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", PAYMENT_EXPLAIN_DISPLAY);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (result != null) {
            if (PAYMENT_EXPLAIN_DISPLAY.equals(sname)) {
                String status = result.optString("status");
                if ("show".equals(status)) {
                    String retStr = result.optString("retStr");
                    if (!TextUtils.isEmpty(retStr)) {
                        title_notify.setVisibility(View.VISIBLE);
                        ImageView icon = (ImageView) findViewById(R.id.iv_icon);
                        if (!TextUtils.isEmpty(result.optString("image"))) {
                            GlideUtil.GlideUrlToImg(MakeCollectionsActivity.this, result.optString("image"), icon);
                            icon.setVisibility(View.GONE);
                        } else {
                            icon.setVisibility(View.GONE);
                        }
                        tv_notify.setText(Html.fromHtml(retStr));
                        url = result.optString("link");
                    }
                }
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (PAYMENT_EXPLAIN_DISPLAY.equals(sname)) {
            tv_notify.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    public void collHelp(View view) {
        loadWeb("http://m.kuaidihelp.com/shoukuan/pack", "");
    }

    /**
     * 线下现金收款**/
    private void addOffline(final String money){
        Map<String,String> params = new HashMap<>();
        params.put("courier_id",SkuaidiSpf.getLoginUser().getUserId());
        params.put("money",money);
        if ("order".equals(getIntent().getStringExtra("tag"))) {// 订单收款
            params.put("order_id", orderID);
        }
        showProgressDialog("");
        ApiWrapper apiWrapper = new ApiWrapper();
        Subscription subscription =apiWrapper.addOffline(params).subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
            @Override
            public void call(com.alibaba.fastjson.JSONObject jsonObject) {
                Intent i = new Intent(mContext, CollectionDetailOfflineActivity.class);
                i.putExtra("id",jsonObject.getString("id"));
                i.putExtra("time",jsonObject.getString("time"));
                i.putExtra("money",money);
                startActivity(i);
                et_scan_money.setText("");
            }
        }));
        mCompositeSubscription.add(subscription);

    }

    @OnClick({R.id.ali_sao_pay, R.id.wx_pay, R.id.jz, R.id.iv_title_back,R.id.tv_more,R.id.title_notify,R.id.offline_collect_money})
    public void onClick(View view) {
        Intent mIntent;
        String money = et_scan_money.getText().toString();
        switch (view.getId()) {
            case R.id.ali_sao_pay:// 二维码收款
                if (isMoreThan10000(money)) {
                    return;
                }
                if (!TextUtils.isEmpty(money)) {// 二维码收款
                    if (validAmount) {
                        UMShareManager.onEvent(mContext, "MakeCollectionsScanQrcode", "CashActivity", "收款界面：二维码收款");
                        mIntent = new Intent(this, CollectionBy2DCodeActivity.class);
                        mIntent.putExtra("money", money);
                        if ("order".equals(getIntent().getStringExtra("tag"))) {// 订单收款
                            mIntent.putExtra("orderID", orderID);
                        }
                        startActivity(mIntent);
                    } else {
                        UtilToolkit.showToast("无效的金额");
                    }

                } else {
                    UtilToolkit.showToast("请输入收款金额");
                }
                break;
            case R.id.wx_pay:// 扫一扫收款
                if (isMoreThan10000(money)) {
                    return;
                }
                if (!TextUtils.isEmpty(money)) {// 扫一扫收款
                    if (validAmount) {
                        UMShareManager.onEvent(mContext, "MakeCollectionsPayByCard", "CashActivity", "收款界面：扫一扫收款");
                        mIntent = new Intent(mContext, PayScanQrcodeActivity.class);
                        mIntent.putExtra("money", money);
                        if ("order".equals(getIntent().getStringExtra("tag"))) {// 订单收款
                            mIntent.putExtra("orderID", orderID);
                        }
                        startActivity(mIntent);
                    } else {
                        UtilToolkit.showToast("无效的金额");
                    }
                } else {
                    UtilToolkit.showToast("请输入收款金额");
                }
                break;
            case R.id.offline_collect_money:// 线下现多收款
                if (isMoreThan10000(money)) {
                    return;
                }
                if (!TextUtils.isEmpty(money)) {// 扫一扫收款
                    if (validAmount) {
                        UMShareManager.onEvent(mContext, "MakeCollectionsPayByOffline", "CashActivity", "收款界面：线下现金收款");
                        addOffline(money);
                    } else {
                        UtilToolkit.showToast("无效的金额");
                    }
                } else {
                    UtilToolkit.showToast("请输入收款金额");
                }
                break;
            case R.id.iv_title_back:// 返回按钮
                finish();
                break;
            case R.id.tv_more://
                Intent intent = new Intent(MakeCollectionsActivity.this, CollectionRecordActivity.class);
                startActivity(intent);

                break;
            case R.id.title_notify:
                loadWeb(url, "收款通知");
                break;
        }
    }
}
