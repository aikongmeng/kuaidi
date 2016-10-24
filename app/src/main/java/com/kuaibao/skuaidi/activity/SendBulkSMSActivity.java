package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Follower;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 谁收藏了我-消息群发，选择模板
 */
public class SendBulkSMSActivity extends SkuaiDiBaseActivity implements OnClickListener {
	private static final String PUSH_COURIER_ADVERTISEMENT = "push.courier_advertisement";// 消息群发
	public static final int REQUEST_SELECT_MODEL = 0X1001;// 请求选择模板
	public static final int RESULT_SELECT_MODEL = 0X1002; // 返回选择模板

	private TextView tv_title;
	private List<Follower> followers;
	private SkuaidiTextView tv_send;
	private TextView tv_sms_content;
	private Context context;
	private ReplyModel mReplyModel;
	private static final String orderdh = "#DHDHDHDHDH#";
	private static final String model_url = "#SURLSURLSURLSURLS#";

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_send_bulk_sms);
		context = this;
		followers = (List<Follower>) getIntent().getSerializableExtra("followers");
		initView();
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_send = (SkuaidiTextView) findViewById(R.id.tv_more);
		tv_send.setOnClickListener(this);
		tv_send.setText("发送");
		tv_sms_content = (TextView) findViewById(R.id.tv_sms_content);
		((TextView) findViewById(R.id.tv_title_des)).setText("消息群发");
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		dismissProgressDialog();
		if (PUSH_COURIER_ADVERTISEMENT.equals(sname)) {
			if (!TextUtils.isEmpty(result)) {
				JSONObject data;
				try {
					data = new JSONObject(result);
					if (data != null) {
						int totalCount = data.optInt("totalCount");
						int successCount = data.optInt("successCount");

						if (totalCount > successCount) {
							UtilToolkit.showToast( successCount + "条消息发送成功;" + (totalCount - successCount)
									+ "条消息发送失败");
						} else {
							UtilToolkit.showToast( "消息发送成功");

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			finish();
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		if (!Utility.isEmpty(sname)) {
			if (PUSH_COURIER_ADVERTISEMENT.equals(sname) && !Utility.isEmpty(result)) {
				ToastHelper.makeText(getApplicationContext(), result, ToastHelper.LENGTH_LONG)
						.setAnimation(R.style.popUpWindowEnterExit).show();
			}
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		// TODO Auto-generated method stub

	}

	public void chooseTemplate(View view) {
		Intent intent = new Intent(this, ModelActivity.class);
		intent.putExtra("template_type", "liuyan");
		startActivityForResult(intent, REQUEST_SELECT_MODEL);
	}

	/**
	 * 发送消息
	 */
	private void sendSMS(String modelID) {
		JSONObject data = new JSONObject();
		JSONArray users = new JSONArray();
		for (Follower f : followers) {
			JSONObject user = new JSONObject();
			try {
				user.put("id", f.getId());
				user.put("type", f.getType());
				users.put(user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			data.put("sname", PUSH_COURIER_ADVERTISEMENT);
			data.put("courierId", SkuaidiSpf.getLoginUser().getUserId());// 快递员id
			data.put("templateId", modelID);// 消息模板id
			data.put("userList", users);// 用户id
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
			showProgressDialog( "消息发送中...");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SELECT_MODEL && resultCode == RESULT_SELECT_MODEL) {
			mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
			if (null != mReplyModel) {

				tv_title.setText(mReplyModel.getTitle());

				String str = mReplyModel.getModelContent();
				if (str.contains("#DH#")) {
					str = str.replaceAll("#DH#", orderdh);
				}
				if (str.contains("#SURL#")) {
					str = str.replace("#SURL#", model_url);
				}
				// 替换字符串中指定字段为指定图片
				TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(context);
				tv_sms_content.setText(mTextInsertImgParser.replace(str));
			} else {
				ToastHelper.makeText(getApplicationContext(), "选择模板有误！", ToastHelper.LENGTH_LONG)
						.setAnimation(R.style.popUpWindowEnterExit).show();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_more:
			if (null == mReplyModel) {
				ToastHelper.makeText(getApplicationContext(), "请选择一个消息模板再发送！", ToastHelper.LENGTH_LONG)
						.setAnimation(R.style.popUpWindowEnterExit).show();
			} else {
				sendSMS(mReplyModel.getTid());
			}
			break;

		default:
			break;
		}
	}

}
