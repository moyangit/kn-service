package com.tsn.serv.mem.service.mem;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.mapper.mem.GuestInfoMapper;

@Service
public class MemGuestInfoService {
	
	@Autowired
	private GuestInfoMapper guestInfoMapper;
	
	@Autowired
	private RedisHandler redisHandler;
	
	private static Logger logger = LoggerFactory.getLogger(MemGuestInfoService.class);
	
	public void insertGuestInfo(GuestInfo guestInfo) {
		if (!StringUtils.isEmpty(guestInfo.getDeviceNo())) {
			String[] deivceInfoArr = guestInfo.getDeviceNo().split("-");
			guestInfo.setDeviceType(deivceInfoArr[0]);
		}
		guestInfo.setStatus("0");
		guestInfo.setRegDate(new Date());
		guestInfoMapper.insertByIgnore (guestInfo);
		
		//触发游客注册事件,统计总游客 按天统计
		EventMsgProducter.build().sendEventMsg(EventMsg.createGuestRegMsg(guestInfo.getMemId(), guestInfo.getRegDate()));
	}
	
	public GuestInfo getGuestByDeviceNo(String deviceNo) {
		return guestInfoMapper.getGuestByDeviceNo(deviceNo);
	}
	
	public GuestInfo getGuestById(String memId) {
		return guestInfoMapper.selectByPrimaryKey(memId);
	}
	
	public void updateGuestInfo(GuestInfo guestInfo) {
		guestInfoMapper.updateByPrimaryKeySelective(guestInfo);
	}
	
	public GuestInfo getCacheGuestById(String memId) {
		
		GuestInfo memInfo = (GuestInfo) redisHandler.get(RedisKey.USER_ID_USERINFO + memId);
		
		if (memInfo == null) {
			GuestInfo memInfoFromDB = guestInfoMapper.selectByPrimaryKey(memId);
			//设置过期30分钟
			redisHandler.set(RedisKey.USER_ID_USERINFO, memInfoFromDB, 30 * 60);
			
			return memInfoFromDB;
		}
		
		return memInfo;
		
	}
	
	public void syncRedis2Pf(String yyyyMMdd) {
		
		logger.info("----> syncRedis2Pf, yyyyMMdd = {} ", yyyyMMdd);
		
		try {
			boolean isExists = redisHandler.hasKey(RedisKey.GUEST_DAY + yyyyMMdd);
			
			if (!isExists) {
				return;
			}
			
			Set<Object> guestList = redisHandler.zRange(RedisKey.GUEST_DAY + yyyyMMdd, 0, -1);
			
			logger.info("syncRedis2Pf, guestList size = {}", guestList == null ? "null" : guestList.size());
			
			long num = 0;
			for (Object obj : guestList) {
				
				String deviceNo = (String) obj;
				
				String guestId = (String) redisHandler.get(deviceNo);
				
				Map<Object, Object> guestInfoMap = redisHandler.hGetMap(RedisKey.GUEST_INFO + guestId);
				
				if (guestInfoMap == null || guestInfoMap.isEmpty()) {
					continue;
				}
				
				redisHandler.pfAdd(RedisKey.GUEST_DAY_PF + yyyyMMdd, guestId);
				
				redisHandler.pfAdd(RedisKey.GUEST_TOTAL, guestId);
				
				//int result = guestInfoMapper.insertByIgnore(guestInfo);
				
				num++;
			}
			
			logger.info("syncRedis2Pf yyyyMMdd= {}, sysnc size = {}", yyyyMMdd , num);
		} catch (Exception e) {
			logger.error("{}",e);
		}
		
	}
	
	public void clearRedisGuest(String yyyyMMdd) {
		
		try {
			boolean isExists = redisHandler.hasKey(RedisKey.GUEST_DAY + yyyyMMdd);
			
			if (!isExists) {
				return;
			}
			
			Set<Object> guestList = redisHandler.zRange(RedisKey.GUEST_DAY + yyyyMMdd, 0, -1);
			
			
			long num = 0;
			for (Object obj : guestList) {
				
				String deviceNo = (String) obj;
				
				String guestId = (String) redisHandler.get(deviceNo);
				
				redisHandler.expire(RedisKey.GUEST_INFO + guestId, 1);
				
				redisHandler.expire(deviceNo, 1);
				
				num++;
			}
			
		} catch (Exception e) {
			logger.error("{}",e);
		}
		
	}
	
	@Transactional
	public void syncGuest2DB(String yyyyMMdd) {
		
		logger.info("----> syncGuest2DB, yyyyMMdd = {} ", yyyyMMdd);
		
		boolean isExists = redisHandler.hasKey(RedisKey.GUEST_DAY + yyyyMMdd);
		
		if (!isExists) {
			return;
		}
		
		Set<Object> guestList = redisHandler.zRange(RedisKey.GUEST_DAY + yyyyMMdd, 0, -1);
		
		logger.info("syncGuest2DB, guestList size = {}", guestList == null ? "null" : guestList.size());
		
		long num = 0;
		for (Object obj : guestList) {
			
			String deviceNo = (String) obj;
			
			String guestId = (String) redisHandler.get(deviceNo);
			
			Map<Object, Object> guestInfoMap = redisHandler.hGetMap(RedisKey.GUEST_INFO + guestId);
			
			GuestInfo guestInfo = map2GuestObj(guestInfoMap);
			
			int result = guestInfoMapper.insertByIgnore(guestInfo);
			
			if (result == 1) {
				num++;
			}
		}
		
		logger.info("syncGuest2DB yyyyMMdd= {}, sysnc size = {}", yyyyMMdd , num);
		
	}
	
	/**
	 * 根据时间段筛选数据同步到游客数据库表中
	 * @param yyyyMMdd
	 * @param startTime
	 * @param endTime
	 */
//	@Transactional
//	public void syncGuest2DBByHour(String yyyyMMdd, long startTime, long endTime) {
//		
//		logger.info("----> syncGuest2DBByHour , yyyyMMdd = {}, startTime = {}, endTime = {}", yyyyMMdd, startTime, endTime);
//		
//		Set<Object> guestList = redisHandler.zRangeByScore(RedisKey.GUEST_DAY + yyyyMMdd, startTime, endTime);
//		
//		logger.info("syncGuest2DBByHour guestList, size = {}", guestList == null ? "null" : guestList.size());
//		
//		long num = 0;
//		for (Object obj : guestList) {
//			
//			//这里获取的是reidis key的完整key，直接带入查询
//			String deviceNo = (String) obj;
//			
//			String guestId = (String) redisHandler.get(deviceNo);
//			
//			Map<Object, Object> guestInfoMap = redisHandler.hGetMap(RedisKey.GUEST_INFO + guestId);
//			
//			GuestInfo guestInfo = map2GuestObj(guestInfoMap);
//			
//			//logger.info("GuestInfo guestInfo = {}", guestInfo);
//			
//			int result = guestInfoMapper.insertByIgnore(guestInfo);
//			
//			if (result == 1) {
//				num++;
//			}
//		}
//		
//		logger.info("syncGuest2DBByHour yyyyMMdd= {}, sysnc size = {}", yyyyMMdd , num);
//		
//	}
	
	/**
	 * @param guestInfoMap
	 * @return
	 */
	private GuestInfo map2GuestObj(Map<Object, Object> guestInfoMap) {
		
		GuestInfo guestInfo = new GuestInfo();
		
		String memId = (String) guestInfoMap.get("memId");
		
		String memName = (String) guestInfoMap.get("memName");
		
		String memPhone = (String) guestInfoMap.get("memPhone");
		
		String deviceNo = (String) guestInfoMap.get("deviceNo");
		
		long suspenDate = (long) guestInfoMap.get("suspenDate");
		
		long regitDate = (long) guestInfoMap.get("regDate");
		
		guestInfo.setMemId(memId);
		guestInfo.setMemName(memName);
		guestInfo.setMemType("00");
		guestInfo.setRegDate(new Date(regitDate));
		guestInfo.setSuspenDate(new Date(suspenDate));
		guestInfo.setMemPhone(memPhone);
		guestInfo.setStatus("0");
		guestInfo.setDeviceNo(deviceNo);
		
		if (!StringUtils.isEmpty(deviceNo)) {
			String[] deivceInfoArr = deviceNo.split("-");
			guestInfo.setDeviceType(deivceInfoArr[0]);
		}
		
		return guestInfo;
		
	}
	
	

}
