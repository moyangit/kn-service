package com.tsn.serv.mem.mapper.flow;

import java.util.List;
import java.util.Map;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.flow.FlowLimit;

public interface FlowLimitMapper extends IBaseMapper<FlowLimit>{
	
	int updateMemFlowNoHostByMemId(FlowLimit flowLimit);
	
	int updateMemFlowByMemIdAnd(FlowLimit flowLimit);
	
	int updateAndResetMemFlowByMemId(FlowLimit flowLimit);
	
	int updateAndResetMemFlowNoHostByMemId(FlowLimit flowLimit);
	
	FlowLimit selectByUserId(String userId);

    List<Map<String, Object>> getFolwPage(Page page);
}