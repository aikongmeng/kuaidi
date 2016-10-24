package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.EthreeOrderInfo;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

public class UploadEthreeAdapter extends ArrayAdapter<EthreeOrderInfo> {
	private List<EthreeOrderInfo> list;
	private FinalDb finalDb;
	private List<String> selected;

	public UploadEthreeAdapter(Context context, List<EthreeOrderInfo> objects,
			FinalDb finalDb) {
		super(context, 0, objects);
		list = objects;
		this.finalDb = finalDb;
		selected = new ArrayList<String>();
	}

	public List<String> getList(){
		return selected;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.listitem_uploadethree, null);
			holder.iv = (ImageView) convertView
					.findViewById(R.id.iv_listitem_uploadethree);
			holder.tv_order = (TextView) convertView
					.findViewById(R.id.tv_listitem_uploadethree_order);
			holder.tv_type = (TextView) convertView
					.findViewById(R.id.tv_listitem_uploadethree_type);
			holder.ll_operate = (LinearLayout) convertView
					.findViewById(R.id.ll_listitem_uploadethree_operate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final EthreeOrderInfo ethreeOrderInfo = getItem(position);
		holder.tv_order.setText(ethreeOrderInfo.getOrdernumber());
		if (selected.size() < position + 1) {
			selected.add("0-" + ethreeOrderInfo.getId());
		}
		final String str = selected.get(position);
		if (str.toString().substring(0, 1).equals("0")) {
			holder.iv.setImageResource(R.drawable.icon_ethreescan_fail);
		} else {
			holder.iv.setImageResource(R.drawable.icon_ethreescan_success);
		}
		if (ethreeOrderInfo.getAction().equals("GOT")) {
			holder.tv_type.setText("揽件");
		} else if (ethreeOrderInfo.getAction().equals("SENT_SCAN")) {
			holder.tv_type.setText("派件");
		} else {
			holder.tv_type.setText(ethreeOrderInfo.getTypededesc());
		}
		final ViewHolder mHolder = holder;
		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (str.toString().substring(0, 1)
						.equals("0")) {
					selected.set(position, str.toString()
							.replace("0-", "1-"));
					mHolder.iv
							.setImageResource(R.drawable.icon_ethreescan_success);
				} else {
					selected.set(position, str.toString()
							.replace("1-", "0-"));
					mHolder.iv
							.setImageResource(R.drawable.icon_ethreescan_fail);
				}
			}
		});

		holder.ll_operate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finalDb.deleteById(EthreeOrderInfo.class, ethreeOrderInfo.getId());
				list.remove(position);
				selected.remove(position);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv_order, tv_type;
		LinearLayout ll_operate;
	}
}
