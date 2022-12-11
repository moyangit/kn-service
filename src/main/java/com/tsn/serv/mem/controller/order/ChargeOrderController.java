package com.tsn.serv.mem.controller.order;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.norepeat.NoRepeatException;
import com.tsn.common.core.norepeat.impl.AtomiRedisNoRepeatService;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.mem.entity.mem.MemCashoutInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.order.CashoutOrder;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.service.order.CashoutOrderService;
import com.tsn.serv.mem.service.order.ChargeOrderService;
import com.tsn.serv.pay.service.yzf.YzfService;

@RestController
@RequestMapping("order")
public class ChargeOrderController {
	
	@Autowired
	private ChargeOrderService chargeOrderService;
	
	@Autowired
	private CashoutOrderService cashoutOrderService;
	
	@Autowired
	private AtomiRedisNoRepeatService atomiRedisNoRepeatService;
	
	@Autowired
	private YzfService yzfService;
	
	@GetMapping("/redirect")
	@AuthClient()
	public Response<?> redirect2Page() {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = null;
		if (info != null) {
			userId = info.getSubject();
		}
		
		String token = info == null ? "" : (String) info.getExt("token");
		
		String result = chargeOrderService.toSign(userId, token);
		
		return Response.ok(result);
	}
	
	@GetMapping("/callback/yzf")
	public String callbackYzf(@RequestParam Map<String, String> callbackMap) {
		
		boolean returnResult = chargeOrderService.queryOrderAndUpdateCallback(callbackMap);
		
		return returnResult ? "success" : "fail";
	}
	
	/**
	 * 只获取签名
	 * @param subOrderMap
	 * @return
	 */
	@PostMapping("/subefore")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> addChargeOrderFormSubmit(@RequestBody Map<String, String> subOrderMap) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		String chargeId = subOrderMap.get("chargeId");
		String payType = subOrderMap.get("payType");
		Map<String, Object> result = chargeOrderService.addChargeOrderFormSubmit(userId, chargeId, payType);
		return Response.ok(result);
	}

	@PostMapping("/charge")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> addRechargeOrder(@RequestBody ChargeOrder chargeOrder) {
		
		try {
			atomiRedisNoRepeatService.validRepeat(chargeOrder.getReToken());
		} catch (NoRepeatException e) {
			throw new BusinessException(e.getCode(), e.getMessage());
		}
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		chargeOrder.setMemId(userId);
		Map<String, Object> result = chargeOrderService.addChargeOrderForQcode(chargeOrder);
		
		return Response.ok(result);
	}
	
	@PostMapping("/cashout")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> addCashoutRechargeOrder(@RequestBody MemCashoutInfo cashoutInfo, BindingResult br) {
		
		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
		}
		
		atomiRedisNoRepeatService.validRepeat(cashoutInfo.getReToken());
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		cashoutInfo.setMemId(userId);
		cashoutOrderService.addCashoutOrder(cashoutInfo);
		return Response.ok();
	}
	
	@GetMapping("/refresh")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> queryOrderRefresh() {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		ChargeOrder chargeOrder = chargeOrderService.queryOrderAndUpByCache(userId);
		
		return Response.ok(chargeOrder);
	}
	
	@GetMapping("/no/{orderNo}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> queryOrderByMemId(@PathVariable String orderNo) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		ChargeOrder chargeOrder = chargeOrderService.queryOrderAndUpdateByNo(userId, orderNo);
		
		return Response.ok(chargeOrder);
	}
	
	@GetMapping("/charge/page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> queryMyChargeOrderByPage(Page page) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		page.setParamObj("memId", userId);
		chargeOrderService.queryMyOrderByPage(page);
		
		return Response.ok(page);
	}
	
	@GetMapping("/cashout/page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> queryMyCashOutOrderByPage(Page page) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		page.setParamObj("memId", userId);
		cashoutOrderService.queryCashoutOrderByPage(page);
		
		return Response.ok(page);
	}
	
	@GetMapping("/reToken")
	@AuthClient(client = AuthEnum.bea_us)
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
	 * @AuthClient("v-pay") 只允许v-pay服务进行访问
	 * @param orderNo
	 * @return
	 */
	@PutMapping(ApiCons.PRIVATE_PATH + "/charge/close")
	@AuthClient(service = "v-pay")
	public Response<?> updateRechargeOrderFail(@RequestBody ChargeOrder chargeOrder) {
		chargeOrderService.updateChargeOrderForClose(chargeOrder);
		return Response.ok();
	}
	
	@PutMapping(ApiCons.PRIVATE_PATH + "/charge/success")
	@AuthClient(service = "v-pay")
	public Response<?> updateRechargeOrderSuccess(@RequestBody ChargeOrder chargeOrder) {
		
		if (StringUtils.isEmpty(chargeOrder.getOrderNo()) || StringUtils.isEmpty(chargeOrder.getTradeNo()) || StringUtils.isEmpty(chargeOrder.getTradeStatus())) {
			throw new RequestParamValidException("updateRechargeOrderSuccess, param can not be null");
		}
		
		chargeOrderService.updateChargeOrderSuccess(chargeOrder);
		
		return Response.ok();
	}

	@GetMapping(ApiCons.PRIVATE_PATH + "/no/{orderNo}")
	@AuthClient(service = "v-pay")
	public Response<?> getOrderByNO(@PathVariable String orderNo) {
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		ChargeOrder chargeOrder = chargeOrderService.queryOrderByNo(orderNo);
		
		return Response.ok(chargeOrder);
	}

	/**
	 * 充值详情页面展示查询
	 * @param page
	 * @return
	 */
	@GetMapping("/charge/list")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> queryChargeOrderList(Page page) {

		JwtInfo info = JwtLocal.getJwt();

		String userId = info.getSubject();

		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}

		List<ChargeOrder> chargeOrderList = chargeOrderService.queryChargeOrderList(page);
		page.setData(chargeOrderList);

        return Response.ok(page);
	}

	/**
	 * 刷新支付订单状态
	 * @param chargeOrder
	 */
	@PutMapping("/refreshOrderStatus")
	@AuthClient(client = AuthEnum.bea_mn)
	public void refreshOrderStatus(@RequestBody ChargeOrder chargeOrder){
		chargeOrderService.queryOrderAndUpdateByNo(chargeOrder.getMemId(),chargeOrder.getOrderNo());
	}

	/**
	 * 充值详情删除
	 * @param map
	 * @return
	 */
    @DeleteMapping("/charge/delete")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> deleteChargeOrderById(@RequestBody Map<String,Object> map) {

        JwtInfo info = JwtLocal.getJwt();

        String userId = info.getSubject();

        if (StringUtils.isEmpty(userId)) {
            throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
        }

        //逻辑删除
        //chargeOrderService.deleteChargeOrderById(map.get("orderId").toString());

        return Response.ok();
    }

	/**
	 *财务管理详情页面展示查询(提现订单)
	 * @param page
	 * @return
	 */
	@GetMapping("/funancialPage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> funancialManageList(Page page) {
		cashoutOrderService.funancialManageList(page);
		return Response.ok(page);
	}

	/**
	 * 财务管理删除(根据ID删除提现订单表数据)
	 * @param cashoutOrderList
	 * @return
	 */
	@DeleteMapping("/deleteFunancialManage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteFunancialManage(@RequestBody List<CashoutOrder> cashoutOrderList) {
		cashoutOrderService.deleteFunancialManage(cashoutOrderList);
		return Response.ok();
	}

	/**
	 * 客服中心-提现管理列表展示
	 * @param page
	 * @return
	 */
	@GetMapping("/customerPage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getCustomerList(Page page) {
		cashoutOrderService.getCustomerList(page);
		return Response.ok(page);
	}

	/**
	 * 客服中心-提现管理-提现订单修改
	 * @param cashoutOrder
	 * @return
	 */
	@PutMapping("/")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateCustomer(@RequestBody CashoutOrder cashoutOrder) {
		cashoutOrderService.updateCustomer(cashoutOrder);
		return Response.ok();
	}

	/**
	 * 驳回提现申请
	 * @param cashoutOrder
	 * @return
	 */
	@PutMapping("/rejected")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> rejectedCustomer(@RequestBody CashoutOrder cashoutOrder) {
		cashoutOrderService.rejectedCustomer(cashoutOrder);
		return Response.ok();
	}

	/**
	 * 批量设置代理
	 * @param memInfoList
	 * @return
	 */
	@PutMapping("/setProxyBatch")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> setProxyBatch(@RequestBody List<MemInfo> memInfoList) {
		chargeOrderService.setProxyBatch(memInfoList);
		return Response.ok();
	}

	/**
	 * 根据日期获取充值订单
	 * @param page
	 * @return
	 */
	@GetMapping("/charge/todayList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> todayChargeOrderList(Page page) {
		List<ChargeOrder> chargeOrderList = chargeOrderService.todayChargeOrderList(page);
		page.setData(chargeOrderList);
		return Response.ok(page);
	}

	/**
	 * 根据会员手机号查询充值订单
	 * @param page
	 * @return
	 */
	@GetMapping("/charge/chargeOrder")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> chargeOrder(Page page) {
		chargeOrderService.getChargeOrder(page);
		return Response.ok(page);
	}

	/**
	 * 获取会员返利和提现订单
	 * @param page
	 * @return
	 */
	@GetMapping("/order/page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getChangeCashoutOrderPage(Page page) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		page.setParamObj("memId", userId);

		cashoutOrderService.getCashoutOrderPage(page);
		return Response.ok(page);
	}
	
	/**
	 * 获取会员返利和提现订单
	 * @param page
	 * @return
	 */
	@GetMapping("/charge/money/statis/{month}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getOrderMoneyStatisByDayAndType(@PathVariable String month) {
		return Response.ok(chargeOrderService.getOrderMoneyStatisByDayAndType(month));
	}
	
	@GetMapping("/type/money/statis/{month}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getOrderMemTypeMoneyByMonth(@PathVariable String month) {
		return Response.ok(chargeOrderService.getOrderMemTypeMoneyByMonth(month));
	}
	
	/**
	 * 获取每日订单对应新用户，老用户，对应的充值金额
	 * @param page
	 * @return
	 */
	@GetMapping("/charge/people/statis/{month}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getOrderRechargePeopleGroupByDay(@PathVariable String month) {
		return Response.ok(chargeOrderService.getOrderRechargePeopleGroupByDay(month));
	}
}
