package com.kuaibao.skuaidi.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MyCustom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * 
 * 通用工具类
 * 
 * @author xy
 * 
 */
@SuppressLint("DefaultLocale")
public class UtilToolkit {

	private static final String SCHEME = "package";

	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

	private static final String APP_PKG_NAME_22 = "pkg";

	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	private static final String CODE = "utf-8";

	/**
	 * 
	 * 根据手机号获取联系人名字
	 * @param context
	 * @param phoneNumber
	 * @return CallerName
	 * 
	 */
	public static String getCallerNameFromPhoneNumber(Context context,
			String phoneNumber) {
		if (phoneNumber == null || phoneNumber == "") {
			return "";
		}

		String strPerson = "";
		String[] projection = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };
		Uri uri_Person = Uri.withAppendedPath(
				ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
				phoneNumber); // phoneNumber 手机号过滤
		Cursor cursor = null;
		try{
			 cursor = context.getContentResolver().query(uri_Person,
				projection, null, null, null);
		}catch(SecurityException e){
			return phoneNumber;
		}
		
		if(cursor==null){
			return phoneNumber;
		}
		
		if (cursor.moveToFirst()) {
			int index_PeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			String strPeopleName = cursor.getString(index_PeopleName);
			strPerson = strPeopleName;
		} else {
			strPerson = phoneNumber;
		}
		cursor.close();
		cursor = null;
		return strPerson;
	}

	/**
	 * 根据手机号获取联系人姓名
	 * 
	 * @param phoneNum
	 * @return
	 */

	public static String getCusNameFromContactsByPhoneNum(String phoneNum) {

		String cusName1 = getPhoneContactNameByPhoneNum(phoneNum);
		String cusName2 = getSIMContactNameByPhoneNum(phoneNum);

		if (TextUtils.isEmpty(cusName1) && TextUtils.isEmpty(cusName2)) {
			return "";
		} else if (!TextUtils.isEmpty(cusName1)) {
			return cusName1;
		} else {
			return cusName2;
		}
	}


	/** 根据手机号得到手机通讯录联系人姓名 **/
	private static String getPhoneContactNameByPhoneNum(String phoneNumber) {
		ContentResolver resolver = SKuaidiApplication.getInstance()
				.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				new String[] { Phone.DISPLAY_NAME }, Phone.NUMBER + " like '%"
						+ phoneNumber + "%'", null, null);
		String cusName = "";
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到联系人名称
				if (!TextUtils.isEmpty(phoneCursor.getString(0))) {
					cusName = phoneCursor.getString(0);
					break;
				}
			}
			phoneCursor.close();
		}
		return cusName;
	}

	/** 根据手机号得到手机SIM卡联系人姓名 **/
	private static String getSIMContactNameByPhoneNum(String phoneNumber) {
		ContentResolver resolver = SKuaidiApplication.getInstance()
				.getContentResolver();
		String cusName = "";
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri,
				new String[] { Phone.DISPLAY_NAME }, Phone.NUMBER + " like '%"
						+ phoneNumber + "%'", null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				if (!TextUtils.isEmpty(phoneCursor.getString(0))) {
					cusName = phoneCursor.getString(0);
					break;
				}
			}

			phoneCursor.close();
		}
		return cusName;
	}

	/**
	 * 是否为桌面
	 * 
	 * @return
	 */

	public static boolean isHome() {
		ActivityManager activityManager = (ActivityManager) SKuaidiApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = activityManager.getRunningTasks(1);
		return getHomes().contains(rti.get(0).topActivity.getPackageName());
	}

	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @return 返回包含所有包名的字符串列表
	 */
	private static List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = SKuaidiApplication.getInstance()
				.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	/**
	 * Toast消息
	 * 
	 * @param msg
	 */

	public static void showToast(String msg) {
		if(Utility.isEmpty(msg))
			return;
		Message message = new Message();
		message.what = SKuaidiApplication.SHOW_TOAST;
		message.obj = msg;
		SKuaidiApplication.getInstance().sendMessage(message);
	}
	
	/**
	 * showToast_Custom:显示自定义toast（居中显示大字体）
	 *
	 * @author 顾冬冬
	 */
	public static void showToast_Custom(String msg){
		if (Utility.isEmpty(msg))
			return;
		Message message = new Message();
		message.what = SKuaidiApplication.SHOW_TOAST_CUSTOM;
		message.obj = msg;
		SKuaidiApplication.getInstance().sendMessage(message);
	}

	/** 
     * 格式化时间，将毫秒转换为分:秒格式 
     * @param time 
     * @return 
     */  
    public static String formatTime(long time) {  
        String min = time / (1000 * 60) + "";  
        String sec = time % (1000 * 60) + "";  
        if (min.length() < 2) {  
            min = "0" + time / (1000 * 60) + "";  
        } else {  
            min = time / (1000 * 60) + "";  
        }  
        if (sec.length() == 4) {  
            sec = "0" + (time % (1000 * 60)) + "";  
        } else if (sec.length() == 3) {  
            sec = "00" + (time % (1000 * 60)) + "";  
        } else if (sec.length() == 2) {  
            sec = "000" + (time % (1000 * 60)) + "";  
        } else if (sec.length() == 1) {  
            sec = "0000" + (time % (1000 * 60)) + "";  
        }  
        return min + ":" + sec.trim().substring(0, 2);  
    }  

    public static List<MyCustom> filledData(List<MyCustom> date) {
		long time1 = System.currentTimeMillis();
		List<MyCustom> mSortList = new ArrayList<MyCustom>();
		for (int i = 0; i < date.size(); i++) {
			MyCustom cus = date.get(i);
			String pinyin = CharacterParser.getInstance().getSelling(
					cus.getName());
			if (pinyin.equals("")) {
				pinyin = "#";
			}
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				cus.setSortLetters(sortString.toUpperCase());
			} else {
				cus.setSortLetters("#");
			}
			cus.setSupportSearchSTR(cus.getName()+cus.getPhone());
			mSortList.add(cus);
		}
		return mSortList; // mSortList
	}
    
    public static MyCustom singlefilledData(MyCustom cus) {
		String pinyin = CharacterParser.getInstance().getSelling(cus.getName());
		if (pinyin.equals("")) {
			pinyin = "#";
		}
		String sortString = pinyin.substring(0, 1).toUpperCase();
		if (sortString.matches("[A-Z]")) {
			cus.setSortLetters(sortString.toUpperCase());
		} else {
			cus.setSortLetters("#");
		}
		cus.setSupportSearchSTR(cus.getName()+cus.getPhone());
		return cus;
	}
    
    /**
     * 复制对象
     * @param obj
     * @return
     * @throws Exception
     */
    public static Object cloneObject(Object obj) throws Exception{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); 
        ObjectOutputStream out = new ObjectOutputStream(byteOut); 
        out.writeObject(obj); 
        
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray()); 
        ObjectInputStream in =new ObjectInputStream(byteIn);
        return in.readObject();
    }
    
    
    /**
     * 根据资源ID获取URI
     * @param context
     * @param res
     * @return
     */
    public static Uri getResourceUri(Context context, int res) {
        try {
        Context packageContext = context.createPackageContext(context.getPackageName(),
                    Context.CONTEXT_RESTRICTED);
            Resources resources = packageContext.getResources();
            String appPkg = packageContext.getPackageName();
            String resPkg = resources.getResourcePackageName(res);
            String type = resources.getResourceTypeName(res);
            String name = resources.getResourceEntryName(res);


            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme(ContentResolver.SCHEME_ANDROID_RESOURCE);
            uriBuilder.encodedAuthority(appPkg);
            uriBuilder.appendEncodedPath(type);
            if (!appPkg.equals(resPkg)) {
                uriBuilder.appendEncodedPath(resPkg + ":" + name);
            } else {
                uriBuilder.appendEncodedPath(name);
            }
            return uriBuilder.build();
        
        } catch (Exception e) {
            return null;
        }
    } 

	public static Bitmap getBarCodeToBitmap(String stBarcode, int width,
			int height) {
		if (width == 0 || width < 200) {
			width = 200;
		}

		if (height == 0 || height < 50) {
			height = 50;
		}

		try {
			// 文字编码
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, CODE);

			BitMatrix bitMatrix = new MultiFormatWriter().encode(stBarcode,
					BarcodeFormat.CODE_128, width, height, hints);
			return BitMatrixToBitmap(bitMatrix);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * BitMatrix转换成Bitmap
	 * 
	 * @param matrix
	 * @return
	 */
	private static Bitmap BitMatrixToBitmap(BitMatrix matrix) {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
			}
		}
		return createBitmap(width, height, pixels);
	}

	/**
	 * 生成Bitmap
	 * 
	 * @param width
	 * @param height
	 * @param pixels
	 * @return
	 */
	private static Bitmap createBitmap(int width, int height, int[] pixels) {
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public static void showBackDialog(final Activity activity) {
		CustomDialog.Builder builder = new CustomDialog.Builder(activity);
		builder.setTitle("放弃").setMessage("您要放弃当前操作吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				activity.finish();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
}
