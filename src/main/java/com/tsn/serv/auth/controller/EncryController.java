/*package com.tsn.serv.auth.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.mem.entity.mem.MemInfo;

@RestController
@RequestMapping("encry")
public class EncryController {
	
	@GetMapping("key")
	public Response<?> getEncry(){
		return Response.ok(Env.getVal("encry.v2.path.key"));
	}
	
	@FeignClient(name = "v-mem")  
	public interface IMemFeignService {
		
		@RequestMapping(value="mem/private/phone/{phone}",method=RequestMethod.GET)
		public Response<MemInfo> selectUserByPhone(@PathVariable("phone") String phone);

	}

}
*/