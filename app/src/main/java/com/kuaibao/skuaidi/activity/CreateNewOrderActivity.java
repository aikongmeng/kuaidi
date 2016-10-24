package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.BarcodeUtils;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cj on 2016/7/6.
 *
 * 创建完善订单信息
 */

public class CreateNewOrderActivity extends SkuaiDiBaseActivity implements View.OnClickListener{

    private TextView tv_info_type1;
    private TextView tv_title_des, tv_info_weixin, tv_info_qq, tv_info_message, tv_qrcode_desc,
            tv_info_tag1, tv_info_tag2, tv_info_tag3;
    private ImageView qrCodeView;
    private Context context;
    private Order order;
    private String urll;
    private String urls;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.SHORT_URL_SUCCESS:
                    dismissProgressDialog();
                    String result = msg.obj.toString();
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONObject response = json.getJSONObject("response");
                        JSONObject body = response.getJSONObject("body");
                        JSONObject url_result = body.getJSONObject("result");
                        urls = url_result.getString("short_url");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.SHORT_URL_FAIL:
                    dismissProgressDialog();
                    break;
                case Constants.NETWORK_FAILED:
                    dismissProgressDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_create);
        context = this;
        order = (Order) getIntent().getSerializableExtra("order");
        initView();
        showProgressDialog( "加载中...");
        if(Utility.isEmpty(order) || Utility.isEmpty(order.getId())){
            urll = "http://m.kuaidihelp.com/wduser/sendexpress?mb="+ SkuaidiSpf.getLoginUser().getPhoneNumber();
//            contentSms = "我是快递员"+SkuaidiSpf.getLoginUser().getPhoneNumber()+"，即将上门收件，请点击"+url+"填写收发信息，免手写快捷寄件";
//            contentWQ = "我是快递员"+SkuaidiSpf.getLoginUser().getPhoneNumber()+"，即将上门收件，请点击填写收发信息，免手写快捷寄件";
        }else{
            urll = "http://m.kuaidihelp.com/wduser/order_info_update?mb=" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "&order_number=" + order.getId();
//            contentSms = "您已成功下单！收派员"+SkuaidiSpf.getLoginUser().getPhoneNumber()+"即将上门取件，点击"
//                    + url + "完善收发信息，免手写快捷寄件。【快递员】";
//            contentWQ = "您已成功下单！收派员"+SkuaidiSpf.getLoginUser().getPhoneNumber()+"即将上门取件，点击完善收发信息，免手写快捷寄件。【快递员】";
        }
        try {
            KuaidiApi.getshorturl(context, handler, URLEncoder.encode(urll, "GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tv_info_type1.setOnClickListener(this);
        tv_info_weixin.setOnClickListener(this);
        tv_info_qq.setOnClickListener(this);
        tv_info_message.setOnClickListener(this);
    }

    private void initView(){
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_info_tag1 = (TextView) findViewById(R.id.tv_info_tag1);
        tv_info_tag2 = (TextView) findViewById(R.id.tv_info_tag2);
        tv_info_tag3 = (TextView) findViewById(R.id.tv_info_tag3);
        tv_info_type1 = (TextView) findViewById(R.id.tv_info_type1);
        tv_info_weixin = (TextView) findViewById(R.id.tv_info_weixin);
        tv_info_qq = (TextView) findViewById(R.id.tv_info_qq);
        tv_info_message = (TextView) findViewById(R.id.tv_info_message);
        qrCodeView = (ImageView) findViewById(R.id.iv_qrcode);
        tv_qrcode_desc = (TextView) findViewById(R.id.tv_qrcode_desc);
        tv_qrcode_desc.setVisibility(View.GONE);
//        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        int size = (int) (DisplayUtil.getScreenMetrics().x/1.5);
        if(Utility.isEmpty(order) || Utility.isEmpty(order.getId())){
            tv_title_des.setText("创建订单");
            tv_info_tag1.setText("方式一：手动输入创建订单");
            tv_info_tag2.setText("方式二：邀请客户填写订单");
            tv_info_tag3.setText("方式三：客户面对面创建订单");
            urll = "http://m.kuaidihelp.com/wduser/sendexpress?mb="+ SkuaidiSpf.getLoginUser().getPhoneNumber();
        }else {
            tv_title_des.setText("完善信息");
            tv_info_type1.setText("立即完善");
            tv_info_tag1.setText("方式一：手动输入完善订单");
            tv_info_tag2.setText("方式二：邀请客户完善订单");
            tv_info_tag3.setText("方式三：客户面对面完善订单");
            urll = "http://m.kuaidihelp.com/wduser/order_info_update?mb=" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "&order_number=" + order.getId();
        }
        qrCodeView.setImageBitmap(BarcodeUtils.createQRImage(urll, size, size, null));
    }

    @Override
    public void onClick(View v) {
        String contentSms;
        String contentWQ;
        contentSms = "我是快递员"+SkuaidiSpf.getLoginUser().getPhoneNumber()+"，即将上门收件，请点击"+urls+" 填写收发信息，免手写快捷寄件";
        contentWQ = "我是快递员"+SkuaidiSpf.getLoginUser().getPhoneNumber()+"，即将上门收件，请点击填写收发信息，免手写快捷寄件";
        switch (v.getId()){
            case R.id.tv_info_type1:
                submitType1();
                break;
            case R.id.tv_info_weixin:
                new ShareAction(CreateNewOrderActivity.this)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle("填写订单信息")
                        .withText(contentWQ)
                        .withTargetUrl(urll)
                        .withMedia(new UMImage(context, R.drawable.share_order))
                        .share();
                break;
            case R.id.tv_info_qq:
                new ShareAction(CreateNewOrderActivity.this)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .withTitle("填写订单信息")
                        .withText(contentWQ)
                        .withTargetUrl(urll)
                        .withMedia(new UMImage(context, R.drawable.share_order))
                        .share();
                break;
            case R.id.tv_info_message:
                if(Utility.isEmpty(urls)){
                    urls = urll;
                    return;
                }
                if(Utility.isEmpty(order) || Utility.isEmpty(order.getId())){
                    doSendSMSTo("", contentSms);
                }else{
                    doSendSMSTo(StringUtil.isEmpty(order.getSenderPhone()), contentSms);
                }
                break;
            default:
                break;
        }
    }

    private void submitType1(){
        if(Utility.isNetworkConnected()) {

            Intent intent = new Intent(context, UpdateAddressorActivity.class);
            if (Utility.isEmpty(order) || Utility.isEmpty(order.getId())) {
                order = new Order();
            }
            intent.putExtra("update", order);
            startActivityForResult(intent, 223);
        }else{
            Utility.showFailDialog(context, "你的网络不太顺畅哦~\n请检查网络", tv_info_type1.getRootView());
        }
    }

    /**
     * 调起系统发短信功能
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber,String message){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 223 && resultCode == 224){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
