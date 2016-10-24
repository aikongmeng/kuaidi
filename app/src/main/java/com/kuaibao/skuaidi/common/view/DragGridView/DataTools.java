package com.kuaibao.skuaidi.common.view.DragGridView;

import android.content.Context;

public class DataTools {
	/**
	 * dip转为 px
	 */
	public static float dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return  (dipValue * scale + 0.5f);
	}

	/**
	 *  px 转为 dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
