package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class Message implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = 5795419377261541003L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -5129096037992888418L;

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public String personName;
	public int messageType;
	public long messageDate;
	public String messageContent;
	public String personPhoneNumber;
	public int thread_id;
	public boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public long getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(long messageDate) {
		this.messageDate = messageDate;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getPersonPhoneNumber() {
		return personPhoneNumber;
	}

	public void setPersonPhoneNumber(String personPhoneNumber) {
		this.personPhoneNumber = personPhoneNumber;
	}

	public int getThread_id() {
		return thread_id;
	}

	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	@Override
	public int compareTo(Message another) {
		// TODO Auto-generated method stub
		return Long.valueOf(this.getMessageDate()).compareTo(Long.valueOf(another.getMessageDate()));
	}

}
