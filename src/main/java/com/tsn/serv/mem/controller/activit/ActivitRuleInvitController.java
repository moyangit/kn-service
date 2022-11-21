package com.tsn.serv.mem.controller.activit;

import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.pojo.Response;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.activit.ActivitRuleInvit;
import com.tsn.serv.mem.service.activit.ActivitRuleInvitService;

@RestController
@RequestMapping("activit/rule/invit")
public class ActivitRuleInvitController {
	
	@Autowired
	private ActivitRuleInvitService activitRuleInvitService;
	
	@PutMapping()
	@ApiOperation(value = "更新",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<ActivitRuleInvit> update(@RequestBody ActivitRuleInvit activitRuleInvit) {
		activitRuleInvitService.updateById(activitRuleInvit);
		return Response.ok();
	}
	
	@PostMapping()
	@ApiOperation(value = "添加",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<ActivitRuleInvit> add(@RequestBody ActivitRuleInvit activitRuleInvit) {
		activitRuleInvitService.save(activitRuleInvit);
		return Response.ok();
	}
	
	@GetMapping("/list")
	@ApiOperation(value = "列表查询",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<List<ActivitRuleInvit>> list() {
		List<ActivitRuleInvit> activitRuleInvitList = activitRuleInvitService.list();
		return Response.ok(activitRuleInvitList);
	}
	

}
