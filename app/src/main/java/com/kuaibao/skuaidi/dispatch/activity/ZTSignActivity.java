package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.activity.adapter.QuickAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZTSignActivity extends SkuaiDiBaseActivity {
    public static final String INFORM_BYDH_SEND1 = "inform_bydh.send1";//查询单号是否存在对应的手机号
    public static final String INFORM_BYDH_SEND2 = "inform_bydh.send2";//发短信
    public static final int REQUEST_CODE_PIC_PREVIEW = 101;
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

    private List<NotifyInfo> infoList = new ArrayList<>();
    private ReplyModel mReplyModel;
    private String signType = "";
    private String courierNO;
    private QuickAdapter mQuickAdapter;
    private TextView tvTemplateName;

    /**
     * 包含图片的签收件
     */
    private List<NotifyInfo> picSignInfos = new ArrayList<>();
    private ArrayList<String> picPathList = new ArrayList<>();// 所有单号对应的图片地址
    private static final int MAX_SIGNED_PIC = 3;//图片签收上传，一次最多包含三张互不相同的照片
    /**
     * 是否包含图片签收
     */
    private boolean signed_pic = false;
    private static final String scanType = "扫签收";

    private JSONArray failDh;//没有手机号的单号
    private JSONArray passDh;//有手机号的单号
    private List<NumberPhonePair> pairs;
    private String ident;
    private String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.
    private List<String> successList = new ArrayList<>();//上传成功的单号
    private List<UploadResutl.ErrorBean> errorBeanList = new ArrayList<>();//失败的单号
    private String sourceType;
    private String sceneId="";
    public static final String SCENEID_NAME="scene_id_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zt_sign);
        ButterKnife.bind(this);
        if(getIntent().hasExtra(SCENEID_NAME)){
            sceneId=getIntent().getStringExtra(SCENEID_NAME);
        }
        infoList = (List<NotifyInfo>) getIntent().getSerializableExtra(PIC_WAY_NAME);
        sourceType = infoList.get(0).getStatus();
        signType = getIntent().getStringExtra(SIGN_TYPE_NAME);
        courierNO = E3SysManager.getCourierNO();//工号
        initView();

    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        if (TextUtils.isEmpty(result)) {
            UtilToolkit.showToast(msg);
            return;
        }
        if (E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            ResponseData data = JSON.parseObject(result, ResponseData.class);
            if (data.getCode() != 0) {
                dismissProgressDialog();//this);
                alert(data.getDesc());
                return;
            }
            UploadResutl uploadResutl = JSON.parseObject(data.getResult().toString(), UploadResutl.class);
            if (uploadResutl != null) {
                errorBeanList.clear();
                errorBeanList.addAll(uploadResutl.getError());
                if (errorBeanList != null && errorBeanList.size() != 0) {
                    for (UploadResutl.ErrorBean error : errorBeanList) {
                        for (int i = infoList.size() - 1; i >= 0; i--) {
                            if (infoList.get(i).getExpress_number().equals(error.getWaybillNo())) {
                                E3SysManager.deletePic(infoList.get(i).getPicPath());//先删图片，如果有的话
                                infoList.remove(infoList.get(i));//上传失败的数据无法处理，所以直接从列表移除

                            }
                        }
                    }
                }
                successList = uploadResutl.getSuccess();
            }

            if (signed_pic) {// 图片签收
                for (int i = 0; i < picSignInfos.size(); i++) {
                    NotifyInfo notifyInfo = picSignInfos.get(i);
                    if (successList != null && !successList.contains(notifyInfo.getExpress_number()))
                        continue;//失败的单号不处理

                    if (picPathList.size() == 1) {
                        E3SysManager.deletePic(notifyInfo.getPicPath());
                    } else {
                        picPathList.remove(notifyInfo.getPicPath());
                        if (!picPathList.contains(notifyInfo.getPicPath())) {
                            // 上传成功，删除图片
                            E3SysManager.deletePic(notifyInfo.getPicPath());
                        }
                    }
                    E3OrderDAO.addOrders(infoToOrder(Arrays.asList(notifyInfo), 1, 0), company, courierNO);
                    infoList.remove(notifyInfo);
                }

                if (infoList.size() > 0) {
                    picSignInfos.clear();
                    upload();
                    return;// 这里返回是为了不执行取消progressDialog 的逻辑
                }
            } else {
                for (int i = infoList.size() - 1; i >= 0; i--) {
                    NotifyInfo info = infoList.get(i);
                    if (!successList.contains(info.getExpress_number())) continue;//失败的单号不处理

                    // 上传成功后保存在数据库，当天去重检查用。
                    E3OrderDAO.addOrders(infoToOrder(Arrays.asList(info), 1, 0), company, courierNO);
                    E3OrderDAO.deleteCacheOrders(infoToOrder(Arrays.asList(info), 1, 1));

                    infoList.remove(info);
                }
            }
            if (infoList.size() == 0) {//单号全部上传成功，并且发短信
                if (tbSmsNotify.isChecked()) {
                    sendSMSAfterUpload();
                } else {
                    UtilToolkit.showToast("上传成功");
                    finish();
                }
            } else {
                alert(data.getDesc());

            }
        } else if (INFORM_BYDH_SEND2.equals(sname)) {
            dismissProgressDialog();//this);
            UtilToolkit.showToast("短信发送成功");
            finish();
        } else if (INFORM_BYDH_SEND1.equals(sname)) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject json = new JSONObject(result);
                    ident = json.optString("ident");
                    JSONObject dhs = json.optJSONObject("dhs");
                    failDh = dhs.optJSONArray("failDh");
                    passDh = dhs.optJSONArray("passDh");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();//this);
        if (INFORM_BYDH_SEND1.equals(sname)) {
            if (data_fail != null) {
                ident = data_fail.optString("ident");
                JSONObject dhs = data_fail.optJSONObject("dhs");
                failDh = dhs.optJSONArray("failDh");
                passDh = dhs.optJSONArray("passDh");
            }
        } else if (INFORM_BYDH_SEND2.equals(sname)) {
            dismissProgressDialog();//this);
            if ("10002".equals(code)) {

                SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(this, SkuaidiE3SysDialog.TYPE_COMMON, new View(this));
                dialog.setTitle("发送短信");
                dialog.setCommonContent(result);
                dialog.isUseSingleButton(true);
                dialog.setSingleButtonTitle("确定");
                dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

                    @Override
                    public void onClick() {
                        finish();
                    }
                });
                if (!isFinishing())
                    dialog.showDialog();
            }
        } else if (E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

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
                if (isChecked){
                    getPhoneState();
                    UMShareManager.onEvent(ZTSignActivity.this, "dispatch_sign_sms_zt", "dispatch_sign", "中通派件：签收：短信通知收件人");
                }

            }
        });
        view.findViewById(R.id.rl_sms_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择模板
                Intent intent1 = new Intent(ZTSignActivity.this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_TEMPLATE);
            }
        });
        return view;
    }

    private void initAdapter() {
        mQuickAdapter = new QuickAdapter(this, infoList);
        //mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {//点击进入预览界面
                if (infoList != null && infoList.size() > 0) {
                    NotifyInfo info = infoList.get(position);
                    if (info != null) {
                        Intent intent = new Intent(ZTSignActivity.this, ZTSignPicPreviewActivity.class);
                        intent.putExtra(ZTSignPicPreviewActivity.PIC_NAME, TextUtils.isEmpty(info.getPicPath()) ? "" : info.getPicPath());
                        intent.putExtra(ZTSignPicPreviewActivity.TRADE_NO, TextUtils.isEmpty(info.getPicPath()) ? "" : info.getExpress_number());
                        intent.putExtra(ZTSignPicPreviewActivity.POSITION, position);
                        startActivityForResult(intent, REQUEST_CODE_PIC_PREVIEW);
                    } else {
                        UtilToolkit.showToast("当前运单号信息丢失,请重新拍照采集数据");
                    }
                } else {
                    UtilToolkit.showToast("当前运单号信息丢失,请重新拍照采集数据");
                }
            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.ll_save, R.id.ll_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                UtilToolkit.showBackDialog(this);
                break;
            case R.id.ll_save:
                save();
                break;
            case R.id.ll_upload:

                if (tbSmsNotify.isChecked()) {//发送短信
                    if (mReplyModel == null) {
                        UtilToolkit.showToast("请选择短信模板！");
                        return;
                    }
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
        if (data == null) return;
        if (requestCode == REQUEST_CODE_CHOOSE_TEMPLATE) {//选择短信模板
            if (data != null && data.hasExtra("modelObject")) {
                mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
                if (null != mReplyModel) {
                    if (TextUtils.isEmpty(mReplyModel.getTitle()))
                        tvTemplateName.setText("签收模板");
                    else
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
        } else if (REQUEST_CODE_INPUT_PHONENUMBER == requestCode) {
            Serializable ser = data.getSerializableExtra("numberPhonePair");
            pairs = (List<NumberPhonePair>) ser;
            upload();
//            sendSMSAfterUpload();
        } else if (REQUEST_CODE_PIC_PREVIEW == requestCode) {
            if (RESULT_OK == resultCode) {
                if (data != null) {
                    String newPicPath = "";
                    int position = 0;
                    if (data.hasExtra(ZTSignPicPreviewActivity.POSITION)) {
                        position = data.getIntExtra(ZTSignPicPreviewActivity.POSITION, 0);
                    }
                    if (data.hasExtra(ZTSignPicPreviewActivity.PIC_NAME)) {
                        newPicPath = data.getStringExtra(ZTSignPicPreviewActivity.PIC_NAME);
                    }
                    if (!TextUtils.isEmpty(newPicPath)) {
                        infoList.get(position).setPicPath(newPicPath);
                        mQuickAdapter.notifyDataSetChanged();
                        //Utility.showToast(getApplicationContext(), "签收照片已更新");
                    }
                }
            }
        }
    }


    /**
     * 保存
     */
    private void save() {

        if (infoList.size() == 0) {
            UtilToolkit.showToast("没有可保存的数据");
        } else {
            for (NotifyInfo info : infoList) {
                if (TextUtils.isEmpty(info.getPicPath())) {
                    UtilToolkit.showToast("请选择签收人！");
                    return;
                }
            }

            ArrayList<E3_order> orders = infoToOrder(infoList, 0, 0);
            for (E3_order order : orders) {
                E3OrderDAO.addOrder(order, company, courierNO);
            }
        }
        if (SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            Intent intent = new Intent(this, BackgroundUploadService.class);
            startService(intent);
        }
        finish();
        UMShareManager.onEvent(ZTSignActivity.this, "dispatch_sign_save_zt", "dispatch_sign", "中通派件：签收：保存");


    }

    /**
     * 检查单号是否存在对应的手机号
     */
    private void getPhoneState() {
        JSONObject data = new JSONObject();
        String[] numbers = new String[infoList.size()];
        for (int i = 0, j = infoList.size(); i < j; i++) {
            numbers[i] = infoList.get(i).getExpress_number();
        }
        try {
            data.put("dhs", JSON.toJSON(numbers));
            data.put("sname", INFORM_BYDH_SEND1);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发短信
     */
    private void sendSMSAfterUpload() {
        JSONObject data = new JSONObject();
        com.alibaba.fastjson.JSONArray array = (com.alibaba.fastjson.JSONArray) JSON.toJSON(pairs);
        try {
            data.put("sname", INFORM_BYDH_SEND2);
            data.put("ident", ident);
            data.put("tid", mReplyModel.getTid());//短信模板id
            if (array == null)
                data.put("dhs", "");//单号，电话键值对
            else
                data.put("dhs", array);//单号，电话键值对
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
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


    private void alert(String errorMsg) {
        SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(this, SkuaidiE3SysDialog.TYPE_COMMON, new View(this));
        dialog.setTitle("上传提醒");
        dialog.setCommonContent(errorMsg);
        dialog.setSingleButtonTitle("确定");
        dialog.isUseSingleButton(true);
        dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

            @Override
            public void onClick() {
                finish();
            }
        });
        if (!isFinishing())
            dialog.showDialog();
    }

    /**
     * 数据上传
     */

    private void upload() {
        JSONObject data = new JSONObject();
        JSONArray wayBills = new JSONArray();
        int pic_no_diverse = 0;// 图片序号
        JSONObject signPics = new JSONObject();// id:图片
        JSONObject picPath = new JSONObject();// id:图片路径
        picPathList.clear();
        picSignInfos.clear();
        for (NotifyInfo info : infoList) {
            if (TextUtils.isEmpty(info.getPicPath())) {
                UtilToolkit.showToast("请选择签收人！");
                return;
            }

            picPathList.add(info.getPicPath());
        }
        try {
            for (int i = 0; i < infoList.size(); i++) {
                JSONObject wayBill = new JSONObject();
                NotifyInfo notifyInfo = infoList.get(i);
                wayBill.put("waybillNo", notifyInfo.getExpress_number());
                wayBill.put("scan_time", notifyInfo.getScanTime());
                if (!TextUtils.isEmpty(notifyInfo.getPicPath())) {
                    if (picSignInfos.size() == 0) {
                        pic_no_diverse++;
                        wayBill.put("signPic", pic_no_diverse);
                        signPics.put("" + pic_no_diverse,
                                Utility.bitMapToString(Utility.getImage(notifyInfo.getPicPath())));
                        picPath.put("" + pic_no_diverse, notifyInfo.getPicPath());

                    } else {
                        boolean samePic = false;
                        Iterator<String> iterator = picPath.keys();
                        while (iterator.hasNext()) {
                            String id = iterator.next();
                            if (picPath.optString(id).equals(notifyInfo.getPicPath())) {
                                wayBill.put("signPic", id);
                                samePic = true;
                                break;
                            }
                        }
                        if (!samePic) {
                            pic_no_diverse++;
                            if (pic_no_diverse <= MAX_SIGNED_PIC) {
                                wayBill.put("signPic", pic_no_diverse);
                                signPics.put("" + pic_no_diverse,
                                        Utility.bitMapToString(Utility.getImage(notifyInfo.getPicPath())));
                                picPath.put("" + pic_no_diverse, notifyInfo.getPicPath());
                            }
                        }

                    }

                    if (!TextUtils.isEmpty(notifyInfo.getPicPath())) {
                        signed_pic = true;
                    }
                    if (pic_no_diverse > MAX_SIGNED_PIC && infoList.size() > MAX_SIGNED_PIC) {
                        break;
                    }
                    picSignInfos.add(notifyInfo);
                }
                wayBills.put(wayBill);
            }
            if (wayBills.length() != 0) {
                data.put("wayBillDatas", wayBills);
                data.put("signPics", signPics);
            } else {
                dismissProgressDialog();//this);
                signed_pic = false;
                picSignInfos.clear();
                return;
            }

            data.put("sname", E3SysManager.getScanNameV2());
            data.put("wayBillType", E3SysManager.typeToIDMap.get(scanType));
            data.put("dev_id", Utility.getOnlyCode());
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            data.put("dev_imei", tm.getDeviceId());
            data.put("wayBillDatas", wayBills);
            data.put("appVersion", SKuaidiApplication.VERSION_CODE+"");
            data.put("sendSms", SkuaidiSpf.getAutoSignNotify(courierNO) ? 1 : 0);
            if(!TextUtils.isEmpty(this.sceneId)) data.put("sceneId",this.sceneId);
//            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            requestV2(data);
            showProgressDialog("");//this, "正在上传，请稍后...");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        UMShareManager.onEvent(ZTSignActivity.this, "dispatch_sign_upload_zt", "dispatch_sign", "中通派件：签收：上传");

    }
}
