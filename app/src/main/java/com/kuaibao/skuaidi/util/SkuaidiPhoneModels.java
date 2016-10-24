package com.kuaibao.skuaidi.util;

import java.util.HashMap;

public class SkuaidiPhoneModels {
	public static final String MODEL_HONGMI_NOTE = "";
	public static final String MODEL_HONGMI = "2013022";
	public static final String MODEL_MINOTE_PRO = "MI NOTE Pro";
	public static final HashMap<String, String> MODEL_CANNOT_VOICECALL = new HashMap<String, String>() {
		{
			put(MODEL_HONGMI, MODEL_HONGMI);
			put(MODEL_MINOTE_PRO, MODEL_MINOTE_PRO);
		}
	};
}
