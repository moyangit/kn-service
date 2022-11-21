package com.tsn.serv.pub.mapper.customer;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.pub.entity.customer.ServiceCustomer;

public interface ServiceCustomerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ServiceCustomer record);

    int insertSelective(ServiceCustomer record);

    ServiceCustomer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ServiceCustomer record);

    int updateByPrimaryKey(ServiceCustomer record);
    
    List<ServiceCustomer> selectList();
    
    List<ServiceCustomer> selectListByType(String cusType);
    
    List<ServiceCustomer> selectListActiveStatus(String cusType);
    
    List<ServiceCustomer> getCusPage(Page page);
}