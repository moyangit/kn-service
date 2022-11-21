package com.tsn.serv.mem.common.queue;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.cache.redis.mq.RedisMqConsumer;
import com.tsn.common.core.cache.redis.mq.RedisMqConsumerListener;
import com.tsn.common.core.jms.kafka.KafkaGroupConsumer;
import com.tsn.common.core.jms.kafka.KafkaGroupConsumerHandler;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.serv.common.cons.mq.MqCons;
import com.tsn.serv.common.cons.redis.RedisMqKey;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.mem.service.channel.ChannelService;
import com.tsn.serv.mem.service.channel.ChannelStatisService;
import com.tsn.serv.mem.service.flow.FlowCollectService;

@Component
public class ChannelMsgConsumer {
	
	private static Logger log = LoggerFactory.getLogger(ChannelMsgConsumer.class);
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private ChannelStatisService channelStatisService;
	
	@Autowired
	private KafkaProperties kafkaProperties;
	
	@Autowired
	private FlowCollectService flowCollectService;
	
    @PostConstruct
    public void init() {
    	
    	RedisMqConsumerListener listener = new RedisMqConsumerListener() {
			
			@Override
			public void recevice(Object msg) {
				
				log.debug("ChannelMsgConsumer recevice, msg = {}", msg);
				
				try {
					ChannelMsg channelMsg = (ChannelMsg) msg;
					
					channelStatisService.setChannelTotalAndDataTypeStatic(channelMsg);
				} catch (Exception e) {
					log.error("e = {}", e);
					e.printStackTrace();
				}
				
//				MsgTypeEnum msgTypeEnum =  channelMsg.getMsgTypeEnum();
//				
//				String key = RedisMqKey.CHANNEL_QUEUE_TYPE_SET + msgTypeEnum.name() + ":" + channelMsg.getDayTime();
//				long result = redisHandler.sAdd(key, channelMsg.getMsgId());
//				//保留一天
//				redisHandler.expire(key, 1 * 24 * 60 * 60);
//				
//				if (result == 0) {
//					log.warn("ChannelMsgConsumer recevice(), found retry msg, msg = {}", channelMsg);
//					return;
//				}
//				
//				
//				try {
//					channelService.updateChannelTypeCount(channelMsg.getChannelCode(), channelMsg.getMsgTypeEnum().name(), channelMsg.getDayTime());
//				} catch (Exception e) {
//					log.error("ChannelMsgConsumer recevice error, e = {}", e);
//				}
				
			}
		};
		
		RedisMqConsumer redisMqConsumer = new RedisMqConsumer(RedisMqKey.CHANNEL_QUEUE_MSG, redisHandler, listener, 60);
		redisMqConsumer.start();
		
		
		
		
		new KafkaGroupConsumer("flow_group", kafkaProperties, new String[]{MqCons.getFlowQueue()}, new KafkaGroupConsumerHandler() {

			@Override
			public void listen(ConsumerRecord<?, ?> cr) {
				log.debug("------>> queue-receiveNotifyQueue message:  {} - {} : {}", cr.topic(), cr.key(), cr.value());
				String memId = null;
				try {
					String msg = (String) cr.value();
					@SuppressWarnings("unchecked")
					Map<String, Object> flowStat = JsonUtils.jsonToPojo(msg, Map.class);
					memId = (String) flowStat.get("userId");
					flowCollectService.collectFlow(flowStat);
					
				} catch (Exception e) {
					log.error("receiveNotifyQueue, userId={}, e = {}", memId, e);
				}
			}
			
		}).start();
    	
    }

}
