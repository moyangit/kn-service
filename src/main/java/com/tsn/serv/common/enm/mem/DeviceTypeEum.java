package com.tsn.serv.common.enm.mem;

public enum DeviceTypeEum {
	
	ad("10"), ios("11"), win("12"), mac("13");
	
	private String code;
	
	private DeviceTypeEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
