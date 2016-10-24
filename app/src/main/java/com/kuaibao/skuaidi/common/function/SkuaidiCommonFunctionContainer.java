package com.kuaibao.skuaidi.common.function;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import com.kuaibao.skuaidi.activity.view.SelectTimePop;

public class SkuaidiCommonFunctionContainer {

	private static SkuaidiCommonFunctionContainer mInstanse;

	private SelectTimePop pop = null;

	private SkuaidiCommonFunctionContainer() {

	}

	public static SkuaidiCommonFunctionContainer getInstanse() {

		if (null == mInstanse) {
			synchronized (SkuaidiCommonFunctionContainer.class) {
				mInstanse = new SkuaidiCommonFunctionContainer();
			}
		}

		return mInstanse;
	}

	/**
	 * @Title: timingTransmission
	 * @Description: 打开定时发送
	 * @param context
	 * @param v
	 *            显示POP基于的控件
	 * @return SelectTimePop
	 */
	public SelectTimePop timingTransmission(Context context, View v, OnClickListener onClickListener) {
		pop = new SelectTimePop(context, onClickListener);
		pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		return pop;
	}
	
	public void cancelTimingTransmission(){
		
	}
	
}
