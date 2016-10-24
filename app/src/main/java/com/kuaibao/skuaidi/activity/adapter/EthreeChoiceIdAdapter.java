package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;

import java.util.List;

/*
 * E3入口选择多个用户信息
 * 自定义适配器
 */
public class EthreeChoiceIdAdapter extends BaseAdapter {
	Context context;
	List<CourierReviewInfo> courierInfos;
	CourierReviewInfo courierReviewInfo;
	private LayoutParams params;
	private int checkedIndex = 0;

	public EthreeChoiceIdAdapter(Context context,
			List<CourierReviewInfo> courierInfos) {
		super();
		this.context = context;
		this.courierInfos = courierInfos;
	}

	@Override
	public int getCount() {
		return courierInfos.size();

	}

	@Override
	public CourierReviewInfo getItem(int position) {
		return courierInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(
				R.layout.e3_choiceid_item, null);
		holder.courier_name = (TextView) convertView
				.findViewById(R.id.courier_name);
		holder.iv_checked = (ImageView) convertView
				.findViewById(R.id.iv_checked);
		holder.courier_jobNo = (TextView) convertView
				.findViewById(R.id.courier_job_number);
		holder.rad_baqiang_no = (TextView) convertView
				.findViewById(R.id.rad_baqiang_no);
		holder.courier_latticepoint = (TextView) convertView
				.findViewById(R.id.courier_latticepoint);
		//holder.ll_item = convertView.findViewById(R.id.ll_item);
		
		
		
		
		if (getItem(position).getCourierName() == null
				|| getItem(position).getCourierName().equals("")) {
			holder.courier_name.setText(holder.courier_name.getText());
		} else {
			String cou_name = getItem(position).getCourierName().toString();
			holder.courier_name.setText(holder.courier_name.getText()
					+ cou_name);
		}
		if (getItem(position).getCourierLatticePoint() == null
				|| getItem(position).getCourierLatticePoint().equals("")) {
			holder.courier_latticepoint.setText(holder.courier_latticepoint
					.getText());
		} else {
			String cou_point = getItem(position).getCourierLatticePoint()
					.toString();
			holder.courier_latticepoint.setText(holder.courier_latticepoint
					.getText() + cou_point);
		}
		if (getItem(position).getCourierJobNo() == null
				|| getItem(position).getCourierJobNo().equals("")) {
			holder.courier_jobNo.setText(holder.courier_jobNo.getText());
		} else {
			String cou_jobno = getItem(position).getCourierJobNo().toString();
			String jobno=cou_jobno.substring(cou_jobno.length()-4);
			
			holder.courier_jobNo.setText(holder.courier_jobNo.getText()
					+ jobno);
		}

		String baqiang = holder.rad_baqiang_no.getText().toString();
		holder.rad_baqiang_no.setText(baqiang + (position + 1) + "");
		if (position == checkedIndex) {
			holder.iv_checked.setImageResource(R.drawable.rad_checked);

		} else {
			holder.iv_checked.setImageResource(R.drawable.rad_nocheck);
		}
		params = (LayoutParams) holder.ll_item.getLayoutParams();
		return convertView;
	}

	/**
	 * 把listview当前选中的ID赋值
	 * 
	 * @param position
	 */
	public void setChecked(int position) {
		checkedIndex = position;
		notifyDataSetChanged();
	}

	class ViewHolder {

		ImageView iv_checked;
		TextView courier_jobNo, courier_name, courier_latticepoint,
				rad_baqiang_no;
		View ll_item;
	}
	
	public LayoutParams getItemViewLayout(){
		return params;
	}
	
}
