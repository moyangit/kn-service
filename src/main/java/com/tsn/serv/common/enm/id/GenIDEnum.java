package com.tsn.serv.common.enm.id;

public enum GenIDEnum {
	
	DEF(""),
	
	MEM_ID("1"), ACCOUNT_NO("10"), RECH_ORDER_NO("20"), BALANCE_ORDER_NO("21"), CASH_ORDER_NO("30"), CHANNEL_NO("40"), 
	//积分流水编号
	CREDITS_RECORD_NO("40"), 
	//任务编号
	TASK_NO("50"), 
	//积分兑换编号
	CREDITS_CONVERT_NO("60"),
	//游客ID
	GUEST_MEMID("2");
	
	String preFix;
	
	GenIDEnum (String preFix) {
		this.preFix = preFix;
	}

	public String getPreFix() {
		return preFix;
	}

	public void setPreFix(String preFix) {
		this.preFix = preFix;
	}

}
