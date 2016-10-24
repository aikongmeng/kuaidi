package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog, null);
			((TextView) layout.findViewById(R.id.title)).setText(title);
			((TextView) layout.findViewById(R.id.title)).setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			((TextView) layout.findViewById(R.id.message)).setText(message);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			final Button bt_positive = (Button) layout
					.findViewById(R.id.positiveButton);
			bt_positive.setText(positiveButtonText);
			bt_positive.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			bt_positive.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					positiveButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_POSITIVE);
				}
			});
			bt_positive.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						bt_positive
								.setBackgroundDrawable(context
										.getResources()
										.getDrawable(
												R.drawable.shape_bottom_left_blue_hover));
						bt_positive.setTextColor(context.getResources()
								.getColor(R.color.white));
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						bt_positive.setBackgroundDrawable(context
								.getResources().getDrawable(
										R.drawable.shape_bottom_left_white));
						bt_positive.setTextColor(context.getResources()
								.getColor(R.color.text_green_four));
					}
					return false;
				}
			});
			final Button bt_negative = (Button) layout
					.findViewById(R.id.negativeButton);
			bt_negative.setText(negativeButtonText);
			bt_negative.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			bt_negative.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					negativeButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			});
			bt_negative.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						bt_negative
								.setBackgroundDrawable(context
										.getResources()
										.getDrawable(
												R.drawable.shape_bottom_right_blue_hover));
						bt_negative.setTextColor(context.getResources()
								.getColor(R.color.white));
					}
					if (event.getAction() == MotionEvent.ACTION_UP) {
						bt_negative.setBackgroundDrawable(context
								.getResources().getDrawable(
										R.drawable.shape_bottom_right_white));
						bt_negative.setTextColor(context.getResources()
								.getColor(R.color.text_green_four));
					}
					return false;
				}
			});
			return dialog;
		}
	}
}
