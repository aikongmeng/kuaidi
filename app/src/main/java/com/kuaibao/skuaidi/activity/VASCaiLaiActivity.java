package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: VASCaiLaiActivity
 * @Description: 财来网活动开关界面
 * @author 顾冬冬
 * @date 2015-11-27 下午2:25:10
 */
public class VASCaiLaiActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private static final int ISOPEN_STATUS_SUCCESS = 0x1001;

	private Context mContext = null;
	private Message msg = null;

	// title
	private SkuaidiImageView back = null;
	private TextView title = null;

	private ImageView cl_switch = null;
	private TextView caiLaiTitle = null, caiLaiDesc = null;

	// 参数
	private boolean clIsOpen = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ISOPEN_STATUS_SUCCESS:
				cl_switch.setVisibility(View.VISIBLE);
				if (clIsOpen == true) {
					cl_switch.setBackgroundResource(R.drawable.icon_push_open);
				} else {
					cl_switch.setBackgroundResource(R.drawable.icon_push_close);
				}
				MessageEvent messageEvent=new MessageEvent(0xca,clIsOpen?"1":"0");
				EventBus.getDefault().post(messageEvent);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.vas_cailai_activity);
		mContext = this;
		initView();
		getData();
		setData();
	}

	private void initView() {
		back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		title = (TextView) findViewById(R.id.tv_title_des);
		cl_switch = (ImageView) findViewById(R.id.cl_switch);
		cl_switch.setVisibility(View.GONE);
		caiLaiTitle = (TextView) findViewById(R.id.caiLaiTitle);
		caiLaiDesc = (TextView) findViewById(R.id.caiLaiDesc);

		back.setOnClickListener(this);
		cl_switch.setOnClickListener(this);

		title.setText("财来网推广活动");
	}

	private void getData() {
		getSwitchStatus();
//		getVasCaiLaiDoc();
	}

	private void setData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * @Title: clSwitch
	 * @Description:财来网开启关闭状态功能
	 * @author: 顾冬冬
	 * @return void
	 */
	private void clSwitch(int switchCode) {
		cl_switch.setEnabled(false);
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "vas/open");
			data.put("switch", switchCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/**
	 * @Title: clSwitch
	 * @Description:获取财来网开启关闭状态功能
	 * @author: 顾冬冬
	 * @return void
	 */
	private void getSwitchStatus() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "vas/cailaiStatus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/**
	 * @Title: clSwitch
	 * @Description:财来网推广doc
	 * @author: 顾冬冬
	 * @return void
	 */
	private void getVasCaiLaiDoc() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "vas/doc");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		cl_switch.setEnabled(true);
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			if (!Utility.isEmpty(sname) && ("vas/cailaiStatus".equals(sname) || "vas/open".equals(sname))) {
				try {
					int isOpen = result.getInt("isOpen");
					clIsOpen = isOpen == 1;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.msg = new Message();
				this.msg.what = ISOPEN_STATUS_SUCCESS;
				mHandler.sendMessage(this.msg);
			}
			/*else if (!Utility.isEmpty(sname) && "vas/doc".equals(sname)) {
				JSONArray doc = result.optJSONArray("doc");
				String strDoc = "";
				try {
					for (int i = 0; i < doc.length(); i++) {
						JSONObject jObject = (JSONObject) doc.get(i);
						strDoc = strDoc + jObject.toString();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (!Utility.isEmpty(strDoc)) {
					caiLaiTitle.setVisibility(View.VISIBLE);
					caiLaiDesc.setVisibility(View.VISIBLE);
					caiLaiDesc.setText(strDoc);
				}
			}*/
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (!Utility.isEmpty(result)) {
			UtilToolkit.showToast(result);
		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.cl_switch:
			int switchCode = 0;
			if (clIsOpen == true) {
				switchCode = 0;// 关闭
			} else {
				switchCode = 1;// 打开
			}
			clSwitch(switchCode);
			break;
		default:
			break;
		}
	}

}
