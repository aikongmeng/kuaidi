package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.kuaibao.skuaidi.R;

/**
 * 使得gridview是一个正方型
 * 
 * @author 顾冬冬
 * 
 */
public class BusinessGridView extends GridView {
	boolean a = false;

	public BusinessGridView(Context context) {
		super(context);
	}

	public BusinessGridView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public BusinessGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public final void a() {
		this.a = true;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		int childHeightSize = getMeasuredHeight();
		// 高度和宽度一样
		// heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void showRedCircle(int[] visble, int[] hide) {
		for (int i : visble) {
			getChildAt(i).findViewById(R.id.iv_red_icon).setVisibility(View.VISIBLE);
		}
		for (int i : hide) {
			getChildAt(i).findViewById(R.id.iv_red_icon).setVisibility(View.GONE);
		}
		invalidate();
	}
}
