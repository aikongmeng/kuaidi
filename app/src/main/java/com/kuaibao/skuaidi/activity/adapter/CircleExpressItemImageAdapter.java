package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;

import java.util.List;

public class CircleExpressItemImageAdapter extends BaseAdapter {

	private Context mContext;// 上下文
	private List<String> imageurls;// 图片url集合

	public CircleExpressItemImageAdapter(Context context,
			List<String> imageurls) {
		this.mContext = context;
		this.imageurls = imageurls;
	}

	@Override
	public int getCount() {
		return imageurls == null ? 0 : imageurls.size();
	}

	@Override
	public Object getItem(int arg0) {
		return imageurls.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(mContext,R.layout.outside_detail_griditem, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
		GlideUtil.GlideUrlToImg(mContext,imageurls.get(position),imageView);
		return view;
	}

}
