package com.tsn.serv.common.enm.mem;

public enum MemProxyLvlEum {
	
	lvl0("20"), lvl1("40");
	
	private String code;
	
	private MemProxyLvlEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
