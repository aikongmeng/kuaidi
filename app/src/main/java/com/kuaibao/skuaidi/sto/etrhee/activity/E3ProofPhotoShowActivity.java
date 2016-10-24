package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.util.AlbumFileUtils;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示照片
 * 
 * @author wq
 * 
 */
public class E3ProofPhotoShowActivity extends SkuaiDiBaseActivity {

	private ArrayList<View> Views = null;
	private List<ShopInfoImg> shopInfoImgs;// 网络加载过来的图片
	private List<Bitmap> bitmaps;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int index;
	private Context context;
	private ImageView photo_bt_exit;
	private ImageView photo_bt_del;
	private ImageView photo_bt_enter;
	private TextView tv_count;
	private ImageView iv_check;
	private Button btn_confir;
	private SkuaidiDialogGrayStyle dialog;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public ArrayList<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	public List<String> imgId = new ArrayList<String>();
	public List<String> imgBackUps = new ArrayList<String>();

	public ArrayList<Bitmap> bitmap_to_dell = new ArrayList<Bitmap>();
	public ArrayList<String> drr_to_dell = new ArrayList<String>();
	private Context fromContext;
	private RelativeLayout photo_relativeLayout,bottom_menu;
	private boolean isClick = false;
	private int checkedCount;
	private boolean isChecked_1 = true;
	private boolean isChecked_2 = true;
	private boolean isChecked_3 = true;
	private String selectedDrr = "";//

	public static final int DELETE_IMAGE_SUCCESS = 810;
	public static final int RESULT_CODE = 3;
	private String from = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_e3_proof_photo_show);
		context = this;
		from = getIntent().getStringExtra("from");

		fromContext = BitmapUtil.getFromContext();
		initView();
		initData();
	}

	private void initView() {
		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		bottom_menu = (RelativeLayout) findViewById(R.id.bottom_menu);
		pager = (ViewPager) findViewById(R.id.viewpager);
		photo_bt_exit = (ImageView) findViewById(R.id.photo_bt_exit);
		photo_bt_enter = (ImageView) findViewById(R.id.photo_bt_enter);
		photo_bt_del = (ImageView) findViewById(R.id.photo_bt_del);
		tv_count = (TextView) findViewById(R.id.tv_count);
		iv_check = (ImageView) findViewById(R.id.iv_check);
		btn_confir = (Button) findViewById(R.id.btn_confir);
		if ("E3ProofAddImgActivity_itemClick".equals(from)) {
			iv_check.setVisibility(View.GONE);
			btn_confir.setVisibility(View.GONE);
			bottom_menu.setVisibility(View.GONE);
			photo_bt_enter.setVisibility(View.VISIBLE);
		} else {
			tv_count.setVisibility(View.VISIBLE);

			iv_check.setVisibility(View.VISIBLE);
			btn_confir.setVisibility(View.VISIBLE);
			bottom_menu.setVisibility(View.VISIBLE);
			photo_bt_enter.setVisibility(View.GONE);
			iv_check.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (index) {
					case 0:
						viewChanged(isChecked_1, 0);
						break;
					case 1:
						viewChanged(isChecked_2, 1);
						break;
					case 2:
						viewChanged(isChecked_3, 2);
						break;
					default:
						break;
					}

				}
			});

			btn_confir.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (BitmapUtil.act_bool) {
						BitmapUtil.act_bool = false;
					}
					if (!isChecked_1) {
						drr_to_dell.add(drr.get(0));
						// bitmap_to_dell.add(BitmapUtil.getBmp(fromContext).get(0));
					}
					if (!isChecked_2 && drr.size() > 1) {
						drr_to_dell.add(drr.get(1));
						// bitmap_to_dell.add(BitmapUtil.getBmp(fromContext).get(1));
					}
					if (!isChecked_3 && drr.size() > 2) {
						drr_to_dell.add(drr.get(2));
						// bitmap_to_dell.add(BitmapUtil.getBmp(fromContext).get(2));
					}
					drr.removeAll(drr_to_dell);
					BitmapUtil.getDrr(fromContext).removeAll(drr_to_dell);
					// BitmapUtil.getBmp(fromContext).removeAll(bitmap_to_dell);
					if ("E3ProofAddImgActivity".equals(getIntent().getStringExtra("from"))) {

						E3ProofAddImgActivity.drr.addAll(drr);

						if (E3ProofAddImgActivity.activityList.size() > 0) {
							for (Activity activity : E3ProofAddImgActivity.activityList) {
								activity.finish();
							}
							E3ProofAddImgActivity.activityList.clear();
						}
						finish();

						// Intent intent = new Intent();
						// intent.putStringArrayListExtra("drr", drr);
						// E3ProofPhotoShowActivity.this.setResult(RESULT_CODE,
						// intent);
					} else {
						Intent intent = new Intent(context, E3ProofAddImgActivity.class);
						intent.putStringArrayListExtra("drr", drr);
						intent.putExtra("from", "E3ProofActivity");
						startActivity(intent);
					}
					finish();
				}
			});
		}

		pager.setOnClickListener(new MyOnClickListener());
		photo_bt_exit.setOnClickListener(new MyOnClickListener());
		photo_bt_enter.setOnClickListener(new MyOnClickListener());
		photo_bt_del.setOnClickListener(new MyOnClickListener());
	}

	private void initData() {
		drr = getIntent().getStringArrayListExtra("drr");
		if (drr == null) {
			return;
		}

		selectedDrr = drr.get(0);
		for (int i = 0; i < drr.size(); i++) {
			Bitmap map = null;
			try {
				map = BitmapUtil.revitionImageSize(drr.get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}

			bmp.add(map);
		}

		// for (int i = 0; i < BitmapUtil.getBmp(fromContext).size(); i++) {
		// bmp.add(BitmapUtil.getBmp(fromContext).get(i));
		// }
		// for (int i = 0; i < BitmapUtil.getDrr(fromContext).size(); i++) {
		// drr.add(BitmapUtil.getDrr(fromContext).get(i));
		// }

		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < bmp.size(); i++) {
			initListViews(bmp.get(i));
		}
		adapter = new MyPageAdapter(Views);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
		Intent intent = getIntent();
		int index = intent.getIntExtra("pic_index", 0);
		pager.setCurrentItem(index);
		tv_count.setText(index + 1 + "/" + bmp.size());
		btn_confir.setText("确定(" + bmp.size() + ")");
		setViewStatus(bmp.size());
		checkedCount = bmp.size();

	}

	private void initListViews(Bitmap bm) {
		if (Views == null)
			Views = new ArrayList<View>();
		ImageView img = new ImageView(this);// 构造textView对象
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Views.add(img);// 添加view
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 页面选择响应函数
			index = arg0;
			tv_count.setText(index + 1 + "/" + bmp.size());
			selectedDrr = drr.get(index);// 当前照片的路径。
			switch (index) {
			case 0:
				if (isChecked_1) {
					iv_check.setImageResource(R.drawable.checked_ok);
				} else {
					iv_check.setImageResource(R.drawable.checked_empty);
				}
				break;
			case 1:
				if (isChecked_2) {
					iv_check.setImageResource(R.drawable.checked_ok);
				} else {
					iv_check.setImageResource(R.drawable.checked_empty);
				}
				break;
			case 2:
				if (isChecked_3) {
					iv_check.setImageResource(R.drawable.checked_ok);
				} else {
					iv_check.setImageResource(R.drawable.checked_empty);
				}
				break;

			default:
				break;
			}
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变
		}
	};

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.photo_bt_exit:
				if (!"E3ProofAddImgActivity".equals(getIntent().getStringExtra("from"))) {
					if (fromContext.getClass().equals(E3ProofActivity.class)) {
						BitmapUtil.getBmp(fromContext).clear();
						BitmapUtil.getDrr(fromContext).clear();
					}
				}
				finish();
				break;
			case R.id.photo_bt_del:
				if (Views.size() == 1) {
					BitmapUtil.getBmp(fromContext).clear();
					BitmapUtil.getDrr(fromContext).clear();
					BitmapUtil.setMax(fromContext, 0);
					AlbumFileUtils.deleteDir();
					finish();
				} else {
					String newStr = drr.get(index).substring(drr.get(index).lastIndexOf("/") + 1,
							drr.get(index).lastIndexOf("."));
					bmp.remove(index);
					drr.remove(index);
					del.add(newStr);
					pager.removeAllViews();
					Views.remove(index);
					adapter.setListViews(Views);
					adapter.notifyDataSetChanged();
				}
				break;
			case R.id.photo_bt_enter:

				dialog = new SkuaidiDialogGrayStyle(context);
				dialog.setTitleGray("提示");
				dialog.setContentGray("您确认要删除这张图片吗？");
				dialog.setPositionButtonTextGray("确认");
				dialog.setNegativeButtonTextGray("取消");
				dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

					@Override
					public void onClick(View v) {
						drr.remove(selectedDrr);
						E3ProofAddImgActivity.drr.remove(selectedDrr);
						BitmapUtil.getDrr(fromContext).remove(selectedDrr);
						if (E3ProofAddImgActivity.activityList.size() > 0) {
							for (Activity activity : E3ProofAddImgActivity.activityList) {
								activity.finish();
							}
							E3ProofAddImgActivity.activityList.clear();
						}
						finish();
					}
				});
				dialog.showDialogGray(photo_bt_enter);
				break;

			case R.id.viewpager:
				if (isClick == false) {
					photo_relativeLayout.setVisibility(View.VISIBLE);
					isClick = true;
				} else {
					photo_relativeLayout.setVisibility(View.GONE);
					isClick = false;
				}
				break;
			default:
				break;
			}
		}
	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
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
			if (!"E3ProofAddImgActivity".equals(getIntent().getStringExtra("from"))) {
				if (fromContext.getClass().equals(E3ProofActivity.class)) {
					BitmapUtil.getBmp(fromContext).clear();
					BitmapUtil.getDrr(fromContext).clear();
				}
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// 调用接口成功
	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
	}

	// 调用 新接口失败
	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		UtilToolkit.showToast(result);
	}

	// 调用老接口失败
	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	public void setViewStatus(int num){
		if(num <= 0){
			btn_confir.setBackgroundResource(R.drawable.shape_btn_gray2);
			btn_confir.setTextColor(context.getResources().getColor(R.color.gray_4));
		}else{
			btn_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);
			btn_confir.setTextColor(context.getResources().getColor(R.color.white));
		}
	}
	
	private void viewChanged(boolean isChecked, int index) {
		if (isChecked) {
			switch (index) {
			case 0:
				isChecked_1 = false;
				break;
			case 1:
				isChecked_2 = false;
				break;
			case 2:
				isChecked_3 = false;
				break;

			default:
				break;
			}
			checkedCount--;
			btn_confir.setText("确定(" + checkedCount + ")");
			setViewStatus(checkedCount);
			iv_check.setImageResource(R.drawable.checked_empty);
			if (index < drr.size()) {
				String newStr = drr.get(index);
				del.add(newStr);
			}
			if (checkedCount == 0) {
				btn_confir.setEnabled(false);
			} else
				btn_confir.setEnabled(true);
		} else {

			switch (index) {
			case 0:
				isChecked_1 = true;
				break;
			case 1:
				isChecked_2 = true;
				break;
			case 2:
				isChecked_3 = true;
				break;
			default:
				break;
			}
			iv_check.setImageResource(R.drawable.checked_ok);
			checkedCount++;
			btn_confir.setEnabled(true);
			btn_confir.setText("确定(" + checkedCount + ")");
			setViewStatus(checkedCount);

		}
	}
}
