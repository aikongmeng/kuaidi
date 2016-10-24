package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiButton;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.customer.adapter.CustomTagsItemAdatper;
import com.kuaibao.skuaidi.customer.entity.Tags;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.adapter.SignAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.dispatch.bean.TagNotice;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.kuaibao.skuaidi.dispatch.activity.DispatchActivity.GET_ADDRESS;

public class SignActivity extends SkuaiDiBaseActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String INFORM_BYDH_SEND2 = "inform_bydh.send2";//发短信
    /**
     * 添加签收类型
     */
    private static final int MAX_SIGNED_PIC = 3;//图片签收上传，一次最多包含三张互不相同的照片

    private static final int REQUEST_CODE_ADD_SIGN_TYPE = 100;
    /**
     * 选择模板
     */
    private static final int REQUEST_CODE_CHOOSE_TEMPLATE = 102;
    /**
     * 填写手机号
     */
    /**
     * 是否包含图片签收
     */
    private boolean signed_pic = false;

    private static final int REQUEST_CODE_INPUT_PHONENUMBER = 103;
    private static final String INFORM_BYDH_SEND1 = "inform_bydh.send1";//查询单号是否存在对应的手机号
    @BindView(R.id.iv_title_back)
    SkuaidiImageView ivTitleBack;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;//标题
    @BindView(R.id.tv_add_sign_type)
    TextView tvAddSignType;//添加签收类型
    @BindView(R.id.iv_take_phone)
    ImageView ivTakePhone;//拍照签收
    @BindView(R.id.rl_sign_photo)
    RelativeLayout rlSignPhoto;//拍照签收布局，批量签收不包含
    @BindView(R.id.tb_sms_notify)
    ToggleButton tbSmsNotify;//短信通知开关
    @BindView(R.id.rl_sms_notify)
    RelativeLayout rlSmsNotify;
    @BindView(R.id.tv_template_name)
    TextView tvTemplateName;//模板名称
    @BindView(R.id.rl_sms_template)
    RelativeLayout rlSmsTemplate;//模板布局
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.ll_save)
    LinearLayout llSave;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.ll_upload)
    LinearLayout llUpload;
    @BindView(R.id.rv_data_container)
    RecyclerView rvDataContainer;
    @BindView(R.id.tv_choose_sign_type)
    TextView tvChooseSignType;
    @BindView(R.id.line_above_sign)
    View lineAboveSign;
    @BindView(R.id.line_under_sign)
    View lineUnderSign;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.tv_pic_name)
    TextView tvPicName;
    @BindView(R.id.bt_title_more)
    SkuaidiButton btTitleMore;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.rl_delivery_number)
    RelativeLayout rl_delivery_number;
    @BindView(R.id.tv_delivery_number)
    TextView tv_delivery_number;
    @BindView(R.id.tv_delivery_status)
    TextView tvDeliveryStatus;//运单状态
    @BindView(R.id.view_line)
    View view_line;
    @BindView(R.id.rv_label)
    RecyclerView rv_label;//标签信息


    private SignAdapter adapter;
    private CustomTagsItemAdatper tagsAdatper;
    private final String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.
    private String courierNO;//工号
    private List<NotifyInfo> infoList;
    private ReplyModel mReplyModel;
    private static final String scanType = "扫签收";
    private String ident;
    /**
     * 包含图片的签收件
     */
    private final List<NotifyInfo> picSignInfos = new ArrayList<>();
    private final ArrayList<String> picPathList = new ArrayList<>();// 所有单号对应的图片地址

    private JSONArray failDh;//没有手机号的单号
    private JSONArray passDh;//有手机号的单号
    private List<NumberPhonePair> pairs;
    private List<String> successList = new ArrayList<>();//上传成功的单号
    private final List<UploadResutl.ErrorBean> errorBeanList = new ArrayList<>();//失败的单号
    private String sourceType;
    private List<Tags> labelList = new ArrayList<>();//标签信息集合
    private Context context;

    private String sceneId = "";
    public static final String SCENE_ID_NAME = "scene_id_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        context = this;
        if (getIntent().hasExtra(SCENE_ID_NAME)) {
            this.sceneId = getIntent().getStringExtra(SCENE_ID_NAME);
        }
        infoList = (ArrayList<NotifyInfo>) getIntent().getSerializableExtra("dataList");
        sourceType = infoList.get(0).getStatus();
        tvTitleDes.setText("签收");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < infoList.size(); i++) {
            buffer.append(infoList.get(i).getExpress_number()).append(",");
        }
        if (infoList.size() == 1) {
            getNotice(infoList.get(0).getExpress_number());
            rl_delivery_number.setVisibility(View.VISIBLE);
            tv_delivery_number.setText(buffer.deleteCharAt(buffer.toString().length() - 1).toString());
            if (TextUtils.isEmpty(infoList.get(0).getStatus())) {
                tvDeliveryStatus.setVisibility(View.GONE);
            } else {
                tvDeliveryStatus.setText(infoList.get(0).getStatus());
            }
            String picPath = infoList.get(0).getPicPath();
            if (!TextUtils.isEmpty(picPath)) {
                tvPicName.setText(StringUtil.getFileName(picPath));
                ivDelete.setVisibility(View.VISIBLE);
            }

        }

        tbSmsNotify.setOnCheckedChangeListener(this);
        courierNO = E3SysManager.getCourierNO();//工号
        rvDataContainer.setHasFixedSize(true);
        rvDataContainer.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        rvDataContainer.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.pickerview_topbar_paddingright),
                        0)
                .build());

        if (E3SysManager.BRAND_ZT.equals(company)) {
            rvDataContainer.setVisibility(View.GONE);
            tvAddSignType.setVisibility(View.GONE);
            tvChooseSignType.setVisibility(View.GONE);
            lineAboveSign.setVisibility(View.GONE);
            lineUnderSign.setVisibility(View.GONE);
        } else {
            if (infoList != null && infoList.size() > 1) {//申通批量签收不能拍照
                rlSignPhoto.setVisibility(View.GONE);
            }
            List<E3Type> list = E3SysManager.getSignedTypes(company);
            adapter = new SignAdapter(this, list);
            rvDataContainer.setAdapter(adapter);
            String picPath = infoList.get(0).getPicPath();
            if (!TextUtils.isEmpty(picPath)) {
                adapter.clearSelect();
            }
            getAddressByWaybillNo(new ArrayList<>(Arrays.asList(infoList.get(0).getExpress_number())));
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        if (TextUtils.isEmpty(result)) {
            UtilToolkit.showToast(msg);
            return;
        }
        if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {

            ResponseData data = JSON.parseObject(result, ResponseData.class);
            if (data.getCode() != 0) {
                dismissProgressDialog();//this);
                alert(data.getDesc());
                return;
            }
            UploadResutl uploadResutl = JSON.parseObject(data.getResult(), UploadResutl.class);
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
                    E3OrderDAO.addOrders(infoToOrder(Collections.singletonList(notifyInfo), 1, 0), company, courierNO);
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
                    if (successList != null && !successList.contains(info.getExpress_number()))
                        continue;//失败的单号不处理

                    // 上传成功后保存在数据库，当天去重检查用。
                    E3OrderDAO.addOrders(infoToOrder(Collections.singletonList(info), 1, 0), company, courierNO);
                    E3OrderDAO.deleteCacheOrders(infoToOrder(Collections.singletonList(info), 1, 1));

                    infoList.remove(info);
                }

            }
            if (errorBeanList.size() == 0) {//单号全部上传成功，并且发短信
                if (tbSmsNotify.isChecked()) {
                    sendSMSAfterUpload();
                } else {
                    UtilToolkit.showToast("上传成功");
                    finish();

                }
            } else {
                dismissProgressDialog();//this);
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
        } else if (GET_ADDRESS.equals(sname)) {
            List<DispatchActivity.AddressInfo> addressList = JSON.parseArray(result, DispatchActivity.AddressInfo.class);
            if (addressList == null) return;
            for (DispatchActivity.AddressInfo address : addressList) {

                for (NotifyInfo info : infoList) {
                    if (address.getWaybillNo().equals(info.getExpress_number())) {
                        if (address.getInfo() != null && !TextUtils.isEmpty(address.getInfo().getAddress())) {
                            if (!TextUtils.isEmpty(address.getInfo().getName())) {
                                E3Type e3Type = new E3Type();
                                e3Type.setCompany(company);
                                e3Type.setType(address.getInfo().getName());
                                adapter.getTypeList().add(0, e3Type);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    }
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
        } else if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            UtilToolkit.showToast(result);
        }


    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
    }
    @OnClick({R.id.iv_title_back, R.id.bt_title_more, R.id.tv_add_sign_type, R.id.iv_take_phone, R.id.iv_delete, R.id.ll_save, R.id.ll_upload, R.id.rl_sms_template})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                UtilToolkit.showBackDialog(this);
                break;
            case R.id.bt_title_more:
                break;
            case R.id.tv_add_sign_type:
                final View contentView = LayoutInflater.from(this).inflate(R.layout.activity_add_sign_type, null);

                CustomDialog.Builder mBuilder = new CustomDialog.Builder(this).setTitle("添加签收人")
                        .setContentView(contentView)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) contentView.findViewById(R.id.edt_user_defined);
                                String str = editText.getText().toString().trim();
                                if (!TextUtils.isEmpty(str)) {
                                    try {
                                        if ("sto".equals(company)) {
                                            if (str.getBytes("GBK").length > 14) {
                                                UtilToolkit.showToast("签收人最多只能有七个字或者十四个字母，两个字母算一个字，请重新编辑后再添加！");
                                                return;
                                            }
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                    E3Type e3Type = new E3Type();
                                    e3Type.setCompany(company);
                                    e3Type.setType(str);
                                    UMShareManager.onEvent(SignActivity.this, "E3_add_signedType", "E3", "E3：自定义签收类型");
                                    SkuaidiNewDB.getInstance().addE3SignedType(e3Type);
                                    adapter.addType(e3Type);
                                    rvDataContainer.smoothScrollToPosition(adapter.getItemCount() - 1);
                                    dialog.dismiss();
                                }
                            }
                        });
                CustomDialog dialog = mBuilder.create();
                //弹出键盘
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
                break;
            case R.id.iv_take_phone://拍照
                Intent mIntent = new Intent(this, EThreeCameraActivity.class);
                mIntent.putExtra("wayBills", (Serializable) infoList);
                startActivityForResult(mIntent,
                        EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                UMShareManager.onEvent(context, "dispatch_sign_photograph", "dispatch_sign", "申通派件：拍照签收");
                break;
            case R.id.iv_delete:
                deletePic();
                break;
            case R.id.rl_sms_template:
                Intent intent1 = new Intent(this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_TEMPLATE);
                break;
            case R.id.ll_save:
//                if (tagsAdatper != null && tagsAdatper.getTagsList() != null && tagsAdatper.getTagsList().size() != 0) {
//                    String saveMesage = "";
//                    for (Tags tags : tagsAdatper.getTagsList()) {
//                        if ("pay".equals(tags.getType())) {
//                            saveMesage = "该件为货到付款件，是否继保存？";
//                            break;
//                        } else if ("intercept".equals(tags.getType())) {
//                            saveMesage = "该件为拦截件，是否继续保存？";
//                            break;
//                        }
//                    }
//                    if (!TextUtils.isEmpty(saveMesage)) {
//                        com.kuaibao.skuaidi.dialog.CustomDialog.Builder builder = new com.kuaibao.skuaidi.dialog.CustomDialog.Builder(this);
//                        builder.setMessage(saveMesage).setTitle("温馨提示").setPositiveButton("继续保存", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                save();
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).create().show();
//                        return;
//                    }
//                }
                save();
                break;
            case R.id.ll_upload:
                if (tagsAdatper != null && tagsAdatper.getTagsList() != null && tagsAdatper.getTagsList().size() != 0) {
                    String mesage = "";
                    for (Tags tags : tagsAdatper.getTagsList()) {
                        if ("pay".equals(tags.getType())) {
                            mesage = "该件为货到付款件，是否继续签收？";
                            break;
                        } else if ("intercept".equals(tags.getType())) {
                            mesage = "该件为拦截件，是否继续签收？";
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(mesage)) {
                        com.kuaibao.skuaidi.dialog.CustomDialog.Builder builder = new com.kuaibao.skuaidi.dialog.CustomDialog.Builder(this);
                        builder.setMessage(mesage).setTitle("温馨提示").setPositiveButton("继续签收", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                smsUpload();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                        return;
                    }
                }
                smsUpload();
                break;
        }
    }

    /**
     * 删除图片
     */
    public void deletePic() {
        tvPicName.setText("");
        for (NotifyInfo info : infoList) {
            info.setPicPath("");
        }
        ivDelete.setVisibility(View.GONE);
    }

    private void smsUpload() {
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

                intent2.putExtra("numbers", failDh.opt(0).toString());
                startActivityForResult(intent2, REQUEST_CODE_INPUT_PHONENUMBER);
            } else {//所有单号都能查到对应的手机号
                upload();
            }
        } else {
            upload();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == REQUEST_CODE_ADD_SIGN_TYPE && resultCode == 101) {//添加签收类型
            E3Type signType = (E3Type) data.getSerializableExtra("new_sign_type");
            adapter.addType(signType);
            rvDataContainer.smoothScrollToPosition(adapter.getItemCount() - 1);
        } else if (requestCode == EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE) {//拍照
            List<NotifyInfo> infos = (List<NotifyInfo>) data.getSerializableExtra("picWayBills");
            infoList = infos;
            if (infos != null && infos.size() != 0) {
                String picPath = infos.get(0).getPicPath();
                tvPicName.setText(StringUtil.getFileName(picPath));
                adapter.clearSelect();
                ivDelete.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_TEMPLATE) {//选择短信模板
            {
                mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
                if (null != mReplyModel && data.hasExtra("modelObject")) {

                    if (TextUtils.isEmpty(mReplyModel.getTitle()))
                        tvTemplateName.setText("签收模板");
                    else
                        tvTemplateName.setText(mReplyModel.getTitle());

                } else {
                    ToastHelper.makeText(getApplicationContext(), "选择模板有误！", ToastHelper.LENGTH_LONG)
                            .setAnimation(R.style.popUpWindowEnterExit).show();
                }
            }

        } else if (REQUEST_CODE_INPUT_PHONENUMBER == requestCode) {
            Serializable ser = data.getSerializableExtra("numberPhonePair");
            pairs = (List<NumberPhonePair>) ser;
            upload();
//            sendSMSAfterUpload();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            rlSmsTemplate.setVisibility(View.VISIBLE);
            getPhoneState();
            UMShareManager.onEvent(context, "dispatch_sign_sms_sto", "dispatch_sign", "申通派件：签收：短信通知收件人");
        } else {
            rlSmsTemplate.setVisibility(View.GONE);
        }

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
            if (!TextUtils.isEmpty(info.getPicPath()))
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
                } else {
                    E3Type singType = adapter.getSelectSignType();
                    notifyInfo.setWayBillTypeForE3(singType.getType());
                    wayBill.put("signType", notifyInfo.getWayBillTypeForE3());
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
            data.put("appVersion", SKuaidiApplication.VERSION_CODE + "");
            if (!tbSmsNotify.isChecked()) {
                data.put("sendSms", SkuaidiSpf.getAutoSignNotify(courierNO) ? 1 : 0);
            }
            if (!TextUtils.isEmpty(this.sceneId)) data.put("sceneId", this.sceneId);
//            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            requestV2(data);
            showProgressDialog("");//this, "正在上传，请稍后...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UMShareManager.onEvent(context, "dispatch_sign_upload_sto", "dispatch_sign", "申通派件：签收：上传");


    }

    /**
     * 保存
     */
    private void save() {
        for (NotifyInfo info : infoList) {
            if (TextUtils.isEmpty(tvPicName.getText().toString().trim())) {
                E3Type singType = adapter.getSelectSignType();
                info.setWayBillTypeForE3(singType.getType());
            }
        }
        if (infoList.size() == 0) {
            UtilToolkit.showToast("没有可保存的数据");
        } else {
            ArrayList<E3_order> orders = infoToOrder(infoList, 0, 0);
            for (E3_order order : orders) {
                E3OrderDAO.addOrder(order, company, courierNO);
            }
        }
        adapter.notifyDataSetChanged();
        if (SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            Intent intent = new Intent(this, BackgroundUploadService.class);
            startService(intent);
        }
        finish();
        UMShareManager.onEvent(context, "dispatch_sign_save_sto", "dispatch_sign", "申通派件：签收：保存");

    }

    /**
     * @param notifyInfos 单号数据集
     * @param isUpload    是否已经上传
     * @param isCache     是否作为缓存
     * @return ArrayList<E3_order>  可保存的数据集
     */
    private ArrayList<E3_order> infoToOrder(List<NotifyInfo> notifyInfos, int isUpload, int isCache) {
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


    private void getNotice(String number) {
        ApiWrapper apiWrapper = new ApiWrapper();
        apiWrapper.getExpressDetailsNotice(number).subscribe(new Action1<TagNotice>() {
            @Override
            public void call(TagNotice tagNotice) {
                if (tagNotice == null) return;
                showTagNotice(tagNotice);
            }
        });
    }


    /**
     * 通过单号获派件地址
     *
     * @param waybillNo 快递单号
     */
    private void getAddressByWaybillNo(List<String> waybillNo) {
        JSONObject data = new JSONObject();
        try {
            String numbers = "";
            for (int i = 0, j = waybillNo.size(); i < j; i++) {
                numbers += waybillNo.get(i) + ",";
            }
            if (TextUtils.isEmpty(numbers)) {
                return;
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("sname", GET_ADDRESS);
            data.put("appVersion", Utility.getVersionCode());
            data.put("waybillNo", numbers);//单号
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示标签
     */
    private void showTagNotice(TagNotice tagNotice) {
        if (tagNotice != null && tagNotice.getInfo() != null) {


            if (!TextUtils.isEmpty(tagNotice.getInfo().getPay())) {
                Tags tags = new Tags();
                tags.setType("pay");
                tags.setDesc(tagNotice.getInfo().getPay());
                labelList.add(tags);
            }
            if (!TextUtils.isEmpty(tagNotice.getInfo().getIntercept())) {
                Tags tags = new Tags();
                tags.setType("intercept");
                tags.setDesc(tagNotice.getInfo().getIntercept());
                labelList.add(tags);
            }

            String tousu = tagNotice.getInfo().getTousu();
            String complain = tagNotice.getInfo().getComplain();
            String nobox = tagNotice.getInfo().getNoBox();
            String sign = tagNotice.getInfo().getSign();
            String send = tagNotice.getInfo().getSend();

            if (!TextUtils.isEmpty(complain)) {
                Tags tags = new Tags();
                tags.setType("complain");
                tags.setDesc(complain);

                labelList.add(tags);
            }

            if (!TextUtils.isEmpty(sign)) {
                Tags tags = new Tags();
                tags.setType("sign");
                tags.setDesc(sign);

                labelList.add(tags);
            }
            if (!TextUtils.isEmpty(nobox)) {
                Tags tags = new Tags();
                tags.setType("nobox");
                tags.setDesc(nobox);

                labelList.add(tags);
            }
            if (!TextUtils.isEmpty(send)) {
                Tags tags = new Tags();
                tags.setType("send");
                tags.setDesc(send);

                labelList.add(tags);
            }
            if (!TextUtils.isEmpty(tousu)) {
                Tags tags = new Tags();
                tags.setType("tousu");
                tags.setDesc(tousu);

                labelList.add(tags);
            }

            if (labelList.size() > 0) {
                view_line.setVisibility(View.VISIBLE);
                rv_label.setVisibility(View.VISIBLE);
                tagsAdatper = new CustomTagsItemAdatper(labelList);
                tagsAdatper.setTagsEditable(false);
                rv_label.setLayoutManager(new LinearLayoutManager(context));
                rv_label.setAdapter(tagsAdatper);
            }
        }

    }
}
