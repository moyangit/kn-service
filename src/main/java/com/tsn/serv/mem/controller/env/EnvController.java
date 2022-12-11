package com.tsn.serv.mem.controller.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.mem.service.env.EnvParamsService;
import com.tsn.serv.pub.common.down.DownManager;

@RestController
@RequestMapping("env")
public class EnvController {

	@Autowired
	private EnvParamsService envParamsService;
	
	@GetMapping("/refresh")
	public Response<?> refresh(String dId) {
		envParamsService.clearParams();
		return Response.ok();
	}
	
	@GetMapping("/refresh/download")
	public Response<?> refreshDownLoad(String dId) {
		DownManager.build().refreshData();
		return Response.ok();
	}
}