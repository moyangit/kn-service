package com.tsn.serv.auth.mapper.sys;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysModule;

public interface SysModuleMapper extends IBaseMapper<SysModule> {

    int delete(String moduleId);

    int insert(SysModule sysModule);

    int insertDynamic(SysModule sysModule);

    int updateDynamic(SysModule sysModule);

    int update(SysModule sysModule);

    SysModule selectByModuleId(String moduleId);

    List<SysModule> sysSysModuleList(Page page);

    int deleteSysModuleById(List<Integer> idList);

    List<SysModule> getOneModuleList();

    List<SysModule> getSubordinateModule(List<String> firstIdList);
}