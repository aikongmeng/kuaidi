package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.R.color;
import com.kuaibao.skuaidi.R.drawable;
import com.kuaibao.skuaidi.activity.adapter.ShopGoodsImageAdapter;
import com.kuaibao.skuaidi.activity.view.GetPhotoTypePop;
import com.kuaibao.skuaidi.activity.view.ImageGridView;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.ShopInfo;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.AlbumFileUtils;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.ImageUtility;
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
 * 创建店铺
 * 
 * @author 顾冬冬
 * @created 2015/1/12
 */
@SuppressLint("SimpleDateFormat")
public class AddShopActivity extends SkuaiDiBaseActivity {

	// private String fromActivity;
	private final int CHANGE_VIEW = 1;

	private Intent intent;
	private Context context;
	public static ShopGoodsImageAdapter adapter;
	private PopupWindow mPopupWindow;// 选择照片POP
	private PopupWindow mPopupWindow2;
	private SkuaidiDialog dialog;
	private String fileName;
	private String from;// 从哪个界面过来的参数
	private int position;// 下标

	// 头像存储路径
	File tempFile;

	private TextView tv_tag1;// *
	private TextView tv_tag2;// *
	private TextView tv_tag3;// *
	private FrameLayout fl_image_logo;// 店铺logo模块
	private ImageView iv_shop_logo_meng;// 店铺logo上的蒙板
	private TextView tv_title_des;// title
	private ImageView iv_title_back;// back
	private EditText et_shop_name;// 输入店铺名称
	private ImageView iv_shop_image;// 选择店铺icon
	private EditText et_shop_address;// 输入店铺地址
	private EditText et_shop_type;// 输入店铺类型
	private EditText et_shop_phones;// 输入联系电话
	private RadioGroup rg_radioGroup;
	private RadioButton task_radio1;// 5元+5%跑腿费
	private RadioButton task_radio2;// 10元跑腿费
	private ImageGridView add_image;// 添加商品图片
	private TextView tv_commit;// 提交按钮
	/** 输入店铺信息相关 **/
	private String shop_id = "";// 创建店铺成功后保存店铺ID字段
	private String shopName = "";
	private String shopAddr = "";
	private String shopType = "";
	private String shopPhone = "";
	private String revenue_demand = "";// 1:5+5%元;2:10元
	private boolean task_radio1_checked = false;
	private boolean task_radio2_checked = false;

	private List<ShopInfo> shopInfos;
	private ShopInfo shopInfo;
	/** 与店铺ICON有关变量 **/
	private String bitmap_str;// 店铺ICON base64字符串
	private String photoName = ""; // 手机拍照后保存的照片名字
	private File picture;// 拍照图片保存路径
	private String shopLogo_str;// 店铺ICON base64位
	private String shopLogoPath;// 店铺Logo路径
	private Bitmap photo;// 裁剪后的图片
	/** 与店铺商品image有关变量 **/
	private String imagePath = "";// 商品照片拍照成功后的路径
	public static List<ShopInfoImg> shopInfoImgs;// 商品下载后的文件名称和ID
	private ShopInfoImg shopInfoImg;
	// private List<String> imagePaths;//缩略图上的保存路径
	// private List<String> imageId;//保存图片ID
	/** 调用接口相关 **/
	private JSONObject data = null;
	
	public static final int ADD_ALBUM_REQUEST = 1002;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CHANGE_VIEW:
				if (!et_shop_name.getText().toString().trim().equals("")
						&& !et_shop_address.getText().toString().trim().equals("")
						&& (task_radio1_checked == true || task_radio2_checked == true)) {
					task_radio1.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_task_radiobutton"));
					task_radio2.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_task_radiobutton"));
					tv_commit.setEnabled(true);
					tv_commit.setBackgroundResource(SkuaidiSkinManager.getSkinResId("big_button_selector"));
				} else {
					tv_commit.setEnabled(false);
					tv_commit.setBackgroundResource(R.drawable.shape_btn_gray1);
					
				}
				break;

			case Constants.ADD_SHOP_SUCCESS:// 创建微店成功
				shop_id = (String) msg.obj;
				// 如果有图片就调传图片接口
				if (BitmapUtil.getBmp(context) != null && BitmapUtil.getBmp(context).size() != 0) {
					String[] pic_arr = new String[BitmapUtil.getBmp(context).size()];
					boolean haveLocImg = false;
					
					// 如果存在本地图片-图片ID是0的时候证明是本地图片
					for (int i = 0; i < BitmapUtil.getBmp(context).size(); i++) {
						// 如果集合里面有图片的ID是0
						if (BitmapUtil.getImgId(context).get(i).equals("0")) {
							// 如果该图片的bitmap转为字符串时不为空字
							if (!bitMapToString(i).equals("")) {
								// 将ID为0的图片的字符类型值保存到数组中
								pic_arr[i] = bitMapToString(i);
								haveLocImg = true;
							}
						}
					}
					if (haveLocImg == true) {
						// 创建成功后接口返回得到店铺ID,再上传该店铺的图片
						JSONObject data = (JSONObject) KuaidiApi.uploadBusinessShopPic("add",shop_id,Utility.Object2String(pic_arr));
						httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
						UtilToolkit.showToast("店铺数据已更新,请耐心等待图片更新...");
					}
					SkuaidiSpf.saveShopLogoStr(context, "");// 清除在SP保存的店铺logo
					setResult(Constants.RESULT_CODE_2);
					BitmapUtil.getBmp(context).clear();
					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getImgBackUps(context).clear();
					BitmapUtil.getImgId(context).clear();
					BitmapUtil.setMax(context, 0);
					finish();
				} else {
					SkuaidiSpf.saveShopLogoStr(context, "");// 清除在SP保存的店铺logo
					setResult(Constants.RESULT_CODE_2);
					BitmapUtil.getBmp(context).clear();
					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getImgBackUps(context).clear();
					BitmapUtil.getImgId(context).clear();
					BitmapUtil.setMax(context, 0);
					finish();
				}
				// 上传商品图片===============================
				break;
			case Constants.UPDATE_SHOP_SUCCESS:// 更新店铺数据成功
				dismissProgressDialog();
				// 如果有图片就调传图片接口
				if (BitmapUtil.getBmp(context) != null
						&& BitmapUtil.getBmp(context).size() != 0) {
					String[] pic_arr = new String[BitmapUtil.getBmp(context).size()];
					boolean haveLocImg = false;
					for (int i = 0; i < BitmapUtil.getBmp(context).size(); i++) {
						if (BitmapUtil.getImgId(context).get(i).equals("0")) {// 如果BitmapUtil.getImgId(context)里的值等于0的时候说明是本地的图片-图片ID是0的时候证明是本地图片
							if (!bitMapToString(i).equals("")) {
								pic_arr[i] = bitMapToString(i);
								haveLocImg = true;
							}
						}
					}
					if (haveLocImg == true) {
						// 创建成功后接口返回得到店铺ID,再上传该店铺的图片
						JSONObject data = (JSONObject) KuaidiApi.uploadBusinessShopPic("add",shop_id, Utility.Object2String(pic_arr));
						httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
						UtilToolkit.showToast("店铺数据已更新,请耐心等待图片更新...");
					}
					setResult(Constants.RESULT_CODE);
					SkuaidiSpf.saveShopLogoStr(context, "");
					BitmapUtil.getBmp(context).clear();
					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getImgBackUps(context).clear();
					BitmapUtil.getImgId(context).clear();
					BitmapUtil.setMax(context, 0);
					finish();
				} else {
					setResult(Constants.RESULT_CODE);
					SkuaidiSpf.saveShopLogoStr(context, "");
					BitmapUtil.getBmp(context).clear();
					BitmapUtil.getDrr(context).clear();
					BitmapUtil.getImgBackUps(context).clear();
					BitmapUtil.getImgId(context).clear();
					BitmapUtil.setMax(context, 0);
					finish();
				}
				break;

			case Constants.ADD_SHOP_IMAGE_ARR_SUCCESS:// 店铺商品图片上传成功
				BitmapUtil.setMax(context, 0);
				BitmapUtil.getDrr(context).clear();
				BitmapUtil.getBmp(context).clear();
				BitmapUtil.getImgId(context).clear();
				BitmapUtil.getImgBackUps(context).clear();
				AlbumFileUtils.deleteDir();
				dismissProgressDialog();
				SkuaidiSpf.saveShopLogoStr(context, "");
				setResult(Constants.RESULT_CODE_2);
				finish();
				break;
//			case Constants.ADD_SHOP_INAGE_ARR_FAIL:// 店铺商品图片上传失败
//				BitmapUtil.setMax(context, 0);
//				BitmapUtil.getDrr(context).clear();
//				BitmapUtil.getBmp(context).clear();
//				BitmapUtil.getImgId(context).clear();
//				BitmapUtil.getImgBackUps(context).clear();
//				AlbumFileUtils.deleteDir();
//				dismissProgressDialog();
//				SkuaidiSpf.saveShopLogoStr(context, "");
//				setResult(Constants.RESULT_CODE_2);
//				finish();
//				break;
			case Constants.GET_SHOP_IMAGE_SUCCESS:// 获取店铺商品图片成功
				shopInfoImgs = (List<ShopInfoImg>) msg.obj;
				adapter.setImageLoaderData(shopInfoImgs);
				break;
			case Constants.GET_SHOP_IMAGE_NULL:// 没获取到图片

				break;
			case Constants.GET_SHOP_IMAGE_FAIL:// 获取店铺商品图片失败

				break;
			default:
				break;
			}
		}
	};

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.add_shop_activity);
		context = this;
		initView();
		initData();
		setControl();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//System.out.println("onRestart");
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.PHOTO_REQUEST_CUT) {// 裁剪后返回请求
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					photo = extras.getParcelable("data");
					iv_shop_image.setImageBitmap(photo);
					shopLogo_str = bitMapToString(photo);// 将bitmap转换成String
					SkuaidiSpf.saveShopLogoStr(context, shopLogo_str);
					ImageUtility.saveBitmapInLocal(photo, "/skuaidi/shopIcon/",
							"shopIcon");// 将图片保存到local
				}
			}
		} else if (requestCode == Constants.PHOTOHRAPH) {// 添加店铺Icon拍照返回压缩过的图片（从原图压缩出来的bitmap,保存的是没有裁剪过的图片）
			if (resultCode == RESULT_OK) {
				shopLogoPath = SkuaidiSpf.getShopLogoPath(context);
				picture = new File(shopLogoPath);
				startPhotoZoom(Uri.fromFile(picture), 300);// 调用裁剪图片方法
			}
		} else if (requestCode == Constants.PHOTO_REQUEST_TAKEPHOTO) {// 添加店铺Icon打开相册请求
			if (resultCode == RESULT_OK) {
				startPhotoZoom(data.getData(), 300);// 调用裁剪图片方法
			}
		} else if (requestCode == Constants.REQUEST_ADD_PHOTO) {// 添加商品图片请求
			if (resultCode == RESULT_OK) {

				if (!SkuaidiSpf.getShopLogoStr(context).equals("")) {
					stringTobitMap(SkuaidiSpf.getShopLogoStr(context));
				}
				/******** photoPath ********/
				List<String> photoImagePath = SkuaidiSpf.getPhotoImagePath(context);// 获取所有的图片路径
				
				if (photoImagePath != null) {
					BitmapUtil.setDrr(context, photoImagePath);
				}
				String imagePath = BitmapUtil.getDrr(context).get((BitmapUtil.getDrr(context).size() - 1));
				BitmapUtil.getDrr(context).remove((BitmapUtil.getDrr(context).size() - 1));
				Bitmap shopGoodsBit = null;
				if (Utility.getImage(imagePath,480f,800f,40) != null) {
					shopGoodsBit = Utility.getImage(imagePath,480f,800f,40);// 压缩图片获取图片bitmap
					//****图片翻转****
					int degree = BitmapUtil.readPictureDegree(imagePath);
					shopGoodsBit = BitmapUtil.rotaingImageView(degree,shopGoodsBit);
					//****将翻转后的图片保存到本地****
					fileName = AlbumFileUtils.SavePicInLocal(shopGoodsBit);
				}
				
				/******** photoID ********/
				List<String> photoImageID = SkuaidiSpf.getPhotoImageID(context);
				if (photoImageID != null) {
					BitmapUtil.setImgId(context, photoImageID);
				}
				String imageID = BitmapUtil.getImgId(context).get(BitmapUtil.getImgId(context).size() - 1);
				BitmapUtil.getImgId(context).remove(BitmapUtil.getImgId(context).size() - 1);
				
				if (BitmapUtil.getDrr(context).size() < 9) {
					ShopInfoImg shopInfoImg = new ShopInfoImg();
					shopInfoImg.setPhotoURL(fileName);
					shopInfoImg.setSpid("");
					shopInfoImg.setBitmap(shopGoodsBit);
					shopInfoImgs.add(shopInfoImg);
					adapter.setImageLoaderData(shopInfoImgs);
				} else {
					UtilToolkit.showToast( "最多选择9张");
				}
			} else {
				//System.out.println("gudd return success");
			}
		}else if (requestCode == ADD_ALBUM_REQUEST) {
			if(resultCode == AlbumImageActivity.ADD_ALBUM_SUCCESS){
				adapter.setImageLoaderData(shopInfoImgs);	
			}
		}
	}

	/**
	 * 店铺ICON拍照后的截图
	 * 
	 * @param uri
	 * @param size
	 */
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Constants.PHOTO_REQUEST_CUT);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		//System.out.println("gudd addshop : " + context.getClass());
		tv_tag1 = (TextView) findViewById(R.id.tv_tag1);
		tv_tag2 = (TextView) findViewById(R.id.tv_tag2);
		tv_tag3 = (TextView) findViewById(R.id.tv_tag3);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("新增店铺/抢地盘");
		tv_title_des.setFocusable(true);
		tv_title_des.setFocusableInTouchMode(true);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		et_shop_name = (EditText) findViewById(R.id.et_shop_name);
		fl_image_logo = (FrameLayout) findViewById(R.id.fl_image_logo);
		iv_shop_logo_meng = (ImageView) findViewById(R.id.iv_shop_logo_meng);
		iv_shop_image = (ImageView) findViewById(R.id.iv_shop_image);
		et_shop_address = (EditText) findViewById(R.id.et_shop_address);
		et_shop_type = (EditText) findViewById(R.id.et_shop_type);
		et_shop_phones = (EditText) findViewById(R.id.et_shop_phones);
		rg_radioGroup = (RadioGroup) findViewById(R.id.rg_radioGroup);
		task_radio1 = (RadioButton) findViewById(R.id.task_radio1);
		task_radio2 = (RadioButton) findViewById(R.id.task_radio2);
		add_image = (ImageGridView) findViewById(R.id.add_image);
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setEnabled(false);
		iv_shop_image.setOnClickListener(new MyOnclickListener());
		tv_commit.setOnClickListener(new MyOnclickListener());
		iv_title_back.setOnClickListener(new MyOnclickListener());
		task_radio1.setOnClickListener(new MyOnclickListener());
		task_radio2.setOnClickListener(new MyOnclickListener());
		adapter = new ShopGoodsImageAdapter(context);
		// adapter.loading(false);
		add_image.setAdapter(adapter);
		add_image.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化控件
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void initData() {
		shopInfoImgs = new ArrayList<ShopInfoImg>();
		from = getIntent().getStringExtra("from");
		if (from.equals("myShopItem") || from.equals("anotherShopItem")) {// 从我的店铺选择一个店铺进来的
			position = getIntent().getIntExtra("position", -1);
			if (from.equals("myShopItem")) {
				shopInfos = (List<ShopInfo>) SKuaidiApplication.getInstance()
						.onReceiveMsg("AddShopActivity", "shopInfos");
			}
			if (from.equals("anotherShopItem")) {
				shopInfos = (List<ShopInfo>) SKuaidiApplication.getInstance()
						.onReceiveMsg("AnotherShopActivity", "shopInfos");
			}
			shopInfo = shopInfos.get(position);

			shop_id = shopInfo.getShop_id();

			/** 设置title **/
			tv_title_des.setText(shopInfo.getShop_name());// 设置title显示店铺名字

			/** 设置数据到新增店铺界面中 **/
			if (shopInfo.getShop_name() != null
					&& !shopInfo.getShop_name().equals("")) {
				et_shop_name.setText(shopInfo.getShop_name());// 设置店铺名字
			}
			if (shopInfo.getShop_address() != null
					&& !shopInfo.getShop_address().equals("")) {
				et_shop_address.setText(shopInfo.getShop_address());// 设置店铺地址
			}
			if (shopInfo.getShop_type() != null
					&& !shopInfo.getShop_type().equals("")) {
				et_shop_type.setText(shopInfo.getShop_type());// 设置店铺类型
			}
			if (shopInfo.getPhone() != null && !shopInfo.getPhone().equals("")) {
				et_shop_phones.setText(shopInfo.getPhone());// 设置店铺手机号
			}
			if (shopInfo.getRevenue_demands() != null
					&& !shopInfo.getRevenue_demands().equals("")) {// 设置店铺收入类型
				if (shopInfo.getRevenue_demands().equals("1")) {
					task_radio1_checked = true;
					task_radio2_checked = false;
					task_radio1.setChecked(true);
					task_radio1.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_task_radiobutton"));
				} else if (shopInfo.getRevenue_demands().equals("2")) {
					task_radio1_checked = false;
					task_radio2_checked = true;
					task_radio2.setChecked(true);
					task_radio2.setBackgroundResource(SkuaidiSkinManager.getSkinResId("selector_task_radiobutton"));
				}
			}
			if (shopInfo.getShop_logo() != null
					&& !shopInfo.getShop_logo().equals("")) {// 加载店铺logo到图标上去
				String imageUrl = Constants.URL_MY_SHOP_IMG_ROOT + "thumb."
						+ shopInfo.getShop_logo();
				// 从网络加载图片
//				DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//						.cacheInMemory(true).cacheOnDisk(true)
//						.bitmapConfig(Config.RGB_565).build();
//				ImageLoader.getInstance().displayImage(imageUrl, iv_shop_image,
//						imageOptions);
				GlideUtil.GlideUrlToImg(AddShopActivity.this,imageUrl,iv_shop_image);
			}
		}
		if (from.equals("myShopItem") || from.equals("anotherShopItem")) {// 如果从我的店铺列表中进来--调加载图片接口
			tv_commit.setEnabled(true);
			tv_commit.setBackgroundResource(SkuaidiSkinManager.getSkinResId("big_button_selector"));
			JSONObject data = (JSONObject) KuaidiApi.getBusinessShopDetail(shop_id, "");
			httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
		}
		if (from.equals("anotherShopItem")) {
			// 隐藏*标记
			tv_tag1.setVisibility(View.INVISIBLE);
			tv_tag2.setVisibility(View.INVISIBLE);
			tv_tag3.setVisibility(View.INVISIBLE);
			// 设置edittext不可以被编辑并且内容文字颜色变灰
			et_shop_name.setTextColor(context.getResources().getColor(
					color.gray_3));
			et_shop_name.setEnabled(false);
			et_shop_address.setTextColor(context.getResources().getColor(
					color.gray_3));
			et_shop_address.setEnabled(false);
			et_shop_type.setTextColor(context.getResources().getColor(
					color.gray_3));
			et_shop_type.setEnabled(false);
			et_shop_phones.setTextColor(context.getResources().getColor(
					color.gray_3));
			et_shop_phones.setEnabled(false);
			// 设置跑腿收入类型不可编辑并且背景色变灰
			if (shopInfo.getRevenue_demands() != null
					&& !shopInfo.getRevenue_demands().equals("")) {// 设置店铺收入类型
				if (shopInfo.getRevenue_demands().equals("1")) {
					task_radio1.setBackgroundDrawable(context.getResources()
							.getDrawable(
									drawable.selector_task_radiobutton_gray));
					task_radio2.setBackgroundDrawable(context.getResources()
							.getDrawable(
									drawable.selector_task_radiobutton_gray));
					rg_radioGroup.setEnabled(false);
					task_radio1.setEnabled(false);
					task_radio1.setChecked(true);
				} else if (shopInfo.getRevenue_demands().equals("2")) {
					task_radio1.setBackgroundDrawable(context.getResources()
							.getDrawable(
									drawable.selector_task_radiobutton_gray));
					task_radio2.setBackgroundDrawable(context.getResources()
							.getDrawable(
									drawable.selector_task_radiobutton_gray));
					rg_radioGroup.setEnabled(false);
					task_radio2.setChecked(true);
					task_radio1.setEnabled(false);
				}
			}
			// 设置跑腿收入类型不可编辑并且文字变灰
			task_radio1.setTextColor(context.getResources().getColor(
					color.gray_2));
			task_radio2.setTextColor(context.getResources().getColor(
					color.gray_2));
			tv_commit.setEnabled(true);
			tv_commit.setBackgroundResource(SkuaidiSkinManager.getSkinResId("big_button_selector"));
			// 设置店铺logo蒙板灰色透明度并让蒙板显示
			iv_shop_logo_meng.getBackground().setAlpha(180);
			iv_shop_logo_meng.setVisibility(View.VISIBLE);
			// 设置店铺logo不可编辑
			fl_image_logo.setEnabled(false);
			iv_shop_logo_meng.setEnabled(false);
			iv_shop_image.setEnabled(false);
		}
	}

	/**
	 * 控件界面显示
	 */
	@SuppressLint("SimpleDateFormat")
	private void setControl() {
		/** 店铺名输入框监听 **/
		et_shop_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Message msg = new Message();
				msg.what = CHANGE_VIEW;
				handler.sendMessage(msg);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				shopName = s.toString();
			}
		});
		/** 店铺地址输入框监听 **/
		et_shop_address.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Message msg = new Message();
				msg.what = CHANGE_VIEW;
				handler.sendMessage(msg);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				shopAddr = s.toString();
			}
		});
		/** 店铺类型输入框监听 **/
		et_shop_type.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				shopType = s.toString();
			}
		});
		/** 店铺联系电话输入框监听 **/
		et_shop_phones.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				shopPhone = s.toString();
			}
		});

		/** 添加店铺商品照片监听 **/
		add_image.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(arg2 == shopInfoImgs.size()){
					mPopupWindow2 = new GetPhotoTypePop(AddShopActivity.this,"", itemOnClick2);
					mPopupWindow2.showAtLocation(findViewById(R.id.add_image),Gravity.CENTER_VERTICAL, 0, 0);
				}else{
					ArrayList<String> urls2 = new ArrayList<String>();
					for(int i = 0;i<shopInfoImgs.size();i++){
						ShopInfoImg shopInfoImg = shopInfoImgs.get(i);
						urls2.add(shopInfoImg.getPhotoName());
					}
					imageBrower(arg2, urls2,"addShopActivity");
				}
			}
		});

	}
	
	/**
	 * 打开图片查看器
	 * 
	 * @param position
	 * @param urls2
	 */
	private void imageBrower(int position, ArrayList<String> urls2,String from) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.EXTRA_FROM, from);
		SKuaidiApplication.getInstance().postMsg("PhotoShowActivity", "ShopInfos",shopInfoImgs);
		context.startActivity(intent);
	}

	class MyOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Message msg = new Message();
			switch (v.getId()) {
			case R.id.iv_shop_image:// 选择店铺icon
				mPopupWindow = new GetPhotoTypePop(AddShopActivity.this,"",itemOnClick);
				mPopupWindow.showAtLocation(findViewById(R.id.add_image),
						Gravity.CENTER_VERTICAL, 0, 0);
				break;
			case R.id.tv_commit:// 提交
				if (!Utility.isNetworkConnected()) {
					SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
					dialog.setTitleGray("提示");
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
					dialog.showDialogGray(tv_commit);
				} else {
					if (from.equals("myShopItem")) {// 从我的店铺列表中点击进来
						// 如果没有对界面数据进行修改
						if (!shopInfo.getShop_name().toString().equals(et_shop_name.getText().toString().trim())
								|| !shopInfo.getShop_address().toString().equals(et_shop_address.getText().toString().trim())
								|| !shopInfo.getShop_type().toString().equals(et_shop_type.getText().toString().trim())
								|| !shopInfo.getPhone().toString().equals(et_shop_phones.getText().toString().trim())
								|| (shopInfo.getRevenue_demands().toString().equals("1") && task_radio2_checked == true)
								|| (shopInfo.getRevenue_demands().toString().equals("2") && task_radio1_checked == true)
								|| photo != null) {
							UMShareManager.onEvent(context, "addshopActivity_updata_img", "AddShopActivity", "店铺详情：更新数据和图片");
							showProgressDialog( "请稍候，您的店铺正在更新中");
							data = (JSONObject) KuaidiApi.AddNewShop("business.shop.update",shopInfo.getShop_id(), shopName, shopAddr,shopType, "", shopPhone, revenue_demand,shopLogo_str);
							httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
						} else {
							// 如果照片被添加或修改了
							List<Bitmap> bitmaps = new ArrayList<Bitmap>();
							List<String> imagePath = new ArrayList<String>();
							boolean haveLocImg = false;// 用来判断是否有要上传的本地图片
							if(shopInfoImgs!=null && shopInfoImgs.size()!=0){
//								showProgressDialog( "图片正在压缩，请等待...");
								UtilToolkit.showToast("图片正在压缩，请等待...");
								for(int i = 0;i<shopInfoImgs.size();i++){
									if(shopInfoImgs.get(i).getBitmap()!=null){
										bitmaps.add(shopInfoImgs.get(i).getBitmap());
										imagePath.add(shopInfoImgs.get(i).getPhotoURL());
									}
								}
								String[] pic_arr = new String[bitmaps.size()];
								for(int i=0;i<bitmaps.size();i++){
									if(!bitMapToString(bitmaps.get(i)).equals("")){//bitmaps.get(i)
										pic_arr[i] = bitMapToString(Utility.getImage(imagePath.get(i), 480f, 800f, 50));
										haveLocImg = true;
									}
								}
								if (haveLocImg == true) {
									showProgressDialog( "正在上传图片，请稍候...");
									// 创建成功后接口返回得到店铺ID,再上传该店铺的图片
									JSONObject data = (JSONObject) KuaidiApi.uploadBusinessShopPic("add",shop_id,Utility.Object2String(pic_arr));// 传图片数组的时候需要将图片数组字符串格式
									httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
								} else {
									UtilToolkit.showToast("您还没有对店铺进行任何修改");
								}
							}
							
						}
					} else if (from.equals("anotherShopItem")) { // 如果是从其他店铺界面进入时候的点击
						
						// 如果照片被添加或修改了
						List<Bitmap> bitmaps = new ArrayList<Bitmap>();
						List<String> imagePath = new ArrayList<String>();
						boolean haveLocImg = false;// 用来判断是否有要上传的本地图片
						if(shopInfoImgs!=null && shopInfoImgs.size()!=0){
							UtilToolkit.showToast("图片正在压缩，请等待...");
							for(int i = 0;i<shopInfoImgs.size();i++){
								if(shopInfoImgs.get(i).getBitmap()!=null){
									bitmaps.add(shopInfoImgs.get(i).getBitmap());
								}
							}
							String[] pic_arr = new String[bitmaps.size()];
							for(int i=0;i<bitmaps.size();i++){
								if(!bitMapToString(bitmaps.get(i)).equals("")){
//									pic_arr[i] = bitMapToString(Utility.compressImage(bitmaps.get(i), 40));
									pic_arr[i] = bitMapToString(Utility.getImage(imagePath.get(i), 480f, 800f, 50));
									haveLocImg = true;
								}
							}
							if (haveLocImg == true) {
								UMShareManager.onEvent(context, "addshopActivity_updata", "AddShopActivity", "店铺详情：更新店铺商品图片");
								showProgressDialog( "正在为您更新店铺商品照片，请耐心等待...");
//								UtilToolkit.showToast("正在为您更新店铺商品照片，请耐心等待...");
								// 创建成功后接口返回得到店铺ID,再上传该店铺的图片
								JSONObject data = (JSONObject) KuaidiApi.uploadBusinessShopPic("add", shop_id,Utility.Object2String(pic_arr));
								httpInterfaceRequest(data, false,INTERFACE_VERSION_NEW);
							} else {
								UtilToolkit.showToast("您暂未修改任何数据");
							}
						}
					} else {
						UMShareManager.onEvent(context, "addshopActivity_commit", "AddShopActivity", "店铺详情：创建店铺");
						data = (JSONObject) KuaidiApi.AddNewShop(
								"business.shop.add", "", shopName,
								shopAddr, shopType, "", shopPhone,
								revenue_demand, shopLogo_str);
						showProgressDialog( "老板别急，小宝正在为您创建店铺");
						httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
					}
				}
				break;
			case R.id.iv_title_back:// 返回按钮
				BitmapUtil.setMax(context, 0);
				BitmapUtil.getDrr(context).clear();
				BitmapUtil.getBmp(context).clear();
				BitmapUtil.getImgId(context).clear();
				BitmapUtil.getImgBackUps(context).clear();
				AlbumFileUtils.deleteDir();
				SkuaidiSpf.saveShopLogoStr(context, "");
				finish();
				break;
			case R.id.task_radio1:
				task_radio1_checked = true;
				task_radio2_checked = false;
				revenue_demand = "1";// 5+5%元
				msg.what = CHANGE_VIEW;
				break;
			case R.id.task_radio2:
				task_radio1_checked = false;
				task_radio2_checked = true;
				revenue_demand = "2";// 10元
				msg.what = CHANGE_VIEW;
				break;
			default:
				break;
			}
			handler.sendMessage(msg);
		}
	}

	/**
	 * 选择店铺ICON
	 */
	private OnClickListener itemOnClick = new OnClickListener() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_paizhao:// 选择拍照
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					String filePath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/";
					File file = new File(filePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					File imageFile = new File(file, "shopIcon.jpg");
					shopLogoPath = imageFile.getPath();
					SkuaidiSpf.saveShopLogoPath(context, shopLogoPath);// 保存拍照店铺logo的地址
					Uri imageUri = Uri.fromFile(imageFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, Constants.PHOTOHRAPH);
				} else {
					UtilToolkit.showToast("未找到存储卡");
				}
				break;
			case R.id.btn_xiangce:// 选择相册-系统相册
				// 调用android的图库
				intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent,Constants.PHOTO_REQUEST_TAKEPHOTO);
				break;
			case R.id.btn_cancel:// 取消
				break;
			default:
				break;
			}
			mPopupWindow.dismiss();
		}
	};

	/**
	 * 添加店铺商品图片
	 */
	private OnClickListener itemOnClick2 = new OnClickListener() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_paizhao:// 选择拍照
				SkuaidiSpf.saveAddGoodsName(context,"IMG" + Utility.getSMSCurTime());// 将照片的名字保存到SP中（支持一些特定手机使用这种情况）
				String status = Environment.getExternalStorageState();// 验证手机是否存在SD卡
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					String filePath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/";// 图片保存路径 
					File file = new File(filePath);
					if (!file.exists()) {
						file.mkdirs();// 创建文件夹
					}
					File imageFile = new File(file, "IMG"+ Utility.getSMSCurTime() + ".jpg");// 创建图片
					imagePath = imageFile.getPath();// 获取图片路径 
					List<String> set = new ArrayList<String>();
					set.addAll(BitmapUtil.getDrr(context));
					set.add(imagePath);
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
				break;
			case R.id.btn_xiangce:// 选择相册
				add_image.setVisibility(View.VISIBLE);
				intent = new Intent(AddShopActivity.this,AlbumSystemActivity.class);
				SKuaidiApplication.getInstance().postMsg("AlbumImageAdapter", "shopInfoImgs",shopInfoImgs);
				intent.putExtra("from", "addShopActivity");
				BitmapUtil.setFromContext(context);
				startActivityForResult(intent, ADD_ALBUM_REQUEST);
				break;
			case R.id.btn_cancel:// 取消
				break;
			default:
				break;
			}
			mPopupWindow2.dismiss();
		}
	};

	public String bitMapToString(int i) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitmapUtil.getBmp(context).get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.NO_WRAP);
	}

	public String bitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.NO_WRAP);
	}

	public void stringTobitMap(String str) {
		// base64转成bitmap
		if (!str.equalsIgnoreCase("")) {
			byte[] b = Base64.decode(str, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			iv_shop_image.setImageBitmap(bitmap);
		}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BitmapUtil.setMax(context, 0);
			BitmapUtil.getDrr(context).clear();
			BitmapUtil.getBmp(context).clear();
			BitmapUtil.getImgId(context).clear();
			BitmapUtil.getImgBackUps(context).clear();
			AlbumFileUtils.deleteDir();
			SkuaidiSpf.saveShopLogoStr(context, "");
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			Message msg = new Message();
			if (sname.equals("business.shop.pic")) {// 商品图片提交成功
				try {
					msg.what = Constants.ADD_SHOP_IMAGE_ARR_SUCCESS;
					UtilToolkit.showToast(result.getString("retStr"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (sname.equals("business.shop.detail")) {
				JsonXmlParser.parseBusinessShopDetail(context, handler, result);
			} else {
				try {
					String retStr = result.getString("retStr");
					if (sname.equals("business.shop.update")) {
						msg.what = Constants.UPDATE_SHOP_SUCCESS;// 更新店铺数据成功
					} else if (sname.equals("business.shop.add")) {
						// success的时候retStr返回9（接口返回）--retStr:店铺ID
						msg.what = Constants.ADD_SHOP_SUCCESS;
						msg.obj = retStr;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onRequestFail(String code, String sname,String message, String act, JSONObject data_fail) {
		if(!message.equals("")){
			if (sname.equals("business.shop.update")) {
				UtilToolkit.showToast(message);
				dismissProgressDialog();
			} else if (sname.equals("business.shop.add")) {
				// fail的时候desc返回：此微店已存在，不能添加（接口返回）
				UtilToolkit.showToast(message);
				dismissProgressDialog();
			} else if (sname.equals("business.shop.pic")) {
				UtilToolkit.showToast(message);
				dismissProgressDialog();
			} else if (sname.equals("business.shop.detail")) {
				UtilToolkit.showToast(message);
				dismissProgressDialog();
			}
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
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
