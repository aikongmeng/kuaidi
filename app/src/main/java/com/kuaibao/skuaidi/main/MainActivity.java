package com.kuaibao.skuaidi.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.david.gradientuilibrary.GradientIconView;
import com.david.gradientuilibrary.GradientTextView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.SendSMSPhoneAboutExpressNoCache;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.BusinessFragment;
import com.kuaibao.skuaidi.business.entity.ResponseReadStatus;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.circle.CircleFragment;
import com.kuaibao.skuaidi.customer.service.CustomService;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.constant.IAMapLocation;
import com.kuaibao.skuaidi.main.constant.MainConst;
import com.kuaibao.skuaidi.manager.IDownloadManager;
import com.kuaibao.skuaidi.personal.PersonalFragment;
import com.kuaibao.skuaidi.personal.personinfo.authentication.RealNameAuthActivity;
import com.kuaibao.skuaidi.personal.setting.accountmanager.AccountManagerActivity;
import com.kuaibao.skuaidi.personal.setting.accountmanager.AccountUtils;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragmentActivity;
import com.kuaibao.skuaidi.service.AlarmReceiver;
import com.kuaibao.skuaidi.service.MyAudioCheckService;
import com.kuaibao.skuaidi.service.OnlineService;
import com.kuaibao.skuaidi.service.RomUtils;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yuntongxun.ecsdk.ECDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lgg on 2016/8/10 10:47.
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
public class MainActivity extends RxRetrofitBaseFragmentActivity implements ViewPager.OnPageChangeListener,AMapLocationListener {
    @BindView(com.kuaibao.skuaidi.R.id.viewpager_show_content)
    ViewPager mViewPager;
    @BindView(R.id.giv_business_icon)
    GradientIconView tabBusinessIcon;
    @BindView(R.id.giv_circle_icon)
    GradientIconView tabCircleIcon;
    @BindView(R.id.giv_personal_icon)
    GradientIconView tabPersonalIcon;
    @BindView(R.id.giv_business_tv)
    GradientTextView tabBusinessTv;
    @BindView(R.id.giv_circle_tv)
    GradientTextView tabCircleTv;
    @BindView(R.id.giv_personal_tv)
    GradientTextView tabPersonalTv;
    @BindView(R.id.iv_red_icon_1)
    ImageView iv_red_icon_1;
    @BindView(R.id.iv_red_icon_3)
    ImageView iv_red_icon_3;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private List<GradientIconView> mTabIconIndicator = new ArrayList<>();
    private List<GradientTextView> mTabTextIndicator = new ArrayList<>();

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;

    private int tabId=0;
    private UserInfo mUserInfo;
    public static final int NEW_NOTICE_COMING=0xABAB;
    private CompositeSubscription mCompositeSubscription;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicSkinChangeManager.changSkinByLoginUser();
        setContentView(com.kuaibao.skuaidi.R.layout.activity_main);
        EventBus.getDefault().register(this);
        AccountUtils.insertOrUpdateKBAccount();
        initData();
        initWidget();
        tabId = getIntent().getIntExtra("tabid", 0);
        if(tabId!=0){
            setCurrentTab(tabId);
        }
        mCompositeSubscription = new CompositeSubscription();
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                RomUtils.SKuaidiAlertWindowPermissionCheck(MainActivity.this,true);
                if(!SkuaidiSpf.hadTransferOldCallLog()){
                    UpdateUtils.transferOldRecords(getApplicationContext(),mUserInfo.getPhoneNumber());
                }
                startServices();
                doAsyncTasks();
            }
        },2000);
    }

    private void initData(){
        mUserInfo=SkuaidiSpf.getLoginUser();
        String headImgSuffix="counterman_" + mUserInfo.getUserId() + ".jpg";
        File file=new File(Constants.HEADER_PATH+headImgSuffix);
        String filePath=file.getAbsolutePath();
        if(!FileUtils.fileExists(filePath.substring(0,filePath.lastIndexOf("/")))){
            FileUtils.fileMkdirs(filePath.substring(0,filePath.lastIndexOf("/")));
        }
        IDownloadManager.getInstance().startDownLoadTask(Uri.parse(Constants.URL_HEADER_ROOT+headImgSuffix),Uri.parse(filePath),null);
        updateReviewInfo();

        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(Build.CPU_ABI) && Build.CPU_ABI.contains("x86")){
                }else{
                    PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, MainConst.BD_PUSH_RELEASE_KEY);
                }
                if(TextUtils.isEmpty(SkuaidiSpf.getCustomerLastSyncTime(mUserInfo.getUserId()))) {
                    Intent intent1=new Intent(MainActivity.this, CustomService.class);
                    startService(intent1);
                }
            }
        },3000);
    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent){
        switch(messageEvent.type){
            case SKuaidiApplication.EXIT_ALL_NOTIFY://注销登录
                finish();
                break;
            case AccountManagerActivity.CHANGE_ACCOUNT://切换账号
                changeGloableInfo();
                break;
            case RealNameAuthActivity.UPDATE_REAL_NAME_COMPLETE:
                changeGloableInfo();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                showReadState();//此接口调用频繁,延时1s调用确保界面加载显示完毕,防止调用频繁在低端机卡顿
                if(mViewPager.getCurrentItem()==2){
                    MessageEvent messageEvent=new MessageEvent(PersonalFragment.P_REFRESH_ACCOUNT_INFO,"");
                    EventBus.getDefault().post(messageEvent);
                }
            }
        },500);
    }

    private void showReadState() {
        if (Utility.isNetworkConnected()) {
            final ApiWrapper apiWrapper = new ApiWrapper();
            Subscription subscription = apiWrapper.getReadStatus("courier").subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<ResponseReadStatus>() {
                @Override
                public void call(ResponseReadStatus result) {
                    if (result != null) {
                        ACache.get(getApplicationContext()).put("ResponseReadStatus",result);
                        SkuaidiSpf.saveClientIsVIP(getApplicationContext(), result.getVip());// 保存用户是否是VIP用户状态【VIP特权可直接发送短信】
                        if(result!=null){
                            iv_red_icon_1.setVisibility(result.isHasNotice()?View.VISIBLE:View.GONE);
                            iv_red_icon_3.setVisibility(result.getNotice()>0? View.VISIBLE:View.GONE);
                        }
                        EventBus.getDefault().post(new MessageEvent(MainActivity.NEW_NOTICE_COMING,""));
                    }
                }
            }));
            mCompositeSubscription.add(subscription);
        }
    }

    private void changeGloableInfo(){
        DynamicSkinChangeManager.changSkinByLoginUser();
        setStatusBar();
        initData();
        initStyle();
        BusinessFragment businessFragment=(BusinessFragment) mTabs.get(0);
        businessFragment.initData();
        businessFragment.initViews();
        CircleFragment circleFragment=(CircleFragment) mTabs.get(1);
        circleFragment.initViews();
        PersonalFragment personalFragment=(PersonalFragment)mTabs.get(2);
        personalFragment.initData();
        personalFragment.initViews();
        personalFragment.getAccountInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    private void initStyle(){
        if(E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())){
            tabBusinessIcon.setTopIconView(ContextCompat.getDrawable(MainActivity.this,R.drawable.sto_bottom_menu_business_press));
            tabCircleIcon.setTopIconView(ContextCompat.getDrawable(MainActivity.this,R.drawable.sto_bottom_menu_circle_press));
            tabPersonalIcon.setTopIconView(ContextCompat.getDrawable(MainActivity.this,R.drawable.sto_bottom_menu_personal_press));
            tabBusinessTv.setTopTextViewColor(ContextCompat.getColor(MainActivity.this,R.color.sto_text_color));
            tabCircleTv.setTopTextViewColor(ContextCompat.getColor(MainActivity.this,R.color.sto_text_color));
            tabPersonalTv.setTopTextViewColor(ContextCompat.getColor(MainActivity.this,R.color.sto_text_color));
        }else{
            tabBusinessIcon.setTopIconView(ContextCompat.getDrawable(MainActivity.this,R.drawable.bottom_menu_business_press));
            tabCircleIcon.setTopIconView(ContextCompat.getDrawable(MainActivity.this,R.drawable.bottom_menu_circle_press));
            tabPersonalIcon.setTopIconView(ContextCompat.getDrawable(MainActivity.this,R.drawable.bottom_menu_personal_press));
            tabBusinessTv.setTopTextViewColor(ContextCompat.getColor(MainActivity.this,R.color.title_bg));
            tabCircleTv.setTopTextViewColor(ContextCompat.getColor(MainActivity.this,R.color.title_bg));
            tabPersonalTv.setTopTextViewColor(ContextCompat.getColor(MainActivity.this,R.color.title_bg));
        }
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    private void updateReviewInfo() {
        String localReviewInfo = UpdateReviewInfoUtil.getCurrentReviewStatus();
        if (TextUtils.isEmpty(localReviewInfo)) {
            UpdateReviewInfoUtil.updateReviewInfo(false);
        }
    }

    private void startServices(){
        Intent intent = new Intent(this, MyAudioCheckService.class);
        startService(intent);
        if (!Utility.isServiceRunning(getApplicationContext(), MainConst.ONLINE_SERVICE_NAME)) {
            Intent intent2 = new Intent(MainActivity.this, OnlineService.class);
            startService(intent2);
        }
    }

    private void doAsyncTasks(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(!TextUtils.isEmpty(Build.CPU_ABI) && Build.CPU_ABI.contains("x86")){
                }else{
                    checkVersion();
                    startAmapLocation();
                    judgeTimeUpdataDb();
                    setReminder();
                }
            }
        }.start();
    }

    /**
     * 判断发短信时候保存到数据库中的时间是不是当前使用手机app的当前时间如果是当前时间则系统不对数据库发送手机号编辑进行重新设置，
     * 否则重置数据库中发短信的时间
     */
    private void judgeTimeUpdataDb() {
        List<SendSMSPhoneAboutExpressNoCache> sendSMSCaches = SKuaidiApplication.getInstance().getFinalDbCache().findAll(SendSMSPhoneAboutExpressNoCache.class, "id = 1");
        if (sendSMSCaches != null && sendSMSCaches.size() != 0) {
            SendSMSPhoneAboutExpressNoCache cache = sendSMSCaches.get(0);
            if (Utility.getSMSCurTime().substring(0, 8).equals(cache.getTodayTime_str().substring(0, 8))) {
                return;// 如果数据库中的时间和当前的时间一致则跳出方法
            } else {
                cache.setLastNo(0);// 将数据库中的编号设置为0
                SKuaidiApplication.getInstance().getFinalDbCache().update(cache, "id = 1");// 更新数据库
            }
        }
    }

    /**
     * 定时提醒设置
     *
     * @param
     */
    private void setReminder() {
        Calendar calendar = Calendar.getInstance();
        long systemTime = System.currentTimeMillis();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 进行闹铃注册
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24 , sender);
    }

    /**
     * 检查是App是否有新版本，如果有则提示更新，无则跳过
     */
    private void checkVersion() {
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);// 不强制在Wifi环境下才更新
    }

    private void startAmapLocation() {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
        }
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(60*1000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                SkuaidiSpf.saveLatitudeAndLongitude(getApplicationContext(), amapLocation.getLatitude() + "", amapLocation.getLongitude() + "");// 保存到sp
                IAMapLocation iaMapLocation=new IAMapLocation();
                iaMapLocation.setProvince(amapLocation.getProvince());
                iaMapLocation.setCity(amapLocation.getCity());
                iaMapLocation.setArea(amapLocation.getDistrict());
                iaMapLocation.setAddress(amapLocation.getAddress());
                iaMapLocation.setLat(amapLocation.getLatitude());
                iaMapLocation.setLng(amapLocation.getLongitude());
                ACache.get(getApplicationContext()).put("amapLocation",iaMapLocation);
            } else {
                KLog.e("gd", "AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }
        }
    }

    private void setCurrentTab(int tabId){
        resetOtherTabs();
        mTabIconIndicator.get(tabId).setIconAlpha(1.0f);
        mTabTextIndicator.get(tabId).setTextViewAlpha(1.0f);
        mViewPager.setCurrentItem(tabId, false);
    }

    private void initWidget() {
        initStyle();
        tabBusinessIcon.setIconAlpha(1.0f);
        tabBusinessTv.setTextViewAlpha(1.0f);
        mTabIconIndicator.add(tabBusinessIcon);
        mTabIconIndicator.add(tabCircleIcon);
        mTabIconIndicator.add(tabPersonalIcon);

        mTabTextIndicator.add(tabBusinessTv);
        mTabTextIndicator.add(tabCircleTv);
        mTabTextIndicator.add(tabPersonalTv);
        initFragments();
    }

    private void initFragments() {
        mTabs.add(BusinessFragment.newInstance());
        mTabs.add(CircleFragment.newInstance());
        mTabs.add(PersonalFragment.newInstance());
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void onPageSelected(int page) {
        if(page==2){
            MessageEvent messageEvent=new MessageEvent(PersonalFragment.P_REFRESH_ACCOUNT_INFO,"");
            EventBus.getDefault().post(messageEvent);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            GradientIconView iconLeft = mTabIconIndicator.get(position);
            GradientIconView iconRight = mTabIconIndicator.get(position + 1);

            GradientTextView textLeft = mTabTextIndicator.get(position);
            GradientTextView textRight = mTabTextIndicator.get(position + 1);

            iconLeft.setIconAlpha(1 - positionOffset);
            textLeft.setTextViewAlpha(1 - positionOffset);
            iconRight.setIconAlpha(positionOffset);
            textRight.setTextViewAlpha(positionOffset);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.ll_business_tab_parent, R.id.ll_circle_tab_parent, R.id.ll_personal_tab_parent})
    public void onClick(View v) {
        resetOtherTabs();
        switch (v.getId()) {
            case R.id.ll_business_tab_parent:
                mTabIconIndicator.get(0).setIconAlpha(1.0f);
                mTabTextIndicator.get(0).setTextViewAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.ll_circle_tab_parent:
                mTabIconIndicator.get(1).setIconAlpha(1.0f);
                mTabTextIndicator.get(1).setTextViewAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.ll_personal_tab_parent:
                mTabIconIndicator.get(2).setIconAlpha(1.0f);
                mTabTextIndicator.get(2).setTextViewAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        resetOtherTabIcons();
        resetOtherTabText();
    }

    /**
     * 重置其他的Tab icon
     */
    private void resetOtherTabIcons() {
        for (int i = 0; i < mTabIconIndicator.size(); i++) {
            mTabIconIndicator.get(i).setIconAlpha(0);
        }
    }

    /**
     * 重置其他的Tab text
     */
    private void resetOtherTabText() {
        for (int i = 0; i < mTabTextIndicator.size(); i++) {
            mTabTextIndicator.get(i).setTextViewAlpha(0);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESULT_BACK_TO_MAIN) {
            tabId = data.getIntExtra("tabid", 0);
            setCurrentTab(tabId);
        }else if(requestCode == PermissionGuideActivity.REQUEST_GO_TO_SETTING){
            if(resultCode==RESULT_OK){
                RomUtils.goToApplicationDetail(MainActivity.this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        setContentView(new View(this));
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption = null;
        ECDevice.unInitial();
        mTabIconIndicator.clear();
        mTabTextIndicator.clear();
        mTabs.clear();
        mAdapter=null;
        EventBus.getDefault().unregister(this);
        mCompositeSubscription.unsubscribe();
    }

}
