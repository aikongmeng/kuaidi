package com.kuaibao.skuaidi.activity.cloudcallrecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.cloudcallrecord.adapter.RecordCloudCallSelectAdapter;
import com.kuaibao.skuaidi.activity.smsrecord.RecordSendSmsSelectActivity;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.kuaibao.skuaidi.R.id.iv_title_back;

public class RecordCloudCallSelectActivity extends SkuaiDiBaseActivity implements View.OnClickListener , RecordCloudCallSelectAdapter.onClickListenerCustom{
    private final int FRESH_SELECT_ALL_STATUS = 0X1001;// 刷新全选按钮状态

    private ListView list;
    private TextView select_all;// 选中全部条目按钮
    private Button next;

    private String status;// 用于判断下一步是发短信还是发云呼
    private String from;// 来自于发送失败界面还是未取件界面
    private Context mContext;
    private RecordCloudCallSelectAdapter adapter;

    private List<CloudVoiceRecordEntry> cvre = new ArrayList<>();
    private boolean isSelectAll = false;// 全部选中状态【true :全部选中|false :没有全部选中】

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FRESH_SELECT_ALL_STATUS:
                    boolean isSelectAll = (boolean) msg.obj;
                    int selectCount = msg.arg1;
                    if (isSelectAll) {
                        Drawable drawable = Utility.getDrawable(mContext,R.drawable.batch_add_checked);
                        // 这一步必须要做，否则不会显示。
                        if (drawable != null)
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        select_all.setCompoundDrawables(drawable, null, null, null);
                    } else {
                        Drawable drawable = Utility.getDrawable(mContext,R.drawable.select_edit_identity);
                        // 这一步必须要做，否则不会显示。
                        if (drawable != null)
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        select_all.setCompoundDrawables(drawable, null, null, null);
                    }
                    if (selectCount==0){
                        next.setText("下一步");
                        next.setEnabled(false);
                        next.setBackgroundResource(R.drawable.shape_btn_gray1);
                    }else{
                        String nextstr = "下一步("+selectCount+")";
                        next.setText(nextstr);
                        next.setEnabled(true);
                        next.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_select_send_sms_or_cloud);
        status = getIntent().getStringExtra("status");
        from = getIntent().getStringExtra("from");

        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        SkuaidiImageView back = (SkuaidiImageView) findViewById(iv_title_back);// title 返回按钮
        TextView title = (TextView) findViewById(R.id.tv_title_des);// title 标题
        select_all = (TextView) findViewById(R.id.select_all);
        next = (Button) findViewById(R.id.next);
        list = (ListView) findViewById(R.id.list);

        back.setOnClickListener(this);
        select_all.setOnClickListener(this);
        next.setOnClickListener(this);

        if ( !Utility.isEmpty(from)) {
            if (from.equals("faild")) {
                title.setText("选择发送失败的客户");
            }else if(from.equals("nosigned")){
                title.setText("选择未取件客户");
            }
        }
    }

    private void initData() {
        cvre = (List<CloudVoiceRecordEntry>) getIntent().getSerializableExtra("list");
        if (null == cvre) {
            cvre = new ArrayList<>();
        }
        adapter = new RecordCloudCallSelectAdapter(mContext,cvre,from);
        adapter.setOnClickListenerCustom(RecordCloudCallSelectActivity.this);
        list.setAdapter(adapter);

    }

    private void initListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CloudVoiceRecordEntry c = cvre.get(position);
                String updateTime = c.getCreate_time().substring(0, 10);
                if (c.isSelect()) {
                    cvre.get(position).setSelect(false);
                } else {
                    cvre.get(position).setSelect(true);
                }
                List<CloudVoiceRecordEntry> bfSmsRecords = new ArrayList<>();
                for (int i = 0; i < cvre.size(); i++) {// 遍历列表，将所有与当前点击的日期相同都都备份到新列表中去
                    CloudVoiceRecordEntry c2 = cvre.get(i);
                    String time = c2.getCreate_time().substring(0, 10);
                    if (updateTime.equals(time)) {// 点击的这条日期和条目中的日期相同【相同说明是一组】
                        bfSmsRecords.add(c2);
                    }
                }
                boolean selectAll = true;// 选中全部
                for (int i = 0; i < bfSmsRecords.size(); i++) {// 遍历备份的列表，查找是否全部都被选中
                    CloudVoiceRecordEntry c3 = bfSmsRecords.get(i);
                    if (!c3.isSelect()) {
                        selectAll = false;// 未选中全部
                        break;// 如果有没有选中的则跳出循环
                    }
                }
                if (selectAll) {
                    for (int i = 0; i < cvre.size(); i++) {
                        CloudVoiceRecordEntry c4 = cvre.get(i);
                        String timeItem = c4.getCreate_time().substring(0, 10);
                        if (timeItem.equals(updateTime)) {
                            cvre.get(i).setSelectTitle(true);
                        }
                    }
                } else {
                    for (int i = 0; i < cvre.size(); i++) {
                        CloudVoiceRecordEntry c4= cvre.get(i);
                        String timeItem = c4.getCreate_time().substring(0, 10);
                        if (timeItem.equals(updateTime)) {
                            cvre.get(i).setSelectTitle(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                for (int i = 0; i < cvre.size(); i++) {
                    boolean isSelect = cvre.get(i).isSelect();
                    if (!isSelect) {
                        isSelectAll = false;
                        break;
                    }else{
                        isSelectAll = true;
                    }
                }
                int selectCount = 0;
                for (int i =0;i<cvre.size();i++){
                    boolean isSelect = cvre.get(i).isSelect();
                    if (isSelect){
                        selectCount++;
                    }
                }
                Message msg = new Message();
                msg.what = FRESH_SELECT_ALL_STATUS;
                msg.obj = isSelectAll;
                msg.arg1 = selectCount;
                mHandler.sendMessage(msg);
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
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RecordSendSmsSelectActivity.REQUEST_RESEND && resultCode == RecordSendSmsSelectActivity.RESULT_RESEND)
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:// 返回
                finish();
                break;
            case R.id.next:// 下一步
                Intent intent;
                if (status.equals("resend_sms")) {
                    intent = new Intent(mContext, ModelActivity.class);// 短信模板
                    intent.putExtra("from_activity", "cloudCall_resend_smsORcloud");
                } else {
                    intent = new Intent(mContext, AddVoiceModelActivity.class);// 云呼模板
                    intent.putExtra("fromActivityType", "cloudCall_resend_smsORcloud");
                }
                List<CloudVoiceRecordEntry> cloudCallRecordLoc = new ArrayList<>();
                for (int i = 0; i < cvre.size(); i++) {
                    CloudVoiceRecordEntry sr = cvre.get(i);
                    if (sr.isSelect()) {
                        cloudCallRecordLoc.add(sr);
                    }
                }
                intent.putExtra("smsRecords", (Serializable) cloudCallRecordLoc);
                startActivityForResult(intent, RecordSendSmsSelectActivity.REQUEST_RESEND);
                break;
            case R.id.select_all:// 选择全部条目
                for (int i = 0; i < cvre.size(); i++) {
                    if (isSelectAll) {
                        cvre.get(i).setSelect(false);
                        cvre.get(i).setSelectTitle(false);
                    } else {
                        cvre.get(i).setSelect(true);
                        cvre.get(i).setSelectTitle(true);
                    }
                }
                if (isSelectAll) {
                    isSelectAll = false;
                    Drawable drawable = Utility.getDrawable(mContext, R.drawable.select_edit_identity);
                    // 这一步必须要做，否则不会显示。
                    if (drawable != null)
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    select_all.setCompoundDrawables(drawable, null, null, null);
                } else {
                    isSelectAll = true;

                    Drawable drawable = Utility.getDrawable(mContext, R.drawable.batch_add_checked);
                    // 这一步必须要做，否则不会显示。
                    if (drawable != null)
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    select_all.setCompoundDrawables(drawable, null, null, null);
                }
//                adapter.setAllSelectTrue(isSelectAll);
                adapter.notifyDataSetChanged();
                handlerIsSelectAll();
                break;
        }
    }
    /**发送消息判断是否选中全部**/
    private void handlerIsSelectAll(){
        int selectCount = 0;
        for (int i =0;i<cvre.size();i++){
            boolean isSelect = cvre.get(i).isSelect();
            if (isSelect){
                selectCount++;
            }
        }
        Message msg = new Message();
        msg.what = FRESH_SELECT_ALL_STATUS;
        msg.obj = isSelectAll;
        msg.arg1 = selectCount;
        mHandler.sendMessage(msg);
    }

    @Override
    public void curSelectAll(int position) {
        CloudVoiceRecordEntry c = cvre.get(position);
        String time = c.getCreate_time().substring(0, 10);
        List<CloudVoiceRecordEntry> bfSmsRecords = new ArrayList<>();
        for (int i = position; i < cvre.size(); i++) {// 遍历列表，将所有与当前点击的日期相同都都备份到新列表中去
            CloudVoiceRecordEntry c2 = cvre.get(i);
            String timeItem =c2.getCreate_time().substring(0, 10);
            if (time.equals(timeItem)) {// 点击的这条日期和条目中的日期相同【相同说明是一组】
                bfSmsRecords.add(c2);
            } else {
                break;
            }
        }
        boolean selectAll = true;// 选中全部
        for (int i = 0; i < bfSmsRecords.size(); i++) {// 遍历备份的列表，查找是否全部都被选中
            CloudVoiceRecordEntry c3 = bfSmsRecords.get(i);
            if (!c3.isSelect()) {
                selectAll = false;// 未选中全部
                break;
            }
        }
        if (selectAll) {// 如果全部选中
            for (int i = position; i < cvre.size(); i++) {
                CloudVoiceRecordEntry c4 = cvre.get(i);
                String timeItem = c4.getCreate_time().substring(0, 10);
                if (timeItem.equals(time)) {
                    cvre.get(i).setSelect(false);
                    cvre.get(i).setSelectTitle(false);
                } else {
                    break;
                }
            }
        } else {
            for (int i = position; i < cvre.size(); i++) {
                CloudVoiceRecordEntry c4 = cvre.get(i);
                String timeItem = c4.getCreate_time().substring(0, 10);
                if (timeItem.equals(time)) {
                    cvre.get(i).setSelect(true);
                    cvre.get(i).setSelectTitle(true);
                } else {
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
        for (int i = 0; i < cvre.size(); i++) {
            boolean isSelect = cvre.get(i).isSelect();
            if (!isSelect) {
                isSelectAll = false;
                break;
            }else{
                isSelectAll = true;
            }
        }
        handlerIsSelectAll();
    }
}
