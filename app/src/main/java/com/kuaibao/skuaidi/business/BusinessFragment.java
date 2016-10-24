package com.kuaibao.skuaidi.business;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CloudVoiceRecordActivity;
import com.kuaibao.skuaidi.activity.FindExpressActivity;
import com.kuaibao.skuaidi.activity.LiuyanCenterActivity;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.activity.MoreChannelActivity;
import com.kuaibao.skuaidi.activity.expressShop.MyShopActivity;
import com.kuaibao.skuaidi.activity.OrderCenterActivity;
import com.kuaibao.skuaidi.activity.OverareaQueryActivity;
import com.kuaibao.skuaidi.activity.SkuaidiCabinetActivity;
import com.kuaibao.skuaidi.activity.adapter.DragGridViewAdapter;
import com.kuaibao.skuaidi.activity.make.realname.RealNameInputActivity;
import com.kuaibao.skuaidi.activity.make.realname.RealNameInputLocalActivity;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.smsrecord.SmsRecordActivity;
import com.kuaibao.skuaidi.application.InitUtil;
import com.kuaibao.skuaidi.business.entity.ResponseReadStatus;
import com.kuaibao.skuaidi.business.entity.ResponseZTRules;
import com.kuaibao.skuaidi.business.nettelephone.NetTelePhoneActivity;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.common.layout.SkuaidiRelativeLayout;
import com.kuaibao.skuaidi.common.view.DragGridView.DragGrid;
import com.kuaibao.skuaidi.customer.MyCustomManageActivity;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dispatch.activity.DispatchActivity;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.main.widget.IntroView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.OkHttpFactory;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.CurrentE3VerifyInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragment;
import com.kuaibao.skuaidi.service.AlarmReceiver;
import com.kuaibao.skuaidi.sto.ethree2.BuildParams;
import com.kuaibao.skuaidi.sto.ethree2.E3StoChooseAccountActivity;
import com.kuaibao.skuaidi.sto.ethree2.E3ZTAccountLoginActivity;
import com.kuaibao.skuaidi.sto.ethree2.E3ZTRegActivity;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysMainActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by lgg on 2016/8/10 11:34.
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
public class BusinessFragment extends RxRetrofitBaseFragment implements DragGrid.MyItemClickListener {

    @BindView(R.id.gv_menu)
    DragGrid gv_menu;// 菜单
    @BindView(R.id.rl_e3_entrance)
    SkuaidiRelativeLayout rl_e3_entrance;// 巴枪扫描按钮
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.intro)
    IntroView mIntroView;
    @BindView(R.id.ll_business_parent)
    LinearLayout ll_business_parent;
    private UserInfo mUserInfo;
    private DragGridViewAdapter businessMenuAdapter;
    private HashMap<String, Integer> function_redCircle_map = new HashMap();//功能名与小红点的键值对
    private Intent intent;
    private boolean isPrepared;

    private enum function {
        短信记录, 云呼记录, 查快递, 留言, 客户管理, 订单, 实名寄递, 收款, 通话, 超派查询, 派件, 我的店铺, 快递柜, 更多
    }

    public static ArrayMap<String, Integer> idNameMap = new ArrayMap<>();

    static {
        idNameMap.put("短信记录", R.drawable.business_send_message_icon);
        idNameMap.put("云呼记录", R.drawable.business_send_cloud_icon);
        idNameMap.put("查快递", R.drawable.business_picup_query_icon);
        idNameMap.put("留言", R.drawable.business_leave_amessage_icon);
        idNameMap.put("广告位", R.drawable.business_manage_customer);
        idNameMap.put("客户管理", R.drawable.business_manage_customer);
        idNameMap.put("派件", R.drawable.icon_business_dispatch);
        idNameMap.put("收款", R.drawable.business_collect_icon);
        idNameMap.put("通话", R.drawable.business_call_recoding_icon);
        idNameMap.put("超派查询", R.drawable.business_super_send_query_icon);
        idNameMap.put("订单", R.drawable.business_order_icon);
        idNameMap.put("实名寄递", R.drawable.business_autonym_icon);
        idNameMap.put("我的店铺", R.drawable.business_express_shop);
        idNameMap.put("快递柜", R.drawable.jb_express_icon);
        idNameMap.put("更多", R.drawable.icon_home_more);
    }

    public static BusinessFragment newInstance() {
        return new BusinessFragment();
    }

    public BusinessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        EventBus.getDefault().register(this);
    }

    public void initData() {
        mUserInfo = SkuaidiSpf.getLoginUser();
    }

    @Override
    public void lazyLoad() {
        if (isPrepared && isVisible) {
            //做加载数据的网络操作
            isPrepared = false;
            if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) {//只中通获取单号规则
                getZTExpressNOregulation();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        gv_menu.postDelayed(new Runnable() {
            @Override
            public void run() {
                String courierNo = E3SysManager.getCourierNO();
                int toUploadCount = E3OrderDAO.getOrderCount(mUserInfo.getExpressNo(), courierNo == null ? "" : courierNo);
                if (toUploadCount > 0) {
                    AlarmReceiver.count = toUploadCount;
                    tv_count.setVisibility(TextView.VISIBLE);
                    tv_count.setText("" + toUploadCount);
                } else {
                    tv_count.setVisibility(TextView.GONE);
                }
            }
        }, 500);
    }

    @Override
    public void initViews() {
        //做初始化View的操作
        isPrepared = true;
        setMenu();
        gv_menu.post(new Runnable() {
            @Override
            public void run() {
                if ("sto".equals(mUserInfo.getExpressNo()) || "qf".equals(mUserInfo.getExpressNo()) || "zt".equals(mUserInfo.getExpressNo())) {
                    rl_e3_entrance.setVisibility(View.VISIBLE);
                } else {
                    rl_e3_entrance.setVisibility(View.GONE);
                }
                showGestureGuide();
            }
        });
    }

    private PopupWindow popupWindow;

    private void showGestureGuide() {
        String guideVersion = SkuaidiSpf.getGestureGuideVersion();
        if (TextUtils.isEmpty(guideVersion) || !guideVersion.equals(InitUtil.GUIDE_VERION)) {
            if (mIntroView != null) mIntroView.setVisibility(View.VISIBLE);
            final BubbleLayout bubbleLayout = (BubbleLayout) getActivity().getLayoutInflater().inflate(R.layout.layout_sample_popup, null);
            if (E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
                bubbleLayout.setBubbleColor(ContextCompat.getColor(getContext(), R.color.sto_text_color));
            }
            popupWindow = BubblePopupHelper.create(getActivity(), bubbleLayout);
            bubbleLayout.setArrowDirection(ArrowDirection.BOTTOM);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mIntroView != null && mIntroView.getVisibility() == View.VISIBLE) {
                        mIntroView.setVisibility(View.GONE);
                        ll_business_parent.removeView(mIntroView);
                    }
                    mIntroView = null;
                    SkuaidiSpf.setGestureGuideVersion(InitUtil.GUIDE_VERION);
                    popupWindow = null;
                }
            });
            if (mIntroView != null)
                popupWindow.showAtLocation(mIntroView, Gravity.NO_GRAVITY, AdUrlBuildUtil.getScreenWith(getContext()) / 2 - 100, AdUrlBuildUtil.getScreenHeight(getContext()) - DisplayUtil.dip2px(150));
        } else {
            if (mIntroView != null) {
                ll_business_parent.removeView(mIntroView);
                mIntroView = null;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.type) {
            case MainActivity.NEW_NOTICE_COMING:
                showNotice();
                break;
        }
    }

    private void showNotice() {
        ResponseReadStatus responseReadStatus = (ResponseReadStatus) ACache.get(getContext()).getAsObject("ResponseReadStatus");
        if (responseReadStatus != null) {
            function_redCircle_map.clear();
            function_redCircle_map.put("短信记录", responseReadStatus.getSms());
            function_redCircle_map.put("云呼记录", responseReadStatus.getIvr());
            function_redCircle_map.put("订单", responseReadStatus.getOrder());
            function_redCircle_map.put("留言", responseReadStatus.getLiuyan());
            function_redCircle_map.put("派件", responseReadStatus.getDelivery());
            businessMenuAdapter.notifyRedCircleChanged(function_redCircle_map);
        }
    }


    private void getZTExpressNOregulation() {
        final ApiWrapper apiWrapper = new ApiWrapper();
        Subscription subscription = apiWrapper.getZTExpressNOregulation().subscribe(newSubscriber(new Action1<ResponseZTRules>() {
            @Override
            public void call(ResponseZTRules result) {
                if (result != null) {
                    if (!TextUtils.isEmpty(result.getZtRules())) {
                        SkuaidiSpf.saveZTExpressNOregulation(result.getZtRules());
                    }
                    if (!TextUtils.isEmpty(result.getZtRulesOrder())) {
                        SkuaidiSpf.saveZTOrderExpressNOreg(result.getZtRulesOrder());
                    }
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    // 设置9宫格菜单
    private void setMenu() {
        List<String> channelList;
        if (Utility.getVersionCode() >= 63 && !SkuaidiSpf.hasCelarItems(mUserInfo.getPhoneNumber(), Utility.getVersionCode())) {
            SkuaidiSpf.setUserBusiinessItems(mUserInfo.getPhoneNumber(), null);
            SkuaidiSpf.clearItems(mUserInfo.getPhoneNumber(), Utility.getVersionCode(), true);
        }
        if (Utility.getVersionCode() >= 63 && !SkuaidiSpf.hasCelarItemsMore(mUserInfo.getPhoneNumber(), Utility.getVersionCode())) {
            SkuaidiSpf.setUserMoreItems(mUserInfo.getPhoneNumber(), null);
            SkuaidiSpf.clearItemsMore(mUserInfo.getPhoneNumber(), Utility.getVersionCode(), true);
            SkuaidiSpf.setUserMoreItems(mUserInfo.getPhoneNumber(), new ArrayList<>(Arrays.asList("超派查询")));
        }
        List<String> savedList = SkuaidiSpf.getUserBusinessItems(mUserInfo.getPhoneNumber());
        if (savedList != null && savedList.size() != 0) {
            channelList = savedList;
        } else {
            channelList = new ArrayList<>();
            if (canShowFunction()) {
                if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo()) || E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
                    String[] mDescription = {"短信记录", "云呼记录", "查快递", "留言", "广告位", "客户管理", "派件", "收款", "通话", "订单", "实名寄递", "我的店铺", "快递柜"};
                    channelList.addAll(Arrays.asList(mDescription));
                } else {
                    String[] mDescription = {"短信记录", "云呼记录", "查快递", "留言", "广告位", "客户管理", "订单", "实名寄递", "收款", "通话", "我的店铺", "快递柜"};
                    channelList.addAll(Arrays.asList(mDescription));
                }
            } else {
                if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo()) || E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
                    String[] mDescription = {"短信记录", "云呼记录", "收款", "留言", "广告位", "客户管理", "派件", "订单", "实名寄递", "我的店铺", "通话", "快递柜"};
                    channelList.addAll(Arrays.asList(mDescription));
                } else {
                    String[] mDescription = {"短信记录", "云呼记录", "收款", "留言", "广告位", "客户管理", "订单", "实名寄递", "我的店铺", "通话", "快递柜"};
                    channelList.addAll(Arrays.asList(mDescription));
                }
            }
            channelList.add("更多");
        }
        SkuaidiSpf.setUserBusiinessItems(mUserInfo.getPhoneNumber(), channelList);
        businessMenuAdapter = new DragGridViewAdapter(getActivity(), channelList, true);
        gv_menu.containMoreItem(true);
        gv_menu.setAdapter(businessMenuAdapter);
        gv_menu.setItemClickListener(this);
        businessMenuAdapter.setOnRemoveListener(new DragGridViewAdapter.RemoveListener() {
            @Override
            public void onRemove(int position) {
                String channelItem = businessMenuAdapter.getChannelList().get(position);
                businessMenuAdapter.remove(position);
                businessMenuAdapter.setReset(true);
                SkuaidiSpf.setUserBusiinessItems(mUserInfo.getPhoneNumber(), businessMenuAdapter.getChannelList());
                List<String> moreChannelItems = SkuaidiSpf.getUserMoreItems(mUserInfo.getPhoneNumber());
                if (moreChannelItems == null) moreChannelItems = new ArrayList<>();
                moreChannelItems.add(channelItem);
                SkuaidiSpf.setUserMoreItems(mUserInfo.getPhoneNumber(), moreChannelItems);
                UtilToolkit.showToast("已移到【更多】");
            }
        });
    }

    @OnClick({R.id.rl_qunfa_duanxin, R.id.rl_business_btn_yunhu, R.id.rl_e3_entrance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_qunfa_duanxin:
                UMShareManager.onEvent(getContext(), "business_item_groupMessage", "business", "群发短信");
                intent = new Intent(getContext(), SendMSGActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_business_btn_yunhu:
                UMShareManager.onEvent(getContext(), "business_item_btn_yunhu", "business", "业务：云呼");
                intent = new Intent(getContext(), SendYunHuActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_e3_entrance:
                getE3VerifyInfo(false);
                break;
        }
    }

    private void getE3VerifyInfo(boolean isGoDispatch) {
        showProgressDialog("巴枪账号更新...");
        Map<String, String> params = BuildParams.buildE3Params(getContext(), "getinfo");
        if (E3SysManager.SCAN_COUNTERMAN_VERIFY.equals(params.get("sname"))) {
            handlerSTOVerifiedInfo(params,isGoDispatch);
        } else if (E3SysManager.SCAN_ZT_VERIFY.equals(params.get("sname"))) {
            handlerZTVerifiedInfo(params,isGoDispatch);
        } else if (E3SysManager.SCAN_QF_VERIFY.equals(params.get("sname"))) {//和申通流程一致
            handlerQFVerifiedInfo(params);
        }
    }

    private void handlerSTOVerifiedInfo(Map<String, String> params, final boolean isGotoDispatch) {
        final ApiWrapper apiWrapper = new ApiWrapper();
        Subscription mSubscription = apiWrapper.getStoVerifyInfo(params.get("sname"), params)
                .subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                        String status = verifyInfo.getString("status");
                        if (!"success".equals(status)) {
                            showFailVerifiedDialog("温馨提示", verifyInfo.getString("desc"), false);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject jsonResult = verifyInfo.getJSONObject("result");
                        if (jsonResult == null || TextUtils.isEmpty(jsonResult.toJSONString())) {
                            UtilToolkit.showToast("服务器繁忙,请稍后重试");
                            return;
                        }
                        String retStr = jsonResult.getString("retStr");
                        if (!TextUtils.isEmpty(retStr)) {//提示正在审核中信息...
                            showFailVerifiedDialog("温馨提示", retStr, false);
                            return;
                        }
                        int verified = jsonResult.getIntValue("verified");//1 审核通过
                        if (1 == verified) {
                            JSONArray jsonArray = jsonResult.getJSONArray("retArr");
                            if (jsonArray != null && jsonArray.size() > 0) {//更新工号信息
                                com.alibaba.fastjson.JSONObject jsonReview = jsonArray.getJSONObject(0);
                                jsonReview.put("isThroughAudit", 1);
                                UpdateReviewInfoUtil.updateCurrentReviewStatus(jsonReview.toJSONString());
                            }
                            if(isGotoDispatch){
                                intent = new Intent(getContext(), DispatchActivity.class);
                                startActivity(intent);
                                if (E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
                                    UMShareManager.onEvent(getContext(), "business_item_dispatch_sto", "business", "申通派件");
                                } else if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                                    UMShareManager.onEvent(getContext(), "business_item_dispatch_zt", "business", "中通派件");
                                }
                            }else{
                                //跳转到E3主界面
                                Intent intent = new Intent(getContext(), EThreeSysMainActivity.class);
                                startActivity(intent);
                            }
                        } else {// 第一次使用，需要确认verified=0时的工号信息
                            JSONArray jsonArray = jsonResult.getJSONArray("retArr");
                            if (jsonArray != null) {
                                ArrayList<CurrentE3VerifyInfo> listData = new ArrayList<>();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    CurrentE3VerifyInfo currentE3VerifyInfo = JSON.parseObject(jsonArray.getJSONObject(i).toJSONString(), CurrentE3VerifyInfo.class);
                                    currentE3VerifyInfo.setPosition(i);
                                    listData.add(currentE3VerifyInfo);
                                }
                                Intent intent = new Intent(getContext(), E3StoChooseAccountActivity.class);
                                intent.putExtra(E3StoChooseAccountActivity.FROM_WHERE, "businessActivity");
                                intent.putExtra(E3StoChooseAccountActivity.TITLT_NAME, "快递员巴枪账号确认");
                                intent.putExtra(E3StoChooseAccountActivity.BRAND_TYPE_NAME, "sto");
                                intent.putParcelableArrayListExtra(E3StoChooseAccountActivity.ACCOUNT_INFO_NAME, listData);
                                startActivity(intent);
                            }
                        }
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }

    private void handlerZTVerifiedInfo(Map<String, String> params, final boolean isGotoDispatch) {
        final ApiWrapper apiWrapper = new ApiWrapper();
        Subscription mSubscription = apiWrapper.getZTVerifyInfo(params.get("sname"), params)
                .subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                        if (!verifyInfo.containsKey("code")) {
                            UtilToolkit.showToast("服务器繁忙,请稍后重试");
                            return;
                        }
                        int code = verifyInfo.getIntValue("code");
                        String status = verifyInfo.getString("status");
                        long servTime = verifyInfo.getLongValue("servTime");
                        SkuaidiSpf.setZTServerTime(servTime);
                        if (code == 0 && "success".equals(status)) {//跳转到E3主界面
                            com.alibaba.fastjson.JSONObject jsonResult = verifyInfo.getJSONObject("result");
                            if (jsonResult == null || TextUtils.isEmpty(jsonResult.toJSONString())) {
                                UtilToolkit.showToast("服务器繁忙,请稍后重试");
                                return;
                            }
                            int verified = jsonResult.getIntValue("verified");// 1 审核通过，账号没问题；0 审核未通过
                            if (verified == 1) {
                                JSONArray jsonArray = jsonResult.getJSONArray("retArr");
                                if (jsonArray == null || jsonArray.size() == 0) {//没有对应的工号信息,发生错误了
                                    UtilToolkit.showToast("服务器繁忙,请稍后重试");
                                    return;
                                }
                                com.alibaba.fastjson.JSONObject jsonReviewInfo = jsonArray.getJSONObject(0);
                                jsonReviewInfo.put("isThroughAudit", 1);
                                String localReviewInfo = UpdateReviewInfoUtil.getCurrentReviewStatus();
                                if (TextUtils.isEmpty(localReviewInfo)) {//本地没有记录要登录一次中天系统
                                    Intent intent = new Intent(getContext(), E3ZTAccountLoginActivity.class);
                                    String counterman_code = jsonReviewInfo.getString("counterman_code");
                                    intent.putExtra(E3ZTAccountLoginActivity.FROM_WHERE_NAME, "businessActivity");
                                    intent.putExtra(E3ZTAccountLoginActivity.BRANCH_NO_NAME, null == counterman_code ? "" : counterman_code);
                                    startActivity(intent);
                                } else {
                                    if(isGotoDispatch){
                                        intent = new Intent(getContext(), DispatchActivity.class);
                                        startActivity(intent);
                                        if (E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
                                            UMShareManager.onEvent(getContext(), "business_item_dispatch_sto", "business", "申通派件");
                                        } else if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                                            UMShareManager.onEvent(getContext(), "business_item_dispatch_zt", "business", "中通派件");
                                        }
                                    }else{
                                        Intent intent = new Intent(getContext(), EThreeSysMainActivity.class);
                                        startActivity(intent);
                                    }

                                }
                                UpdateReviewInfoUtil.updateCurrentReviewStatus(jsonReviewInfo.toJSONString());
                            } else {
                                showFailVerifiedDialog("快递员巴枪账号更新", verifyInfo.getString("desc"), true);
                            }
                        } else if (code == 1101) {//第一次使用，注册把枪
                            String desc = verifyInfo.getString("desc");
                            String branch_code = "";
                            String shop_name = "";
                            if (OkHttpFactory.JSON_TYPE.JSON_TYPE_OBJECT == OkHttpFactory.getJSONType(verifyInfo.get("recommendation").toString())) {
                                com.alibaba.fastjson.JSONObject jsonObject = verifyInfo.getJSONObject("recommendation");
                                if (jsonObject != null) {
                                    branch_code = jsonObject.getString("branch_code");
                                    shop_name = jsonObject.getString("shop_name");
                                }
                            }
                            Intent intent = new Intent(getContext(), E3ZTRegActivity.class);
                            intent.putExtra(E3ZTRegActivity.REG_TITLE, TextUtils.isEmpty(desc) ? "用户注册" : desc);
                            intent.putExtra(E3ZTRegActivity.BRANCH_NO_NAME, branch_code);
                            intent.putExtra(E3ZTRegActivity.SHOP_NAME, shop_name);
                            intent.putExtra(E3ZTRegActivity.FROM_WHERE, "businessActivity");
                            startActivity(intent);
                        } else if (code == 1102) {//请输入工号,密码登录
                            String desc = verifyInfo.getString("desc");
                            String branch_no = verifyInfo.getString("branch_no");
                            Intent intent = new Intent(getContext(), E3ZTAccountLoginActivity.class);
                            intent.putExtra(E3ZTAccountLoginActivity.BRANCH_NO_NAME, null == branch_no ? "" : branch_no);
                            intent.putExtra(E3ZTAccountLoginActivity.FROM_WHERE_NAME, "businessActivity");
                            startActivity(intent);
                            if (!TextUtils.isEmpty(desc)) {
                                UtilToolkit.showToast(desc);
                            }
                        } else if (code == 1105 || code == 1106) {//服务器的一些错误信息展示
                            showFailVerifiedDialog("温馨提示", verifyInfo.getString("desc"), true);//,R.drawable.icon_xiaolian);
                        } else {
                            showFailVerifiedDialog("温馨提示", verifyInfo.getString("desc"), false);//,R.drawable.icon_auditing);
                        }
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }

    private void handlerQFVerifiedInfo(Map<String, String> params) {
        final ApiWrapper apiWrapper = new ApiWrapper();
        Subscription mSubscription = apiWrapper.getQFVerifyInfo(params.get("sname"), params)
                .subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                        String status = verifyInfo.getString("status");
                        if (!"success".equals(status)) {
                            showFailVerifiedDialog("温馨提示", verifyInfo.getString("desc"), false);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject jsonResult = verifyInfo.getJSONObject("result");
                        if (jsonResult == null || TextUtils.isEmpty(jsonResult.toJSONString())) {
                            UtilToolkit.showToast("服务器繁忙,请稍后重试");
                            return;
                        }
                        String retStr = jsonResult.getString("retStr");
                        if (!TextUtils.isEmpty(retStr)) {//提示正在审核中信息...
                            showFailVerifiedDialog("温馨提示", retStr, false);
                            return;
                        }
                        int verified = jsonResult.getIntValue("verified");//1 审核通过
                        if (1 == verified) {
                            com.alibaba.fastjson.JSONObject jsonRet = jsonResult.getJSONObject("retArr");
                            Iterator<String> keys = jsonRet.keySet().iterator();
                            if (keys != null) {
                                int index = 0;
                                while (keys.hasNext()) {
                                    if (index == 1) {
                                        break;
                                    }
                                    String key = keys.next();
                                    com.alibaba.fastjson.JSONObject jsonReviewInfo = jsonRet.getJSONObject(key);
                                    jsonReviewInfo.put("isThroughAudit", 1);
                                    UpdateReviewInfoUtil.updateCurrentReviewStatus(jsonReviewInfo.toJSONString());
                                    index++;
                                }
                            }
                            //跳转到E3主界面
                            Intent intent = new Intent(getContext(), EThreeSysMainActivity.class);
                            startActivity(intent);
                        } else {// 第一次使用，需要确认verified=0时的工号信息
                            com.alibaba.fastjson.JSONObject jsonRet = jsonResult.getJSONObject("retArr");
                            if (jsonRet != null) {
                                ArrayList<CurrentE3VerifyInfo> listData = new ArrayList<>();
                                Iterator<String> keys = jsonRet.keySet().iterator();
                                if (keys != null) {
                                    int index = 0;
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        com.alibaba.fastjson.JSONObject jsonReviewInfo = jsonRet.getJSONObject(key);
                                        jsonReviewInfo.put("isThroughAudit", 1);
                                        CurrentE3VerifyInfo currentE3VerifyInfo = JSON.parseObject(jsonReviewInfo.toJSONString(), CurrentE3VerifyInfo.class);
                                        currentE3VerifyInfo.setPosition(index);
                                        listData.add(currentE3VerifyInfo);
                                        index++;
                                    }
                                }
                                Intent intent = new Intent(getContext(), E3StoChooseAccountActivity.class);
                                intent.putExtra(E3StoChooseAccountActivity.FROM_WHERE, "businessActivity");
                                intent.putExtra(E3StoChooseAccountActivity.TITLT_NAME, "快递员巴枪账号确认");
                                intent.putExtra(E3StoChooseAccountActivity.BRAND_TYPE_NAME, "qf");
                                intent.putParcelableArrayListExtra(E3StoChooseAccountActivity.ACCOUNT_INFO_NAME, listData);
                                startActivity(intent);
                            }
                        }
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }

    private void showFailVerifiedDialog(String title, String desc, boolean show) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(desc);
        builder.setTitle(title);
        if (show) {
            builder.setVerifiedFail(show);
            builder.setDialogChildViewClick(new CustomDialog.Builder.DialogChildViewClick() {
                @Override
                public void onClickReVerified() {
                    Intent intent = new Intent(getContext(), E3ZTRegActivity.class);
                    intent.putExtra(E3ZTRegActivity.REG_TITLE, "用户注册");
                    intent.putExtra(E3ZTRegActivity.BRANCH_NO_NAME, "");
                    intent.putExtra(E3ZTRegActivity.SHOP_NAME, "");
                    intent.putExtra(E3ZTRegActivity.FROM_WHERE, "businessActivity");
                    startActivity(intent);
                }
            });
        }
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void OnItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (function.valueOf(businessMenuAdapter.getChannelList().get(position))) {
            case 短信记录:
                UMShareManager.onEvent(getContext(), "business_item_piePiece", "business", "派件短信");
                intent = new Intent(getContext(), SmsRecordActivity.class);
                startActivity(intent);
                break;
            case 云呼记录:
                UMShareManager.onEvent(getContext(), "business_item_cloudVoice", "business", "云呼记录");
                intent = new Intent(getContext(), CloudVoiceRecordActivity.class);
                startActivity(intent);
                break;
            case 查快递:
                UMShareManager.onEvent(getContext(), "business_item_lanPiece", "business", "揽件查询");
                intent = new Intent(getContext(), FindExpressActivity.class);
                startActivity(intent);
                break;
            case 收款:
                UMShareManager.onEvent(getContext(), "business_item_gathering", "business", "业务界面收款");
                intent = new Intent(getContext(), MakeCollectionsActivity.class);
                startActivity(intent);
                break;
            case 留言:
                UMShareManager.onEvent(getContext(), "business_item_Message", "business", "业务留言");
                intent = new Intent(getContext(), LiuyanCenterActivity.class);
                LiuyanCenterActivity.fromWhere = 1;
                intent.putExtra("type", 1);
                intent.putExtra("FlagIsRun", true);
                startActivity(intent);
                break;
            case 客户管理:
                UMShareManager.onEvent(getContext(), "callLog_customer_manager", "callLog", "客户管理");
                intent = new Intent(getContext(), MyCustomManageActivity.class);
                startActivity(intent);
                break;
            case 订单:
                UMShareManager.onEvent(getContext(), "business_item_order", "business", "业务订单");
                intent = new Intent(getContext(), OrderCenterActivity.class);
                OrderCenterActivity.fromWhere = 1;
                startActivity(intent);
                break;
            case 实名寄递:
                if (!TextUtils.isEmpty(mUserInfo.getArea()) && mUserInfo.getArea().contains("浙江")) {// 浙江地区
                    if (!TextUtils.isEmpty(mUserInfo.getExpressNo()) && ("sto".equals(mUserInfo.getExpressNo()) || "zt".equals(mUserInfo.getExpressNo()))) {// 中通|申通【使用浙江地区H5网页】
                        intent = new Intent(getContext(), RealNameInputActivity.class);
                        startActivity(intent);
                    } else {// 非中通|申通【使用原生实名登记】
                        originalAuth();
                    }
                } else {// 非浙江地区
                    if (!TextUtils.isEmpty(mUserInfo.getExpressNo()) && "sto".equals(mUserInfo.getExpressNo()) || "zt".equals(mUserInfo.getExpressNo())) {// 申通【使用全国H5网页】
                        loadWebCommon("http://m.kuaidihelp.com/realname/senderInfo?mobile=" + mUserInfo.getPhoneNumber());
                    } else {// 非申通【使用原生实名登记】
                        originalAuth();
                    }
                }
                break;
            case 我的店铺:
                UMShareManager.onEvent(getContext(), "business_item_shop", "business", "更多模块：我的快递店铺");
                intent = new Intent(getContext(), MyShopActivity.class);
                startActivity(intent);
                break;
            case 通话:
                UMShareManager.onEvent(getContext(), "business_item_customer", "business", "通话");
                intent = new Intent(getContext(), NetTelePhoneActivity.class);
                startActivity(intent);
                break;
            case 超派查询:
                UMShareManager.onEvent(getContext(), "business_item_superSends", "business", "超派查询");
                intent = new Intent(getContext(), OverareaQueryActivity.class);
                startActivity(intent);
                break;

            case 派件:
                CurrentE3VerifyInfo currentE3VerifyInfo = E3SysManager.getReviewInfoNew();
                if (currentE3VerifyInfo != null&&!TextUtils.isEmpty(currentE3VerifyInfo.getCounterman_code())) {
                    intent = new Intent(getContext(), DispatchActivity.class);
                    startActivity(intent);
                    if (E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
                        UMShareManager.onEvent(getContext(), "business_item_dispatch_sto", "business", "申通派件");
                    } else if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) {
                        UMShareManager.onEvent(getContext(), "business_item_dispatch_zt", "business", "中通派件");
                    }
                } else {
                    getE3VerifyInfo(true);
                }
                break;
            case 快递柜:
                UMShareManager.onEvent(getContext(), "business_item_cabinet", "business", "快递柜");
                intent = new Intent(getContext(), SkuaidiCabinetActivity.class);
                startActivity(intent);
                break;
            case 更多:
                List<String> moreChannelItems = SkuaidiSpf.getUserMoreItems(mUserInfo.getPhoneNumber());
                if (moreChannelItems == null || moreChannelItems.size() == 0) return;
                intent = new Intent(getContext(), MoreChannelActivity.class);
                intent.putExtra("read_map", function_redCircle_map);
                startActivityForResult(intent, 100);
                break;
        }
    }

    private void originalAuth() {
        intent = new Intent(getContext(), RealNameInputLocalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        if (requestCode == 100 && resultCode == 101) {
            List<String> list = SkuaidiSpf.getUserBusinessItems(mUserInfo.getPhoneNumber());
            if(list!=null){
                businessMenuAdapter.getChannelList().clear();
                businessMenuAdapter.getChannelList().addAll(list);
                businessMenuAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * 判断当前用户是否可以显示查快递和超派查询功能
     **/
    private boolean canShowFunction() {
        String[] showFuction = getResources().getStringArray(R.array.show_function);
        for (int i = 0; i < showFuction.length; i++) {
            if (showFuction[i].equals(mUserInfo.getExpressNo())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_business;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


