package com.kuaibao.skuaidi.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.kuaibao.skuaidi.application.SKuaidiApplication;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 * android系统数据库操作类
 * @author xy
 *
 */
public class SkuaidiAndroidSystemDBManager {
	private static Context context = SKuaidiApplication.getInstance();
	
	
	/**
	 * 获取手机通讯录
	 * @return
	 */
	public static List<String> getSysAdressBook(){
		ArrayList<String> list = new ArrayList<String>();
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, new String[] {
				Phone.DISPLAY_NAME, Phone.NUMBER, Phone.SORT_KEY_PRIMARY },
				Phone.TYPE + "='" + Phone.TYPE_MOBILE + "'", null,
				Phone.SORT_KEY_PRIMARY);
		if (phoneCursor != null) {
			
			while (phoneCursor.moveToNext()) {
				String tel = phoneCursor.getString(1);
				if (TextUtils.isEmpty(tel))
					continue;
				String name = phoneCursor.getString(0);
				list.add(name + "-contact-" + tel);
			}
			phoneCursor.close();
		}
		return list;
	}
}
