package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Vantages;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 积分列表
 * 
 * @author xuyang
 * 
 */

public class MyVantagesActivity extends SkuaiDiBaseActivity implements OnHeaderRefreshListener,
		OnFooterRefreshListener, OnItemClickListener, OnClickListener {
	// 积分说明
	private static final String HELP_VANTAGES = "http://m.kuaidihelp.com/help/app_exp_jf.html";
	private TextView title, myVantages;
	private Context context;
	private PullToRefreshView pullView;
	private ListView lv_vantages_journal;
	private List<Vantages> vantageses;
	private MyVantagesAdapter vantagesAdapter;
	private SkuaidiTextView tv_title_more;
	private int page = 1;
	private int pageSize = 15;
	private boolean isFooter = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.my_vantages_layout);
		context = this;
		getControl();
		initDatas();
		addListener();
	}

	private void getControl() {
		title = (TextView) findViewById(R.id.tv_title_des);
		tv_title_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		myVantages = (TextView) findViewById(R.id.tv_my_vantages);
		pullView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		pullView.setOnFooterRefreshListener(this);
		pullView.setOnHeaderRefreshListener(this);
		lv_vantages_journal = (ListView) findViewById(R.id.lv_vantages_journal);
	}

	private void initDatas() {
		title.setText("我的积分");
		tv_title_more.setText("积分说明");
		myVantages.setText(getIntent().getStringExtra("score"));
		vantageses = new ArrayList<Vantages>();
		vantagesAdapter = new MyVantagesAdapter();
		lv_vantages_journal.setAdapter(vantagesAdapter);
		requestData();
	}

	private void requestData() {
		if (!Utility.isNetworkConnected()) {
			UtilToolkit.showToast("网络未连接，连接网络后再重试");
			return;
		}
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "score");
			data.put("act", "score.list");
			data.put("page", page);
			data.put("page_size", pageSize);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	private void addListener() {
		tv_title_more.setOnClickListener(this);
		lv_vantages_journal.setOnItemClickListener(this);
	}

	public void back(View view) {
		finish();
	}

	private class MyVantagesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return vantageses.size();
		}

		@Override
		public Vantages getItem(int position) {
			return vantageses.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(R.layout.my_vantages_item, null);
			TextView vantagesTag = (TextView) convertView.findViewById(R.id.tv_vantages_tag);
			TextView vantagesTime = (TextView) convertView.findViewById(R.id.tv_vantages_time);
			TextView vantagesPs = (TextView) convertView.findViewById(R.id.tv_vantages_ps);
			Vantages vantages = getItem(position);
			vantagesTag.setText(vantages.getRuleName());
			vantagesTime.setText(vantages.getTime());
			vantagesPs.setText(vantages.getPs());
			if (vantages.getRuleType().equals("out")) {
				vantagesPs.setTextColor(getResources().getColor(R.color.red));
			}
			return convertView;
		}

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (sname.equals("score") && act.equals("score.list")) {
			JSONArray array = result.optJSONArray("list");
			if (array != null) {
				List<Vantages> tempList = new ArrayList<Vantages>();
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.optJSONObject(i);
					Vantages vantages = new Vantages();
					vantages.setId(object.optString("id"));
					vantages.setRuleName(object.optString("ruleName"));
					vantages.setTime(object.optString("datetime"));
					vantages.setRuleType(object.optString("ruleType"));
					vantages.setScore(object.optString("score"));
					vantages.setRuleAlias(object.optString("ruleAlias"));
					vantages.setRuleId(object.optString("ruleId"));
					vantages.setRuleInfo(object.optString("ruleInfo"));
					vantages.setDesc(object.optString("desc"));
					vantages.setRemarkUrl(object.optString("url"));
					vantages.setPs((vantages.getRuleType().equals("in") ? "+" : "-") + vantages.getScore());
					tempList.add(vantages);
				}
				if (isFooter) {
					vantageses.addAll(tempList);
				} else {
					vantageses.clear();
					vantageses.addAll(tempList);
				}
				vantagesAdapter.notifyDataSetChanged();
			}
			pullView.onFooterRefreshComplete();
			pullView.onHeaderRefreshComplete();
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		UtilToolkit.showToast(result);
		pullView.onFooterRefreshComplete();
		pullView.onHeaderRefreshComplete();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		pullView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!Utility.isNetworkConnected()) {
					UtilToolkit.showToast("无网络连接");
					pullView.onFooterRefreshComplete();
					pullView.onHeaderRefreshComplete();
				} else {
					page = page + 1;
					isFooter = true;
					requestData();
				}
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		pullView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!Utility.isNetworkConnected()) {
					UtilToolkit.showToast("无网络连接");
					pullView.onFooterRefreshComplete();
					pullView.onHeaderRefreshComplete();
				} else {
					isFooter = false;
					page = 1;
					requestData();
				}
			}
		}, 1000);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(context, MyVantagesDetailActivity.class);
		intent.putExtra("vantages", vantageses.get(position));
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_more:
			loadWeb(HELP_VANTAGES, "");
			break;

		default:
			break;
		}
	}

}
