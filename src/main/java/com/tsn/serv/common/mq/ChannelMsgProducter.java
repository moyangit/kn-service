package com.tsn.serv.common.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.core.cache.redis.mq.RedisMqProductor;
import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.common.cons.redis.RedisMqKey;

public class ChannelMsgProducter {
	
	private static Logger log = LoggerFactory.getLogger(ChannelMsgProducter.class);
	
	private static ChannelMsgProducter channelMsgProducter = new ChannelMsgProducter();
	
	private RedisMqProductor redisMqProductor;
	
	private ChannelMsgProducter() {
		
		this.redisMqProductor = (RedisMqProductor) SpringContext.getBean("redisMqProductor");
		
	}
	
	public static ChannelMsgProducter build() {
		return channelMsgProducter;
	}
	
	public void sendChannelMsg(ChannelMsg channelMsg) {
		
		//RedisHandler redisHandler = redisMqProductor.getRedisHander();
		
//		MsgTypeEnum msgTypeEnum =  channelMsg.getMsgTypeEnum();
//		
//		String key = RedisMqKey.CHANNEL_QUEUE_TYPE_SET + msgTypeEnum.name() + ":" + channelMsg.getDayTime();
//		long result = redisHandler.sAdd(key, channelMsg.getMsgId());
//		redisHandler.expire(key, 48 * 60 * 60);
//		
//		if (result == 0) {
//			log.warn("ChannelMsgProducter, found retry msg, msg = {}", channelMsg);
//			return;
//		}
		
		//这里以前是要判空是因为针对的是渠道，现在除了channelcode，要统计整个平台的数据
//		if (StringUtils.isEmpty(channelMsg.getChannelCode())) {
//			
//			log.error("sendChannelMsg msg channelCode is null, msg = {}", channelMsg);
//			
//			return;
//		}
		
		try {
			long size = redisMqProductor.send(RedisMqKey.CHANNEL_QUEUE_MSG, channelMsg);
			
			log.debug("ChannelMsgProducter sendChannelMsg send msg success, channelMsg = {}, queue size = {}", channelMsg, size);
		} catch (Exception e) {
			log.error("ChannelMsgProducter sendChannelMsg Exception e = {}",e);
			e.printStackTrace();
		}
	}

}
