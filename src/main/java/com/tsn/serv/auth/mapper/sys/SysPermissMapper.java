package com.tsn.serv.auth.mapper.sys;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysPermiss;

public interface SysPermissMapper extends IBaseMapper<SysPermiss> {

    int delete(String permissId);

    int insert(SysPermiss sysPermiss);

    int insertDynamic(SysPermiss sysPermiss);

    int updateDynamic(SysPermiss sysPermiss);

    int update(SysPermiss sysPermiss);

    SysPermiss selectByPermissId(String permissId);

    List<SysPermiss> sysPermissList(Page page);

    int deleteSysPermissById(List<Integer> idList);

    List<SysPermiss> getPermissByModuleCode(String moduleCode);
}