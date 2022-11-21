package com.tsn.serv.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.utils.tools.regx.RegxValidUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.web.auth.AuthCode;
import com.tsn.serv.auth.service.UserAuthService;

@RestController
@RequestMapping("valid")
public class MsgValidController {
	
	@Autowired
	private UserAuthService authService;
	
	/**
	 * 上传参数直接调用发送短信
	 * type login， reg
	 * @param ValiCodeSms
	 * @return
	 */
	@RequestMapping(value="/code/{type}")
	public Response<?> validUserBySms(String name, @PathVariable String type){
		
		if (RegxValidUtils.checkEmail(name)) {
			String code = authService.sendSMSEmail(name, type);
			return Response.ok(code);
		}
		
		if (RegxValidUtils.checkPhone(name)) {
			String code = authService.sendSMSCode(name, type);
			return Response.ok(code);
		}
		
		//参数错误
		return Response.retn(AuthCode.AUTH_PARAM_ERROR);
	}
	
}
