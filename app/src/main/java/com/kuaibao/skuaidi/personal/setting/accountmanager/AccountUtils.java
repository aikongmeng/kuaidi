package com.kuaibao.skuaidi.personal.setting.accountmanager;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.util.Date;
import java.util.List;

import gen.greendao.bean.KBAccount;
import gen.greendao.bean.LoginUserAccount;
import gen.greendao.dao.KBAccountDao;
import gen.greendao.dao.LoginUserAccountDao;

/**
 * Created by lgg on 2016/6/23 16:13.
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
public class AccountUtils {
    public static void insertOrUpdateLoginAccount(){
        LoginUserAccountDao userDao = SKuaidiApplication.getInstance().getDaoSession().getLoginUserAccountDao();
        List<LoginUserAccount> users = userDao.queryBuilder()
                .where(LoginUserAccountDao.Properties.PhoneNumber.notEq("''"))
                .build().list();
        if (users != null) {
            for (LoginUserAccount account : users) {
                account.setCurrentUser(false);
                userDao.update(account);
            }
        }
        LoginUserAccount user = new LoginUserAccount();
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        user.setNickName(userInfo.getUserName());
        user.setPhoneNumber(userInfo.getPhoneNumber());
        user.setUserId(userInfo.getUserId());
        user.setLastUpdateTime(new Date());
        String downloadHeaderUrl = Constants.URL_HEADER_ROOT + "counterman_" + userInfo.getUserId() + ".jpg";
        user.setHeadImgUrl(downloadHeaderUrl);
        user.setCurrentUser(true);
        user.setCodeId(userInfo.getCodeId());
        user.setIdImg(userInfo.getIdImg());
        user.setRealnameAuthStatus(userInfo.getRealnameAuthStatus());
        userDao.insertOrReplace(user);
    }

    public static void insertOrUpdateKBAccount(){
        KBAccountDao userDao = SKuaidiApplication.getInstance().getDaoSession().getKBAccountDao();
        KBAccount user = new KBAccount();
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        user.setNickName(userInfo.getUserName());
        user.setPhoneNumber(userInfo.getPhoneNumber());
        user.setPassword(userInfo.getPwd());
        user.setUserId(userInfo.getUserId());
        user.setLastUpdateTime(new Date());
        String downloadHeaderUrl = Constants.URL_HEADER_ROOT + "counterman_" + userInfo.getUserId() + ".jpg";
        user.setHeadImgUrl(downloadHeaderUrl);
        user.setCodeId(userInfo.getCodeId());
        userDao.insertOrReplace(user);
    }

}
