package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 
 * @author a16 快件签收短信设置
 */
public class SelectModeActivity extends SkuaiDiBaseActivity {

	private LinearLayout ll_notice_sender;

	private LinearLayout ll_select_mode;
	private TextView tv_title_des, tv_sms_title; // 提示
	private EditText et_notice_content;
	private boolean hasnotice;
	private int click_num = 0;
	private Context context;
	private ImageView iv_notice_addressor, iv_sms_arrow;
	private boolean select_mode;
	private SkuaidiDB skuaidiDb;
	private List<ReplyModel> models;
	private String modelContentSpf;// 从数据库中获取的短信内容（模板）
	private String from_activity;
	private String contentSpf_zhuan;
	private String finalContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.select_mode);
		context = this;
		getControl();

		// 调接口，获取模板内容
		/*if (Utility.isNetworkConnected() == false) {
			SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
			dialog.setTitleGray("提示");
			dialog.setTitleSkinColor("main_color");
			dialog.setContentGray("您没有连接网络，是否进行设置？");
			dialog.setPositionButtonTextGray("设置");
			dialog.setNegativeButtonTextGray("取消");
			dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
					startActivity(intent);
				}
			});
			if (!isFinishing())
				dialog.showDialogGray(ll_select_mode);
		} else {
			// 从服务器读取用户存储的短信模板
			JSONObject object = new JSONObject();
			try {
				object.put("sname", "inform.order.sender.template");
				object.put("act", "get");
				httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}*/

	}

	private void getControl() {
		et_notice_content = (EditText) findViewById(R.id.et_notice_content);
		ll_notice_sender = (LinearLayout) findViewById(R.id.ll_notice_sender);
		ll_select_mode = (LinearLayout) findViewById(R.id.ll_select_mode);
		iv_notice_addressor = (ImageView) findViewById(R.id.iv_notice_addressor);
		iv_sms_arrow = (ImageView) findViewById(R.id.iv_sms_arrow);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_sms_title = (TextView) findViewById(R.id.tv_sms_title);
		tv_sms_title.setText("短信内容");
		iv_sms_arrow.setVisibility(View.GONE);
		ll_notice_sender.setOnClickListener(new MyClickListener());
//		ll_select_mode.setOnClickListener(new MyClickListener());
		et_notice_content.setText("您好！您的“公司”快件（单号 “No.”）已签收，如需帮助，请联系收派员“电话”");
		et_notice_content.setTextColor(getResources().getColor(R.color.gray_2));
		tv_title_des.setText("快件签收短信设置");
		hasnotice = SkuaidiSpf.getHasNoticeAddressor(context);
		if (hasnotice) {
			click_num += 1;
			iv_notice_addressor.setBackgroundResource(R.drawable.icon_push_open);
		} else {
			iv_notice_addressor.setBackgroundResource(R.drawable.icon_push_close);
		}

		from_activity = getIntent().getStringExtra("from_activity");

	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 跳转到选模板
			case R.id.ll_select_mode:
				Intent intent1 = new Intent(context, ModelActivity.class);
				intent1.putExtra("from_activity", "select_mode");
				startActivityForResult(intent1, Constants.SELECT_MODEL_SUCCESS);
				break;
			// 自动通知收件人
			case R.id.ll_notice_sender:
				click_num += 1;
				if (click_num % 2 == 1) {
					hasnotice = true;
					iv_notice_addressor.setBackgroundResource(R.drawable.icon_push_open);
				} else {
					hasnotice = false;
					iv_notice_addressor.setBackgroundResource(R.drawable.icon_push_close);
				}
				SkuaidiSpf.saveHasNoticeAddressor(context, hasnotice);
				break;
			default:
				break;
			}

		}

	}

	public void back(View view) {

		if (from_activity != null && from_activity.equals("orderinfo_activity")) {
			setResult(1002);
		}
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.SELECT_MODEL_SUCCESS && resultCode == Constants.SELECT_MODEL_SUCCESS) {
			// 选短信模板
			skuaidiDb = SkuaidiDB.getInstanse(context);// 实例化数据库类
			models = skuaidiDb.getReplyModels(Constants.TYPE_REPLY_MODEL_SIGN);
			if (models == null || models.size() == 0) {
				return;
			}
			ReplyModel model = new ReplyModel();
			for (int i = 0; i < models.size(); i++) {
				// 判断是否有选中模板
				if (models.get(i).isChoose() == true) {
					select_mode = true;
					model = models.get(i);
					break;
				}
			}
			modelContentSpf = model.getModelContent();// 从数据库中找到已经被选中的模板内容

			// 编译短信中包含的特殊字符
			if (null != modelContentSpf && !"".equals(modelContentSpf)) {
				if (modelContentSpf.contains("#DH#")) {
					contentSpf_zhuan = modelContentSpf.replaceAll("#DH#", "#DHDHDHDHDH#");
				} else {
					contentSpf_zhuan = modelContentSpf;
				}
				if (contentSpf_zhuan.contains("#SURL#")) {
					finalContent = contentSpf_zhuan.replaceAll("#SURL#", "#SURLSURLSURLSURLS#");
				} else {
					finalContent = contentSpf_zhuan;

				}
			} else {
				finalContent = "";
			}
			TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(context);
			if (null != finalContent && !"".equals(finalContent)) {
				et_notice_content.setText(mTextInsertImgParser.replace(finalContent));
			} else {
				et_notice_content.setText("");
			}

		}
		// 把用户选中的模板传给服务器
		JSONObject object = new JSONObject();
		try {
			object.put("sname", "inform.order.sender.template");// 接口名
			object.put("act", "update");// 更新模板
			object.put("content", modelContentSpf);// 模板内容

			httpInterfaceRequest(object, false, INTERFACE_VERSION_NEW);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 获取短信内容成功
		if (sname.equals("inform.order.sender.template") && act.equals("get")) {
			String modelContentSpf = result.optString("retStr");

			// 编译短信中包含的特殊字符
			if (null != modelContentSpf && !"".equals(modelContentSpf)) {
				if (modelContentSpf.contains("#DH#")) {
					contentSpf_zhuan = modelContentSpf.replaceAll("#DH#", "#DHDHDHDHDH#");
				} else {
					contentSpf_zhuan = modelContentSpf;
				}
				if (contentSpf_zhuan.contains("#SURL#")) {
					finalContent = contentSpf_zhuan.replaceAll("#SURL#", "#SURLSURLSURLSURLS#");
				} else {
					finalContent = contentSpf_zhuan;

				}
			} else {
				finalContent = "";
			}
			TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(context);
			if (null != finalContent && !"".equals(finalContent)) {
				et_notice_content.setText(mTextInsertImgParser.replace(finalContent));
			} else {
				et_notice_content.setText("");
			}

		} else if (sname.equals("inform.order.sender.template") && act.equals("update")) {
			boolean is_select = true;
			SkuaidiSpf.saveHasSelectModel(context, is_select);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if (code.equals("7") && null != result) {
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
