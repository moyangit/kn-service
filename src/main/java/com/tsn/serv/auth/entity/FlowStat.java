/*package com.tsn.serv.auth.entity;

import com.tsn.common.utils.utils.CommUtils;

public class FlowStat {

	private String userId;

	private String host;

	// B 字节
	private Long readUsed;

	private Long writeUsed;

	private Long used;

	private String uuid;

	public FlowStat(String userId, String host, Long readUsed, Long writeUsed) {
		this.userId = userId;
		this.host = host;
		this.readUsed = readUsed;
		this.writeUsed = writeUsed;
		this.used = this.readUsed + this.writeUsed;
		this.uuid = CommUtils.getUuid();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getUsed() {
		return used;
	}

	public void setUsed(Long used) {
		this.used = used;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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
}*/