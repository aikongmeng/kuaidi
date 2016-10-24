package com.kuaibao.skuaidi.entry;

public class VisitBusinessCardInfo {

	private String pv_count ;//二维码名片访问量
	private String add_count ;//二维码名片收藏量
	private String picked_map_count;// 取派点数量

	public String getPicked_map_count() {
		return picked_map_count;
	}

	public void setPicked_map_count(String picked_map_count) {
		this.picked_map_count = picked_map_count;
	}

	public String getPv_count() {
		return pv_count;
	}
	
	public void setPv_count(String pv_count) {
		this.pv_count = pv_count;
	}
	
	public String getAdd_count() {
		return add_count;
	}
	
	public void setAdd_count(String add_count) {
		this.add_count = add_count;
	}
	
	@Override
	public String toString() {
		return "VisitBusinessCardInfo [pv_count=" + pv_count + ", add_count="
				+ add_count + "]";
	}
	
	
	
	
}
