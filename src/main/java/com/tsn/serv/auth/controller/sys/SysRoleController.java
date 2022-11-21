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
import com.tsn.serv.auth.entity.sys.SysRole;
import com.tsn.serv.auth.service.sys.SysRoleService;

/**
 * 角色信息
 * @author admin
 *
 */
@RestController
@RequestMapping("sysRole")
public class SysRoleController {

	@Autowired
	private SysRoleService sysRoleService;

	// 获取所有角色信息
	@GetMapping("getRoles")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getRoles(){
		List<SysRole> roleList = sysRoleService.getRoles();
		return Response.ok(roleList);
	}

	@GetMapping("getRolesByCodes")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getRolesByCodes(String roles){
		List<SysRole> roleList = sysRoleService.getRolesByCodes(roles);
		return Response.ok(roleList);
	}

	// 角色列表分页展示
	@GetMapping("page")
	public Response<?> sysRoleList(Page page){
		sysRoleService.sysRoleList(page);
		return Response.ok(page);
	}

	// 角色新增
	@PostMapping("/addSysRole")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addSysRole(@RequestBody SysRole sysRole) {
		return sysRoleService.addSysRole(sysRole);
	}

	// 角色修改
	@PutMapping("/updateSysRole")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateSysRole(@RequestBody SysRole sysRole) {
		sysRoleService.updateSysRole(sysRole);
		return Response.ok();
	}

	// 角色删除
	@DeleteMapping("/deleteSysRole")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteSysRole(@RequestBody List<SysRole> sysRoleList) {
		sysRoleService.deleteSysRole(sysRoleList);
		return Response.ok();
	}
}
