package com.tsn.serv.mem.mapper.flow;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.flow.FlowMonthCy;

public interface FlowMonthCyMapper {
    int deleteByPrimaryKey(String uuid);

    int insert(FlowMonthCy record);

    int insertSelective(FlowMonthCy record);

    FlowMonthCy selectByPrimaryKey(String uuid);

    int updateByPrimaryKeySelective(FlowMonthCy record);

    int updateByPrimaryKeyWithBLOBs(FlowMonthCy record);

    int updateByPrimaryKey(FlowMonthCy record);

    List<FlowMonthCy> getMemFlowMonth(Page page);
}