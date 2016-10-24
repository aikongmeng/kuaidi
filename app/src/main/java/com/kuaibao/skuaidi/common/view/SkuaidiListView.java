package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class SkuaidiListView extends ListView {

	public SkuaidiListView(Context context) {
		super(context);
	}

	public SkuaidiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SkuaidiListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int hMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		
		super.onMeasure(widthMeasureSpec, hMeasureSpec);
	}

}
