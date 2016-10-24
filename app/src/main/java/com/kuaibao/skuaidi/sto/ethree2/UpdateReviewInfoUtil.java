package com.kuaibao.skuaidi.sto.ethree2;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.application.bugfix.LocalPreferencesHelper;
import com.kuaibao.skuaidi.application.bugfix.SPConst;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.OkHttpFactory;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.util.Iterator;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by lgg on 2016/6/15 10:26.
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
public class UpdateReviewInfoUtil {
    private static Subscription mSubscription;
    private static boolean isSto_ZT_QF(){
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        if(TextUtils.isEmpty(userInfo.getExpressNo())){
            return false;
        }
        if ("sto".equals(userInfo.getExpressNo())) {
            return true;
        }
        if ("zt".equals(userInfo.getExpressNo())) {
            return true;
        }
        if("qf".equals(userInfo.getExpressNo())){
            return true;
        }
        return false;
    }
    public static void updateReviewInfo(final boolean toast){
        if(!isSto_ZT_QF()){
            return;
        }
        Map<String,String> params= BuildParams.buildE3Params(SKuaidiApplication.getContext(),"getinfo");
        if(E3SysManager.SCAN_COUNTERMAN_VERIFY.equals(params.get("sname"))){
            final ApiWrapper apiWrapper=new ApiWrapper();
             mSubscription=apiWrapper.getStoVerifyInfo(params.get("sname"),params)
                    .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<JSONObject>() {
                        @Override
                        public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                            String status=verifyInfo.getString("status");
                            if(!"success".equals(status)){
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast(verifyInfo.getString("desc"));
                                return;
                            }
                            com.alibaba.fastjson.JSONObject jsonResult=verifyInfo.getJSONObject("result");
                            if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                                return;
                            }
                            String retStr=jsonResult.getString("retStr");
                            if(!TextUtils.isEmpty(retStr)){//提示正在审核中信息...
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast(retStr);
                                return;
                            }
                            int verified=jsonResult.getIntValue("verified");//1 审核通过
                            if(1==verified){
                                JSONArray jsonArray=jsonResult.getJSONArray("retArr");
                                if(jsonArray!=null && jsonArray.size()>0){//更新工号信息
                                    com.alibaba.fastjson.JSONObject jsonReview=jsonArray.getJSONObject(0);
                                    jsonReview.put("isThroughAudit",1);
                                    updateCurrentReviewStatus(jsonReview.toJSONString());
                                }
                            }else {// 第一次使用，需要确认verified=0时的工号信息
                                updateCurrentReviewStatus("");
                                //UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                            }
                            if(mSubscription!=null && mSubscription.isUnsubscribed()){
                                mSubscription.unsubscribe();
                                mSubscription=null;
                            }
                        }}));
        }else if(E3SysManager.SCAN_ZT_VERIFY.equals(params.get("sname"))){
            final ApiWrapper apiWrapper=new ApiWrapper();
            mSubscription=apiWrapper.getZTVerifyInfo(params.get("sname"),params)
                    .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<JSONObject>() {
                        @Override
                        public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                            if(!verifyInfo.containsKey("code")){
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                                return;
                            }
                            int code=verifyInfo.getIntValue("code");
                            String status=verifyInfo.getString("status");
//                            long servTime=verifyInfo.getLongValue("servTime");
//                            SkuaidiSpf.setZTServerTime(servTime);
                            if(code==0 && "success".equals(status)){//跳转到E3主界面
                                if(OkHttpFactory.JSON_TYPE.JSON_TYPE_OBJECT==OkHttpFactory.getJSONType(verifyInfo.get("result").toString())){
                                    com.alibaba.fastjson.JSONObject jsonResult=verifyInfo.getJSONObject("result");
                                    if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
                                        updateCurrentReviewStatus("");
                                        if(toast)
                                        UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                                        return;
                                    }
                                    int verified=jsonResult.getIntValue("verified");// 1 审核通过，账号没问题；0 审核未通过
                                    if(verified==1){
                                        JSONArray jsonArray=jsonResult.getJSONArray("retArr");
                                        if(jsonArray==null || jsonArray.size()==0){//没有对应的工号信息,发生错误了
                                            updateCurrentReviewStatus("");
                                            if(toast)
                                            UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                                            return;
                                        }
                                        com.alibaba.fastjson.JSONObject jsonReviewInfo=jsonArray.getJSONObject(0);
                                        jsonReviewInfo.put("isThroughAudit",1);
                                        updateCurrentReviewStatus(jsonReviewInfo.toJSONString());
                                    }else{
                                        updateCurrentReviewStatus("");
                                        //UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                                    }
                                }
                            }else{
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast(verifyInfo.getString("desc"));
                            }
                            if(mSubscription!=null && mSubscription.isUnsubscribed()){
                                mSubscription.unsubscribe();
                                mSubscription=null;
                            }
                        }}));
        }else if(E3SysManager.SCAN_QF_VERIFY.equals(params.get("sname"))){
            final ApiWrapper apiWrapper=new ApiWrapper();
            mSubscription=apiWrapper.getQFVerifyInfo(params.get("sname"),params)
                    .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<JSONObject>() {
                        @Override
                        public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                            String status=verifyInfo.getString("status");
                            if(!"success".equals(status)){
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast(verifyInfo.getString("desc"));
                                return;
                            }
                            com.alibaba.fastjson.JSONObject jsonResult=verifyInfo.getJSONObject("result");
                            if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                                return;
                            }
                            String retStr=jsonResult.getString("retStr");
                            if(!TextUtils.isEmpty(retStr)){//提示正在审核中信息...
                                updateCurrentReviewStatus("");
                                if(toast)
                                UtilToolkit.showToast(retStr);
                                return;
                            }
                            int verified=jsonResult.getIntValue("verified");//1 审核通过
                            if(1==verified){
                                JSONObject jsonRet=jsonResult.getJSONObject("retArr");
                                Iterator<String> keys=jsonRet.keySet().iterator();
                                int index=0;
                                if(keys!=null){
                                    while(keys.hasNext()){
                                        if(index==1){
                                            break;
                                        }
                                        String key=keys.next();
                                        JSONObject jsonReviewInfo=jsonRet.getJSONObject(key);
                                        jsonReviewInfo.put("isThroughAudit",1);
                                        updateCurrentReviewStatus(jsonReviewInfo.toJSONString());
                                        index++;
                                    }
                                }
                            }else {// 第一次使用，需要确认verified=0时的工号信息
                                updateCurrentReviewStatus("");
                                //UtilToolkit.showToast("快递员工号信息异常,请进入巴枪界面核对信息");
                            }
                            if(mSubscription!=null && mSubscription.isUnsubscribed()){
                                mSubscription.unsubscribe();
                                mSubscription=null;
                            }
                        }}));
        }
    }
    public static void updateCurrentReviewStatus(String info){
        LocalPreferencesHelper mLocalPreferencesHelper = new LocalPreferencesHelper(SKuaidiApplication.getContext(), SPConst.E3_NAME);
        mLocalPreferencesHelper.saveOrUpdate(SPConst.getVerifiedInfoKey(),info);
    }

    public static String getCurrentReviewStatus(){
        LocalPreferencesHelper mLocalPreferencesHelper = new LocalPreferencesHelper(SKuaidiApplication.getContext(), SPConst.E3_NAME);
        return mLocalPreferencesHelper.getString(SPConst.getVerifiedInfoKey());
    }
}
