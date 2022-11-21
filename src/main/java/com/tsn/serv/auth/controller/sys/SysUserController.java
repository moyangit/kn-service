package com.tsn.serv.auth.controller.sys;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.auth.entity.sys.SysUser;
import com.tsn.serv.auth.service.UserAuthService;
import com.tsn.serv.auth.service.sys.SysUserService;

/**
 * 用户授权实现
 * @author admin
 *
 */
@RestController
@RequestMapping("sysUser")
public class SysUserController {

	@Autowired
	private SysUserService sysUserService;

	@Autowired
	private UserAuthService authService;

	/**
	 * pc登录
	 * @param sysUser
	 * @param br
	 * @return
	 */
	@PostMapping("token")
	public Response<?> token(@Valid @RequestBody SysUser sysUser, BindingResult br){
		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
			
		}
		AccessToken smsToken = sysUserService.authToken(sysUser);

		return Response.ok(smsToken);
	}
	
	/**
	 * 刷新token
	 * @param reToken
	 * @return
	 */
	@GetMapping("refresh")
	public Response<?> refresh(String reToken){
		Map<String, String> result = authService.refresh(reToken, AuthEnum.bea_mn);
		return Response.ok(result);
	}

	/**
	 * 根据用户账号查询用户信息
	 * @param username
	 * @return
	 */
	@GetMapping("selectSysUserByUserName")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> selectSysUserByUserName(String username){

		SysUser sysUser = sysUserService.selectSysUserByUserName(username);

		return Response.ok(sysUser);
	}

	// 用户列表分页展示
	@GetMapping("page")
	public Response<?> sysUserList(Page page){
		sysUserService.sysUserList(page);
		return Response.ok(page);
	}

	//用户新增
	@PostMapping("/addSysUser")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addSysUser(@RequestBody SysUser sysUser) {
		sysUserService.addSysUser(sysUser);
		return Response.ok();
	}

	//用户修改
	@PutMapping("/updateSysUser")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateSysUser(@RequestBody SysUser sysUser) {
		sysUserService.updateSysUser(sysUser);
		return Response.ok();
	}

	//用户删除
	@DeleteMapping("/deleteSysUser")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteSysUser(@RequestBody List<SysUser> sysUserList) {
		sysUserService.deleteSysUser(sysUserList);
		return Response.ok();
	}
}
