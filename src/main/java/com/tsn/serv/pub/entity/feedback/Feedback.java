package com.tsn.serv.pub.entity.feedback;

import java.util.Date;

/**
 * 会员反馈
 */
public class Feedback {
    /**
     * 反馈id
     */
    private Integer id;

    /**
     * 会员id
     */
    private String memId;

    /**
     * 会员手机号
     */
    private String memPhone;

    /**
     * 会员名称
     */
    private String memName;

    /**
     * app类型 快加速：1 黑豹：2
     */
    private String appType;

    /**
     * 设备类型：10 安卓， 11 ios，12 pc， 13 mac
     */
    private String deviceType;

    /**
     * 反馈内容
     */
    private String remarks;

    /**
     * 创建时间
     */
    private Date createTime;

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

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone;
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
