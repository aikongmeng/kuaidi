package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FastReplyAdapter;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 罗娜
 * 订单模块 快捷回复
 */
public class FastReplyActivity extends RxRetrofitBaseActivity {
	private Context context;
	private ExpandableListView exlist_reply;
	private FastReplyAdapter adapter;
	
	private int replyType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.pop_reply);
		context = this;
		
		Intent intent=getIntent();
		replyType=intent.getIntExtra("replytype", Constants.TYPE_REPLY_CUSTOMER);

		getControl();
		addListener();

	}
	private void getControl(){
		exlist_reply = (ExpandableListView) findViewById(R.id.exlist_reply);
		adapter = new FastReplyAdapter(context,replyType);
		exlist_reply.setAdapter(adapter);

	}

	private void addListener() {
		exlist_reply.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (adapter.getChildrenCount(groupPosition)<1) {
					Intent intent = getIntent();
					intent.putExtra("reply_content",
							adapter.getGroup(groupPosition));
					setResult(Constants.RESULT_CHOOSE_FAST_REPLY, intent);
					finish();
					return true;
				}
				return false;
			}
		});

		exlist_reply.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = getIntent();
				intent.putExtra("reply_content",
						adapter.getChild(groupPosition, childPosition));
				setResult(Constants.RESULT_CHOOSE_FAST_REPLY, intent);
				finish();
				return false;
			}
		});
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

}
