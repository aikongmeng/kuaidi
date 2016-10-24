package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.E3ProofAddImgAdapter;
import com.kuaibao.skuaidi.activity.adapter.E3ProofAddImgAdapter.ViewHolder;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 举证上传照片界面
 * 
 * @author wq
 * 
 */
public class E3ProofAddImgActivity extends SkuaiDiBaseActivity {

	private static final int MAX_COUNT = 3;

	private Context context;

	private ImageView iv_title_back;// back
	private TextView tv_title_des, tv_delete_confir;// title
	private Button bt_title_more;// commit|
	private GridView add_image;

	private GetPhotoTypePop mPopupWindow;
	private static final int REQUEST_CODE = 3;

	public static E3ProofAddImgAdapter adapter;
	private String imagePath = "";// 图片路径
	private RelativeLayout rl_bottom_btn;// 删除确认部分
	private String action = "";
	public ArrayList<CheckableImage> checkableImageList;
	Button btn_middle;
	private boolean canCommit = false;
	public static ArrayList<String> drr = new ArrayList<String>();
	public static ArrayList<Activity> activityList = new ArrayList<Activity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_e3_proof_image_addimg);
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
		rl_bottom_btn = (RelativeLayout) findViewById(R.id.rl_bottom_btn);
		tv_delete_confir = (TextView) findViewById(R.id.tv_delete_confir);
		bt_title_more.setVisibility(View.VISIBLE);
		add_image = (GridView) findViewById(R.id.add_image);
		rl_bottom_btn.setOnClickListener(clickListener);
		iv_title_back.setOnClickListener(clickListener);
		bt_title_more.setOnClickListener(clickListener);
		if ("E3ProofActivity_view".equals(getIntent().getStringExtra("from"))) {// 查看图证

			drr = getIntent().getStringArrayListExtra("drr");
			checkableImageList = CheckableImageList(drr);
			adapter = new E3ProofAddImgAdapter(context, checkableImageList, "view", 3);
			tv_title_des.setText("图证");
			bt_title_more.setText("编辑");
			action = "edit";
			bt_title_more.setVisibility(View.VISIBLE);
		} else {
			if (getIntent().getStringArrayListExtra("drr") != null) {// 添加图证
				drr = getIntent().getStringArrayListExtra("drr");
				checkableImageList = CheckableImageList(drr);
				adapter = new E3ProofAddImgAdapter(context, checkableImageList, "add", 3);// 图证最多3张
				// E3ProofAddImgActivity.adapter.loadLocBitmap(getIntent().getStringArrayListExtra("drr"));
				bt_title_more.setVisibility(View.VISIBLE);
				tv_title_des.setText("图证");
				bt_title_more.setText("提交");
			}
		}

		add_image.setAdapter(adapter);
		add_image.setVisibility(View.VISIBLE);

		add_image.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ViewHolder holder = (ViewHolder) view.getTag();

				if ("提交".equals(bt_title_more.getText()) || "编辑".equals(bt_title_more.getText())) {// 首次进入，添加照片
					if (position == checkableImageList.size()) {
						mPopupWindow = new GetPhotoTypePop(E3ProofAddImgActivity.this, "", clickListener);
						mPopupWindow.showAtLocation(findViewById(R.id.add_image), Gravity.CENTER_VERTICAL, 0, 0);
					} else {
						Intent intent = new Intent(E3ProofAddImgActivity.this, E3ProofPhotoShowActivity.class);
						intent.putExtra("drr", drr);
						intent.putExtra("pic_index", position);
						intent.putExtra("from", "E3ProofAddImgActivity_itemClick");
						startActivity(intent);
					}
				} else if ("取消".equals(bt_title_more.getText())) {// 如果是编辑状态下
					// 判断是否选中图片
					if (checkableImageList.get(position).isSelected) {
						checkableImageList.get(position).setSelected(false);
						// holder.iv_checked_status.setImageResource(R.drawable.batch_add_checked);//
						// 如果选中的话设置选中图片
					} else {
						checkableImageList.get(position).setSelected(true);
						// holder.iv_checked_status.setImageResource(-1);//
						// 如果没选中的话取消设置图片
					}
					adapter.notifyDataSetChanged();
					if (adapter.getSelextedCount() > 0) {
						rl_bottom_btn.setEnabled(true);
						tv_delete_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);
					} else {
						rl_bottom_btn.setEnabled(false);
						tv_delete_confir.setBackgroundResource(R.drawable.shape_btn_gray1);
					}

				}
			}
		});
	}

	private String fileName;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_ADD_PHOTO) {
			if (resultCode == RESULT_OK) {
				List<String> photoImagePath = SkuaidiSpf.getPhotoImagePath(context);
				if (photoImagePath != null) {
					drr = (ArrayList<String>) photoImagePath;
				}
				String imagePath = drr.get(drr.size() - 1);
				drr.remove(drr.get(drr.size() - 1));
				// //
				// BitmapUtil.getDrr(context).remove((BitmapUtil.getDrr(context).size()
				// // - 1));
				Bitmap bitmap = null;
				if (Utility.getImage(imagePath, 800f, 480f, 40) != null) {
					bitmap = Utility.getImage(imagePath, 800f, 480f, 40);//
					// 压缩图片获取图片bitmap
					int degree = BitmapUtil.readPictureDegree(imagePath);
					bitmap = BitmapUtil.rotaingImageView(degree, bitmap);
					fileName = AlbumFileUtils.SavePicInLocal(bitmap);
				}
				drr.add(fileName);
				// }
				// List<String> photoImageID =
				// SkuaidiSpf.getPhotoImageID(context);
				// if (photoImageID != null) {
				// BitmapUtil.setImgId(context, photoImageID);
				// }
				// String imageID =
				// BitmapUtil.getImgId(context).get(BitmapUtil.getImgId(context).size()
				// - 1);
				// BitmapUtil.getImgId(context).remove(BitmapUtil.getImgId(context).size()
				// - 1);

				if (drr.size() <= 3) {
					// BitmapUtil.getDrr(context).add(fileName);
					// BitmapUtil.getImgId(context).add(imageID);
					// BitmapUtil.getBmp(context).add(bitmap);
					checkableImageList = CheckableImageList(drr);
					adapter.notifyDataSetChanged();
				} else {
					UtilToolkit.showToast( "最多选择9张");
				}
				adapter.notifyDataSetChanged();
			}
		} else if (requestCode == E3ProofAddImgActivity.REQUEST_CODE
				&& resultCode == E3ProofPhotoShowActivity.RESULT_CODE) {
			BitmapUtil.getBmp(context).clear();
			E3ProofAddImgActivity.adapter.loadLocBitmap(data.getStringArrayListExtra("drr"));
			adapter.notifyDataSetChanged();

		} else if (requestCode == E3ProofAddImgActivity.REQUEST_CODE) {
			drr.addAll(getIntent().getStringArrayListExtra("drr"));
			checkableImageList = CheckableImageList(drr);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 按钮的点击事件
	 */
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:// 返回

				if ("E3ProofActivity".equals(getIntent().getStringExtra("from"))) {
					if (drr.size() != 0 && "提交".equals(bt_title_more.getText())) {

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
								BitmapUtil.getDrr(BitmapUtil.getFromContext()).clear();
								BitmapUtil.getBmp(BitmapUtil.getFromContext()).clear();
								BitmapUtil.setMax(BitmapUtil.getFromContext(), 0);
								AlbumFileUtils.deleteDir();

								BitmapUtil.getDrr(context).clear();
								BitmapUtil.getBmp(context).clear();
								BitmapUtil.setMax(context, 0);
								drr.clear();
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
						if ("编辑".equals(bt_title_more.getText())) {
							BitmapUtil.getDrr(BitmapUtil.getFromContext()).clear();
							BitmapUtil.getBmp(BitmapUtil.getFromContext()).clear();
							BitmapUtil.setMax(BitmapUtil.getFromContext(), 0);

							BitmapUtil.getDrr(context).clear();
							BitmapUtil.getBmp(context).clear();
							BitmapUtil.setMax(context, 0);

							BitmapUtil.setDrr(BitmapUtil.getFromContext(), drr);

						}
						checkableImageList.clear();
						drr.clear();
						if (E3ProofActivity.activityList.size() > 0) {
							for (Activity activity : E3ProofActivity.activityList) {
								activity.finish();
							}
							E3ProofActivity.activityList.clear();
						}
						finish();

					}

				} else {

					if ("编辑".equals(bt_title_more.getText())) {
						BitmapUtil.getDrr(BitmapUtil.getFromContext()).clear();
						BitmapUtil.getBmp(BitmapUtil.getFromContext()).clear();
						BitmapUtil.setMax(BitmapUtil.getFromContext(), 0);
						ArrayList<String> mDrr = drr;
						BitmapUtil.setDrr(BitmapUtil.getFromContext(), mDrr);
						BitmapUtil.getDrr(context).clear();
						BitmapUtil.getBmp(context).clear();
						BitmapUtil.setMax(context, 0);

						if (E3ProofActivity.activityList.size() > 0) {
							for (Activity activity : E3ProofActivity.activityList) {
								activity.finish();
							}
							E3ProofActivity.activityList.clear();
						}
						finish();
						checkableImageList.clear();

					} else {

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
								BitmapUtil.getDrr(BitmapUtil.getFromContext()).clear();
								BitmapUtil.getBmp(BitmapUtil.getFromContext()).clear();
								BitmapUtil.setMax(BitmapUtil.getFromContext(), 0);
								AlbumFileUtils.deleteDir();

								BitmapUtil.getDrr(context).clear();
								BitmapUtil.getBmp(context).clear();
								BitmapUtil.setMax(context, 0);
								drr.clear();

								if (E3ProofActivity.activityList.size() > 0) {
									for (Activity activity : E3ProofActivity.activityList) {
										activity.finish();
									}
									E3ProofActivity.activityList.clear();
								}
								finish();
								checkableImageList.clear();
							}
						});

					}

				}
				break;
			case R.id.bt_title_more:// 提交或者编辑

				if ("提交".equals(bt_title_more.getText())) {
					if (BitmapUtil.getFromContext() != null) {
						if (E3ProofActivity.class.equals(BitmapUtil.getFromContext().getClass())) {
							BitmapUtil.setDrr(BitmapUtil.getFromContext(), drr);
							((E3ProofActivity) BitmapUtil.getFromContext()).loadLocBitmap(getIntent()
									.getStringArrayListExtra("drr"));
						}
					}

					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getBmp(context).clear();
					if (E3ProofActivity.activityList.size() > 0) {
						for (Activity activity : E3ProofActivity.activityList) {
							activity.finish();
						}
						E3ProofActivity.activityList.clear();
					}
					for (Activity activity : E3ProofAddImgActivity.activityList) {
						activity.finish();
					}
					E3ProofAddImgActivity.activityList.clear();
					finish();
				} else if ("编辑".equals(bt_title_more.getText())) {

					mPopupWindow = new GetPhotoTypePop(context, "editPirce", clickListener);
					mPopupWindow.showAtLocation(findViewById(R.id.bt_title_more), Gravity.CENTER_VERTICAL, 0, 0);
				} else if ("取消".equals(bt_title_more.getText())) {
					tv_title_des.setText("图证");
					bt_title_more.setText("编辑");
					rl_bottom_btn.setVisibility(View.GONE);
					// add_image.setVisibility(View.VISIBLE);
					mPopupWindow.dismiss();

				}

				break;
			case R.id.btn_paizhao:// 选择拍照
				adapter.setFrom("add");
				adapter.notifyDataSetChanged();
				if ("拍照".equals(mPopupWindow.btn_paizhao.getText())) {
					SkuaidiSpf.saveAddGoodsName(context, "IMG" + Utility.getSMSCurTime());// 将照片的名字保存到SP中（支持一些特定手机使用这种情况）
					String status = Environment.getExternalStorageState();//
					// 验证手机是否存在SD卡
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
						set.addAll(drr);
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
						canCommit = true;
					} else {
						UtilToolkit.showToast("未找到存储卡");
					}
				} else if ("上传照片".equals(mPopupWindow.btn_paizhao.getText())) {
					if (checkableImageList.size() >= MAX_COUNT) {
						UtilToolkit.showToast("最多添加" + MAX_COUNT + "张照片");

					}

				}
				// Intent intent = new Intent(getApplicationContext(),
				// E3AddImgActivity.class);
				// startActivityForResult(intent, REQUEST_CODE);
				// mPopupWindow.dismiss();
				mPopupWindow.dismiss();
				break;

			case R.id.btn_xiangce:// 删除

				if ("从相册选取".equals(mPopupWindow.btn_xiangce.getText())) {
					add_image.setVisibility(View.VISIBLE);
					Intent mIntent = new Intent(E3ProofAddImgActivity.this, E3AlbumSystemActivity.class);
					mIntent.putExtra("from", "E3ProofAddImgActivity");
					// BitmapUtil.setFromContext(context);
					startActivity(mIntent);
					canCommit = true;
					mPopupWindow.dismiss();
				} else {
					if (checkableImageList.size() > 0) {
						tv_title_des.setText("删除图证");
						bt_title_more.setText("取消");
						rl_bottom_btn.setVisibility(View.VISIBLE);
						add_image.setVisibility(View.VISIBLE);

					}
					mPopupWindow.dismiss();
				}
				break;

			case R.id.btn_cancel:// 取消
				mPopupWindow.dismiss();
				break;
			case R.id.rl_bottom_btn:// 确认删除
				ArrayList<CheckableImage> mList = new ArrayList<CheckableImage>();
				for (int i = 0; i < checkableImageList.size(); i++) {
					if (checkableImageList.get(i).isSelected()) {
						// BitmapUtil.getBmp(BitmapUtil.getFromContext()).remove(checkableImageList.get(i).getBitmap());
						BitmapUtil.getDrr(BitmapUtil.getFromContext()).remove(checkableImageList.get(i).getDrr());
						mList.add(checkableImageList.get(i));
						drr.remove(checkableImageList.get(i).getDrr());
					}
				}
				checkableImageList.removeAll(mList);
				adapter.notifyDataSetChanged();
				tv_title_des.setText("图证");
				bt_title_more.setText("编辑");
				rl_bottom_btn.setVisibility(View.GONE);
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
		if (canCommit) {
			bt_title_more.setText("提交");
		}
		checkableImageList = CheckableImageList(drr);
		adapter.notifyDatas(checkableImageList);
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

			if ("E3ProofActivity".equals(getIntent().getStringExtra("from"))) {
				if (BitmapUtil.getDrr(BitmapUtil.getFromContext()).size() != 0// 可能异常
						&& BitmapUtil.getBmp(BitmapUtil.getFromContext()).size() != 0) {

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
							BitmapUtil.getDrr(BitmapUtil.getFromContext()).clear();
							BitmapUtil.getBmp(BitmapUtil.getFromContext()).clear();
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
					if (E3ProofActivity.activityList.size() > 0) {
						for (Activity activity : E3ProofActivity.activityList) {
							activity.finish();
						}
						E3ProofActivity.activityList.clear();
					}
					finish();
				}
			}

		}
		return true;
	}

	// interface to request success
	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	public void reelect(View view) {
		// BitmapUtil.getDrr(BitmapUtil.getFromContext()).clear();
		// BitmapUtil.getBmp(BitmapUtil.getFromContext()).clear();
		BitmapUtil.getDrr(context).clear();
		BitmapUtil.getBmp(context).clear();
		Intent intent = new Intent(context, E3AlbumSystemActivity.class);
		startActivity(intent);
		finish();

	}

	private ArrayList<CheckableImage> CheckableImageList(ArrayList<String> drrs) {
		ArrayList<CheckableImage> checkableImageList = new ArrayList<CheckableImage>();
		if (drrs == null)
			return checkableImageList;
		for (int i = 0; i < drrs.size(); i++) {
			try {
				CheckableImage image = new CheckableImage();
				image.setBitmap(BitmapUtil.revitionImageSize(drrs.get(i)));
				image.setDrr(drrs.get(i));
				image.setSelected(false);
				checkableImageList.add(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return checkableImageList;
	}

	public class CheckableImage {
		private String drr;
		private boolean isSelected;
		private Bitmap bitmap;

		public String getDrr() {
			return drr;
		}

		public void setDrr(String drr) {
			this.drr = drr;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

	}

}
