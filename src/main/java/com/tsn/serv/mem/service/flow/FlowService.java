package com.tsn.serv.mem.service.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.serv.mem.entity.flow.FlowDay;
import com.tsn.serv.mem.mapper.flow.FlowDayMapper;


@Service
public class FlowService {
	
	@Autowired
	private FlowDayMapper flowMapper;
	
	public void updateFlowDay(FlowDay flow) {
		int result = flowMapper.updateFlowDay(flow);
		
		if (result == 0) {
			
			List<Map<String, Object>> flowList = new ArrayList<Map<String, Object>>();
			
			Map<String, Object> flowMap = new HashMap<String, Object>();
			flowMap.put("ip", flow.getHost());
			flowMap.put("upFlow", flow.getUpFlow());
			flowMap.put("downFlow", flow.getDownFlow());
			flowList.add(flowMap);
			flow.setHostFlow(JsonUtils.objectToJson(flowList));
			flowMapper.insert(flow);
		}
		
	}
	
}