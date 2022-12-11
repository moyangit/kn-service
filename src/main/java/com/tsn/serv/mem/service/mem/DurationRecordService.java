package com.tsn.serv.mem.service.mem;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.common.enm.credits.convert.ConvertDurationEum;
import com.tsn.serv.mem.entity.credits.CreditsTask;
import com.tsn.serv.mem.entity.env.enm.EnvKeyEnum;
import com.tsn.serv.mem.entity.mem.DurationRecord;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.credits.CreditsTaskMapper;
import com.tsn.serv.mem.mapper.mem.DurationRecordMapper;
import com.tsn.serv.mem.service.env.EnvParamsService;

/**
 * 时长记录操作
 * @author work
 *
 */
@Service
public class DurationRecordService {

	@Autowired
	private DurationRecordMapper durationRecordMapper;
	@Autowired
	private CreditsTaskMapper creditsTaskMapper;
	
	@Autowired
	private EnvParamsService envParamsService;
	
	@Autowired
	private MemService memService;
	
	public void insert(DurationRecord durationRecord) {
		// TODO Auto-generated method stub
		durationRecordMapper.insert(durationRecord);
	}
	
	@Transactional
	public void addDurationOfInvite(String memId) {
		
		String duration = envParamsService.getValByKey(EnvKeyEnum.invit_people_reward_duration);
		
		if (StringUtils.isEmpty(duration)) {
			//duration = "30";//分钟
			return;
		}
		
		int durationInt = Integer.parseInt(duration);
		
		MemInfo memInfo = memService.queryMemById(memId);
		
		MemInfo memInfoTmp = new MemInfo();
		memInfoTmp.setMemId(memId);
		
		if (memInfo.isExpire()) {
			memInfoTmp.setLastRechargeDate(new Date());
			memInfoTmp.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), durationInt));
		}else {
			memInfoTmp.setSuspenDate(DateUtils.getCurrDateAddMinTime(memInfo.getSuspenDate(), durationInt));
		}
		memService.updateMemInfo(memInfoTmp);
		
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setConvertCardType(ConvertDurationEum.friend_invite.name());
		durationRecord.setConvertDuration(durationInt);
		durationRecord.setDurationSources(ConvertDurationEum.friend_invite.name());
		this.insert(durationRecord);
	}
	
	/**
	 * 邀请充值成功奖励
	 * @param memId
	 * @param cdkNo
	 * @param minTime
	 */
	@Transactional
	public void addDurationOfInvitCharge(String memId, Integer duration) {
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setConvertCardType(ConvertDurationEum.friend_invite.name());
		durationRecord.setConvertDuration(duration);
		durationRecord.setDurationSources(ConvertDurationEum.friend_invite.name());
		this.insert(durationRecord);
		
		memService.updateSuspenDateByMinute(memId, duration);
		
	}
	
	public void addDuration(String memId, String convertCardType, String convertSource, Integer convertDuration) {
		
		CreditsTask creditsTask = creditsTaskMapper.getCreditsTaskByTaskType("friend_invite","win");
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setConvertCardType(convertCardType);
		durationRecord.setConvertDuration(creditsTask.getTaskVal());
		durationRecord.setDurationSources(convertSource);
		this.insert(durationRecord);
	}
	
	
	public void addDurationOfStimulate(String memId,String orderNo) {
		
		CreditsTask creditsTask = creditsTaskMapper.getCreditsTaskByTaskType(ConvertDurationEum.ad_stimulate.name(),"win");
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setOrderNo(orderNo);
		durationRecord.setConvertCardType(ConvertDurationEum.ad_stimulate.name());
		durationRecord.setConvertDuration(creditsTask.getTaskVal());
		durationRecord.setDurationSources(ConvertDurationEum.ad_stimulate.name());
		this.insert(durationRecord);
	}
	
	public void addDurationOfAppraiseGoogle(String memId,String orderNo) {
		
		CreditsTask creditsTask = creditsTaskMapper.getCreditsTaskByTaskType(ConvertDurationEum.google_evaluate.name(),"ad");
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setOrderNo(orderNo);
		durationRecord.setConvertCardType(ConvertDurationEum.google_evaluate.name());
		durationRecord.setConvertDuration(creditsTask.getTaskVal());
		durationRecord.setDurationSources(ConvertDurationEum.google_evaluate.name());
		this.insert(durationRecord);
	}
	
	@Transactional
	public void addDurationCDK(String memId, String cdkNo, int minTime) {
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setOrderNo(cdkNo);
		durationRecord.setConvertCardType(ConvertDurationEum.CDK.name());
		durationRecord.setConvertDuration(minTime);
		durationRecord.setDurationSources(ConvertDurationEum.CDK.name());
		this.insert(durationRecord);
		
		memService.updateSuspenDateByMinute(memId, minTime);
		
	}
	
}
