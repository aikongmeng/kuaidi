package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
public static  Pattern pattern = Pattern.compile("^1\\d{10}$");
    /**
     * Json 转成 Map<>
     *
     * @param jsonStr
     * @return
     */
    public static LinkedHashMap<String, String> json2Map(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr))
            return null;
        JSONObject jsonObject;
        LinkedHashMap<String, String> valueMap = null;
        try {
            jsonObject = new JSONObject(jsonStr);

            Iterator<String> keyIter = jsonObject.keys();
            String key;
            String value;
            valueMap = new LinkedHashMap<String, String>();
            while (keyIter.hasNext()) {
                key = keyIter.next();
                value = jsonObject.get(key).toString();
                valueMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return valueMap;
    }

    /**
     * 判断字符串是否为空
     *
     * @param txt
     * @return
     */

    public static boolean isBlank(String txt) {
        return txt != null && !txt.trim().equals("") && !txt.equals("\\s");
    }

    public static String isEmpty(String txt) {
        if (txt != null && !txt.trim().equals("") && !txt.equals("\\s") && !txt.equals("null") && !txt.equals("NULL")
                && !txt.equals("Null")) {
            return txt;
        }
        return "";
    }

    /**
     * @param phoneNumber
     * @return 判断字符串是否是电话号码, 是则返回true，否则则返回false。
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {

		/*
         * 目前国内号码段分配如下：
		 * 移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、182
		 * 、183、187、188、147 联通：130、131、132、136、152、155、156、185、186
		 * 电信：133、153、180、189、（1349卫通） 增加了对182、183号段的支持
		 */

        // String expression="1\\d{10}";
        String expression = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
        phoneNumber = phoneNumber.replaceAll(" ", "");
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static String timeFormat(long time) {
        Date sendtime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(sendtime);

        return dateString;
    }

    /**
     * @return 将文件转化为二进制流
     */
    public static String recorderToString(String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream in = null;
        byte[] b = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));

            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                baos.write(temp, 0, size);
            }
            in.close();
            b = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (b == null)
            return "";
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    /**
     * @return 将图片转化为二进制流
     */
    public static String bitmapToString(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String transString(String str) {
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        return str;
    }

    public static String transBackString(String str) {
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&amp;", "&");
        return str;
    }

    public static String getFileName(String pathName) {
        if (TextUtils.isEmpty(pathName)) {
            return null;
        }
        int start = pathName.lastIndexOf("/");
        int end = pathName.length();
        if (start != -1 && end != -1) {
            return pathName.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /**
     * 判断字符串是否为手机号
     * @param phone
     * @return
     */
    public static boolean isPhoneString(String phone) {
        Matcher matcher=pattern.matcher(phone);
        return  matcher.matches();
    }


    /**判断一个字条串中是不是全部是数字 是则返回true**/
    public static boolean judgeStringEveryCharacterIsNumber(String mobilePhone){
        if (com.kuaibao.skuaidi.util.Utility.isEmpty(mobilePhone))
            return false;
        for (int i = 0;i<mobilePhone.length();i++){
            if (!Character.isDigit(mobilePhone.charAt(i)))// 如果该char不是数字
                return false;
        }
        return true;
    }

    /**
     * 判断字符串中每个字符是否都是数字或存在*号
     * @param str
     * @param containsChar
     * @return
     */
    public static boolean isPhoneString(String str,char containsChar){
        if (Utility.isEmpty(str))
            return false;
        for (int i = 0;i<str.length();i++){
            char c = str.charAt(i);
            if (!Character.isDigit(c) && c != containsChar){// 字符串中有不是数字且不是字符‘*’的时候则判定这个字符不是想要的字符
                return false;
            }
        }
        return true;
    }

    /**获取字符串中不是数字的字符下标【保存到集合中】**/
    public static List<Integer> getNotNumberList(String str){
        if (TextUtils.isEmpty(str))
            return null;
        List<Integer> intlist = new ArrayList<>();
        for (int i = 0; i < str.length();i++){
            char c = str.charAt(i);
            if (!Character.isDigit(c)){
                intlist.add(i);
            }
        }
        return intlist;
    }

}
