package com.kuaibao.skuaidi.activity.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

public class EditIdentityPop extends PopupWindow {

	View view;
	RelativeLayout rl_kuaidiyuan, rl_daishoudian, rl_wangdiankefu,
			rl_wangdianjingli, rl_paotuiren, rl_other_identity, rl_bottom_ok;
	ImageView iv_select_kuaidiyuan, iv_select_daishoudian,
			iv_select_wangdiankefu, iv_select_wangdianjingli,
			iv_select_paotuiren, iv_select_identity;
	TextView tv_title,tv_sure;
	EditText et_input_identity;
	Handler handler;
	Context context;
	
	private String roleType = "快递员";
	
	public EditIdentityPop(Activity context ,Handler handler){
		super(context);
		this.handler = handler;
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.pop_edit_identity, null);
		
		rl_kuaidiyuan = (RelativeLayout) view.findViewById(R.id.rl_kuaidiyuan);
		rl_daishoudian = (RelativeLayout) view.findViewById(R.id.rl_daishoudian);
		rl_wangdiankefu = (RelativeLayout) view.findViewById(R.id.rl_wangdiankefu);
		rl_wangdianjingli = (RelativeLayout) view.findViewById(R.id.rl_wangdianjingli);
		rl_paotuiren = (RelativeLayout) view.findViewById(R.id.rl_paotuiren);
		rl_other_identity = (RelativeLayout) view.findViewById(R.id.rl_other_identity);
		rl_bottom_ok = (RelativeLayout) view.findViewById(R.id.rl_bottom_ok);
		et_input_identity = (EditText) view.findViewById(R.id.et_input_identity);
		
		iv_select_kuaidiyuan = (ImageView) view.findViewById(R.id.iv_select_kuaidiyuan);
		iv_select_daishoudian = (ImageView) view.findViewById(R.id.iv_select_daishoudian);
		iv_select_wangdiankefu = (ImageView) view.findViewById(R.id.iv_select_wangdiankefu);
		iv_select_wangdianjingli = (ImageView) view.findViewById(R.id.iv_select_wangdianjingli);
		iv_select_paotuiren = (ImageView) view.findViewById(R.id.iv_select_paotuiren);
		iv_select_identity = (ImageView) view.findViewById(R.id.iv_select_identity);
		
		
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		tv_title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		
		iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity_hovery);
		
		rl_kuaidiyuan.setOnClickListener(new MyonClickListener());
		rl_daishoudian.setOnClickListener(new MyonClickListener());
		rl_wangdiankefu.setOnClickListener(new MyonClickListener());
		rl_wangdianjingli.setOnClickListener(new MyonClickListener());
		rl_paotuiren.setOnClickListener(new MyonClickListener());
		rl_other_identity.setOnClickListener(new MyonClickListener());
		rl_bottom_ok.setOnClickListener(new MyonClickListener());
		et_input_identity.setOnClickListener(new MyonClickListener());
		
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
		
		et_input_identity.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!et_input_identity.equals("")){
					et_input_identity.setTextColor(color.black);
				}
				if(et_input_identity.getText().length() >=10){
					UtilToolkit.showToast("最大不超过10个字");
				}
			}
		});
		
	}
	
	class MyonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_kuaidiyuan:
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity_hovery);
//				iv_select_kuaidiyuan.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity);
				roleType = "快递员";
				break;
			case R.id.rl_daishoudian:
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity);
//				iv_select_daishoudian.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity);
				roleType = "代收点";
				break;
			case R.id.rl_wangdiankefu:
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity_hovery);
//				iv_select_wangdiankefu.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity);
				roleType = "网点客服";
				break;
			case R.id.rl_wangdianjingli:
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity_hovery);
//				iv_select_wangdianjingli.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity);
				roleType = "网点经理";
				break;
			case R.id.rl_paotuiren:
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity_hovery);
//				iv_select_paotuiren.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity);
				roleType = "跑腿人";
				break;
			case R.id.rl_other_identity://///注意
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity_hovery);
//				iv_select_identity.setBackgroundResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
				break;
			case R.id.rl_bottom_ok:
				if(Utility.isNetworkConnected() == false){
					UtilToolkit.showToast("请检查网络");
				}else {
					if(!et_input_identity.getText().toString().equals("")){
						//如果文本框中有文本，则上传自己编写的身份
						roleType = et_input_identity.getText().toString();
					}
					KuaidiApi.UserRoleType(context, handler, "add", roleType);
				}
				
				dismiss();
				break;
			case R.id.et_input_identity:
				iv_select_kuaidiyuan.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_daishoudian.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdiankefu.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_wangdianjingli.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_paotuiren.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_identity.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				break;

			default:
				break;
			}
		}
		
	}

}
