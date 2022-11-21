package com.tsn.serv.mem.entity.credits;

public class CreditsRecordStatistics {
    
	private String createTime;
	
	private int obtainCredits;
	
	private int useCredits;

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getObtainCredits() {
		return obtainCredits;
	}

	public void setObtainCredits(int obtainCredits) {
		this.obtainCredits = obtainCredits;
	}

	public int getUseCredits() {
		return useCredits;
	}

	public void setUseCredits(int useCredits) {
		this.useCredits = useCredits;
	}
}