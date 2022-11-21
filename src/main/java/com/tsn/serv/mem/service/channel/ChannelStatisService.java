package com.tsn.serv.mem.service.channel;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.http.HttpGetReq;
import com.tsn.common.utils.utils.tools.http.entity.HttpCode;
import com.tsn.common.utils.utils.tools.http.entity.HttpRes;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.cons.redis.RedisMqKey;
import com.tsn.serv.common.enm.charge.ChargeTypeEum;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsg.MsgTypeEnum;
import com.tsn.serv.mem.entity.channel.Channel;
import com.tsn.serv.mem.entity.channel.ChannelStatisHour;
import com.tsn.serv.mem.entity.mem.MemExtInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.mapper.channel.ChannelMapper;
import com.tsn.serv.mem.service.mem.MemExtInfoService;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.order.ChargeOrderService;

@Service
public class ChannelStatisService {
	
	private static Logger log = LoggerFactory.getLogger(ChannelStatisService.class);
	
	@Autowired
	private ChannelMapper channelMapper;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private MemExtInfoService memExtInfoService;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private ChargeOrderService chargeOrderService;
	
	@Autowired
	private ChannelAdLogService channelAdLogService;
	
	/**
	 * 主要用户注册流程，获取后就删除
	 * @param channelCode
	 * @param yyyyMMdd
	 * @return
	 */
	public Map<String, String> getAndSetChannelClickIdAndExId(String channelCode, String yyyyMMdd) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		Set<Object> channelResult = null;
		try {
			
			//获取cId，将金额回调给vm
			channelResult = redisHandler.zRange(RedisKey.VM_QUEUE_CID_CHANNEL + channelCode + ":" + yyyyMMdd, 0, 0);
			if (channelResult != null) {
				Object[] obj = channelResult.toArray();
				
				if (obj != null && obj.length > 0) {
					String cidStr = String.valueOf(obj[0]);
					
					if (cidStr.indexOf("#") > -1) {
						String[] cidArr = cidStr.split("#");
						if (cidArr.length > 1) {
							String cid = cidArr[0];
							String eid = cidArr[1];
							
							//如果数据不对就删除
							if (StringUtils.isEmpty(eid) || "null".equals(eid)) {
								redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelCode + ":" + yyyyMMdd, channelResult.toArray());
								return result;
							}
							
							result.put("cid", cid);
							result.put("eid", eid);
							log.info("getAndSetChannelClickIdAndExId, yyyyMMdd = {}, channelCode = {}, cid = {}, eid = {}", yyyyMMdd, cid, eid);
							return result;
						} else if (cidArr.length == 1) {
							String cid = cidArr[0];
							
							//如果数据不对就删除
							if (StringUtils.isEmpty(cid) || "null".equals(cid)) {
								redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelCode + ":" + yyyyMMdd, channelResult.toArray());
								return result;
							}
							
							result.put("cid", cid);
							result.put("eid", "");
							log.info("getAndSetChannelClickIdAndExId, yyyyMMdd = {}, channelCode = {}, cid = {}, eid = {}", yyyyMMdd, cid, "");
							return result;
						}
					}else {
						redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelCode + ":" + yyyyMMdd, channelResult.toArray());
					}
				}
				
			}
			
			return result;
		} catch (Exception e) {
			log.error("getAndSetChannelClickIdAndExId, e = {}", e);
			return result;
		} finally {
			
			if (channelResult != null && !channelResult.isEmpty()) {
				redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelCode + ":" + yyyyMMdd, channelResult.toArray());
			}
			
		}
		
		
	}
	
	public void revmoveChannelClickIdAndExId(String channelCode, String yyyyMMdd, Set<Object> channelResult) {
		
		try {
			//不管有没有成功就删除
			redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelCode + ":" + yyyyMMdd, channelResult.toArray());
		} catch (Exception e) {
			log.error("revmoveChannelClickIdAndExId, e = {}", e);
		}
		
		
	}
	
	public void setChannelTotalAndDataTypeStatic(ChannelMsg channelMsg){
		
		//2021082315
		if (StringUtils.isEmpty(channelMsg.getDayTime()) || channelMsg.getDayTime().length() != 10) {
			
			throw new IllegalArgumentException("ChannelStatisService param error, msg = " + channelMsg.getDayTime());
			
		}
//		if (StringUtils.isEmpty(channelMsg.getChannelCode())) {
//			throw new IllegalArgumentException("ChannelStatisService param error, channel code is null");
//		}
		
		MsgTypeEnum msgTypeEnum = channelMsg.getMsgTypeEnum();
		
		//下面统计平台总共的，只统计付款人数，激励任务人数，签到任务人数，积分兑换人数，支付订单个数，签到任务次数，激励任务次数，积分兑换次数
		if ("channel_pay_people_count,channel_urge_task_people_count,channel_sign_task_people_count,channel_credits_convert_people_count,channel_order_num,channel_sign_task_num,channel_urge_task_num,channel_credits_convert_num".contains(msgTypeEnum.name())){
			//按日，去掉dayTime 的小时
			String dayTimeTemp = channelMsg.getDayTime().substring(0,8);
			String platRecordKey = RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + msgTypeEnum.name() + ":" + dayTimeTemp;
			redisHandler.pfAdd(platRecordKey, channelMsg.getMsgId());
			
			String platTotalKey = RedisMqKey.CHANNEL_TOTAL_QUEUE_TYPE_HLL + msgTypeEnum.name();
			redisHandler.pfAdd(platTotalKey, channelMsg.getMsgId());
		}
		
		//如果为空就不往下走了，下面只针对channel数据
		if (StringUtils.isEmpty(channelMsg.getChannelCode())) {
			
			log.debug("sendChannelMsg msg channelCode is null, msg = {}", channelMsg);
			
			return;
		}
		
		
		switch (msgTypeEnum) {
		//金额数要累加，这种数据用incr存
		case channel_pay_momey_val:
			
			Long logId = null;
			try {
				String orderNo = channelMsg.getMsgId();
				
				ChargeOrder chargeOrder = chargeOrderService.queryOrderByNo(orderNo);
				
				String memId = chargeOrder.getMemId();
				
				MemExtInfo memExtInfoTmp = memExtInfoService.getById(memId);
				
				String cId = memExtInfoTmp.getChannelClickId();
				
				String vmReqUrl = "https://praltancewhobust.com/postback?cid="+cId+"&payout="+channelMsg.getValue()+"&currency=cny";
				
				logId = channelAdLogService.recordVmCallbackLog(memId, vmReqUrl, channelMsg.getChannelCode());
				
				if (!StringUtils.isEmpty(cId)) {
					
					HttpRes httpRes = new HttpGetReq(vmReqUrl).excuteReturnObj();
					
					log.info("channel_pay_momey_va HttpGetReq, result = {}, channelCode= {}, cid = {}, money = {} ", JsonUtils.objectToJson(httpRes), channelMsg.getChannelCode(), cId, channelMsg.getValue());
					
					if (HttpCode.RES_SUCCESS.equals(httpRes.getCode())) {
						channelAdLogService.updateCallbackLogSuccess(logId, httpRes.getData());
					}else {
						channelAdLogService.updateCallbackLogFail(logId, httpRes.getData());
					}
				}
				
			} catch (Exception e1) {
				log.error("channel_pay_momey_va VM_QUEUE_CID_CHANNEL error, e = {}", e1);
				
				if (logId != null) {
					channelAdLogService.updateCallbackLogFail(logId, e1.toString());
				}
				
			}
			
			/*try {
				//获取cId，将金额回调给vm
				Set<Object> channelResult = redisHandler.zRange(RedisKey.VM_QUEUE_CID_CHANNEL + channelMsg.getChannelCode(), 0, 0);
				if (channelResult != null) {
					Object[] obj = channelResult.toArray();
					
					if (obj != null && obj.length > 0) {
						String cidStr = String.valueOf(obj[0]);
						
						if (cidStr.indexOf("#") > -1) {
							String[] cidArr = cidStr.split("#");
							if (cidArr.length > 0) {
								String cid = cidArr[0];
								HttpRes httpRes = new HttpGetReq("https://praltancewhobust.com/postback?cid="+cid+"&payout="+channelMsg.getValue()+"&currency=cny").excuteReturnObj();
								
								log.info("channel_pay_momey_va HttpGetReq, result = {}, channelCode= {}, cid = {}, money = {} ", JsonUtils.objectToJson(httpRes), channelMsg.getChannelCode(), cid, channelMsg.getValue());
								
								if (HttpCode.RES_SUCCESS.equals(httpRes.getCode())) {
									
								}
								
								//不管有没有成功就删除
								redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelMsg.getChannelCode(), channelResult.toArray());
							}
						}else {
							//不管有没有成功就删除
							redisHandler.zRemove(RedisKey.VM_QUEUE_CID_CHANNEL + channelMsg.getChannelCode(), channelResult.toArray());
						}
						
						
					}
					
					
				}
				
			} catch (Exception e) {
				log.error("channel_pay_momey_va VM_QUEUE_CID_CHANNEL error, e = {}", e);
			}*/
			
			
			String incrKey = RedisMqKey.CHANNEL_QUEUE_TYPE_INCR + channelMsg.getChannelCode() + ":" + msgTypeEnum.name() + ":" + channelMsg.getDayTime();
			
			String noReapKey = RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + channelMsg.getChannelCode() + ":" + msgTypeEnum.name() + ":" + channelMsg.getDayTime();
			//如果不重复 就添加，单位时间是一个小时，如果一个小时出现同样的数据，可能是数据重复消费，这里做个判断，不重复就添加
			long res = redisHandler.pfAdd(noReapKey, channelMsg.getMsgId());
			if(res > 0) {
				redisHandler.incr(incrKey, channelMsg.getValue());
			}
			
			//暂时存放两分钟
			redisHandler.expire(noReapKey, 2 * 60);
			
			//统计每个渠道总的数据
			String defChannelTotalKey = RedisMqKey.CHANNEL_TOTAL_QUEUE_TYPE_HLL + channelMsg.getChannelCode() + ":" + msgTypeEnum.name();
			redisHandler.incr(defChannelTotalKey, channelMsg.getValue());

			break;

		default:
			
			Long logId2 = null;
			if (MsgTypeEnum.channel_guest_people_count.name().equals(msgTypeEnum.name())) {
				try {
					String deviceNo = channelMsg.getMsgId();
					QueryWrapper<MemExtInfo> queryWrapper = new QueryWrapper<MemExtInfo>();
					queryWrapper.eq("device_no", deviceNo);
					MemExtInfo memExtInfo = memExtInfoService.getOne(queryWrapper);
					String eId = memExtInfo.getChannelExId();
					//根据渠道取对应广告回传地址
					Channel channel = channelMapper.selectByPrimaryKey(channelMsg.getChannelCode());
					String url = channel.getChannelCallbackUrl();
					url = String.format(url,StringUtils.isEmpty(eId) ? "" : eId);
					logId2 = channelAdLogService.recordAdCallbackLog(memExtInfo.getId(), url, channelMsg.getChannelCode());
					
					HttpRes httpRes = new HttpGetReq(url).excuteReturnObj();
					log.info("channel_guest_people_count HttpGetReq, url={}, result = {}, channelCode= {}, cid = {}, money = {} ", url, JsonUtils.objectToJson(httpRes), channelMsg.getChannelCode(), eId, channelMsg.getValue());
			
					if (HttpCode.RES_SUCCESS.equals(httpRes.getCode())) {
						channelAdLogService.updateCallbackLogSuccess(logId2, httpRes.getData());
					}else {
						channelAdLogService.updateCallbackLogFail(logId2, httpRes.getData());
					}
					
				} catch (Exception e) {
					log.error("channel_guest_people_count VM_QUEUE_CID_CHANNEL error, e = {}", e);
					if (logId2 != null) {
						channelAdLogService.updateCallbackLogFail(logId2, e.toString());
					}
				}
				
			}
				
			setChannel2RedisStatisData(channelMsg, msgTypeEnum);
			
			if (MsgTypeEnum.channel_pay_people_count.name().equals(msgTypeEnum.name())) {//付款人数 顺便统计用户是否是当日用户
				
				//userId
				String msgId = channelMsg.getMsgId();
				MemInfo memInfo = memService.queryMemById(msgId);
				boolean isToday = DateUtils.isToday(memInfo.getRegDate());
				
				if (isToday) {
					setChannel2RedisStatisData(channelMsg, MsgTypeEnum.channel_new_pay_people_count);
				}
				
				
			}else if (MsgTypeEnum.channel_order_num.name().equals(msgTypeEnum.name())) {//订单数顺便统计月卡，季卡，年卡等
				
				//订单号
				String msgId = channelMsg.getMsgId();
				
				ChargeOrder order = chargeOrderService.queryOrderByNo(msgId);
				//month("10", "月卡"), quarter("11", "季卡"), halfYear("12", "半年卡"), year("13", "年卡"), forever("14", "永久");
				String chargeType = order.getChargeType();
				
				if (ChargeTypeEum.month.getType().equals(chargeType))  {
					setChannel2RedisStatisData(channelMsg, MsgTypeEnum.channel_order_month_num);
				}else if (ChargeTypeEum.quarter.getType().equals(chargeType))  {
					setChannel2RedisStatisData(channelMsg, MsgTypeEnum.channel_order_quarter_num);
				}if (ChargeTypeEum.halfYear.getType().equals(chargeType))  {
					setChannel2RedisStatisData(channelMsg, MsgTypeEnum.channel_order_halfyear_num);
				}if (ChargeTypeEum.year.getType().equals(chargeType))  {
					setChannel2RedisStatisData(channelMsg, MsgTypeEnum.channel_order_year_num);
				}if (ChargeTypeEum.forever.getType().equals(chargeType))  {
					setChannel2RedisStatisData(channelMsg, MsgTypeEnum.channel_order_forever_num);
				}
				
				
			}
			
			break;
		}
		
	}
	
	private void setChannel2RedisStatisData(ChannelMsg channelMsg, MsgTypeEnum msgTypeEnum) {
		
		//统计每个渠道，不用数据类型，不同小时段 不重复的数据
		String key = RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + channelMsg.getChannelCode() + ":" +  msgTypeEnum.name() + ":" + channelMsg.getDayTime();
		
		redisHandler.pfAdd(key, channelMsg.getMsgId());
		
		//记录每个时间点对应的数据record，这种数据比较占内存，一般不会查询它，只会保留2天
		String recordKey = RedisMqKey.CHANNEL_QUEUE_TYPE_SET + channelMsg.getChannelCode() + ":" +  msgTypeEnum.name() + ":" + channelMsg.getDayTime();
		redisHandler.sAdd(recordKey, channelMsg.getMsgId());
		//保留2天
		redisHandler.expire(recordKey, 2 * 24 * 60 * 60);
		
		//统计每个渠道总的数据
		String channelTotalKey = RedisMqKey.CHANNEL_TOTAL_QUEUE_TYPE_HLL + channelMsg.getChannelCode() + ":" +  msgTypeEnum.name();
		redisHandler.pfAdd(channelTotalKey, channelMsg.getMsgId());
		
	}
	
	
	public void getChannelListByPageByTime(Page page) {
		
		List<ChannelStatisHour> channelStatisHourList = new ArrayList<ChannelStatisHour>();
		
		List<Channel> channelList = channelMapper.selectByPage(page);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> params = (Map<String, Object>) page.getParamObj();
		
		for (Channel channel : channelList) {
			
			ChannelStatisHour hour = getChannelListByTime(channel.getChannelCode(), channel.getChannelName(), (String)params.get("startTime"), (String)params.get("endTime"));
			
			hour.setMark(channel.getMark());
			hour.setChannelCallbackUrl(channel.getChannelCallbackUrl());
			channelStatisHourList.add(hour);
			
		}
		
		page.setData(channelStatisHourList);
		
	}
	
	public ChannelStatisHour getChannelListByTime(String channelCode, String channelName, String startTime, String endTime) {
		
		
		ChannelStatisHour channelStatisHour = new ChannelStatisHour();
		channelStatisHour.setChannelCode(channelCode);
		channelStatisHour.setChannelName(channelName);
		channelStatisHour.setDayTime(startTime + "-" + endTime);
		
		MsgTypeEnum[] enums = MsgTypeEnum.values();
		
		
		Field[] fileds = channelStatisHour.getClass().getDeclaredFields();
		
		for (MsgTypeEnum msgType : enums) {
			
			switch (msgType) {
			
				case channel_pay_momey_val:
					
					
					for (Field fd : fileds) {
		        		fd.setAccessible(true);
		        		
		        		String msgTypeEnumVal = getHumnName(msgType.name());
		        		
		        		if (msgTypeEnumVal.equals(fd.getName())) {
		        			
		        			String[] channelTypeArr = getRedisHllHourList(RedisMqKey.CHANNEL_QUEUE_TYPE_INCR + channelCode + ":", msgType, startTime, endTime);
		        			
		        			int totalValue = 0;
		        			
		        			List<Object> values = redisHandler.mGet(channelTypeArr);
		        			
		        			for (Object obj : values) {
		        				
		        				if (obj == null) {
		        					continue;
		        				}
		        				
		        				totalValue = totalValue + (int)obj;
		        				
		        			}
		        			
		        			try {
								fd.set(channelStatisHour, totalValue);
							} catch (Exception e) {
								log.error("{}",e);
							}
		        			
		        			values = null;
		        		}
		        		
		        	}
					
					break;
	
				default:
					
		        	for (Field fd : fileds) {
		        		fd.setAccessible(true);
		        		
		        		String msgTypeEnumVal = getHumnName(msgType.name());
		        		
		        		if (msgTypeEnumVal.equals(fd.getName())) {
		        			
		        			String[] channelTypeArr = getRedisHllHourList(RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + channelCode + ":", msgType, startTime, endTime);
		        			
		        			long count = redisHandler.pfcount(channelTypeArr);
		        			
		        			try {
								fd.set(channelStatisHour, new Long(count).intValue());
							} catch (Exception e) {
								log.error("{}",e);
							} 
		        		}
		        		
		        		
		        	}
					
					break;
			}
			
			
		}
		
		
		Channel channel = new Channel();
		
		Field[] channelFields = channel.getClass().getDeclaredFields();
		
		
		for (MsgTypeEnum msgType : enums) {
			
			
			switch (msgType) {
			case channel_pay_momey_val:
				
				for (Field fd : channelFields) {
	        		fd.setAccessible(true);
	        		
	        		String msgTypeEnumVal = getHumnName(msgType.name());
	        		
	        		if (msgTypeEnumVal.equals(fd.getName())) {
	        			
	        			
	        			String defChannelTotalKey = RedisMqKey.CHANNEL_TOTAL_QUEUE_TYPE_HLL + channelCode + ":" + msgType.name();
	        			
	        			Object totalValue = redisHandler.get(defChannelTotalKey);
	        			
	        			try {
							fd.set(channel, totalValue == null ? 0 :(int)totalValue);
						} catch (Exception e) {
							log.error("{}",e);
						}
	        			
	        		}
	        		
	        	}
				
				break;

			default:
				
				
				for (Field fd : channelFields) {
		    		fd.setAccessible(true);
		    		
		    		String msgTypeEnumVal = getHumnName(msgType.name());
		    		
		    		if (msgTypeEnumVal.equals(fd.getName())) {
		    			
		    			long count = redisHandler.pfcount(RedisMqKey.CHANNEL_TOTAL_QUEUE_TYPE_HLL + channelCode + ":"+ msgType.name());
		    			
		    			try {
							fd.set(channel, new Long(count).intValue());
						} catch (Exception e) {
							log.error("{}",e);
						} 
		    		}
		    		
		    		
		    	}
				
				
				break;
			}
			
		}
		
		channel.setChannelCode(channelCode);
		channel.setChannelName(channelName);
		channelStatisHour.setChannelTotalData(channel);
		
		return channelStatisHour;
	}
	
//	public static void main(String[] args) {
//		
//		long a= 111111111;
//		
////		new Long(a).intValue();
////		int b =(int) a;
//		
//		System.out.println(new Long(a).intValue());
//		
////		String a = MsgTypeEnum.channel_active_reg_people_count.name();
////		
////		System.out.println(getHumnName(a));
//		
////		String[] rest = new ChannelStatisService().getRedisHllHourList(RedisMqKey.CHANNEL_QUEUE_TYPE_HLL, MsgTypeEnum.channel_active_reg_people_count, "2021082110", "2021082317");
////		for (String res : rest) {
////			System.out.println(res);
////		}
//	}
	
	private String[] getRedisHllHourList(String keyPrefix, MsgTypeEnum msgType, String startTime, String endTime) {
		
		List<String> hourList = new ArrayList<String>();
		try {
			hourList = DateUtils.getHourListRange(new SimpleDateFormat("yyyyMMddHH").parse(startTime), new SimpleDateFormat("yyyyMMddHH").parse(endTime), "yyyyMMddHH");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> result = new ArrayList<String>();
		
		for (String hour : hourList) {
			
			result.add(keyPrefix + msgType.name() + ":" + hour);
			
		}
		
		String[] resArr = new String[result.size()];

		result.toArray(resArr);
		
		return resArr;
		
	}
	
	private static String getHumnName(String fieldName) {
        if (null == fieldName) {
            return "";
        }
        fieldName = fieldName.toLowerCase();
        char[] chars = fieldName.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(StringUtils.upperCase(CharUtils.toString(chars[j])));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}


