package com.kuaibao.skuaidi.personal.setting.aboutus;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.VersionUtil;

import butterknife.BindView;

/**
 * Created by cj on 2016/8/31.
 * Description:    联系我们
 */
public class ContactUsActivity extends RxRetrofitBaseActivity{

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.tv_more)
    TextView tv_more;
    @BindView(R.id.web_content)
    WebView web_content;
    private Context context;

    private static final String CONTACT_URL = "http://m.kuaidihelp.com/help/s_contact?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        context = this;
        tv_title_des.setText("联系我们");
        tv_more.setVisibility(View.GONE);
        web_content.loadUrl(CONTACT_URL + VersionUtil.getCurrentVersion(context).substring(1));
        WebSettings webSettings = web_content.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        web_content.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }

    public void back(View view){
        finish();
    }
}
