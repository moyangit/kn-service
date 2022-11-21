package com.tsn.serv.pub.controller.ad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.service.ad.AdStatisService;

@Controller
@RequestMapping("advert")
public class AdStatisController {
	
	@Autowired
	private AdStatisService adStatisService;
	
	private static Logger logger = LoggerFactory.getLogger(AdStatisController.class);
	
	@GetMapping("/redirect/{adId}")
	@AuthClient()
	public ModelAndView queryAdByPos(@PathVariable String adId, String uId, String redirectUrl) {
		try {
			logger.info("AdStatisController queryAdByPos() uId = {} ,adId = {}， redirectUrl = {}", uId, adId, redirectUrl);
			adStatisService.statisClick(adId, uId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ModelAndView("redirect:" + redirectUrl);
	}
	
}
