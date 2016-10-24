package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.kuaibao.skuaidi.R;

/**
 * 快递圈中点击添加照片后弹出来的pop
 * 
 * @author gudd
 * 
 */
public class GetPhotoTypePop extends PopupWindow {
	View view;
	public Button btn_paizhao;
	public Button btn_xiangce;
	public Button btn_cancel;

	public GetPhotoTypePop(Context context, String scanType, OnClickListener itemOnclick) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.choose_image_method, null);
		btn_paizhao = (Button) view.findViewById(R.id.btn_paizhao);
		btn_xiangce = (Button) view.findViewById(R.id.btn_xiangce);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		// 设置按钮监听
		btn_paizhao.setOnClickListener(itemOnclick);
		btn_xiangce.setOnClickListener(itemOnclick);
		btn_cancel.setOnClickListener(itemOnclick);
		this.setFocusable(true);
		if ("editPirce".equals(scanType)) {// 如果是从我的收件价格单界面进来的话则修改弹窗上的item文本
			btn_paizhao.setText("上传照片");
			btn_xiangce.setText("删除");
			btn_cancel.setText("取消");
		} else if ("upload".equals(scanType)) {
			btn_xiangce.setText("上传同时发短信");
			btn_paizhao.setText("上传同时云呼");
			btn_cancel.setVisibility(View.GONE);
			this.setFocusable(false);
		}

		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
	}

	public void isOutsideTouchCancleable(boolean cancleable) {
		if (cancleable) {
			view.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					dismiss();
				}
			});
		}

	}

}
