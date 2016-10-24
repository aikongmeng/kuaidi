package com.kuaibao.skuaidi.activity.model;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;
@Table(name = "CallRecordingMp3")
public class CallRecordingMp3 implements Serializable{
	@Transient
	private static final long serialVersionUID = 891301496350225349L;

	//private static final long serialVersionUID = -4865306732798713489L;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	private String id;
	private String title;
	private String artist;
	private String album;
	private String url;
	private long recordingTime;
	private long duration;
	private int size;
	private String orderNum;
	private int cacheOrderId;
	private int isBindLiuyanList;
	private String callId;
	private String phoneNum;
	public String getCallId() {
		return callId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getRecordingTime() {
		return recordingTime;
	}
	public void setRecordingTime(long recordingTime) {
		this.recordingTime = recordingTime;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public int getIsBindLiuyanList() {
		return isBindLiuyanList;
	}

	public void setIsBindLiuyanList(int isBindLiuyanList) {
		this.isBindLiuyanList = isBindLiuyanList;
	}

	public int getCacheOrderId() {
		return cacheOrderId;
	}

	public void setCacheOrderId(int cacheOrderId) {
		this.cacheOrderId = cacheOrderId;
	}

	@Override
	public String toString() {
		return "CallRecordingMp3 [id=" + id + ", title=" + title + ", artist="
				+ artist + ", album=" + album + ", url=" + url
				+ ", recordingTime=" + recordingTime + ", duration=" + duration
				+ ", size=" + size + ", orderNum=" + orderNum
				+ ", cacheOrderId=" + cacheOrderId + ", isBindLiuyanList="
				+ isBindLiuyanList + ", callId=" + callId + ", phoneNum="
				+ phoneNum + "]";
	}
	
	
}
