package com.tsn.serv.pub.entity.customer;

import java.util.Date;

public class ServiceCustomerRefUser {
    private String userId;

    private Date createTime;

    private Date updateTime;

    private String cusIds;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
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

    public String getCusIds() {
        return cusIds;
    }

    public void setCusIds(String cusIds) {
        this.cusIds = cusIds == null ? null : cusIds.trim();
    }
}