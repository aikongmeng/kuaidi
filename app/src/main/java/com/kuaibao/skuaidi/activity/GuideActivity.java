package com.kuaibao.skuaidi.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.GuidPageAdapter;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.IsGuid;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * lgg 重构
 */
public class GuideActivity extends RxRetrofitBaseActivity {
	@BindView(R.id.viewpager)
	ViewPager viewPager;
	private GuidPageAdapter adapter;
	private String[] guideImgs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_guider);
		IsGuid.clearGuid(this);
		initViewPager();
	}

	@Override
	protected  void setStatusBar(){
		StatusBarUtil.setColor(GuideActivity.this, ContextCompat.getColor(GuideActivity.this, R.color.white));
	}

	private void initViewPager() {
		try {
			guideImgs=getAssets().list("guide");
		} catch (IOException e) {
			e.printStackTrace();
			enter();
		}
		if(guideImgs!=null && guideImgs.length>0){
			List<View> guideViews=new ArrayList<>();
			for(String img:guideImgs){
				View view=getLayoutInflater().inflate(R.layout.welcome_guider_img,null);
				ImageView imageView= (ImageView) view.findViewById(R.id.imv_image_guider);
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(viewPager.getCurrentItem()==(guideImgs.length-1)){
							enter();
						}else{
							viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
						}
					}
				});
				Drawable drawable= Utility.loadImageFromAsserts(this,"guide/"+img);
				if(drawable!=null){
					imageView.setImageDrawable(drawable);
					guideViews.add(view);
				}
			}
			if(guideViews.size()>0){
				adapter = new GuidPageAdapter(guideViews);
				viewPager.setAdapter(adapter);
				viewPager.setCurrentItem(0);
			}else{
				enter();
			}
		}else{
			enter();
		}
	}

	public void enter() {
		Intent intent;
		if (SkuaidiSpf.IsLogin()) {
			intent = new Intent(this, MainActivity.class);
		} else {
			intent = new Intent(this, LoginActivity.class);
		}
		startActivity(intent);
		SkuaidiSpf.setVersionCode(this, SKuaidiApplication.VERSION_CODE);
		SkuaidiSpf.setIsHadGuidNewVersion(this, true);
		finish();
	}
}
