package com.kuaibao.skuaidi.business.nettelephone.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Encryption {
	 public static void main(String args[]) throws Exception {
	        //System.out.println(encrypt());
	        //System.out.println(desEncrypt("test123000000000","0000000000000000","hNcLM0m/YX9P2e9OXkSZqw=="));
	    }
	 
	    public static String encrypt() throws Exception {
	        try {
	            String data = "p1212345";
	            String key = "test123000000000";
	            String iv = "0000000000000000";
	 
	            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	            int blockSize = cipher.getBlockSize();
	 
	            byte[] dataBytes = data.getBytes();
	            int plaintextLength = dataBytes.length;
	            if (plaintextLength % blockSize != 0) {
	                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
	            }
	 
	            byte[] plaintext = new byte[plaintextLength];
	            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
	             
	            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
	            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	 
	            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	            byte[] encrypted = cipher.doFinal(plaintext);
	 
	            return new BASE64Encoder().encode(encrypted);
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 
	    public static String desEncrypt(String key,String iv,String data) throws Exception {
	        try
	        {
	            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(data);
	             
	            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
	            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	             
	            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
	  
	            byte[] original = cipher.doFinal(encrypted1);
	            String originalString = new String(original,"utf-8");
	            return originalString;
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
}
