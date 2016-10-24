package com.kuaibao.skuaidi.business.nettelephone.widget;

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
public class TeleGuideComponent implements Component {

    @Override public View getView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(param);
        ImageView imageView = new ImageView(inflater.getContext());
        imageView.setImageResource(R.drawable.jt_up);
        imageView.setScaleX(-1);
        imageView.setScaleY(1);

        TextView textView = new TextView(inflater.getContext());
//        AssetManager mgr=inflater.getContext().getAssets();//得到AssetManager
//        Typeface tf=Typeface.createFromAsset(mgr, "fonts/font_1.otf");//根据路径得到Typeface
//        textView.setTypeface(tf);//设置字体
        textView.setText("点此切换呼叫方式");
        textView.setPadding(0, DensityUtil.dip2px(inflater.getContext(),38),0,0);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(inflater.getContext().getResources().getColor(R.color.white));
        textView.setTextSize(20);
        ll.removeAllViews();
        ll.addView(textView);
        ll.addView(imageView);
        return ll;
    }

    @Override public int getAnchor() {
        return Component.ANCHOR_BOTTOM;
    }

    @Override public int getFitPosition() {
        return Component.FIT_END;
    }

    @Override public int getXOffset() {
        return -30;
    }

    @Override public int getYOffset() {
        return 0;
    }
}
