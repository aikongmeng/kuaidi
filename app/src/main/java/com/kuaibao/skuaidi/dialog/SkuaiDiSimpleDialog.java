package com.kuaibao.skuaidi.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;

public class SkuaiDiSimpleDialog extends PopupWindow {
	private WindowManager manager;
	private View layout;
	private SkuaiDiSimpleDialogButtonOnclickListener positiveButtonListener, negativeButtonListener;
	private Context context;
	private TextView title, content;
	private Button positiveButton, negativeButton;
	private View parent;

	public SkuaiDiSimpleDialog(Context context) {
		this.context = context;
		initWindow();
	}

	/**
	 * 是否添加遮罩
	 * 
	 * @param isAddGuide
	 */
	@SuppressWarnings("deprecation")
	public void isAddGuide(boolean isAddGuide) {
		if (isAddGuide) {
			parent.setBackgroundDrawable(new ColorDrawable(0x40000000));
		} else {
			parent.setBackgroundDrawable(new ColorDrawable(0x00000000));
		}
	}

	public void show(View paView) {
		showAtLocation(paView, Gravity.CENTER, 0, 0);
	}

	/**
	 * 设置确定按钮文字
	 * 
	 * @param title
	 */
	public void setPositiveButtonTitle(String title) {
		positiveButton.setText(title);
	}

	/**
	 * 设置取消按钮文字
	 * 
	 * @param title
	 */
	public void setNegativeButtonTitle(String title) {
		negativeButton.setText(title);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * 设置提示信息
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content.setText(content);
	}

	private void initWindow() {
		LayoutInflater inflater = LayoutInflater.from(context);
		layout = inflater.inflate(R.layout.dialog_check, null);
		title = (TextView) layout.findViewById(R.id.dialog_title);
		content = (TextView) layout.findViewById(R.id.dialog_content);
		parent = layout.findViewById(R.id.window_is_show_mengban);
		negativeButton = (Button) layout.findViewById(R.id.btn_cancle);
		positiveButton = (Button) layout.findViewById(R.id.btn_do);
		negativeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (negativeButtonListener != null) {
					negativeButtonListener.onClick();
				}
				removeWindow();
			}
		});

		positiveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (positiveButtonListener != null) {
					positiveButtonListener.onClick();
				}
				removeWindow();
			}
		});
		manager = SKuaidiApplication.getInstance().getMWindowManager();
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.type = 2002;
		layoutParams.flags = 40;
		layoutParams.format = PixelFormat.RGBA_8888;
		ColorDrawable dw = new ColorDrawable(0x00000000);
		layout.setBackgroundDrawable(dw);
		manager.addView(layout, layoutParams);

	}

	/**
	 * 设置确定按钮点击事件
	 * 
	 * @param listener
	 */
	public void setPositiveButtonOnclickListener(SkuaiDiSimpleDialogButtonOnclickListener listener) {
		positiveButtonListener = listener;
	}

	/**
	 * 设置取消按钮点击事件
	 * 
	 * @param listener
	 */
	public void setNegativeButtonOnclickListener(SkuaiDiSimpleDialogButtonOnclickListener listener) {
		negativeButtonListener = listener;
	}

	public interface SkuaiDiSimpleDialogButtonOnclickListener {
		void onClick();
	}

	public void removeWindow() {
		manager.removeView(layout);
		// ((SkuaiDiBaseActivity) context).sysDialog = null;
	}

}
