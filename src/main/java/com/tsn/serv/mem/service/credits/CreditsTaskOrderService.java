package com.tsn.serv.mem.service.credits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.serv.common.code.credits.TaskExceptionCode;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.credits.task.CreditsTaskEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsgProducter;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.mem.entity.credits.CreditsTask;
import com.tsn.serv.mem.entity.credits.CreditsTaskLast;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.credits.CreditsTaskOrderMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.service.flow.FlowLimitService;
import com.tsn.serv.mem.service.mem.DurationRecordService;
import com.tsn.serv.mem.service.mem.MemExtInfoService;
import com.tsn.serv.mem.service.mem.MemGuestInfoService;
import com.tsn.serv.mem.service.mem.MemService;

@Service
public class CreditsTaskOrderService {

	private static Logger log = LoggerFactory.getLogger(CreditsTaskOrderService.class);
	@Autowired
	private CreditsTaskOrderMapper creditsTaskOrderMapper;
	
	@Autowired
	private CreditsTaskService creditsTaskService;
	
	@Autowired
	private CreditsTaskLastSrevice creditsTaskLastSrevice;
	
	@Autowired
	private CreditsService creditsService;
	
	@Autowired
	private CreditsRecordService creditsRecordService;
	
	@Autowired
	private MemInfoMapper memInfoMapper;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();
	
	@Autowired
	private FlowLimitService flowLimitService;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private MemGuestInfoService memGuestInfoService;
	
	//private static final String guest = "guest:memInfo:";
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private DurationRecordService durationRecordService;
	
	@Autowired
	private MemExtInfoService memExtInfoService;
	/**
	 * 
	 * 新增任务订单
	 * @param creditsTaskOrder
	 */
	@Transactional
	public void addTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		Map<String, Object> map = jwtInfo.getExtend();
		creditsTaskOrder.setDeviceNo((String) map.get("deviceNo"));
		
		//触发实时活跃online用户
		EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(creditsTaskOrder.getMemId()));
		
		// TODO Auto-generated method stub
		Date time = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String key = creditsTaskOrder.getMemId() + ":" + creditsTaskOrder.getTaskType() + ":" + sdf1.format(time);
		try {
			Long increment = redisTemplate.opsForValue().increment(key, 1);
			if(increment > 1) {
				throw new BusinessException(TaskExceptionCode.SUBMIT_AGAIN, "任务重复提交");
			}
			//1.根据任务类型查出任务规则
			CreditsTask creditsTask = creditsTaskService.getCreditsTaskByTaskType(creditsTaskOrder.getTaskType(),creditsTaskOrder.getDeviceType());
			if(creditsTask == null) {
				throw new BusinessException(TaskExceptionCode.TASK_NOTHINGNESS, "任务不存在");
			}
			
			//----- start 临时 代码 ------
			//判断是否老用户，如果不是老用户，全部按照时长来
			/*boolean oldUser =memExtInfoService.isOldUser(creditsTaskOrder.getMemId());
			if (!oldUser) {
				creditsTask.setTaskAwardsType((byte) 0);
				if ("sign".equals(creditsTask.getTaskType())) {
					//30分钟
					creditsTask.setTaskVal(30);
				}
				if ("ad_view".equals(creditsTask.getTaskType())) {
					//15分钟
					creditsTask.setTaskVal(15);
				}
			}*/
			
			creditsTask.setTaskAwardsType((byte) 0);
			if ("sign".equals(creditsTask.getTaskType())) {
				//30分钟
				creditsTask.setTaskVal(30);
			}
			if ("ad_view".equals(creditsTask.getTaskType())) {
				//15分钟
				creditsTask.setTaskVal(15);
			}
			
			//----- end 临时 代码 ------
			
			//2.生成任务订单表  同时修改用户最后领取任务表
			//校验当前任务是否已领取
			
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    CreditsTaskLast taskLast = new CreditsTaskLast();
			taskLast.setTaskType(creditsTaskOrder.getTaskType());
			taskLast.setTaskValue(creditsTask.getTaskVal());
			taskLast.setReceiveTiem(sdf.format(time));
			taskLast.setMemId(creditsTaskOrder.getMemId());
			
			//如果有记录，说明今日做了任务,然后判断是否任务次数限制
			CreditsTaskLast creditsTaskLast1 = creditsTaskLastSrevice.selectCreditsTaskLast(taskLast);
			if(creditsTaskLast1 != null){//当天任务已领取 判断周期内能做领取多少次任务
				CreditsTaskOrder taskOrder = new CreditsTaskOrder();
				taskOrder.setTaskType(creditsTaskOrder.getTaskType());
				taskOrder.setMemId(creditsTaskOrder.getMemId());
				taskOrder.setTaskStatus((byte)1);//成功
				if(creditsTask.getTaskCycleLimit() != 0) {
					//任务周期不为零的查询领取订单数
					//查询今天已领取的订单
					taskOrder.setStartTime(sdf.format(time));
				    List<CreditsTaskOrder> creditsTaskOrders = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    if(creditsTaskOrders != null && creditsTaskOrders.size() >= creditsTask.getTaskLimit()) {
				    	throw new BusinessException(TaskExceptionCode.TASK_RECEIVE_EXCEED_THE_LIMIT, "领取任务超过限制");
				    }/*else {
				    	taskOrder.setTaskStatus((byte)0);
				    	List<CreditsTaskOrder> creditsTaskOrders1 = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    	if(creditsTaskOrders1 != null && creditsTaskOrders1.size() >= creditsTask.getTaskLimit()) {
				    		throw new BusinessException(TaskExceptionCode.TASK_RECEIVE_EXCEED_THE_LIMIT, "领取任务超过限制");
				    	}
				    }*/
				}else {
					//任务周期为0是 不限周期的
					//判断 不限周期的允许领取任务的次数
					taskOrder.setStartTime(null);
				    List<CreditsTaskOrder> creditsTaskOrders = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
					if(creditsTaskOrders != null && creditsTaskOrders.size() >= creditsTask.getTaskLimit() && creditsTask.getTaskLimit() != 0) {
						throw new BusinessException(TaskExceptionCode.TASK_RECEIVE_EXCEED_THE_LIMIT, "领取任务超过限制");
					}/*else {
				    	taskOrder.setTaskStatus((byte)0);
				    	List<CreditsTaskOrder> creditsTaskOrders1 = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    	if(creditsTaskOrders1 != null && creditsTaskOrders1.size() >= creditsTask.getTaskLimit() && creditsTask.getTaskLimit() != 0) {
				    		throw new BusinessException(TaskExceptionCode.TASK_RECEIVE_EXCEED_THE_LIMIT, "领取任务超过限制");
				    	}
				    }*/
				}
			}
			String orderNo = this.createOrderNo();
			//生成任务订单 同时 修改最后一次领取任务的记录表
			CreditsTaskOrder order = new CreditsTaskOrder();
			order.setTaskStartTime(time);
			order.setMemId(creditsTaskOrder.getMemId());
			if(StringUtils.isEmpty(creditsTaskOrder.getOrderNo()) || "sign".equals(creditsTaskOrder.getTaskType())) {
				order.setOrderNo(orderNo);
			}else {
				order.setOrderNo(creditsTaskOrder.getOrderNo());
			}
			
			//用来做其他业务
			creditsTaskOrder.setOrderNo(order.getOrderNo());
			
			order.setTaskType(creditsTaskOrder.getTaskType());
			order.setTaskValue(creditsTask.getTaskVal());
			order.setValueType(creditsTask.getTaskAwardsType());
			//除了邀请好友 分享朋友圈和谷歌商店 其余任务直接已完成
			if(CreditsTaskEum.friend_invite.name().equals(creditsTaskOrder.getTaskType()) //邀请好友
					|| CreditsTaskEum.google_evaluate.name().equals(creditsTaskOrder.getTaskType())//谷歌商店评价 
					|| CreditsTaskEum.app_share.name().equals(creditsTaskOrder.getTaskType()) //分享
					|| CreditsTaskEum.ad_view.name().equals(creditsTaskOrder.getTaskType())//视频任务
					|| CreditsTaskEum.ad_stimulate.name().equals(creditsTaskOrder.getTaskType())) {//激励视频
				order.setTaskStatus((byte)0);//进行中
				if(!StringUtils.isEmpty(creditsTaskOrder.getFileUrl())) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("fileUploadPath", creditsTaskOrder.getFileUrl());
					
					order.setFileUrl(JSONObject.toJSONString(jsonObject));
				}
			}else {
				order.setTaskStatus((byte)1);//完成
			}
			//修改任务last表
			int i = creditsTaskLastSrevice.updateByTaskType(taskLast);
			if(i == 0) {
				creditsTaskLastSrevice.insertByTaskType(taskLast);
			}
			 
			//3.修改积分表
			//判断任务获得的是积分还是时长   任务为及时完成的 在这里直接送积分
			/*if(creditsTask.getTaskAwardsType() == 1 && order.getTaskStatus() == 1){
				int creditsBalance = 0;//余额
				//修改积分
				Credits credits = creditsService.selectCreditsByMemId(creditsTaskOrder.getMemId());
				if(credits == null) {
					credits = new Credits();
					credits.setCreditsVal(0);
				}
				creditsBalance = credits.getCreditsVal() + creditsTask.getTaskVal();
				credits.setMemId(creditsTaskOrder.getMemId());
				credits.setCreditsTotalVal(creditsTask.getTaskVal());
				credits.setCreditsVal(creditsTask.getTaskVal());
				int c = creditsService.updateCreditsByMemId(credits);
				if(c == 0) {
					credits.setCountLock(1);
					creditsService.insert(credits);
				}
				//获取积分
				//插入积分记录表
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("value", creditsTask.getTaskVal());
				jsonObject.put("creRecordType", (byte)0);
				jsonObject.put("memId", creditsTaskOrder.getMemId());
				jsonObject.put("orderNo", orderNo);
				jsonObject.put("taskType", creditsTaskOrder.getTaskType());
				jsonObject.put("type", "source");
				jsonObject.put("creditsBalance", creditsBalance);
				creditsRecordService.insert(jsonObject);
			}
			//任务是获得时长并及时完成的在这里直接赠送时长
			else */
			
			if(creditsTask.getTaskAwardsType() == 0 && order.getTaskStatus() == 1){
				//获取时长
				//修改用户时长
				memService.updateSuspenDateByMinute(creditsTaskOrder.getMemId(),creditsTask.getTaskVal());
			}
			
			MemInfo memInfo = memService.queryMemById(creditsTaskOrder.getMemId());
			//插入任务订单
			if(!StringUtils.isEmpty(creditsTaskOrder.getDeviceNo())) {
				String[] dIdSplit = creditsTaskOrder.getDeviceNo().split("-");
				order.setDeviceType(dIdSplit[0]);
			}
/*			String tokenType = jwtInfo.getTokenType();
			if(AuthEnum.guest_us.name().equals(tokenType)) {
				GuestInfo guestInfo = memGuestInfoService.getGuestById(creditsTaskOrder.getMemId());
//				Map<Object, Object> map1 = redisHandler.hGetMap(RedisKey.GUEST_INFO + creditsTaskOrder.getMemId());
				//order.setMemType((String)map1.get("memType"));
				order.setMemType(guestInfo.getMemType());
				order.setSuspenDate(guestInfo.getSuspenDate());
			}else {
				order.setMemType(memInfo.getMemType());
				order.setSuspenDate(memInfo.getSuspenDate());
			}*/
			
			order.setMemType(memInfo.getMemType());
			order.setSuspenDate(memInfo.getSuspenDate());
			
			order.setDeviceNo(creditsTaskOrder.getDeviceNo());
			creditsTaskOrderMapper.insert(order);
			
		} finally {
			// TODO: handle exception
			redisTemplate.expire(key, 5, TimeUnit.SECONDS);
		} 

	}

	@Transactional
	public void addSignTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		this.addTaskOrder(creditsTaskOrder);
		
		//签到人数事件
		MemInfo memInfo = memInfoMapper.selectByPrimaryKey(creditsTaskOrder.getMemId());
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createSignPeopleCount(memInfo.getChannelCode(), creditsTaskOrder.getMemId()));
		
		//签到事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelSignTaskNum(memInfo.getChannelCode(), creditsTaskOrder.getOrderNo()));
	}

	@Transactional
	public void addAdViewTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		this.addTaskOrder(creditsTaskOrder);
		
		//激励人数事件
		MemInfo memInfo = memInfoMapper.selectByPrimaryKey(creditsTaskOrder.getMemId());
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createUrgePeopleCount(memInfo.getChannelCode(), creditsTaskOrder.getMemId()));
		//激励事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelUrgeTaskNum(memInfo.getChannelCode(), creditsTaskOrder.getOrderNo()));
	}
	
	@Transactional
	public void addStimulateAdViewTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		this.addTaskOrder(creditsTaskOrder);
		
		//激励人数事件
		MemInfo memInfo = memInfoMapper.selectByPrimaryKey(creditsTaskOrder.getMemId());
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createUrgePeopleCount(memInfo.getChannelCode(), creditsTaskOrder.getMemId()));
		
		//激励事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createChannelUrgeTaskNum(memInfo.getChannelCode(), creditsTaskOrder.getOrderNo()));
	}

	@Transactional
	public void addAdDownTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	@Transactional
	public void addGoogleEvaluateTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	@Transactional
	public void addFriendInviteTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	@Transactional
	public void addFqFillTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	@Transactional
	public void addAppShareTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	@Transactional
	public void addUrlFillTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	@Transactional
	public void addUrlSaveTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		this.addTaskOrder(creditsTaskOrder);
	}

	public CreditsTaskOrder selectCreditsTaskOrderById(String id) {
		// TODO Auto-generated method stub
		return creditsTaskOrderMapper.selectByPrimaryKey(id);
	}

	@Transactional
	public void updateTaskOrderStatusById(CreditsTaskOrder taskOrder) {
		// TODO Auto-generated method stub
		creditsTaskOrderMapper.updateTaskOrderStatusById(taskOrder);
	}


	public List<CreditsTaskOrder> selectByEntity(CreditsTaskOrder creditsTaskOrder) {
		// TODO Auto-generated method stub
		return creditsTaskOrderMapper.selectByEntity(creditsTaskOrder);
	}

	public String createOrderNo() {
		
		String orderNo = snowFlakeManager.create(GenIDEnum.TASK_NO.name()).getIdByPrefix(GenIDEnum.TASK_NO.getPreFix());
		
		return orderNo;
	}


	public CreditsTaskOrder selectCreditsTaskOrderByOrderNo(String OrderNo) {
		
		return creditsTaskOrderMapper.selectCreditsTaskOrderByOrderNo(OrderNo);
	}

	@SuppressWarnings("unchecked")
	public void selectTaskOrderByPage(Page page) {
		
		Map<String, Object> params = (Map<String, Object>) page.getParamObj();
		String currMonthTime = DateUtils.getCurrYMD("yyyyMM");
		params.put("currMonthTime", currMonthTime);
		
		//必须要传一个分表的后缀字段monthTime，如202111，年+月
		List<Map<String, Object>> creditsTaskOrderList;
		try {
			creditsTaskOrderList = creditsTaskOrderMapper.selectTaskOrderByPage(page);
			creditsTaskOrderList.stream()
					.forEach(item -> {
						if(Integer.parseInt(item.get("valueType").toString()) == 0 && item.get("taskType").equals("google_evaluate")) {
							item.put("taskValue", Integer.parseInt(item.get("taskValue").toString())/60/24);
							item.put("unit", "天");
						}else if (Integer.parseInt(item.get("valueType").toString()) == 1) {
							item.put("unit", "积分");
						}else {
							item.put("unit", "分钟");
						}
					});
		} catch (Exception e) {
			log.error("{}", e);
			creditsTaskOrderList = new ArrayList<Map<String, Object>>();
		}
		page.setData(creditsTaskOrderList);
	}

	public void examineTaskOrder(CreditsTaskOrder creditsTaskOrder) {
		creditsService.creditsConfirmAdd(creditsTaskOrder);
	}


	@SuppressWarnings("static-access")
	@Transactional
	public void updateStimulateToReward(CreditsTaskOrder creditsTaskOrder, String sign, String tokenType, String userId) {
		
		//游客免费使用时长
		String USETIME = Env.getVal("mem.guest.use.time");
		
		/*//游客限速
		String maxUpFlowLimit = Env.getVal("mem.guest.use.maxUpFlowLimit");
		
		//游客限速
		String maxDownFlowLimit = Env.getVal("mem.guest.use.maxDownFlowLimit");*/
		
		int useTime = 15;
		if(!StringUtils.isEmpty(USETIME)) {
			useTime = Integer.valueOf(USETIME);
		}
		
		//long time = this.addDate(new Date(), useTime);
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			String key = RedisKey.GUEST_INFO + userId;
			redisHandler.hSet(key, "suspenDate", DateUtils.getCurrDateAddMinTime(new Date(), useTime));
		}else {
			MemInfo memInfo = new MemInfo();
			memInfo.setMemId(creditsTaskOrder.getMemId());
			memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), useTime));
			memInfoMapper.updateByPrimaryKeySelective(memInfo);
		}
		
		CreditsTaskOrder taskOrder = this.selectCreditsTaskOrderByOrderNo(creditsTaskOrder.getOrderNo());
		if(taskOrder == null || taskOrder.getTaskStatus() == 1) {
			throw new BusinessException(TaskExceptionCode.TASK_NOTHINGNESS, "任务不存在或者已完成");
		}
		//修改订单状态
		taskOrder.setTaskStatus((byte)1);
		this.updateTaskOrderStatusById(taskOrder);
		
		
		//新增时长记录
		durationRecordService.addDurationOfStimulate(creditsTaskOrder.getMemId(),taskOrder.getOrderNo());
		
		//flowLimitService.updateExpireUserLimit(userId);
		
		/*FlowLimit flowLimit = new FlowLimit();
		flowLimit.setMemId(userId);
		Long limit = (long) 5242880;
		flowLimit.setMaxUpFlowLimit(StringUtils.isEmpty(maxUpFlowLimit) ? limit : Long.valueOf(maxUpFlowLimit));
		flowLimit.setMaxDownFlowLimit(StringUtils.isEmpty(maxDownFlowLimit) ? limit : Long.valueOf(maxDownFlowLimit));
		flowLimitService.guestFlowLimit(flowLimit);*/
	}
	
	public static long addDate(Date date,long minute) {
		long time = date.getTime(); 
		minute = minute*60*1000;
		time+=minute;
		return time; 
	}

	@Transactional
	public void taskOrderReject(CreditsTaskOrder creditsTaskOrder) {
		
		creditsTaskOrder.setTaskStatus((byte)2);//任务未完成
		int i = creditsTaskOrderMapper.updateTaskOrderStatusByIdAndStatus(creditsTaskOrder);
		if(i > 0) {
			CreditsTaskLast creditsTaskLast = new CreditsTaskLast();
			creditsTaskLast.setTaskType(creditsTaskOrder.getTaskType());
			creditsTaskLast.setMemId(creditsTaskOrder.getMemId());
			creditsTaskLastSrevice.updateTaskLastTime(creditsTaskLast);
		}
	}
}
