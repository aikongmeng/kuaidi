package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.BaseModelDragListAdapter;
import com.kuaibao.skuaidi.activity.template.sms_yunhu.ModelVoiceDragListActivity;

import org.greenrobot.eventbus.EventBus;

public class SkuaidiDragListView extends ListView {

	private ImageView dragImageView;// 被拖拽的项，其实就是一个ImageView
	private int dragSrcPosition;// 手指拖动项原始在列表中的位置
	private int dragPosition;// 手指拖动的时候，当前拖动项在列表中的位置

	private int dragPoint;// 在当前数据项中的位置
	private int dragOffset;// 当前视图和屏幕的距离(这里只使用了y方向上)

	private WindowManager windowManager;// windows窗口控制类
	private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数

	private int scaledTouchSlop;// 判断滑动的一个距离
	private int upScrollBounce;// 拖动的时候，开始向上滚动的边界
	private int downScrollBounce;// 拖动的时候，开始向下滚动的边界

	public SkuaidiDragListView(Context context) {
		super(context);
	}

	public SkuaidiDragListView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public SkuaidiDragListView(Context context, AttributeSet attributeSet, int i) {
		super(context, attributeSet, i);
	}

	// 拦截touch事件，其实就是加一层控制
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();

			dragSrcPosition = dragPosition = pointToPosition(x, y);
			if (dragPosition == AdapterView.INVALID_POSITION) {
				return super.onInterceptTouchEvent(ev);
			}

			ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
			dragPoint = y - itemView.getTop();
			dragOffset = (int) (ev.getRawY() - y);

			View dragger = itemView.findViewById(R.id.drag_list_item_image);
			if (dragger != null && x > dragger.getLeft() - 20) {
				//
				upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 3);
				downScrollBounce = Math.max(y + scaledTouchSlop, getHeight() * 2 / 3);

				itemView.setDrawingCacheEnabled(true);
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				startDrag(bm, y);
			}
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 触摸事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (dragImageView != null && dragPosition != INVALID_POSITION) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			case MotionEvent.ACTION_DOWN:
				// 以下eventbus方法针对语音模板录音播放停止功能
				ModelVoiceDragListActivity mvda = new ModelVoiceDragListActivity();
				EventBus.getDefault().post(mvda.new UpdateList("updateList"));
				break;
			default:
				break;
			}
			return true;
		}
		// 也决定了选中的效果
		return super.onTouchEvent(ev);
	}

	/**
	 * 准备拖动，初始化拖动项的图像
	 * 
	 * @param bm
	 * @param y
	 */
	public void startDrag(Bitmap bm, int y) {
		stopDrag();

		windowParams = new WindowManager.LayoutParams();
		// 从上到下计算y方向上的相对位置
		windowParams.gravity = Gravity.TOP;
		windowParams.x = 0;
		windowParams.y = y - dragPoint + dragOffset;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// 下面这些参数能够帮助准确定位到选中项点击位置，照抄即可
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE // 不需获取焦点
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE // 不需接受触摸事件
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // 保持设备常开，并保持亮度不变。
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。
		windowParams.format = PixelFormat.TRANSLUCENT; // 默认为不透明，这里设成透明效果.
		windowParams.windowAnimations = 0; // 窗口所使用的动画设置
		// 把影像ImagView添加到当前视图中
		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService("window");
		windowManager.addView(imageView, windowParams);
		// 把影像ImageView引用到变量drawImageView，用于后续操作(拖动，释放等等)
		dragImageView = imageView;
	}

	/**
	 * 停止拖动，去除拖动项的头像
	 */
	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	/**
	 * 拖动执行，在Move方法中执行
	 * 
	 * @param y
	 */
	public void onDrag(int y) {
		if (dragImageView != null) {
			windowParams.alpha = 0.8f;
			windowParams.y = y - dragPoint + dragOffset;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
		// 为了避免滑动到分割线的时候，返回-1的问题
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		// 滚动
		int scrollHeight = 0;
		if (y < upScrollBounce) {
			scrollHeight = 8;// 定义向上滚动8个像素，如果可以向上滚动的话
		} else if (y > downScrollBounce) {
			scrollHeight = -8;// 定义向下滚动8个像素，，如果可以向上滚动的话
		}

		if (scrollHeight != 0) {
			// 真正滚动的方法setSelectionFromTop()
			setSelectionFromTop(dragPosition, getChildAt(dragPosition - getFirstVisiblePosition()).getTop() + scrollHeight);
		}
	}

	/**
	 * 拖动放下的时候
	 * 
	 * @param y
	 */
	public void onDrop(int y) {

		// 为了避免滑动到分割线的时候，返回-1的问题
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		// 超出边界处理
		if (y < getChildAt(0).getTop()) {// 之前为 if(y<getChildAt(1).getTop())//
											// 但列表中只有一条数据时拖动会crash
			// 超出上边界
			dragPosition = 0;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			// 超出下边界
			dragPosition = getAdapter().getCount() - 1;
		}

		// 数据交换
		if (dragPosition >= 0 && dragPosition < getAdapter().getCount()) {
			@SuppressWarnings("unchecked")
			BaseModelDragListAdapter<Object> adapter = (BaseModelDragListAdapter<Object>) getAdapter();
			Object dragItem = adapter.getItem(dragSrcPosition);
			adapter.remove(dragItem);
			adapter.insert(dragItem, dragPosition);
			adapter.notifyDataSetChanged();
			// Toast.makeText(getContext(), adapter.getList().toString(),
			// Toast.LENGTH_SHORT).show();
		}
	}
}
