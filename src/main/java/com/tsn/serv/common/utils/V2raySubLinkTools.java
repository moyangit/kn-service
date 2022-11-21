package com.tsn.serv.common.utils;

import java.net.URLEncoder;

import com.tsn.common.utils.utils.tools.security.Base64Utils;

public class V2raySubLinkTools {
	//path,add,port,name, name, userId
	//{"host":"","path":"/564dfa20/","tls":"tls","verify_cert":true,"add":"aflcn.yixuedianzishu.com","port":51522,"aid":2,"net":"ws","headerType":"none","v":"2","type":"vmess","ps":"Anycast|韩国","remark":"Anycast|韩国","id":"fff5804a-8f35-37da-b539-250b9bfe89f7","class":1}
	public final static String GENERAL_V2RAY_LINK = "{\"host\":\"\",\"path\":\"%s\",\"tls\":\"none\",\"verify_cert\":false,\"add\":\"%s\",\"port\":%d,\"aid\":0,\"net\":\"ws\",\"headerType\":\"none\",\"v\":\"2\",\"type\":\"none\",\"ps\":\"%s\",\"remark\":\"%s\",\"id\":\"%s\",\"class\":1}";

	//vmess://Y2hhY2hhMjAtcG9seTEzMDU6ZmZmNTgwNGEtOGYzNS0zN2RhLWI1MzktMjUwYjliZmU4OWY3QGFmbHp6dWwueWl4dWVkaWFuemlzaHUuY29tOjUxNTE1?remarks=%E7%A6%8F%E5%88%A9%7C%E6%96%B0%E5%8A%A0%E5%9D%A1%7C0.1x%7C%E9%99%90%E9%80%9F10Mbps&obfsParam=aflzzul.yixuedianzishu.com&path=/564dfa20/&obfs=websocket&tls=1&peer=
	//uuid,ip,port
	public final static String SR_V2RAY_LINK = "chacha20-poly1305:%s@%s:%d";
	
	//常规v2ray
	public static String getGeneralV2rayLink(String path, String add, int port, String pathName, String uuid) {
		
		String generalLink = String.format(V2raySubLinkTools.GENERAL_V2RAY_LINK, path, add, port, pathName, pathName, uuid);
		
		System.out.println(generalLink);
		StringBuffer sb = new StringBuffer("vmess://");
		sb.append(Base64Utils.encodeToString(generalLink));
		return sb.toString();
	}
	
	//小火箭
	public static String getSrV2rayLink(String path, String add, int port, String pathName, String uuid) {
		
		String generalLink = String.format(V2raySubLinkTools.SR_V2RAY_LINK, uuid, add, port);
		
		System.out.println(generalLink);
		StringBuffer sb = new StringBuffer("vmess://");
		sb.append(Base64Utils.encodeToString(generalLink));
		//flag表示国旗
		//remarks=xxxxx&obfsParam=aflzzul.yixuedianzishu.com&flag=&path=/564dfa20/&obfs=websocket&tls=1&peer=
		sb.append("?").append("remarks=").append(URLEncoder.encode(pathName)).append("&obfsParam=").append("&path=").append(path).append("&obfs=websocket").append("&tls=0&peer=");
		return sb.toString();
	}
	
	
}
