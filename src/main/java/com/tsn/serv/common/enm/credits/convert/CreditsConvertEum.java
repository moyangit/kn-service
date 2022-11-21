package com.tsn.serv.common.enm.credits.convert;

/**
 * 积分兑换
 * @author work
 *
 */
public enum CreditsConvertEum {
	//一小时时长卡
	one_hour("50"), 
	//三小时时长卡
	three_hour("150"),
	//五小时时长卡
	five_hour("250");
	
	private String code;
	
	CreditsConvertEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
