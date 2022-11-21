package com.tsn.serv.pub.controller.visits;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.service.visits.VisitsService;

@RestController
@RequestMapping("visits")
public class VisitsController {

	@Autowired
	private VisitsService visitsService;
	/**
	 * 访问量更新
	 * @param source
	 * @return
	 */
	@PutMapping("up")
	public Response<?> upVisits(String source){
		visitsService.upVisits(source);
		return Response.ok();
	}

	/**
	 * 根据时间和推广来源类型查询对应月数据
	 * @param time
	 * @param source
	 * @return
	 */
	@GetMapping("line")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getVisitsChart(String time, String source){
		Map<Object,Object> visitsMap =  visitsService.getVisitsChart(time, source);
		return Response.ok(visitsMap);
	}

	/**
	 * 获取所有访问来源类型
	 * @return
	 */
	@GetMapping("source")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getSourceType(){
		List<Map<Object, Object>> sourceMap =  visitsService.getSourceType();
		return Response.ok(sourceMap);
	}
}
