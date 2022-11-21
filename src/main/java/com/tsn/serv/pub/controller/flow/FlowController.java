package com.tsn.serv.pub.controller.flow;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.core.jms.MqHandler;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.common.cons.mq.MqCons;
import com.tsn.serv.pub.service.mem.MemFeignService;

/**
 * 
 * @author work
 *
 */
@RestController
@RequestMapping("flow")
public class FlowController {
	
	@Autowired
	private MqHandler mqHandler;
	
	@Autowired
	private MemFeignService memService;
	
	@PostMapping()
	public Response<?> flowUp(@RequestBody Map<String, Object> flowSatMap){
		mqHandler.send(MqCons.getFlowQueue(), JsonUtils.objectToJson(flowSatMap));
		return Response.ok();
	}
	
	@GetMapping("{memId}")
	public Response<?> getFlowInfo(@PathVariable String memId){
		Response<?> response = memService.queryMemLimit(memId);
		return response;
	}

}
