package com.tsn.serv.mem.controller.path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.enm.v2ray.PathCodeEnum;
import com.tsn.serv.mem.entity.mem.MemInfoConfig;
import com.tsn.serv.mem.entity.node.NodePath;
import com.tsn.serv.mem.service.mem.MemInfoConfigService;
import com.tsn.serv.mem.service.path.PathService;

@RestController
@RequestMapping("path")
public class PathController {
	
	@Autowired
	private PathService pathService;
	
	@Autowired
	private MemInfoConfigService memInfoConfigService;
	
	@GetMapping("all")
	@AuthClient()
	public Response<?> access() {
		
		JwtInfo info = JwtLocal.getJwt();
		String userId = info == null ? null : info.getSubject();
		List<NodePath> pathInfoList = pathService.getPathInfosByUserId(userId);
		return Response.ok(pathInfoList);
	}
	
	@GetMapping("/default")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> defaultPath(boolean global, String dType) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		Map<String, Object> result = pathService.getPathByCode(PathCodeEnum.HK.name(), userId, !global, dType);
		return Response.ok(result);
	}
	
	@GetMapping("/{code}")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> getPathByCode(@PathVariable String code, boolean global, String dType) {
		
		JwtInfo info = JwtLocal.getJwt();

		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		Map<String, Object> result = pathService.getPathByCode(code, userId, !global, dType);
		return Response.ok(result);
	}
	
	@GetMapping("/config")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> subClientPatConfig(String cid, HttpServletRequest request) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		String subAddr = Env.getVal("path.sub.addr");
		
		//350000是福建省，主要是泉州用户
		if (!StringUtils.isEmpty(cid) && cid.contains("350")) {
			String subAddrTmp = Env.getVal("path.sub.addr.backup");
			if (!StringUtils.isEmpty(subAddrTmp)) {
				subAddr = subAddrTmp;
			}
		}
		
		String[] addrs = subAddr.split(",");
		
		
		MemInfoConfig memInfoConfig = memInfoConfigService.getMemConfigAndAddByMemId(userId);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("SR", "/path/sub/all/srvmess/" + memInfoConfig.getSubKey());
		result.put("General", "/path/sub/all/generalvmess/" + memInfoConfig.getSubKey());
		result.put("domains", Arrays.asList(addrs));
		return Response.ok(result);
	}
	
	@GetMapping("/sub/all/{clientType}/{userId}")
	public String subClientPath(@PathVariable String clientType, @PathVariable String userId) {
		String result = pathService.getSubLinkAll(userId, clientType);
		return result;
	}
	
	@GetMapping("/sub/{clientType}/{pathCode}/{userId}")
	public String subClientPathByCode(@PathVariable String clientType, @PathVariable String pathCode, @PathVariable String userId) {
		
		//Map<String, Object> result = pathService.getPathByCode(PathCodeEnum.HK.name(), userId, !global, dType);
		String result = pathService.getSubLinkByCode(userId, clientType, pathCode);
		return result;
	}

}
