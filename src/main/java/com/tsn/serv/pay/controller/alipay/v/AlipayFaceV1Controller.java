package com.tsn.serv.pay.controller.alipay.v;

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
import com.tsn.serv.pay.service.yuansfer.YuanSferService;

/**
 * 阿里当面付款接口
 * @author work
 *
 */
@RestController
@RequestMapping("v1/aliface/" + ApiCons.PRIVATE_PATH)
public class AlipayFaceV1Controller {
	
	@Autowired
	private AlipayFaceService alipayService;
	
	@Autowired
	private YuanSferService yuanSferService;
	
	@PostMapping("/qcode/{productCode}")
	public Response<?> sendTrade(@PathVariable String productCode, @RequestBody AliFacePay aliFacePay) {
		String qCode = null;
		if (productCode.contains("yuansfer")) {
			qCode = yuanSferService.tradeByQCode(productCode,aliFacePay.getOutTradeNo(), aliFacePay.getSubject(), aliFacePay.getTotalAmount(), aliFacePay.getTimeout());
		}else {
			qCode = alipayService.tradeByQCode(productCode,aliFacePay.getOutTradeNo(), aliFacePay.getSubject(), aliFacePay.getTotalAmount(), aliFacePay.getTimeout());
		}
		
		return Response.ok(qCode);
		
	}
	
	@GetMapping("/{outTradeNo}/{productCode}")
	public Response<?> sendTrade(@PathVariable String productCode, @PathVariable String outTradeNo) {
		AlipayF2FQueryResult result = null;
		if (productCode.contains("yuansfer")) {
			result = yuanSferService.queryTrade(outTradeNo);
		}else {
			result = alipayService.queryTrade(productCode, outTradeNo);
		}
		return Response.ok(result);
	}
	
	
}

