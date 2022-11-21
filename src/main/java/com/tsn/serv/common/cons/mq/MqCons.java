package com.tsn.serv.common.cons.mq;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.serv.common.cons.sys.SysCons;


public class MqCons {
	
	/*public static String getQueue() {
		return Env.getVal("vpn.queue.notify.public");
	}*/
	
	public static String getFlowQueue() {
		//String val = Env.getVal("kafka.queue.flow.name");
		String val = "vpn.queue.flow.up";
		return StringUtils.isEmpty(val) ? SysCons.getPlatRedisSuffix() + "vpn.queue.flow.up" : SysCons.getPlatRedisSuffix() + val;
	}
	
	/**
	 * 短信，邮件，推送
	 */
	//public final static String QUEUE_NOTIFY_NAME = "vpn.queue.notify.public";
	
	/**
	 * 流量上报
	 */
	//public final static String QUEUE_FLOW = "vpn.queue.flow.up";
	
	/**
	 * 用于存放流量控制事件流量
	 */
	//public final static String QUEUE_USER_EVENT = "vpn.queue.user.event";
	
	/**
	 * 订单返利
	 */
	//public final static String QUEUE_OREDER_RABATE = "vpn.queue.order.rabate";
	
}
