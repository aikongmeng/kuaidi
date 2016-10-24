package com.kuaibao.skuaidi.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.LiuyanDetailAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.MessageDetail;
import com.kuaibao.skuaidi.entry.MessageDetail.Attach;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.service.AudioOnLinePlayService;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wq 留言详情
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LiuyanDetailActivity extends SkuaiDiBaseActivity implements OnClickListener {
	private static final String SNAME_MCG_DETAIL = "liuyan.reply_query";
	private static final String SNAME_MCG_ADD = "liuyan.reply_add";
	private Context context;

	private ListView listview;
	private TextView tv_orderinfo_title;
	private ImageButton imb_input_voice;
	private EditText tv_content_txt;
	private Button imb_send;
	private TextView btn_record_voice;
	private SkuaidiImageView iv_title_back;// 返回按钮

	private TextView tv_title_order;
	private TextView tv_title_phone;
	private Button btn_FlowExpressNo;
	private ImageView iv_call, iv_pic_upload;
	private RelativeLayout rl_bottom;

	private LiuyanDetailAdapter adapter;

	private String mix_content;

	private String attachs[];
	private ArrayList<MessageDetail> messages = new ArrayList<MessageDetail>();
	private String push;

	private View line, view_view;

	private RelativeLayout ll_order_no;
	private RelativeLayout ll_phone_no;
	private String id;
	private String type;
	private String is_reply;
	private String user_phone;
	private String waybill_no;
	private String expressNo;
	private int page_num = 1;
	private String last_reply;
	private String post_time;
	private long post_timestramp;
	private String post_username;
	private boolean requesting;
	private ArrayList<String> imgUrls = new ArrayList<String>();

	private RelativeLayout rl_phone_order;

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		KLog.json(json);
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (result == null) {
			requesting = false;
			return;
		}
		if (SNAME_MCG_ADD.equals(sname)) {
			last_reply = result.optString("content");
			post_time = result.optString("post_time");
			MessageDetail msd = new MessageDetail();
			msd.setContent(result.optString("content"));
			msd.setPost_time(post_time);
			msd.setT("to");
			if(!Utility.isEmpty(result.optJSONObject("attach")) && !Utility.isEmpty(result.optJSONObject("attach").optJSONArray("img_arr"))
					&& result.optJSONObject("attach").optJSONArray("img_arr").length() > 0){
				for(int i = 0; i < result.optJSONObject("attach").optJSONArray("img_arr").length(); i++){
					try {
						MessageDetail msgd = new MessageDetail();
						msgd.setContent("");
						if(i == 0){
							msgd.setPost_time(post_time);
						}
						msgd.setT("to");
						msgd.setContent_type(2);
						String url = (String) result.optJSONObject("attach").optJSONArray("img_arr").get(i);
						Attach attach = msgd.new Attach();
						attach.setType("image");
						attach.setUrl(url);
						msgd.setAttach(attach);
						messages.add(msgd);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				messages.add(msd);
			}
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			listview.setSelection(messages.size()-1);
			requesting = false;

		} else if (SNAME_MCG_DETAIL.equals(sname)) {
			JSONArray array = result.optJSONArray("list");
			JSONObject topic = result.optJSONObject("topic");
			if (topic != null) {
				mix_content = topic.optString("mix_content");
				waybill_no = topic.optString("waybill_no");
				user_phone = topic.optString("user_phone");
				JSONArray ats = topic.optJSONArray("attachs");
				if(!Utility.isEmpty(ats)) {
					String[] strs = new String[ats.length()];
					for(int i = 0; i <ats.length(); i++){
						strs[i] = ats.optString(i);
					}
					attachs = strs;
				}

				post_timestramp = topic.optLong("post_timestramp", 0l);
				post_username = topic.optString("post_username");
				dataToView();
			}
			if (array != null) {
				ArrayList<MessageDetail> details = parseMsgDetailFromJson(array.toString());
				// MessageDetail ms= new MessageDetail();
				// ms.setContent_type(2);
				// ms.setContent("/storage/emulated/0/skuaidi/call_recording/13127966212_1446435485494.amr");
				messages.addAll(details);
				adapter.notifyDataSetChanged(messages);
			}

		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		requesting = false;
		UtilToolkit.showToast( result);
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.list_exception_sameorder);
		context = this;
		id = getIntent().getStringExtra("m_id");
		type = getIntent().getStringExtra("m_type");
		push = getIntent().getStringExtra("push");
		waybill_no = getIntent().getStringExtra("waybill_no");
		user_phone = getIntent().getStringExtra("user_phone");
		mix_content = getIntent().getStringExtra("mix_content");
		attachs = getIntent().getStringArrayExtra("attachs");
		post_timestramp = getIntent().getLongExtra("post_timestramp", 0l);
		post_username = getIntent().getStringExtra("post_username");
		is_reply = getIntent().getStringExtra("is_reply");
		expressNo = SkuaidiSpf.getLoginUser().getExpressNo();
		getControl();
		getMessageDetail();
		initPopupWindow();
		Intent intent=new Intent(context, AudioOnLinePlayService.class);
		context.startService(intent);
	}

	private void getControl() {
		ll_order_no = (RelativeLayout) findViewById(R.id.ll_order_no);
		ll_phone_no = (RelativeLayout) findViewById(R.id.ll_phone_no);
		rl_phone_order = (RelativeLayout) findViewById(R.id.rl_phone_order);
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		iv_title_back.setOnClickListener(this);
		btn_record_voice = (TextView) findViewById(R.id.btn_record_voice);
		tv_content_txt = (EditText) findViewById(R.id.tv_content_txt);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		view_view = findViewById(R.id.view_view);
		imb_send = (Button) findViewById(R.id.imb_send);
		imb_input_voice = (ImageButton) findViewById(R.id.imb_input_voice);
		iv_pic_upload = (ImageView) findViewById(R.id.iv_pic_upload);
		if("zt".equals(expressNo) && "1".equals(is_reply)){
			view_view.setVisibility(View.GONE);
			rl_bottom.setVisibility(View.GONE);
		}else if("sto".equals(expressNo) && "branch".equals(getIntent().getStringExtra("m_type"))){
			iv_pic_upload.setVisibility(View.VISIBLE);
			iv_pic_upload.setOnClickListener(this);
		}else{
			iv_pic_upload.setVisibility(View.GONE);
		}
		//tv_content_txt.setLongClickable(false);
		tv_content_txt.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

		tv_orderinfo_title = (TextView) findViewById(R.id.tv_title_des);
		if("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
			btn_record_voice.setBackgroundResource(R.drawable.sto_selector_send_bt);
			imb_send.setBackgroundResource(R.drawable.sto_selector_send_bt);
		}else{
			btn_record_voice.setBackgroundResource(R.drawable.selector_send_bt);
			imb_send.setBackgroundResource(R.drawable.selector_send_bt);
		}
		btn_record_voice.setTextColor(getResources().getColorStateList(
				SkuaidiSkinManager.getSkinResId("selector_green_white")));
		imb_send.setTextColor(getResources().getColorStateList(SkuaidiSkinManager.getSkinResId("selector_green_white")));
		imb_send.setOnClickListener(this);
		imb_input_voice.setImageResource(SkuaidiSkinManager.getSkinResId("selector_imb_yuyin"));
		imb_input_voice.setVisibility(View.GONE);
		tv_orderinfo_title.setText("留言详情");
		listview = (ListView) findViewById(R.id.lv_exception_deal);
		tv_title_order = (TextView) findViewById(R.id.tv_title_order);
		tv_title_phone = (TextView) findViewById(R.id.tv_title_phone);
		btn_FlowExpressNo = (Button) findViewById(R.id.btn_FlowExpressNo);
		line = findViewById(R.id.line);
		btn_FlowExpressNo.setOnClickListener(this);
		iv_call = (ImageView) findViewById(R.id.iv_call);
		iv_call.setOnClickListener(this);
		dataToView();
	}

	/**
	 * 
	 * 控制控件显示
	 * 
	 * */
	private void dataToView() {
		if (!TextUtils.isEmpty(mix_content)) {
			MessageDetail title = new MessageDetail();
			title.setContent(mix_content);
			String post_time_m = UtilityTime.getDateTimeByMillisecond(post_timestramp, "yyyy-MM-dd HH:mm");
			title.setPost_time(post_time_m);
			title.setPost_username(post_username);
			title.setT("from");
			Attach attach = new MessageDetail().new Attach();
			if (attachs != null && attachs.length != 0) {
				attach.setUrl(attachs[0]);
				attach.setType("pic");
			}
			title.setAttach(attach);
			messages.add(title);
		}

		if (!TextUtils.isEmpty(waybill_no)) {
			tv_title_order.setText("运单号 " + waybill_no);
			ll_order_no.setVisibility(View.VISIBLE);
		} else {
			ll_order_no.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(user_phone)) {
			tv_title_phone.setText("手机号 " + user_phone);
			ll_phone_no.setVisibility(View.VISIBLE);
		} else {
			line.setVisibility(View.GONE);
			ll_phone_no.setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(waybill_no) && TextUtils.isEmpty(user_phone)) {
			rl_phone_order.setVisibility(View.GONE);
		} else {
			if (!TextUtils.isEmpty(waybill_no) && !TextUtils.isEmpty(user_phone)) {
				line.setVisibility(View.VISIBLE);
			}
			rl_phone_order.setVisibility(View.VISIBLE);
		}
		adapter = new LiuyanDetailAdapter(messages, context);
		adapter.setMsgId(id);
		adapter.setMsgType(type);
		listview.setAdapter(adapter);
	}

	/**
	 * 获取留言详情
	 */
	private void getMessageDetail() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", SNAME_MCG_DETAIL);
			if ("push".equals(push)) {
				data.put("with_topic", 1);
			}
			data.put("m_id", id);
			data.put("m_type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);

	}

	PopupWindow popupWindow;
	ImageView pop_rec_iv;

	public void initPopupWindow() {
		View view = getLayoutInflater().inflate(R.layout.pop_recorder_layout, null);
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pop_rec_iv = (ImageView) view.findViewById(R.id.pop_rec_iv);
		timer = new Timer();
	}

	/**
	 * 根据声音的振幅来切换录音图片的效果
	 */
	MyTimer timeTask;
	Timer timer;

	class MyTimer extends TimerTask {

		@Override
		public void run() {
			Message message = new Message();
			message.what = Constants.MSG_UPDATE_RECORDER;
			// handler.sendMessage(message);

		}

	}

	private ArrayList<MessageDetail> parseMsgDetailFromJson(String jsonData) {
		Gson gson = new Gson();
		ArrayList<MessageDetail> list = new ArrayList<MessageDetail>();
		ArrayList<MessageDetail> devideMsg = new ArrayList<MessageDetail>();
		try {
			list = gson.fromJson(jsonData, new TypeToken<List<MessageDetail>>() {
			}.getType());

			MessageDetail msgd;
			for(MessageDetail detail : list){
				if(!Utility.isEmpty(detail.getAttach()) && !Utility.isEmpty(detail.getAttach().getImg_arr())
						&& detail.getAttach().getImg_arr().length > 0){
					if(!Utility.isEmpty(detail.getContent())){
						msgd = new MessageDetail();
						msgd.setContent_type(1);
						msgd.setT(detail.getT());
						msgd.setContent(detail.getContent());
						msgd.setPost_time(detail.getPost_time());
						msgd.setPost_username(detail.getPost_username());
						devideMsg.add(msgd);
						for(int i = 0; i < detail.getAttach().getImg_arr().length; i++){
							msgd = new MessageDetail();
							msgd.setContent("");
//								if(i == 0){
//									msgd.setPost_time(detail.getPost_time());
//								}
							msgd.setPost_username(detail.getPost_username());
							msgd.setT(detail.getT());
							msgd.setContent_type(detail.getContent_type());
							Attach attach = msgd.new Attach();
							attach.setType(detail.getAttach().getType());
							attach.setUrl(detail.getAttach().getImg_arr()[i]);
							msgd.setAttach(attach);
							devideMsg.add(msgd);
						}
					}else{
						for(int i = 0; i < detail.getAttach().getImg_arr().length; i++){
							msgd = new MessageDetail();
							msgd.setContent("");
							if(i == 0){
								msgd.setPost_time(detail.getPost_time());
							}
							msgd.setT(detail.getT());
							msgd.setPost_username(detail.getPost_username());
							msgd.setContent_type(detail.getContent_type());
							Attach attach = msgd.new Attach();
							attach.setType(detail.getAttach().getType());
							attach.setUrl(detail.getAttach().getImg_arr()[i]);
							msgd.setAttach(attach);
							devideMsg.add(msgd);
						}
					}
				}else{
					devideMsg.add(detail);
				}
			}

		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return devideMsg;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imb_send:
			long timeflag = 0L;
			timeflag = System.currentTimeMillis() - timeflag;

			if (timeflag > 1000) {
				String content = tv_content_txt.getText().toString();
				content = StringUtil.transString(content);
				tv_content_txt.setText("");
				if (!content.trim().equals("")) {
					sedMessage(content, 1);
				} else {
					UtilToolkit.showToast("发送内容不得为空");
				}
			} else {
				UtilToolkit.showToast( "请勿重复发送");
			}
			break;

		case R.id.btn_FlowExpressNo:
			Intent intent = new Intent();
			intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
			intent.putExtra("express_no", expressNo);
			intent.putExtra("order_number", waybill_no);
			intent.putExtra("from", "LiuyanDetailActivity1");
			intent.setClass(context, CopyOfFindExpressResultActivity.class);
			startActivity(intent);
			break;

		case R.id.iv_call:
			// Intent mIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
			// + AllInterface.formatCall(user_phone)));
			// startActivity(mIntent);
			AcitivityTransUtil.showChooseTeleTypeDialog(LiuyanDetailActivity.this, "", user_phone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
			break;

		case R.id.iv_title_back:
			Intent intent1 = new Intent();
			intent1.putExtra("last_reply", last_reply);
			intent1.putExtra("post_time", post_time);
			setResult(100, intent1);
			if (adapter != null) {
				adapter.stopPlayRecording();
			}

			finish();
			break;
		case R.id.iv_pic_upload:
			Intent intent2 = new Intent(context, LiuyanDetailUploadPicActivity.class);
			startActivityForResult(intent2, Constants.PIC_UP_REQUESTCODE);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		imgUrls.clear();
		if(requestCode == Constants.PIC_UP_REQUESTCODE && resultCode == Constants.PIC_UP_RESULTCODE){
			imgUrls.addAll(data.getStringArrayListExtra("imgUrls"));
			sedMessage("", 2);
		}
		if(resultCode == 222 && requestCode == 333){
			String content = data.getStringExtra("messageContent");
			if(!Utility.isEmpty(content)){
				sedMessage(content, 1);
			}
		}
	}

	private void sedMessage(String content, int content_type) {
		if (requesting) {
			UtilToolkit.showToast( "留言发送中...");
			return;
		}
		String id = getIntent().getStringExtra("m_id");
		String type = getIntent().getStringExtra("m_type");
		JSONObject data = new JSONObject();
		try {
			data.put("sname", SNAME_MCG_ADD);
			data.put("m_id", id);
			data.put("m_type", type);
			data.put("content", content);
			data.put("content_type", content_type);
			data.put("voice_length", 0);
			data.put("pics", new JSONArray(imgUrls));
			imgUrls.clear();
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		requesting = true;

	}

	private void setListViewPos(int pos) {
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			listview.smoothScrollToPosition(pos);
		} else {
			listview.setSelection(pos);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent1 = new Intent();
			intent1.putExtra("last_reply", last_reply);
			intent1.putExtra("post_time", post_time);
			setResult(100, intent1);
			if (adapter != null) {
				adapter.stopPlayRecording();
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MessageEvent event = new MessageEvent(0XF3,"stop service");
		EventBus.getDefault().post(event);
		EventBus.getDefault().unregister(this);
	}
}
