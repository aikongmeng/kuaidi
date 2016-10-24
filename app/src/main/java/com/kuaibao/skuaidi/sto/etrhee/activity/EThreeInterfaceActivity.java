package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.PositiveButtonOnclickListener;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.entry.ScanScope;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 巴枪扫描与短信对接接口
 * 
 * @author xy
 * 
 */

public class EThreeInterfaceActivity extends SkuaiDiBaseActivity {

	private TextView title, tv_desc;
	private String type;
	private List<NotifyInfo> infos = new ArrayList<NotifyInfo>();
	private Intent intent;
	private CourierReviewInfo courierReviewInfo;
	private Context context;

	public static ArrayList<Activity> activityList = new ArrayList<Activity>();
	RelativeLayout layout_pie, layout_bad, layout_signed;
	View line_top_pie, line_below_pie, line_top_bad, line_below_bad, line_top_signed, line_below_signed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		activityList.add(this);
		setContentView(R.layout.e3_interface_layout);
		getScanScope();
		getControl();
		getData();
	}

	@SuppressWarnings("unchecked")
	public void getData() {
		intent = new Intent(this, EthreeInfoScanActivity.class);
		intent.putExtra("from", "EThreeInterfaceActivity");
		for (NotifyInfo2 info : (List<NotifyInfo2>) getIntent().getSerializableExtra("e3WayBills")) {
			if (!KuaiBaoStringUtilToolkit.isEmpty(info.getExpress_number())) {
				NotifyInfo info2 = new NotifyInfo();
				info2.setSender_mobile(info.getSender_mobile());
				info2.setExpress_number(info.getExpress_number());
				info2.setScanTime(E3SysManager.getTimeBrandIndentify());
				infos.add(info2);
			}
		}
//		courierReviewInfo = BackUpService
//				.getfinalDb()
//				.findAllByWhere(CourierReviewInfo.class,
//						"courierPhone = '" + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'").get(0);
		courierReviewInfo= E3SysManager.getReviewInfo();
		tv_desc.setText("已扫描的单号将在操作后上传到E3系统，请选择操作类型");

	}

	/**
	 * 查询快递员巴枪扫描权限
	 */
	private void getScanScope() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "scan.access.get");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
	}

	private void getControl() {
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		title = (TextView) findViewById(R.id.tv_title_des);
		title.setText("巴枪扫描");
		layout_pie = (RelativeLayout) findViewById(R.id.pie_scan);
		layout_bad = (RelativeLayout) findViewById(R.id.bad_scan);
		layout_signed = (RelativeLayout) findViewById(R.id.signed_scan);
		line_top_pie = findViewById(R.id.line_top_pie);
		line_below_pie = findViewById(R.id.line_below_pie);
		line_top_bad = findViewById(R.id.line_top_bad);
		line_below_bad = findViewById(R.id.line_below_bad);
		line_top_signed = findViewById(R.id.line_top_signed);
		line_below_signed = findViewById(R.id.line_below_signed);
		ScanScope ss = SkuaidiSpf.getUserScanScope(context);
		setScanItems(ss);
	}

	private void setScanItems(ScanScope ss) {
		if (ss != null) {
			if (ss.getPj() != null) {
				if (ss.getPj().getAccess() == 1) {
					layout_pie.setVisibility(RelativeLayout.VISIBLE);
					line_top_pie.setVisibility(View.VISIBLE);
					line_below_pie.setVisibility(View.VISIBLE);
				} else {
					layout_pie.setVisibility(RelativeLayout.GONE);
					line_top_pie.setVisibility(View.GONE);
					line_below_pie.setVisibility(View.GONE);
				}
			}
			if (ss.getWtj() != null) {
				if (ss.getWtj().getAccess() == 1) {
					layout_bad.setVisibility(RelativeLayout.VISIBLE);
					line_top_bad.setVisibility(View.VISIBLE);
					line_below_bad.setVisibility(View.VISIBLE);
				} else {
					layout_bad.setVisibility(RelativeLayout.GONE);
					line_top_bad.setVisibility(View.GONE);
					line_below_bad.setVisibility(View.GONE);
				}
			}
			if (ss.getQsj() != null) {
				if (ss.getQsj().getAccess() == 1) {
					layout_signed.setVisibility(RelativeLayout.VISIBLE);
					line_top_signed.setVisibility(View.VISIBLE);
					line_below_signed.setVisibility(View.VISIBLE);
				} else {
					layout_signed.setVisibility(RelativeLayout.GONE);
					line_top_signed.setVisibility(View.GONE);
					line_below_signed.setVisibility(View.GONE);
				}
			}
		}
	}

	public void piePiceScan(View v) {
		type = "扫派件";
		for (int i = 0; i < infos.size(); i++) {
			infos.get(i).setCourierJobNO(courierReviewInfo.getCourierJobNo());
			infos.get(i).setWayBillTypeForE3(courierReviewInfo.getCourierName());
		}
		intent.putExtra("scanType", type);
		intent.putExtra("e3WayBills", (Serializable) infos);
		startActivity(intent);
	}

	public void badPiceScan(View v) {
		for (int i = 0; i < infos.size(); i++) {
			infos.get(i).setCourierJobNO("");
			infos.get(i).setWayBillTypeForE3("");
		}
		type = "问题件";
		intent.putExtra("scanType", type);
		intent.putExtra("e3WayBills", (Serializable) infos);
		startActivity(intent);
	}

	public void signedPiceScan(View v) {
		for (int i = 0; i < infos.size(); i++) {
			infos.get(i).setCourierJobNO("");
			infos.get(i).setWayBillTypeForE3("");
		}
		type = "扫签收";
		intent.putExtra("scanType", type);
		intent.putExtra("e3WayBills", (Serializable) infos);
		startActivity(intent);
	}

	public void back(View v) {
		showDialog(v);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(findViewById(R.id.iv_title_back));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	SkuaidiE3SysDialog dialog;

	private void showDialog(View v) {
		dialog = new SkuaidiE3SysDialog(this, SkuaidiE3SysDialog.TYPE_COMMON, v);
		if (infos.size() != 0) {
			if (!dialog.isShowing()) {

				dialog.setTitle("放弃巴枪扫描上传");
				dialog.setCommonContent("你将放弃上传已经扫描的单号!\n确认放弃？");
				dialog.setPositiveButtonTitle("确认");
				dialog.setNegativeButtonTitle("取消");
				dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

					@Override
					public void onClick() {
						UMShareManager.onEvent(EThreeInterfaceActivity.this, "E3_giveUp_scanConfirm", "E3",
								"E3：放弃上传扫描单号");
						finish();
					}
				});
				if (!isFinishing())
					dialog.showDialog();
			} else {
				if (!isFinishing())
					dialog.dismiss();
			}
		} else {
			finish();
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String data, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("scan.access.get".equals(sname)) {
			try {
				JSONObject jb = result.optJSONObject("retArr");
				if (jb != null) {
					Gson gson = new Gson();
					ScanScope ss = gson.fromJson(jb.toString(), ScanScope.class);
					SkuaidiSpf.saveUserScanScope(EThreeInterfaceActivity.this, ss);
					setScanItems(ss);
				}

			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if ("scan.access.get".equals(sname)) {

		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
