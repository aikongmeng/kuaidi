package com.kuaibao.skuaidi.personal.personinfo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.constant.IAMapLocation;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class ISignActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
//    @BindView(R.id.loadView)
//    LoadingView mLoadingView;
    @BindView(R.id.tv_sign_status)
    TextView tv_sign_status;
    @BindView(R.id.btn_sign)
    TextView btn_sign;
    private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isign);
        mUserInfo=SkuaidiSpf.getLoginUser();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSignStatus();
    }

    private void getSignStatus(){
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.queryUserSignStatus(mUserInfo.getUserId(),mUserInfo.getPhoneNumber()).subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                if(jsonObject!=null){
                    initView(jsonObject.getString("create_time"),jsonObject.getString("day"),jsonObject.getString("up_time"));
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void initView(String createTime,String day,String up_time){
        if("1".equals(day)){//今天已签到
            if("1".equals(up_time)){//五分钟内已签到
                initHadSign(TextUtils.isEmpty(createTime) ? System.currentTimeMillis():UtilityTime.timeStringToTimeStamp(createTime,UtilityTime.YYYY_MM_DD_HH_MM_SS));
            }else{
                btn_sign.setEnabled(true);
                Drawable img_left = ContextCompat.getDrawable(this,R.drawable.batch_add_checked);
                img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
                tv_sign_status.setCompoundDrawables(img_left,null,null,null);
                tv_sign_status.setCompoundDrawablePadding(DisplayUtil.dip2px(10));
                IAMapLocation iaMapLocation=(IAMapLocation)ACache.get(this).getAsObject("amapLocation");
                String text="最新签到:  "+ UtilityTime.timeFormat2(TextUtils.isEmpty(createTime) ? System.currentTimeMillis():UtilityTime.timeStringToTimeStamp(createTime,UtilityTime.YYYY_MM_DD_HH_MM_SS))+"  "+(iaMapLocation==null?"":iaMapLocation.getAddress());
                tv_sign_status.setTextColor(ContextCompat.getColor(this,R.color.gray_1));
                tv_sign_status.setText(text);
            }
        }else{
            btn_sign.setEnabled(true);
            tv_sign_status.setText("今天还没签到哦,立即签到");
        }
    }

    private void initView(){
        tvDes.setText("签到");
    }

    private void initHadSign(long lastSignTime){
        btn_sign.setEnabled(false);
        String textStr = "已签到\n5分钟后再来";
        SpannableStringBuilder builder = new SpannableStringBuilder(textStr);
        builder.setSpan(new AbsoluteSizeSpan(36), textStr.length()-6, textStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.red_f74739)), textStr.length()-6, textStr.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        btn_sign.setText(builder);
        Drawable img_left = ContextCompat.getDrawable(this,R.drawable.batch_add_checked);
        img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
        tv_sign_status.setCompoundDrawables(img_left,null,null,null);
        tv_sign_status.setCompoundDrawablePadding(DisplayUtil.dip2px(10));
        IAMapLocation iaMapLocation=(IAMapLocation)ACache.get(this).getAsObject("amapLocation");
        String text="最新签到:  "+ UtilityTime.timeFormat2(lastSignTime)+"  "+(iaMapLocation==null?"":iaMapLocation.getAddress());
        tv_sign_status.setTextColor(ContextCompat.getColor(this,R.color.gray_1));
        tv_sign_status.setText(text);
    }

    @OnClick({R.id.iv_title_back,R.id.btn_sign})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_sign:
                doSign();
                break;
        }
    }

    private void doSign(){
        btn_sign.setEnabled(false);
        //mLoadingView.setVisibility(View.VISIBLE);
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.userSign(buildParams())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //mLoadingView.setVisibility(View.GONE);
                        btn_sign.setEnabled(true);
//                        if(throwable instanceof RetrofitUtil.APIException){
//                            RetrofitUtil.APIException exception=(RetrofitUtil.APIException) throwable;
//                            if(exception.code==1008){
//                                initHadSign(System.currentTimeMillis());
//                            }else{
//                                btn_sign.setEnabled(true);
//                            }
//                        }else{
//                            btn_sign.setEnabled(true);
//                        }
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                //mLoadingView.setVisibility(View.GONE);
                if(jsonObject!=null){
                    String lastTime=jsonObject.getString("create_time");
                    initHadSign(TextUtils.isEmpty(lastTime) ? System.currentTimeMillis():UtilityTime.timeStringToTimeStamp(lastTime,UtilityTime.YYYY_MM_DD_HH_MM_SS));
                    UtilToolkit.showToast(jsonObject.getString("qianDaoDesc"));
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private Map<String,String> buildParams(){
        LatitudeAndLongitude latitudeAndLongitude=SkuaidiSpf.getLatitudeOrLongitude(getApplicationContext());
        Map<String,String> params=new HashMap<>();
        params.put("mobile",mUserInfo.getPhoneNumber());
        params.put("userID",mUserInfo.getUserId());
        params.put("ing",latitudeAndLongitude.getLatitude());
        params.put("long",latitudeAndLongitude.getLongitude());
        return params;
    }


}
