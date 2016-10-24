package com.kuaibao.skuaidi.activity.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GuidPageAdapter extends PagerAdapter {

	private List<View> imgView;
	public GuidPageAdapter(List<View> imgView) {
		this.imgView = imgView;
	}
	@Override
	public int getCount() {
		return imgView.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(imgView.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(imgView.get(position));
		return imgView.get(position);
	}
}
