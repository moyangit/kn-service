package com.tsn.serv.mem.controller.credits;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.credits.CreditsTask;
import com.tsn.serv.mem.service.credits.CreditsTaskService;

/**
 * 任务控制台
 * @author work
 *
 */
@RestController
@RequestMapping("creditsTask")
public class CreditsTaskController {
	
	@Autowired
	private CreditsTaskService creditsTaskService;
	
	/**
	 * 获取任务列表
	 * @param memId
	 * @return
	 */
	@GetMapping("/list/{memId}/{deviceType}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> selectCreditsTask(@PathVariable String memId,@PathVariable String deviceType){
		
		List<CreditsTask> creditsTasks = creditsTaskService.selectCreditsTaskByEntity(memId,deviceType);
		
		return Response.ok(creditsTasks);
	}
	
	@GetMapping("/list/{deviceType}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> selectCreditsTask(@PathVariable String deviceType){
		JwtInfo info = JwtLocal.getJwt();
		String memId = info.getSubject();
		List<CreditsTask> creditsTasks = creditsTaskService.selectCreditsTaskByEntity(memId,deviceType);
		
		return Response.ok(creditsTasks);
	}
	
	/**
	 * win在用
	 * @param page
	 * @return
	 */
	@GetMapping("page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> selectByPage(Page page){
		JwtInfo info = JwtLocal.getJwt();
		String memId = info.getSubject();
		List<CreditsTask> creditsTasks = creditsTaskService.selectByPage(memId, page);
		page.setData(creditsTasks);
		
		return Response.ok(page);
	}

	/**根据taskType获取对应信息
	 * @param taskType
	 * @return
	 */
	@GetMapping("byType")
	public Response<?> getCreditsTaskByTaskType(String taskType, String deviceType) {
		CreditsTask creditsTask = creditsTaskService.getCreditsTaskByTaskType(taskType, deviceType);
		return Response.ok(creditsTask);
	}

}
