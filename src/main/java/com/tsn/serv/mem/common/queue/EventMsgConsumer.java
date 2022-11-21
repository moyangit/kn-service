package com.tsn.serv.mem.common.queue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.cache.redis.mq.RedisMqConsumer;
import com.tsn.common.core.cache.redis.mq.RedisMqConsumerListener;
import com.tsn.serv.common.cons.redis.RedisMqKey;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.mem.service.statis.StatisDataService;

@Component
public class EventMsgConsumer {
	
	private static Logger log = LoggerFactory.getLogger(EventMsgConsumer.class);
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private StatisDataService statisDataService;
	
    @PostConstruct
    public void init() {
    	
    	RedisMqConsumerListener listener = new RedisMqConsumerListener() {
			
			@Override
			public void recevice(Object msg) {
				
				log.debug("EventMsgConsumer recevice, msg = {}", msg);
				
				try {
					EventMsg channelMsg = (EventMsg) msg;
					statisDataService.collectEvent(channelMsg);
				} catch (Exception e) {
					log.error("RedisMqConsumerListener e = {}", e);
					e.printStackTrace();
				}
				
			}
		};
		
		RedisMqConsumer redisMqConsumer = new RedisMqConsumer(RedisMqKey.EVENT_QUEUE_MSG, redisHandler, listener, 60);
		redisMqConsumer.start();
    	
    }

}
