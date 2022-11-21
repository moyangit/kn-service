package com.tsn.serv.mem.entity.source;

public class MemSourceInviter {
    private Integer id;

    private String sourceName;

    private String sourcePath;

    private String inviterCode;

    private String rewardType;

    private Integer rewardVal;

    private Integer num;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        this.sourcePath = sourcePath;
    }

    public String getInviterCode() {
        return inviterCode;
    }

    public void setInviterCode(String inviterCode) {
        this.inviterCode = inviterCode == null ? null : inviterCode.trim();
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType == null ? null : rewardType.trim();
    }

    public Integer getRewardVal() {
        return rewardVal;
    }

    public void setRewardVal(Integer rewardVal) {
        this.rewardVal = rewardVal;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

	@Override
	public String toString() {
		return "MemSourceInviter [id=" + id + ", sourceName=" + sourceName
				+ ", sourcePath=" + sourcePath + ", inviterCode=" + inviterCode
				+ ", rewardType=" + rewardType + ", rewardVal=" + rewardVal
				+ ", num=" + num + "]";
	}
}