package com.kuaibao.skuaidi.activity.picksendmapmanager.guidcomponent;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blog.www.guideview.Component;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.DensityUtil;

/**
 * Created by lgg on 2016/7/13 16:45.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #                     no bug forever                #
 * #                                                   #
 */
public class MapGuideComponent implements Component {
    private int index=0;
    public MapGuideComponent(int index){
        this.index=index;
    }
    @Override public View getView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        if(index==4){
            ll.setOrientation(LinearLayout.HORIZONTAL);
        }
        ll.setLayoutParams(param);
        ImageView imageView = new ImageView(inflater.getContext());
        if(index==1){
            imageView.setImageResource(R.drawable.icon_mask_gesture);
            imageView.setPadding(0,DensityUtil.dip2px(inflater.getContext(),8),0,0);
        }else if(index==2 || index==3){
            imageView.setImageResource(R.drawable.jt_down);
        }else if(index==4){
            imageView.setImageResource(R.drawable.jt_up);
        }

        TextView textView = new TextView(inflater.getContext());
//        AssetManager mgr=inflater.getContext().getAssets();//得到AssetManager
//        Typeface tf=Typeface.createFromAsset(mgr, "fonts/font_1.otf");//根据路径得到Typeface
//        textView.setTypeface(tf);//设置字体
        textView.setPadding(0,DensityUtil.dip2px(inflater.getContext(),8),0,0);
        if(index==1){
            textView.setText("长按地图中任意点\n即可将该处设置成取派点");
        }else if(index==2){
            textView.setText("点此查看附近的取派点");
        }else if(index==3){
            textView.setText("点击可将该条设置为取派点\n再次点击可取消");
        }else if(index==4){
            textView.setText("点此返回");
            textView.setPadding(0, DensityUtil.dip2px(inflater.getContext(),16),0,0);
        }
        textView.setLineSpacing(0, 1.2f);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(inflater.getContext().getResources().getColor(R.color.white));
        textView.setTextSize(20);
        ll.removeAllViews();
        if(index==1 || index==2 || index==3){
            ll.addView(textView);
            ll.addView(imageView);
        }else if(index == 4){
            ll.addView(imageView);
            ll.addView(textView);
        }
        return ll;
    }

    @Override public int getAnchor() {
        if(index==1 || index==4 ){
            return Component.ANCHOR_BOTTOM;
        }else if(index==2 || index==3){
            return Component.ANCHOR_TOP;
        }
        return 0;
    }

    @Override public int getFitPosition() {
        return Component.FIT_START;
    }

    @Override public int getXOffset() {
        if(index==2){
            return 50;
        }else if(index==4){
            return 15;
        }else {
            return 30;
        }
    }

    @Override public int getYOffset() {
        if(index==1){
            return 200;
        }
        return 0;
    }
}
