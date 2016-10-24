package com.kuaibao.skuaidi.activity.smsrecord;

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
import com.kuaibao.skuaidi.activity.smsrecord.adapter.RecordSendSmsSelectAdapter;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.AddVoiceModelActivity;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.kuaibao.skuaidi.R.id.iv_title_back;

/**
 * 顾冬冬
 * 选择发送短信失败或者未取件记录重新发送短信或云呼功能界面
 */
public class RecordSendSmsSelectActivity extends SkuaiDiBaseActivity implements View.OnClickListener, RecordSendSmsSelectAdapter.onClickListenerCustom {

    private final int FRESH_SELECT_ALL_STATUS = 0X1001;// 刷新全选按钮状态
    public static final int REQUEST_RESEND = 0X1002;
    public static final int RESULT_RESEND = 0x1103;
    private ListView list;
    private TextView select_all;// 选中全部条目按钮
    private Button next;


    private Context mContext;
    private String status;// 用于判断下一步是发短信还是发云呼
    private String from;// 来自于发送失败界面还是未取件界面
    private List<SmsRecord> smsRecords;
    private RecordSendSmsSelectAdapter adapter;

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
            }else if(from.equals("notsigned")){
                title.setText("选择未取件客户");
            }
        }
    }

    private void initData() {
        smsRecords = (List<SmsRecord>) getIntent().getSerializableExtra("list");
        if (null == smsRecords) {
            smsRecords = new ArrayList<>();
        }
        adapter = new RecordSendSmsSelectAdapter(mContext, smsRecords,from);
        adapter.setOnClickListenerCustom(RecordSendSmsSelectActivity.this);
        list.setAdapter(adapter);

    }

    private void initListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SmsRecord smsRecord = smsRecords.get(position);
                String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(smsRecord.getLast_update_time() * 1000).substring(0, 10);
                if (smsRecord.isSelect()) {
                    smsRecords.get(position).setSelect(false);
                } else {
                    smsRecords.get(position).setSelect(true);
                }
                List<SmsRecord> bfSmsRecords = new ArrayList<>();
                for (int i = 0; i < smsRecords.size(); i++) {// 遍历列表，将所有与当前点击的日期相同都都备份到新列表中去
                    SmsRecord smsRecord2 = smsRecords.get(i);
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(smsRecord2.getLast_update_time() * 1000).substring(0, 10);
                    if (updateTime.equals(time)) {// 点击的这条日期和条目中的日期相同【相同说明是一组】
                        bfSmsRecords.add(smsRecord2);
                    }
                }
                boolean selectAll = true;// 选中全部
                for (int i = 0; i < bfSmsRecords.size(); i++) {// 遍历备份的列表，查找是否全部都被选中
                    SmsRecord smsRecord3 = bfSmsRecords.get(i);
                    if (!smsRecord3.isSelect()) {
                        selectAll = false;// 未选中全部
                        break;// 如果有没有选中的则跳出循环
                    }
                }
                if (selectAll) {
                    for (int i = 0; i < smsRecords.size(); i++) {
                        SmsRecord smsRecord4 = smsRecords.get(i);
                        String timeItem = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(smsRecord4.getLast_update_time() * 1000).substring(0, 10);
                        if (timeItem.equals(updateTime)) {
                            smsRecords.get(i).setSelectTitle(true);
                        }
                    }
                } else {
                    for (int i = 0; i < smsRecords.size(); i++) {
                        SmsRecord smsRecord4 = smsRecords.get(i);
                        String timeItem = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecord4.getLast_update_time() * 1000).substring(0, 10);
                        if (timeItem.equals(updateTime)) {
                            smsRecords.get(i).setSelectTitle(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                for (int i = 0; i < smsRecords.size(); i++) {
                    boolean isSelect = smsRecords.get(i).isSelect();
                    if (!isSelect) {
                        isSelectAll = false;
                        break;
                    }else{
                        isSelectAll = true;
                    }
                }
                int selectCount = 0;
                for (int i =0;i<smsRecords.size();i++){
                    boolean isSelect = smsRecords.get(i).isSelect();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESEND && resultCode == RESULT_RESEND)
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
                    intent.putExtra("from_activity", "resend_smsORcloud");
                }else {
                    intent = new Intent(mContext, AddVoiceModelActivity.class);// 云呼模板
                    intent.putExtra("fromActivityType","resend_smsORcloud");
                }
                List<SmsRecord> smsRecordsloc = new ArrayList<>();
                for (int i = 0;i<smsRecords.size();i++){
                    SmsRecord sr = smsRecords.get(i);
                    if (sr.isSelect()){
                        smsRecordsloc.add(sr);
                    }
                }
                intent.putExtra("smsRecords",(Serializable) smsRecordsloc);
//                startActivity(intent);
                startActivityForResult(intent,REQUEST_RESEND);
                break;
            case R.id.select_all:// 选择全部条目
                for (int i = 0; i < smsRecords.size(); i++) {
                    if (isSelectAll) {
                        smsRecords.get(i).setSelect(false);
                        smsRecords.get(i).setSelectTitle(false);
                    } else {
                        smsRecords.get(i).setSelect(true);
                        smsRecords.get(i).setSelectTitle(true);
                    }
                }
                if (isSelectAll) {
                    isSelectAll = false;
                    Drawable drawable = Utility.getDrawable(mContext,R.drawable.select_edit_identity);
                    // 这一步必须要做，否则不会显示。
                    if (drawable != null)
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    select_all.setCompoundDrawables(drawable, null, null, null);
                } else {
                    isSelectAll = true;

                    Drawable drawable = Utility.getDrawable(mContext,R.drawable.batch_add_checked);
                    // 这一步必须要做，否则不会显示。
                    if (drawable!=null)
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
        for (int i =0;i<smsRecords.size();i++){
            boolean isSelect = smsRecords.get(i).isSelect();
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
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }


    @Override
    public void curSelectAll(int position) {
        SmsRecord smsRecord = smsRecords.get(position);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecord.getLast_update_time() * 1000).substring(0, 10);
        List<SmsRecord> bfSmsRecords = new ArrayList<>();
        for (int i = position; i < smsRecords.size(); i++) {// 遍历列表，将所有与当前点击的日期相同都都备份到新列表中去
            SmsRecord smsRecord2 = smsRecords.get(i);
            String timeItem = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecord2.getLast_update_time() * 1000).substring(0, 10);
            if (time.equals(timeItem)) {// 点击的这条日期和条目中的日期相同【相同说明是一组】
                bfSmsRecords.add(smsRecord2);
            } else {
                break;
            }
        }
        boolean selectAll = true;// 选中全部
        for (int i = 0; i < bfSmsRecords.size(); i++) {// 遍历备份的列表，查找是否全部都被选中
            SmsRecord smsRecord3 = bfSmsRecords.get(i);
            if (!smsRecord3.isSelect()) {
                selectAll = false;// 未选中全部
                break;
            }
        }
        if (selectAll) {// 如果全部选中
            for (int i = position; i < smsRecords.size(); i++) {
                SmsRecord smsRecord4 = smsRecords.get(i);
                String timeItem = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecord4.getLast_update_time() * 1000).substring(0, 10);
                if (timeItem.equals(time)) {
                    smsRecords.get(i).setSelect(false);
                    smsRecords.get(i).setSelectTitle(false);
                } else {
                    break;
                }
            }
        } else {
            for (int i = position; i < smsRecords.size(); i++) {
                SmsRecord smsRecord4 = smsRecords.get(i);
                String timeItem = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecord4.getLast_update_time() * 1000).substring(0, 10);
                if (timeItem.equals(time)) {
                    smsRecords.get(i).setSelect(true);
                    smsRecords.get(i).setSelectTitle(true);
                } else {
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
        for (int i = 0; i < smsRecords.size(); i++) {
            boolean isSelect = smsRecords.get(i).isSelect();
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
