package com.tsn.serv.mem.service.mem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.comm.FlowStatusEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.AccRecordTypeEum;
import com.tsn.serv.common.enm.mem.MemInvitorTypeEum;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.account.MemAccountRecord;
import com.tsn.serv.mem.entity.activit.ActivitRuleInvit;
import com.tsn.serv.mem.entity.mem.MemExtInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.entity.order.RebateWaitOrder;
import com.tsn.serv.mem.entity.proxy.ProxyInfo;
import com.tsn.serv.mem.mapper.account.MemAccountMapper;
import com.tsn.serv.mem.mapper.account.MemAccountRecordMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.order.ChargeOrderMapper;
import com.tsn.serv.mem.mapper.proxy.ProxyInfoMapper;
import com.tsn.serv.mem.service.activit.ActivitRuleInvitService;
import com.tsn.serv.mem.service.order.RebateWaitOrderService;

@Service
public class MemActiviService {
	
	@Autowired
	private MemInfoMapper memMapper;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	public ProxyInfoMapper proxyInfoMapper;

	@Autowired
	private MemAccountMapper memAccountMapper;
	
	@Autowired
	private MemAccountRecordMapper memAccountRecordMapper;

	@Autowired
	private DurationRecordService durationRecordService;

	@Autowired
	private MemExtInfoService memExtInfoService;
	
	@Autowired
	private ActivitRuleInvitService activitRuleInvitService;
	
	@Autowired
	private ChargeOrderMapper chargeOrderMapper;
	
	@Autowired
	private CDKService cdkService;
	
	@Autowired
	private RebateWaitOrderService rebateWaitOrderService;
	
	private static Logger log = LoggerFactory.getLogger(MemActiviService.class);
	
	/**
	 * 邀请用户注册后，会调用该方法
	 * 注意：游客也有可能成为代理
	 * @param parentId
	 */
	/*@Transactional
	public void inviteUpdateMemTimeByPeopleNum1(String parentId) {
		
		int people2Time = 10;
		int people2ProxyLvl20 = 20;
		int people2ProxyLvl40 = 50;
		
		//如果memExtInfo == null 说明是老用户
		MemExtInfo memExtInfo = memExtInfoService.getById(parentId);
		
		//如果是老用户
		if (memExtInfo == null) {
			return;
		}
		
		//获取父级邀请的用户的信息
		int inviCount = memMapper.getInvitationUserCount(parentId);
		
		//如果小于10用户个就不做处理
		if (inviCount < people2Time) {
			return;
		}
		
		//获取用户信息
		MemInfo memInfo = memMapper.selectByPrimaryKey(parentId);
		boolean isProxy = "1".equals(memInfo.getIsProxy()) ? true :  false;
		
		//如果不是代理 且 邀请人数大于等于10，延长30天
		if (!isProxy && inviCount >= people2Time) {
			
			//如果已经邀请任务兑换了就不走下面
			if (memExtInfo == null || memExtInfo.getIsInvitGetTime() == null || memExtInfo.getIsInvitGetTime() != 1) {
				//新增时长记录
				durationRecordService.addDurationOfInvite(parentId);
				int duration = 30 * 24 * 60;
				MemInfo memInfoTemp = new MemInfo();
				memInfoTemp.setMemId(parentId);
				if (memInfo.isExpire()) {
					memInfoTemp.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), duration));
				}else {
					memInfoTemp.setSuspenDate(DateUtils.getCurrDateAddMinTime(memInfo.getSuspenDate(), duration));
				}
				memMapper.updateByPrimaryKeySelective(memInfoTemp);
				
				//更新扩展信息表
				MemExtInfo memExtInfoTemp = new MemExtInfo();
				memExtInfoTemp.setId(parentId);
				memExtInfoTemp.setIsInvitGetTime(1);
				memExtInfoService.updateById(memExtInfoTemp);
				if(!res) {
					memExtInfoService.save(memExtInfoTemp);
				}
			}
		}
		
		//成为代理,并修改等级
		if (inviCount >= people2ProxyLvl20 && inviCount < people2ProxyLvl40) {
			
			//如果不是代理，升级代理，如果是代理，说明已经升级过了，不操作任何
			if (!isProxy) {
				//20表示百分比
				upUser2Proxy(parentId, MemProxyLvlEum.lvl0.getCode());
			}
			
		}
		
		//修改等级
		if (inviCount >= people2ProxyLvl40) {
			
			String rebate = MemProxyLvlEum.lvl1.getCode();
			
			//如果不是代理，升级代理，同时修改代理级别  这种情况只会出现老用户
			if (!isProxy) {
				//20表示百分比
				upUser2Proxy(parentId, rebate);
			}else {//如果是代理，可能之前的比例是20，现在要升级成40
				//如果是永久用户，同时添加代理商表
				ProxyInfo proxyInfoTmp = new ProxyInfo();
				proxyInfoTmp.setProxyId(memInfo.getMemId());
				proxyInfoTmp.setProxyLvl(rebate);
				//添加代理商用户表
				proxyInfoMapper.updateByPrimaryKeySelective(proxyInfoTmp);
			}
		}
		
	}*/
	
	/**
	 * 根据该用户下邀请的用户充值的数量来决定，返利，成为代理
	 * @param parentId
	 */
	@Transactional
	public void inviteUpdateMemTimeByRechargeUser(String parentId) {
		
		//如果memExtInfo == null 说明是老用户
		MemExtInfo memExtInfo = memExtInfoService.getById(parentId);
		
		//如果是老用户
		/*if (memExtInfo == null) {
			return;
		}*/
		
		//获取父级邀请的已充值的用户的信息
		int inviMemCount = memMapper.getInvitationMemCount(parentId);
		
		//获取邀请人数活动
		QueryWrapper<ActivitRuleInvit> queryWapper = new QueryWrapper<ActivitRuleInvit>();
		queryWapper.orderByDesc("people_num");
		List<ActivitRuleInvit> activitRuleInvit = activitRuleInvitService.list(queryWapper);
		
		for (ActivitRuleInvit activitInvit : activitRuleInvit) {
			
			Integer peopleNum = activitInvit.getPeopleNum();
			
			if (peopleNum == null) {
				continue;
			}
			
			String dayCdkType = activitInvit.getDayCdkType();
			String weekCdkType = activitInvit.getWeekCdkType();
			String monthCdkType = activitInvit.getMonthCdkType();
			
			//参与过的活动人数 大于 任务人数 说明参与过了，就不执行了
			if (memExtInfo != null && memExtInfo.getInivActivitPeopleNum() != null && memExtInfo.getInivActivitPeopleNum() >= peopleNum) {
				continue;
			}
			
			//如果邀请人数大于活动人数，就奖励
			if (inviMemCount >= peopleNum) {
				
				//给用户发放CDK开 
				if (!StringUtils.isEmpty(dayCdkType)) {
					Integer dayCdkNum = activitInvit.getDayCdkNum();
					cdkService.insertCDK(parentId, dayCdkType, dayCdkNum, "auto");
				} 
				
				if (!StringUtils.isEmpty(weekCdkType)) {
					Integer weekCdkNum = activitInvit.getWeekCdkNum();
					cdkService.insertCDK(parentId, weekCdkType, weekCdkNum, "auto");
				} 
				
				if (!StringUtils.isEmpty(monthCdkType)) {
					Integer monthCdkNum = activitInvit.getMonthCdkNum();
					cdkService.insertCDK(parentId, monthCdkType, monthCdkNum, "auto");
				}
				
				
				//int duration = activitInvit.getDuration();
				Integer proxyRebate = activitInvit.getProxyRebate();
				//不能超过60%
				if (proxyRebate != null && proxyRebate > 0 && proxyRebate < 60) {
					//获取用户信息
					MemInfo memInfo = memMapper.selectByPrimaryKey(parentId);
					boolean isProxy = "1".equals(memInfo.getIsProxy()) ? true :  false;
					
					if (!isProxy) {
						//20表示百分比
						upUser2Proxy(parentId, String.valueOf(proxyRebate));
					}else {//如果是代理，可能之前的比例是20，现在要升级成40
						//如果是永久用户，同时添加代理商表
						ProxyInfo proxyInfoTmp = new ProxyInfo();
						proxyInfoTmp.setProxyId(memInfo.getMemId());
						proxyInfoTmp.setProxyLvl(String.valueOf(proxyRebate));
						//添加代理商用户表
						proxyInfoMapper.updateByPrimaryKeySelective(proxyInfoTmp);
					}
					
				}
				
				//更新扩展信息表
				MemExtInfo memExtInfoTemp = new MemExtInfo();
				memExtInfoTemp.setId(parentId);
				memExtInfoTemp.setInivActivitPeopleNum(peopleNum);
				boolean res = memExtInfoService.updateById(memExtInfoTemp);
				
				if(!res) {
					memExtInfoService.save(memExtInfoTemp);
				}
				
				break;
			}
		}
		
		/*int people2Time = 10;
		int people2ProxyLvl20 = 20;
		int people2ProxyLvl40 = 50;*/
		
		/*//如果小于10用户个就不做处理
		if (inviMemCount < people2Time) {
			return;
		}
		
		//获取用户信息
		MemInfo memInfo = memMapper.selectByPrimaryKey(parentId);
		boolean isProxy = "1".equals(memInfo.getIsProxy()) ? true :  false;
		
		//如果不是代理 且 邀请人数大于等于10，延长30天
		if (!isProxy && inviMemCount >= people2Time) {
			
			//如果已经邀请任务兑换了就不走下面
			if (memExtInfo == null || memExtInfo.getIsInvitGetTime() == null || memExtInfo.getIsInvitGetTime() != 1) {
				//新增时长记录
				durationRecordService.addDurationOfInvite(parentId);
				int duration = 30 * 24 * 60;
				MemInfo memInfoTemp = new MemInfo();
				memInfoTemp.setMemId(parentId);
				if (memInfo.isExpire()) {
					memInfoTemp.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), duration));
				}else {
					memInfoTemp.setSuspenDate(DateUtils.getCurrDateAddMinTime(memInfo.getSuspenDate(), duration));
				}
				memMapper.updateByPrimaryKeySelective(memInfoTemp);
				
				//更新扩展信息表
				MemExtInfo memExtInfoTemp = new MemExtInfo();
				memExtInfoTemp.setId(parentId);
				memExtInfoTemp.setIsInvitGetTime(1);
				boolean  res = memExtInfoService.updateById(memExtInfoTemp);
				if(!res) {
					memExtInfoService.save(memExtInfoTemp);
				}
			}
		}
		
		//成为代理,并修改等级
		if (inviMemCount >= people2ProxyLvl20 && inviMemCount < people2ProxyLvl40) {
			
			//如果不是代理，升级代理，如果是代理，说明已经升级过了，不操作任何
			if (!isProxy) {
				//20表示百分比
				upUser2Proxy(parentId, MemProxyLvlEum.lvl0.getCode());
			}
			
		}
		
		//修改等级
		if (inviMemCount >= people2ProxyLvl40) {
			
			String rebate = MemProxyLvlEum.lvl1.getCode();
			
			//如果不是代理，升级代理，同时修改代理级别  这种情况只会出现老用户
			if (!isProxy) {
				//20表示百分比
				upUser2Proxy(parentId, rebate);
			}else {//如果是代理，可能之前的比例是20，现在要升级成40
				//如果是永久用户，同时添加代理商表
				ProxyInfo proxyInfoTmp = new ProxyInfo();
				proxyInfoTmp.setProxyId(memInfo.getMemId());
				proxyInfoTmp.setProxyLvl(rebate);
				//添加代理商用户表
				proxyInfoMapper.updateByPrimaryKeySelective(proxyInfoTmp);
			}
		}*/
		
	}
	
	/**
	 * 升级用户成为代理
	 */
	@Transactional
	public void upUser2Proxy(String userId, String lvl) {
		
		MemInfo memInfo = memMapper.selectByPrimaryKey(userId);
		
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
		
		proxyInfo.setProxyLvl(lvl);

		//添加代理商用户表
		proxyInfoMapper.insert(proxyInfo);

		//如果购买的订单是代理上则添加账户
		MemAccount memAccount = new MemAccount();
		memAccount.setMemId(memInfo.getMemId());
		addAccount(memAccount);

		// 计算代理到期日期（50年）,成为代理跟购买套餐没有啥关系，先暂时去掉
		//int monthNum = getMonthNum(ChargeTypeEum.forever.getType());
		//int monthNum = 12 * 50;
		//Date date = addDate(new Date(), monthNum * 30);
		//memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), monthNum * 30 * 24 * 60));
		//memInfo.setSuspenDate(date);
		// 设置为普通会员
		//memInfo.setMemType(MemTypeEum.general_mem.getCode());
		// 设置为代理
		memInfo.setIsProxy("1");

		memMapper.updateByPrimaryKeySelective(memInfo);
	}
	
	private int addAccount(MemAccount memAccount) {
		
		if (StringUtils.isEmpty(memAccount.getMemId())) {
			throw new BusinessException(ResultCode.DATABASE_OPERATIOIN_IS_ERROR, "insert memAccount memId is not null");
		}
		
		memAccount.setAccountNo(SnowFlakeManager.build().create(GenIDEnum.ACCOUNT_NO.name()).getIdByPrefix(GenIDEnum.ACCOUNT_NO.getPreFix()));
		memAccount.setAccountMoney(new BigDecimal(0));
		memAccount.setCreateTime(new Date());
		memAccount.setAccountStatus(String.valueOf(EnableStatus.enable.getCode()));
		
		return memAccountMapper.insert(memAccount);
		
	}
	
	public Map<String, Object> queryMemActiviData(String memId) {
		
		//获取邀请会员数
		int inviMemCount = memMapper.getInvitationMemCount(memId);
		
		//获取邀请所有得用户数
		int inviUserCount = memMapper.getInvitationUserCount(memId);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("inviMemCount", inviMemCount);
		result.put("inviUserCount", inviUserCount);
		
		return result;
		
	}
	
	@Transactional
	public void rebateMoney(ChargeOrder order){
		//判断会员表中是否有邀请人Id，没有则不执行下面，如果有，则查询订单的金额，返利给邀请人，添加到邀请人账户中，同时生成流水
		//走返利流程
		if (!StringUtils.isEmpty(order.getRebateUserId()) && MemInvitorTypeEum.mem.name().equals(order.getRebateUserType())){

			//RebateInfo rebateInfo = new RebateInfo(order.getOrderNo(), order.getRebateUserType());

			log.info("charge order update success, orderNo= {}， ready rebateing, then put mq ", new Object[]{order.getChargeType()});
			
			
			//判断邀请用户是否成为了10的代理
			Integer rebate = order.getRebate();
			
			Integer lowerRebate = 10;
			
			if (rebate == lowerRebate) {
				
				//获取待返利订单表种的所有充值人数
				QueryWrapper<RebateWaitOrder> queryWrapper = new QueryWrapper<RebateWaitOrder>();
				queryWrapper.eq("invit_mem_id", order.getRebateUserId());
				queryWrapper.eq("status", 0);
				List<RebateWaitOrder> rebateWaitOrderList = rebateWaitOrderService.list(queryWrapper);
				
				Set<String> invitChargeUserNum = new HashSet<>();
				Set<String> invitOrderNos = new HashSet<>();
				for (RebateWaitOrder rebateWaitOrder : rebateWaitOrderList) {
					invitChargeUserNum.add(rebateWaitOrder.getMemId());
					invitOrderNos.add(rebateWaitOrder.getMemOrderNo());
				}
				
				//邀请充值的人数
				//int inviMemCount = memService.getInvitationMemCount(order.getRebateUserId());
				int inviMemCount = invitChargeUserNum.size();
				
				if (inviMemCount == 10 || inviMemCount == 11) {
					
					for (String orderNo : invitOrderNos) {
						
						this.operaOrderRabate(orderNo);
						
					}
					
					//
					UpdateWrapper<RebateWaitOrder> updateWrapper = new UpdateWrapper<RebateWaitOrder>();
					updateWrapper.set("status", 1);
					updateWrapper.eq("invit_mem_id", order.getRebateUserId());
					rebateWaitOrderService.update(updateWrapper);
				}
			}else {
				this.operaOrderRabate(order.getOrderNo());
			}
			
			//mqHandler.send(MqCons.QUEUE_OREDER_RABATE, JsonUtils.objectToJson(rebateInfo));
			
			//log.info("put mq complete!!!!");

		}
	}
	
	@Transactional
	public void operaOrderRabate(String orderNo) {
		
		ChargeOrder order = chargeOrderMapper.queryOrderByOrderNo(orderNo);
		
		// 当订单需要返利，但返利状态为未返利时重新计算返利
		if ("1".equals(order.getRebateStatus())){
			return;
		}
		
		//如果返利为10，可能邀请人不是代理，这里要判断下邀请人是不是代理，如果不是代理，就不返利，如果是代理就返利
		if (order.getRebate() == 10) {
			
			ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(order.getRebateUserId());
			
			if (proxyInfo == null) {
				return;
			}
		}
		
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
		
		/*String rebateConfig = proxyInfo.getRebateConfig();
		@SuppressWarnings("unchecked")
		Map<String, String> rebateConfigMap = JsonUtils.jsonToPojo(rebateConfig, Map.class);
		// 取一级返利级别
		String rebateStr = rebateConfigMap.get("lvl1");*/
		int rebateValue = order.getRebate();
		rebateLvl1Map.put("rebate", rebateValue);
		rebateLvl1Map.put("rebateMoney", fianlPrice.multiply(BigDecimal.valueOf(0.01 * rebateValue)).setScale(2,BigDecimal.ROUND_FLOOR));
		rebateDetail.add(rebateLvl1Map);

		int rabateLvl = order.getRebateLvl();
		
		if (rabateLvl > 1) {
			for (int lvl = 2; lvl <= rabateLvl; lvl++) {

				MemInfo memInfo = memMapper.selectParentByMemId(order.getRebateUserId());
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
	}
	
	@Transactional
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
		
	}
	
}
