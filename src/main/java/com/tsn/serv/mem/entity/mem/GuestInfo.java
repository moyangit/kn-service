package com.tsn.serv.mem.entity.mem;

import java.util.Date;

public class GuestInfo {
    private String memId;

    private String memName;

    private String memPhone;

    private String memAvatar;

    private String memType;

    private String deviceNo;

    private String deviceType;

    private String status;

    private Date suspenDate;

    private Date regDate;

    private String remarks;

    private Date lastUseTime;
    
    private String channelCode;
    
    private boolean isExpire;
    
    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName == null ? null : memName.trim();
    }

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone == null ? null : memPhone.trim();
    }

    public String getMemAvatar() {
        return memAvatar;
    }

    public void setMemAvatar(String memAvatar) {
        this.memAvatar = memAvatar == null ? null : memAvatar.trim();
    }

    public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType == null ? null : memType.trim();
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo == null ? null : deviceNo.trim();
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType == null ? null : deviceType.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getSuspenDate() {
        return suspenDate;
    }

    public void setSuspenDate(Date suspenDate) {
        this.suspenDate = suspenDate;
        
        if (suspenDate == null) {
        	this.setExpire(true);
        	return;
        }
        
		long currTime = new Date().getTime();
		long susTime = suspenDate.getTime();
		if (currTime > susTime) {
			this.setExpire(true);
		}else {
			this.setExpire(false);
		}
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Date getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(Date lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public boolean isExpire() {
		return isExpire;
	}

	public void setExpire(boolean isExpire) {
		this.isExpire = isExpire;
	}
}