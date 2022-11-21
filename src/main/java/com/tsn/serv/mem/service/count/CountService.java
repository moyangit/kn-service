package com.tsn.serv.mem.service.count;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.charge.ChargeTypeEum;
import com.tsn.serv.mem.entity.device.MemDevice;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.reqData.HomePageDto;
import com.tsn.serv.mem.entity.reqData.TodayChargeOrderDto;
import com.tsn.serv.mem.entity.reqData.UserDataStatisDto;
import com.tsn.serv.mem.mapper.device.MemDeviceMapper;
import com.tsn.serv.mem.mapper.device.MemDeviceOlineMapper;
import com.tsn.serv.mem.mapper.flow.FlowDayMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.order.ChargeOrderMapper;


@Service
public class CountService {

	@Autowired
	private ChargeOrderMapper chargeOrderMapper;

	@Autowired
	private MemInfoMapper memMapper;

	@Autowired
	private MemDeviceMapper memDeviceMapper;

	@Autowired
	private MemDeviceOlineMapper memDeviceOlineMapper;

	@Autowired
	private FlowDayMapper flowDayMapper;

	@Autowired
	private RedisHandler redisHandler;

	private static final String key = "guest:deviceNo:memId:";

	public HomePageDto homePageData(HomePageDto homePageDto) {
		Map<String,Object> chartDataList = chargeOrderMapper.getChartData();
		List<Map<String,Object>> chargeDataList = chargeOrderMapper.getChargeData(homePageDto);
		List<Map<String,Object>> invitationDataList = chargeOrderMapper.getInvitationData(homePageDto);


		// 获取游客总人数
		Map<String, Object> map = new HashMap<String, Object>();
		Set<String> keys = redisHandler.keys(key+"*");
		int guestTotalNum = keys.size();
		map.put("name", "游客");
		map.put("value", guestTotalNum);
		// 获取注册用户总人数
		Map<String,Object> regDataMap = chargeOrderMapper.getCustomerData();
		Map<String,Object> noRegDataMap = new HashMap<>();
		noRegDataMap.put("name", "未注册用户");
		noRegDataMap.put("value", guestTotalNum - Integer.parseInt(regDataMap.get("value").toString()));

		List<Map<String,Object>> customerDataList = new ArrayList<>();
		customerDataList.add(map);
		customerDataList.add(regDataMap);
		customerDataList.add(noRegDataMap);

		HomePageDto homePageDtoList = new HomePageDto();
		homePageDtoList.setChartData(chartDataList);
		homePageDtoList.setChargeData(chargeDataList);
		homePageDtoList.setCustomerData(customerDataList);
		homePageDtoList.setInvitationData(invitationDataList);

		return homePageDtoList;
	}

	public Map<String, Object> getLineChartData(String monthDate) {

		List<Map<String,Object>> lineChartDataList = chargeOrderMapper.getLineChartData(monthDate);

		List<Map<String,Object>> getLineChartDataTo = chargeOrderMapper.getLineChartDataTo(monthDate);
		//日期
		List<String> chartTypeDataList = new ArrayList<>();
		//普通会员
		List<String> generalMemberList = new ArrayList<>();
		//代理会员
		List<String> actingMemberList = new ArrayList<>();

		for(int i = 0;i < lineChartDataList.size();i++){
			chartTypeDataList.add(lineChartDataList.get(i).get("day").toString());
			generalMemberList.add(lineChartDataList.get(i).get("generalMember").toString());
			actingMemberList.add(getLineChartDataTo.get(i).get("actingMember").toString());
		}

		Map<String,Object> lineChartDataMap = new HashMap<>();
		lineChartDataMap.put("generalMemberList",generalMemberList);
		lineChartDataMap.put("actingMemberList",actingMemberList);

		Map<String,Object> lineChartDataMapTo = new HashMap<>();
		lineChartDataMapTo.put("chartTypeDataList", chartTypeDataList);
		lineChartDataMapTo.put("lineChartData", lineChartDataMap);
		return lineChartDataMapTo;
	}

	public TodayChargeOrderDto todayLineChart(String selectDate) {
		TodayChargeOrderDto todayChargeOrderDto = new TodayChargeOrderDto();

		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("selectDate", selectDate);

		// 月卡
		selectMap.put("chargeType", ChargeTypeEum.month.getType());
		List<Map> monthCard = chargeOrderMapper.getCardList(selectMap);
		// 季卡
		selectMap.put("chargeType", ChargeTypeEum.quarter.getType());
		List<Map> seasonCard = chargeOrderMapper.getCardList(selectMap);
		// 半年卡
		selectMap.put("chargeType", ChargeTypeEum.halfYear.getType());
		List<Map> halfYearCard = chargeOrderMapper.getCardList(selectMap);
		// 年卡
		selectMap.put("chargeType", ChargeTypeEum.year.getType());
		List<Map> yearCard = chargeOrderMapper.getCardList(selectMap);
		// 永久
		selectMap.put("chargeType", ChargeTypeEum.forever.getType());
		List<Map> permanentCard = chargeOrderMapper.getCardList(selectMap);

		List<String> chartTypeDataList = new ArrayList<>();
		Map<String, Object> lineChartData = new HashMap<>();
		List<String> monthCardList = new ArrayList<>();
		List<String> seasonCardList = new ArrayList<>();
		List<String> halfYearCardList = new ArrayList<>();
		List<String> yearCardList = new ArrayList<>();
		List<String> permanentCardList = new ArrayList<>();
		for(int i = 0;i < monthCard.size(); i++){
			chartTypeDataList.add(monthCard.get(i).get("hour").toString() + ":00");
			monthCardList.add(monthCard.get(i).get("finalPrice").toString());
			seasonCardList.add(seasonCard.get(i).get("finalPrice").toString());
			halfYearCardList.add(halfYearCard.get(i).get("finalPrice").toString());
			yearCardList.add(yearCard.get(i).get("finalPrice").toString());
			permanentCardList.add(permanentCard.get(i).get("finalPrice").toString());
		}

		lineChartData.put("monthCardList", monthCardList);
		lineChartData.put("seasonCardList", seasonCardList);
		lineChartData.put("halfYearCardList", halfYearCardList);
		lineChartData.put("yearCardList", yearCardList);
		lineChartData.put("permanentCardList", permanentCardList);

		todayChargeOrderDto.setChartTypeDataList(chartTypeDataList);
		todayChargeOrderDto.setLineChartData(lineChartData);
		return todayChargeOrderDto;
	}

	public Map<String, Object> getBarChartList(String selectDate) {
		// 获取今日新增用户占比
		Map<String, Object> memMap = memMapper.getTodayAddMem(selectDate);
		// 获取今日新增充值用户占比
		Map<String, Object> memChargeMap = memMapper.getTodayAddChargeMem(selectDate);
		// 获取今日新增用户充值占比
		Map<String, Object> chargeMap = memMapper.getTodayAddCharge(selectDate);

		Map<String, Object> noMemChargeMap = new HashMap<>();
		// 今日新增自行注册但未充值用户
		noMemChargeMap.put("zjzcNum",Integer.parseInt(memMap.get("zjzcNum").toString()) - Integer.parseInt(memChargeMap.get("zjzcNum").toString()));
		// 今日新增被邀请但未充值用户
		noMemChargeMap.put("byqNum",Integer.parseInt(memMap.get("byqNum").toString()) - Integer.parseInt(memChargeMap.get("byqNum").toString()));

		Map<String, Object> retnMap = new HashMap<>();
		retnMap.put("memMap", memMap);
		retnMap.put("memChargeMap", memChargeMap);
		retnMap.put("chargeMap", chargeMap);
		retnMap.put("noMemChargeMap", noMemChargeMap);
		return retnMap;
	}

	public UserDataStatisDto userDataStatusList(UserDataStatisDto userDataStatisDto) {

		Map<String,Object> chartDataList = memMapper.getChartData(userDataStatisDto);
		List<Map<String,Object>> chargeDataList = memMapper.getChargeData(userDataStatisDto);
		List<Map<String,Object>> invitationDataList = memMapper.getInvitationData(userDataStatisDto);

		UserDataStatisDto userDataStatisList = new UserDataStatisDto();
		userDataStatisList.setChartData(chartDataList);
		userDataStatisList.setChargeData(chargeDataList);
		userDataStatisList.setInvitationData(invitationDataList);
		return userDataStatisList;
	}

	public Map<String, Object> getMemLineChart(String monthDate) {
		List<Map<String,Object>> memLineChart = memMapper.getMemLineChart(monthDate);

		List<Map<String,Object>> memLineChartTo = memMapper.getMemLineChartTo(monthDate);
		//日期
		List<String> chartDataList = new ArrayList<>();
		//自行注册
		List<String> zxzcMemberList = new ArrayList<>();
		//被邀请
		List<String> byqMemberList = new ArrayList<>();

		for(int i = 0;i < memLineChart.size();i++){
			chartDataList.add(memLineChart.get(i).get("day").toString());
			zxzcMemberList.add(memLineChart.get(i).get("zxzcNum").toString());
			byqMemberList.add(memLineChartTo.get(i).get("byqNum").toString());
		}

		Map<String,Object> lineChartDataMap = new HashMap<>();
		lineChartDataMap.put("zxzcMemberList",zxzcMemberList);
		lineChartDataMap.put("byqMemberList",byqMemberList);
		lineChartDataMap.put("guestMemberList",selectGuestNumByMonth(monthDate));

		Map<String,Object> lineChartDataMapTo = new HashMap<>();
		lineChartDataMapTo.put("chartDataList", chartDataList);
		lineChartDataMapTo.put("memLineChart", lineChartDataMap);
		return lineChartDataMapTo;
	}
	
	public List<Long> selectGuestNumByMonth(String month){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		List<Long> numbers = new ArrayList<Long>();
		try {
			Date date = sdf.parse(month);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			for (int j = 1; j <= i; j++) {
				String m = "";
				if(j < 10) {
					m = "0" + j;
				}else {
					m = j+"";
				}
				String day = month + m;
				long size = this.getGuestNumByDay(day);
				numbers.add(size);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return numbers;
	}
	
	
	public Long getGuestNumByDay(String day){
		//return redisHandler.zSize(RedisKey.GUEST_DAY + day);
		return redisHandler.pfcount(RedisKey.GUEST_DAY_PF + day);
	}

	public Map<String, Object> getYearLineChart(String yearDate) {
		List<String> chargeLineChart = chargeOrderMapper.getChargeLineChart(yearDate);

		List<String> memBarChart = memMapper.getMemBarChart(yearDate);

		Map<String, Object> retnMap = new HashMap<>();
		retnMap.put("chargeLineChart", chargeLineChart);
		retnMap.put("memBarChart", memBarChart);
		return retnMap;
	}

	public List<Map<String,Object>> getDeviceNum(String selectType) {
		// 获取每个设备端的使用人数
		List<Map<String,Object>> deviceNumList = memDeviceMapper.getDeviceNum(selectType);
		return deviceNumList;
	}

	public Map<String,Object> getOlineDeviceNum(String selectType) {
		// // 查询最近两分钟内有更新的设备信息
		List<MemDevice> memDeviceList = memDeviceMapper.getAllUpDevice(selectType);
		Map<String,Object> lineDevice = new HashMap<>();

		int android = 0;
		int ios = 0;
		int pc = 0;
		int mac = 0;

		for (MemDevice device : memDeviceList){
			if ("10".equals(device.getDeviceType())) {
				android += 1;
			} else if ("11".equals(device.getDeviceType())) {
				ios += 1;
			} else if ("12".equals(device.getDeviceType())) {
				pc += 1;
			} else if ("13".equals(device.getDeviceType())) {
				mac += 1;
			}
		}

		lineDevice.put("Android",android);
		lineDevice.put("IOS",ios);
		lineDevice.put("PC",pc);
		lineDevice.put("Mac",mac);
		return lineDevice;
	}

	public Map<String, Object> memFlowDay(String memPhone, String monthDate) {
		Map<String, String> requestMap = new HashMap<>();
		MemInfo memInfo = memMapper.selectMemByPhone(memPhone);
		if (memInfo != null) {
			requestMap.put("memId", memInfo.getMemId());
			requestMap.put("monthDate", monthDate);

			List<Map> lineChart = flowDayMapper.memFlowDay(requestMap);

			//日期
			List<String> chartDataList = new ArrayList<>();
			//上行
			List<String> upFlowList = new ArrayList<>();
			//下行
			List<String> downFlowList = new ArrayList<>();

			lineChart.stream()
					.forEach(flow -> {
						chartDataList.add(flow.get("day").toString());
						upFlowList.add(flow.get("upFlow").toString());
						downFlowList.add(flow.get("downFlow").toString());
					});

			Map<String,Object> lineChartDataMap = new HashMap<>();
			lineChartDataMap.put("upFlowList",upFlowList);
			lineChartDataMap.put("downFlowList",downFlowList);

			Map<String,Object> lineChartDataMapTo = new HashMap<>();
			lineChartDataMapTo.put("chartDataList", chartDataList);
			lineChartDataMapTo.put("memLineChart", lineChartDataMap);
			return lineChartDataMapTo;
		}
		return null;
	}

	public List<Map<String,Object>> getMemExpire() {
		List<Map<String,Object>> mapList = memMapper.getMemExpire();
		return mapList;
	}
}