package com.kuaibao.skuaidi.dispatch.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dispatch.bean.AliWXImgInfo;
import com.kuaibao.skuaidi.dispatch.bean.RequestScanSignDto;
import com.kuaibao.skuaidi.dispatch.bean.ResponseSignState;
import com.kuaibao.skuaidi.dispatch.bean.ScanResult;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.main.constant.IAMapLocation;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class SignWithScanActivity extends RxRetrofitBaseActivity {
    public static final String NOTIFY_INIFO_NAME="notify_list";
    private List<NotifyInfo> infoList;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_hint_alipay)
    TextView tv_hint_alipay;
    @BindView(R.id.tv_hint_WeChat)
    TextView tv_hint_WeChat;
    @BindView(R.id.iv_alipay)
    ImageView iv_alipay;
    @BindView(R.id.iv_WeChat)
    ImageView iv_wechat;
    @BindView(R.id.tv_scan_expressNo)
    TextView scanNo;
    @BindView(R.id.tv_scan_state)
    TextView noState;
    private String signType="1";
    @BindView(R.id.ll_expressNo_parent)
    LinearLayout ll_expressNo_parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_with_scan);
        if(getIntent().hasExtra(NOTIFY_INIFO_NAME)){
            infoList= (List<NotifyInfo>) (getIntent().getSerializableExtra(NOTIFY_INIFO_NAME));
            if(infoList==null || infoList.size()==0) {
                UtilToolkit.showToast("数据错误，请返回重试");
                return;
            }
            this.signType=infoList.size()==1?"1":"2";
        }
        initView();
        getStoZTAliAndWxImg();
    }
    private void setSignState(String state){
        noState.setText(state);
    }

    private void initView(){
        tvTitleDes.setText("1".equals(this.signType)?"签收":"批量签收");
        if("1".equals(this.signType)){
            ll_expressNo_parent.setVisibility(View.VISIBLE);
            if(infoList.get(0)!=null && !TextUtils.isEmpty(infoList.get(0).getExpress_number())) scanNo.setText(infoList.get(0).getExpress_number());
        }else{
            ll_expressNo_parent.setVisibility(View.GONE);
        }
        SpannableStringBuilder builder= new SpannableStringBuilder("客户使用 支付宝 扫描，进行签收");
        builder.setSpan(new ForegroundColorSpan(Color.rgb(29, 126, 221)), 5,  8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_hint_alipay.setText(builder);

        SpannableStringBuilder builder2= new SpannableStringBuilder("客户使用 微信 扫描，进行签收");
        builder2.setSpan(new ForegroundColorSpan(Color.rgb(2, 179, 0)), 5,  7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_hint_WeChat.setText(builder2);
    }

    private void getStoZTAliAndWxImg(){
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.getAliAndWXImg2(SKuaidiApplication.VERSION_CODE+"")
                .subscribe(newSubscriber(new Action1<AliWXImgInfo>() {
                    @Override
                    public void call(AliWXImgInfo imgInfo) {
                        if(!TextUtils.isEmpty(imgInfo.getAlipay())){
                            GlideUtil.GlideUrlToImg(SignWithScanActivity.this,imgInfo.getAlipay(),iv_alipay);
                        }
                        if(!TextUtils.isEmpty(imgInfo.getWeixin())){
                            GlideUtil.GlideUrlToImg(SignWithScanActivity.this,imgInfo.getWeixin(),iv_wechat);
                        }
                        if(!TextUtils.isEmpty(imgInfo.getScene_id())){
                            getStoAutoSignResult(imgInfo.getScene_id());
                        }else{
                            UtilToolkit.showToast("扫码唯一标识为空,请退出页面尝试重新生成");
                        }
                        setAliWeChatTextVisible(imgInfo.getAlipay(),imgInfo.getWeixin());
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void setAliWeChatTextVisible(String aliUrl,String weChatUrl){
        if(TextUtils.isEmpty(aliUrl)){
            tv_hint_alipay.setVisibility(View.INVISIBLE);
        }
        if(TextUtils.isEmpty(weChatUrl)){
            tv_hint_WeChat.setVisibility(View.INVISIBLE);
        }
    }

    private void getStoAutoSignResult(final String scene_id){
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription mySubscription = wrapper.getSignState(scene_id)
                .subscribe(newSubscriber(new Action1<ResponseSignState>() {
                    @Override
                    public void call(final ResponseSignState signState) {
                        if("init".equals(signState.getScanStatus())){//未扫码
                            getWindow().getDecorView().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if("1".equals(signType)){
                                        setSignState("等待扫码...");
                                    }
                                    getStoAutoSignResult(scene_id);
                                }
                            },3000);
                        }else if("invalid".equals(signState.getScanStatus())){
                            getWindow().getDecorView().post(new Runnable() {
                                @Override
                                public void run() {
                                    if("1".equals(signType)){
                                        setSignState("二维码失效");
                                    }
                                    ToastHelper.makeText(getApplicationContext(),"二维码已失效,请退出重新生成", ToastHelper.LENGTH_LONG).show();
                                }
                            });
                        }else if("finish".equals(signState.getScanStatus())){
                            getWindow().getDecorView().post(new Runnable() {
                                @Override
                                public void run() {
                                    if("1".equals(signType)){
                                        setSignState("扫码成功");
                                    }
                                    ToastHelper.makeText(getApplicationContext(),"扫码成功,请继续操作完成签收", ToastHelper.LENGTH_LONG).show();
                                    UMShareManager.onEvent(SKuaidiApplication.getContext(), "sign_with_scan", "扫码签收", "1");
                                    autoUploadStoWaybills(signState);
                                }
                            });
                        }
                    }
                }));
        mCompositeSubscription.add(mySubscription);
    }

    private void autoUploadStoWaybills(final ResponseSignState responseSignState){
        showProgressDialog("");//SignWithScanActivity.this,"正在签收,请稍等...");
        RequestScanSignDto requestScanSignDto=new RequestScanSignDto();
        requestScanSignDto.setWayBillType("3");
        requestScanSignDto.setSceneId(responseSignState.getSceneId());
        JSONArray wayBillDataArray=new JSONArray();
        final String scanTime=E3SysManager.getTimeBrandIndentify();
        LatitudeAndLongitude latitudeAndLongitude=SkuaidiSpf.getLatitudeOrLongitude(getApplicationContext());
        for(NotifyInfo notifyInfo:infoList){
            RequestScanSignDto.WayBillData wayBillData=new RequestScanSignDto.WayBillData();
            wayBillData.setWaybillNo(notifyInfo.getExpress_number());
            wayBillData.setScan_time(scanTime);
            wayBillData.setSignType("扫码签收");
            wayBillData.setLocation(latitudeAndLongitude);
            wayBillDataArray.add(JSON.toJSON(wayBillData));
        }
        requestScanSignDto.setWayBillDatas(wayBillDataArray);
        Map<String,Object> uploadParams=requestScanSignDto.buildUploadParams();
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription = apiWrapper.uploadScanSignWaybills((String)uploadParams.get("sname"),uploadParams).subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonData) {
                int code=jsonData.getIntValue("code");
                if(code!=0){
                    showFailInfoDialog(jsonData.getString("desc"));
                }else{
                    if(jsonData.containsKey("result")){
                        JSONObject jsonResult=jsonData.getJSONObject("result");
                        if(jsonResult.containsKey("error")){
                            JSONArray jsonArray=jsonResult.getJSONArray("error");
                            if(jsonArray!=null && jsonArray.size()>0){
                                StringBuilder sb=new StringBuilder();
                                for(int i=0;i<jsonArray.size();i++){
                                    JSONObject waybill =jsonArray.getJSONObject(i);
                                    String waybillNo=waybill.getString("waybillNo");
                                    String reason=waybill.getString("reason");
                                    sb.append(waybillNo+",").append(reason+";");
                                }
                                showFailInfoDialog(sb.toString());
                            }else{
                                showSucessResult(responseSignState,scanTime);
                            }
                        }else{
                            showSucessResult(responseSignState,scanTime);
                        }
                    }else{
                        ToastHelper.makeText(SignWithScanActivity.this,"签收失败",ToastHelper.LENGTH_LONG).show();
                    }
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }


    private void showSucessResult(final ResponseSignState responseSignState,String scanTime){
        ToastHelper.makeText(SignWithScanActivity.this,"签收成功",ToastHelper.LENGTH_LONG).show();
        final ScanResult scanResult=new ScanResult();
        List<String> waybillNums=new ArrayList<>();
        for(NotifyInfo notifyInfo:infoList){
            waybillNums.add(notifyInfo.getExpress_number());
        }
        scanResult.setWaybillNums(waybillNums);
        scanResult.setNickName(responseSignState.getThirdNickName());
        scanResult.setPhone(responseSignState.getThirdAccount());
        scanResult.setSignType("扫码签收");
        IAMapLocation iaMapLocation=(IAMapLocation)ACache.get(this).getAsObject("amapLocation");
        scanResult.setAddress(iaMapLocation==null? "":iaMapLocation.getAddress());
        scanResult.setSignTime(scanTime);
        scanResult.setScanPlatform(responseSignState.getPlatform());
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SignWithScanActivity.this,ShowSignResultActivity.class);
                intent.putExtra(ShowSignResultActivity.SIGN_RESULT,scanResult);
                startActivity(intent);
                finish();
            }
        },2000);
    }

    private void showFailInfoDialog(String msg){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle("温馨提示");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @OnClick({R.id.iv_title_back})
    public void onClick(View view) {
        switch (view.getId()) {
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
