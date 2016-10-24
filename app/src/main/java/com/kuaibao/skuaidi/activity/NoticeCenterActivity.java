package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.NoticeAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.NoticeInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * 通知中心
 * @author xy,gdd
 *
 */
public class NoticeCenterActivity extends SkuaiDiBaseActivity {
	private Context context;

	private ListView lv_notice_detail;
	private List<NoticeInfo> notices;
	private NoticeAdapter adapter;
	private PullToRefreshView refreshView;
	private String resource;
	
	private View  rl_notice_null;
	private TextView tv_title_des;
	private static final int REQUEST_DETAIL = 900;
	private static final int RESULT_CODE = 901;
	
	private int unRead;
	private int page = 1;
	private int pageSize = 15;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.list_notice);
		context = this;
		notices = new ArrayList<NoticeInfo>();
		resource = getIntent().getStringExtra("resource");
		getControl();
		getData();
		getNoticeList();
		setListener();
	}

	private void setListener() {
		
		refreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				refreshView.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (Utility.isNetworkConnected() == true) {
							notices.clear();
							page = 1;
							getNoticeList();
						}
						refreshView.onFooterRefreshComplete();
						refreshView.onHeaderRefreshComplete();
					}
				}, 1000);
			}
		});
		refreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (Utility.isNetworkConnected() == true) {
							page += 1;
							getNoticeList();
						}
						refreshView.onFooterRefreshComplete();
						refreshView.onHeaderRefreshComplete();
					}
				}, 1000);

			}
		});
		
		lv_notice_detail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(context, NoticeDetailActivity.class);
				intent.putExtra("notice", notices.get(arg2));
				startActivityForResult(intent, REQUEST_DETAIL);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_CODE){
			for (int i = 0; i < notices.size(); i++) {
				if(notices.get(i).getNoticeId().equals(data.getStringExtra("notice_id"))){
					notices.get(i).setUnRead(data.getIntExtra("read_state", 0));
					adapter.notifyDataSetChanged();
					break;
				}
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void getData() {
		if (!Utility.isNetworkConnected()) {
			lv_notice_detail.setVisibility(View.GONE);
		} else {
			lv_notice_detail.setVisibility(View.VISIBLE);
		}
	}

	private void getNoticeList() {
		JSONObject data = new JSONObject();
		try {
//			data.put("sname", "cm_notice_get_list");
//			data.put("cm_id", SkuaidiSpf.getLoginUser().getUserId());
//			data.put("page", page);
//			data.put("limit", pageSize);
			data.put("sname", "notice/getlist");
			data.put("cm_id", SkuaidiSpf.getLoginUser().getUserId());
			data.put("limit", pageSize);
			data.put("page", page);
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	private void getControl() {
		refreshView = (PullToRefreshView) findViewById(R.id.refreshView);
		lv_notice_detail = (ListView) findViewById(R.id.lv_notice_detail);
		adapter = new NoticeAdapter(context, notices, unRead);
		lv_notice_detail.setAdapter(adapter);
		rl_notice_null = findViewById(R.id.rl_notice_null);
		
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("通知中心");
		
	}

	public void back(View view) {
		if("splash".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}else if("circle".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("tabid",1);
			startActivity(intent);
		}
		finish();
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
	public void onBackPressed() {
//		super.onBackPressed();
		if("splash".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}else if("circle".equals(resource)){
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra("tabid",1);
			startActivity(intent);
		}
		finish();
	}
	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		try {
			if(sname.equals("notice/getlist")){
				JSONArray array = new JSONArray(json);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					NoticeInfo info = new NoticeInfo();
					info.setType(object.optString("type"));
					info.setCreatTime(object.optLong("create_time"));
					info.setUnRead(object.optInt("read_status"));
					info.setContent(object.optString("content"));
					info.setNoticeId(object.optString("cmn_id"));
					info.setIs_top(object.optInt("is_top"));
					if(object.optInt("is_top") == 1){
						notices.add(0, info);
					}else{
						notices.add(info);
					}
				}
				adapter.notifyDataSetChanged();
				if(array == null || array.length() == 0){
					UtilToolkit.showToast("加载完成...");
					if(page==1){
						refreshView.setVisibility(View.GONE);
						rl_notice_null.setVisibility(View.VISIBLE);
					}
				}else{
					if(refreshView.getVisibility() == View.GONE){
						refreshView.setVisibility(View.VISIBLE);
					}
					if(rl_notice_null.getVisibility() == View.VISIBLE){
						rl_notice_null.setVisibility(View.GONE);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		UtilToolkit.showToast(result);
		
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
