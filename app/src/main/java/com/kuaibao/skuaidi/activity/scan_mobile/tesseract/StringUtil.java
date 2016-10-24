package com.kuaibao.skuaidi.activity.scan_mobile.tesseract;

public class StringUtil {

	public static String getPhone(String str){
		if (str != null && !str.equals("") && str.length() != 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
				}
			}
			System.out.println("gudd & stringbuilder to string is ====>  "+sb.toString());
			return sb.toString();
		}
		return str;
    }
	
	
}
