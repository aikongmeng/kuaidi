package com.kuaibao.skuaidi.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

/**
 * 
 * 通用PopupWindow
 * 
 * @author xy
 * 
 */
public class SkuaiDiPopupWindow extends PopupWindow {
	private LayoutInflater inflater = LayoutInflater.from(SKuaidiApplication.getInstance());
	private SkuaiDiDialogButtonOnclickListener positiveButtonListener, negativeButtonListener;
	private Context context;
	private TextView title, content, positiveButton, negativeButton, tv_topic_title, tv_topic_content;
	private EditText editText;
	private LinearLayout ll_topic_content;
	private View parent;
	private int maxLength;
	private LinearLayout ll_center_content;

	public SkuaiDiPopupWindow(Context context) {
		super(context);
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

	/**
	 * 设置弹窗距离顶部的距离
	 * @param top
     */
	public void setWindowLocation(int top){
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ll_center_content.getLayoutParams();
		params.gravity =Gravity.CENTER_HORIZONTAL;
		params.topMargin = top;
		ll_center_content.setLayoutParams(params);
	}
	/**
	 * 设置确定按钮文字
	 * 
	 * @param title
	 */
	public void setPositiveButtonTitle(String title) {
		positiveButton.setText(title.toString());
	}

	/**
	 * 设置取消按钮文字
	 * 
	 * @param title
	 */
	public void setNegativeButtonTitle(String title) {
		negativeButton.setText(title.toString());
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title.toString());
	}

	/**
	 * 设置提示信息
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content.setText(content.toString());
	}

	/**
	 * 设置editText hint
	 * 
	 * @param hint
	 */
	public void setEditTextHint(String hint) {
		editText.setHint(hint);
	}

	/**
	 * 是否使用输入框
	 * 
	 * @param isUseEditText
	 */
	public void isUseEditText(boolean isUseEditText) {
		if (isUseEditText) {
			editText.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
			this.setFocusable(true);
		} else {
			editText.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置输入框内容
	 *
	 * @param content
     */
	public void setEditTextContent(String content){
		editText.setText(content);
	}

	/**
	 * 设置输入框输入类型
	 * @param type
     */
	public void setEditTextInputType(int type){
		editText.setInputType(type);
	}

	/**
	 * 设置输入框焦点位置
	 *
	 * @param content
	 */
	public void setEditTextFocus(int index){
		editText.setSelection(index);
	}

	/**
	 * 获取输入框内容
	 * 
	 * @return
	 */
	public String getEditTextContent() {
		return editText.getText().toString();
	}

	private void initWindow() {
		View layout = inflater.inflate(R.layout.miui_setting_layout, null);
		ll_center_content = (LinearLayout) layout.findViewById(R.id.ll_center_content);
		title = (TextView) layout.findViewById(R.id.dialog_title);
		title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		content = (TextView) layout.findViewById(R.id.dialog_content);
		parent = layout.findViewById(R.id.window_is_show_mengban);
		ll_topic_content = (LinearLayout) layout.findViewById(R.id.ll_topic_content);
		tv_topic_title = (TextView) layout.findViewById(R.id.tv_topic_title);
		tv_topic_content = (TextView) layout.findViewById(R.id.tv_topic_content);
		View vi_line = layout.findViewById(R.id.vi_line);
		vi_line.setBackgroundColor(SkuaidiSkinManager.getTextColor("main_color"));
		editText = (EditText) layout.findViewById(R.id.dialog_et);
		negativeButton = (TextView) layout.findViewById(R.id.btn_pop_cancle);
		negativeButton.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		negativeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (negativeButtonListener != null) {
					negativeButtonListener.onClick();
				}
				dismiss();
			}
		});
		positiveButton = (TextView) layout.findViewById(R.id.btn_pop_sure);
		positiveButton.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		positiveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (positiveButtonListener != null) {
					positiveButtonListener.onClick();
				}
				dismiss();
			}
		});

		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

	}

	public void show(View paView) {
		showAtLocation(paView, Gravity.CENTER, 0, 0);
	}

	/**
	 * 设置确定按钮点击事件
	 * 
	 * @param listener
	 */
	public void setPositiveButtonOnclickListener(SkuaiDiDialogButtonOnclickListener listener) {
		positiveButtonListener = listener;
	}

	/**
	 * 设置取消按钮点击事件
	 * 
	 * @param listener
	 */
	public void setNegativeButtonOnclickListener(SkuaiDiDialogButtonOnclickListener listener) {
		negativeButtonListener = listener;
	}

	/**
	 * 设置对话框下面的模块可见
	 *
	 * @param visibile
     */
	public void setTopicVisibile(boolean visibile){
		if(visibile){
			ll_topic_content.setVisibility(View.VISIBLE);
		}else{
			ll_topic_content.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 设置对话框下面的模块标题
	 *
	 * @param title
     */
	public void setTopicTitle(String title){
		tv_topic_title.setText(title);
	}

	/**
	 * 设置对话框下面的模块内容
	 *
	 * @param content
     */
	public void setTopicContent(String content){
		tv_topic_content.setText(content);
	}

	public void setSingleButton(){
		negativeButton.setVisibility(View.GONE);
	}

	public void setEditTextMaxLength(int length){
		this.maxLength = length;
	}

	public void setEditTextWatcher(TextWatcher watcher){
		editText.addTextChangedListener(watcher);
	}

	public interface SkuaiDiDialogButtonOnclickListener {
		void onClick();
	}

	public interface EditTextLengthListener {
		void onEditTextMaxLength(EditText edit, String content);
	}
}
