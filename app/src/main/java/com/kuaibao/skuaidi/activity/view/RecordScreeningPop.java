package com.kuaibao.skuaidi.activity.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.util.List;

public class RecordScreeningPop implements OnClickListener {

	private Context mContext = null;
	private PopupWindow popupWindow = null;
	private CheckGridAdapter adapter = null;
	private View view = null;
	private GridView gvMenu = null;
	private ViewGroup llMenu = null;
	private List<String> itemArr;
	private ItemOnClickListener itemOnClickListener = null;
	private LayoutDismissListener layoutDismissListener = null;

	public RecordScreeningPop(Context context, View view, List<String> itemArr) {
		this.mContext = context;
		this.view = view;
		this.itemArr = itemArr;
		initView();
	}

	private void initView() {
		popupWindow = new PopupWindow(mContext);
		View layout = LayoutInflater.from(mContext).inflate(R.layout.sms_record_screening_pop, null);

		llMenu = (ViewGroup) layout.findViewById(R.id.llMenu);
		gvMenu = (GridView) layout.findViewById(R.id.gvMenu);

		adapter = new CheckGridAdapter();
		gvMenu.setAdapter(adapter);
		
		popupWindow.setWidth(LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(LayoutParams.MATCH_PARENT);
		popupWindow.setAnimationStyle(R.style.AnimBottom);
		popupWindow.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		popupWindow.setBackgroundDrawable(dw);

		// 点击界面隐藏popupwindow
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutDismissListener.onDismiss();
			}
		});
	}
	
	/** 设置gridview的每行条目个数【默认是3个】 **/
	public void setNumColumns(int numColumns){
		if(numColumns<=0){
			gvMenu.setNumColumns(3);
		}else{
			gvMenu.setNumColumns(numColumns);
		}
	}

	public boolean isShowing() {
		if (popupWindow != null) {
			return popupWindow.isShowing();
		} else {
			return false;
		}
	}

	public void showPop() {
		popIn();
		popupWindow.showAsDropDown(view);
	}

	public void dismissPop() {
		popOut();
	}

	private void popIn() {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		llMenu.measure(w, h);
		int width = llMenu.getMeasuredWidth();
		int height = llMenu.getMeasuredHeight();

		int mode = itemArr.size() % 3;
		int lineNum = itemArr.size() / 3;
		if (mode > 0) {
			lineNum = mode;
		}

		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -height * lineNum, 0);
		translateAnimation.setDuration(400);
		llMenu.startAnimation(translateAnimation);
	}
	
	private void popOut(){
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		llMenu.measure(w, h);
		int width = llMenu.getMeasuredWidth();
		int height = llMenu.getMeasuredHeight();

		int mode = itemArr.size() % 3;
		int lineNum = itemArr.size() / 3;
		if (mode > 0) {
			lineNum = mode;
		}

		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,-height * lineNum);
		translateAnimation.setDuration(400);
		translateAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
			
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
//				popupWindow.dismiss();
			}
		});
		llMenu.startAnimation(translateAnimation);
	}

	@Override
	public void onClick(View v) {

	}

	private class CheckGridAdapter extends BaseAdapter {
		private TextView tvItem = null;
		private ViewGroup llItem = null;

		@Override
		public int getCount() {
			return itemArr.size();
		}

		@Override
		public String getItem(int position) {
			return itemArr.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.check_grid_pop_item, null);
				tvItem = (TextView) convertView.findViewById(R.id.tvItem);
				llItem = (ViewGroup) convertView.findViewById(R.id.llItem);

				tvItem.setText(getItem(position));
				if (position == SkuaidiSpf.getRecordChooseItem(mContext)) {
					tvItem.setBackgroundResource(R.drawable.shape_green_radius_hover_2);
					tvItem.setTextColor(mContext.getResources().getColor(R.color.white));
				} else {
					tvItem.setBackgroundResource(R.drawable.shape_white);
					tvItem.setTextColor(mContext.getResources().getColor(R.color.gray_1));
				}

				llItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						itemOnClickListener.itemOnClick(position);
						SkuaidiSpf.saveRecordChooseItem(mContext, position);// 保存选中的条目下标
					}
				});
			}
			return convertView;
		}
	}

	public void setItemOnclickListener(ItemOnClickListener itemOnClickListener) {
		this.itemOnClickListener = itemOnClickListener;
	}
	
	public void setLayoutDismissListener(LayoutDismissListener layoutDismissListener){
		this.layoutDismissListener = layoutDismissListener;
	}

	public interface ItemOnClickListener {
		void itemOnClick(int position);
	}
	
	public interface LayoutDismissListener{
		void onDismiss();
	}

}
