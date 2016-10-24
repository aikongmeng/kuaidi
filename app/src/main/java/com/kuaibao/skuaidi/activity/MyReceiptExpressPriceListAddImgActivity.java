package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.MyReceiptExpressPriceAddImgAdapter;
import com.kuaibao.skuaidi.activity.view.GetPhotoTypePop;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.util.AlbumFileUtils;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的收件价格单上传照片界面
 * @author 顾冬冬
 * 
 */
public class MyReceiptExpressPriceListAddImgActivity extends SkuaiDiBaseActivity {

	private Context context;

	private ImageView iv_title_back;// back
	private TextView tv_title_des;// title
	private Button bt_title_more;// commit
	private GridView add_image;

	private GetPhotoTypePop mPopupWindow;

	public static MyReceiptExpressPriceAddImgAdapter adapter;
	private String imagePath = "";// 图片路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.my_receipt_express_price_list_addimg_activity);
		context = this;
		findView();
	}

	/**
	 * 查找控件
	 */
	private void findView() {
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		bt_title_more = (Button) findViewById(R.id.bt_title_more);
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setText("发送");
		add_image = (GridView) findViewById(R.id.add_image);
		tv_title_des.setText("我的收件价格单");

		iv_title_back.setOnClickListener(clickListener);
		bt_title_more.setOnClickListener(clickListener);

		add_image.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == BitmapUtil.getBmp(context).size()) {
					mPopupWindow = new GetPhotoTypePop(MyReceiptExpressPriceListAddImgActivity.this,"",clickListener);
					mPopupWindow.showAtLocation(findViewById(R.id.add_image),Gravity.CENTER_VERTICAL, 0, 0);
				} else {
					Intent intent = new Intent(MyReceiptExpressPriceListAddImgActivity.this,PhotoShowActivity.class);
					intent.putExtra("ID", position);
					BitmapUtil.setFromContext(context);
					startActivity(intent);
				}
			}
		});

		adapter = new MyReceiptExpressPriceAddImgAdapter(context);
		add_image.setAdapter(adapter);
		add_image.setVisibility(View.VISIBLE);
	}

	private String fileName;
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == Constants.REQUEST_ADD_PHOTO) {
				if (resultCode == RESULT_OK) {
					/******** photoImagePath ********/
					List<String> photoImagePath = SkuaidiSpf.getPhotoImagePath(context);
					if (photoImagePath != null) {
						BitmapUtil.setDrr(context, photoImagePath);
						// 路径放入BitmapUtil.getDrr(context)中
					}
					String imagePath = BitmapUtil.getDrr(context).get((BitmapUtil.getDrr(context).size() - 1));
					BitmapUtil.getDrr(context).remove((BitmapUtil.getDrr(context).size() - 1));
					Bitmap shopGoodsBit = null;
					if (Utility.getImage(imagePath,800f,480f,40) != null) {
						shopGoodsBit = Utility.getImage(imagePath,800f,480f,40);// 压缩图片获取图片bitmap
						int degree = BitmapUtil.readPictureDegree(imagePath);
						shopGoodsBit = BitmapUtil.rotaingImageView(degree,shopGoodsBit);
						fileName = AlbumFileUtils.SavePicInLocal(shopGoodsBit);
					}
					/******** photoImageID ********/
					List<String> photoImageID = SkuaidiSpf.getPhotoImageID(context);
					if (photoImageID != null) {
						BitmapUtil.setImgId(context, photoImageID);
						// BitmapUtil.getImgId(context) = photoImageID;//
						// 图片ID放到BitmapUtil.getImgId(context)中
					}
					String imageID = BitmapUtil.getImgId(context).get(BitmapUtil.getImgId(context).size() - 1);
					BitmapUtil.getImgId(context).remove(BitmapUtil.getImgId(context).size() - 1);

					if (BitmapUtil.getDrr(context).size() < 9) {
						BitmapUtil.getDrr(context).add(fileName);
						BitmapUtil.getImgId(context).add(imageID);
						BitmapUtil.getBmp(context).add(shopGoodsBit);
						adapter.notifyDataSetChanged();
					} else {
						UtilToolkit.showToast("最多选择9张");
					}
				}
			}
	}

	

	/**
	 * 按钮的点击事件
	 */
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// back click event
			case R.id.iv_title_back://
				
				if ((BitmapUtil.getDrr(context).size() != 0 && BitmapUtil.getBmp(context).size() != 0)) {
					SkuaidiDialog dialog = new SkuaidiDialog(context);
					dialog.setTitle("提示");
					dialog.setContent("您要取消编辑吗？");
					dialog.isUseEditText(false);
					dialog.setPositionButtonTitle("取消");
					dialog.setNegativeButtonTitle("确认");
					dialog.showDialog();
					dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {
						
						@Override
						public void onClick() {
							BitmapUtil.getDrr(context).clear();
							BitmapUtil.getBmp(context).clear();
							BitmapUtil.setMax(context, 0);
							AlbumFileUtils.deleteDir();
							MyReceiptExpressPriceListAddImgActivity.this.finish();
						}
					});
				} else {
					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getBmp(context).clear();
					BitmapUtil.setMax(context, 0);
					AlbumFileUtils.deleteDir();
					MyReceiptExpressPriceListAddImgActivity.this.finish();
				}
				break;
			case R.id.bt_title_more:
				if(Utility.isNetworkConnected() == true){
					if (BitmapUtil.getBmp(context).size() != 0) {
						String[] bitmapArr;
						bitmapArr = new String[BitmapUtil.getBmp(context).size()];
						for (int i = 0; i < BitmapUtil.getBmp(context).size(); i++) {
							if (!bitMapToString(i).equals("")) {
								bitmapArr[i] = bitMapToString(i);
							}
						}
						submitImg(bitmapArr);
						showProgressDialog( "正在上传，请稍候...");
					}else{
						UtilToolkit.showToast("请设置网络...");
					}
				}
				break;
			// ******************************* pop对话框上按钮************************************//
			case R.id.btn_paizhao:// 选择拍照
				SkuaidiSpf.saveAddGoodsName(context,"IMG" + Utility.getSMSCurTime());// 将照片的名字保存到SP中（支持一些特定手机使用这种情况）
				String status = Environment.getExternalStorageState();// 验证手机是否存在SD卡
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					String filePath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/";// 设置图片存放目录
					File file = new File(filePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					File imageFile = new File(file, "IMG"+ Utility.getSMSCurTime() + ".jpg");
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

			case R.id.btn_xiangce:// 选择相册
				add_image.setVisibility(View.VISIBLE);
				Intent mIntent = new Intent(MyReceiptExpressPriceListAddImgActivity.this,AlbumSystemActivity.class);
				BitmapUtil.setFromContext(context);
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
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		adapter.notifyDataSetChanged();
	}
	
	public String bitMapToString(int i) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitmapUtil.getBmp(context).get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		try {
			return Base64.encodeToString(b, Base64.NO_WRAP);
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((BitmapUtil.getDrr(context).size() != 0 && BitmapUtil.getBmp(context).size() != 0)) {

				SkuaidiDialog dialog = new SkuaidiDialog(context);
				dialog.setTitle("提示");
				dialog.setContent("您要取消编辑吗？");
				dialog.setPositionButtonTitle("取消");
				dialog.setNegativeButtonTitle("确认");
				dialog.isUseEditText(false);
				dialog.setNegativeClickListener(new NegativeButtonOnclickListener() {

					@Override
					public void onClick() {
						BitmapUtil.getDrr(context).clear();
						BitmapUtil.getBmp(context).clear();
						BitmapUtil.setMax(context, 0);
						AlbumFileUtils.deleteDir();
						finish();
					}
				});
				dialog.showDialog();
			} else {
				BitmapUtil.getDrr(context).clear();
				BitmapUtil.getBmp(context).clear();
				BitmapUtil.setMax(context, 0);
				AlbumFileUtils.deleteDir();
				finish();
			}
		}
		return true;
	}
	
	// 调用接口-上传照片
	private void submitImg(String[] picArr){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "cm.price.pic");
			data.put("pname", "androids");
			data.put("act", "add");
			data.put("arrPic",Utility.Object2String(picArr));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
	}

	// interface to request success
	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(result!=null){
			if(sname.equals("cm.price.pic")){
				if(act.equals("add")){
					try {
						String retStr = result.getString("retStr");
						UtilToolkit.showToast(retStr);
						dismissProgressDialog();
						
						BitmapUtil.getDrr(context).clear();
						BitmapUtil.getBmp(context).clear();
						BitmapUtil.setMax(context, 0);
						AlbumFileUtils.deleteDir();
						MyReceiptExpressPriceListAddImgActivity.this.finish();
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if(!result.equals("")){
			UtilToolkit.showToast(result);
			dismissProgressDialog();
		}

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if(code.equals("7") && null != result){
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
