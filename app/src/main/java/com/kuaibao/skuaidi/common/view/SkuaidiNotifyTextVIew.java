package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class SkuaidiNotifyTextVIew extends LinearLayout {

	float textSize;
	int textColor;
	Drawable leftDrawable;
	Drawable rightDrawable;
	ImageView leftIcon;
	TextView tv_notify;
	String notify;

	public SkuaidiNotifyTextVIew(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		setAttributes(context, attrs);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.notify_view, this);
		tv_notify = (TextView) findViewById(R.id.tv_notify);
		leftIcon = (ImageView) findViewById(R.id.iv_icon);
	}

	private void setAttributes(Context context, AttributeSet attrs) {
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.notify_view);
		textColor = array.getColor(R.styleable.notify_view_android_textColor, 0x000000);
		textSize = array.getDimension(R.styleable.notify_view_android_textSize, 12);
		leftDrawable = array.getDrawable(R.styleable.notify_view_leftDrawable);
		rightDrawable = array.getDrawable(R.styleable.notify_view_rightDrawable);
		notify = array.getString(R.styleable.notify_view_notify);
		array.recycle();

		tv_notify.setText(notify);
		if (rightDrawable != null) {
			rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
			tv_notify.setCompoundDrawables(null, null, rightDrawable, null);
		}
		if (leftDrawable != null)
			leftIcon.setImageDrawable(leftDrawable);
		else
			leftIcon.setVisibility(View.GONE);
	}

	public Drawable[] getCompoundDrawables() {
		return tv_notify.getCompoundDrawables();
	}


}
