package com.tsn.serv.mem.service.mem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.mem.entity.mem.MemExtInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.mem.MemExtInfoMapper;

/**
 * <p>
 * 用户留存统计 服务实现类
 * </p>
 *
 * @author Tang longsen
 * @since 2022-06-15
 */
@Service
public class MemExtInfoService extends ServiceImpl<MemExtInfoMapper, MemExtInfo>{
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private MemService memService;
	
	public boolean isOldUser(String memId) {
		//如果memExtInfo == null 说明是老用户
		/*MemExtInfo memExtInfo = super.getById(memId);
		if (memExtInfo == null) {
			return true;
		}*/
		return false;
	}
	
	public MemInfo getMemInfoByCacheDeviceId(String deviceNo) {
		
		if (StringUtils.isEmpty(deviceNo)) {
			return null;
		}
		
		MemInfo memInfoTmp = (MemInfo) redisHandler.get(RedisKey.USER_DEVICE_NO_USERINFO + deviceNo);
		
		if (memInfoTmp != null) {
			return memInfoTmp;
		}
		
		QueryWrapper<MemExtInfo> queryWrapper = new QueryWrapper<MemExtInfo>();
		queryWrapper.eq("device_no", deviceNo);
		MemExtInfo memExtInfo = super.getOne(queryWrapper);
		if (memExtInfo == null) {
			return null;
		}
		
		MemInfo memInfo = memService.queryMemById(memExtInfo.getId());
		
		if (memInfo == null) {
			return null;
		}
		
		if (MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType()) ) {
			memInfo.setMemPhone("游客" + (memInfo.getMemId().substring(memInfo.getMemId().length() - 5)));
		}
		
		//一天过期时间
		redisHandler.set(RedisKey.USER_DEVICE_NO_USERINFO + deviceNo, memInfo, 1 * 24 * 60 * 60);
		
		return memInfo;
		
	}
	
	
	public MemInfo getMemInfoByDeviceId(String deviceNo) {
		
		if (StringUtils.isEmpty(deviceNo)) {
			return null;
		}
		
		QueryWrapper<MemExtInfo> queryWrapper = new QueryWrapper<MemExtInfo>();
		queryWrapper.eq("device_no", deviceNo);
		MemExtInfo memExtInfo = super.getOne(queryWrapper);
		if (memExtInfo == null) {
			return null;
		}
		
		MemInfo memInfo = memService.queryMemById(memExtInfo.getId());
		
		if (memInfo == null) {
			return null;
		}
		
		if (MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType()) ) {
			memInfo.setMemPhone("游客" + (memInfo.getMemId().substring(memInfo.getMemId().length() - 5)));
		}
		
		
		return memInfo;
		
	}

}
