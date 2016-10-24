package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class LatestTakeOutAdapter extends BaseAdapter {

	private Context context;// 上下文
	private viewHolder holder;
	private LayoutInflater inflater;
	private List<LatestOutSide> latestOutSides;// 外单集合
	private LatestOutSide latestOutSide;

	private String money;// 外快费
	private String distance;// 距离我的距离
	private String send_receive_distance;// 发货和收货之间的距离
	private String time;// 时间
	private String sendAddr;// 发货地址
	private String receiveAddr;// 收货地址
	private String mission;// 需求

	public LatestTakeOutAdapter(Context context,
			List<LatestOutSide> latestOutSides) {
		this.latestOutSides = latestOutSides;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void notifyData(List<LatestOutSide> latestOutSides) {
		this.latestOutSides = latestOutSides;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return latestOutSides.size();
	}

	@Override
	public Object getItem(int position) {
		return latestOutSides.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder = new viewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.outside_block_activity_item, null);
			holder.tv_money = (TextView) convertView
					.findViewById(R.id.tv_money);
			holder.tv_distance = (TextView) convertView
					.findViewById(R.id.tv_distance);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_receive = (TextView) convertView
					.findViewById(R.id.tv_receive);
			holder.tv_receipt_address = (TextView) convertView
					.findViewById(R.id.tv_receipt_address);
			holder.rl_send_receive_distance = (RelativeLayout) convertView
					.findViewById(R.id.rl_send_receive_distance);
			holder.rl_distance = (RelativeLayout) convertView
					.findViewById(R.id.rl_distance);
			holder.tv_send_receive_distance = (TextView) convertView
					.findViewById(R.id.tv_send_receive_distance);
			holder.tv_send = (TextView) convertView.findViewById(R.id.tv_send);
			holder.tv_need = (TextView) convertView.findViewById(R.id.tv_need);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		
		
		latestOutSide = latestOutSides.get(position);// 获得单个条目
		money = latestOutSide.getPay();// 金额
		distance = latestOutSide.getDistance();// 收货或发货距离我的距离
		time = latestOutSide.getLimit_time();// 单子剩下的有效时间
		sendAddr = latestOutSide.getSend();// 发货地址
		send_receive_distance = latestOutSide.getSend_receive_distance();// 发货与收货之间的距离
		receiveAddr = latestOutSide.getReceive();// 收货地址
		mission = latestOutSide.getMission();// 需求
		holder.tv_money.setText(money);// 设置收入金额
		if (distance != null && !distance.equals("")) {// 判断距离我的距离
			String distance_cur = Utility.formatDistance(distance);
			holder.tv_distance.setText("距您" + distance_cur);
		} else {
			holder.rl_distance.setVisibility(View.GONE);// 如果距离为空，则隐藏距离显示
		}
		holder.tv_time.setText(Utility.CalculationSurplusTime(time));// 设置剩余时间
		if (sendAddr.equals("null") || sendAddr.equals("")) {// 如果发货地址为空则为“发货和需求”格式item
			holder.tv_receive.setText("收货");
			holder.tv_send.setText("需求");
			// 设置不同公司区默认时候的背景
			holder.tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
			holder.tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_whitebg_colorworld"));
			holder.tv_send.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			
			holder.tv_need.setText(mission);
			holder.tv_receipt_address.setText(receiveAddr);
			holder.tv_send
					.setBackgroundResource(R.drawable.shape_whitebg_colorworld);
			holder.tv_send.setTextColor(context.getResources().getColor(
					R.color.title_bg));
			holder.rl_send_receive_distance.setVisibility(View.GONE);
		} else {// 如果发货地址不为空则为“发货地址和收货地址”格式item
			holder.tv_receive.setText("发货");
			holder.tv_send.setText("收货");
			// 设置不同公司区默认时候的背景
			holder.tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
			holder.tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
			holder.tv_send.setTextColor(context.getResources().getColor(R.color.white));
			
			holder.tv_receipt_address.setText(sendAddr);
			holder.tv_need.setText(receiveAddr);
			if (send_receive_distance != null
					&& !send_receive_distance.equals("")) {// 判断发货与收货之间的距离是否为空
				holder.rl_send_receive_distance.setVisibility(View.VISIBLE);
				holder.tv_send_receive_distance.setText("相距"
						+ Utility.formatDistance(send_receive_distance));
			} else if (distance != null && !distance.equals("")) {
				holder.rl_send_receive_distance.setVisibility(View.VISIBLE);
				holder.tv_send_receive_distance.setText("相距"
						+ Utility.formatDistance(distance));
			} else {
				holder.rl_send_receive_distance.setVisibility(View.VISIBLE);
				holder.tv_send_receive_distance.setText("距离无法测算...");
			}
		}
		return convertView;
	}

	class viewHolder {
		private TextView tv_money;// 外快费
		private TextView tv_distance;// 距离我的距离
		private TextView tv_time;// 时间
		private TextView tv_receive;// 收货或发货tag
		private TextView tv_receipt_address;// 收货或发货地址
		private RelativeLayout rl_send_receive_distance;// 发货和收货之间的距离
		private RelativeLayout rl_distance;// 距离我的距离
		private TextView tv_send_receive_distance;// 发货和收货之间的距离
		private TextView tv_send;// 需求或收货TAG
		private TextView tv_need;// 需求或收货地址
	}

}
