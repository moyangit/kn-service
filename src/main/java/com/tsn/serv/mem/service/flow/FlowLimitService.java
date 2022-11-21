package com.tsn.serv.mem.service.flow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.jms.MqHandler;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.mem.entity.device.MemDeviceOline;
import com.tsn.serv.mem.entity.flow.FlowDay;
import com.tsn.serv.mem.entity.flow.FlowLimit;
import com.tsn.serv.mem.entity.flow.FlowLimitConfig;
import com.tsn.serv.mem.entity.flow.FlowMonthCy;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.device.MemDeviceOlineMapper;
import com.tsn.serv.mem.mapper.flow.FlowDayMapper;
import com.tsn.serv.mem.mapper.flow.FlowLimitConfigMapper;
import com.tsn.serv.mem.mapper.flow.FlowLimitMapper;
import com.tsn.serv.mem.mapper.flow.FlowMonthCyMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;

@Service
public class FlowLimitService {
	
	@Autowired
	private FlowLimitMapper flowLimitMapper;
	
	@Autowired
	private MemInfoMapper memMapper;
	
	@Autowired
	private FlowLimitConfigMapper flowLimitConfigMapper;
	
	@Autowired
	private FlowLimitConfigService flowLimitConfigService;

	@Autowired
	private FlowMonthCyMapper flowMonthCyMapper;
	
	@Autowired
	private MqHandler mqHandler;

	@Autowired
	private FlowDayMapper flowDayMapper;

	@Autowired
	private MemDeviceOlineMapper memDeviceOlineMapper;

	@Autowired
	private RedisHandler redisHandler;
	
	private static Logger log = LoggerFactory.getLogger(FlowLimitService.class);
	
	@Transactional
	public void updateAndAddFlowLimitForMem(FlowLimit flowLimit) {
		
		//先更新流量，
		int result = flowLimitMapper.updateMemFlowNoHostByMemId(flowLimit);
		
		FlowLimit newflowLimit = flowLimitMapper.selectByPrimaryKey(flowLimit.getMemId());
		
		//如果已经限速了，则不走下面流程
		if (result == 1 && newflowLimit != null && newflowLimit.getIsLimit() == 1) {
			return;
		}
		
		//如果返回为1，表示更新成功，如果返回为0，表示不存在，或者超出周期范围
		if (result == 0) {
			
			//如果返回为0，这是先认为它超出周期范围，同时重置流量，并重新更新周期范围
			flowLimit.setIsLimit((byte)0);
			//flowLimit.setMaxDownFlowLimit(0l);
			//flowLimit.setMaxUpFlowLimit(0l);
			int resetResult = flowLimitMapper.updateAndResetMemFlowNoHostByMemId(flowLimit);
			
			//重置返回0表示，没有数据，就得添加/。
			if (resetResult == 0) {
				
				//判断是游客还是快加速
				String memId = flowLimit.getMemId();
				//用户类型，2是游客， 1是注册用户
				String userType = memId.substring(0, 1);
				
				if (GenIDEnum.MEM_ID.getPreFix().equals(userType)) {
					MemInfo memInfo = memMapper.selectByPrimaryKey(flowLimit.getMemId());
					
					//如果重置流量更新也返回0，那就说明不存在该记录，这时添加
					flowLimit.setCreateTime(new Date());
					flowLimit.setMemType(memInfo.getMemType());
					flowLimit.setTotalUseUpFlow(flowLimit.getCurrUseUpFlow());
					flowLimit.setTotalUseDownFlow(flowLimit.getCurrUseDownFlow());
					
					//如果为空表示，没有购买过
					Date cyStartTime = memInfo.getLastRechargeDate() == null ? new Date() : memInfo.getLastRechargeDate();
					flowLimit.setCyStartTime(cyStartTime);
					flowLimit.setCyUseUpFlow(flowLimit.getCurrUseUpFlow());
					flowLimit.setCyUseDownFlow(flowLimit.getCurrUseDownFlow());
					flowLimit.setCurrCyStartTime(flowLimit.getCyStartTime());
					flowLimit.setCurrCyEndTime(DateUtils.getCurrDateAddMonth(flowLimit.getCurrCyStartTime(), 1));
					
					
					//根据类型获取最大值，第一次使用用户不限速，获取最大值即可
					FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(memInfo.getMemType());
					flowLimit.setMaxDownFlowLimit(config.getMonthDownFlowLimit());
					flowLimit.setMaxUpFlowLimit(config.getMonthUpFlowLimit());
					
					flowLimitMapper.insert(flowLimit);
				} else if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType)) {
					
					flowLimit.setCreateTime(new Date());
					flowLimit.setMemType("00");
					flowLimit.setTotalUseUpFlow(flowLimit.getCurrUseUpFlow());
					flowLimit.setTotalUseDownFlow(flowLimit.getCurrUseDownFlow());
					
					flowLimit.setCyStartTime(new Date());
					flowLimit.setCyUseUpFlow(flowLimit.getCurrUseUpFlow());
					flowLimit.setCyUseDownFlow(flowLimit.getCurrUseDownFlow());
					flowLimit.setCurrCyStartTime(flowLimit.getCyStartTime());
					flowLimit.setCurrCyEndTime(DateUtils.getCurrDateAddMonth(flowLimit.getCurrCyStartTime(), 1));
					
					
					//查询更新后的值是否大于阈值，如果超过阈值，则限制，修改最大限速值
					FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig("00");
					flowLimit.setMaxDownFlowLimit(config.getMonthDownFlowLimit());
					flowLimit.setMaxUpFlowLimit(config.getMonthUpFlowLimit());
					flowLimitMapper.insert(flowLimit);
				}
				
				return;
				
			}
			
			//如果流量上传的时间小于当前周期的结束时间，这种情况不应该插入周期表
			if (flowLimit.getUpdateTime().getTime() < newflowLimit.getCurrCyEndTime().getTime()) {
				log.warn("flowLimit.getUpdateTime().getTime() < newflowLimit.getCurrCyEndTime().getTime(), {}, {}",flowLimit.getUpdateTime().getTime(), newflowLimit.getCurrCyEndTime().getTime());
				return;
			}
			
			//重置后，插入历史记录中
			FlowMonthCy flowMonthCy = new FlowMonthCy();
			flowMonthCy.setMemId(flowLimit.getMemId());
			flowMonthCy.setLimitHapTime(newflowLimit.getFirstLimitHapTime());
			flowMonthCy.setCurrCyStartTime(newflowLimit.getCurrCyStartTime());
			flowMonthCy.setCurrCyEndTime(newflowLimit.getCurrCyEndTime());
			flowMonthCy.setCreateDate(new Date());
			flowMonthCy.setUpdateDate(flowMonthCy.getCreateDate());
			flowMonthCy.setUpFlow(newflowLimit.getCyUseUpFlow());
			flowMonthCy.setDownFlow(newflowLimit.getCyUseDownFlow());
			flowMonthCy.setHostFlow(newflowLimit.getHostFlow());
			try {
				//这个地方curr_start_time 可能重复，这个地方先捕获异常，不影响事务
				flowMonthCyMapper.insert(flowMonthCy);
			} catch (Exception e) {
				log.error("flowMonthCyMapper.insert error, may be curr_start_time repeat!, e= {} ", e);
			}
			
			return;
			
		}
		
		
		//查询更新后的值是否大于阈值，如果超过阈值，则限制，修改最大限速值
		FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(newflowLimit.getMemType());
		
		if (config == null) {
			return;
		}
		
		if (newflowLimit.getCyUseTotalFlow() == null) {
			return;
		}
		
		//如果超过，则限速
		if (newflowLimit.getCyUseTotalFlow() >= config.getMaxMonthUseFlow()) {
			newflowLimit.setMaxDownFlowLimit(config.getMonthDownFlowLimit());
			newflowLimit.setMaxUpFlowLimit(config.getMonthUpFlowLimit());
			newflowLimit.setIsLimit((byte)1);
			newflowLimit.setUpdateTime(new Date());
			newflowLimit.setLimitUploadTime(new Date());
			newflowLimit.setFirstLimitHapTime(new Date());
			flowLimitMapper.updateByPrimaryKeySelective(newflowLimit);
			
			//mqHandler.send(MqCons.QUEUE_USER_EVENT, JsonUtils.objectToJson(new Event(EventEum.flow_control).setFlowControl(flowLimit.getMemId())));
			
			return;
		}
		
		
	}

	/*public FlowLimit getFlowLimit1(String userId) {

		try {
			FlowLimit newflowLimit = flowLimitMapper.selectByPrimaryKey(userId);

			if (newflowLimit == null) {

				MemInfo memInfo = memMapper.selectByPrimaryKey(userId);

				newflowLimit = new FlowLimit();
				newflowLimit.setMemId(userId);
				newflowLimit.setMemType(memInfo.getMemType());
				newflowLimit.setMaxDownFlowLimit(0l);
				newflowLimit.setMaxUpFlowLimit(0l);
			}

			FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(newflowLimit.getMemType());

			if (config == null) {
				newflowLimit.setMaxUpFlowLimit(1024 * 1024l);
				newflowLimit.setMaxDownFlowLimit(1024 * 1024l);
				return newflowLimit;
			}

			if (newflowLimit.getMaxUpFlowLimit() == null || newflowLimit.getMaxUpFlowLimit() == 0) {
				newflowLimit.setMaxUpFlowLimit(config.getMonthUpFlowLimit());
			}

			if (newflowLimit.getMaxDownFlowLimit() == null || newflowLimit.getMaxDownFlowLimit() == 0) {
				newflowLimit.setMaxDownFlowLimit(config.getMonthDownFlowLimit());
			}

			return newflowLimit;
		} catch (Exception e) {
			log.error("e = {}", e);

			FlowLimit newflowLimit = new FlowLimit();
			newflowLimit.setMemId(userId);
			newflowLimit.setMaxUpFlowLimit(1024 * 1024l);
			newflowLimit.setMaxDownFlowLimit(1024 * 1024l);
			return newflowLimit;
		}

	}*/

	public Map<String, Object> getFlowLimitTo(String userId) {
		
		log.debug(">>>>getFlowLimitTo, userId = {}", userId);
		
		long defUpFlow = 50 * 1024 * 1024 / 8;
		long defDownFlow = 50 * 1024 * 1024 / 8;
		
		Map<String, Object> retnMap = new HashMap<>();
		
		try {
			
			// 根据会员ID获取实时设备表信息
			MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
			
			if(memDeviceOline != null){
				List<Map> memDevices = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);

				StringBuilder devicesNo = new StringBuilder();
				if(memDevices != null){
					memDevices.stream()
							.forEach(item -> {
								devicesNo.append(item.get("deviceNo") + "#");
							});
				}
				// 去除最后一位多余的#号
				String subDevicesNo = devicesNo.toString().substring(0, devicesNo.length()-1);
				retnMap.put("deviceIds", subDevicesNo);
			} else {
				retnMap.put("deviceIds", "");
			}

			
			//如果为空，说明用户第一次使用，现在的情况就是不限速，获取config表中的数据即可
			///如果不为空 ，使用该对象的限速值进行限速
			FlowLimit newflowLimit = flowLimitMapper.selectByPrimaryKey(userId);

			if (newflowLimit == null) {
				
				//如果查询为空，判断用户类型，游客还是注册用户
				String userType = userId.substring(0,1);
				
				String memType = "01";
				//如果为游客
				if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType)) {
					memType = "00";
				}else {
					
					MemInfo memInfo = memMapper.selectByPrimaryKey(userId);
					memType = memInfo.getMemType();
				}
				
				
				FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(memType);

				//MemInfo memInfo = memMapper.selectByPrimaryKey(userId);
				retnMap.put("memId", userId);
				retnMap.put("maxUpFlowLimit", config.getMonthUpFlowLimit());
				retnMap.put("maxDownFlowLimit", config.getMonthUpFlowLimit());
				log.debug("<<<< newflowLimit is null, getFlowLimitTo , retnMap = {}", retnMap);
				return retnMap;
				
			}

			FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(newflowLimit.getMemType());

			if (newflowLimit.getMaxUpFlowLimit() == null || newflowLimit.getMaxUpFlowLimit() == 0) {
				newflowLimit.setMaxUpFlowLimit(config.getMonthUpFlowLimit());
			}

			if (newflowLimit.getMaxDownFlowLimit() == null || newflowLimit.getMaxDownFlowLimit() == 0) {
				newflowLimit.setMaxDownFlowLimit(config.getMonthDownFlowLimit());
			}

			retnMap.put("memId", userId);
			retnMap.put("maxUpFlowLimit", newflowLimit.getMaxUpFlowLimit());
			retnMap.put("maxDownFlowLimit", newflowLimit.getMaxDownFlowLimit());
			log.debug("<<<< newflowLimit is not null , retnMap = {}", retnMap);
			return retnMap;
		} catch (Exception e) {
			log.error("e = {}", e);
			retnMap.put("memId", userId);
			retnMap.put("maxUpFlowLimit", defUpFlow);
			retnMap.put("maxDownFlowLimit", defDownFlow);
			log.debug("<<<< error getFlowLimitTo , retnMap = {}", retnMap);
			return retnMap;
		}

	}
	
	public int insertMemFlowLimit(FlowLimit flowLimit) {
		
		int res = flowLimitMapper.insert(flowLimit);
		
		return res;
	}
	
	public FlowLimit getCurrMemFlow(String memId) {
		FlowLimit flowLimit = flowLimitMapper.selectByUserId(memId);
		
		if (flowLimit == null) {
			return null;
		}
		
		FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(flowLimit.getMemType());
		flowLimit.setMaxMonthUseFlow(config.getMaxMonthUseFlow());
		return flowLimit;
	}

    public void getMemFlowDay(Page page) {
        List<FlowDay> flowDayList = flowDayMapper.getMemFlowDay(page);
        page.setData(flowDayList);
    }

    public void getMemFlowMonth(Page page) {
        List<FlowMonthCy> flowMonthCyList = flowMonthCyMapper.getMemFlowMonth(page);
        page.setData(flowMonthCyList);
    }

	public void getFolwPage(Page page) {
		List<Map<String, Object>> flowLimitList = flowLimitMapper.getFolwPage(page);
		page.setData(flowLimitList);
	}
	
	/**
	 * 通用方式修改限速
	 * @param flowLimit
	 */
	public void updateLimit(FlowLimit flowLimit) {
		flowLimitMapper.updateByPrimaryKeySelective(flowLimit);
		redisHandler.del(RedisKey.MEM_LIMIT_FLOW_ID + flowLimit.getMemId());
	}
	
	/**
	 * 对过期用户做限速,
	 * @param userId
	 */
	public void updateExpireUserLimit(String userId) {
		/*FlowLimit flowLimit = new FlowLimit();
		flowLimit.setMemId(userId);
		//5M 换成 速度 除于 8
		Long limit = (long) 5242880 / 8;
		
		//游客限速
		String maxUpFlowLimit = Env.getVal("mem.guest.use.maxUpFlowLimit");
		
		//游客限速
		String maxDownFlowLimit = Env.getVal("mem.guest.use.maxDownFlowLimit");
				
		flowLimit.setMaxUpFlowLimit(StringUtils.isEmpty(maxUpFlowLimit) ? limit : Long.valueOf(maxUpFlowLimit) / 8);
		flowLimit.setMaxDownFlowLimit(StringUtils.isEmpty(maxDownFlowLimit) ? limit : Long.valueOf(maxUpFlowLimit) / 8);*/
		
		FlowLimit flowLimit = getDefLimitSpeed(userId);
		flowLimitMapper.updateByPrimaryKeySelective(flowLimit);
	}
	
	private FlowLimit getDefLimitSpeed(String userId) {
		FlowLimit flowLimit = new FlowLimit();
		flowLimit.setMemId(userId);
		//5M 换成 速度 除于 8
		Long limit = (long) 5242880 / 8;
		
		//游客限速
		String maxUpFlowLimit = Env.getVal("mem.guest.use.maxUpFlowLimit");
		
		//游客限速
		String maxDownFlowLimit = Env.getVal("mem.guest.use.maxDownFlowLimit");
				
		flowLimit.setMaxUpFlowLimit(StringUtils.isEmpty(maxUpFlowLimit) ? limit : Long.valueOf(maxUpFlowLimit) / 8);
		flowLimit.setMaxDownFlowLimit(StringUtils.isEmpty(maxDownFlowLimit) ? limit : Long.valueOf(maxUpFlowLimit) / 8);
		
		return flowLimit;
	}
	
	/**
	 * 对充值成功得用户做限速放开.
	 * @param userId
	 */
	public void updateRechargeUserLimit(String userId) {
		
		FlowLimitConfig config = flowLimitConfigService.getFlowLimitConfig(MemTypeEum.general_mem.getCode());

		FlowLimit flowLimit = new FlowLimit();
		flowLimit.setMemId(userId);
		//300M 换成 速度 除于 8
		Long limit = (long) 300 * 1024 * 1024 / 8;
		
		//游客限速,这里存得就是速度字节，不用除于8
		Long maxUpFlowLimit = config.getMonthUpFlowLimit();
		
		//游客限速
		Long maxDownFlowLimit = config.getMonthDownFlowLimit();
				
		flowLimit.setMaxUpFlowLimit(maxUpFlowLimit == null ? limit : maxUpFlowLimit);
		flowLimit.setMaxDownFlowLimit(maxDownFlowLimit == null ? limit : maxDownFlowLimit);
		
		flowLimitMapper.updateByPrimaryKeySelective(flowLimit);
	}
	
/*	public void updateLimit1(FlowLimit flowLimit) {
		flowLimitMapper.updateByPrimaryKeySelective(flowLimit);
		Map<String, Object> retnMap = new HashMap<>();
		retnMap.put("memId", flowLimit.getMemId());
		retnMap.put("maxUpFlowLimit", flowLimit.getMaxUpFlowLimit());
		retnMap.put("maxDownFlowLimit", flowLimit.getMaxDownFlowLimit());
		redisHandler.del(RedisKey.MEM_LIMIT_FLOW_ID + flowLimit.getMemId());
		// redisHandler.set(RedisKey.MEM_LIMIT_FLOW_ID + flowLimit.getMemId(), Response.ok(retnMap), 60);
	}*/

	/*public void guestFlowLimit(FlowLimit flowLimit) {
		// TODO Auto-generated method stub
		int i = flowLimitMapper.updateByPrimaryKeySelective(flowLimit);
		if(i == 0) {
			flowLimit.setCreateTime(new Date());
			flowLimitMapper.insert(flowLimit);
		}
		Map<String, Object> retnMap = new HashMap<>();
		retnMap.put("memId", flowLimit.getMemId());
		retnMap.put("maxUpFlowLimit", flowLimit.getMaxUpFlowLimit());
		retnMap.put("maxDownFlowLimit", flowLimit.getMaxDownFlowLimit());
		redisHandler.del(RedisKey.MEM_LIMIT_FLOW_ID + flowLimit.getMemId());
	}*/
	
}
