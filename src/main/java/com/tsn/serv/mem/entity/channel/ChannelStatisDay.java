package com.tsn.serv.mem.entity.channel;

import java.util.Date;

public class ChannelStatisDay {
    private String channelRecordId;

    private String dayTime;

    private String channelCode;

    private String channelName;

    private Integer channelGuestPeopleCount;

    private Integer channelPayPeopleCount;

    private Integer channelRegPeopleCount;

    private Integer channelUrgeTaskPeopleCount;

    private Integer channelSignTaskPeopleCount;

    private Integer channelActiveGuestPeopleCount;

    private Integer channelActiveRegPeopleCount;

    private Integer channelCreditsConvertPeopleCount;

    private Integer channelOrderNum;

    private Integer channelSignTaskNum;

    private Integer channelUrgeTaskNum;

    private Integer channelCreditsConvertNum;

    private Date createTime;

    private Date updateTime;

    public String getChannelRecordId() {
        return channelRecordId;
    }

    public void setChannelRecordId(String channelRecordId) {
        this.channelRecordId = channelRecordId == null ? null : channelRecordId.trim();
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime == null ? null : dayTime.trim();
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName == null ? null : channelName.trim();
    }

    public Integer getChannelGuestPeopleCount() {
        return channelGuestPeopleCount;
    }

    public void setChannelGuestPeopleCount(Integer channelGuestPeopleCount) {
        this.channelGuestPeopleCount = channelGuestPeopleCount;
    }

    public Integer getChannelPayPeopleCount() {
        return channelPayPeopleCount;
    }

    public void setChannelPayPeopleCount(Integer channelPayPeopleCount) {
        this.channelPayPeopleCount = channelPayPeopleCount;
    }

    public Integer getChannelRegPeopleCount() {
        return channelRegPeopleCount;
    }

    public void setChannelRegPeopleCount(Integer channelRegPeopleCount) {
        this.channelRegPeopleCount = channelRegPeopleCount;
    }

    public Integer getChannelUrgeTaskPeopleCount() {
        return channelUrgeTaskPeopleCount;
    }

    public void setChannelUrgeTaskPeopleCount(Integer channelUrgeTaskPeopleCount) {
        this.channelUrgeTaskPeopleCount = channelUrgeTaskPeopleCount;
    }

    public Integer getChannelSignTaskPeopleCount() {
        return channelSignTaskPeopleCount;
    }

    public void setChannelSignTaskPeopleCount(Integer channelSignTaskPeopleCount) {
        this.channelSignTaskPeopleCount = channelSignTaskPeopleCount;
    }

    public Integer getChannelActiveGuestPeopleCount() {
        return channelActiveGuestPeopleCount;
    }

    public void setChannelActiveGuestPeopleCount(Integer channelActiveGuestPeopleCount) {
        this.channelActiveGuestPeopleCount = channelActiveGuestPeopleCount;
    }

    public Integer getChannelActiveRegPeopleCount() {
        return channelActiveRegPeopleCount;
    }

    public void setChannelActiveRegPeopleCount(Integer channelActiveRegPeopleCount) {
        this.channelActiveRegPeopleCount = channelActiveRegPeopleCount;
    }

    public Integer getChannelCreditsConvertPeopleCount() {
        return channelCreditsConvertPeopleCount;
    }

    public void setChannelCreditsConvertPeopleCount(Integer channelCreditsConvertPeopleCount) {
        this.channelCreditsConvertPeopleCount = channelCreditsConvertPeopleCount;
    }

    public Integer getChannelOrderNum() {
        return channelOrderNum;
    }

    public void setChannelOrderNum(Integer channelOrderNum) {
        this.channelOrderNum = channelOrderNum;
    }

    public Integer getChannelSignTaskNum() {
        return channelSignTaskNum;
    }

    public void setChannelSignTaskNum(Integer channelSignTaskNum) {
        this.channelSignTaskNum = channelSignTaskNum;
    }

    public Integer getChannelUrgeTaskNum() {
        return channelUrgeTaskNum;
    }

    public void setChannelUrgeTaskNum(Integer channelUrgeTaskNum) {
        this.channelUrgeTaskNum = channelUrgeTaskNum;
    }

    public Integer getChannelCreditsConvertNum() {
        return channelCreditsConvertNum;
    }

    public void setChannelCreditsConvertNum(Integer channelCreditsConvertNum) {
        this.channelCreditsConvertNum = channelCreditsConvertNum;
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