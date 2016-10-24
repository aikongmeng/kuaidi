package com.kuaibao.skuaidi.activity.model;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;

/**
 * 
 * @author a16
 * 
 */
@Table(name = "CourierReviewInfo")
public class CourierReviewInfo implements Serializable {
	@Transient
	private static final long serialVersionUID = -3780489282493248003L;
	private int id;
	/** 是否通过审核 */
	private int isThroughAudit;
	/** 快递员电话 */
	private String courierPhone = "";
	/** 快递员工号 */
	private String courierJobNo = "";
	/** 快递员姓名 */
	private String courierName = "";
	/** 快递员所在网点 */
	private String courierLatticePoint = "";
	/** 快递员所在网点ID */
	private String courierLatticePointId;
	public String getCourierLatticePointId() {
		return courierLatticePointId;
	}

	public void setCourierLatticePointId(String courierLatticePointId) {
		this.courierLatticePointId = courierLatticePointId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsThroughAudit() {
		return isThroughAudit;
	}

	public void setIsThroughAudit(int isThroughAudit) {
		this.isThroughAudit = isThroughAudit;
	}

	public String getCourierPhone() {
		return courierPhone;
	}

	public void setCourierPhone(String courierPhone) {
		this.courierPhone = courierPhone;
	}

	public String getCourierJobNo() {
		return courierJobNo;
	}

	public void setCourierJobNo(String courierJobNo) {
		this.courierJobNo = courierJobNo;
	}

	public String getCourierName() {
		return courierName;
	}

	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}

	public String getCourierLatticePoint() {
		return courierLatticePoint;
	}

	public void setCourierLatticePoint(String courierLatticePoint) {
		this.courierLatticePoint = courierLatticePoint;
	}

}
