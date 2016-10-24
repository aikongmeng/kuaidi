package com.kuaibao.skuaidi.personal.personinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateInfoStep1Activity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(R.id.tv_reg_idcard)
    ClearEditText tv_reg_idcard;
    @BindView(R.id.btn_next)
    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info_step1);
        initView();
    }

    private void initView(){
        tvDes.setText("修改个人信息");
        tv_reg_idcard.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                    if(canNext()){
                        doNext();
                    }
                    return true;
                }
                return false;
            }
        });
        tv_reg_idcard.addTextChangedListener(myTextWatcher);
        updateButtonStatus();
        tv_reg_idcard.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)tv_reg_idcard.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(tv_reg_idcard, 0);
            }
        },500);
    }

    private  final TextWatcher myTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            updateButtonStatus();
        }
    };

    private void updateButtonStatus(){
        btnNext.setEnabled(canNext());
    }

    private boolean canNext(){
        if(TextUtils.isEmpty(tv_reg_idcard.getText().toString().trim()) || tv_reg_idcard.getText().toString().trim().length()<15){
            return false;
        }
        return true;
    }
    @OnClick({R.id.iv_title_back,R.id.btn_next})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_next:
                if(!tv_reg_idcard.getText().toString().trim().equals(SkuaidiSpf.getLoginUser().getCodeId())){
                    UtilToolkit.showToast("身份证号验证失败,请重新输入");
                    return;
                }
                doNext();
                break;
        }
    }

    private void doNext(){
        Intent intent=new Intent(this, RegisterOrModifyInfoActivity.class);
        intent.putExtra(RegisterOrModifyInfoActivity.FROM_WHERE_NAME,RegisterOrModifyInfoActivity.MODIFY_TYPE);
        startActivity(intent);
        finish();
    }
}
