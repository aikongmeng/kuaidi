package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.TakeOutFragmentPagerAdapter;
import com.kuaibao.skuaidi.activity.view.GetOutsideBlockPop;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.viewpager.latestTakeOutFragment;
import com.kuaibao.skuaidi.viewpager.myShopFragment;
import com.kuaibao.skuaidi.viewpager.myTakeOutFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//import com.umeng.socialize.controller.UMServiceFactory;
//import com.umeng.socialize.controller.UMSocialService;
//import com.umeng.socialize.sso.UMSsoHandler;

public class OutsideTheBlockActivity extends FragmentActivity {

	private Context context;// 上下文
	private Intent intent;
	private RelativeLayout more;// 更多（三个点）
	private String downLoadUrl = "";//邀请同事下载url

	private ViewPager mPager;
	private ImageView image;
	private SkuaidiImageView title_back;//返回按钮 
	private LinearLayout ll_tab1, ll_tab2, ll_tab3;
	private TextView tv_tab1, tv_tab2, tv_tab3;
	private int currIndex;// 当前页卡编号
	private int bmpWidth;// 横线图片的宽度
	private int offset;// 图片移动的偏移量
	private ArrayList<Fragment> fragmentList;
	
	/**分享相关**/
	private String title;
	private String shareText;
	private String targetUrl;
	private Map<String, String> shareTexts;
	
	
	private DisplayMetrics dm;
	private long mExitTime;
	private GetOutsideBlockPop getOutsideBlockPop;// 外快更多操作
//	private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	
	private String from;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.outside_block_activity);
		context = this;
		downLoadUrl = "http://m.kuaidihelp.com/go?type=sinvite&uid="+SkuaidiSpf.getLoginUser().getUserId();
		initView();
		initImage();
		initViewPager();
		
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		image = (ImageView) findViewById(R.id.iv_cursor);
		image.setLayoutParams(new LinearLayout.LayoutParams(
				(dm.widthPixels - 1) / 3,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		image.setImageResource(SkuaidiSkinManager.getSkinResId("viewpager_line"));
		bmpWidth = image.getWidth();
		title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		ll_tab1 = (LinearLayout) findViewById(R.id.ll_tab1);
		ll_tab2 = (LinearLayout) findViewById(R.id.ll_tab2);
		ll_tab3 = (LinearLayout) findViewById(R.id.ll_tab3);
		tv_tab1 = (TextView) findViewById(R.id.tv_tab1);
		tv_tab2 = (TextView) findViewById(R.id.tv_tab2);
		tv_tab3 = (TextView) findViewById(R.id.tv_tab3);
		tv_tab1.setTextColor( SkuaidiSkinManager.getTextColor("main_color"));
		
		
		title_back.setOnClickListener(new myOnclickListener());
		ll_tab1.setOnClickListener(new TabClickListener(0));
		ll_tab2.setOnClickListener(new TabClickListener(1));
		ll_tab3.setOnClickListener(new TabClickListener(2));

		more = (RelativeLayout) findViewById(R.id.more);
		more.setOnClickListener(new myOnclickListener());
		
		from = getIntent().getStringExtra("from");
		if(from != null && from.equals("myExpressShopActivity")){
			title_back.setVisibility(View.VISIBLE);
		}else {
			title_back.setVisibility(View.GONE);
		}
		
		
		
		
	}

	public class TabClickListener implements OnClickListener {
		int index = 0;

		public TabClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}

	}

	/**
	 * 初始化图片的位移像素
	 */
	public void initImage() {
		int screenWidth = dm.widthPixels;
		offset = (screenWidth / 3 - bmpWidth) / 2;// 偏移量
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Log.d("", "#### ssoHandler.authorizeCallBack11111");
		String result = "null";
		try {
			Bundle b = data.getExtras();
			Set<String> keySet = b.keySet();
			if(keySet.size() > 0)
				result = "result size:"+keySet.size();
			for(String key : keySet){
				Object object = b.get(key);
				//Log.d("TestData", "Result:"+key+"   "+object.toString());
			}
		}
		catch (Exception e) {

		}
		//Log.d("TestData", "onActivityResult   " + requestCode + "   " + resultCode + "   " + result);
		
//	    // 根据requestCode获取对应的SsoHandler
//	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
//		if (ssoHandler != null) {
//			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//			//Log.d("", "#### ssoHandler.authorizeCallBack");
//		}
	}
	
	public void initViewPager() {
		fragmentList = new ArrayList<Fragment>();
		Fragment bestTakeOut = new latestTakeOutFragment();
		Fragment myTakeOut = new myTakeOutFragment();
		Fragment myShop = new myShopFragment();
		fragmentList.add(bestTakeOut);
		fragmentList.add(myTakeOut);
		fragmentList.add(myShop);
		TakeOutFragmentPagerAdapter adapter = new TakeOutFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList);
		// 为viewPager设置适配器
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new myOnpageChangeListener());
	}

	public class myOnpageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpWidth;// 两个相邻页面的偏移量

		// 此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。
		// arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		// 当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
		// arg0 :当前页面，及你点击滑动的页面
		// arg1:当前页面偏移的百分比
		// arg2:当前页面偏移的像素位置

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		// 此方法是页面跳转完后得到调用，arg0是你当前选中的页面的Position（位置编号）。
		@Override
		public void onPageSelected(int position) {
			Animation animation = new TranslateAnimation(currIndex * one,
					position * one, 0, 0);// 平移画面
			currIndex = position;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用imageViewf来显示动画 的
			mPager.setCurrentItem(position);
			int checkedColor = SkuaidiSkinManager.getTextColor("main_color");
			int defaultColor = context.getResources().getColor(R.color.gray_3);
			if (position == 0) {
				tv_tab1.setTextColor(checkedColor);
				tv_tab2.setTextColor(defaultColor);
				tv_tab3.setTextColor(defaultColor);
			} else if (position == 1) {
				tv_tab1.setTextColor(defaultColor);
				tv_tab2.setTextColor(checkedColor);
				tv_tab3.setTextColor(defaultColor);
			} else if (position == 2) {
				tv_tab1.setTextColor(defaultColor);
				tv_tab2.setTextColor(defaultColor);
				tv_tab3.setTextColor(checkedColor);
			}
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
	protected void onDestroy() {

		super.onDestroy();
	}

	/**
	 * 点击事件
	 * 
	 * @author gudd
	 * 
	 */
	class myOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.more:
				getOutsideBlockPop = new GetOutsideBlockPop(context,
						onClickListener);
				getOutsideBlockPop.showAtLocation(findViewById(R.id.iv_more),
						0, 0, 0);
				break;

			case R.id.iv_title_back:
				if(from!=null && from.equals("myExpressShopActivity")){
					finish();
				}
				break;
			default:
				break;
			}
		}
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 抢地盘
			case R.id.ll_rob_territory:
				UMShareManager.onEvent(context, "outsideTheBlock_rob_space", "outsideTheBlock", "跑腿：抢地盘");
				intent = new Intent(context,AddShopActivity.class);
				intent.putExtra("from","OutsideTheBlockActivity");
				startActivityForResult(intent, Constants.REQUEST_CODE);//REQUEST_CODE
				break;
			// 其他店铺
			case R.id.ll_other_shop:
				UMShareManager.onEvent(context, "outsideTheBlock_other_shop", "outsideTheBlock", "跑腿：其他店铺");
				intent = new Intent(context, OutsideAnotherShopActivity.class);
				startActivity(intent);
				break;
			// 邀请客户
			case R.id.ll_invite_friends:
				UMShareManager.onEvent(context, "outsideTheBlock_invite_customer", "outsideTheBlock", "跑腿：邀请客户");
				title = "快递小哥送外卖";
				shareText = "亲，我是一名勤快又善良的快递小哥！如需跑腿买东西，请召唤我，火箭速度，金牌服务！随时恭候您哟！";
				targetUrl = "http://ckd.so/1";
				shareTexts = new HashMap<String, String>();
				shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText+targetUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText+targetUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText+targetUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText+targetUrl);
				UMShareManager.openShare(getParent(),title, shareTexts, targetUrl,R.drawable.share_software);
				break;
			// 邀请同事
			case R.id.ll_invite_colleague:
				UMShareManager.onEvent(context, "outsideTheBlock_invite_colleague", "outsideTheBlock", "跑腿：邀请同事");
				title = "快递员都在赚外快";
				shareText = "我正使用快递员APP，自从开通跑腿业务，赚外快，so easy！推荐你也来使用！";
				targetUrl = downLoadUrl;
				shareTexts = new HashMap<String, String>();
				shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText+downLoadUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText+downLoadUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText+downLoadUrl);
				shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText+downLoadUrl);
				UMShareManager.openShare(getParent(),title, shareTexts, targetUrl,R.drawable.share_software);
				break;
			default:
				break;
			}
			getOutsideBlockPop.dismiss();
		}
	};

	// 第二次后退键退出程序
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(from != null && from.equals("myExpressShopActivity")){
//				finish();
//				from = "";
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
