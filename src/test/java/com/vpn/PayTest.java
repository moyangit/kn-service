package com.vpn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.utils.tools.http.HttpPostReq;
import com.tsn.common.utils.utils.tools.http.entity.HttpRes;
import com.tsn.common.utils.utils.tools.security.MD5Utils;

public class PayTest {
	
	public static void main(String[] args) {
		String busisId = "1067";
		//String scret = "1Jj1Q7i9HJnWmi95hWva15Weg92MZVmG";
		
		String payApiUrl = "https://www.sanzongzui.com/submit.php";
		
		
		String pid = busisId;
		String type = "alipay";
		String out_trade_no = "20221124184339935";
		String notify_url = "http://127.0.0.1/SDK/notify_url.php";
		String return_url = "http://127.0.0.1/SDK/return_url.php";
		String name = "测试商品";
		String money = "10";
		String clientip = "";
		String device = "";
		String param = "";
		String sign_type = "MD5";
		
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("pid", busisId));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("out_trade_no",out_trade_no));
		
		if (!StringUtils.isEmpty(notify_url)) {
			params.add(new BasicNameValuePair("notify_url",notify_url));
		}
		
		if (!StringUtils.isEmpty(return_url)) {
			params.add(new BasicNameValuePair("return_url",return_url));
		}
		
		params.add(new BasicNameValuePair("name",name));
		params.add(new BasicNameValuePair("money",money));
		
		if (!StringUtils.isEmpty(clientip)) {
			params.add(new BasicNameValuePair("clientip",clientip));
		}
		
		if (!StringUtils.isEmpty(device)) {
			params.add(new BasicNameValuePair("device",device));
		}
		
		if (!StringUtils.isEmpty(param)) {
			params.add(new BasicNameValuePair("param",param));
		}
		
		String sign = toSign(pid, type, out_trade_no, notify_url, return_url, name, money, clientip, device, param);
		
		params.add(new BasicNameValuePair("sign",sign));
		params.add(new BasicNameValuePair("sign_type",sign_type));
		
		HttpPostReq httpReq = new HttpPostReq(payApiUrl, null, params);
		
		try {
			HttpRes httpRes = httpReq.excuteReturnObj();
			System.out.println("Result : " + httpRes.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String toSign(String pid, String type, String out_trade_no, String notify_url, String return_url, String name, String money, String clientip, String device, String param) {
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
		
		String str= Ksort(map);
		System.out.println("排序参数:" + str);
		String secret = "1Jj1Q7i9HJnWmi95hWva15Weg92MZVmG";
		String sign = signMd5(str,secret);
		System.out.println("sign:" + sign);
		return sign;

	}

	public static String Ksort(Map<String, String> map) {
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
