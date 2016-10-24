package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.AreaListAdapter;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.LatticePoint;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 罗娜 超派查询结果（查询条件包含区域和详细地址）
 */
public class OverAreaListActivity extends SkuaiDiBaseActivity {
	private static final String STRING_EMPTY = "[]";// 接口返回空数据

	private static final String ACT = "area_list";

	private static final String SNAME = "get_range_list";

	private Context context;

	private TextView tv_title_des;
	private ListView lv_area_list;
	private String address, detail;
	private AreaListAdapter mAdapter;
	private ArrayList<LatticePoint> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);

		setContentView(R.layout.activity_over_area_list);

		context = this;

		getControl();
		getData();
	}

	private void getControl() {
		lv_area_list = (ListView) findViewById(R.id.lv_area_list);

		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("网点列表");

		lv_area_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UMShareManager.onEvent(context, "OverAreaList", "OverArea", "网点列表:点击查看网点");
				Intent intent = new Intent(context, LatticePointAreaActivity.class);
				intent.putExtra("latticePoint", list.get(position));
				startActivity(intent);
			}
		});
	}

	private void getData() {
		Intent intent = getIntent();
		address = intent.getStringExtra("address");
		detail = intent.getStringExtra("detail");

		getRangeList(intent.getStringExtra("area_id"));

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

	private void getRangeList(String area_id) {
		JSONObject json = new JSONObject();
		try {
			json.put("sname", SNAME);
			json.put("act", ACT);
			json.put("area_id", area_id);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (SNAME.equals(sname)) {

			if (result != null) {
				JSONArray array = result.optJSONArray("retArr");
				if (array != null)
					list = parseSysMsgFromJson(array.toString());

			}

			if (list != null && list.size() != 0) {
				mAdapter = new AreaListAdapter(context, list);
				lv_area_list.setAdapter(mAdapter);
			} else {
				if (result != null) {
					if (STRING_EMPTY.equals(result.optString("retArr")))
						UtilToolkit.showToast( "没有相关网点信息!");
					else
						UtilToolkit.showToast(result.optString("retArr"));
				}
			}

		}

	}

	public ArrayList<LatticePoint> parseSysMsgFromJson(String jsonData) {
		Gson gson = new Gson();
		ArrayList<LatticePoint> list = new ArrayList<LatticePoint>();
		list = gson.fromJson(jsonData, new TypeToken<List<LatticePoint>>() {
		}.getType());

		return list;
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (SNAME.equals(sname))
			UtilToolkit.showToast( result);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	public void back(View view) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back(null);
		}

		return super.onKeyDown(keyCode, event);
	}
}
