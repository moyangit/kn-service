package com.tsn.serv.auth.mapper.sys;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysUser;

public interface SysUserMapper extends IBaseMapper<SysUser> {

    SysUser selectSysUserByUserName(@Param("username")String username);

    List<Map> sysUserList(Page page);

    int deleteSysUserById(List<Integer> idList);
}