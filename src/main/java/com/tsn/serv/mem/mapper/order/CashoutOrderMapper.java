package com.tsn.serv.mem.mapper.order;

import java.util.List;
import java.util.Map;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.order.CashoutOrder;

public interface CashoutOrderMapper extends IBaseMapper<CashoutOrder>{

    List<CashoutOrder> funancialManageList(Page page);

    int deleteFunancialManage(List<String> idList);

    List<CashoutOrder> getCustomerList(Page page);

    Map<String,Object> getOrderStatus();

    CashoutOrder getOneNewestOrder(String memId);
    
    List<Map<String,Object>> getCashoutOrderPage(Page page);
}