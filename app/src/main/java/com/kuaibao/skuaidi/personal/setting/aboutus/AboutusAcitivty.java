package com.kuaibao.skuaidi.personal.setting.aboutus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.FunctionActivity;
import com.kuaibao.skuaidi.activity.MakeHelpActivity;
import com.kuaibao.skuaidi.application.InitUtil;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.kuaibao.skuaidi.util.VersionUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cj on 2016/8/25.
 * Description:    关于我们
 */
public class AboutusAcitivty extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_current_version)
    TextView tv_current_version;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    private Intent intent;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        context = this;
        tv_title_des.setText("关于快递员");
        tv_more.setVisibility(View.GONE);
        tv_current_version.setText(Utility.getVersionName());
    }

    @OnClick({R.id.rl_use_help, R.id.rl_function_introduce, R.id.rl_check_version,
            R.id.rl_contact_us, R.id.rl_advice_review})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_use_help://使用帮助
                UMShareManager.onEvent(context, "more_about_help", "about_help", "更多设置：关于快递员-使用帮助");
                intent = new Intent(this, MakeHelpActivity.class);
                String params = "?v="+ VersionUtil.getCurrentVersion(context).substring(1)+"&brand="+SkuaidiSpf.getLoginUser().getExpressNo();
                intent.putExtra("url", Constants.NEW_USE_HELP+params);
                intent.putExtra("comeFrom", "moreSetting");
                startActivity(intent);
                break;
            case R.id.rl_function_introduce://功能介绍
                intent = new Intent(this, FunctionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_check_version://检查新版本
                // 设置手动检查更新
//                UmengUpdateAgent.forceUpdate(this);
                showProgressDialog( "正在检查...");
                UmengUpdateAgent.setUpdateAutoPopup(false);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        switch (updateStatus) {
                            case UpdateStatus.Yes: // has update
                                dismissProgressDialog();
                                UmengUpdateAgent.showUpdateDialog(AboutusAcitivty.this, updateInfo);
                                break;
                            case UpdateStatus.No: // has no update
                                dismissProgressDialog();
                                Toast.makeText(AboutusAcitivty.this, "你的版本已经是最新的了", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.NoneWifi: // none wifi
                                dismissProgressDialog();
                                Toast.makeText(AboutusAcitivty.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout: // time out
                                dismissProgressDialog();
                                Toast.makeText(AboutusAcitivty.this, "超时", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                UmengUpdateAgent.update(AboutusAcitivty.this);
                break;
            case R.id.rl_contact_us://联系我们
                intent = new Intent(this, ContactUsActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_advice_review://意见反馈
                UMShareManager.onEvent(context, "salesman_more_feedback", "salesman_more", "更多模块：产品意见反馈");
                FeedbackAPI.Chat_Url = InitUtil.CHAT_URL_ONLINE;
                //获取反馈未读数
                //可选功能，第二个参数是当前登录的openim账号，如果是匿名账号方式使用，则可以传空的。返回的未读数在onsuccess接口数组中第一个元素，直接转成Integer就可以。
                //FeedbackAPI.getFeedbackUnreadCount(Context context, String uid, final IWxCallback callback)
				/*
 				 * 可以设置UI自定义参数，如主题色等,具体为：
 				 * enableAudio(是否开启语音 1：开启 0：关闭)
 				 * bgColor(消息气泡背景色 "#ffffff")，
 				 * color(消息内容文字颜色)，
 				 * avatar(当前登录账号的头像)，
 				 * toAvatar(客服账号的头像)
 				 * themeColor(标题栏自定义颜色 "#ffffff")
     			 */
                Map<String, String> uiMap = new HashMap<>();
                uiMap.put("enableAudio", "1");
                uiMap.put("themeColor", "#0CBAA0");
                uiMap.put("env", "online");
                uiMap.put("avatar",Constants.URL_HEADER_ROOT+ "counterman_"
                        + SkuaidiSpf.getLoginUser().getUserId() + ".jpg");
                uiMap.put("toAvatar", "http://img.kuaidihelp.com/www/new/logo_s.png");//客服账号的头像
                FeedbackAPI.setUICustomInfo(uiMap);
                //可以设置反馈消息自定义参数，方便在反馈后台查看自定义数据，参数是json对象，里面所有的数据都可以由开发者自定义
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("feedbackloginTime", UtilityTime.getDateTimeByMillisecond(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                    jsonObject.put("visitPath", "Android:SKuadidi_" +SKuaidiApplication.VERSION_CODE+"_"+SKuaidiApplication.VERSION_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FeedbackAPI.setAppExtInfo(jsonObject);
                /**
                 *设置自定义联系方式
                 * @param customContact  自定义联系方式
                 * @param hideContactView 是否隐藏联系人设置界面
                 */
                FeedbackAPI.setCustomContact(SkuaidiSpf.getLoginUser().getPhoneNumber(), false);
                //FeedbackAPI.mFeedbackCustomInfoMap.remove("uid");
                //FeedbackAPI.initOpenImAccount(getApplication(), InitUtil.ALI_BAICHUAN_APPID, getRandAccount(), "taobao1234");
                String errorMsg = FeedbackAPI.openFeedbackActivity(getApplicationContext());
                if (!TextUtils.isEmpty(errorMsg)) {
                    UtilToolkit.showToast( errorMsg);
                }
                break;
        }
    }

    public void back(View view){
        finish();
    }
}
