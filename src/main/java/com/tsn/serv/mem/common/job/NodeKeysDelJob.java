package com.tsn.serv.mem.common.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.mem.service.node.NodeKeyService;

public class NodeKeysDelJob implements Job{
	
	private static Logger logger = LoggerFactory.getLogger(NodeKeysDelJob.class);
	
	private NodeKeyService nodeService;
	
	private String addCron;
	
	public NodeKeysDelJob() {
		this.nodeService = (NodeKeyService) SpringContext.getBean("nodeKeyService");
		this.addCron = Env.getVal("node.key.add.cron");
		
	}
	
	/**
	 * 每隔1天执行一次
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		logger.debug(">>>>>>> start NodeKeysDelJob {}", new Date());
		
		/*try {
			Date currdate = context.getScheduledFireTime();
			
			CronExpression exp = new CronExpression(addCron);
			
			if (exp.isSatisfiedBy(currdate)) {
				
				NodeKey nodeKey = nodeService.getNodekeyByAge0();
				//如果存在年龄为0的记录，且没有写入服务器，则先将该keys继续写入节点服务器
				if (nodeKey != null && nodeKey.getStatus() == 0) {
					new KeyTaskManager(10, nodeKey).runTaskAddKeys();
				}else {
					NodeKey nodekey = nodeService.createMemNodekey();
					new KeyTaskManager(10, nodekey).runTaskAddKeys();
				}
				
				return;
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			List<NodeKey> nodeKey = nodeService.getNodeKeysByMoreAge3();
			
			if (nodeKey == null || nodeKey.isEmpty()) {
				throw new Exception("**** end query age > 3 , return nodeKeys not exists");
			}
			
			//如果有多个先删除年龄最老的一个，其他的后面定时任务一个个删除
			NodeKey delNodekey = nodeKey.get(nodeKey.size() - 1);
			
			//交给管理器处理，处理流程固定10个删除成功后，会将删除成功的key放入完成队中中，固定数量也是10，如果完成队列数据满了，这是触发操作数据库，更改数据库状态
			new KeyTaskManager(10, delNodekey).runTaskDelKeys();
			
			//nodeService.createNodekey();
			logger.debug(">>>>>>> end NodeKeysDelJob, success ");
		} catch (Exception e) {
			logger.error("**** end NodeKeysDelJob exception , e = {}", e.toString());
		}*/
		
	}
}