package com.tsn.serv.mem.service.v2ray;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.serv.mem.entity.node.NodeServer;
import com.tsn.serv.mem.mapper.node.NodeServerMapper;

@Service
public class NodeServerService {
	
	@Autowired
	private NodeServerMapper nodeServerMapper;
	
	@Autowired
	private RedisHandler redisHandler;
	
	public List<NodeServer> selectUseServer(String type) {
		return nodeServerMapper.selectUseServer(type);
	}
	
}
