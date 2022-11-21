package com.tsn.serv.mem.entity.credits;

import java.util.Date;

public class CreditsRecord {
    private String id;

    private String creRecordNo;

    private Byte creRecordType;

    private String memId;

    private Integer credits;
    
    private Integer creditsBalance;

    private String creSourceId;

    private String creSourceNo;

    private String creSourceType;

    private String creSourceDetail;

    private String creTargetId;

    private String creTargetNo;

    private String creTargetType;

    private String creTargetDetail;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCreRecordNo() {
        return creRecordNo;
    }

    public void setCreRecordNo(String creRecordNo) {
        this.creRecordNo = creRecordNo == null ? null : creRecordNo.trim();
    }

    public Byte getCreRecordType() {
        return creRecordType;
    }

    public void setCreRecordType(Byte creRecordType) {
        this.creRecordType = creRecordType;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer integer) {
        this.credits = integer;
    }

    public String getCreSourceId() {
        return creSourceId;
    }

    public void setCreSourceId(String creSourceId) {
        this.creSourceId = creSourceId == null ? null : creSourceId.trim();
    }

    public String getCreSourceNo() {
        return creSourceNo;
    }

    public void setCreSourceNo(String creSourceNo) {
        this.creSourceNo = creSourceNo == null ? null : creSourceNo.trim();
    }

    public String getCreSourceType() {
        return creSourceType;
    }

    public void setCreSourceType(String creSourceType) {
        this.creSourceType = creSourceType == null ? null : creSourceType.trim();
    }

    public String getCreSourceDetail() {
        return creSourceDetail;
    }

    public void setCreSourceDetail(String creSourceDetail) {
        this.creSourceDetail = creSourceDetail == null ? null : creSourceDetail.trim();
    }

    public String getCreTargetId() {
        return creTargetId;
    }

    public void setCreTargetId(String creTargetId) {
        this.creTargetId = creTargetId == null ? null : creTargetId.trim();
    }

    public String getCreTargetNo() {
        return creTargetNo;
    }

    public void setCreTargetNo(String creTargetNo) {
        this.creTargetNo = creTargetNo == null ? null : creTargetNo.trim();
    }

    public String getCreTargetType() {
        return creTargetType;
    }

    public void setCreTargetType(String creTargetType) {
        this.creTargetType = creTargetType == null ? null : creTargetType.trim();
    }

    public String getCreTargetDetail() {
        return creTargetDetail;
    }

    public void setCreTargetDetail(String creTargetDetail) {
        this.creTargetDetail = creTargetDetail == null ? null : creTargetDetail.trim();
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
    
    public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public Integer getCreditsBalance() {
		return creditsBalance;
	}

	public void setCreditsBalance(Integer creditsBalance) {
		this.creditsBalance = creditsBalance;
	}

	private String createTimeStr;
}