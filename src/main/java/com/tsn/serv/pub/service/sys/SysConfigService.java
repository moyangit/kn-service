package com.tsn.serv.pub.service.sys;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.serv.pub.entity.sys.SysConfig;
import com.tsn.serv.pub.mapper.sys.SysConfigMapper;

/**
 * <p>
 * 用户留存统计 服务实现类
 * </p>
 *
 * @author Tang longsen
 * @since 2022-06-15
 */
@Service
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig>{
	
	@Autowired
	private RedisHandler redisHandler;
	
	public BigDecimal getUsdRate() {
		
		QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<SysConfig>();
		queryWrapper.eq("conf_key", "usdRate");
		
		SysConfig sysConfig = super.getOne(queryWrapper);
		
		if (sysConfig == null || StringUtils.isEmpty(sysConfig.getConfVal())) {
			return new BigDecimal("0.1394");
		}
		
		return new BigDecimal(sysConfig.getConfVal());
		
	}

}
