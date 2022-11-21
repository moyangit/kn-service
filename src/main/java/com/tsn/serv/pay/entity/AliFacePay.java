package com.tsn.serv.pay.entity;

public class AliFacePay {
	
	private String outTradeNo;
	
	private String subject;
	
	private String totalAmount;
	
	private String timeout;

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "AliFacePay [outTradeNo=" + outTradeNo + ", subject=" + subject
				+ ", totalAmount=" + totalAmount + ", timeout=" + timeout + "]";
	}

}
