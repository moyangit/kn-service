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
import com.tsn.serv.auth.entity.sys.SysModule;
import com.tsn.serv.auth.service.sys.SysModuleService;

@RestController
@RequestMapping("sysModule")
public class SysModuleController {

	@Autowired
	private SysModuleService sysModuleService;

	// 列表分页展示
	@GetMapping("page")
	public Response<?> sysSysModuleList(Page page){
		sysModuleService.sysSysModuleList(page);
		return Response.ok(page);
	}

	// 新增
	@PostMapping("/addSysModule")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addSysModule(@RequestBody SysModule sysModule) {
		sysModuleService.addSysModule(sysModule);
		return Response.ok();
	}

	// 修改
	@PutMapping("/updateSysModule")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateSysModule(@RequestBody SysModule sysModule) {
		sysModuleService.updateSysModule(sysModule);
		return Response.ok();
	}

	// 删除
	@DeleteMapping("/deleteSysModule")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteSysModule(@RequestBody List<SysModule> sysModuleList) {
		sysModuleService.deleteSysModule(sysModuleList);
		return Response.ok();
	}

	/**
	 * 获取一级代理菜单
	 * @param page
	 * @return
	 */
	@GetMapping("/getOneModuleList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getOneModuleList(Page page) {
		sysModuleService.getOneModuleList(page);
		return Response.ok(page);
	}
}
