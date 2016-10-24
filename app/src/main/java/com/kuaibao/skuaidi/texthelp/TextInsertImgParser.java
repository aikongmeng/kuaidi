package com.kuaibao.skuaidi.texthelp;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.kuaibao.skuaidi.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于将字符串中的指定文字替换成指定图片
 * 
 * @author 顾冬冬
 * 
 */
public class TextInsertImgParser {

	private Context mContext;
	private String[] mImageTxts;
	private Pattern mPattern;
	private HashMap<String, Integer> mImgToRes;

	// public static final int[] DEFAULT_IMG_RES_IDS =
	// {SkuaidiSkinManager.getSkinResId("model_no"),
	// SkuaidiSkinManager.getSkinResId("model_order") };// 需要替换的图片数组
	public static final int[] DEFAULT_IMG_RES_IDS = { R.drawable.model_no,R.drawable.model_no,R.drawable.model_password, R.drawable.model_order, R.drawable.model_url };// 需要替换的图片数组

	public static final int DEFAULT_IMG_TESTS = R.array.default_imgs_tests;// 包含要替换的字符数组

	public TextInsertImgParser(Context context) {
		mContext = context;
		mImageTxts = mContext.getResources().getStringArray(DEFAULT_IMG_TESTS);
		mImgToRes = buildImgToRes();
		mPattern = buildPattern();
	}

	private HashMap<String, Integer> buildImgToRes() {
		if (DEFAULT_IMG_RES_IDS.length != mImageTxts.length) {
			// 表情的数量需要和数组定义的长度一致！
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}

		HashMap<String, Integer> imgToRes = new HashMap<>(mImageTxts.length);
		for (int i = 0; i < mImageTxts.length; i++) {
			imgToRes.put(mImageTxts[i], DEFAULT_IMG_RES_IDS[i]);
		}
		return imgToRes;
	}

	// 构建正则表达式
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(mImageTxts.length * 3);
		patternString.append('(');
		for (String s : mImageTxts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1, patternString.length(), ")");

		return Pattern.compile(patternString.toString());

	}

	// 根据文本替换成图片
	public CharSequence replace(CharSequence text) {
		if (null != text || !"".equals(text)) {
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			Matcher matcher = mPattern.matcher(text);
			while (matcher.find()) {
				int resId = mImgToRes.get(matcher.group());
				builder.setSpan(new ImageSpan(mContext, resId), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			return builder;
		}
		return "";
	}

}
