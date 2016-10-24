package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * @author 罗娜 超派查询结果（查询条件为区域时）显示超派路段
 */
public class RoadNumbersActivity extends RxRetrofitBaseActivity {
	private Context context;

	private TextView tv_outofrange, tv_sendrang, tv_sendrange_info, tv_roadname;
	private TextView tv_roadnos;

	private List<String> roadnos;// 路段列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.pop_roadnos);
		context = this;

		Intent intent = getIntent();
		roadnos = (List<String>) intent.getSerializableExtra("roadnumbers");
		String roadname = intent.getStringExtra("roadname");

		tv_roadname = (TextView) findViewById(R.id.tv_roadname_pop);
		tv_roadnos = (TextView) findViewById(R.id.tv_roadnos);

		tv_roadname.setText(roadname);
		String text = "";
		for (int i = 0; i < roadnos.size(); i++) {
			if (i < roadnos.size() - 1) {
				text += roadnos.get(i) + "\n";
			} else {
				text += roadnos.get(i);
			}
		}
		tv_roadnos.setText(text);
		tv_roadnos.setMovementMethod(new LinkMovementMethod());

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

	private class roadnoAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return roadnos.size();
		}

		@Override
		public Object getItem(int position) {
			return roadnos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.pop_roadnos_item, null);
				holder.tv_roadnumber = (TextView) convertView.findViewById(R.id.tv_roadno);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_roadnumber.setText(roadnos.get(position));
			return convertView;
		}

		class ViewHolder {
			TextView tv_roadnumber;
		}

	}

}
