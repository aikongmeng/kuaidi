package com.kuaibao.skuaidi.entry;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;

@Table(name = "circleexpresstucaoinfo2")
public class CircleExpressTuCaoInfo implements Serializable {
	@Transient
	private static final long serialVersionUID = 1352940395148892750L;
	//private static final long serialVersionUID = 712608840414659742L;

	private String id;// 吐槽id
	private String wduser_id;// 网点用户id
	private String shop;// 网点
	private String brand;// 快递公司
	private String county;// 区域
	private String content;// 吐槽内容
	private String update_time;// 更新时间
	private String channel = "";// 吐槽来涛s&c
	private boolean is_good;// 点赞状态
	private String huifu;// 回复数
	private String good;// 点赞
	private String pic;//图片（可包含多张图片）
	private String message;// 快递员（网点+公司）
	//gudd
//	private ArrayList<String> imageurls;// 九宫格图片的URL集合(小图)
//	private ArrayList<String> imageurlsbig;// 九宫格图片的URL集合(大图)

	private String imageurls;// 九宫格图片的URL集合(小图)
	private String imageurlsbig;// 九宫格图片的URL集合(大图)
	
	//gudd
//	public ArrayList<String> getImageurlsbig(){
//		return imageurlsbig;
//	}
	
	public String getImageurls() {
		return imageurls;
	}

	public void setImageurls(String imageurls) {
		this.imageurls = imageurls;
	}

	public String getImageurlsbig() {
		return imageurlsbig;
	}

	public void setImageurlsbig(String imageurlsbig) {
		this.imageurlsbig = imageurlsbig;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
//gudd
//	public void setImageurlsbig(ArrayList<String> imageurlsbig){
//		this.imageurlsbig = imageurlsbig;
//	}
	//gudd
//	public ArrayList<String> getImageurls() {
//		return imageurls;
//	}
//gudd
//	public void setImageurls(ArrayList<String> imageurls) {
//		this.imageurls = imageurls;
//	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public boolean isIs_good() {
		return is_good;
	}

	public void setIs_good(boolean is_good) {
		this.is_good = is_good;
	}

	public String getHuifu() {
		return huifu;
	}

	public void setHuifu(String huifu) {
		this.huifu = huifu;
	}

	public String getGood() {
		return good;
	}

	public void setGood(String good) {
		this.good = good;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWduser_id() {
		return wduser_id;
	}

	public void setWduser_id(String wduser_id) {
		this.wduser_id = wduser_id;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "CircleExpressTuCaoInfo [id=" + id + ", wduser_id=" + wduser_id
				+ ", shop=" + shop + ", brand=" + brand + ", county=" + county
				+ ", content=" + content + ", update_time=" + update_time
				+ ", is_good=" + is_good + ", huifu=" + huifu + ", good="
				+ good + ", pic=" + pic + ", message=" + message
				+ ", imageurls=" + imageurls + ", imageurlsbig=" + imageurlsbig
				+ "]";
	}

}
