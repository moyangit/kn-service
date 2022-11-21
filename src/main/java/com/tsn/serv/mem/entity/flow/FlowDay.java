package com.tsn.serv.mem.entity.flow;

import java.util.Date;

public class FlowDay {
    private String seriNo;

    @Override
	public String toString() {
		return "FlowDay [seriNo=" + seriNo + ", memId=" + memId + ", host="
				+ host + ", day=" + day + ", upFlow=" + upFlow + ", downFlow="
				+ downFlow + ", createDate=" + createDate + ", hostFlow="
				+ hostFlow + ", updateDate=" + updateDate + "]";
	}

	private String memId;
    
    private String host;
    
	private String day;

    private Long upFlow;

    private Long downFlow;

    private Date createDate;

    private String hostFlow;
    
    private Date updateDate;

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day == null ? null : day.trim();
    }

    public Long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(Long upFlow) {
        this.upFlow = upFlow;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Long getDownFlow() {
		return downFlow;
	}

	public void setDownFlow(Long downFlow) {
		this.downFlow = downFlow;
	}

	public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getHostFlow() {
        return hostFlow;
    }

    public void setHostFlow(String hostFlow) {
        this.hostFlow = hostFlow == null ? null : hostFlow.trim();
    }

	public String getSeriNo() {
		return seriNo;
	}

	public void setSeriNo(String seriNo) {
		this.seriNo = seriNo;
	}
}