package com.tsn.serv.mem.entity.proxy;

public class ProxyGroup {
    private Integer proxyGroupId;

    private String groupName;

    private String rebateConfig;

    private Integer isDefault;

    public Integer getProxyGroupId() {
        return proxyGroupId;
    }

    public void setProxyGroupId(Integer proxyGroupId) {
        this.proxyGroupId = proxyGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getRebateConfig() {
        return rebateConfig;
    }

    public void setRebateConfig(String rebateConfig) {
        this.rebateConfig = rebateConfig == null ? null : rebateConfig.trim();
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}