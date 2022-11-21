package com.tsn.serv.common.cons.sys;

import org.springframework.util.StringUtils;

import com.tsn.common.utils.web.utils.env.Env;

/**
 * 模块:业务类型：key-value
 * @author work
 *
 */
public class SysCons {
	
	public static final int ONLINE_TIME = 5;
	
	public static String getPlatType() {
		String suffix = Env.getVal("plat.redis.suffix");
		
		return StringUtils.isEmpty(suffix) ? "HB" : suffix;
	}
	
	public static String getPlatRedisSuffix() {
		
		String suffix = Env.getVal("plat.redis.suffix");
		
		return StringUtils.isEmpty(suffix) ? "" : suffix + ":";
	}
	
	
}
