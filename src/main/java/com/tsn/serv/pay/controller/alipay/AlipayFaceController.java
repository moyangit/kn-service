package com.tsn.serv.pay.controller.alipay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.pay.common.cons.ApiCons;
import com.tsn.serv.pay.entity.AliFacePay;
import com.tsn.serv.pay.service.alipay.AlipayFaceService;

/**
 * 阿里当面付款接口
 * @author work
 *
 */
@RestController
@RequestMapping("aliface/" + ApiCons.PRIVATE_PATH)
public class AlipayFaceController {
	
	@Autowired
	private AlipayFaceService alipayService;
	
	@PostMapping("/qcode")
	public Response<?> sendTrade(@RequestBody AliFacePay aliFacePay) {
		
		String qCode = alipayService.tradeByQCode("kjs",aliFacePay.getOutTradeNo(), aliFacePay.getSubject(), aliFacePay.getTotalAmount(), aliFacePay.getTimeout());
		
		return Response.ok(qCode);
		
	}
	
	@GetMapping("/{outTradeNo}")
	public Response<?> sendTrade(@PathVariable String outTradeNo) {
		AlipayF2FQueryResult result = alipayService.queryTrade("kjs", outTradeNo);
		return Response.ok(result);
	}
	
	
}