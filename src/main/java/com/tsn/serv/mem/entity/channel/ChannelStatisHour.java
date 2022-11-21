package com.tsn.serv.mem.entity.channel;

import java.util.Date;

public class ChannelStatisHour {
    private String channelRecordId;

    private String dayTime;

    private String channelCode;

    private String channelName;
    
    private String channelCallbackUrl;
    
    private String mark;

	private Integer channelGuestPeopleCount;

    private Integer channelPayPeopleCount;
    
    private Integer channelNewPayPeopleCount;

    private Integer channelRegPeopleCount;

    private Integer channelUrgeTaskPeopleCount;

    private Integer channelSignTaskPeopleCount;

    private Integer channelActiveGuestPeopleCount;

    private Integer channelActiveRegPeopleCount;

    private Integer channelCreditsConvertPeopleCount;

    private Integer channelOrderNum;
    
    private Integer channelOrderMonthNum;
    private Integer channelOrderQuarterNum;
    private Integer channelOrderHalfyearNum;
    private Integer channelOrderYearNum;
    private Integer channelOrderForeverNum;

    private Integer channelSignTaskNum;

    private Integer channelUrgeTaskNum;

    private Integer channelCreditsConvertNum;
    
    private Integer channelPayMomeyVal;

    private Date createTime;

    private Date updateTime;
    
    private Channel channelTotalData;
    
    public String getChannelCallbackUrl() {
		return channelCallbackUrl;
	}

	public Integer getChannelNewPayPeopleCount() {
		return channelNewPayPeopleCount;
	}

	public void setChannelNewPayPeopleCount(Integer channelNewPayPeopleCount) {
		this.channelNewPayPeopleCount = channelNewPayPeopleCount;
	}

	public Integer getChannelOrderMonthNum() {
		return channelOrderMonthNum;
	}

	public void setChannelOrderMonthNum(Integer channelOrderMonthNum) {
		this.channelOrderMonthNum = channelOrderMonthNum;
	}

	public Integer getChannelOrderQuarterNum() {
		return channelOrderQuarterNum;
	}

	public void setChannelOrderQuarterNum(Integer channelOrderQuarterNum) {
		this.channelOrderQuarterNum = channelOrderQuarterNum;
	}

	public Integer getChannelOrderHalfyearNum() {
		return channelOrderHalfyearNum;
	}

	public void setChannelOrderHalfyearNum(Integer channelOrderHalfyearNum) {
		this.channelOrderHalfyearNum = channelOrderHalfyearNum;
	}

	public Integer getChannelOrderYearNum() {
		return channelOrderYearNum;
	}

	public void setChannelOrderYearNum(Integer channelOrderYearNum) {
		this.channelOrderYearNum = channelOrderYearNum;
	}

	public Integer getChannelOrderForeverNum() {
		return channelOrderForeverNum;
	}

	public void setChannelOrderForeverNum(Integer channelOrderForeverNum) {
		this.channelOrderForeverNum = channelOrderForeverNum;
	}

	public void setChannelCallbackUrl(String channelCallbackUrl) {
		this.channelCallbackUrl = channelCallbackUrl;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

    public Channel getChannelTotalData() {
		return channelTotalData;
	}

	public void setChannelTotalData(Channel channelTotalData) {
		this.channelTotalData = channelTotalData;
	}

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

	@Override
	public String toString() {
		return "ChannelStatisHour [channelRecordId=" + channelRecordId
				+ ", dayTime=" + dayTime + ", channelCode=" + channelCode
				+ ", channelName=" + channelName + ", channelGuestPeopleCount="
				+ channelGuestPeopleCount + ", channelPayPeopleCount="
				+ channelPayPeopleCount + ", channelRegPeopleCount="
				+ channelRegPeopleCount + ", channelUrgeTaskPeopleCount="
				+ channelUrgeTaskPeopleCount + ", channelSignTaskPeopleCount="
				+ channelSignTaskPeopleCount
				+ ", channelActiveGuestPeopleCount="
				+ channelActiveGuestPeopleCount
				+ ", channelActiveRegPeopleCount="
				+ channelActiveRegPeopleCount
				+ ", channelCreditsConvertPeopleCount="
				+ channelCreditsConvertPeopleCount + ", channelOrderNum="
				+ channelOrderNum + ", channelSignTaskNum="
				+ channelSignTaskNum + ", channelUrgeTaskNum="
				+ channelUrgeTaskNum + ", channelCreditsConvertNum="
				+ channelCreditsConvertNum + ", channelPayMomeyVal="
				+ channelPayMomeyVal + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", channelTotalData="
				+ channelTotalData + "]";
	}

	public Integer getChannelPayMomeyVal() {
		return channelPayMomeyVal;
	}

	public void setChannelPayMomeyVal(Integer channelPayMomeyVal) {
		this.channelPayMomeyVal = channelPayMomeyVal;
	}
}