package com.tsn.serv.pub.common.job;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tsn.common.utils.utils.tools.quartz.JobScheManager;

@Component
public class AccessHisDayJobInit {
	
	private static Logger logger = LoggerFactory.getLogger(AccessHisDayJobInit.class);

	private String accessCron = "0 0 0/1 * * ?";
	// private String accessCron = "*/5 * * * * ?";

	@PostConstruct
	public void init(){
		try {
			// 每隔一个小时
			JobScheManager.getInstance().addJob("accessHisDayJob", "group-1", AccessHisDayJob.class, accessCron);
			JobScheManager.getInstance().addJob("sourceInvitHourJob", "group-1", SourceInvitHourJob.class, accessCron);
			JobScheManager.getInstance().start();
		} catch (Exception e) {
			logger.error("{}",e);
		} 
	}
}