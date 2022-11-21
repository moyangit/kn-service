package com.tsn.serv.common.utils.valid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tsn.common.utils.utils.tools.security.Base64Utils;
import com.tsn.common.utils.utils.tools.sign.MapUtil;
import com.tsn.common.utils.utils.tools.sign.SignatureUtil;

public class ShareUtils {
	
	public static String getSign(String userId, String ivitCode, String shareUrl) {
		long shareTime = new Date().getTime();
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", userId);
		params.put("ivCode", ivitCode);
		params.put("ctime", String.valueOf(shareTime));
		params.put("url", shareUrl);
		String sign = SignatureUtil.generateSign(params, "share_vip@xx.com");
		params.put("sign", sign);
		
		params.remove("url");
		String str = MapUtil.mapJoin(params, false, false);
		return str;
	}
	
	public static Map<String, String> validSign(String reqUrl) {
		try {
			
			reqUrl = Base64Utils.decodeToString(reqUrl);
			
			Map<String, String> params = new HashMap<String, String>();
			
			String[] reqParamArr = reqUrl.split("\\?");
			String[] paramsArr = reqParamArr[1].split("\\&");
			
			for (String param : paramsArr) {
				String[] paramArr = param.split("\\=");
				params.put(paramArr[0], paramArr[1]);
			}
			
			Map<String, String> validParams = new HashMap<String, String>();
			validParams.put("uId", params.get("uId"));
			validParams.put("ivCode",params.get("ivCode"));
			validParams.put("ctime", params.get("ctime"));
			validParams.put("url", reqParamArr[0]);
			String sign = SignatureUtil.generateSign(validParams, "share_vip@xx.com");
			String reqSign = params.get("sign");
			if (sign.equals(reqSign)) {
				return params;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
