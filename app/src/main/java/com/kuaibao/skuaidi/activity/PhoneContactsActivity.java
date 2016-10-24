package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.PhoneContactsAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.api.HttpHelper.OnResultListener;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import com.kuaibao.skuaidi.manager.SkuaidiCustomerManager;

@SuppressLint("SimpleDateFormat")
public class PhoneContactsActivity extends SkuaiDiBaseActivity implements
		View.OnClickListener {
	private TextView tv_title;
	private ListView lv;
	private Button btn_batchAdd;
	private PhoneContactsAdapter adapter;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
//	private SkuaidiCustomerManager mCustomerManager;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.phonecontacts);
//		mCustomerManager = SkuaidiCustomerManager.getInstanse();
		getControl();

		setListener();

		setData();
	}

	public void getControl() {
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		lv = (ListView) findViewById(R.id.lv_phonecontacts);
		btn_batchAdd = (Button) findViewById(R.id.bt_title_more);
	}

	public void setListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {

				if (btn_batchAdd.getText().toString().equals("保存")) {
					if (adapter.getCus(position) != null) {
						((ImageView) v.findViewById(R.id.batch_add_icon))
								.setImageResource(R.drawable.batch_add_cancel);
						adapter.removeCus(position);
					} else {
						((ImageView) v.findViewById(R.id.batch_add_icon))
								.setImageResource(R.drawable.batch_add_checked);
						MyCustom cus = new MyCustom();
						cus.setName(((TextView) v
								.findViewById(R.id.tv_phonecontacts_name))
								.getText().toString());
						cus.setPhone(((TextView) v
								.findViewById(R.id.tv_phonecontacts_tel))
								.getText().toString().replaceAll(" ", ""));
						adapter.addCus(position + "", cus);
					}

				} else {
					String name = ((TextView) v
							.findViewById(R.id.tv_phonecontacts_name))
							.getText().toString();
					String tel = ((TextView) v
							.findViewById(R.id.tv_phonecontacts_tel)).getText()
							.toString().replaceAll(" ", "");

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("name", name);
					bundle.putString("tel", tel);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		btn_batchAdd.setOnClickListener(this);
	}

	public void setData() {
		tv_title.setText("手机通讯录");
		btn_batchAdd.setVisibility(View.VISIBLE);

		List<String> list = getIntent().getStringArrayListExtra("list");
		adapter = new PhoneContactsAdapter(this, list);
		if (getIntent().getStringExtra("type") != null
				&& getIntent().getStringExtra("type").equals("batchAdd")) {
			btn_batchAdd.setText("保存");
			adapter.isBatchAdd(true);
		} else {
			btn_batchAdd.setText("批量添加");
		}
		if (getIntent().getStringExtra("isShowButton") != null
				&& getIntent().getStringExtra("isShowButton").equals("0")) {
			btn_batchAdd.setVisibility(View.GONE);
		}
		lv.setAdapter(adapter);

	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_title_more) {
			if (((Button) v).getText().toString().equals("批量添加")) {
				adapter.isBatchAdd(true);
				((Button) v).setText("保存");
			} else {
				batchAddCustom(adapter.getMyCustoms());
			}
		}
	}

	private void batchAddCustom(final List<MyCustom> customs) {
		if (customs.size() == 0) {
			UtilToolkit.showToast("通讯录的客户已全部导入");
			return;
		}

		if (!Utility.isNetworkConnected()) {
			for (MyCustom myCustom : customs) {
				myCustom.setTime(new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date(System.currentTimeMillis())));
				newDB.insertCustomer(myCustom);
				myCustom.set_index(newDB.queryCustomerMaxId());
//				BackUpService.addData(myCustom);
//				BackUpService.ListSort();
//				mCustomerManager.addData(myCustom);
//				mCustomerManager.ListSort();
				btn_batchAdd.setText("批量添加");
				adapter.isBatchAdd(false);
				SKuaidiApplication.getInstance().postMsg("MycustomActivity",
						"isonResumeFresh", true);
				UtilToolkit.showToast("添加成功");
			}
			return;
		}

		JSONObject data = new JSONObject();
		try {
			data.put("pname", "androids");
			data.put("sname", "counterman.consumer.batadd");
			data.put("cm_id", SkuaidiSpf.getLoginUser().getUserId());
			JSONArray array = new JSONArray();
			for (int i = 0; i < customs.size(); i++) {
				JSONObject param = new JSONObject();
				MyCustom cus = customs.get(i);
				param.put("phone", cus.getPhone());
				param.put("address",
						cus.getAddress() != null ? cus.getAddress() : "");
				param.put("note", cus.getNote() != null ? cus.getNote() : "");
				param.put("name", cus.getName() != null ? cus.getName() : "");
				array.put(param);
			}
			data.put("data", array);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		showProgressDialog("");//PhoneContactsActivity.this,"");

		HttpHelper httpHelper = new HttpHelper(
				new OnResultListener() {

					@Override
					public void onSuccess(String result, String sname) {
						dismissProgressDialog();//PhoneContactsActivity.this);
						try {
							JSONObject json = new JSONObject(result);
							String code = json.getString("code");
							JSONObject array = json.getJSONObject("data");
							if (code.equals("0")) {
								FinalDb finalDb = SKuaidiApplication
										.getInstance().getFinalDbCache();
								for (int i = 0; i < customs.size(); i++) {
									try {
										customs.get(i)
												.setTime(
														new SimpleDateFormat(
																"yyyy-MM-dd")
																.format(new Date(
																		System.currentTimeMillis())));
										customs.get(i).setId(
												array.getString(customs.get(i)
														.getPhone()));
										newDB.insertCustomer(customs.get(i));
										customs.get(i).set_index(
												newDB.queryCustomerMaxId());
										//BackUpService.addData(customs.get(i));
//										mCustomerManager.addData(customs.get(i));
										// }
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								//BackUpService.ListSort();
//								mCustomerManager.ListSort();
								btn_batchAdd.setText("批量添加");
								adapter.isBatchAdd(false);
								SKuaidiApplication.getInstance().postMsg(
										"MycustomActivity", "isonResumeFresh",
										true);
								UtilToolkit.showToast("添加成功");
							} else {
								UtilToolkit.showToast(json.getString("msg"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							UtilToolkit.showToast("添加完成");
						}
					}

					@Override
					public void onFail(String result, JSONObject data_fail, String code) {
						dismissProgressDialog();//PhoneContactsActivity.this);
						UtilToolkit.showToast(result);
					}
				}, handler);
		httpHelper.getPart(data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}


	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if(code.equals("7") && null != result){
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
