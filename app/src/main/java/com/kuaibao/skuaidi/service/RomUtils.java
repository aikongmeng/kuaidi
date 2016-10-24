package com.kuaibao.skuaidi.service;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.main.widget.ScalePopupGuide;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by lgg on 2016/9/28 16:47.
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

public class RomUtils {

    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * 华为rom
     * @return
     */
    public static boolean isEMUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_EMUI_VERSION_CODE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 小米rom
     * @return
     */
    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            /*String rom = "" + prop.getProperty(KEY_MIUI_VERSION_CODE, null) + prop.getProperty(KEY_MIUI_VERSION_NAME, null)+prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null);
            Log.d("Android_Rom", rom);*/
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 魅族rom
     * @return
     */
    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

//    public static String getSystemProperty(String propName) {
//        String line;
//        BufferedReader input = null;
//        try {
//            Process p = Runtime.getRuntime().exec("getprop " + propName);
//            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
//            line = input.readLine();
//            input.close();
//        } catch (IOException ex) {
//            return null;
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return line;
//    }

//    public static boolean isMIUIRom(){
//        String property = getSystemProperty("ro.miui.ui.version.name");
//        return !TextUtils.isEmpty(property);
//    }

    public static boolean shouldShowWindowElertDialog(){// 如果是小米,华为,三星非nexus,魅族,联想 这些厂商的手机要提示悬浮窗权限开启
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String displayStr = Build.DISPLAY;
        String brand = Build.BRAND;
        if (model.startsWith("Lenovo")) {
            return true;
        }
        if ("Meizu".equals(brand)) {
            return true;
        }
        if ((manufacturer != null && manufacturer.trim().contains("samsung"))
                && (model == null || (!model.trim().toLowerCase()
                .contains("google") && !model.trim().toLowerCase()
                .contains("nexus")))) {
            return true;
        }
        if (displayStr != null && displayStr.toLowerCase().contains("miui")) {
            return true;
        }
        if (brand.equals("Xiaomi")) {
            return true;
        }
        if(isMIUI()){
            return true;
        }
        if(isEMUI()){
            return true;
        }
        if(isFlyme()){
            return true;
        }
        return false;
    }
    //private static CustomDialog sCustomDialog;
    public static void createWindowAlertPermissionDialog(final Activity activity, final boolean belowApi21){
        final ScalePopupGuide scalePopupGuide=new ScalePopupGuide(activity,belowApi21);
        scalePopupGuide.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(scalePopupGuide.isBelowApi21()){
                    SkuaidiSpf.setBelowApi21NeedShowPermissionDialog(false);
                }else{
                    //SkuaidiSpf.setNeedShowPermissionDialog(false);
                }
            }
        });
        if(!scalePopupGuide.isShowing()) {
            scalePopupGuide.showPopupWindow();
        }
    }

    public static void goToApplicationDetail(Activity activity){
        Intent intent;
        try {
            Uri packageURI = Uri.parse("package:"+ "com.kuaibao.skuaidi");
            intent=  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
            SKuaidiApplication.getInstance().exit();
        } catch (ActivityNotFoundException e) {
            intent =  new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
            SKuaidiApplication.getInstance().exit();
        } catch (SecurityException e) {
            KLog.e("kb", e.getMessage());
            intent =  new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
            SKuaidiApplication.getInstance().exit();
        }
    }

    public static void SKuaidiAlertWindowPermissionCheck(Activity activity,boolean isMain){
        if(RomUtils.shouldShowWindowElertDialog()){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                if(!RomUtils.getAppOps(activity.getApplicationContext())){
                    if(isMain){
                        if(SkuaidiSpf.needShowPermissionDialog()){
                            RomUtils.createWindowAlertPermissionDialog(activity,false);
                        }
                    }else{
                        RomUtils.createWindowAlertPermissionDialog(activity,false);
                    }
                }
            }else{
                if(isMain){
                    if(SkuaidiSpf.BelowApi21NeedShowPermissionDialog()){
                        RomUtils.createWindowAlertPermissionDialog(activity,true);
                    }
                }else{
                    RomUtils.createWindowAlertPermissionDialog(activity,true);
                }
            }
        }
    }
}
