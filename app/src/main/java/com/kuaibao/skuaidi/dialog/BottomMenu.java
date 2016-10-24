package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

/**
 * 
 * E3举证相关DiaLog
 * 
 * @author wq
 * 
 */
public class BottomMenu extends Dialog {
	private Context context;
	private TextView tv_item_1, tv_item_2, tv_item_3, tv_cancle;
	private View line1, line2;
	private android.view.View.OnClickListener firstButtonLisenter, secondButtonLisenter, thirdButtonLisenter, cancleButtonLisenter;

	public BottomMenu(Context context, String firstTitle, String secondTitle, String thirdTitle) {
		this(context);
		tv_item_1.setText(firstTitle);
		tv_item_2.setText(secondTitle);
		if(TextUtils.isEmpty(thirdTitle)){
			setThirdButtonVisibility(false);
		}else{
			tv_item_3.setText(thirdTitle);
		}
		
	}


	public BottomMenu(Context context, String firstTitle, String secondTitle) {
		this(context,firstTitle,secondTitle,null);
		
	}

	
	public BottomMenu(Context context) {
		super(context, R.style.Dialog);
		this.context = context;
		onCreateView();
	}

	private void onCreateView() {
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.e3_menu_pop, null);
		tv_item_1 = (TextView) layout.findViewById(R.id.tv_item_1);
		tv_item_2 = (TextView) layout.findViewById(R.id.tv_item_2);
		tv_item_3 = (TextView) layout.findViewById(R.id.tv_item_3);
		tv_cancle = (TextView) layout.findViewById(R.id.tv_cancle);
		line1 = layout.findViewById(R.id.line1);
		line2 = layout.findViewById(R.id.line2);
		// final SkuaidiE3ProofDialog dialog = new SkuaidiE3ProofDialog(context,
		// R.style.Dialog);R.style.Dialog
		this.setContentView(layout);
		Window window = this.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		this.setCancelable(true);
		tv_item_1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				firstButtonLisenter.onClick(v);

			}
		});
		tv_item_2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				secondButtonLisenter.onClick(v);

			}
		});
		tv_item_3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				thirdButtonLisenter.onClick(v);
			}
		});

		tv_cancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(cancleButtonLisenter==null){
					BottomMenu.this.cancel();
				}else{
					cancleButtonLisenter.onClick(v);
				}

			}
		});

	}

	public void setFirstButtonVisibility(boolean b){
		if (b) {
			line1.setVisibility(View.VISIBLE);
			tv_item_1.setVisibility(View.VISIBLE);
		} else {
			line1.setVisibility(View.GONE);
			tv_item_1.setVisibility(View.GONE);
		}
	}

	public void setFirstButtonTitle(String str) {
		tv_item_1.setText(str);
	}

	public void setSecondButtonTitle(String str) {
		tv_item_2.setText(str);
	}

	public void setThirdButtonTitle(String str) {
		tv_item_3.setText(str);
	}

	public void setSecondButtonVisibility(boolean b) {
		if (b) {
			tv_item_2.setVisibility(View.VISIBLE);
		} else {
			tv_item_2.setVisibility(View.GONE);
		}
	}

	public void setThirdButtonVisibility(boolean b) {
		if (b) {
			line2.setVisibility(View.VISIBLE);
			tv_item_3.setVisibility(View.VISIBLE);
		} else {
			line2.setVisibility(View.GONE);
			tv_item_3.setVisibility(View.GONE);
		}
	}

	public void setCancleButtonTitle(String str) {
		tv_cancle.setText(str);
	}

	

	public void setFirstButtonLisenter(android.view.View.OnClickListener lisenter) {
		firstButtonLisenter = lisenter;

	}

	public void setSecondButtonLisenter(android.view.View.OnClickListener lisenter) {
		secondButtonLisenter = lisenter;

	}

	public void setThirdButtonLisenter(android.view.View.OnClickListener lisenter) {
		thirdButtonLisenter = lisenter;
	}

	public void setCancleButtonLisenter(android.view.View.OnClickListener lisenter) {
		cancleButtonLisenter = lisenter;

	}
}
