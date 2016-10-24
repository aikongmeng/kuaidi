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
import com.kuaibao.skuaidi.activity.adapter.DeliveryManChooseAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.DeliveryManInfo;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kuaibao on 2016/5/26.
 */
public class DeliverymanActivity extends SkuaiDiBaseActivity {

    private final static String TEAM_QUERY = "liuyan/team_query";
    private ListView lv_deliveryman;
    private TextView tv_title_des;
    private ImageView iv_title_back;
    private Context context;
    private DeliveryManChooseAdapter adapter;
    private ArrayList<DeliveryManInfo> infos = new ArrayList<DeliveryManInfo>();
    private String choosedMan;
    private int pos = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryman);
        context = this;
        lv_deliveryman = (ListView) findViewById(R.id.lv_deliveryman);
        iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("快递员");
        choosedMan = getIntent().getStringExtra("choose");
        getData();
        setListener();
    }

    private void getData(){
        showProgressDialog( "加载中...");
        JSONObject data = new JSONObject();
        try {
            data.put("sname", TEAM_QUERY);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListener(){
        lv_deliveryman.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("info", infos.get(i));
                setResult(30, intent);
                finish();
            }
        });

        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        if(TEAM_QUERY.equals(sname)){
            DeliveryManInfo info;
//            String names[] = new String[array.length()];
            for(int i = 0; i < array.length(); i++){
                info = new DeliveryManInfo();
                info.setUid(array.optJSONObject(i).optString("uid"));
                info.setUserno(array.optJSONObject(i).optString("userno"));
                String name = array.optJSONObject(i).optString("username");
                info.setUsername(name);
//                names[i] = array.optJSONObject(i).optString("username");
                if(name.equals(choosedMan)){
                    pos = i;
                }
                infos.add(info);
            }
            lv_deliveryman.setAdapter(new DeliveryManChooseAdapter(context, infos, pos));
            if(pos != -1){
                lv_deliveryman.setSelection(pos);
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
