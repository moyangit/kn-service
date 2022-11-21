package com.tsn.serv.mem.mapper.proxy;

import com.tsn.serv.mem.entity.proxy.ProxyInfo;

public interface ProxyInfoMapper {
    int deleteByPrimaryKey(Integer proxyId);

    int insert(ProxyInfo record);

    int insertSelective(ProxyInfo record);

    ProxyInfo selectByPrimaryKey(String proxyId);

    int updateByPrimaryKeySelective(ProxyInfo record);

    int updateByPrimaryKey(ProxyInfo record);
    
    ProxyInfo selectProxyAndGroupByProxyId(String proxyId);
    
}