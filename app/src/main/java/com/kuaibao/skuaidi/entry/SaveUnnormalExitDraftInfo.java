package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * 保存非正常退出时候草稿箱里面存在的数据
 * 
 * @author 顾冬冬
 */
public class SaveUnnormalExitDraftInfo implements Serializable {

	private static final long serialVersionUID = -2665702384903991072L;
	//private static final long serialVersionUID = 1L;
	private String from_data = "";// 用于保存是从哪个功能保存的数据，
									// 在进入界面时是否要取出数据而使用【例：isSms|isCloud】
	private String draft_id = "";// 对应草稿唯一ID

	private String draft_no = "";// 保存于草稿箱中的编号
	private String draft_phoneNumber = "";// 保存于草稿箱中的手机号码
	private String draft_orderNumber = "";// 保存于草稿箱中的单号 【在发送短信中有单号】
	private int draft_position = -1;// 保存于草稿箱中当前号码输入以后的下标
	private int draft_positionNo = -1;// 保存于草稿中当前序号

	public String getFrom_data() {
		return from_data;
	}

	public void setFrom_data(String from_data) {
		this.from_data = from_data;
	}

	public String getDraft_id() {
		return draft_id;
	}

	public void setDraft_id(String draft_id) {
		this.draft_id = draft_id;
	}

	public String getDraft_no() {
		return draft_no;
	}

	public void setDraft_no(String draft_no) {
		this.draft_no = draft_no;
	}

	public String getDraft_phoneNumber() {
		return draft_phoneNumber;
	}

	public void setDraft_phoneNumber(String draft_phoneNumber) {
		this.draft_phoneNumber = draft_phoneNumber;
	}

	public String getDraft_orderNumber() {
		return draft_orderNumber;
	}

	public void setDraft_orderNumber(String draft_orderNumber) {
		this.draft_orderNumber = draft_orderNumber;
	}

	public int getDraft_position() {
		return draft_position;
	}

	public void setDraft_position(int draft_position) {
		this.draft_position = draft_position;
	}

	public int getDraft_positionNo() {
		return draft_positionNo;
	}

	public void setDraft_positionNo(int draft_positionNo) {
		this.draft_positionNo = draft_positionNo;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
