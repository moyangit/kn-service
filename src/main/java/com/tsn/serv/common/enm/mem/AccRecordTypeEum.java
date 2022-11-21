package com.tsn.serv.common.enm.mem;

/**
 * //流水类型 10支付宝充值， 20微信充值，30银行卡充值 //11支付宝续费， 21微信续费， 31银行卡续费
 * @author work
 *
 */
public enum AccRecordTypeEum {
	//支付宝充值， 支付宝续费， 系统返现，个人提现， 提现驳回,  代理余额充值
	ali_pay_rech("10"), ali_pay_renew("11"), sys_rebate("40"), cashout("50"), reason("51"), recharge("52");
	
	private String code;
	
	AccRecordTypeEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
