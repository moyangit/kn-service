package com.tsn.serv.pay.service.yzf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.web.utils.env.Env;

/**
 * 牛肉付
 * @author 28338
 *
 */
@Service
public class YzfService {
	
	public boolean validCallBack(Map<String, String> callbackMap) {
		
		String sign = callbackMap.get("sign");
		String signType = callbackMap.get("sign_type");
		
		callbackMap.remove("sign");
		callbackMap.remove("sign_type");
		String str= keySort(callbackMap);
		//System.out.println("排序参数:" + str);
		String secret = Env.getVal("yzf_secret");
		String ownSign = signMd5(str,secret);
		
		if (ownSign.equals(sign)) {
			return true;
		}
		
		return false;
	}
	
	public Map<String, Object> param2sign(String orderNo, String payType, String productName, String finalPrice, String signType) {
		
		//1067
		String pid = Env.getVal("yzf_pid");
		String type = payType;
		String out_trade_no = orderNo;
		String notifyUrl = Env.getVal("yzf_notify_url");
		String returnUrl = Env.getVal("yzf_return_url");
		String name = productName;
		String money = finalPrice;
		//String out_trade_no = orderNo;
		//String notify_url = "http://127.0.0.1/SDK/notify_url.php";
		//String return_url = "http://127.0.0.1/SDK/return_url.php";
		//String name = "测试商品";
		//String money = "10";
		String clientip = "";
		String device = "";
		String param = "";
		String sign_type = StringUtils.isEmpty(signType) ? "MD5" : signType;
		
		String sign = toSign(pid, type, out_trade_no, notifyUrl, returnUrl, name, money, clientip, device, param);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("pid", pid);
		result.put("sign", sign);
		result.put("productName", productName);
		result.put("signType", sign_type);
		result.put("orderNo", out_trade_no);
		result.put("finalPrice", finalPrice);
		result.put("notifyUrl", notifyUrl);
		result.put("returnUrl", returnUrl);
		return result;
	}
	
	private String toSign(String pid, String type, String out_trade_no, String notify_url, String return_url, String name, String money, String clientip, String device, String param) {
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("pid", pid);
		// 货币类型，港币
		map.put("type", type);
		// 交易金额
		map.put("out_trade_no", out_trade_no);

		// 支付的用户信息
		// 必填
		map.put("notify_url", notify_url);
		
		if (!StringUtils.isEmpty(return_url)) {
			// 回调地址
			map.put("return_url", return_url);
		}
		
		// 必填
		map.put("name", name);
		// 非必填
		map.put("money", money);
		
		if (!StringUtils.isEmpty(clientip)) {
			// 回调地址
			map.put("clientip", clientip);
		}
		
		
		if (!StringUtils.isEmpty(device)) {
			// 回调地址
			map.put("device", device);
		}

		if (!StringUtils.isEmpty(param)) {
			// 回调地址
			map.put("param", param);
		}
		
		String str= keySort(map);
		//System.out.println("排序参数:" + str);
		String secret = Env.getVal("yzf_secret");
		String sign = signMd5(str,secret);
		//System.out.println("sign:" + sign);
		return sign;

	}

	private String keySort(Map<String, String> map) {
		String sb = "";
		String[] key = new String[map.size()];
		int index = 0;
		for (String k : map.keySet()) {
			key[index] = k;
			index++;
		}
		//Arrays.sort(key, Collections.reverseOrder());
		Arrays.sort(key);
		for (String s : key) {
			// sb += s + "=" + map.get(s) + "&";

			sb += s;
			sb += "=";
			sb += map.get(s);
			sb += "&";

		}
		sb = sb.substring(0, sb.length() - 1);

		return sb;
	}

	public static String signMd5(String paramStr, String key) {
		
		String md5Str = MD5Utils.digest(paramStr + key);
		
		return md5Str;
	}
	
	
}
