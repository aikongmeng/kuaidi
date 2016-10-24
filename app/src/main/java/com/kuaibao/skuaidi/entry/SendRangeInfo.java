package com.kuaibao.skuaidi.entry;

import java.io.Serializable;
import java.util.List;

public class SendRangeInfo implements Serializable{

	private static final long serialVersionUID = -1129378209781588596L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -8540527465051402302L;
	
	private List<RangeInfo> sendranges;
	private List<String> notsendranges;
	public List<RangeInfo> getSendranges() {
		return sendranges;
	}
	public void setSendranges(List<RangeInfo> sendranges) {
		this.sendranges = sendranges;
	}
	
	public List<String> getNotsendranges() {
		return notsendranges;
	}
	public void setNotsendranges(List<String> notsendranges) {
		this.notsendranges = notsendranges;
	}
	@Override
	public String toString() {
		return "SendRangeInfo [sendranges=" + sendranges + ", notsendranges="
				+ notsendranges + "]";
	}
	
	
	

}
