package com.tsn.serv.mem.controller.credits;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.norepeat.impl.AtomiRedisNoRepeatService;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.code.credits.TaskExceptionCode;
import com.tsn.serv.mem.entity.credits.CreditsConvertOrder;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;
import com.tsn.serv.mem.service.credits.CreditsService;

/**
 * 积分兑换
 * @author work
 *
 */
@RestController
@RequestMapping("credits")
public class CreditsController {
	
	@Autowired
	private CreditsService creditsService;
	
	@Autowired
	private AtomiRedisNoRepeatService atomiRedisNoRepeatService;
	
	private static final String SALT = "db908c3f";

	/**
	 * 根据memId获取积分信息
	 * @param memId
	 * @return
	 */
	@GetMapping("byMem")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getCreditsByMemId(String memId){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		if (!StringUtils.isEmpty(userId)) {
			memId = userId;
		}
		return Response.ok(creditsService.selectCreditsByMemId(memId));
	}
	
	@GetMapping("my")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getCreditsByMy(){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		return Response.ok(creditsService.selectCreditsByMemId(userId));
	}
	
	/**
	 * 积分兑换
	 * @param credits
	 * @return
	 */
	@PostMapping("exchange")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> creditsExchange(@RequestBody CreditsConvertOrder credits){
		
		atomiRedisNoRepeatService.validRepeat(credits.getReToken());
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		creditsService.creditsExchange(credits);
		
		return Response.ok();
	}
	
	/**
	 * 确认发放积分
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("confirm/{sign}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> creditsConfirmAdd(@RequestBody CreditsTaskOrder creditsTaskOrder,@PathVariable("sign") String sign){
		//分享朋友圈和谷歌商店评价由后台管理页面返送
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		if(!userId.equals(creditsTaskOrder.getMemId())) {
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user valid error");
		}
		
		String ciphertext = creditsTaskOrder.getMemId() + creditsTaskOrder.getOrderNo() + SALT;
		ciphertext = MD5Utils.digest(ciphertext);
		
		if(!ciphertext.equals(sign)) {
			throw new AuthException(TaskExceptionCode.SIGN_VALID_ERROR, "sign valid error");
		}
		
		creditsService.creditsConfirmAdd(creditsTaskOrder);
		
		return Response.ok();
	}
	
	

}
