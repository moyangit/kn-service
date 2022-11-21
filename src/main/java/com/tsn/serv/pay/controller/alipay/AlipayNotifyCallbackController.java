package com.tsn.serv.pay.controller.alipay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.pay.service.mem.MemChargeOrderService;

@RestController
@RequestMapping("alipay/callback")
public class AlipayNotifyCallbackController {
	
	@Autowired
	private MemChargeOrderService memChargeOrderService;

	@GetMapping("/charge/ali/callback")
	public Response<?> updateRechargeOrder() {
		
		//chargeOrderService.updateChargeOrder(chargeOrder);
		
		return Response.ok();
	}
}