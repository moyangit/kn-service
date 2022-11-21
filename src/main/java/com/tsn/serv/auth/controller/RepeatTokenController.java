package com.tsn.serv.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.core.norepeat.impl.AtomiRedisNoRepeatService;
import com.tsn.common.utils.web.entity.Response;

@RestController
@RequestMapping("reToken")
public class RepeatTokenController {
	
	@Autowired
	private AtomiRedisNoRepeatService atomiRedisNoRepeatService;
	
	@RequestMapping
	public Response<?> validUserBySms(){
		String reToken = atomiRedisNoRepeatService.geneToken();
		return Response.ok(reToken);
	}

}
