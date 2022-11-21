package com.tsn.serv.pub.entity.access;

import java.util.Date;

/**
 * 访问
 */
public class AccessHis {
    private String accessId;

    /**
     * 访问ip
     */
    private String accessIp;
    
    private String accessPath;
    
    private String sourceCode;

    private String sourceTermType;

    private String sourceArea;

    private String sourceAreaName;

    /**
     * 访问时间
     */
    private Date accessTime;

    /**
     * 上次访问时间
     */
    private Date accessLastTime;

    /**
     * 停留时间
     */
    private Long remainTime;
    
    private Date createTime;

    private String isDown;
    
    public AccessHis() {
    	
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public void setAccessIp(String accessIp) {
        this.accessIp = accessIp;
    }

    public String getSourceTermType() {
        return sourceTermType;
    }

    public void setSourceTermType(String sourceTermType) {
        this.sourceTermType = sourceTermType;
    }

    public String getSourceArea() {
        return sourceArea;
    }

    public void setSourceArea(String sourceArea) {
        this.sourceArea = sourceArea;
    }

    public String getSourceAreaName() {
        return sourceAreaName;
    }

    public void setSourceAreaName(String sourceAreaName) {
        this.sourceAreaName = sourceAreaName;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public Date getAccessLastTime() {
        return accessLastTime;
    }

    public void setAccessLastTime(Date accessLastTime) {
        this.accessLastTime = accessLastTime;
    }

    public Long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Long remainTime) {
        this.remainTime = remainTime;
    }

	public String getAccessPath() {
		return accessPath;
	}

	public void setAccessPath(String accessPath) {
		this.accessPath = accessPath;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

    public String getIsDown() {
        return isDown;
    }

    public void setIsDown(String isDown) {
        this.isDown = isDown;
    }

    @Override
	public String toString() {
		return "AccessHis [accessId=" + accessId + ", accessIp=" + accessIp
				+ ", accessPath=" + accessPath + ", sourceCode=" + sourceCode
				+ ", sourceTermType=" + sourceTermType + ", sourceArea="
				+ sourceArea + ", sourceAreaName=" + sourceAreaName
				+ ", accessTime=" + accessTime + ", accessLastTime="
				+ accessLastTime + ", remainTime=" + remainTime
				+ ", createTime=" + createTime + "]";
	}
}
