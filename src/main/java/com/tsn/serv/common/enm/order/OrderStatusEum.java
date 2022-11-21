package com.tsn.serv.common.enm.order;

public enum OrderStatusEum {
	
	pay_wait("0"), pay_success("1"), pay_close("9");

	String status;
	OrderStatusEum (String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
