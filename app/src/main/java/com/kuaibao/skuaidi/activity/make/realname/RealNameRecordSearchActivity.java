package com.kuaibao.skuaidi.activity.make.realname;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.RealNameRecordAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.SkuaidiEditText;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 实名寄递记录搜索界面
 * Created by gdd on 2016/4/24.
 */
public class RealNameRecordSearchActivity extends SkuaiDiBaseActivity{

    private static final String REAL_SEAECH_RECORD = "enrollments/getInfo";
    private static final String REAL_RECORDS_ALL = "enrollments/get";
    private SkuaidiEditText etInputNo;
    private TextView tvSearch, tv_no_search_data;
    private View line1;
    private ViewGroup llRecords;
    private PullToRefreshView pull;
    private ListView lvRecord;
    private RelativeLayout rl_select;
    private View line;
//    private String input;
    private List<CollectionRecords> searchRecords;
    private RealNameRecordAdapter adapter;
    private int page_num = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_liuyan_search);
        context = this;
        rl_select = (RelativeLayout) findViewById(R.id.select);
        line = findViewById(R.id.line);
        rl_select.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        etInputNo = (SkuaidiEditText) findViewById(R.id.etInputNo);
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        line1 = findViewById(R.id.line1);
        llRecords = (ViewGroup) findViewById(R.id.llSmsRecord);
        tv_no_search_data = (TextView) findViewById(R.id.tv_no_search_data);
        pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        lvRecord = (ListView) findViewById(R.id.lv_exception_list);
        initDataListener();
    }

    private void initDataListener(){
        if(null == searchRecords){
            searchRecords = new ArrayList<>();
        }
        if(null == adapter){
            adapter = new RealNameRecordAdapter(context, searchRecords);
            lvRecord.setAdapter(adapter);
        }
        etInputNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				/* 判断是否是“GO”键 */
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSearchResult();
					/* 隐藏软键盘 */
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                getSearchResult();

            }
        });

        pull.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {

            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                if(Utility.isEmpty(etInputNo.getText().toString()))
                page_num = 1;
                getSearchResult();
            }
        });
        // 上拉
        pull.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {

            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                if(Utility.isEmpty(etInputNo.getText().toString()))
                page_num += 1;
                getSearchResult();
            }
        });

        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, CollectionAccountDetailActivity.class);
                intent.putExtra("detailInfo", searchRecords.get(position));
                startActivity(intent);
            }
        });

    }

/*
    private List<CollectionRecords> parseCollectionRecords(String records) {
        List<CollectionRecords> list = new ArrayList<CollectionRecords>();
        if(!Utility.isEmpty(records)){
            JSONArray array = null;
            try {
                array = new JSONArray(records);
                for(int i = 0; i < array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    CollectionRecords record = new CollectionRecords();
                    record.setTran_msg(obj.optString("name"));
                    record.setMoney(obj.optString("money"));
                    record.setOrder_number(obj.optString("waybill_no"));
                    record.setAvail_time(obj.optString("avail_time"));
                    record.setDesc(obj.optString("desc"));

                    list.add(record);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;

    }
*/


    private List<CollectionRecords> parseCollectionRecords(String records) {
        List<CollectionRecords> list = new ArrayList<>();
        if(!Utility.isEmpty(records)){
            JSONArray array = null;
            try {
                array = new JSONArray(records);
                for(int i = 0; i < array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    CollectionRecords record = new CollectionRecords();
                    record.setTran_msg(obj.optString("name"));
                    record.setMoney(obj.optString("money"));
//                    record.setOrder_number(obj.optString("waybill_no"));
                    record.setAvail_time(obj.optString("create_time"));
                    record.setDesc(obj.optString("desc"));
                    record.setStatus(obj.optInt("status"));
                    record.setOfId(obj.optString("ofId"));
                    JSONArray arr = obj.optJSONArray("waybill_list");

                    StringBuilder builder = new StringBuilder();
                    for(int j = 0; j < arr.length(); j++){
                        if(j == arr.length() - 1){
                            builder.append(arr.optString(j));
                        }else{
                            builder.append(arr.optString(j)+",");
                        }
                    }
                    record.setOrder_number(builder.toString());
                    list.add(record);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;

    }



    private void getSearchResult(){
        JSONObject data = new JSONObject();
        try {
            if(Utility.isEmpty(etInputNo.getText().toString())){
                data.put("sname", REAL_RECORDS_ALL);
                data.put("page", page_num);
                data.put("page_size", 10);
            }else{
                data.put("sname", REAL_SEAECH_RECORD);
                data.put("waybill_no", etInputNo.getText().toString());
            }
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(REAL_SEAECH_RECORD.equals(sname)){
//            CollectionRecords searchRecord = new CollectionRecords();
//            searchRecord.setTran_msg(result.optString("name"));
//            searchRecord.setMoney(result.optString("money"));
////            searchRecord.setOrder_number(result.optString("waybill_no"));
//            searchRecord.setAvail_time(result.optString("create_time"));
//            searchRecord.setDesc(result.optString("desc"));
//            JSONArray arr = result.optJSONArray("waybill_list");
//            StringBuilder builder = new StringBuilder();
//            for(int j = 0; j < arr.length(); j++){
//                if(j == arr.length() - 1){
//                    builder.append(arr.optString(j));
//                }else{
//                    builder.append(arr.optString(j)+",");
//                }
//            }
//            searchRecord.setOrder_number(builder.toString());
            List<CollectionRecords> searchResult = parseCollectionRecords(json);
            searchRecords.clear();
            searchRecords.addAll(searchResult);
            adapter.notifyDataSetChanged();
        }else if(REAL_RECORDS_ALL.equals(sname)){
            List<CollectionRecords> list = parseCollectionRecords(result.optString("list"));
            if (page_num == 1) {
                searchRecords.clear();
                searchRecords.addAll(list);
            } else {
                searchRecords.addAll(list);
            }
            adapter.notifyDataSetChanged();
        }
        if(searchRecords.size() != 0){
            llRecords.setVisibility(View.VISIBLE);
            tv_no_search_data.setVisibility(View.GONE);
        }
//        else{
//            llRecords.setVisibility(View.GONE);
//        }
        pull.onHeaderRefreshComplete();
        pull.onFooterRefreshComplete();
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
//        Utility.showToast(context, result);
        searchRecords.clear();
        adapter.notifyDataSetChanged();
        llRecords.setVisibility(View.VISIBLE);
        tv_no_search_data.setVisibility(View.VISIBLE);
        pull.onFooterRefreshComplete();
        pull.onHeaderRefreshComplete();
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
