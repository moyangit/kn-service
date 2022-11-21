package com.tsn.serv.mem.entity.source;

import java.util.Date;

public class SourceInvitStatisHour {
	
    private String inviterHourId;

    private String dayTime;

    private String hourTime;

    private String sourceInvitId;

    private String sourceInvitCode;

    private String sourceName;

    private String sourcePath;

    private Date createTime;

    private Date updateTime;
    
    private long num;

    public String getInviterHourId() {
        return inviterHourId;
    }

    public void setInviterHourId(String inviterHourId) {
        this.inviterHourId = inviterHourId;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime == null ? null : dayTime.trim();
    }

    public String getHourTime() {
        return hourTime;
    }

    public void setHourTime(String hourTime) {
        this.hourTime = hourTime == null ? null : hourTime.trim();
    }

    public String getSourceInvitId() {
        return sourceInvitId;
    }

    public void setSourceInvitId(String sourceInvitId) {
        this.sourceInvitId = sourceInvitId == null ? null : sourceInvitId.trim();
    }

    public String getSourceInvitCode() {
        return sourceInvitCode;
    }

    public void setSourceInvitCode(String sourceInvitCode) {
        this.sourceInvitCode = sourceInvitCode == null ? null : sourceInvitCode.trim();
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName == null ? null : sourceName.trim();
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath == null ? null : sourcePath.trim();
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

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}
}