package com.tsn.serv.pub.controller.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.serv.auth.entity.app.AppDownload;
import com.tsn.serv.auth.service.app.AppDownloadService;
import com.tsn.serv.common.utils.BlackDomainsFilter;

@Controller
@RequestMapping("/")
public class OweController {
	
	@Autowired
	private AppDownloadService appDownloadService;
	
	@GetMapping("/")
	public ModelAndView index(String rd, Model model, HttpServletRequest request) {
		
		if (!BlackDomainsFilter.isOweValid(getDomainName(request))) {
			return new ModelAndView("error");
		}
		
		Map<String, List<String>> downMap = getDownAddr();
    	model.addAttribute("down", downMap);
    	
    	//如果不是，直接跳转到目标页面gg_index
	    return new ModelAndView("owe/index");
        
		
	}
	
	public static String getDomainName(HttpServletRequest request) {
		String reqPath = request.getRequestURL().toString();
		
		reqPath = reqPath.replace("http:", "https:");
		reqPath = reqPath.replace(":443", "");
		String host = "";
		try {
			URL Url = new URL(reqPath);
			host = Url.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return host;
	}
	
	private Map<String, List<String>> getDownAddr() {
		Map<String, List<String>> downMap = new HashMap<String, List<String>>();
		//默认
		downMap.put("win", new ArrayList<>(Arrays.asList(new String[]{"https://down.knsj.xyz/down/pc"})));
		downMap.put("ad", new ArrayList<>(Arrays.asList(new String[]{"https://down.knsj.xyz/down/ad"})));
		downMap.put("ios",new ArrayList<>(Arrays.asList(new String[]{"https://user.kuainiaojsq.xyz/page/subscribe.html"})));
		downMap.put("mac", new ArrayList<>(Arrays.asList(new String[]{"https://down.knsj.xyz/down/mac"})));
		
		List<AppDownload> downloadList = appDownloadService.getAppDownloadListUse();
		
		if (downloadList == null || downloadList.isEmpty()) {
			return downMap;
		}
		
		//清空list
//		downMap.forEach((key, list) -> {
//			list.clear();
//		});
		
		//先清空
		for (AppDownload appDownload : downloadList) {
			String type = appDownload.getType();
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
		for (AppDownload appDownload : downloadList) {
			String type = appDownload.getType();
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
				valList.add(appDownload.getPath());
			}
			
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
	
	
	@GetMapping("hb/protocol.html")
	public ModelAndView toHbProtocolPage(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("hb/protocol");
	}
	
	@GetMapping("/hb/pay/priv.html")
	public ModelAndView tohbPayPriv(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("hb/recharge_priv");
	}

}
