package com.tsn.serv.mem.controller.charge;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.charge.AliSubject;
import com.tsn.serv.mem.service.charge.AliSubjectService;

@RestController
@RequestMapping("aliSubject")
public class AliSubjectController {
	
	@Autowired
	private AliSubjectService aliSubjectService;

	@GetMapping("/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getAliSubjectList(Page page) {
		aliSubjectService.getAliSubjectList(page);
		return Response.ok(page);
	}

	@PostMapping("/addAliSubject")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addAliSubject(@RequestBody List<AliSubject> aliSubjectList) {
		aliSubjectService.addAliSubject(aliSubjectList);
		return Response.ok();
	}

	@DeleteMapping("/deleteAliSubject")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteAliSubject(@RequestBody List<AliSubject> aliSubjectList) {
		aliSubjectService.deleteAliSubject(aliSubjectList);
		return Response.ok();
	}
}
