package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dispatch.adapter.AddMulityPhoneAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddMultiplePhoneNumberActivity extends SkuaiDiBaseActivity {

    private TextView tv_top_title_desc, tv_top_introduce;
    private ListView lv_add_phone;
    private Button btn_ignore_upload, btn_upload;
    private AddMulityPhoneAdapter adapter;
    private List<NumberPhonePair> notifyInfos;
    private Context context;
    private String numberList;
    private int count;//总计数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_multiple_phone_number);
        context = this;
        numberList = getIntent().getStringExtra("numbers");
        count = getIntent().getIntExtra("count", 0);
        tv_top_title_desc = (TextView) findViewById(R.id.tv_title_des);
        tv_top_introduce = (TextView) findViewById(R.id.tv_add_phone_introduce);
        lv_add_phone = (ListView) findViewById(R.id.lv_add_phone);
        btn_ignore_upload = (Button) findViewById(R.id.btn_ignore_upload);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        initData();
        addListener();
    }

    private void initData() {
        //模拟数据
        notifyInfos = new ArrayList<>();
        JSONArray array = JSON.parseArray(numberList);
        if (array == null) {
            return;
        }
        for (int i = 0, j = array.size(); i < j; i++) {
            NumberPhonePair info = new NumberPhonePair();
            info.setDh(array.get(i).toString());
            notifyInfos.add(info);
        }
        adapter = new AddMulityPhoneAdapter(context, notifyInfos);
        lv_add_phone.setAdapter(adapter);
//        lv_add_phone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                adapter.setTouchedPosition(position);
//                adapter.notifyDataSetChanged();
//            }
//        });
        tv_top_title_desc.setText("通知收件人");
        tv_top_introduce.setText("系统已为您自动识别出" + (count - array.size()) + "位收件人号码，" + "还有" + array.size() + "个对应的收件人号码需要您来自己填写");

    }

    private void addListener() {
        btn_ignore_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NumberPhonePair> numberPhoneList = adapter.getNotifyPhoneInfos();
                Intent mIntent = new Intent();
                mIntent.putExtra("numberPhonePair", (Serializable) numberPhoneList);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NumberPhonePair> numberPhoneList = adapter.getNotifyPhoneInfos();
                Intent mIntent = new Intent();
                mIntent.putExtra("numberPhonePair", (Serializable) numberPhoneList);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }

    /**
     * 动态设置上传按钮的状态
     */
    public void setUploadButtonEnable(boolean clickable) {
        btn_upload.setEnabled(clickable);
        if (clickable) {

            btn_upload.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        } else {
            btn_upload.setBackgroundResource(R.drawable.shape_btn_gray1);
        }

    }

    public void back(View view) {
        finish();
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
}
