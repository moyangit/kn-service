package com.tsn.serv.mem.entity.order;

import java.math.BigDecimal;
import java.util.Date;

import com.tsn.common.core.norepeat.ReToken;

public class CashoutOrder extends ReToken{
    private String orderId;

    private String orderNo;

    private String cashType;

    private String cashAccNo;

    private String realName;

    private String realPhone;

    private String wxAccNo;
    
    private String memAccountNo;

	private String memId;

    private String memName;

    private String memRealName;

    private String memPhone;

    private String memType;

    private String remark;

    private BigDecimal cashMoney;

    private BigDecimal surplusMoney;

    private String orderStatus;

    private Date createTime;

    private Date updateTime;

    private String reason;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    @Override
    public String toString() {
        return "CashoutOrder{" +
                "orderId='" + orderId + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", cashType='" + cashType + '\'' +
                ", cashAccNo='" + cashAccNo + '\'' +
                ", realName='" + realName + '\'' +
                ", realPhone='" + realPhone + '\'' +
                ", wxAccNo='" + wxAccNo + '\'' +
                ", memAccountNo='" + memAccountNo + '\'' +
                ", memId='" + memId + '\'' +
                ", memName='" + memName + '\'' +
                ", memRealName='" + memRealName + '\'' +
                ", memPhone='" + memPhone + '\'' +
                ", memType='" + memType + '\'' +
                ", remark='" + remark + '\'' +
                ", cashMoney=" + cashMoney +
                ", surplusMoney=" + surplusMoney +
                ", orderStatus='" + orderStatus + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", reason='" + reason + '\'' +
                '}';
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getCashType() {
        return cashType;
    }

    public void setCashType(String cashType) {
        this.cashType = cashType == null ? null : cashType.trim();
    }

    public String getCashAccNo() {
        return cashAccNo;
    }

    public void setCashAccNo(String cashAccNo) {
        this.cashAccNo = cashAccNo == null ? null : cashAccNo.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getRealPhone() {
        return realPhone;
    }

    public void setRealPhone(String realPhone) {
        this.realPhone = realPhone == null ? null : realPhone.trim();
    }

    public String getWxAccNo() {
        return wxAccNo;
    }

    public void setWxAccNo(String wxAccNo) {
        this.wxAccNo = wxAccNo == null ? null : wxAccNo.trim();
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName == null ? null : memName.trim();
    }

    public String getMemRealName() {
        return memRealName;
    }

    public void setMemRealName(String memRealName) {
        this.memRealName = memRealName == null ? null : memRealName.trim();
    }

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone == null ? null : memPhone.trim();
    }

    public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType == null ? null : memType.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public BigDecimal getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(BigDecimal cashMoney) {
        this.cashMoney = cashMoney;
    }

    public BigDecimal getSurplusMoney() {
        return surplusMoney;
    }

    public void setSurplusMoney(BigDecimal surplusMoney) {
        this.surplusMoney = surplusMoney;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getMemAccountNo() {
		return memAccountNo;
	}

	public void setMemAccountNo(String memAccountNo) {
		this.memAccountNo = memAccountNo;
	}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}