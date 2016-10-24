package com.kuaibao.skuaidi.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.ViewTouchDelegate;

/**
 * 
 * @author a16 回填单号的popupwindow
 */
public class SkuaidiOrderDialog extends PopupWindow {
	private Context context;
	private LayoutInflater inflater;
	private Button btn_cancel, btn_ok;
	private TextView tv_title, tv_diliverNo;
	private ImageView iv_check;
	private PositionButtonOnclickListenerGray positionButtonOnclickListenerGray;
	private NegativeButtonOnclickListenerGray negativeButtonOnclickListenerGray;
	private OnClickListener onClickListener;

	private boolean autoDismiss = false;// 是否自动隐藏dialog
	private TextView tv_select_mode;
	private TextView tv_content;
	private TextView tv_intent_select;
private RelativeLayout rl_left_check;
	public SkuaidiOrderDialog(Context context, OnClickListener onClickListener) {
		super(context);
		this.context = context;
		this.onClickListener = onClickListener;
		inflater = LayoutInflater.from(context);
		initView();

	}

	private void initView() {
		View layout = inflater.inflate(R.layout.dialog_order, null);
		rl_left_check = (RelativeLayout)layout. findViewById(R.id.rl_left_check);
		tv_title = (TextView) layout.findViewById(R.id.tv_title);
		tv_diliverNo = (TextView) layout.findViewById(R.id.tv_diliverNo);
		btn_cancel = (Button) layout.findViewById(R.id.btn_cancle);
		btn_ok = (Button) layout.findViewById(R.id.btn_ok);
		tv_intent_select = (TextView) layout
				.findViewById(R.id.tv_intent_select);
		iv_check = (ImageView) layout.findViewById(R.id.iv_check);
		ViewTouchDelegate.expandViewTouchDelegate(iv_check, 10, 10, 10, 10);// 扩大点击区域
		tv_content = (TextView) layout.findViewById(R.id.tv_content);
		// 设置立即设置的颜色
		tv_intent_select.setBackgroundResource(SkuaidiSkinManager
				.getSkinResId("shape_model_setting"));
		tv_intent_select.setTextColor(SkuaidiSkinManager
				.getTextColor("main_color"));
		tv_title.setText("回填单号");
		tv_title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		tv_select_mode = (TextView) layout.findViewById(R.id.tv_select_mode);
		btn_cancel.setOnClickListener(new MyOnclickListener());
		btn_ok.setOnClickListener(new MyOnclickListener());
		tv_intent_select.setOnClickListener(onClickListener);
		iv_check.setOnClickListener(onClickListener);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
	}

	public View getTV_Select() {
		return tv_intent_select;
	}
	public View getLeftView() {
		return rl_left_check;
	}
	
	public View getIvCheck() {
		return iv_check;
	}

	public View getTvselect() {
		return tv_select_mode;
	}

	public View getBtn_ok() {
		return btn_ok;
	}

	public View getTv_content() {
		return tv_content;
	}

	class MyOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 取消按钮
			case R.id.btn_cancle:
				if (negativeButtonOnclickListenerGray != null) {
					negativeButtonOnclickListenerGray.onClick();
				}
				if (autoDismiss == false) {
					dismiss();
				}
				break;
			// 确定按钮
			case R.id.btn_ok:
				if (positionButtonOnclickListenerGray != null) {
					positionButtonOnclickListenerGray.onClick();
				}
				if (autoDismiss == false) {
					dismiss();
				}
				break;

			default:
				break;
			}

		}

	}

	/**
	 * 设置确定按钮点击事件
	 * 
	 * @param positionButtonOnclickListener
	 */
	public void setPositionButtonClickListenerGray(
			PositionButtonOnclickListenerGray positionButtonOnclickListenerGray) {
		this.positionButtonOnclickListenerGray = positionButtonOnclickListenerGray;
	}

	/**
	 * 设置取消按钮点击事件
	 * 
	 * @param negativeButtonOnclickListener
	 */
	public void setNegativeButtonClickListenerGray(
			NegativeButtonOnclickListenerGray negativeButtonOnclickListenerGray) {
		this.negativeButtonOnclickListenerGray = negativeButtonOnclickListenerGray;
	}

	// 显示popupwindow
	public void showDialog(View v) {
		showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	public interface PositionButtonOnclickListenerGray {
		void onClick();
	}

	public interface NegativeButtonOnclickListenerGray {
		void onClick();
	}

	public interface CheckButtonOnclickListenerGray {
		void onClick();
	}

	/**
	 * 设置确定按钮文本
	 * 
	 * @param position_text
	 */
	public void setDiliverNO(String diliverNo) {
		tv_diliverNo.setText("运单号：" + diliverNo);
	}

	public void setSelectNotice(String str) {
		tv_select_mode.setText(str);
	}
}
