package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.E3InfoNumberAdapter;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 选择单号举证页面
 * 
 * @author wq
 * 
 */
public class E3InfoNumberActivity extends SkuaiDiBaseActivity {
	private Context context;
	private E3InfoNumberAdapter adapter;
	private static final String HELP_BAD_WAYBILL_BURDEN_URL = "http://m.kuaidihelp.com/help/q_case.html";
	private static final String HELP_SIGNED_WAYBILL_BURDEN_URL = "http://m.kuaidihelp.com/help/sign_case.html";
	private static final String HELP_ORDER_TYPE_IN_URL = "http://m.kuaidihelp.com/help/ld_explain.html";
	private String scanType;

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.activity_e3info_number);
		TextView tv_title = (TextView) findViewById(R.id.tv_title_des);
		scanType = getIntent().getStringExtra("scanType");
		if ("OrderTypeInActivity".equals(getIntent().getStringExtra("to"))) {
			tv_title.setText("录单");
		} else {
			if ("问题件".equals(scanType)) {
				tv_title.setText("问题件举证");
			} else {
				tv_title.setText("签收件举证");
			}
		}

		ArrayList<NotifyInfo> infos = (ArrayList<NotifyInfo>) getIntent().getSerializableExtra("orderNumbers");
		ListView listView = (ListView) findViewById(R.id.lv_e3info_number);
		adapter = new E3InfoNumberAdapter(context, infos, scanType);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NotifyInfo info = (NotifyInfo) adapter.getItem(position);
				if ("OrderTypeInActivity".equals(getIntent().getStringExtra("to"))) {
					Intent mIntent = new Intent(context, OrderTypeInActivity.class);
					mIntent.putExtra("NotifyInfo", info);
					startActivity(mIntent);
				} else {

					Intent intent = new Intent(context, E3ProofActivity.class);
					intent.putExtra("NotifyInfo", info);
					intent.putExtra("proof_type", scanType);
					startActivity(intent);
				}

			}

		});
	}

	public void back(View view) {
		finish();
	}

	public void more(View view) {
		if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
			loadWeb(HELP_BAD_WAYBILL_BURDEN_URL, "问题件举证说明");
		} else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
			loadWeb(HELP_SIGNED_WAYBILL_BURDEN_URL, "签收件举证说明");
		} else {
			loadWeb(HELP_ORDER_TYPE_IN_URL, "录单说明");
		}
	}
}
