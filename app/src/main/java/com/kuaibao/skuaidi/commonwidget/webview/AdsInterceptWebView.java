package com.kuaibao.skuaidi.commonwidget.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kuaibao.skuaidi.activity.AdsShowActivity;
import com.kuaibao.skuaidi.activity.GuideActivity;
import com.kuaibao.skuaidi.activity.InformationActivity;
import com.kuaibao.skuaidi.activity.LoadWebInformationActivity;
import com.kuaibao.skuaidi.activity.LoginActivity;
import com.kuaibao.skuaidi.activity.NoticeCenterActivity;
import com.kuaibao.skuaidi.activity.NoticeDetailActivity;
import com.kuaibao.skuaidi.activity.SplashActivityNew;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.circle.CircleExpressDetailActivity;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import static com.socks.library.KLog.i;

//import com.kuaibao.skuaidi.activity.MainActivity;

/**
 * Created by lgg on 2016/8/17 10:38.
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
public class AdsInterceptWebView extends WebView {

    private String loadTag="";

    public String getLoadTag() {
        return loadTag;
    }

    public void setLoadTag(String loadTag) {
        this.loadTag = loadTag;
    }

    public AdsInterceptWebView(Context context) {
        super(context);
        init();
    }

    public AdsInterceptWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdsInterceptWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setBackgroundColor(0); // 设置背景色
        getSettings().setBuiltInZoomControls(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //webView.getSettings().setCacheMode(CacheMode);  //设置 缓存模式
        // 开启 DOM storage API 功能
        getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getContext().getFilesDir().getAbsolutePath() + AdUrlBuildUtil.APP_CACAHE_DIRNAME;
        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        i("cacheDirPath=" + cacheDirPath);
        //设置数据库缓存路径
        getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        getSettings().setAppCacheEnabled(true);
        getSettings().setAppCacheMaxSize(8 * 1024 * 1024);
        getSettings().setAllowFileAccess(true);
        if (Utility.isNetworkConnected()) {
            if ("".equals(SkuaidiSpf.getWebAdLocalVersion(loadTag))) {
                getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                i("WebSettings.LOAD_DEFAULT");
            } else if (!"".equals(SkuaidiSpf.getWebAdLocalVersion(loadTag)) && !"".equals(SkuaidiSpf.getWebAdServerVersion(loadTag)) && !SkuaidiSpf.getWebAdLocalVersion(loadTag).equals(SkuaidiSpf.getWebAdServerVersion(loadTag))) {
                i("local version:--->"+SkuaidiSpf.getWebAdLocalVersion(loadTag)+";server version:--->"+SkuaidiSpf.getWebAdServerVersion(loadTag));
                getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                i("WebSettings.LOAD_NO_CACHE");
                SkuaidiSpf.setWebAdLocalVersion(loadTag,SkuaidiSpf.getWebAdServerVersion(loadTag));
            } else {
                getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                i("WebSettings.LOAD_CACHE_ONLY");
            }
        } else {
            getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            i("WebSettings.LOAD_CACHE_ONLY");
        }

        setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                i("onLoadResource url=" + url);
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }
                //view.loadUrl(url);
                i("intercept url=" + url);
                String strUrl = "";
                String params = "";
                if (url.contains("?")) {
                    strUrl = url.substring(0, url.indexOf("?"));
                    params = url.substring(url.indexOf("=") + 1);
                } else {
                    strUrl = url;
                }
                if (ConstWebView.SPLASH_WEB_PREFIX.equals(loadTag) && (SkuaidiSpf.getVersionCode(getContext().getApplicationContext())!=SKuaidiApplication.VERSION_CODE || !SkuaidiSpf.IsHadGuidNewVersion(getContext().getApplicationContext()))) {
                    Intent intent = new Intent(getContext(), GuideActivity.class);
                    getContext().startActivity(intent);
                } else if (!SkuaidiSpf.IsLogin()) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    getContext().startActivity(intent);
                } else if ("goto://home".equals(strUrl)) {
                    //主页
                    Intent intent = new Intent(getContext(), com.kuaibao.skuaidi.main.MainActivity.class);
                    getContext().startActivity(intent);
                } else if ("goto://news_getlist/".equals(strUrl)) {
                    //咨讯中心列表
                    Intent intent = new Intent(getContext(), InformationActivity.class);
                    intent.putExtra("resource", loadTag);
                    getContext().startActivity(intent);
                } else if ("goto://tucaoDetail/".equals(strUrl)) {
                    //快递圈详情
                    Intent intent = new Intent(getContext(), CircleExpressDetailActivity.class);
                    intent.putExtra("topic_id", params);
                    intent.putExtra("resource", loadTag);
                    getContext().startActivity(intent);
                } else if ("goto://notice_getinfo/".equals(strUrl)) {
                    //通知中心详情
                    Intent intent = new Intent(getContext(), NoticeDetailActivity.class);
                    intent.putExtra("id", params);
                    intent.putExtra("resource", loadTag);
                    getContext().startActivity(intent);
                } else if ("goto://tucaoList/".equals(strUrl)) {
                    //快递圈列表
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("tabid", 1);
                    getContext().startActivity(intent);
                } else if ("goto://notice_getlist/".equals(strUrl)) {
                    //通知中心列表
                    Intent intent = new Intent(getContext(), NoticeCenterActivity.class);
                    intent.putExtra("resource", loadTag);
                    getContext().startActivity(intent);
                } else if ("goto://news_getinfo/".equals(strUrl)) {
                    //资讯中心详情
                    Intent intent = new Intent(getContext(), LoadWebInformationActivity.class);
                    intent.putExtra("id", params);
                    intent.putExtra("resource", loadTag);
                    getContext().startActivity(intent);
                } else if ("goto://browser".equals(strUrl)) {
                    Intent intent = new Intent(getContext(), AdsShowActivity.class);
                    intent.putExtra("url", url.substring(url.indexOf("'") + 1, url.lastIndexOf("'")));
                    getContext().startActivity(intent);
                }
                if(ConstWebView.SPLASH_WEB_PREFIX.equals(loadTag)){
                    ((SplashActivityNew)getContext()).finishActivity();
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                KLog.e("onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                KLog.d("pageFinish:--->" + url);
                if(!getSettings().getLoadsImagesAutomatically()) {
                    getSettings().setLoadsImagesAutomatically(true);
                }
                if(getVisibility()!= View.VISIBLE){
                    setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                KLog.e("webView error:--->" + errorCode + ";description:---->" + description);
                if(ConstWebView.SPLASH_WEB_PREFIX.equals(loadTag)){
                    if(errorCode==-2 || (!TextUtils.isEmpty(description) && description.contains("ERR_NAME_NOT_RESOLVED"))
                            || (!TextUtils.isEmpty(description) && description.contains("暂时出现故障"))){
                        view.stopLoading();
                        view.clearView();
                        if (SkuaidiSpf.getVersionCode(getContext().getApplicationContext())!=SKuaidiApplication.VERSION_CODE || !SkuaidiSpf.IsHadGuidNewVersion(getContext().getApplicationContext())) {
                            Intent intent = new Intent(getContext(), GuideActivity.class);
                            getContext().startActivity(intent);
                        } else if (!SkuaidiSpf.IsLogin()) {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            getContext().startActivity(intent);
                        }
                        ((SplashActivityNew)getContext()).finishActivity();
                    }
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }
        });
        if(Build.VERSION.SDK_INT >= 19) {
            getSettings().setLoadsImagesAutomatically(true);
        } else {
            getSettings().setLoadsImagesAutomatically(false);
        }
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                                long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(estimatedDatabaseSize * 10);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }
}
