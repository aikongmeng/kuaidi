package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

public class GetQrcodePayment extends PopupWindow {

	View view;
	ImageView iv_close;
	ImageView iv_qrcode;
	TextView tv_shopname;
	String qrcodeUrl;
	String shop;
	String name;
	TextView tv_activity_description;

	/**
	 * @author gudd  生成支付二维码图片的pop
	 * @param activity
	 *            上下文
	 * @param orderNo
	 *            订单号
	 * @param onClickListener
	 *            点击事件
	 */
	public GetQrcodePayment(Context activity, String orderNo,
			OnClickListener onClickListener) {
		super(activity);

		LayoutInflater layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = layoutInflater.inflate(R.layout.pop_payment_qrcode, null);
		iv_close = (ImageView) view.findViewById(R.id.iv_close);
		iv_qrcode = (ImageView) view.findViewById(R.id.iv_qrcode);
		tv_shopname = (TextView) view.findViewById(R.id.tv_shopname);
		tv_activity_description = (TextView) view.findViewById(R.id.tv_activity_description);
		iv_close.setOnClickListener(onClickListener);
		tv_activity_description.setOnClickListener(onClickListener);

		// 二维码生成url+订单号
//		qrcodeUrl = "http://m.kuaidihelp.com/order/show/?order_no=" + orderNo;
		qrcodeUrl = Constants.ZHIFU_QRCODE + orderNo;

		try {
			iv_qrcode.setImageBitmap(BitmapUtil.Create2DCode(activity,
					qrcodeUrl));
		} catch (WriterException e) {
			UtilToolkit.showToast( "二维码生成失败");
			e.printStackTrace();
		}

		shop = SkuaidiSpf.getLoginUser().getExpressFirm();
		name = SkuaidiSpf.getLoginUser().getUserName();
		tv_shopname.setText(shop + name);
		tv_activity_description.setText(Utility.getUnderLineSpan(activity, "支付及补贴活动说明", 0, 9));
		

		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

}
