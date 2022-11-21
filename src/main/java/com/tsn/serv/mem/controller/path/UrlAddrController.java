/*package com.tsn.serv.mem.controller.path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;

@RestController
@RequestMapping("official")
public class UrlAddrController {

	@GetMapping("addrs")
	public Response<?> addrs(){
		String addr = Env.getVal("official.url.addrs");
		String[] addrs = addr.split(",");
		return Response.ok(addrs);
	}
	
	@GetMapping("addr")
	public Response<?> addr(){
		String addr = Env.getVal("official.url.addr");
		return Response.ok(addr);
	}
	
}
*/