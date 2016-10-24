package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.DeleteCallLogsAdapter;
import com.kuaibao.skuaidi.activity.adapter.DeleteCallLogsAdapter.ViewHolder;
import com.kuaibao.skuaidi.activity.model.CallLog;
import com.kuaibao.skuaidi.activity.model.CallRecordingMp3;
import com.kuaibao.skuaidi.activity.model.CustomerCallLog;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * 录音举证：选择录音
 * 
 * @author wq
 * 
 */
@SuppressLint("HandlerLeak")
public class E3ProofAudioActivity extends SkuaiDiBaseActivity implements View.OnClickListener {
	private static final String BASE_TYPE = "delete";
	private List<CallLog> callLogs = new ArrayList<CallLog>();
	private ListView lv;
	private DeleteCallLogsAdapter adapter;
	private FinalDb finalDb;
	private TextView tv_title;
	private Context context;
	private SkuaidiImageView iv_title_back;// 返回按钮
	// 确定按钮
	private TextView log_btn_ok;
	private int select_num = 0;
	// 判断是否删除成功
	private boolean dellete_ok = false;
	// 存放删除item的集合
	private ArrayList<CallLog> checkedList = new ArrayList<CallLog>();
	// 界面底部
	private RelativeLayout ll_call_log;

	// 控制全选状态
	private boolean all_select = false;
	private String type = BASE_TYPE;
	public static final int RESULT_CODE = 2;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (getIntent().hasExtra("type")) {
			type = getIntent().getStringExtra("type");
		}
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.activity_e3proof_audio);
		finalDb = SKuaidiApplication.getInstance().getFinalDbCache();
		getViewInParent();
		getCallLogsFromDB();
		adapter = new DeleteCallLogsAdapter(this, callLogs);
		lv.setAdapter(adapter);
		setViewOnClickListener();

	}

	@SuppressWarnings("deprecation")
	private void getViewInParent() {
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(onclickListener);
		lv = (ListView) findViewById(R.id.call_logs);
		ll_call_log = (RelativeLayout) findViewById(R.id.ll_call_log);
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		tv_title.setText("上传录音证据");
		log_btn_ok = (TextView) findViewById(R.id.log_btn_ok);
		log_btn_ok.setText("确定(" + select_num + "/" + callLogs.size() + ")");
		ll_call_log.setBackgroundResource(R.color.add_cus_bg);
		log_btn_ok.setTextColor(getResources().getColorStateList(R.color.white));
		// 设置确定按钮 禁用时的 颜色
		log_btn_ok.setBackgroundResource(R.drawable.shape_btn_gray1);
//		log_btn_ok.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_btn_gray1));
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setItemsCanFocus(false);
		lv.setClickable(true);

	}

	/**
	 * 页面的点击事件
	 */
	private OnClickListener onclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				if (E3ProofActivity.activityList.size() > 0) {
					for (Activity activity : E3ProofActivity.activityList) {
						activity.finish();
					}
					E3ProofActivity.activityList.clear();
				}
				adapter.stopPlayRecording();
				finish();
				break;
			default:
				break;
			}
		}
	};

	protected void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	private void setViewOnClickListener() {
		log_btn_ok.setOnClickListener(this);
		lv.setOnItemClickListener(new MOnItemClickListener());
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			adapter.stopPlayRecording();
			if (E3ProofActivity.activityList.size() > 0) {
				for (Activity activity : E3ProofActivity.activityList) {
					activity.finish();
				}
				E3ProofActivity.activityList.clear();
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(final View v) {
		if (v.getId() == R.id.log_btn_ok) {
			// UMShareManager.onEvent(context, "callLog_callRecording_clear",
			// "callLog", "通话记录:清空录音");
			if ("E3ProofAudioViewActivity".equals(getIntent().getStringExtra("from"))) {
				E3ProofActivity.selectedList_audio = checkedList;
				finish();
			} else {
				Intent intent = new Intent();
				intent.putExtra("callLogs", checkedList);
				this.setResult(RESULT_CODE, intent);
				finish();
			}

		}
	}

	private class MOnItemClickListener implements OnItemClickListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ViewHolder holder = (ViewHolder) view.getTag();
			// 改变单选按钮状态
			holder.delete_list_item_new.toggle();
			adapter.getAllItem().get(position).setFlag(holder.delete_list_item_new.isChecked());
			if (holder.delete_list_item_new.isChecked()) {
				if (select_num >= callLogs.size()) {
					select_num = callLogs.size();
				}
				select_num += 1;
				// 关闭禁用功能
				log_btn_ok.setEnabled(true);
				log_btn_ok.setBackgroundResource(R.drawable.selector_base_green_qianse1);
				checkedList.add((CallLog) adapter.getItem(position));
			} else {
				if (select_num >= 1) {
					select_num -= 1;
					checkedList.remove(adapter.getItem(position));
					if (select_num == 0) {
						// 打开禁用功能
						log_btn_ok.setEnabled(false);
						log_btn_ok.setBackgroundResource(R.drawable.shape_btn_gray1);
//						log_btn_ok
//								.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_btn_gray1));
					}
				}

			}
			all_select = true;
			log_btn_ok.setText("确定(" + select_num + "/" + callLogs.size() + ")");
		}

	}

	private class MOnCheckedChangeListener implements OnCheckedChangeListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (arg1) {
				// 关闭确定禁用功能
				log_btn_ok.setEnabled(true);
				log_btn_ok.setBackgroundResource(R.drawable.selector_base_green_qianse1);
				all_select = false;
				for (int i = 0; i < callLogs.size(); i++) {
					if (callLogs.get(i).getFlag() == false) {
						callLogs.get(i).setFlag(true);
						checkedList.add(callLogs.get(i));
						select_num += 1;

					}
					// else if (callLogs.get(i).getFlag() == true) {
					// callLogs.get(i).setFlag(true);
					// }
					log_btn_ok.setText("确定(" + select_num + "/" + callLogs.size() + ")");
					adapter.notifyDataSetChanged();
				}
			} else {
				if (all_select == false) {
					// 开启确定禁用功能
					log_btn_ok.setEnabled(false);
					log_btn_ok.setBackgroundResource(R.drawable.shape_btn_gray1);
//					log_btn_ok.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_btn_gray1));
					for (int i = 0; i < callLogs.size(); i++) {
						if (callLogs.get(i).getFlag() == true) {
							select_num -= 1;
							callLogs.get(i).setFlag(false);
						}
						log_btn_ok.setText("确定(" + select_num + "/" + callLogs.size() + ")");
						adapter.notifyDataSetChanged();
					}
					checkedList.clear();
				}
			}
		}

	}

	protected String getType() {
		return type;
	}

	protected List<CallLog> getCallLogs() {
		return adapter.getAllItem();
	}

	protected ArrayList<CallLog> getCheckedList() {
		return checkedList;
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (code.equals("7") && null != result) {
			try {
				String desc = result.optString("desc");
				UtilToolkit.showToast(desc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * 获取通话记录(新)
	 */
	private void getCallLogsFromDB() {
		FinalDb finalDb = SKuaidiApplication.getInstance().getFinalDbCache();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -31);
		List<CustomerCallLog> customerCallLogs = finalDb.findAllByWhere(CustomerCallLog.class, "callDate >= "
				+ calendar.getTimeInMillis(), "callDate DESC");

		if (customerCallLogs.size() > 0) {
			for (CustomerCallLog customerCallLog : customerCallLogs) {
				CallLog callLog = new CallLog();
				callLog.setCallDate(customerCallLog.getCallDate());
				callLog.setCallDuration(customerCallLog.getCallDurationTime());
				callLog.setCallerName(KuaiBaoStringUtilToolkit.isEmpty(customerCallLog.getCustomerName()) ? "新客户"
						: customerCallLog.getCustomerName());
				callLog.setPhoneNumber(customerCallLog.getCallNum());
				callLog.setId(customerCallLog.getId());
				callLog.setCallType(customerCallLog.getType());
				callLog.setIsCanAddMSG(customerCallLog.getIsCanAddMSG());
				//System.out.println("isCanAddMSG : " + customerCallLog.getIsCanAddMSG());
				callLog.setIsCanAddOrder(customerCallLog.getIsCanAddOrder());
				//System.out.println("isCanAddOrder : " + customerCallLog.getIsCanAddOrder());
				callLog.setCustomerCallLog(customerCallLog);
				callLog.setIsUploaded(customerCallLog.getIsUploaded());
				List<CallRecordingMp3> mp3s = finalDb.findAllByWhere(CallRecordingMp3.class, "callId = '"
						+ customerCallLog.getId() + "'");
				if (mp3s.size() != 0) {
					callLog.setCallRecordingMp3(mp3s.get(0));
				}

				if (callLog.getCallRecordingMp3() != null)
					callLogs.add(callLog);
			}
		}

	}
}
