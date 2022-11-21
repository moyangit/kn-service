/*package com.tsn.serv.pay.service.alipay;

import org.springframework.stereotype.Service;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.tsn.common.pay.alipay.wap.WapAlipayTradeHandler;

@Service
public class AlipayWapService {
	
    private WapAlipayTradeHandler tradeHandler = WapAlipayTradeHandler.build();
    
	public String tradeByQCode(String outTradeNo, String subject, String totalAmount, String timeOut) {
		
		String from = tradeHandler.trade2Pay(outTradeNo, subject, totalAmount, timeOut);
		
		return from;
		
	}
	
	public AlipayTradeQueryResponse queryTrade(String outTradeNo) {
		
		AlipayTradeQueryResponse result = tradeHandler.queryTrade(outTradeNo);
		
		return result;
		
	}
}
*/