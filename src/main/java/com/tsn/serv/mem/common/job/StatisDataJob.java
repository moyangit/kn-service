package com.tsn.serv.mem.common.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.mem.service.statis.StatisDataService;

public class StatisDataJob implements Job{

	private static Logger logger = LoggerFactory.getLogger(ChangeOrderJob.class);

	private StatisDataService statisService;
	
	public StatisDataJob() {
		this.statisService = (StatisDataService) SpringContext.getBean("statisService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug(">>>>>>> start StatisDataJob {}", new Date());

		statisService.statis();
	}
}