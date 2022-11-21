package com.tsn.serv.common.cons.redis;

import com.tsn.serv.common.cons.sys.SysCons;

public class RedisMqKey {
	
		/**
		 * 存放渠道队列消息
		 */
		public static String CHANNEL_QUEUE_MSG = SysCons.getPlatRedisSuffix() + "queue:msg:channel";
		
		//存放公共事件队列消息
		public static String EVENT_QUEUE_MSG = SysCons.getPlatRedisSuffix() + "queue:msg:event";
		
		//用于去重
		//public static  String EVENT_QUEUE_MSG_DUP = "queue:msg:event:dup:";
		
		public static String CHANNEL_QUEUE_TYPE_SET = SysCons.getPlatRedisSuffix() + "queue:msg:channel:set:";
		public static String CHANNEL_QUEUE_TYPE_HLL = SysCons.getPlatRedisSuffix() + "queue:msg:channel:hll:";
		public static String CHANNEL_QUEUE_TYPE_INCR = SysCons.getPlatRedisSuffix() + "queue:msg:channel:incr:";
		public static String CHANNEL_TOTAL_QUEUE_TYPE_HLL = SysCons.getPlatRedisSuffix() + "queue:msg:total:hll:";

}
