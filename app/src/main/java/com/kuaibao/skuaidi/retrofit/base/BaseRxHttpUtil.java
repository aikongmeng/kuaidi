package com.kuaibao.skuaidi.retrofit.base;

import android.text.TextUtils;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by ligg on 2016/5/10 15:25.
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
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public class BaseRxHttpUtil {
    public interface ReLoginCallBack{
        void onReLoginSuccess();
    }

    private static Subscription subscription;
    public static void reLogin(final ReLoginCallBack reLoginCallBack){//自动重新登录
        final ApiWrapper wrapper=new ApiWrapper();
        subscription = wrapper.loginV1(SkuaidiSpf.getLoginUser().getPhoneNumber(),SkuaidiSpf.getLoginUser().getPwd())
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<LoginUserInfo>() {
                    @Override
                    public void call(LoginUserInfo userInfo) {
                        SkuaidiSpf.setSessionId(TextUtils.isEmpty(userInfo.getSession_id())?"":userInfo.getSession_id());
                        saveLoginUserInfo(userInfo);
                        reLoginCallBack.onReLoginSuccess();
                        if(subscription!=null && !subscription.isUnsubscribed()){
                            subscription.unsubscribe();
                        }
                    }
                }));
    }

    public static void saveLoginUserInfo(LoginUserInfo user){
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        userInfo.setSession_id(user.getSession_id());
        userInfo.setUserName(user.getRealname());
        userInfo.setArea(user.getArea());
        userInfo.setBranch(user.getIndex_shop_name());
        userInfo.setIndexShopId(user.getIndex_shop_id());
        userInfo.setExpressNo(user.getBrand());
        userInfo.setUserId(user.getUser_id());
        SkuaidiSpf.saveLoginInfo(
                SKuaidiApplication.getContext(),
                userInfo.getSession_id(),
                userInfo.getPhoneNumber(),
                userInfo.getArea(),
                userInfo.getExpressNo(),
                userInfo.getBranch(),
                userInfo.getIndexShopId(),
                userInfo.getUserName(),
                userInfo.getUserId(),
                userInfo.getPwd(),
                true,user.getCodeId(),user.getIdImg(),user.getRealnameAuthStatus());
        SkuaidiSpf.setLastLoginName(SKuaidiApplication.getContext(),userInfo.getPhoneNumber());
        SkuaidiSpf.setIsLogin(true);
    }
    public static void changeLoginUserInfo(LoginUserInfo user){
        SkuaidiSpf.saveLoginInfo(
                SKuaidiApplication.getContext(),
                user.getSession_id(),
                user.getPhoneNumber(),
                user.getArea(),
                user.getBrand(),
                user.getIndex_shop_name(),
                user.getIndex_shop_id(),
                user.getRealname(),
                user.getUser_id(),
                user.getPassword(),
                true,user.getCodeId(),user.getIdImg(),user.getRealnameAuthStatus());
        SkuaidiSpf.setLastLoginName(SKuaidiApplication.getContext(),user.getPhoneNumber());
        SkuaidiSpf.setIsLogin(true);
    }


    /**
     * 创建观察者
     */
    public static <T> Subscriber newSubscriberUtil(final Action1<? super T> onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                KLog.i("rx","onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                if (e instanceof RetrofitUtil.APIException) {
                    RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;
                    if(exception.code==1011 ||exception.code==1103 || exception.code==5 || exception.code==6 || exception.code==401){// session失效，自动重新登录 排除第一次安装的情况
                        if(!TextUtils.isEmpty(exception.sname) && !exception.sname.contains("android/patch")){
                            BaseRxHttpUtil.reLogin(new ReLoginCallBack() {
                                @Override
                                public void onReLoginSuccess() {

                                }
                            });
                            if(isVerifySname(exception.sname)){
                                showToast("登录状态已失效,自动重新登录...");
                            }
                        }
                    }else{
                        if(!TextUtils.isEmpty(exception.sname) && !exception.sname.contains("android/patch")){
                            showToast(exception.msg);
                        }
                    }
                } else if (e instanceof SocketTimeoutException) {
                    showToast(e.getMessage());
                } else if (e instanceof ConnectException) {
                    showToast(e.getMessage());
                }else if(e.getMessage().contains("START_ARRAY")){
                    //showToast("暂无信息");
                }else{
                    if(!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("No address associated with hostname")){
                        showToast("网络连接失败,请检查网络设置");
                    }else{
                        showToast(String.valueOf(e.getMessage())+",请稍后重试");
                    }
                }
                KLog.e("rx", "BaseRxHttpUtil onError:--->"+String.valueOf(e.getMessage()));
            }
            @Override
            public void onNext(T t) {
                    onNext.call(t);
                    KLog.i("rx","BaseRxHttpUtil onNext");
            }

        };
    }

    private static boolean isVerifySname(String sname){
        if(TextUtils.isEmpty(sname))return false;
        return sname.contains(E3SysManager.SCAN_QF_VERIFY) ||
                sname.contains(E3SysManager.SCAN_COUNTERMAN_VERIFY) ||
                sname.contains(E3SysManager.SCAN_ZT_VERIFY);
    }
    public static void showToast(String content) {
        if(content!=null)
            UtilToolkit.showToast(content);
    }
}
