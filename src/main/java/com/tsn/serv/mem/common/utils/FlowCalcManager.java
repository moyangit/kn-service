package com.tsn.serv.mem.common.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.tsn.common.utils.utils.tools.pool.SynchronousPoolUtils;
import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.mem.service.flow.FlowCollectService;
import com.tsn.serv.mem.service.limit.LimitOperaService;

public class FlowCalcManager {
	
	private static FlowCalcManager allPathBalanceManager = new FlowCalcManager();
	
	// 线程池执行定时任务
    //private ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
    
    private Logger log = LoggerFactory.getLogger(FlowCalcManager.class);
    
    private final Cache<String, FlowStat> userAndFLowCahe;
    
    private FlowCollectService flowCollectService;
    
    private LimitOperaService limitOperaService;
	
	private FlowCalcManager() {
		
		flowCollectService =  (FlowCollectService) SpringContext.getBean(FlowCollectService.class);
		
		limitOperaService =  (LimitOperaService) SpringContext.getBean(LimitOperaService.class);
		
		this.userAndFLowCahe = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).removalListener((RemovalListener<Object, FlowStat>) rn -> 
		{
			FlowStat flowStat = rn.getValue();
			
			if (flowStat == null) {
				return;
			}
			
			log.debug("--- userAndFLowCahe clean and push flow, reason:{} , key:{}, value:{}", rn.getCause(), rn.getKey(), rn.getValue());
			
			if (flowStat.getUsed() == null || flowStat.getUsed() == 0) {
				return;
			}
			
			//如果是存储过了，就不存储了,如果手动调动invicedate清空key也会触发，所有这里不能重复计算
			if (!flowStat.isStore()) {
				long readUsedTemp = Long.parseLong(String.valueOf(flowStat.getReadUsed()));
				long writeUsedRemp = Long.parseLong(String.valueOf(flowStat.getWriteUsed()));
				limitOperaService.calcUserFlowAndLimit(flowStat.getUserId(), readUsedTemp, writeUsedRemp);
				flowCollectService.updateFlow(flowStat);
			}
			
			
		}
	).build();
	}
	
//	public void startTask() {
//		//每隔10秒重新扫描失败列表是否有“活”的，固定时间
//		schedule.scheduleAtFixedRate(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					isLives();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					log.error("MonitTaskManager fail..............., e = {}", e);
//				}
//				
//			}
//		}, 60, 60, TimeUnit.SECONDS);
//	}
	
	//心跳检测
//	private void isLives() {
//		
//		//查询所有中转
//		List<ServerApp> serverAppList = serverAppService.listApp("0");
//		
//		for (ServerApp serverApp : serverAppList) {
//			
//			Integer appPort = serverApp.getAppPort();
//			
//			String serverIp = serverApp.getServer().getServerIp();
//			
//			if (!isHostConnectable(serverIp, appPort)) {
//				serverApp.setOutFlowPortStatus(2);
//			}else {
//				serverApp.setOutFlowPortStatus(1);
//			}
//			
//		}
//		
//		log.debug("monit proxy , serverAppList = {}", JsonUtils.objectToJson(serverAppList));
//		boolean result = serverAppService.updateBatchById(serverAppList);
//		log.debug("monit proxy success, result = {}", result);
//	}
	
	public FlowStat addServerAppFlow(String key, FlowStat flowStat) {
		
		synchronized (SynchronousPoolUtils.getWeakReference(key + ":flow")) {
			FlowStat flowStatTemp = this.userAndFLowCahe.getIfPresent(key);
			
			if (flowStatTemp == null) {
				this.userAndFLowCahe.put(key, flowStat);
				return flowStat;
			}
			
			flowStatTemp.addUsed(flowStat.getReadUsed(), flowStat.getWriteUsed());
			
			//如果要存储了，就清空key
//			if (flowStatTemp.isStore()) {
//				//先更新在清空key
//				//flowCollectService.updateFlow(flowStatTemp);
//				this.userAndFLowCahe.invalidate(key);
//			}
			
			return flowStatTemp;
        }
		
	}
	
	public void clearMemFlow(String key) {
		synchronized (SynchronousPoolUtils.getWeakReference(key + ":flow")) {
			this.userAndFLowCahe.invalidate(key);
		}
	}
	
	public static FlowCalcManager getInstance() {
		return allPathBalanceManager;
	}
	
}
