package com.tsn.serv.pub.controller.domains;
import java.util.Arrays;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;

@RestController
@RequestMapping("domains")
public class DomainsController {

	@RequestMapping()
	public Response<?> getDomains() {
		
		String addr = Env.getVal("domain.api.addrs");
		
		String[] addrArr = addr.split(",");
		
		return Response.ok(Arrays.asList(addrArr));
	}
}