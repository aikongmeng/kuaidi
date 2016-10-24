package com.kuaibao.skuaidi.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.CustomDialog;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.progressbar.CustomProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("NewApi")
public class Utility {

    private static MediaPlayer mp;
    public static InputMethodManager m;
    private static ClipboardManager mClipboard = null;

    /**
     * 播放识别成功音乐
     **/
    public static void playMusicDing() {
        if (mp == null) {
            mp = MediaPlayer.create(SKuaidiApplication.getContext(), R.raw.ding);
        }
        mp.start();
    }

    public static void ShowDialog(final Context context, final Handler handler, String title, String message,
                                  String btnPositive, String btnNegative, final Object obj, final int type) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(btnPositive, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Message msg = new Message();
                msg.what = type;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        });

        builder.setNegativeButton(btnNegative, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        CustomDialog dialog = builder.create();

        if (type == Constants.SHOP_MIGRATE) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // builder.create().show();
    }

    /**
     * 获取当前APP（应用）的版本号
     *
     * @return
     * @throws Exception
     * @author gudd
     */
    public static int getVersionCode() {
        // 获取packagemanager的实例
        PackageManager packageManager = SKuaidiApplication.getContext().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        int version = 0;
        try {
            packInfo = packageManager.getPackageInfo(SKuaidiApplication.getContext().getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = SKuaidiApplication.getContext().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(SKuaidiApplication.getContext().getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    /**
     * 判断服务是否启动
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判断网络情况
     *
     * @return false 表示没有网络 true 表示有网络
     */

    public static boolean isNetworkConnected() {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) SKuaidiApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }

        return false;
    }

    public static String getIp(Context context) {
        String ip = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "."
                    + (ipAddress >> 24 & 0xFF);
        } else {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            ip = inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {

            }
        }
        return ip;
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */

    public static void copy(String content) {

        if (null == mClipboard) {
            mClipboard = (ClipboardManager) SKuaidiApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }

        ClipData clip = ClipData.newPlainText("", content);
        mClipboard.setPrimaryClip(clip);
    }

    /**
     * 实现粘贴功能，本方法未用到
     *
     * @return
     */

    public static String paste() {
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) SKuaidiApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }

        String resultString = "";
        // 检查剪贴板是否有内容
        if (!mClipboard.hasPrimaryClip()) {

        } else {
            ClipData clipData = mClipboard.getPrimaryClip();
            int count = clipData.getItemCount();

            for (int i = 0; i < count; ++i) {

                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item.coerceToText(SKuaidiApplication.getContext());

                resultString += str;
            }

        }
        return resultString;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void hideSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置时间为今天，昨天，...（不带秒的时间）
     *
     * @param date
     * @param v
     * @author gudd
     */
    public static void setTimeDate(String date, TextView v) {
        try {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            if (now.substring(0, 10).equals(date.substring(0, 10))) {
                v.setText("今天 " + date.substring(10, 16));
            } else if (now.substring(0, 8).equals(date.substring(0, 8))
                    && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
                v.setText("昨天 " + date.substring(10, 16));
            } else {
                v.setText(date.substring(5, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**date : yyyy-MM-dd HH:mm:ss**/
    public static String getTimeDate(String date){
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        if (now.substring(0,10).equals(date.substring(0,10)))
            return "今天 "+date.substring(10,16);
        if (now.substring(0,8).equals(date.substring(0,8)))
            return "昨天 "+date.substring(10,16);
        return date.substring(5,16);
    }

    /**
     * 设置时间为今天，昨天，...（不带秒的时间）
     *
     * @param date
     * @param v
     * @author gudd
     */
    public static void setTimeDate3(String date, TextView v) {
        try {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            if (now.substring(0, 10).equals(date.substring(0, 10))) {
                v.setText("今天 ");
            } else if (now.substring(0, 8).equals(date.substring(0, 8))
                    && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
                v.setText("昨天 ");
            } else {
                if (date.length() >= 16) {
                    v.setText(date.substring(0, 16));
                } else if (date.length() >= 10) {
                    v.setText(date.substring(0, 10));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置时间为今天，昨天，...（带年份，不带秒的时间）
     *
     * @param date
     * @param v
     * @author gudd
     */
    public static void setTimeDate1(String date, TextView v) {
        if (TextUtils.isEmpty(date)) {
            v.setVisibility(View.GONE);
            return;
        }
        try {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            if (now.substring(0, 10).equals(date.substring(0, 10))) {
                String str = date.length() >= 16 ? date.substring(10, 16) : "";
                v.setText("今天 " + str);
            } else if (now.substring(0, 8).equals(date.substring(0, 8))
                    && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
                String str1 = date.length() >= 16 ? date.substring(10, 16) : "";
                v.setText("昨天 " + str1);
            } else {
                if (date.length() >= 16) {
                    v.setText(date.substring(0, 16));
                } else if (date.length() >= 10) {
                    v.setText(date.substring(0, 10));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置时间为今天，昨天，...(带秒的时间)
     *
     * @param date
     * @param v
     * @author gudd
     */
    public static void setTimeDate2(String date, TextView v) {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        if (now.substring(0, 10).equals(date.substring(0, 10))) {
            v.setText("今天" + date.substring(10,16));
        } else if (now.substring(0, 8).equals(date.substring(0, 8))
                && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
            v.setText("昨天" + date.substring(10,16));
        } else {
            v.setText(date.substring(5,16));
        }
    }

    public static String getTimeDate2(String date) {
        String time = "";
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        if (now.substring(0, 10).equals(date.substring(0, 10))) {
            time = "今天 " + date.substring(10);
        } else if (now.substring(0, 8).equals(date.substring(0, 8))
                && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
            time = "昨天 " + date.substring(10);
        } else {
            time = date.substring(5);
        }
        return time;
    }


    public static void setTimeDate4(String date, TextView v) {
        if (TextUtils.isEmpty(date)) {
            v.setVisibility(View.GONE);
            return;
        }
        v.setVisibility(View.VISIBLE);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        if (now.substring(0, 10).equals(date.substring(0, 10))) {
            v.setText("今天 " + date.substring(10));
        } else if (now.substring(0, 8).equals(date.substring(0, 8))
                && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
            v.setText("昨天 " + date.substring(10));
        } else {
            v.setText(date.substring(5));
        }
    }

    /**
     * 设置字体下划线
     *
     * @param context
     * @param str     目标字符串
     * @param start   开始位置
     * @param end     结束位置
     * @return
     * @author gudd
     */
    public static SpannableString getUnderLineSpan(Context context, String str, int start, int end) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 设置字体大小，用dip
     *
     * @param context
     * @param str     目标字符串
     * @param start   开始位置
     * @param end     结束位置
     * @param dipSize 像素大小
     * @return
     * @author gudd
     */
    public static SpannableString getSizeSpanUseDip(Context context, String str, int start, int end, int dipSize) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new AbsoluteSizeSpan(dipSize, true), start, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 返回当前时间字符串 不带空格、分割线、冒号-"yyyyMMddHHmmss"
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getSMSCurTime() {
        String filename = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date();
        filename = formatter.format(curDate);
        return filename;
    }

    /**
     * 返回当前时间字符串 带空格、分割线、冒号-"yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getSMSCurTime2() {
        String currTime = "";
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        currTime = formater.format(curDate);
        return currTime;
    }

    /**
     * 计算时间差
     *
     * @param limitTime
     */
    @SuppressLint("SimpleDateFormat")
    public static String CalculationTime(String limitTime) {
        String timedifference = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String currTime = getSMSCurTime2();
        try {
            // Date d1 = df.parse(currTime);
            // Date d2 = df.parse(previouslyTime);
            // long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
            long limittime = Long.parseLong(limitTime);
            long diff = limittime;
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            if (days != 0 && hours != 0 && minutes != 0) {
                timedifference = days + "天" + hours + "小时" + minutes + "分钟前";
            } else if (days != 0 && hours != 0 && minutes == 0) {
                timedifference = days + "天" + hours + "小时前";
            } else if (days != 0 && hours == 0 && minutes != 0) {
                timedifference = days + "天" + minutes + "分钟前";
            } else if (days != 0 && hours == 0 && minutes == 0) {
                timedifference = days + "天前";
            } else if (days == 0 && hours != 0 && minutes != 0) {
                timedifference = hours + "小时" + minutes + "分钟前";
            } else if (days == 0 && hours != 0 && minutes == 0) {
                timedifference = hours + "小时前";
            } else {
                if (minutes == 0) {
                    timedifference = "1分钟前";
                } else {
                    timedifference = minutes + "分钟前";
                }
            }
        } catch (Exception e) {
        }
        return timedifference;
    }

    /**
     * 计算剩余时间
     *
     * @param limitTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String CalculationSurplusTime(String limitTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long limittime = Long.parseLong(limitTime);
            long diff = limittime;
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            if (days != 0 && hours != 0 && minutes != 0) {
                limitTime = "还剩" + days + "天" + hours + "小时" + minutes + "分钟";
            } else if (days != 0 && hours != 0 && minutes == 0) {
                limitTime = "还剩" + days + "天" + hours + "小时";
            } else if (days != 0 && hours == 0 && minutes != 0) {
                limitTime = "还剩" + days + "天" + minutes + "分钟";
            } else if (days != 0 && hours == 0 && minutes == 0) {
                limitTime = "还剩" + days + "天";
            } else if (days == 0 && hours != 0 && minutes != 0) {
                limitTime = "还剩" + hours + "小时" + minutes + "分钟";
            } else if (days == 0 && hours != 0 && minutes == 0) {
                limitTime = "还剩" + hours + "小时";
            } else {
                limitTime = "还剩" + minutes + "分钟";
            }
        } catch (Exception e) {
        }
        return limitTime;
    }

    /**
     * 获取session_id
     *
     * @param context
     * @return
     */
    public static Map<String, String> getSession_id(Context context) {
        Map<String, String> sessionId = new HashMap<>();
        sessionId.put("session_id", SkuaidiSpf.getLoginUser().getSession_id());
        // System.out.println("session_Id : " +
        // SkuaidiSpf.getLoginUser().getSession_id());
        return sessionId;
    }

    /**
     * 失败或问题情况下的弹窗提示
     *
     * @param context
     * @param info    提示的内容
     * @param root    显示的根view
     */
    public static void showFailDialog(Context context, String info, View root) {
        SkuaidiDialogGrayStyle sdialog = new SkuaidiDialogGrayStyle(context);
        sdialog.setTitleGray("温馨提示");
        sdialog.setTitleGray("温馨提示");
        if ("404".equals(info)) {
            sdialog.setContentGray("请求失败，请重试！");
        } else {
            sdialog.setContentGray(info);
        }
        sdialog.isUseMiddleBtnStyle(true);
        sdialog.setMiddleButtonTextGray("我知道了");
        sdialog.showDialogGray(root);
    }
private static CustomProgressDialog mProgressDialog;
    /**
     * @Title: showProgressDialog
     * @Description:
     * @Author 顾冬冬
     * @CreateDate 2015-7-13 下午7:52:47
     * @Param @param context
     * @Param @param message
     * @Param @param cancleable 设置ProgressDialog 是否可以按退回按键取消
     * @Return void
     */
    public static void showProgressDialog(Context context, String message, boolean cancleable) {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
        mProgressDialog = new CustomProgressDialog((Activity) context, message);
        mProgressDialog.setCancelable(cancleable);
        if(!((Activity) context).isFinishing()) mProgressDialog.show();
    }

    /**
     * 关闭提示框progress
     *
     * @param context
     */
    public static void dismissProgressDialog(Context context) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mProgressDialog) {
                mProgressDialog = null;
            }
        }

    }

    public static boolean progressIsShow() {
        return null != mProgressDialog;
    }

    /**
     * 发送短信
     *
     * @param smsToUri 手机号
     *                 （一条或多条，手机号中间以';'分隔）
     * @param content
     */
    public static void sendSMSMessage(Context context, String smsToUri, String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", smsToUri);
        intent.putExtra("sms_body", content);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    /**
     * 将图片按照分辨率尺寸来进行压缩
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getImage(String srcPath, float width, float height, int compressIMGSize) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = height;// 这里设置高度为800f
        float ww = width;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (w / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (h / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        if (bitmap != null) {
            float scaleWidth = width / bitmap.getWidth();
            float scaleHeight = height / bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = null;
            try {
                newbm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                bitmap = null;
            } catch (java.lang.OutOfMemoryError e) {
                return compressImage(bitmap, compressIMGSize);
            }
            return compressImage(newbm, compressIMGSize);// 压缩好比例大小后再进行质量压缩
        } else {
            return bitmap;
        }
    }

    /**
     * 计算压缩比例值
     *
     * @param options   解析图片的配置信息
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原图片的 宽和高
        final int height = options.outHeight;
        final int width = options.outWidth;
        // 初始化压缩比例为1
        int inSampleSize = 1;

        // 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 计算最大采样率，采样率须为2的指数幂
            // 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * @param srcPath 图片路径
     * @return
     * @desc 只按照图片大小来进行压缩。
     */
    public static Bitmap getImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 1;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);
        return bitmap;
    }

    /**
     * @param srcPath 图片路径
     * @return
     * @desc 只按照图片大小来进行压缩。
     */
    public static Bitmap comPressImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = calculateInSampleSize(newOpts, 120, 70);// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);
        return bitmap;
    }

    /**
     * 将bitmap按照大小来进行压缩（直到小于100kb）
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int compressIMGSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 先按比例缩放
        image = imageZoom(image, compressIMGSize);
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > compressIMGSize) { // 循环判断如果压缩后图片是否大于compressIMGSize,大于继续压缩s
            if (options == 0) {
                break;
            } else {
                // 每次都减少10
                if (options - 10 >= 0) {
                    options = options - 10;
                } else {
                    options = 10;
                }
            }
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        image.recycle();
        image = null;
        return bitmap;
    }

    public static Bitmap imageZoom(Bitmap image, int maxSize) {
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            image = zoomImage(image, image.getWidth() / Math.sqrt(i), image.getHeight() / Math.sqrt(i));
        }
        return image;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 将Object对象转换为JSON字符串
     *
     * @return
     */
    public static String Object2String(Object obj) {
        String result = "";
        Gson gson = new GsonBuilder().create();
        result = gson.toJson(obj);
        return result;
    }

    /**
     * byte转bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 将bitmap转换成base64二进制流
     *
     * @return
     */
    public static String bitMapToString(Bitmap bitmap) {
        if (null != bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        }
        return "";
    }

    /**
     * 将base64二进制流转换成bitmap
     *
     * @param str
     * @param v
     */
    public void stringTobitMap(String str, ImageView v) {
        // base64转成bitmap
        if (!str.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(str, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            v.setImageBitmap(bitmap);
        }
    }

    /**
     * 把m转换成km
     *
     * @param distance
     * @return
     */
    public static String formatDistance(String distance) {
        try {
            int parsedouble = Integer.parseInt(distance);
            if (parsedouble > 1000) {
                double d = doubleMultiply(parsedouble, 0.001f);
                double d2 = doubleRound(d, 2);
                return d2 + "km";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return distance + "m";
    }

    /**
     * double 乘法运算
     *
     * @param xx
     * @param yy
     * @return
     */
    public static double doubleMultiply(double xx, double yy) {
        BigDecimal b1 = new BigDecimal(Double.toString(xx));
        BigDecimal b2 = new BigDecimal(Double.toString(yy));
        double ss = b1.multiply(b2).doubleValue();
        return ss;
    }

    /**
     * @param xx
     * @param scale 保留有多少个小数位
     * @return
     */
    public static double doubleRound(double xx, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(xx));
        // http://www.360doc.com/content/10/0303/19/59141_17459593.shtml
        // 只要第scale位的数大于0 则第secale位就+1 BigDecimal.ROUND_UP 如12.234 = 12.24
        // -12.234 = -12.24
        // 直接舍弃第scale后所有的小树数 BigDecimal.ROUND_down 如12.234 = 12.24 -12.234 =
        // -12.24
        b1 = b1.setScale(scale, BigDecimal.ROUND_DOWN);
        double doubleValue = b1.doubleValue();
        b1 = null;
        return doubleValue;
    }

    /**
     * EditText 控件
     *
     * @param hintContent hint 内容
     * @param hintSize    hint 内容size
     */
    public static void setEditTextHintSize(EditText editText, String hintContent, int hintSize) {
        // 设置hint字体大小
        SpannableString ss = new SpannableString(hintContent);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(hintSize, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        editText.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }

    /**
     * 判断对象是否为空
     **/
    public static boolean isEmpty(Object obj) {
        return null == obj || "null".equals(obj) || "".equals(obj);
    }

    /**
     * 将带有小数点的数字转成以分为单位的整型-用于微信支付
     *
     * @param total_money
     * @return
     */
    public static String getMinuteMoney(String total_money) {
        String newMoney = "";
        if (total_money.contains(".")) {
            String[] money = total_money.split("\\u002E");// \\u002E是点的转义
            if (total_money.substring(total_money.length() - 1).equals(".")) {
                newMoney = money[0];
                newMoney = String.valueOf(Integer.parseInt(newMoney) * 100);
            } else {
                switch (money[1].length()) {
                    case 1:
                        newMoney = money[0] + money[1];
                        newMoney = String.valueOf(Integer.parseInt(newMoney) * 10);
                        break;
                    case 2:
                        newMoney = money[0] + money[1];
                        newMoney = String.valueOf(Integer.parseInt(newMoney));
                        break;
                    default:
                        break;
                }
            }
        } else {
            newMoney = Integer.parseInt(total_money) * 100 + "";
            // System.out.println("gudd new Money  " + newMoney);
        }
        return newMoney;
    }

    /**
     * 时间格式化
     *
     * @param format , "yyyyMMdd" or "yyyy-MM-dd HH:mm:ss" or "yyyy/MM/dd HH:mm:ss"
     * @param date
     * @return
     */
    public static final String getFromatString(String format, String date) {

        SimpleDateFormat sd = new SimpleDateFormat(format);

        Date mDate = null;
        String dateString = "";
        try {
            mDate = sd.parse(date);
            dateString = sd.format(mDate);
        } catch (ParseException e) {
        }
        return dateString;
    }

    /**
     * @Description: 将毫秒转化成分秒时间格式--00：00
     * @Title: formatTime
     * @Author 顾冬冬
     * @CreateDate 2015-7-16 下午5:17:19
     * @Param @param time
     * @Param @return
     * @Return String
     */
    public static String formatTime(long time) {
        String strHour = "" + (time / 3600);
        String strMinute = "" + time % 3600 / 60;
        String strSecond = "" + time % 3600 % 60;
        strHour = strHour.length() < 2 ? "0" + strHour : strHour;
        strMinute = strMinute.length() < 2 ? "0" + strMinute : strMinute;
        strSecond = strSecond.length() < 2 ? "0" + strSecond : strSecond;
        String strRsult = "";
        if (!strHour.equals("00")) {
            strRsult += strHour + ":";
        }
        // if (!strMinute.equals("00")) {
        strRsult += strMinute + ":";
        // }
        strRsult += strSecond;
        return strRsult;
    }

    /**
     * 判断手机sd卡是否存在，true存在,false不存在
     **/
    public static boolean getSDIsExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @return String
     * @Title: formatMoney
     * @Description:将金额的长度里面加“，”,方便阅读！--字符串超过6位和字符串超过3位时候的不同显示
     * @author: 顾冬冬
     */
    public static String formatMoney(String money) {
        String showMoney = money;
        if (!Utility.isEmpty(money)) {
            if (showMoney.contains(".")) {
                if (showMoney.substring(0, showMoney.indexOf(".")).length() > 6) {
                    showMoney = showMoney.substring(0, showMoney.indexOf(".") - 6) + ","
                            + showMoney.substring(showMoney.indexOf(".") - 6, showMoney.indexOf(".") - 3) + ","
                            + showMoney.substring(showMoney.indexOf(".") - 3);
                } else if (money.substring(0, showMoney.indexOf(".")).length() > 3) {
                    showMoney = showMoney.substring(0, showMoney.indexOf(".") - 3) + ","
                            + showMoney.substring(showMoney.indexOf(".") - 3);
                }
            } else {
                int moneyLength = showMoney.length();
                if (showMoney.length() > 9) {
                    showMoney = showMoney.substring(0, moneyLength - 9) + ","
                            + showMoney.substring(moneyLength - 9, moneyLength - 6) + ","
                            + showMoney.substring(moneyLength - 6, moneyLength - 3) + ","
                            + showMoney.substring(moneyLength - 3);
                } else if (showMoney.length() > 6) {
                    showMoney = showMoney.substring(0, moneyLength - 6) + ","
                            + showMoney.substring(moneyLength - 6, moneyLength - 3) + ","
                            + showMoney.substring(moneyLength - 3);
                } else if (showMoney.length() > 3) {
                    showMoney = showMoney.substring(0, moneyLength - 3) + "," + showMoney.substring(moneyLength - 3);
                }
            }
            return showMoney;
        } else {
            return "0";
        }
    }

    /**
     * 获取屏幕状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * KeyBoard:强制显示或者关闭系统键盘
     * <p/>
     * 作者： 顾冬冬
     *
     * @param txtSearchKey
     * @param status
     */
    public static void showKeyBoard(final EditText txtSearchKey, final boolean status) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (m == null) {
                    m = (InputMethodManager) txtSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                if (status) {
                    m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
                }
                // else {
                // m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
                // }
            }
        }, 300);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23)
            return ContextCompat.getColor(context, id);
        else
            return context.getResources().getColor(id);
    }

    public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 22)
            return ResourcesCompat.getDrawable(context.getResources(), id, null);
        else
            return context.getResources().getDrawable(id);
    }

    public static final String getAbsolutePathByURI(Context context, Intent data) {
        final int version = Build.VERSION.SDK_INT;
        String filePath = "";
        if (version > 19) {
            filePath = Uri.decode(data.getDataString());
            filePath = filePath.substring(7, filePath.length());
        } else {
            Uri uri = data.getData();
            if (uri.getScheme().toString().compareTo("content") == 0) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.Media.DATA}, null, null, null);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(0);
                }
            } else if (data.getScheme().toString().compareTo("file") == 0) {         //file:///开头的uri
//                filePath = data.toString();
                filePath = data.toString().replace("file://", "");
                //替换file://
                if (!filePath.startsWith("/mnt")) {
                    //加上"/mnt"头
                    filePath += "/mnt";
                }
            }
        }

        return filePath;
    }

//	private static long lastClickTime;
//	public static boolean isFastDoubleClick() {
//		long time = System.currentTimeMillis();
//		long timeD = time - lastClickTime;
//		lastClickTime = time;
//		return timeD <= 500;
//	}

    /**
     * 生成一个唯一标识only_code 用来判断一个用户1分钟内最多扫描5单
     */
    public static String getOnlyCode() {
        Date date = new Date();
        int rad = (int) (Math.random() * 90) + 10;
        SharedPreferences sp = null;
        sp = SKuaidiApplication.getInstance().getSharedPreferences("code", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String only_code = sp.getString("only_code", null);
        if (TextUtils.isEmpty(only_code)) {
            only_code = date.getTime() + "" + rad;
            only_code = "K" + only_code.substring(1);
            editor.putString("only_code", only_code);
            editor.commit();
        }
        return only_code;
    }

    public static String getDevIMEI(){
        TelephonyManager tm = (TelephonyManager) SKuaidiApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return TextUtils.isEmpty(tm.getDeviceId())? "":tm.getDeviceId();
    }

    /**
     * 通过getRunningTasks判断App是否位于前台
     *
     * @param context     上下文参数
     * @param packageName 需要检查是否位于栈顶的App的包名
     * @return
     */
    public static boolean getRunningTask(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return !TextUtils.isEmpty(packageName) && packageName.equals(cn.getPackageName());
    }

    public static void stopPushService() {
        PushManager.stopWork(SKuaidiApplication.getContext());
    }

//    public static void clearAppData(final boolean stop){
//        // 删除旧用户的数据
//        // SKuaidiApplication.getInstance().orderDb.deleteAllOrder();
//        // 清空订单,留言数据
//        final SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                newDB.deleteOrder();
//                newDB.deleteMessage();
//                newDB.deleteOrderDeatil();
//                newDB.deleteMessageDeatil();
//                newDB.deleteAllCustomer();
////                SkuaidiDB.getInstanse(SKuaidiApplication.getContext()).clearReplymodel();// 删除发短信模板
////                SkuaidiDB.getInstanse(SKuaidiApplication.getContext()).clearCloudModel();// 删除云呼语音模板
////                                    RecordDraftBoxDAO.deleteALLDraft(SkuaidiSpf.getLoginUser().getPhoneNumber());// 删除短信记录中草稿箱的记录
////                                    SKuaidiApplication.getInstance().clearCustom();
////								//Log.i("iii", "用户数据:"+SKuaidiApplication.getInstance().getCustoms().size());
//                SkuaidiSpf.clearCustomersLastSyncTime();
//                SkuaidiSpf.removeKeyValue("customerLastSyncTime");
//                //清除客户管理时间戳
//                SkuaidiSpf.setOrderListLastSyncDate("");
//                SkuaidiSpf.setOrderDetailListLastSyncDate("");
//                SkuaidiSpf.saveExpressShopQrcodeSaveTime(SKuaidiApplication.getContext(), 0);// 我的快递店铺中二维码的保存时间
//                SkuaidiSpf.setCustomerLastSyncDate(0);
//                SkuaidiSpf.saveUserRoleType(SKuaidiApplication.getContext(), "");// 身份
//                SkuaidiSpf.saveUserBusinessType(SKuaidiApplication.getContext(), "");// 业务类型
//                SkuaidiSpf.saveHasRangeContent(SKuaidiApplication.getContext(), false);// 设置没有填写取派范围
//                SkuaidiSpf.setModelDragSort(SKuaidiApplication.getContext(), "");// 清除模板排序保存的记录
//                BackUpService.clearTimer();
////                                    SkuaidiCustomerManager.getInstanse().clearList();
//                SkuaidiSpf.saveClientIsVIP(SKuaidiApplication.getContext(), "");// 取消VIP特权
//                SkuaidiSpf.setIsSetFee("");
//                SkuaidiSpf.setIsShopSetFee("");
//                SkuaidiSpf.setIsBossSetFee("");
//                SkuaidiSpf.setDeliveryFee("");
//                // BackUpService.clearList();
//                if(stop){
//                    PushManager.stopWork(SKuaidiApplication.getContext());
//                }
//            }
//        }).start();
//    }

    /**
     * 转化十六进制编码为对应的字符
     **/
    public static String hexToString(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 运单号中每个字符之间添加一个空格
     * @param no
     * @return
     */
    public static String formatOrderNo(String no){
        StringBuffer buf = new StringBuffer();
        for(int i = 0 ; i < no.length(); i++){
            if(i == no.length() - 1){
                buf.append(no.substring(i, i+1));
            }else{
                buf.append(no.substring(i, i+1)).append(" ");
            }
        }

        return buf.toString();
    }

    /**
     * 用分隔符连接字符串
     * @param str1 字符串1
     * @param str2 字符串2
     * @param tag 分隔符
     * @return 连接后的字符串
     */
    public static String mergeString(String str1, String str2, String tag){
        StringBuffer buffer = new StringBuffer();
        buffer.append(str1).append(tag).append(str2);
        return buffer.toString();
    }

    public static String replaceSpecStr(String orgStr){
        if (null!=orgStr&&!"".equals(orgStr.trim())) {
            String regEx="[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(orgStr);
            return m.replaceAll("*");
        }
        return null;
    }

    public  static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }


    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_3G = 0x02;
    public static final int NETTYPE_2G = 0x03;

    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) SKuaidiApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if ("cmnet".equals(extraInfo.toLowerCase())) {
                    netType = NETTYPE_2G;
                } else {
                    netType = networkInfo.getSubtype();
                    if ("3gnet".equals(extraInfo.toLowerCase()) || netType == TelephonyManager.NETWORK_TYPE_UMTS
                            || netType == TelephonyManager.NETWORK_TYPE_HSDPA
                            || netType == TelephonyManager.NETWORK_TYPE_EVDO_0
                            || netType == TelephonyManager.NETWORK_TYPE_EVDO_A
                            || netType == TelephonyManager.NETWORK_TYPE_HSPAP) {
                        netType = NETTYPE_3G;

                    } else {
                        netType = NETTYPE_2G;
                    }
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 获取类名
     * @param activity
     * @return
     */
    public static String getLocalClassName(Activity activity){
        String className = activity.getLocalClassName();
        String arr[] = className.split("\\.");
        int size = arr.length;
        if (size < 1){
            return className;
        }
        String res = arr[size -1];
        return res;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "K");
    }

    /** 从assets 文件夹中读取图片 */
    public static Drawable loadImageFromAsserts(final Context ctx, String fileName) {
        try {
            InputStream is = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
