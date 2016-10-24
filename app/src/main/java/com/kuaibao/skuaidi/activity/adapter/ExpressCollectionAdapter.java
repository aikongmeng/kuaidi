package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo;

import java.util.List;

public class ExpressCollectionAdapter extends BaseAdapter {
	private List<NotifyInfo> mList;
	private Context context;
	private Delete delete;

	public ExpressCollectionAdapter(List<NotifyInfo> mList, Context context,Delete delete) {
		this.mList = mList;
		this.context = context;
		this.delete=delete;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public NotifyInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder=new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_collection_express_number, parent,
					false);
			holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_number.setText(mList.get(position).getExpress_number());
		
		if(mList.get(position).getIsUpload()==1){
			holder.iv_delete.setVisibility(View.GONE);
		}else{
			holder.iv_delete.setVisibility(View.VISIBLE);
		}
		holder.iv_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long lastClickTime = 0L;
				if(System.currentTimeMillis()-lastClickTime>1000){
					delete.dedete(position);
					lastClickTime=System.currentTimeMillis();
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_number;
		private ImageView iv_delete;

	}
	
	public interface Delete{
		 void dedete(int iindex);
	}

}
