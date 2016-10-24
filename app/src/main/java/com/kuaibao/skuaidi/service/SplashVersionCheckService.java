package com.kuaibao.skuaidi.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.kuaibao.skuaidi.BuildConfig;
import com.kuaibao.skuaidi.application.InitUtil;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.application.bugfix.RepairBugUtil;
import com.kuaibao.skuaidi.application.bugfix.model.PatchBean;
import com.kuaibao.skuaidi.commonwidget.webview.ConstWebView;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashVersionCheckService extends IntentService {
    private static final String dbpath = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/com.kuaibao.skuaidi/databases";
    private Subscriber<String> mSubscriber;
    public SplashVersionCheckService(){
        super("SplashVersionCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        copySkins();
        copyOrderDB();
        if(!TextUtils.isEmpty(SkuaidiSpf.getSessionId())){
            getAndFixParam();
        }
        getAdsVersions();
    }

    private void copySkins(){
        File skinFile=new File(InitUtil.SKIN_PATH+InitUtil.SKIN_FILE_NAME);
        if(!InitUtil.SKIN_VERION.equals(SkuaidiSpf.getSkinVersion()) || !skinFile.exists()){
            mSubscriber=new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    KLog.i("kb","mSubscriber copy skin completed");
                }
                @Override
                public void onError(Throwable e) {
                    KLog.e("kb",e.getMessage());
                    UtilToolkit.showToast("皮肤资源加载失败");
                }
                @Override
                public void onNext(String savePath) {
                    KLog.i("kb","mSubscriber copy skin onNext:--->"+savePath);
                    SkuaidiSpf.setSkinVersion(InitUtil.SKIN_VERION);
                }
            };
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    if(!FileUtils.fileExists(InitUtil.SKIN_PATH)){
                        FileUtils.fileMkdirs(InitUtil.SKIN_PATH);
                    }
                    File of = new File(InitUtil.SKIN_PATH + InitUtil.SKIN_FILE_NAME);
                    if(of.exists()){
                        of.delete();
                    }
                    try {
                        FileUtils.copyAssetFileToFiles(getApplicationContext(),InitUtil.SKIN_PATH,InitUtil.SKIN_FILE_NAME);
                    } catch (IOException e) {
                        subscriber.onError(e);
                        e.printStackTrace();
                    }
                    subscriber.onNext(InitUtil.SKIN_PATH+InitUtil.SKIN_FILE_NAME);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSubscriber);
        }
    }

    private Subscription subscription;

    private void getAndFixParam(){
        final ApiWrapper wrapper = new ApiWrapper();
        subscription = wrapper.getFixBugVersion(SKuaidiApplication.VERSION_CODE+"")
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<PatchBean>() {
                    @Override
                    public void call(PatchBean bean) {
                        if(!BuildConfig.DEBUG){
                            try {
                                //进行判断当前版本是否有补丁需要下载更新
                                new RepairBugUtil().comparePath(bean);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }));
    }
    private void copyOrderDB() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                File file = new File(dbpath + "/order.db");
                if (file.exists()) {
                    file.delete();
                }
                if (!isAddressDBExist()) {
                    KLog.e("kb","copyOrderDB");
                    File dir = new File(dbpath);
                    if (!dir.exists())
                        dir.mkdir();
                    File addressfile = new File(dbpath + "/address3.db");
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = getApplicationContext().getResources().getAssets().open("address416.mp3");
                        fos = new FileOutputStream(addressfile);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    private boolean isAddressDBExist() {
            String dbPath = dbpath + "/address3.db";
            File file=new File(dbPath);
        return (file!=null&&file.exists());
    }

    private void getAdWebVersion(final String adUrls, final String localETag, final String tag) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(adUrls);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setRequestProperty("E-Tag", localETag);
                    Map<String, List<String>> header = conn.getHeaderFields();
                    if(header!=null && header.entrySet()!=null){
                        String serverETag = localETag;
                        for (Map.Entry<String, List<String>> entry : header.entrySet()) {
                            if ("E-Tag".equals(entry.getKey())) {
                                serverETag = entry.getValue() + "";
                                if(!TextUtils.isEmpty(serverETag) && serverETag.length()>1 && serverETag.contains("]")){
                                    serverETag = serverETag.substring(1, serverETag.lastIndexOf("]"));
                                    if("".equals(SkuaidiSpf.getWebAdServerVersion(tag))){
                                        SkuaidiSpf.setWebAdLocalVersion(tag,serverETag);
                                    }
                                    SkuaidiSpf.setWebAdServerVersion(tag,serverETag);
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getAdsVersions() {
        getAdWebVersion(AdUrlBuildUtil.buidStartUrl(getApplicationContext()),SkuaidiSpf.getWebAdLocalVersion(ConstWebView.SPLASH_WEB_PREFIX),ConstWebView.SPLASH_WEB_PREFIX);
        getAdWebVersion(AdUrlBuildUtil.buildIndexUrl(getApplicationContext()),SkuaidiSpf.getWebAdLocalVersion(ConstWebView.BUSINESS_WEB_PREFIX),ConstWebView.BUSINESS_WEB_PREFIX);
        getAdWebVersion(AdUrlBuildUtil.buildBBSUrl(getApplicationContext()),SkuaidiSpf.getWebAdLocalVersion(ConstWebView.CIRCLE_WEB_PREFIX),ConstWebView.CIRCLE_WEB_PREFIX);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription!=null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        if(mSubscriber!=null && !mSubscriber.isUnsubscribed()){
            mSubscriber.unsubscribe();
        }
    }
}