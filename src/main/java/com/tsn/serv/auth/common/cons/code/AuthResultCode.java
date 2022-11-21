package com.tsn.serv.auth.common.cons.code;


public class AuthResultCode{
	
	/**
	 * Http请求失败
	 */
	public static final String REQUEST_FAILED_ERROR = "001111";
	
	/**
	 * 参数错误
	 */
	public static final String PARAM_ERROR = "001996";

	/**
	 * 数据已存在异常
	 */
	public static final String DATA_EXISTS_ERROR = "001997";
	/**
	 * 参数为NULL
	 */
	public static final String PARAMETER_NULL_ERROR = "001998";
	
	/**
	 * 参数存在
	 */
	public static final String PARAM_EXIST_ERROR = "001999";
	
	/**
	 * url不合法
	 */
	public static final String URL_ILLEGAL_ERROR = "002000";
	
	/**
	 * 未满足条件异常
	 */
	public static final String CRITERIA_UNSATISFIED_ERROR = "002003";
	
	/**
	 * 不执行
	 */
	public static final String NO_EXECUTE_ERROR = "002777";
	
	/**
	 * 未知错误
	 */
	public static final String UNKNOW_ERROR = "002888";
	
	/**
	 * 未推送
	 */
	public static final String NOT_PUSH = "002999";
	
	/**
	 * 返回数据为null异常
	 */
	public static final String DATA_EXISTS_NULL = "003000";
	
	/**
	 * 不能删除
	 */
	public static final String CAN_NOT_DELETE_ERROR = "003888";
}
