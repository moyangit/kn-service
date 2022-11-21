package com.tsn.serv.mem.mapper.proxy;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.proxy.ProxyGroup;

public interface ProxyGroupMapper {
    int deleteByPrimaryKey(Integer proxyGroupId);

    int insert(ProxyGroup record);

    int insertSelective(ProxyGroup record);

    ProxyGroup selectByPrimaryKey(Integer proxyGroupId);

    int updateByPrimaryKeySelective(ProxyGroup record);

    int updateByPrimaryKeyWithBLOBs(ProxyGroup record);

    int updateByPrimaryKey(ProxyGroup record);

    List<ProxyGroup> proxyGroupList(Page page);
}