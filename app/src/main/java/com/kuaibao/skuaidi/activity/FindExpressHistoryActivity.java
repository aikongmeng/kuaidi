package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FindExpressAdapter;
import com.kuaibao.skuaidi.activity.adapter.FindExpressHistoryAdapter;
import com.kuaibao.skuaidi.activity.view.CustomDialog;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.ExpressHistory;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 查件记录
 */
public class FindExpressHistoryActivity extends RxRetrofitBaseActivity {
	private Context context;

	private List<ExpressHistory> expressHistories;

	private ListView lv;
	private TextView tv_title_des;
	private boolean moreScan = false;
	private FindExpressHistoryAdapter adapter;
	private FindExpressAdapter mAdapter;
	private List<ExpressHistory> infos;
	private List<NotifyInfo> ConScans;
	
	private SkuaidiDB skuaidiDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.findexpress_history);
		context = this;
		skuaidiDb = SkuaidiDB.getInstanse(context);
		expressHistories = new ArrayList<ExpressHistory>();
		infos = new ArrayList<ExpressHistory>();
		ConScans = new ArrayList<NotifyInfo>();
		getControl();
		setListener();
		setData();

	}

	/*
	 * @Override protected void onRestart() { expressHistories.clear();
	 * expressHistories.addAll(skuaidiDb.getExpressHistory());
	 * 
	 * adapter.notifyDataSetChanged(); }
	 */

	public void getControl() {
		lv = (ListView) findViewById(R.id.lv_findexpress_history);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("查件记录");
		moreScan = getIntent().getBooleanExtra("moreScan", false);
	}
	
	public void setListener() {

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				if (!moreScan) {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							FindExpressHistoryActivity.this);
					builder.setTitle("提示");
					builder.setMessage("是否确定删除该条记录?");
					builder.setPositiveButton("确定", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							skuaidiDb.deleteExpressHistory(expressHistories
									.get(arg2).getDeliverNo());
							if (skuaidiDb.getExpressHistory() != null) {
								expressHistories.clear();
								expressHistories.addAll(skuaidiDb
										.getExpressHistory());
								adapter.notifyDataSetChanged();
							} else {
								expressHistories.clear();
								adapter.notifyDataSetChanged();
							}
						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
				return false;
			}

		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();

				intent.putExtra("expressfirmName",
						SkuaidiSpf.getLoginUser().getExpressFirm());
				intent.putExtra("express_no", SkuaidiSpf.getLoginUser()
						.getExpressFirm());
				if (moreScan) {
					intent.putExtra("order_number", ConScans.get(arg2)
							.getExpress_number());
					intent.putExtra("moreScan", moreScan);
					// skuaidiDb.clearTableOrder();
				} else {
					intent.putExtra("order_number", expressHistories.get(arg2)
							.getDeliverNo());
				}
				intent.setClass(context, CopyOfFindExpressResultActivity.class);
				startActivity(intent);
			}

		});
	}

	public void setData() {
		if (moreScan) {
			List<NotifyInfo> list = (List<NotifyInfo>) getIntent().getSerializableExtra("express_list");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					ConScans.add(list.get(i));
				}
			}
			mAdapter = new FindExpressAdapter(context, ConScans);
			lv.setAdapter(mAdapter);
		} else if (skuaidiDb.getExpressHistory() != null) {
			expressHistories.clear();
			expressHistories.addAll(skuaidiDb.getExpressHistory());
			adapter = new FindExpressHistoryAdapter(context, expressHistories);
			lv.setAdapter(adapter);  
			adapter.notifyDataSetChanged();
		}
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
	
	public void back(View view) {
		finish();
	}

}
