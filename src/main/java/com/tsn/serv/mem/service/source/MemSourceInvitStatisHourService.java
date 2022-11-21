package com.tsn.serv.mem.service.source;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.source.MemSourceInviter;
import com.tsn.serv.mem.entity.source.SourceInvitStatisHour;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.source.MemSourceInviterMapper;
import com.tsn.serv.mem.mapper.source.SourceInvitStatisHourMapper;

@Service
public class MemSourceInvitStatisHourService {
	
	private static Logger log = LoggerFactory.getLogger(MemSourceInvitStatisHourService.class);
	
	@Autowired
	private SourceInvitStatisHourMapper memSourceInvitStatisHourMapper;
	
	@Autowired
	private MemSourceInviterMapper memSourceInviterMapper;
	
	@Autowired
	private MemInfoMapper memMapper;
	
	public void insertSourceInvitStaticHour(String sourceCode, long calcTime) {
		
		log.info(">>start insertSourceInvitStaticHour, sourceCode = {}, calcTime = {}", sourceCode, calcTime);
		if (StringUtils.isEmpty(sourceCode)) {
			throw new IllegalArgumentException("time param can not be empty()");
		}
		
		Date calcDate = new Date(calcTime);
		
		String YMDHTime = DateUtils.getCurrYMD(calcDate, "yyyyMMddHH");
		String hourTime = YMDHTime.substring(YMDHTime.length() - 2);
		MemSourceInviter memSourceInviter =  memSourceInviterMapper.selectByInviterCode(sourceCode);
		
		if (memSourceInviter == null) {
			log.error("memSourceInviterMapper.selectByInviterCode(sourceCode) return is null");
			return;
		}
		log.info("memSourceInviterMapper.selectByInviterCode(sourceCode); sourceCode = {}， result= {}", sourceCode, JSONUtils.toJSONString(memSourceInviter));
		SourceInvitStatisHour sourceInvitStaticHour = new SourceInvitStatisHour();
		sourceInvitStaticHour.setCreateTime(new Date());
		sourceInvitStaticHour.setUpdateTime(sourceInvitStaticHour.getCreateTime());
		sourceInvitStaticHour.setDayTime(YMDHTime);
		sourceInvitStaticHour.setHourTime(hourTime);
		sourceInvitStaticHour.setSourceInvitCode(sourceCode);
		sourceInvitStaticHour.setSourceInvitId(String.valueOf(memSourceInviter.getId()));
		sourceInvitStaticHour.setSourceName(memSourceInviter.getSourceName());
		sourceInvitStaticHour.setSourcePath(memSourceInviter.getSourcePath());
		
		//查看用户表计算次数计算次数
		List<MemInfo> memInfo = memMapper.getUserListByInvitCodeAndHour(sourceInvitStaticHour.getSourceInvitId(), DateUtils.getCurrYMD(calcDate, "yyyy-MM-dd HH"));
		sourceInvitStaticHour.setNum(memInfo.size());
		
		//查询统计表中
		memSourceInvitStatisHourMapper.insertByIgnore(sourceInvitStaticHour);
		
		log.info(">>end insertSourceInvitStaticHour success");
	}
	
	@Transactional
	public void insertAllSourceInvitStaticHour(long calcTime) {
		
		log.info(">>start insertAllSourceInvitStaticHour, calcTime = {}", calcTime);
		
		Date calcDate = new Date(calcTime);
		
		String YMDHTime = DateUtils.getCurrYMD(calcDate, "yyyyMMddHH");
		String hourTime = YMDHTime.substring(YMDHTime.length() - 2);
		List<MemSourceInviter> sourceList = memSourceInviterMapper.selectAll();
		
		for (MemSourceInviter memSourceInviter : sourceList) {
			log.info("starting calc memSourceInviter = {}", memSourceInviter);
			SourceInvitStatisHour sourceInvitStaticHour = new SourceInvitStatisHour();
			sourceInvitStaticHour.setCreateTime(new Date());
			sourceInvitStaticHour.setUpdateTime(sourceInvitStaticHour.getCreateTime());
			sourceInvitStaticHour.setDayTime(YMDHTime);
			sourceInvitStaticHour.setHourTime(hourTime);
			sourceInvitStaticHour.setSourceInvitCode(memSourceInviter.getInviterCode());
			sourceInvitStaticHour.setSourceInvitId(String.valueOf(memSourceInviter.getId()));
			sourceInvitStaticHour.setSourceName(memSourceInviter.getSourceName());
			sourceInvitStaticHour.setSourcePath(memSourceInviter.getSourcePath());
			
			//查看用户表计算次数计算次数
			List<MemInfo> memInfo = memMapper.getUserListByInvitCodeAndHour(sourceInvitStaticHour.getSourceInvitId(), DateUtils.getCurrYMD(calcDate, "yyyy-MM-dd HH"));
			sourceInvitStaticHour.setNum(memInfo.size());
			
			//查询统计表中
			memSourceInvitStatisHourMapper.insertByIgnore(sourceInvitStaticHour);
			log.info("ending calc, success!");
		}
		
		log.info(">>end insertAllSourceInvitStaticHour, success!");
		
	}
	
	/**
	 * 根据邀请码，自然天来查询
	 * @param sourceCode
	 * @param ymdTime
	 * @return
	 */
	public List<SourceInvitStatisHour> querySourceInvitStatisHourByDay (String sourceCode, String ymdTime) {
		List<SourceInvitStatisHour> sourceInvitList = memSourceInvitStatisHourMapper.selectBySourceCodeAndDay(sourceCode, ymdTime);
		return sourceInvitList;
	}
	
	/**
	 * 根据邀请码。时间段来查询
	 * @param sourceCode
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<SourceInvitStatisHour> querySourceInvitStatisHourByTime (String sourceCode, String startTime, String endTime) {
		List<SourceInvitStatisHour> sourceInvitList = memSourceInvitStatisHourMapper.selectBySourceCodeAndTime(sourceCode, startTime, endTime);
		return sourceInvitList;
	}
	
	public List<SourceInvitStatisHour> querySourceInvitStatisHourByPathTime (String sourcePath, String startTime, String endTime) {
		List<SourceInvitStatisHour> sourceInvitList = memSourceInvitStatisHourMapper.selectBySourcePathAndTime(sourcePath, startTime, endTime);
		return sourceInvitList;
	}

	public List<String> getInviterChart(String source, String monthDate) {
		return memSourceInvitStatisHourMapper.getInviterChart(source, monthDate);
	}

	public List<String> getInviterChartTo(String source, String statDate, String endDate) {
		return memSourceInvitStatisHourMapper.getInviterChartTo(source, statDate, endDate);
	}

	public Map<String, Object> getInviterByDayTime(String source, String dayTime) {
		return memSourceInvitStatisHourMapper.getInviterByDayTime(source, dayTime);
	}
}
