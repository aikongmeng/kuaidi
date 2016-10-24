package com.kuaibao.skuaidi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//MD5加密
public class MD5 {
	public String toMD5(String str){
		MessageDigest md5;
		StringBuffer sb = new StringBuffer();
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte[] m = md5.digest();
			int i;
			for (byte b : m) {
				i = b;
                if (i < 0){
                    i += 256;
                }    
                if (i < 16){
                    sb.append("0");
                }
				sb.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
