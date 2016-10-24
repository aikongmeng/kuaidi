package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.InformationAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.InformationInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * InformationActivity
 * 资讯中心
 * 顾冬冬
 * 2015-12-11 上午11:31:56
 */
public class InformationActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private SkuaidiImageView back;
	private TextView title;
	private ListView lv;
	private int maxPage;// 总页数
	private List<InformationInfo> list;
	private PullToRefreshView pull;
	private InformationInfo info;
	private InformationAdapter adapter;
	private final int SUCCESS = 0;
	private Context context;
	private String resource;
	private Message msg;

	private int page = 1;
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				list.addAll((List<InformationInfo>) msg.obj);
				if (list.size() == 0) {
					UtilToolkit.showToast("没有内容!");
				} else if (list.size() >= 10) {
					pull.enableScroolUp();
				}
				adapter.UpdateList(list);
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		UMShareManager.onEvent(this, "salesman_more_informationCenter", "salesman_more", "更多模块：资讯中心");
		setContentView(R.layout.infor_center);
		context = this;
		resource = getIntent().getStringExtra("resource");
		list = new ArrayList<>();
		getCoutrol();
		setListener();

		getNewsList(page);
		adapter = new InformationAdapter(context, list);
		lv.setAdapter(adapter);
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

	public void getCoutrol() {
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		pull.disableScroolDown();
		pull.disableScroolUp();
		lv = (ListView) findViewById(R.id.lv_information);
		title = (TextView) findViewById(R.id.tv_title_des);

		back.setOnClickListener(this);

		title.setText("资讯中心");
	}

	public void setListener() {
		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				if (page < maxPage) {
					page++;
					getNewsList(page);
				} else {
					UtilToolkit.showToast( "已加载全部数据");
					pull.onFooterRefreshComplete();
				}
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView tv_title = (TextView) arg1.findViewById(R.id.tv_information_title);
				Intent intent = new Intent();
				intent.putExtra("title", list.get(arg2).getTitle());
				intent.putExtra("group_id", list.get(arg2).getGroup_id());
				intent.putExtra("url", tv_title.getTag().toString());
				intent.setClass(InformationActivity.this, LoadWebInformationActivity.class);
				startActivity(intent);
			}

		});
	}

	public void back(View view) {
		if("splash".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}else if("circle".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("tabid", 1);
			startActivity(intent);
		}
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			if("splash".equals(resource)){
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
			}else if("circle".equals(resource)){
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("tabid", 1);
				startActivity(intent);
			}
			finish();
			break;

		default:
			break;
		}

	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		if("splash".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}
		finish();
	}

	/** 获取资讯中心列表[page:页数] **/
	private void getNewsList(int page) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "news/getlist");
			data.put("number", "10");// 每页条目
			data.put("page", page);// 页数
			data.put("detail", "1");// 传true or false 【新接口用1（true）或0表示】
			data.put("type", "server");// client or server
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		showProgressDialog( "正在加载...");
	}

	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		dismissProgressDialog();
		pull.onFooterRefreshComplete();
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (sname.equals("news/getlist") && null != result) {
			JSONObject state = result.optJSONObject("state");
			maxPage = state.optInt("MaxPage");
			try {
				JSONArray content = state.optJSONArray("content");
				List<InformationInfo> list = new ArrayList<>();
				if (null != content && content.length() > 0) {
					for (int i = 0; i < content.length(); i++) {
						info = new InformationInfo();
						JSONObject obj = (JSONObject) content.get(i);
						info.setId(obj.optString("id"));
						info.setSort(obj.optString("sort"));
						info.setKeywords(obj.optString("keywords"));
						info.setPriority(obj.optString("priority"));
						info.setClicks(obj.optString("clicks"));
						info.setImg(obj.optString("img"));
						info.setGroup_id(obj.optString("group_id"));
						info.setTitle(obj.optString("title"));
						info.setDescription(obj.optString("description"));
						info.setTime(obj.optString("time"));
						info.setUrl(obj.optString("url"));
						list.add(info);
					}
				}
				msg = new Message();
				msg.what = SUCCESS;
				msg.obj = list;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		pull.onFooterRefreshComplete();
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		pull.onFooterRefreshComplete();

	}
}
