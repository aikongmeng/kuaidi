package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FindExpressResultAdapter;
import com.kuaibao.skuaidi.activity.model.Deliver;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.activity.view.ExpressInfoDialog;
import com.kuaibao.skuaidi.activity.view.HurryOrderMenuDialog;
import com.kuaibao.skuaidi.activity.view.NotifySearchPhoneShowTishi;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.BottomScrollView;
import com.kuaibao.skuaidi.common.view.MyListView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.MiddleButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dispatch.bean.TagNotice;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.entry.ClickItem;
import com.kuaibao.skuaidi.entry.WuliuInfo;
import com.kuaibao.skuaidi.entry.WuliuItem;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 查快递结果——在C端基础上修改而来
 */
public class CopyOfFindExpressResultActivity extends SkuaiDiBaseActivity {

    public static final String DELIVERY_GET_EXPRESS_DETAILS_NOTICE = "delivery.getExpressDetailsNotice";
    private static final String EXPRESS_PROBLEM_INFO = "express.problem_info";
    private String express;
    private String order_number;
    private SkuaidiDB skuaidiDb;
    private FindExpressResultAdapter adapter;
    private WuliuInfo wuliuInfo = new WuliuInfo();
    private final int EXCEPTION_DATA = 3;
    private final int NOT_PHONE = 4;
    private final int NOT_INFO = 5;

    // 生成时间，查询失败插入数据库的 ‘ 第一次查询时间’
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String time;
    private Deliver deliver = new Deliver();
    protected Context context;

    private String company;
    private NotifySearchPhoneShowTishi searchPhoneShowTishi;
    public static final String DELIVERY_WRITE_NOTES = "delivery.writeNotes";//添加备注
    //最近一次添加的备注
    private String lastRemark;
    private String remarkActionType;
    private boolean isSvToBottom = false;
    private float mLastY;
    @BindView(R.id.lv_tousu)
    ListView lvTousu;
    @BindView(R.id.tv_daofu)
    TextView tvDaofu;
    @BindView(R.id.line_below_lan)
    View lineBelowLan;
    @BindView(R.id.line_below_huo)
    View lineBelowHuo;
    @BindView(R.id.scroll_view)
    BottomScrollView scrollView;

    @BindView(R.id.tv_intercept)
    TextView tvIntercept;
    @BindView(R.id.tv_new_message)
    TextView tvNewMessage;
    @BindView(R.id.line_above_message)
    View line_above_message;
    @BindView(R.id.tv_weight)
    TextView tv_weight;
    @BindView(R.id.rl_remark)
    RelativeLayout rl_remark;
    @BindView(R.id.tv_add_remark)
    TextView tv_add_remark;
    @BindView(R.id.tv_delete)
    ImageView tv_delete;

    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.rt_bottom_info)
    RelativeLayout rt_bottom_info;
    // 收件人信息 name,tel,address
    @BindView(R.id.cust_address)
    TextView cust_address;
    @BindView(R.id.cust_tel)
    TextView cust_tel;
    @BindView(R.id.cust_name)
    TextView cust_name;

    @BindView(R.id.line_below_info)
    View line_below_info;
    @BindView(R.id.tv_findexpress_result_order)
    TextView tv_order;
    @BindView(R.id.tv_remark)
    TextView tv_remark;

    @BindView(R.id.lv_findexpress_result)
    MyListView listView;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.iv_findexpress_result_brand)
    ImageView ivIcon;


    private TagNotice tagNotice;
    /**
     * listview竖向滑动的阈值
     */
    private static final int THRESHOLD_Y_LIST_VIEW = 20;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                // 查询请求成功
                case Constants.FIND_EXPRESS_GET_OK:
                    JsonXmlParser.parseFindExpress(handler, msg.obj.toString());

                    break;
                // 快递查询失败
                case Constants.FIND_EXPRESS_PAISE_FAID:
                    tv_status.setText(String.format(getString(R.string.latest_status), "无记录"));
                    setStatusStyle();
                    if (express.equals("fedexInter") || express.equals("fedex") || express.equals("hq")) {
                    } else {
                    }

                    /**
                     * 查询失败 把记录保存到数据库
                     */
                    skuaidiDb.insertExpressHistory(order_number, "", "", time.substring(0, 11));
                    skuaidiDb.insertDeliverNo(order_number, "", "", time.substring(0, 11), "");

                    break;
                // 快递查询成功
                case Constants.FIND_EXPRESS_PAISE_OK:
                    wuliuInfo = (WuliuInfo) msg.obj;

                    // 如果物流信息为空
                    if (wuliuInfo.getWuliuItems() == null || wuliuInfo.getWuliuItems().size() <= 0) {
                        tv_status.setText(String.format(getString(R.string.latest_status), "无记录"));
                        if (express.equals("fedexInter") || express.equals("fedex") || express.equals("hq")) {
                        } else {
                        }
                        skuaidiDb.insertExpressHistory(order_number, "", "", time.substring(0, 11));
                        skuaidiDb.insertDeliverNo(order_number, "", "", time.substring(0, 11), "");

                    } else {// 物流信息不为空
                        skuaidiDb.insertExpressHistory(order_number, wuliuInfo.getStatus(), wuliuInfo.getRecord(),
                                wuliuInfo.getFirst_time());
                        skuaidiDb.insertDeliverNo(order_number, wuliuInfo.getStatus(), wuliuInfo.getRecord(),
                                wuliuInfo.getFirst_time(), "");

                        if ("1".equals(wuliuInfo.getIsException())) {
                            tv_status.setText(String.format(getString(R.string.latest_status), "出现异常"));
                        } else {
                            if ("collected".equals(wuliuInfo.getStatus())) {
                                tv_status.setText(String.format(getString(R.string.latest_status), "已取件"));
                            } else if ("sending".equals(wuliuInfo.getStatus()) || "arrived".equals(wuliuInfo.getStatus())) {
                                tv_status.setText(String.format(getString(R.string.latest_status), "运送中"));
                            } else if ("delivering".equals(wuliuInfo.getStatus())) {
                                tv_status.setText(String.format(getString(R.string.latest_status), "派送中"));
                            } else if ("signed".equals(wuliuInfo.getStatus())) {
                                tv_status.setText(String.format(getString(R.string.latest_status), "已签收"));
                            } else if ("question".equals(wuliuInfo.getStatus())) {
                                tv_status.setText(String.format(getString(R.string.latest_status), "问题件"));
                            } else {
                                tv_status.setText("最新状态：");
                            }
                        }

                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(adapter);

                        List<WuliuItem> wuliulist = wuliuInfo.getWuliuItems();
                        Collections.reverse(wuliulist);
                        adapter = new FindExpressResultAdapter(CopyOfFindExpressResultActivity.this, wuliulist,
                                wuliuInfo.getIsException(), handler);
                        listView.setAdapter(adapter);

                    }
                    setStatusStyle();
                    break;
                // 快递查询请求失败
                case Constants.FIND_EXPRESS_GET_FAID:
                    tv_status.setText(String.format(getString(R.string.latest_status), "无记录"));
                    if (express.equals("fedexInter") || express.equals("fedex") || express.equals("hq")) {
                    } else {
                    }
                    setStatusStyle();
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
                    KuaidiApi.getBranchInfo(CopyOfFindExpressResultActivity.this, handler, ((ClickItem) msg.obj).getExpressId());
                    break;
                case Constants.GET_SENDER_INFO:// 获取派件员的信息

                    final String phoneString = wuliuInfo.getDiliver_phone();
                    if (!phoneString.trim().equals("") && !TextUtils.isEmpty(phoneString)) {

                        final HurryOrderMenuDialog.Builder senderbuilder = new HurryOrderMenuDialog.Builder(
                                CopyOfFindExpressResultActivity.this);
                        // 联系方式非手机号则 发短信功能隐藏
                        HurryOrderMenuDialog.flag = phoneString.length() == 11 && phoneString.startsWith("1");

                        // 打电话
                        senderbuilder.setCallButton(new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AcitivityTransUtil
                                        .showChooseTeleTypeDialog(CopyOfFindExpressResultActivity.this, "", phoneString, AcitivityTransUtil.NORMAI_CALL_DIALOG, "", "");
                                dialog.dismiss();

                            }
                        });
                        // 发短信
                        senderbuilder.setMessageButton(new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CopyOfFindExpressResultActivity.this, HurryOrderActivity.class);
                                SKuaidiApplication.getInstance().postMsg("HurryOrderActivity", "deliver_info", deliver);
                                CopyOfFindExpressResultActivity.this.startActivity(intent);
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
                    JsonXmlParser.parseBranchInfo(context, handler, msg.obj.toString());
                    break;

                case Constants.BRANCH_GET_FAILD:
                    UtilToolkit.showToast("网络异常");
                    break;

                case Constants.BRANCH_PARSE_OK:

                    final ExpressInfoDialog.Builder builder = new ExpressInfoDialog.Builder(CopyOfFindExpressResultActivity.this);
                    builder.setName(((BranchInfo) msg.obj).getIndexShopName());
                    builder.setTel(((BranchInfo) msg.obj).getCustomerServicePhone());
                    builder.setAddress(((BranchInfo) msg.obj).getAddress_detail());
                    builder.setPositiveButton(new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // stub
                            if (!builder.getTel().equals("")) {
                                AcitivityTransUtil.showChooseTeleTypeDialog(CopyOfFindExpressResultActivity.this, "",
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
                    if (!((CopyOfFindExpressResultActivity) context).isFinishing()) {
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
            dismissProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.copyfindexpress_result);
        ButterKnife.bind(this);
        company = SkuaidiSpf.getLoginUser().getExpressNo();
        order_number = getIntent().getStringExtra("order_number");
        getExpressDetailsNotice(order_number);
        initView();
        findexpress();
        // 获取收件人信息
        GetUserinfo();
        time = sdf.format(date);
        time = time.substring(5, time.length());

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
    protected void onDestroy() {
        if (searchPhoneShowTishi != null) {
            searchPhoneShowTishi.dismiss();
            searchPhoneShowTishi = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setContentView(R.layout.findexpress_result);
        initView();
        findexpress();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void initView() {

        tvMore.setText("分享");


        tv_title_des.setText("运单详情");
        skuaidiDb = SkuaidiDB.getInstanse(this);

        cust_address.requestFocus();

        /**
         * 拨号
         */
        cust_tel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AcitivityTransUtil.showChooseTeleTypeDialog(CopyOfFindExpressResultActivity.this, "", cust_tel.getText()
                        .toString(), AcitivityTransUtil.NORMAI_CALL_DIALOG, "", "");
            }
        });
        express = SkuaidiSpf.getLoginUser().getExpressNo();
        String code = SkuaidiSpf.getLoginUser().getExpressNo();

        try {
            if (code.equals("fedexInter")) {
                code = "fedexinter";
            }
            int value = R.drawable.class.getDeclaredField("icon_" + code).getInt(null);
            ivIcon.setImageResource(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_order.setText(order_number);
        /**
         * 判断是否有备注信息
         */
        String remark = null;
        if (getIntent().hasExtra("remark")) {
            remark = getIntent().getStringExtra("remark");
        }
        if (TextUtils.isEmpty(remark)) {
            remark = skuaidiDb.getExpressById(order_number);
        }
        if (!TextUtils.isEmpty(remark)) {
            tv_remark.setText(String.format(getString(R.string.dispatch_remark), remark));
            rl_remark.setVisibility(View.VISIBLE);
            tv_add_remark.setText("编辑备注");
        } else {
            rl_remark.setVisibility(View.GONE);
        }

        listView.setParentScrollView(scrollView);
        View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_view, (ViewGroup) listView.getParent(), false);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        ((TextView) emptyView.findViewById(R.id.tv_msg_empty)).setText("暂无物流信息");
        listView.setEmptyView(emptyView);

        scrollView.setScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
            @Override
            public void onScrollToBottom() {
                isSvToBottom = true;
            }

            @Override
            public void onNotScrollToBottom() {
                isSvToBottom = false;
            }
        });
        // ListView滑动冲突解决
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mLastY = event.getY();
                }
                if (action == MotionEvent.ACTION_MOVE) {
                    int top = listView.getChildAt(0).getTop();
                    float nowY = event.getY();
                    if (!isSvToBottom) {
                        // 允许scrollview拦截点击事件, scrollView滑动
                        scrollView.requestDisallowInterceptTouchEvent(false);
                    } else if (top == 0 && nowY - mLastY > THRESHOLD_Y_LIST_VIEW) {
                        // 允许scrollview拦截点击事件, scrollView滑动
                        scrollView.requestDisallowInterceptTouchEvent(false);
                    } else {
                        // 不允许scrollview拦截点击事件， listView滑动
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 显示标签
     */
    private void showTagNotice() {
        if (tagNotice != null && tagNotice.getInfo() != null) {
            if (!TextUtils.isEmpty(tagNotice.getInfo().getPay())) {
                tvDaofu.setVisibility(View.VISIBLE);
                tvDaofu.setText(tagNotice.getInfo().getPay());
            }
            if (!TextUtils.isEmpty(tagNotice.getInfo().getIntercept())) {
                tvIntercept.setVisibility(View.VISIBLE);
                tvIntercept.setText(tagNotice.getInfo().getIntercept());
            }

            String tousu = tagNotice.getInfo().getTousu();
            String complain = tagNotice.getInfo().getComplain();
            String nobox = tagNotice.getInfo().getNoBox();
            String sign = tagNotice.getInfo().getSign();
            String send = tagNotice.getInfo().getSend();
            final List<String> tousuList = new ArrayList<>();

            if (!TextUtils.isEmpty(complain)) {
                tousuList.add(complain);
            }

            if (!TextUtils.isEmpty(sign)) {
                tousuList.add(sign);
            }
            if (!TextUtils.isEmpty(nobox)) {
                tousuList.add(nobox);
            }
            if (!TextUtils.isEmpty(send)) {
                tousuList.add(send);
            }
            if (!TextUtils.isEmpty(tousu)) {
                tousuList.add(tousu);
            }
            if (tousuList != null && tousuList.size() != 0) {
                TousuAdapter adapter = new TousuAdapter(context, tousuList);
                lvTousu.setVisibility(View.VISIBLE);
                lvTousu.setAdapter(adapter);
            }
            if (tvIntercept.getVisibility() == View.VISIBLE) {
                if (tvDaofu.getVisibility() == View.VISIBLE || lvTousu.getVisibility() == View.VISIBLE) {
                    lineBelowLan.setVisibility(View.VISIBLE);
                }
            } else if (tvDaofu.getVisibility() == View.VISIBLE) {
                if (lvTousu.getVisibility() == View.VISIBLE) {
                    lineBelowHuo.setVisibility(View.VISIBLE);
                }

            }

        }
    }


    /**
     * 初始化底部短信和留言通知
     */
    private void initNotifycationView() {
        if (tagNotice == null || tagNotice.getInfo() == null) {
            return;
        }
        if (!TextUtils.isEmpty(tagNotice.getInfo().getLiuyan()) && !TextUtils.isEmpty(tagNotice.getInfo().getMessage())) {
            String msg = "您有新的短信消息及留言消息 >";
            SpannableStringBuilder sb = new SpannableStringBuilder(msg);
            setMessageSpan(tagNotice, msg, sb);
            setLiuyanSpan(tagNotice, msg, sb);
            tvNewMessage.setMovementMethod(LinkMovementMethod.getInstance());
            tvNewMessage.setText(sb);
            tvNewMessage.setHighlightColor(getResources().getColor(R.color.trans));
            tvNewMessage.setVisibility(View.VISIBLE);
            line_above_message.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(tagNotice.getInfo().getMessage())) {
            String msg = "您有新的短信消息 >";
            SpannableStringBuilder sb = new SpannableStringBuilder(msg);
            setMessageSpan(tagNotice, msg, sb);
            tvNewMessage.setMovementMethod(LinkMovementMethod.getInstance());
            tvNewMessage.setText(sb);
            tvNewMessage.setHighlightColor(getResources().getColor(R.color.trans));
            tvNewMessage.setVisibility(View.VISIBLE);
            line_above_message.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(tagNotice.getInfo().getLiuyan())) {
            String msg = "您有新的留言消息 >";
            SpannableStringBuilder sb = new SpannableStringBuilder(msg);
            setLiuyanSpan(tagNotice, msg, sb);
            tvNewMessage.setMovementMethod(LinkMovementMethod.getInstance());
            tvNewMessage.setHighlightColor(getResources().getColor(R.color.trans));
            tvNewMessage.setText(sb);
            tvNewMessage.setVisibility(View.VISIBLE);
            line_above_message.setVisibility(View.VISIBLE);
        } else {
            tvNewMessage.setVisibility(View.GONE);
            line_above_message.setVisibility(View.GONE);
        }
    }

    private void setMessageSpan(final TagNotice notice, String msg, SpannableStringBuilder sb) {
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent mIntent = new Intent(CopyOfFindExpressResultActivity.this, RecordDetailActivity.class);
                if (notice.getInfo() != null) {
                    mIntent.putExtra("topic_id", String.valueOf(notice.getInfo().getMessage()));
                    startActivity(mIntent);
                }

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.green_39b54a));
                ds.setUnderlineText(false); //去掉下划线
            }

        }, msg.indexOf("短信消息"), msg.indexOf("短信消息") + "短信消息".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 留言显示样式
     *
     * @param notice
     * @param msg
     * @param sb
     */
    private SpannableStringBuilder setLiuyanSpan(final TagNotice notice, String msg, SpannableStringBuilder sb) {
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                Intent intent = new Intent(CopyOfFindExpressResultActivity.this, LiuyanDetailActivity.class);
                String m_type = notice.getInfo().getLiuyan().substring(0, 1);
                String m_id = notice.getInfo().getLiuyan().substring(1);
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
//                        intent.putExtra("post_timestramp", messageList.getPost_timestramp());
//                        intent.putExtra("waybill_no", messageList.getWaybill_no());
//                        if (!"record".equals(messageList.getTopic_cate())) {
//                            intent.putExtra("mix_content", messageList.getMix_content());
//                        }
//                        intent.putExtra("attachs", messageList.getAttachs());
//                        intent.putExtra("user_phone", messageList.getUser_phone());
//                        intent.putExtra("post_username", messageList.getPost_username());
//                        intent.putExtra("messageList", messageList);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.blue_099fff));
                ds.setUnderlineText(false); //去掉下划线
            }
        }, msg.indexOf("留言消息"), msg.indexOf("留言消息") + "留言消息".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
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
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 查询失败 刷新事件 加载动画
    public void handle(View view) {

        if (wuliuInfo.getWuliuItems() == null || wuliuInfo.getWuliuItems().size() == 0) {
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
        new Thread(new Runnable() {

            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("sname", "express.contacts");
                    object.put("pname", "androids");
                    object.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
                    object.put("no", order_number);
                    object.put("switch", 2);
                    httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

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
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void web_express(View view) {
        Intent intent = new Intent();
        intent.putExtra("type", express);
        intent.setClass(this, LoadWebActivity.class);
        startActivity(intent);
    }

    // 调用查快递接口
    public void findexpress() {
        showProgressDialog("");//this, "加载中...", true);
        KuaidiApi.findExpress(this, handler, order_number);
    }

    /**
     * 查询收件人信息的回调
     */
    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {

        if (EXPRESS_PROBLEM_INFO.equals(sname)) {
            JSONObject result = null;
            try {
                result = new JSONObject(json);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (result == null) {
                return;
            }
            String problem_cause = result.optString("reason");
            if (!TextUtils.isEmpty(problem_cause)) {
                showCauseInfo(problem_cause);
            }
        } else if (DELIVERY_WRITE_NOTES.equals(sname)) {
            if ("delete".equals(remarkActionType)) {
                UtilToolkit.showToast("备注删除成功");
            } else {
                UtilToolkit.showToast("备注添加成功");
            }
            skuaidiDb.updateRemark(order_number, lastRemark);
            SkuaidiSpf.saveremarks(context, lastRemark);
            tv_remark.setText(String.format(getString(R.string.dispatch_remark), lastRemark));
            if (TextUtils.isEmpty(lastRemark)) {
                rl_remark.setVisibility(View.GONE);
                tv_add_remark.setText("添加备注");
            } else {
                tv_add_remark.setText("编辑备注");
                rl_remark.setVisibility(View.VISIBLE);
            }
            setResult(103);
        } else if (DELIVERY_GET_EXPRESS_DETAILS_NOTICE.equals(sname)) {
            tagNotice = JSON.parseObject(json, TagNotice.class);
            String weight = tagNotice.getInfo().getWeight();
            if (TextUtils.isEmpty(weight) || "0".equals(weight)) {
                tv_weight.setVisibility(View.GONE);
            } else {
                tv_weight.setVisibility(View.VISIBLE);
                tv_weight.setText(weight);
            }
            initNotifycationView();
            showTagNotice();
        } else {
            JSONObject result = null;
            try {
                result = new JSONObject(json);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (result == null) {
                return;
            }
            JSONObject userInfo = null;
            JSONArray array = result.optJSONArray("retArr");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    userInfo = array.optJSONObject(i);
                }
            }
            if (userInfo != null) {
                // 设置页面的收件人信息
                String user_name = userInfo.optString("rec_name");
                String user_address = userInfo.optString("address");
                String user_mobile = userInfo.optString("rec_mobile");
                if (TextUtils.isEmpty(user_mobile) && TextUtils.isEmpty(user_address)) {
                    rt_bottom_info.setVisibility(View.GONE);
                    line_below_info.setVisibility(View.GONE);
                } else {
                    rt_bottom_info.setVisibility(View.VISIBLE);
                    line_below_info.setVisibility(View.VISIBLE);
                }

                // 催件短信的信息
                deliver.setRecipient_name(user_name);
                deliver.setRecipient_phone(user_mobile);
                deliver.setRecipient_address(user_address);
                deliver.setExpress_no(order_number);
                deliver.setCourier_phone(wuliuInfo.getDiliver_phone());
                cust_name.setText(String.format(getString(R.string.addressee), user_name));
                if (TextUtils.isEmpty(user_mobile)) {
                    cust_tel.setVisibility(View.GONE);
                } else {
                    cust_tel.setVisibility(View.VISIBLE);
                    cust_tel.setText(user_mobile);
                    if (user_mobile.contains("*")) {
                        cust_tel.setCompoundDrawables(null, null, null, null);
                        cust_tel.setOnClickListener(null);
                    }
                }
                cust_address.setText(String.format(getString(R.string.addressee_address), user_address));
            } else {
                rt_bottom_info.setVisibility(View.GONE);
                line_below_info.setVisibility(View.GONE);
            }
        }
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
            dialog.showDialogGray(rt_bottom_info);
        dialog.setMiddleButtonClickListenerGray(new MiddleButtonOnclickListenerGray() {

            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (DELIVERY_WRITE_NOTES.equals(sname)) {
            UtilToolkit.showToast("备注添加失败");
        }
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

    @OnClick({R.id.tv_add_remark, R.id.tv_delete, R.id.tv_more,R.id.iv_title_back})
    public void onClick(View v) {
        switch (v.getId()) {
            //添加备注
            case R.id.tv_add_remark:
                String text = tv_remark.getText().toString().trim();
                if (!TextUtils.isEmpty(text) && text.length() >= 3) {
                    text = text.substring(3);
                }
                showRemarkDialog(text);
                break;
            case R.id.tv_delete:
                addRemark("");
                lastRemark = "";
                break;
            case R.id.tv_more:
                share();
                break;
            case R.id.iv_title_back:
                finish();
                break;
        }
    }

    /**
     * 设置状态颜色
     */
    private void setStatusStyle() {
        if ("最新状态：已签收".equals(tv_status.getText().toString().trim())) {
            SpannableStringBuilder sb = new SpannableStringBuilder(tv_status.getText().toString().trim());
            sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green_39b54a)),
                    5, tv_status.getText().toString().trim().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_status.setText(sb);
        }
    }

    /**
     * 添加备注对话框
     *
     * @param remark 已经存在的备注
     */
    private void showRemarkDialog(String remark) {
        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        if (!TextUtils.isEmpty(remark)) {
            dialog.setTitle("编辑备注");
        } else {
            dialog.setTitle("添加备注");
        }
        dialog.setEditTextHint("最多不超过20字");
        if (!TextUtils.isEmpty(remark)) {
            dialog.setEditText(remark);
        }
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                String text = dialog.getEditTextContent().trim();
                if (!TextUtils.isEmpty(text) && text.length() > 20) {
                    UtilToolkit.showToast("最多不超过20字");
                } else {
                    lastRemark = text;
                    addRemark(lastRemark);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /**
     * 添加备注
     *
     * @param remark 备注内容
     */
    private void addRemark(String remark) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", DELIVERY_WRITE_NOTES);
            data.put("waybillNo", order_number);
            data.put("brand", company);
            data.put("empNo", E3SysManager.getCourierNO());
            data.put("notes", remark);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            if (TextUtils.isEmpty(remark)) {
                remarkActionType = "delete";
            } else {
                remarkActionType = "add";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取标签
     *
     * @param number 单号
     */
    private void getExpressDetailsNotice(String number) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", DELIVERY_GET_EXPRESS_DETAILS_NOTICE);
            data.put("waybillNo", number);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class TousuAdapter extends BaseAdapter {
        private List<String> sourcList;
        private List<String> adapterList;

        TousuAdapter(Context context, List<String> list) {
            sourcList = list;
            adapterList = sourcList.subList(0, 1);
        }

        @Override
        public int getCount() {
            return adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tousu, null);
            }
            TextView tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            tv_text.setText(adapterList.get(position));
            View down = convertView.findViewById(R.id.iv_down);
            View up = convertView.findViewById(R.id.iv_up);
            if (sourcList.size() > adapterList.size()) {
                down.setVisibility(View.VISIBLE);
                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapterList = sourcList;
                        notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(lvTousu);
                    }
                });
            } else {
                if (adapterList.size() > 1) {
                    if (position == adapterList.size() - 1) {
                        up.setVisibility(View.VISIBLE);
                        down.setVisibility(View.GONE);
                        up.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapterList = sourcList.subList(0, 1);
                                notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(lvTousu);
                            }
                        });
                    } else {
                        up.setVisibility(View.GONE);
                        down.setVisibility(View.GONE);
                    }
                } else {
                    down.setVisibility(View.GONE);
                }
            }


            return convertView;
        }
    }


}