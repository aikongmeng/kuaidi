package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.AudioProofPreviewAdapter;
import com.kuaibao.skuaidi.activity.model.CallLog;
import com.kuaibao.skuaidi.activity.model.CallRecordingMp3;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.business.nettelephone.calllog.ICallLogManagerActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.customer.MyCustomManageActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiPopAboutCheckList;
import com.kuaibao.skuaidi.dialog.SkuaidiPopAboutCheckList.ItemOnclickListener;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.KuaiBaoFileUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 通讯记录
 * 
 * @author xy
 * 
 */
public class E3ProofAudioViewActivity extends SkuaiDiBaseActivity implements View.OnClickListener {
	private List<CallLog> callLogs = new ArrayList<CallLog>();
	private ListView lv;
	private AudioProofPreviewAdapter adapter;
	private TextView tv_title;
	private Context context;
	private View page_jump_customer_management,out_fram;
	private SkuaidiImageView iv_title_back;// 返回按钮
	private PopupWindow popupWindow;
	private SkuaidiPopAboutCheckList popAboutCheckList;
	private Button btn_reelect;

	// 删除按钮
	private View more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.call_log_layout);
		getViewInParent();
		callLogs = (List<CallLog>) getIntent().getSerializableExtra("callLogs");
		adapter = new AudioProofPreviewAdapter(this, callLogs);
		lv.setAdapter(adapter);
		setViewOnClickListener();

	}

	private void getViewInParent() {
		out_fram = findViewById(R.id.out_fram);
		more = findViewById(R.id.bt_title_more);
		more.setVisibility(View.GONE);
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(onclickListener);
		lv = (ListView) findViewById(R.id.call_logs);
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		tv_title.setText("录音证据");
		btn_reelect = (Button) findViewById(R.id.btn_reelect);
		out_fram.setVisibility(View.VISIBLE);

		/**
		 * 没有通话记录，隐藏删除按钮
		 */
		if (callLogs.size() != 0) {
			more.setVisibility(View.VISIBLE);
		}
		page_jump_customer_management = findViewById(R.id.page_jump_customer_management);

	}

	/**
	 * 页面的点击事件
	 */
	private OnClickListener onclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				adapter.stopPlayRecording();
				finish();
				break;
			case R.id.bt_title_more:

				if (popAboutCheckList == null || !popAboutCheckList.isShowing()) {
					List<String> titleList = new ArrayList<String>();
					titleList.add("上传录音");
					titleList.add("删除记录");
					popAboutCheckList = new SkuaidiPopAboutCheckList(context, v, titleList);
					popAboutCheckList.setItemOnclickListener(new ItemOnclickListener() {

						@Override
						public void onClick(int position) {
							if (position == 0) {
								Intent intent = new Intent(context, ICallLogManagerActivity.class);
								intent.putExtra(ICallLogManagerActivity.MANAGER_TYPE_NAME,ICallLogManagerActivity.MANAGER_UPLOAD_TYPE);
								context.startActivity(intent);
								finish();
							} else if (position == 1) {
								Intent intent=new Intent(E3ProofAudioViewActivity.this, ICallLogManagerActivity.class);
								intent.putExtra(ICallLogManagerActivity.MANAGER_TYPE_NAME,ICallLogManagerActivity.MANAGER_DELETE_TYPE);
								context.startActivity(intent);
								finish();
							}
						}
					});
					popAboutCheckList.showPop();
				} else if (popAboutCheckList != null && popAboutCheckList.isShowing()) {
					popAboutCheckList.dismiss();
				}
				break;
			default:
				break;
			}
		}
	};

	private void setViewOnClickListener() {
		page_jump_customer_management.setOnClickListener(this);
		more.setOnClickListener(onclickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SKuaidiApplication.getInstance().onReceiveMsg("CallLogsActivity", "addSuccess") != null
				&& Integer.parseInt(SKuaidiApplication.getInstance().onReceiveMsg("CallLogsActivity", "addSuccess")
						.toString()) == 1) {
			adapter.notifyDataSetChanged();
			SKuaidiApplication.getInstance().postMsg("CallLogsActivity", "addSuccess", 0);
		}
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
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
				return true;
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(final View v) {
		if (v.getId() == R.id.bt_title_more && ((Button) v).getText().toString().equals("更多选项")) {
			popupWindow = new PopupWindow(E3ProofAudioViewActivity.this);

			View layout = LayoutInflater.from(E3ProofAudioViewActivity.this).inflate(
					R.layout.window_call_log_more_check, null);
			layout.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					if ((arg1 == KeyEvent.KEYCODE_MENU) && (popupWindow.isShowing())) {
						popupWindow.dismiss();
						return true;
					}
					return false;
				}
			});
			layout.findViewById(R.id.call_log_batch_add).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					((Button) v).setText("保存");
					adapter.isBatchAdd(true);
					v.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
						}
					});
					popupWindow.dismiss();
				}
			});
			layout.findViewById(R.id.call_log_clear_recording).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						KuaiBaoFileUtilToolkit.deleteAllFileInFolder(new File(Constants.ROOT
								+ "/skuaidi/call_recording"));
						SKuaidiApplication.getInstance().getFinalDbCache().deleteByWhere(CallRecordingMp3.class, "1=1");
						for (int i = 0; i < callLogs.size(); i++) {
							callLogs.get(i).setCallRecordingMp3(null);
						}
						adapter.notifyDataSetChanged();
						UtilToolkit.showToast("清空成功");
					} catch (Exception e) {
						e.printStackTrace();
						UtilToolkit.showToast("清空失败");
					}
					popupWindow.dismiss();
				}
			});
			popupWindow.setWidth(v.getWidth());
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(layout);
			popupWindow.setFocusable(true);
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			popupWindow.setBackgroundDrawable(dw);
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
				popupWindow = null;
			} else {
				popupWindow.showAsDropDown(v);
			}
		} else if (v.getId() == R.id.page_jump_customer_management) {
			UMShareManager.onEvent(context, "callLog_customer_manager", "callLog", "通话记录:管理客户");
			Intent intent = new Intent(context, MyCustomManageActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();
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

	/**
	 * 重选
	 * 
	 * @param view
	 */
	public void reelect(View view) {

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
				Intent intent = new Intent(context, E3ProofAudioActivity.class);
				intent.putExtra("from", "E3ProofAudioViewActivity");
				startActivity(intent);
				finish();
			}
		});
		// E3ProofActivity.selectedList_audio.clear();

	}

}
