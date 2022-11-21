package com.tsn.serv.common.code.order;

public class OrderCode {
	
	/**
	 * 订单号不存在
	 */
	public static final String ORDER_NO_NOT_EXISTS = "300990";
	
	/**
	 * 下订单与用户身份不符合，请求会盗取或者系统设置异常
	 */
	public static final String ORDER_ADD_NOMATCH_MEM = "300991";

	/**
	 * 提现金额大于了账户剩余金额
	 */
	public static final String ORDER_MONEY_NOMATCH_ACC = "300992";
	
	/**
	 * 提现金额不能小于某个数
	 */
	public static final String ORDER_MONEY_DAYU_0 = "300993";
	
	/**
	 * 提现金额不能小于某个数
	 */
	public static final String ORDER_MONEY_XIAOYU_0 = "300994";

	/**
	 * 提现金额必须是整数
	 */
	public static final String CASHOUT_MONEY_MUST_BE_NUMBER = "300995";

	/**
	 * 当前时间小于允许提现时间，不允许提现
	 */
	public static final String CASHOUT_DATE_ERREY = "300997";
}
