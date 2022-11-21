package com.tsn.serv.pay.service.alipay;

import org.springframework.stereotype.Service;

import com.tsn.common.pay.alipay.face.FaceAlipayTradeHandler;
import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;

@Service
public class AlipayFaceService {
	
	public String tradeByQCode(String productCode, String outTradeNo, String subject, String totalAmount, String timeOut) {
		
		FaceAlipayTradeHandler tradeHandler = new FaceAlipayTradeHandler(productCode);
		
		String qCode = tradeHandler.tradeForQCode(outTradeNo, subject, totalAmount, timeOut);
		
		return qCode;
		
	}
	
	public AlipayF2FQueryResult queryTrade(String productCode, String outTradeNo) {
		
		FaceAlipayTradeHandler tradeHandler = new FaceAlipayTradeHandler(productCode);
		
		AlipayF2FQueryResult result = tradeHandler.queryTrade(outTradeNo);
		
		return result;
		
	}
}
