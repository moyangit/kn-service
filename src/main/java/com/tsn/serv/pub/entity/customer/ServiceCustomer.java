package com.tsn.serv.pub.entity.customer;

import java.util.Date;

public class ServiceCustomer {
    private Long id;

    private String cusName;

    private String cusText;

    private String cusPic;

    private String cusLink;

    private String cusType;

    private String showType;

    private Byte seq;

    private Byte weight;
    
    private Byte status;
    
    private String remark;

    private Date createTime;

    private Date updateTime;

    public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName == null ? null : cusName.trim();
    }

    public String getCusText() {
        return cusText;
    }

    public void setCusText(String cusText) {
        this.cusText = cusText == null ? null : cusText.trim();
    }

    public String getCusPic() {
        return cusPic;
    }

    public void setCusPic(String cusPic) {
        this.cusPic = cusPic == null ? null : cusPic.trim();
    }

    public String getCusLink() {
        return cusLink;
    }

    public void setCusLink(String cusLink) {
        this.cusLink = cusLink == null ? null : cusLink.trim();
    }

    public String getCusType() {
        return cusType;
    }

    public void setCusType(String cusType) {
        this.cusType = cusType == null ? null : cusType.trim();
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType == null ? null : showType.trim();
    }

    public Byte getSeq() {
        return seq;
    }

    public void setSeq(Byte seq) {
        this.seq = seq;
    }

    public Byte getWeight() {
        return weight;
    }

    public void setWeight(Byte weight) {
        this.weight = weight;
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
}