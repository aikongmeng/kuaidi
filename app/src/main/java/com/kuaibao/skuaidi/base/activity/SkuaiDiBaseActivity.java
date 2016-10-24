package com.kuaibao.skuaidi.base.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CommonWebViewActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dialog.SkuaiDiSysDialog;
import com.kuaibao.skuaidi.dialog.SkuaiDiSysDialog.SkuaiDiSysDialogButtonOnclickListener;
import com.kuaibao.skuaidi.manager.SKuaidiSMSManager;
import com.kuaibao.skuaidi.manager.SkuaidiThreasManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.progressbar.CustomProgressDialog;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.feng.skin.manager.base.BaseActivity;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
//import //KLog.LogUtils;

/**
 * activity基类 封装大多数通用函数，以及抽象函数，回调函数
 *
 * @author xy
 */
@SuppressLint("HandlerLeak")
public abstract class SkuaiDiBaseActivity extends BaseActivity {
    /**
     * 打印activity的类名
     */
    private static final String ACTIVITY_NAME = "activity_name";
    private static final int ASYNCHRONOUS_PROCESSING_FINISH = 1;
    public static final int LOADCOMMON_WEBVIEW = 0x100001;
    //    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    public SkuaiDiSysDialog sysDialog = null;
    private Context context;
    protected static final int INTERFACE_VERSION_OLD = 1;
    protected static final int INTERFACE_VERSION_NEW = 2;
    private static final int NETWORK_DOWN = 3;
    private static final int NETWORK_UP = 4;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals("network_down")) {
                Message msg = new Message();
                msg.what = NETWORK_DOWN;
                handler.sendMessage(msg);
            }
            if (action.equals("network_up")) {
                Message msg = new Message();
                msg.what = NETWORK_UP;
                handler.sendMessage(msg);
            }
        }

    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case ASYNCHRONOUS_PROCESSING_FINISH:
                    ((OnAsynchronous) (msg.obj)).onProcessingFinish();
                    break;
//                case SKuaidiSMSBroadcastListener.SMS_SEND_SUCCESS:
//                    OnSMSSendSuccess();
//                    break;
//                case SKuaidiSMSBroadcastListener.SMS_SEND_FAIL:
//                    OnSMSSendFail();
//                    break;
                case NETWORK_DOWN:
                    onNetWorkChanged(false);
                    break;
                case NETWORK_UP:
                    onNetWorkChanged(true);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    protected CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        registerBoradcastReceiver();
        mCompositeSubscription = new CompositeSubscription();
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())? R.color.sto_text_color:R.color.title_bg),0);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    /**
     * 服务器接口请求
     *
     * @param data 需要传到服务器的Json
     *             需要传到服务器的cookie 不需要则传入NULL
     */
    public void httpInterfaceRequest(JSONObject data, boolean isOffLineProcessing, int interfaceVersion) {
        if (interfaceVersion == INTERFACE_VERSION_OLD) {
            request(data, isOffLineProcessing);
        } else if (interfaceVersion == INTERFACE_VERSION_NEW) {
            requestNewInterface(data, isOffLineProcessing);
        } else if (interfaceVersion == HttpHelper.SERVICE_V1) {
            requestV3(data);
        }
    }

    /**
     * 正式请求接口 当session失效时会后台重新登录获取session再次请求
     *
     * @param data
     * @param isOffLineProcessing
     */

    private void request(final JSONObject data, final boolean isOffLineProcessing) {
        try {
            data.put("pname", "androids");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
            @Override
            public void onSuccess(String result, String sname) {
                //Log.i("result", result);
                try {
                    JSONObject json = new JSONObject(result);
                    String code = json.getString("code");
                    String msg = json.getString("msg");
                    if (code.equals("1103") || code.equals("5") || code.equals("6") || code.equals("401")) {// SESSION失效
//                        SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//
//                            @Override
//                            public void todo() {
//                                request(data, isOffLineProcessing);
//                            }
//
//                            @Override
//                            public void faild() {
//
//                            }
//                        });
                        BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
                            @Override
                            public void onReLoginSuccess() {
                                request(data, isOffLineProcessing);
                            }
                        });
                    } else if (code.equals("0")) {

//						onRequestSuccess(sname, msg, json, "");// 请求成功
                        onRequestSuccess(sname, msg, result, "");// 请求成功

                    } else {
                        onRequestOldInterFaceFail(code, sname, msg, json.optJSONObject("data"));// 请求异常
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String result, JSONObject data_fail, String code) {
                if (result.contains("<!DOCTYPE html>")) {
                    result = "网络无连接";
                }
//				JSONObject json;
//				String code_old = Constants.SPACE;
//				try {
//					json = new JSONObject(result);
//					code_old = json.getString("code");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				} finally {
                onRequestOldInterFaceFail(code, data.optString("sname"), result, null);
//				}
            }
        }, handler);
        httpHelper.getPart(data, Utility.getSession_id(context));
    }

    private void requestV3(final JSONObject data) {
        try {
            data.put("pname", "androids");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
            @Override
            public void onSuccess(String result, String sname) {
                KLog.d("接口sname:--->" + sname + ";接口返回Result:--->" + result);
                String msg = "";
                try {
                    JSONObject json = new JSONObject(result);
                    //KLog.d("$$$$$");
                    String code = json.getString("code");
                    msg = json.getString("msg");
                    if (code.equals("0")) {
//						onRequestSuccess(sname, msg, json.optJSONObject("data"), data.optString("act"));
                        onRequestSuccess(sname, msg, json.optString("data"), data.optString("act"));
                    } else if (code.equals("1011")) {
                        BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
                            @Override
                            public void onReLoginSuccess() {
                                requestV3(data);
                            }
                        });
//                         SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//                            /**
//                             * 再次请求
//                             */
//                            @Override
//                            public void todo() {
//                                   requestV3(data);
//                            }
//
//                            @Override
//                            public void faild() {
//
//                            }
//                        });
                    } else {
                        try {
                            onFail(msg, json.optJSONObject("data"), code);
                        } catch (Exception e) {
                            onFail(msg, null, code);
                        }
                    }

                } catch (Exception e) {
                    onFail(result, null, "");
                    //KLog.e(e.getMessage());
                }

            }

            @Override
            public void onFail(String result, JSONObject data_fail, String code) {
                KLog.e("接口请求Fail:--->result=" + result + ";data_fail=" + data_fail + ";code=" + code);
                if (result.contains("<!DOCTYPE html>")) {
                    result = "请连接网络";
                }
                onRequestFail(code, data.optString("sname"), result, data.optString("act"), data_fail);
            }
        }, handler);
        httpHelper.getPartV3(data);
    }

    public void requestV2(final JSONObject data) {
        try {
            data.put("pname", "androids");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
            @Override
            public void onSuccess(String result, String sname) {
                KLog.d("接口sname:--->" + sname + ";接口返回Result:--->" + result);
                String msg = "";
                try {
                    JSONObject json = new JSONObject(result);
                    String code = json.getString("code");
                    msg = json.getString("msg");
                    if (code.equals("0")) {
                        onRequestSuccess(sname, msg, json.optString("data"), data.optString("act"));
                    } else if (code.equals("1103") || code.equals("5") || code.equals("6") || code.equals("401")) {// SESSION失效
//                        SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//                            /**
//                             * 再次请求
//                             */
//                            @Override
//                            public void todo() {
//                                requestV2(data);
//                            }
//
//                            @Override
//                            public void faild() {
//
//                            }
//                        });
                        BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
                            @Override
                            public void onReLoginSuccess() {
                                requestV2(data);
                            }
                        });
                    } else {
                        try {
                            onFail(msg, json.optJSONObject("data"), code);
                        } catch (Exception e) {
                            onFail(msg, null, code);
                        }
                    }

                } catch (Exception e) {
                    onFail(result, null, "");
                }

            }

            @Override
            public void onFail(String result, JSONObject data_fail, String code) {
                if (result.contains("<!DOCTYPE html>")) {
                    result = "请连接网络";
                }
                onRequestFail(code, data.optString("sname"), result, data.optString("act"), data_fail);
            }
        }, handler);
        httpHelper.getPart(data, Utility.getSession_id(SKuaidiApplication.getInstance()));
    }

    private void requestNewInterface(final JSONObject data, final boolean isOffLineProcessing) {
        try {
            data.put("pname", "androids");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        HttpHelper httpHelper = new HttpHelper(new OnResultListener() {
            @Override
            public void onSuccess(String result, String sname) {

                String msg = "";
                try {
                    JSONObject json = new JSONObject(result);
                    String code = json.getString("code");
                    msg = json.getString("msg");
                    if (code.equals("1103") || code.equals("5") || code.equals("6") || code.equals("401")) {// SESSION失效
//                        SkuaidiUserManager.getInstanse().userLogin(new WhatToDo() {
//                            /**
//                             * 再次请求
//                             */
//                            @Override
//                            public void todo() {
//                                requestNewInterface(data, isOffLineProcessing);
//                            }
//
//                            @Override
//                            public void faild() {
//
//                            }
//                        });
                        BaseRxHttpUtil.reLogin(new BaseRxHttpUtil.ReLoginCallBack() {
                            @Override
                            public void onReLoginSuccess() {
                                requestNewInterface(data, isOffLineProcessing);
                            }
                        });
                    } else if (code.equals("0")) {
                        JSONObject resultData = json.optJSONObject("data");
                        if (resultData == null) {
                            onRequestFail(code, sname, json.toString(), data.optString("act"), null);
                            return;
                        }
                        String act = resultData.optString("act");
                        if ("scan.to.qf".equals(act)) {// 全峰不支持批量上传
//							onRequestSuccess(sname, msg, resultData, act);// 请求成功
                            onRequestSuccess(sname, msg, json.optString("data"), act);// 请求成功
                            return;
                        }
                        String status = resultData.getString("status");
                        if (status.equals("success")) {
                            JSONObject datas = resultData.optJSONObject("result");
//							onRequestSuccess(sname, msg, datas, act);// 请求成功
                            onRequestSuccess(sname, msg, resultData.optString("result"), act);// 请求成功
                        } else if (status.equals("fail")) {
                            if ("scan.zt.verify".equals(sname)) {
                                onRequestFail(code, sname, resultData.toString(), act, null);
                            } else {
                                info = resultData.optString("confirm");
                                String desc = resultData.optString("desc");
                                if (Utility.isEmpty(desc)) {
                                    JSONObject result1 = resultData.optJSONObject("result");
                                    if (result1 != null) {
                                        desc = result1.optString("retStr");
                                    }
                                }
                                onRequestFail(code, sname, desc, act, resultData);
                            }
                        }
                    } else {
                        onRequestFail(code, sname, msg, data.optString("act"), json.optJSONObject("data"));// 请求异常
                    }
                } catch (Exception e) {
                    onRequestFail("", sname, result, data.optString("act"), null);// 请求异常
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String result, JSONObject data_fail, String code) {

                if (result.contains("<!DOCTYPE html>")) {
                    result = "网络无连接";
                }
                onRequestFail(code, data.optString("sname"), result, data.optString("act"), null);
            }
        }, handler);
        httpHelper.getPart(data, Utility.getSession_id(SKuaidiApplication.getInstance()));
    }

    /**
     * 异步处理函数
     *
     * @param asynchronous 异步处理接口
     *                     异步处理接口对接标示
     */
    protected void onAsynchronousProcessing(final OnAsynchronous asynchronous) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                asynchronous.onAsynchronousFunction();
                Message msg = new Message();
                msg.what = ASYNCHRONOUS_PROCESSING_FINISH;
                msg.obj = asynchronous;
                handler.sendMessage(msg);
            }
        }).start();
    }

    String info;

    /**
     * 异步处理接口
     *
     * @author pc
     */
    public interface OnAsynchronous {
        void onAsynchronousFunction();// 异步处理

        void onProcessingFinish();// 异步处理完成
    }

    /**
     * 服务器接口请求成功
     * <p/>
     * Json返回code
     */
    protected abstract void onRequestSuccess(String sname, String msg, String result, String act);

    /**
     * 不点击控件弹Dialog函数
     *
     * @param title                         标题
     * @param positionButtonTitle           确定按钮标题
     * @param nagtiveButtonTitle            取消按钮标题
     * @param content                       提示信息
     * @param positionButtonOnclickListener 确定按钮点击事件
     * @param nagtiveButtonOnclickListener  取消按钮点击事件
     */
    protected void startUsingSysDialog(String title, String positionButtonTitle, String nagtiveButtonTitle,
                                       String content, SkuaiDiSysDialogButtonOnclickListener positionButtonOnclickListener,
                                       SkuaiDiSysDialogButtonOnclickListener nagtiveButtonOnclickListener) {
        sysDialog = new SkuaiDiSysDialog(SkuaiDiBaseActivity.this);
        sysDialog.setContent(content);
        sysDialog.setTitle(title);
        sysDialog.setPositionButtonTitle(positionButtonTitle);
        sysDialog.setNagtiveButtonTitle(nagtiveButtonTitle);
        sysDialog.setPositionButtonOnclickListener(positionButtonOnclickListener);
        sysDialog.setNagtiveButtopnOnclickListener(nagtiveButtonOnclickListener);
    }

    /**
     * 服务器接口请求失败
     *
     * @param code      TODO
     * @param result    失败信息
     * @param data_fail TODO
     */
    protected abstract void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail);

    /**
     * 服务器老版本接口请求失败
     *
     * @param sname
     * @param msg
     * @param result
     */

    protected abstract void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result);

    /**
     * 短信发送成功
     */
    protected void OnSMSSendSuccess() {

    }

    /**
     * 短信发送失败
     */
    protected void OnSMSSendFail() {

    }

    @Override
    protected void onDestroy() {
        setContentView(new View(this));
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        mCompositeSubscription.unsubscribe();
    }

    /**
     * 后台发送短信
     *
     * @param sendNum         ： 短信接收者的号码 群发:号码之间以;隔开 例:13112341234;13988888888;15255555555
     *                        单发:短信接收者号码
     * @param sendTextMessage 发送内容
     * @param splitJointStr   发送内容后面需要拼接的内容 key:电话号码 value:拼接的内容
     */
    protected void sendSMS(String sendNum, String sendTextMessage, Map<String, String> splitJointStr) {
        Set<String> addr = new HashSet<>();

        if (sendNum.indexOf(";") != -1) {
            String[] addrs = sendNum.split(";");
            for (int i = 0; i < addrs.length; i++) {
                addr.add(addrs[i]);
            }
            try {
                long id = SkuaidiThreasManager.getOrCreateThreadId(this, addr);
                SKuaidiSMSManager.sendSMSMessage(null, addr, sendTextMessage, splitJointStr, this, id);
            } catch (java.lang.IllegalArgumentException e) {
                e.printStackTrace();
                UtilToolkit.showToast("请到应用管理中心设置发送短信权限");
            }
        } else {
            try {
                long id = SkuaidiThreasManager.getOrCreateThreadId(this, sendNum);
                SKuaidiSMSManager.sendSMSMessage(sendNum, null, sendTextMessage, splitJointStr, this, id);
            } catch (java.lang.IllegalArgumentException e) {
                e.printStackTrace();
                UtilToolkit.showToast("请到应用管理中心设置发送短信权限");
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sysDialog != null) {
            sysDialog.removeWindow();
        }
    }

    private void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("network_down");
        myIntentFilter.addAction("network_up");
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    protected void loadWeb(String url, String title) {
        Intent intent = new Intent(context, WebLoadView.class);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(url);
        parameters.add(title);
        intent.putStringArrayListExtra("parameters", parameters);
        startActivity(intent);
    }

    protected void loadWebCommon(String url){
        Intent intent = new Intent(context, CommonWebViewActivity.class);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(url);
        intent.putStringArrayListExtra("parameter",parameters);
        startActivityForResult(intent,LOADCOMMON_WEBVIEW);
    }

    /**
     * SINA SSO 回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = "null";
        try {
            Bundle b = data.getExtras();
            Set<String> keySet = b.keySet();
            if (keySet.size() > 0)
                result = "result size:" + keySet.size();
            for (String key : keySet) {
                Object object = b.get(key);
                //Log.d("TestData", "Result:" + key + "   " + object.toString());
            }
        } catch (Exception e) {

        }
        //Log.d("TestData", "onActivityResult   " + requestCode + "   " + resultCode + "   " + result);

        // 根据requestCode获取对应的SsoHandler
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
//        if (ssoHandler != null) {
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }

    protected String getAnotherInfo() {
        return info;
    }

    /**
     * 分享
     *
     * @param title     : 标题
     *                  : 分享内容
     * @param targetUrl :点击内容跳转的链接URL
     */
    public void openShare(String title, Map<String, String> shareTexts, String targetUrl, int drawableId) {
        UMShareManager.openShare(SkuaiDiBaseActivity.this, title, shareTexts, targetUrl, drawableId);
    }

    protected void onNetWorkChanged(boolean isNetWorkUp) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.i(ACTIVITY_NAME + "," + getClass().getName());
    }

    private CustomProgressDialog mProgressDialog;

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

}
