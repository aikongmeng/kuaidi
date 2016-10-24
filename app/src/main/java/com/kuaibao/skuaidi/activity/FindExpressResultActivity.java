package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FindExpressResultAdapter;
import com.kuaibao.skuaidi.activity.model.Deliver;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.activity.view.ExpressInfoDialog;
import com.kuaibao.skuaidi.activity.view.HurryOrderMenuDialog;
import com.kuaibao.skuaidi.activity.view.NotifySearchPhoneShowTishi;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop;
import com.kuaibao.skuaidi.activity.view.RotateLoadView;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.MiddleButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiPopAboutCheckList;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.entry.ClickItem;
import com.kuaibao.skuaidi.entry.WuliuInfo;
import com.kuaibao.skuaidi.entry.WuliuItem;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 查快递结果——在C端基础上修改而来
 */
public class FindExpressResultActivity extends SkuaiDiBaseActivity {

    private static final String EXPRESS_PROBLEM_INFO = "express.problem_info";
    private static final String EXPRESS_ORDER_TAG = "delivery/getExpressDetailsNotice";
    private SkuaidiPopAboutCheckList checkList;
    private ListView lv;
    private LinearLayout ll_load, ll_load_two, ll_status, ll_handle, ll_msg_des;
    private TextView tv_title_des, tv_order, tv_remark;
    private SkuaidiTextView tv_more;
    private ImageView iv_remark, iv_status, iv_note_delete, iv_content_expand;
    private View view_devider;
    private TextView tv_status;
    private TextView tv_handle;
    private TextView tv_note_detail, tv_get_pay, tv_intercept, tv_complain1,tv_complain2,tv_complain3,tv_complain4, tv_complain5, tv_brand;
    private View line_devider1, line_devider2, line_devider3,line_devider4, line_devider5, line_devider6, line_devider7 ;
    private ScrollView sv_notfind;
    private TextView tv_sms_msg, tv_msg_tag, tv_liuyan_msg;
    private RotateLoadView rotate_load;
    private RotateLoadView rotate_load_two;
    private String expressfirmname;
    private String express;
    private String order_number;
    private String message_id;
    private String sms_id;
    private boolean moreScan;
    private SkuaidiDB skuaidiDb;
    private FindExpressResultAdapter adapter;
    private SharedPreferences sp;
    private WuliuInfo wuliuInfo = new WuliuInfo();
    private final int EXCEPTION_DATA = 3;
    private final int NOT_PHONE = 4;
    private final int NOT_INFO = 5;
    // 收件人信息 name,tel,address
    private TextView cust_address;
    private TextView cust_tel;
    private TextView cust_name;
    private ImageView call_phone;
    // 生成时间，查询失败插入数据库的 ‘ 第一次查询时间’
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String time;
    private Deliver deliver = new Deliver();
    protected Context context;
    // 界面下面信息布局
    private RelativeLayout rt_bottom_info;
    private RelativeLayout rl_bottom_left;
    private RelativeLayout rl_note_detail;//备注信息

    private RelativeLayout rt_bottom_info_fail;
    private TextView tv_fail_info;
    private SkuaidiPopAboutCheckList popAboutCheckList;
    private RecordScreeningPop recordScreeningPop;

//    private LinearLayout ll_title_sto;
//    private LinearLayout ll_title_other;
    private String pay, intercept, tousu, complain, noBox, sign, send;
    private int total_num = 0;//标签信息的个数
    private boolean toExpanded = true;
    private String company;
    private SkuaidiImageView iv_title_more;
    private NotifySearchPhoneShowTishi searchPhoneShowTishi;

    // private final UMSocialService mController = UMServiceFactory
    // .getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 查询请求成功
                case Constants.FIND_EXPRESS_GET_OK:
                    JsonXmlParser.parseFindExpress(handler, msg.obj.toString());

                    break;
                // 快递查询失败
                case Constants.FIND_EXPRESS_PAISE_FAID:
                    //Log.i("test", "FIND_EXPRESS_PAISE_FAID===");
                    ll_load.setVisibility(View.GONE);
                    ll_load_two.setVisibility(View.GONE);
                    ll_status.setVisibility(View.GONE);
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.icon_ku));
                    tv_status.setText("最新状态：查询失败");
                    tv_handle.setText("再次查询");
                    sv_notfind.setVisibility(View.VISIBLE);
                    if (express.equals("fedexInter") || express.equals("fedex") || express.equals("hq")) {
                        LinearLayout ll_web = (LinearLayout) findViewById(R.id.ll_findexpress_result_web_express);
                        ll_web.setVisibility(View.GONE);
                    } else {

                        tv_brand.setText(expressfirmname + "官网");
                    }

                    /**
                     * 查询失败 把记录保存到数据库
                     */
                    skuaidiDb.insertExpressHistory(order_number, "", "", time.substring(0, 11));
                    skuaidiDb.insertDeliverNo(order_number, "", "", time.substring(0, 11), "");

                    rotate_load.clearAnimation();
                    rotate_load_two.clearAnimation();
                    break;
                // 快递查询成功
                case Constants.FIND_EXPRESS_PAISE_OK:
                    //Log.i("test", "FIND_EXPRESS_PAISE_OK===");
                    wuliuInfo = (WuliuInfo) msg.obj;
                    ll_status.setVisibility(View.GONE);
                    ll_handle.setVisibility(View.VISIBLE);
                    // 如果物流信息为空
                    if (wuliuInfo.getWuliuItems() == null || wuliuInfo.getWuliuItems().size() <= 0) {
                        ll_load.setVisibility(View.GONE);
                        ll_load_two.setVisibility(View.GONE);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.icon_ku));
                        tv_status.setText("最新状态：查询失败");
                        tv_handle.setText("再次查询");
                        sv_notfind.setVisibility(View.VISIBLE);
                        if (express.equals("fedexInter") || express.equals("fedex") || express.equals("hq")) {
                            LinearLayout ll_web = (LinearLayout) findViewById(R.id.ll_findexpress_result_web_express);
                            ll_web.setVisibility(View.GONE);
                        } else {
                            tv_brand.setText(expressfirmname + "官网");
                        }
                        skuaidiDb.insertExpressHistory(order_number, "", "", time.substring(0, 11));
                        skuaidiDb.insertDeliverNo(order_number, "", "", time.substring(0, 11), "");

                        rotate_load.clearAnimation();
                        rotate_load_two.clearAnimation();
                    } else {// 物流信息不为空

                        // 获取收件人信息
                        GetUserinfo();
                        //获取标签信息
                        getUserTag();

                        skuaidiDb.insertExpressHistory(order_number, wuliuInfo.getStatus(), wuliuInfo.getRecord(),
                                wuliuInfo.getFirst_time());
                        skuaidiDb.insertDeliverNo(order_number, wuliuInfo.getStatus(), wuliuInfo.getRecord(),
                                wuliuInfo.getFirst_time(), "");

                        ll_load.setVisibility(View.GONE);
                        ll_load_two.setVisibility(View.GONE);
                        ll_status.setVisibility(View.GONE);
                        if ("1".equals(wuliuInfo.getIsException())) {
                            iv_status.setImageResource(R.drawable.icon_ku);
                            tv_status.setText("最新状态：出现异常");
                            ll_handle.setBackgroundResource(R.drawable.selector_red);
                            TextView tv = (TextView) findViewById(R.id.tv_findexpress_result_handle);
                            // tvMore.setText("立即处理");
                        } else {
                            ll_handle.setVisibility(View.GONE);
                            if ("collected".equals(wuliuInfo.getStatus())) {
                                tv_status.setText("最新状态：已取件");
                            } else if ("sending".equals(wuliuInfo.getStatus()) || "arrived".equals(wuliuInfo.getStatus())) {
                                tv_status.setText("最新状态：运送中");
                            } else if ("delivering".equals(wuliuInfo.getStatus())) {
                                tv_status.setText("最新状态：派送中");
                            } else if ("signed".equals(wuliuInfo.getStatus())) {

                                iv_status.setImageDrawable(getResources().getDrawable(R.drawable.icon_xiao_green));
                                tv_status.setText("最新状态：已签收");
                            } else {
                                tv_status.setVisibility(View.GONE);
                            }
                        }

                        lv.setVisibility(View.VISIBLE);
                        lv.setAdapter(adapter);
                        // 取消动画
                        rotate_load.clearAnimation();
                        rotate_load_two.clearAnimation();

                        List<WuliuItem> wuliulist = wuliuInfo.getWuliuItems();
                        Collections.reverse(wuliulist);
                        adapter = new FindExpressResultAdapter(FindExpressResultActivity.this, wuliulist,
                                wuliuInfo.getIsException(), handler);
                        lv.setAdapter(adapter);

                    }
                    break;
                // 快递查询请求失败
                case Constants.FIND_EXPRESS_GET_FAID:
                    //Log.i("test", "FIND_EXPRESS_GET_FAID===");
                    ll_load.setVisibility(View.GONE);
                    ll_load_two.setVisibility(View.GONE);
                    ll_status.setVisibility(View.GONE);
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.icon_ku));
                    tv_status.setText("最新状态：查询失败");
                    tv_handle.setText("再次查询");
                    sv_notfind.setVisibility(View.VISIBLE);
                    if (express.equals("fedexInter") || express.equals("fedex") || express.equals("hq")) {
                        LinearLayout ll_web = (LinearLayout) findViewById(R.id.ll_findexpress_result_web_express);
                        ll_web.setVisibility(View.GONE);
                    } else {
                        tv_brand.setText(expressfirmname + "官网");
                    }
                    rotate_load.clearAnimation();
                    rotate_load_two.clearAnimation();
                    break;
                //
                case EXCEPTION_DATA:
                    UtilToolkit.showToast("对不起,数据发生异常!");
                    break;

                case NOT_PHONE:
                    UtilToolkit.showToast("对不起,未找到该网点电话!");
                    break;

                case NOT_INFO:
                    UtilToolkit.showToast("未找到该网点详情!");
                    break;
                // 网点信息查询成功
                case Constants.GET_BRNCH_INFO:
                    //Log.i("test", "BRANCH_PARSE_OK===");
                    KuaidiApi.getBranchInfo(FindExpressResultActivity.this, handler, ((ClickItem) msg.obj).getExpressId());
                    break;
                case Constants.GET_SENDER_INFO:// 获取派件员的信息

                    final String phoneString = wuliuInfo.getDiliver_phone();
                    if (!phoneString.trim().equals("") && phoneString != null) {

                        final HurryOrderMenuDialog.Builder senderbuilder = new HurryOrderMenuDialog.Builder(
                                FindExpressResultActivity.this);
                        // 联系方式非手机号则 发短信功能隐藏
                        HurryOrderMenuDialog.flag = phoneString.length() == 11 && phoneString.startsWith("1");

                        // builder.setName(((BranchInfo)
                        // msg.obj).getIndexShopName());
                        // 打电话
                        senderbuilder.setCallButton(new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // stub
                                // Intent intent = new
                                // Intent("android.intent.action.CALL",
                                // Uri.parse("tel:"
                                // + AllInterface.formatCall(phoneString)));
                                // FindExpressResultActivity.this.startActivity(intent);
                                AcitivityTransUtil
                                        .showChooseTeleTypeDialog(FindExpressResultActivity.this, "", phoneString, AcitivityTransUtil.NORMAI_CALL_DIALOG, "", "");
                                dialog.dismiss();

                            }
                        });
                        // 发短信
                        senderbuilder.setMessageButton(new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(FindExpressResultActivity.this, HurryOrderActivity.class);
                                SKuaidiApplication.getInstance().postMsg("HurryOrderActivity", "deliver_info", deliver);
                                FindExpressResultActivity.this.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        // 取消
                        senderbuilder.setCancelButton(new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        senderbuilder.create().show();
                    }
                    break;
                case Constants.BRANCH_GET_OK:
                    // //System.out.println(msg.obj.toString());
                    JsonXmlParser.parseBranchInfo(FindExpressResultActivity.this, handler, msg.obj.toString());
                    break;

                case Constants.BRANCH_GET_FAILD:
                    UtilToolkit.showToast("网络异常");
                    break;

                case Constants.BRANCH_PARSE_OK:

                    //Log.i("test", "BRANCH_PARSE_OK===");
                    final ExpressInfoDialog.Builder builder = new ExpressInfoDialog.Builder(FindExpressResultActivity.this);
                    builder.setName(((BranchInfo) msg.obj).getIndexShopName());
                    builder.setTel(((BranchInfo) msg.obj).getCustomerServicePhone());
                    builder.setAddress(((BranchInfo) msg.obj).getAddress_detail());
                    builder.setPositiveButton(new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // stub
                            if (!builder.getTel().equals("")) {
                                // Intent intent = new
                                // Intent("android.intent.action.CALL",
                                // Uri.parse("tel:"
                                // + AllInterface.formatCall(builder.getTel())));
                                // FindExpressResultActivity.this.startActivity(intent);
                                AcitivityTransUtil.showChooseTeleTypeDialog(FindExpressResultActivity.this, "",
                                        builder.getTel(), AcitivityTransUtil.NORMAI_CALL_DIALOG, "", "");
                            } else {
                                Message msg = new Message();
                                msg.what = Constants.NOT_PHONE;
                                handler.sendMessage(msg);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setBackButton(new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    if (!((FindExpressResultActivity) context).isFinishing()) {
                        builder.create().show();
                    }
                    break;

                case Constants.BRANCH_PARSE_FAILD:

                    UtilToolkit.showToast("未找到该网点详情!");
                    break;
                case Constants.PROBLEM_CAUSE:
                    int item = (Integer) msg.obj;
                    getProblemCause(item);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SKuaidiApplication.getInstance().addActivity(this);
        context = this;
        setContentView(R.layout.findexpress_result);
        company = SkuaidiSpf.getLoginUser().getExpressNo();

        getControl();
        rotate_load.show();
        rotate_load_two.show();
        findexpress();

        time = sdf.format(date);
        time = time.substring(5, time.length());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        setContentView(R.layout.findexpress_result);

        getControl();

        rotate_load.show();
        rotate_load_two.show();
        findexpress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** 使用SSO授权必须添加如下代码 */
        // UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
        // requestCode);
        // if (ssoHandler != null) {
        // ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        // }
    }

    public void getControl() {
        tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
        tv_more.setText("分享");
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("查询结果");
        skuaidiDb = SkuaidiDB.getInstanse(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        view_devider = findViewById(R.id.view_devider);
        line_devider1 = findViewById(R.id.line_devider1);
        line_devider2 = findViewById(R.id.line_devider2);
        line_devider3 = findViewById(R.id.line_devider3);
        line_devider4 = findViewById(R.id.line_devider4);
        line_devider5 = findViewById(R.id.line_devider5);
        line_devider6 = findViewById(R.id.line_devider6);
        line_devider7 = findViewById(R.id.line_devider7);
        tv_get_pay = (TextView) findViewById(R.id.tv_get_pay);
        tv_intercept = (TextView) findViewById(R.id.iv_intercept);
        tv_complain1 = (TextView) findViewById(R.id.tv_complain1);
        tv_complain2 = (TextView) findViewById(R.id.tv_complain2);
        tv_complain3 = (TextView) findViewById(R.id.tv_complain3);
        tv_complain4 = (TextView) findViewById(R.id.tv_complain4);
        tv_complain5 = (TextView) findViewById(R.id.tv_complain5);
        iv_note_delete = (ImageView) findViewById(R.id.iv_note_delete);
        iv_content_expand = (ImageView) findViewById(R.id.iv_content_expand);
        iv_content_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toExpanded){
                    show();
                    toExpanded = false;
                }else{
                    hidden();
                    toExpanded = true;
                }
            }
        });
        rl_note_detail = (RelativeLayout) findViewById(R.id.rl_note_detail);
        tv_note_detail = (TextView) findViewById(R.id.tv_note_detail);
        rl_bottom_left = (RelativeLayout) findViewById(R.id.rl_bottom_left);
        rt_bottom_info = (RelativeLayout) findViewById(R.id.rt_bottom_info);
        rt_bottom_info_fail = (RelativeLayout) findViewById(R.id.rt_bottom_info_fail);
        tv_fail_info = (TextView) findViewById(R.id.tv_fail_info);

        cust_address = (TextView) findViewById(R.id.cust_address);
        cust_address.requestFocus();
        cust_tel = (TextView) findViewById(R.id.cust_tel);
        cust_name = (TextView) findViewById(R.id.cust_name);
        call_phone = (ImageView) findViewById(R.id.call_phone);
        call_phone.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_find_express_phone"));

        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        iv_note_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skuaidiDb.updateRemark(order_number, "");
                SkuaidiSpf.saveremarks(context, "");
                rl_note_detail.setVisibility(View.GONE);
                tv_remark.setText("添加备注");
            }
        });
        /**
         * 拨号
         */
        call_phone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Toast.makeText(context,
                // "list:"+lv.getItemAtPosition(0),0).show();
                // Uri uri = Uri.parse("tel:" + cust_tel.getText().toString());
                // Intent intent = new Intent(Intent.ACTION_CALL, uri);
                // startActivity(intent);
                AcitivityTransUtil.showChooseTeleTypeDialog(FindExpressResultActivity.this, "", cust_tel.getText()
                        .toString(), AcitivityTransUtil.NORMAI_CALL_DIALOG, "", "");
            }
        });
        expressfirmname = getIntent().getStringExtra("expressfirmName");
        express = SkuaidiSpf.getLoginUser().getExpressNo();
        order_number = getIntent().getStringExtra("order_number");
//        shortUrl = "http://www.kuaidihelp.com/mobwebapp/dh?com=" + express + "&num=" + order_number;
        String code = SkuaidiSpf.getLoginUser().getExpressNo();
        ImageView iv = (ImageView) findViewById(R.id.iv_findexpress_result_brand);
        try {
            if (code.equals("fedexInter")) {
                code = "fedexinter";
            }
            int value = R.drawable.class.getDeclaredField("icon_" + code).getInt(null);
            iv.setImageResource(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_order = (TextView) findViewById(R.id.tv_findexpress_result_order);
        tv_remark = (TextView) findViewById(R.id.tv_findexpress_result_remark);
        iv_remark = (ImageView) findViewById(R.id.iv_findexpress_result_remark);

        /**
         * 判断是否有备注信息
         */
        String remark = skuaidiDb.getExpressById(order_number);
        tv_order.setText(order_number);
        if (remark.equals("")) {
            tv_remark.setText("添加备注");
            rl_note_detail.setVisibility(View.GONE);
        } else {
            tv_remark.setText("编辑备注");
            rl_note_detail.setVisibility(View.VISIBLE);
            tv_note_detail.setText("备注："+remark);
        }
        lv = (ListView) findViewById(R.id.lv_findexpress_result);
        ll_load = (LinearLayout) findViewById(R.id.ll_findexpress_load);
        ll_load_two = (LinearLayout) findViewById(R.id.ll_findexpress_load_two);
        ll_status = (LinearLayout) findViewById(R.id.ll_findexpress_result_status);
        iv_status = (ImageView) findViewById(R.id.iv_findexpress_result_status);
        tv_status = (TextView) findViewById(R.id.tv_findexpress_result_status);
        tv_handle = (TextView) findViewById(R.id.tv_findexpress_result_handle);
        sv_notfind = (ScrollView) findViewById(R.id.sv_findexpress_notfind);
        tv_brand = (TextView) findViewById(R.id.tv_findexpress_result_notfind_brand);
        ll_handle = (LinearLayout) findViewById(R.id.ll_findexpress_result_handle);
        rotate_load = (RotateLoadView) findViewById(R.id.view_rotateload);
        rotate_load_two = (RotateLoadView) findViewById(R.id.view_rotateload_two);

        ll_msg_des = (LinearLayout) findViewById(R.id.ll_msg_des);
        tv_sms_msg = (TextView) findViewById(R.id.tv_sms_msg);
        tv_msg_tag = (TextView) findViewById(R.id.tv_msg_tag);
        tv_liuyan_msg = (TextView) findViewById(R.id.tv_liuyan_msg);
        tv_sms_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, RecordDetailActivity.class);
                mIntent.putExtra("topic_id", sms_id);
                startActivity(mIntent);
            }
        });
        tv_liuyan_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LiuyanDetailActivity.class);
                String m_type = message_id.substring(0, 1);
                String m_id = message_id.substring(1);
                if ("f".equals(m_type.toLowerCase())) {
                    m_type = "customer";
                } else if ("b".equals(m_type.toLowerCase())) {
                    m_type = "branch";

                } else {
                    m_type = "order";
                }
                intent.putExtra("m_id", m_id);
                intent.putExtra("m_type", m_type);
                intent.putExtra("waybill_no", order_number);
                //为了能显示群不全部留言信息
                intent.putExtra("push", "push");
                startActivity(intent);
            }
        });
        moreScan = getIntent().getBooleanExtra("moreScan", false);
//        KuaidiApi.getShortUrl(this, handler, order_number);

    }

    /**
     * 分享
     */
    private void share() {
        UMShareManager.onEvent(context, "find_express_result_share", "find_express_result", "查快递结果:分享");
        String title = "超准查件功能";
        String targetUrl = "http://m.kuaidihelp.com/express/query?word=" + order_number + "+" + company;
        String shareText = "我正在使用准确及时的查件功能，客户流转信息在手，优质跟踪服务不愁！快来免费下载“快递员”App，创建属于你自己的服务：" + targetUrl;
        Map<String, String> shareTexts = new HashMap<String, String>();
        shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_WX, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText);
        openShare(title, shareTexts, targetUrl, R.drawable.share_check);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

			/*
             * if (moreScan) { try { skuaidiDb.insertDeliverNo(order_number,
			 * wuliuInfo.getRecord(), wuliuInfo.getFirst_time(),
			 * wuliuInfo.getStatus(), SkuaidiSpf.getremarks(this)); Intent
			 * intent = new Intent(this, FindExpressActivity.class);
			 * intent.putExtra("moreScan", true); startActivity(intent); } catch
			 * (Exception e) { e.printStackTrace(); } }
			 */

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back(View view) {
		/*
		 * if (moreScan) { try { skuaidiDb.insertDeliverNo(order_number,
		 * wuliuInfo.getStatus(), wuliuInfo.getRecord(),
		 * wuliuInfo.getFirst_time(), SkuaidiSpf.getremarks(this)); Intent
		 * intent = new Intent(this, FindExpressActivity.class);
		 * intent.putExtra("moreScan", true); startActivity(intent); } catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * }
		 */
        finish();
    }

    /**
     * 查看短信消息
     * @param view
     */
    public void getSmsDetail(View view){
        KLog.i("tag", "查看短信消息");
    }

    /**
     * 查看留言消息
     * @param view
     */
    public void getLiuyanDetail(View view){
        KLog.i("tag", "查看留言消息");
    }

    // 添加备注
    public void setremark(View view) {
        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        dialog.setTitle("添加备注");
        dialog.isUseEditText(true);
        dialog.setPositionButtonTitle("添加");
        dialog.setNegativeButtonTitle("取消");
        dialog.showEditTextTermsArea(false);
        dialog.showTermsSelect(false);
        dialog.setEditText(skuaidiDb.getExpressById(order_number));
        dialog.setEditTextHint("最多不超过20字");
        dialog.setEditTextContent(20);
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                skuaidiDb.updateRemark(order_number, dialog.getEditTextContent());
                SkuaidiSpf.saveremarks(context, dialog.getEditTextContent());
                String remark = skuaidiDb.getExpressById(order_number);

                if (remark.equals("")) {
//                    tv_order.setText(expressfirmname + order_number);
                    tv_note_detail.setText("");
                    rl_note_detail.setVisibility(View.GONE);
                    tv_remark.setText("添加备注");
                    iv_remark.setVisibility(View.VISIBLE);
                } else {
//                    tv_order.setText(remark);
                    tv_note_detail.setText("备注："+remark);
                    rl_note_detail.setVisibility(View.VISIBLE);
                    tv_remark.setText("编辑备注");
                    iv_remark.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.showDialog();
    }

    // 查询失败 刷新事件 加载动画
    public void handle(View view) {

        if (wuliuInfo.getWuliuItems() == null || wuliuInfo.getWuliuItems().size() == 0) {
            ll_load.setVisibility(View.VISIBLE);
            ll_load_two.setVisibility(View.VISIBLE);
            sv_notfind.setVisibility(View.GONE);
            ll_status.setVisibility(View.GONE);
            rotate_load.show();
            rotate_load_two.show();
            findexpress();
        } else if ("1".equals(wuliuInfo.getIsException())) {
            Intent intent = new Intent();
            intent.putExtra("express", express);
            intent.putExtra("order_number", order_number);
            intent.putExtra("name", wuliuInfo.getExceptionName());
            intent.putExtra("type", wuliuInfo.getExceptionType());
            intent.putExtra("reason", wuliuInfo.getExceptionReason());
            intent.putExtra("exception_id", wuliuInfo.getExceptionId());
            intent.putExtra("message", wuliuInfo.getExceptionMessage());
            intent.putExtra("home_shop_id", wuliuInfo.getHomeShopId());
            intent.putExtra("customer_service_phone", wuliuInfo.getCustomerServicePhole());

            intent.setClass(this, FindExpressExceptionActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 获取收件人信息接口
     */
    private void GetUserinfo() {
        JSONObject object = new JSONObject();
        try {
            object.put("sname", "express.contacts");
            object.put("pname", "androids");
            object.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
            object.put("no", order_number);
            object.put("switch", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
    }

    /**
     * 获取收件人标签信息接口
     */
    private void getUserTag(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("sname", EXPRESS_ORDER_TAG);
            obj.put("waybillNo", order_number);
            httpInterfaceRequest(obj, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 中通问题件获取问题原因详细描述
     */
    private void getProblemCause(int itemIndex) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", EXPRESS_PROBLEM_INFO);
            data.put("express_no", order_number);
            data.put("item", itemIndex);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    public void web_express(View view) {
        Intent intent = new Intent();
        intent.putExtra("type", express);
        intent.setClass(this, LoadWebActivity.class);
        startActivity(intent);
    }

    // 调用查快递接口
    public void findexpress() {
        KuaidiApi.findExpress(this, handler, order_number);
    }

    /**
     * 查询收件人信息的回调
     */
    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (result == null) {
            return;
        }
        if (EXPRESS_PROBLEM_INFO.equals(sname)) {
            String problem_cause = result.optString("reason");
            if (!"".equals(problem_cause)) {
                showCauseInfo(problem_cause);
            }
        }else if(EXPRESS_ORDER_TAG.equals(sname)){
            JSONObject obj = result.optJSONObject("info");
            pay = obj.optString("pay");
            intercept = obj.optString("intercept");
            tousu = obj.optString("tousu");
            complain = obj.optString("complain");
            noBox = obj.optString("noBox");
            sign = obj.optString("sign");
            send = obj.optString("send");
            hidden();

            if(!TextUtils.isEmpty(obj.optString("message"))){
                sms_id = obj.optString("message");
                ll_msg_des.setVisibility(View.VISIBLE);
                tv_sms_msg.setVisibility(View.VISIBLE);
                tv_msg_tag.setVisibility(View.GONE);
                tv_liuyan_msg.setVisibility(View.GONE);
            }
            if(!TextUtils.isEmpty(obj.optString("liuyan"))){
                message_id = obj.optString("liuyan");
                ll_msg_des.setVisibility(View.VISIBLE);
                tv_msg_tag.setVisibility(tv_sms_msg.getVisibility() == View.VISIBLE?View.VISIBLE:View.GONE);
                tv_liuyan_msg.setVisibility(View.VISIBLE);
            }
        }else {
            JSONObject userinfo = null;
            JSONArray array = result.optJSONArray("retArr");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    userinfo = array.optJSONObject(i);
                }
            }
            if (userinfo != null) {
                // 设置页面的收件人信息
                String user_name = userinfo.optString("rec_name");
                String user_address = userinfo.optString("address");
                String user_mobile = userinfo.optString("rec_mobile");

                if (TextUtils.isEmpty(user_mobile) && TextUtils.isEmpty(user_address)) {
                    rt_bottom_info.setVisibility(View.GONE);
                    view_devider.setVisibility(total_num > 0 ? View.VISIBLE : View.GONE);
                } else {
                    rt_bottom_info.setVisibility(View.VISIBLE);
                    view_devider.setVisibility(View.VISIBLE);
                }

                // 催件短信的信息
                deliver.setRecipient_name(user_name);
                deliver.setRecipient_phone(user_mobile);
                deliver.setRecipient_address(user_address);
                deliver.setExpress_no(order_number);
                deliver.setCourier_phone(wuliuInfo.getDiliver_phone());

                cust_name.setText(TextUtils.isEmpty(user_name) ? cust_name.getText() + "" : cust_name.getText()
                        + user_name);
                cust_tel.setText(StringUtil.isEmpty(user_mobile));
                cust_address.setText(TextUtils.isEmpty(user_address) ? cust_address.getText() + "" :cust_address.getText()
                        + user_address);
                if (TextUtils.isEmpty(user_mobile) || user_mobile.contains("*")) {
                    call_phone.setVisibility(View.GONE);
                } else {
                    call_phone.setVisibility(View.VISIBLE);
                }

            } else {
                call_phone.setVisibility(View.GONE);
            }
        }
    }

    private void show(){
        total_num = 0;
        if(!TextUtils.isEmpty(intercept)){
            total_num++;
            line_devider2.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_intercept.setVisibility(View.VISIBLE);
            tv_intercept.setText(intercept);
        }
        if(!TextUtils.isEmpty(pay)){
            total_num++;
            line_devider1.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_get_pay.setVisibility(View.VISIBLE);
            tv_get_pay.setText(pay);
        }
        if(!TextUtils.isEmpty(tousu)){
            total_num++;
            line_devider3.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_complain1.setText(tousu);
            tv_complain1.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(complain)){
            total_num++;
            line_devider4.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_complain2.setText(complain);
            tv_complain2.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(noBox)){
            total_num++;
            line_devider5.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_complain3.setText(noBox);
            tv_complain3.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(sign)){
            total_num++;
            line_devider6.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_complain4.setText(sign);
            tv_complain4.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(send)){
            total_num++;
            line_devider7.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_complain5.setText(send);
            tv_complain5.setVisibility(View.VISIBLE);
        }
        iv_content_expand.setImageResource(R.drawable.dispatch_dropmenuoff_icon);
    }
    private void hidden(){
        total_num = 0;
        if(!TextUtils.isEmpty(intercept)){
            total_num++;
            line_devider2.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_intercept.setVisibility(View.VISIBLE);
            tv_intercept.setText(intercept);
        }
        if(!TextUtils.isEmpty(pay)){
            total_num++;
            line_devider1.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_get_pay.setVisibility(View.VISIBLE);
            tv_get_pay.setText(pay);
        }
        if(!TextUtils.isEmpty(tousu)){
            total_num ++;
            line_devider3.setVisibility(total_num == 1 ? View.GONE : View.VISIBLE);
            tv_complain1.setText(tousu);
            tv_complain1.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(complain)){
            total_num++;
            line_devider4.setVisibility(total_num > 3 || total_num ==1 ?View.GONE:View.VISIBLE);
            tv_complain2.setText(complain);
            tv_complain2.setVisibility(total_num > 3 ?View.GONE:View.VISIBLE);
        }
        if(!TextUtils.isEmpty(noBox)){
            total_num++;
            line_devider5.setVisibility(total_num > 3 || total_num ==1 ?View.GONE:View.VISIBLE);
            tv_complain3.setText(noBox);
            tv_complain3.setVisibility(total_num > 3 ?View.GONE:View.VISIBLE);
        }
        if(!TextUtils.isEmpty(sign)){
            total_num++;
            line_devider6.setVisibility(total_num > 3 || total_num == 1 ?View.GONE:View.VISIBLE);
            tv_complain4.setText(sign);
            tv_complain4.setVisibility(total_num > 3 ?View.GONE:View.VISIBLE);
        }
        if(!TextUtils.isEmpty(send)){
            total_num++;
            line_devider7.setVisibility(total_num > 3 || total_num == 1 ?View.GONE:View.VISIBLE);
            tv_complain5.setText(send);
            tv_complain5.setVisibility(total_num > 3 ?View.GONE:View.VISIBLE);
        }
        if(total_num > 3){
            iv_content_expand.setImageResource(R.drawable.dispatch_dropmenu_icon);
            iv_content_expand.setVisibility(View.VISIBLE);
        }else{
            iv_content_expand.setVisibility(View.GONE);
        }
        view_devider.setVisibility(total_num > 0 || rt_bottom_info.getVisibility() == View.VISIBLE? View.VISIBLE : View.GONE);
    }

    /**
     * 显示问题件原因详情的弹窗
     *
     * @param problem_cause
     */
    private void showCauseInfo(String problem_cause) {

        final SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
        dialog.setTitleGray("问题件原因");
        dialog.setTitleSkinColor("main_color");
        dialog.setContentGray(problem_cause);
        dialog.setMiddleButtonTextGray("确定");
        dialog.isUseMiddleBtnStyle(true);
        if (!isFinishing())
            dialog.showDialogGray(rl_bottom_left);
        dialog.setMiddleButtonClickListenerGray(new MiddleButtonOnclickListenerGray() {

            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        if (code.equals("7") && null != result) {
            try {
                String desc = result.optString("desc");
                UtilToolkit.showToast(desc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (searchPhoneShowTishi != null) {
            searchPhoneShowTishi.dismiss();
            searchPhoneShowTishi = null;
        }
        super.onDestroy();
    }

}