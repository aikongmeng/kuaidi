package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.E3ProofSMSDetailAdapter;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.asynctask.E3GetSMSTask;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class E3ProofSMSDetailActivity extends RxRetrofitBaseActivity implements View.OnClickListener, OnItemClickListener {

	public static final String DATA_FROM_E3_PROOF_ACTIVITY = "E3ProofActivity";
	private TextView titile;
	private Message SMS;
	private ListView lvSMSDetail;
	private MyCustom cus;
	private List<Message> SMSes = new ArrayList<Message>();
	/** 选中的消息集合 */
	private ArrayList<Message> selectedList = new ArrayList<Message>();
	private E3ProofSMSDetailAdapter adapter;
	/** 选中的消息数目 */
	private int selected_num = 0;
	// 确定按钮
	private TextView btn_confir;
	private String dataFrom;
	private Context context;
	private E3GetSMSTask e3GetSMSTask = null;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x10001:
				if (e3GetSMSTask != null) {
					e3GetSMSTask.cancel(true);
				}
				dismissProgressDialog();
				@SuppressWarnings("unchecked")
				List<Message> list = (List<Message>) msg.obj;
				Intent mIntent = new Intent(context, E3ProofSMSActivity.class);
				mIntent.putExtra("message", (Serializable) list);
				mIntent.putExtra("from", "E3ProofSMSDetailActivity");
				startActivity(mIntent);
				finish();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.e3_proof_sms_detail_layout);
		context = this;
		dataFrom = getIntent().getStringExtra("from");
		if (DATA_FROM_E3_PROOF_ACTIVITY.equals(dataFrom)) {
			SMSes = (List<Message>) getIntent().getSerializableExtra("SMS_list");
			SMS = SMSes.get(0);
			initView();
		} else {
			SMS = (Message) getIntent().getSerializableExtra("SMS");
			initView();
			SMSes = (List<Message>) getIntent().getSerializableExtra("SMSes");
			// getSmsInPhone();
		}
		adapter = new E3ProofSMSDetailAdapter(this, SMSes, dataFrom);
		lvSMSDetail.setAdapter(adapter);
		setViewOnclickListener();

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

	private void initView() {
		String phoneNumber = SMS.getPersonPhoneNumber();
		String phoneNum = phoneNumber.length() >= 11 ? phoneNumber.substring(phoneNumber.length() - 11,
				phoneNumber.length()) : phoneNumber;
		String cusName;
		titile = (TextView) findViewById(R.id.tv_title_des);
		lvSMSDetail = (ListView) findViewById(R.id.lv_sms_detail);
		btn_confir = (TextView) findViewById(R.id.tv_confir);

		FinalDb db = FinalDb.create(this, "skuaidi.db");
		List<MyCustom> cuss = db.findAllByWhere(MyCustom.class, "phone like '%" + phoneNum + "%'");
		if (cuss != null && cuss.size() != 0) {
			cus = cuss.get(0);
			cusName = cus.getName();
		} else {
			cusName = SMS.getPersonName();
			cus = new MyCustom();
			cus.setName(cusName);
			cus.setPhone(phoneNum);
		}
		titile.setText(cusName);
		btn_confir = (TextView) findViewById(R.id.tv_confir);

		if (DATA_FROM_E3_PROOF_ACTIVITY.equals(getIntent().getStringExtra("from"))) {
			btn_confir.setText("重新选择");
			btn_confir.setEnabled(true);
			btn_confir.setTextColor(context.getResources().getColor(R.color.white));
			btn_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);
		} else {
			btn_confir.setText("确定(" + selected_num + "/" + SMSes.size() + ")");
			btn_confir.setTextColor(getResources().getColorStateList(R.color.white));
			// 设置确定按钮 禁用时的 颜色
			btn_confir.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_btn_gray1));
		}

	}

	/*
	 * private void getSmsInPhone() { final String SMS_URI_ALL =
	 * "content://sms/"; Calendar calendar = Calendar.getInstance();
	 * calendar.add(Calendar.DAY_OF_MONTH, -365); try { ContentResolver cr =
	 * getContentResolver(); String[] projection = new String[] { "_id",
	 * "address", "person", "thread_id", "body", "date", "type" }; Uri uri =
	 * Uri.parse(SMS_URI_ALL); Cursor cur = cr.query(uri, projection,
	 * "thread_id = " + SMS.getThread_id(), null, "date asc"); if
	 * (cur.moveToFirst()) { // int nameColumn = cur.getColumnIndex("person");
	 * int phoneNumberColumn = cur.getColumnIndex("address"); int smsbodyColumn
	 * = cur.getColumnIndex("body"); int dateColumn =
	 * cur.getColumnIndex("date"); int typeColumn = cur.getColumnIndex("type");
	 * int thread_idColum = cur.getColumnIndex("thread_id");
	 * 
	 * do {
	 * 
	 * Message message = new Message();
	 * 
	 * message.setPersonPhoneNumber(cur.getString(phoneNumberColumn)); // name =
	 * cur.getString(nameColumn); 这样获取的联系人为空 if
	 * (TextUtils.isEmpty(UtilToolkit.getCallerNameFromPhoneNumber(this,
	 * cur.getString(phoneNumberColumn)))) {
	 * message.setPersonName(cur.getString(phoneNumberColumn)); } else {
	 * message.setPersonName(UtilToolkit.getCallerNameFromPhoneNumber(this,
	 * cur.getString(phoneNumberColumn))); } if (cur.getString(smsbodyColumn) ==
	 * null) { message.setMessageContent(""); } else {
	 * message.setMessageContent(cur.getString(smsbodyColumn)); }
	 * message.setMessageDate(Long.parseLong(cur.getString(dateColumn)));
	 * message.setMessageType(cur.getInt(typeColumn));
	 * message.setThread_id(cur.getInt(thread_idColum)); if
	 * (message.getMessageType() == 1 || message.getMessageType() == 2)
	 * SMSes.add(message); } while (cur.moveToNext()); } cur.close(); cur =
	 * null; } catch (SQLiteException ex) {
	 * //Log.e("SQLiteException in getSmsInPhone", ex.getMessage()); }
	 * 
	 * }
	 */

	public void back(View v) {
		if (selectedList.isEmpty() && !"重新选择".equals(btn_confir.getText())) {
			E3ProofActivity.selectedList_sms = selectedList;
		}
		finish();
	}

	private void setViewOnclickListener() {
		btn_confir.setOnClickListener(this);
		lvSMSDetail.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_confir) {
			if (DATA_FROM_E3_PROOF_ACTIVITY.equals(getIntent().getStringExtra("from"))) {
				if ("重新选择".equals(btn_confir.getText())) {

					SkuaidiDialog dialog = new SkuaidiDialog(context);
					dialog.setTitle("提示");
					dialog.setContent("重选将清除现有数据，确定要重选？");
					dialog.isUseEditText(false);
					dialog.setPositionButtonTitle("取消");
					dialog.setNegativeButtonTitle("确认");
					dialog.showDialog();
					dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

						@Override
						public void onClick() {
							E3ProofActivity.selectedList_sms = selectedList;
							showProgressDialog( "正在加载短信，请稍候...");
							e3GetSMSTask = new E3GetSMSTask(context, handler);
							e3GetSMSTask.execute();
						}
					});

					// btn_confir.setText("确定(" + selected_num + "/" +
					// SMSes.size() + ")");
					// btn_confir.setTextColor(getResources().getColorStateList(R.color.white));
					// // 设置确定按钮 禁用时的 颜色
					// btn_confir.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_btn_gray1));
					// adapter.selectData = true;
					// adapter.notifyDataSetChanged();

				} else {
					commit();
				}
			} else {
				commit();
			}

		}
	}

	/**
	 * 确认提交，返回举证主页面
	 */
	private void commit() {

		Collections.sort(selectedList);
		E3ProofActivity.selectedList_sms = selectedList;
		if (E3ProofActivity.activityList.size() > 0) {
			for (Activity activity : E3ProofActivity.activityList) {
				activity.finish();
			}
			E3ProofActivity.activityList.clear();
		}
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if ("重新选择".equals(btn_confir.getText())) {
			return;// 查看状态时，item不可点击。
		}
		if (SMSes.get(position).isSelected) {
			// 改变单选按钮状态
			SMSes.get(position).setSelected(false);
		} else {
			SMSes.get(position).setSelected(true);
		}

		if (SMSes.get(position).getMessageType() == 1) {
			CheckBox cb_check_receive = (CheckBox) (view.findViewById(R.id.cb_check_receive));
			if (SMSes.get(position).isSelected()) {
				cb_check_receive.setChecked(true);
			} else {
				cb_check_receive.setChecked(false);
			}
		} else {
			CheckBox cb_check_send = (CheckBox) (view.findViewById(R.id.cb_check_send));
			if (SMSes.get(position).isSelected()) {
				cb_check_send.setChecked(true);
			} else {
				cb_check_send.setChecked(false);
			}
		}

		if (SMSes.get(position).isSelected) {
			selectedList.add(SMSes.get(position));
			if (selected_num >= SMSes.size()) {
				selected_num = SMSes.size();
			}
			selected_num++;
			btn_confir.setEnabled(true);
			btn_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);

		} else {
			if (selected_num >= 1)
				selected_num--;
			selectedList.remove(SMSes.get(position));
			if (selected_num == 0) {
				// 禁用
				btn_confir.setEnabled(false);
				btn_confir.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_btn_gray1));
			}
		}
		btn_confir.setText("确定(" + selected_num + "/" + SMSes.size() + ")");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (selectedList.isEmpty() && !"重新选择".equals(btn_confir.getText())) {
				E3ProofActivity.selectedList_sms = selectedList;
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
