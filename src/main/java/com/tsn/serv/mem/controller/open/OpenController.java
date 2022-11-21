package com.tsn.serv.mem.controller.open;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.mem.common.limit.LimitCacheManager;
import com.tsn.serv.mem.common.limit.LimitCacheManager.LimitStatus;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.service.mem.MemService;


@RestController
@RequestMapping("open")
public class OpenController {
	
	@Autowired
	private MemService memService;

	/**
	 *	提供给代理商修改会员信息和修改会员到期日期生成订单的
	 * @param reqJson
	 * @return
	 */
	@PostMapping("/order")
	public Response<?> upMemOrder(@RequestBody JSONObject reqJson) {
		memService.upMemOrder(reqJson);
		return Response.ok();
	}

	/**
	 * 提供给代理商新增会员信息
	 * @param memInfo
	 * @return
	 */
	@PostMapping("/mem")
	public Response<?> adMem(@RequestBody MemInfo memInfo) {
		memService.addMem(memInfo);
		return Response.ok();
	}
	
	@GetMapping("/limit/users")
	public Response<?> getFlowLimitUserInfo(){
		Cache<String, LimitStatus> limitUserCache = LimitCacheManager.build().getUserLimitCache();
		return Response.ok(limitUserCache.asMap());
	}
}
