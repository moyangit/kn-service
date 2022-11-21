package com.tsn.serv.mem.entity.credits;

import java.util.Date;

/**
 * 兑换券
 */
public class CreditsCoupon {
    private String id;

    private String couponName;

    private String couponType;

    private Integer couponDuration;

    private Integer couponCredits;

    private Integer couponNum;

    private String iconUrl;

    private String status;

    private Date createTime;

    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName == null ? null : couponName.trim();
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType == null ? null : couponType.trim();
    }

    public Integer getCouponDuration() {
        return couponDuration;
    }

    public void setCouponDuration(Integer couponDuration) {
        this.couponDuration = couponDuration;
    }

    public Integer getCouponCredits() {
        return couponCredits;
    }

    public void setCouponCredits(Integer couponCredits) {
        this.couponCredits = couponCredits;
    }

    public Integer getCouponNum() {
        return couponNum;
    }

    public void setCouponNum(Integer couponNum) {
        this.couponNum = couponNum;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl == null ? null : iconUrl.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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
}