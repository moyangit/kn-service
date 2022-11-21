package com.tsn.serv.pub.service.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tsn.common.utils.web.entity.Response;

@Service
public class SourceInvitService {

	@Autowired
	private ISourceInvitService sourceInvitService;
	
	//feign调用mem项目，计算小时数据

	public void calcSourceInvitHour(long calcTime) {
		sourceInvitService.calcSourceInvitStaticHour(calcTime);
	}

	public List<String> getInviterChart(String source, String monthDate) {
		Response<?> inviterResult = sourceInvitService.getInviterChart(source, monthDate);
		return (List<String>)inviterResult.getData();
	}

	public List<String> getInviterChartTo(String source, String statDate, String endDate) {
		Response<?> inviterResult = sourceInvitService.getInviterChartTo(source, statDate, endDate);
		return (List<String>)inviterResult.getData();
	}
	
	public List<Map<String, Object>> getSourceHourDataByTime(String source, String statDate, String endDate) {
		Response<?> inviterResult = sourceInvitService.getSourceHourDataByPathTime(source, statDate, endDate);
		return (List<Map<String, Object>>)inviterResult.getData();
	}

	public Map<String,Object> getInviterByDayTime(String source, String dayTime) {
		Response<?> inviterResult = sourceInvitService.getInviterByDayTime(source, dayTime);
		return (Map<String,Object>)inviterResult.getData();
	}

	public void upSourceInvitByStatEnd(String statDate, String endDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
				sourceInvitService.calcSourceInvitStaticHour(hourDate.getTime());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void upSourceInvitDayByDate(String date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dateTo = simpleDateFormat.parse(date);
			sourceInvitService.calcSourceInvitStaticHour(dateTo.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@FeignClient(name = "hb-service", url="${feign.client.url.addr}")
	@Deprecated
	public interface ISourceInvitService {
		
		@RequestMapping(value="source/statis/all/{calcTime}",method=RequestMethod.POST)
		public Response<?> calcSourceInvitStaticHour(@PathVariable("calcTime") long calcTime);
		
		@RequestMapping(value="source/statis/data/path/{sourcePath}/{startTime}/{endTime}",method=RequestMethod.GET)
		public Response<?> getSourceHourDataByPathTime(@PathVariable("sourcePath")String sourcePath, @PathVariable("startTime")String startTime, @PathVariable("endTime")String endTime);

		@RequestMapping(value="source/statis/chart/{source}/{monthDate}",method=RequestMethod.POST)
		public Response<?> getInviterChart( @PathVariable("source")String source, @PathVariable("monthDate")String monthDate);

		@RequestMapping(value="source/statis/chart/{source}/{statDate}/{endDate}",method=RequestMethod.POST)
		public Response<?> getInviterChartTo(@PathVariable("source")String source, @PathVariable("statDate")String statDate, @PathVariable("endDate")String endDate);

		@RequestMapping(value="source/statis/chart/datTime/{source}/{dayTime}",method=RequestMethod.POST)
		public Response<?> getInviterByDayTime(@PathVariable("source")String source, @PathVariable("dayTime")String dayTime);
	}
}
