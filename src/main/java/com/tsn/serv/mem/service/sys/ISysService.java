package com.tsn.serv.mem.service.sys;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tsn.common.utils.web.entity.Response;

@FeignClient(name = "hb-service", url="${feign.client.url.addr}")  
public interface ISysService {
	
	@RequestMapping(value="node/private/key",method=RequestMethod.GET)
	public Response<?> getNodeKey();

}
