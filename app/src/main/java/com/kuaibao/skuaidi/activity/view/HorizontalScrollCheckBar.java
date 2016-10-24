package com.kuaibao.skuaidi.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dialog.SkuaidiBigAboutCheckListManager;
import com.kuaibao.skuaidi.dialog.SkuaidiBigPopAboutCheckList;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * 横向滑动选择栏
 * @author xy
 *
 */
@SuppressLint("DrawAllocation")
public class HorizontalScrollCheckBar extends HorizontalScrollView{
	private Context context;
	private LinearLayout layout;
	private Map<Integer,Integer> pressed = new HashMap<Integer, Integer>();
	private Map<Integer,Integer> defalut = new HashMap<Integer, Integer>();
	private int defaultImageId = -1;
	private int defaultPressedId = -1;
	private OnItemClickListener onItemClickListener;
	private OnCheckListPopItemClickListener onCheckListPopItemClickListener;
	private List<String> checkListPopItemTitles = new ArrayList<String>();
	private SkuaidiBigPopAboutCheckList checkList;
	private List<Integer> notUses = new ArrayList<Integer>();
	public HorizontalScrollCheckBar(Context context) {
		super(context);
		this.context = context;
		//System.out.println("Construction one");
		init();
	}
	
	public HorizontalScrollCheckBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		//System.out.println("Construction two");
		init();
	}
	
	public HorizontalScrollCheckBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		//System.out.println("Construction three");
		init();
	}
	
	private void init(){
		layout = new LinearLayout(context);
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(params);
		addView(layout);
	}
	@SuppressWarnings("deprecation")
	public void setItems(List<String> items,int defaultImageId){
		for (int i = 0; i < items.size(); i++) {
			View child = LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_check_bar_item, null);
			View left = child.findViewById(R.id.left_split_line);
			View right = child.findViewById(R.id.right_split_line);
			TextView center = (TextView) child.findViewById(R.id.tv_center_item);
			ImageView imageView = (ImageView) child.findViewById(R.id.iv_center_item);
			imageView.setImageResource(defaultImageId);
			child.setLayoutParams(new android.widget.LinearLayout.LayoutParams(((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()/3, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
			center.setText(items.get(i));
			if(i!=items.size()-1){
				left.setVisibility(View.GONE);
			}else{
				left.setVisibility(View.GONE);
				right.setVisibility(View.GONE);
			}
			child.setTag(i);
			layout.addView(child);
		}
	}
	
	
	public void setPressedImage(int defaultPressedId){
		this.defaultPressedId = defaultPressedId;
	}
	
	public void setPressedImage(int position,int imageId){
		pressed.put(position, imageId);
	}
	
	public void setPressedImage(Map<Integer,Integer> pressedItems){
		for (int key : pressedItems.keySet()) {
			pressed.put(key, pressedItems.get(key));
		}
	}
	
	public void setDefaultImage(int defaultId){
		this.defaultImageId = defaultId;
	}
	public void setDefalutImage(int position,int imageId){
		defalut.put(position, imageId);
	}
	
	public void setDefalutImage(Map<Integer,Integer> defaultItems){
		for (int key : defaultItems.keySet()) {
			pressed.put(key, defaultItems.get(key));
		}
	}
	
	private int lastClickIndex = -1;
	private int index;
	TextView tv;
	ImageView iv;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(onItemClickListener!=null){
			for (int i = 0; i < layout.getChildCount(); i++) {
				View view = layout.getChildAt(i);
				TextView center = (TextView) view.findViewById(R.id.tv_center_item);
				if(nullCheckMarkIcons.get(i)!=null){
					view.findViewById(R.id.iv_center_item).setVisibility(View.GONE);
				}
				if(SkuaidiBigAboutCheckListManager.getChecks(context)!=null
						&&SkuaidiBigAboutCheckListManager.getChecks(context).get(view.getTag())!=null&&checkMarks.get(i)!=null){
					for (Integer key : SkuaidiBigAboutCheckListManager.getChecks(context).get(view.getTag()).keySet()) {
						center.setText(SkuaidiBigAboutCheckListManager.getChecks(context).get(view.getTag()).get(key));
					}
					
				}
				
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						index = (Integer)v.getTag();
						tv = (TextView)v.findViewById(R.id.tv_center_item);
						iv = (ImageView)v.findViewById(R.id.iv_center_item);
						onItemClickListener.onClick(v,(Integer)v.getTag());
						
						if(checkList == null){
							checkList = new SkuaidiBigPopAboutCheckList(context, v, checkListPopItemTitles);
							checkList.setItemOnclickListener(new SkuaidiBigPopAboutCheckList.ItemOnclickListener() {
								
								@Override
								public void onClick(int position) {
									onCheckListPopItemClickListener.onClick(position);
									iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_down"));
								}
							});
						}else{
							checkList.setItemOnclickListener(new SkuaidiBigPopAboutCheckList.ItemOnclickListener() {
								
								@Override
								public void onClick(int position) {
									onCheckListPopItemClickListener.onClick(position);
									iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_down"));
								}
							});
							checkList.notifyDataSetChanged(checkListPopItemTitles,v);
						}
						
						if(index == lastClickIndex){
							tv.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
							if(checkList.isShowing()){
								//System.out.println("dismiss");
								checkList.dismiss();
								iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_down"));
							}else{
								//System.out.println("showpop");
								iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_up"));
								for (int j = 0; j < notUses.size(); j++) {
									if(notUses.get(j)== v.getTag()){
										iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_down"));
										if(checkList!=null){
											if(checkList.isShowing()){
												checkList.dismiss();
											}
										}
										lastClickIndex = index;
										return;
									}
								}
								checkList.showPopOnTopCenter();
								
							}
						}else{
							if(lastClickIndex!=-1){
								TextView lastTv = (TextView)layout.getChildAt(lastClickIndex).findViewById(R.id.tv_center_item);
								ImageView lastIv = (ImageView)layout.getChildAt(lastClickIndex).findViewById(R.id.iv_center_item);
								lastTv.setTextColor(context.getResources().getColorStateList(R.color.text_black));
								lastIv.setImageResource(R.drawable.default_down);
							}
							tv.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
							iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_up"));
							for (int j = 0; j < notUses.size(); j++) {
								if(notUses.get(j)== v.getTag()){
									iv.setImageResource(SkuaidiSkinManager.getSkinResId("checked_down"));
									if(checkList!=null){
										if(checkList.isShowing()){
											checkList.dismiss();
										}
									}
									lastClickIndex = index;
									return;
								}
							}
							checkList.showPopOnTopCenter();
						}
						lastClickIndex = index;
					}
				});
			}
			
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private Map<Integer,Integer> checkMarks = new HashMap<Integer,Integer>();
	private Map<Integer,Integer> nullCheckMarkIcons = new HashMap<Integer,Integer>();
	public void addCheckMark(int index){
		checkMarks.put(index,index);
	}
	
	public void addNullCheckMarkIcon(int index){
		nullCheckMarkIcons.put(index, index);
	}
	
	public void notUseCheckListPop(int position){
		notUses.add(position);
	}
	
	public void setCheckListPopItemTitles(List<String> checkListPopItemTitles){
		this.checkListPopItemTitles = checkListPopItemTitles;
	}
	
	public interface OnItemClickListener{
		void onClick(View v,int index);
	}
	
	public interface OnCheckListPopItemClickListener{
		void onClick(int position);
	}
	
	public void setOnCheckListPopItemClickListener(OnCheckListPopItemClickListener onCheckListPopItemClickListener){
		this.onCheckListPopItemClickListener = onCheckListPopItemClickListener;
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener = onItemClickListener;
	}
}
