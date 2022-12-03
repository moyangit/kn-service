package com.tsn.serv.mem.service.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.cache.redis.delay.DelayRedisHandler;
import com.tsn.common.core.jms.MqHandler;
import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.sign.SignatureUtil;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.code.order.OrderCode;
import com.tsn.serv.common.cons.key.KeyCons;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.charge.ChargeTypeEum;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.comm.FlowStatusEum;
import com.tsn.serv.common.enm.credits.convert.ConvertDurationEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.MemInvitorTypeEum;
import com.tsn.serv.common.enm.mem.MemProxyLvlEum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.enm.order.OrderStatusEum;
import com.tsn.serv.common.enm.order.PayTypeEum;
import com.tsn.serv.common.entity.pay.AliFacePay;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsgProducter;
import com.tsn.serv.common.pay.PaySel;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.charge.AliSubject;
import com.tsn.serv.mem.entity.charge.MemCharge;
import com.tsn.serv.mem.entity.mem.DurationRecord;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.entity.order.RebateWaitOrder;
import com.tsn.serv.mem.entity.proxy.ProxyInfo;
import com.tsn.serv.mem.mapper.account.MemAccountMapper;
import com.tsn.serv.mem.mapper.account.MemAccountRecordMapper;
import com.tsn.serv.mem.mapper.charge.AliSubjectMapper;
import com.tsn.serv.mem.mapper.charge.MemChargeMapper;
import com.tsn.serv.mem.mapper.flow.FlowLimitMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.order.ChargeOrderMapper;
import com.tsn.serv.mem.mapper.proxy.ProxyInfoMapper;
import com.tsn.serv.mem.service.flow.FlowLimitService;
import com.tsn.serv.mem.service.mem.DurationRecordService;
import com.tsn.serv.mem.service.mem.MemActiviService;
import com.tsn.serv.mem.service.mem.MemExtInfoService;
import com.tsn.serv.mem.service.mem.MemGuestInfoService;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.pay.PayService;
import com.tsn.serv.pay.service.yzf.YzfService;

@Service
public class ChargeOrderService {
	
	private static Logger logger = LoggerFactory.getLogger(ChargeOrderService.class);
	
	@Autowired
	private ChargeOrderMapper chargeOrderMapper;
	
	@Autowired
	private ChargeOrderService chargeOrderService;
	
	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private MemGuestInfoService memGuestInfoService;
	
	@Autowired
	private FlowLimitMapper flowLimitMapper;
	
	@Autowired
	private FlowLimitService flowLimitService;
	
	@Autowired
	private MemInfoMapper memInfoMapper;
	
	@Autowired
	private MqHandler mqHandler;
	
	@Autowired
	private MemAccountMapper memAccountMapper;
	
	@Autowired
	private MemAccountRecordMapper memAccountRecordMapper;
	
	@Autowired
	private MemChargeMapper memChargeMapper;
	
	@Autowired
	private PayService payService;
	
	@Autowired
	private ProxyInfoMapper proxyInfoMapper;
	
	@Autowired
	private DelayRedisHandler delayRedisHandler;

	@Autowired
	private AliSubjectMapper aliSubjectMapper;
	
	@Autowired
	private DurationRecordService durationRecordService;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private MemActiviService memActiviService;
	
	@Autowired
	private MemExtInfoService memExtService;
	
	@Autowired
	private RebateWaitOrderService rebateWaitOrderService;
	
	@Autowired
	private YzfService yzfService;
	
	private static Logger log = LoggerFactory.getLogger(ChargeOrderService.class);
	
	public String toSign(String mId, String token) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		String addr = Env.getVal("serv.pay.addr");
		
		String payUrl = StringUtils.isEmpty(addr) ? "https://user.kuainiaojsq.xyz/page/paycenter.html" : addr;
		
		if ("dev".equals(Env.getVal("spring.profiles.active"))){
			payUrl = "http://localhost:9077/page/paycenter.html";
		}
		
		if (StringUtils.isEmpty(mId) || StringUtils.isEmpty(token)) {
			return payUrl;
		}
		
		//缓存6个小时
		redisHandler.set(RedisKey.USER_TOKEN + mId, token, 1 * 60 * 60);
		
		map.put("payUrl", payUrl);
		map.put("mId", mId);
		map.put("code", CommUtils.getStringRandom(4));
		map.put("time", String.valueOf(new Date().getTime()));
		
		String result = SignatureUtil.generateSign(map, KeyCons.PAY_SING_KEY);
		
		StringBuffer sb = new StringBuffer(payUrl);
		sb.append("?").append("mId=").append(mId).append("&time=").append(map.get("time")).append("&code=").append(map.get("code")).append("&sign=").append(result);
		
		return sb.toString();
	}
	
	public void queryMyOrderByPage(Page page) {
		List<ChargeOrder> chargeOrder = chargeOrderMapper.queryMyOrderByPage(page);
		page.setData(chargeOrder);
	}
	
	@Transactional
	public ChargeOrder queryOrderAndUpByCache(String memId) {
		
		String orderNo = (String) redisHandler.get(RedisKey.USER_LAST_ORDERs + memId);
		
		ChargeOrder chargeOrder = queryOrderAndUpdateByNo(memId, orderNo);
		
		return chargeOrder;
	}
	
	@Transactional
	public boolean queryOrderAndUpdateCallback(Map<String, String> callbackParams) {
		
		log.info("queryOrderAndUpdateCallback result = {}", JsonUtils.objectToJson(callbackParams));
		
		if (callbackParams == null || callbackParams.isEmpty()) {
			return false;
		}
		
		boolean validResult = yzfService.validCallBack(callbackParams);
		
		if (!validResult) {
			log.info("queryOrderAndUpdateCallback validcallback fail.......");
			return false;
		}
		
		log.info("queryOrderAndUpdateCallback validcallback success..........");
		
		String orderNo = callbackParams.get("out_trade_no");
		String tradeStatus = callbackParams.get("trade_status");
		String tradeNo = callbackParams.get("trade_no");
		
		ChargeOrder order = chargeOrderMapper.queryOrderByOrderNo(orderNo);
		
		if (OrderStatusEum.pay_success.getStatus().equals(order.getOrderStatus())) {
			// 计算返利
			memActiviService.rebateMoney(order);
			return true;
		}
		
		if ("TRADE_SUCCESS".equals(tradeStatus)) {
			ChargeOrder chargeOrderSuccess = new ChargeOrder();
			chargeOrderSuccess.setOrderNo(orderNo);
			chargeOrderSuccess.setTradeNo(tradeNo);
			chargeOrderSuccess.setTradeStatus(tradeStatus);
			updateChargeOrderSuccess(chargeOrderSuccess);
			return true;
		}
		
		return false;
		
		
		
		
	}
	
	@Transactional
	public ChargeOrder queryOrderAndUpdateByNo(String memId, String orderNo) {
		ChargeOrder order = chargeOrderMapper.queryOrderByOrderNo(orderNo);

		if (!memId.equals(order.getMemId())) {
			throw new AuthException(ResultCode.UNKNOW_ERROR, "User does not have permission");
		}
		
		if (OrderStatusEum.pay_success.getStatus().equals(order.getOrderStatus())) {
			// 计算返利
			memActiviService.rebateMoney(order);
			return order;
		}
		
		
		Set<String> prodCodeSet = PaySel.getPayWeightMap().keySet();
		
		for (String prodCode : prodCodeSet) {
			AlipayF2FQueryResult result = payService.queryAliOrderByNo(prodCode, orderNo);
			AlipayTradeQueryResponse queryResp = result.getResponse();
			
			switch (result.getTradeStatus()) {
		        case SUCCESS:
		        	
		        	String tradeStatus = queryResp.getTradeStatus();
		        	
		        	//交易支付成功
		        	if ("TRADE_SUCCESS".equals(tradeStatus)) {
		        		log.info("orderNo = {} queryTrade result = {}", orderNo, JsonUtils.objectToJson(queryResp));
		        		
		        		ChargeOrder chargeOrderSuccess = new ChargeOrder();
		        		chargeOrderSuccess.setOrderNo(orderNo);
		        		chargeOrderSuccess.setTradeNo(queryResp.getTradeNo());
		        		chargeOrderSuccess.setTradeStatus(queryResp.getTradeStatus());
		        		updateChargeOrderSuccess(chargeOrderSuccess);
		        		order.setOrderStatus(OrderStatusEum.pay_success.getStatus());
		        	}
		        	
		        	break;
		
		        default:
		            log.error("orderNo = {}, result = {}, 不支持的交易状态，交易返回异常!!!", orderNo, JsonUtils.objectToJson(queryResp));
			}
		}
		

		return order;
	}
	
	public ChargeOrder queryOrderByNo(String orderNo) {
		ChargeOrder order = chargeOrderMapper.queryOrderByOrderNo(orderNo);
		return order;
	}
	
	/**
	 * 
	 * @param chargeOrder
	 * @return
	 */
	@Transactional
	public Map<String, Object> addChargeOrderFormSubmit(String memId, String chargeId, String payType) {
		
		log.debug("enter method addChargeOrderFormSubmit , chargeId = {}", chargeId);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (StringUtils.isEmpty(chargeId)) {
			throw new RequestParamValidException("chargeId is not null");
		}
		
		String orderNo = snowFlakeManager.create(GenIDEnum.RECH_ORDER_NO.name()).getIdByPrefix(GenIDEnum.RECH_ORDER_NO.getPreFix());
		
		ChargeOrder chargeOrder = new ChargeOrder();
		chargeOrder.setChargeId(chargeId);
		chargeOrder.setMemId(memId);
		chargeOrder.setPayType(payType);
		if (StringUtils.isEmpty(payType)) {
			chargeOrder.setPayType(PayTypeEum.alipay.name());
		} else {
			
			try {
				PayTypeEum payTypeEnum = PayTypeEum.valueOf(chargeOrder.getPayType());
				chargeOrder.setPayType(payTypeEnum.name());
			} catch (Exception e) {
				log.info("user pay type is not supported, type = {}", chargeOrder.getPayType());
				throw new BusinessException(ResultCode.UNKNOW_ERROR, "ser pay type is not supported, type" + chargeOrder.getPayType());
			}
			
			
		}
		
		
		chargeOrder.setOrderNo(orderNo);
		chargeOrder.setCreateTime(new Date());
		chargeOrder.setUpdateTime(new Date());
		chargeOrder.setOrderStatus(OrderStatusEum.pay_wait.getStatus());
		
		MemInfo memInfo = memService.queryMemById(chargeOrder.getMemId());
		
		MemCharge memCharge = memChargeMapper.selectByPrimaryKey(chargeOrder.getChargeId());
		
		if (memCharge == null) {/*!memInfo.getMemType().equals(memCharge.getMemType())*/
			
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "memCharge info is empty , query by chargeId");
			
		}
		
		chargeOrder.setMemBeforeSuspenDate(memInfo.getSuspenDate());
		chargeOrder.setChargeId(chargeOrder.getChargeId());
		chargeOrder.setChargePrice(memCharge.getChargeMoney());
		
		ChargeTypeEum chargeTypeEum = ChargeTypeEum.getChargeTypeEnum(memCharge.getChargeType());
		chargeOrder.setChargeType(memCharge.getChargeType());
		chargeOrder.setSubject(chargeTypeEum.getDetail());
		chargeOrder.setMemId(chargeOrder.getMemId());
		chargeOrder.setMemNo(memInfo.getMemNo());
		chargeOrder.setMemName(memInfo.getMemName());
		chargeOrder.setMemPhone(memInfo.getMemPhone());
		chargeOrder.setMemEmail(memInfo.getMemEmail());
		chargeOrder.setMemRealName(memInfo.getMemReallyName());
		chargeOrder.setMemType(memInfo.getMemType());
		
		int orderTimeout = StringUtils.isEmpty(Env.getVal("order.alipay.timeout")) ? 5 : Integer.parseInt(Env.getVal("order.alipay.timeout"));
		
		chargeOrder.setOrderTimeout(orderTimeout);
		
		//原价
		chargeOrder.setCostPrice(memCharge.getChargeMoney());
		//折扣
		chargeOrder.setDiscount(memCharge.getDiscount());
		//最终价
		chargeOrder.setFinalPrice(memCharge.getChargeMoney().multiply(BigDecimal.valueOf(memCharge.getDiscount() * 0.01)).setScale(2,BigDecimal.ROUND_FLOOR));
		
		//这个地方要根据用户类型进行配置，如果改会员有上级代理信息，获取给上级代理多少比例分成信息  && MemInvitorTypeEum.mem.name().equals(memInfo.getInviterUserType())
		if (!StringUtils.isEmpty(memInfo.getInviterUserId())) {
			
			MemInfo inviMemInfo = memInfoMapper.selectByPrimaryKey(memInfo.getInviterUserId());
			
			if (inviMemInfo != null) {
				chargeOrder.setInvitUserId(inviMemInfo.getMemId());
				chargeOrder.setInvitUserType(inviMemInfo.getMemType());
				chargeOrder.setInvitUserName(StringUtils.isEmpty(inviMemInfo.getMemPhone()) ? inviMemInfo.getInviterCode() : inviMemInfo.getMemPhone());
				
				chargeOrder.setRebateUserId(memInfo.getInviterUserId());
				chargeOrder.setRebateUserType(MemInvitorTypeEum.mem.name());
				chargeOrder.setRebateUserName(inviMemInfo.getMemNickName());
				chargeOrder.setRebateUserPhone(inviMemInfo.getMemPhone());
				// 默认返利等级两级,这里之前是2
				chargeOrder.setRebateLvl(1);
				chargeOrder.setRebateStatus(FlowStatusEum.create.getStatus());
				
				//如果是父级用户是代理
				//ProxyInfo proxyInfo = proxyInfoMapper.selectProxyAndGroupByProxyId(memInfo.getInviterUserId());
				ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(memInfo.getInviterUserId());
				log.debug("addChargeOrderForQcode , selectProxyAndGroupByProxyId(memId), memId = {} ,  proxyInfo  = {}", memInfo.getMemId(), proxyInfo == null ? "" : proxyInfo.toString());
				if (proxyInfo != null && "1".equals(inviMemInfo.getIsProxy())) {
					int rebateValue = Integer.parseInt(proxyInfo.getProxyLvl());
					chargeOrder.setRebate(rebateValue);
				}else {
					//这个地方10先写死吧！
					chargeOrder.setRebate(10);
					
				}
			}
			
		}
		
		chargeOrderMapper.insert(chargeOrder);
		
		//添加渠道订单金额,用来测试
		//ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelOrderMoneyValue(memInfo.getChannelCode(), chargeOrder.getOrderNo(), chargeOrder.getFinalPrice() == null ? 0 : chargeOrder.getFinalPrice().intValue()));
		
		//如果是ios苹果内购支付，直接返回订单号，这时先不做验证
		if (PayTypeEum.apple.name().equals(chargeOrder.getPayType())) {
			log.info("user paying by apple pay, order no = {}", orderNo);
			result.put("orderNo", orderNo);
			result.put("productId", chargeOrder.getChargeId());
			return result;
		}
		
		String productName = getSubjectName();
		
		result = yzfService.param2sign(chargeOrder.getOrderNo(), payType, productName, chargeOrder.getFinalPrice().toString(), null);
		
		redisHandler.set(RedisKey.USER_LAST_ORDERs + chargeOrder.getMemId(), orderNo, 6 * 60 * 60); 
		
		return result;
	}
	
	@Transactional
	public Map<String, Object> addChargeOrderForQcode(ChargeOrder chargeOrder) {
		
		log.debug("enter method addChargeOrderForQcode , chargeOrder = {}", chargeOrder.toString());
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (chargeOrder.getChargeType() == null || chargeOrder.getChargeId() == null) {
			throw new RequestParamValidException("costprice is not null");
		}
		
		String orderNo = snowFlakeManager.create(GenIDEnum.RECH_ORDER_NO.name()).getIdByPrefix(GenIDEnum.RECH_ORDER_NO.getPreFix());
		
		if (StringUtils.isEmpty(chargeOrder.getPayType())) {
			
			chargeOrder.setPayType(PayTypeEum.alipay.name());
			
		} else {
			
			try {
				PayTypeEum payTypeEnum = PayTypeEum.valueOf(chargeOrder.getPayType());
				chargeOrder.setPayType(payTypeEnum.name());
			} catch (Exception e) {
				log.info("user pay type is not supported, type = {}", chargeOrder.getPayType());
				throw new BusinessException(ResultCode.UNKNOW_ERROR, "ser pay type is not supported, type" + chargeOrder.getPayType());
			}
			
			
		}
		
		
		chargeOrder.setOrderNo(orderNo);
		chargeOrder.setCreateTime(new Date());
		chargeOrder.setUpdateTime(new Date());
		chargeOrder.setOrderStatus(OrderStatusEum.pay_wait.getStatus());
		
		MemInfo memInfo = memService.queryMemById(chargeOrder.getMemId());
		
		MemCharge memCharge = memChargeMapper.selectByPrimaryKey(chargeOrder.getChargeId());
		
		if (memCharge == null) {/*!memInfo.getMemType().equals(memCharge.getMemType())*/
			
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "memCharge info is empty , query by chargeId");
			
		}
		
		chargeOrder.setMemBeforeSuspenDate(memInfo.getSuspenDate());
		chargeOrder.setChargeId(chargeOrder.getChargeId());
		chargeOrder.setChargePrice(memCharge.getChargeMoney());
		chargeOrder.setChargeType(chargeOrder.getChargeType());
		
		ChargeTypeEum chargeTypeEum = ChargeTypeEum.getChargeTypeEnum(chargeOrder.getChargeType());
		
		chargeOrder.setSubject(chargeTypeEum.getDetail());
		chargeOrder.setMemId(chargeOrder.getMemId());
		chargeOrder.setMemNo(memInfo.getMemNo());
		chargeOrder.setMemName(memInfo.getMemNickName());
		chargeOrder.setMemPhone(memInfo.getMemPhone());
		chargeOrder.setMemEmail(memInfo.getMemEmail());
		chargeOrder.setMemRealName(memInfo.getMemReallyName());
		chargeOrder.setMemType(memInfo.getMemType());
		
		int orderTimeout = StringUtils.isEmpty(Env.getVal("order.alipay.timeout")) ? 5 : Integer.parseInt(Env.getVal("order.alipay.timeout"));
		
		chargeOrder.setOrderTimeout(orderTimeout);
		
		//原价
		chargeOrder.setCostPrice(memCharge.getChargeMoney());
		//折扣
		chargeOrder.setDiscount(memCharge.getDiscount());
		//最终价
		chargeOrder.setFinalPrice(memCharge.getChargeMoney().multiply(BigDecimal.valueOf(memCharge.getDiscount() * 0.01)).setScale(2,BigDecimal.ROUND_FLOOR));
		
		//这个地方要根据用户类型进行配置，如果改会员有上级代理信息，获取给上级代理多少比例分成信息  && MemInvitorTypeEum.mem.name().equals(memInfo.getInviterUserType())
		if (!StringUtils.isEmpty(memInfo.getInviterUserId())) {
			
			MemInfo inviMemInfo = memInfoMapper.selectByPrimaryKey(memInfo.getInviterUserId());
			
			if (inviMemInfo != null) {
				chargeOrder.setInvitUserId(inviMemInfo.getMemId());
				chargeOrder.setInvitUserType(inviMemInfo.getMemType());
				chargeOrder.setInvitUserName(StringUtils.isEmpty(inviMemInfo.getMemPhone()) ? inviMemInfo.getInviterCode() : inviMemInfo.getMemPhone());
				
				chargeOrder.setRebateUserId(memInfo.getInviterUserId());
				chargeOrder.setRebateUserType(MemInvitorTypeEum.mem.name());
				chargeOrder.setRebateUserName(inviMemInfo.getMemNickName());
				chargeOrder.setRebateUserPhone(inviMemInfo.getMemPhone());
				// 默认返利等级两级,这里之前是2
				chargeOrder.setRebateLvl(1);
				chargeOrder.setRebateStatus(FlowStatusEum.create.getStatus());
				
				//如果是父级用户是代理
				//ProxyInfo proxyInfo = proxyInfoMapper.selectProxyAndGroupByProxyId(memInfo.getInviterUserId());
				ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(memInfo.getInviterUserId());
				log.debug("addChargeOrderForQcode , selectProxyAndGroupByProxyId(memId), memId = {} ,  proxyInfo  = {}", memInfo.getMemId(), proxyInfo == null ? "" : proxyInfo.toString());
				if (proxyInfo != null && "1".equals(inviMemInfo.getIsProxy())) {
					int rebateValue = Integer.parseInt(proxyInfo.getProxyLvl());
					chargeOrder.setRebate(rebateValue);
				}else {
					//这个地方10先写死吧！
					chargeOrder.setRebate(10);
					
				}
			}
			
		}
		
		log.debug("insert order info , insert = {}", chargeOrder.toString());
		chargeOrderMapper.insert(chargeOrder);
		
		//添加渠道订单金额,用来测试
		//ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelOrderMoneyValue(memInfo.getChannelCode(), chargeOrder.getOrderNo(), chargeOrder.getFinalPrice() == null ? 0 : chargeOrder.getFinalPrice().intValue()));
		
		//如果是ios苹果内购支付，直接返回订单号，这时先不做验证
		if (PayTypeEum.apple.name().equals(chargeOrder.getPayType())) {
			log.info("user paying by apple pay, order no = {}", orderNo);
			result.put("orderNo", orderNo);
			result.put("productId", chargeOrder.getChargeId());
			return result;
		}
		
		//调用支付接口
		AliFacePay aliFacePay = new AliFacePay();
		aliFacePay.setOutTradeNo(orderNo);

		// 随机获取商品名称
		String subjectName = getSubjectName();
		if (!StringUtils.isEmpty(subjectName)){
			aliFacePay.setSubject(subjectName);
		} else {
			aliFacePay.setSubject(chargeOrder.getSubject());
		}

		aliFacePay.setTotalAmount(chargeOrder.getFinalPrice().toString());
		aliFacePay.setTimeout(orderTimeout + "m");
		
		
		String prodCode = PaySel.getPayProd();
		String qrCode = null;
		try {
			qrCode = payService.sendQcodeTrade(prodCode, aliFacePay);
			PaySel.resetProFail(prodCode);
		} catch (Exception e) {
			
			logger.error("pay error , e = {}", e);
			
			if (e instanceof BusinessException) {
				//出现问题就添加次数
				PaySel.recordProFail(prodCode);
			}
			
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "sendQcodeTrade error");
		}
		
		//String qrCode = payService.sendQcodeTrade(aliFacePay);
		log.debug("addChargeOrderForQcode qrcode  = {}", qrCode);
		
		result.put("qrcode", qrCode);
		result.put("orderNo", orderNo);
		
/*		try {
			delayRedisHandler.put(new DelayMsg(DelayRedisKey.DELAY_ORDER_KEY, orderNo, chargeOrder.getOrderTimeout() * 60 * 1000 + 2 * 60 * 1000));
		} catch (Exception e) {
			log.debug("delayRedisHandler, e = {}", e);
		}*/
		
		redisHandler.set(RedisKey.USER_LAST_ORDERs + chargeOrder.getMemId(), orderNo, 6 * 60 * 60); 
		
		return result;
	}
	
	public void updateChargeOrderForClose(ChargeOrder chargeOrder) {
		chargeOrderMapper.updateChargeOrderForClose(chargeOrder);
	}
	
	
	@Transactional
	public void updateChargeOrderSuccess(ChargeOrder chargeOrder) {
		
		//更新订单号状态，时间，交易信息
		int res = chargeOrderMapper.updateOrderByOrderNoSuccess(chargeOrder);
		
		if (res == 0) {
			throw new BusinessException(OrderCode.ORDER_NO_NOT_EXISTS, "order_no is not exits, query is empty111");
		}

		ChargeOrder order = chargeOrderMapper.queryOrderByOrderNo(chargeOrder.getOrderNo());
		String memId = order.getMemId();
		
		//计算到期时间，并打算更新
		int monthNum = getMonthNum(order.getChargeType());
		
		//boolean guest = memService.isGuest(memId);
		boolean guest = memService.isGuestById(memId);
		
		String channelCode = null;
		
		//如果是非游客,充值成功，判断是否有父级节点，如果有父级，这时 + 1
		if (!guest) {
			//更新用户会员到期时间,是否代理，最后一次充值时间（不包括续费）
			MemInfo memInfo = memService.queryMemById(memId);
			
			channelCode = memInfo.getChannelCode();
			
			//如果是第一次
			if (memInfo.getFirstRechargeDate() == null) {
				memInfo.setFirstRechargeDate(new Date());
				memInfo.setLastRechargeDate(memInfo.getFirstRechargeDate());
				//不管还有没有剩余时间，从充值时间开始算起
				//memInfo.setSuspenDate(DateUtils.getCurrDateAddMonth(new Date(), monthNum));
				//Date date = addDate(new Date(), monthNum * 30);
				memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), monthNum * 30 * 24 * 60));
				// 记录用户充值到期日期，用作判断用户使用线路的级别
				memInfo.setChangeSuspenDate(memInfo.getSuspenDate());

			}else {//如果不是第一次，判断是续费还是充值（中间有断层）
				
				//如果过期，更新lastRechargteDate，
				if (memInfo.isExpire()) {
					Date currDate = new Date();
					memInfo.setLastRechargeDate(currDate);
					memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(currDate, monthNum * 30 * 24 * 60));
				}else {
					Date date = DateUtils.getCurrDateAddMinTime(memInfo.getSuspenDate(), monthNum * 30 * 24 * 60);
					memInfo.setSuspenDate(date);
				}
				
				memInfo.setChangeSuspenDate(memInfo.getSuspenDate());
				
			}
			
			//通过类型判断如果是游客,不改会员类型， 充值后还是游客 但是可以充值，如果是注册用户改变会员类型
			if (!memService.isGuestByType(memId)) {
				memInfo.setMemType(MemTypeEum.general_mem.getCode());
			}
			
			//设置标识已付款
			memInfo.setIsPay("1");
			
			//现在充值不改变类型
			/*//会员类型
			memInfo.setMemType(MemTypeEum.general_mem.getCode());*/
			
			memService.updateMemInfo(memInfo);
			
			//保存待返利订单队列
			//如果返利为10，可能邀请人不是代理，这里要判断下邀请人是不是代理，如果不是代理 就插入等待返利队列
			if (order.getRebate() != null && order.getRebate() == 10) {
				
				ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(order.getRebateUserId());
				
				if (proxyInfo == null) {
					RebateWaitOrder rebateWaitOrder = new RebateWaitOrder();
					rebateWaitOrder.setInvitMemId(memInfo.getInviterUserId());
					rebateWaitOrder.setMemId(memId);
					rebateWaitOrder.setMemOrderNo(chargeOrder.getOrderNo());
					rebateWaitOrderService.save(rebateWaitOrder);
				}
			}
			
			//邀请活动逻辑， 获取用户父级Id,如果不为空, 不影响事务,
			String initUserId = memInfo.getInviterUserId();
			if (!StringUtils.isEmpty(initUserId)) {
				try {
					memActiviService.inviteUpdateMemTimeByRechargeUser(initUserId);
					
					// 计算返利
					//游客用户暂时没有返利,不是游客才返利
					if (!memService.isGuestByType(memId)) {
						memActiviService.rebateMoney(order);
					}
				} catch (Exception e) {
					log.error("inviteUpdateMemTimeByRechargeUser, e = {}", e);
				}
			}
			
			
		}else {
			
			GuestInfo memGuestInfo = memGuestInfoService.getGuestById(memId);
			
			channelCode = memGuestInfo.getChannelCode();
			//如果过期，更新lastRechargteDate，
			if (memGuestInfo.isExpire()) {
				Date currDate = new Date();
				memGuestInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(currDate, monthNum * 30 * 24 * 60));
			}else {
				Date date = DateUtils.getCurrDateAddMinTime(memGuestInfo.getSuspenDate(), monthNum * 30 * 24 * 60);
				memGuestInfo.setSuspenDate(date);
			}
			
			memGuestInfoService.updateGuestInfo(memGuestInfo);
		}
		
		
		try {
			
			//插入时长兑换记录
			DurationRecord durationRecord = new DurationRecord();
			durationRecord.setMemId(chargeOrder.getMemId());
			if (ChargeTypeEum.month.getType().equals(order.getChargeType())){
				durationRecord.setConvertCardType(ConvertDurationEum.month.name());
			}else if (ChargeTypeEum.quarter.getType().equals(order.getChargeType())){
				durationRecord.setConvertCardType(ConvertDurationEum.quarter.name());
			}else if (ChargeTypeEum.halfYear.getType().equals(order.getChargeType())){
				durationRecord.setConvertCardType(ConvertDurationEum.half_year.name());
			}else if (ChargeTypeEum.year.getType().equals(order.getChargeType())){
				durationRecord.setConvertCardType(ConvertDurationEum.year.name());
			}
			durationRecord.setConvertValue(order.getFinalPrice());
			durationRecord.setConvertDuration(monthNum * 43200);
			durationRecord.setDurationSources("charge");
			durationRecordService.insert(durationRecord);
			
			//限速放开
			flowLimitService.updateRechargeUserLimit(memId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//添加付款人数事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createPayPeopleCount(channelCode, memId));
		
		//添加付款订单事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelOrderNum(channelCode, chargeOrder.getOrderNo()));
		
		//添加渠道订单金额
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelOrderMoneyValue(channelCode, chargeOrder.getOrderNo(), order.getFinalPrice() == null ? 0 : order.getFinalPrice().intValue()));
	}

	/*public void rebateMoney(ChargeOrder order){
		//判断会员表中是否有邀请人Id，没有则不执行下面，如果有，则查询订单的金额，返利给邀请人，添加到邀请人账户中，同时生成流水
		//走返利流程
		if (!StringUtils.isEmpty(order.getRebateUserId()) && MemInvitorTypeEum.mem.name().equals(order.getRebateUserType())){

			//RebateInfo rebateInfo = new RebateInfo(order.getOrderNo(), order.getRebateUserType());

			log.info("charge order update success, orderNo= {}， ready rebateing, then put mq ", new Object[]{order.getChargeType()});
			
			//判断邀请用户是否成为了20的代理
			//邀请充值的人数
			int inviMemCount = memService.getInvitationMemCount(order.getRebateUserId());
			
			if (inviMemCount == 10)
			
			//mqHandler.send(MqCons.QUEUE_OREDER_RABATE, JsonUtils.objectToJson(rebateInfo));
			this.operaOrderRabate(order.getOrderNo());
			log.info("put mq complete!!!!");

		}
	}*/
	
	/*@Transactional
	public void operaOrderRabate(String orderNo) {
		
		ChargeOrder order = chargeOrderMapper.queryOrderByOrderNo(orderNo);

		List<Map<String, Object>> rebateDetail = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> rebateLvl1Map = new HashMap<String, Object>();
		rebateLvl1Map.put("rebateUserId", order.getRebateUserId());
		rebateLvl1Map.put("rebateUserType", MemInvitorTypeEum.mem.name());
		rebateLvl1Map.put("rebateUserPhone", order.getRebateUserPhone());
		rebateLvl1Map.put("rebateUserEmail", order.getRebateUserEmail());
		rebateLvl1Map.put("rebateUserName", order.getRebateUserName());
		// 上级代理返利级别  写死：1
		rebateLvl1Map.put("upLvl", "1");
		
		ProxyInfo proxyInfo = proxyInfoMapper.selectProxyAndGroupByProxyId(order.getRebateUserId());
		
		log.debug("operaOrderRabate selectProxyAndGroupByProxyId(memId), memId = {} ,  proxyInfo  = {}", order.getMemId(), proxyInfo == null ? "" : proxyInfo.toString());
		
		BigDecimal fianlPrice = order.getFinalPrice();
		
		if (proxyInfo == null) {
			log.error("operaOrderRabate, query proxyInfo error . proxyInfo is null");
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "query proxyInfo error . proxyInfo is null");
		}
		
		String rebateConfig = proxyInfo.getRebateConfig();
		@SuppressWarnings("unchecked")
		Map<String, String> rebateConfigMap = JsonUtils.jsonToPojo(rebateConfig, Map.class);
		// 取一级返利级别
		String rebateStr = rebateConfigMap.get("lvl1");
		int rebateValue = order.getRebate();
		rebateLvl1Map.put("rebate", rebateValue);
		rebateLvl1Map.put("rebateMoney", fianlPrice.multiply(BigDecimal.valueOf(0.01 * rebateValue)).setScale(2,BigDecimal.ROUND_FLOOR));
		rebateDetail.add(rebateLvl1Map);

		int rabateLvl = order.getRebateLvl();
		
		if (rabateLvl > 1) {
			for (int lvl = 2; lvl <= rabateLvl; lvl++) {

				MemInfo memInfo = memInfoMapper.selectParentByMemId(order.getRebateUserId());
				// 当返利等级为两级时，而只有一级代理时处理
				if(memInfo != null){
					ProxyInfo proxyTempInfo = proxyInfoMapper.selectProxyAndGroupByProxyId(memInfo.getMemId());

					if (proxyTempInfo == null) {
						log.error("operaOrderRabate, query proxyInfo error . proxyInfo is null");
						break;
						//throw new BusinessException(ResultCode.UNKNOW_ERROR, "query proxyInfo error . proxyInfo is null");
					}

					if (memInfo == null || !"1".equals(memInfo.getIsProxy())) {
						break;
					}

					String rebateConfigInfo = proxyInfo.getRebateConfig();
					@SuppressWarnings("unchecked")
					Map<String, String> rebateConfigInfoMap = JsonUtils.jsonToPojo(rebateConfigInfo, Map.class);

					Map<String, Object> rebateLvlMap = new HashMap<String, Object>();
					rebateLvlMap.put("rebateUserId", memInfo.getMemId());
					rebateLvlMap.put("rebateUserType", MemInvitorTypeEum.mem.name());
					rebateLvlMap.put("rebateUserPhone", memInfo.getMemPhone());
					rebateLvlMap.put("rebateUserEmail", memInfo.getMemEmail());
					rebateLvlMap.put("rebateUserName", memInfo.getMemNickName());
					rebateLvlMap.put("upLvl", lvl);
					String parentRebate = rebateConfigInfoMap.get("lvl" + lvl);
					int parentRebateValue = Integer.parseInt(parentRebate);
					rebateLvlMap.put("rebate", parentRebateValue);
					rebateLvlMap.put("rebateMoney", fianlPrice.multiply(BigDecimal.valueOf(0.01 * parentRebateValue)).setScale(2,BigDecimal.ROUND_FLOOR));
					rebateDetail.add(rebateLvlMap);
				}
			}
		}
		
		//为每个要分成的用户的账户添加余额
		updateAccountAndRecord(order, rebateDetail, fianlPrice);
		
		//更新订单表中的分成数据和状态
		ChargeOrder chargeOrder = new ChargeOrder();
		chargeOrder.setOrderNo(orderNo);
		chargeOrder.setRebateDetails(JsonUtils.objectToJson(rebateDetail));
		chargeOrder.setRebateStatus(FlowStatusEum.success.getStatus());
		
		chargeOrderMapper.updateOrderMemRebateStatusByOrderNo(chargeOrder);
	}*/

	/*@Transactional
	public void updateAccountAndRecord(ChargeOrder order, List<Map<String, Object>> rebateDetail, BigDecimal fianlPrice) {
		//为没有要分成的用户的账户添加余额
		for (Map<String, Object> rebateMap : rebateDetail) {
			
			String rebateUserId = (String) rebateMap.get("rebateUserId");
			BigDecimal rebateMoney = (BigDecimal) rebateMap.get("rebateMoney");
			MemAccount memAccount = memAccountMapper.queryMemAccountByMemId(rebateUserId);
			memAccount.setUpdateTime(new Date());
			memAccount.setAccountMoney(rebateMoney.add(memAccount.getAccountMoney()));
			memAccountMapper.updateMoneyByAccountNo(memAccount);
			
			MemAccountRecord memAccountRecord = new MemAccountRecord();
			memAccountRecord.setAccountNo(memAccount.getAccountNo());
			memAccountRecord.setRecordType(AccRecordTypeEum.sys_rebate.getCode());
			memAccountRecord.setRecordWay((byte)0);
			memAccountRecord.setRecordStatus(FlowStatusEum.success.getStatus());
			memAccountRecord.setOrderNo(order.getOrderNo());
			memAccountRecord.setChangeMoney(rebateMoney);
			memAccountRecord.setBalanceMoney(memAccount.getAccountMoney());
			memAccountRecord.setMemId(rebateUserId);
			
			rebateMap.put("finalPrice", fianlPrice);
			
			rebateMap.put("chargeType", order.getChargeType());
			memAccountRecord.setOrderContent(JsonUtils.objectToJson(rebateMap));
			
			String rebateUserName = (String) rebateMap.get("rebateUserName");
			String rebateUserPhone = (String) rebateMap.get("rebateUserPhone");
			memAccountRecord.setMemName(rebateUserName);
			memAccountRecord.setMemPhone(rebateUserPhone);
			
			memAccountRecord.setOutMemId(order.getMemId());
			memAccountRecord.setOutMemName(order.getMemName());
			memAccountRecord.setOutMemPhone(order.getMemPhone());
			memAccountRecord.setCreateTime(new Date());
			memAccountRecord.setUpdateTime(memAccountRecord.getCreateTime());
			
			memAccountRecordMapper.insert(memAccountRecord);
		}
		
	}*/
	
	private void addAccount(MemAccount memAccount) {
		
		if (StringUtils.isEmpty(memAccount.getMemId())) {
			throw new BusinessException(ResultCode.DATABASE_OPERATIOIN_IS_ERROR, "insert memAccount memId is not null");
		}
		
		memAccount.setAccountNo(snowFlakeManager.create(GenIDEnum.ACCOUNT_NO.name()).getIdByPrefix(GenIDEnum.ACCOUNT_NO.getPreFix()));
		memAccount.setAccountMoney(new BigDecimal(0));
		memAccount.setCreateTime(new Date());
		memAccount.setAccountStatus(String.valueOf(EnableStatus.enable.getCode()));
		
		memAccountMapper.insert(memAccount);
		
	}
	
	private int getMonthNum(String chargeType) {
		
		int monthNum = 1;
		
		if (ChargeTypeEum.month.getType().equals(chargeType)){
			monthNum = 1;
		}else if (ChargeTypeEum.quarter.getType().equals(chargeType)){
			monthNum = 3;
		}else if (ChargeTypeEum.halfYear.getType().equals(chargeType)){
			monthNum = 6;
		}else if (ChargeTypeEum.year.getType().equals(chargeType)){
			monthNum = 12;
		}else if (ChargeTypeEum.forever.getType().equals(chargeType)){
			monthNum = 12 * 50;
		}else {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "getMonthNum chargeType do not matched");
		}
		
		return monthNum;
	}

	public static Date addDate(Date date,long day) {
		long time = date.getTime(); // 得到指定日期的毫秒数
		day = day*24*60*60*1000; // 要加上的天数转换成毫秒数
		time+=day; // 相加得到新的毫秒数
		return new Date(time); // 将毫秒数转换成日期
	}

	public List<ChargeOrder> queryChargeOrderList(Page page) {
		//return chargeOrderMapper.queryChargeOrderListPage(page);
		return chargeOrderMapper.queryChargeOrderAndMemListPage(page);
	}

	public void deleteChargeOrderById(String orderId) {
		chargeOrderMapper.deleteByPrimaryKey(orderId);
	}

	/**
	 * 批量设置会员代理
	 * @param memInfoList
	 */
	@Transactional
	public void setProxyBatch(List<MemInfo> memInfoList){
		memInfoList.stream().forEach(memInfo -> {
			//如果是永久用户，同时添加代理商表
			ProxyInfo proxyInfo = new ProxyInfo();
			proxyInfo.setCreateTime(new Date());
			proxyInfo.setMemType(memInfo.getMemType());
			proxyInfo.setProxyId(memInfo.getMemId());
			proxyInfo.setProxyCard(memInfo.getMemCard());
			proxyInfo.setProxyGroupid("1");
			proxyInfo.setProxyName(memInfo.getMemName());
			proxyInfo.setProxyNickName(memInfo.getMemNickName());
			proxyInfo.setProxyPhone(memInfo.getMemPhone());
			proxyInfo.setProxyReallyName(memInfo.getMemReallyName());
			proxyInfo.setUpdateTime(proxyInfo.getCreateTime());
			proxyInfo.setProxyLvl(MemProxyLvlEum.lvl0.getCode());

			//添加代理商用户表
			proxyInfoMapper.insert(proxyInfo);

			//如果购买的订单是代理上则添加账户
			MemAccount memAccount = new MemAccount();
			memAccount.setMemId(memInfo.getMemId());
			addAccount(memAccount);

			// 计算代理到期日期（50年）
			/*int monthNum = getMonthNum(ChargeTypeEum.forever.getType());
			//Date date = addDate(new Date(), monthNum * 30);
			memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), monthNum * 30 * 24 * 60));*/
			//memInfo.setSuspenDate(date);
			// 设置为普通会员
			memInfo.setMemType(MemTypeEum.general_mem.getCode());
			// 设置为代理
			memInfo.setIsProxy("1");

			memService.updateMemInfo(memInfo);
		});
	}

	/*
	* 随机获取一条商品名称
	* */
	public String getSubjectName(){
		synchronized (this){
			// 获取一条商品信息
			AliSubject aliSubject = aliSubjectMapper.getAliSubjectOne();
			String subjectName = aliSubject.getSubjectName();
			// 删除商品信息
			// int delete = aliSubjectMapper.delete(aliSubject.getId());
			return subjectName;
		}
	}

	/**
	 * 查询当前时间前三分钟未支付成功的充值订单，并更新订单状态
	 * @return
	 */
	public List<ChargeOrder> getChargeOrderByCron(){
		List<ChargeOrder> chargeOrderList = chargeOrderMapper.getChargeOrderByCron();
		chargeOrderList.stream().forEach(chargeOrder -> {
			queryOrderAndUpdateByNo(chargeOrder.getMemId(),chargeOrder.getOrderNo());
		});
		return chargeOrderList;
	}

	public List<ChargeOrder> todayChargeOrderList(Page page) {
		return chargeOrderMapper.todayChargeOrderList(page);
	}

	public void getChargeOrder(Page page) {
		List<ChargeOrder> chargeOrderList = chargeOrderMapper.getChargeOrderByPage(page);
		page.setData(chargeOrderList);
	}
	
	public List<Map<String, Object>> getOrderMoneyStatisByDayAndType(String month) {
		String yyyy = month.substring(0, 4);
		String mm = month.substring(4, 6);
		String formatMonth = yyyy + "-" + mm;
		List<Map<String, Object>> orderList = chargeOrderMapper.selectChargeMoneyGroupbyTypeTime(formatMonth);
		int dayNum = DateUtils.getMaxDayNumByMonth(month);
		
		//month("10", "月卡"), quarter("11", "季卡"), halfYear("12", "半年卡"), year("13", "年卡"), forever("14", "永久");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		for (int i = 1; i <= dayNum; i++) {
			String dayStr =String.format("%02d", i);
			dayStr = yyyy + "-" + mm + "-" + dayStr;
			
			Map<String, Object> mapList = new HashMap<String, Object>();
			mapList.put("day", dayStr);
			
			mapList.put("newchannelcount", "0");
			mapList.put("newchannelmonthcount", "0");
			mapList.put("newchannelquartercount", "0");
			mapList.put("newchannelhalfYearcount", "0");
			mapList.put("newchannelyearcount", "0");
			mapList.put("newchannelforevercount", "0");
			mapList.put("newchannelmonth", "0");
			mapList.put("newchannelquarter", "0");
			mapList.put("newchannelhalfYear", "0");
			mapList.put("newchannelyear", "0");
			mapList.put("newchannelforever", "0");
			
			mapList.put("newcount", "0");
			mapList.put("newmonthcount", "0");
			mapList.put("newquartercount", "0");
			mapList.put("newhalfYearcount", "0");
			mapList.put("newyearcount", "0");
			mapList.put("newforevercount", "0");
			mapList.put("newmonth", "0");
			mapList.put("newquarter", "0");
			mapList.put("newhalfYear", "0");
			mapList.put("newyear", "0");
			mapList.put("newforever", "0");
			
			mapList.put("oldcount", "0");
			mapList.put("oldmonthcount", "0");
			mapList.put("oldquartercount", "0");
			mapList.put("oldhalfYearcount", "0");
			mapList.put("oldyearcount", "0");
			mapList.put("oldforevercount", "0");
			mapList.put("oldmonth", "0");
			mapList.put("oldquarter", "0");
			mapList.put("oldhalfYear", "0");
			mapList.put("oldyear", "0");
			mapList.put("oldforever", "0");
			
			mapList.put("monthcount", "0");
			mapList.put("quartercount", "0");
			mapList.put("halfYearcount", "0");
			mapList.put("yearcount", "0");
			mapList.put("forevercount", "0");
			
			mapList.put("month", "0");
			mapList.put("quarter", "0");
			mapList.put("halfYear", "0");
			mapList.put("year", "0");
			mapList.put("forever", "0");
			
			mapList.put("total", "0");
			mapList.put("totalcount", "0");
			
			boolean flag = false;
			for (Map<String, Object> orderMap : orderList) {
				String day = String.valueOf(orderMap.get("day"));
				
				if (dayStr.equals(day)) {
					flag = true;
					mapList.clear();
					mapList.putAll(orderMap);
					result.add(mapList);
				} 
			}
			
			if (!flag) {
				result.add(mapList);
			}
			
		}
		
		/*result.forEach(item -> {
			
			BigDecimal monthTemp = new BigDecimal(String.valueOf(item.get("month")));
			BigDecimal quarterTemp = new BigDecimal(String.valueOf(item.get("quarter")));
			BigDecimal halfYearTemp = new BigDecimal(String.valueOf(item.get("halfYear")));
			BigDecimal yearMonthTemp = new BigDecimal(String.valueOf(item.get("year")));
			BigDecimal foreverMonthTemp = new BigDecimal(String.valueOf(item.get("forever")));
			item.put("total", monthTemp.add(quarterTemp).add(halfYearTemp).add(yearMonthTemp).add(foreverMonthTemp).toString());
			
		});*/
		
		return result;
		
	}
	
	public List<Map<String, Object>> getOrderRechargePeopleGroupByDay(String month) {
		
		String yyyy = month.substring(0, 4);
		String mm = month.substring(4, 6);
		String formatMonth = yyyy + "-" + mm;
		int dayNum = DateUtils.getMaxDayNumByMonth(month);
		
		List<Map<String, Object>> orderList = chargeOrderMapper.getOrderRechargePeopleGroupByDay(formatMonth);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int i = 1; i <= dayNum; i++) {
			String dayStr =String.format("%02d", i);
			dayStr = yyyy + "-" + mm + "-" + dayStr;
			
			Map<String, Object> mapList = new HashMap<String, Object>();
			mapList.put("day", dayStr);
			mapList.put("totalMoney", "0");
			mapList.put("newMoney", "0");
			mapList.put("oldMoney", "0");
			mapList.put("newCount", "0");
			mapList.put("oldCount", "0");
			
			boolean flag = false;
			for (Map<String, Object> orderMap : orderList) {
				String day = String.valueOf(orderMap.get("day"));
				
				if (dayStr.equals(day)) {
					flag = true;
					mapList.clear();
					mapList.putAll(orderMap);
					result.add(mapList);
				} 
			}
			
			//flag为false的时候给默认值
			if (!flag) {
				result.add(mapList);
			}
			
			
		}
		
		return result;
	}
	
	public List<Map<String, Object>> getOrderMemTypeMoneyByMonth(String month) {
		
		if (month.indexOf("-") > 0) {
			month = month.replace("-", "");
		}
		
		String yyyy = month.substring(0, 4);
		String mm = month.substring(4, 6);
		String formatMonth = yyyy + "-" + mm;
		int dayNum = DateUtils.getMaxDayNumByMonth(month);
		
		List<Map<String, Object>> orderList = chargeOrderMapper.selectChargeMemTypeMoneyGroupbyTypeTime(formatMonth);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int i = 1; i <= dayNum; i++) {
			String dayStr =String.format("%02d", i);
			dayStr = yyyy + "-" + mm + "-" + dayStr;
			
			Map<String, Object> mapList = new HashMap<String, Object>();
			mapList.put("day", dayStr);
			mapList.put("guestTotal", "0");
			mapList.put("guestTotalCount", "0");

			mapList.put("tryTotal", "0");
			mapList.put("tryTotalCount", "0");
			
			mapList.put("memTotal", "0");
			mapList.put("memTotalCount", "0");
			
			mapList.put("total", "0");
			mapList.put("totalcount", "0");
			
			boolean flag = false;
			for (Map<String, Object> orderMap : orderList) {
				String day = String.valueOf(orderMap.get("day"));
				
				if (dayStr.equals(day)) {
					flag = true;
					mapList.clear();
					mapList.putAll(orderMap);
					result.add(mapList);
				} 
			}
			
			//flag为false的时候给默认值
			if (!flag) {
				result.add(mapList);
			}
			
			
		}
		
		return result;
	}
}
