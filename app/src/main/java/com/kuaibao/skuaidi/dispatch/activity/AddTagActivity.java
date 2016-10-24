package com.kuaibao.skuaidi.dispatch.activity;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dispatch.view.ChoiceView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wang on 2016/8/29.
 */
public class AddTagActivity extends SkuaiDiBaseActivity {

    public static final String DELIVERY_ADD_LABEL = "delivery.AddLabel";
    @BindView(R.id.iv_title_back)
    SkuaidiImageView ivTitleBack;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.lv_tags)
    ListView lvTags;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private String waybillNO;
    private String name;
    private String mobile;
    private String address;
    private String complainType;
    private static List<String> tags = new ArrayList<>(Arrays.asList("该客户经常投诉快递员", "该客户经常要求送件上门", "该客户经常不让送快递柜", "收件人要求本人签收"));
    private static ArrayMap<String, String> tagMap = new ArrayMap<>();

    static {
        tagMap.put(tags.get(0), "complain");
        tagMap.put(tags.get(1), "send");
        tagMap.put(tags.get(2), "noBox");
        tagMap.put(tags.get(3), "sign");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_mark);
        ButterKnife.bind(this);
        tvTitleDes.setText("选择标签");
        waybillNO = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        address = getIntent().getStringExtra("address");


        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.item_dispatch_tags, tags) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ChoiceView view;
                if (convertView == null) {
                    view = new ChoiceView(AddTagActivity.this);
                } else {
                    view = (ChoiceView) convertView;
                }
                view.setText(getItem(position));
                return view;
            }
        };
        lvTags.setAdapter(adapter);
        lvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lvTags.getCheckedItemCount() > 0) {
                    btnCommit.setEnabled(true);
                    btnCommit.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                } else {
                    btnCommit.setEnabled(false);
                    btnCommit.setBackgroundResource(R.drawable.shape_btn_gray1);
                }

            }
        });
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        if (DELIVERY_ADD_LABEL.equals(sname)) {
            com.alibaba.fastjson.JSONObject dataResult = JSON.parseObject(result);
            if (dataResult.getIntValue("code") == 0) {
                UtilToolkit.showToast("标签添加成功");
                setResult(101);
                finish();
            } else {
                UtilToolkit.showToast(dataResult.getString("desc"));
            }

        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        UtilToolkit.showToast(result);
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @OnClick({R.id.iv_title_back, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_commit:
                StringBuilder builder = new StringBuilder();
                for (int i = 0, j = tags.size(); i < j; i++) {
                    if (lvTags.getCheckedItemPositions().get(i)) {
                        builder.append(tagMap.get(tags.get(i))).append(",");
                    }
                }
                complainType = builder.toString();
                if (complainType.length() > 0) {
                    complainType= complainType.substring(0, complainType.length() - 1);
                }
                UMShareManager.onEvent(AddTagActivity.this, "dispatch_saveTag", "dispatch", "派件：提交标签");
                addTags(waybillNO, name, mobile, address, complainType);
                break;
        }
    }

    private void addTags(String waybillNo, String name, String mobile, String address, String complainType) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", DELIVERY_ADD_LABEL);
            data.put("waybillNo", waybillNo);
            data.put("name", name);
            data.put("mobile", mobile);
            data.put("address", address);
            data.put("complainType", complainType);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
