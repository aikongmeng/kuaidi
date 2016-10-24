package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataScanSettingActivity extends SkuaiDiBaseActivity {
    private String company;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        context = this;
        company = SkuaidiSpf.getLoginUser().getExpressNo();
        setContentView(R.layout.activity_data_scan_setting);
        ButterKnife.bind(this);
        TextView tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("设置");
        ImageView functional_switch_alarm = (ImageView) findViewById(R.id.functional_switch_alarm);
        if (SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            functional_switch_alarm.setImageResource(R.drawable.icon_push_open);
        } else {
            functional_switch_alarm.setImageResource(R.drawable.icon_push_close);
        }
        SkuaidiImageView iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);// 返回按钮
        iv_title_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    public void guidance(View view) {

        if ("sto".equals(company)) {
            UMShareManager.onEvent(context, "E3_help", "E3", "E3：巴枪扫描说明");
            loadWeb("http://m.kuaidihelp.com/help/baqiang_sto.html", "巴枪扫描说明");
        } else if ("qf".equals(company)) {
            UMShareManager.onEvent(context, "qf_help", "QF", "QF：巴枪扫描说明");
            loadWeb("http://m.kuaidihelp.com/help/baqiang_qf.html", "巴枪扫描说明");
        } else if ("zt".equals(company)) {
            UMShareManager.onEvent(context, "zt_help", "ZT", "ZT：巴枪扫描说明");
            loadWeb("http://m.kuaidihelp.com/help/baqiang_zt.html", "巴枪扫描说明");
        }
    }

    public void functionalSwitchAboutAlarm(final View view) {

        if (!SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            ((ImageView) view).setImageResource(R.drawable.icon_push_open);
            SkuaidiSpf.setAutoUpload(E3SysManager.getCourierNO(),true);
            Intent intent = new Intent(context, BackgroundUploadService.class);
            startService(intent);

        } else {
            ((ImageView) view).setImageResource(R.drawable.icon_push_close);
            SkuaidiSpf.setAutoUpload(E3SysManager.getCourierNO(),false);
            Intent intent = new Intent(context, BackgroundUploadService.class);
            stopService(intent);
        }

    }


    /**
     * 手机back键点击事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @OnClick({R.id.rl_notify_sign, R.id.rl_notify_problem})
    public void onClick(View view) {
        Intent intent = new Intent(this, SMSNotifySwitchActivity.class);
        switch (view.getId()) {
            case R.id.rl_notify_sign:
                intent.putExtra("notifyType","sign");
                break;
            case R.id.rl_notify_problem:
                intent.putExtra("notifyType","problem");
                break;
        }
        startActivity(intent);
    }
}
