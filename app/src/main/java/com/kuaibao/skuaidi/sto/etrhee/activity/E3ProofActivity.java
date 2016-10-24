package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.CallLog;
import com.kuaibao.skuaidi.activity.model.Message;
import com.kuaibao.skuaidi.activity.view.GetPhotoTypePop;
import com.kuaibao.skuaidi.asynctask.E3GetSMSTask;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.util.AlbumFileUtils;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.core.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择单号举证页面
 * 
 * @author wq
 * 
 */
public class E3ProofActivity extends SkuaiDiBaseActivity implements OnClickListener {
	private static final int REQUEST_CODE_AUDIO = 2;
	private static final int REQUEST_CODE_SMS = 3;
	private Context context;
	private GetPhotoTypePop mPopupWindow;
	private String imagePath = "";// 图片路径
	private String fileName;
	private TextView tv_count, tv_count_audio, tv_count_sms;
	private Button btn_upload;
	public static ArrayList<Activity> activityList = new ArrayList<Activity>();
	public static final int MAX_COUNT = 3;
	private String orderNumber;
	public static ArrayList<CallLog> selectedList_audio = new ArrayList<CallLog>();
	public static ArrayList<Message> selectedList_sms = new ArrayList<Message>();
	private View view;
	private int proof_count;
	private int response_number;
	private int fail_count;
	private boolean isUpLoading = false;
	private TextView tv_title;
	private E3GetSMSTask e3GetSMSTask = null;
	private String proof_type;
	private List<String> drr_to_del = new ArrayList<String>();
	private ArrayList<CallLog> audios_to_dell=new ArrayList<CallLog>();
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x10001:
				if (null == e3GetSMSTask) {
					e3GetSMSTask.cancel(true);
				}
				dismissProgressDialog();
				@SuppressWarnings("unchecked")
				List<Message> list = (List<Message>) msg.obj;
				Intent mIntent = new Intent(context, E3ProofSMSActivity.class);
				mIntent.putExtra("from", "E3ProofActivity");

				mIntent.putExtra("message", (Serializable) list);
				startActivity(mIntent);
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onRequestSuccess(String sname, String msg, String data, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("express_proof".equals(sname)) {
			try {

				int mThreadId = Integer.parseInt(result.opt("threadId").toString());
				if (mThreadId < BitmapUtil.getDrr(context).size() && BitmapUtil.getDrr(context).size() != 0) {
					// BitmapUtil.getDrr(context).remove(mThreadId);
					drr_to_del.add(BitmapUtil.getDrr(context).get(mThreadId));
				} else {
					if (mThreadId < BitmapUtil.getDrr(context).size() + selectedList_audio.size()) {

						audios_to_dell.add(selectedList_audio.get(BitmapUtil.getDrr(context).size()
								+ selectedList_audio.size() - 1 - mThreadId));
						// selectedList_audio.remove();
					} else {
						selectedList_sms.clear();
					}
				}
				response_number++;
				if (response_number == proof_count) {

					BitmapUtil.getDrr(context).removeAll(drr_to_del);
					selectedList_audio.remove(audios_to_dell);
					SkuaidiE3SysDialog e3SysDialog;
					if (fail_count == 0) {
						e3SysDialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);
						e3SysDialog.setTitle("举证");
						e3SysDialog.setCommonContent("上传成功！");
						e3SysDialog.isUseSingleButton(true);
						e3SysDialog.setSingleButtonTitle("确定");
						if (!isFinishing())
							e3SysDialog.showDialog();
						fail_count = 0;
						response_number = 0;
						BitmapUtil.getBmp(context).clear();
						selectedList_audio.clear();
						selectedList_sms.clear();
						showDataCount();
					} else {
						e3SysDialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);
						e3SysDialog.setCommonContent(fail_count + " 个文件上传失败，请重试！");
						e3SysDialog.isUseSingleButton(true);
						e3SysDialog.setSingleButtonTitle("确定");
						e3SysDialog.showDialog();
						fail_count = 0;
						response_number = 0;
						showDataCount();
					}
					isUpLoading = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dismissProgressDialog();
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if ("express_proof".equals(sname)) {
			response_number++;
			fail_count++;
			if (response_number == proof_count) {
				SkuaidiE3SysDialog e3SysDialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);
				e3SysDialog.setTitle("举证");
				e3SysDialog.isUseSingleButton(true);
				e3SysDialog.setCommonContent(fail_count + " 个文件上传失败，请重试！");
				e3SysDialog.setSingleButtonTitle("确定");
				if (!isFinishing())
					e3SysDialog.showDialog();
				fail_count = 0;
				response_number = 0;
				showDataCount();
				isUpLoading = false;
			}

		}
dismissProgressDialog();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.activity_e3_prof);
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_count_audio = (TextView) findViewById(R.id.tv_count_audio);
		tv_count_sms = (TextView) findViewById(R.id.tv_count_sms);
		btn_upload = (Button) findViewById(R.id.btn_upload);
		NotifyInfo info = (NotifyInfo) getIntent().getSerializableExtra("NotifyInfo");
		proof_type = getIntent().getStringExtra("proof_type");
		if (info != null) {
			orderNumber = info.getExpress_number();
			tv_title.setText(orderNumber);
		}

		RelativeLayout ll_pic = (RelativeLayout) findViewById(R.id.rl_proof_pic);
		RelativeLayout ll_audio = (RelativeLayout) findViewById(R.id.rl_proof_audio);
		RelativeLayout ll_sms = (RelativeLayout) findViewById(R.id.rl_proof_sms);
		ll_pic.setOnClickListener(this);
		ll_audio.setOnClickListener(this);
		ll_sms.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_proof_pic:// 图片举证
			BitmapUtil.setFromContext(context);
			if (BitmapUtil.getDrr(context).size() > 0) {
				Intent intent = new Intent(context, E3ProofAddImgActivity.class);
				intent.putExtra("from", "E3ProofActivity_view");
				intent.putStringArrayListExtra("drr", (ArrayList<String>) BitmapUtil.getDrr(context));
				startActivity(intent);
			} else {
				mPopupWindow = new GetPhotoTypePop(context, "", itemOnClick);
				mPopupWindow.showAtLocation(findViewById(R.id.iv_title_back), Gravity.CENTER_VERTICAL, 0, 0);
			}
			break;
		case R.id.rl_proof_audio:// 录音举证
			if (selectedList_audio != null && selectedList_audio.size() != 0) {
				Intent intent = new Intent(context, E3ProofAudioViewActivity.class);
				intent.putExtra("callLogs", selectedList_audio);
				startActivityForResult(intent, REQUEST_CODE_AUDIO);
			} else {
				Intent intent = new Intent(context, E3ProofAudioActivity.class);
				startActivityForResult(intent, REQUEST_CODE_AUDIO);
			}

			break;
		case R.id.rl_proof_sms:// 短信举证
			if (selectedList_sms != null && selectedList_sms.size() != 0) {
				Intent intent = new Intent(context, E3ProofSMSDetailActivity.class);
				intent.putExtra("SMS_list", selectedList_sms);
				intent.putExtra("from", "E3ProofActivity");
				startActivityForResult(intent, REQUEST_CODE_SMS);
			} else {
				showProgressDialog( "正在加载短信，请稍候...");
				e3GetSMSTask = new E3GetSMSTask(context, handler);
				e3GetSMSTask.execute();
				// Intent mIntent = new Intent(context,
				// E3ProofSMSActivity.class);
				// startActivity(mIntent);
			}
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_AUDIO) {
			if (resultCode == E3ProofAudioActivity.RESULT_CODE) {
				selectedList_audio = (ArrayList<CallLog>) data.getSerializableExtra("callLogs");

			}
		} else if (Constants.REQUEST_ADD_PHOTO == requestCode) {
			if (resultCode == RESULT_OK) {
				List<String> photoImagePath = SkuaidiSpf.getPhotoImagePath(context);
				if (photoImagePath != null) {
					BitmapUtil.setDrr(context, photoImagePath);
				}
				String imagePath = BitmapUtil.getDrr(context).get((BitmapUtil.getDrr(context).size() - 1));
				BitmapUtil.getDrr(context).remove((BitmapUtil.getDrr(context).size() - 1));
				Bitmap shopGoodsBit = null;
				if (Utility.getImage(imagePath, 800f, 480f, 40) != null) {
					shopGoodsBit = Utility.getImage(imagePath, 800f, 480f, 40);// 压缩图片获取图片bitmap
					int degree = BitmapUtil.readPictureDegree(imagePath);
					shopGoodsBit = BitmapUtil.rotaingImageView(degree, shopGoodsBit);
					fileName = AlbumFileUtils.SavePicInLocal(shopGoodsBit);
				}
				BitmapUtil.getDrr(context).add(fileName);
				BitmapUtil.setFromContext(context);
				BitmapUtil.getBmp(context).add(shopGoodsBit);
				Intent intent = new Intent(context, E3ProofAddImgActivity.class);
				intent.putExtra("drr", new ArrayList<String>(Arrays.asList(fileName)));
				intent.putExtra("from", "E3ProofActivity");
				startActivity(intent);
			}
		}
	}

	/**
	 * This method is used to get real path of file from from uri<br/>
	 * http://stackoverflow.com/questions/11591825/how-to-get-image-path-just-
	 * captured-from-camera
	 * 
	 * @param contentUri
	 * @return String
	 */
	public String getRealPathFromURI(Uri contentUri) {
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			// Do not call Cursor.close() on a cursor obtained using this
			// method,
			// because the activity will do that for you at the appropriate time
			Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return contentUri.getPath();
		}
	}

	private OnClickListener itemOnClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_paizhao:// 选择拍照
				SkuaidiSpf.saveAddGoodsName(context, "IMG" + Utility.getSMSCurTime());// 将照片的名字保存到SP中（支持一些特定手机使用这种情况）
				String status = Environment.getExternalStorageState();// 验证手机是否存在SD卡
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					String filePath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/";// 设置图片存放目录
					File file = new File(filePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					File imageFile = new File(file, "IMG" + Utility.getSMSCurTime() + ".jpg");
					imagePath = imageFile.getPath();// 获得图片路径
					List<String> set = new ArrayList<String>();
					set.addAll(BitmapUtil.getDrr(context));
					set.add(imagePath);
					//System.out.println("gudd   set.size   " + set.size());
					SkuaidiSpf.setPhotoImagePath(context, set);
					// 设置图片的ID
					List<String> imgIDs = new ArrayList<String>();
					imgIDs.addAll(BitmapUtil.getImgId(context));
					imgIDs.add("0");// 设置手机拍照进去的图片ID都为“0”
					SkuaidiSpf.setPhotoImageID(context, imgIDs);

					Uri imageUri = Uri.fromFile(imageFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

					startActivityForResult(intent, Constants.REQUEST_ADD_PHOTO);
				} else {
					UtilToolkit.showToast("未找到存储卡");
				}
				mPopupWindow.dismiss();
				break;
			case R.id.btn_xiangce:// 选择相册-系统相册
				Intent mIntent = new Intent(E3ProofActivity.this, E3AlbumSystemActivity.class);
				mIntent.putExtra("from", "E3ProofActivity");
				startActivity(mIntent);
				mPopupWindow.dismiss();
				break;
			case R.id.btn_cancel:// 取消
				mPopupWindow.dismiss();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	public void back(View view) {
		checkBeforExit();

	}

	/**
	 * 退出前检查
	 */
	private void checkBeforExit() {
		if (BitmapUtil.getDrr(context).size() > 0 || E3ProofActivity.selectedList_audio.size() > 0
				|| E3ProofActivity.selectedList_sms.size() > 0) {

			SkuaidiDialog dialog = new SkuaidiDialog(context);
			dialog.setTitle("提示");
			dialog.setContent("您有未上传的举证数据，确定要放弃吗？");
			dialog.isUseEditText(false);
			dialog.setPositionButtonTitle("取消");
			dialog.setNegativeButtonTitle("确认");
			dialog.showDialog();
			dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

				@Override
				public void onClick() {
					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getBmp(context).clear();
					BitmapUtil.setFromContext(null);
					E3ProofActivity.selectedList_sms.clear();
					E3ProofActivity.selectedList_audio.clear();
					if (E3ProofActivity.activityList.size() > 0) {
						for (Activity activity : E3ProofActivity.activityList) {
							activity.finish();
						}
						E3ProofActivity.activityList.clear();
					}
					finish();
				}
			});

		} else {
			BitmapUtil.setFromContext(null);
			if (E3ProofActivity.activityList.size() > 0) {
				for (Activity activity : E3ProofActivity.activityList) {
					activity.finish();
				}
				E3ProofActivity.activityList.clear();
			}
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			checkBeforExit();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void loadLocBitmap(List<String> bitmaps) {
		BitmapUtil.getBmp(context).clear();
		for (int i = 0; i < bitmaps.size(); i++) {
			try {
				BitmapUtil.getBmp(context).add(BitmapUtil.revitionImageSize(bitmaps.get(i)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		showDataCount();

	}

	/**
	 * 
	 */
	private void showDataCount() {
		int count_bitmap = BitmapUtil.getDrr(context).size();
		if (count_bitmap > 0)
			tv_count.setText(count_bitmap + "个图片证据");
		else
			tv_count.setText("");

		int count_audio = selectedList_audio.size();
		if (count_audio > 0)
			tv_count_audio.setText(count_audio + "个录音证据");
		else
			tv_count_audio.setText("");

		int count_sms = selectedList_sms.size();
		if (count_sms > 0)
			tv_count_sms.setText(count_sms + "个短信证据");
		else
			tv_count_sms.setText("");

		if (count_bitmap + count_audio + count_sms == 0) {
			btn_upload.setEnabled(false);
			btn_upload.setBackgroundResource(R.drawable.shape_btn_gray1);

		} else {
			btn_upload.setEnabled(true);
			btn_upload.setBackgroundResource(R.drawable.selector_base_green_qianse1);
		}
	}

	/**
	 * 上传所有类型举证
	 * 
	 * @param view
	 */
	public void uploadAll(View view) {
		if (isUpLoading) {
			UtilToolkit.showToast("请勿重复提交！");
			return;
		}
		this.view = view;
		if (!Utility.isNetworkConnected()) {// 无网络
			UtilToolkit.showToast("请检查网络设置！");
			return;
		}

		final SkuaidiDialog dialog = new SkuaidiDialog(context);
		dialog.setTitle("提示");
		dialog.setContent("所有证据将上传到微快递网点管理中心，确认要继续上传？");
		dialog.isUseEditText(false);
		dialog.setPositionButtonTitle("取消");
		dialog.setNegativeButtonTitle("确认");
		dialog.showDialog();
		dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

			@Override
			public void onClick() {
				dialog.dismiss();
				int threadId = 0;
				// 上传图片举证
				loadLocBitmap(BitmapUtil.getDrr(context));
				for (int i = 0; i < BitmapUtil.getDrr(context).size(); i++) {
					Bitmap map = BitmapUtil.getBmp(context).get(i);
					uploadProofFile(map, null, null, "photo", threadId);
					threadId++;
				}
				// 录音
				for (int i = 0; i < selectedList_audio.size(); i++) {

					CallLog callLog;
					callLog = selectedList_audio.get(i);
					// String fileContent = new String(buffer);
					uploadProofFile(null, callLog, null, "audio", threadId);
					threadId++;

				}// 短信
				if (selectedList_sms.size() > 0) {
					uploadProofFile(null, null, selectedList_sms, "sms", threadId);
					proof_count = BitmapUtil.getDrr(context).size() + selectedList_audio.size() + 1;
				} else {
					proof_count = BitmapUtil.getDrr(context).size() + selectedList_audio.size();
				}

			}
		});

	}

	/**
	 * @param map
	 */
	private void uploadProofFile(Bitmap map, CallLog callLog, ArrayList<Message> sms, String pType, int threadId) {
		try {
			isUpLoading = true;
			NotifyInfo info = (NotifyInfo) getIntent().getSerializableExtra("NotifyInfo");
			if (info != null) {
				orderNumber = info.getExpress_number();
			}

			JSONObject data = new JSONObject();
			data.put("sname", "express_proof");
			data.put("threadId", threadId);
			data.put("no", orderNumber);
			if ("问题件".equals(proof_type)) {
				data.put("proof_type", 4);
			} else {
				data.put("proof_type", 3);
			}
			if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 申通
				data.put("company", "sto");
				data.put("ptype", pType);
				if (map != null) {
					data.put("file", Utility.bitMapToString(map));
				} else if (callLog != null) {
					data.put("file", Utility.bitMapToString(map));
					data.put("type", callLog.getCallType());
					data.put("audio_length", String.valueOf(callLog.getCallDuration()));
					data.put("createtime", String.valueOf(callLog.getCallDate()));
					data.put("receive_name", callLog.getCallerName());
					data.put("receive_phone", callLog.getPhoneNumber());
					FileInputStream fis = null;
					String fileContent = "";
					String dirPath = callLog.getCallRecordingMp3().getUrl();
					File audioFile = new File(dirPath);
					try {
						fis = new FileInputStream(audioFile.toString());
						byte[] buffer = new byte[(int) audioFile.length()];
						fis.read(buffer);
						fileContent = Base64.encodeToString(buffer, Base64.NO_WRAP);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (fis != null)
							fis.close();
					}

					data.put("file", fileContent);
				} else if (sms != null) {

					JSONArray array = new JSONArray();
					for (int i = 0; i < sms.size(); i++) {
						JSONObject json = new JSONObject();
						json.put("personName", sms.get(i).getPersonName());
						json.put("messageType", sms.get(i).getMessageType());
						json.put("messageDate", sms.get(i).getMessageDate());
						json.put("messageContent", sms.get(i).getMessageContent());
						json.put("personPhoneNumber", sms.get(i).getPersonPhoneNumber());
						array.put(i, json);
					}
					data.put("messages", array);
				}
			}
			httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
			showProgressDialog("正在上传，请稍后...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
