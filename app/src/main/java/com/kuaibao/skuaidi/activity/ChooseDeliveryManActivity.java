package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.WangdianInfo;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.DeliveryManInfo;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.MessageList;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuaibao on 2016/5/26.
 */
public class ChooseDeliveryManActivity extends SkuaiDiBaseActivity implements View.OnClickListener{

    private final static int REQUEST_CODE_MAN = 200;
    private final static int REQUEST_CODE_WANGDIAN = 200;
    private final static String ASSIGN_MESSAGE = "liuyan/assign";
    private RelativeLayout rl_select_wangdian, rl_select_deliveryman;
    private TextView tv_title_des, tv_wangdian_name, tv_deliveryman_account, tv_submit_delivery;
    private Context context;
    private DeliveryManInfo info;
    private List<MessageList> messages = new ArrayList<MessageList>();
    private StringBuilder msgIds = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_deliveryman);
        context = this;
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        rl_select_wangdian = (RelativeLayout) findViewById(R.id.rl_select_wangdian);
        rl_select_deliveryman = (RelativeLayout) findViewById(R.id.rl_select_deliveryman);
        tv_wangdian_name = (TextView) findViewById(R.id.tv_wangdian_name);
        tv_deliveryman_account = (TextView) findViewById(R.id.tv_deliveryman_account);
        tv_submit_delivery = (TextView) findViewById(R.id.tv_submit_delivery);
        tv_title_des.setText("选择快递员");
        changeSubStatus();
        getData();
        setListener();
    }

    private void getData(){
        messages.addAll((List<MessageList>) getIntent().getSerializableExtra("messages"));
        for(int i = 0; i < messages.size(); i++){
            if(i == messages.size() - 1){
                msgIds.append(messages.get(i).getM_id());
            }else{
                msgIds.append(messages.get(i).getM_id() + ",");
            }
        }
    }

    private void setListener(){
        rl_select_wangdian.setOnClickListener(this);
        rl_select_deliveryman.setOnClickListener(this);
        tv_submit_delivery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_select_wangdian:
                Intent intnt = new Intent(context, ChildWangdianActivity.class);
                intnt.putExtra("wangdian", tv_wangdian_name.getText().toString());
                startActivityForResult(intnt, REQUEST_CODE_WANGDIAN);
                break;
            case R.id.rl_select_deliveryman:
                Intent intent = new Intent(context, DeliverymanActivity.class);
                intent.putExtra("choose", tv_deliveryman_account.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_MAN);
                break;
            case R.id.tv_submit_delivery:
                JSONObject data = new JSONObject();
                try {
                    data.put("sname", ASSIGN_MESSAGE);
                    data.put("mcg_id", msgIds);
                    data.put("userno", info.getUserno());
                    httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private boolean isDispatchClickable(){
        if(Utility.isEmpty(tv_deliveryman_account.getText().toString())
            || Utility.isEmpty(tv_wangdian_name.getText().toString())){
            return false;
        }
        return true;
    }

    private void changeSubStatus(){
        if(isDispatchClickable()){
            tv_submit_delivery.setClickable(true);
            tv_submit_delivery.setEnabled(true);
            tv_submit_delivery.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        }else{
            tv_submit_delivery.setClickable(false);
            tv_submit_delivery.setEnabled(false);
            tv_submit_delivery.setBackgroundResource(R.drawable.shape_btn_gray1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MAN && resultCode == 30){
            info = (DeliveryManInfo) data.getSerializableExtra("info");
            if(!Utility.isEmpty(info)){
                tv_deliveryman_account.setText(info.getUsername());
            }
            changeSubStatus();
        }else if(requestCode == REQUEST_CODE_WANGDIAN && resultCode == 40){
            WangdianInfo wangdian = (WangdianInfo) data.getSerializableExtra("wangdian");
            if(!Utility.isEmpty(wangdian)){
                tv_wangdian_name.setText(wangdian.getW_name());
            }
            changeSubStatus();
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        if(ASSIGN_MESSAGE.equals(sname)){
            MessageEvent msge=new MessageEvent(0x66, "deliverySuccess");
            EventBus.getDefault().post(msge);
            UtilToolkit.showToast( "留言分配成功！");
            setResult(68);
            finish();
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
//        UtilToolkit.showToast( result);
        Utility.showFailDialog(context, result, tv_submit_delivery.getRootView());
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    public void back(View view){
        finish();
    }
}
