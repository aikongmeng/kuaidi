package com.kuaibao.skuaidi.manager;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.common.view.SkuaidiButton;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 皮肤管理器
 * 
 * @author xy
 * 
 */
public class SkuaidiSkinManager {

	private static final boolean IS_CHANGE_SKIN = false;

	private static boolean isChangeSkin = IS_CHANGE_SKIN;

	public static final int CONERS_NULL = -1;
	public static final int CONERS_ALL = 0;
	public static final int CONERS_TOP = 1;
	public static final int CONERS_BOTTOM = 2;

	public static final int FRAME_NULL = -1;
	public static final int FRAME_DOTTED = 0;
	private static final String BASE_SKIN_PATH = "skins";
	private static final String DEFAULT_BRAND = "default";
	private static Map<String, SoftReference<Bitmap>> bms = new HashMap<String, SoftReference<Bitmap>>();
	private static Map<String, SoftReference<Drawable>> dbs = new HashMap<String, SoftReference<Drawable>>();
	private static Map<String, Properties> pps = new HashMap<String, Properties>();

	private static final String colorFileName = "color.properties";
	final static String packageName = SKuaidiApplication.getInstance().getPackageName();
	static Resources res = SKuaidiApplication.getInstance().getResources();
	//private static final Context context = SKuaidiApplication.getInstance();

	public static void skinColorChange(View v, int coner_type, int frame_type, String fileName, boolean isClickChange) {
		String brach = SkuaidiSpf.getLoginUser().getExpressNo();

		if (isChangeSkin == false) {
			return;
		}

		if (brach.equals("sto")) {
			int sto = SKuaidiApplication.getInstance().getResources().getColor(R.color.sto_main_color);
			if (isClickChange == false) {
				if (v.getClass().equals(SkuaidiTextView.class) || v.getClass().equals(SkuaidiButton.class)) {
					((TextView) v).setTextColor(sto);
				} else {
					v.setBackgroundResource(R.color.sto_main_color);
				}
			} else {
				skinColorChangeAboutHover(v, brach, coner_type, frame_type, fileName);
			}
		}
	}

	public static int getTextColor(String colorName) {
		return Color.parseColor(initProperties().getProperty(colorName));
	}

	private static void skinColorChangeAboutHover(View v, String brand, int coner_type, int frame_type, String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			if (frame_type == FRAME_NULL) {
				if (coner_type == CONERS_NULL) {
					v.setBackgroundResource(R.drawable.sto_selector_btn_orange);
				} else if (coner_type == CONERS_ALL) {
					v.setBackgroundResource(R.drawable.sto_selector_btn_orange_radius);
				} else if (coner_type == CONERS_TOP) {

				} else if (coner_type == CONERS_BOTTOM) {

				}
			} else {
				if (frame_type == FRAME_DOTTED) {
					v.setBackgroundResource(R.drawable.sto_selector_btn_orange_xuxian);
				}
			}
		} else {
			v.setBackgroundResource(res.getIdentifier(brand + "_" + fileName, "drawable", packageName));
		}
	}

	public static int getSkinResId(String resName) {
		String brand = SkuaidiSpf.getLoginUser().getExpressNo();

		if (isChangeSkin == false) {
			return res.getIdentifier(resName, "drawable", packageName);
		} else {

			if ("sto".equals(brand)) {
				return res.getIdentifier(brand + "_" + resName, "drawable", packageName);
			} else {
				// return
				// res.getIdentifier(brand+"_"+resName,"drawable",packageName);//qf图片
				return res.getIdentifier(resName, "drawable", packageName);
			}
		}
	}
	public static int getSkinResId_Active2(String resName) {
		String brand = SkuaidiSpf.getLoginUser().getExpressNo();

//		if (isChangeSkin == false) {
//			return res.getIdentifier(resName, "drawable", packageName);
//		} else {

			if ("sto".equals(brand)) {
				return res.getIdentifier(brand + "_" + resName, "drawable", packageName);
			} else {
				// return
				// res.getIdentifier(brand+"_"+resName,"drawable",packageName);//qf图片
				return res.getIdentifier(resName, "drawable", packageName);
			}
		//}
	}

	private static Properties initProperties() {
		String brach = SkuaidiSpf.getLoginUser().getExpressNo();
		String path;

		if (isChangeSkin == false) {
			path = BASE_SKIN_PATH + "/" + DEFAULT_BRAND + "/" + colorFileName;
		} else {
			if (brach.equals("sto")) {
				path = BASE_SKIN_PATH + "/" + brach + "/" + colorFileName;
			} else {
				path = BASE_SKIN_PATH + "/" + DEFAULT_BRAND + "/" + colorFileName;
			}
		}
		if (pps.get(path) == null) {
			try {
				InputStream is = SKuaidiApplication.getInstance().getAssets().open(path);
				if (is != null) {
					Properties properties = new Properties();
					properties.load(is);
					pps.put(path, properties);
					return properties;
				}
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return pps.get(path);
		}
	}

}
