package com.tsn.serv.mem.service.mem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tsn.serv.common.enm.credits.convert.ConvertDurationEum;
import com.tsn.serv.mem.entity.credits.CreditsTask;
import com.tsn.serv.mem.entity.mem.DurationRecord;
import com.tsn.serv.mem.mapper.credits.CreditsTaskMapper;
import com.tsn.serv.mem.mapper.mem.DurationRecordMapper;

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
	private MemService memService;
	
	public void insert(DurationRecord durationRecord) {
		// TODO Auto-generated method stub
		durationRecordMapper.insert(durationRecord);
	}
	
	public void addDurationOfInvite(String memId) {
		
		CreditsTask creditsTask = creditsTaskMapper.getCreditsTaskByTaskType("friend_invite","win");
		//新增时长记录
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(memId);
		durationRecord.setConvertCardType(ConvertDurationEum.friend_invite.name());
		durationRecord.setConvertDuration(creditsTask.getTaskVal());
		durationRecord.setDurationSources(ConvertDurationEum.friend_invite.name());
		this.insert(durationRecord);
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
