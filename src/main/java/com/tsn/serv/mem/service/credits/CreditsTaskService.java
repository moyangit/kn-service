package com.tsn.serv.mem.service.credits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.mem.entity.credits.CreditsTask;
import com.tsn.serv.mem.entity.credits.CreditsTaskLast;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.credits.CreditsTaskMapper;
import com.tsn.serv.mem.mapper.credits.CreditsTaskOrderMapper;
import com.tsn.serv.mem.service.mem.MemExtInfoService;
import com.tsn.serv.mem.service.mem.MemService;

@Service
public class CreditsTaskService {

	@Autowired
	private CreditsTaskMapper creditsTaskMapper;
	@Autowired
	private CreditsTaskLastSrevice creditsTaskLastSrevice;
	@Autowired
	private CreditsTaskOrderMapper creditsTaskOrderMapper;
	@Autowired
	private MemExtInfoService memExtInfoService;
	
	@Autowired
	private MemService memService;

	public List<CreditsTask> selectCreditsTaskByEntity(String memId, String deviceType) {
		// TODO Auto-generated method stub
		Date time = new Date();
		CreditsTask creditsTask = new CreditsTask();
		creditsTask.setTaskStatus((byte)1);
		creditsTask.setDeviceType(deviceType);
		List<CreditsTask> creditsTasks = creditsTaskMapper.selectCreditsTaskByEntity(creditsTask);
		for (CreditsTask creditsTask2 : creditsTasks) {
			
			
			creditsTask2.setIsUsable((byte)1);//是否可以领取任务1是0否
			//校验当前任务是否已领取
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    CreditsTaskLast taskLast = new CreditsTaskLast();
			taskLast.setTaskType(creditsTask2.getTaskType());
			taskLast.setReceiveTiem(sdf.format(time));
			taskLast.setMemId(memId);
			
			creditsTask2.setTaskAwardsType((byte) 0);
			if ("sign".equals(creditsTask2.getTaskType())) {
				//30分钟
				creditsTask2.setTaskVal(30);
			}
			if ("ad_view".equals(creditsTask2.getTaskType())) {
				//15分钟
				//creditsTask2.setTaskVal(15);
				creditsTask2.setTag("每日");
			}
			
			if ("friend_invite".equals(creditsTask2.getTaskType())) {
				//30天
				//creditsTask2.setTaskVal(30 * 24 * 60);
				creditsTask2.setTaskVal(0);
				creditsTask2.setIsHideVal(1);
			}
			
			if(creditsTask2.getTaskAwardsType() == 0) {
				
				int taskVal = creditsTask2.getTaskVal();
				if (taskVal >= 24 * 60) {
					creditsTask2.setTaskVal(taskVal/60/24);
					creditsTask2.setUnit("天");
				}
				
				if (taskVal > 60 && taskVal < 24 * 60) {
					creditsTask2.setTaskVal(taskVal/60);
					creditsTask2.setUnit("小时");
				}
				
				if (taskVal <= 60 && taskVal > 0) {
					creditsTask2.setTaskVal(taskVal);
					creditsTask2.setUnit("分钟");
				}
				
			}else {
				creditsTask2.setUnit("积分");
			}
			
			if ("reg".equals(taskLast.getTaskType())) {
				continue;
			}
			
			CreditsTaskLast creditsTaskLast1 = creditsTaskLastSrevice.selectCreditsTaskLast(taskLast);
			if(creditsTaskLast1 != null){//当天任务已领取 判断周期内能做领取多少次任务
				CreditsTaskOrder taskOrder = new CreditsTaskOrder();
				taskOrder.setTaskType(creditsTask2.getTaskType());
				taskOrder.setMemId(memId);
				taskOrder.setTaskStatus((byte)1);//成功
				if(creditsTask2.getTaskCycleLimit() != 0) {
					//任务周期不为零的查询领取订单数
					//查询今天已领取的订单
					taskOrder.setStartTime(sdf.format(time));
				    List<CreditsTaskOrder> creditsTaskOrders = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    creditsTask2.setTaskUseNum(creditsTaskOrders.size());
				    if(creditsTaskOrders != null && creditsTaskOrders.size() >= creditsTask2.getTaskLimit()) {
				    	creditsTask2.setIsUsable((byte)0);//是否可以领取任务1是0否
				    }/*else {
				    	taskOrder.setTaskStatus((byte)0);
				    	List<CreditsTaskOrder> creditsTaskOrders1 = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    	if(creditsTaskOrders1 != null) {
				    		creditsTask2.setIsUsable((byte)2);//任务进行中
				    	}
				    }*/
				}else {
					//任务周期为0是 不显周期的
					//判断不显周期的允许领取任务的次数
					taskOrder.setStartTime(null);
				    List<CreditsTaskOrder> creditsTaskOrders = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    creditsTask2.setTaskUseNum(creditsTaskOrders.size());
					if(creditsTaskOrders != null && creditsTaskOrders.size() >= creditsTask2.getTaskLimit() && creditsTask2.getTaskLimit() != 0) {
						creditsTask2.setIsUsable((byte)0);//是否可以领取任务1是0否
						if(creditsTaskOrders.get(0).getTaskStatus() == 0) {
				    		creditsTask2.setIsUsable((byte)2);//任务进行中
				    	}
					}/*else {
				    	taskOrder.setTaskStatus((byte)0);
				    	List<CreditsTaskOrder> creditsTaskOrders1 = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    	if(creditsTaskOrders1 != null) {
				    		creditsTask2.setIsUsable((byte)2);//任务进行中
				    	}
				    }*/
				}
			}
			
			
		}
		
		
		return creditsTasks;
	}

	public CreditsTask selectCreditsTaskByTaskType(String taskType) {
		// TODO Auto-generated method stub
		return creditsTaskMapper.selectCreditsTaskByTaskType(taskType);
	}

	public CreditsTask getCreditsTaskByTaskType(String taskType, String deviceType) {
		return creditsTaskMapper.getCreditsTaskByTaskType(taskType, deviceType);
	}

	public List<CreditsTask> selectByPage(String memId, Page page) {
		
		Date time = new Date();
		String paramObjStr = (String) page.getParamObj();
		JSONObject jsonObject = JSONObject.parseObject(paramObjStr); 
		page.setParamObj(jsonObject);
		List<CreditsTask> creditsTasks = creditsTaskMapper.selectByPage(page);
		
		MemInfo memInfo = memService.queryMemById(memId);
		
		List<CreditsTask> creditsTasksTmp = new ArrayList<CreditsTask>();
		for (CreditsTask creditsTask2 : creditsTasks) {
			
			if (memInfo != null && !MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType()) && "reg".equals(creditsTask2.getTaskType())) {
				continue;
			}
			
			creditsTask2.setIsUsable((byte)1);//是否可以领取任务1是0否
			//校验当前任务是否已领取
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    CreditsTaskLast taskLast = new CreditsTaskLast();
			taskLast.setTaskType(creditsTask2.getTaskType());
			taskLast.setReceiveTiem(sdf.format(time));
			taskLast.setMemId(memId);
			
			//----- start 临时 代码 ------
			//判断是否老用户，如果不是老用户，全部按照时长来
			boolean oldUser = memExtInfoService.isOldUser(memId);
			if (!oldUser) {
				creditsTask2.setTaskAwardsType((byte) 0);
				if ("sign".equals(creditsTask2.getTaskType())) {
					//30分钟
					creditsTask2.setTaskVal(30);
				}
				if ("ad_view".equals(creditsTask2.getTaskType())) {
					//15分钟
					creditsTask2.setTaskVal(15);
				}
				
				if ("friend_invite".equals(creditsTask2.getTaskType())) {
					//30天
					//creditsTask2.setTaskVal(30 * 24 * 60);
					creditsTask2.setTaskVal(0);
					creditsTask2.setIsHideVal(1);
				}
				
			}
			//----- end 临时 代码 ------
			
			if(creditsTask2.getTaskAwardsType() == 0) {
				
				int taskVal = creditsTask2.getTaskVal();
				if (taskVal >= 24 * 60) {
					creditsTask2.setTaskVal(taskVal/60/24);
					creditsTask2.setUnit("天");
				}
				
				if (taskVal > 60 && taskVal < 24 * 60) {
					creditsTask2.setTaskVal(taskVal/60);
					creditsTask2.setUnit("小时");
				}
				
				if (taskVal <= 60 && taskVal > 0) {
					creditsTask2.setTaskVal(taskVal);
					creditsTask2.setUnit("分钟");
				}
				
			}else {
				creditsTask2.setUnit("积分");
			}
			
			if ("reg".equals(taskLast.getTaskType())) {
				creditsTasksTmp.add(creditsTask2);
				continue;
			}
			
			CreditsTaskLast creditsTaskLast1 = creditsTaskLastSrevice.selectCreditsTaskLast(taskLast);
			if(creditsTaskLast1 != null){//当天任务已领取 判断周期内能做领取多少次任务
				CreditsTaskOrder taskOrder = new CreditsTaskOrder();
				taskOrder.setTaskType(creditsTask2.getTaskType());
				taskOrder.setMemId(memId);
				taskOrder.setTaskStatus((byte)1);//成功
				if(creditsTask2.getTaskCycleLimit() != 0) {
					//任务周期不为零的查询领取订单数
					//查询今天已领取的订单
					taskOrder.setStartTime(sdf.format(time));
				    List<CreditsTaskOrder> creditsTaskOrders = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    creditsTask2.setTaskUseNum(creditsTaskOrders.size());
				    if(creditsTaskOrders != null && creditsTaskOrders.size() >= creditsTask2.getTaskLimit()) {
				    	creditsTask2.setIsUsable((byte)0);//是否可以领取任务1是0否
				    }/*else {
				    	taskOrder.setTaskStatus((byte)0);
				    	List<CreditsTaskOrder> creditsTaskOrders1 = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    	if(creditsTaskOrders1 != null) {
				    		creditsTask2.setIsUsable((byte)2);//任务进行中
				    	}
				    }*/
				}else {
					//任务周期为0是 不显周期的
					//判断不显周期的允许领取任务的次数
					taskOrder.setStartTime(null);
				    List<CreditsTaskOrder> creditsTaskOrders = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    creditsTask2.setTaskUseNum(creditsTaskOrders.size());
					if(creditsTaskOrders != null && creditsTaskOrders.size() >= creditsTask2.getTaskLimit() && creditsTask2.getTaskLimit() != 0) {
						creditsTask2.setIsUsable((byte)0);//是否可以领取任务1是0否
						if(creditsTaskOrders.get(0).getTaskStatus() == 0) {
				    		creditsTask2.setIsUsable((byte)2);//任务进行中
				    	}
					}/*else {
				    	taskOrder.setTaskStatus((byte)0);
				    	List<CreditsTaskOrder> creditsTaskOrders1 = creditsTaskOrderMapper.selectTodayTaskOrder(taskOrder);
				    	if(creditsTaskOrders1 != null) {
				    		creditsTask2.setIsUsable((byte)2);//任务进行中
				    	}
				    }*/
				}
			}
			
			creditsTasksTmp.add(creditsTask2);
			
		}
		
		
		return creditsTasksTmp;
	}

}
