package com.tsn.serv.pub.service.ad;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.common.cons.redis.RedisKey;

@Service
public class AdStatisService {
	
	@Autowired
	private RedisHandler redisHandler;
	
	public void statisClick(String adId, String uId) {
		
		String yyyMMdd = DateUtils.getCurrYMD("yyyyMMdd");
		
		redisHandler.pfAdd(RedisKey.AD_DAY_CLICK + yyyMMdd + ":" + adId, new Date().getTime());
		
		redisHandler.pfAdd(RedisKey.AD_DAY_USER + yyyMMdd + ":" + adId, uId);
		
	}
	
	
	public void statisShow(String adId) {
		
		String yyyMMdd = DateUtils.getCurrYMD("yyyyMMdd");
		
		redisHandler.pfAdd(RedisKey.AD_DAY_SHOW + yyyMMdd + ":" + adId, new Date().getTime());
		
	}
	
	/**
	 * 获取一个广告 每天的广告数据
	 * @param month yyyyMM
	 * @return
	 */
	public List<Map<String, String>> getAdStatisListByAd(String adId, String month) {
		
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		int maxDate = DateUtils.getMaxDayNumByMonth(month);
		
		for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
			Map<String, String> dataMap = new HashMap<String, String>();
			String day = month + (dateNum < 10 ? "0" + dateNum : dateNum);
			dataMap.put("day", day);
			
			long clickCount = redisHandler.pfcount(RedisKey.AD_DAY_CLICK + day + ":" + adId);
			dataMap.put("clickCount", String.valueOf(clickCount));
			
			long userCount = redisHandler.pfcount(RedisKey.AD_DAY_USER + day + ":" + adId);
			dataMap.put("userCount", String.valueOf(userCount));
			
			long showCount = redisHandler.pfcount(RedisKey.AD_DAY_SHOW + day + ":" + adId);
			dataMap.put("showCount", String.valueOf(showCount));
			result.add(dataMap);
		}
		
		return result;
	}
	

}
