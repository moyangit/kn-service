package com.tsn.serv.common.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.cache.redis.RedisService;
import com.tsn.common.core.cache.redis.mq.RedisMqProductor;
import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.common.cons.redis.RedisMqKey;

public class EventMsgProducter {
	
	private static Logger log = LoggerFactory.getLogger(EventMsgProducter.class);
	
	private static EventMsgProducter EventMsgProducter = new EventMsgProducter();
	
	private RedisMqProductor redisMqProductor;
	
	private RedisHandler redisHandler;
	
	private EventMsgProducter() {
		
		this.redisMqProductor = (RedisMqProductor) SpringContext.getBean("redisMqProductor");
		
		this.redisHandler = SpringContext.getBean(RedisService.class);
		
	}
	
	public static EventMsgProducter build() {
		return EventMsgProducter;
	}
	
	public void sendEventMsg(EventMsg event) {
		
		try {
			
			//这里product的数据比消费的速度快，用户和设备的实时数据要不停的刷新，而活跃数据按天统计即可
			//如果是活跃事件和设备事件，按天去重,30分钟去重， 避免消息队列重复数据太多
//			if (EventTypeEnum.active_user_count_msg.equals(event.getMsgTypeEnum())) {
//				String key = RedisMqKey.EVENT_QUEUE_MSG_DUP + event.getMsgTypeEnum().name() + ":" + event.getCreateTime();
//				long result = redisHandler.sAdd(key, event.getMsgId());
//				//过期半个小时
//				redisHandler.expire(key, 30 * 60);
//				if (result == 0) {
//					log.warn("active_user_count_msg is duplicate, result = {}, msg = {}", result, event);
//					return;
//				}
//			}
			
			long size = redisMqProductor.send(RedisMqKey.EVENT_QUEUE_MSG, event);
			
			log.debug("EventMsgProducter sendEventMsg send msg success, EventMsg = {}, queue size = {}", event, size);
		} catch (Exception e) {
			log.error("EventMsgProducter sendEventMsg Exception e = {}",e);
		}
	}

}
