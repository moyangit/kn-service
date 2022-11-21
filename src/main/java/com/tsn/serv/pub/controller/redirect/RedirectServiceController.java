package com.tsn.serv.pub.controller.redirect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.pub.service.feedback.FeedbackService;

@RestController
@RequestMapping("redirect")
public class RedirectServiceController {

	@Autowired
	private FeedbackService feedbackService;

	@GetMapping("/box")
	public Response<?> getFeedbackPage(String uId) {
		//String cusServiceUrl = "http://chat.ahcdialogchat.com/chat/h5/chatLink.html?channelId=2O9dNy";
		
		String addr = Env.getVal("redirect.box.url");
		
		if (!StringUtils.isEmpty(addr)) {
			return Response.ok(addr);
		}
		
		String cusServiceUrl = "https://web.heibaojiasuqi.com/website/page/box.html?uId="+uId;
		
		return Response.ok(cusServiceUrl);
	}
}