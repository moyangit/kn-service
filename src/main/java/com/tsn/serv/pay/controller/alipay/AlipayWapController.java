/*package com.tsn.serv.pay.controller.alipay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.common.entity.pay.AliFacePay;
import com.tsn.serv.pay.service.alipay.AlipayFaceService;
import com.tsn.serv.pay.service.alipay.AlipayWapService;

*//**
 * 阿里当面付款接口
 * @author work
 *
 *//*
@RestController
@RequestMapping("aliface/" + ApiCons.PRIVATE_PATH)
public class AlipayWapController {
	
	@Autowired
	private AlipayWapService alipayService;
	
	@PostMapping("/wap")
	public Response<?> sendTrade(@RequestBody AliFacePay aliFacePay) {
		
		String form = alipayService.tradeByQCode(aliFacePay.getOutTradeNo(), aliFacePay.getSubject(), aliFacePay.getTotalAmount(), aliFacePay.getTimeout());
		
		return Response.ok(form);
		
	}
	
	@GetMapping("/{outTradeNo}")
	public Response<?> sendTrade(@PathVariable String outTradeNo) {
		AlipayTradeQueryResponse result = alipayService.queryTrade(outTradeNo);
		return Response.ok(result);
	}
	
	
}
*/