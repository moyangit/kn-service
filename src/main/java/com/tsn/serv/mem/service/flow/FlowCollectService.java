package com.tsn.serv.mem.service.flow;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.common.enm.v2ray.FlowLimitEnum;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.mem.common.utils.FlowCalcManager;
import com.tsn.serv.mem.common.utils.FlowStat;
import com.tsn.serv.mem.entity.flow.FlowDay;
import com.tsn.serv.mem.entity.flow.FlowLimit;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.service.limit.LimitOperaService;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.statis.StatisDataService;

@Service
public class FlowCollectService {
	
	@Autowired
	private FlowService flowService;
	
	@Autowired
	private FlowLimitService flowLimitService;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private StatisDataService statisDataService;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private LimitOperaService limitOperaService;
	

	@Transactional
	public void collectFlow(Map<String, Object> flowStat) {
		
		if (flowStat == null) {
			return;
		}
		
		String memId = (String) flowStat.get("userId");
		String host = (String) flowStat.get("host");
		long readUsed = Long.parseLong(String.valueOf(flowStat.get("readUsed")));
		long writeUsed = Long.parseLong(String.valueOf(flowStat.get("writeUsed")));
		long createDate = Long.parseLong(String.valueOf(flowStat.get("createTime")));
		
		FlowStat flowStatTmp = FlowCalcManager.getInstance().addServerAppFlow(memId, new FlowStat(memId, host, readUsed, writeUsed, createDate));
		
		if (flowStatTmp.isStore()) {
			
			long readUsedTemp = Long.parseLong(String.valueOf(flowStatTmp.getReadUsed()));
			long writeUsedRemp = Long.parseLong(String.valueOf(flowStatTmp.getWriteUsed()));
			limitOperaService.calcUserFlowAndLimit(memId, readUsedTemp, writeUsedRemp);
			
			//先更新在清空key,清空key表示重新计算
			updateFlow(flowStatTmp);
			FlowCalcManager.getInstance().clearMemFlow(memId);
		}
		
		statisDataService.doOnlineAndAtivceUserCount(EventMsg.createOnlineUserRegMsg(memId));
		
		MemInfo memInfo = memService.queryMemCacheById(memId);
		if (memInfo != null && !StringUtils.isEmpty(memInfo.getDeviceNo())) {
			EventMsgProducter.build().sendEventMsg(EventMsg.createDeviceUserRegMsg(memInfo.getDeviceNo()));
		}
		
	}
	
	@Transactional
	public void updateFlow(FlowStat flowStat) {
		String memId = flowStat.getUserId();
		String host = flowStat.getHost();
		long readUsed = Long.parseLong(String.valueOf(flowStat.getReadUsed()));
		long writeUsed = Long.parseLong(String.valueOf(flowStat.getWriteUsed()));
		//long readTime = Long.parseLong(String.valueOf(flowStat.getReadTime()));
		
		FlowDay fd = new FlowDay();
		fd.setDay(DateUtils.getCurrYMD("yyyyMMdd"));
		fd.setMemId(memId);
		fd.setHost(host);
		//服务器的上行流量对于用户而言是下载/下行流量，反之
		fd.setUpFlow(readUsed);
		fd.setDownFlow(writeUsed);
		fd.setCreateDate(new Date());
		fd.setUpdateDate(new Date());
		
		//更新自然日
		flowService.updateFlowDay(fd);
		
		//根据用户的购买周期进行更新使用总流量，如果不在使用周期范围内，会重新重置流量，同时更新数据记录
		FlowLimit flowLimit = new FlowLimit();
		flowLimit.setHost(host);
		flowLimit.setMemId(memId);
		flowLimit.setUpdateTime(new Date());
		flowLimit.setCurrUseUpFlow(readUsed);
		flowLimit.setCurrUseDownFlow(writeUsed);
		flowLimit.setFlowLimitType(FlowLimitEnum.month.name());
		flowLimit.setLimitUploadTime(fd.getCreateDate());
		flowLimitService.updateAndAddFlowLimitForMem(flowLimit);
	}

}
