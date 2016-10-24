package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSkinResourceManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cj on 2016/8/11.
 * Description: 快递柜，包括蜂巢管家和格格货站两个tab，通过加载webview实现
 */
public class SkuaidiCabinetActivity extends SkuaiDiBaseActivity implements View.OnClickListener {
    private TextView tv_cabinet_item1, tv_cabinet_item2;
    private WebView wv_cabinet;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabinet);
        context = this;
        tv_cabinet_item1 = (TextView) findViewById(R.id.tv_cabinet_item1);
        tv_cabinet_item2 = (TextView) findViewById(R.id.tv_cabinet_item2);
        wv_cabinet = (WebView) findViewById(R.id.wv_cabinet);
        tv_cabinet_item1.setEnabled(false);
        tv_cabinet_item1.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
        tv_cabinet_item2.setEnabled(true);
        tv_cabinet_item2.setTextColor(getResources().getColor(R.color.white));
        WebSettings webSettings = wv_cabinet.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        wv_cabinet.setBackgroundColor(Color.argb(0, 0, 0, 0));
        wv_cabinet.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                switch (url) {
                    case "js-call://location/locationCallback":
                        JSONObject json = new JSONObject();
                        try {
                            json.put("longitude", SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude());// 经度
                            json.put("latitude", SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude());// 纬度
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        wv_cabinet.loadUrl("javascript:locationCallback('" + json + "')");
                        break;
                    default:
                        wv_cabinet.loadUrl(url);
                        break;
                }
                return true;
            }
        });
        wv_cabinet.loadUrl("http://m.kuaidihelp.com/expressBox/fc?longitude="+SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude()+"&latitude="+SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude());

        tv_cabinet_item1.setOnClickListener(this);
        tv_cabinet_item2.setOnClickListener(this);
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cabinet_item1://蜂巢管家
                tv_cabinet_item1.setEnabled(false);
                tv_cabinet_item1.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundLeft_white());
                tv_cabinet_item2.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundRight());
                tv_cabinet_item1.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
                tv_cabinet_item2.setEnabled(true);
                tv_cabinet_item2.setTextColor(getResources().getColor(R.color.white));
                wv_cabinet.loadUrl("http://m.kuaidihelp.com/expressBox/fc?longitude="+SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude()+"&latitude="+SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude());
                break;
            case R.id.tv_cabinet_item2://格格货站
                tv_cabinet_item2.setEnabled(false);
                tv_cabinet_item1.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundLeft());
                tv_cabinet_item2.setBackgroundResource(SkuaidiSkinResourceManager.getTitleButtonBackgroundRight_white());
                tv_cabinet_item2.setTextColor(DynamicSkinChangeManager.getTextColorSkin());
                tv_cabinet_item1.setEnabled(true);
                tv_cabinet_item1.setTextColor(getResources().getColor(R.color.white));
                wv_cabinet.loadUrl("http://m.kuaidihelp.com/expressBox/gg?longitude="+SkuaidiSpf.getLatitudeOrLongitude(context).getLongitude()+"&latitude="+SkuaidiSpf.getLatitudeOrLongitude(context).getLatitude());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (wv_cabinet != null) {
            wv_cabinet.getSettings().setJavaScriptEnabled(false);
            if(wv_cabinet.getVisibility()==View.VISIBLE){
                wv_cabinet.setVisibility(View.GONE);
            }
            ViewGroup parent = (ViewGroup) wv_cabinet.getParent();
            if (parent != null) {
                parent.removeView(wv_cabinet);
            }
            wv_cabinet.removeAllViews();
            wv_cabinet.destroy();
        }
        super.onDestroy();
    }
}
