package com.kuaibao.skuaidi.activity.sendmsg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.sendmsg.apibaseactivity.SettingTemplatePwdApiBaseActivity;
import com.kuaibao.skuaidi.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**取件密码设置【选择】界面**/
public class SettingTemplatePasswordTypeActivity extends SettingTemplatePwdApiBaseActivity {

    private final int REQUEST_CODE = 0x1111;

    private final int RANDOM_PWD = 1;
    private final int FIXED_PWD = 2;

    private Activity mActivity;

    @BindView(R.id.tv_title_des)
    TextView mTitle;
    @BindView(R.id.random_hint)
    TextView randomHint;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.btn_random_pwd)
    RelativeLayout btnRandomPwd;
    @BindView(R.id.iv_check2)
    ImageView ivCheck2;
    @BindView(R.id.btn_sample_safety)
    RelativeLayout btnSampleSafety;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.modify_password)
    TextView modifyPassword;
    @BindView(R.id.init_pwd)
    TextView initPwd;
    @BindView(R.id.tv_more)
    TextView tv_more;

    private int selectItem = 1;// 选中的是随机密码还是固定密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password_type);
        ButterKnife.bind(this);
        mActivity = this;

        initView();
        getPwdInfo();// 获取密码信息【接口】
    }

    private void initView() {
        refreshUI();
        mTitle.setText("密码类型");
        String random_hint = randomHint.getText().toString();
        // 设置初始密码
        initPwd.setText("初始密码");
        password.setText("0000");
        tv_more.setText("保存");


        SpannableStringBuilder style = new SpannableStringBuilder(random_hint);
        style.setSpan(new ForegroundColorSpan(Color.rgb(102, 102, 102)), 0, random_hint.length() - 12, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.rgb(152, 152, 152)), random_hint.length() - 12, random_hint.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        randomHint.setText(style);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case SettingTemplatePasswordActivity.MODIFY_PASSWORD_SUCCESS:
                    String pwd = data.getStringExtra("pwd");
                    if (!Utility.isEmpty(pwd)) {
                        initPwd.setText(pwd.equals("0000") ? "初始密码" : "当前密码");
                        password.setText(pwd);
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.iv_title_back, R.id.btn_random_pwd, R.id.btn_sample_safety, R.id.modify_password, R.id.tv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_random_pwd:
                selectItem = RANDOM_PWD;
                refreshUI();
                break;
            case R.id.btn_sample_safety:
                selectItem = FIXED_PWD;
                refreshUI();
                break;
            case R.id.modify_password:
                Intent intent = new Intent(mActivity, SettingTemplatePasswordActivity.class);
                intent.putExtra("pwd",password.getText().toString());
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_more:
                savePwdInfo(selectItem,password.getText().toString());
                tv_more.setEnabled(false);
                showProgressDialog("");//mActivity,"正在保存");
                break;
        }
    }

    private void refreshUI() {
        switch (selectItem) {
            case RANDOM_PWD:
                ivCheck.setBackgroundResource(R.drawable.batch_add_checked);
                ivCheck2.setBackgroundResource(R.drawable.select_edit_identity);
                break;
            case FIXED_PWD:
                ivCheck.setBackgroundResource(R.drawable.select_edit_identity);
                ivCheck2.setBackgroundResource(R.drawable.batch_add_checked);
                break;
        }
    }

    @Override
    protected void getPwdInfoStatus(boolean isSuccess, int pwdType, String customizePwd) {
        initPwd.setText(customizePwd.equals("0000") ? "初始密码" : "当前密码");
        password.setText(customizePwd);
        selectItem = pwdType;
        refreshUI();
    }

    @Override
    protected void savePwdInfoStatus(boolean isSuccess) {
        dismissProgressDialog();//mActivity);
        tv_more.setEnabled(true);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ButterKnife.unbind(this);
    }
}
