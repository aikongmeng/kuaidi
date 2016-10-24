package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Vantages;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 * 积分详情
 * @author xuyang
 *
 */
public class MyVantagesDetailActivity extends SkuaiDiBaseActivity {
	private TextView title,tag, scoreinfo, time, remarks,scoreNum,scoreTag;
	private Vantages vantages;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.my_vantagesdetail_layout);
		getControl();
		initDatas();
	}

	private void getControl() {
		title = (TextView) findViewById(R.id.tv_title_des);
		tag = (TextView) findViewById(R.id.tv_vantagesdetail_tag);
		scoreinfo = (TextView) findViewById(R.id.tv_vantagesdetail_scoreinfo);
		time = (TextView) findViewById(R.id.tv_vantagesdetail_time);
		remarks = (TextView) findViewById(R.id.tv_vantagesdetail_remarks);
		scoreTag = (TextView) findViewById(R.id.tv_vantagesdetail_scoreTag);
		scoreNum = (TextView) findViewById(R.id.tv_vantagesdetail_scoreNum);
	}
	
	private void initDatas(){
		vantages = (Vantages) getIntent().getSerializableExtra("vantages");
		title.setText("积分详情");
		scoreTag.setText(vantages.getRuleType().equals("in")?"获得积分：":"扣除积分：");
		tag.setText(vantages.getRuleName());
		time.setText(vantages.getTime());
		remarks.setText(vantages.getDesc());
		scoreinfo.setText(vantages.getRuleInfo());
		scoreNum.setText(vantages.getScore());
//		requestData();
	}
	
//	private void requestData(){
//		if(!Utility.isNetworkConnected()){
//			UtilToolkit.showToast("当前网络未连接，请连接后再重试");
//			return;
//		}
//		JSONObject data = new JSONObject();
//		try {
//			data.put("sname", "score");
//			data.put("act", "score.detail");
//			data.put("id", vantages.getId());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
//	}
	
	public void scoreInfoCheck(View view){
		if(!TextUtils.isEmpty(vantages.getRemarkUrl())){
			loadWeb(vantages.getRemarkUrl(), "备注信息");
		}
	}
	
	public void back(View view){
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
		if(sname.equals("score")&&act.equals("score.detail")){
			remarks.setText(result.optString("desc"));
			scoreinfo.setText(result.optString("ruleInfo"));
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		UtilToolkit.showToast(result);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
