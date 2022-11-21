package com.tsn.serv.mem.entity.device;

import java.util.Date;

public class MemDevice {

    /**
     * 会员设备表
     */
    private Integer id;

    /**
     * 会员id
     */
    private String memId;

    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * 设备名
     */
    private String deviceName;

    /**
     * 设备类型：10 安卓， 11 ios，12 pc， 13 mac
     */
    private String deviceType;

    /**
     * 设备状态 0：离线 1：使用
     */
    private String deviceStatus;

    /**
     * 最近操作时间
     */
    private Date useTime;

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

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }
}
