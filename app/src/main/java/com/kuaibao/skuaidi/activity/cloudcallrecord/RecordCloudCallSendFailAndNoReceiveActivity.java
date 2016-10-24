package com.kuaibao.skuaidi.activity.cloudcallrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.cloudcallrecord.adapter.RecordCloudCallSendFailAndNoReceiveAdapter;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.DrawableLeftWithTextViewCenter;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecordCloudCallSendFailAndNoReceiveActivity extends SkuaiDiBaseActivity implements View.OnClickListener ,RecordCloudCallSendFailAndNoReceiveAdapter.ButtonClickListener{

    private final int GET_VOICE_LIST_SUCCESS = 0x1001;// 获取列表成功
    private final static int UPDATE_SIGNED_STATUS = 0x1007;

    private ListView list;// 列表
    private LinearLayout resend_area;// 重发按钮区域
    private TextView noData;

    private Context mContext;
    private Intent mIntent;
    private RecordCloudCallSendFailAndNoReceiveAdapter adapter;
    private String status;// 状态【用于区分发送失败还是未取件-调用接口使用】

    private List<CloudVoiceRecordEntry> cvre = new ArrayList<>();

    private int page_num = 1;
    private int total_page = 0;// 总页数
    private int total_records = 0;// 总记录条数
    private int updatePosition = -1;
    private boolean loading = true;// 正在加载中

    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    list.setVisibility(View.GONE);
                    resend_area.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                    dismissProgressDialog();
                    break;
                case GET_VOICE_LIST_SUCCESS:
                    list.setVisibility(View.VISIBLE);
                    resend_area.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                    if (page_num == 1) {
                        cvre.clear();
                        if (msg.obj instanceof ArrayList) {
                            cvre = (List<CloudVoiceRecordEntry>) msg.obj;
                        }
                    } else {
                        cvre.addAll((List<CloudVoiceRecordEntry>) msg.obj);
                    }
                    page_num ++;
                    // 加载完全部数据
                    if (page_num<=total_page){
                        getList(page_num);
                    }else{
                        dismissProgressDialog();
                        UtilToolkit.showToast("数据加载完成");
                    }
                    adapter.notifyList(cvre);
                    break;
                case UPDATE_SIGNED_STATUS:
//                    adapter.modifySignedStatus(updatePosition);
                    if(cvre.get(updatePosition).getSigned() == 0){
                        cvre.get(updatePosition).setSigned(1);
                    }else if(cvre.get(updatePosition).getSigned() == 1){
                        cvre.get(updatePosition).setSigned(0);
                    }
                    adapter.notifyDataSetChanged();
                    EventBus.getDefault().post(cvre.get(updatePosition));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_send_fail);
        mContext = this;
        //EventBus.getDefault().register(this);
        status = getIntent().getStringExtra("status");
        initView();
        initListener();
        getList(page_num);
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
            title.setText("呼叫失败记录");
            tv_warning.setText("只显示近5天内的云呼发送失败记录");
        } else if (status.equals("nosigned")) {
            title.setText("未取件记录");
            tv_warning.setText("只显示近5天内的云呼未取件记录");
        }
    }

    private void initListener() {
        adapter = new RecordCloudCallSendFailAndNoReceiveAdapter(mContext, cvre);
        adapter.setOnButtonClickListener(RecordCloudCallSendFailAndNoReceiveActivity.this);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CloudVoiceRecordEntry entry = cvre.get(position);
                if (Integer.parseInt(entry.getTopic_id()) == 0) {
                    UtilToolkit.showToast("无可查看的详情内容");
                    return;
                }
                cvre.get(position).setNoreadFlag(0);
                Intent intent = new Intent(mContext, RecordDetailActivity.class);
                intent.putExtra("fromActivity", "cloudVoiceRecordActivity");
                intent.putExtra("cloudEntry", cvre.get(position));
                startActivity(intent);
                adapter.notifyDataSetChanged();
            }
        });
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
        //EventBus.getDefault().unregister(this);
    }
    // 接收eventbus传递过来的对象，此处用于更新列表小红点
//    @Subscribe
//    public void onEventMainThread(CloudVoiceRecordEntry cvre) {
////        for (int i = 0; i < adapter.getListDetail().size(); i++) {
////            CloudVoiceRecordEntry cvre2 = adapter.getListDetail().get(i);
////            if (cvre.getTopic_id().equals(cvre2.getTopic_id())) {
////                cvre2.setNoreadFlag(0);
////                cvre2.setSigned(cvre.getSigned());
////                adapter.getListDetail().set(i, cvre2);
////                adapter.notifyDataSetChanged();
////            }
////        }
//    }
    /**
     * pageNum 页数
     * call_number 按手机号
     * start_time 开始时间
     * end_time 结束时间
     * bh 编号
     * status 接听状态
     * signed n:未签收 y:已签收 ，不传此参数则为所有
     * read   n:未签收 y:已签收 ，不传此参数则为所有
     * 调用云呼语音列表接口
     * author: 顾冬冬
     */
    private void getList(int pageNum) {
        if(loading){
            showProgressDialog( "数据加载中...");
        }
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr.call.list");
            data.put("page_num", pageNum);
            data.put("page_size", Constants.PAGE_SIZE);
            data.put("call_number", "");
            data.put("query_number", "");
            data.put("start_time", "");
            data.put("end_time", "");
            data.put("bh", "");
            if (status.equals("faild")){// 如果是云呼发送失败
                data.put("status", 1);
            }else{
                data.put("status", "");
            }
            if (status.equals("nosigned")) {// 如果是云呼记录中未取件
                data.put("signed", "n");
            }else{
                data.put("signed", "");
            }
            data.put("read", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
    }

    /**
     * @param cid
     * 更新取件状态接口
     * 顾冬冬
     */
    private void updateSigned(String cid) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "ivr/updateSign");
            data.put("id", cid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String message, String json1, String act) {

        loading = false;
        JSONObject result = null;
        Message msg ;
        try {
            result = new JSONObject(json1);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if ("ivr.call.list".equals(sname)){
            if (null == result){
                UtilToolkit.showToast("获取数据失败");
                return;
            }
            List<CloudVoiceRecordEntry> cvre = new ArrayList<>();
            try {
                JSONObject json = result.getJSONObject("retArr");
                total_page = json.optInt("total_page");
                total_records = json.optInt("total_records");
                if (!Utility.isEmpty(total_records)&&total_records==0){
                    msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    return;
                }
                JSONArray jArr = json.optJSONArray("data");
                if (null != jArr && 0 != jArr.length()) {
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.optJSONObject(i);
                        CloudVoiceRecordEntry cvRecordEntry = new CloudVoiceRecordEntry();
                        cvRecordEntry.setCid(jObj.optString("cid"));
                        cvRecordEntry.setTopic_id(jObj.optString("topic_id"));
                        cvRecordEntry.setBh(jObj.optString("bh"));
                        cvRecordEntry.setDh(jObj.optString("dh"));
                        cvRecordEntry.setVoice_title(jObj.optString("voice_title"));
                        cvRecordEntry.setVoice_name(jObj.optString("voice_name"));
                        cvRecordEntry.setVoice_path(jObj.optString("voice_path"));
                        cvRecordEntry.setFee_mins(jObj.optString("fee_mins"));
                        cvRecordEntry.setCall_number(jObj.optString("call_number"));
                        cvRecordEntry.setUser_input_key(jObj.optString("user_input_key"));
                        cvRecordEntry.setCall_duration(jObj.optInt("call_duration"));
                        cvRecordEntry.setStatus(jObj.optString("status"));
                        cvRecordEntry.setStatus_msg(jObj.optString("status_msg"));
                        cvRecordEntry.setCreate_time(jObj.optString("create_time"));
                        cvRecordEntry.setSigned(jObj.optInt("signed"));
                        cvRecordEntry.setNoreadFlag(jObj.optInt("noread_flag"));
                        cvRecordEntry.setLastMsgContent(jObj.optString("last_msg_content"));
                        cvRecordEntry.setLastMsgContentType(jObj.optString("last_msg_content_type"));
                        cvRecordEntry.setLastMsgTime(jObj.optString("last_msg_time"));
                        cvRecordEntry.setRetCount(jObj.optInt("retCount"));
                        cvRecordEntry.setCall_time(jObj.optString("call_time"));
                        cvre.add(cvRecordEntry);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg = new Message();
            msg.what = GET_VOICE_LIST_SUCCESS;
            msg.obj = cvre;
            mHandler.sendMessage(msg);
        }else if ("ivr/updateSign".equals(sname)) {
            dismissProgressDialog();
            msg = new Message();
            msg.what = UPDATE_SIGNED_STATUS;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        if (!Utility.isEmpty(result)){
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:// 返回按钮
                finish();
                break;
            case R.id.btn_resend_sms:// 重新发送短信
                if (cvre.size()<=0){
                    return;
                }
                mIntent = new Intent(mContext, RecordCloudCallSelectActivity.class);
                mIntent.putExtra("list", (Serializable) cvre);
                if (status.equals("faild")) {
                    mIntent.putExtra("from", "faild");
                } else if (status.equals("nosigned")) {
                    mIntent.putExtra("from", "nosigned");
                }
                mIntent.putExtra("status", "resend_sms");
                startActivity(mIntent);
                break;
            case R.id.btn_resend_cloud:// 重新发送云呼
                if (cvre.size()<=0){
                    return;
                }
                mIntent = new Intent(mContext, RecordCloudCallSelectActivity.class);
                mIntent.putExtra("list", (Serializable) cvre);
                mIntent.putExtra("status", "resend_cloud");
                if (status.equals("faild")) {
                    mIntent.putExtra("from", "faild");
                } else if (status.equals("nosigned")) {
                    mIntent.putExtra("from", "nosigned");
                }
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void call(View v, int position, String number) {
        if (Utility.isEmpty(number)) {
            UtilToolkit.showToast("本条记录没有联系号码");
        } else {
            AcitivityTransUtil.showChooseTeleTypeDialog(RecordCloudCallSendFailAndNoReceiveActivity.this, "", number,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
        }
    }

    @Override
    public void updateSignedStatus(View v, int position, String cid) {
        if (Utility.isNetworkConnected()) {
            updatePosition = position;
            updateSigned(cid);
        }else{
            UtilToolkit.showToast("请设置您的网络");
        }
    }
}
