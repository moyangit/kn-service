package com.tsn.serv.pub.controller.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.serv.common.cons.redis.RedisKey;

@Controller
@RequestMapping("link")
public class FHViewController {
	
	@Autowired
	private RedisHandler redisHandler;
	
	/**
	 * 
	 * @param model
	 * @param linkCode 和你要跳转的目标地址进行绑定
	 * @return
	 */
	@GetMapping("/share/{linkCode}")
	public ModelAndView toFHPage(Model model, @PathVariable String linkCode, HttpServletRequest request){
		
		//判断浏览器的类型，如果不是直接跳转linkcode对应的网址；如果是先跳到防洪自定义页面
		String ua = request.getHeader("user-agent").toLowerCase();
		
		String targetUrl = (String) redisHandler.get(RedisKey.SHARE_LINK_CODE + linkCode);
		
		if (StringUtils.isEmpty(targetUrl)) {
			return new ModelAndView("error");
		}
		
		//如果是微信浏览器,返回防红页面
        if (ua.indexOf("micromessenger") > 0){
        	model.addAttribute("targetUrl", targetUrl);
        	return new ModelAndView("fh");
        }
        
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("redirect:" + targetUrl);
	}
	
	@GetMapping("/report.html")
	public ModelAndView toReportPage(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("fh_report");
	}
	
	@GetMapping("/report_success.html")
	public ModelAndView toReportSuccess(Model model, HttpServletRequest request){
		
        //如果不是，直接跳转到目标页面
	    return new ModelAndView("fh_report_success");
	}

}
