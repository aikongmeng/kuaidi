package com.kuaibao.skuaidi.util;
import java.util.ArrayList;
import java.util.List;

public class KuaiBaoStringUtilToolkit {
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return true:空 false:非空
	 */
	public static boolean isEmpty(String str){
		return str == null || str.trim().equals("") || str.equals("\\s");
	}
	
	private static List<String> mSplitString(String str,String flag,List<String> list){
		if(str.indexOf(flag)==-1){
			list.add(str);
		}else if (str.indexOf(flag) != -1) {  
			list.add(str.substring(0, str.indexOf(flag)));
			mSplitString(str.substring(str.indexOf(flag) +  
            		flag.length()), flag,list);  
        }  
		return list;
	}
	/**
	 * 
	 * @param str
	 *            需要截的字符串
	 * @param flag
	 *            截断标识 （例 "#%#" 或"$%#"）
	 * @return
	 */
	public static String[] SplitString(String str,String flag){
		
		List<String> list = mSplitString(str, flag,new ArrayList<String>());
		String[] elements = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			elements[i] = list.get(i);
		}
		return elements;
	}
	
	/**
	 * 电话号码去区号
	 * 
	 *  a)	去掉所有非数字的字符
	 *	b)  若以+86或0086开头，去掉+86或0086
	 *
	 *	1.	10位及以下：保留原文
 	 * 	
	 *	2.  11位
	 *	 a)  以1开头：保留原文
	 *	 b)  以0开头
	 *		 i.  第二个数字为1或2：截掉前面的3个数字
	 *		 ii. 其他：截掉前面的4个数字
	 *	 c)  其他：保留原文
	 *
	 *	3.  12位
	 *	 a)  以0开头：截掉前面的4个数字
	 *	 b)  其他：保留原文
	 * @param phone
	 * @return
	 */
	public static String clearPhoneAreaCode(String phone){
		
		if(KuaiBaoStringUtilToolkit.isEmpty(phone)){
			return "";
		}
		phone = phone.replaceAll("\\D+", "");
		try {
			if(phone.substring(0, 2).equals("86")){
				return phone.substring(2);
			}else if(phone.length() >= 4 && phone.substring(0, 4).equals("0086")){
				try{
					return phone.substring(4);
				}catch(java.lang.StringIndexOutOfBoundsException e){
					return phone;
				}
			}
		} catch (java.lang.StringIndexOutOfBoundsException e) {
			return phone;
		}
		
		if(phone.length()==10){
			return phone;
		}else if(phone.length()==11){
			if(phone.substring(0, 1).equals("1")){
				return phone;
			}else if(phone.substring(0, 1).equals("0")){
				if(phone.substring(1, 2).equals("1")||phone.substring(1, 2).equals("2")){
					return phone.substring(3);
				}else{
					return phone.substring(4);
				}
			}else{
				return phone;
			}
			
		}else if(phone.length()==12){
			if(phone.substring(0, 1).equals("0")){
				return phone.substring(4);
			}else{
				return phone;
			}
		}else{
			return phone;
		}
		
	}
	
	
	/**
	 * 去掉字符串中的特殊字符
	 * @param str
	 * @return
	 */
	public static String clearNonNumericCharacters(String str){
		if(KuaiBaoStringUtilToolkit.isEmpty(str)){
			return "";
		}
		str = str.replaceAll("\\D+", "");
		return str;
	}
	
	
}
