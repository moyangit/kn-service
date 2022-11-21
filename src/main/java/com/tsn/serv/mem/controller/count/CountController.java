package com.tsn.serv.mem.controller.count;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.reqData.HomePageDto;
import com.tsn.serv.mem.entity.reqData.TodayChargeOrderDto;
import com.tsn.serv.mem.entity.reqData.UserDataStatisDto;
import com.tsn.serv.mem.service.count.CountService;

@RestController
@RequestMapping("/count")
public class CountController {

	@Autowired
	private CountService countService;

	/**
	 *首页Chart展示
	 * @param homePageDto
	 * @return
	 */
	@GetMapping("/homePage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> homePageData(HomePageDto homePageDto) {
		HomePageDto homePageDtoList = countService.homePageData(homePageDto);
		return Response.ok(homePageDtoList);
	}

	/**
	 * 首页收入曲线图(月)
	 * @param monthDate
	 * @return
	 */
	@GetMapping("/lineChart")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> lineChart(String monthDate) {
		Map<String,Object> LineChartMap = countService.getLineChartData(monthDate);
		return Response.ok(LineChartMap);
	}

	/**
	 * 首页会员曲线图(月)
	 * @param monthDate
	 * @return
	 */
	@GetMapping("/memLineChart")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> memLineChart(String monthDate) {
		Map<String,Object> memLineChartMap = countService.getMemLineChart(monthDate);
		return Response.ok(memLineChartMap);
	}

	/**
	 * 首页数据汇总(年)
	 * @param yearDate
	 * @return
	 */
	@GetMapping("/yearLineChart")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> yearLineChart(String yearDate) {
		Map<String,Object> memLineChartMap = countService.getYearLineChart(yearDate);
		return Response.ok(memLineChartMap);
	}

	/**
	 * 根据日期获取对应当天时间段各个资费类型的充值金额，默认获取当日
	 * @param selectDate
	 * @return
	 */
	@GetMapping("/todayLineChart")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> todayLineChart(String selectDate){
		TodayChargeOrderDto todayChargeOrderDto = countService.todayLineChart(selectDate);
		return Response.ok(todayChargeOrderDto);
	}

	/**
	 * 今日新增用户图表
	 * @param selectDate
	 * @return
	 */
	@GetMapping("/todayBarChart")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> todayBarChart(String selectDate){
		Map<String, Object> barChartMap = countService.getBarChartList(selectDate);
		return Response.ok(barChartMap);
	}

	/**
	 * 用户数据统计面板查询
	 * @param userDataStatisDto
	 * @return
	 */
	@GetMapping("/userData")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> userData(UserDataStatisDto userDataStatisDto) {
		UserDataStatisDto userDataStatusList = countService.userDataStatusList(userDataStatisDto);
		return Response.ok(userDataStatusList);
	}

	/**
	 * 统计每个设备端的使用人数
	 * @return
	 */
	@GetMapping("/deviceNum")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deviceNum(String selectType) {
		List<Map<String, Object>> deviceNumList = countService.getDeviceNum(selectType);
		return Response.ok(deviceNumList);
	}

	/**
	 * 统计每个端的在线人数(获取最近两分钟内的数据，根据updateTime)
	 * @return
	 */
	@GetMapping("/olineDeviceNum")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> olineDeviceNum(String selectType) {
		Map<String, Object> deviceNumList = countService.getOlineDeviceNum(selectType);
		return Response.ok(deviceNumList);
	}

	/**
	 * 根据用户手机号和月份统计当前月每天的流量信息
	 * @param memPhone
	 * @param monthDate
	 * @return
	 */
	@GetMapping("/memFlowDayLine")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> memFlowDay(@RequestParam String memPhone, @RequestParam String monthDate) {
		Map<String, Object> lineMap = countService.memFlowDay(memPhone, monthDate);
		return Response.ok(lineMap);
	}

	/**
	 * 统计过期用户(充值过期用户和未充值过期用户)
	 * @return
	 */
	@GetMapping("/memExpire")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getMemExpire() {
		List<Map<String,Object>> memList = countService.getMemExpire();
		return Response.ok(memList);
	}
}
