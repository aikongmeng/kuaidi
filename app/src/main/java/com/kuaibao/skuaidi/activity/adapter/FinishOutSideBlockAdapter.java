package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.LatestOutSide;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

import java.util.List;

public class FinishOutSideBlockAdapter extends BaseAdapter {

	private Context context;
	private List<LatestOutSide> latestOutSides;
	private LatestOutSide latestOutSide;
	private LayoutInflater inflater;
	private Holder holder;

	private String money;// 外快费
	private String sendAddr;// 发货地址
	private String receiveAddr;// 收货地址
	private String mission;// 需求

	public FinishOutSideBlockAdapter(Context context,
			List<LatestOutSide> latestOutSides) {
		this.context = context;
		this.latestOutSides = latestOutSides;
		inflater = LayoutInflater.from(context);
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
		holder = new Holder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.finish_outside_block_activity_item, null);
			holder.tv_receive = (TextView) convertView.findViewById(R.id.tv_receive);
			holder.tv_send = (TextView) convertView.findViewById(R.id.tv_send);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_receipt_address = (TextView) convertView.findViewById(R.id.tv_receipt_address);
			holder.tv_need = (TextView) convertView.findViewById(R.id.tv_need);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		latestOutSide = new LatestOutSide();
		latestOutSide = latestOutSides.get(position);

		money = latestOutSide.getPay();// 获得外快费
		sendAddr = latestOutSide.getSend();// 获得发货地址
		receiveAddr = latestOutSide.getReceive();// 获得收货地址
		mission = latestOutSide.getMission();// 获得需求

		holder.tv_money.setText(money);
		if (sendAddr.equals("null") || sendAddr.equals("")) {// 如果发货地址为空则为“发货和需求”格式item
			holder.tv_receive.setText("收货");
			holder.tv_send.setText("需求");
			holder.tv_receipt_address.setText(receiveAddr);
			holder.tv_need.setText(mission);
			holder.tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
			holder.tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_whitebg_colorworld"));
			holder.tv_send.setTextColor(context.getResources().getColor(R.color.title_bg));
		} else {// 如果发货地址不为空则为“发货地址和收货地址”格式item
			holder.tv_receive.setText("发货");
			holder.tv_send.setText("收货");
			holder.tv_receive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
			holder.tv_send.setBackgroundResource(SkuaidiSkinManager.getSkinResId("shape_colorbg_whiteworld"));
			holder.tv_send.setTextColor(context.getResources().getColor(R.color.white));
			holder.tv_receipt_address.setText(sendAddr);
			holder.tv_need.setText(receiveAddr);
		}

		return convertView;
	}

	class Holder {
		TextView tv_receive;
		TextView tv_send;
		TextView tv_money;// 显示外快收入的金额
		TextView tv_receipt_address;// 收货或发货地址
		TextView tv_need;// 需求或收货地址
	}

}
