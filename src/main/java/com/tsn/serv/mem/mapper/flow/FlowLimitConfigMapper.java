package com.tsn.serv.mem.mapper.flow;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.flow.FlowLimitConfig;

public interface FlowLimitConfigMapper extends IBaseMapper<FlowLimitConfig>{

    List<FlowLimitConfig> selectFlowLimitAll();

    List<FlowLimitConfig> limitConfigList(Page page);
}