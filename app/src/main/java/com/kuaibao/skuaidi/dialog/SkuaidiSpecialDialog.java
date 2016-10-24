package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class SkuaidiSpecialDialog extends Dialog {

	private Context mContext = null;
	
	private TextView title = null;
	private ViewGroup llBody1 = null;
	private ImageView ivPic = null;
	private TextView tvTxt = null;
	private Button BtnCancle = null;
	private Button BtnOk = null;
	
	private boolean isNotAutoDismiss = false;
	
	private Body1PicClickListener body1PicClickListener = null;
	private ButtonCancleClickListener buttonCancleClickListener = null;
	private ButtonOkClickListener buttonOkClickListener = null;
	
	
	public SkuaidiSpecialDialog(Context context) {
		super(context);
		this.mContext = context;
		
		initView();
		
	}

	private void initView() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.skuaidi_special_dialog, null);
		title = (TextView) layout.findViewById(R.id.tvTitle);
		llBody1 = (ViewGroup) layout.findViewById(R.id.llBody1);
		ivPic = (ImageView) layout.findViewById(R.id.ivPic);
		tvTxt = (TextView) layout.findViewById(R.id.tvTxt);
		BtnCancle = (Button) layout.findViewById(R.id.BtnCancle);
		BtnOk = (Button) layout.findViewById(R.id.BtnOk);
		
		ivPic.setOnClickListener(new MyClickListener());
		BtnCancle.setOnClickListener(new MyClickListener());
		BtnOk.setOnClickListener(new MyClickListener());
		
		Window window = this.getWindow();
		window.setBackgroundDrawable(new ColorDrawable(0x00000000));
		window.setGravity(Gravity.CENTER);
		window.setContentView(layout);
		
	}
	
	/**
	 * @Title: setTitle 
	 * @Description: 设置标题
	 * @param @param title
	 * @return void
	 */
	public void setTitle(String title){
		this.title.setText(title);
	}
	
	/**
	 * @Title: setBody1PicIcon 
	 * @Description: 设置body1中的icon
	 * @param @param resourceId
	 * @return void
	 */
	public void setBody1PicIcon(int resourceId){
		ivPic.setBackgroundResource(resourceId);
	}
	
	/**
	 * @Title: setBody1TextContext 
	 * @Description: 设置body1中的内容
	 * @param @param context
	 * @return void
	 */
	public void setBody1TextContext(String context){
		tvTxt.setText(context);
	}
	
	/**
	 * @Title: setButtonCancleTitle 
	 * @Description: 设置取消按钮文本
	 * @param @param text
	 * @return void
	 */
	public void setButtonCancleTitle(String text){
		BtnCancle.setText(text);
	}
	
	/**
	 * @Title: setButtonOkTitle 
	 * @Description:设置确定按钮文本 
	 * @param @param text
	 * @return void
	 */
	public void setButtonOkTitle(String text){
		BtnOk.setText(text);
	}
	
	public void setNotAutoDismiss(boolean isNotAutoDismiss){
		this.isNotAutoDismiss = isNotAutoDismiss;
	}
	
	public void showDialog(){
		this.show();
	}
	
	private class MyClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ivPic:
				body1PicClickListener.onclick();
				break;
			case R.id.BtnCancle:
				buttonCancleClickListener.onclick();
				if(isNotAutoDismiss == false){
					dismiss();
				}
				break;
			case R.id.BtnOk:
				buttonOkClickListener.onclick();
				if(isNotAutoDismiss == false){
					dismiss();
				}
				break;

			default:
				break;
			}
			
		}
		
	}
	
	public interface Body1PicClickListener{
		void onclick();
	}
	
	public interface ButtonCancleClickListener{
		void onclick();
	}
	
	public interface ButtonOkClickListener{
		void onclick();
	}
	
	public void setBody1PicClickListener(Body1PicClickListener body1PicClickListener){
		this.body1PicClickListener = body1PicClickListener;
	}
	
	public void setButtonCancleClickListener(ButtonCancleClickListener buttonCancleClickListener){
		this.buttonCancleClickListener = buttonCancleClickListener;
	}
	
	public void setButtonOkClickListener(ButtonOkClickListener buttonOkClickListener){
		this.buttonOkClickListener = buttonOkClickListener;
	}


}
