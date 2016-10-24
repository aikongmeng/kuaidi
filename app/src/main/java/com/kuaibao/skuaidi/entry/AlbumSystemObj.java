package com.kuaibao.skuaidi.entry;

import java.io.Serializable;
import java.util.List;

public class AlbumSystemObj implements Serializable{

	private static final long serialVersionUID = -6720390201374042792L;
	/**
	 * 相册列表集
	 */
	//private static final long serialVersionUID = 1L;

	public int count = 0;//相册照片数
	public String albumName;//相册名字
	public List<AlbumImageObj> albumImageList ;//相册照片列表
	
}
