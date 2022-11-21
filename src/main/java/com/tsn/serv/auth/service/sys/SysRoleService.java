package com.tsn.serv.auth.service.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysRole;
import com.tsn.serv.auth.mapper.sys.SysRoleMapper;

@Service
public class SysRoleService {

	@Autowired
	private SysRoleMapper sysRoleMapper;

	public List<SysRole> getRoles() {
		List<SysRole> roleList = sysRoleMapper.getRoles();
		return roleList;
	}

	public List<SysRole> getRolesByCodes(String roles) {
		JSONArray jsonArray = JSONObject.parseArray(roles);
		List<String> codeList = new ArrayList<>();
		jsonArray.stream().forEach(
				item -> {
					codeList.add(item.toString());
				}
		);
		List<SysRole> roleList = new ArrayList<>();
		if (codeList.size() > 0){
			roleList = sysRoleMapper.getRolesByCodes(codeList);
		}
		return roleList;
	}

	public void sysRoleList(Page page) {
		List<SysRole> roleList = sysRoleMapper.sysRoleList(page);
		page.setData(roleList);
	}

	public Response<?> addSysRole(SysRole sysRole) {
		// 判断当前编码是否已存在
		List<String> codeList = new ArrayList<>();
		codeList.add(sysRole.getRoleCode());
		List<SysRole> roleList = sysRoleMapper.getRolesByCodes(codeList);
		if (roleList.size() > 0){
			return Response.retn(ResultCode.UNKNOW_ERROR,"当前编码已存在");
		}
		sysRole.setCreateTime(new Date());
		sysRoleMapper.insert(sysRole);

		return Response.ok();
	}

	public void updateSysRole(SysRole sysRole) {
		sysRoleMapper.updateDynamic(sysRole);
	}

	public void deleteSysRole(List<SysRole> sysRoleList) {
		List<Integer> idList = new ArrayList<>();
		sysRoleList.stream()
				.forEach(item -> {
					idList.add(item.getRoleId());
				});
		int i = sysRoleMapper.deleteSysRoleById(idList);
	}
}
