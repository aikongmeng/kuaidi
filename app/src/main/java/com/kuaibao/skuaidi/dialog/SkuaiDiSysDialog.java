package com.kuaibao.skuaidi.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;

public class SkuaiDiSysDialog{
	private WindowManager manager;
	private View pop;
	private SkuaiDiSysDialogButtonOnclickListener positionButtonListener,nagtiveButtonListener;
	private Context context;
	private TextView title,content,positionButton,nagtiveButton;
	private View parent, vi_line, ll_center_content;
	public SkuaiDiSysDialog(Context context) {
		this.context = context;
		initWindow();
	}
	
	/**
	 * 是否添加遮罩
	 * @param isAddGuide
	 */
	@SuppressWarnings("deprecation")
	public void isAddGuide(boolean isAddGuide){
		if(isAddGuide){
			parent.setBackgroundDrawable(new ColorDrawable(0x40000000));
		}else{
			parent.setBackgroundDrawable(new ColorDrawable(0x00000000));
		}
	}
	/**
	 * 设置确定按钮文字
	 * @param title
	 */
	public void setPositionButtonTitle(String title){
		positionButton.setText(title.toString());
	}
	/**
	 * 设置取消按钮文字
	 * @param title
	 */
	public void setNagtiveButtonTitle(String title){
		nagtiveButton.setText(title.toString());
	}
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		this.title.setText(title.toString());
	}
	/**
	 * 设置标题颜色
	 * @param color 本地颜色
     */
	public void setTitleColor(int color){
		this.title.setTextColor(context.getResources().getColor(color));
	}
	/**
	 * 设置标题大小
	 * @param size
     */
	public void setTitleSize(int size){
		this.title.setTextSize(size);
	}
	/**
	 * 设置标题分割线的颜色
	 * @param color 本地颜色
     */
	public void setTitleDividerColor(int color){
		this.vi_line.setBackgroundColor(context.getResources().getColor(color));
	}

	/**
	 * 设置提示信息的颜色
	 * @param color
     */
	public void setContentColor(int color){
		this.content.setTextColor(context.getResources().getColor(color));
	}
	/**
	 * 设置提示信息
	 * @param content
	 */
	public void setContent(String content){
		this.content.setText(content.toString());
	}

	public void setContentWithImage(String content, int startImgIndex, int endImgIndex, Bitmap bitmap,
									int startTxtIndex, int endTxtIndex, int textSize){
		SpannableString spannableString =  new  SpannableString(content);
		ImageSpan imageSpan =  new ImageSpan( context , bitmap);
		// 用ImageSpan对象替换字符
		spannableString.setSpan(imageSpan, startImgIndex , endImgIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new AbsoluteSizeSpan(textSize),startTxtIndex, endTxtIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		this.content.setText(spannableString);
	}
	/**
	 * 设置确定按钮的背景颜色
	 * @param color
     */
	public void setPositionButtonColor(int color){
		this.positionButton.setBackgroundColor(context.getResources().getColor(color));
	}
	/**
	 * 设置取消按钮的背景颜色
	 * @param color
     */
	public void setNagtiveButtonColor(int color){
		this.nagtiveButton.setBackgroundColor(context.getResources().getColor(color));
	}
	/**
	 * 设置确定按钮的字体颜色
	 * @param color
	 */
	public void setPositionButtonTextColor(int color){
		this.positionButton.setTextColor(context.getResources().getColor(color));
	}
	/**
	 * 设置取消按钮的字体颜色
	 * @param color
	 */
	public void setNagtiveButtonTextColor(int color){
		this.nagtiveButton.setTextColor(context.getResources().getColor(color));
	}
	private void initWindow(){
		LayoutInflater inflater = LayoutInflater.from(context);
		pop = inflater.inflate(R.layout.miui_setting_layout, null);
		ll_center_content = pop.findViewById(R.id.ll_center_content);
		title = (TextView) pop.findViewById(R.id.dialog_title);
		vi_line = pop.findViewById(R.id.vi_line);
		content = (TextView) pop.findViewById(R.id.dialog_content);
		parent = pop.findViewById(R.id.window_is_show_mengban);
		nagtiveButton = (TextView) pop.findViewById(R.id.btn_pop_cancle);
		nagtiveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(nagtiveButtonListener != null){
					nagtiveButtonListener.onClick();
				}
				removeWindow();
			}
		});
		positionButton = (TextView) pop.findViewById(R.id.btn_pop_sure);
		positionButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(positionButtonListener != null){
					positionButtonListener.onClick();
				}
				removeWindow();
			}
		});
		pop.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int x = (int) event.getX();
				int y = (int) event.getY();
				Rect rect = new Rect();
				ll_center_content.getGlobalVisibleRect(rect);
				if (!rect.contains(x, y)) {
					removeWindow();
				}

				return false;
			}
		});
		pop.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						removeWindow();
						return true;
					default:
						return false;
				}
			}
		});
		manager = SKuaidiApplication.getInstance().getMWindowManager();
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//		layoutParams.type = 2002;
		layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		layoutParams.format = PixelFormat.RGBA_8888;
		ColorDrawable dw = new ColorDrawable(0x00000000);
		pop.setBackgroundDrawable(dw);
		manager.addView(pop, layoutParams);
	}
	/**
	 * 设置确定按钮点击事件
	 * @param listener
	 */
	public void setPositionButtonOnclickListener(SkuaiDiSysDialogButtonOnclickListener listener){
		positionButtonListener = listener;
	}
	/**
	 * 设置取消按钮点击事件
	 * @param listener
	 */
	public void setNagtiveButtopnOnclickListener(SkuaiDiSysDialogButtonOnclickListener listener){
		nagtiveButtonListener = listener;
	}
	public interface SkuaiDiSysDialogButtonOnclickListener{
		void onClick();
	}
	public void removeWindow() {
		manager.removeView(pop);
		((SkuaiDiBaseActivity)context).sysDialog = null;
	}
	
}

