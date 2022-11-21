package com.tsn.serv.mem.controller.mem;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.tsn.serv.common.enm.mem.AccRecordTypeEum;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.service.account.MemAccountService;
import com.tsn.serv.mem.service.mem.MemService;

@RestController
@RequestMapping("acc")
public class AccountController {
	
	@Autowired
	private MemAccountService memAccountService;
	
	@Autowired
	private MemService memService;
	
	@GetMapping
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getAcc() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		//获取返现状态成功的记录
		MemAccount memAccount = memAccountService.queryMemAccountByMemId(userId);
		
		return Response.ok(memAccount);
	}
	
	@GetMapping("/record/page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getAccRecordPage(Page page) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		//获取返现状态成功的记录
		MemAccount memAccount = memAccountService.queryMemAccountByMemId(userId);
		page.setParamObj("accountNo", memAccount.getAccountNo());
		page.setParamObj("recordStatus", "1");
		//设置流水类型为返现流水
		page.setParamObj("recordType", AccRecordTypeEum.sys_rebate.getCode());
		memAccountService.queryMemAccountRecordPage(page);
		
		return Response.ok(page);
	}

	/**
	 *返现详情列表展示
	 * @param page
	 * @return
	 */
	@GetMapping("/page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getPcAccRecordPage(Page page) {
		memAccountService.getPcAccRecordPage(page);
		return Response.ok(page);
	}

	/**
	 * 运营后台
	 * 根据手机号查询返利信息
	 * @param page
	 * @return
	 */
	@GetMapping("/rebate/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getRebateOrder(Page page) {
		memAccountService.getRebateOrder(page);
		return Response.ok(page);
	}
	
	/**
	 * 获取总收益和昨日收益
	 * @return
	 */
	@GetMapping("/record/sum")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getAccRecordSum() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		Map<String,Object> retnMap = memAccountService.getAccRecordSum(userId);
		return Response.ok(retnMap);
	}
}
