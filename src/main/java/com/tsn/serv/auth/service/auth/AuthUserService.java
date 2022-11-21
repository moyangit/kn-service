package com.tsn.serv.auth.service.auth;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.serv.auth.entity.auth.AuthUser;
import com.tsn.serv.auth.mapper.auth.AuthUserMapper;
import com.tsn.serv.common.cons.redis.RedisKey;

@Service
public class AuthUserService {
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private AuthUserMapper authUserMapper;
	
	public String queryAuthUser(String code, String authType) {
		
		String authUserStr = (String) redisHandler.get(RedisKey.AUTH_USER_SECRET + code);
		
		if (StringUtils.isEmpty(authUserStr)) {
			
			AuthUser authUser = authUserMapper.selectByPrimaryKey(code, authType);
			
			String authUserJson = JsonUtils.objectToJson(authUser);
			
			redisHandler.set(RedisKey.AUTH_USER_SECRET + code, authUserJson);
			
			return authUserJson;
		}
		
		return authUserStr;
		
		
	}

}
