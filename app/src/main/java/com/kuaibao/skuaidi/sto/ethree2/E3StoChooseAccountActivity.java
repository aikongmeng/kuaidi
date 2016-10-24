package com.kuaibao.skuaidi.sto.ethree2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.CurrentE3VerifyInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysMainActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class E3StoChooseAccountActivity extends RxRetrofitBaseActivity {
    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.recycler_list)
    RecyclerView mRecyclerView;
    private ArrayList<CurrentE3VerifyInfo> data=new ArrayList<>();
    public static final String ACCOUNT_INFO_NAME="sto_account";
    public static final String TITLT_NAME="sto_account_title";
    public static final String FROM_WHERE="from_where";
    public static final String BRAND_TYPE_NAME="brand_type";
    private String brandType;
    private String title;
    @BindView(R.id.tv_show_phone)
    TextView tvShowPhoneNumber;
    private E3StoAccountAdapter mE3StoAccountAdapter;
    private String fromWhere;
    @BindView(R.id.tv_e3account_desc)
    TextView tvE3Desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e3_sto_choose_account);
        title=getIntent().getStringExtra(TITLT_NAME);
        fromWhere=getIntent().getStringExtra(FROM_WHERE);
        brandType=getIntent().getStringExtra(BRAND_TYPE_NAME);
        if(!"e3MainActivity".equals(fromWhere)){
            data=getIntent().getParcelableArrayListExtra(ACCOUNT_INFO_NAME);
            initView();
            setData();
        }else{
            initView();
            getData();
        }
    }
    private void getData(){
        if(TextUtils.isEmpty(brandType)){
            UtilToolkit.showToast("brandType为空");
            return;
        }
        showProgressDialog("");//E3StoChooseAccountActivity.this,"获取账号...");
        Map<String,String> params= BuildParams.buildE3Params(getApplicationContext(),"getinfo");
        params.put("reapply","1");
        final ApiWrapper apiWrapper=new ApiWrapper();
        if("sto".equals(brandType)){
            Subscription mSubscription=apiWrapper.getStoVerifyInfo(params.get("sname"),params)
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                            String status=verifyInfo.getString("status");
                            if(!"success".equals(status)){
                                showVerifiedDialog("温馨提示",verifyInfo.getString("desc"),"error","");
                                return;
                            }
                            com.alibaba.fastjson.JSONObject jsonResult=verifyInfo.getJSONObject("result");
                            if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
                                UtilToolkit.showToast("服务器繁忙,请稍后重试");
                                return;
                            }
                            JSONArray jsonArray = jsonResult.getJSONArray("retArr");
                            if (jsonArray != null && jsonArray.size()>0) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    CurrentE3VerifyInfo currentE3VerifyInfo = JSON.parseObject(jsonArray.getJSONObject(i).toJSONString(), CurrentE3VerifyInfo.class);
                                    currentE3VerifyInfo.setPosition(i);
                                    data.add(currentE3VerifyInfo);
                                }
                                setData();
                            }else{
                                UtilToolkit.showToast("服务器繁忙,请稍后重试");
                            }
                        }
                    }));
            mCompositeSubscription.add(mSubscription);
        }
//        else if("qf".equals(brandType)){
//            Subscription mSubscription=apiWrapper.getQFVerifyInfo(params.get("sname"),params)
//                    .subscribe(BaseRxHttpUtil.newSubscriber(new Action1<JSONObject>() {
//                        @Override
//                        public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
//                            String status=verifyInfo.getString("status");
//                            if(!"success".equals(status)){
//                                showVerifiedDialog("温馨提示",verifyInfo.getString("desc"),"error","");
//                                return;
//                            }
//                            com.alibaba.fastjson.JSONObject jsonResult=verifyInfo.getJSONObject("result");
//                            if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
//                                UtilToolkit.showToast("服务器繁忙,请稍后重试");
//                                return;
//                            }
//                            JSONObject jsonRet = jsonResult.getJSONObject("retArr");
//                            Iterator<String> keys=jsonRet.keySet().iterator();
//                            if(keys!=null){
//                                int index=0;
//                                while (keys.hasNext()){
//                                    String key=keys.next();
//                                    JSONObject jsonReviewInfo=jsonRet.getJSONObject(key);
//                                    CurrentE3VerifyInfo currentE3VerifyInfo = JSON.parseObject(jsonReviewInfo.toJSONString(), CurrentE3VerifyInfo.class);
//                                    currentE3VerifyInfo.setPosition(index);
//                                    data.add(currentE3VerifyInfo);
//                                    index++;
//                                }
//                                setData();
//                            }else{
//                                UtilToolkit.showToast("服务器繁忙,请稍后重试");
//                            }
//                        }
//                    }));
//            mCompositeSubscription.add(mSubscription);
//        }
    }
    private void showVerifiedDialog(String title,String desc,String type,final String jobNumber){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(desc);
        builder.setTitle(title);
        switch (type){
            case "error":
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
//            case "confirm":
//                builder.setPositiveButton("确认信息", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        confirmVerifiedInfo(jobNumber);
//                    }
//                });
//                builder.setNegativeButton("这不是我",new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        loadWeb(BuildParams.STO_BAQIANG, "巴枪扫描使用说明");
//                    }
//                });
//                break;
            case "success":
                builder.setCancleable(false);
                builder.setCancleOutTouch(false);
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if("e3MainActivity".equals(fromWhere)){
                            EventBus.getDefault().post(EThreeSysMainActivity.FINISH_MESSAGE);
                        }
                        finish();
                    }
                });
                break;
            case "error_act"://错误提示并带操作
                builder.setPositiveButton("稍后认证", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("如何录入", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if("sto".equals(brandType)){
                            loadWeb(BuildParams.STO_BAQIANG, "巴枪扫描使用说明");
                        }else if("qf".equals(brandType)){
                            loadWeb(BuildParams.QF_BAQIANG, "巴枪扫描使用说明");
                        }
                    }
                });
                break;
        }
        builder.create().show();
    }

    private void confirmVerifiedInfo(String jobNumber){
        showProgressDialog("");//E3StoChooseAccountActivity.this,"提交中...");
        Map<String,String> params= BuildParams.buildE3Params(getApplicationContext(),"confirm");
        params.put("confirm_code", jobNumber);
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription mSubscription=apiWrapper.getStoVerifyInfo(params.get("sname"),params)
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                        String status=verifyInfo.getString("status");
                        if(!"success".equals(status)){
                            showVerifiedDialog("温馨提示",verifyInfo.getString("desc"),"error_act","");
                            return;
                        }
                        JSONObject jsonResult=verifyInfo.getJSONObject("result");
                        if(jsonResult==null || TextUtils.isEmpty(jsonResult.toJSONString())){
                            UtilToolkit.showToast("服务器繁忙,请稍后重试");
                            return;
                        }
                        showVerifiedDialog("快递员巴枪账号确认",jsonResult.getString("retStr"),"success","");
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }
    private void initView(){
        mTvTitleDes.setText(title);
        String templateTitle="你的手机号："+SkuaidiSpf.getLoginUser().getPhoneNumber();
        SpannableStringBuilder ssb = new SpannableStringBuilder(templateTitle);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_green_2)), 6, templateTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvShowPhoneNumber.setText(ssb);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
    }
    private void setData(){
        if(data!=null){
            tvE3Desc.setText(data.size()==1?"请选择这是不是你的信息?":"请选择并确认你的信息!");
        }
        mE3StoAccountAdapter=new E3StoAccountAdapter(this,data);
        mE3StoAccountAdapter.addFooterView(getFooterView());
        mE3StoAccountAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(position==mE3StoAccountAdapter.currentCheckPosition){
                    return;
                }
                mE3StoAccountAdapter.currentCheckPosition=position;
                mE3StoAccountAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerView.setAdapter(mE3StoAccountAdapter);
    }
    private View getFooterView(){
        final View view = getLayoutInflater().inflate(R.layout.activity_e3_sto_choose_account_footer, null);
        view.findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentE3VerifyInfo currentE3VerifyInfo=data.get(mE3StoAccountAdapter.currentCheckPosition);
                if("e3MainActivity".equals(fromWhere) && E3SysManager.getCourierNO().equals(currentE3VerifyInfo.getCounterman_code())){
                    UtilToolkit.showToast("请选择与当前登录工号不一致的账号");
                    return;
                }
                confirmVerifiedInfo(currentE3VerifyInfo.getCounterman_code());
//                String desc="网点："+currentE3VerifyInfo.getShop_name()+"\n"
//                            +"工号："+currentE3VerifyInfo.getCounterman_code()+"\n"
//                        +"姓名："+currentE3VerifyInfo.getCounterman_name()+"\n"
//                        +"请确认这是不是你的信息?";
//                showVerifiedDialog("快递员巴枪账号确认",desc,"confirm",currentE3VerifyInfo.getCounterman_code());
            }
        });
        view.findViewById(R.id.bt_updateWD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("sto".equals(brandType)){
                    loadWeb(BuildParams.STO_BAQIANG, "巴枪扫描使用说明");
                }else if("qf".equals(brandType)){
                    loadWeb(BuildParams.QF_BAQIANG, "巴枪扫描使用说明");
                }
            }
        });
        return view;
    }

    @OnClick({R.id.iv_title_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
        }
    }

}
