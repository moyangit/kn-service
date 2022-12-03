package com.vpn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.utils.tools.json.JsonUtils;

public class LanzhouTest {
	
public static void main(String[] args) throws IOException {
		
		String reqUrl = "https://wwyh.lanzoue.com/iRmNN0hbe72h";
		String passwd = "46m5";
		
		Connection.Response response = Jsoup.connect(reqUrl).execute();
//		System.out.println(response.cookies());
//		System.out.println(response.body());
		
		Document document = Jsoup.parse(response.body());
		
		//System.out.println(document.html());
		
		String content = document.html();
		
		String[] contentArr = content.split("\n");
		
		String postUrl = "https://wwyh.lanzoue.com/ajaxm.php";
		String htmlData = "";
		for (String html : contentArr) {
			
			if (html.contains("//")) {
				continue;
			}
			
			if (html.contains("data : ")) {
				htmlData = html.substring(html.indexOf("'") + 1, html.lastIndexOf("'"));
				//params.put("signs", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}
		}
		
		Map<String, String> params = new HashMap<String, String>();
		if (!StringUtils.isEmpty(htmlData)) {
			htmlData = htmlData + passwd;
			String[] htmlDataArr = htmlData.split("&");
			for (String param : htmlDataArr) {
				String[] paramArr = param.split("=");
				params.put(paramArr[0], paramArr[1]);
			}
		}
		
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("origin", "https://wwyh.lanzoue.com");
		headerMap.put("Referer", reqUrl);
		
		Connection.Response responseFn = Jsoup.connect(postUrl).method(Method.POST)
				.data(params)
				.headers(headerMap)
				.cookies(response.cookies()).followRedirects(true)
		        .execute();
		
		Map<String, String> resultMap = JsonUtils.jsonToPojo(responseFn.body(), Map.class);
		
		String dom = resultMap.get("dom");
		String domUrl = resultMap.get("url");
		
		String downUrl = dom + "/file/" + domUrl;
		
		System.out.println(downUrl);
		//Connection.Response responseFn1 = Jsoup.connect(document.select("script").get(0).attr("src")).cookies(response.cookies()).execute();
		
		//System.out.println(responseFn1.cookies());
		/*String iframeContent = "";
		String iframeAffer = "";
		
		Elements elements = document.select("iframe[class='ifr2']");
		
		if (elements == null || elements.isEmpty()) {
			elements = document.select("iframe[class='n_downlink']");
		}
		
		for (Element element : elements) {
			iframeAffer = "https://kjsdd.lanzoui.com/" + element.baseUri() + element.attr("src");
		    Connection.Response responseFn = Jsoup.connect(iframeAffer).cookies(response.cookies()).execute();
		    
		    //System.out.println(responseFn.cookies());
		    iframeContent  = responseFn.body();
		    System.out.println(iframeContent);
		}
		
		Document iframeDoc = Jsoup.parse(iframeContent);
		Element script = iframeDoc.select("script").get(1); // Get the script part

		String content = script.html();
		
		System.out.println(content);
		
		String[] contentArr = content.split("\n");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "downprocess");
		params.put("ves", "1");
		params.put("websign", "");
		
		*//**
		 * 
		 * 
		 * 
		 * var ajaxdata = '?ctdf';
			var ispostdowns = 'UDZaZAk4U2JUXVdoUWEAPFc4BjNeMVFgV2FbaQFuV25TdVBzCGgCZwlpVzIDYl1kUz0OOABmAjlQYg_c_c';
			$.ajax({
				type : 'post',
				url : '/ajaxm.php',
				data : { 'action':'downprocess','signs':ajaxdata,'sign':ispostdowns,'ves':1,'websign':'','websignkey':'qNpa' },
				//data : { 'action':'downprocess','sign':sign,'sign':sign,'ves':1},
				dataType : 'json',
				success:function(msg){
					var date = msg;
					if(date.zt == '1'){
											$("#go").html("<a href="+date.dom+"/file/"+ date.url +" target=_blank rel=noreferrer>下载</a>");
						setTimeout('$("#outime").css("display","block");',1800000);
										}else{
						$("#go").html("网页超时，请刷新");
					};
					
				},
				error:function(){
					$("#go").html("获取失败，请刷新");
				}
		
			});

		 * 
		 * 
		 * 
		 * 
		 * 
		 *//*
		
		for (String html : contentArr) {
			
			if (html.contains("//")) {
				continue;
			}
			
			if (html.contains("var ajaxdata")) {
				params.put("signs", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("var vsign")) {
				params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("var ispostdowns")) {
				params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("var pdownload")) {
				params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("var msigns")) {
				params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("var postdown")) {
				params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("var s_sign")) {
				params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
			}else if (html.contains("data :")) {
				String[] htmlArr = html.split(":");
				String websignKey = htmlArr[htmlArr.length - 1].replace("'", "").replace("}", "").replace(",", "").trim();
				//'VDJUag4_aBzYIAQM8ATECPlo1BTACbVRlUGZaaFI9BD1QdlJxWzsAZVU1AmBWMQUzVzUDMVc6AjRQYw_c_c','ves'
				String signStr = htmlArr[4];
				//如果sign直接写在ajax data中
				if (!StringUtils.isEmpty(signStr) && signStr.indexOf("','v") > -1) {
					params.put("sign", signStr.replaceAll("','ves'", "").replaceAll("'", "").trim());
				}
				params.put("websignkey", websignKey);
				break;
			}
		}
		
		List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
		paramsList.add(new BasicNameValuePair("action", params.get("action")));
		paramsList.add(new BasicNameValuePair("signs", params.get("signs")));
		paramsList.add(new BasicNameValuePair("sign", params.get("sign")));
		paramsList.add(new BasicNameValuePair("ves", params.get("ves")));
		paramsList.add(new BasicNameValuePair("websign", params.get("websign")));
		paramsList.add(new BasicNameValuePair("websignkey", params.get("websignkey")));
		
		//UM_distinctid=17b447b3a1916d-0036a02eb37d8d-4343363-144000-17b447b3a1a5ee; CNZZDATA5289133=cnzz_eid%3D1236576885-1628937230-%26ntime%3D1628937230; CNZZDATA1253610888=181102480-1628936242-%7C1628936242; CNZZDATA422640=cnzz_eid%3D1603193747-1628937477-%26ntime%3D1628937477
		//response.cookies()
		response.cookies().put("UM_distinctid", "17b447b3a1916d-0036a02eb37d8d-4343363-144000-17b447b3a1a5ee");
		response.cookies().put("CNZZDATA5289133", "cnzz_eid%3D1236576885-1628937230-%26ntime%3D1628937230");
		response.cookies().put("CNZZDATA1253610888", "181102480-1628936242-%7C1628936242");
		response.cookies().put("CNZZDATA422640", "cnzz_eid%3D1603193747-1628937477-%26ntime%3D1628937477");
		
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("origin", "https://kjsdd.lanzoui.com");
		headerMap.put("referer", iframeAffer);
		
		
		Connection.Response responseFn = Jsoup.connect("https://kjsdd.lanzoui.com/ajaxm.php").method(Method.POST)
				.data("action", params.get("action"))
				.data("signs", params.get("signs"))
				.data("sign", params.get("sign") == null ? "" : params.get("sign"))
				.data("ves", params.get("ves"))
				.data("websign", params.get("websign"))
				.data("websignkey", params.get("websignkey"))
				.headers(headerMap)
				.cookies(response.cookies()).followRedirects(true)
		        .execute();
		
		
		HttpPostReq req = new HttpPostReq("https://kjsdd.lanzoui.com/ajaxm.php", null, paramsList);
		String result = req.excuteReturnStr();
		//System.out.println(responseFn.body());
		//date.dom+"/file/"+ date.url
		//String paraStr = MapUtil.mapJoin(params, false, true);
		//String paraStr = "action=downprocess&signs=%3Fctdf&sign=AWcGOFprVWRUXVBvAjJSbgFvUGJSPFZhAzxRbl0zBDYBJwckCGgOa1IyAWUHa1JqAWgEMlA2UGNQYg_c_c&ves=1&websign=&websignkey=8PaD";
		Map<String, Object> resultMap = JsonUtils.jsonToPojo(responseFn.body(), Map.class);
		
		String downUrl = resultMap.get("dom") + "/file/" + resultMap.get("url");
		System.out.println(downUrl);*/
		//Document responseFn = Jsoup.connect("https://kjsdd.lanzoui.com/ajaxm.php").requestBody(paraStr).header("Content-Type", "application/x-www-form-urlencoded").post();
				
				//requestBody(JsonUtils.objectToJson(params))
		//System.out.println("????" + responseFn.data());
		
		
	}

}
