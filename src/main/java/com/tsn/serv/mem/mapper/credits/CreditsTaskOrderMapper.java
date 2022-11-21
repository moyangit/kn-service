package com.tsn.serv.mem.mapper.credits;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;

public interface CreditsTaskOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(CreditsTaskOrder record);

    int insertSelective(CreditsTaskOrder record);

    CreditsTaskOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CreditsTaskOrder record);

    int updateByPrimaryKey(CreditsTaskOrder record);

	List<CreditsTaskOrder> selectByEntity(CreditsTaskOrder creditsTaskOrder);

	List<CreditsTaskOrder> selectTodayTaskOrder(CreditsTaskOrder taskOrder);

	void updateTaskOrderStatusById(CreditsTaskOrder taskOrder);

    List<Map<String,Object>> selectTaskOrderByPage(Page page);
    
    List<Map<String,Object>> selectTaskOrderPageNoTotal(Map<String, Object> paramMap);
    
    int countTaskOrderPageNoTotal(Map<String, Object> paramMap);

	CreditsTaskOrder selectCreditsTaskOrderByOrderNo(String orderNo);

	int updateTaskOrderStatusByIdAndStatus(CreditsTaskOrder creditsTaskOrder);
	
	int createTaskOrderTable(@Param("timeSuffix") String timeSuffix);
}