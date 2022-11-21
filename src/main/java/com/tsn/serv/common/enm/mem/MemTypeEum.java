package com.tsn.serv.common.enm.mem;

public enum MemTypeEum {
	
	guest_mem("00"), trial_mem("01"), general_mem("02");
	
	private String code;
	
	private MemTypeEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
