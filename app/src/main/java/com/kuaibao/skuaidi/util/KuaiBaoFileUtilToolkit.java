package com.kuaibao.skuaidi.util;

import java.io.File;

public class KuaiBaoFileUtilToolkit {
	
	public static void deleteAllFileInFolder(File file){
		for (File fi : file.listFiles()) {
			if(fi.isDirectory()){
				deleteAllFileInFolder(fi);
			}else{
				fi.delete();
			}
		}
	}
	public static void deleteFile(File file){
		file.delete();
	}
}
