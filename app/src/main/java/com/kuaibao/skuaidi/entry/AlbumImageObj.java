package com.kuaibao.skuaidi.entry;

import android.graphics.Bitmap;

import java.io.Serializable;

public class AlbumImageObj implements Serializable{

	private static final long serialVersionUID = -751167387640865177L;
	/**
	 * 照片对象 
	 */
	//private static final long serialVersionUID = 1L;

	public String imageId;
	public String thumbPath;
	public String imagePath;
	private Bitmap bitmap;
	public boolean isSelect = false;
	
}
