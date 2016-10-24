package com.kuaibao.skuaidi.application;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import java.io.File;

import cn.feng.skin.manager.config.SkinConfig;
import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * Created by ligg on 2016/4/19 17:06.
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
public class DynamicSkinChangeManager {
    public static void changSkinByLoginUser(){
        if(InitUtil.SHENGTONG.equals(SkuaidiSpf.getLoginUser().getExpressNo())){
            if(SkinConfig.isDefaultSkin(SKuaidiApplication.getContext())){
            File skin = new File(InitUtil.SKIN_PATH+InitUtil.SKIN_FILE_NAME);
            if(skin == null || !skin.exists()){
                //SkinManager.getInstance().restoreDefaultTheme();
                UtilToolkit.showToast("请检查皮肤资源是否存在"+InitUtil.SKIN_PATH+InitUtil.SKIN_FILE_NAME);
                return;
            }
            SkinManager.getInstance().load(skin.getAbsolutePath(),
                    new ILoaderListener() {
                        @Override
                        public void onStart() {
                            KLog.e("kb","startloadSkin");
                        }

                        @Override
                        public void onSuccess() {
                            KLog.e("kb","loadSkinSuccess");
                            //Utility.showToast(SKuaidiApplication.getContext(),"切换成功");
                        }

                        @Override
                        public void onFailed() {
                            KLog.e("kb","loadSkinFail");
                            //SkinManager.getInstance().restoreDefaultTheme();
                            UtilToolkit.showToast("主题切换失败,使用默认主题");
                        }
                    });
            }
        }else{
            if(!SkinConfig.isDefaultSkin(SKuaidiApplication.getContext())){
                SkinManager.getInstance().restoreDefaultTheme();
            }
        }
    }

    public static int getTextColorSkin(){
        String company=SkuaidiSpf.getLoginUser().getExpressNo();
        if("sto".equals(company)){
            return SKuaidiApplication.getContext().getResources().getColor(R.color.sto_text_color);
        }else{
            return SKuaidiApplication.getContext().getResources().getColor(R.color.title_bg);
        }
    }
}
