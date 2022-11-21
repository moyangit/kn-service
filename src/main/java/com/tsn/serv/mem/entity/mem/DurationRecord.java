package com.tsn.serv.mem.entity.mem;

import java.math.BigDecimal;
import java.util.Date;

public class DurationRecord {
    private String id;

    private String memId;

    private String durationSources;

    private Integer convertDuration;

    private BigDecimal convertValue;

    private String convertCardType;

    private Date convertTime;
    
    private String orderNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getDurationSources() {
        return durationSources;
    }

    public void setDurationSources(String durationSources) {
        this.durationSources = durationSources == null ? null : durationSources.trim();
    }

    public Integer getConvertDuration() {
        return convertDuration;
    }

    public void setConvertDuration(Integer convertDuration) {
        this.convertDuration = convertDuration;
    }

    public String getConvertCardType() {
        return convertCardType;
    }

    public void setConvertCardType(String convertCardType) {
        this.convertCardType = convertCardType == null ? null : convertCardType.trim();
    }

    public Date getConvertTime() {
        return convertTime;
    }

    public void setConvertTime(Date convertTime) {
        this.convertTime = convertTime;
    }

	public BigDecimal getConvertValue() {
		return convertValue;
	}

	public void setConvertValue(BigDecimal convertValue) {
		this.convertValue = convertValue;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}