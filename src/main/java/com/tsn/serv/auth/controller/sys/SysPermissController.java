package com.tsn.serv.auth.controller.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.auth.entity.sys.SysPermiss;
import com.tsn.serv.auth.service.sys.SysPermissService;

@RestController
@RequestMapping("sysPermiss")
public class SysPermissController {

	@Autowired
	private SysPermissService sysPermissService;

	// 列表分页展示
	@GetMapping("page")
	public Response<?> sysPermissList(Page page){
		sysPermissService.sysPermissList(page);
		return Response.ok(page);
	}

	// 新增
	@PostMapping("/addSysPermiss")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addSysPermiss(@RequestBody SysPermiss sysPermiss) {
		sysPermissService.addSysPermiss(sysPermiss);
		return Response.ok();
	}

	// 修改
	@PutMapping("/updateSysPermiss")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateSysPermiss(@RequestBody SysPermiss sysPermiss) {
		sysPermissService.updateSysPermiss(sysPermiss);
		return Response.ok();
	}

	// 删除
	@DeleteMapping("/deleteSysPermiss")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteSysPermiss(@RequestBody List<SysPermiss> sysPermissList) {
		sysPermissService.deleteSysPermiss(sysPermissList);
		return Response.ok();
	}
}
