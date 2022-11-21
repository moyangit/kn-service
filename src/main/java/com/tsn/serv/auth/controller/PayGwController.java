package com.tsn.serv.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;

@RestController
@RequestMapping("pay/callback")
public class PayGwController {
	
	@RequestMapping("alipay/h5")
	public Response<?> alipayCallback(){
		
		return Response.ok();
	}

}
