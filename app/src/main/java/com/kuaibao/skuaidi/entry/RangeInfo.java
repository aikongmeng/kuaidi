package com.kuaibao.skuaidi.entry;

import java.io.Serializable;
import java.util.List;

public class RangeInfo implements Serializable{

	private static final long serialVersionUID = -324188016606821060L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -8540527465051402302L;
	
	private String roadname;
	private int start;
	private int end;
	private List<String> road_numbers;
	public String getRoadname() {
		return roadname;
	}
	public void setRoadname(String roadname) {
		this.roadname = roadname;
	}
	public List<String> getRoad_numbers() {
		return road_numbers;
	}
	public void setRoad_numbers(List<String> road_numbers) {
		this.road_numbers = road_numbers;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "RangeInfo [roadname=" + roadname + ", start=" + start
				+ ", end=" + end + ", road_numbers=" + road_numbers + "]";
	}

}
