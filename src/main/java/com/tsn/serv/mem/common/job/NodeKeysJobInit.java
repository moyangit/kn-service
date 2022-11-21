package com.tsn.serv.mem.common.job;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tsn.common.utils.utils.tools.quartz.JobScheManager;
import com.tsn.serv.mem.service.node.NodeKeyService;

@Component
public class NodeKeysJobInit{
	
	private static Logger logger = LoggerFactory.getLogger(NodeKeysJobInit.class);
	
	@Autowired
	private NodeKeyService nodeService;
		
	@Value("${node.key.del.cron:0 0 6 1/1 * ? *}")
	private String delCron;
	
	@Value("${node.key.add.cron:0 0 6 1/2 * ? *}")
	private String addCron;

	private String orderCron = "0 0/1 * * * ? *";
	
	@PostConstruct
	public void init(){
		try {//0 0/1 * * * ? *              0/5 * * * * ? 
			//JobScheManager.getInstance().addJob("nodeKeysJob", "group-1", NodeKeysDelJob.class, delCron);
			JobScheManager.getInstance().addJob("changeOrderJob", "group-1", ChangeOrderJob.class, orderCron);
			
			//每小时执行
			JobScheManager.getInstance().addJob("GuestSyncDBJob", "group-2", GuestSyncDBJob.class, "0 0 */1 * * ?");
			
			//每小时执行
			JobScheManager.getInstance().addJob("StatisDataJob", "group-3", StatisDataJob.class, "0 0 */1 * * ?");
			
			//每天凌晨2点执行
			JobScheManager.getInstance().addJob("calcStaticUserKeepDay", "group-1", CalcStaticUserKeepDayJob.class, "0 0 2 * * ?");
			
			//每个月的凌晨2点
			JobScheManager.getInstance().addJob("calcStaticUserKeepMonth", "group-1", CalcStaticUserKeepMonthJob.class, "0 0 2 1 * ?");
			
			JobScheManager.getInstance().start();
		} catch (Exception e) {
			logger.error("{}",e);
		} 
	}
	
	/**
	 * 每隔3天执行一次
	 */
	/*@Scheduled(cron = "0 0 0 1/3 * ? ")
	public void scheduled(){
		logger.debug(">>>>>>> start NodeKeysJob {}", new Date());
		try {
			nodeService.createNodekey();
			logger.debug(">>>>>>> end NodeKeysJob, success ");
		} catch (Exception e) {
			logger.error("**** end NodeKeysJob exception , e = {}", e.toString());
		}
		
	}*/
}