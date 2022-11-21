package com.tsn.serv.pub.service.feedback;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.pub.entity.feedback.Feedback;
import com.tsn.serv.pub.mapper.feedback.FeedbackMapper;

@Service
public class FeedbackService {

	@Autowired
	private FeedbackMapper feedbackMapper;

	public void getFeedbackPage(Page page) {
		List<Feedback> feedbackList = feedbackMapper.getFeedbackPage(page);
		page.setData(feedbackList);
	}

	public void addFeedback(Feedback feedback) {
		/*MemInfo memInfo = memService.selectUserByPhone(feedback.getMemPhone());
		if (memInfo != null) {
			feedback.setMemId(memInfo.getMemId());
			feedback.setMemPhone(memInfo.getMemPhone());
			feedback.setMemName(memInfo.getMemNickName());
		}*/

		feedback.setCreateTime(new Date());
		feedbackMapper.insertDynamic(feedback);
	}

	public void upFeedback(Feedback feedback) {
		feedbackMapper.updateDynamic(feedback);
	}
}
