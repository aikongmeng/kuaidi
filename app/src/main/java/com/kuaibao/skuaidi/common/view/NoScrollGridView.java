package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


/**
 * 自定义9宫格
 * @author gudd
 *
 */

public class NoScrollGridView extends GridView {

	public NoScrollGridView(Context context) {
		super(context);
	}
	
	public NoScrollGridView(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
	}

	public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
