package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiButton;
import com.kuaibao.skuaidi.entry.LatticePoint;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 超派查询-添加评论界面
 * 
 * @author a21
 * 
 */
public class AddCommentActivity extends SkuaiDiBaseActivity {

	private static final String SNAME = "shop_comment";
	private static final String ACT = "comment_add";
	private static final int MAX_CONTENT_LENGTH = 180;
	private EditText et_comment;
	private TextView tv_hint;
	private Context context;
	private SkuaidiButton btn_add_comment;

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (SNAME.equals(sname)) {
			if (result != null)
				UtilToolkit.showToast( result.optString("retStr"));
			finish();

		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (SNAME.equals(sname)) {
			UtilToolkit.showToast( result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_add_comment);
		context = this;
		getContral();

	}

	private void getContral() {

		TextView title_des = (TextView) findViewById(R.id.tv_title_des);
		title_des.setText("评论");
		btn_add_comment = (SkuaidiButton) findViewById(R.id.btn_add_comment);

		et_comment = (EditText) findViewById(R.id.et_comment);
		tv_hint = (TextView) findViewById(R.id.tv_hint);

		et_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				int length = s.toString().trim().length();
				if (length <= MAX_CONTENT_LENGTH) {
					tv_hint.setText("还能输入" + (MAX_CONTENT_LENGTH - length) + "个字");
				}
				if (length == 0) {
					btn_add_comment.setEnabled(false);
					// btn_add_comment.setBackgroundResource(R.drawable.shape_btn_gray1);
				} else {
					btn_add_comment.setEnabled(true);
					// TypedArray a =
					// context.obtainStyledAttributes(btn_add_comment.attrs,
					// R.styleable.custom_atts);
					// boolean isClickedChangeBackground =
					// a.getBoolean(R.styleable.custom_atts_isClickedChangeBackground,
					// false);
					// int coner = a.getInt(R.styleable.custom_atts_coners,
					// SkuaidiSkinManager.CONERS_NULL);
					// int frames = a.getInt(R.styleable.custom_atts_frames,
					// SkuaidiSkinManager.FRAME_NULL);
					// String fileName =
					// a.getString(R.styleable.custom_atts_clickedChangeBackgrounOfcus);
					// a.recycle();
					// SkuaidiSkinManager.skinColorChange(btn_add_comment,
					// coner, frames, fileName,
					// isClickedChangeBackground);
				}

			}
		});

	}

	public void commit(View view) {
		UMShareManager.onEvent(context, "comment_commit", "add_comment", "超派：提交评论");
		String content = et_comment.getText().toString().trim();
		LatticePoint latticePoint = (LatticePoint) getIntent().getSerializableExtra("latticePoint");
		JSONObject json = new JSONObject();
		try {
			json.put("sname", SNAME);
			json.put("act", ACT);
			json.put("index_shop_id", latticePoint.getIndex_shop_id());
			json.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);

	}

	public void back(View view) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back(null);
		}

		return super.onKeyDown(keyCode, event);
	}

}
