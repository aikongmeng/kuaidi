package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

public class SkuaidiTextView extends TextView {
	private boolean isClickedChangeBackground = false;
	private int coner = SkuaidiSkinManager.CONERS_NULL;
	private int frame = SkuaidiSkinManager.FRAME_NULL;
	private String fileName = "";
	public SkuaidiTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public SkuaidiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom_atts);
		isClickedChangeBackground = a.getBoolean(R.styleable.custom_atts_isClickedChangeBackground, false);
		coner = a.getInt(R.styleable.custom_atts_coners, SkuaidiSkinManager.CONERS_NULL);
		frame = a.getInt(R.styleable.custom_atts_frames, SkuaidiSkinManager.FRAME_NULL);
		fileName = a.getString(R.styleable.custom_atts_clickedChangeBackgrounOfcus);
        a.recycle();
		if (!isInEditMode())
        SkuaidiSkinManager.skinColorChange(this, coner, frame,fileName,isClickedChangeBackground);
	}

	public SkuaidiTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
}
