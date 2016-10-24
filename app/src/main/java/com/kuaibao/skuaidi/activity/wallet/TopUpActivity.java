package com.kuaibao.skuaidi.activity.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.activity.wallet.entity.PayInfoResponse;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dialog.menu.TopUpMenuDialog;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.payali.PayResult;
import com.kuaibao.skuaidi.paywx.MD5;
import com.kuaibao.skuaidi.paywx.Util;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.wxapi.WXPayEntryActivity;
import com.socks.library.KLog;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 充值界面
 *
 * @author gudd
 */
public class TopUpActivity extends SkuaiDiBaseActivity implements TopUpMenuDialog.TopUpMenuSelectOnClickListener {
    @BindView(R.id.tv_title_des) TextView tv_title_des;// 标题
    @BindView(R.id.et_scan_money) EditText et_scan_money;// 金额输入框
    @BindView(R.id.iv_pay_icon) ImageView ivPayIcon;// 充值方式ICON
    @BindView(R.id.tv_pay_title) TextView tvPayTitle;// 充值方式标题
    @BindView(R.id.tv_pay_desc) TextView tvPayDesc;// 充值方式说明
//    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.tv_more) SkuaidiTextView tvMore;
    @BindView(R.id.chongzhi_layout) LinearLayout chongzhiLayout;
//    @BindView(R.id.rg_money_group) RadioGroup rgMoneyGroup;
    @BindView(R.id.tv_10) TextView tv_10;
    @BindView(R.id.tv_20) TextView tv_20;
    @BindView(R.id.tv_50) TextView tv_50;
    @BindView(R.id.tv_100) TextView tv_100;
    @BindView(R.id.tv_200) TextView tv_200;
    @BindView(R.id.tv_500) TextView tv_500;
    @BindView(R.id.bottom_line) TextView bottomLine;

    private Activity activity;
    private Context context;
    private InputMethodManager manager;

    // 状态栏的高度
    private int statusBarHeight;
    // 软键盘的高度
    private int keyboardHeight;
    // 软键盘的显示状态
    private boolean isShowKeyboard;
    private int refercnce_line_y;
    private int rootViewHeight;

    private Intent intent;
    private String payment_type = "";// 支付方式
    private boolean lock = false;
    private int rechargeWay = 1;
    private TopUpMenuDialog topUpMenuDialog;
//    private TopUpAdapter adapter;
//    private List<TopUpMoney> listTopUpMoney;
    // 创建订单信息********以下*********
    private String createTime = "";// 创建 时间
    private String total_amount = "";// 充值金额
    private String good_name = "";// 商品名称
    private String buyer_sp_username = "";// 被充值人公司网点和姓名
    private String order_no = "";// 订单号
    private String return_url = "";// 支付宝sdk请求快递员后台
    // 创建订单信息********以上*********
    public static final int SDK_PAY_FLAG = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    lock = false;
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    //String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    intent = new Intent(activity, TopUpDetailActivity.class);
                    intent.putExtra("createTime", mPayInfo.getCreatetime());
                    intent.putExtra("total_amount", mPayInfo.getTotal_amount());
                    intent.putExtra("good_name", mPayInfo.getGood_name());
                    intent.putExtra("buyer_sp_username", mPayInfo.getBuyer_sp_username());
                    intent.putExtra("order_no", mPayInfo.getOrder_no());
                    //System.out.println("gudd result status   " + resultStatus);
                    //MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
                    if (TextUtils.equals(resultStatus, "9000")) {
                        intent.putExtra("pay_status", "success");
                        startActivity(intent);
                        //EventBus.getDefault().post(m);
                    } else {
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            intent.putExtra("pay_status", "review");
                            startActivity(intent);
                            //EventBus.getDefault().post(m);
                        } else if (TextUtils.equals(resultStatus, "6001")) {// 中途取消
                            UtilToolkit.showToast("你已取消订单");
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            UtilToolkit.showToast("订单支付失败，请重新支付");
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.top_up_activity);
        activity = this;
        context = this;
        ButterKnife.bind(this);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        req = new PayReq();
        sb = new StringBuffer();
        msgApi.registerApp(Constants.APP_ID);

        topUpMenuDialog = new TopUpMenuDialog(activity);
        topUpMenuDialog.setTopUpMenuSelectOnclickListener(this);
        initView();
        initListener();


        statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
        //设置监听事件
        chongzhiLayout.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        bottomLine.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                bottomLine.getLocationOnScreen(location);
                refercnce_line_y=location[1];
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // 应用可以显示的区域。此处包括应用占用的区域，
            // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
            Rect r = new Rect();
            chongzhiLayout.getWindowVisibleDisplayFrame(r);

            // 屏幕高度。这个高度不含虚拟按键的高度
            int screenHeight = chongzhiLayout.getRootView().getHeight();
            rootViewHeight=screenHeight;
            int heightDiff = screenHeight - (r.bottom - r.top);

            // 在不显示软键盘时，heightDiff等于状态栏的高度
            // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
            // 所以heightDiff大于状态栏高度时表示软键盘出现了，
            // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
            if(keyboardHeight == 0 && heightDiff > statusBarHeight){
                keyboardHeight = heightDiff - statusBarHeight;
            }

            if (isShowKeyboard) {
                // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                // 说明这时软键盘已经收起
                if (heightDiff <= statusBarHeight) {
                    isShowKeyboard = false;
                    onHideKeyboard();
                }
            } else {
                // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
                // 说明这时软键盘已经弹出
                if (heightDiff > statusBarHeight) {
                    isShowKeyboard = true;
                    onShowKeyboard();
                }
            }
        }
    };

    private void onShowKeyboard() {
        // 在这里处理软键盘弹出的回调
        chongzhiLayout.post(new Runnable() {
            @Override
            public void run() {
                //KLog.i("kb","rootViewHeight："+rootViewHeight+";Y："+refercnce_line_y+";keyboardHeight："+keyboardHeight);
                int padding=keyboardHeight-(rootViewHeight-(refercnce_line_y+20));
                //KLog.i("kb","padding："+padding);
                if(padding>0) chongzhiLayout.setPadding(0, -padding, 0, 0);
            }
        });
    }

    private void onHideKeyboard() {
        // 在这里处理软键盘收回的回调
        if(chongzhiLayout.getPaddingTop()!=0){
            chongzhiLayout.post(new Runnable() {
                @Override
                public void run() {
                    chongzhiLayout.setPadding(0, 0, 0, 0);
                }
            });
        }
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
            public void afterTextChanged(Editable s)
            {
//                refreshRecyclerView(s.toString());
                refreshMoneyItemStatus(s.toString());
            }
        });
    }

    private void refreshMoneyItemStatus(String money){
        int[] tvs = new int[]{R.id.tv_10,R.id.tv_20,R.id.tv_50,R.id.tv_100,R.id.tv_200,R.id.tv_500};
        for (int tv : tvs){
            TextView t = (TextView) findViewById(tv);
            String s = t.getText().toString();
            if (s.substring(0,s.indexOf("元")).equals(money)){
                t.setTextColor(Utility.getColor(activity,R.color.white));
                t.setBackgroundResource(R.drawable.shape_full_green_main);
            }else{
                t.setTextColor(Utility.getColor(activity,R.color.default_green));
                t.setBackgroundResource(R.drawable.shape_fram_green_main);
            }
        }
    }

    private void initView() {
        setRechargeWay();
//        setAdapter();
        // 设置hint字体大小
        SpannableString ss;
        ss = new SpannableString("请输入你要充值的金额");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        et_scan_money.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失

        tv_title_des.setText("充值");
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText("帮助");
    }

    /**
     * 设置充值方式
     **/
    private void setRechargeWay() {
        int selectItem = SkuaidiSpf.getSelectItem(activity);
        rechargeWay = selectItem;// 0：支付宝扫码充值| 1：支付宝快捷充值 | 2：微信充值 | 3：可提现转可消费充值
        String[] payTitle = new String[]{"支付宝扫码支付", "支付宝快捷支付", "微信支付", "可提现转入可消费"};
        String[] payDesc = new String[]{"推荐支付宝安装在其他手机上的用户使用", "推荐支付宝安装在本机的用户使用", "推荐已安装微信的用户使用", "将提现金额转入可消费金额"};
        Drawable[] payIcon = new Drawable[]{Utility.getDrawable(activity, R.drawable.btn_alipay), Utility.getDrawable(activity, R.drawable.btn_alipay), Utility.getDrawable(activity, R.drawable.btn_weixin), Utility.getDrawable(activity, R.drawable.btn_turn)};

        for (int i = 0; i < 4; i++) {
            if (selectItem == i) {
                ivPayIcon.setBackground(payIcon[i]);
                tvPayTitle.setText(payTitle[i]);
                tvPayDesc.setText(payDesc[i]);
            }
        }

    }

    /*private void setAdapter() {
        // 传入所有列数的最小公倍数，1和3的最小公倍数为3，即意味着每一列将被分为3格
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(40));
        String[] money = new String[]{"10.00", "20.00", "50.00", "100.00", "200.00", "500.00"};
        listTopUpMoney = new ArrayList<>();
        for (int i = 0; i < money.length; i++) {
            TopUpMoney topUpMoney = new TopUpMoney();
            topUpMoney.setMoney(money[i]);
            topUpMoney.setSelect(false);
            listTopUpMoney.add(topUpMoney);
        }
        adapter = new TopUpAdapter(activity, listTopUpMoney);
        adapter.setTopUpSelectMoneyOnClickCallBack(this);
        recyclerView.setAdapter(adapter);

    }*/

    @Override
    public void onClick(int selectItem) {
        setRechargeWay();
    }

//    @Override
//    public void onClick(TopUpMoney topUpMoney) {
//        refreshRecyclerView(topUpMoney.getMoney());
//        et_scan_money.setText(topUpMoney.getMoney());
//        et_scan_money.setSelection(et_scan_money.getText().length());
//    }

    /*private void refreshRecyclerView(String money) {
        listTopUpMoney = adapter.getList();
        for (int i = 0; i < listTopUpMoney.size(); i++) {
            if (listTopUpMoney.get(i).getMoney().equals(money)) {
                listTopUpMoney.get(i).setSelect(true);
            } else {
                listTopUpMoney.get(i).setSelect(false);
            }
        }
        adapter.notifyDataSetChanged();
    }*/

    @OnClick({R.id.rl_pay, R.id.tv_more, R.id.iv_title_back,R.id.tv_next
    ,R.id.tv_10
    ,R.id.tv_20
    ,R.id.tv_50
    ,R.id.tv_100
    ,R.id.tv_200
    ,R.id.tv_500})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_pay:
                topUpMenuDialog.builder().setCancledOnTouchOutside(true).showDialog();
                break;
            case R.id.tv_more:// 帮助
                UMShareManager.onEvent(activity, "TopUpActivity_btn_help", "topUpActivity", "充值界面：帮助");
                intent = new Intent(TopUpActivity.this, WebViewActivity.class);
                intent.putExtra("fromwhere", "help");
                startActivity(intent);
                break;
            case R.id.iv_title_back:// 返回
                finish();
                break;
            case R.id.tv_next:// 下一步
                int selectItem = SkuaidiSpf.getSelectItem(activity);
                String pay_price;// 支付金额
                switch (selectItem){
                    case 0:// 支付宝扫码支付
                        if (lock)
                            return;
                        lock = true;
                        rechargeWay = 2;
                        payment_type = "alisan";
                        pay_price = et_scan_money.getText().toString();
                        payMethod(payment_type, pay_price);
                        showProgressDialog("");//activity, "请稍候...");
                        break;
                    case 1:// 支付宝快捷支付
                        if (lock)
                            return;
                        lock = true;
                        rechargeWay = 3;
                        payment_type = "alipay";
                        pay_price = et_scan_money.getText().toString();
                        //payMethod(payment_type, pay_price);
                        doAliPay(payment_type,pay_price);
                        showProgressDialog("");//activity, "请稍候...");
                        break;
                    case 2:// 微信支付
                        if (lock)
                            return;
                        lock = true;
                        rechargeWay = 1;
                        payment_type = "wxpay";
                        pay_price = et_scan_money.getText().toString();
                        payMethod(payment_type, pay_price);
                        showProgressDialog("");//activity, "请稍候...");
                        break;
                    case 3:// 可提现转可消费
                        if (lock)
                            return;
                        lock = true;
                        payment_type = "baidupay";
                        pay_price = et_scan_money.getText().toString();
                        withdrawChange(pay_price);
                        showProgressDialog("");//activity, "请稍候...");
                        break;
                }
                break;
            case R.id.tv_10:
                UMShareManager.onEvent(activity, "TopUpActivity_btn_10", "topUpActivity", "充值界面：10元");
                selectedMoneyItem(v);
                et_scan_money.setText("10.00");
                et_scan_money.setSelection(et_scan_money.getText().toString().length());
                break;
            case R.id.tv_20:
                UMShareManager.onEvent(activity, "TopUpActivity_btn_20", "topUpActivity", "充值界面：20元");
                selectedMoneyItem(v);
                et_scan_money.setText("20.00");
                et_scan_money.setSelection(et_scan_money.getText().toString().length());
                break;
            case R.id.tv_50:
                UMShareManager.onEvent(activity, "TopUpActivity_btn_50", "topUpActivity", "充值界面：50元");
                selectedMoneyItem(v);
                et_scan_money.setText("50.00");
                et_scan_money.setSelection(et_scan_money.getText().toString().length());
                break;
            case R.id.tv_100:
                UMShareManager.onEvent(activity, "TopUpActivity_btn_100", "topUpActivity", "充值界面：100元");
                selectedMoneyItem(v);
                et_scan_money.setText("100.00");
                et_scan_money.setSelection(et_scan_money.getText().toString().length());
                break;
            case R.id.tv_200:
                UMShareManager.onEvent(activity, "TopUpActivity_btn_200", "topUpActivity", "充值界面：200元");
                selectedMoneyItem(v);
                et_scan_money.setText("200.00");
                et_scan_money.setSelection(et_scan_money.getText().toString().length());
                break;
            case R.id.tv_500:
                UMShareManager.onEvent(activity, "TopUpActivity_btn_500", "topUpActivity", "充值界面：500元");
                selectedMoneyItem(v);
                et_scan_money.setText("500.00");
                et_scan_money.setSelection(et_scan_money.getText().toString().length());
                break;
            default:
                break;
        }
    }

    private void selectedMoneyItem(View v){
        int[] views = new int[]{R.id.tv_10,R.id.tv_20,R.id.tv_50,R.id.tv_100,R.id.tv_200,R.id.tv_500};
        for (int view : views){
            TextView tv = (TextView) findViewById(view);
//            String s = tv.getText().toString();
            if (v.getId() == view){
                tv.setTextColor(Utility.getColor(activity,R.color.white));
                tv.setBackgroundResource(R.drawable.shape_full_green_main);
//                et_scan_money.setText(s.substring(0,s.indexOf("元")));
            }else{
                tv.setTextColor(Utility.getColor(activity,R.color.default_green));
                tv.setBackgroundResource(R.drawable.shape_fram_green_main);
            }
        }

    }




    /**
     选择充值金额
     */
    /*private void selectTopUpMoney(int checkedId) {
        rgMoneyGroup.clearCheck();
        for (int i = 0;i < rgMoneyGroup.getChildCount();i++){
            RadioButton rb = (RadioButton) rgMoneyGroup.getChildAt(i);
            if (rb.getId() == checkedId){
                rgMoneyGroup.getChildAt(i).setPressed(true);
                ((RadioButton) rgMoneyGroup.getChildAt(i)).setChecked(true);
            }

            UtilToolkit.showToast(((RadioButton) rgMoneyGroup.getChildAt(i)).getText().toString());
            setRadioGroupItemChecked(rb);
        }
    }*/

    /**
     设置RadioGroup中RadioButton的选择事件
     */
    /*private void setRadioGroupItemChecked(RadioButton rb) {
        if (rb.isPressed()){
            et_scan_money.setText(getMoney(rb.getText().toString()));
            et_scan_money.setSelection(et_scan_money.getText().length());
            setRadioButtonSelectedBackGround();
        }
    }*/

    /**
     将金额中的元字去掉
     */
    /*private String getMoney(String text) {
        if (!TextUtils.isEmpty(text) && text.contains("元")){
            text = text.substring(0,text.indexOf("元"));
        }
        return text;
    }*/

    /*private void setRadioButtonSelectedBackGround(){
        for (int i = 0; i < rgMoneyGroup.getChildCount(); i++){
            RadioButton rb = (RadioButton) rgMoneyGroup.getChildAt(i);
            rb.setBackgroundResource((rb.isPressed() && rb.isChecked()) ? R.drawable.shape_full_green_main : R.drawable.shape_fram_green_main);
            if (rb.isPressed() && rb.isChecked()){
                rb.setTextColor(Utility.getColor(activity,R.color.white));
            }else{
                rb.setTextColor(Utility.getColor(activity,R.color.default_green));
            }
        }
    }*/




    private PayInfoResponse mPayInfo;
    private void doAliPay(String payment_type,String price){
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.aliPayPrepare(payment_type,price).subscribe(newSubscriber(new Action1<PayInfoResponse>() {
            @Override
            public void call(PayInfoResponse payInfo) {
                if(payInfo!=null && !TextUtils.isEmpty(payInfo.getSign())){
                    mPayInfo=payInfo;
                    doPayTask(payInfo.getSign());
                }else{
                    UtilToolkit.showToast("网络繁忙,请稍后重试");
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void doPayTask(final String  orderInfo){
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(TopUpActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                KLog.i("kb", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * @param payment_type
     * @param price
     */
    private void payMethod(String payment_type, String price) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "payment.recharge");
            data.put("payment_type", payment_type);
            data.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 可提现转入可消费功能
     */
    private void withdrawChange(String money) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "withdraw/change");
            data.put("money", money);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
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
    protected void onRestart() {
        super.onRestart();
        int errorCode = WXPayEntryActivity.getPayErrorCode();
        switch (errorCode) {
            case -1:// 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
                break;
            case -2:// 无需处理。发生场景：用户不支付了，点击取消，返回APP
                UtilToolkit.showToast("支付失败");
                break;
            case 0:// 展示成功页面
                UtilToolkit.showToast("支付成功");
//                MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//                EventBus.getDefault().post(m);
                finish();
                break;
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String data1, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(data1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!isFinishing()) {
            dismissProgressDialog();//activity);
        }
        if ("payment.recharge".equals(sname)) {
            lock = false;
            if (null != result) {
                createTime = result.optString("createtime");
                total_amount = result.optString("total_amount");
                good_name = result.optString("good_name");
                buyer_sp_username = result.optString("buyer_sp_username");
                order_no = result.optString("order_no");
                return_url = result.optString("return_url");

                Map<String, String> params = new HashMap<>();
                params.put("createtime", createTime);
                params.put("total_amount", total_amount);
                params.put("good_name", good_name);
                params.put("buyer_sp_username", buyer_sp_username);
                params.put("order_no", order_no);
                params.put("payment_type", payment_type);
                params.put("return_url", return_url);

                switch (rechargeWay) {
                    case 1:// 微信支付
                        UMShareManager.onEvent(activity, "top_up_wxpay", "top_up_activity", "充值界面：微信支付");
                        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);// IWXAPI 是第三方app和微信通信的openapi接口
                        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                        if (isPaySupported) {
                            GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
                            getPrepayId.execute();
                        } else {
                            UtilToolkit.showToast("您没有安装微信或微信版本过低");
                        }
                        break;
                    case 2:// 支付宝扫码
                        UMShareManager.onEvent(activity, "top_up_alipay_sao", "top_up_activity", "充值界面：支付宝扫码支付");
                        intent = new Intent(activity, WebViewActivity.class);
                        intent.putExtra("urlParams", (Serializable) params);
                        intent.putExtra("fromwhere", "alisan");
                        startActivity(intent);
                        break;
//                    case 3:// 支付宝sdk
                    //                        UMShareManager.onEvent(activity, "top_up_alipay", "top_up_activity", "充值界面：支付宝支付");
                    //                        AliPay.pay(activity, mHandler, order_no, good_name, buyer_sp_username, et_scan_money.getText().toString(), return_url);
                    //                        break;
                    case 4:// 百度支付
                        UMShareManager.onEvent(activity, "top_up_baidupay", "top_up_activity", "充值界面：百度钱包支付");
                        intent = new Intent(activity, WebViewActivity.class);
                        intent.putExtra("urlParams", (Serializable) params);
                        intent.putExtra("fromwhere", "baidupay");
                        startActivity(intent);
                        break;
                }
            }
        } else if ("payment.promotion".equals(sname)) {
            if (null != result) {
                try {
                    JSONObject json = new JSONObject(result.toString());
                    String code = json.optString("code");
                    if ("0".equals(code)) {
                        JSONObject data = json.optJSONObject("data");
                        String status = data.optString("status");
                        if ("success".equals(status)) {
                            String resultStr = data.optString("result");// 获取到优惠通知
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if ("withdraw/change".equals(sname)) {
            UtilToolkit.showToast(msg);
//            MessageEvent m = new MessageEvent(PersonalFragment.P_REFRESH_MONEY,"");
//            EventBus.getDefault().post(m);
            finish();
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        lock = false;
        dismissProgressDialog();//activity);
        if (!TextUtils.isEmpty(result)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        lock = false;
        if (Utility.isNetworkConnected()) {
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

    PayReq req;
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    Map<String, String> resultunifiedorder;
    StringBuffer sb;

    private static final String TAG = "MicroMsg.SDKSample.PayActivity";

    /**
     * 生成签名
     */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        //Log.e("orion", packageSign);
        return packageSign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);

        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        //Log.e("orion", appSign);
        return appSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        //Log.e("orion", sb.toString());
        return sb.toString();
    }


    /**
     * 生成预付订单
     **/
    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected void onPreExecute() {
            showProgressDialog("");//TopUpActivity.this, getString(R.string.getting_prepayid));
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            dismissProgressDialog();//TopUpActivity.this);
            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
            //System.out.println("gudd result : " + result.toString());
            //System.out.println("gudd prepay_id : " + result.get("prepay_id"));
            resultunifiedorder = result;
            genPayReq();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();
            //System.out.println("gudd entity : " + entity);
            byte[] buf = Util.httpPost(url, entity);
            //System.out.println("gudd buf : " + buf);
            String content = new String(buf);
            Map<String, String> xml = decodeXml(content);
            return xml;
        }
    }

    public Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (!"xml".equals(nodeName)) {
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
            return xml;
        } catch (Exception e) {
            //Log.e("orion", e.toString());
        }
        return null;

    }

    // 用MD5生成随机数
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    // 获取当前时间
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    // 用MD5生成随机数
    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    // 请求生成预付订单ID号
    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();
        try {
            String nonceStr = genNonceStr();
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<>();
            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));// 开放平台账号ID
            packageParams.add(new BasicNameValuePair("attach", buyer_sp_username));// 附加数据new
            // String(buyer_sp_username.getBytes(),"UTF-8")
            packageParams.add(new BasicNameValuePair("body", good_name));// 商品描述new
            // String(good_name.getBytes(),"UTF-8")
            packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));// 商户号
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));// 随机字符串，不长于32们
            packageParams.add(new BasicNameValuePair("notify_url", return_url));// 通知地址
            packageParams.add(new BasicNameValuePair("out_trade_no", order_no));// 商户订单号
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));// 终端IP
            packageParams.add(new BasicNameValuePair("total_fee", Utility.getMinuteMoney(total_amount)));// 总金额
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));// 交易类型

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));// 签名

            String xmlstring = toXml(packageParams);

            return new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");
        } catch (Exception e) {
            //Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }

    // 请求支付
    private void genPayReq() {

        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");

        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);
        sb.append("sign\n" + req.sign + "\n\n");

        //System.out.println("gudd sign : " + req.sign);
        sendPayReq();// 发起支付
    }

    /**
     * 支付
     */
    private void sendPayReq() {
        // 调用IWXMsg.registerApp将应用注册到微信
        msgApi.registerApp(Constants.APP_ID);
        // 支付
        msgApi.sendReq(req);
    }
}
