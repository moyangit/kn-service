package com.tsn.serv.mem.controller.source;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.mem.entity.source.SourceInvitStatisHour;
import com.tsn.serv.mem.service.source.MemSourceInvitStatisHourService;

@RestController
@RequestMapping("source/statis")
public class SourceInvitStatisHourController {
	
	@Autowired
	private MemSourceInvitStatisHourService sourceInvitStatisHourService;
	
	/**
	 * 计算所有邀请码的数量
	 * @param calcTime
	 * @return
	 */
	@PostMapping("/all/{calcTime}")
	public Response<?> calcSourceInvitStaticHour(@PathVariable long calcTime) {
		sourceInvitStatisHourService.insertAllSourceInvitStaticHour(calcTime);
		return Response.ok();
	}
	
	/**
	 * 计算某一个邀请码的数量
	 * @param invitCode
	 * @param calcTime
	 * @return
	 */
	@PostMapping("/code/{invitCode}/{calcTime}")
	public Response<?> calcSourceInvitStaticHour(@PathVariable("invitCode") String invitCode, @PathVariable("calcTime") long calcTime) {
		sourceInvitStatisHourService.insertSourceInvitStaticHour(invitCode, calcTime);
		return Response.ok();
	}
	
	@GetMapping("/data/{invitCode}/{startTime}/{endTime}")
	public Response<?> getSourceHourDataByTime(@PathVariable("invitCode") String invitCode, @PathVariable("startTime") String startTime, @PathVariable("endTime") String endTime) {
		List<SourceInvitStatisHour> sourceList = sourceInvitStatisHourService.querySourceInvitStatisHourByTime(invitCode, startTime, endTime);
		return Response.ok(sourceList);
	}

	@GetMapping("/data/path/{sourcePath}/{startTime}/{endTime}")
	public Response<?> selectBySourcePathAndTime(@PathVariable("sourcePath") String sourcePath, @PathVariable("startTime") String startTime, @PathVariable("endTime") String endTime) {
		List<SourceInvitStatisHour> sourceList = sourceInvitStatisHourService.querySourceInvitStatisHourByPathTime(sourcePath, startTime, endTime);
		return Response.ok(sourceList);
	}
	
	@PostMapping("/chart/{source}/{monthDate}")
	public Response<?> getInviterChart(@PathVariable("source")String source, @PathVariable("monthDate")String monthDate) {
		List<String> sourceList = sourceInvitStatisHourService.getInviterChart(source,monthDate);
		return Response.ok(sourceList);
	}

	@PostMapping("/chart/{source}/{statDate}/{endDate}")
	public Response<?> getInviterChartTo(@PathVariable("source")String source, @PathVariable("statDate")String statDate, @PathVariable("endDate")String endDate) {
		List<String> sourceList = sourceInvitStatisHourService.getInviterChartTo(source, statDate, endDate);
		return Response.ok(sourceList);
	}

	@PostMapping("/chart/datTime/{source}/{dayTime}")
	public Response<?> getInviterByDayTime(@PathVariable("source")String source, @PathVariable("dayTime")String dayTime) {
		Map<String, Object> sourceList = sourceInvitStatisHourService.getInviterByDayTime(source, dayTime);
		return Response.ok(sourceList);
	}

}
