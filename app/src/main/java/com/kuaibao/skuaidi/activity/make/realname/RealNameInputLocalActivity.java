package com.kuaibao.skuaidi.activity.make.realname;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.make.realname.AsyncTask.IDCardImgAsyncTask;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.RealNameInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.readidcard.PreviewActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.yunmai.android.vo.IDCard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 顾冬冬
 * 原生 - 实名寄递【非中通，申通】
 */
public class RealNameInputLocalActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_more)
    SkuaidiTextView tvMore;
    @BindView(R.id.et_order)
    EditText etOrder;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_gather_status)
    TextView tvGatherStatus;
    @BindView(R.id.iv_gather)
    ImageView ivGather;
    @BindView(R.id.tv_next)
    TextView tvNext;

    private RealNameInfo realNameInfoObj;
    private Subscription subscription;
    private ApiWrapper apiWrapper;
    private String imageBit2Str;
    private String order;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_input_local);
        context = this;
        EventBus.getDefault().register(this);
        apiWrapper = new ApiWrapper();
        initView();
    }

    private void initView() {
        tvTitleDes.setText("实名寄递");
        tvMore.setText("寄递记录");
        tvNext.setText("提交");
    }

    @OnClick({R.id.iv_title_back, R.id.iv_scan, R.id.iv_gather, R.id.tv_next,R.id.tv_more})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.iv_scan:// 扫码
                intent = new Intent(this, CaptureActivity.class);
                intent.putExtra("isContinuous", false);
                intent.putExtra("qrcodetype", Constants.TYPE_KEEP_ACCOUNTS);
                intent.putExtra("from", "keep_accounts");// 来自记账
                startActivityForResult(intent, Constants.TYPE_SCAN_ORDER_REQUEST);
                break;
            case R.id.iv_gather:// 身份证识别
                cameraIdentifyIdCard();

                break;
            case R.id.tv_next:// 提交
                UMShareManager.onEvent(this,"RealName_Commit","RealNameInputLocalActivity","实名寄递：提交按钮");
                if (TextUtils.isEmpty(etOrder.getText().toString())){
                    UtilToolkit.showToast("请填写运单号");
                }else if(TextUtils.isEmpty(tvGatherStatus.getText().toString())){
                    UtilToolkit.showToast("请采集身份信息");
                }else{
                    getServiceIDCardImgUrl();
                }
                break;
            case R.id.tv_more:// 寄递记录
                intent = new Intent(this, RealNameRecordActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**保存实名寄递身份证识别信息**/
    private void idCardSaveInfo(final String image){
        showProgressDialog("");//this,"请稍候...");
        Map<String, String> realNameInfo_map = new HashMap<>();
        realNameInfo_map.put("name", realNameInfoObj.getName());
        realNameInfo_map.put("sex", realNameInfoObj.getSex());
        realNameInfo_map.put("nation", realNameInfoObj.getNation());
        realNameInfo_map.put("born", realNameInfoObj.getBorn());
        realNameInfo_map.put("address", realNameInfoObj.getAddress());
        realNameInfo_map.put("idno", realNameInfoObj.getIdno());
        realNameInfo_map.put("portrait","");
        realNameInfo_map.put("profile",image);
        realNameInfo_map.put("weixin_openid","");
        realNameInfo_map.put("alipay_openid","");
        realNameInfo_map.put("source","app_s_android");

        subscription = apiWrapper.saveIdCardInfo(realNameInfo_map)
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonObject) {
                        billDetailIndex(image);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    /**保存身份证识别后照片并返回照片在服务器中地址**/
    private void getServiceIDCardImgUrl(){
        showProgressDialog("");//this,"请稍候...");
        subscription = apiWrapper.uploadImgData(imageBit2Str,".jpg").subscribe(newSubscriber(new Action1<JSONObject>() {

            @Override
            public void call(JSONObject jsonData) {
                if(jsonData!=null && !TextUtils.isEmpty(jsonData.getString("src"))){
                    idCardSaveInfo(jsonData.getString("src"));
                }else{
                    UtilToolkit.showToast("提交失败,请重试");
                }
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    /**保存实名寄递记录**/
    private void billDetailIndex(String image){
        showProgressDialog("");//this,"请稍候...");
        Map<String,String> params = new HashMap<>();
        params.put("waybill_no",etOrder.getText().toString());// 运单号
        params.put("scan_time",Utility.getSMSCurTime2());// 扫描时间
        params.put("money","");// 包裹价格
        params.put("idno",realNameInfoObj.getIdno());// 身份证号
        params.put("name",realNameInfoObj.getName());// 寄件用户姓名
        params.put("address",realNameInfoObj.getAddress());// 住址
        params.put("latitude","");// 投递包裹时纬度
        params.put("longitude","");// 投递包裹时经度
        params.put("image",image);// 包裹图片地址
        params.put("order_no","");// 收款订单号
        params.put("version","1.0");

        subscription = apiWrapper.billDetailIndex(params).subscribe(newSubscriber(new Action1<String>() {
            @Override
            public void call(String jsonObject) {
                CollectionRecords colRecords = new CollectionRecords();
                colRecords.setTran_msg(realNameInfoObj.getName());
                colRecords.setOrder_number(etOrder.getText().toString());
                colRecords.setAvail_time(Utility.getSMSCurTime2());
                colRecords.setDesc("已采集");
                colRecords.setStatus(0);
                Intent mIntent = new Intent(context,CollectionAccountDetailActivity.class);
                mIntent.putExtra("detailInfo", colRecords);
                startActivity(mIntent);
                finish();
            }
        }));
        mCompositeSubscription.add(subscription);

    }

    //相机识别身份证
    private void cameraIdentifyIdCard() {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("type", false);
        intent.putExtra("box", true);
        startActivityForResult(intent, Constants.CAMREA_IDENTIFY_IDCARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.TYPE_SCAN_ORDER_REQUEST && resultCode == Constants.TYPE_SCAN_ORDER_RESULT) {
            List<NotifyInfo> mList = (List<NotifyInfo>) data.getSerializableExtra("express_list");
            if (null != mList && 0 != mList.size()) {
                order = mList.get(0).getExpress_number();
                etOrder.setText(order);
//                Intent intent = new Intent(this, AccountingActivity.class);
//                intent.putExtra("mList", (Serializable) mList);
//                startActivity(intent);
            }else{
                order = data.getStringExtra("express_no");
                etOrder.setText(order);
            }
            etOrder.setSelection(etOrder.getText().length());
        } else if (requestCode == Constants.CAMREA_IDENTIFY_IDCARD && resultCode == RESULT_OK) {
            if (null != data) {
                IDCard idCard = (IDCard) data.getSerializableExtra("Data");
                String cardFolder;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    cardFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/pic/realname";
                } else {
                    cardFolder = this.getFilesDir().getAbsolutePath();
                }
                File outImgFile = new File(cardFolder, "idCard.jpg");
                KLog.i("TAG", "fdl--idCard =" + idCard.toString());
                IDCardImgAsyncTask mTask = new IDCardImgAsyncTask(RealNameInputLocalActivity.this,idCard, outImgFile);
                mTask.execute();
            }

        }
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        if (event.type == Constants.EVENT_BUS_TYPE_10005) {
            realNameInfoObj = (RealNameInfo) event.getIntent().getSerializableExtra("realNameInfoObj");
            imageBit2Str = event.getIntent().getStringExtra("imageStr");
            tvGatherStatus.setText("已采集");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
