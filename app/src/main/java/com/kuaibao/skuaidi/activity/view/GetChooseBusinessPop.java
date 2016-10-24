package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

/**
 * 弹出修改业务类型的pop
 * 
 * @author gudd
 * 
 */
public class GetChooseBusinessPop extends PopupWindow {
	
	RelativeLayout rl_tongcheng_business,rl_guonei,rl_guoji,rl_other,rl_bottom_ok;
	ImageView iv_select_tongcheng,iv_select_guonei,iv_select_guoji,iv_select_other;
	TextView tv_tongcheng_business, tv_guonei_business, tv_guoji_business,tv_title,tv_sure;
	EditText et_other_business;
	View view;
	Context context;
	Handler handler;
	
	private String businessType = "同城业务";

	public GetChooseBusinessPop(Context context,Handler handler) {
		super(context);
		this.context = context;
		this.handler = handler;
		
		view = View.inflate(context, R.layout.pop_choose_business_type, null);
		rl_tongcheng_business = (RelativeLayout) view.findViewById(R.id.rl_tongcheng_business);
		rl_guonei = (RelativeLayout) view.findViewById(R.id.rl_guonei);
		rl_guoji = (RelativeLayout) view.findViewById(R.id.rl_guoji);
		rl_other = (RelativeLayout) view.findViewById(R.id.rl_other);
		rl_bottom_ok = (RelativeLayout) view.findViewById(R.id.rl_bottom_ok);
		
		iv_select_tongcheng = (ImageView) view.findViewById(R.id.iv_select_tongcheng);
		iv_select_tongcheng.setBackgroundResource(R.drawable.select_edit_identity_hovery);
		iv_select_guonei = (ImageView) view.findViewById(R.id.iv_select_guonei);
		iv_select_guoji = (ImageView) view.findViewById(R.id.iv_select_guoji);
		iv_select_other = (ImageView) view.findViewById(R.id.iv_select_other);
		et_other_business = (EditText) view.findViewById(R.id.et_other_business);
		
		tv_title = (TextView) view.findViewById(R.id.rl_title_identity);
		tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		tv_title.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		
		rl_tongcheng_business.setOnClickListener(new MyOnclicklistener());
		rl_guonei.setOnClickListener(new MyOnclicklistener());
		rl_guoji.setOnClickListener(new MyOnclicklistener());
		rl_other.setOnClickListener(new MyOnclicklistener());
		rl_bottom_ok.setOnClickListener(new MyOnclicklistener());
		et_other_business.setOnClickListener(new MyOnclicklistener());

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
		
		et_other_business.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!et_other_business.equals("")){
					et_other_business.setTextColor(color.black);
				}
				if(et_other_business.getText().length() >=10){
					UtilToolkit.showToast("最大不超过10个字");
				}
			}
		});
	}

	class MyOnclicklistener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_tongcheng_business:
				iv_select_tongcheng.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				iv_select_guonei.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guoji.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_other.setBackgroundResource(R.drawable.select_edit_identity);
				businessType ="同城业务";
				break;
			case R.id.rl_guonei:
				iv_select_tongcheng.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guonei.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				iv_select_guoji.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_other.setBackgroundResource(R.drawable.select_edit_identity);
				businessType ="国内业务";
				break;
			case R.id.rl_guoji:
				iv_select_tongcheng.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guonei.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guoji.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				iv_select_other.setBackgroundResource(R.drawable.select_edit_identity);
				businessType ="国际业务";
				break;
			case R.id.rl_other:
				iv_select_tongcheng.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guonei.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guoji.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_other.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				break;
			case R.id.rl_bottom_ok:
				if(Utility.isNetworkConnected() == false){
					UtilToolkit.showToast("请检查网络");
				}else {
					if(!et_other_business.getText().toString().equals("")){
						businessType = et_other_business.getText().toString();
					}
					KuaidiApi.UserBusinessType(context, handler, SkuaidiSpf.getLoginUser().getUserId(),"add", businessType,"modify");
				}
				dismiss();
				break;
			case R.id.et_other_business:
				iv_select_tongcheng.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guonei.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_guoji.setBackgroundResource(R.drawable.select_edit_identity);
				iv_select_other.setBackgroundResource(R.drawable.select_edit_identity_hovery);
				break;
			default:
				break;
			}

		}

	}

}
