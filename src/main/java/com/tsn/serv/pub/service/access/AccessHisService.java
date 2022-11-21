package com.tsn.serv.pub.service.access;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.serv.pub.entity.access.AccessHis;
import com.tsn.serv.pub.entity.access.AccessHisDay;
import com.tsn.serv.pub.mapper.access.AccessHisDayMapper;
import com.tsn.serv.pub.mapper.access.AccessHisMapper;

@Service
public class AccessHisService {

	@Autowired
	private AccessHisMapper accessHisMapper;

	@Autowired
	private AccessHisDayMapper accessHisDayMapper;
	
	public void insertAccessHis(AccessHis accessHis) {
		
		accessHisMapper.insert(accessHis);
		
	}

	public void uPAccessHis(AccessHis accessHis) {

		accessHisMapper.updateByPrimaryKeySelective(accessHis);

	}
	
	public int updateAccessHis(AccessHis accessHis) {
		
		if (StringUtils.isEmpty(accessHis.getAccessId())) {
			throw new BusinessException("500000", "");
		}
		
		//如果大于10分钟 就没必要记录了
		if ((accessHis.getRemainTime() / 1000) > 10 * 60) {
			throw new BusinessException("500000", "");
		}
		
		return accessHisMapper.updateByPrimaryKeySelective(accessHis);
		
	}

	public void upAccessHisDayByStatEnd(String statDate, String endDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat("yyyyMMddHH");
		try {
			Date stat = simpleDateFormat.parse(statDate);
			Date end = simpleDateFormat.parse(endDate);
			// 计算出相差多少小时
			long nh = 1000*60*60;
			long hours = (end.getTime() - stat.getTime()) / nh;


			for (int i = 1; i <= hours; i++){
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(stat);
				calendar.add(calendar.HOUR,i);
				Date hourDate = calendar.getTime();
				List<Map<String, Object>> accessList = accessHisMapper.selectAccessByHour(hourDate);

				accessList.stream()
						.forEach(access -> {
							String day = simpleDateFormatTo.format(hourDate);
							String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
							String path = StringUtils.isEmpty(access.get("accessPath").toString()) ? "" : access.get("accessPath").toString();
							upAccessHisDay(access, day, hour, path);
						});
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void upAccessHisDayByDate(String date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat("yyyyMMddHH");


		try {
			Date hourDate = simpleDateFormat.parse(date);
			List<Map<String, Object>> accessList = accessHisMapper.selectAccessByHour(hourDate);

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(hourDate);
			accessList.stream()
					.forEach(access -> {
						String day = simpleDateFormatTo.format(hourDate);
						String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
						String path = StringUtils.isEmpty(access.get("accessPath").toString()) ? "" : access.get("accessPath").toString();

						upAccessHisDay(access, day, hour, path);
					});
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public void upAccessHisDay(Map<String, Object> access, String day, String hour, String path) {
		// 先查询
		AccessHisDay accessHisDay = accessHisDayMapper.selectListByDayHourPath(day, hour, path);
		String downNum = StringUtils.isEmpty(access.get("downNum").toString()) ? "0" : access.get("downNum").toString();
		if (accessHisDay == null) {
			accessHisDay = new AccessHisDay();
			accessHisDay.setDay(day);
			accessHisDay.setHour(hour);
			accessHisDay.setAccessNum(Integer.parseInt(access.get("accessNum").toString()));
			accessHisDay.setDownNum(Integer.parseInt(downNum));
			accessHisDay.setAccessPath(path);
			accessHisDayMapper.insert(accessHisDay);
		} else {
			accessHisDay.setAccessNum(Integer.parseInt(access.get("accessNum").toString()));
			accessHisDay.setDownNum(Integer.parseInt(downNum));
			accessHisDayMapper.updateByPrimaryKeySelective(accessHisDay);
		}
	}
}
