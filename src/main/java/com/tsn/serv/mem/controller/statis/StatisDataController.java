package com.tsn.serv.mem.controller.statis;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.statis.StatisRegUserKeepDay;
import com.tsn.serv.mem.entity.statis.StatisRegUserKeepMonth;
import com.tsn.serv.mem.entity.statis.StatisUserKeepDay;
import com.tsn.serv.mem.entity.statis.StatisUserKeepMonth;
import com.tsn.serv.mem.service.statis.StatisDataService;

@RestController
@RequestMapping("/statis")
public class StatisDataController {

	@Autowired
	private StatisDataService statisDataService;
	
	@GetMapping("/keep/calc/day/{yyyyMMdd}")
	public Response<?> calcKeepDay(@PathVariable String yyyyMMdd) {
		statisDataService.calcAddAndUpdateStaticUserKeepDay(yyyyMMdd);
		return Response.ok();
	}
	
	@GetMapping("/keep/calc/month/{yyyyMM}")
	public Response<?> calcKeepMonth(@PathVariable String yyyyMM) {
		statisDataService.calcAddAndUpdateStaticUserKeepMonth(yyyyMM);
		return Response.ok();
	}
	

	/**
	 *首页Chart展示
	 * @param homePageDto
	 * @return
	 */
	@GetMapping("/order")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> orderStatis() {
		Map<String, Object> orderDataMap = statisDataService.getAllOrderData();
		return Response.ok(orderDataMap);
	}
	
	@GetMapping("/user")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> userStatis() {
		Map<String, Object> userDataMap = statisDataService.getAllUserData();
		return Response.ok(userDataMap);
	}
	
	@GetMapping("/online")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> onlineStatis() {
		Map<String, Object> userDataMap = statisDataService.getOnlineUserAndDeviceCount();
		return Response.ok(userDataMap);
	}
	
	@GetMapping("/active/{type}/{time}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> onlineStatis(@PathVariable String type, @PathVariable String time) {
		Map<String, List<Map<String, Object>>> userDataMap = statisDataService.getActiveUserAndDeviceCount(type, time);
		return Response.ok(userDataMap);
	}
	
	/**
	 * 统计任务人数和任务次数
	 * @param type
	 * @param time
	 * @return
	 */
	@GetMapping("/task/people/{time}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> taskPeopleStatis(@PathVariable String time) {
		Map<String, Object> peopleDataMap = statisDataService.statisTaskPeopleMonth(time);
		return Response.ok(peopleDataMap);
	}
	
	/**
	 * 统计任务人数和任务次数
	 * @param type
	 * @param time
	 * @return
	 */
	@GetMapping("/task/num/{time}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> taskNumStatis(@PathVariable String time) {
		Map<String, Object> taskNumMap = statisDataService.statisTaskNumMonth(time);
		return Response.ok(taskNumMap);
	}
	
	@GetMapping("/active/path/count/{pathGrade}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> activePathCount(@PathVariable String pathGrade) {
		Map<String, Object> pathDataList = statisDataService.statisPathPeopleMonth(pathGrade);
		return Response.ok(pathDataList);
	}
	
	/**
	 * 留存人数统计，按今日，近3天，近5天，等等
	 * @param type
	 * @param time
	 * @return
	 */
	@GetMapping("/keep/people/day/{yyyyMM}/{type}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> keepPeopleStatisDay(@PathVariable String yyyyMM, @PathVariable String type) {
		List<StatisUserKeepDay> statisUserKeepDayList = statisDataService.getStatisUserKeepDayList(yyyyMM, type);
		return Response.ok(statisUserKeepDayList);
	}
	
	/**
	 * 留存人数统计
	 * @param type
	 * @param time
	 * @return
	 */
	@GetMapping("/keep/people/months/{year}/{type}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> keepPeopleStatisMonth(@PathVariable String year, @PathVariable String type) {
		List<StatisUserKeepMonth> statisUserKeepMonthList = statisDataService.getStatisUserKeepMonthList(year, type);
		return Response.ok(statisUserKeepMonthList);
	}
	
	/**
	 * 留存人数统计，按今日，近3天，近5天，等等
	 * @param type
	 * @param time
	 * @return
	 */
	@GetMapping("/keep/regpeople/day/{yyyyMM}/{type}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> keepRegPeopleStatisDay(@PathVariable String yyyyMM, @PathVariable String type) {
		List<StatisRegUserKeepDay> statisUserKeepDayList = statisDataService.getStatisRegUserKeepDayList(yyyyMM, type);
		return Response.ok(statisUserKeepDayList);
	}
	
	/**
	 * 留存人数统计
	 * @param type
	 * @param time
	 * @return
	 */
	@GetMapping("/keep/regpeople/months/{year}/{type}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> keepRegPeopleStatisMonth(@PathVariable String year, @PathVariable String type) {
		List<StatisRegUserKeepMonth> statisUserKeepMonthList = statisDataService.getStatisRegUserKeepMonthList(year, type);
		return Response.ok(statisUserKeepMonthList);
	}
}
