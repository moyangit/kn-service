package com.tsn.serv.mem.mapper.node;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.serv.mem.entity.node.NodeServer;

public interface NodeServerMapper extends IBaseMapper<NodeServer>{
	
	List<NodeServer> selectUseServer(String type);
	
}