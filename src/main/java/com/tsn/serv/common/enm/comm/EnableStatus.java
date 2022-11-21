package com.tsn.serv.common.enm.comm;

public enum EnableStatus {
	
	enable(0), disable(1);
	
	private int code;
	
	private EnableStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
