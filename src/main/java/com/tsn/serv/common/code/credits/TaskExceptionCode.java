package com.tsn.serv.common.code.credits;

/**
 * 任务异常
 * @author work
 *
 */
public class TaskExceptionCode {
	
	/**
	 * 任务不存在
	 */
	public static final String TASK_NOTHINGNESS = "100001";
	
	/**
	 * 任务进行中
	 */
	public static final String TASK_HAVE_IN_HAND = "100002";
	
	/**
	 * 领取任务超过限制
	 */
	public static final String TASK_RECEIVE_EXCEED_THE_LIMIT = "100003";
	
	/**
	 * 重复提交
	 */
	public static final String SUBMIT_AGAIN = "100004";
	
	/**
	 * 积分不足
	 */
	public static final String CREDITS_NOT_ENOUGH = "100005";
	
	/**
	 * url填写错误
	 */
	public static final String URL_FILL_ERRPR = "100006";
	
	/**
	 * 记录不存在
	 */
	public static final String RECORD_NOTHINGNESS = "100007";
	
	/**
	 * 签名验证错误
	 */
	public static final String SIGN_VALID_ERROR = "100008";
	
	/**
	 * 游客不允许做任务
	 */
	public static final String TASK_GUEST_NOT_ALLOW  = "100009";
	
	/**
	 * 上传file为空
	 */
	public static final String TASK_FILE_IS_NULL  = "100010";

}
