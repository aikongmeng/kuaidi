package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * @ClassName: VASInfo
 * @Description: 增值业务列表实体类
 * @author 顾冬冬
 * @date 2015-11-26 上午10:11:35
 */
public class VASInfo implements Serializable {
	private static final long serialVersionUID = -6575067179715639618L;

	//private static final long serialVersionUID = 1L;

	private String id = "";// 唯一识别ID
	private String cm_id = "";// 用户ID
	private String vasName = "";// 增值业务标题
	private String vasDescription = "";// 增值业务详情
	private String vasPrice = "";// 增值业务价格
	private String createTime = "";// 创建时间
	private String updateTime = "";// 内容更新时间

	public String getVasName() {
		return vasName;
	}

	public void setVasName(String vasName) {
		this.vasName = vasName;
	}

	public String getVasDescription() {
		return vasDescription;
	}

	public void setVasDescription(String vasDescription) {
		this.vasDescription = vasDescription;
	}

	public String getVasPrice() {
		return vasPrice;
	}

	public void setVasPrice(String vasPrice) {
		this.vasPrice = vasPrice;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCm_id() {
		return cm_id;
	}

	public void setCm_id(String cm_id) {
		this.cm_id = cm_id;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
