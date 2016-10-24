package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dispatch.activity.adapter.QuickAdapter;
import com.kuaibao.skuaidi.dispatch.activity.helper.ETHelperActivity;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ZTSignActivity1 extends ETHelperActivity implements ETHelperActivity.GetPhoneListenr,
        ETHelperActivity.UploadExpressDataListener, ETHelperActivity.SendSMSAfterUploadListener,ETHelperActivity.GetPhoneStateListener {
    public static final String INFORM_BYDH_SEND1 = "inform_bydh.send1";//查询单号是否存在对应的手机号

    //选择模板
    public static final int REQUEST_CODE_CHOOSE_TEMPLATE = 102;
    public static final int REQUEST_CODE_INPUT_PHONENUMBER = 103;
    private static final String orderdh = "#DHDHDHDHDH#";
    private static final String model_url = "#SURLSURLSURLSURLS#";
    public static final String PIC_WAY_NAME = "picWayBills";
    public static final String SIGN_TYPE_NAME = "ztSignType";
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    ToggleButton tbSmsNotify;//短信通知开关

    private List<NotifyInfo> infoList = new ArrayList<NotifyInfo>();
    private ReplyModel mReplyModel;
    private String signType = "";
    private String courierNO;
    private QuickAdapter mQuickAdapter;
    private TextView tvTemplateName;

    /**
     * 包含图片的签收件
     */
    private List<NotifyInfo> picSignInfos = new ArrayList<NotifyInfo>();
    private ArrayList<String> picPathList = new ArrayList<String>();// 所有单号对应的图片地址
    private static final int MAX_SIGNED_PIC = 3;//图片签收上传，一次最多包含三张互不相同的照片
    /**
     * 是否包含图片签收
     */
    private boolean signed_pic = false;
    public  static final String scanType = "扫签收";

    private JSONArray failDh;//没有手机号的单号
    private JSONArray passDh;//有手机号的单号
    private List<NumberPhonePair> pairs;
    private String ident;
    private String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zt_sign);
        infoList = (List<NotifyInfo>) getIntent().getSerializableExtra(PIC_WAY_NAME);
        signType = getIntent().getStringExtra(SIGN_TYPE_NAME);
        courierNO = getIntent().getStringExtra("courierNO");//工号
        initView();
        setGetPhoneListenr(this);
        setUploadDataListener(this);
        getPhoneByTradeNo(new ArrayList<String>()); //调用父类封装的根据单号获取手机号公共方法

    }

    private void initView() {
        tvTitleDes.setText("single".equals(signType) ? "签收" : "批量签收");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        mQuickAdapter.addFooterView(getView());
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin),
                        getResources().getDimensionPixelSize(R.dimen.recyle_divider_rightmargin))
                .build());  //添加分割线
        mRecyclerView.setAdapter(mQuickAdapter);
    }

    private View getView() {
        final View view = getLayoutInflater().inflate(R.layout.zt_sign_footerview, null);
        view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tvTemplateName = (TextView) view.findViewById(R.id.tv_template_name);
        tbSmsNotify = (ToggleButton) view.findViewById(R.id.tb_sms_notify);
        tbSmsNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.findViewById(R.id.rl_sms_template).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if(isChecked)
                    getPhoneState();
            }
        });
        view.findViewById(R.id.rl_sms_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择模板
                Intent intent1 = new Intent(ZTSignActivity1.this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_TEMPLATE);
            }
        });
        //添加阴影效果
//        ShadowProperty shadowProperty = new ShadowProperty()
//                .setShadowColor(0x77000000)
//                .setShadowDy(DisplayUtil.dip2px(0.5f));
//                .setShadowRadius(DisplayUtil.dip2px(3f));
//        ShadowViewHelper.bindShadowHelper(shadowProperty, view);
//        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) view.getLayoutParams();
//        lp.leftMargin = -shadowProperty.getShadowOffset();
//        lp.rightMargin = -shadowProperty.getShadowOffset();
//        lp.topMargin=-shadowProperty.getShadowOffset()/2;
//        view.setLayoutParams(lp);
        return view;
    }

    private void initAdapter() {
        mQuickAdapter = new QuickAdapter(this, infoList);
        //mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {//点击进入预览界面

            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.ll_save, R.id.ll_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.ll_save:
                save();
                break;
            case R.id.ll_upload:
                if (tbSmsNotify.isChecked()) {//发送短信
                    if (failDh != null && failDh.length() > 0) {//部分单号没有对应的手机号
                        Intent intent2 = new Intent();
                        if (infoList.size() > 1) {//批量签收,添加手机号
                            intent2.setClass(this, AddMultiplePhoneNumberActivity.class);
                            intent2.putExtra("count", infoList.size());
                        } else {
                            intent2.setClass(this, AddSinglePhoneNumberActivity.class);//单个添加手机号
                        }
                        intent2.putExtra("numbers", failDh.toString());
                        startActivityForResult(intent2, REQUEST_CODE_INPUT_PHONENUMBER);
                    } else {//所有单号都能查到对应的手机号
                        upload();
                    }
                } else {
                    upload();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_TEMPLATE) {//选择短信模板
            if (data != null && data.hasExtra("modelObject")) {
                mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
                if (null != mReplyModel) {
                    tvTemplateName.setText(mReplyModel.getTitle());
                    String str = mReplyModel.getModelContent();
                    if (str.contains("#DH#")) {
                        str = str.replaceAll("#DH#", orderdh);
                    }
                    if (str.contains("#SURL#")) {
                        str = str.replace("#SURL#", model_url);
                    }
                } else {
                    ToastHelper.makeText(getApplicationContext(), "选择模板有误！", ToastHelper.LENGTH_LONG).setAnimation(R.style.popUpWindowEnterExit).show();
                }
            }
        }
    }

    @Override
    public void onGetPhoneSuccess(List<String> phones) {

    }

    @Override
    public void onGetPhoneFail(int code, String msg) {

    }


    /**
     * 检查单号是否存在对应的手机号
     */
    private void getPhoneState() {
        JSONObject data = new JSONObject();
        String[] numbers = {"3307406640890", "868554459901"};
        try {
            data.put("dhs", JSON.toJSON(numbers));
            data.put("sname", INFORM_BYDH_SEND1);
//            getPhoneState(JSON.toJSON(numbers).toString());
//            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param notifyInfos
     * @param isUpload
     * @param isCache
     * @return
     */
    public ArrayList<E3_order> infoToOrder(List<NotifyInfo> notifyInfos, int isUpload, int isCache) {
        ArrayList<E3_order> orders = new ArrayList<>();
        for (int i = 0; i < notifyInfos.size(); i++) {

            E3_order order = new E3_order();
            order.setOrder_number(notifyInfos.get(i).getExpress_number());
            order.setType_extra(notifyInfos.get(i).getWayBillTypeForE3());
            order.setFirmname(company);
            order.setType("签收件");
            if (!TextUtils.isEmpty(notifyInfos.get(i).getPicPath())) {
                order.setPicPath(notifyInfos.get(i).getPicPath());
                order.setWayBillType_E3("图片签收");
                order.setType_extra("图片签收");// 两个字段表示同一意思
            } else {
                order.setWayBillType_E3(notifyInfos.get(i).getWayBillTypeForE3());
                order.setType_extra(notifyInfos.get(i).getWayBillTypeForE3());
            }
            order.setScan_time(notifyInfos.get(i).getScanTime());
            order.setCompany(company);
            order.setCourier_job_no(courierNO);
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            order.setLatitude(notifyInfos.get(i).getLatitude());
            order.setLongitude(notifyInfos.get(i).getLongitude());
            orders.add(order);

        }
        return orders;
    }



    @Override
    public void onUploadSuccess(com.alibaba.fastjson.JSONObject result) {
        {
            if (signed_pic) {// 图片签收
                for (int i = 0; i < picSignInfos.size(); i++) {
                    if (picPathList.size() == 1) {
                        E3SysManager.deletePic(picSignInfos.get(i).getPicPath());
                    } else {
                        picPathList.remove(picSignInfos.get(i).getPicPath());
                        if (!picPathList.contains(picSignInfos.get(i).getPicPath())) {
                            // 上传成功，删除图片
                            E3SysManager.deletePic(picSignInfos.get(i).getPicPath());
                        }
                    }
                    E3OrderDAO.addOrders(infoToOrder(Arrays.asList(picSignInfos.get(i)), 1, 0), company, courierNO);
                    infoList.remove(picSignInfos.get(i));
                }

                if (infoList.size() > 0) {
                    picSignInfos.clear();
                    upload();
                    return;// 这里返回是为了不执行取消progressDialog 的逻辑
                }
            } else {
                // 上传成功后保存在数据库，当天去重检查用。
                E3OrderDAO.addOrders(infoToOrder(infoList, 1, 0), company, courierNO);
            }
            if (infoList.size() == 0 && tbSmsNotify.isChecked()) {//单号全部上传成功，并且发短信
                sendSMSAfterUpload();
            }
        }
    }

    @Override
    public void onUploadFail(int code, String msg) {

    }

    /**
     * 发短信
     */
    private void sendSMSAfterUpload() {
        JSONArray array=new JSONArray(pairs);
//        sendSMSAfterUpload(ident, mReplyModel.getTid(), array);
    }

    @Override
    public void onSendSMSSuccess(com.alibaba.fastjson.JSONObject result) {

    }

    @Override
    public void onSendSMSFail(int code, String msg) {

    }

    @Override
    public void onGetPhoneStateSuccess(com.alibaba.fastjson.JSONObject result) {

    }

    @Override
    public void onGetPhoneStateFail(int code, String msg) {
//        if (data_fail != null) {
//            ident = data_fail.optString("ident");
//            JSONObject dhs = data_fail.optJSONObject("dhs");
//            failDh = dhs.optJSONArray("failDh");
//            passDh = dhs.optJSONArray("passDh");
//        }
    }
}
