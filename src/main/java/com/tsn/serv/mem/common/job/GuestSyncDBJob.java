package com.tsn.serv.mem.common.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.mem.service.mem.MemGuestInfoService;

public class GuestSyncDBJob implements Job{

	private static Logger logger = LoggerFactory.getLogger(GuestSyncDBJob.class);

	private MemGuestInfoService memGuestInfoService;

	public GuestSyncDBJob() {
		this.memGuestInfoService = (MemGuestInfoService) SpringContext.getBean("memGuestInfoService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			
			logger.info(">>>>>>> start GuestSyncDBJob syncGuest2DBByHour {}", context.getJobRunTime());
			
			Date runDate = context.getFireTime();
			
			String yyyyMMdd = DateUtils.getCurrYMD(runDate, "yyyyMMdd");
			
			logger.info("syncGuest2DBByHour yyyyMMdd = {}", yyyyMMdd);
			
			Date startDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(yyyyMMdd + " 00:00:00");
			
			long startTime = startDate.getTime();
			if (context.getPreviousFireTime() != null) {
				startTime = context.getPreviousFireTime().getTime();
			}
			
			//memGuestInfoService.syncGuest2DBByHour(yyyyMMdd, startTime, runDate.getTime());
		} catch (Exception e) {
			logger.error("GuestSyncDBJob syncGuest2DBByHour, e = {}", e);
		}
	}
	
}