package com.tsn.serv.mem.controller.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.mem.entity.flow.FlowLimit;
import com.tsn.serv.mem.entity.flow.FlowLimitConfig;
import com.tsn.serv.mem.service.flow.FlowLimitConfigService;
import com.tsn.serv.mem.service.flow.FlowLimitService;

@RestController
@RequestMapping("flow")
public class FlowDataController {
	
	@Autowired
	private FlowLimitService flowLimitService;

	@Autowired
	private FlowLimitConfigService flowLimitConfigService;
	
	@GetMapping("curr")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> getFlow() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		FlowLimit flowLimit = flowLimitService.getCurrMemFlow(userId);
		return Response.ok(flowLimit);
		
	}

	/**
	 * 限速设置列表展示
	 * @param page
	 * @return
	 */
	@GetMapping("/limitConfigPage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> limitConfigList(Page page) {
		flowLimitConfigService.limitConfigList(page);
		return Response.ok(page);
	}

	/**
	 * 限速设置修改
	 * @param flowLimitConfig
	 * @return
	 */
	@PutMapping("/updateLimitConfig")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateLimitConfig(@RequestBody FlowLimitConfig flowLimitConfig) {
		flowLimitConfigService.updateLimitConfig(flowLimitConfig);
		return Response.ok();
	}

	@GetMapping("/day")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> memFlowDay(Page page) {
		flowLimitService.getMemFlowDay(page);
		return Response.ok(page);
	}

	@GetMapping("/month")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> memFlowMonth(Page page) {
		flowLimitService.getMemFlowMonth(page);
		return Response.ok(page);
	}

	/**
	 * 用户限速信息列表查询
	 * @param page
	 * @return
	 */
	@GetMapping("/folwPage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> folwPage(Page page) {
		flowLimitService.getFolwPage(page);
		return Response.ok(page);
	}

	/**
	 * 用户限速信息修改
	 * @param flowLimit
	 * @return
	 */
	@PutMapping("/limit")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateLimit(@RequestBody FlowLimit flowLimit) {
		flowLimitService.updateLimit(flowLimit);
		return Response.ok();
	}
	
}
