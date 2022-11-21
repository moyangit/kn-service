package com.tsn.serv.mem.entity.credits;

import java.util.Date;

import com.tsn.common.core.norepeat.ReToken;

public class CreditsConvertOrder  extends ReToken{
    private String id;

    private String memId;

    private String orderNo;

    private Integer convertDuration;

    private String convertType;

    private Date convertTime;

    private Byte convertStatus;

    private Date updateTime;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getConvertDuration() {
        return convertDuration;
    }

    public void setConvertDuration(Integer convertDuration) {
        this.convertDuration = convertDuration;
    }

    public String getConvertType() {
        return convertType;
    }

    public void setConvertType(String convertType) {
        this.convertType = convertType == null ? null : convertType.trim();
    }

    public Date getConvertTime() {
        return convertTime;
    }

    public void setConvertTime(Date convertTime) {
        this.convertTime = convertTime;
    }

    public Byte getConvertStatus() {
        return convertStatus;
    }

    public void setConvertStatus(Byte convertStatus) {
        this.convertStatus = convertStatus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}