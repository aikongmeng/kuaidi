package com.kuaibao.skuaidi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.commonwidget.webview.AdsInterceptWebView;
import com.kuaibao.skuaidi.commonwidget.webview.ConstWebView;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.service.SplashVersionCheckService;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import butterknife.BindView;


public class SplashActivityNew extends RxRetrofitBaseActivity {
    @BindView(R.id.splash_webview)
    AdsInterceptWebView webView;
    @BindView(R.id.rl_splash_parent)
    RelativeLayout rl_splash_parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        rl_splash_parent.setBackgroundResource("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())?R.drawable.splash_sto:R.drawable.splash);
        webView.setLoadTag(ConstWebView.SPLASH_WEB_PREFIX);
        startSplashBindService();
        if ("".equals(SkuaidiSpf.getWebAdServerVersion(webView.getLoadTag()))) {
            if (Utility.isNetworkConnected()) {
                webView.loadUrl(AdUrlBuildUtil.buidStartUrl(getApplicationContext()));
            } else {
                if (SkuaidiSpf.getVersionCode(getApplicationContext())!=SKuaidiApplication.VERSION_CODE || !SkuaidiSpf.IsHadGuidNewVersion(getApplicationContext())) {
                    Intent intent = new Intent(SplashActivityNew.this, GuideActivity.class);
                    startActivity(intent);
                    finishActivity();
                } else if (!SkuaidiSpf.IsLogin()) {
                    Intent intent = new Intent(SplashActivityNew.this, LoginActivity.class);
                    startActivity(intent);
                    finishActivity();
                }else{
                    Intent intent = new Intent(SplashActivityNew.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            webView.loadUrl(AdUrlBuildUtil.buidStartUrl(getApplicationContext()));
        }
    }

    @Override
    protected  void setStatusBar(){
        StatusBarUtil.setTransparent(this);
    }

    public void finishActivity() {
        finish();
    }


    private void startSplashBindService() {
        Intent intent = new Intent(SplashActivityNew.this, SplashVersionCheckService.class);
        startService(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(false);
            if(webView.getVisibility()== View.VISIBLE){
                webView.setVisibility(View.GONE);
            }
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
        }
    }

}
