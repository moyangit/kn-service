package com.tsn.serv.pub.controller.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.entity.feedback.Feedback;
import com.tsn.serv.pub.service.feedback.FeedbackService;

@RestController
@RequestMapping("feedback")
public class FeedbackController {

	@Autowired
	private FeedbackService feedbackService;

	@PostMapping("/")
	//@AuthClient(client = AuthEnum.bea_us)
	public Response<?> addFeedback(@RequestBody Feedback feedback) {
		feedbackService.addFeedback(feedback);
		return Response.ok();
	}

	@GetMapping("/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getFeedbackPage(Page page) {
		feedbackService.getFeedbackPage(page);
		return Response.ok(page);
	}

	@PutMapping("/")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> upFeedback(@RequestBody Feedback feedback) {
		feedbackService.upFeedback(feedback);
		return Response.ok();
	}
}
