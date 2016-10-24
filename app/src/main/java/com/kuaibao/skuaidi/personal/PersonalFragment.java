package com.kuaibao.skuaidi.personal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.DeliveryFeeMenuActivity;
import com.kuaibao.skuaidi.activity.InformationActivity;
import com.kuaibao.skuaidi.activity.MyVantagesActivity;
import com.kuaibao.skuaidi.activity.NoticeCenterActivity;
import com.kuaibao.skuaidi.activity.wallet.WalletMainActivity;
import com.kuaibao.skuaidi.business.entity.ResponseReadStatus;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.MyFundsAccount;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.personal.personinfo.IQRCodeActivity;
import com.kuaibao.skuaidi.personal.personinfo.ReviewInfoActivity;
import com.kuaibao.skuaidi.personal.setting.SettingActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragment;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by lgg on 2016/8/10 11:36.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #                     no bug forever                #
 * #                                                   #
 */
public class PersonalFragment extends RxRetrofitBaseFragment {
    @BindView(R.id.iv_defalt_head)
    ImageView ivHeadeImg;
    @BindView(R.id.tv_more_user_name)
    TextView tvUserName;
    @BindView(R.id.tvBrand)
    TextView tvBrand;
    @BindView(R.id.tv_userPhone)
    TextView tvUserPhone;
    @BindView(R.id.rl_delivery_fees)
    RelativeLayout rl_delivery_fees;
    @BindView(R.id.line_VD)
    View line_VD;
    @BindView(R.id.tv_more_my_account)
    TextView tv_more_my_account;
    @BindView(R.id.tvShowCredit)
    TextView tvShowCredit;
    @BindView(R.id.iv_user_qrcode)
    ImageView iv_user_qrcode;
    @BindView(R.id.iv_red_icon_notice)
    ImageView iv_red_icon_notice;
    private UserInfo mUserInfo;
    private boolean isPrepared;
    private Intent mIntent;
    private String score;
    public static final int P_REFRESH_HEAD_IMG=0xEEFF;
    public static final int P_REFRESH_ACCOUNT_INFO = 0xA1004;
    private boolean querying=false;
    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }

    public PersonalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        EventBus.getDefault().register(this);
    }

    public void initData(){
        mUserInfo=SkuaidiSpf.getLoginUser();
    }

    @Override
    protected void lazyLoad() {
        if(isPrepared && isVisible) {
            //KLog.i("kb","loadLazyData");
            //isPrepared = false;
            getAccountInfo();
        }
    }

    public void getAccountInfo(){
        if(!querying){
            querying=true;
            isPrepared = false;
            final ApiWrapper apiWrapper=new ApiWrapper();
            Subscription subscription=apiWrapper.getAccountInfo()
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            querying=false;
                        }
                    })
                    .subscribe(newSubscriber(new Action1<MyFundsAccount>() {
                @Override
                public void call(MyFundsAccount account) {
                    querying=false;
                    if (account!=null) {
                        String avail_money = account.getAvail_money();
                        String withDrawable_money = account.getWithdrawable_money();
                        score=account.getScore();
                        // 计算总额
                        if (!Utility.isEmpty(avail_money)) {
                            BigDecimal availMoney = new BigDecimal(avail_money);
                            BigDecimal withDrawableMoney = new BigDecimal(withDrawable_money);
                            BigDecimal totalMoney = availMoney.add(withDrawableMoney);// 计算得出总额
                            tv_more_my_account.setText(Utility.formatMoney(totalMoney + "元"));
                            tvShowCredit.setText(account.getScore() + "分");
                        } else {
                            tv_more_my_account.setText("0元");
                            tvShowCredit.setText("0分");
                        }
                    }
                }
            }));
            mCompositeSubscription.add(subscription);
        }
    }

    @OnClick({R.id.ll_myaccount,R.id.rl_share,R.id.rl_delivery_fees,R.id.rl_inform_center,
            R.id.ll_myVantages,R.id.rlInformationCenter,R.id.rl_more_setting,R.id.iv_user_qrcode,R.id.ll_more_user})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_more_user:
                mIntent = new Intent(getContext(), ReviewInfoActivity.class);
                startActivityForResult(mIntent,0X1A);
                break;
            case R.id.iv_user_qrcode:
                UMShareManager.onEvent(getContext(), "salesman_more_careMeQrCode", "careMeQrCode_more", "更多模块：客户扫二维码关注我");
                mIntent = new Intent(getContext(), IQRCodeActivity.class);
                startActivity(mIntent);
                break;
            case R.id.ll_myaccount://钱包
                UMShareManager.onEvent(getContext(), "salesman_more_myAccount", "salesman_more", "更多模块：我的资金账户");
                mIntent = new Intent(getContext(), WalletMainActivity.class);
                mIntent.putExtra("mark", "usercenter");
                startActivity(mIntent);
                break;
            case R.id.rl_delivery_fees://派费
                UMShareManager.onEvent(getContext(), "delivery_fees", "delivery_fees", "更多模块：派费");
                mIntent = new Intent(getContext(), DeliveryFeeMenuActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_inform_center://通知中心
                UMShareManager.onEvent(getContext(), "notice_center", "more_activity", "更多模块：通知中心");
                mIntent = new Intent(getContext(), NoticeCenterActivity.class);
                startActivity(mIntent);
                break;
            case R.id.ll_myVantages://积分
                UMShareManager.onEvent(getContext(), "salesman_more_myVantages", "salesman_more", "更多模块：我的积分");
                mIntent = new Intent(getContext(), MyVantagesActivity.class);
                if (!TextUtils.isEmpty(score)) {
                    mIntent.putExtra("score", score);
                }
                startActivity(mIntent);
                break;
            case R.id.rlInformationCenter://资讯中心
                UMShareManager.onEvent(getContext(), "information_center", "more_activity", "更多模块：资讯中心");
                mIntent = new Intent(getContext(), InformationActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_more_setting:
                UMShareManager.onEvent(getContext(), "salesman_more_setUp", "salesman_more", "更多模块：设置");
                mIntent= new Intent(getContext(), SettingActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_share://分享
                UMShareManager.onEvent(getContext(), "salesman_more_share", "salesman_more", "更多模块：邀请朋友");
                String title = "免费安装快递员APP";
                String targetUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.kuaibao.skuaidi#opened";
                String shareText = "我正在使用“快递员”APP-全国快递员都在用的取派神器，赶快来用哦，免费高效省钱！";
                onOpenShareEvent(title, shareText, targetUrl, R.drawable.share_logo);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent){
        switch (messageEvent.type){
            case P_REFRESH_HEAD_IMG:
                ivHeadeImg.post(new Runnable() {
                    @Override
                    public void run() {
                        GlideUtil.GlideHeaderImg(getActivity(),mUserInfo.getUserId(),ivHeadeImg,R.drawable.icon_yonghu, R.drawable.icon_yonghu);
                    }
                });
                break;
            case MainActivity.NEW_NOTICE_COMING:
                showNotice();
                break;
            case P_REFRESH_ACCOUNT_INFO:// 在别的界面如：收款界面|充值界面对金额处理成功以后需要调用获取金额接口
                getAccountInfo();
                break;
        }
    }

    private void showNotice(){
        ResponseReadStatus responseReadStatus=(ResponseReadStatus) ACache.get(getContext()).getAsObject("ResponseReadStatus");
        if(responseReadStatus!=null){
            iv_red_icon_notice.setVisibility(responseReadStatus.getNotice()>0 ? View.VISIBLE:View.GONE);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_personal;
    }

    @Override
    public void initViews() {
        isPrepared = true;
        tvUserName.setText(mUserInfo.getUserName());
        tvUserPhone.setText(mUserInfo.getPhoneNumber());
        // 快递品牌
        String brand = mUserInfo.getExpressFirm();
        if (brand.contains("快递")) {
            brand = brand.replace("快递", "");
        }
        tvBrand.setText(brand);
        if(E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())){
            tvBrand.setBackgroundResource(R.drawable.sto_icon_more_brand);
            iv_user_qrcode.setImageResource(R.drawable.sto_more_code);
        }else{
            tvBrand.setBackgroundResource(R.drawable.icon_more_brand);
            iv_user_qrcode.setImageResource(R.drawable.more_code);
        }
        GlideUtil.GlideHeaderImg(getActivity(),mUserInfo.getUserId(),ivHeadeImg,R.drawable.icon_yonghu, R.drawable.icon_yonghu);
        if (!E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo()) && !E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo()) && !E3SysManager.BRAND_QF.equals(mUserInfo.getExpressNo())) {
            rl_delivery_fees.setVisibility(View.GONE);
            line_VD.setVisibility(View.GONE);
        }
    }

}
