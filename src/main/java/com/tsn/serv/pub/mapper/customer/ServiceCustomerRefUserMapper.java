package com.tsn.serv.pub.mapper.customer;

import com.tsn.serv.pub.entity.customer.ServiceCustomerRefUser;

public interface ServiceCustomerRefUserMapper {
    int deleteByPrimaryKey(String userId);

    int insert(ServiceCustomerRefUser record);

    int insertSelective(ServiceCustomerRefUser record);

    ServiceCustomerRefUser selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(ServiceCustomerRefUser record);

    int updateByPrimaryKeyWithBLOBs(ServiceCustomerRefUser record);

    int updateByPrimaryKey(ServiceCustomerRefUser record);
}