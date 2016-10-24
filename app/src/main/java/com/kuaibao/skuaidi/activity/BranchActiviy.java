package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.BranchAdapter;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 罗娜 根据快递公司和地区标识显示网点列表 列表最后一行为其他网点
 */
public class BranchActiviy extends RxRetrofitBaseActivity {
	private Context context;
	private LinearLayout ll_other;
	private ListView lv_branchresult;
	private TextView tv_title_des;

	private List<BranchInfo> overareas = new ArrayList<BranchInfo>();
	private BranchAdapter adapter;

	private String express_no, area_id;// 公司标识和地区标识
	private String branch_name;
	//private ProgressDialog pdWaitingBranch;

	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.BRANCH_GET_OK:
				JsonXmlParser.parseBranchInfo(handler, msg.obj.toString(),
						SkuaidiSpf.getLoginUser().getExpressNo());
				break;

			case Constants.BRANCH_GET_FAILD:
				dismissProgressDialog();
				//pdWaitingBranch.dismiss();
				UtilToolkit.showToast( "对不起,网络发生异常!");
				break;

			case Constants.BRANCH_PARSE_OK:
				overareas = (List<BranchInfo>) msg.obj;
				adapter = new BranchAdapter(context, overareas);
				lv_branchresult.setAdapter(adapter);
				dismissProgressDialog();
				//pdWaitingBranch.dismiss();

				break;

			case Constants.BRANCH_PARSE_FAILD:
				ll_other.setVisibility(View.VISIBLE);
				dismissProgressDialog();
				//pdWaitingBranch.dismiss();
				break;
			case Constants.NETWORK_FAILED:
				UtilToolkit.showToast( "网络连接错误,请稍后重试!");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.list_branch);

		context = this;
		branch_name = "其他网点";

		getControl();
		getData();
		setListener();
	}

	private void setListener() {
		lv_branchresult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = getIntent();

				if (arg2 < overareas.size()) {
					branch_name = overareas.get(arg2).getIndexShopName();
					intent.putExtra("branch_name", branch_name);
					intent.putExtra("index_shop_id", overareas.get(arg2)
							.getIndexShopId());
				} else {
					intent.putExtra("branch_name", "其他网点");
				}
				setResult(Constants.RESULT_CHOOSE_BRANCH, intent);
				finish();
			}
		});
	}

	private void getControl() {
		ll_other = (LinearLayout) findViewById(R.id.ll_list_branch);
		lv_branchresult = (ListView) findViewById(R.id.lv_branchresult);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("选择网点");
	}

	private void getData() {
		Intent intent = getIntent();
		express_no = intent.getStringExtra("express_no");
		area_id = intent.getStringExtra("count_id");

		// KuaidiApi.getBranch(context, handler, express_no, area_id);
		KuaidiApi.getBranch(context, handler, area_id, "", express_no);

		showProgressDialog("");
	}

	public void selectbranch(View view) {
		Intent intent = new Intent();
		intent.putExtra("branch_name", "其他网点");
		setResult(Constants.RESULT_CHOOSE_BRANCH, intent);
		finish();
	}

	public void back(View view) {
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
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

}
