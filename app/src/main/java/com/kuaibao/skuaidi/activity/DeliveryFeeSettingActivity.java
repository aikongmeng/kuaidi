package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kuaibao on 2016/4/14.
 */
public class DeliveryFeeSettingActivity extends SkuaiDiBaseActivity implements View.OnClickListener, TextWatcher{

    private static final String PER_PAI_FEE = "waybill_fee/set";
    private TextView tv_title_des, tv_submit_fee;
    private EditText et_per_fee;
    private Context context;
    private WebView wv_delivery_introduce;
    private RelativeLayout rl_delivery_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_setting);
        context = this;
        String type = getIntent().getStringExtra("type");
        wv_delivery_introduce = (WebView) findViewById(R.id.wv_delivery_introduce);
        rl_delivery_setting = (RelativeLayout) findViewById(R.id.rl_delivery_setting);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        if("info".equals(type)){
            String url = "";
            KLog.i("tag", SkuaidiSpf.getLoginUser().toString());
            if("申通快递".equals(SkuaidiSpf.getLoginUser().getExpressFirm())){
                url ="http://m.kuaidihelp.com/help/pf_exp?brand=sto";
            }else if("中通快递".equals(SkuaidiSpf.getLoginUser().getExpressFirm())){
                url = "http://m.kuaidihelp.com/help/pf_exp?brand=zto";
            }else{
                url = "http://m.kuaidihelp.com/help/pf_exp";
            }
            tv_title_des.setText("派费说明");
            rl_delivery_setting.setVisibility(View.GONE);
            wv_delivery_introduce.setVisibility(View.VISIBLE);
            WebSettings webSettings = wv_delivery_introduce.getSettings();
            //设置WebView属性，能够执行Javascript脚本
            webSettings.setJavaScriptEnabled(true);
            //设置可以访问文件
            webSettings.setAllowFileAccess(true);
            //设置支持缩放
            webSettings.setBuiltInZoomControls(true);
            //设置Web视图
            wv_delivery_introduce.setWebViewClient(new WebViewClient());
            wv_delivery_introduce.loadUrl(url);
        }else if("setting".equals(type)){
            tv_title_des.setText("派费设置");
            rl_delivery_setting.setVisibility(View.VISIBLE);
            wv_delivery_introduce.setVisibility(View.GONE);
            initView();
            addListener();
        }

    }

    private void initView(){
        tv_submit_fee = (TextView) findViewById(R.id.tv_submit_fee);
        et_per_fee = (EditText) findViewById(R.id.et_per_fee);
        et_per_fee.setText(SkuaidiSpf.getDeliveryFee());
        if(Utility.isEmpty(et_per_fee.getText().toString())){
            tv_submit_fee.setText("保存");
            tv_submit_fee.setEnabled(false);
            tv_submit_fee.setBackgroundResource(R.drawable.shape_btn_gray1);
        }else if(Float.parseFloat(et_per_fee.getText().toString()) == 0){
            tv_submit_fee.setText("保存");
            tv_submit_fee.setEnabled(true);
            tv_submit_fee.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        }else{
            et_per_fee.setEnabled(false);
            tv_submit_fee.setText("已设置");
            tv_submit_fee.setEnabled(false);
            tv_submit_fee.setBackgroundResource(R.drawable.shape_btn_gray1);
        }
    }

    @Override
    protected void onDestroy() {
        if (wv_delivery_introduce != null) {
            wv_delivery_introduce.getSettings().setJavaScriptEnabled(false);
            if(wv_delivery_introduce.getVisibility()==View.VISIBLE){
                wv_delivery_introduce.setVisibility(View.GONE);
            }
            ViewGroup parent = (ViewGroup) wv_delivery_introduce.getParent();
            if (parent != null) {
                parent.removeView(wv_delivery_introduce);
            }
            wv_delivery_introduce.removeAllViews();
            wv_delivery_introduce.destroy();
        }
        super.onDestroy();
    }

    private void addListener(){
        tv_submit_fee.setOnClickListener(this);
        et_per_fee.addTextChangedListener(this);
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
           result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(PER_PAI_FEE.equals(sname)){
            SkuaidiSpf.setDeliveryFee(result.optString("waybill_money"));
            tv_submit_fee.setText("已设置");
            tv_submit_fee.setEnabled(false);
            tv_submit_fee.setBackgroundResource(R.drawable.shape_btn_gray1);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        tv_submit_fee.setText("保存");
        tv_submit_fee.setEnabled(true);
        tv_submit_fee.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        UtilToolkit.showToast( result);
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onClick(View v) {
        if(tv_submit_fee.getId() == v.getId()){
            if(Utility.isEmpty(et_per_fee.getText().toString().trim())){
                UtilToolkit.showToast( "金额不能为空");
            }else{
                uploadPaiFee(et_per_fee.getText().toString().trim());
            }
        }
    }

    private void uploadPaiFee(String fee){
        JSONObject data = new JSONObject();
        try {
            data.put("sname", PER_PAI_FEE);
            data.put("waybill_money", fee);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(Utility.isEmpty(s.toString())){
            tv_submit_fee.setText("保存");
            tv_submit_fee.setEnabled(false);
            tv_submit_fee.setBackgroundResource(R.drawable.shape_btn_gray1);
        }else{
            tv_submit_fee.setText("保存");
            tv_submit_fee.setEnabled(true);
            tv_submit_fee.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
