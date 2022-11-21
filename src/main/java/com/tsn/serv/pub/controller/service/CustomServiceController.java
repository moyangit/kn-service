package com.tsn.serv.pub.controller.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.pub.service.feedback.FeedbackService;

@RestController
@RequestMapping("cust/serv")
public class CustomServiceController {

	@Autowired
	private FeedbackService feedbackService;

	@GetMapping("/url")
	public Response<?> getFeedbackPage() {
		//String cusServiceUrl = "http://chat.ahcdialogchat.com/chat/h5/chatLink.html?channelId=2O9dNy";
		
		String addr = Env.getVal("cus.serv.addr");
		
		if (!StringUtils.isEmpty(addr)) {
			return Response.ok(addr);
		}
		
		String cusServiceUrl = "https://chat.aiheconglink.com/chat/h5/chatLink.html?channelId=2O9dNy";
		
		return Response.ok(cusServiceUrl);
	}
}