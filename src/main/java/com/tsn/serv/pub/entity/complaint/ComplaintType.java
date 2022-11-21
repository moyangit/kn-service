package com.tsn.serv.pub.entity.complaint;

public class ComplaintType {
	/**
	 * 投诉类型ID
	 */
	private Integer complaintTypeId;
	/**
	 * 投诉类型描述
	 */
	private String complaintTypeName;

	private String status;

	public Integer getComplaintTypeId() {
		return complaintTypeId;
	}

	public void setComplaintTypeId(Integer complaintTypeId) {
		this.complaintTypeId = complaintTypeId;
	}

	public String getComplaintTypeName() {
		return complaintTypeName;
	}

	public void setComplaintTypeName(String complaintTypeName) {
		this.complaintTypeName = complaintTypeName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
