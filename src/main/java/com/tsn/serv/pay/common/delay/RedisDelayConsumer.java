/*package com.tsn.serv.pay.common.delay;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.tsn.common.core.cache.redis.delay.DelayMsg;
import com.tsn.common.core.cache.redis.delay.DelayRedisHandler;
import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.serv.common.cons.redis.DelayRedisKey;
import com.tsn.serv.common.enm.order.OrderStatusEum;
import com.tsn.serv.pay.service.alipay.AlipayFaceService;
import com.tsn.serv.pay.service.mem.MemChargeOrderService;

@Component
public class RedisDelayConsumer {
	
	private static Logger log = LoggerFactory.getLogger(RedisDelayConsumer.class);
	
	@Autowired
	private DelayRedisHandler delayRedisHandler;
	
	@Autowired
	private AlipayFaceService alipayFaceService;
	
	@Autowired
	private MemChargeOrderService memChargeOrderService;
	
	@PostConstruct
	public void init() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					
					try {
						log.debug("---------start scan order-----------");
						
						Set<Object> orderData = delayRedisHandler.consum(DelayRedisKey.DELAY_ORDER_KEY);
						
						if (orderData == null) {
							
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							continue;
							
						}
						
						log.debug("---------get scan order result, orderData = {}", orderData);
						
						for (Object obj : orderData) {
							
							DelayMsg delayMsg = (DelayMsg) obj;
							
							//如果超过5次 则
							if (delayMsg.getCount() >= 5) {
								log.debug("delayMsg.getCount() >= 5, count = {}", delayMsg.getCount());
								continue;
							}
							
							String orderNo = delayMsg.getBody();
							
							Map<String, Object> orderMap = memChargeOrderService.queryOrderByNo(orderNo);
							
							String orderStatus = (String) orderMap.get("orderStatus");
							
							//只处理订单状态为0 的
							if (!OrderStatusEum.pay_wait.name().equals(orderStatus)) {
								log.debug("orderNo = {}, query order info, found orderStatus is not pay_wait, status = {}", orderNo, orderStatus);
								continue;
							}
							
							
							AlipayF2FQueryResult result = alipayFaceService.queryTrade(orderNo);
							
							AlipayTradeQueryResponse queryResp = result.getResponse();
							String tradeStatus = queryResp.getTradeStatus();
							
							switch (result.getTradeStatus()) {
								
						        case SUCCESS:
						        	
						        	
						        	//交易创建，等待买家付款
						        	if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
						        		log.info("orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
						        		delayRedisHandler.rePut(delayMsg, 15);
						        		continue;
						        	}
						        	
						        	//（未付款交易超时关闭，或支付完成后全额退款）
						        	if ("TRADE_CLOSED".equals(tradeStatus)) {
						        		log.info("orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
						        		memChargeOrderService.updateChargeOrderClose(orderNo, queryResp.getTradeNo(), tradeStatus);
						        		continue;
						        	}
						        	
						        	//交易支付成功
						        	if ("TRADE_SUCCESS".equals(tradeStatus)) {
						        		log.info("orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
						        		memChargeOrderService.updateChargeOrderSuccess(orderNo, queryResp.getTradeNo(), queryResp.getTradeStatus(), queryResp.getPayCurrency());
						        		continue;
						        	}
						        	
						        	if ("TRADE_FINISHED".equals(tradeStatus)) {
						        		log.info("orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
						        		memChargeOrderService.updateChargeOrderSuccess(orderNo, queryResp.getTradeNo(), queryResp.getTradeStatus(), queryResp.getPayCurrency());
						        		continue;
						        	}
						        	
						        case FAILED:
						        	//参数无效
						        	if ("ACQ.INVALID_PARAMETER".equals(queryResp.getSubCode())) {
						        		log.error("orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
						        		continue;
						        	}
						        	
						        	log.error("FAILED orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
						        	
						        	//查询的交易不存在/系统错误
						        	if ("ACQ.SYSTEM_ERROR".equals(queryResp.getSubCode()) || "ACQ.TRADE_NOT_EXIST".equals(queryResp.getSubCode())) {
						        		
						        		int orderTimeout = Integer.parseInt(String.valueOf(orderMap.get("orderTimeout")));
						        		long orderCreateTime = Long.parseLong(String.valueOf(orderMap.get("createTime")));
						        		//如果当前时间和创建时间超过了5分钟如果交易不存在，说明用户没有支付，这时关闭订单
						        		if (new Date().getTime() - orderCreateTime > orderTimeout * 60 * 1000) {
						        			memChargeOrderService.updateChargeOrderClose(orderNo, queryResp.getTradeNo(), tradeStatus);
						        		}
						        		
						        		//delayRedisHandler.rePut(delayMsg, 15);
						        		continue;
						        	}
						        	

						        case UNKNOWN:
						            log.error("UNKNOWN orderNo = {}, result = {}, 系统异常，订单支付状态未知!!!", orderNo, JsonUtils.objectToJson(queryResp));
						            continue;

						        default:
						            log.error("default orderNo = {}, result = {}, 不支持的交易状态，交易返回异常!!!", orderNo, JsonUtils.objectToJson(queryResp));
						            continue;
							}
							
						}
					} catch (Exception e) {
						
						log.error("Exception e = {}", e);
						
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
					
				}
				
			}
		}).start();
		
	
		
	}

}
*/