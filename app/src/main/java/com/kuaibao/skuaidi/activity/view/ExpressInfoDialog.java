package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

/**
 * @author a13 查快递模块，用于弹出网点信息的dialog
 */
public class ExpressInfoDialog extends Dialog {

	public ExpressInfoDialog(Context context, int theme) {
		super(context, theme);
	}

	public ExpressInfoDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context context;
		private String name;
		private String tel;
		private String address;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener backButtonClickListener;
		private TextView tv_name;
		private TextView tv_tel;
		private TextView tv_address;
		private TextView dialog_title;

		public Builder(Context context) {
			this.context = context;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getTel() {
			return tel;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getAddress(String string) {
			return address;
		}

		public Builder setPositiveButton(DialogInterface.OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setBackButton(DialogInterface.OnClickListener listener) {
			this.backButtonClickListener = listener;
			return this;
		}

		public ExpressInfoDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ExpressInfoDialog dialog = new ExpressInfoDialog(context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_express_info, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			tv_name = (TextView) layout.findViewById(R.id.tv_expressinfo_name);
			tv_tel = (TextView) layout.findViewById(R.id.tv_expressinfo_tel);
			dialog_title = (TextView) layout.findViewById(R.id.dialog_title);
			dialog_title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			tv_address = (TextView) layout.findViewById(R.id.tv_expressinfo_address);
			tv_name.setText(name);
			tv_tel.setText(tel);
			tv_address.setText(address);
			final ImageView bt_positive = (ImageView) layout.findViewById(R.id.bt_expressinfo_telphone);
			bt_positive.setBackgroundResource(SkuaidiSkinManager.getSkinResId("call_icon"));
			bt_positive.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
				}
			});

			final Button bt_back = (Button) layout.findViewById(R.id.bt_expressinfo_back);
			bt_back.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
				}
			});
			return dialog;
		}
	}
}
