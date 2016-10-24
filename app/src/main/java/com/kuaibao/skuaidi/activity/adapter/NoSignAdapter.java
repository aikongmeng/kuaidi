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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.NoSignActivity;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 未签收适配器
 * 
 * @author hh
 * 
 */
@SuppressLint("SimpleDateFormat")
public class NoSignAdapter extends BaseAdapter {
	public List<NotifyInfo> orders;
	private Context context;
	public int check_index;
	private NotifyInfo order;
	private CheckCallBack checkCallBack;
	private int checkCount = 0;
	private String scantype;
	private Map<Integer, NotifyInfo> checkedList = new HashMap<Integer, NotifyInfo>();

	public NoSignAdapter(Context context, List<NotifyInfo> orders, CheckCallBack checkCallBack, String scantype) {
		this.context = context;
		this.orders = orders;
		this.checkCallBack = checkCallBack;
		this.scantype = scantype;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

	public int getCheckCount() {
		return checkCount;
	}

	@Override
	public int getCount() {
		return orders.size();
	}

	@Override
	public Object getItem(int position) {

		return orders.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	ViewHolder holder;

	@Override
	public View getView(final int position, View convertView, final ViewGroup arg2) {
		order = (NotifyInfo) getItem(position);
		check_index = position;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_ethreeinfoscan, null);
			holder = new ViewHolder();

			// holder.lanPiece.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			// holder.lanPiece.setBackgroundResource(SkuaidiSkinManager.getSkinResId("big_button_selector"));

			holder.tv_express_status = (TextView) convertView.findViewById(R.id.tv_express_status);
			holder.tv_listitem_ethreeinfoscan_order = (TextView) convertView
					.findViewById(R.id.tv_listitem_ethreeinfoscan_order);
			holder.iv_listitem_ethreeinfoscan = (ImageView) convertView.findViewById(R.id.iv_checkBox);
			// 问题类型
			holder.ll_listitem_ethreeinfoscan_question_type = (LinearLayout) convertView
					.findViewById(R.id.ll_listitem_ethreeinfoscan_question_type);
			holder.ll_listitem_ethreeinfoscan_operate = (LinearLayout) convertView
					.findViewById(R.id.ll_listitem_ethreeinfoscan_operate);
			holder.e3_question_type = (TextView) convertView.findViewById(R.id.e3_question_type);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.findViewById(R.id.e3_type_check).setVisibility(View.GONE);
		if (scantype.equals("question")) {
			holder.ll_listitem_ethreeinfoscan_question_type.setVisibility(View.VISIBLE);
			holder.ll_listitem_ethreeinfoscan_operate.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(orders.get(position).getQuestion_detail())
					&& !"null".equals(orders.get(position).getQuestion_detail())) {

				holder.e3_question_type.setText(orders.get(position).getQuestion_detail());
			}
		} else {
			holder.ll_listitem_ethreeinfoscan_question_type.setVisibility(View.GONE);
			holder.ll_listitem_ethreeinfoscan_operate.setVisibility(View.VISIBLE);
		}
		if (null != orders.get(position).getStatus() && !orders.get(position).getStatus().equals("")) {
			holder.tv_express_status.setText(orders.get(position).getStatus());
		} else {
			holder.tv_express_status.setText("");
		}
		if (null != orders.get(position).getExpress_number() && !orders.get(position).getExpress_number().equals("")) {
			holder.tv_listitem_ethreeinfoscan_order.setText(orders.get(position).getExpress_number());
		} else {
			holder.tv_listitem_ethreeinfoscan_order.setText("");
		}
		if (order.isChecked()) {
			holder.iv_listitem_ethreeinfoscan.setImageResource(R.drawable.batch_add_checked);
			checkedList.put(position, order);
		} else {
			holder.iv_listitem_ethreeinfoscan.setImageResource(R.drawable.rad_nocheck);
			checkedList.remove(position);
		}
		// 复选框
		holder.iv_listitem_ethreeinfoscan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!orders.get(position).isChecked()) {
					holder.iv_listitem_ethreeinfoscan.setImageResource(SkuaidiSkinManager
							.getSkinResId("icon_ethreescan_success"));
					orders.get(position).setChecked(true);
					checkedList.put(position, orders.get(position));
					checkCount = checkCount + 1;
					if (checkCount == getCount()) {
						checkCallBack.checkStatus(true);
					}
					((NoSignActivity) context).getSignView().setText("签收扫描(" + checkCount + "/" + orders.size() + ")");
					((NoSignActivity) context).getBadView().setText("问题件(" + checkCount + "/" + orders.size() + ")");
				} else {
					holder.iv_listitem_ethreeinfoscan.setImageResource(R.drawable.icon_ethreescan_fail);
					orders.get(position).setChecked(false);
					checkedList.remove(position);
					checkCallBack.checkStatus(false);
					checkCount = checkCount - 1;
					((NoSignActivity) context).getSignView().setText("签收扫描(" + checkCount + "/" + orders.size() + ")");
					((NoSignActivity) context).getBadView().setText("问题件(" + checkCount + "/" + orders.size() + ")");

				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	public int setPosition() {
		return check_index;
	}

	public interface CheckCallBack {
		void checkStatus(boolean isAllCheck);
	}

	public static class ViewHolder {
		public TextView tv_listitem_ethreeinfoscan_order, tv_express_status, e3_question_type;
		public ImageView iv_listitem_ethreeinfoscan;
		public LinearLayout ll_listitem_ethreeinfoscan_question_type,ll_listitem_ethreeinfoscan_operate;

	}

	public int getCheckNum() {
		return checkCount;
	}

	public List<NotifyInfo> getList() {
		return orders;
	}

}
