package com.tsn.serv.mem.entity.proxy;

import java.util.Date;

public class ProxyInfo {
    private String proxyId;

    private String memType;
    
    private String proxyLvl;

    private String proxyName;

    private String proxyReallyName;

    private String proxyCard;

    private String proxyNickName;

    private String proxyPhone;

    private String proxyGroupid;
    
    private String rebateConfig;

	private Date createTime;

    private Date updateTime;

    @Override
	public String toString() {
		return "ProxyInfo [proxyId=" + proxyId + ", memType=" + memType
				+ ", proxyName=" + proxyName + ", proxyReallyName="
				+ proxyReallyName + ", proxyCard=" + proxyCard
				+ ", proxyNickName=" + proxyNickName + ", proxyPhone="
				+ proxyPhone + ", proxyGroupid=" + proxyGroupid
				+ ", rebateConfig=" + rebateConfig + ", createTime="
				+ createTime + ", updateTime=" + updateTime + "]";
	}
    
    public String getProxyId() {
		return proxyId;
	}

	public void setProxyId(String proxyId) {
		this.proxyId = proxyId;
	}

	public String getMemType() {
        return memType;
    }

    public void setMemType(String memType) {
        this.memType = memType == null ? null : memType.trim();
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName == null ? null : proxyName.trim();
    }

    public String getProxyReallyName() {
        return proxyReallyName;
    }

    public void setProxyReallyName(String proxyReallyName) {
        this.proxyReallyName = proxyReallyName == null ? null : proxyReallyName.trim();
    }

    public String getProxyCard() {
        return proxyCard;
    }

    public void setProxyCard(String proxyCard) {
        this.proxyCard = proxyCard == null ? null : proxyCard.trim();
    }

    public String getProxyNickName() {
        return proxyNickName;
    }

    public void setProxyNickName(String proxyNickName) {
        this.proxyNickName = proxyNickName == null ? null : proxyNickName.trim();
    }

    public String getProxyPhone() {
        return proxyPhone;
    }

    public void setProxyPhone(String proxyPhone) {
        this.proxyPhone = proxyPhone == null ? null : proxyPhone.trim();
    }

    public String getProxyGroupid() {
        return proxyGroupid;
    }

    public void setProxyGroupid(String proxyGroupid) {
        this.proxyGroupid = proxyGroupid == null ? null : proxyGroupid.trim();
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

	public String getRebateConfig() {
		return rebateConfig;
	}

	public void setRebateConfig(String rebateConfig) {
		this.rebateConfig = rebateConfig;
	}

	public String getProxyLvl() {
		return proxyLvl;
	}

	public void setProxyLvl(String proxyLvl) {
		this.proxyLvl = proxyLvl;
	}
}