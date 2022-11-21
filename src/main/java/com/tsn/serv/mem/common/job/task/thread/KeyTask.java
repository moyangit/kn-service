package com.tsn.serv.mem.common.job.task.thread;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.common.entity.v2ray.V2User;
import com.tsn.serv.mem.common.job.task.KeyTaskManager;
import com.tsn.serv.mem.entity.node.NodeServer;
import com.tsn.serv.mem.service.node.NodeKeyService;
import com.tsn.serv.mem.service.v2ray.V2rayService;

public class KeyTask implements Runnable{
	
	private V2User v2User;
	
	private String type;
	
	public static final Logger log = LoggerFactory.getLogger(KeyTask.class);
	
	private KeyTaskManager keyTaskManager;
	
	private V2rayService v2rayService;
	
	private NodeKeyService nodeKeyService;
	
	public KeyTask(V2User v2User, String type, KeyTaskManager keyTaskManager) {
		this.v2User = v2User;
		this.type = type;
		this.keyTaskManager = keyTaskManager;
		this.v2rayService = (V2rayService) SpringContext.getBean("v2rayService");
		this.nodeKeyService = (NodeKeyService) SpringContext.getBean("nodeKeyService");
	}

	public V2User getV2User() {
		return v2User;
	}

	public void setV2User(V2User v2User) {
		this.v2User = v2User;
	}

	@Override
	public void run() {
		
		if ("delete".equals(type)) {
			delKeyTask();
		}else if ("add".equals(type)) {
			addKeyTask();
		}	
		
	}
	
	private void delKeyTask() {
		
		List<NodeServer> nodeServerList = v2rayService.selectUseServer();
		
		for (NodeServer nodeServer : nodeServerList) {
			
			try {
				
				String portArr = nodeServer.getPortArr();
				
				@SuppressWarnings("unchecked")
				Map<String, Integer> portMap = JsonUtils.jsonToPojo(portArr, Map.class);
			
				//获取每个服务器的信息，循环设置
				v2rayService.rmProxyAccount(nodeServer.getServerIp(), portMap.get("v2Port"), v2User, this.keyTaskManager.getNodeKey().getInboundTag());
				log.debug("delete success...............");
			} catch (Exception e) {
				log.error("Exception.........e={}", e);
			}
			
		}
		
			//设置成功后，表示任务执行成功
		try {
			boolean isFull = keyTaskManager.putCompleteIsFull(v2User);
			
			//如果满了
			if (isFull) {
				
				nodeKeyService.updateNodeKeysByBatchNo(this.keyTaskManager.getNodeKey().getKeyBatchNo());
				
			}
		} catch (Exception e) {
			log.error("Exception， e = {}", e);
		}
		
	}
	
	private void addKeyTask() {
		
		List<NodeServer> nodeServerList = v2rayService.selectUseServer();
		
		for (NodeServer nodeServer : nodeServerList) {
			
			try {
				String portArr = nodeServer.getPortArr();
				
				@SuppressWarnings("unchecked")
				Map<String, Integer> portMap = JsonUtils.jsonToPojo(portArr, Map.class);
				log.info("................start add v2ray..............., data = {}", nodeServer.toString());
				//获取每个服务器的信息，循环设置
				v2rayService.addProxyAccount(nodeServer.getServerIp(), portMap.get("v2Port"), v2User, this.keyTaskManager.getNodeKey().getInboundTag());
				log.info("................result success...............");
			} catch (Exception e) {
				log.error("Exception.........e={}", e);
			}
			
		}
		
		//设置成功后，表示任务执行成功
		try {
			boolean isFull = keyTaskManager.putCompleteIsFull(v2User);
			
			//如果满了
			if (isFull) {
				String keyBatchNo = this.keyTaskManager.getNodeKey().getKeyBatchNo();
				log.info("isfull is true, then update nodekeys status。。keyBatchNo = {} ", keyBatchNo);
				nodeKeyService.updateNodeKeysStatusByBatchNo(keyBatchNo);
				
			}
		} catch (Exception e) {
			log.error("Exception， e = {}", e);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

	
}
