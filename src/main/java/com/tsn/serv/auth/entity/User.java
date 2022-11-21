package com.tsn.serv.auth.entity;

import javax.validation.constraints.NotNull;


/**
 * 
 * @author work
 *
 */
public class User extends Cert{
	
	private String userId;
	
	@NotNull
	private String userPhone;
	
	private String userName;
	
	private String nickName;
	
    private String sex;

    private String userAvatar;

    private String userType;
	
	private String password;
	
	private String parenInviCode;

	private String terminalType;
	
	private String channelCode;
	
	private String deviceNo;
	
	private String ip;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getNickName() {
		return nickName;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userPhone=" + userPhone
				+ ", userName=" + userName + ", nickName=" + nickName
				+ ", sex=" + sex + ", userAvatar=" + userAvatar + ", userType="
				+ userType + ", password=" + password + ", parenInviCode="
				+ parenInviCode + ", terminalType=" + terminalType
				+ ", channelCode=" + channelCode + ", deviceNo=" + deviceNo
				+ ", smsCode=" + smsCode + "]";
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * SMS Code
	 */
	// @NotNull
    private String smsCode;


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParenInviCode() {
		return parenInviCode;
	}

	public void setParenInviCode(String parenInviCode) {
		this.parenInviCode = parenInviCode;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
}
