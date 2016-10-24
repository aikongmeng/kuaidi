package com.kuaibao.skuaidi.personal.setting.accountmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.IDownloadManager;
import com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.greenrobot.greendao.query.Query;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import gen.greendao.bean.KBAccount;
import gen.greendao.bean.UserBind;
import gen.greendao.dao.KBAccountDao;
import gen.greendao.dao.UserBindDao;
import rx.Subscription;
import rx.functions.Action1;

import static com.kuaibao.skuaidi.R.id.tv_reg_mobile;

public class AddAccountActivity extends RxRetrofitBaseActivity {
    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(tv_reg_mobile)
    ClearEditText mobile;
    @BindView(R.id.et_login_pwd)
    ClearEditText pwd;
    @BindView(R.id.bt_login)
    Button btnLogin;
    private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        mUserInfo=SkuaidiSpf.getLoginUser();
        initView();
    }

    private void initView(){
        mTvTitleDes.setText("添加账号");
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
        mobile.setFocusable(true);
        mobile.requestFocus();
        mobile.setText("");
        mobile.setSelection(mobile.getText().toString().length());
        mobile.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)mobile.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mobile, 0);
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
        btnLogin.setEnabled(canLogin());
    }

    private boolean canLogin(){
        if(TextUtils.isEmpty(mobile.getText().toString().trim()) || TextUtils.isEmpty(pwd.getText().toString().trim())){
            return false;
        }
        return true;
    }
    @OnClick({R.id.iv_title_back,R.id.bt_login,R.id.bt_forgetPWD})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.bt_login:
                doLogin();
                break;
            case R.id.bt_forgetPWD:
                Intent intent1 = new Intent(AddAccountActivity.this, RegisterOrModifyInfoActivity.class);
                intent1.putExtra(RegisterOrModifyInfoActivity.FROM_WHERE_NAME,RegisterOrModifyInfoActivity.FORGETPWD_TYPE);
                startActivity(intent1);
                break;
        }
    }
    private void doLogin(){
        if(TextUtils.isEmpty(mobile.getText().toString().trim()) || TextUtils.isEmpty(mobile.getText().toString().trim())){
            UtilToolkit.showToast("账号或密码不能为空");
            return;
        }
        if(mobile.getText().toString().trim().length()!=11){
            UtilToolkit.showToast("手机号不合法");
            return;
        }
        if(mUserInfo.getPhoneNumber().equals(mobile.getText().toString().trim())){
            UtilToolkit.showToast("该账号已登录");
            return;
        }
        Login(mobile.getText().toString().trim(),pwd.getText().toString().trim());
    }
    private void Login(final String phoneNumber,final String pwd){
        showProgressDialog("正在登录...");
        final ApiWrapper wrapper=new ApiWrapper();
        Subscription subscription = wrapper.loginV1(phoneNumber,pwd)
                .subscribe(newSubscriber(new Action1<LoginUserInfo>() {
                    @Override
                    public void call(LoginUserInfo userInfo) {
                        KBAccount user=new KBAccount();
                        user.setNickName(userInfo.getRealname());
                        user.setPhoneNumber(phoneNumber);
                        user.setPassword(pwd);
                        userInfo.setPhoneNumber(phoneNumber);
                        userInfo.setPassword(pwd);
                        user.setUserId(userInfo.getUser_id());
                        user.setLastUpdateTime(new Date());
                        String suffix="counterman_" + user.getUserId() + ".jpg";
                        String downloadHeaderUrl = Constants.URL_HEADER_ROOT + suffix;
                        user.setHeadImgUrl(downloadHeaderUrl);
                        KBAccountDao dao= SKuaidiApplication.getInstance().getDaoSession().getKBAccountDao();
                        dao.insertOrReplace(user);

                        UserBindDao userBindDao=SKuaidiApplication.getInstance().getDaoSession().getUserBindDao();
                        Query query = userBindDao.queryBuilder().where(
                                UserBindDao.Properties.Master.eq(mUserInfo.getPhoneNumber()), UserBindDao.Properties.Guest.eq(user.getPhoneNumber()))
                                .build();
                        List<UserBind> userBinds= query.list();
                        if(userBinds==null || userBinds.size()==0){
                            UserBind userBind=new UserBind();
                            userBind.setMaster(mUserInfo.getPhoneNumber());
                            userBind.setGuest(user.getPhoneNumber());
                            userBindDao.insertOrReplace(userBind);
                        }
                        onLoginSuccess(suffix);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void onLoginSuccess(String suffix){
        File file=new File(Constants.HEADER_PATH+suffix);
        String filePath=file.getAbsolutePath();
        if(!FileUtils.fileExists(filePath.substring(0,filePath.lastIndexOf("/")))){
            FileUtils.fileMkdirs(filePath.substring(0,filePath.lastIndexOf("/")));
        }
        IDownloadManager.getInstance().startDownLoadTask(Uri.parse(Constants.URL_HEADER_ROOT+suffix),Uri.parse(filePath),null);
            setResult(RESULT_OK);
            UtilToolkit.showToast("添加成功");
            finish();
    }
}
