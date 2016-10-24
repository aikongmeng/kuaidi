package com.kuaibao.skuaidi.personal.personinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.OptionsPickerView;
import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ExpressFirmActivity2;
import com.kuaibao.skuaidi.activity.LoginActivity;
import com.kuaibao.skuaidi.db.AddressDB;
import com.kuaibao.skuaidi.entry.AreaItem;
import com.kuaibao.skuaidi.entry.MyExpressBrandEntry;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.personal.personinfo.querybranch.SelectBranchActivity;
import com.kuaibao.skuaidi.personal.personinfo.register.RegisterSuccessActivity;
import com.kuaibao.skuaidi.personal.setting.accountmanager.AccountUtils;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.ExpressFirm;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.FROM_WHERE_NAME;
import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.REGISTR_TYPE;
import static com.kuaibao.skuaidi.personal.personinfo.authentication.RealNameAuthActivity.REAL_NAME_COMPLETE;

public class CompleteUserInfoActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
    private String fromWhere;
    @BindView(R.id.ll_warn_parent)
    LinearLayout  ll_warn_parent;
    @BindView(R.id.btn_complete)
    Button btn_complete;
    @BindView(R.id.et_real_name)
    EditText et_real_name;
    @BindView(R.id.tv_select_company)
    TextView tv_select_company;
    @BindView(R.id.tv_select_area)
    TextView tv_select_area;
    @BindView(R.id.tv_select_wangdian)
    TextView tv_select_wangdian;
    @BindView(R.id.ll_select_wangdian)
    RelativeLayout ll_select_wangdian;
    @BindView(R.id.et_select_wangdian)
    EditText et_select_wangdian;
    private Intent mIntent;
    private String registIndexShopId="";
    private String expressNo="";
    private String registAreaId="";
    private UserInfo mUserInfo;
    private ArrayList<AreaItem> provinceOptions = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<AreaItem>>> districtOptions = new ArrayList<>();
    private ArrayList<ArrayList<AreaItem>> cityOptions = new ArrayList<>();
    private OptionsPickerView pvOptions;
    public static final String USER_NAME_TITLE="username";
    public static final String LOGIN_PWD_TITLE="login_pwd";
    private String username;
    private LoginUserInfo requestParams;
    private String loginPwd;
    private String verifyCode;
    public static final String VERIFY_CODE_NAME="VERIFY_CODE_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra(FROM_WHERE_NAME)){
            fromWhere=getIntent().getStringExtra(FROM_WHERE_NAME);
        }
        if(REGISTR_TYPE.equals(fromWhere)){
            loginPwd=getIntent().getStringExtra(LOGIN_PWD_TITLE);
        }else{
            mUserInfo= SkuaidiSpf.getLoginUser();
        }
        setContentView(R.layout.activity_complete_user_info);
        verifyCode=getIntent().getStringExtra(VERIFY_CODE_NAME);
        username=getIntent().getStringExtra(USER_NAME_TITLE);
        initView();
        if(REGISTR_TYPE.equals(fromWhere)){
            getOriginInfo();
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

    private void getOriginInfo(){
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.getOriginInfo(username).subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                if(jsonObject!=null){
                    et_real_name.setText(jsonObject.getString("real_name"));
                    et_real_name.setSelection(et_real_name.getText().toString().length());
                    tv_select_company.setText(ExpressFirm.expressNoToFirm(jsonObject.getString("brand")));
                    tv_select_area.setText(jsonObject.getString("area"));
                    expressNo=jsonObject.getString("brand");
                    tv_select_wangdian.setText(jsonObject.getString("company_name"));
                    registIndexShopId=jsonObject.getString("index_shop_id");
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void initView(){
        tvDes.setText(REGISTR_TYPE.equals(fromWhere)?"注册":"修改个人信息");
        if(RegisterOrModifyInfoActivity.MODIFY_TYPE.equals(fromWhere)){
            ll_warn_parent.setVisibility(View.VISIBLE);
            btn_complete.setText("保存");
        }
        if(mUserInfo!=null){
            et_real_name.setText(mUserInfo.getUserName());
            et_real_name.setSelection(et_real_name.getText().toString().length());
            tv_select_company.setText(mUserInfo.getExpressFirm());
            tv_select_area.setText(mUserInfo.getArea());
            tv_select_wangdian.setText(mUserInfo.getBranch());
            expressNo=mUserInfo.getExpressNo();
            AreaItem areaItem=AddressDB.getAreafromNames(mUserInfo.getArea().replaceAll("-", ","));
            if(areaItem!=null){
                registAreaId = areaItem.getId();
            }
        }
        et_real_name.addTextChangedListener(myTextWatcher);
        tv_select_company.addTextChangedListener(myTextWatcher);
        tv_select_area.addTextChangedListener(myTextWatcher);
        tv_select_wangdian.addTextChangedListener(myTextWatcher);
        et_select_wangdian.addTextChangedListener(myTextWatcher);
        updateButtonStatus();
        tv_select_area.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)et_real_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et_real_name, 0);
            }
        },800);

        tv_select_area.postDelayed(new Runnable() {
            @Override
            public void run() {
                initAddressData();
            }
        },2000);
    }

    private void initAddressData(){
        List<AreaItem> pros = AddressDB.getAllProInfoStrs();
        for (int i = 0; i < pros.size(); i++) {
            provinceOptions.add(pros.get(i));
            ArrayList<AreaItem> citys = (ArrayList<AreaItem>)AddressDB.getCityInfoStr(pros.get(i).getId());
            cityOptions.add(citys);
            ArrayList<ArrayList<AreaItem>> tempList=new ArrayList<>();
            for (int j = 0; j < citys.size(); j++) {
                ArrayList<AreaItem> mDistricts = (ArrayList<AreaItem>)AddressDB.getCityInfoStr(citys.get(j).getId());
                tempList.add(mDistricts);
            }
            districtOptions.add(tempList);
        }
        initPickerView();
    }

    private void initPickerView(){
        pvOptions = new OptionsPickerView(this);
        pvOptions.setPicker(provinceOptions, cityOptions, districtOptions, true);
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                AreaItem a = provinceOptions.get(options1);
                AreaItem b = cityOptions.get(options1).get(option2);
                AreaItem c = districtOptions.get(options1).get(option2).get(options3);
                String names=a.getName()+"-"+b.getName()+"-"+c.getName();
                tv_select_area.setText(names);
                AreaItem areaItem=AddressDB.getAreafromNames(names.replaceAll("-",","));
                if(areaItem!=null){
                    registAreaId = areaItem.getId();
                    KLog.i("kb","registAreaId:--->"+registAreaId);
                }
            }
        });
        pvOptions.setCancelable(true);
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
        btn_complete.setEnabled(canNext());
    }

    private boolean canNext(){
        if(!TextUtils.isEmpty(et_real_name.getText().toString().trim()) && !TextUtils.isEmpty(tv_select_company.getText().toString())
                && !TextUtils.isEmpty(tv_select_area.getText().toString()) &&
                (et_select_wangdian.getVisibility()==View.VISIBLE ? !TextUtils.isEmpty(et_select_wangdian.getText().toString().trim()):!TextUtils.isEmpty(tv_select_wangdian.getText().toString()))){
            return true;
        }
        return false;
    }

    @OnClick({R.id.iv_title_back,R.id.btn_complete,R.id.rl_choose_area,R.id.rl_choose_shop_name,
              R.id.rl_choose_wangdian})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_title_back:
                if(REGISTR_TYPE.equals(fromWhere)){
                    Intent intent=new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.rl_choose_shop_name:
                mIntent = new Intent(this, ExpressFirmActivity2.class);
                startActivityForResult(mIntent, Constants.REQUEST_CHOOSE_EXPRESSFIRM);
                break;
            case R.id.rl_choose_area:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if(pvOptions!=null && !pvOptions.isShowing()){
                    pvOptions.show();
                }
                break;
            case R.id.rl_choose_wangdian:
                mIntent = new Intent(this, SelectBranchActivity.class);
                mIntent.putExtra(SelectBranchActivity.BRAND_TITLE, expressNo);
                mIntent.putExtra(SelectBranchActivity.AREA_ID_TITLE, registAreaId);
                mIntent.putExtra(SelectBranchActivity.AREA_TITLE,tv_select_area.getText().toString());
                startActivityForResult(mIntent,SelectBranchActivity.REQUEST_INTENT_BRANCH);
                break;
            case R.id.btn_complete:
                updateUserInfo();
                break;
        }
    }

    private void updateUserInfo(){
        showProgressDialog("");//this,"提交中....");
        requestParams=new LoginUserInfo();
        requestParams.setRealname(et_real_name.getText().toString().trim());
        requestParams.setArea(tv_select_area.getText().toString());
        requestParams.setBrand(expressNo);
        if(et_select_wangdian.getVisibility()==View.VISIBLE){
            requestParams.setShop_name(et_select_wangdian.getText().toString().trim());
        }else{
            requestParams.setIndex_shop_name(tv_select_wangdian.getText().toString().trim());
            requestParams.setIndex_shop_id(registIndexShopId);
        }
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription;
        if(REGISTR_TYPE.equals(fromWhere)){
            subscription=apiWrapper.userRegister(requestParams.buildRegisterParams(username,loginPwd,verifyCode))
                    .subscribe(newSubscriber(new Action1<LoginUserInfo>() {
                        @Override
                        public void call(LoginUserInfo loginUserInfo) {
                            handlerSuccessResult(loginUserInfo);
                        }
                    }));
        }else{
            subscription=apiWrapper.modifyUserInfo(requestParams.buildModifyParams(username,mUserInfo==null? "":mUserInfo.getUserId(),verifyCode))
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonObject) {
                            handlerSuccessResult(null);
                        }
                    }));
        }
        mCompositeSubscription.add(subscription);
    }

    private void handlerSuccessResult(LoginUserInfo userInfo){
        if(REGISTR_TYPE.equals(fromWhere)){
            SkuaidiSpf.setSessionId(TextUtils.isEmpty(userInfo.getSession_id())?"":userInfo.getSession_id());
            userInfo.setPhoneNumber(username);
            userInfo.setPassword(loginPwd);
            BaseRxHttpUtil.changeLoginUserInfo(userInfo);
            AccountUtils.insertOrUpdateLoginAccount();
            mIntent = new Intent(CompleteUserInfoActivity.this, RegisterSuccessActivity.class);
            mIntent.putExtra(FROM_WHERE_NAME,fromWhere);
            startActivity(mIntent);
            finish();
        }else{
            requestParams.setPhoneNumber(username);
            requestParams.setPassword(mUserInfo.getPwd());
            requestParams.setUser_id(mUserInfo.getUserId());
            requestParams.setCodeId(mUserInfo.getCodeId());
            BaseRxHttpUtil.changeLoginUserInfo(requestParams);
            AccountUtils.insertOrUpdateLoginAccount();
            ToastHelper.showToast(getApplicationContext(),"保存成功");
            setStatusBar();
            EventBus.getDefault().post(REAL_NAME_COMPLETE);
            btn_complete.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },2000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHOOSE_EXPRESSFIRM && resultCode == Constants.RESULT_CHOOSE_EXPRESSFIRM) {
            MyExpressBrandEntry myExpressBrandEntry = (MyExpressBrandEntry) data.getSerializableExtra("myexpressbrand");
            if(myExpressBrandEntry!=null){
                tv_select_company.setText(myExpressBrandEntry.getExpressName());
                expressNo=myExpressBrandEntry.getExpressCode();
            }
        }else if (requestCode == SelectBranchActivity.REQUEST_INTENT_BRANCH && resultCode == RESULT_OK) {
            String registBranch = data.getStringExtra("branch_name");
            registIndexShopId = data.getStringExtra("index_shop_id");
            if (registBranch.equals("其他网点")) {
                ll_select_wangdian.setVisibility(View.GONE);
                et_select_wangdian.setVisibility(View.VISIBLE);
                et_select_wangdian.setText("");
                et_select_wangdian.setFocusable(true);
                et_select_wangdian.requestFocus();
            } else {
                ll_select_wangdian.setVisibility(View.VISIBLE);
                et_select_wangdian.setVisibility(View.GONE);
                tv_select_wangdian.setText(registBranch);
            }
        }
    }
}
