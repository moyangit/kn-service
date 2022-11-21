package com.tsn.serv.mem.entity.credits;

import java.util.Date;

import com.tsn.common.core.norepeat.ReToken;

public class Credits  extends ReToken{
    private String id;

    private String memId;

    private Integer creditsVal;

    private Integer creditsTotalVal;

    private Date createTime;

    private Date updateTime;
    
    private Integer countLock;

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

    public Integer getCreditsVal() {
        return creditsVal;
    }

    public void setCreditsVal(Integer creditsVal) {
        this.creditsVal = creditsVal;
    }

    public Integer getCreditsTotalVal() {
        return creditsTotalVal;
    }

    public void setCreditsTotalVal(Integer creditsTotalVal) {
        this.creditsTotalVal = creditsTotalVal;
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
    
    	
	/**
	 * 时长卡片类型
	 */
	private String cardType;

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Integer getCountLock() {
		return countLock;
	}

	public void setCountLock(Integer countLock) {
		this.countLock = countLock;
	}
}