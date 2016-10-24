package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.SetUpStationActivity;
import com.kuaibao.skuaidi.activity.model.Station;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 物流站点适配器
 * 
 * @author hh
 * 
 */
@SuppressLint("SimpleDateFormat")
public class SetUpstationAdapter extends BaseAdapter {
	public List<Station> stations;
	private Context context;
	public int check_index;
	private Station station;
	private SkuaidiSpf spf;
	private Map<String, String>map;
	private List<Station>add_station=new ArrayList<Station>();
	public SetUpstationAdapter(Context context, List<Station> stations) {
		this.context = context;
		this.stations = stations;
	}

	@Override
	public int getCount() {
		return stations.size();
	}

	@Override
	public Object getItem(int position) {

		return stations.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	ViewHolder holder;

	@SuppressLint("ShowToast")
	@Override
	public View getView( final int position, View convertView,
			 ViewGroup arg2) {
		station = (Station) getItem(position);
		check_index = position;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.upstation_item, null);
			holder = new ViewHolder();

			holder.tv_station_name = (TextView) convertView
					.findViewById(R.id.tv_station_name);
			holder.tv_station_no = (TextView) convertView
					.findViewById(R.id.tv_station_no);
			holder.tv_station_add = (TextView) convertView
					.findViewById(R.id.tv_station_add);
			holder.iv_sta_delete=(ImageView) convertView.findViewById(R.id.iv_sta_delete);
			if (SetUpStationActivity.my_save==true) {
				holder.iv_sta_delete.setVisibility(View.VISIBLE);
				holder.tv_station_add.setVisibility(View.GONE);
			}else {
				holder.iv_sta_delete.setVisibility(View.GONE);
				holder.tv_station_add.setVisibility(View.VISIBLE);
			}
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(stations.get(position).getStation_Name())
				&& null != stations.get(position).getStation_Name()) {
			holder.tv_station_name.setText(stations.get(position)
					.getStation_Name());
		} else {
			holder.tv_station_name.setText("");
		}
		if (!TextUtils.isEmpty(stations.get(position).getStation_No())
				&& null != stations.get(position).getStation_No()) {
			holder.tv_station_no.setText(stations.get(position)
					.getStation_No());
		} else {
			holder.tv_station_name.setText("");
		}
		//删除
		holder.iv_sta_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
				Toast.makeText(context, "删除", 0).show();
			}
		});
		
		// 添加
		holder.tv_station_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加之前判断是否已经添加
				
//				add_station.add(station);
			//	stations.remove(position);
				//notifyDataSetChanged();
				
				
			}
		}); 

		return convertView;
	}


	public List<Station> getMySave() {
		return add_station;
	}
	public int getPosition() {
		return check_index;
	}

	

	public static class ViewHolder {
		public TextView tv_station_no, tv_station_name, tv_station_add;
		public ImageView iv_sta_delete;

	}

}
