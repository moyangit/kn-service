package com.tsn.serv.mem.controller.charge;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.mem.entity.charge.MemCharge;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.service.charge.MemChargeService;
import com.tsn.serv.mem.service.mem.MemService;

@RestController
@RequestMapping("charge")
public class MemChargeController {
	
	@Autowired
	private MemChargeService memChargeService;
	
	@Autowired
	private MemService memInfoService;
	
	@GetMapping("/{memType}")
	public Response<?> getChargeByMemType(@PathVariable String memType){
		List<MemCharge> chargeList = memChargeService.selectChargeByMemType(memType);
		return Response.ok(chargeList);
	}
	
	@GetMapping("type")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getChargeByType(@PathVariable String memType){
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		MemInfo memInfo = memInfoService.queryMemById(userId);
		List<MemCharge> chargeList = memChargeService.selectChargeByMemType(memInfo.getMemType());
		return Response.ok(chargeList);
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/mId")
	public Response<?> getChargeListByMemId(String memId){
		
		if (StringUtils.isEmpty(memId)) {
			List<MemCharge> chargeList = memChargeService.selectChargeByMemType("01");
			return Response.ok(chargeList);
		}
		
		MemInfo memInfo = memInfoService.queryMemById(memId);
		
		String memType = memInfo == null ?  "01" : memInfo.getMemType();
		
		List<MemCharge> chargeList = memChargeService.selectChargeByMemType(memType);
		return Response.ok(chargeList);
	}

	/**
	 * 运营管理列表展示
	 * @param page
	 * @return
	 */
	@GetMapping("/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getMenChargeList(Page page) {
		memChargeService.getMenChargeList(page);
		return Response.ok(page);
	}

	@PutMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateMenCharge(@RequestBody MemCharge memCharge) {
		memChargeService.updateCustomer(memCharge);
		return Response.ok();
	}
}
