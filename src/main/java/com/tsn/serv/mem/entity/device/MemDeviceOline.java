package com.tsn.serv.mem.entity.device;

import java.util.Date;

public class MemDeviceOline {
    /**
     * 会员设备实时表
     */
    private Integer id;

    /**
     * 会员id
     */
    private String memId;

    /**
     * 设备登录信息：对应设备表(v_mem_device)
     */
    private String deviceJson;

    private String status;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getDeviceJson() {
        return deviceJson;
    }

    public void setDeviceJson(String deviceJson) {
        this.deviceJson = deviceJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
