package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by ligg on 2016/3/31.
 */
public class AdUrlBuildUtil {
    public static int getScreenWith(Context context){
        WindowManager manager =(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context){
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static String buidStartUrl(Context context){
        return "http://m.kuaidihelp.com/app/launcher?type=s&system=android&screenWidth="
                +getScreenWith(context)+"&screenHeight="+getScreenHeight(context)+"&userId="+SkuaidiSpf.getLoginUser().getUserId();
    }
    public static String buildIndexUrl(Context context){
        return "http://m.kuaidihelp.com/app/indexer?type=s&system=android&screenWidth="
                +getScreenWith(context)+"&screenHeight="+getScreenHeight(context)+"&userId="+SkuaidiSpf.getLoginUser().getUserId();
    }
    public static String buildBBSUrl(Context context){
        return "http://m.kuaidihelp.com/app/banner?type=s&system=android&screenWidth="
                +getScreenWith(context)+"&screenHeight="+getScreenHeight(context)+"&userId="+SkuaidiSpf.getLoginUser().getUserId();
    }

    public static final String APP_CACAHE_DIRNAME = "/webcache";
//    public static WebView  initWebView(Context context,final WebView webView, int CacheMode){
//        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webView.getSettings().setCacheMode(CacheMode);  //设置 缓存模式
//        // 开启 DOM storage API 功能
//        webView.getSettings().setDomStorageEnabled(true);
//        //开启 database storage API 功能
//        webView.getSettings().setDatabaseEnabled(true);
//        String cacheDirPath = context.getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
////      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
//        KLog.i("cacheDirPath="+cacheDirPath);
//        //设置数据库缓存路径
//        webView.getSettings().setDatabasePath(cacheDirPath);
//        //设置  Application Caches 缓存目录
//        webView.getSettings().setAppCachePath(cacheDirPath);
//        //开启 Application Caches 功能
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setAppCacheMaxSize(8*1024*1024);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
//                                                long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
//                quotaUpdater.updateQuota(estimatedDatabaseSize * 3);
//            }
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                KLog.e("onJsAlert " + message);
//                result.confirm();
//                return true;
//            }
//            @Override
//            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//                KLog.e("onJsConfirm " + message);
//                return super.onJsConfirm(view, url, message, result);
//            }
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                KLog.e("onJsPrompt " + url);
//                return super.onJsPrompt(view, url, message, defaultValue, result);
//            }
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                if (newProgress >= 100) {
//                    webView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        return webView;
//    }
}
