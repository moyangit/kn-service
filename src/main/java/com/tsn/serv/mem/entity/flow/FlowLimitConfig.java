package com.tsn.serv.mem.entity.flow;

import java.util.Date;

public class FlowLimitConfig {
    private String uuid;

    private String memType;

    private Long maxDayUseFlow;

    private Long dayUpFlowLimit;

    private Long dayDownFlowLimit;

    private Long maxMonthUseFlow;

    private Long monthUpFlowLimit;

    private Long monthDownFlowLimit;

    private String createUserId;

    private Date createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType == null ? null : memType.trim();
    }

    public Long getMaxDayUseFlow() {
        return maxDayUseFlow;
    }

    public void setMaxDayUseFlow(Long maxDayUseFlow) {
        this.maxDayUseFlow = maxDayUseFlow;
    }

    public Long getDayUpFlowLimit() {
        return dayUpFlowLimit;
    }

    public void setDayUpFlowLimit(Long dayUpFlowLimit) {
        this.dayUpFlowLimit = dayUpFlowLimit;
    }

    public Long getDayDownFlowLimit() {
        return dayDownFlowLimit;
    }

    public void setDayDownFlowLimit(Long dayDownFlowLimit) {
        this.dayDownFlowLimit = dayDownFlowLimit;
    }

    public Long getMaxMonthUseFlow() {
		return maxMonthUseFlow;
	}

	public void setMaxMonthUseFlow(Long maxMonthUseFlow) {
		this.maxMonthUseFlow = maxMonthUseFlow;
	}

	public Long getMonthUpFlowLimit() {
        return monthUpFlowLimit;
    }

    public void setMonthUpFlowLimit(Long monthUpFlowLimit) {
        this.monthUpFlowLimit = monthUpFlowLimit;
    }

    public Long getMonthDownFlowLimit() {
        return monthDownFlowLimit;
    }

    public void setMonthDownFlowLimit(Long monthDownFlowLimit) {
        this.monthDownFlowLimit = monthDownFlowLimit;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}