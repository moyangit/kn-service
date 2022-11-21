package com.tsn.serv.mem.entity.order;

import java.math.BigDecimal;
import java.util.Date;

import com.tsn.common.core.norepeat.ReToken;

public class ChargeOrder extends ReToken{
    private String orderId;

    private String orderNo;

    private String payType;

    private String tradeNo;

    private String tradeStatus;

    private String memId;
    
    private Integer memNo;

    private String memName;

    private String memRealName;
    
    private String memPhone;
    
    private String memEmail;

    private String memType;
    
    private String memRegTime;
    
    private Date memBeforeSuspenDate;
    
    private String chargeId;

    private String chargeType;

    private BigDecimal chargePrice;

    private String subject;

    private BigDecimal costPrice;

    private Integer discount;

    private BigDecimal finalPrice;

    private Integer rebate;
    
    private String invitUserId;
    
    private String invitUserType;
    
    private String invitUserName;
    
    private String rebateUserId;

    private String rebateUserType;

    private String rebateStatus;
    
    private String rebateUserAccountNo;
    
    private String rebateUserPhone;
    
    private String rebateUserEmail;
    
    private String rebateUserName;
    
    private Integer rebateLvl;
    
    private String rebateDetails;

    private String orderStatus;

    private Date createTime;

    private Date updateTime;

    private Date tradePayTime;

    private Date tradeRefundTime;
    
    private Integer orderTimeout;

    public String getMemEmail() {
		return memEmail;
	}

	public void setMemEmail(String memEmail) {
		this.memEmail = memEmail;
	}

	public String getRebateUserPhone() {
		return rebateUserPhone;
	}

	public void setRebateUserPhone(String rebateUserPhone) {
		this.rebateUserPhone = rebateUserPhone;
	}

	public String getRebateUserName() {
		return rebateUserName;
	}

	public void setRebateUserName(String rebateUserName) {
		this.rebateUserName = rebateUserName;
	}

    public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
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

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId == null ? null : chargeId.trim();
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType == null ? null : chargeType.trim();
    }

    public BigDecimal getChargePrice() {
        return chargePrice;
    }

    public void setChargePrice(BigDecimal chargePrice) {
        this.chargePrice = chargePrice;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getRebate() {
        return rebate;
    }

    public void setRebate(Integer rebate) {
        this.rebate = rebate;
    }

    public String getRebateUserId() {
        return rebateUserId;
    }

    public void setRebateUserId(String rebateUserId) {
        this.rebateUserId = rebateUserId == null ? null : rebateUserId.trim();
    }

    public String getRebateUserType() {
        return rebateUserType;
    }

    public void setRebateUserType(String rebateUserType) {
        this.rebateUserType = rebateUserType == null ? null : rebateUserType.trim();
    }

    public String getRebateStatus() {
        return rebateStatus;
    }

    public void setRebateStatus(String rebateStatus) {
        this.rebateStatus = rebateStatus == null ? null : rebateStatus.trim();
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

    public Date getTradePayTime() {
        return tradePayTime;
    }

    public void setTradePayTime(Date tradePayTime) {
        this.tradePayTime = tradePayTime;
    }

    public Date getTradeRefundTime() {
        return tradeRefundTime;
    }

    public void setTradeRefundTime(Date tradeRefundTime) {
        this.tradeRefundTime = tradeRefundTime;
    }

	public String getRebateUserAccountNo() {
		return rebateUserAccountNo;
	}

	public void setRebateUserAccountNo(String rebateUserAccountNo) {
		this.rebateUserAccountNo = rebateUserAccountNo;
	}

	@Override
	public String toString() {
		return "ChargeOrder [orderId=" + orderId + ", orderNo=" + orderNo
				+ ", payType=" + payType + ", tradeNo=" + tradeNo
				+ ", tradeStatus=" + tradeStatus + ", memId=" + memId
				+ ", memName=" + memName + ", memRealName=" + memRealName
				+ ", memPhone=" + memPhone + ", memType=" + memType
				+ ", memBeforeSuspenDate=" + memBeforeSuspenDate
				+ ", chargeId=" + chargeId + ", chargeType=" + chargeType
				+ ", chargePrice=" + chargePrice + ", subject=" + subject
				+ ", costPrice=" + costPrice + ", discount=" + discount
				+ ", finalPrice=" + finalPrice + ", rebate=" + rebate
				+ ", rebateUserId=" + rebateUserId + ", rebateUserType="
				+ rebateUserType + ", rebateStatus=" + rebateStatus
				+ ", rebateUserAccountNo=" + rebateUserAccountNo
				+ ", rebateUserPhone=" + rebateUserPhone + ", rebateUserName="
				+ rebateUserName + ", rebateLvl=" + rebateLvl
				+ ", rebateDetails=" + rebateDetails + ", orderStatus="
				+ orderStatus + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", tradePayTime=" + tradePayTime
				+ ", tradeRefundTime=" + tradeRefundTime + ", orderTimeout="
				+ orderTimeout + "]";
	}

	public String getRebateDetails() {
		return rebateDetails;
	}

	public void setRebateDetails(String rebateDetails) {
		this.rebateDetails = rebateDetails;
	}

	public Integer getRebateLvl() {
		return rebateLvl;
	}

	public void setRebateLvl(Integer rebateLvl) {
		this.rebateLvl = rebateLvl;
	}

	public Date getMemBeforeSuspenDate() {
		return memBeforeSuspenDate;
	}

	public void setMemBeforeSuspenDate(Date memBeforeSuspenDate) {
		this.memBeforeSuspenDate = memBeforeSuspenDate;
	}

	public Integer getOrderTimeout() {
		return orderTimeout;
	}

	public void setOrderTimeout(Integer orderTimeout) {
		this.orderTimeout = orderTimeout;
	}

	public String getRebateUserEmail() {
		return rebateUserEmail;
	}

	public void setRebateUserEmail(String rebateUserEmail) {
		this.rebateUserEmail = rebateUserEmail;
	}

	public String getInvitUserId() {
		return invitUserId;
	}

	public void setInvitUserId(String invitUserId) {
		this.invitUserId = invitUserId;
	}

	public String getInvitUserType() {
		return invitUserType;
	}

	public void setInvitUserType(String invitUserType) {
		this.invitUserType = invitUserType;
	}

	public String getInvitUserName() {
		return invitUserName;
	}

	public void setInvitUserName(String invitUserName) {
		this.invitUserName = invitUserName;
	}

	public String getMemRegTime() {
		return memRegTime;
	}

	public void setMemRegTime(String memRegTime) {
		this.memRegTime = memRegTime;
	}

	public Integer getMemNo() {
		return memNo;
	}

	public void setMemNo(Integer memNo) {
		this.memNo = memNo;
	}

}