package com.tsn.serv.pub.common.job;

import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.pub.service.source.SourceInvitService;

public class SourceInvitHourJob implements Job{

	private static Logger logger = LoggerFactory.getLogger(SourceInvitHourJob.class);

	private SourceInvitService sourceInvitService;

	public SourceInvitHourJob() {
		this.sourceInvitService = (SourceInvitService) SpringContext.getBean("sourceInvitService");

	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug(">>>>>>> execute SourceInvitService {}", new Date());
		
		//获取上一次执行的时间
		Date preTime = context.getPreviousFireTime();
		
		//获取yyyyMMddHH
		// String YMDHTime = DateUtils.getCurrYMD(preTime, "yyyyMMddHH");
		//获取HH
		// String hour = YMDHTime.substring(YMDHTime.length() - 2);
		if (preTime == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			preTime = calendar.getTime();
		}
		//feign嗲用接口执行计算
		sourceInvitService.calcSourceInvitHour(preTime.getTime());
		
	}
}