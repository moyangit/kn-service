package com.tsn.serv.mem.controller.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.cons.redis.RedisKey;

@Controller
@RequestMapping("redirect")
public class PageRedirectController {
	
	@Autowired
	private RedisHandler redisHandler;
	
	@GetMapping("/share")
	@ResponseBody
	@AuthClient(client = AuthEnum.bea_us)
    public Response<?> toSharePage(){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String token = (String) jwtInfo.getExt("token");
		
		//缓存1个小时
		redisHandler.set(RedisKey.USER_TOKEN + userId, token, 1 * 60 * 60);
		
		String addr = Env.getVal("serv.share.addr");
		
		String cusServiceUrl = StringUtils.isEmpty(addr) ? "https://user.kuainiaojsq.xyz/page/invitfriend.html?uId=" + userId : addr + userId;
		
		if ("dev".equals(Env.getVal("spring.profiles.active"))){
			cusServiceUrl = "http://localhost:9077/page/invitfriend.html?uId=" + userId;
		}
		
		return Response.ok(cusServiceUrl);
	}
	
	@GetMapping("/msg")
	@ResponseBody
	@AuthClient(client = AuthEnum.bea_us)
    public Response<?> toMsgPage(){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			//throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
			userId = "";
		}
		
		String token = (String) jwtInfo.getExt("token");
		
		if (!StringUtils.isEmpty(userId)) {
			//缓存1个小时
			redisHandler.set(RedisKey.USER_TOKEN + userId, token, 1 * 60 * 60);
		}
		
		String addr = Env.getVal("serv.msgcenter.addr");
		
		String cusServiceUrl = StringUtils.isEmpty(addr) ? "https://user.kuainiaojsq.xyz/page/msgcenter.html?uId=" + userId : addr + userId;
		
		if ("dev".equals(Env.getVal("spring.profiles.active"))){
			cusServiceUrl = "http://localhost:9077/page/msgcenter.html?uId=" + userId;
		}
		
		return Response.ok(cusServiceUrl);
	}
	
	@GetMapping("/customer")
	@ResponseBody
    public Response<?> toCustomer(){
		
		String addr = Env.getVal("serv.customer.addr");
		
		if (!StringUtils.isEmpty(addr)) {
			return Response.ok(addr);
		}
		
		String cusServiceUrl = "https://user.kuainiaojsq.xyz/page/linkcustomer.html";
		
		return Response.ok(cusServiceUrl);
	}
	
	@GetMapping("/proxy")
	@ResponseBody
    public Response<?> proxy(){
		
		String addr = Env.getVal("serv.proxy.addr");
		
		if (!StringUtils.isEmpty(addr)) {
			return Response.ok(addr);
		}
		
		String cusServiceUrl = "https://user.kuainiaojsq.xyz/page/subscribe.html";
		
		return Response.ok(cusServiceUrl);
	}

}
