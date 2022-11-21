package com.tsn.serv.auth.entity.sys;

import java.util.Date;

public class SysPermiss {
	private Integer permissId;

	private String permissCode;

	private String permissName;

	private String reqUrl;

	private String reqMethod;

	private String moduleCode;

	private Date updateTime;

	public Integer getPermissId() {
		return permissId;
	}

	public void setPermissId(Integer permissId) {
		this.permissId = permissId;
	}

	public String getPermissCode() {
		return permissCode;
	}

	public void setPermissCode(String permissCode) {
		this.permissCode = permissCode;
	}

	public String getPermissName() {
		return permissName;
	}

	public void setPermissName(String permissName) {
		this.permissName = permissName;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(String reqMethod) {
		this.reqMethod = reqMethod;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}