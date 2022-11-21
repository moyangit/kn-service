package com.tsn.serv.mem.common.utils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class FlowStat {
	
	private boolean store = false;
	
	private String userId;
	
	private String host;
		
	//B 字节
	private Long readUsed;
	
	private Long writeUsed;
	
    private Long used;
    
    private long readTime;
    
    private long createTime;
    
    private AtomicLong calcNum = new AtomicLong();
    
    private long updateTime;
    
    public FlowStat (String userId, String host, Long readUsed, Long writeUsed, long readTime) {
    	this.setReadTime(readTime);
    	this.userId = userId;
    	this.readUsed = readUsed;
    	this.host = host;
    	this.writeUsed = writeUsed;
    	this.used = this.readUsed + this.writeUsed;
    	this.createTime = new Date().getTime();
    	this.updateTime = this.createTime;
    }
    
    public void addUsed(long readUsed, long writeUsed) {
    	this.readUsed = this.readUsed + readUsed;
    	this.writeUsed = this.writeUsed + writeUsed;
    	this.used = this.readUsed + this.writeUsed;
    	//更新创建时间
    	this.updateTime = new Date().getTime();
    	//this.calcNum++;
    	long num = this.calcNum.incrementAndGet();
    	if (num >= 50) {
    		this.store = true;
    	}
    }

	public Long getReadUsed() {
		return readUsed;
	}

	public void setReadUsed(Long readUsed) {
		this.readUsed = readUsed;
	}

	public Long getWriteUsed() {
		return writeUsed;
	}

	public void setWriteUsed(Long writeUsed) {
		this.writeUsed = writeUsed;
	}

	@Override
	public String toString() {
		return "FlowStat [store=" + store + ", userId=" + userId
				+ ", readUsed=" + readUsed + ", writeUsed=" + writeUsed
				+ ", used=" + used + ", createTime=" + createTime
				+ ", calcNum=" + calcNum + ", updateTime=" + updateTime + "]";
	}

	public Long getUsed() {
		return used;
	}

	public void setUsed(Long used) {
		this.used = used;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public AtomicLong getCalcNum() {
		return calcNum;
	}

	public void setCalcNum(AtomicLong calcNum) {
		this.calcNum = calcNum;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isStore() {
		return store;
	}

	public void setStore(boolean store) {
		this.store = store;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getReadTime() {
		return readTime;
	}

	public void setReadTime(long readTime) {
		this.readTime = readTime;
	}

}