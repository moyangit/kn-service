package com.tsn.serv.mem.entity.flow;

import java.util.Date;

public class FlowMonthCy {
	private String seriNo;

    private String memId;

    private Date currCyStartTime;

    private Date currCyEndTime;

    private Date limitHapTime;

    private Long upFlow;

    private Long downFlow;

    private Date createDate;

    private Date updateDate;

    private String hostFlow;

    public String getSeriNo() {
		return seriNo;
	}

	public void setSeriNo(String seriNo) {
		this.seriNo = seriNo;
	}

	public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public Date getCurrCyStartTime() {
        return currCyStartTime;
    }

    public void setCurrCyStartTime(Date currCyStartTime) {
        this.currCyStartTime = currCyStartTime;
    }

    public Date getCurrCyEndTime() {
        return currCyEndTime;
    }

    public void setCurrCyEndTime(Date currCyEndTime) {
        this.currCyEndTime = currCyEndTime;
    }

    public Date getLimitHapTime() {
        return limitHapTime;
    }

    public void setLimitHapTime(Date limitHapTime) {
        this.limitHapTime = limitHapTime;
    }

    public Long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(Long upFlow) {
        this.upFlow = upFlow;
    }

    public Long getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(Long downFlow) {
        this.downFlow = downFlow;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getHostFlow() {
        return hostFlow;
    }

    public void setHostFlow(String hostFlow) {
        this.hostFlow = hostFlow == null ? null : hostFlow.trim();
    }
}