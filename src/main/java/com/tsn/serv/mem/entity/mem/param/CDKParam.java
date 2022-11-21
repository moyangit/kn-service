package com.tsn.serv.mem.entity.mem.param;


public class CDKParam {
	
	private Integer status;
	
	private String yyyyMMdd;
	
	private String sourceType;
	
	private String memName;
	
	private String cdkNo;
	
	private String convertMemName;

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

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public String getConvertMemName() {
		return convertMemName;
	}

	public void setConvertMemName(String convertMemName) {
		this.convertMemName = convertMemName;
	}

	public String getCdkNo() {
		return cdkNo;
	}

	public void setCdkNo(String cdkNo) {
		this.cdkNo = cdkNo;
	}

}

