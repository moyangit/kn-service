package com.tsn.serv.mem.entity.mem;

import javax.validation.constraints.NotNull;

import com.tsn.common.core.norepeat.ReToken;

public class MemCashoutInfo extends ReToken{
	
	//支付宝 和  微信
	@NotNull
	private String cashType;
	
	@NotNull
	private String cashAccNo;
	
	@NotNull
	private String wxNo;
	
	@NotNull
	private String userPhone;
	
	@NotNull
	private String realName;
	
	@NotNull
	private String money;
	
	private String memId;

	public String getCashType() {
		return cashType;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public void setCashType(String cashType) {
		this.cashType = cashType;
	}

	public String getCashAccNo() {
		return cashAccNo;
	}

	public void setCashAccNo(String cashAccNo) {
		this.cashAccNo = cashAccNo;
	}

	public String getWxNo() {
		return wxNo;
	}

	public void setWxNo(String wxNo) {
		this.wxNo = wxNo;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	@Override
	public String toString() {
		return "MemCashoutInfo [cashType=" + cashType + ", cashAccNo="
				+ cashAccNo + ", wxNo=" + wxNo + ", userPhone=" + userPhone
				+ ", realName=" + realName + ", money=" + money + ", memId="
				+ memId + "]";
	}

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

}
