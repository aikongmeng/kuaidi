package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.E3ProofSMAdapter;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.asynctask.E3GetSMSDetailTask;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 短信记录界面
 * 
 * @author wq
 */
public class E3ProofSMSActivity extends SkuaiDiBaseActivity implements OnItemClickListener {
	ListView lv_sms;
	private E3ProofSMAdapter adapter;
	private Context context;
	private List<Message> infos;
	private E3GetSMSDetailTask e3GetSMSDetailTask;
	private Message SMS;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x10002:
				dismissProgressDialog();
				e3GetSMSDetailTask.cancel(true);
				List<Message> SMSes = (List<Message>) msg.obj;
				Intent mIntent = new Intent(context, E3ProofSMSDetailActivity.class);
				mIntent.putExtra("SMS", SMS);
				mIntent.putExtra("SMSes", (Serializable) SMSes);
				mIntent.putExtra("from", "E3ProofSmsActivity");
				startActivity(mIntent);
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_e3_proof_short_message);
		context = this;
		E3ProofActivity.activityList.add(this);

		infos = (List<Message>) getIntent().getSerializableExtra("message");

		adapter = new E3ProofSMAdapter(context, infos);
		lv_sms = (ListView) findViewById(R.id.lv_sms);
		lv_sms.setAdapter(adapter);
		lv_sms.setOnItemClickListener(this);
	}

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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showProgressDialog( "正在加载，请稍候...");
		e3GetSMSDetailTask = new E3GetSMSDetailTask(context, handler, infos.get(position));
		e3GetSMSDetailTask.execute();
		SMS = infos.get(position);
		// Intent intent = new Intent(context, E3ProofSMSDetailActivity.class);
		// intent.putExtra("SMS", infos.get(position));
		// intent.putExtra("from", "E3ProofSmsActivity");
		// startActivity(intent);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void back(View view) {
		finish();
	}
}
