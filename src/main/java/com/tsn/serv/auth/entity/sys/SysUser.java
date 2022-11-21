package com.tsn.serv.auth.entity.sys;

import java.util.Date;

public class SysUser{
	
	private Integer userId;

	private String userCode;

	private String realName;
	
	private String username;
	
	private String password;

    private String sex;

    private String age;
	
	private String phone;

	private String roles;

	private String status;

	private Date createDate;

	private String createBy;

	private Date updateDate;

	private String updateBy;

	private String remark;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
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

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "SysUser{" +
				"userId='" + userId + '\'' +
				", userCode='" + userCode + '\'' +
				", username='" + username + '\'' +
				", realName='" + realName + '\'' +
				", password='" + password + '\'' +
				", sex='" + sex + '\'' +
				", age='" + age + '\'' +
				", phone='" + phone + '\'' +
				", roles=" + roles +
				", status='" + status + '\'' +
				", createDate=" + createDate +
				", createBy='" + createBy + '\'' +
				", updateDate=" + updateDate +
				", updateBy='" + updateBy + '\'' +
				", remark='" + remark + '\'' +
				'}';
	}
}
