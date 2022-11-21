package com.tsn.serv.common.code.auth;

public class AuthCode {
	
	/**
	 * 用户token不存在
	 */
	public static final String AUTH_TOKEN_NOT_ISEXISTS_ERROR = "100990";
	
	/**
	 * 用户token验证失败
	 */
	public static final String AUTH_TOKEN_VALID_ERROR = "100991";
	
	/**
	 * 用户授权参数错误
	 */
	public static final String AUTH_PARAM_ERROR = "100997";
	
	/**
	 * 用户不存在
	 */
	public static final String AUTH_USER_NOT_ISEXISTS_ERROR = "100998";
	
	/**
	 * 用户和密码错误
	 */
	public static final String AUTH_PWD_ERROR = "100999";
	
	/**
	 * 验证码不能为空
	 */
	public static final String AUTH_USER_CODE_NOT_EMPTY_ERROR = "100994";
	
	/**
	 * 验证码验证错误
	 */
	public static final String AUTH_USER_CODE_ERROR = "100996";
	
	/**
	 * 验证码60s内重复发送错误
	 */
	public static final String AUTH_USER_CODE_SENDD_ERROR = "100995";
	
	/**
	 * 用户已存在
	 */
	public static final String AUTH_USER_EXISTS_ERROR = "100993";
	
	/**
	 * 该设备号无法授权
	 */
	public static final String AUTH_DEVICE_NOT_LOGIN_ERROR = "100900";
	
	/**
	 * 游客用户登录异常
	 */
	public static final String GUEST_LOGIN_ERROR = "900991";
	
	/**
	 * 修改密码，老密码错误
	 */
	public static final String AUTH_O_PWD_ERROR = "100899";
	
}
