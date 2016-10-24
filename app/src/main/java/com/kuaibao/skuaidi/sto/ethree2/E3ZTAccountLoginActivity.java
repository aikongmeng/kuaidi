package com.kuaibao.skuaidi.sto.ethree2;

import android.content.Context;
import android.content.DialogInterface;
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

import com.alibaba.fastjson.JSONArray;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysMainActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class E3ZTAccountLoginActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.tv_reg_mobile)
    ClearEditText mobile;
    @BindView(R.id.et_login_pwd)
    ClearEditText pwd;
    public static final String BRANCH_NO_NAME="branch_no";
    private String branchNo;
    public static final String FROM_WHERE_NAME="from_where";
    private String fromWhere;
    @BindView(R.id.bt_login)
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e3_account_login);
        branchNo=getIntent().getStringExtra(BRANCH_NO_NAME);
        fromWhere=getIntent().getStringExtra(FROM_WHERE_NAME);
        initView();
    }

    private void initView(){
        mTvTitleDes.setText("登录中天系统");
        if(!TextUtils.isEmpty(branchNo)){
            mobile.setText(branchNo.contains(".")?branchNo:branchNo+".");
            mobile.setSelection(mobile.getText().toString().length());
            mobile.setFocusable(true);
            mobile.setFocusableInTouchMode(true);
            mobile.requestFocus();
        }
        pwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                    doLogin();
                    return true;
                }
                return false;
            }
        });
        mobile.addTextChangedListener(myTextWatcher);
        pwd.addTextChangedListener(myTextWatcher);
        updateButtonStatus();
        mobile.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)mobile.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mobile, 0);
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
        btnLogin.setEnabled(canLogin());
    }

    private boolean canLogin(){
        if(TextUtils.isEmpty(mobile.getText().toString().trim()) || TextUtils.isEmpty(pwd.getText().toString().trim())){
            return false;
        }
        return true;
    }
    @OnClick({R.id.iv_title_back,R.id.bt_login,R.id.bt_updateWD})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.bt_login:
                doLogin();
                break;
            case R.id.bt_updateWD:
                showAlertMessageDialog();
                break;
        }
    }

    private void showAlertMessageDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("如果修改网点编号,需要让客服到中天系统 中通IT系统-pda管理-中通助手审核 中重新审核你的App巴枪身份。确认修改网点吗?");
        builder.setTitle("温馨提示");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent(E3ZTAccountLoginActivity.this, E3ZTRegActivity.class);
                intent.putExtra(E3ZTRegActivity.REG_TITLE,"修改网点编号");
                intent.putExtra(E3ZTRegActivity.BRANCH_NO_NAME,"");
                intent.putExtra(E3ZTRegActivity.SHOP_NAME,"");
                intent.putExtra(E3ZTRegActivity.FROM_WHERE,"e3ZTAccountActivity");
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }

    private void doLogin(){
        if(TextUtils.isEmpty(mobile.getText().toString().trim()) || TextUtils.isEmpty(mobile.getText().toString().trim())){
            UtilToolkit.showToast("工号或密码不能为空");
            return;
        }
//        if("E3MainActivity".equals(fromWhere) && E3SysManager.getCourierNO().equals(mobile.getText().toString().trim())){
//            UtilToolkit.showToast("请输入与当前登录工号不一致的工号");
//            return;
//        }
        loginZT(mobile.getText().toString().trim(),pwd.getText().toString().trim());
    }
    private void loginZT(String account,String pwd){
        showProgressDialog("");//E3ZTAccountLoginActivity.this,"登录中...");
        Map<String,String> params= BuildParams.buildE3Params(getApplicationContext(),"getinfo");
        params.put("emp_no", account);
        params.put("emp_pwd", pwd);
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription mSubscription=apiWrapper.getZTVerifyInfo(params.get("sname"),params)
                .subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                            if(!verifyInfo.containsKey("code")) {
                            UtilToolkit.showToast("系统异常,请稍后重试");
                            return;
                            }
                            int code=verifyInfo.getIntValue("code");
                            String status=verifyInfo.getString("status");
                            if(code==0 && "success".equals(status)){//跳转到E3主界面
                                com.alibaba.fastjson.JSONObject jsonResult=verifyInfo.getJSONObject("result");
                                if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
                                UtilToolkit.showToast("服务器繁忙,请稍后重试");
                                return;
                                }
                                int verified=jsonResult.getIntValue("verified");// 1 审核通过，账号没问题；0 审核未通过
                                if(verified==1){
                                    JSONArray jsonArray=jsonResult.getJSONArray("retArr");
                                    if(jsonArray==null || jsonArray.size()==0){//没有对应的工号信息,发生错误了
                                    UtilToolkit.showToast("服务器繁忙,请稍后重试");
                                    return;
                                }
                                com.alibaba.fastjson.JSONObject jsonReviewInfo=jsonArray.getJSONObject(0);
                                    jsonReviewInfo.put("isThroughAudit",1);
                                    UpdateReviewInfoUtil.updateCurrentReviewStatus(jsonReviewInfo.toJSONString());
                                    if("E3MainActivity".equals(fromWhere)){
                                        EventBus.getDefault().post(EThreeSysMainActivity.FINISH_MESSAGE);
                                    }
                                Intent intent = new Intent(E3ZTAccountLoginActivity.this, EThreeSysMainActivity.class);
                                startActivity(intent);
                                    finish();
                                }else{
                                    UtilToolkit.showToast("巴强账号异常,请退出重试");
                                }
                            }else{// 提示错误信息
                                showFailVerifiedDialog("温馨提示",verifyInfo.getString("desc"));
                            }
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }

    private void showFailVerifiedDialog(String title,String desc){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(desc);
        builder.setTitle(title);
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
