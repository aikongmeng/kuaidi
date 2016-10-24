/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 * 
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kuaibao.skuaidi.wheelview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.wheelview.widget.adapter.WheelViewAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Numeric wheel view.
 * 
 * @author gudd
 */
public class WheelView extends View {

	/** Top and bottom shadows colors */
	/*/ Modified by wulianghuan 2014-11-25
	private int[] SHADOWS_COLORS = new int[] { 0xFF111111,
			0x00AAAAAA, 0x00AAAAAA };
	//*/
	private int[] SHADOWS_COLORS = new int[] { 0xefE9E9E9,
			0xcfE9E9E9, 0x3fE9E9E9 };

	/** Top and bottom items offset (to hide that) */
	private static final int ITEM_OFFSET_PERCENT = 0;

	/** Left and right padding value */
	private static final int PADDING = 10;

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;

	 /** 瀛椾綋澶у皬 */
    private static final int TEXT_SIZE = 24;

    /** 椤堕儴 鍜� 搴曢儴 鏉＄洰鐨勯殣钘忓ぇ灏�, 
     * 濡傛灉鏄鏁� 浼氶殣钘忎竴閮ㄤ唤, 
     * 0 椤堕儴 鍜� 搴曢儴鐨勫瓧姝ｅソ绱ц创 杈圭紭, 
     * 璐熸暟鏃� 椤堕儴鍜屽簳閮� 涓� 瀛楁湁涓�瀹氶棿璺� */
    private static final int ITEM_OFFSET = TEXT_SIZE / 5;
    /** 缁樺埗鏅�氭潯鐩敾绗� */
    private TextPaint itemsPaint;
    /** 缁樺埗閫変腑鏉＄洰鐢荤瑪 */
    private TextPaint valuePaint;
    /** 褰撳墠鏉＄洰涓殑鏂囧瓧棰滆壊 */
    private static final int VALUE_TEXT_COLOR = 0xF0f99755;

    /** 闈炲綋鍓嶆潯鐩殑鏂囧瓧棰滆壊 */
    private static final int ITEMS_TEXT_COLOR = 0xFFcccccc;
    /** 鏅�氭潯鐩竷灞�
     * StaticLayout 甯冨眬鐢ㄤ簬鎺у埗 TextView 缁勪欢, 涓�鑸儏鍐典笅涓嶄細鐩存帴浣跨敤璇ョ粍浠�, 
     * 闄ら潪浣犺嚜瀹氫箟涓�涓粍浠� 鎴栬�� 鎯宠鐩存帴璋冪敤  Canvas.drawText() 鏂规硶
     *  */
//    private StaticLayout itemsLayout;
    private StaticLayout labelLayout;
    /** 閫変腑鏉＄洰甯冨眬 */
    private StaticLayout valueLayout;
    /** Label offset */
    private static final int LABEL_OFFSET = 8;
	
	
	
	// Wheel Values
	private int currentItem = 0;

	//  榛樿鍙鏉℃暟
	private int visibleItems = DEF_VISIBLE_ITEMS;

	// Item height
	private int itemHeight = 0;

	// Center Line
	private Drawable centerDrawable;

	// Wheel drawables
	private int wheelBackground = R.drawable.wheel_bg;
	private int wheelForeground = R.drawable.wheel_val;

	// Shadows drawables
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Draw Shadows
	private boolean drawShadows = true;

	// Scrolling
	private WheelScroller scroller;
	private boolean isScrollingPerformed;
	private int scrollingOffset;

	// Cyclic
	boolean isCyclic = false;

	// Items layout
	private LinearLayout itemsLayout;

	// The number of first item in layout
	private int firstItem;

	// View adapter
	private WheelViewAdapter viewAdapter;

	// Recycle
	private WheelRecycle recycle = new WheelRecycle(this);

	// Listeners
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
	private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}

	/**
	 * Initializes class data
	 * @param context the context
	 */
	private void initData(Context context) {
		scroller = new WheelScroller(getContext(), scrollingListener);
	}

	// Scrolling listener
	WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
		@Override
		public void onStarted() {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}

		@Override
		public void onScroll(int distance) {
			doScroll(distance);

			int height = getHeight();
			if (scrollingOffset > height) {
				scrollingOffset = height;
				scroller.stopScrolling();
			} else if (scrollingOffset < -height) {
				scrollingOffset = -height;
				scroller.stopScrolling();
			}
		}

		@Override
		public void onFinished() {
			if (isScrollingPerformed) {
				notifyScrollingListenersAboutEnd();
				isScrollingPerformed = false;
			}

			scrollingOffset = 0;
			invalidate();
		}

		@Override
		public void onJustify() {
			if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
				scroller.scroll(scrollingOffset, 0);
			}
		}
	};

	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.setInterpolator(interpolator);
	}

	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets the desired count of visible items.
	 * Actual amount of visible items depends on wheel layout parameters.
	 * To apply changes and rebuild view call measure().
	 * 
	 * @param count the desired count for visible items
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
	}

	/**
	 * Gets view adapter
	 * @return the view adapter
	 */
	public WheelViewAdapter getViewAdapter() {
		return viewAdapter;
	}

	// Adapter listener
	private DataSetObserver dataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			invalidateWheel(false);
		}

		@Override
		public void onInvalidated() {
			invalidateWheel(true);
		}
	};

	/**
	 * Sets view adapter. Usually new adapters contain different views, so
	 * it needs to rebuild view by calling measure().
	 * 
	 * @param viewAdapter the view adapter
	 */
	public void setViewAdapter(WheelViewAdapter viewAdapter) {
		if (this.viewAdapter != null) {
			this.viewAdapter.unregisterDataSetObserver(dataObserver);
		}
		this.viewAdapter = viewAdapter;
		if (this.viewAdapter != null) {
			this.viewAdapter.registerDataSetObserver(dataObserver);
		}

		invalidateWheel(true);
	}

	/**
	 * Adds wheel changing listener
	 * @param listener the listener
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * @param listener the listener
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}

	/**
	 * Notifies changing listeners
	 * @param oldValue the old wheel value
	 * @param newValue the new wheel value
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * @param listener the listener
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * @param listener the listener
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}

	/**
	 * Notifies listeners about starting scrolling
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Adds wheel clicking listener
	 * @param listener the listener
	 */
	public void addClickingListener(OnWheelClickedListener listener) {
		clickingListeners.add(listener);
	}

	/**
	 * Removes wheel clicking listener
	 * @param listener the listener
	 */
	public void removeClickingListener(OnWheelClickedListener listener) {
		clickingListeners.remove(listener);
	}

	/**
	 * Notifies listeners about clicking
	 */
	protected void notifyClickListenersAboutClick(int item) {
		for (OnWheelClickedListener listener : clickingListeners) {
			listener.onItemClicked(this, item);
		}
	}

	/**
	 * 鑾峰彇褰撳墠item鐨刬d
	 * 
	 * @return the current value
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 * @param animated the animation flag
	 */
	public void setCurrentItem(int index, boolean animated) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return; // throw?
		}

		int itemCount = viewAdapter.getItemsCount();
		if (index < 0 || index >= itemCount) {
			if (isCyclic) {
				while (index < 0) {
					index += itemCount;
				}
				index %= itemCount;
			} else{
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				int itemsToScroll = index - currentItem;
				if (isCyclic) {
					int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
					if (scroll < Math.abs(itemsToScroll)) {
						itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
					}
				}
				scroll(itemsToScroll, 0);
			} else {
				scrollingOffset = 0;

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * @param isCyclic the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		invalidateWheel(false);
	}

	/**
	 * Determine whether shadows are drawn
	 * @return true is shadows are drawn
	 */
	public boolean drawShadows() {
		return drawShadows;
	}

	/**
	 * Set whether shadows should be drawn
	 * @param drawShadows flag as true or false
	 */
	public void setDrawShadows(boolean drawShadows) {
		this.drawShadows = drawShadows;
	}

	/**
	 * Set the shadow gradient color
	 * @param start
	 * @param middle
	 * @param end
	 */
	public void setShadowColor(int start, int middle, int end) {
		SHADOWS_COLORS = new int[] {start, middle, end};
	}

	/**
	 * Sets the drawable for the wheel background
	 * @param resource
	 */
	public void setWheelBackground(int resource) {
		wheelBackground = resource;
		setBackgroundResource(wheelBackground);
	}

	/**
	 * Sets the drawable for the wheel foreground
	 * @param resource
	 */
	public void setWheelForeground(int resource) {
		wheelForeground = resource;
		centerDrawable = getContext().getResources().getDrawable(wheelForeground);
	}

	/**
	 * Invalidates wheel
	 * @param clearCaches if true then cached views will be clear
	 */
	public void invalidateWheel(boolean clearCaches) {
		if (clearCaches) {
			recycle.clearAll();
			if (itemsLayout != null) {
				itemsLayout.removeAllViews();
			}
			valueLayout = null;
			scrollingOffset = 0;
		} else if (itemsLayout != null) {
			// cache all items
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
		}

		invalidate();
	}

	/**
	 * Initializes resources
	 */
	private void initResourcesIfNecessary() {
		/*
    	 * 璁剧疆缁樺埗鏅�氭潯鐩殑鐢荤瑪, 鍏佽鎶楁嫆榻�, 鍏佽 fake-bold
    	 * 璁剧疆鏂囧瓧澶у皬涓� 24
    	 */
        if (itemsPaint == null) {
            itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            itemsPaint.setTextSize(TEXT_SIZE);
        }

        /*
         * 璁剧疆缁樺埗閫変腑鏉＄洰鐨勭敾绗�
         * 璁剧疆鏂囧瓧澶у皬 24
         */
        if (valuePaint == null) {
            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
            valuePaint.setTextSize(TEXT_SIZE);
            valuePaint.setShadowLayer(0.1f, 0, 0.1f, 0xFFc0c0c0);
        }

        //閫変腑鐨勬潯鐩儗鏅�
		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(wheelForeground);
		}
		 //鍒涘缓椤堕儴闃村奖鍥剧墖
		if (topShadow == null) {
			topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		}
		//鍒涘缓搴曢儴闃村奖鍥剧墖
		if (bottomShadow == null) {
			bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
		}
		//璁剧疆 View 缁勪欢鐨勮儗鏅�
		setBackgroundResource(wheelBackground);
	}

	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 */
	private int getDesiredHeight(LinearLayout layout) {
		if (layout != null && layout.getChildAt(0) != null) {
			itemHeight = layout.getChildAt(0).getMeasuredHeight();
		}

		int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;

		return Math.max(desired, getSuggestedMinimumHeight());
	}

	/**
	 * Returns height of wheel item
	 * @return the item height
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		}

		if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
			itemHeight = itemsLayout.getChildAt(0).getHeight();
			return itemHeight;
		}

		return getHeight() / visibleItems;
	}

	/**
	 * Calculates control width and creates text layouts
	 * @param widthSize the input layout width
	 * @param mode the layout mode
	 * @return the calculated control width
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();
		
		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int width = itemsLayout.getMeasuredWidth();

		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width += 2 * PADDING;

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
			}
		}

		itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		return width;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		buildViewForMeasuring();

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layout(r - l, b - t);
	}

	/**
	 * Sets layouts width and height
	 * @param width the layout width
	 * @param height the layout height
	 */
	private void layout(int width, int height) {
		int itemsWidth = width - 2 * PADDING;

		itemsLayout.layout(0, 0, itemsWidth, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
			updateView();

			canvas.save();
			// 浣跨敤骞崇Щ鏂规硶蹇界暐 濉厖鐨勭┖闂� 鍜� 椤堕儴搴曢儴闅愯棌鐨勪竴閮ㄤ唤鏉＄洰
//            canvas.translate(PADDING, -ITEM_OFFSET);
            //缁樺埗鏅�氭潯鐩�
			drawItems(canvas);
			//缁樺埗閫変腑鏉＄洰
            drawValue(canvas);
			drawCenterRect(canvas);
			canvas.restore();
		}

		if (drawShadows) drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {
		/*/ Modified by wulianghuan 2014-11-25
		int height = (int)(1.5 * getItemHeight());
		//*/
		int height = 3 * getItemHeight();
		//*/
		topShadow.setBounds(0, 0, getWidth(), height);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}

	/**
	 * Draws items
	 * @param canvas the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();

		int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
		canvas.translate(PADDING, - top + scrollingOffset);

		itemsLayout.draw(canvas);

		canvas.restore();
	}
	
	/**
     * 缁樺埗閫変腑鏉＄洰
     * @param canvas  鐢诲竷
     */
    private void drawValue(Canvas canvas) {
        valuePaint.setColor(VALUE_TEXT_COLOR);
        
        //灏嗗綋鍓� View 鐘舵�佸睘鎬у�� 杞负鏁村瀷闆嗗悎, 璧嬪�肩粰 鏅�氭潯鐩竷灞�鐨勭粯鍒跺睘鎬�
        valuePaint.drawableState = getDrawableState();

        Rect bounds = new Rect();
        //鑾峰彇閫変腑鏉＄洰甯冨眬鐨勮竟鐣�
//        itemsLayout.getLineBounds(visibleItems / 2, bounds);

        // 缁樺埗鏍囩
        if (labelLayout != null) {
            canvas.save();
            canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
            labelLayout.draw(canvas);
            canvas.restore();
        }

        // 缁樺埗閫変腑鏉＄洰
        if (valueLayout != null) {
            canvas.save();
            canvas.translate(0, bounds.top + scrollingOffset);
            valueLayout.draw(canvas);
            canvas.restore();
        }
    }

	/**
	 * Draws rect for current value
	 * @param canvas the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = (int) (getItemHeight() / 2 * 1.2);
		/*/ Remarked by wulianghuan 2014-11-27  浣跨敤鑷繁鐨勭敾绾匡紝鑰屼笉鏄弿杈�
		Rect rect = new Rect(left, top, right, bottom)
		centerDrawable.setBounds(bounds)
		centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerDrawable.draw(canvas);
		//*/
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.province_line_border));
		// 璁剧疆绾垮
		paint.setStrokeWidth((float) 3);
		// 缁樺埗涓婅竟鐩寸嚎
		canvas.drawLine(0, center - offset, getWidth(), center - offset, paint);
		// 缁樺埗涓嬭竟鐩寸嚎
		canvas.drawLine(0, center + offset, getWidth(), center + offset, paint);
		//*/
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled() || getViewAdapter() == null) {
			return true;
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (getParent() != null) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				break;

			case MotionEvent.ACTION_UP:
				if (!isScrollingPerformed) {
					int distance = (int) event.getY() - getHeight() / 2;
					if (distance > 0) {
						distance += getItemHeight() / 2;
					} else {
						distance -= getItemHeight() / 2;
					}
					int items = distance / getItemHeight();
					if (items != 0 && isValidItemIndex(currentItem + items)) {
						notifyClickListenersAboutClick(currentItem + items);
					}
				}
				break;
		}

		return scroller.onTouchEvent(event);
	}

	/**
	 * Scrolls the wheel
	 * @param delta the scrolling value
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;

		int itemHeight = getItemHeight();
		int count = scrollingOffset / itemHeight;

		int pos = currentItem - count;
		int itemCount = viewAdapter.getItemsCount();

		int fixPos = scrollingOffset % itemHeight;
		if (Math.abs(fixPos) <= itemHeight / 2) {
			fixPos = 0;
		}
		if (isCyclic && itemCount > 0) {
			if (fixPos > 0) {
				pos--;
				count++;
			} else if (fixPos < 0) {
				pos++;
				count--;
			}
			// fix position by rotating
			while (pos < 0) {
				pos += itemCount;
			}
			pos %= itemCount;
		} else {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= itemCount) {
				count = currentItem - itemCount + 1;
				pos = itemCount - 1;
			} else if (pos > 0 && fixPos > 0) {
				pos--;
				count++;
			} else if (pos < itemCount - 1 && fixPos < 0) {
				pos++;
				count--;
			}
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * itemHeight;
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	/**
	 * Scroll the wheel
	 * @param itemsToSkip items to scroll
	 * @param time scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		int distance = itemsToScroll * getItemHeight() - scrollingOffset;
		scroller.scroll(distance, time);
	}

	/**
	 * Calculates range for wheel items
	 * @return the items range
	 */
	private ItemsRange getItemsRange() {
		if (getItemHeight() == 0) {
			return null;
		}

		int first = currentItem;
		int count = 1;

		while (count * getItemHeight() < getHeight()) {
			first--;
			count += 2; // top + bottom items
		}

		if (scrollingOffset != 0) {
			if (scrollingOffset > 0) {
				first--;
			}
			count++;

			// process empty items above the first or below the second
			int emptyItems = scrollingOffset / getItemHeight();
			first -= emptyItems;
			count += Math.asin(emptyItems);
		}
		return new ItemsRange(first, count);
	}

	/**
	 * Rebuilds wheel items if necessary. Caches all unused items.
	 * 
	 * @return true if items are rebuilt
	 */
	private boolean rebuildItems() {
		boolean updated = false;
		ItemsRange range = getItemsRange();
		if (itemsLayout != null) {
			int first = recycle.recycleItems(itemsLayout, firstItem, range);
			updated = firstItem != first;
			firstItem = first;
		} else {
			createItemsLayout();
			updated = true;
		}

		if (!updated) {
			updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
		}

		if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
			for (int i = firstItem - 1; i >= range.getFirst(); i--) {
				if (!addViewItem(i, true)) {
					break;
				}
				firstItem = i;
			}
		} else {
			firstItem = range.getFirst();
		}

		int first = firstItem;
		for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
			if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
				first++;
			}
		}
		firstItem = first;

		return updated;
	}

	/**
	 * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
	 */
	private void updateView() {
		if (rebuildItems()) {
			calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			layout(getWidth(), getHeight());
		}
	}

	/**
	 * Creates item layouts if necessary
	 */
	private void createItemsLayout() {
		if (itemsLayout == null) {
			itemsLayout = new LinearLayout(getContext());
			itemsLayout.setOrientation(LinearLayout.VERTICAL);
		}
	}

	/**
	 * Builds view for measuring
	 */
	private void buildViewForMeasuring() {
		// clear all items
		if (itemsLayout != null) {
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
		} else {
			createItemsLayout();
		}

		// add views
		int addItems = visibleItems / 2;
		for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
			if (addViewItem(i, true)) {
				firstItem = i;
			}
		}
	}

	/**
	 * Adds view for item to items layout
	 * @param index the item index
	 * @param first the flag indicates if view should be first
	 * @return true if corresponding item exists and is added
	 */
	private boolean addViewItem(int index, boolean first) {
		View view = getItemView(index);
		if (view != null) {
			if (first) {
				itemsLayout.addView(view, 0);
			} else {
				itemsLayout.addView(view);
			}

			return true;
		}

		return false;
	}

	/**
	 * Checks whether intem index is valid
	 * @param index the item index
	 * @return true if item index is not out of bounds or the wheel is cyclic
	 */
	private boolean isValidItemIndex(int index) {
		return viewAdapter != null && viewAdapter.getItemsCount() > 0 &&
				(isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
	}

	/**
	 * Returns view for specified item
	 * @param index the item index
	 * @return item view or empty view if index is out of bounds
	 */
	private View getItemView(int index) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return null;
		}
		int count = viewAdapter.getItemsCount();
		if (!isValidItemIndex(index)) {
			return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
	}

	/**
	 * Stops scrolling
	 */
	public void stopScrolling() {
		scroller.stopScrolling();
	}
}
