package com.kuaibao.skuaidi.util;

import com.kuaibao.skuaidi.R;

/**
 * Created by kb82 on 2016/8/23.
 */
public class SkuaidiSkinResourceManager {

    public static int getTitleButtonBackgroundLeft(){
        String company=SkuaidiSpf.getLoginUser().getExpressNo();
        if ("sto".equals(company)){
            return R.drawable.shape_radius_btn_left_white1_sto;
        }else{
            return R.drawable.shape_radius_btn_left_white1;
        }
    }

    public static int getTitleButtonBackgroundRight(){
        String company=SkuaidiSpf.getLoginUser().getExpressNo();
        if ("sto".equals(company)){
            return R.drawable.shape_radius_btn_right_white1_sto;
        }else{
            return R.drawable.shape_radius_btn_right_white1;
        }
    }

    public static int getTitleButtonBackgroundLeft_white(){
        return R.drawable.shape_radius_btn_left_white2;
    }

    public static int getTitleButtonBackgroundRight_white(){
        return R.drawable.shape_radius_btn_right_white2;
    }



}
