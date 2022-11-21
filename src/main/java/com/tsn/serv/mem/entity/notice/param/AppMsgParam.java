package com.tsn.serv.mem.entity.notice.param;


public class AppMsgParam {
	
	private Integer status;
	
	private String yyyyMMdd;
	
	private String userId;
	
	private String receiverPhone;
	
	private String convertUserPhone;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getYyyyMMdd() {
		return yyyyMMdd;
	}

	public void setYyyyMMdd(String yyyyMMdd) {
		this.yyyyMMdd = yyyyMMdd;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getConvertUserPhone() {
		return convertUserPhone;
	}

	public void setConvertUserPhone(String convertUserPhone) {
		this.convertUserPhone = convertUserPhone;
	}

}

