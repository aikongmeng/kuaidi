package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.manager.SkuaidiTelPhoneManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpressWayBillManageActivity extends SkuaiDiBaseActivity implements OnClickListener, TextWatcher{
	String[] wayBills;
	EditText et_first_waybill_num,et_last_waybill_num;
	View ll_no_bill,ll_has_bill,back,more,save;
	TextView tv_first_to_last_bill_num,tv_managed_bill_num,tv_used_num;
	ListView lv_used_bill;
	private static final String SNAME = "spreadsheets";
	private static final String ACT_ADD = "express_add";
	private static final String ACT_GET = "express_get";
	private String scope;
	private Context context;
	private UseNumAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.express_bill_manage_layout);
		context = this;
		getContorl();
		initDatas();
		setListener();
	}
	
	private void getContorl(){
		
		ll_no_bill = findViewById(R.id.ll_no_bill);
		ll_has_bill = findViewById(R.id.ll_has_bill);
		et_first_waybill_num = (EditText) findViewById(R.id.et_first_waybill_num);
		et_last_waybill_num = (EditText) findViewById(R.id.et_last_waybill_num);
		tv_first_to_last_bill_num = (TextView) findViewById(R.id.tv_first_to_last_bill_num);
		tv_managed_bill_num = (TextView) findViewById(R.id.tv_managed_bill_num);
		tv_used_num = (TextView) findViewById(R.id.tv_used_num);
		lv_used_bill = (ListView) findViewById(R.id.lv_used_bill);
		save = findViewById(R.id.tv_save);
		back = findViewById(R.id.iv_title_back);
		more = findViewById(R.id.more);
	}
	
	private void setListener(){
		et_first_waybill_num.addTextChangedListener(this);
		et_last_waybill_num.addTextChangedListener(this);
		back.setOnClickListener(this);
		more.setOnClickListener(this);
		save.setOnClickListener(this);
	}
	
	private void initDatas(){
		requestInterface(ACT_GET);
	}
	
	private void viewContorl(){
		if(TextUtils.isEmpty(scope)){
			ll_no_bill.setVisibility(View.VISIBLE);
			ll_has_bill.setVisibility(View.GONE);
		}else{
			ll_no_bill.setVisibility(View.GONE);
			ll_has_bill.setVisibility(View.VISIBLE);
			tv_first_to_last_bill_num.setText(scope);
			String[] flag = scope.split("-");
			tv_managed_bill_num.setText("本次共管理"+(Long.parseLong(flag[1].trim())-Long.parseLong(flag[0].trim())+1)+"单（按顺序取单号）");
			tv_used_num.setText("今日共使用"+wayBills.length+"单");
			adapter = new UseNumAdapter();
			lv_used_bill.setAdapter(adapter);
		}
	}
	
	private void requestInterface(String act){
		JSONObject object = new JSONObject();
		JSONObject array = new JSONObject();
		try {
			object.put("sname", SNAME);
			object.put("act", act);
//			object.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
			if(act.equals(ACT_ADD)){
				object.put("dev_id", Utility.getOnlyCode());
				object.put("dev_imei", SkuaidiTelPhoneManager.getInstanse(this).getPhoneIME());
				array.put("start_number", et_first_waybill_num.getText().toString());
				array.put("end_number", et_last_waybill_num.getText().toString());
				array.put("brand", SkuaidiSpf.getLoginUser().getExpressNo());
				object.put("data", array);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
		
	}
	
	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(ACT_GET.equals(act)){
			scope = result.optString("scope");
			JSONArray array = result.optJSONArray("numbers");
			wayBills = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				wayBills[i] = array.optString(i);
			}
			viewContorl();
		}else if(ACT_ADD.equals(act)){
			requestInterface(ACT_GET);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		UtilToolkit.showToast(result);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname,
			String msg, JSONObject result) {

	}

	private class UseNumAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wayBills.length;
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return wayBills[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(R.layout.use_num_item, null);
			TextView useNum = (TextView) convertView.findViewById(R.id.tv_use_num);
			useNum.setText(getItem(position));
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_save:
			requestInterface(ACT_ADD);
			break;
		
		case R.id.iv_title_back:
			finish();
			break;
			
		case R.id.more:
			loadWeb("http://m.kuaidihelp.com/facesheet/list", "电子面单使用统计");
			break;
		
		default:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if(!Utility.isEmpty(et_first_waybill_num.getText().toString())
				&& !Utility.isEmpty(et_last_waybill_num.getText().toString())){
			save.setEnabled(true);
			save.setBackgroundResource(R.drawable.selector_base_green_qianse1);
		}else{
			save.setEnabled(false);
			save.setBackgroundResource(R.drawable.shape_btn_gray1);
		}
	}

}
