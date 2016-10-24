package com.kuaibao.skuaidi.activity.sendmsg;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**设置新的取件密码界面**/
public class SettingTemplatePasswordActivity extends RxRetrofitBaseActivity {

    public static final int MODIFY_PASSWORD_SUCCESS = 0x10011;
    @BindView(R.id.input_pwd)
    EditText inputPassword;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_template_password);
        ButterKnife.bind(this);

        tvTitleDes.setText("设置");
        setListener();
        inputPassword.setText(!Utility.isEmpty(getIntent().getStringExtra("pwd")) ? getIntent().getStringExtra("pwd") : "");
        inputPassword.setSelection(inputPassword.getText().length());
        inputPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);// 如果输入法在窗口上已经显示，则隐藏，反之则显示
            }
        },300);
    }

    // 设置监听
    private void setListener() {
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 4) {
                    btnCommit.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                    btnCommit.setEnabled(true);
                } else {
                    btnCommit.setBackgroundResource(R.drawable.shape_btn_gray1);
                    btnCommit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.btn_commit, R.id.iv_title_back})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_commit:
                Intent intent = new Intent();
                intent.putExtra("pwd", inputPassword.getText().toString());
                setResult(MODIFY_PASSWORD_SUCCESS, intent);
                finish();
                break;
            case R.id.iv_title_back:
                finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
