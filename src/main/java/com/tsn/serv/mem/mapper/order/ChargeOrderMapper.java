package com.tsn.serv.mem.mapper.order;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.entity.reqData.HomePageDto;

public interface ChargeOrderMapper extends IBaseMapper<ChargeOrder>{
	
	Integer updateOrderGuest2Mem(@Param("guestMemId") String guestMemId, @Param("memId") String memId, @Param("memPhone") String memPhone, @Param("memName") String memName, @Param("memType") String memType);
	
	int updateOrderByOrderNo(ChargeOrder chargeOrder); 
	
	int updateOrderByOrderNoSuccess(ChargeOrder chargeOrder); 
	
	int updateChargeOrderForClose(ChargeOrder chargeOrder); 
	
	ChargeOrder queryOrderByOrderNo(String orderNo);
	
	int updateOrderMemRebateStatusByOrderNo(ChargeOrder chargeOrder);
	
	List<ChargeOrder> queryMyOrderByPage(Page page);

    List<ChargeOrder> queryChargeOrderListPage(Page page);
    
    Map<String,Object> getStatisDataTodayBefore(@Param("todayTime") String todayTime);
    
    Map<String,Object> getStatisDataToday(@Param("todayTime") String todayTime);
    
    Map<String,Object> getOrderStatisNumDataTodayBefore(@Param("todayTime") String todayTime);
    
    Map<String,Object> getOrderStatisNumDataToday(@Param("todayTime") String todayTime);

    Map<String,Object> getChartData();

	List<Map<String,Object>> getChargeData(HomePageDto homePageDto);

	Map<String,Object> getCustomerData();

	List<Map<String,Object>> getInvitationData(HomePageDto homePageDto);

	List<Map<String,Object>> getIineChartData(HomePageDto homePageDto);

    List<ChargeOrder> getChargeOrderByCron();

    List<ChargeOrder> getChargeOrder(Page page);
    
    List<ChargeOrder> getChargeOrderByProxy(Page page);

    List<ChargeOrder> todayChargeOrderList(Page page);

	List<Map> getCardList(Map selectDate);

    List<Map<String,Object>> getLineChartData(String monthDate);

	List<Map<String,Object>> getLineChartDataTo(String monthDate);

    List<ChargeOrder> getChargeOrderByPage(Page page);

    List<String> getChargeLineChart(String yearDate);

	ChargeOrder queryMyOrderByMemId(String memId);
	
    /**
     * 根据付费类型和天分组查询
     * monthDate 按月条件
     * @param monthDate
     * @return
     */
    List<Map<String,Object>> selectChargeMoneyGroupbyTypeTime(String monthDate);

    /**
     * 查询每天新用户和老用户人数，充值金额
     * @param monthDate
     * @return
     */
    List<Map<String,Object>> getOrderRechargePeopleGroupByDay(String monthDate);
    
    /**
     * 查询每个用户类型对应的订单金额 按月，查每天
     * @param monthDate
     * @return
     */
    List<Map<String,Object>> selectChargeMemTypeMoneyGroupbyTypeTime(String monthDate);
    
    List<ChargeOrder> queryUnRelateAndSuccessOrderByParentId(String memParentId);
    
    List<ChargeOrder> queryChargeOrderAndMemListPage(Page page);
    
}