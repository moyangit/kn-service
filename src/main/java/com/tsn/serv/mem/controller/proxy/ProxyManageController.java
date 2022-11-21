package com.tsn.serv.mem.controller.proxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.norepeat.NoRepeatException;
import com.tsn.common.core.norepeat.impl.AtomiRedisNoRepeatService;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.charge.MemCharge;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.reqData.ProxyBingdingDto;
import com.tsn.serv.mem.entity.reqData.ProxyRechargeDto;
import com.tsn.serv.mem.service.proxy.ProxyService;

/**
 * 代理后台管理系统
 * @author admin
 *
 */
@RestController
@RequestMapping("proxyManage")
public class ProxyManageController {

	@Autowired
	private ProxyService proxyService;

	@Autowired
	private AtomiRedisNoRepeatService atomiRedisNoRepeatService;

	/**
	 * 查询代理账户余额
	 * @param memId
	 * @return
	 */
	@GetMapping("getAccountMoney")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> getAccountMoney(String memId){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		MemAccount memAccount = proxyService.getAccountMoney(userId);
		return Response.ok(memAccount);
	}

	/**
	 * 获取列表table数据
	 * @param page
	 * @return
	 */
	@GetMapping("page")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> getTableList(Page page){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		page.setParamObj("memId", userId);
		proxyService.getTableList(page);
		return Response.ok(page);
	}

	/**
	 * 根据被充值用户类型，获取对应的资费类型
	 * @param memType
	 * @return
	 */
	@GetMapping("getChargeType")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> getChargeType(String memType){
		List<MemCharge> chargeList = proxyService.getChargeType(memType);
		return Response.ok(chargeList);
	}

	/**
	 * 修改用户备注
	 * @param memInfo
	 * @return
	 */
	@PutMapping("updateRemarks")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> updateRemarks(@RequestBody MemInfo memInfo){
		proxyService.updateRemarks(memInfo);
		return Response.ok();
	}

	@GetMapping("/reToken")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> getOrderReToken() {
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		String token = atomiRedisNoRepeatService.geneToken();
		return Response.ok(token);
	}

	/**
	 * 代理为下级充值
	 * @param proxyRechargeDto
	 * @return
	 */
	@PutMapping("recharge")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> recharge(@RequestBody ProxyRechargeDto proxyRechargeDto){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		proxyRechargeDto.setProxyMemId(userId);
		atomiRedisNoRepeatService.validRepeat(proxyRechargeDto.getReToken());
		proxyService.recharge(proxyRechargeDto);
		return Response.ok();
	}
	
	@PostMapping("recharge")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> rechargePost(@RequestBody ProxyRechargeDto proxyRechargeDto){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		proxyRechargeDto.setProxyMemId(userId);
		try {
			atomiRedisNoRepeatService.validRepeat(proxyRechargeDto.getReToken());
		} catch (NoRepeatException e) {
			throw new BusinessException(e.getCode(), e.getMessage());
		}
		proxyService.recharge(proxyRechargeDto);
		return Response.ok();
	}

	/**
	 * 绑定用户
	 * @param proxyBingdingDto
	 * @return
	 */
	@PutMapping("bingding")
	@AuthClient(client = AuthEnum.pxy_us)
	public Response<?> bingding(@RequestBody ProxyBingdingDto proxyBingdingDto){
		proxyService.bingding(proxyBingdingDto);
		return Response.ok();
	}
}
