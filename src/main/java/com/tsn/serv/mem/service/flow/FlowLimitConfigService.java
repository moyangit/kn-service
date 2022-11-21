package com.tsn.serv.mem.service.flow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.mem.entity.flow.FlowLimitConfig;
import com.tsn.serv.mem.mapper.flow.FlowLimitConfigMapper;

@Service
public class FlowLimitConfigService {
	
	@Autowired
	private FlowLimitConfigMapper flowLimiConfigMapper;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@SuppressWarnings("unchecked")
	public FlowLimitConfig getFlowLimitConfig(String memType) {
		
		List<FlowLimitConfig> flowLimitList = (List<FlowLimitConfig>) redisHandler.get(RedisKey.MEM_TYPE_LIMIT_FLOW);
		
		if (flowLimitList == null || flowLimitList.isEmpty()) {
			flowLimitList =  flowLimiConfigMapper.selectFlowLimitAll();
			redisHandler.set(RedisKey.MEM_TYPE_LIMIT_FLOW, flowLimitList, 300);
		}
		
		for (FlowLimitConfig config : flowLimitList) {
			
			if (config.getMemType().equals(memType)) {
				return config;
			}
			
		}
		
		//默认50M
		long monthDownFlowLimit = 50 * 1024 * 1024 / 8;
		long monthUpFlowLimit = 50 * 1024 * 1024 / 8;
		
		FlowLimitConfig config = new FlowLimitConfig();
		config.setMonthUpFlowLimit(monthUpFlowLimit);
		config.setMonthDownFlowLimit(monthDownFlowLimit);
		return config;
	}
	
	public void limitConfigList(Page page) {
		List<FlowLimitConfig> flowLimitConfigList = flowLimiConfigMapper.limitConfigList(page);
		page.setData(flowLimitConfigList);
	}

	public void updateLimitConfig(FlowLimitConfig flowLimitConfig) {
		flowLimiConfigMapper.updateByPrimaryKeySelective(flowLimitConfig);
		redisHandler.del(RedisKey.MEM_TYPE_LIMIT_FLOW);
	}
}
