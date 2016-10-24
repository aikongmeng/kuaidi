package com.kuaibao.skuaidi.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.Utility;

/**
 * 灰色系dialog
 * 
 * @author 顾冬冬
 * @created 2015/1/26
 */

public class SkuaidiDialogGrayStyle extends PopupWindow {
	private Context context;
	private LayoutInflater inflater;
	private PositionButtonOnclickListenerGray positionButtonOnclickListenerGray;
	private NegativeButtonOnclickListenerGray negativeButtonOnclickListenerGray;
	private MiddleButtonOnclickListenerGray middleButtonOnclickListenerGray;
	private SelectTimeOncilckListenerGray selectTimeOncilckListenerGray;

	// 控件
	private TextView tv_title;// title
	private TextView tv_chooseTime;// 仅发送短信功能中的定时发送dialog才显示
	private TextView tv_content;// show_content
	private EditText et_content;// input_content
	private RelativeLayout include_contains_checkbox;// 带有checkbox和文本框部分
	private CheckBox cb_choose;// 选择按钮
	private TextView tv_desc;// 文本说明--显示在选择按钮的右边文本框
	private Button btn_cancle;// cancel
	private Button btn_ok;// ok
	private Button btn_middle;// middle
	private View view0;// line-0
	private View view1;// line-1

	private boolean autoDismiss = false;// 是否自动隐藏dialog

	public SkuaidiDialogGrayStyle(Context context) {
		super(context);
		this.context = context;
		inflater = LayoutInflater.from(context);
		initView();
		setListener();
	}

	private void initView() {
		View layout = inflater.inflate(R.layout.dialog_gray, null);
		tv_title = (TextView) layout.findViewById(R.id.tv_title);
		tv_chooseTime = (TextView) layout.findViewById(R.id.tv_chooseTime);
		tv_content = (TextView) layout.findViewById(R.id.tv_content);
		tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
		et_content = (EditText) layout.findViewById(R.id.et_content);
		include_contains_checkbox = (RelativeLayout) layout.findViewById(R.id.include_contains_checkbox);
		cb_choose = (CheckBox) layout.findViewById(R.id.cb_choose);
		tv_desc = (TextView) layout.findViewById(R.id.tv_desc);
		btn_cancle = (Button) layout.findViewById(R.id.btn_cancle);
		btn_ok = (Button) layout.findViewById(R.id.btn_ok);
		btn_middle = (Button) layout.findViewById(R.id.btn_middle);
		view0 = layout.findViewById(R.id.view0);
		view1 = layout.findViewById(R.id.view1);

		tv_chooseTime.setOnClickListener(new MyOnClickListener());
		btn_cancle.setOnClickListener(new MyOnClickListener());
		btn_ok.setOnClickListener(new MyOnClickListener());
		btn_middle.setOnClickListener(new MyOnClickListener());

		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
	}

	class MyOnClickListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 取消按钮
			case R.id.btn_cancle:
				if (negativeButtonOnclickListenerGray != null) {
					negativeButtonOnclickListenerGray.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
				break;
			// 确定按钮
			case R.id.btn_ok:
				if (positionButtonOnclickListenerGray != null) {
					positionButtonOnclickListenerGray.onClick(v);
				}
				if (!autoDismiss) {
					dismiss();
				}
				break;
			// 中间按钮
			case R.id.btn_middle:
				if (middleButtonOnclickListenerGray != null) {
					middleButtonOnclickListenerGray.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
				break;
			// 选择时间按钮
			case R.id.tv_chooseTime:
				if (selectTimeOncilckListenerGray != null) {
					selectTimeOncilckListenerGray.onClick();
				}
				if (!autoDismiss) {
					dismiss();
				}
				break;
			default:
				break;
			}

		}

	}

	/**
	 * 设置是否自动隐藏dialog
	 * 
	 * @param donotdismiss
	 */
	public void setDonotAutoDismiss(boolean donotdismiss) {
		this.autoDismiss = donotdismiss;
	}

	/**
	 * 设置dialog隐藏
	 */
	public void setDismiss() {
		dismiss();
	}

	/** 设置是否只显示带有选择按钮和提示的文本框 **/
	public void setShowCheckBoxText(boolean useCheckboxText) {
		if (useCheckboxText == true) {
			include_contains_checkbox.setVisibility(View.VISIBLE);
		} else {
			include_contains_checkbox.setVisibility(View.GONE);
		}
	}

	/**
	 * 使用三个按钮
	 * 
	 * @param useThree
	 */
	public void isUseThreeBtnStyle(boolean useThree) {
		if (useThree == false) {
			btn_middle.setVisibility(View.GONE);
			view1.setVisibility(View.GONE);
		} else {
			btn_middle.setVisibility(View.VISIBLE);
			view1.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 使用一个按钮
	 * 
	 * @param useMiddle
	 */
	public void isUseMiddleBtnStyle(boolean useMiddle) {
		if (useMiddle == true) {
			btn_cancle.setVisibility(View.GONE);
			btn_ok.setVisibility(View.GONE);
			btn_middle.setVisibility(View.VISIBLE);
			view0.setVisibility(View.GONE);
			view1.setVisibility(View.GONE);
			btn_middle.setBackgroundResource(R.drawable.selector_dialog_middle2);
		}
	}

	/**
	 * 请选择定时发送时间 显示先把时间按钮 仅发送短信功能中的定时发送dialog才使用
	 * 
	 * @param useChooseTimeBtn
	 *            是否显示按钮
	 * @param txt 显示文本
	 */
	public void isUseChooseTimeBtn(boolean useChooseTimeBtn, String context, String txtHint) {
		if (useChooseTimeBtn) {
			tv_chooseTime.setVisibility(View.VISIBLE);
			tv_chooseTime.setText(context);
			tv_chooseTime.setHint(txtHint);
		}
	}

	/**
	 * pop中使用编辑框EditText
	 * 
	 * @param useEditText
	 */
	public void isUseInputEditTextStyle(boolean useEditText) {
		if (useEditText) {
			tv_content.setVisibility(View.VISIBLE);
			et_content.setVisibility(View.GONE);
		} else {
			tv_content.setVisibility(View.GONE);
			et_content.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取编辑框中输入的内容-对应控件（et_content）
	 * 
	 * @return
	 */
	public String getInputEditTextContent() {
		return et_content.getText().toString();
	}

	/**
	 * 设置编辑控件中的hint提示
	 * 
	 * @param inputET_Hint
	 */
	public void setEditTexttHint(String inputET_Hint) {
		et_content.setHint(inputET_Hint);
	}

	/**
	 * 设置编辑框输入弹出键盘类型
	 * 
	 * @param inputTypeStyle
	 */
	public void setEditTextInputTypeStyle(int inputTypeStyle) {
		et_content.setInputType(inputTypeStyle);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitleGray(String title) {
		tv_title.setText(title);
		tv_title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
	}

	/**
	 * 设置title字体颜色-换肤时使用
	 * 
	 * @param color
	 */
	public void setTitleSkinColor(String color) {
		tv_title.setTextColor(SkuaidiSkinManager.getTextColor(color));
	}

	/**
	 * 设置title字体颜色-一般更换(非换肤)
	 * 
	 * @param colorId
	 */
	public void setTitleColor(int colorResourcesId) {
		tv_title.setTextColor(context.getResources().getColor(colorResourcesId));
	}

	/**
	 * 设置提示内容
	 * 
	 * @param content
	 */
	public void setContentGray(String content) {
		tv_content.setVisibility(View.VISIBLE);
		tv_content.setText(content);
	}
	
	/**设置提示内容**/
	public void setContentGray(SpannableStringBuilder ssb){
		tv_content.setVisibility(View.VISIBLE);
		tv_content.setText(ssb);
	}

	/**
	 * 设置文字颜色
	 * 
	 * @param colorID
	 */
	public void setContentGrayColor(int colorID) {
		tv_content.setVisibility(View.VISIBLE);
		tv_content.setTextColor(colorID);
	}

	/** 设置选择框右边文本框 **/
	public void setCBTextConGray(String con) {
		tv_desc.setText(con);
	}

	/**
	 * 设置确定按钮文本
	 * 
	 * @param position_text
	 */
	public void setPositionButtonTextGray(String position_text) {
		if(!Utility.isEmpty(position_text)){
			btn_ok.setText(position_text);
		}else{
			btn_ok.setText("");
		}
	}

	/**
	 * 设置取消按钮文本
	 * 
	 * @param negative_text
	 */
	public void setNegativeButtonTextGray(String negative_text) {
		btn_cancle.setText(negative_text);
	}

	/**
	 * 设置中间按钮文本
	 * 
	 * @param middle_text
	 */
	public void setMiddleButtonTextGray(String middle_text) {
		btn_middle.setText(middle_text);
	}

	/** 获取CheckBox是否被选中 **/
	public boolean getCheckBoxIsChecked() {
		return cb_choose.isChecked();
	}

	/** 部分按钮监听事件 **/
	public void setListener() {
		// 设置checkbox点击后背景改变
		cb_choose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_choose.setButtonDrawable(R.drawable.check_select_surl);
				} else {
					cb_choose.setButtonDrawable(R.drawable.check_select_empty);
				}
			}
		});
	}

	/**
	 * 设置确定按钮点击事件
	 * 
	 */
	public void setPositionButtonClickListenerGray(PositionButtonOnclickListenerGray positionButtonOnclickListenerGray) {
		this.positionButtonOnclickListenerGray = positionButtonOnclickListenerGray;
	}

	/**
	 * 设置取消按钮点击事件
	 * 
	 */
	public void setNegativeButtonClickListenerGray(NegativeButtonOnclickListenerGray negativeButtonOnclickListenerGray) {
		this.negativeButtonOnclickListenerGray = negativeButtonOnclickListenerGray;
	}

	/**
	 * 设置中间按钮点击事件
	 * 
	 * @param middleButtonOnclickListenerGray
	 */
	public void setMiddleButtonClickListenerGray(MiddleButtonOnclickListenerGray middleButtonOnclickListenerGray) {
		this.middleButtonOnclickListenerGray = middleButtonOnclickListenerGray;
	}

	/**
	 * 选择时间按钮点击事件
	 * 
	 * @param selectTimeOncilckListenerGray
	 */
	public void setSelectTimeClickListenerGray(SelectTimeOncilckListenerGray selectTimeOncilckListenerGray) {
		this.selectTimeOncilckListenerGray = selectTimeOncilckListenerGray;
	}

	public void showDialogGray(final View v) {

		v.post(new Runnable() {
			@Override
			public void run() {
				Activity activity = (Activity) context;
				if (!activity.isFinishing()){
					showAtLocation(v, Gravity.CENTER, 0, 0);
				}

			}
		});

	}

	public interface PositionButtonOnclickListenerGray {
		void onClick(View v);
	}

	public interface NegativeButtonOnclickListenerGray {
		void onClick();
	}

	public interface MiddleButtonOnclickListenerGray {
		void onClick();
	}

	public interface SelectTimeOncilckListenerGray {
		void onClick();
	}

}
