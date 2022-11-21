package com.tsn.serv.mem.entity.mem;

import java.util.Date;

public class MemInfo {
    private String memId;
    
    private Integer memNo;
    
    private String chargeId;

    private String inviterUserId;
    
    private String inviterUserType;
    
    private String inviterCode;

    private Date inviterTime;

    private String memName;

    private String memCard;

    private String memPwd;

    private String memNickName;

    private String memSex;

    private String memReallyName;
    
    private String memPhone;
    
    private String memEmail;

    private String memAvatar;

    //会员类型 01:试用02;初级03:高级04:永久/代理
    private String memType;
    
    private String memCashDetail;
    
    private String isProxy;
    
    //是否已经支付过
    private String isPay;

    private String status;

    private Date firstRechargeDate;

    private Date lastRechargeDate;

    private Date suspenDate;
    
    private boolean isExpire;
    
    private int surDayNum;

	private String useTotalTraffic;

    private Date lastLoginTime;
    
    private Date guestRegDate;

    private Date regDate;
    
    private String parentInvCode;

    private Settings settings;

    private String remarks;
    //终端类型(1：PC  2：Android  3：IOS  4：MacOS)
    private String terminalType;

    private Date lastCashoutDate;

    private String deviceNum;

    private Integer duration;

    private Date changeSuspenDate;
    
    private String channelCode;
    
    private String deviceNo;

    public Settings getSettings() {
        return settings;
    }

    public String getIsPay() {
		return isPay;
	}

	public void setIsPay(String isPay) {
		this.isPay = isPay;
	}

	public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId == null ? null : chargeId.trim();
    }

    public String getInviterUserId() {
		return inviterUserId;
	}

	public void setInviterUserId(String inviterUserId) {
		this.inviterUserId = inviterUserId;
	}

	public String getInviterUserType() {
		return inviterUserType;
	}

	public void setInviterUserType(String inviterUserType) {
		this.inviterUserType = inviterUserType;
	}

	public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName == null ? null : memName.trim();
    }

    public int getSurDayNum() {
		return surDayNum;
	}

	public void setSurDayNum(int surDayNum) {
		this.surDayNum = surDayNum;
	}

	public String getMemCard() {
        return memCard;
    }

    public boolean isExpire() {
    	
    	if(suspenDate == null) {
    		return true;
    	}
    	
		return isExpire;
	}

	public void setExpire(boolean isExpire) {
		this.isExpire = isExpire;
	}
    public void setMemCard(String memCard) {
        this.memCard = memCard == null ? null : memCard.trim();
    }

    public String getMemPwd() {
        return memPwd;
    }

    public void setMemPwd(String memPwd) {
        this.memPwd = memPwd == null ? null : memPwd.trim();
    }

    public String getMemNickName() {
        return memNickName;
    }

    public void setMemNickName(String memNickName) {
        this.memNickName = memNickName == null ? null : memNickName.trim();
    }

    public String getMemSex() {
        return memSex;
    }

    public void setMemSex(String memSex) {
        this.memSex = memSex == null ? null : memSex.trim();
    }

    public String getMemReallyName() {
        return memReallyName;
    }

    public void setMemReallyName(String memReallyName) {
        this.memReallyName = memReallyName == null ? null : memReallyName.trim();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getFirstRechargeDate() {
        return firstRechargeDate;
    }

    public void setFirstRechargeDate(Date firstRechargeDate) {
        this.firstRechargeDate = firstRechargeDate;
    }

    public Date getLastRechargeDate() {
        return lastRechargeDate;
    }

    public void setLastRechargeDate(Date lastRechargeDate) {
        this.lastRechargeDate = lastRechargeDate;
    }

    public String getUseTotalTraffic() {
        return useTotalTraffic;
    }

    public void setUseTotalTraffic(String useTotalTraffic) {
        this.useTotalTraffic = useTotalTraffic == null ? null : useTotalTraffic.trim();
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

	public Date getSuspenDate() {
		return suspenDate;
	}

	public void setSuspenDate(Date suspenDate) {
		
		this.suspenDate = suspenDate;
		
		if (suspenDate == null) {
			this.isExpire = true;
			this.surDayNum = 0;
			return;
		}
		
		long currTime = new Date().getTime();
		long susTime = suspenDate.getTime();
		if (currTime > susTime) {
			this.isExpire = true;
			this.surDayNum = 0;
		}else {
			
			long diff =  (susTime - currTime) / (1000 * 60 * 60);
			
			this.surDayNum = (int) (diff < 24 ? 1 : diff / 24 + 1);
			
			this.isExpire = false;
		}
		
	}

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

    public String getIsProxy() {
		return isProxy;
	}

	public void setIsProxy(String isProxy) {
		this.isProxy = isProxy;
	}

	public String getInviterCode() {
		return inviterCode;
	}

	public void setInviterCode(String inviterCode) {
		this.inviterCode = inviterCode;
	}

    public Date getInviterTime() {
        return inviterTime;
    }

    public void setInviterTime(Date inviterTime) {
        this.inviterTime = inviterTime;
    }

    public String getParentInvCode() {
		return parentInvCode;
	}

	public void setParentInvCode(String parentInvCode) {
		this.parentInvCode = parentInvCode;
	}

	public String getMemCashDetail() {
		return memCashDetail;
	}

	public void setMemCashDetail(String memCashDetail) {
		this.memCashDetail = memCashDetail;
	}

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public Date getLastCashoutDate() {
        return lastCashoutDate;
    }

    public void setLastCashoutDate(Date lastCashoutDate) {
        this.lastCashoutDate = lastCashoutDate;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

    public Date getChangeSuspenDate() {
        return changeSuspenDate;
    }

    public void setChangeSuspenDate(Date changeSuspenDate) {
        this.changeSuspenDate = changeSuspenDate;
    }

    @Override
    public String toString() {
        return "MemInfo{" +
                "memId='" + memId + '\'' +
                ", chargeId='" + chargeId + '\'' +
                ", inviterUserId='" + inviterUserId + '\'' +
                ", inviterUserType='" + inviterUserType + '\'' +
                ", inviterCode='" + inviterCode + '\'' +
                ", inviterTime=" + inviterTime +
                ", memName='" + memName + '\'' +
                ", memCard='" + memCard + '\'' +
                ", memPwd='" + memPwd + '\'' +
                ", memNickName='" + memNickName + '\'' +
                ", memSex='" + memSex + '\'' +
                ", memReallyName='" + memReallyName + '\'' +
                ", memPhone='" + memPhone + '\'' +
                ", memAvatar='" + memAvatar + '\'' +
                ", memType='" + memType + '\'' +
                ", memCashDetail='" + memCashDetail + '\'' +
                ", isProxy='" + isProxy + '\'' +
                ", status='" + status + '\'' +
                ", firstRechargeDate=" + firstRechargeDate +
                ", lastRechargeDate=" + lastRechargeDate +
                ", suspenDate=" + suspenDate +
                ", isExpire=" + isExpire +
                ", surDayNum=" + surDayNum +
                ", useTotalTraffic='" + useTotalTraffic + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", regDate=" + regDate +
                ", parentInvCode='" + parentInvCode + '\'' +
                ", settings=" + settings +
                ", remarks='" + remarks + '\'' +
                ", terminalType='" + terminalType + '\'' +
                ", lastCashoutDate=" + lastCashoutDate +
                ", deviceNum='" + deviceNum + '\'' +
                ", duration=" + duration +
                ", changeSuspenDate=" + changeSuspenDate +
                '}';
    }

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getMemEmail() {
		return memEmail;
	}

	public void setMemEmail(String memEmail) {
		this.memEmail = memEmail;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public Date getGuestRegDate() {
		return guestRegDate;
	}

	public void setGuestRegDate(Date guestRegDate) {
		this.guestRegDate = guestRegDate;
	}

	public Integer getMemNo() {
		return memNo;
	}

	public void setMemNo(Integer memNo) {
		this.memNo = memNo;
	}
}