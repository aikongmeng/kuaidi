package com.kuaibao.skuaidi.personal.personinfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.FastRegistActivity;
import com.kuaibao.skuaidi.activity.LoginActivity;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.personal.personinfo.utils.ITimeCountDown;
import com.kuaibao.skuaidi.personal.setting.aboutus.ContactUsActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.robin.lazy.sms.SmsObserver;
import com.robin.lazy.sms.SmsResponseCallback;
import com.robin.lazy.sms.VerificationCodeSmsFilter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.config.SkinConfig;
import cn.feng.skin.manager.loader.SkinManager;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Subscription;
import rx.functions.Action1;

public class RegisterOrModifyInfoActivity extends RxRetrofitBaseActivity implements SmsResponseCallback,ITimeCountDown.ITimerCallBack,EasyPermissions.PermissionCallbacks {
    public final static int FAST_FINDPWD_OR_REGIST_REQUESTCODE = 0x1001;
    public final static int FAST_FINDPWD_OR_REGIST_RESULTCODE = 0X1002;
    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(R.id.tv_code_msg)
    TextView tv_code_msg;
    @BindView(R.id.ck_argree_article)
    CheckBox ck_argree_article;
    @BindView(R.id.ll_register_type)
    LinearLayout ll_register_type;
    @BindView(R.id.ll_warn_parent)
    LinearLayout ll_warn_parent;
    @BindView(R.id.tv_reg_mobile)
    ClearEditText tv_reg_mobile;
    @BindView(R.id.et_verify_code)
    ClearEditText et_verify_code;
    @BindView(R.id.btn_get_checkcode)
    Button btn_get_checkcode;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.et_set_password)
    ClearEditText et_set_password;
    @BindView(R.id.et_set_password_again)
    ClearEditText et_set_password_again;
    @BindView(R.id.tv_mianze)
    TextView tv_mianze;
    private String fromWhere;
    public static final String FROM_WHERE_NAME="from_where";
    public static final String MODIFY_TYPE="modify";
    public static final String REGISTR_TYPE="register";
    public static final String FORGETPWD_TYPE="forget_pwd";
    private SmsObserver smsObserver;
    private ITimeCountDown iTimeCountDown;
    private UserInfo mUserInfo;
    private static final int REQUEST_CODE_PERMISSION_READ_SMS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra(FROM_WHERE_NAME)){
            fromWhere=getIntent().getStringExtra(FROM_WHERE_NAME);
        }
        if(REGISTR_TYPE.equals(fromWhere) || FORGETPWD_TYPE.equals(fromWhere)){
            if(!SkinConfig.isDefaultSkin(SKuaidiApplication.getContext())){
                SkinManager.getInstance().restoreDefaultTheme();
            }
        }else{
            mUserInfo= SkuaidiSpf.getLoginUser();
        }
        setContentView(R.layout.activity_register_or_modify_info);
        initView();
        smsObserver=new SmsObserver(this,this,new VerificationCodeSmsFilter("106"));
        smsObserver.registerSMSObserver();
        String[] perms = {Manifest.permission.READ_SMS};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "请求以下权限:\n\n1.访问您的短信", REQUEST_CODE_PERMISSION_READ_SMS, perms);
        }
    }

    @Override
    protected void setStatusBar() {
        if(REGISTR_TYPE.equals(fromWhere)){
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.title_bg),0);
        }else{
            super.setStatusBar();
        }
    }

    private void initView(){
        if(REGISTR_TYPE.equals(fromWhere)){
            tvDes.setText("注册");
            ll_warn_parent.setVisibility(View.VISIBLE);
            ll_register_type.setVisibility(View.VISIBLE);
        }else if(MODIFY_TYPE.equals(fromWhere)){
            tvDes.setText("修改个人信息");
            if(!TextUtils.isEmpty(mUserInfo.getPhoneNumber()) && Utility.isNumeric(mUserInfo.getPhoneNumber())){
                tv_reg_mobile.setText(mUserInfo.getPhoneNumber());
                tv_reg_mobile.setSelection(tv_reg_mobile.getText().toString().length());
            }
        }else if(FORGETPWD_TYPE.equals(fromWhere)){
            ll_register_type.setVisibility(View.VISIBLE);
            tvDes.setText("忘记密码");
            btn_next.setText("完成");
        }

        SpannableStringBuilder builder= new SpannableStringBuilder(FORGETPWD_TYPE.equals(fromWhere)?"还没收到验证码？快速重置":"还没收到验证码？联系客服");
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.default_green_2)), 8,  12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_code_msg.setText(builder);
        if(REGISTR_TYPE.equals(fromWhere) || FORGETPWD_TYPE.equals(fromWhere)){
            SpannableStringBuilder builder1= new SpannableStringBuilder("我已阅读并同意《免责声明》");
            builder1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.default_green_2)), 7,  13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_mianze.setText(builder1);
        }

        et_verify_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                    if(canNext()){
                        checkCode();
                    }
                    return true;
                }
                return false;
            }
        });
        et_verify_code.addTextChangedListener(myTextWatcher);
        tv_reg_mobile.addTextChangedListener(myTextWatcher);
        et_set_password.addTextChangedListener(myTextWatcher);
        et_set_password_again.addTextChangedListener(myTextWatcher);
        ck_argree_article.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateButtonStatus();
            }
        });
        updateButtonStatus();
        tv_reg_mobile.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)tv_reg_mobile.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(tv_reg_mobile, 0);
            }
        },800);
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
        btn_next.setEnabled(canNext());
    }

    private boolean canNext(){
        if(MODIFY_TYPE.equals(fromWhere)){
            if(!TextUtils.isEmpty(tv_reg_mobile.getText().toString().trim()) && tv_reg_mobile.getText().toString().trim().length()==11 && !TextUtils.isEmpty(et_verify_code.getText().toString().trim())){
                return true;
            }
        } else if(REGISTR_TYPE.equals(fromWhere) || FORGETPWD_TYPE.equals(fromWhere)){
            if(!TextUtils.isEmpty(tv_reg_mobile.getText().toString().trim()) &&
                    tv_reg_mobile.getText().toString().trim().length()==11 &&
                    !TextUtils.isEmpty(et_verify_code.getText().toString().trim()) &&
                    !TextUtils.isEmpty(et_set_password.getText().toString().trim()) &&
                    (et_set_password.getText().toString().trim().length()>=6 && et_set_password.getText().toString().trim().length()<=16) &&
                    !TextUtils.isEmpty(et_set_password_again.getText().toString().trim()) &&
                    et_set_password.getText().toString().trim().equals(et_set_password_again.getText().toString().trim()) &&
                    ck_argree_article.isChecked()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCallbackSmsContent(String code) {
        et_verify_code.setText(code);
    }
    private Intent mIntent;
    @OnClick({R.id.iv_title_back,R.id.btn_get_checkcode,R.id.btn_next,R.id.tv_code_msg,R.id.tv_mianze})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                if(REGISTR_TYPE.equals(fromWhere)){
                    mIntent=new Intent(this, LoginActivity.class);
                    startActivity(mIntent);
                }
                finish();
                break;
            case R.id.btn_get_checkcode:
                getVerifyCode();
                break;
            case R.id.btn_next:
                checkCode();
                break;
            case R.id.tv_code_msg:
                if(FORGETPWD_TYPE.equals(fromWhere)){
                    mIntent = new Intent(RegisterOrModifyInfoActivity.this, FastRegistActivity.class);
                    mIntent.putExtra("phone_str", tv_reg_mobile.getText().toString().trim());
                    mIntent.putExtra("fromEvent", "getBackPwd");
                    startActivityForResult(mIntent, FAST_FINDPWD_OR_REGIST_REQUESTCODE);
                }else {
                    mIntent = new Intent(this, ContactUsActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.tv_mianze:
                mIntent = new Intent(this, WebViewActivity.class);
                mIntent.putExtra("fromwhere", "disclaimer");
                startActivity(mIntent);
                break;
        }
    }

    private void checkCode(){
        showProgressDialog("验证中...");
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription;
        if(REGISTR_TYPE.equals(fromWhere)){
            subscription=apiWrapper.validRegVerifyCode(tv_reg_mobile.getText().toString().trim(),et_verify_code.getText().toString().trim())
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            goToNextStep();
                        }
                    }));
        }else if(FORGETPWD_TYPE.equals(fromWhere)){
            subscription = apiWrapper.findBackPwd(et_set_password.getText().toString(), et_verify_code.getText().toString(), tv_reg_mobile.getText().toString())
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            dismissProgressDialog();//RegisterOrModifyInfoActivity.this);
                            UtilToolkit.showToast( "密码修改成功");
                            finish();
                        }
                    }));
        }else{
            subscription=apiWrapper.validModifyInfoVerifyCode(tv_reg_mobile.getText().toString().trim(),mUserInfo.getUserId(),et_verify_code.getText().toString().trim())
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            goToNextStep();
                        }
                    }));
        }
        mCompositeSubscription.add(subscription);
    }

    private void goToNextStep(){
        mIntent=new Intent(RegisterOrModifyInfoActivity.this,CompleteUserInfoActivity.class);
        mIntent.putExtra(FROM_WHERE_NAME,fromWhere);
        mIntent.putExtra(CompleteUserInfoActivity.USER_NAME_TITLE,tv_reg_mobile.getText().toString().trim());
        if(REGISTR_TYPE.equals(fromWhere)){
            mIntent.putExtra(CompleteUserInfoActivity.LOGIN_PWD_TITLE,et_set_password.getText().toString().trim());
        }
        mIntent.putExtra(CompleteUserInfoActivity.VERIFY_CODE_NAME,et_verify_code.getText().toString().trim());
        startActivity(mIntent);
        finish();
    }

    private void getVerifyCode(){
        btn_get_checkcode.setEnabled(false);
        if(iTimeCountDown==null){
            iTimeCountDown=new ITimeCountDown(60*1000,1000);
            iTimeCountDown.setITimerCallBack(this);
        }
        iTimeCountDown.start();
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription;
        if(REGISTR_TYPE.equals(fromWhere)){
            subscription = apiWrapper.getRegVerifyCode(tv_reg_mobile.getText().toString().trim())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            iTimeCountDown.cancel();
                            btn_get_checkcode.setText("获取验证码");
                            btn_get_checkcode.setEnabled(true);
                        }
                    })
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            if(jsonObject!=null && jsonObject.containsKey("register_des")){
                                UtilToolkit.showToast(jsonObject.getString("register_des"));
                            }
                        }
                    }));
        }else if(FORGETPWD_TYPE.equals(fromWhere)){
            subscription = apiWrapper.getVerifyCode(tv_reg_mobile.getText().toString().trim())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            iTimeCountDown.cancel();
                            btn_get_checkcode.setText("获取验证码");
                            btn_get_checkcode.setEnabled(true);
                        }
                    })
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            UtilToolkit.showToast(getResources().getString(R.string.check_code_tip2) + " "+ tv_reg_mobile.getText().toString());// 吐司弹出验证码发送到xx手机号
                        }
                    }));
        }else {
            subscription = apiWrapper.getModifyInfoVerifyCode(tv_reg_mobile.getText().toString().trim(),mUserInfo.getUserId().trim())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            iTimeCountDown.cancel();
                            btn_get_checkcode.setText("获取验证码");
                            btn_get_checkcode.setEnabled(true);
                        }
                    })
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            if(jsonObject!=null && jsonObject.containsKey("upUserDesc")){
                                UtilToolkit.showToast(jsonObject.getString("upUserDesc"));
                            }
                        }
                    }));
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FAST_FINDPWD_OR_REGIST_REQUESTCODE && resultCode == FAST_FINDPWD_OR_REGIST_RESULTCODE) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(smsObserver!=null){
            smsObserver.unregisterSMSObserver();
        }
    }

    @Override
    public void onTimeCountDown(long mills) {
        btn_get_checkcode.setText("重新获取("+mills/1000+")");
        btn_get_checkcode.setEnabled(false);
    }

    @Override
    public void onTimerFinish() {
        btn_get_checkcode.setText("获取验证码");
        btn_get_checkcode.setEnabled(true);
        iTimeCountDown.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION_READ_SMS) {
            Toast.makeText(this, "您拒绝了「访问您的短信」权限", Toast.LENGTH_SHORT).show();
        }
    }
}
