package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ChildWangdianAdapter;
import com.kuaibao.skuaidi.activity.model.WangdianInfo;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kuaibao on 2016/7/12.
 */

public class ChildWangdianActivity extends SkuaiDiBaseActivity{

    private final static String CHILD_WANGDIAN = "liuyan/childList";
    private ListView lv_wangdian;
    private TextView tv_title_des;
    private ImageView iv_title_back;
    private Context context;
    private ChildWangdianAdapter adapter;
    private ArrayList<WangdianInfo> infos = new ArrayList<WangdianInfo>();
    private String choosedWangdian;
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryman);
        context = this;
        lv_wangdian = (ListView) findViewById(R.id.lv_deliveryman);
        iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("快递员");
        choosedWangdian = getIntent().getStringExtra("wangdian");
        lv_wangdian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("wangdian", infos.get(i));
                setResult(40, intent);
                finish();
            }
        });

        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getData();
    }

    private void getData(){
        showProgressDialog("加载中...");
        JSONObject data = new JSONObject();
        try {
            data.put("sname", CHILD_WANGDIAN);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        dismissProgressDialog();
        JSONArray array = null;
        try {
            array = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(CHILD_WANGDIAN.equals(sname)){
            WangdianInfo info;
            for(int i = 0; i < array.length(); i++){
                info = new WangdianInfo();
                JSONObject obj = array.optJSONObject(i);
                info.setW_id(obj.optString("org_code"));
                String wName = obj.optString("org_name");
                info.setW_name(wName);
                if(choosedWangdian.equals(wName)){
                    pos = i;
                }
                infos.add(info);
                lv_wangdian.setAdapter(new ChildWangdianAdapter(context, infos, pos));
                if(pos != -1){
                    lv_wangdian.setSelection(pos);
                }
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        UtilToolkit.showToast( result);
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
