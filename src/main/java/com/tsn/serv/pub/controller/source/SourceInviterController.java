package com.tsn.serv.pub.controller.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.pub.service.mem.MemFeignService;
import com.tsn.serv.pub.service.source.SourceInvitService;

@RestController
@RequestMapping("source")
public class SourceInviterController {

	@Autowired
	private MemFeignService memService;

	@Autowired
	private SourceInvitService sourceInvitService;

	@GetMapping("/path")
	public Response<?> getFeedbackPage(String sourcePath) {
		Response<?> resp = memService.getSourceInviterByPath(sourcePath);
		return resp;
	}

	/**
	 * 自定义开始日期和结束日期统计邀请码填写量
	 * @param statDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/statEnd")
	public Response<?> upSourceInvitByStatEnd(String statDate, String endDate) {
		sourceInvitService.upSourceInvitByStatEnd(statDate, endDate);
		return Response.ok();
	}

	/**
	 * 指定日期统计邀请码填写量
	 * @param date
	 * @return
	 */
	@GetMapping("/date")
	public Response<?> upSourceInvitDayByDate(String date) {
		sourceInvitService.upSourceInvitDayByDate(date);
		return Response.ok();
	}
}
