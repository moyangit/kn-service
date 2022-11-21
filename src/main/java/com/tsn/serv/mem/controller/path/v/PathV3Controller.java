package com.tsn.serv.mem.controller.path.v;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.enm.v2ray.PathCodeEnum;
import com.tsn.serv.mem.service.path.PathService;

@RestController
@RequestMapping("v3/path")
public class PathV3Controller {
	
	@Autowired
	private PathService pathService;
	
	@GetMapping("/default")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> defaultPath(boolean global, String dType, String dId, boolean isEncrypt) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		String tokenType = info.getTokenType();
		
		Map<String, Object> result = pathService.getPathByCodeV3(PathCodeEnum.HK.name(), userId, !global, dType, dId, isEncrypt,tokenType);
		return Response.ok(result);
	}
	
	@GetMapping("/{code}")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> getPathByCode(@PathVariable String code, boolean global, String dType, String dId, boolean isEncrypt) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		String tokenType = info.getTokenType();
		
		Map<String, Object> result = pathService.getPathByCodeV3(code, userId, !global, dType, dId,isEncrypt,tokenType);
		return Response.ok(result);
	}
	
	 

}
