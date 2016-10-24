package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.HistoryOrder;
import com.kuaibao.skuaidi.entry.Order;
import com.kuaibao.skuaidi.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressLint({ "UseSparseArrays", "SimpleDateFormat" })
public class ListOrderAdapter extends BaseAdapter {

	Context context;
	Handler handler;
	List<Order> orders;
	LayoutInflater inflater;

	Set<String> messageSet;
	String deleteIds = "";
	Order order;
	HistoryOrder hisOrder;
	boolean ck_flag = false;
	private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	// boolean select_flag = false;
	int selectCount = 0;

	public ListOrderAdapter(Context context, Handler handler, List<Order> customers) {
		super();
		this.context = context;
		this.orders = customers;
		this.handler = handler;

		inflater = LayoutInflater.from(context);
		order = new Order();
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
		return deleteIds;
	}

	public void selectAll() {
		// select_flag = true;
		for (int i = 0; i < getCount(); i++) {
			map.put(i, true);
			messageSet.add(orders.get(i).getId());
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

		return orders.size();
	}

	@Override
	public Object getItem(int arg0) {
		return orders.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		// if (convertView==null) {
		holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.listitem_orders, null);
		holder.ll_listitem_order = (LinearLayout) convertView.findViewById(R.id.ll_listitem_order);
		holder.imv_service_icon = (ImageView) convertView.findViewById(R.id.imv_service_icon);
		holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
		holder.tv_sender_phone = (TextView) convertView.findViewById(R.id.tv_order_phone);
		holder.tv_sender_name = (TextView) convertView.findViewById(R.id.tv_order_name);
		holder.tv_data = (TextView) convertView.findViewById(R.id.tv_data);
		holder.tv_sender_address = (TextView) convertView.findViewById(R.id.tv_order_address);
		holder.imv_phone_icon = (ImageView) convertView.findViewById(R.id.imv_phone_icon);

		// holder.tv_time_detail = (TextView) convertView
		// .findViewById(R.id.tv_time_detail);

		holder.view_divider = convertView.findViewById(R.id.view_divider);
		holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
		holder.ckbox_delete = (CheckBox) convertView.findViewById(R.id.ckbox_delete);
		holder.tv_new_im = (ImageView) convertView.findViewById(R.id.tv_new_im);
		// holder.view_line = convertView.findViewById(R.id.view_line);
		// convertView.setTag(holder);
		// }else {
		// holder=(ViewHolder) convertView.getTag();
		// }

		order = orders.get(position);

 /*		// 批量删除时视图改变
		if (ck_flag) {
			holder.ckbox_delete.setVisibility(View.VISIBLE);
			holder.imv_phone_icon.setVisibility(View.GONE);
			// holder.view_line.setVisibility(View.GONE);
			// if (select_flag) {
			// holder.ckbox_delete.setChecked(true);
			// } else {
			// holder.ckbox_delete.setChecked(false);
			// }
			holder.ckbox_delete.setChecked(map.get(position));
		} else {
			holder.ckbox_delete.setVisibility(View.GONE);
			holder.imv_phone_icon.setVisibility(View.VISIBLE);
			// holder.view_line.setVisibility(View.VISIBLE);
		}

		// 批量删除的同时删除对应的数据
		holder.ckbox_delete.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					messageSet.add(orders.get(position).getId());
					selectCount = messageSet.size();
					map.put(position, true);
				} else {
					messageSet.remove(orders.get(position).getId());
					selectCount = messageSet.size();
					map.put(position, false);
				}

				Message msg = new Message();
				msg.what = Constants.DELETE_TO_FINISH;
				msg.arg1 = selectCount;
				handler.sendMessage(msg);
			}
		});
*/
		String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

		if (now.substring(0, 10).equals(order.getTime().substring(0, 10))) {
			holder.tv_time.setVisibility(View.GONE);
		} else if (now.substring(0, 8).equals(order.getTime().substring(0, 8))
				&& Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(order.getTime().substring(8, 10)) == 1) {
			holder.tv_time.setText("昨天");
		} else {
			holder.tv_time.setText(order.getTime().substring(0, 10));

		}

		if (position != 0
				&& order.getTime().substring(0, 10).equals(orders.get(position - 1).getTime().substring(0, 10))) {
			holder.tv_time.setVisibility(View.GONE);
			holder.view_divider.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(order.getInform_sender_when_sign()) && order.getInform_sender_when_sign().equals("1")) {
			holder.imv_service_icon.setImageResource(R.drawable.icon_order_signed);

		} else{
			holder.imv_service_icon.setImageResource(R.drawable.icon_user_order);
		}
		holder.tv_sender_phone.setText(StringUtil.isEmpty(order.getSenderPhone()));
		holder.tv_sender_address.setText(TextUtils.isEmpty(order.getSenderDetailAddress())?"暂无发件人地址":order.getSenderDetailAddress());
		holder.tv_sender_name.setText(StringUtil.isEmpty(order.getSenderName()));
		holder.imv_phone_icon.setImageResource(R.drawable.icon_phone);
		holder.tv_data.setText(order.getTime().substring(10, 16));

		if ("1".equals(order.getOrder_type())) {
			 holder.tv_status.setTextColor(context.getResources().getColor(R.color.text_order));
			 holder.tv_status.setText("未完成");
			 holder.tv_status.setVisibility(View.VISIBLE);

		} else if ("2".equals(order.getOrder_type())) {
			/*
			 * holder.tv_status.setBackgroundResource(SkuaidiSkinManager
			 * .getSkinResId("selector_radius20_lines_btn"));
			 */
			// if (order.getOrder_state_cname().equals("sending")) {
			// holder.tv_status.setText("运送中");
			// } else if (order.getOrder_state_cname().equals("delivering")) {
			// holder.tv_status.setText("派送中");
			// } else if (order.getOrder_state_cname().equals("signed")) {
			// holder.tv_status.setText("已签收");
			// } else if (order.getOrder_state_cname().equals("collected")) {
			// holder.tv_status.setText("已揽件");
			// } else {
			holder.tv_status.setText("已完成");
			holder.tv_status.setTextColor(context.getResources().getColor(R.color.click_green_3));
			// }
		}else if("4".equals(order.getOrder_type())){
			holder.tv_status.setText("已打回");
			holder.tv_status.setTextColor(context.getResources().getColor(R.color.status_orange));
		}
		if("1".equals(order.getIsPrint())){
			holder.tv_status.setText("已打印");
			holder.tv_status.setTextColor(context.getResources().getColor(R.color.click_green_2));
		}

		if (order.getIsread() == 0) {
			holder.tv_new_im.setVisibility(View.VISIBLE);
		} else {
			holder.tv_new_im.setVisibility(View.GONE);
		}
/*		点击电话按钮拨打电话
		holder.imv_phone_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UMShareManager.onEvent(context, "order_center_phoneCall", "order_center", "订单：打电话");
				Message msg = new Message();
				msg.what = Constants.PHONE_CALL;
				msg.obj = orders.get(position).getSenderPhone();

				handler.sendMessage(msg);

			}
		});*/
//		if (order.getType().equals("onecity")) {
//			if (SkuaidiSpf.getOneCity(context).equals("")) {
//				callBack.OneCity();
//			}
//		}

		return convertView;
	}

	class ViewHolder {
		LinearLayout ll_listitem_order;
		ImageView imv_service_icon, imv_phone_icon;
		TextView tv_time, tv_sender_phone, tv_sender_address, tv_status, tv_data, tv_sender_name;
		ImageView tv_new_im;
		View view_divider;
		CheckBox ckbox_delete;
		// View view_line;

	}

	public void notifyDataSetChanged(List<Order> orders) {
		this.orders = orders;
		super.notifyDataSetChanged();
	}
}
