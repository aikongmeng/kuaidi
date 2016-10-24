package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.AreaItem;
import com.kuaibao.skuaidi.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectCountyAdapter extends BaseAdapter {
	private Context context;
	private Handler handler;
	private List<AreaItem> areas;
	private List<AreaItem> areas_pack0;
	private List<AreaItem> areas_pack1;
	private List<AreaItem> areas_pack2;

	public SelectCountyAdapter(Context context, Handler handler, List<AreaItem> areas) {
		super();
		this.context = context;
		this.handler = handler;
		this.areas = areas;
		this.areas_pack0 = new ArrayList<AreaItem>();
		this.areas_pack1 = new ArrayList<AreaItem>();
		this.areas_pack2 = new ArrayList<AreaItem>();

		setData(areas);
	}

	public void setData(List<AreaItem> areas) {
		this.areas_pack0.clear();
		this.areas_pack1.clear();
		this.areas_pack2.clear();
		if (areas == null)
			return;
		for (int i = 0; i < areas.size(); i++) {
			if (i % 3 == 0) {
				this.areas_pack0.add(areas.get(i));
			} else if (i % 3 == 1) {
				this.areas_pack1.add(areas.get(i));
			} else if (i % 3 == 2) {
				this.areas_pack2.add(areas.get(i));
			}

		}
	}

	@Override
	public int getCount() {
		return areas_pack0.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.selectcounty_item, null);
		TextView tv_city_one = (TextView) convertView.findViewById(R.id.tv_selectcounty_item_one);
		TextView tv_city_two = (TextView) convertView.findViewById(R.id.tv_selectcounty_item_two);
		TextView tv_city_three = (TextView) convertView.findViewById(R.id.tv_selectcounty_item_three);

		tv_city_one.setText(areas_pack0.get(position).getName());

		if (position < areas_pack1.size()) {
			tv_city_two.setText(areas_pack1.get(position).getName());
		} else {
			tv_city_two.setText("");
		}
		if (position < areas_pack2.size()) {
			tv_city_three.setText(areas_pack2.get(position).getName());
		} else {
			tv_city_three.setText("");
		}

		tv_city_one.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//System.out.println(position + "," + 0);
				Message msg = new Message();
				msg.what = Constants.CLICK_AREA;
				msg.arg1 = position;
				msg.arg2 = 0;
				handler.sendMessage(msg);
			}
		});

		tv_city_two.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//System.out.println(position + "," + 1);
				Message msg = new Message();
				msg.what = Constants.CLICK_AREA;
				msg.arg1 = position;
				msg.arg2 = 1;
				handler.sendMessage(msg);
			}
		});

		tv_city_three.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//System.out.println(position + "," + 2);
				Message msg = new Message();
				msg.what = Constants.CLICK_AREA;
				msg.arg1 = position;
				msg.arg2 = 2;
				handler.sendMessage(msg);
			}
		});

		return convertView;
	}

}
