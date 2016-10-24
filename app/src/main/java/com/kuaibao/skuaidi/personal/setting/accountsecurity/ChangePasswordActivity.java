package com.kuaibao.skuaidi.personal.setting.accountsecurity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.LoginActivity;
import com.kuaibao.skuaidi.activity.view.SkuaidiEditText;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by cj on 2016/8/26.
 * Description:    修改登录密码
 */
public class ChangePasswordActivity extends RxRetrofitBaseActivity implements TextWatcher {

    @BindView(R.id.tv_title_des)
    TextView tv_title_desc;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.iv_icon_user)
    ImageView iv_icon_user;
    @BindView(R.id.tv_brand_name)
    TextView tv_brand_name;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_user_phone)
    TextView tv_user_phone;
    @BindView(R.id.et_old_pwd)
    SkuaidiEditText et_old_pwd;
    @BindView(R.id.et_new_pwd)
    SkuaidiEditText et_new_pwd;
    @BindView(R.id.et_sure_pwd)
    SkuaidiEditText et_sure_pwd;
    @BindView(R.id.tv_submit_info)
    TextView tv_submit_info;

    private UserInfo userInfo;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        context = this;
        tv_title_desc.setText("修改密码");
        tv_more.setVisibility(View.GONE);
        userInfo = SkuaidiSpf.getLoginUser();
        tv_brand_name.setText(userInfo.getExpressFirm().substring(0, 2));
        tv_user_name.setText(userInfo.getUserName());
        tv_user_phone.setText(userInfo.getPhoneNumber());
//        String headUrl= Constants.URL_HEADER_ROOT+ "counterman_" + SkuaidiSpf.getLoginUser().getUserId() + ".jpg";
//        GlideUtil.GlideCircleImgSkip(ChangePasswordActivity.this,
//                headUrl,iv_icon_user,
//                R.drawable.geng_icon_touxiang,
//                R.drawable.geng_icon_touxiang);
        GlideUtil.GlideHeaderImg(ChangePasswordActivity.this,SkuaidiSpf.getLoginUser().getUserId(),iv_icon_user,R.drawable.icon_yonghu,R.drawable.icon_yonghu);
        et_old_pwd.addTextChangedListener(this);
        et_new_pwd.addTextChangedListener(this);
        et_sure_pwd.addTextChangedListener(this);
        automaticPopsoftKeyboard();
    }

    @OnClick({R.id.tv_submit_info})
    public void onClick(View view){
        if(view.getId() == R.id.tv_submit_info){
            if(et_new_pwd.getText().toString().equals(et_sure_pwd.getText().toString())) {
                final ApiWrapper apiWrapper = new ApiWrapper();
                apiWrapper.changeLoginPassword(userInfo.getPhoneNumber(), et_old_pwd.getText().toString(),
                        et_new_pwd.getText().toString(), et_sure_pwd.getText().toString())
                        .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonObject) {
                        UpdateReviewInfoUtil.updateCurrentReviewStatus("");
                        SkuaidiSpf.exitLogin(getApplicationContext());
                        SkuaidiSpf.setIsLogin(false);
                        UtilToolkit.showToast("密码修改成功，请重新登录！");
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        EventBus.getDefault().post(new MessageEvent(SKuaidiApplication.EXIT_ALL_NOTIFY,""));
                        Utility.stopPushService();
                    }
                }));
            }else{
                UtilToolkit.showToast("两次输入的密码不一致，请重新输入！");
            }
        }
    }

    public void back(View view){
        finish();
    }

    /**
     * 自动弹出软键盘
     */
    private void automaticPopsoftKeyboard() {
        et_old_pwd.setFocusable(true);
        et_old_pwd.requestFocus();
        et_old_pwd.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) et_old_pwd.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et_old_pwd, 0);
            }
        },998);
    }

    /**
     * 信息是否输入完整
     * @return
     */
    private boolean isCompleted(){
        return !TextUtils.isEmpty(et_old_pwd.getText().toString())
                && !TextUtils.isEmpty(et_new_pwd.getText().toString())
                && !TextUtils.isEmpty(et_sure_pwd.getText().toString())
                && et_new_pwd.getText().toString().length() > 5
                && et_sure_pwd.getText().toString().length() > 5;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isCompleted()) {
            tv_submit_info.setEnabled(true);
            tv_submit_info.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        } else {
            tv_submit_info.setEnabled(false);
            tv_submit_info.setBackgroundResource(R.drawable.shape_btn_gray1);

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
