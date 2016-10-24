package com.kuaibao.skuaidi.activity.make.realname.AsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.kuaibao.skuaidi.activity.ShowImageActivity;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.RealNameInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.PictureUtil;
import com.kuaibao.skuaidi.util.Utility;
import com.yunmai.android.vo.IDCard;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * 异步压缩身份证识别后的图片
 * Created by gudongdong
 * on 2016/10/11.
 */

public class IDCardImgAsyncTask extends AsyncTask {
    String imagePath;
    File outImgFile;
    IDCard idCard;
    Activity activity;
    RealNameInfo realNameInfoObj;
    Map<String,String> recordContent;

    public IDCardImgAsyncTask(Activity activity, IDCard idCard, File outImgFile){
        this.idCard = idCard;
        this.activity = activity;
        this.imagePath = outImgFile.getPath();
        this.outImgFile = outImgFile;
    }

    public void setRecordContent(Map<String,String> content){
        this.recordContent = content;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(activity instanceof SkuaiDiBaseActivity){
            ((SkuaiDiBaseActivity)activity).showProgressDialog("");
        }else if(activity instanceof RxRetrofitBaseActivity){
            ((RxRetrofitBaseActivity)activity).showProgressDialog("");
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return PictureUtil.compressImage(PictureUtil.getSmallBitmap(imagePath), outImgFile);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(activity instanceof SkuaiDiBaseActivity){
            ((SkuaiDiBaseActivity)activity).dismissProgressDialog();
        }else if(activity instanceof RxRetrofitBaseActivity){
            ((RxRetrofitBaseActivity)activity).dismissProgressDialog();
        }
        String imgPath = PictureUtil.bitmapToString(outImgFile.getPath());

        realNameInfoObj = new RealNameInfo();
        realNameInfoObj.setName(idCard.getName());// 姓名
        realNameInfoObj.setSex(idCard.getSex());// 性别
        realNameInfoObj.setNation(idCard.getEthnicity());// 民族
        String birth = idCard.getBirth().replaceAll("年","-").replaceAll("月","-").replaceAll("日","-");
        realNameInfoObj.setBorn(birth.substring(0,birth.length()-1));// 生日
        realNameInfoObj.setAddress(idCard.getAddress());
        realNameInfoObj.setIdno(idCard.getCardNo());

        Intent mIntent = new Intent(activity, ShowImageActivity.class);
        mIntent.putExtra("realNameInfo", realNameInfoObj);
        mIntent.putExtra("imagePath",imgPath);
        mIntent.putExtra("comeType",Utility.getLocalClassName(activity));
        if (null != recordContent)
            mIntent.putExtra("recordContent", (Serializable) recordContent);
        activity.startActivity(mIntent);
    }


}
