package com.tsn.serv.mem.entity.mem;

import java.util.Date;

/*
* 新老账户信息绑定
* */
public class NewUser {
	private Integer id;

	private String oldAccount;

	private String newAccount;

	private String wxCode;

	/**
	 * 审核状态 0：未审核 1：已通过
	 */
	private String status;

	private Date createDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOldAccount() {
		return oldAccount;
	}

	public void setOldAccount(String oldAccount) {
		this.oldAccount = oldAccount;
	}

	public String getNewAccount() {
		return newAccount;
	}

	public void setNewAccount(String newAccount) {
		this.newAccount = newAccount;
	}

	public String getWxCode() {
		return wxCode;
	}

	public void setWxCode(String wxCode) {
		this.wxCode = wxCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}