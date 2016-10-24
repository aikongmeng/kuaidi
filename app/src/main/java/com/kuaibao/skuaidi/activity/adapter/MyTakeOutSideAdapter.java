package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.FinishOutSideBlockActivity;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class MyTakeOutSideAdapter extends BaseAdapter {

	private Context context;
	private Intent intent;
	private List<LatestOutSide> latestOutSides;
	private LatestOutSide latestOutSide;
	private LayoutInflater inflater;

	private String money;// 外快费
	private String distance;// 距离我的距离
	private String send_receive_distance;// 发货和收货之间的距离
	private String time;// 时间
	private String sendAddr;// 发货地址
	private String receiveAddr;// 收货地址
	private String mission;// 需求

	final int TYPE_VIEW_FIRST = 0;
	final int TYPE_VIEW_SECOND = 1;

	public MyTakeOutSideAdapter(Context context,
			List<LatestOutSide> latestOutSides) {
		this.context = context;
		this.latestOutSides = latestOutSides;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return latestOutSides.size() + 1;
	}

	public void notifyData(List<LatestOutSide> latestOutSides) {
		this.latestOutSides = latestOutSides;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return latestOutSide;
		} else {
			return latestOutSides.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_VIEW_FIRST;
		} else {
			return TYPE_VIEW_SECOND;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	FirstHolder firstHolder;
	SecondHolder secondHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		firstHolder = null;
		secondHolder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case TYPE_VIEW_FIRST:
				firstHolder = new FirstHolder();
				convertView = inflater.inflate(R.layout.finish_outside_block,
						parent, false);
				firstHolder.rl_finish_outside_block = (RelativeLayout) convertView
						.findViewById(R.id.rl_finish_outside_block);
				convertView.setTag(firstHolder);
				break;
			case TYPE_VIEW_SECOND:
				secondHolder = new SecondHolder();
				convertView = inflater.inflate(
						R.layout.outside_block_activity_item, parent, false);
				secondHolder.tv_money = (TextView) convertView
						.findViewById(R.id.tv_money);
				secondHolder.tv_distance = (TextView) convertView
						.findViewById(R.id.tv_distance);
				secondHolder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				secondHolder.tv_receive = (TextView) convertView
						.findViewById(R.id.tv_receive);
				secondHolder.tv_receipt_address = (TextView) convertView
						.findViewById(R.id.tv_receipt_address);
				secondHolder.rl_send_receive_distance = (RelativeLayout) convertView
						.findViewById(R.id.rl_send_receive_distance);
				secondHolder.rl_distance = (RelativeLayout) convertView
						.findViewById(R.id.rl_distance);
				secondHolder.tv_send_receive_distance = (TextView) convertView
						.findViewById(R.id.tv_send_receive_distance);
				secondHolder.tv_send = (TextView) convertView
						.findViewById(R.id.tv_send);
				secondHolder.tv_need = (TextView) convertView
						.findViewById(R.id.tv_need);
				convertView.setTag(secondHolder);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case TYPE_VIEW_FIRST:
				firstHolder = (FirstHolder) convertView.getTag();
				break;
			case TYPE_VIEW_SECOND:
				secondHolder = (SecondHolder) convertView.getTag();
				break;
			default:
				break;
			}
		}

		switch (type) {
		case TYPE_VIEW_FIRST:
			firstHolder.rl_finish_outside_block
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 进入已完成外单界面
							intent = new Intent(context,
									FinishOutSideBlockActivity.class);
							context.startActivity(intent);
						}
					});
			break;
		case TYPE_VIEW_SECOND:
			latestOutSide = (LatestOutSide) getItem(position);
			if (latestOutSide != null) {
				money = latestOutSide.getPay();// 金额
				distance = latestOutSide.getDistance();// 收货或发货距离我的距离
				time = latestOutSide.getLimit_time();// 单子剩下的有效时间
				sendAddr = latestOutSide.getSend();// 发货地址
				send_receive_distance = latestOutSide
						.getSend_receive_distance();// 发货与收货之间的距离
				receiveAddr = latestOutSide.getReceive();// 收货地址
				mission = latestOutSide.getMission();// 需求
				secondHolder.tv_money.setText(money);// 设置收入金额
				if (distance != null && !distance.equals("")) {// 判断距离我的距离
					secondHolder.tv_distance.setText("距您" +Utility.formatDistance(distance));
				} else {
					secondHolder.rl_distance.setVisibility(View.GONE);// 如果距离为空，则隐藏距离显示
				}
				secondHolder.tv_time.setText(Utility.CalculationTime(time));// 设置剩余时间
				if (sendAddr.equals("null") || sendAddr.equals("")) {// 如果发货地址为空则为“发货和需求”格式item
					secondHolder.tv_receive.setText("收货");
					secondHolder.tv_send.setText("需求");
					// 设置不同公司区默认时候的背景
					secondHolder.tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
					secondHolder.tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_whitebg_colorworld"));
					secondHolder.tv_send.setText(SkuaidiSkinManager.getTextColor("main_color"));
					
					secondHolder.tv_receipt_address.setText(receiveAddr);
					secondHolder.tv_need.setText(mission);
					secondHolder.tv_send
							.setBackgroundResource(R.drawable.shape_whitebg_colorworld);
					secondHolder.tv_send.setTextColor(context.getResources()
							.getColor(R.color.title_bg));
					secondHolder.rl_send_receive_distance
							.setVisibility(View.GONE);
				} else {// 如果发货地址不为空则为“发货地址和收货地址”格式item
					secondHolder.tv_receive.setText("发货");
					secondHolder.tv_send.setText("收货");
					// 设置不同公司区默认时候的背景
					secondHolder.tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
					secondHolder.tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
					secondHolder.tv_send.setTextColor(context.getResources().getColor(R.color.white));
					
					secondHolder.tv_receipt_address.setText(sendAddr);
					secondHolder.tv_need.setText(receiveAddr);
					if (send_receive_distance != null
							&& !send_receive_distance.equals("")) {// 判断发货与收货之间的距离是否为空
						secondHolder.rl_send_receive_distance.setVisibility(View.VISIBLE);
						secondHolder.tv_send_receive_distance.setText("相距"+ Utility.formatDistance(send_receive_distance));
					} else if (distance != null && !distance.equals("")) {
						secondHolder.rl_send_receive_distance.setVisibility(View.VISIBLE);
						secondHolder.tv_send_receive_distance.setText("相距"+ Utility.formatDistance(distance));
					} else {
						secondHolder.rl_send_receive_distance
								.setVisibility(View.VISIBLE);
						secondHolder.tv_send_receive_distance
								.setText("距离无法测算...");
					}
				}
			}
			break;

		default:
			break;

		}

		return convertView;
	}

	class FirstHolder {
		RelativeLayout rl_finish_outside_block;
	}

	class SecondHolder {
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
