package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.customer.adapter.CustomTagsItemAdatper;
import com.kuaibao.skuaidi.customer.entity.Tags;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.activity.adapter.QuickAdapter;
import com.kuaibao.skuaidi.dispatch.adapter.ZTSignAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.dispatch.bean.TagNotice;
import com.kuaibao.skuaidi.dispatch.bean.ZTSignType;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
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
import rx.Subscription;
import rx.functions.Action1;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ZTSingleSignActivity extends SkuaiDiBaseActivity {
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
    //    @BindView(R.id.rv_list)
//    RecyclerView mRecyclerView;
    @BindView(R.id.rl_zt_delivery_number)
    RelativeLayout rl_zt_delivery_number;
    @BindView(R.id.tv_zt_delivery_number)
    TextView tv_zt_delivery_number;
    @BindView(R.id.tv_zt_delivery_status)
    TextView tv_zt_delivery_status;//运单状态
    @BindView(R.id.view_line)
    View view_line;
    @BindView(R.id.rv_zt_label)
    RecyclerView rv_zt_label;//标签信息
    @BindView(R.id.listView)
    ExpandableStickyListHeadersListView listView;
    @BindView(R.id.tv_pic_name)
    TextView tvPicName;
    @BindView(R.id.iv_take_phone)
    ImageView ivTakePhone;//拍照签收
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.tb_sms_notify)
    ToggleButton tbSmsNotify;//短信通知开关
    @BindView(R.id.rl_sms_template)
    RelativeLayout rlSmsTemplate;//模板布局
    @BindView(R.id.tv_template_name)
    TextView tvTemplateName;//模板名称
//    @BindView(R.id.scrollview)
//    BottomScrollView scrollView;

    private List<NotifyInfo> infoList = new ArrayList<>();
    private ReplyModel mReplyModel;
    private String signType = "";
    private String courierNO;
    private QuickAdapter mQuickAdapter;
    private CustomTagsItemAdatper tagsAdatper;
    //scrollview 是否滑动到底部
    private boolean isSvToBottom = false;
    /**
     * listview竖向滑动的阈值
     */
    private static final int THRESHOLD_Y_LIST_VIEW = 20;

    private float mLastY;
    /**
     * 包含图片的签收件
     */
    private List<NotifyInfo> picSignInfos = new ArrayList<>();
    private ArrayList<String> picPathList = new ArrayList<>();// 所有单号对应的图片地址
    private List<Tags> labelList = new ArrayList<>();//标签信息集合
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
    private String sceneId = "";
    public static final String SCENEID_NAME = "scene_id_name";
    private Context context;
    private List<ZTSignType> ztSignTypes = new ArrayList<>();

    private int checkedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zt_single_sign);
        ButterKnife.bind(this);
        context = this;
        signType = getIntent().getStringExtra(SIGN_TYPE_NAME);
        courierNO = E3SysManager.getCourierNO();//工号
        infoList = (List<NotifyInfo>) getIntent().getSerializableExtra(PIC_WAY_NAME);
        if (getIntent().hasExtra(SCENEID_NAME)) {
            sceneId = getIntent().getStringExtra(SCENEID_NAME);
        }
        if (SkuaidiSpf.getZTSignTypes() == null || SkuaidiSpf.getZTSignTypes().size() == 0) {
            getSignType();
        } else {
            ztSignTypes = SkuaidiSpf.getZTSignTypes();
            signAdapter = new ZTSignAdapter(context, ztSignTypes);
            listView.setAdapter(signAdapter);
            int checkPosition = signAdapter.getDataList().indexOf(SkuaidiSpf.getLatestSignTypeZT(courierNO));
            for (int i = 0, j = signAdapter.getTitleList().size(); i < j; i++) {
                listView.collapse(i);
            }
            if (checkPosition != -1) {
                listView.setItemChecked(checkPosition, true);
                infoList.get(0).setWayBillTypeForE3(signAdapter.getDataList().get(checkPosition).getType());
                listView.expand(signAdapter.getHeaderId(checkPosition));
                listView.smoothScrollToPosition(checkPosition);
            }
        }

        if (infoList.size() == 1) {
            getNotice(infoList.get(0).getExpress_number());
            rl_zt_delivery_number.setVisibility(View.VISIBLE);
            tv_zt_delivery_number.setText(infoList.get(0).getExpress_number());
            if (TextUtils.isEmpty(infoList.get(0).getStatus())) {
                tv_zt_delivery_status.setVisibility(View.GONE);
            } else {
                tv_zt_delivery_status.setText(infoList.get(0).getStatus());
            }
            String picPath = infoList.get(0).getPicPath();
            if (!TextUtils.isEmpty(picPath)) {
                tvPicName.setText(StringUtil.getFileName(picPath));
                ivDelete.setVisibility(View.VISIBLE);
            }
        }
        sourceType = infoList.get(0).getStatus();

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
        tvTitleDes.setText("签收扫描");
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        initAdapter();
//        mQuickAdapter.addFooterView(getView());
//        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
//                .color(getResources().getColor(R.color.gray_4))
//                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
//                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin),
//                        getResources().getDimensionPixelSize(R.dimen.recyle_divider_rightmargin))
//                .build());  //添加分割线
//        mRecyclerView.setAdapter(mQuickAdapter);

        rl_zt_delivery_number.setVisibility(View.VISIBLE);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < infoList.size(); i++) {
            buffer.append(infoList.get(i).getExpress_number()).append(",");
        }
        tv_zt_delivery_number.setText(buffer.deleteCharAt(buffer.toString().length() - 1).toString());
        // TODO 此处是模拟数据，需要替换成真实的标签信息
//        Tags tags1 = new Tags();
//        tags1.setType("pay");
//        tags1.setDesc("测试数据");
//        Tags tags2 = new Tags();
//        tags2.setType("tousu");
//        tags2.setDesc("测试数据");
//        labelList.add(tags1);
//        labelList.add(tags2);
//
//
//        if(labelList.size() > 0){
//            view_line.setVisibility(View.VISIBLE);
//            rv_zt_label.setVisibility(View.VISIBLE);
//            tagsAdatper = new CustomTagsItemAdatper(labelList);
//            tagsAdatper.setTagsEditable(false);
//            rv_zt_label.setLayoutManager(new LinearLayoutManager(context));
//            rv_zt_label.setAdapter(tagsAdatper);
//        }

        tbSmsNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rlSmsTemplate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (isChecked) {
                    getPhoneState();
                    UMShareManager.onEvent(ZTSingleSignActivity.this, "dispatch_sign_sms_zt", "dispatch_sign", "中通派件：签收：短信通知收件人");
                }

            }
        });
//        scrollView.setScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
//            @Override
//            public void onScrollToBottom() {
//                isSvToBottom = true;
//            }
//
//            @Override
//            public void onNotScrollToBottom() {
//                isSvToBottom = false;
//            }
//        });
//        // ListView滑动冲突解决
//        listView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                if (action == MotionEvent.ACTION_DOWN) {
//                    mLastY = event.getY();
//                }
//                if (action == MotionEvent.ACTION_MOVE) {
//                    int top = listView.getChildAt(0).getTop();
//                    float nowY = event.getY();
//                    if (!isSvToBottom) {
//                        // 允许scrollview拦截点击事件, scrollView滑动
//                        scrollView.requestDisallowInterceptTouchEvent(false);
//                    } else if (top == 0 && nowY - mLastY > THRESHOLD_Y_LIST_VIEW) {
//                        // 允许scrollview拦截点击事件, scrollView滑动
//                        scrollView.requestDisallowInterceptTouchEvent(false);
//                    } else {
//                        // 不允许scrollview拦截点击事件， listView滑动
//                        scrollView.requestDisallowInterceptTouchEvent(true);
//                    }
//                }
//                return false;
//            }
//        });

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
                rv_zt_label.setVisibility(View.VISIBLE);
                tagsAdatper = new CustomTagsItemAdatper(labelList);
                tagsAdatper.setTagsEditable(false);
                rv_zt_label.setLayoutManager(new LinearLayoutManager(context));
                rv_zt_label.setAdapter(tagsAdatper);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkedPosition == listView.getCheckedItemPosition()) {
                    listView.setItemChecked(-1, true);
                    checkedPosition = -1;
                    for (NotifyInfo info : infoList) {
                        info.setWayBillTypeForE3(null);
                    }
                } else {
                    checkedPosition = position;
                    String signType = (signAdapter.getSignTypes().get(checkedPosition));
                    for (NotifyInfo info : infoList) {
                        info.setWayBillTypeForE3(signType);
                        SkuaidiSpf.saveLatestSignTypeZT(courierNO, signAdapter.getDataList().get(listView.getCheckedItemPosition()));
                    }
                    deletePic();
                }
            }
        });
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (listView.isHeaderCollapsed(headerId)) {
                    listView.expand(headerId);
                } else {
                    listView.collapse(headerId);
                }
                for (int i = 0, j = signAdapter.getTitleList().size(); i < j; i++) {
                    if (i != headerId) {
                        listView.collapse(i);

                    }
                }

                CheckBox cb = (CheckBox) header.findViewById(R.id.cb_check);
                cb.toggle();
            }
        });

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
                if (isChecked) {
                    getPhoneState();
                    UMShareManager.onEvent(ZTSingleSignActivity.this, "dispatch_sign_sms_zt", "dispatch_sign", "中通派件：签收：短信通知收件人");
                }

            }
        });
        view.findViewById(R.id.rl_sms_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择模板
                Intent intent1 = new Intent(ZTSingleSignActivity.this, ModelActivity.class);
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
//        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {//点击进入预览界面
                if (infoList != null && infoList.size() > 0) {
                    NotifyInfo info = infoList.get(position);
                    if (info != null) {
                        Intent intent = new Intent(ZTSingleSignActivity.this, ZTSignPicPreviewActivity.class);
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

    @OnClick({R.id.iv_title_back, R.id.ll_save, R.id.iv_take_phone, R.id.iv_delete, R.id.rl_sms_template, R.id.ll_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                UtilToolkit.showBackDialog(this);
                break;
            case R.id.ll_save:
                if (tagsAdatper != null && tagsAdatper.getTagsList() != null && tagsAdatper.getTagsList().size() != 0) {
                    String saveMesage = "";
                    for (Tags tags : tagsAdatper.getTagsList()) {
                        if ("pay".equals(tags.getType())) {
                            saveMesage = "该件为货到付款件，是否继保存？";
                            break;
                        } else if ("intercept".equals(tags.getType())) {
                            saveMesage = "该件为拦截件，是否继续保存？";
                            break;
                        }
                    }
                    if (!TextUtils.isEmpty(saveMesage)) {
                        com.kuaibao.skuaidi.dialog.CustomDialog.Builder builder = new com.kuaibao.skuaidi.dialog.CustomDialog.Builder(this);
                        builder.setMessage(saveMesage).setTitle("温馨提示").setPositiveButton("继续保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                save();
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
                save();
                break;
            case R.id.iv_delete:
                deletePic();
                break;
            case R.id.iv_take_phone://拍照
                Intent mIntent = new Intent(this, EThreeCameraActivity.class);
                mIntent.putExtra("wayBills", (Serializable) infoList);
                startActivityForResult(mIntent,
                        EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                break;
            case R.id.rl_sms_template:
                Intent intent1 = new Intent(this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_TEMPLATE);
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
    private void deletePic() {
        tvPicName.setText("");
        infoList.get(0).setPicPath("");
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
        } else if (requestCode == EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE) {//拍照
            List<NotifyInfo> infos = (List<NotifyInfo>) data.getSerializableExtra("picWayBills");
            infoList = infos;
            if (infos != null && infos.size() != 0) {
                String picPath = infos.get(0).getPicPath();
                tvPicName.setText(StringUtil.getFileName(picPath));
                ivDelete.setVisibility(View.VISIBLE);

                listView.setItemChecked(-1, true);
                checkedPosition = -1;
                for (NotifyInfo info : infoList) {
                    info.setWayBillTypeForE3(null);
                }
            }
        }
    }


    /**
     * 保存
     */
    private void save() {
        for (NotifyInfo info : infoList) {
            if (TextUtils.isEmpty(tvPicName.getText().toString().trim())) {

                info.setWayBillTypeForE3(info.getWayBillTypeForE3());
            }
        }
        if (infoList.size() == 0) {
            UtilToolkit.showToast("没有可保存的数据");
        } else {
            for (NotifyInfo info : infoList) {
                if (TextUtils.isEmpty(info.getPicPath()) && TextUtils.isEmpty(info.getWayBillTypeForE3())) {
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
        UMShareManager.onEvent(ZTSingleSignActivity.this, "dispatch_sign_save_zt", "dispatch_sign", "中通派件：签收：保存");


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
            if (TextUtils.isEmpty(info.getPicPath()) && TextUtils.isEmpty(info.getWayBillTypeForE3())) {
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
                } else {
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
            if (!tbSmsNotify.isChecked()) {
                data.put("sendSms", SkuaidiSpf.getAutoSignNotify(courierNO) ? 1 : 0);
            }
            data.put("wayBillDatas", wayBills);
            data.put("appVersion", SKuaidiApplication.VERSION_CODE + "");
            if (!TextUtils.isEmpty(this.sceneId)) data.put("sceneId", this.sceneId);
//            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            requestV2(data);
            showProgressDialog("");//this, "正在上传，请稍后...");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        UMShareManager.onEvent(ZTSingleSignActivity.this, "dispatch_sign_upload_zt", "dispatch_sign", "中通派件：签收：上传");

    }

    /**
     * 获取中通签收类型
     */
    ZTSignAdapter signAdapter;

    private void getSignType() {
        ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.getSignType().doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                int code = -1;
                String msg = "";
                if (throwable instanceof RetrofitUtil.APIException) {
                    RetrofitUtil.APIException exception = (RetrofitUtil.APIException) throwable;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                    code = exception.code;
                    msg = exception.msg;
                }
            }
        }).subscribe(newSubscriber(new Action1<List<ZTSignType>>() {
                                       @Override
                                       public void call(List<ZTSignType> ztSignTypes) {
                                           if (ztSignTypes != null && ztSignTypes.size() != 0) {
                                               ZTSingleSignActivity.this.ztSignTypes = ztSignTypes;
                                               SkuaidiSpf.setZTSignTypes(ztSignTypes);
                                               signAdapter = new ZTSignAdapter(context, ztSignTypes);
                                               listView.setAdapter(signAdapter);
                                               int checkPosition = signAdapter.getDataList().indexOf(SkuaidiSpf.getLatestSignTypeZT(courierNO));

                                               for (int i = 0, j = signAdapter.getTitleList().size(); i < j; i++) {
                                                   listView.collapse(i);
                                               }
                                               if (checkPosition != -1) {
                                                   listView.setItemChecked(checkPosition, true);
                                                   infoList.get(0).setWayBillTypeForE3(signAdapter.getDataList().get(checkPosition).getType());
                                                   listView.expand(signAdapter.getHeaderId(checkPosition));
                                                   listView.smoothScrollToPosition(checkPosition);

                                               }
                                           }
                                       }
                                   }
        ));
        mCompositeSubscription.add(subscription);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
