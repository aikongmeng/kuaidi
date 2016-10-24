package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.kuaibao.skuaidi.R;

/**
 * 中间带搜索图标，点击后自动左移的edittext
 */
public class IconCenterEditText extends EditText implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
	private static final String TAG = IconCenterEditText.class.getSimpleName();
	/**
	 * 是否是默认图标再左边的样式
	 */
	private boolean isLeft = false;
	/**
	 * 是否点击软键盘搜索
	 */
	private boolean pressSearch = false;
	private boolean hasFoucs;

	private Drawable mClearDrawable;
	/**
	 * 软键盘搜索键监听
	 */
	private OnSearchClickListener listener;

	private DrawableRightListener drawableRightListener;

	public void setOnSearchClickListener(OnSearchClickListener listener) {
		this.listener = listener;
	}

	public IconCenterEditText(Context context) {
		this(context, null);
		init();
	}

	public IconCenterEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
		init();
	}

	public IconCenterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setOnFocusChangeListener(this);
		setOnKeyListener(this);

		//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
			mClearDrawable = getResources().getDrawable(R.drawable.icon_update_delete);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
		//默认设置隐藏图标
//		setClearIconVisible(false);
		//设置焦点改变的监听
//		setOnFocusChangeListener(this);
		//设置输入框里面内容发生改变的监听
		addTextChangedListener(this);
}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isLeft) { // 如果是默认样式，则直接绘制
			super.onDraw(canvas);
		} else { // 如果不是默认样式，需要将图标绘制在中间
			Drawable[] drawables = getCompoundDrawables();
			if (drawables != null) {
				Drawable drawableLeft = drawables[0];
				if (drawableLeft != null) {
					float textWidth = getPaint().measureText(getHint().toString());
					int drawablePadding = getCompoundDrawablePadding();
					int drawableWidth = drawableLeft.getIntrinsicWidth();
					float bodyWidth = textWidth + drawableWidth + drawablePadding;
					canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
				}
			}
			super.onDraw(canvas);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		//Log.d(TAG, "onFocusChange execute");
		// 恢复EditText默认的样式
		if (!pressSearch && TextUtils.isEmpty(getText().toString())) {
			isLeft = hasFocus;
		}
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
		if (pressSearch && listener != null) {
			/* 隐藏软键盘 */
			InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
			}
			listener.onSearchClick(v);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					//里面写上DrawableRight的触发事件
					drawableRightListener.onDrawableRightClick();
				}
			}
		}

		return super.onTouchEvent(event);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count,
							  int after) {
		if(hasFoucs){
			setClearIconVisible(s.length() > 0);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	public void setDrawableRightListener(DrawableRightListener drawableRightListener){
		this.drawableRightListener = drawableRightListener;
	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * @param visible
	*/
	protected void setClearIconVisible(boolean visible) {
		//如果你想让它一直显示DrawableRight的图标，并且还需要让它触发事件，直接把null改成mClearDrawable即可
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}


	public interface OnSearchClickListener {
		void onSearchClick(View view);
	}

	public interface DrawableRightListener{
		void onDrawableRightClick();
	}

}