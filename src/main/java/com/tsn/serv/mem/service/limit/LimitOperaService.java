package com.tsn.serv.mem.service.limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.mem.common.limit.LimitCacheManager;
import com.tsn.serv.mem.common.limit.LimitCacheManager.LimitStatus;

@Service
public class LimitOperaService {
	
	private Logger log = LoggerFactory.getLogger(LimitOperaService.class);
	
	@Autowired
	private RedisHandler redisHander;
	
	//19点00分钟00秒
	private long limitStartTime = 170000;
	
	//24点00分钟00秒,改成 01点00分钟00秒
	private long limitEndTime = 10000;
	
	//一段时间内使用的总流量，超出就限速
	private long maxLimitFlowVal = 2 * 1024 * 1024 * 1024l;
	
	private int limitMins = 15;
	
	public LimitOperaService() {
		try {
			
			String limitStartTime = Env.getVal("limit.flow.start.time");
			String limitEndTime = Env.getVal("limit.flow.end.time");
			String maxLimitFlowVal = Env.getVal("limit.flow.total.val");
			String limitMins = Env.getVal("limit.flow.limit.time");
			
			if (!StringUtils.isEmpty(limitStartTime)) {
				this.limitStartTime = Long.valueOf(limitStartTime);
			}
			
			if (!StringUtils.isEmpty(limitEndTime)) {
				this.limitEndTime = Long.valueOf(limitEndTime);
			}
			
			if (!StringUtils.isEmpty(maxLimitFlowVal)) {
				this.maxLimitFlowVal = Long.valueOf(maxLimitFlowVal);
			}
	
			if (!StringUtils.isEmpty(limitMins)) {
				this.limitMins = Integer.valueOf(limitMins);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void calcUserFlowAndLimit(String userId, long upFlowVal, long downFlowVal) {
		
		String userType = userId.substring(0, 1);
		//判断是否游客,如果是游客，则不参与限速，已经限速到最低
		if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType))  {
			return;
		}
		
		long currHourTime = Long.valueOf(DateUtils.getCurrYMD("HHmmss"));
		
		boolean condition = false;
		
		if (limitEndTime >= limitStartTime) {
			condition = (currHourTime >= limitStartTime && currHourTime <= limitEndTime);
		}else {
			condition = (currHourTime >= limitStartTime || currHourTime <= limitEndTime);
		}
		
		
		if (condition) {
			
			long totalFlowVal = upFlowVal + downFlowVal;
			
			//判断这个用户的key是否存在，如果不存在表示过期 或者没创建
			//如果不存在，设置0，然后，过期时间设置30分钟
			if (!redisHander.hasKey(RedisKey.USER_FLOW_LIMIT + userId)) {
				redisHander.set(RedisKey.USER_FLOW_LIMIT + userId, totalFlowVal, limitMins * 60);
			}else {
				long result = redisHander.incr(RedisKey.USER_FLOW_LIMIT + userId, totalFlowVal);
				
				//如果超过就限速
				if (result > maxLimitFlowVal && !LimitCacheManager.build().isLimit(userId)) {
					
					//获取可用的中转信息，然后获取调用中转的接口，进行限速
					LimitCacheManager.build().put(userId, new LimitStatus(result, maxLimitFlowVal));
					//Set<String> proxyIps = LimitCacheManager.build().getProxyServers();
					
					//限速成功后,删除，重新按周期30分钟进行计算
					redisHander.del(RedisKey.USER_FLOW_LIMIT + userId);
					
				}
			}
			
			
		}
		
	}
	
}


