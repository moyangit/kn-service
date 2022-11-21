package com.tsn.serv.pay.service.yuansfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.tsn.common.pay.alipay.face.model.TradeStatus;
import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;
import com.tsn.common.utils.utils.tools.http.HttpPostReq;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.utils.tools.sign.MapUtil;
import com.tsn.serv.pub.service.sys.SysConfigService;

@Service
public class YuanSferService {
	
	private static Logger logger = LoggerFactory.getLogger(YuanSferService.class);
	
	@Autowired
	private SysConfigService sysConfigService;
	
	public static final String TEST_URL = "https://mapi.yuansfer.yunkeguan.com";          
    public static final String PROD_URL = "https://mapi.yuansfer.com";
    public static final String YUANSFER_TOKEN = "5c5fe30183be69fceff8174358d4b8ae";
    public static final String MERCHANT_NO = "202550";
    public static final String STORE_NO = "302749";
    public static final String SECRET = "4a23e05a695e996782816a5436b4f78d";
    public static final String CURRENCY = "USD";
	
	@SuppressWarnings("unchecked")
	public String tradeByQCode(String productCode, String outTradeNo, String subject, String totalAmount, String timeOut) {
		
		BigDecimal rate = sysConfigService.getUsdRate();
		
		totalAmount = new BigDecimal(totalAmount).multiply(rate).toString();
		
		String cashierUrl = null;
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantNo", MERCHANT_NO);
			map.put("storeNo", STORE_NO);
			
			map.put("amount", totalAmount);
			map.put("currency", CURRENCY);
			map.put("settleCurrency", CURRENCY);
			map.put("ipnUrl", "https://test.com/aa/bb");
			map.put("callbackUrl", "https://test.com/aa/bb");
			map.put("reference", outTradeNo);
			map.put("terminal", "WAP");
			map.put("timeout", "120");
			map.put("vendor", "alipay");
			
			map.put("description", subject);
			map.put("note", subject);
			
			
			String sign = MD5Utils.digest(sign(map) + "&"+MD5Utils.digest(SECRET));
			map.put("verifySign", sign);
			
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			// 自己平台的订单号
			params.add(new BasicNameValuePair("merchantNo", map.get("merchantNo")));
			// 货币类型，港币
			params.add(new BasicNameValuePair("storeNo", map.get("storeNo")));
			// 交易金额
			params.add(new BasicNameValuePair("amount", map.get("amount")));

			// 支付的用户信息
			// 必填
			params.add(new BasicNameValuePair("currency", map.get("currency")));
			// 必填
			params.add(new BasicNameValuePair("settleCurrency", map.get("settleCurrency")));
			// 必填
			params.add(new BasicNameValuePair("ipnUrl", map.get("ipnUrl")));
			// 非必填
			params.add(new BasicNameValuePair("callbackUrl", map.get("callbackUrl")));
			// 必填
			params.add(new BasicNameValuePair("terminal", map.get("terminal")));
			
			params.add(new BasicNameValuePair("reference", map.get("reference")));  
			
			// 必填
			params.add(new BasicNameValuePair("timeout", map.get("timeout")));

			// 回调地址
			params.add(new BasicNameValuePair("vendor", map.get("vendor")));
			// 支付方式
			params.add(new BasicNameValuePair("description", map.get("description")));
			// 交易商品名
			params.add(new BasicNameValuePair("note", map.get("note")));
			
			// 签名是否正确
			params.add(new BasicNameValuePair("verifySign",map.get("verifySign")));
			
			HttpPostReq req  = new HttpPostReq(PROD_URL + "/online/v3/secure-pay", null, params, 10000, 10000);
			String content = req.excuteReturnStr();
			
			Map<String, Object> contentMap = JsonUtils.jsonToPojo(content, Map.class);
			
			Map<String, Object> result = (Map<String, Object>) contentMap.get("result");
			
			cashierUrl = String.valueOf(result.get("cashierUrl"));
		} catch (Exception e) {
			logger.error("tradeByQCode error, e = {}", e);
		}
		
		return cashierUrl;
		
	}
	
	public String sign(Map<String, String> params) {
    	
    	Map<String, String> orderParams = MapUtil.order(params);
    	
    	return MapUtil.mapJoin(orderParams, false, false);
    }
	
	public AlipayF2FQueryResult queryTrade(String reference){
    	
    	AlipayF2FQueryResult payResult = new AlipayF2FQueryResult();
    	payResult.setTradeStatus(TradeStatus.UNKNOWN);
    	
    	AlipayTradeQueryResponse resp = new AlipayTradeQueryResponse();
    	//resp.setTradeStatus("TRADE_SUCCESS");
    	payResult.setResponse(resp);
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantNo", MERCHANT_NO);
			map.put("storeNo", STORE_NO);
			map.put("reference", reference);
			
			String sign = MD5Utils.digest(sign(map) + "&"+MD5Utils.digest(SECRET));
			map.put("verifySign", sign);
			
			
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			// 自己平台的订单号
			params.add(new BasicNameValuePair("merchantNo", map.get("merchantNo")));
			// 货币类型，港币
			params.add(new BasicNameValuePair("storeNo", map.get("storeNo")));
			
			params.add(new BasicNameValuePair("reference", map.get("reference")));  
			
			// 签名是否正确
			params.add(new BasicNameValuePair("verifySign",map.get("verifySign")));
			
			HttpPostReq req  = new HttpPostReq(PROD_URL + "/app-data-search/v3/tran-query", null, params, 2000, 2000);
			String content = req.excuteReturnStr();
			
			@SuppressWarnings("unchecked")
			Map<String, Object> contentMap = JsonUtils.jsonToPojo(content, Map.class);
			
			Map<String, Object> result = (Map<String, Object>) contentMap.get("result");
			
			if (result != null) {
				String status = String.valueOf(result.get("status"));
				if ("success".equals(status)) {
					String transactionNo = String.valueOf(result.get("transactionNo"));
					payResult.getResponse().setTradeNo(transactionNo);
					payResult.getResponse().setTradeStatus("TRADE_SUCCESS");
					payResult.setTradeStatus(TradeStatus.SUCCESS);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return payResult;
    }
	
	public static void main(String[] args) {
		
		AlipayF2FQueryResult result = new YuanSferService().queryTrade("201015289256865828864");
		
		System.out.println(result);
	}
	
}
