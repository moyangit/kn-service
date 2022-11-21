package com.tsn.serv.common.entity.rebate;

public class RebateInfo {
	
	private String orderNo;
	
	private String rebateType;
	
	public RebateInfo() {
		
	}
	
	public RebateInfo(String orderNo, String rebateType) {
		this.orderNo = orderNo;
		this.rebateType = rebateType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRebateType() {
		return rebateType;
	}

	public void setRebateType(String rebateType) {
		this.rebateType = rebateType;
	}
	
}
