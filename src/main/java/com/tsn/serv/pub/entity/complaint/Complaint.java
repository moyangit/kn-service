package com.tsn.serv.pub.entity.complaint;

import java.util.Date;

/**
 * 客服投诉
 */
public class Complaint {

	/**
	 * 投诉ID
	 */
	private Integer id;
	/**
	 * 会员ID
	 */
	private String memId;
	/**
	 * 会员手机号
	 */
	private String memPhone;
	/**
	 * 会员名称
	 */
	private String memName;
	/**
	 * 客服名称或微信号
	 */
	private String compCustomer;
	/**
	 * 投诉类型ID
	 */
	private Integer compTypeId;
	/**
	 * 问题描述
	 */
	private String compRemarks;
	/**
	 * 评分
	 */
	private String compRate;
	/**
	 * 状态
	 */
	private String compStatus;
	/**
	 * 图片地址(支持多张图片上传)
	 */
	private String compPic;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 解决时间
	 */
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

	public String getMemPhone() {
		return memPhone;
	}

	public void setMemPhone(String memPhone) {
		this.memPhone = memPhone;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public String getCompCustomer() {
		return compCustomer;
	}

	public void setCompCustomer(String compCustomer) {
		this.compCustomer = compCustomer;
	}

	public Integer getCompTypeId() {
		return compTypeId;
	}

	public void setCompTypeId(Integer compTypeId) {
		this.compTypeId = compTypeId;
	}

	public String getCompRemarks() {
		return compRemarks;
	}

	public void setCompRemarks(String compRemarks) {
		this.compRemarks = compRemarks;
	}

	public String getCompRate() {
		return compRate;
	}

	public void setCompRate(String compRate) {
		this.compRate = compRate;
	}

	public String getCompStatus() {
		return compStatus;
	}

	public void setCompStatus(String compStatus) {
		this.compStatus = compStatus;
	}

	public String getCompPic() {
		return compPic;
	}

	public void setCompPic(String compPic) {
		this.compPic = compPic;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
