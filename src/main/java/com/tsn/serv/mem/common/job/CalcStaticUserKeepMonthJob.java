package com.tsn.serv.mem.common.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.mem.service.statis.StatisDataService;

public class CalcStaticUserKeepMonthJob implements Job{

	private static Logger logger = LoggerFactory.getLogger(ChangeOrderJob.class);

	private StatisDataService statisDataService;
	

	public CalcStaticUserKeepMonthJob() {
		this.statisDataService = SpringContext.getBean(StatisDataService.class);
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug(">>>>>>> start CalcStaticUserKeepJob {}", new Date());

		//获取当前时间，获取当前月，然后对该月之前的12个月做 留存计算
		Date currTime = new Date();
		statisDataService.calcAddAndUpdateStaticUserKeepMonth(DateUtils.getCurrYMD(currTime, "yyyyMM"));
	}
}