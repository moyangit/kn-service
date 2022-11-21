package com.tsn.serv.mem.mapper.node;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.node.NodeKey;

public interface NodeKeyMapper extends IBaseMapper<NodeKey>{
	
	int updateNodeKeyAgeIncr();
	
	NodeKey getNodeKeyByAge0();
	
	NodeKey getNodeKeyByAge1();
	
	List<NodeKey> getNodeKeysByMoreAge(int age);
	
	void updateByBatchNo(NodeKey nodeKey);
	
	void updateStatusByBatchNo(NodeKey batchNo);

    List<NodeKey> nodeKeyList(Page page);
}