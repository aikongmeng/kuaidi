package com.kuaibao.skuaidi.retrofit.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.wallet.TopUpActivity;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.SKuaidiVoIPCallActivity;
import com.kuaibao.skuaidi.business.nettelephone.YTXConst;
import com.kuaibao.skuaidi.business.nettelephone.entity.UserNetCall;
import com.kuaibao.skuaidi.business.nettelephone.util.Encryption;
import com.kuaibao.skuaidi.business.nettelephone.util.NetTeleUtils;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.progressbar.CustomProgressDialog;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import cn.feng.skin.manager.base.BaseFragmentActivity;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lgg on 2016/10/14 11:24.
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

public class RxRetrofitBaseFragmentActivity extends BaseFragmentActivity {
    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    protected CompositeSubscription mCompositeSubscription;

    private CustomProgressDialog mProgressDialog;
    public static final int GoToNetCallRequest=3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())? R.color.sto_text_color:R.color.title_bg),0);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
        ButterKnife.bind(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    /**
     * 创建观察者
     */
    protected <T> Subscriber newSubscriber(final Action1<? super T> onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                dismissProgressDialog();
                KLog.i("rx","onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                if (e instanceof RetrofitUtil.APIException) {
                    RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;
                    KLog.i("okHttp","APIException:--->"+exception.toString());
                    if(exception.code==1011 ||exception.code==1103 || exception.code==5 || exception.code==6 || exception.code==401){// session失效，自动重新登录 排除第一次安装的情况
                        if(!TextUtils.isEmpty(exception.sname) && !exception.sname.contains("android/patch")){
                            BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
                                @Override
                                public void onReLoginSuccess() {
                                }
                            });
                        }else{
                            showToast(exception.msg);
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
                Log.e("rx", "RxRetrofitBaseActivity onError:--->"+String.valueOf(e.getMessage()));
                dismissProgressDialog();
            }
            @Override
            public void onNext(T t) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.call(t);
                    KLog.i("rx","RxBaseActivity onNext");
                }
            }
        };
    }

    /**
     * 显示一个Toast信息
     */
    public void showToast(String content) {
        if(content!=null)
            UtilToolkit.showToast(content);
    }
    @Override
    protected void onDestroy() {
        setContentView(new View(this));
        super.onDestroy();
        //一旦调用了 CompositeSubscription.unsubscribe()，这个CompositeSubscription对象就不可用了,
        // 如果还想使用CompositeSubscription，就必须在创建一个新的对象了。
        mCompositeSubscription.unsubscribe();
    }
    public void showProgressDialog(String msg){
        if(mProgressDialog==null){
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
        }
        if(TextUtils.isEmpty(msg)){
            msg="加载中...";
        }
        mProgressDialog.setMessage(msg);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void getMoneyAndToken(final String callType,final String callerName,final String phoneNum){
        showProgressDialog("");
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.getMoneyAndToken(buildParams(callType))
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if(throwable instanceof RetrofitUtil.APIException){
                            RetrofitUtil.APIException apiException=(RetrofitUtil.APIException)throwable;
                            if(apiException!=null && apiException.code==10001){
                                showNoMoneyDialog();
                            }
                        }
                    }
                })
                .subscribe(newSubscriber(new Action1<UserNetCall>() {
                    @Override
                    public void call(UserNetCall userNetCall) {
                        if(TextUtils.isEmpty(userNetCall.getAcc())||TextUtils.isEmpty(userNetCall.getPwd())){
                            if(!TextUtils.isEmpty(userNetCall.getToken())){
                                goVoipActivity(userNetCall,callerName,phoneNum,callType);
                            }else{
                                UtilToolkit.showToast( "服务器异常");
                            }
                        }else{
                            try {
                                String decodeStr = Encryption.desEncrypt(YTXConst.SECRET_KEY,YTXConst.IV,userNetCall.getAcc());
                                if(!TextUtils.isEmpty(decodeStr)){
                                    userNetCall.setVoip(decodeStr.length()>14?decodeStr.substring(0,14):decodeStr);
                                    goVoipActivity(userNetCall,callerName,phoneNum,callType);
                                }else{
                                    UtilToolkit.showToast( "服务器异常");
                                }
                            } catch (Exception e1) {
                                UtilToolkit.showToast( e1.getMessage());
                            }
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    protected void showNoMoneyDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("账户余额不足\n请充值后再继续操作");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("充值", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent mIntent = new Intent(getApplicationContext(), TopUpActivity.class);
                startActivity(mIntent);
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void showNoAudioPerssiomDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("麦克风无声音，可能是录音权限被禁。请到手机的设置-应用-快递员-权限管理-录音-设为允许");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                SKuaidiApplication.getInstance().exit();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void goVoipActivity(UserNetCall userNetCall,String callerName,String phoneNum,String callIsLog){
        if(!SkuaidiSpf.getAudioPermission()){
            showNoAudioPerssiomDialog();
            return;
        }
        Intent callAction = new Intent(getApplicationContext() , SKuaidiVoIPCallActivity.class);
        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_CALL_NAME , callerName);
        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_CALL_NUMBER , phoneNum);
        callAction.putExtra(ECDevice.CALLTYPE ,  ECVoIPCallManager.CallType.DIRECT);
        callAction.putExtra(SKuaidiVoIPCallActivity.EXTRA_OUTGOING_CALL , true);
        callAction.putExtra("avail_time", userNetCall.getAvail_time());
        callAction.putExtra("callIsLog",callIsLog);
        callAction.putExtra("disNum",userNetCall.getDisNum());
        if(TextUtils.isEmpty(userNetCall.getAcc())||TextUtils.isEmpty(userNetCall.getPwd())){
            callAction.putExtra("token",userNetCall.getToken());
        }else{
            callAction.putExtra("voip",userNetCall.getVoip());
            callAction.putExtra("passward",userNetCall.getPwd());
        }
        startActivityForResult(callAction,GoToNetCallRequest);
    }

    private Map<String,String> buildParams(String callType){
        Map<String,String> params=new HashMap<>();
        params.put("from_app","1");
        String tag= NetTeleUtils.getMyTelePrefer(SkuaidiSpf.getLoginUser().getPhoneNumber());
        if("0".equals(tag)){
            params.put("call_back","0");
        }else if("1".equals(tag)){
            params.put("call_back","1");
        }else if("2".equals(tag)){
            if(NetTeleUtils.isWifi(getApplicationContext())){
                params.put("call_back","0");
            }else{
                params.put("call_back","1");
            }
        }
        params.put("not_log","normalLog".equals(callType)?"0":"1");
        return params;
    }

}
