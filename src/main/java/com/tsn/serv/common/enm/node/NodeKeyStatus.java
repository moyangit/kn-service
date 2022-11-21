package com.tsn.serv.common.enm.node;

public enum NodeKeyStatus {
	
	create(0), write_success(1);
	
	private int code;
	
	private NodeKeyStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
