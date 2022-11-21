package com.tsn.serv.mem.controller.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.mem.entity.source.MemSourceInviter;
import com.tsn.serv.mem.service.source.MemSourceInviterService;

@RestController
@RequestMapping("source")
public class MemSourceInviterController {

	@Autowired
	private MemSourceInviterService memSourceInviterService;

	@GetMapping("page")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> getListByPage(Page page){
		memSourceInviterService.getListByPage(page);
		return Response.ok(page);
	}


	@PostMapping("/add")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addSourceInviter(@RequestBody MemSourceInviter sourceInviter) {
		return memSourceInviterService.addSourceInviter(sourceInviter);
	}

	@DeleteMapping("/")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteSourceInviter(@RequestBody MemSourceInviter sourceInviter) {
		return memSourceInviterService.deleteSourceInviter(sourceInviter);
	}

    @GetMapping(ApiCons.PRIVATE_PATH + "/path/{sourcePath}")
	public Response<?> getDetailsByPath(@PathVariable String sourcePath){
		MemSourceInviter memSourceInviter = memSourceInviterService.getDetailsByPath(sourcePath);
		return Response.ok(memSourceInviter);
	}

}
