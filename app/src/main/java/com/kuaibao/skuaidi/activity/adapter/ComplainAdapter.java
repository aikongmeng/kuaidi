package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ComplainInfo;
import com.kuaibao.skuaidi.entry.MessageInfo;
import com.kuaibao.skuaidi.util.Constants;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComplainAdapter extends BaseAdapter {

	Context context;

	List<ComplainInfo> complains;
	Handler handler;
	LayoutInflater inflater;

	String messagetime;
	String messageTimeResponse;
	long dealtime;

	ComplainInfo complain;

	Set<String> messageSet;
	String deleteIds = "";
	MessageInfo message;
	boolean ck_flag = false;
	// boolean select_flag = false;
	private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	int selectCount = 0;

	public ComplainAdapter(Context context, List<ComplainInfo> complains, Handler handler) {
		this.context = context;
		this.complains = complains;
		this.handler = handler;

		inflater = LayoutInflater.from(context);
		complain = new ComplainInfo();
	}

	public void show_checkbox() {
		ck_flag = true;
		messageSet = new HashSet<String>();
		deleteIds = "";
		initSelected();
		this.notifyDataSetChanged();
	}

	private void initSelected() {
		for (int i = 0; i < getCount(); i++) {
			map.put(i, false);
		}
	}

	public void hide_checkbox() {
		ck_flag = false;
		messageSet = null;
		deleteIds = "";
		this.notifyDataSetChanged();
	}

	public String deletemessages() {
		deleteIds = "";
		for (String targetId : messageSet) {
			if (!deleteIds.equals("")) {
				deleteIds += "," + targetId;
			} else {
				deleteIds += targetId;
			}
		}

		// System.out.println(deleteIds);
		return deleteIds;
	}

	public void selectAll() {
		// select_flag = true;
		for (int i = 0; i < getCount(); i++) {
			map.put(i, true);
			messageSet.add(complains.get(i).getComplainId());
		}
		notifyDataSetChanged();
	}

	public void noSelect() {
		// select_flag = false;
		for (int i = 0; i < getCount(); i++) {
			map.put(i, false);
		}
		messageSet.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return complains.size();
	}

	@Override
	public Object getItem(int position) {
		return complains.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// if (convertView == null) {
		holder = new ViewHolder();
		convertView = inflater
				.inflate(R.layout.listitem_exception_center, null);
		convertView.findViewById(R.id.recording_center_view).setVisibility(View.GONE);
		convertView.findViewById(R.id.bottom_margin).setVisibility(View.GONE);
		holder.imv_order_icon = (ImageView) convertView
				.findViewById(R.id.img_order_icon);
		holder.tv_order_no = (TextView) convertView
				.findViewById(R.id.tv_order_no);
		holder.tv_txt_content = (TextView) convertView
				.findViewById(R.id.tv_txt_content);
		holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
		holder.imv_icon_jiant = (ImageView) convertView
				.findViewById(R.id.imv_icon_jiant);
		holder.ck_delete = (CheckBox) convertView
				.findViewById(R.id.ckbox_delete);
		//holder.tv_isnew = (TextView) convertView.findViewById(R.id.tv_isnew);
		holder.tv_time_title = (TextView) convertView.findViewById(R.id.tv_time_title);
		holder.view_divider = convertView.findViewById(R.id.view_divider);
		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }

		complain = complains.get(position);

		if (complain.getUnRead() == 1) {
			holder.tv_isnew.setVisibility(View.VISIBLE);
		} else {
			holder.tv_isnew.setVisibility(View.GONE);
		}

		// 批量删除时视图改变
		if (ck_flag) {
			holder.ck_delete.setVisibility(View.VISIBLE);
			holder.imv_icon_jiant.setVisibility(View.GONE);
			// if (select_flag) {
			// holder.ck_delete.setChecked(true);
			// } else {
			// holder.ck_delete.setChecked(false);
			// }
			holder.ck_delete.setChecked(map.get(position));
		} else {
			holder.ck_delete.setVisibility(View.GONE);
			holder.imv_icon_jiant.setVisibility(View.VISIBLE);
		}

		// 批量删除的同时删除对应的数据
		holder.ck_delete
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							//System.out.println("check:"+ complains.get(position).getComplainId());
							messageSet.add(complains.get(position)
									.getComplainId());
							selectCount = messageSet.size();
							//System.out.println(messageSet.toString());
							map.put(position, true);
						} else {
							//System.out.println("not check:"+ complains.get(position).getComplainId());
							messageSet.remove(complains.get(position)
									.getComplainId());
							selectCount = messageSet.size();
							//System.out.println(messageSet.toString());
							map.put(position, false);
						}

						Message msg = new Message();
						msg.what = Constants.DELETE_TO_FINISH;
						msg.arg1 = selectCount;
						handler.sendMessage(msg);

					}
				});

		// if (position % 5 == 0) {
		// holder.imv_order_icon.setImageResource(R.drawable.icon_yonghu1);
		// } else if (position % 5 == 1) {
		// holder.imv_order_icon.setImageResource(R.drawable.icon_yonghu2);
		// } else if (position % 5 == 2) {
		// holder.imv_order_icon.setImageResource(R.drawable.icon_yonghu3);
		// } else if (position % 5 == 3) {
		// holder.imv_order_icon.setImageResource(R.drawable.icon_yonghu4);
		// } else if (position % 5 == 4) {
		// holder.imv_order_icon.setImageResource(R.drawable.icon_yonghu5);
		// }
		holder.imv_order_icon.setImageResource(R.drawable.icon_yonghu);
		holder.tv_order_no.setText("运单号：" + complain.getDeliverNo());
		holder.tv_txt_content.setText(complain.getContent());

		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System
				.currentTimeMillis());
//		long dealtimemsg = Long.parseLong(complain.getDealTime());
//		messagetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(complain.getDealTime()*1000);
		
//		if(position != 0){
//			//System.out.println("转换前："+complains.get(position-1).getDealTime());
//			dealtime = Long.parseLong(complains.get(position-1).getDealTime());
//			messageTimeResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//			.format(dealtime*1000);
//		}
		
		// //System.out.println(now+"_now");
		// //System.out.println(complain.getDealTime()+"_time");
		if (now.substring(0, 10)
				.equals(complain.getDealTime().substring(0, 10))) {
			holder.tv_time.setText(complain.getDealTime().substring(10, 16));
			holder.tv_time_title.setText("今天");
		} else if (now.substring(0, 8).equals(
				complain.getDealTime().substring(0, 8))
				&& Integer.parseInt(now.substring(8, 10))
						- Integer.parseInt(complain.getDealTime().substring(8,
								10)) == 1) {
			holder.tv_time.setText("昨天 "
					+ complain.getDealTime().substring(10, 16));
			holder.tv_time_title.setText("昨天");
		} else {
			holder.tv_time.setText(complain.getDealTime().substring(10, 16));
			holder.tv_time_title.setText(complain.getDealTime().subSequence(0, 10));
		}

		// 时间一起的留言放在一起~
				if (position != 0
						&& complain.getDealTime().substring(0, 10).equals(
								complains.get(position-1).getDealTime().substring(0, 10))) {

					holder.tv_time_title.setVisibility(View.GONE);
					holder.view_divider.setVisibility(View.GONE);
				}
		
		
		return convertView;
	}

	class ViewHolder {
		ImageView imv_order_icon;// 图标
		TextView tv_order_no;
		TextView tv_txt_content;
		TextView tv_time;
		CheckBox ck_delete;
		ImageView imv_icon_jiant;
		TextView tv_isnew;
		TextView tv_time_title;
		View view_divider;
	}

}
