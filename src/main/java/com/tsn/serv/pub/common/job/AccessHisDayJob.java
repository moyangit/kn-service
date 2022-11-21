package com.tsn.serv.pub.common.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.pub.service.access.AccessHisDayService;

public class AccessHisDayJob implements Job{

	private static Logger logger = LoggerFactory.getLogger(AccessHisDayJob.class);

	private AccessHisDayService accessHisDayService;

	public AccessHisDayJob() {
		this.accessHisDayService = (AccessHisDayService) SpringContext.getBean("accessHisDayService");

	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug(">>>>>>> start AccessHisDayService {}", new Date());

		accessHisDayService.upAccessHisDay();
	}
}