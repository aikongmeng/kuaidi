package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ImageGridView extends GridView {

	public ImageGridView(Context context) {
		super(context);
	}

	public ImageGridView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
	}

	public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
