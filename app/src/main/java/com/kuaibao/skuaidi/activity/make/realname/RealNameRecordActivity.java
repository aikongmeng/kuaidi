package com.kuaibao.skuaidi.activity.make.realname;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.RealNameRecordAdapter;
import com.kuaibao.skuaidi.activity.view.IconCenterEditText;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 实名寄递记录页面
 * Created by cj on 2016/4/8.
 * Update by gudongdong on 2016/10/21
 */
public class RealNameRecordActivity extends SkuaiDiBaseActivity {

    private SkuaidiImageView iv_title_back, tv_more;
    private PullToRefreshView pullToRefreshView;
    private ListView ls_real_customer;
    private TextView tv_title_des;
    private Context context;
    private List<CollectionRecords> realRecords;
    private RealNameRecordAdapter adapter;
    private static final String REAL_RECORDS_LIST = "enrollments/get";
    private static final String SEARCH_INFO = "enrollments/getInfo";
    private IconCenterEditText icet_search;
    private int page_num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_exception_center2);
        context = this;
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(page_num == 1 && realRecords.size() == 1){
            initData();
        }
    }

    public void initView(){
        iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("实名寄递记录");
        tv_more = (SkuaidiImageView) findViewById(R.id.tv_more);
        tv_more.setVisibility(View.INVISIBLE);
        icet_search = (IconCenterEditText) findViewById(R.id.icet_search);
        icet_search.setHint("输入运单号搜索");
        icet_search.setInputType(InputType.TYPE_NULL);

        icet_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    icet_search.clearFocus();
                    Intent mIntent = new Intent(context, RealNameRecordSearchActivity.class);
                    startActivity(mIntent);
                }

            }
        });
        icet_search.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = icet_search.getCompoundDrawables()[2];
                // 如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                // 如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                // 计算点击的位置，如果点击到小叉删除按钮，则清除数据
                if (event.getX() > icet_search.getWidth() - icet_search.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    icet_search.setText("");
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(icet_search.getWindowToken(), 0); // 强制隐藏键盘

        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {

            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                page_num = 1;
                getRealRecords();
            }
        });

        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {

            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                page_num += 1;
                getRealRecords();

            }
        });
        ls_real_customer = (ListView) findViewById(R.id.lv_exception_list);
        ls_real_customer.setVisibility(View.VISIBLE);
        initData();
    }

    public void initData(){
        if(null == realRecords){
            realRecords = new ArrayList<>();
        }
        if(null == adapter){
            adapter = new RealNameRecordAdapter(context, realRecords);
            ls_real_customer.setAdapter(adapter);
        }
        getRealRecords();
        ls_real_customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, CollectionAccountDetailActivity.class);
                intent.putExtra("detailInfo", realRecords.get(position));
                startActivityForResult(intent, CollectionRecordActivity.REALNAME_COLLECTION);
            }
        });
    }

    public void getRealRecords(){
        JSONObject data = new JSONObject();
        try {
            data.put("sname", REAL_RECORDS_LIST);
            data.put("page", page_num);
            data.put("page_size", 10);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CollectionRecordActivity.REALNAME_COLLECTION && resultCode == RESULT_OK){
           finish();
        }
    }

    private List<CollectionRecords> parseCollectionRecords(String records) {
        List<CollectionRecords> list = new ArrayList<>();
        if(!Utility.isEmpty(records)){
            try {
                JSONArray array = new JSONArray(records);
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


    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
//        KLog.json(json);
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == null) {
            pullToRefreshView.onHeaderRefreshComplete();
            pullToRefreshView.onFooterRefreshComplete();
            return;
        }
        if(REAL_RECORDS_LIST.equals(sname)){
            List<CollectionRecords> list = parseCollectionRecords(result.optString("list"));
            if (page_num == 1) {
                realRecords.clear();
                realRecords.addAll(list);
            } else {
                realRecords.addAll(list);
            }
            adapter.notifyDataSetChanged();
            pullToRefreshView.onHeaderRefreshComplete();
            pullToRefreshView.onFooterRefreshComplete();
        }else if(SEARCH_INFO.equals(sname)){
            CollectionRecords searchRecord = new CollectionRecords();
            searchRecord.setTran_msg(result.optString("name"));
            searchRecord.setMoney(result.optString("money"));
            searchRecord.setOrder_number(result.optString("waybill_no"));
            searchRecord.setAvail_time(result.optString("avail_time"));
            searchRecord.setDesc(result.optString("desc"));
            page_num = result.optInt("page");
            realRecords.clear();
            realRecords.add(searchRecord);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (REAL_RECORDS_LIST.equals(sname)) {
            if (!TextUtils.isEmpty(result))
                UtilToolkit.showToast( result);
            pullToRefreshView.onHeaderRefreshComplete();
            pullToRefreshView.onFooterRefreshComplete();
        }else if(SEARCH_INFO.equals(sname)){
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }


    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
