package com.tsn.serv.pub.controller.access;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.service.access.AccessHisDayService;

@RestController
@RequestMapping("accessDay")
public class AccessHisDayController {

	@Autowired
	private AccessHisDayService accessHisDayService;

	/**
	 * 根据时间和推广来源类型查询对应天数据
	 * @param monthDate
	 * @param source
	 * @return
	 */
	@GetMapping("line")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getAccessChart(String monthDateTo, String source, String selectType, String statDateTo, String endDateTo){
		Map<Object,Object> accessMap =  accessHisDayService.getAccessChart(monthDateTo, source, selectType, statDateTo, endDateTo);
		return Response.ok(accessMap);
	}

	/**
	 * 获取所有访问来源类型
	 * @return
	 */
	@GetMapping("accessType")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getAccessType(String time, String selectType, String statDate, String endDate){
		List<Map<Object, Object>> accessMap =  accessHisDayService.getAccessType(time, selectType, statDate, endDate);
		return Response.ok(accessMap);
	}
}
