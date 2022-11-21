package com.tsn.serv.pub.service.access;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.pub.mapper.access.AccessHisDayMapper;
import com.tsn.serv.pub.mapper.access.AccessHisMapper;
import com.tsn.serv.pub.service.source.SourceInvitService;

@Service
public class AccessHisDayService {

	private static Logger logger = LoggerFactory.getLogger(AccessHisDayService.class);

	@Autowired
	private AccessHisMapper accessHisMapper;

	@Autowired
	private AccessHisDayMapper accessHisDayMapper;

	@Autowired
	private AccessHisService accessHisService;

	@Autowired
	private SourceInvitService sourceInvitService;

	public void upAccessHisDay() {
		System.out.println("整点执行：" + new Date());

		// 获取最近一小时的访问量
		List<Map<String, Object>> accessList =  accessHisMapper.selectAccessByLastHour();

		accessList.stream()
				.forEach(access -> {
					SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat("yyyyMMddHH");
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.HOUR_OF_DAY, -1);
					String day = simpleDateFormatTo.format(calendar.getTime());
					String hour;
					if (new GregorianCalendar().get(Calendar.HOUR_OF_DAY) == 0) {
						hour = String.valueOf(23);
					} else {
						hour = String.valueOf(new GregorianCalendar().get(Calendar.HOUR_OF_DAY) - 1);
					}
					String path = StringUtils.isEmpty(access.get("accessPath").toString()) ? "" : access.get("accessPath").toString();
					accessHisService.upAccessHisDay(access, day, hour, path);
				});
	}

	public Map<Object, Object> getAccessChart(String monthDate, String source, String selectType, String statDate, String endDate) {
		/*String monthDateTo = DateUtils.getCurrYMD(new Date(), "yyyyMMdd");
		String statDateTo = DateUtils.getCurrYMD(new Date(), "yyyyMMddHH");
		String endDateTo = DateUtils.getCurrYMD(new Date(), "yyyyMMddHH");
		if (!StringUtils.isEmpty(monthDate)) {
			monthDateTo = DateUtils.getCurrYMD(new Date(Long.parseLong(monthDate)), "yyyyMMdd");
		}
		if (!StringUtils.isEmpty(statDate)) {
			statDateTo = DateUtils.getCurrYMD(new Date(Long.parseLong(statDate)), "yyyyMMddHH");
		}
		if (!StringUtils.isEmpty(endDate)) {
			endDateTo = DateUtils.getCurrYMD(new Date(Long.parseLong(endDate)), "yyyyMMddHH");
		}*/
		logger.debug("--------getAccessChart, monthDate:{}, source:{}, selectType:{}, statDate:{}, endDate:{}",monthDate, source, selectType, statDate, endDate);
		Map<Object, Object> retnMap = new HashMap<>();
		try {
			String monthDateTo = "";
			String statDateTo = "";
			String endDateTo = "";
			//获取所有时间小时数据
			List<String> hourList = null;
			if (!StringUtils.isEmpty(monthDate)) {
				Date monthDateTime = new Date(Long.parseLong(monthDate));
				monthDate = DateUtils.getCurrYMD(monthDateTime,"yyyy-MM-dd");
				Date startTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(monthDate + " 00:00:00");
				Date endTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(monthDate + " 23:59:59");
				statDateTo = DateUtils.getCurrYMD(startTimeDate, "yyyyMMddHH");
				endDateTo = DateUtils.getCurrYMD(endTimeDate, "yyyyMMddHH");
				hourList = DateUtils.getHourListRange(startTimeDate, endTimeDate, "yyyyMMddHH");
				monthDateTo = DateUtils.getCurrYMD(monthDateTime, "yyyyMMdd");
			}

			if (!StringUtils.isEmpty(statDate) && !StringUtils.isEmpty(endDate)) {
				
				hourList = DateUtils.getHourListRange(new Date(Long.parseLong(statDate)), new Date(Long.parseLong(endDate)), "yyyyMMddHH");
				statDateTo = DateUtils.getCurrYMD(new Date(Long.parseLong(statDate)), "yyyyMMddHH");
				endDateTo = DateUtils.getCurrYMD(new Date(Long.parseLong(endDate)), "yyyyMMddHH");
			}

			logger.debug("----hourList = {}", hourList);

			//获取访问数据数据-小时
			List<Map<Object, Object>> accessList = accessHisDayMapper.getAccessChart(monthDateTo, source, selectType, statDateTo, endDateTo);
			logger.debug("----accessList = {}", accessList);
			//获取邀请数据-小时
			List<Map<String, Object>> sourceList = sourceInvitService.getSourceHourDataByTime(source, statDateTo, endDateTo);
			logger.debug("----sourceList = {}", sourceList);
			List<String> keyList = new ArrayList<>();
			List<String> valueList = new ArrayList<>();
			List<String> downList = new ArrayList<>();
			List<String> inviterList = new ArrayList<>();
			//获取时间段所有的小时
			for (String hour : hourList) {
                String dayTime = hour.substring(0, 4) + "-"
                        + hour.substring(4, 6) + "-"
                        + hour.substring(6, 8) + " "
                        + hour.substring(8, 10)
                        + ":00";
				keyList.add(dayTime);

				boolean isExist = false;

				if (accessList != null && !accessList.isEmpty()) {
					for (int i =0; i < accessList.size(); i++){

						if (accessList.get(i).get("dayTime").equals(hour)) {
							isExist = true;
							valueList.add(String.valueOf(accessList.get(i).get("accessNum")));
							downList.add(String.valueOf(accessList.get(i).get("downNum")));
							break;
						}

					}
				}

				if (!isExist) {
					valueList.add("0");
					downList.add("0");
				}

				isExist = false;

				if (sourceList != null && !sourceList.isEmpty()) {
					for (int i =0; i < sourceList.size(); i++){

						if (sourceList.get(i).get("dayTime").equals(hour)) {
							isExist = true;
							inviterList.add(String.valueOf(sourceList.get(i).get("num")));
							break;
						}

					}
				}

				if (!isExist) {
					inviterList.add("0");
				}

				/*Map<String, Object> inviterMap = sourceInvitService.getInviterByDayTime(source, hour);
				if (inviterMap == null) {
					inviterList.add("0");
				} else {
					inviterList.add(inviterMap.get("inviterNum").toString());
				}*/




			}


			/*List<Map<Object, Object>> accessChartList = accessHisDayMapper.getAccessChart(monthDateTo, source, selectType, statDateTo, endDateTo);
			Map<Object, Object> retnMap = new HashMap<>();
			List<String> keyList = new ArrayList<>();
			List<String> valueList = new ArrayList<>();
			List<String> downList = new ArrayList<>();
			List<String> inviterList = new ArrayList<>();

			for (int i =0; i < accessChartList.size(); i++){
				String dayTime = accessChartList.get(i).get("dayTime").toString().substring(0, 4) + "-"
						+ accessChartList.get(i).get("dayTime").toString().substring(4, 6) + "-"
						+ accessChartList.get(i).get("dayTime").toString().substring(6, 8);
				keyList.add(dayTime + " " + accessChartList.get(i).get("hourTime").toString() + ":00");
				valueList.add(accessChartList.get(i).get("accessNum").toString());
				downList.add(accessChartList.get(i).get("downNum").toString());

				Map<String, Object> inviterMap = sourceInvitService.getInviterByDayTime(source, accessChartList.get(i).get("dayTime").toString());
				if (inviterMap == null) {
					inviterList.add("0");
				} else {
					inviterList.add(inviterMap.get("inviterNum").toString());
				}
			}*/
/*		accessChartList.stream()
					.forEach(access -> {
						keyList.add(access.get("hourTime").toString());
						valueList.add(access.get("accessNum").toString());
						downList.add(access.get("downNum").toString());
					});*/
			// 获取邀请码填写次数

/*		if ("0".equals(selectType)) {
				inviterList = sourceInvitService.getInviterChart(source, monthDateTo);
			} else if("1".equals(selectType)) {
				inviterList = sourceInvitService.getInviterChartTo(source, statDateTo, endDateTo);
			}*/
			retnMap.put("keyList", keyList);
			retnMap.put("valueList", valueList);
			retnMap.put("downList", downList);
			retnMap.put("inviterList", inviterList);
			return retnMap;
		} catch (Exception e) {
			logger.error("getAccessChart error ,e = {}", e);
		}

		return retnMap;
	}

	public List<Map<Object, Object>> getAccessType(String time, String selectType, String statDate, String endDate) {
		Date dayDate = new Date(Long.parseLong(time));
		List<Map<Object, Object>> accessTypeList = accessHisDayMapper.getAccessType(dayDate, selectType, statDate, endDate);
		return accessTypeList;
	}

}
