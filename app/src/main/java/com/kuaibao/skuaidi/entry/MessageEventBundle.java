package com.kuaibao.skuaidi.entry;

import android.os.Bundle;

public class MessageEventBundle {
	 public Bundle messageBundle;
	 public String type;
	    public MessageEventBundle(String type, Bundle message){
	    	this.type=type;
	        this.messageBundle=message;
	    }
	public MessageEventBundle(){
	}

	public Bundle getMessageBundle() {
		return messageBundle;
	}

	public void setMessageBundle(Bundle messageBundle) {
		this.messageBundle = messageBundle;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
