package com.kuaibao.skuaidi.activity.smsrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.smsrecord.adapter.RecordSmsSendFailAndNoReceiveAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.DrawableLeftWithTextViewCenter;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 发送失败|未取件 记录界面【短信记录】
 **/
public class RecordSmsSendFailAndNoReceiveActivity extends SkuaiDiBaseActivity implements View.OnClickListener, RecordSmsSendFailAndNoReceiveAdapter.OnclickListener {

    private final int GET_LIST_SUCCESS = 0x1001;// 获取列表成功
    private final int REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY = 0x1005;// 进入详情界面

    private ListView list;// 列表
    private TextView noData;
    private LinearLayout resend_area;// 重发按钮区域

    private Context mContext;
    private Intent mIntent;
    private List<SmsRecord> smsRecords = new ArrayList<>();
    private RecordSmsSendFailAndNoReceiveAdapter adapter;

    private int page_num = 1;
    private int total_page = 0;// 总页数
    private int total_records = 0;// 总条数
    private String status;// 状态【用于区分发送失败还是未取件-调用接口使用】
    private boolean loading = true;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list.setVisibility(View.GONE);
                    resend_area.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    dismissProgressDialog();
                    break;
                case GET_LIST_SUCCESS:
                    list.setVisibility(View.VISIBLE);
                    resend_area.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                    if (page_num == 1) {
                        smsRecords.clear();
                        smsRecords = (List<SmsRecord>) msg.obj;
                    } else {
                        smsRecords.addAll((List<SmsRecord>) msg.obj);
                    }
                    page_num++;
                    // 加载完全部数据
                    if (page_num <= total_page) {
                        getDeliveryList(page_num);
                    } else {
                        dismissProgressDialog();
                        UtilToolkit.showToast("数据加载完成");
                    }
                    adapter.notifyDataChanged(smsRecords);

                    break;
                case Constants.GET_SIGN_IN_STATUS_SUCCESS:
                    int position = msg.arg1;// 获取点击条目的下标
                    String getSingleValue = smsRecords.get(position).getSigned();// 获取列表集合里面被点击条目的签收标记
                    if (getSingleValue.equals("0")) {
                        smsRecords.get(position).setSigned("1");// 将未签收标记设置成签收状态
                    } else {
                        smsRecords.get(position).setSigned("0");
                    }
                    adapter.notifyDataSetChanged();
                    EventBus.getDefault().post(smsRecords.get(position));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_send_fail);
        mContext = this;
        EventBus.getDefault().register(this);
        status = getIntent().getStringExtra("status");
        initView();
        initListener();
        getDeliveryList(page_num);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY) {
            adapter.notifyDataSetChanged();
        }
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
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        SkuaidiImageView back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        TextView title = (TextView) findViewById(R.id.tv_title_des);
        DrawableLeftWithTextViewCenter btn_resend_sms = (DrawableLeftWithTextViewCenter) findViewById(R.id.btn_resend_sms);// 重新发送短信
        DrawableLeftWithTextViewCenter btn_resend_cloud = (DrawableLeftWithTextViewCenter) findViewById(R.id.btn_resend_cloud);// 重新发送云呼
        TextView tv_warning = (TextView) findViewById(R.id.tv_warning);
        list = (ListView) findViewById(R.id.list);
        resend_area = (LinearLayout) findViewById(R.id.resend_area);
        noData = (TextView) findViewById(R.id.noData);

        back.setOnClickListener(this);
        btn_resend_sms.setOnClickListener(this);
        btn_resend_cloud.setOnClickListener(this);

        if (status.equals("faild")) {
            title.setText("发送失败记录");
            tv_warning.setText("只显示近5天内的短信发送失败记录");
        } else if (status.equals("notsigned")) {
            title.setText("未取件记录");
            tv_warning.setText("只显示近5天内的短信未取件记录");
        }
    }

    private void initListener() {
        adapter = new RecordSmsSendFailAndNoReceiveAdapter(mContext, smsRecords);
        adapter.setOnclickListener(RecordSmsSendFailAndNoReceiveActivity.this);
        list.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:// 返回按钮
                finish();
                break;
            case R.id.btn_resend_sms:// 重新发送短信
                if (smsRecords.size()<=0){
                    return;
                }
                mIntent = new Intent(mContext, RecordSendSmsSelectActivity.class);
                mIntent.putExtra("list", (Serializable) smsRecords);
                if (status.equals("faild")) {
                    mIntent.putExtra("from", "faild");
                } else if (status.equals("notsigned")) {
                    mIntent.putExtra("from", "notsigned");
                }
                mIntent.putExtra("status", "resend_sms");
                startActivity(mIntent);
                break;
            case R.id.btn_resend_cloud:// 重新发送云呼
                if (smsRecords.size()<=0){
                    return;
                }
                mIntent = new Intent(mContext, RecordSendSmsSelectActivity.class);
                mIntent.putExtra("list", (Serializable) smsRecords);
                mIntent.putExtra("status", "resend_cloud");
                if (status.equals("faild")) {
                    mIntent.putExtra("from", "faild");
                } else if (status.equals("notsigned")) {
                    mIntent.putExtra("from", "notsigned");
                }
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 调用短信记录列表接口方法
     * noread 未读数【0：全部|1：未读|2：已读】
     */
    private void getDeliveryList(int page_num) {
        if (loading){
            showProgressDialog( "数据加载中...");
        }
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "inform_user/get_delivery_list");
            data.put("role", "courier");
            data.put("page_size", Constants.PAGE_SIZE);
//            data.put("page_size",1);
            data.put("page_num", page_num);
            data.put("user_phone", "");
            data.put("query_number", "");
            data.put("order_number", "");
            if (status.equals("faild")) {
                data.put("status", "faild");
            } else if (status.equals("notsigned")) {
                data.put("status", "notsigned");
            }
            data.put("start_date", "");
            data.put("end_date", "");
            data.put("noread", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        loading = false;
        JSONObject result = null;
        Message message;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sname.equals("inform_user/get_delivery_list")) {// 获取记录列表接口
            try {
                if (null == result) {
                    UtilToolkit.showToast("获取数据失败");
                    return;
                }
                total_page = result.optInt("total_page");
                total_records = result.optInt("total_records");
                if (!Utility.isEmpty(total_records)&&total_records==0){
                    message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                    return;
                }
                List<SmsRecord> smsRecords = new ArrayList<>();
                JSONArray desc = result.getJSONArray("desc");
                if (desc == null)
                    return;
                for (int i = 0; i < desc.length(); i++) {
                    JSONObject object = (JSONObject) desc.get(i);
                    SmsRecord smsRecord = new SmsRecord();
                    smsRecord.setInform_id(object.getString("inform_id"));
                    smsRecord.setTopic_id(object.getString("topic_id"));
                    smsRecord.setExpress_number(object.getString("express_number"));
                    smsRecord.setDh(object.getString("dh"));
                    smsRecord.setUser_phone(object.getString("user_phone"));
                    smsRecord.setContent(object.getString("content"));
                    smsRecord.setLast_update_time(object.getLong("last_update_time"));
                    smsRecord.setStatus(object.getString("status"));
                    smsRecord.setSigned(object.getString("signed"));
                    smsRecord.setLast_msg_content(object.getString("last_msg_content"));
                    smsRecord.setLast_msg_content_type(object.getString("last_msg_content_type"));
                    smsRecord.setLast_msg_time(object.getString("last_msg_time"));
                    smsRecord.setCm_nr_flag(object.optInt("cm_nr_flag"));
                    smsRecord.setRetCount(object.optInt("retCount"));
                    smsRecords.add(smsRecord);
                }
                message = new Message();
                message.what = GET_LIST_SUCCESS;
                message.obj = smsRecords;
                mHandler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        if (!Utility.isEmpty(result)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void itemClickEvent(View view, int position, SmsRecord smsRecord) {
        smsRecord.setCm_nr_flag(0);
        smsRecords.set(position,smsRecord);
        mIntent = new Intent(mContext, RecordDetailActivity.class);
        mIntent.putExtra("smsRecord", smsRecord);
        mIntent.putExtra("fromActivity", "smsRecordActivity");
        startActivityForResult(mIntent, REQUEST_GETINTO_RECORD_DETAIL_ACTIVITY);
        EventBus.getDefault().post(new SmsRecord(smsRecord));
    }

    @Override
    public void updateSign(View view, int position, String informId) {
        if (!Utility.isNetworkConnected()) {// 无网络
            UtilToolkit.showToast("请设置网络");
        } else {// 有网络
            KuaidiApi.setSignInStatus(mHandler, informId, position);
        }
    }

    // 接收eventbus传递过来的对象，此处用于更新列表小红点
    @Subscribe
    public void onEventMainThread(SmsRecord smsRecord) {

        for (int i = 0; i < adapter.getAdapterData().size(); i++) {
            SmsRecord smsRecord2 = adapter.getAdapterData().get(i);
            if (smsRecord2.getTopic_id().equals(smsRecord.getTopic_id())) {
                smsRecord2.setCm_nr_flag(0);
                smsRecord2.setSigned(smsRecord.getSigned());
                adapter.getAdapterData().set(i, smsRecord2);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
