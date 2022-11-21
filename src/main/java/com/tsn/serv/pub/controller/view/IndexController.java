package com.tsn.serv.pub.controller.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.common.utils.utils.cons.FHCons;
import com.tsn.common.utils.utils.tools.http.HttpGetReq;
import com.tsn.common.utils.utils.tools.http.entity.HttpRes;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.security.Base64Utils;
import com.tsn.common.utils.web.utils.env.Env;

@Controller
public class IndexController {
	
	private static Logger log = LoggerFactory.getLogger(IndexController.class);
	
	@GetMapping("/gg")
	public ModelAndView ggIndex(String rd, Model model, HttpServletRequest request) {
		Map<String, List<String>> downMap = getDownAddr("kjs");
    	model.addAttribute("down", downMap);
    	//如果不是，直接跳转到目标页面ss
	    return new ModelAndView("gg_index");
	}
	
	@GetMapping("/")
	public ModelAndView index(String rd, Model model, HttpServletRequest request) {
		
		//判断浏览器的类型，如果不是直接跳转linkcode对应的网址；如果是先跳到防洪自定义页面
		String ua = request.getHeader("user-agent").toLowerCase();
		
		ua = ua == null ? "" : ua.toLowerCase();
		
		String reqPath = request.getRequestURL().toString();
		
		reqPath = reqPath.replace("http:", "https:");
		reqPath = reqPath.replace(":443", "");
		
		String fhRedirectUrl = Env.getVal("fh.redirect.url");
		
		String websiteUrl = Env.getVal("webiste.urls");
		websiteUrl = StringUtils.isEmpty(websiteUrl) ? "" : websiteUrl;
		
		String websiteRedirectUrl = Env.getVal("webiste.redirect.urls");
		websiteRedirectUrl = StringUtils.isEmpty(websiteRedirectUrl) ? "" : websiteRedirectUrl;
		
		String userWebsiteRedirectUrl = Env.getVal("webiste.user.redirect.urls");
		userWebsiteRedirectUrl = StringUtils.isEmpty(userWebsiteRedirectUrl) ? "" : userWebsiteRedirectUrl;
		
		String kefuWebsiteRedirectUrl = Env.getVal("webiste.kefu.redirect.urls");
		
		String domainName = getDomainName(reqPath);
		
		log.info("index: req:{}, ua:{}, fhUrl:{}", reqPath, ua, fhRedirectUrl);
		
		//如果是微信浏览器,返回防红页面
        if (ua.indexOf("micromessenger") > 0){
        	model.addAttribute("targetUrl", reqPath);
        	
        	String r = Base64Utils.encodeToString(reqPath);
        	
        	return new ModelAndView("redirect:https://" + fhRedirectUrl + "/link?r=" + r + "&s=" + FHCons.sign(reqPath));
        }
        
        //如果访问的域名是kjsjsq.com, 如果有重定向链接，则进行跳转，如果没有就跳转到 kjs用户管理页跳板页面
        if (reqPath.contains("kjslink.me") || reqPath.contains("kjslink.xyz") ||reqPath.contains("kjslink.net") || userWebsiteRedirectUrl.contains(domainName)) {
        	if (!StringUtils.isEmpty(rd)) {//rd也暂时没有用
    			return new ModelAndView("redirect:" + rd);
    		}
    		
        	//使用官网跳转页
    	    return new ModelAndView("skip_website_index");
        } if (reqPath.contains("kjskjs.xyz") || websiteRedirectUrl.contains(domainName)) { // 跳转到官网跳板页 先不考虑官网
        	if (!StringUtils.isEmpty(rd)) {
    			return new ModelAndView("redirect:" + rd);
    		}
    		
        	//使用用户后台网站
    	    return new ModelAndView("skip_usermana_index");
        } /*if (kefuWebsiteRedirectUrl.contains(domainName)) { // 跳转到官网跳板页 先不考虑官网
        	if (!StringUtils.isEmpty(rd)) {
    			return new ModelAndView("redirect:" + rd);
    		}
    		
        	//使用官网跳转页
    	    return new ModelAndView("skip_kefu_index");
        } */else if (reqPath.contains("kuaijiasu.xyz") || reqPath.contains("kuaijiasujiasuqi.com") || reqPath.contains("kjskjs-a.xyz")
        		|| reqPath.contains("kjskjs-b.xyz") || reqPath.contains("kjskjs-c.xyz") || reqPath.contains("kjskjs-d.xyz") || reqPath.contains("kjskjs-e.xyz") || websiteUrl.contains(domainName) ) {
        	//如果不是，直接跳转到目标页面
        	
        	Map<String, List<String>> downMap = getDownAddr("kjs");
        	model.addAttribute("down", downMap);
        	
    	    return new ModelAndView("kjs_index");
        } else if (reqPath.contains("heibaojsq.xyz") || reqPath.contains("heibaojiasuqi.com")) {
        	
        	Map<String, List<String>> downMap = getDownAddr("hb");
        	model.addAttribute("down", downMap);
        	
        	//如果不是，直接跳转到目标页面hb_index
    	    return new ModelAndView("hb/index");
        } else if (reqPath.contains("gagajsq.com")) {
        	
        	Map<String, List<String>> downMap = getDownAddr("gg");
        	model.addAttribute("down", downMap);
        	
        	//如果不是，直接跳转到目标页面gg_index
    	    return new ModelAndView("gg/index");
        } else {
        	Map<String, List<String>> downMap = getDownAddr("kjs");
        	model.addAttribute("down", downMap);
        	//如果不是，直接跳转到目标页面ss
    	    return new ModelAndView("kjs_index");
        }
        
		
	}
	
	public static String getDomainName(String url) {
		String host = "";
		try {
			URL Url = new URL(url);
			host = Url.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return host;
	}
	
	private Map<String, List<String>> getDownAddr(String platType) {
		Map<String, List<String>> downMap = new HashMap<String, List<String>>();
		String downUrl = "";
		if ("kjs".equals(platType)) {
			downUrl = "https://mapi.kuaijiasuhouduan.com/auth_api/appDownloa/all";
			//默认
			downMap.put("win", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/kjs/pc"})));
			downMap.put("ad", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/kjs/ad"})));
			downMap.put("ios",new ArrayList<>(Arrays.asList(new String[]{"https://kjskjs.com"})));
			downMap.put("mac", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/kjs/mac"})));
		}else if ("hb".equals(platType)) {
			downUrl = "https://mapi.heibaohouduan.com/auth_api/appDownloa/all";
			//默认
			downMap.put("win", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/hb/pc"})));
			downMap.put("ad", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/hb/ad"})));
			downMap.put("ios",new ArrayList<>(Arrays.asList(new String[]{"http://note.youdao.com/s/F629CgsM"})));
			downMap.put("mac", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/hb/mac"})));
		}else if ("gg".equals(platType)) {
			downUrl = "https://mapi.gagahouduan.com/auth_api/appDownloa/all";
			//默认
			downMap.put("win", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/gg/pc"})));
			downMap.put("ad", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/gg/ad"})));
			downMap.put("ios",new ArrayList<>(Arrays.asList(new String[]{"http://note.youdao.com/s/F629CgsM"})));
			downMap.put("mac", new ArrayList<>(Arrays.asList(new String[]{"https://s456s456.com/down/gg/mac"})));
		}
		
		
		try {
			HttpRes res = new HttpGetReq(downUrl).excuteReturnObj();
			if ("000".equals(res.getCode())) {
				String content = res.getData();
				@SuppressWarnings("unchecked")
				Map<String, Object> contentMap = JsonUtils.jsonToPojo(content, Map.class);
				List<Map> pathList = JsonUtils.jsonToList(JsonUtils.objectToJson(contentMap.get("data")), Map.class);
				
				if (pathList == null || pathList.isEmpty()) {
					return downMap;
				}
				
				//清空list
//				downMap.forEach((key, list) -> {
//					list.clear();
//				});
				
				//先清空
				for (Map map : pathList) {
					String type = (String) map.get("type");
					String typeName = "";
					if ("1".equals(type)) {
						typeName = "win";
					}else if ("2".equals(type)) {
						typeName = "ad";
					}else if ("3".equals(type)) {
						typeName = "ios";
					}else if ("4".equals(type)) {
						typeName = "mac";
					}
					
					downMap.get(typeName).clear();
				}
				
				
				//1 win 2 ad 4 mac
				for (Map map : pathList) {
					String type = (String) map.get("type");
					String typeName = "";
					if ("1".equals(type)) {
						typeName = "win";
					}else if ("2".equals(type)) {
						typeName = "ad";
					}else if ("3".equals(type)) {
						typeName = "ios";
					}else if ("4".equals(type)) {
						typeName = "mac";
					}
					
					if (StringUtils.isEmpty(typeName)) {
						continue;
					}
					
					List<String> valList = downMap.get(typeName);
					
					if (valList.size() < 2) {
						valList.add((String)map.get("path"));
					}
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return downMap;
		
		
	}
	
	@GetMapping("/private.html")
	public ModelAndView toReportPage(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("kjs_private");
	}
	
	@GetMapping("/priv.html")
	public ModelAndView toPrivPage(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("kjs_private_v2");
	}
	
	@GetMapping("/pay/priv.html")
	public ModelAndView toPayPriv(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("kjs_recharge_priv");
	}

	@GetMapping("/protocol.html")
	public ModelAndView toProtocolPage(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("kjs_protocol");
	}

}
