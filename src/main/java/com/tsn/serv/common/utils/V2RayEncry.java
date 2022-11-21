package com.tsn.serv.common.utils;

import java.util.Date;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.tsn.common.utils.utils.tools.security.Base64Utils;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.utils.tools.security.SignUtil;

public class V2RayEncry {
	
	public static String getEncryPathV2(String path, String userId, long susDate, Map<String, String> extInfo) {
		
		//String encryPathKey = Env.getVal("encry.v2.path.key");
		
		long currDate = new Date().getTime();
		
		String resetAndUserId = resetAndUserIdV2(path, userId, susDate, currDate, map2str(extInfo));
		
		String desData;
		try {
			
			desData = resetAndUserId + ":" + userId + ":" + susDate + ":" + currDate + ":" + map2str(extInfo);
			
			String encodeStr = Base64Utils.encodeToString(Base64Utils.encodeToString(desData.getBytes()));
			
			return encodeStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private static String map2str(Map<String, String> extInfo) {
		if (extInfo == null) {
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : extInfo.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue() + ",");
		}
		
		return sb.substring(0, sb.lastIndexOf(","));
	}
	
	private static String resetAndUserIdV2(String path, String userId, long subDate, long currDate, String extInfo) {
		
		if (StringUtils.isEmpty(userId)) {
			return "";
		}
		
		String userId2Md5 = MD5Utils.digest(userId);
		
		char[] contents = userId2Md5.toCharArray();
		
		StringBuffer jsSb = new StringBuffer();
		StringBuffer osSb = new StringBuffer();
		
		for (int num = 0; num < contents.length; num++) {
			
			if (num % 2 == 0) {
				osSb.append(contents[num]);
				continue;
			}
			
			jsSb.append(contents[num]);
			
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(jsSb.toString()).append(path).append(subDate).append(currDate).append(extInfo).append(osSb.toString()).append("v2.0");
		
		return SignUtil.sha1(sb.toString());
		
	}
	
	
	
	
	public static String getEncryPath(String path, String userId, long susDate) {
		
		//String encryPathKey = Env.getVal("encry.v2.path.key");
		
		long currDate = new Date().getTime();
		
		String resetAndUserId = resetAndUserId(path, userId, susDate, currDate);
		
		String desData;
		try {
			
			desData = userId + ":" + susDate + ":" + currDate + ":" + resetAndUserId;
			
			String encodeStr = Base64Utils.encodeToString(Base64Utils.encodeToString(desData.getBytes()));
			
			return encodeStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private static String resetAndUserId(String path, String userId, long subDate, long currDate) {
		
		if (StringUtils.isEmpty(userId)) {
			return "";
		}
		
		String userId2Md5 = MD5Utils.digest(userId);
		
		char[] contents = userId2Md5.toCharArray();
		
		StringBuffer jsSb = new StringBuffer();
		StringBuffer osSb = new StringBuffer();
		
		for (int num = 0; num < contents.length; num++) {
			
			if (num % 2 == 0) {
				osSb.append(contents[num]);
				continue;
			}
			
			jsSb.append(contents[num]);
			
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(jsSb.toString()).append(path).append(subDate).append(currDate).append(osSb.toString()).append("v1.0");
		
		return SignUtil.sha1(sb.toString());
		
	}
/*	
	public static void main(String[] args) {
		System.out.println(Base64Utils.decodeToString(Base64Utils.decodeToString("TVRjMU5qSTVNRFV3T0RRMU1UYzBOVGM1TWpveE5qQXdORFF4TnpBNE1EQXdPakUyTURBek5UVTNOalkzT1RRNk1qVkNORFF6UWpJNFJEUkJSak5FUVRJNFJUWTFNVVZGUkRNNFFqZzJOemRDTkVGR1JERkJSQT09")));
	}*/
	
}
