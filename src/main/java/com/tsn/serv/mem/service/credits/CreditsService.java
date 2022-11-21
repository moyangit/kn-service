package com.tsn.serv.mem.service.credits;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.code.credits.TaskExceptionCode;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsgProducter;
import com.tsn.serv.mem.entity.credits.Credits;
import com.tsn.serv.mem.entity.credits.CreditsConvertOrder;
import com.tsn.serv.mem.entity.credits.CreditsCoupon;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;
import com.tsn.serv.mem.entity.mem.DurationRecord;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.credits.CreditsMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.service.mem.DurationRecordService;
import com.tsn.serv.mem.service.mem.MemService;

@Service
public class CreditsService {

	@Autowired
	private CreditsMapper creditsMapper;
	
	@Autowired
	private CreditsConvertOrderService creditsConvertOrderService;
	
	@Autowired
	private CreditsRecordService creditsRecordService;
	
	@Autowired
	private MemInfoMapper memInfoMapper;
	
	@Autowired
	private CreditsTaskOrderService creditsTaskOrderService;
	
	@Autowired
	private CreditsService creditsService;
	
	@Autowired
	private DurationRecordService durationRecordService;
	
	@Autowired
	private CreditsCouponService creditsCouponService;
	
	@Autowired
	private MemService memService;
	
	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();

	public int updateCreditsByMemId(Credits credits) {
		// TODO Auto-generated method stub
		return creditsMapper.updateCreditsByMemId(credits);
	}

	public void insert(Credits credits) {
		// TODO Auto-generated method stub
		creditsMapper.insert(credits);
	}

	/**
	 * 积分兑换时长卡
	 * @param credits
	 */
	@Transactional
	public void creditsExchange(CreditsConvertOrder credits) {
		
		int duration = 0;//时长
		int buyCredits = 0;//积分
		List<CreditsCoupon> creditsCoupons = creditsCouponService.getCreditsCouponListByType(credits.getConvertType());
//		if(CreditsConvertEum.one_hour.name().equals(credits.getCardType())) {
//			buyCredits = Integer.valueOf(CreditsConvertEum.one_hour.getCode());
//			duration = 1;
//		}else if(CreditsConvertEum.three_hour.name().equals(credits.getCardType())){
//			buyCredits = Integer.valueOf(CreditsConvertEum.three_hour.getCode());
//			duration = 3;
//		}else if(CreditsConvertEum.five_hour.name().equals(credits.getCardType())){
//			buyCredits = Integer.valueOf(CreditsConvertEum.five_hour.getCode());
//			duration = 5;
//		}
		if(creditsCoupons == null || creditsCoupons.size() == 0) {
			throw new BusinessException(TaskExceptionCode.RECORD_NOTHINGNESS, "时长兑换卡不存在");
		}
		duration = creditsCoupons.get(0).getCouponDuration();
		buyCredits = creditsCoupons.get(0).getCouponCredits();
		//查询用户所剩积分是否允许兑换
		Credits credits2 = this.selectCreditsByMemId(credits.getMemId());
		if(credits2 == null || credits2.getCreditsVal() < buyCredits) {
			throw new BusinessException(TaskExceptionCode.CREDITS_NOT_ENOUGH, "用户积分不足");
		}
		int creditsBalance = 0;
		//1.修改用户积分
		Credits credits1 = creditsService.selectCreditsByMemId(credits.getMemId());
		creditsBalance = credits1.getCreditsVal() - buyCredits;
		credits1.setCreditsVal(-buyCredits);//用户剩余积分
		credits1.setCreditsTotalVal(0);//用户获得总积分
		credits1.setMemId(credits.getMemId());
		creditsMapper.updateCreditsByMemId(credits1);
		//2.添加兑换订单表
		String orderNo = snowFlakeManager.create(GenIDEnum.CREDITS_CONVERT_NO.name()).getIdByPrefix(GenIDEnum.CREDITS_CONVERT_NO.getPreFix());
		CreditsConvertOrder creditsConvertOrder = new CreditsConvertOrder();
		creditsConvertOrder.setMemId(credits.getMemId());
		creditsConvertOrder.setOrderNo(orderNo);
		creditsConvertOrder.setConvertDuration(duration);
		creditsConvertOrder.setConvertStatus((byte)1);
		creditsConvertOrder.setConvertType(credits.getConvertType());
		creditsConvertOrder.setConvertTime(new Date());
		creditsConvertOrderService.insert(creditsConvertOrder);
		//3.添加积分流水表
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("creRecordType", (byte)1);
		jsonObject.put("memId", credits.getMemId());
		jsonObject.put("orderNo", orderNo);
		jsonObject.put("cardType", credits.getConvertType());
		jsonObject.put("type", "target");
		jsonObject.put("value", buyCredits);
		jsonObject.put("creditsBalance", creditsBalance);
		creditsRecordService.insert(jsonObject);
		//4.修改用户时长
		memService.updateSuspenDateByMinute(credits.getMemId(),duration);
		
		//5.添加时长兑换表
		DurationRecord durationRecord = new DurationRecord();
		durationRecord.setMemId(credits.getMemId());
		durationRecord.setConvertCardType(credits.getConvertType());
		durationRecord.setConvertDuration(duration);
		durationRecord.setDurationSources("credits");
		durationRecord.setConvertValue(new BigDecimal(buyCredits));
		durationRecord.setOrderNo(orderNo);
		durationRecordService.insert(durationRecord);
		
		//6.兑换人数
		creditsCouponService.updateByCouponType(credits.getConvertType());
		
		MemInfo memInfoTemp = memService.queryMemById(credits.getMemId());
		//兑换任务人数事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelCreditsConvertPeopleCount(memInfoTemp.getChannelCode(), credits.getMemId()));
		
		//兑换任务事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelCreditsConvertNum(memInfoTemp.getChannelCode(), orderNo));
	}
	
	public Credits selectCreditsByMemId(String memId) {
		
		return creditsMapper.selectCreditsByMemId(memId);
	}

	/**
	 * 确认发放积分
	 * @param creditsTaskOrder
	 */
	@Transactional
	public void creditsConfirmAdd(CreditsTaskOrder creditsTaskOrder) {
		
		//1.根据任务ID查出任务
		CreditsTaskOrder taskOrder = creditsTaskOrderService.selectCreditsTaskOrderByOrderNo(creditsTaskOrder.getOrderNo());
		if(taskOrder == null || taskOrder.getTaskStatus() == 1) {
			throw new BusinessException(TaskExceptionCode.TASK_NOTHINGNESS, "任务不存在或者已完成");
		}
		//2.根据任务判断任务是赠送积分还是直接给天数
		//3.赠送积分修改积分表和新增积分流水
		if(taskOrder.getValueType() == 1) {
			//修改积分
			int creditsBalance = 0;//余额
			Credits credits = creditsService.selectCreditsByMemId(creditsTaskOrder.getMemId());
			if(credits == null) {
				credits = new Credits();
				credits.setCreditsVal(0);
			}
			creditsBalance = credits.getCreditsVal() + taskOrder.getTaskValue();
			credits.setMemId(creditsTaskOrder.getMemId());
			credits.setCreditsTotalVal(taskOrder.getTaskValue());
			credits.setCreditsVal(taskOrder.getTaskValue());
			int c = creditsService.updateCreditsByMemId(credits);
			if(c == 0) {
				credits.setCountLock(1);
				creditsService.insert(credits);
			}
			//赠送积分
			//插入积分记录表
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", taskOrder.getTaskValue());
			jsonObject.put("creRecordType", (byte)0);
			jsonObject.put("memId", taskOrder.getMemId());
			jsonObject.put("orderNo", taskOrder.getOrderNo());
			jsonObject.put("taskType", taskOrder.getTaskType());
			jsonObject.put("type", "source");
			jsonObject.put("creditsBalance", creditsBalance);
			creditsRecordService.insert(jsonObject);
			
		}else {
			//赠送时长
			//修改用户时长
//			MemInfo memInfo = new MemInfo();
//			memInfo.setMemId(taskOrder.getMemId());
//			memInfo.setDuration(taskOrder.getTaskValue());
//			memInfoMapper.updateSuspenDateMinute(memInfo);
			
			memService.updateSuspenDateByMinute(taskOrder.getMemId(),taskOrder.getTaskValue());
			//时长表新增
			
			//5.添加时长兑换表
			durationRecordService.addDurationOfAppraiseGoogle(taskOrder.getMemId(),taskOrder.getOrderNo());
			
		}
		
		//4.根据任务id修改任务状态
		taskOrder.setTaskStatus((byte)1);
		creditsTaskOrderService.updateTaskOrderStatusById(taskOrder);
	}
	
}
