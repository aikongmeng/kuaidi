package com.kuaibao.skuaidi.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

/**
 * @author 顾冬冬
 * @ClassName: SelectConditionsListPOP
 * @Description: 选择器-列表中显示条件(控件：listview)(bg:汽泡/箭头在上/箭头在中间)
 * @date 2015-12-9 下午2:17:42
 */
@SuppressLint("InflateParams")
public class SelectConditionsListPOP extends PopupWindow {

    public final static int SHOW_RIGHT = 0x1001;
    public final static int SHOW_CENTER = 0x1002;

    private Context context = null;
    private PopupWindow mPop = null;
    private CheckListAdapter adapter = null;
    private LayoutInflater inflater = null;
    private ItemOnClickListener itemOnClickListener = null;
    private PopDismissClickListener popDismissClickListener = null;
    private AlphaAnimation aam = null;
    private List<String> conditions = null;

    private ViewGroup llMenu = null;
    private ListView lvMenu = null;
    private TextView tvcondition = null;
    private boolean textDefine;//是否采用自定义字体颜色
    private int textColor;//字体颜色
    private boolean show_bg = false;// 是否显示透明背景

    private float proportion = 0;// 比例（用于决定pop的宽度。范围：0~1。范例：pop_width = 0.5*screen_width）
    private int show_position = 0;// 显示位置
    private int selectedPosition = -1;

    /**
     * @param context
     * @param conditionArr  要在列表中显示的内容集合
     * @param proportion    比例值（用于决定pop的宽度。范围：0~1。范例：pop_width = 0.5*screen_width）
     * @param show_bg       是否显示透明背景
     * @param show_position 设置里面内容显示位置（SHOW_RIGHT|SHOW_CENTER[前者显示居右|后者显示居中]）
     */
    public SelectConditionsListPOP(Context context, List<String> conditionArr, float proportion, boolean show_bg, int show_position) {
        super(context);
        this.context = context;
        this.conditions = conditionArr;
        this.show_bg = show_bg;
        this.proportion = proportion;
        this.show_position = show_position;
        inflater = LayoutInflater.from(context);
        initView();
    }

    public void setConditions( List<String> conditionArr){
        this.conditions = conditionArr;
        adapter.notifyDataSetChanged();
    }


    /**
     * 设置菜单的背影
     *
     * @param drawableResource
     * @return
     */
    public SelectConditionsListPOP setBackgroundResource(int drawableResource) {
        llMenu.setBackgroundResource(drawableResource);
        return this;
    }

    /**
     * 设置选项条目的背影
     *
     * @param drawableResource
     */
    public void setSelectionBackGroundResource(int drawableResource) {
        lvMenu.setBackgroundResource(drawableResource);
        lvMenu.setAlpha(0.95f);
    }



    private void initView() {
        mPop = new PopupWindow(context);
        View layout = null;
        if (show_position == SHOW_RIGHT) {
            layout = inflater.inflate(R.layout.select_conditions_pop_right, null);// 尖儿在靠右显示的位置
        } else if (show_position == SHOW_CENTER) {// 尖儿在居中显示的位置

        } else {//
            layout = inflater.inflate(R.layout.select_conditions_pop, null);
        }

        llMenu = (ViewGroup) layout.findViewById(R.id.llMenu);
        lvMenu = (ListView) layout.findViewById(R.id.lvMenu);

        // mPop.setAnimationStyle(R.style.AnimBottom);
        mPop.setContentView(layout);
        if (show_bg == true) {
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            mPop.setBackgroundDrawable(dw);
            mPop.setWidth(LayoutParams.MATCH_PARENT);
            mPop.setHeight(LayoutParams.MATCH_PARENT);
        } else {
            ColorDrawable dw = new ColorDrawable(0x00000000);
            mPop.setBackgroundDrawable(dw);
            mPop.setWidth(LayoutParams.WRAP_CONTENT);
            mPop.setHeight(LayoutParams.WRAP_CONTENT);
        }


        adapter = new CheckListAdapter();
        lvMenu.setAdapter(adapter);

        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        int W = mDisplayMetrics.widthPixels;// 获取当前屏幕的像素宽度
        int H = mDisplayMetrics.heightPixels;// 获取当前屏幕的像素高度

        android.view.ViewGroup.LayoutParams layoutParams = llMenu.getLayoutParams();
        layoutParams.width = (int) (W * proportion);
        llMenu.setLayoutParams(layoutParams);

        // 点击界面隐藏popupwindow
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isEmpty(popDismissClickListener)) {
                    popDismissClickListener.onDismiss();
                }

//				dismissPop();
            }
        });
//		layout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    /**
     * 判断菜单是否正在显示
     */
    public boolean isShowing() {
        if (mPop != null) {
            return mPop.isShowing();
        } else {
            return false;
        }
    }

    /**
     * 关闭菜单
     */
    public void dismissPop() {
        popOut();
    }

    /**
     * v：显示在-控件"v"的下面 xoff:横坐标的偏移量 yoff:纵坐标的偏移量
     */
    public void showAsDropDown(final View v, final int xoff, final int yoff) {
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPop.showAsDropDown(v, xoff, yoff);
            }
        }, 100);
    }

    /**
     * 菜单进入界面
     */
    private void popIn() {
        aam = null;
        aam = new AlphaAnimation(0, 1);
        aam.setDuration(200);
        llMenu.startAnimation(aam);
    }

    /**
     * 菜单退出界面
     */
    private void popOut() {
        mPop.dismiss();
        /*aam = null;
        aam = new AlphaAnimation(1, 0);
		aam.setDuration(200);

		aam.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mPop.dismiss();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		llMenu.startAnimation(aam);*/
    }

    /**
     * @author 顾冬冬
     * @ClassName: CheckListAdapter
     * @Description: 筛选列表适配器
     * @date 2015-12-9 下午6:51:04
     */
    private class CheckListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return conditions.size();
        }

        @Override
        public String getItem(int position) {
            return conditions.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder= new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.select_conditions_list_pop_item, null);
                holder.tvcondition= (TextView) convertView.findViewById(R.id.condition);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }

            holder.tvcondition.setText(getItem(position));

            if(textDefine){
                holder.tvcondition.setTextColor(textColor);
            }else{
                if (position == selectedPosition) {
                    holder.tvcondition.setTextColor(context.getResources().getColor(R.color.default_green_2));
                } else {
                    holder.tvcondition.setTextColor(context.getResources().getColor(R.color.gray_1));
                }
            }

            holder.tvcondition.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(android.view.View v) {
                    itemOnClickListener.itemOnClick(position);
                    selectedPosition = position;// 保存选中的条目下标
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView tvcondition;
        }
    }

    public void setItemOnclickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public void setPopDismissClickListener(PopDismissClickListener popClickListener) {
        this.popDismissClickListener = popClickListener;
    }

    // 条目点击接口
    public interface ItemOnClickListener {
        void itemOnClick(int position);
    }

    public interface PopDismissClickListener {
        void onDismiss();
    }

}
