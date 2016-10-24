package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * @author 罗娜 超派查询
 */
public class OverareaQueryActivity extends SkuaiDiBaseActivity {
	private Context context;

	private LinearLayout ll_area;
	private TextView tv_area, tv_title_des;
	private EditText et_address;
	private Button bt_overarea_query, bt_delete_address;

	private String area = null, count_id = null;
	private int pro_no, ct_no, cy_no;
	private SkuaidiImageView iv_title_back;// 返回按钮

	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);

		setContentView(R.layout.overarea_query);

		context = this;

		getControl();
		// setListener();
	}

	private void getControl() {
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(onclickListener);
		ll_area = (LinearLayout) findViewById(R.id.ll_area);
		tv_area = (TextView) findViewById(R.id.tv_area);
		// et_address = (EditText) findViewById(R.id.et_address);
		bt_overarea_query = (Button) findViewById(R.id.bt_overarea_query);
		// bt_overarea_query.setBackgroundColor(SkuaidiSkinManager.getTextColor("default_green_2"));
		// bt_delete_address = (Button) findViewById(R.id.bt_delete_address);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("超派查询");

	}

	private OnClickListener onclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;

			default:
				break;
			}
		}
	};

	private void setListener() {
		et_address.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (et_address.getText().toString().equals("")) {
					bt_delete_address.setVisibility(View.GONE);
				} else {
					bt_delete_address.setVisibility(View.VISIBLE);
				}

			}
		});

	}

	// 选择行政区域
	public void chooseArea(View view) {
		intent = new Intent(context, SelectCountyActivity.class);
		startActivityForResult(intent, Constants.REQUEST_CHOOSE_AREA);
	}

	public void deleteAddress(View view) {
		et_address.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_CHOOSE_AREA && resultCode == Constants.RESULT_CHOOSE_AREA) {
			area = data.getStringExtra("area");
			pro_no = data.getIntExtra("pro_index", 0);
			ct_no = data.getIntExtra("ct_index", 0);
			cy_no = data.getIntExtra("cy_index", 0);
			count_id = data.getStringExtra("count_id");

			tv_area.setText(area);
		}

	}

	/**
	 * @param view
	 *            根据输入信息开始超派查询
	 */
	public void overareaQuery(View view) {
		UMShareManager.onEvent(context, "OverAreaQuery", "OverArea", "区域网点查询");

		if (TextUtils.isEmpty(area)) {
			UtilToolkit.showToast( "请选择行政区域");
		}

		else if (false) {
			Intent intent = new Intent(context, OverareaResultNoAddressActivity.class);
			intent.putExtra("area_id", count_id);
			startActivity(intent);
		}

		else {
			// Intent intent = new Intent(context,
			// OverareaResultActivity.class);
			Intent intent = new Intent(context, OverAreaListActivity.class);
			intent.putExtra("area_id", count_id);
			// intent.putExtra("address", et_address.getText().toString());
			// intent.putExtra("detail", area +
			// et_address.getText().toString());
			intent.putExtra("detail", area);
			startActivity(intent);
		}

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
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

}
