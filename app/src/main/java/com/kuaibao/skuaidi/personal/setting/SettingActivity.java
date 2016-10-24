package com.kuaibao.skuaidi.personal.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.LoginActivity;
import com.kuaibao.skuaidi.activity.SavePrintPermissionActivity;
import com.kuaibao.skuaidi.activity.SelectModeActivity;
import com.kuaibao.skuaidi.activity.VIPPrivilegeActivity;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.personal.setting.aboutus.AboutusAcitivty;
import com.kuaibao.skuaidi.personal.setting.accountmanager.AccountManagerActivity;
import com.kuaibao.skuaidi.personal.setting.accountsecurity.AccountSecurityActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
    private UserInfo mUserInfo;
    @BindView(R.id.line_setting_electronic)
    View line;
    @BindView(R.id.rl_setting_electronic_sheet)
    RelativeLayout rl_setting_electronic_sheet;
    @BindView(R.id.tv_vip_status)
    TextView tv_vip_status;

    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        EventBus.getDefault().register(this);
        mUserInfo=SkuaidiSpf.getLoginUser();
        initView();
    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent){
        if(AccountManagerActivity.CHANGE_ACCOUNT==messageEvent.type){
            setStatusBar();
        }
    }

    private void initView(){
        tvDes.setText("设置");
        if (!"sto".equals(mUserInfo.getExpressNo()) && !"zt".equals(mUserInfo.getExpressNo())) {// 非申通或者中通
            line.setVisibility(View.GONE);
            rl_setting_electronic_sheet.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(SkuaidiSpf.getClientIsVIP(getApplicationContext())) && "y".equals(SkuaidiSpf.getClientIsVIP(getApplicationContext()))) {
            tv_vip_status.setText("已开通");
        } else {
            tv_vip_status.setText("");
        }
    }

    @OnClick({R.id.iv_title_back,R.id.rl_sms_vip_privilege,R.id.rl_setting_tele,R.id.rl_setting_delivery_notify,
              R.id.rl_setting_electronic_sheet,R.id.rl_browser_web_version,R.id.rl_aboutus,R.id.rl_change_account,
              R.id.rl_account_security,R.id.rl_quit_current})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.rl_sms_vip_privilege://短信VIP特权
                UMShareManager.onEvent(getApplicationContext(), "VIP_Privilege", "more_setting", "更多设置：短信VIP特权");
                mIntent = new Intent(this, VIPPrivilegeActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_setting_tele://通话
                UMShareManager.onEvent(getApplicationContext(), "more_telPrefer", "more_personal", "更多设置：通话");
                mIntent = new Intent(this, TelePreferActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_setting_delivery_notify://快件签收短信通知
                UMShareManager.onEvent(getApplicationContext(), "more_sign_smsNotice", "more_sms", "更多设置：快件签收短信通知");
                mIntent = new Intent(this, SelectModeActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_setting_electronic_sheet://电子面单权限管理
                UMShareManager.onEvent(getApplicationContext(), "more_print_permission", "more_permission", "更多设置：电子面单权限管理");
                mIntent = new Intent(this, SavePrintPermissionActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_browser_web_version://网页版
                UMShareManager.onEvent(getApplicationContext(), "more_web_version", "more_web", "更多设置：网页版");
                mIntent = new Intent(this, WebViewActivity.class);
                mIntent.putExtra("fromwhere", "moreActivity");
                startActivity(mIntent);
                break;
            case R.id.rl_aboutus://关于快递员
                UMShareManager.onEvent(getApplicationContext(), "more_about_us", "more_about", "更多设置：关于快递员");
                mIntent = new Intent(this, AboutusAcitivty.class);
                startActivity(mIntent);
                break;
            case R.id.rl_change_account://账号切换
                mIntent=new Intent(this, AccountManagerActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_account_security://账号安全
                mIntent = new Intent(this, AccountSecurityActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_quit_current://退出登录
                showSureQuitDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showSureQuitDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("确定退出？");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateReviewInfoUtil.updateCurrentReviewStatus("");
                SkuaidiSpf.exitLogin(getApplicationContext());
                SkuaidiSpf.setIsLogin(false);
                mIntent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finish();
                EventBus.getDefault().post(new MessageEvent(SKuaidiApplication.EXIT_ALL_NOTIFY,""));
                UtilToolkit.showToast( "已退出登录");
                Utility.stopPushService();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
