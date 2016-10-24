package com.kuaibao.skuaidi.entry;

import java.io.Serializable;



public class MessageDetail implements Serializable {
	private static final long serialVersionUID = 2975485371486206384L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	/**from or to or main*/
	private String  t;
    private String  content;
    /**留言发起人*/
    private String  post_username;
    /**留言发起时间*/
    private String  post_time;
    private String post_id;
    private String post_timestramp;
    private String post_role;
    private String post_mobile;
    private int content_type;
    private String[] attachs;
	private long length;
    private Shop_meta shop_meta;
    private Attach attach;
    
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPost_username() {
		return post_username;
	}
	public void setPost_username(String post_username) {
		this.post_username = post_username;
	}
	public String getPost_time() {
		return post_time;
	}
	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	
	public Shop_meta getShop_meta() {
		return shop_meta;
	}
	public void setShop_meta(Shop_meta shop_meta) {
		this.shop_meta = shop_meta;
	}

	public class Shop_meta {
		private String shop_id;
		private String shop_code;
		private String shop_name;
		public String getShop_id() {
			return shop_id;
		}
		public void setShop_id(String shop_id) {
			this.shop_id = shop_id;
		}
		public String getShop_code() {
			return shop_code;
		}
		public void setShop_code(String shop_code) {
			this.shop_code = shop_code;
		}
		public String getShop_name() {
			return shop_name;
		}
		public void setShop_name(String shop_name) {
			this.shop_name = shop_name;
		}
		
	}
	
	public class Attach{
		private String type;
		private String url;
		private String[] img_arr;
		private long length;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}

		public String[] getImg_arr() {
			return img_arr;
		}

		public void setImg_arr(String[] img_arr) {
			this.img_arr = img_arr;
		}

		public long getLength() {
			return length;
		}
		public void setLength(long length) {
			this.length = length;
		}
		
	}
	public Attach getAttach() {
		return attach;
	}
	public void setAttach(Attach attach) {
		this.attach = attach;
	}
	public String getPost_id() {
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getPost_timestramp() {
		return post_timestramp;
	}
	public void setPost_timestramp(String post_timestramp) {
		this.post_timestramp = post_timestramp;
	}
	public String getPost_role() {
		return post_role;
	}
	public void setPost_role(String post_role) {
		this.post_role = post_role;
	}
	public String getPost_mobile() {
		return post_mobile;
	}
	public void setPost_mobile(String post_mobile) {
		this.post_mobile = post_mobile;
	}
	public int getContent_type() {
		return content_type;
	}
	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}
	public String[] getAttachs() {
		return attachs;
	}
	public void setAttachs(String[] attachs) {
		this.attachs = attachs;
	}
	
}
