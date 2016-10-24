package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.HuodongDescriptionActivity;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

public class GetBusinessQrcodePaymentPop extends PopupWindow {
	View view;
	ImageView iv_qrcode_show;
	TextView tv_explain;
	String qrcodeUrl;
	
	public GetBusinessQrcodePaymentPop(final Context context){
		super(context);
		view = View.inflate(context, R.layout.pop_business_payment_qrcode, null);
		
		iv_qrcode_show = (ImageView) view.findViewById(R.id.iv_qrcode_show);
		tv_explain = (TextView) view.findViewById(R.id.tv_explain);
		
		tv_explain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						HuodongDescriptionActivity.class);
				intent.putExtra("url", Constants.ACTIVITY_EXPLAIN);
				context.startActivity(intent);
				dismiss();
			}
		});
		
		qrcodeUrl = Constants.BUSINESS_ZHIFU_QRCODE + SkuaidiSpf.getLoginUser().getUserId();

		try {
			iv_qrcode_show.setImageBitmap(BitmapUtil.Create2DCode(context,
					qrcodeUrl));
		} catch (WriterException e) {
			UtilToolkit.showToast("二维码生成失败");
			e.printStackTrace();
		}
		
		tv_explain.setText(Utility.getUnderLineSpan(context, "支付及补贴活动说明", 0, 9));
		
		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

		view.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});
		
		
	}
	
	
}
