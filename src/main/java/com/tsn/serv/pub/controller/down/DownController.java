package com.tsn.serv.pub.controller.down;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.serv.pub.common.down.DownManager;
import com.tsn.serv.pub.common.down.DownManager.DownUrl;

@Controller
@RequestMapping("down")
public class DownController {
	
/*	@GetMapping("{appType}/{type}")
	public ModelAndView toFHPage(Model model, @PathVariable String appType, @PathVariable String type, HttpServletRequest request){
		
		if ("kn".equals(appType)) {
			
			DownUrl downUrl = DownManager.build().getKnMap().get(type);
			
			if (downUrl == null) {
				//如果不是，直接跳转到目标页面
			    return new ModelAndView("error");
			}
			
			 //如果不是，直接跳转到目标页面
		    return new ModelAndView("redirect:" + downUrl.getDownUrl());
		} 
	    
		return new ModelAndView("error");
	}*/
	
	@GetMapping("/{type}")
	public ModelAndView toFHPage(Model model, @PathVariable String type, HttpServletRequest request){
		
		DownUrl downUrl = DownManager.build().getKnMap().get(type);
		
		if (downUrl == null) {
			//如果不是，直接跳转到目标页面
		    return new ModelAndView("error");
		}
		
		 //如果不是，直接跳转到目标页面
	    return new ModelAndView("redirect:" + downUrl.getDownUrl());
	}
	
//	private String getOwnServer(String appType) {
//		
//		
//	}
}

