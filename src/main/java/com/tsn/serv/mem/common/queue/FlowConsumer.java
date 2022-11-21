package com.tsn.serv.mem.common.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tsn.serv.mem.service.flow.FlowCollectService;
import com.tsn.serv.mem.service.mem.MemActiviService;
import com.tsn.serv.mem.service.order.ChargeOrderService;
import com.tsn.serv.mem.service.statis.StatisDataService;
import com.tsn.serv.mem.service.v2ray.NodeServerService;

/**
 * 消费通知记录
 * @author admin
 *
 */
@Component
public class FlowConsumer {
	
	private static Logger log = LoggerFactory.getLogger(FlowConsumer.class);
	
//	@Autowired
//	private MqHandler mqHandler;
//	
//	@Autowired
//	private FlowService flowService;
//	
//	@Autowired
//	private FlowLimitService flowLimitService;
	
	@Autowired
	private NodeServerService nodeServerService;
	
	@Autowired
	private ChargeOrderService chargeOrderService;
	
	@Autowired
	private MemActiviService memActiviService;
	
	@Autowired
	private StatisDataService statisDataService;
	
	@Autowired
	private FlowCollectService flowCollectService;
	//plat.redis.suffix
	/*@KafkaListener(topics = "${plat.redis.suffix}" + ":" + "${kafka.queue.flow.name}")
	public void receiveNotifyQueue(ConsumerRecord<?, ?> cr){
		log.debug("------>> queue-receiveNotifyQueue message:  {} - {} : {}", cr.topic(), cr.key(), cr.value());
		String memId = null;
		try {
			String msg = (String) cr.value();
			@SuppressWarnings("unchecked")
			Map<String, Object> flowStat = JsonUtils.jsonToPojo(msg, Map.class);
			memId = (String) flowStat.get("userId");
			flowCollectService.collectFlow(flowStat);
			
            //String memId = (String) flowStat.get("userId");
//			String host = (String) flowStat.get("host");
//			long readUsed = Long.parseLong(String.valueOf(flowStat.get("readUsed")));
//			long writeUsed = Long.parseLong(String.valueOf(flowStat.get("writeUsed")));
//			long createDate = Long.parseLong(String.valueOf(flowStat.get("createTime")));
			
			//触发实时活跃online用户
			//EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(memId));
			//这里不往队列插了，直接写redis,防止队列拥挤
			
//			FlowDay fd = new FlowDay();
//			fd.setDay(DateUtils.getCurrYMD("yyyyMMdd"));
//			fd.setMemId(memId);
//			fd.setHost(host);
//			//服务器的上行流量对于用户而言是下载/下行流量，反之
//			fd.setUpFlow(readUsed);
//			fd.setDownFlow(writeUsed);
//			fd.setCreateDate(new Date());
//			fd.setUpdateDate(new Date());
//			
//			//更新自然日
//			flowService.updateFlowDay(fd);
//			
//			//根据用户的购买周期进行更新使用总流量，如果不在使用周期范围内，会重新重置流量，同时更新数据记录
//			FlowLimit flowLimit = new FlowLimit();
//			flowLimit.setHost(host);
//			flowLimit.setMemId(memId);
//			flowLimit.setUpdateTime(new Date());
//			flowLimit.setCurrUseUpFlow(readUsed);
//			flowLimit.setCurrUseDownFlow(writeUsed);
//			flowLimit.setFlowLimitType(FlowLimitEnum.month.name());
//			flowLimit.setLimitUploadTime(new Date(createDate));
//			flowLimitService.updateAndAddFlowLimitForMem(flowLimit);
			
			//statisDataService.doOnlineAndAtivceUserCount(EventMsg.createOnlineUserRegMsg(memId));
		} catch (Exception e) {
			log.error("receiveNotifyQueue, userId={}, e = {}", memId, e);
		}
	}*/
	
	/*@KafkaListener(topics = MqCons.QUEUE_USER_EVENT)
	public void flowControl(ConsumerRecord<?, ?> cr){
		log.debug("------>> queue-flowControl message:  {} - {} : {}", cr.topic(), cr.key(), cr.value());
		String msg = (String) cr.value();
		Event event = JsonUtils.jsonToPojo(msg, Event.class);
		String content = event.getContent();
		
		EventEum eventEnum = event.getEventEum();
		
		switch (eventEnum) {
		case flow_control:
			
			@SuppressWarnings("unchecked")
			Map<String, String> contentMap = JsonUtils.jsonToPojo(content, Map.class);
			
			String userId = contentMap.get("userId");
			
			List<NodeServer> proxyServerList = nodeServerService.selectUseServer("proxy");
			
			if (proxyServerList == null || proxyServerList.isEmpty()) {
				log.error("query proxy is null , can not send flow control to proxy");
				return;
			}
			
			for (NodeServer nodeService : proxyServerList) {
				
				String portArr = nodeService.getPortArr();
				
				@SuppressWarnings("unchecked")
				Map<String, Integer> portMap = JsonUtils.jsonToPojo(portArr, Map.class);
				
				try {
					HttpRes res = new HttpPostReq("http://" + nodeService.getServerIp() + ":" + portMap.get("httpPort") + "/proxy/flow/" + userId).excuteReturnObj();
					
					if (ResultCode.OPERATION_IS_SUCCESS.equals(res.getCode())) {
						log.debug("userId is flowing control and Send proxy success, userId = {}", userId);
					}
					
				} catch (Exception e) {
					log.error("Exception , e = {}", e.toString());
				}
				
			}
			
			break;

		default:
			break;
		}
		
	}*/
	
/*	@KafkaListener(topics = MqCons.QUEUE_OREDER_RABATE)
	public void orderRebate(ConsumerRecord<?, ?> cr){
		log.debug("------>> queue-orderRebate message:  {} - {} : {}", cr.topic(), cr.key(), cr.value());
		String msg = (String) cr.value();
		RebateInfo event = JsonUtils.jsonToPojo(msg, RebateInfo.class);
		String orderNo = event.getOrderNo();
		
		try {
			memActiviService.operaOrderRabate(orderNo);
		} catch (Exception e) {
			log.error("orderRebate method operaOrderRabate is exception, e = {}", e);
		}
		
	}*/
	
}
