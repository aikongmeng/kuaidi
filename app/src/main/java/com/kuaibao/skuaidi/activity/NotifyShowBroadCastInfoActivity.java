package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.texthelp.TextToLink;

/**
 * 显示发送短信通知
 * @author gudd
 *
 */
public class NotifyShowBroadCastInfoActivity extends RxRetrofitBaseActivity {
	
	private ImageView iv_title_back;//返回按钮 
	private TextView tv_title_des;// title
	private TextView tv_show_broadcast;//显示内容区域
	
	private String contentStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.notify_show_broadcast_info_activity);
		initView();
		getData();
		setData();
	}
	
	/**设置界面数据**/
	private void setData() {
		tv_title_des.setText("最新通知");
//		tv_show_broadcast.setText(contentStr);
		tv_show_broadcast.setText(Html.fromHtml(TextToLink.urlToLink(contentStr)));
		tv_show_broadcast.setMovementMethod(LinkMovementMethod.getInstance());
	}
	/**获取界面数据**/
	private void getData() {
		contentStr = getIntent().getStringExtra("broadcast");
	}
	/**初始化界面控件**/
	private void initView() {
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_show_broadcast = (TextView) findViewById(R.id.tv_show_broadcast);
		
		iv_title_back.setOnClickListener(new MyOnClickListener());
	}
	/**按钮点击事件**/
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;

			default:
				break;
			}
		}
		
	}
}
