package com.kuaibao.skuaidi.retrofit.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuaibao.skuaidi.activity.CommonWebViewActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.progressbar.CustomProgressDialog;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import cn.feng.skin.manager.base.BaseFragment;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lgg on 2016/8/15 15:06.
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
public abstract class RxRetrofitBaseFragment extends BaseFragment{
    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    protected CompositeSubscription mCompositeSubscription;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //KLog.i("kb","onCreateView");
            View view = inflater.inflate(getContentView(), container, false);
            ButterKnife.bind(this,view);
            initViews();// 控件初始化
        return view;
    }

    protected boolean isVisible;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    protected void onVisible(){
        lazyLoad();
    }
    protected abstract void lazyLoad();
    protected void onInvisible(){}

    public abstract int getContentView();
    public abstract void initViews();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getUserVisibleHint())lazyLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 创建观察者
     */
    protected <T> Subscriber newSubscriber(final Action1<? super T> onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                dismissProgressDialog();
                //KLog.i("rx","onCompleted");
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
                    //KLog.i("rx","RxBaseActivity onNext");
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

    private CustomProgressDialog mProgressDialog;

    public void showProgressDialog(String msg){
        if(mProgressDialog==null){
            mProgressDialog = new CustomProgressDialog(getActivity());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }
    protected void loadWeb(String url, String title) {
        Intent intent = new Intent(getContext(), WebLoadView.class);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(url);
        parameters.add(title);
        intent.putStringArrayListExtra("parameters", parameters);
        startActivity(intent);
    }

    protected void loadWebCommon(String url){
        Intent intent = new Intent(getContext(), CommonWebViewActivity.class);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(url);
        intent.putStringArrayListExtra("parameter",parameters);
        startActivity(intent);
    }

    protected void onOpenShareEvent(String title,String shareText,String targetUrl,int drawableId){
        Map<String, String> shareTexts = new HashMap<>();
        shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText+targetUrl);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText+targetUrl);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText+targetUrl);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText+targetUrl);
        UMShareManager.openShare(getActivity(), title, shareTexts, targetUrl, drawableId);
    }

}
