package com.tsn.serv.auth.mapper.sys;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysRole;

public interface SysRoleMapper extends IBaseMapper<SysRole> {

    int delete(String roleId);

    int insert(SysRole sysRole);

    int insertDynamic(SysRole sysRole);

    int updateDynamic(SysRole sysRole);

    int update(SysRole sysRole);

    SysRole selectByRoleId(String roleId);

    List<SysRole> getRoles();

    List<SysRole> getRolesByCodes(List<String> codeList);

    List<SysRole> sysRoleList(Page page);

    int deleteSysRoleById(List<Integer> idList);
}