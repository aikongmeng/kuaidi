package com.kuaibao.skuaidi.personal.personinfo;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class IQRCodeActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.iv_defalt_head)
    ImageView iv_defalt_head;
    private UserInfo mUserInfo;
    @BindView(R.id.iv_WeChat)
    ImageView iv_wechat;
    @BindView(R.id.iv_alipay)
    ImageView iv_alipay;
    @BindView(R.id.tv_more_user_name)
    TextView tv_more_user_name;
    @BindView(R.id.tv_userPhone)
    TextView tv_userPhone;
    @BindView(R.id.tv_wechat_msg)
    TextView tv_wechat_msg;
    @BindView(R.id.tv_alipay_msg)
    TextView tv_alipay_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iqrcode);
        mUserInfo= SkuaidiSpf.getLoginUser();
        initView();
        getQRCode();
    }

    @OnClick({R.id.iv_title_back,R.id.tv_wechat_msg,R.id.tv_alipay_msg})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_wechat_msg:
                if("重新加载".equals(tv_wechat_msg.getText().toString())){
                    getQRCode();
                }
                break;
            case R.id.tv_alipay_msg:
                if("重新加载".equals(tv_alipay_msg.getText().toString())){
                    getQRCode();
                }
                break;
        }
    }

    private void initView(){
        tv_title_des.setText("我的二维码");
        //String headUrl= Constants.URL_HEADER_ROOT+ "counterman_" + mUserInfo.getUserId() + ".jpg";
        //GlideUtil.GlideCircleImg(this, headUrl,iv_defalt_head, R.drawable.geng_icon_touxiang, R.drawable.geng_icon_touxiang);
        GlideUtil.GlideHeaderImg(this,mUserInfo.getUserId(),iv_defalt_head,R.drawable.icon_yonghu, R.drawable.icon_yonghu);
        tv_more_user_name.setText(mUserInfo.getUserName());
        tv_userPhone.setText(mUserInfo.getPhoneNumber());
    }

    private void getQRCode(){
        Map<String,Object> params=new HashMap<>();
        params.put("action","collect");
        JSONArray jsonArray=new JSONArray();
        jsonArray.add("weixin");
        jsonArray.add("alipay");
        params.put("platform",jsonArray.toJSONString());
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.getUserQR(params).subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                if(jsonObject!=null){
                    if(!TextUtils.isEmpty(jsonObject.getString("weixin"))){
                        GlideUtil.GlideUrlToImg(IQRCodeActivity.this,jsonObject.getString("weixin"),iv_wechat);
                        tv_wechat_msg.setText("客户用微信扫描可以关注我");
                        tv_wechat_msg.setTextColor(ContextCompat.getColor(IQRCodeActivity.this,R.color.gray_2));
                    }else{
                        Glide.with(IQRCodeActivity.this).load(R.drawable.icon_error_qrcode).into(iv_wechat);
                        tv_wechat_msg.setText("重新加载");
                        tv_wechat_msg.setTextColor(ContextCompat.getColor(IQRCodeActivity.this,R.color.default_green_2));
                    }
                    if(!TextUtils.isEmpty(jsonObject.getString("alipay"))){
                        GlideUtil.GlideUrlToImg(IQRCodeActivity.this,jsonObject.getString("alipay"),iv_alipay);
                        tv_alipay_msg.setText("客户用支付宝扫描可以关注我");
                        tv_alipay_msg.setTextColor(ContextCompat.getColor(IQRCodeActivity.this,R.color.gray_2));
                    }else{
                        Glide.with(IQRCodeActivity.this).load(R.drawable.icon_error_qrcode).into(iv_alipay);
                        tv_alipay_msg.setText("重新加载");
                        tv_alipay_msg.setTextColor(ContextCompat.getColor(IQRCodeActivity.this,R.color.default_green_2));
                    }
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }
}
