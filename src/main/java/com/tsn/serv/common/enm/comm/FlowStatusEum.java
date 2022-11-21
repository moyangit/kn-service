package com.tsn.serv.common.enm.comm;

public enum FlowStatusEum {
	
	create("0"), success("1"), fail("2");

	String status;
	
	FlowStatusEum(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
