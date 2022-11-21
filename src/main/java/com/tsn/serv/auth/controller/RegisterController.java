package com.tsn.serv.auth.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.tools.ip.IpUtil;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.serv.auth.entity.ShareUser;
import com.tsn.serv.auth.entity.User;
import com.tsn.serv.auth.service.UserAuthService;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.utils.valid.ShareUtils;
	
@RestController
@RequestMapping("reg")
public class RegisterController {
	
	@Autowired
	private UserAuthService authService;
	
	@RequestMapping(value="mem")
	public Response<?> regMem(@RequestBody User user, HttpServletRequest request){
		String deviceNo = request.getHeader("D-NO");
		user.setDeviceNo(deviceNo);
		
		String ip = IpUtil.getIPAddress(request);
		user.setIp(ip);
		AccessToken accessToken = authService.regMem(user);
		return Response.ok(accessToken);
	}
	
	@RequestMapping(value="share/mem")
	public Response<?> regShareMem(@RequestBody ShareUser user, HttpServletRequest request){
		
		if (StringUtils.isEmpty(user.getShareUrl())) {
			throw new BusinessException(AuthCode.AUTH_PARAM_ERROR, "");
		}
		
		Map<String, String> result = ShareUtils.validSign(user.getShareUrl());
		
		if (result == null) {
			throw new BusinessException(AuthCode.AUTH_PARAM_ERROR, "share url is be Tampering!");
		}
		
		user.setParenInviCode(result.get("ivCode"));
		
		String ip = IpUtil.getIPAddress(request);
		user.setIp(ip);
		authService.regMem(user);
		return Response.ok();
	}
	
	
	@RequestMapping(value="v2/mem")
	public Response<?> regMemV2(@RequestBody User user, HttpServletRequest request){
		
		String ip = IpUtil.getIPAddress(request);
		user.setIp(ip);
		
		String deviceNo = request.getHeader("D-NO");
		user.setDeviceNo(deviceNo);
		
		AccessToken accessToken = authService.regMemV2(user);
		
		return Response.ok(accessToken);
	}
	
	@RequestMapping(value="v2/share/mem")
	public Response<?> regShareMemV2(@RequestBody ShareUser user, HttpServletRequest request){
		
		if (StringUtils.isEmpty(user.getShareUrl())) {
			throw new BusinessException(AuthCode.AUTH_PARAM_ERROR, "");
		}
		
		if (StringUtils.isEmpty(user.getSmsCode())) {
			throw new BusinessException(AuthCode.AUTH_PARAM_ERROR, "user input sms code is null");
		}
		
		Map<String, String> result = ShareUtils.validSign(user.getShareUrl());
		
		if (result == null) {
			throw new BusinessException(AuthCode.AUTH_PARAM_ERROR, "share url is be Tampering!");
		}
		
		String ip = IpUtil.getIPAddress(request);
		user.setIp(ip);
		
		user.setParenInviCode(result.get("ivCode"));
		authService.regMemV2(user);
		return Response.ok();
	}

}
