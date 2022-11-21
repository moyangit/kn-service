package com.tsn.serv.common.cons.redis;

import com.tsn.serv.common.cons.sys.SysCons;

public class DelayRedisKey {
	//delay:msg:
	public static String DELAY_ORDER_KEY1 = SysCons.getPlatRedisSuffix() + "order:status";

}
