package com.kuaibao.skuaidi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {

	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index"; 
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	
	public static final String EXTRA_FROM = "from";
	
	private ViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	
	private RelativeLayout rl_head;// title部分
	private ImageView back ;// 返回按钮
	private ImageView delete;// 删除按钮
	
	private String imageSpid;// 图片对应服务器上的ID
	
	private List<ShopInfoImg> shopInfoImgs;
	private ArrayList<String> urls;
	private ImagePagerAdapter mAdapter;
	
	private boolean isRequesting = false;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PhotoShowActivity.DELETE_IMAGE_SUCCESS:
				isRequesting = false;
				boolean isLastImg = false;
				if(pagerPosition==shopInfoImgs.size()-1){
					isLastImg = true;
				}
				urls.remove(pagerPosition);
				mAdapter.notifyDataSetChanged();
				shopInfoImgs.remove(pagerPosition);
				if(urls.size()==0){
					finish();
				}else if(isLastImg==true){
					mPager.setCurrentItem(shopInfoImgs.size()-1);
				}else {
					if(pagerPosition==0){
						mPager.setCurrentItem(pagerPosition+1);
					}else{
						mPager.setCurrentItem(pagerPosition-1);
					}
//					mPager.removeViewAt(pagerPosition);
				}
				
				break;

			default:
				break;
			}
		}
	};
	
	private String from="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.circle_express_grid_image_item_show);
		findView();
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
		from = getIntent().getStringExtra(EXTRA_FROM);
		if(from!=null && from.equals("addShopActivity")){// 如果是从添加店铺界面进来的话显示删除按钮
			rl_head.setVisibility(View.VISIBLE);
			shopInfoImgs = (List<ShopInfoImg>) SKuaidiApplication.getInstance().onReceiveMsg("PhotoShowActivity", "ShopInfos");// 获取网络上的图片
			imageSpid = shopInfoImgs.get(pagerPosition).getSpid();// viewpager进入第一页的时候是不执行pageChangeListener的，所以手动获取到第一张图片的spid
		}
		
		mPager = (ViewPager) findViewById(R.id.viewpager);
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				pagerPosition = arg0;
				CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
				if(from != null && from.equals("addShopActivity")){
					imageSpid = shopInfoImgs.get(pagerPosition).getSpid();
				}
				
				
			}
		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		mPager.setCurrentItem(pagerPosition);
		
		
		
	}
	
	private void findView() {
		rl_head = (RelativeLayout) findViewById(R.id.rl_head);
		back = (ImageView) findViewById(R.id.back);
		delete = (ImageView) findViewById(R.id.iv_delete);
		
		back.setOnClickListener(onClickListener);
		delete.setOnClickListener(onClickListener);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:// 返回按钮
				finish();
				break;
			case R.id.iv_delete:// 删除按钮
				if(!imageSpid.equals("")){
					if(isRequesting == true||shopInfoImgs.size()==0){
						return;
					}
					isRequesting = true;
					KuaidiApi.deleteAddShopImgs(handler, imageSpid);
				}else{
					Message msg = new Message();
					msg.what = PhotoShowActivity.DELETE_IMAGE_SUCCESS;
					handler.sendMessage(msg);
				}
				break;
			default:
				break;
			}
			
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}
	
	/**
	 * 适配器
	 * @author 顾冬冬
	 *
	 */
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
		public ArrayList<String> fileList;
		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return CircleExpressGridItemFragment.newInstance(url,position);
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
	


}
