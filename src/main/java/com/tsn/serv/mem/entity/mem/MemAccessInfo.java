package com.tsn.serv.mem.entity.mem;

import java.util.Date;

/**
 * 会员连接信息
 * @author work
 *
 */
public class MemAccessInfo {
	
	private String memId;
	
	private String memType;
	
	private Date suspenDate;
	
	private String memStatus;
	
	private String nodeKey;
	
    private Integer alterId;
    
    private Integer level;

    private String email;

    private String inBoundTag;
    
    private long upFlowLimit;
    
    private long downFlowLimit;

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public Date getSuspenDate() {
		return suspenDate;
	}

	public void setSuspenDate(Date suspenDate) {
		this.suspenDate = suspenDate;
	}

	public String getMemStatus() {
		return memStatus;
	}

	public void setMemStatus(String memStatus) {
		this.memStatus = memStatus;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public Integer getAlterId() {
		return alterId;
	}

	public void setAlterId(Integer alterId) {
		this.alterId = alterId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInBoundTag() {
		return inBoundTag;
	}

	public void setInBoundTag(String inBoundTag) {
		this.inBoundTag = inBoundTag;
	}

	public long getUpFlowLimit() {
		return upFlowLimit;
	}

	public void setUpFlowLimit(long upFlowLimit) {
		this.upFlowLimit = upFlowLimit;
	}

	public long getDownFlowLimit() {
		return downFlowLimit;
	}

	public void setDownFlowLimit(long downFlowLimit) {
		this.downFlowLimit = downFlowLimit;
	}
}
