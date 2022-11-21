package com.tsn.serv.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.auth.service.UserAuthService;

@RestController
@RequestMapping("sms")
public class SmsController {
	
	@Autowired
	private UserAuthService authService;
	
	/**
	 * 上传参数直接调用发送短信
	 * type login， reg
	 * @param ValiCodeSms
	 * @return
	 */
	@RequestMapping(value="/send/{type}/{phone}")
	public Response<?> validUserBySms(@PathVariable String phone, @PathVariable String type){
		String resultCode = authService.sendSMSCode(phone, type);
		//临时加在这里
		return Response.ok(resultCode);
	}
	
}
