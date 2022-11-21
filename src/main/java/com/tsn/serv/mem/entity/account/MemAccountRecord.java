package com.tsn.serv.mem.entity.account;

import java.math.BigDecimal;
import java.util.Date;

public class MemAccountRecord {
    private String recordId;

    private String accountNo;

    private String recordType;

    private Byte recordWay;

    private String recordStatus;

    private String orderNo;
    
    private String orderContent;

    private String orderPayType;

    private BigDecimal changeMoney;

    private BigDecimal balanceMoney;

    private String remark;

    private String memId;

    private String memPhone;

    private String memName;

    private String outMemId;

    public String getOutMemId() {
		return outMemId;
	}

	public void setOutMemId(String outMemId) {
		this.outMemId = outMemId;
	}

	private String outMemPhone;

    private String outMemName;

    private Date createTime;

    private Date updateTime;

    public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo == null ? null : accountNo.trim();
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType == null ? null : recordType.trim();
    }

    public Byte getRecordWay() {
        return recordWay;
    }

    public void setRecordWay(Byte recordWay) {
        this.recordWay = recordWay;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus == null ? null : recordStatus.trim();
    }

    @Override
	public String toString() {
		return "MemAccountRecord [recordId=" + recordId + ", accountNo="
				+ accountNo + ", recordType=" + recordType + ", recordWay="
				+ recordWay + ", recordStatus=" + recordStatus + ", orderNo="
				+ orderNo + ", orderContent=" + orderContent
				+ ", orderPayType=" + orderPayType + ", changeMoney="
				+ changeMoney + ", balanceMoney=" + balanceMoney + ", remark="
				+ remark + ", memId=" + memId + ", memPhone=" + memPhone
				+ ", memName=" + memName + ", outMemId=" + outMemId
				+ ", outMemPhone=" + outMemPhone + ", outMemName=" + outMemName
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderPayType() {
        return orderPayType;
    }

    public void setOrderPayType(String orderPayType) {
        this.orderPayType = orderPayType == null ? null : orderPayType.trim();
    }

    public BigDecimal getChangeMoney() {
        return changeMoney;
    }

    public void setChangeMoney(BigDecimal changeMoney) {
        this.changeMoney = changeMoney;
    }

    public BigDecimal getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(BigDecimal balanceMoney) {
        this.balanceMoney = balanceMoney;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone == null ? null : memPhone.trim();
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName == null ? null : memName.trim();
    }

    public String getOutMemPhone() {
        return outMemPhone;
    }

    public void setOutMemPhone(String outMemPhone) {
        this.outMemPhone = outMemPhone == null ? null : outMemPhone.trim();
    }

    public String getOutMemName() {
        return outMemName;
    }

    public void setOutMemName(String outMemName) {
        this.outMemName = outMemName == null ? null : outMemName.trim();
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

	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}
}