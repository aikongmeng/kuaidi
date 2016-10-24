package com.kuaibao.skuaidi.retrofit.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.Constants;

import java.io.File;

/**
 * Created by ligg on 2016/5/31 17:46.
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
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public class GlideUtil {
    public static void GlideUrlToImg(Context context, String url, ImageView iv){
        Glide.with(context).load(url).asBitmap()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_fail)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }

    public static void GlideHeaderImg(Context context,String userId,ImageView iv,int placeHolderDrawable,int errorDrawable){
        String headImgSuffix="counterman_" + userId + ".jpg";
        File file=new File(Constants.HEADER_PATH+headImgSuffix);
        if(!file.exists()){
            GlideCircleImgSkip(context,Constants.URL_HEADER_ROOT+headImgSuffix,iv,placeHolderDrawable,errorDrawable);
        }else{
            GlideLocalHeadImg(context,Constants.HEADER_PATH+headImgSuffix,iv);
        }
    }

    public static void GlideCircleImg(Context context,String url,ImageView iv,int placeHolderDrawable,int errorDrawable){//no cache
        Glide.with(context).load(url).asBitmap()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(placeHolderDrawable)
                .error(errorDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(context))
                .into(iv);
    }

    public static void GlideCircleImgSkip(Context context,String url,ImageView iv,int placeHolderDrawable,int errorDrawable){//no cache
        Glide.with(context).load(url).asBitmap()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(placeHolderDrawable)
                .error(errorDrawable)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new GlideCircleTransform(context))
                .into(iv);
    }


    public static void GlideLocalImg(Context context,String picPath,ImageView iv){
        Glide.with(context).load(picPath).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);
    }

    public static void GlideLocalHeadImg(Context context,String picPath,ImageView iv){
        Glide.with(context).load(picPath).asBitmap()
                .format(DecodeFormat.PREFER_RGB_565)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new GlideCircleTransform(context))
                .into(iv);
    }
}
