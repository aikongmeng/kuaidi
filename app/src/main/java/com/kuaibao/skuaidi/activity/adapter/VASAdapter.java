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
import com.kuaibao.skuaidi.entry.VASInfo;

import java.util.List;

/**
 * @ClassName: VASAdapter
 * @Description: 增值业务列表适配器
 * @author 顾冬冬
 * @date 2015-11-26 下午5:08:55
 */
public class VASAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private onClickListener onClick = null;

	private List<VASInfo> vasInfos = null;

	/**
	 * @param context上下文
	 * @param vasInfos列表信息
	 * @param onClick事件
	 * @param clSwitch财来网开关
	 */
	public VASAdapter(Context context, List<VASInfo> vasInfos, onClickListener onClick) {
		this.vasInfos = vasInfos;
		inflater = LayoutInflater.from(context);
		this.onClick = onClick;
	}


	/**
	 * @Title: setNotifyVASInfoList
	 * @Description:刷新列表界面
	 * @author: 顾冬冬
	 * @return void
	 */
	public void setNotifyVASInfoList(List<VASInfo> vasInfos) {
		this.vasInfos = vasInfos;
		notifyDataSetChanged();
	}
	/**
	 * @Title: getVasInfos 
	 * @Description: 获取列表中的数据
	 * @return
	 * @author: 顾冬冬
	 * @return List<VASInfo>
	 */
	public List<VASInfo> getVasInfos(){
		return vasInfos;
	}
	
	/**
	 * @Title: delItem 
	 * @Description:删除条目
	 * @author: 顾冬冬
	 * @return void
	 */
	public void delItem(int position){
		if(position != -1 && position <vasInfos.size()){
			vasInfos.remove(position);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
			return vasInfos.size();
	}

	@Override
	public VASInfo getItem(int position) {

		return vasInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.vas_activity_item, null);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
		TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
		TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
		ImageView ivEdit = (ImageView) convertView.findViewById(R.id.ivEdit);
		ImageView ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

		tvTitle.setText(getItem(position).getVasName());
		tvPrice.setText("￥ " + getItem(position).getVasPrice());
		tvDescription.setText(getItem(position).getVasDescription());

		ivEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClick.itemEdit(position, getItem(position));
			}
		});
		ivDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClick.itemDelete(position);
			}
		});
		
		return convertView;
	}

	
	public interface onClickListener {
		void itemEdit(int position, VASInfo vasInfo);

		void itemDelete(int position);
	}

}
