package com.tsn.serv.auth.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.auth.service.auth.AuthUserService;
import com.tsn.serv.common.cons.api.ApiCons;

@RestController
@RequestMapping("auth" + ApiCons.PRIVATE_PATH)
public class AuthController {
	
	@Autowired
	private AuthUserService authUserService;
	
	@GetMapping("/client/{authCode}")
	public Response<?> getClientUser(@PathVariable String authCode){
		String authUser = authUserService.queryAuthUser(authCode, "client");
		return Response.ok(authUser);
	}
	
	@GetMapping("/user/{authCode}")
	public Response<?> getUserInfo(@PathVariable String authCode){
		String authUser = authUserService.queryAuthUser(authCode, "user");
		return Response.ok(authUser);
	}


}
