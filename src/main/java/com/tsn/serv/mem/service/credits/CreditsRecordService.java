package com.tsn.serv.mem.service.credits;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.enm.credits.convert.CreditsConvertEum;
import com.tsn.serv.common.enm.credits.task.CreditsTaskEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.mem.entity.credits.CreditsRecord;
import com.tsn.serv.mem.entity.credits.CreditsRecordStatistics;
import com.tsn.serv.mem.mapper.credits.CreditsRecordMapper;

@Service
public class CreditsRecordService {

	@Autowired
	private CreditsRecordMapper creditsRecordMapper;
	
	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();

	public void insert(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		String recordNo = snowFlakeManager.create(GenIDEnum.CREDITS_RECORD_NO.name()).getIdByPrefix(GenIDEnum.CREDITS_RECORD_NO.getPreFix());
		CreditsRecord creditsRecord = new CreditsRecord();
		creditsRecord.setCreRecordNo(recordNo);
		creditsRecord.setCredits(jsonObject.getInteger("value"));
		creditsRecord.setCreRecordType(jsonObject.getByte("creRecordType"));//0获取积分
		creditsRecord.setMemId(jsonObject.getString("memId"));
		creditsRecord.setCreditsBalance(jsonObject.getInteger("creditsBalance"));
		if("source".equals(jsonObject.getString("type"))) {
			creditsRecord.setCreSourceNo(jsonObject.getString("orderNo"));
			creditsRecord.setCreSourceType(jsonObject.getString("taskType"));
			if(CreditsTaskEum.sign.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.sign.getCode());
			}else if(CreditsTaskEum.ad_view.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.ad_view.getCode());
			}else if(CreditsTaskEum.url_save.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.url_save.getCode());
			}else if(CreditsTaskEum.url_fill.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.url_fill.getCode());
			}else if(CreditsTaskEum.app_share.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.app_share.getCode());
			}else if(CreditsTaskEum.fq_fill.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.fq_fill.getCode());
			}else if(CreditsTaskEum.friend_invite.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.friend_invite.getCode());
			}else if(CreditsTaskEum.ad_down.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.ad_down.getCode());
			}else if(CreditsTaskEum.google_evaluate.name().equals(jsonObject.getString("taskType"))) {
				creditsRecord.setCreSourceDetail(CreditsTaskEum.google_evaluate.getCode());
			}
		}else if("target".equals(jsonObject.getString("type"))) {
			creditsRecord.setCreTargetNo(jsonObject.getString("orderNo"));
			if(CreditsConvertEum.one_hour.name().equals(jsonObject.getString("cardType"))) {
				creditsRecord.setCreTargetType(jsonObject.getString("cardType"));
				creditsRecord.setCreTargetDetail("一小时卡");
			}else if(CreditsConvertEum.three_hour.name().equals(jsonObject.getString("cardType"))){
				creditsRecord.setCreTargetType(jsonObject.getString("cardType"));
				creditsRecord.setCreTargetDetail("三小时卡");
			}else if(CreditsConvertEum.five_hour.name().equals(jsonObject.getString("cardType"))){
				creditsRecord.setCreTargetType(jsonObject.getString("cardType"));
				creditsRecord.setCreTargetDetail("五小时卡");
			}
		}
		creditsRecordMapper.insert(creditsRecord);
	}


	public CreditsRecord selectById(String id) {
		// TODO Auto-generated method stub
		return creditsRecordMapper.selectByPrimaryKey(id);
	}

	public CreditsRecordStatistics selectCreditsStatistics(String createTime, String userId) {
		
		CreditsRecordStatistics map = creditsRecordMapper.selectCreditsStatistics(createTime,userId);
		
		
		
		return map;
	}

	public void selectCreditsRecordByPage(Page page) {
		List<CreditsRecord> creditsRecordList = creditsRecordMapper.selectCreditsRecordByPage(page);
		page.setData(creditsRecordList);
	}

	public List<CreditsRecord> selectCreditsRecordByEntity(CreditsRecord creditsRecord) {
		List<CreditsRecord> creditsRecordList = creditsRecordMapper.selectCreditsRecordByEntity(creditsRecord);
		return creditsRecordList;
	}
	
	
}
