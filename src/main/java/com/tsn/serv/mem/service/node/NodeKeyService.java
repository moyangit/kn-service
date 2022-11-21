package com.tsn.serv.mem.service.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.node.NodeKeyStatus;
import com.tsn.serv.common.entity.v2ray.V2User;
import com.tsn.serv.mem.entity.node.NodeKey;
import com.tsn.serv.mem.mapper.node.NodeKeyMapper;
import com.tsn.serv.mem.mapper.node.NodePathMapper;

@Service
public class NodeKeyService {

	@Autowired
	private NodeKeyMapper nodeKeyMapper;
	
	@Autowired
	private NodePathMapper pathMapper;

	public static String memProxyTag = "mem-proxy";

	@Transactional
	public NodeKey createMemNodekey() {

		nodeKeyMapper.updateNodeKeyAgeIncr();

		NodeKey nodekey = new NodeKey();
		nodekey.setSeriNo(CommUtils.getUuid());
		nodekey.setCreateDate(new Date());
		nodekey.setKeyAge(0);
		nodekey.setInboundTag(memProxyTag);
		nodekey.setStatus((byte)0);
		List<V2User> keys = new ArrayList<V2User>();
		for (int num = 0; num < 10; num ++) {
			V2User v2User  = new V2User(CommUtils.getOrigUuid(), "mem" + num + "-" + DateUtils.getCurrYMD("yyyyMMddHHmmss") + "@sf.com");
			keys.add(v2User);
		}
		nodekey.setKeyArry(JsonUtils.objectToJson(keys));
		
		nodekey.setKeyBatchNo(getPreStr() + DateUtils.getCurrYMD("yyyyMMddHHmmss"));
		
		nodeKeyMapper.insert(nodekey);
		
		return nodekey;
		
	}
	
	public NodeKey getNodekeyByAge0() {
		NodeKey nodeKey = nodeKeyMapper.getNodeKeyByAge0();
		return nodeKey;
	}
	
	public String getPreStr(){
		return "K";
	}
	
	public List<NodeKey> getNodeKeysByMoreAge3() {
		
		List<NodeKey> nodeKeys = nodeKeyMapper.getNodeKeysByMoreAge(3);

		return nodeKeys; 
		
	}
	
	public void updateNodeKeysByBatchNo(String batchNo) {
		NodeKey nodeKey = new NodeKey();
		nodeKey.setKeyBatchNo(batchNo);
		nodeKey.setIsDel((byte)EnableStatus.disable.getCode());
		nodeKey.setUpdateDate(new Date());
		nodeKeyMapper.updateByBatchNo(nodeKey);
	}
	
	public void updateNodeKeysStatusByBatchNo(String batchNo) {
		NodeKey nodeKey = new NodeKey();
		nodeKey.setKeyBatchNo(batchNo);
		nodeKey.setStatus((byte)NodeKeyStatus.write_success.getCode());
		nodeKey.setUpdateDate(new Date());
		nodeKeyMapper.updateStatusByBatchNo(nodeKey);
	}

	public void nodeKeyList(Page page) {
		List<NodeKey> nodeKeyList = nodeKeyMapper.nodeKeyList(page);
	}

}
