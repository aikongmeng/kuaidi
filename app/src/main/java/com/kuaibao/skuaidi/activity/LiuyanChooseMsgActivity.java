package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.LiuyanListAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.MessageList;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuaibao on 2016/5/26.
 */
public class LiuyanChooseMsgActivity extends SkuaiDiBaseActivity{

    private static final String LIUYAN_Message_LIST = "liuyan.topic_query";
    private TextView tv_title_des, tv_confirm;
    private ListView lv_message_list;
    private PullToRefreshView pullto_refresh_view;
    private ImageView iv_select_all;
    private List<MessageList> messageLists = new ArrayList<MessageList>();
    private List<MessageList> choosedMessage = new ArrayList<MessageList>();
    private Context context;
    private LiuyanListAdapter adapter;
    private int page = 1;
    private boolean isAllChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_message);
        context = this;
        getControl();
        setListener();
        showProgressDialog( "加载中...");
        getData();
    }

//    @Subscribe
//    public void onEvent(MessageEvent event) {
//        if (event.type == 0x66) {
//            messageLists.clear();
//            page = 1;
//            getData();
//        }
//    }
    private void getControl(){
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("选择单号");
        lv_message_list = (ListView) findViewById(R.id.lv_message_list);
        pullto_refresh_view = (PullToRefreshView) findViewById(R.id.pullto_refresh_view);
        iv_select_all = (ImageView) findViewById(R.id.iv_select_all);
        iv_select_all.setImageResource(R.drawable.select_edit_identity);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setClickable(false);
        tv_confirm.setBackgroundResource(R.drawable.shape_btn_gray1);
        adapter = new LiuyanListAdapter(context, messageLists, true);
        lv_message_list.setAdapter(adapter);
    }

    private void setListener(){
        lv_message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(((MessageList)adapter.getItem(i)).isSelected()){
                    ((MessageList)adapter.getItem(i)).setSelected(false);
                    choosedMessage.remove(messageLists.get(i));
                    if(isAllChecked){
                        iv_select_all.setImageResource(R.drawable.select_edit_identity);
                        isAllChecked = false;
                    }
                }else{
                    ((MessageList)adapter.getItem(i)).setSelected(true);
                    choosedMessage.add(messageLists.get(i));
                    if(choosedMessage.size() == messageLists.size()){
                        iv_select_all.setImageResource(R.drawable.batch_add_checked);
                        isAllChecked = true;
                    }
                }
                setCheckedCount();
                adapter.notifyDataSetChanged();
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choosedMessage.size() > 0){
                    Intent intent = new Intent(context, ChooseDeliveryManActivity.class);
                    intent.putExtra("messages", (Serializable)choosedMessage);
//                    context.startActivity(intent);
                    startActivityForResult(intent, 86);
                }else{
                    UtilToolkit.showToast( "请选择分配的留言");
                }
            }
        });

        iv_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAllChecked){
                    choosedMessage.clear();
                    for (int i = 0; i < adapter.getList().size(); i++) {
                        ((MessageList)adapter.getList().get(i)).setSelected(true);
                        choosedMessage.add(adapter.getList().get(i));
                    }
                    iv_select_all.setImageResource(R.drawable.batch_add_checked);
                    isAllChecked = true;
                }else{
                    for (int i = 0; i < adapter.getList().size(); i++) {
                        ((MessageList)adapter.getList().get(i)).setSelected(false);
                    }
                    choosedMessage.clear();
                    iv_select_all.setImageResource(R.drawable.select_edit_identity);
                    isAllChecked = false;
                }
                setCheckedCount();
                adapter.notifyDataSetChanged();
            }
        });

        pullto_refresh_view.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                messageLists.clear();
                page = 1;
                getData();
            }
        });

        pullto_refresh_view.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                page = page + 1;
                getData();
            }
        });
    }

    private void getData(){
        requestDatas(LIUYAN_Message_LIST, page, 20);
    }

    private void requestDatas(String sname, int pageNum, int pageSize) {
        if (!Utility.isNetworkConnected()) {// 无网络
            pullto_refresh_view.onFooterRefreshComplete();
            pullto_refresh_view.onHeaderRefreshComplete();
            UtilToolkit.showToast( "无网络连接");
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("sname", sname);
            data.put("page_num", pageNum);
            data.put("page_size", pageSize);
            data.put("status", "all");
            data.put("search_keyword", "");
            data.put("assign_status", "N");
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
//        if (!Utility.progressIsShow() && !isFinishing() && showProgress)
//            showProgressDialog( "数据加载中", false);

    }

    public void back(View view){
        finish();
    }

    private void changeConfirmStatus(){
        if(choosedMessage.size() > 0){
            tv_confirm.setClickable(true);
            tv_confirm.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        }else{
            tv_confirm.setClickable(false);
            tv_confirm.setBackgroundResource(R.drawable.shape_btn_gray1);
        }
    }

    private void setCheckedCount(){
        tv_confirm.setText("确定("+getCheckedCount()+")");
        changeConfirmStatus();
    }

    private int getCheckedCount() {
        int checkedCount = 0;
        for(int i = 0; i < adapter.getCount(); i++){
            if(adapter.getList().get(i).isSelected()){
                checkedCount = checkedCount + 1;
            }
        }
        return checkedCount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 86 && resultCode == 68){
            finish();
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        dismissProgressDialog();
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if(LIUYAN_Message_LIST.equals(sname)){
            if (result != null) {
                JSONArray array = result.optJSONArray("list");
//                ArrayList<MessageList> allMessage = parseMsgFromJson(array.toString());
//                for(MessageList msgl: allMessage){
//                    if("inbox".equals(msgl.getTopic_cate()) && "0".equals(msgl.getHas_assign())){
//                        messageLists.add(msgl);
//                    }
//                }
//                messageLists.clear();
                if(array.length() > 0){
                    iv_select_all.setImageResource(R.drawable.select_edit_identity);
                    isAllChecked = false;
                }
                messageLists.addAll( parseMsgFromJson(array.toString()));
                adapter.notifyDataSetChanged();
            }
        }
        pullto_refresh_view.onHeaderRefreshComplete();
        pullto_refresh_view.onFooterRefreshComplete();
    }

    private ArrayList<MessageList> parseMsgFromJson(String jsonData) {
        Gson gson = new Gson();
        ArrayList<MessageList> list = new ArrayList<MessageList>();
        try {
            list = gson.fromJson(jsonData, new TypeToken<List<MessageList>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        UtilToolkit.showToast(result);
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
