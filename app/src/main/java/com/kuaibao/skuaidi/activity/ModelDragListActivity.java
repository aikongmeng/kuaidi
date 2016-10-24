package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ModelDragListAdapter;
import com.kuaibao.skuaidi.activity.view.SkuaidiDragListView;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.util.IsGuid;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板列表拖曳
 * @author gudd
 *
 */
public class ModelDragListActivity extends SkuaiDiBaseActivity {
	private Context context;
	private SkuaidiDB skuaidiDb;
	
	private ModelDragListAdapter modelDragListAdapter;
	private SkuaidiImageView iv_title_back;// 返回按钮
	private TextView tv_title_des;// title
	private SkuaidiTextView tv_more;// 完成按钮
	private SkuaidiDragListView drag_list;// 模板列表
	
	private List<ReplyModel> models = new ArrayList<ReplyModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.model_drag_list_activity);
		this.context = this;
		skuaidiDb = SkuaidiDB.getInstanse(context);

		initView();
		initData();
		addGuid();
	}
	
	private void initView(){
		drag_list = (SkuaidiDragListView) findViewById(R.id.drag_list);
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		
		iv_title_back.setOnClickListener(onClickListener);
		tv_more.setOnClickListener(onClickListener);
	}
	
	private void initData() {
		tv_title_des.setText("我的模板");
		tv_more.setVisibility(View.VISIBLE);
		tv_more.setText("完成");
		getModels();// 获取模板
	}
	
	private void getModels(){
		models = ModelActivity.getDragModels();
		modelDragListAdapter = new ModelDragListAdapter(context, models);
		drag_list.setAdapter(modelDragListAdapter);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:// 返回
				finish();
				break;
				
			case R.id.tv_more:// 完成按钮
				List<ReplyModel> models = new ArrayList<>();
				models = modelDragListAdapter.getModels();
				// 将新获取到的列表按照下标设置好顺序【从0开始】
				for (int i = 0; i < models.size(); i++) {
					models.get(i).setSortNo(i+"");
				}
				skuaidiDb.insertNewReplyModel(models);
				Intent intent = new Intent();
				intent.putExtra("models", (Serializable)models);
				setResult(902, intent);
				finish();
				break;

			default:
				break;
			}
		
		}
	};
	
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
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		return false;
	}
	
	private void addGuid() {
		final RelativeLayout meng = (RelativeLayout) findViewById(R.id.meng);
		if (IsGuid.activityIsGuided(this, this.getClass().getName())) {
			meng.setVisibility(View.GONE);
			return;
		} else {
			meng.setVisibility(View.VISIBLE);
		}
		meng.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					meng.setVisibility(View.GONE);
					IsGuid.setIsGuided(getApplicationContext(),ModelDragListActivity.this.getClass().getName());
			}
		});
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
	}

}
