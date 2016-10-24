package com.kuaibao.skuaidi.circle.data;

import android.text.TextUtils;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.util.Constants;

import java.util.List;

import gen.greendao.bean.SKuaidiCircle;
import gen.greendao.dao.SKuaidiCircleDao;

/**
 * Circle的观察者
 * <p>
 * Created by ligg on 2016/8/18.
 */
public class ObservableCircleRepoDb {
    public static final int PAGE_SIZE=30;
    public ObservableCircleRepoDb() {
    }
    // 从数据库获得数据
    public List<SKuaidiCircle> getCircleList() {
        SKuaidiCircleDao sKuaidiCircleDao= SKuaidiApplication.getInstance().getDaoSession().getSKuaidiCircleDao();
        List<SKuaidiCircle> listCircles = sKuaidiCircleDao.queryBuilder().limit(PAGE_SIZE).list();
        //List<SKuaidiCircle> listCircles = sKuaidiCircleDao.loadAll();
        //KLog.i("kb","getCircleList size:--->"+listCircles.size());
        return listCircles;
    }

    public void updateCircleData(SKuaidiCircle circle){
        if(circle!=null){
            SKuaidiCircleDao sKuaidiCircleDao= SKuaidiApplication.getInstance().getDaoSession().getSKuaidiCircleDao();
            sKuaidiCircleDao.insertOrReplace(circle);
        }
    }

    // 插入列表
    public void insertCircleList(List<SKuaidiCircle> circles,boolean refreshing) {
        SKuaidiCircleDao sKuaidiCircleDao= SKuaidiApplication.getInstance().getDaoSession().getSKuaidiCircleDao();
        if(refreshing){
            sKuaidiCircleDao.deleteAll();
        }
        sKuaidiCircleDao.insertOrReplaceInTx(convertCircles(circles));
        //KLog.i("kb","insertCircleList size:--->"+circles.size());
    }

    public List<SKuaidiCircle> convertCircles(List<SKuaidiCircle> circles){
        if(circles!=null && circles.size()>0){
            for(SKuaidiCircle circle:circles){
                String pics = circle.getPic();
                if(TextUtils.isEmpty(pics)) continue;
                if (pics.contains("$%#")) {
                    String[] array = pics.split("\\$%#");
                    String picsmall = "";
                    String picbig = "";
                    for (int i = 1; i < array.length; i++) {
                        picbig = picbig + "#%#" + Constants.URL_TUCAO_ROOT + array[i];
                        picsmall = picsmall + "#%#" + Constants.URL_TUCAO_ROOT + "thumb." + array[i];
                    }
                    circle.setImageurls(picsmall);
                    circle.setImageurlsbig(picbig);
                }
            }
        }
        return circles;
    }
}
