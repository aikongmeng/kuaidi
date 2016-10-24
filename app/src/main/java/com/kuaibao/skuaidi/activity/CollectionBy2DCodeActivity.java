package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 二维码收款页面
 *
 * @author wangqiang
 */
public class CollectionBy2DCodeActivity extends SkuaiDiBaseActivity {
    // 生成二维码
    private static final String SCAN_CODE = "ScanCode";
    // 获取收款结果，总次数
    private static final int REQUEST_TIEMS = 200;
    // 帮助
    private static final String HTTP_M_KUAIDIHELP_COM_HELP_EWM_SK = "http://m.kuaidihelp.com/help/ewm_sk";
    /**
     * 获取收款结果接口
     */
    private static final String PAYMENT_GET_ORDER_STATUS = "payment.getOrderStatus";
    /**
     * 获取二维码接口
     */
    private static final String PAYMENT_ORDER = "payment.order";
    /**
     * 获取二维码接口版本号
     **/
    private static final String PAYMENT_ORDER_VERSION = "v1";
    private ImageView iv_alipay;
    private ImageView iv_WeChat;
    private TextView tv_alipay;
    private TextView tv_WeChat;
    private TextView tv_title;
    private SkuaidiTextView tv_more;
    private Context context;
    private String money;
    private int getResultTimes = 0;
    private String create_time;
    private String order_number;
    private MyTimerTask mTask;
    private TextView tv_hint_WeChat;
    private TextView tv_hint_alipay;
    private TextView tv_desc;// 微信支付说明

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_collection_by_2dcode);
        context = this;
        if (MakeCollectionsActivity.activitys != null) {// 订单收款
            MakeCollectionsActivity.activitys.add(this);
            order_number = getIntent().getStringExtra("orderID");
        }
        initView();
        ScanCodePay();
    }

    private void initView() {
        money = getIntent().getStringExtra("money");
        iv_alipay = (ImageView) findViewById(R.id.iv_alipay);
        iv_WeChat = (ImageView) findViewById(R.id.iv_WeChat);
        tv_alipay = (TextView) findViewById(R.id.tv_alipay);
        tv_WeChat = (TextView) findViewById(R.id.tv_WeChat);
        tv_title = (TextView) findViewById(R.id.tv_title_des);
        tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
        tv_hint_WeChat = (TextView) findViewById(R.id.tv_hint_WeChat);
        tv_hint_alipay = (TextView) findViewById(R.id.tv_hint_alipay);
        tv_desc = (TextView) findViewById(R.id.desc);


        SpannableStringBuilder builder = new SpannableStringBuilder("使用 支付宝钱包 扫二维码向我付款");
        builder.setSpan(new ForegroundColorSpan(Color.rgb(29, 126, 221)), 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_hint_alipay.setText(builder);

        SpannableStringBuilder builder2 = new SpannableStringBuilder("使用 微信 扫二维码向我付款");
        builder2.setSpan(new ForegroundColorSpan(Color.rgb(2, 179, 0)), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_hint_WeChat.setText(builder2);

        tv_title.setText("二维码收款");
        tv_more.setText("帮助");
        tv_more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadWeb(HTTP_M_KUAIDIHELP_COM_HELP_EWM_SK, "");

            }
        });
        tv_alipay.setText(money);
        tv_WeChat.setText(money);
    }

    /**
     * 获取收款二维码
     */
    private void ScanCodePay() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", PAYMENT_ORDER);
            data.put("pay_method", SCAN_CODE);
            data.put("money", money);
            if (TextUtils.isEmpty(order_number)) {
                data.put("pay_type", "d");
            } else {
                data.put("order_number", order_number);
            }
            data.put("version", PAYMENT_ORDER_VERSION);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取收款结果
     */
    private void getPaymentStatus() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", PAYMENT_GET_ORDER_STATUS);
            data.put("order_number", order_number);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        back(null);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (result == null)
            return;
        if (PAYMENT_ORDER.equals(sname)) {
            String alipay = result.optString("alipay");
            String wxpay = result.optString("wxpay");
            create_time = result.optString("create_time");
            if (TextUtils.isEmpty(order_number)) {
                order_number = result.optString("order_number");
            }
            String money = result.optString("money");// 收款金额
            String realMoney = result.optString("real_money");// 实际到账金额
            String desc = result.optString("desc");// 收款金额方案
            String formatStr = money + "(实际到账：" + Utility.hexToString("C2A5") + realMoney + ")";
            int startIndex = money.length();
            int endIndex = formatStr.length();
            Spannable span = new SpannableString(formatStr);
            span.setSpan(new AbsoluteSizeSpan(40), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_WeChat.setText(span);
            tv_desc.setText(desc);
            tv_desc.setVisibility(View.VISIBLE);
//			DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
//					.bitmapConfig(Config.RGB_565).build();
//			ImageLoader.getInstance().displayImage(alipay, iv_alipay, imageOptions);
//			ImageLoader.getInstance().displayImage(wxpay, iv_WeChat, imageOptions);
            GlideUtil.GlideUrlToImg(CollectionBy2DCodeActivity.this, alipay, iv_alipay);
            GlideUtil.GlideUrlToImg(CollectionBy2DCodeActivity.this, wxpay, iv_WeChat);
            Timer timer = new Timer(true);
            mTask = new MyTimerTask();
            timer.schedule(mTask, 0, 3 * 1000);
        } else if (PAYMENT_GET_ORDER_STATUS.equals(sname) && getResultTimes != REQUEST_TIEMS) {
            if ("SUCCESS".equals(result.optString("status"))) {
                if (mTask != null)
                    mTask.cancel();
                Intent intent = new Intent(this, CollectionAddExpressNoActivity.class);
                intent.putExtra("money", money);
                intent.putExtra("create_time", create_time);
                intent.putExtra("order_number", order_number);
                intent.putExtra("desc", result.optString("desc"));
                intent.putExtra("isContinuous", false);
                if (result.optJSONObject("real_info") != null) {
                    intent.putExtra("instruction", result.optJSONObject("real_info").optString("instruction"));
                }
                intent.putExtra("qrcodetype", Constants.TYPE_COLLECTION);
                startActivity(intent);
            } else if ("FAIL".equals(result.optString("status"))) {
                if (mTask != null)
                    mTask.cancel();
                UtilToolkit.showToast(result.optString("tv_desc"));
                finish();

            }

        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (result == null) {
            return;
        }
        UtilToolkit.showToast(result);
        if (PAYMENT_ORDER.equals(sname)) {
            if (mTask != null)
                mTask.cancel();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (PAYMENT_GET_ORDER_STATUS.equals(sname)) {
            if (mTask != null)
                mTask.cancel();
            finish();
        }

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    protected void onDestroy() {
        if (mTask != null)
            mTask.cancel();
        if (MakeCollectionsActivity.activitys != null)
            MakeCollectionsActivity.activitys.remove(this);
        super.onDestroy();
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            if (getResultTimes >= REQUEST_TIEMS) {
                this.cancel();
                // 超时退出
                finish();
            } else {
                getPaymentStatus();
                //Log.d("iii", "getResultTimes:" + getResultTimes);
                getResultTimes++;
            }

        }

    }

}
