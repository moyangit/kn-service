package com.tsn.serv.mem.entity.flow;

import java.util.Date;

public class FlowLimit {
    private String memId;
    
    private String memType;

    private String flowLimitType;
    
    private String host;
    
    private String hostFlow;
    
    private long totalUseUpFlow;
    
    private long totalUseDownFlow;
    
    private long totalUseFlow;

    private Long cyUseUpFlow;

    private Long cyUseDownFlow;

    private Long cyUseTotalFlow;

    private Byte isLimit = 0;

    private Date cyStartTime;

    private Date currCyStartTime;

    private Date currCyEndTime;

    private Long maxUpFlowLimit;

    private Long maxDownFlowLimit;
    
    private Date limitUploadTime;
    
    private Date firstLimitHapTime;

    private Date createTime;

    private Date updateTime;
    
    private Long currUseUpFlow;
    
    private Long currUseDownFlow;
    
    private long maxMonthUseFlow;
    
	@Override
	public String toString() {
		return "FlowLimit [memId=" + memId + ", memType=" + memType
				+ ", flowLimitType=" + flowLimitType + ", host=" + host
				+ ", hostFlow=" + hostFlow + ", totalUseUpFlow="
				+ totalUseUpFlow + ", totalUseDownFlow=" + totalUseDownFlow
				+ ", totalUseFlow=" + totalUseFlow + ", cyUseUpFlow="
				+ cyUseUpFlow + ", cyUseDownFlow=" + cyUseDownFlow
				+ ", cyUseTotalFlow=" + cyUseTotalFlow + ", isLimit=" + isLimit
				+ ", cyStartTime=" + cyStartTime + ", currCyStartTime="
				+ currCyStartTime + ", currCyEndTime=" + currCyEndTime
				+ ", maxUpFlowLimit=" + maxUpFlowLimit + ", maxDownFlowLimit="
				+ maxDownFlowLimit + ", limitUploadTime=" + limitUploadTime
				+ ", firstLimitHapTime=" + firstLimitHapTime + ", createTime="
				+ createTime + ", updateTime=" + updateTime
				+ ", currUseUpFlow=" + currUseUpFlow + ", currUseDownFlow="
				+ currUseDownFlow + ", maxMonthUseFlow=" + maxMonthUseFlow
				+ "]";
	}

	public Long getCurrUseUpFlow() {
		return currUseUpFlow;
	}

	public void setCurrUseUpFlow(Long currUseUpFlow) {
		this.currUseUpFlow = currUseUpFlow;
	}

	public Long getCurrUseDownFlow() {
		return currUseDownFlow;
	}

	public void setCurrUseDownFlow(Long currUseDownFlow) {
		this.currUseDownFlow = currUseDownFlow;
	}

	public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getFlowLimitType() {
        return flowLimitType;
    }

    public void setFlowLimitType(String flowLimitType) {
        this.flowLimitType = flowLimitType == null ? null : flowLimitType.trim();
    }

    public Long getCyUseUpFlow() {
        return cyUseUpFlow;
    }

    public void setCyUseUpFlow(Long cyUseUpFlow) {
        this.cyUseUpFlow = cyUseUpFlow;
    }

    public Long getCyUseDownFlow() {
        return cyUseDownFlow;
    }

    public void setCyUseDownFlow(Long cyUseDownFlow) {
        this.cyUseDownFlow = cyUseDownFlow;
    }

    public Long getCyUseTotalFlow() {
        //return cyUseTotalFlow;
    	if (cyUseDownFlow != null && cyUseUpFlow != null) {
    		return cyUseDownFlow + cyUseUpFlow;
    	}
    	
    	return null;
    }

    public void setCyUseTotalFlow(Long cyUseTotalFlow) {
        this.cyUseTotalFlow = cyUseTotalFlow;
    }

    public Byte getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Byte isLimit) {
        this.isLimit = isLimit;
    }

    public Date getCyStartTime() {
        return cyStartTime;
    }

    public void setCyStartTime(Date cyStartTime) {
        this.cyStartTime = cyStartTime;
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

    public Long getMaxUpFlowLimit() {
        return maxUpFlowLimit;
    }

    public void setMaxUpFlowLimit(Long maxUpFlowLimit) {
        this.maxUpFlowLimit = maxUpFlowLimit;
    }

    public Long getMaxDownFlowLimit() {
        return maxDownFlowLimit;
    }

    public void setMaxDownFlowLimit(Long maxDownFlowLimit) {
        this.maxDownFlowLimit = maxDownFlowLimit;
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

	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public Date getFirstLimitHapTime() {
		return firstLimitHapTime;
	}

	public void setFirstLimitHapTime(Date firstLimitHapTime) {
		this.firstLimitHapTime = firstLimitHapTime;
	}

	public Date getLimitUploadTime() {
		return limitUploadTime;
	}

	public void setLimitUploadTime(Date limitUploadTime) {
		this.limitUploadTime = limitUploadTime;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHostFlow() {
		return hostFlow;
	}

	public void setHostFlow(String hostFlow) {
		this.hostFlow = hostFlow;
	}

	public long getTotalUseUpFlow() {
		return totalUseUpFlow;
	}

	public void setTotalUseUpFlow(long totalUseUpFlow) {
		this.totalUseUpFlow = totalUseUpFlow;
	}

	public long getTotalUseDownFlow() {
		return totalUseDownFlow;
	}

	public void setTotalUseDownFlow(long totalUseDownFlow) {
		this.totalUseDownFlow = totalUseDownFlow;
	}

	public long getTotalUseFlow() {
		return totalUseFlow;
	}

	public void setTotalUseFlow(long totalUseFlow) {
		this.totalUseFlow = totalUseFlow;
	}

	public long getMaxMonthUseFlow() {
		return maxMonthUseFlow;
	}

	public void setMaxMonthUseFlow(long maxMonthUseFlow) {
		this.maxMonthUseFlow = maxMonthUseFlow;
	}
}