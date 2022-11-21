package com.tsn.serv.mem.mapper.flow;

import java.util.List;
import java.util.Map;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.flow.FlowDay;

public interface FlowDayMapper extends IBaseMapper<FlowDay>{
	
	int updateFlowDay(FlowDay flowDay);

    List<FlowDay> getMemFlowDay(Page page);

    List<Map> memFlowDay(Map<String, String> requestMap);
}