package com.tsn.serv.mem.controller.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.AuthCode;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.credits.TaskExceptionCode;
import com.tsn.serv.common.enm.credits.task.CreditsTaskEum;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder.ExtendCreditsTaskOrder;
import com.tsn.serv.mem.service.credits.CreditsTaskOrderService;

/**
 * 任务订单
 * @author work
 *
 */
@RestController
@RequestMapping("task")
public class CreditsTaskOrderController {

	@Autowired
	private CreditsTaskOrderService creditsTaskOrderService;
	
	
	/**
	 * 新增任务 领取任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		creditsTaskOrderService.addTaskOrder(creditsTaskOrder); 
		
		return Response.ok();
	}
	
	/**
	 * 新增签到任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/sign")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addSignTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		creditsTaskOrder.setMemId(userId);
		creditsTaskOrder.setTaskType(CreditsTaskEum.sign.name());
		creditsTaskOrderService.addSignTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}
	
	/**
	 * 新增视频广告任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/ad_view")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addAdViewTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		creditsTaskOrder.setMemId(userId);
		String orderNo = creditsTaskOrderService.createOrderNo();
		creditsTaskOrder.setTaskType(CreditsTaskEum.ad_view.name());
		creditsTaskOrder.setOrderNo(orderNo);
		creditsTaskOrderService.addAdViewTaskOrder(creditsTaskOrder);
		
		return Response.ok(orderNo);
	}
	
	/**
	 * 新增激励视频广告任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/ad_stimulate")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addStimulateAdViewTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
//		String tokenType = jwtInfo.getTokenType();
//		if(AuthEnum.guest_us.name().equals(tokenType)) {
//			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
//		}
		creditsTaskOrder.setMemId(userId);
		String orderNo = creditsTaskOrderService.createOrderNo();
		creditsTaskOrder.setTaskType(CreditsTaskEum.ad_stimulate.name());
		creditsTaskOrder.setOrderNo(orderNo);
		creditsTaskOrderService.addStimulateAdViewTaskOrder(creditsTaskOrder);
		
		return Response.ok(orderNo);
	}
	
	
	/**
	 * 新增网址保存任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/url_save")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addUrlSaveTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		creditsTaskOrder.setMemId(userId);
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		creditsTaskOrder.setTaskType(CreditsTaskEum.url_save.name());
		creditsTaskOrderService.addUrlSaveTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}
	
	/**
	 * 新增填写网址任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/url_fill")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addUrlFillTaskOrder(@RequestBody ExtendCreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		/**
		 * 校验网址是否填写正确
		 */
		if(!isVerificationUrlSuccess(creditsTaskOrder.getUrl())) {
			throw new BusinessException(TaskExceptionCode.URL_FILL_ERRPR, "网址填写错误");
		}
		
		creditsTaskOrder.setTaskType(CreditsTaskEum.url_fill.name());
		creditsTaskOrderService.addUrlFillTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}
	
	private boolean isVerificationUrlSuccess(String url) {
		boolean isSuccess = false; 
		String urls[] = Env.getVal("mem.credits.task.urls").split(",");
		for (String string : urls) {
			if(url.equals(string)) {
				isSuccess = true; 
				break;
			}
		}
		return isSuccess;
	}
	
	/**
	 * 新增朋友圈分享任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/app_share")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addAppShareTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new BusinessException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new BusinessException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		if(StringUtils.isEmpty(creditsTaskOrder.getFileUrl())) {
			throw new BusinessException(TaskExceptionCode.TASK_FILE_IS_NULL, "TASK FILE IS NULL");
		}
		creditsTaskOrder.setMemId(userId);
		creditsTaskOrder.setTaskType(CreditsTaskEum.app_share.name());
		creditsTaskOrderService.addAppShareTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}
	
	/**
	 * 新增问卷填写任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/fq_fill")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addFqFillTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		creditsTaskOrder.setTaskType(CreditsTaskEum.fq_fill.name());
		creditsTaskOrderService.addFqFillTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}
	
	/**
	 * 新增好友邀请任务
	 * @param creditsTaskOrder
	 * @return
	 */
//	@PostMapping("add/friend_invite")
//	public Response<?> addFriendInviteTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
//		
//		creditsTaskOrder.setTaskType(CreditsTaskEum.friend_invite.name());
//		creditsTaskOrderService.addFriendInviteTaskOrder(creditsTaskOrder);
//		
//		return Response.ok();
//	}
	
	/**
	 * 新增谷歌商店评价任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/google_evaluate")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addGoogleEvaluateTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		
		creditsTaskOrder.setTaskType(CreditsTaskEum.google_evaluate.name());
		creditsTaskOrderService.addGoogleEvaluateTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}
	
	/**
	 * 新增软件下载广告任务
	 * @param creditsTaskOrder
	 * @return
	 */
	@PostMapping("add/ad_down")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> addAdDownTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			throw new AuthException(TaskExceptionCode.TASK_GUEST_NOT_ALLOW, "TASK GUEST NOT ALLOW");
		}
		creditsTaskOrder.setMemId(userId);
		creditsTaskOrder.setTaskType(CreditsTaskEum.ad_down.name());
		creditsTaskOrderService.addAdDownTaskOrder(creditsTaskOrder);
		
		return Response.ok();
	}

	/**
	 * 微信上传朋友圈，谷歌商店评价审核列表
	 * @param page
	 * @return
	 */
	@GetMapping("/page")
	public Response<?> selectTaskOrderByPage(Page page) {
		creditsTaskOrderService.selectTaskOrderByPage(page);
		return Response.ok(page);
	}

	/**
	 * 审任务核
	 * @param creditsTaskOrder
	 * @return
	 */
	@PutMapping("/examine")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> examineTaskOrder(@RequestBody CreditsTaskOrder creditsTaskOrder) {
		creditsTaskOrderService.examineTaskOrder(creditsTaskOrder);
		return Response.ok();
	}
	
	/**
	 * 任务审核驳回
	 * @return
	 */
	@PutMapping("reject")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> taskOrderReject(@RequestBody CreditsTaskOrder creditsTaskOrder){
		
		creditsTaskOrderService.taskOrderReject(creditsTaskOrder);
		
		return Response.ok();
	}
}
