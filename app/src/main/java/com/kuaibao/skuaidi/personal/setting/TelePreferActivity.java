package com.kuaibao.skuaidi.personal.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.YunhuOutNumSettingActivity;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.PermissionGuideActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.service.RomUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import butterknife.BindView;
import butterknife.OnClick;



/**
 * Created by kuaibao on 2016/8/25.
 * Description:    通话设置
 */
public class TelePreferActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.iv_auto_record_switch)
    ImageView iv_auto_record_switch;
    @BindView(R.id.iv_from_notice_switch)
    ImageView iv_from_notice_switch;
    private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_prefer);
        tv_title_des.setText("通话设置");
        mUserInfo=SkuaidiSpf.getLoginUser();
        toggleViewState2(SkuaidiSpf.getIsShowWindow(this,mUserInfo.getPhoneNumber()));
        toggleViewState(SkuaidiSpf.getIsRecordingOpen(this,mUserInfo.getPhoneNumber()));
    }

    private void toggleViewState(boolean recordOpened){
        iv_auto_record_switch.setImageResource(recordOpened? R.drawable.icon_push_open:R.drawable.icon_push_close);
        SkuaidiSpf.setIsRecordingOpen(this, recordOpened,mUserInfo.getPhoneNumber());
    }

    private void toggleViewState2(boolean noticeOpened){
        iv_from_notice_switch.setImageResource(noticeOpened? R.drawable.icon_push_open:R.drawable.icon_push_close);
        SkuaidiSpf.setIsShowWindow(this, noticeOpened,mUserInfo.getPhoneNumber());
        if(!noticeOpened){
            iv_auto_record_switch.setImageResource(R.drawable.icon_push_close);
            SkuaidiSpf.setIsRecordingOpen(this, false,mUserInfo.getPhoneNumber());
            iv_auto_record_switch.setEnabled(false);
        }else{
            iv_auto_record_switch.setEnabled(true);
        }
    }

    @OnClick({R.id.rl_tele_show, R.id.iv_auto_record_switch, R.id.iv_from_notice_switch})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_tele_show://网络电话去电显示
                UMShareManager.onEvent(this, "more_setting_showNum", "more_setting", "更多设置：去电显示号码设置");
                Intent intent = new Intent(this, YunhuOutNumSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_auto_record_switch://来电自动录音
                toggleViewState(!SkuaidiSpf.getIsRecordingOpen(this,mUserInfo.getPhoneNumber()));
                break;
            case R.id.iv_from_notice_switch://来电智能信息提醒
                toggleViewState2(!SkuaidiSpf.getIsShowWindow(this,mUserInfo.getPhoneNumber()));
                if(SkuaidiSpf.getIsShowWindow(this,mUserInfo.getPhoneNumber())){
                    RomUtils.SKuaidiAlertWindowPermissionCheck(TelePreferActivity.this,false);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PermissionGuideActivity.REQUEST_GO_TO_SETTING){
            if(resultCode==RESULT_OK){
                RomUtils.goToApplicationDetail(TelePreferActivity.this);
            }
        }
    }

    public void back(View view){
        finish();
    }

}
