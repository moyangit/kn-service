package com.tsn.serv.mem.common.job.task;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.queue.AbsTaskManager;
import com.tsn.serv.common.entity.v2ray.V2User;
import com.tsn.serv.mem.common.job.task.thread.KeyTask;
import com.tsn.serv.mem.entity.node.NodeKey;

public class KeyTaskManager extends AbsTaskManager{
	
	private int taskCount;
	
	private ArrayBlockingQueue<V2User> completeQueue;
	
	private List<V2User> v2Users;
	
	private NodeKey nodeKey;
	
	public KeyTaskManager(int taskCount, NodeKey nodeKey) {
		if (this.completeQueue == null) {
			this.taskCount = taskCount;
			this.nodeKey = nodeKey;
			String keys = nodeKey.getKeyArry();
			List<V2User> keyArr = JsonUtils.jsonToList(keys, V2User.class);
			this.v2Users = keyArr;
			this.completeQueue = new ArrayBlockingQueue<V2User>(taskCount);
		}
	}
	
	public synchronized boolean putCompleteIsFull(V2User v2User) throws InterruptedException {
		completeQueue.put(v2User);
		if (completeQueue.size() == taskCount) {
			return true;
		}
		return false;
	}
	
	public void runTaskDelKeys() {
		
		//运行前先清空complete
		completeQueue.clear();
		
		if (getQueueSize() != v2Users.size()) {
			throw new IllegalArgumentException("keys size is not 10");
		}
		
		for (V2User v2User : v2Users) {
			put(new KeyTask(v2User, "delete", this));
		}
	}
	
	public void runTaskAddKeys() {
		
		//运行前先清空complete
		completeQueue.clear();
		
		if (getQueueSize() != v2Users.size()) {
			throw new IllegalArgumentException("keys size is not 10");
		}
		
		for (V2User v2User : v2Users) {
			put(new KeyTask(v2User, "add", this));
		}
	}
	
	@Override
	public int getQueueSize() {
		return this.taskCount;
	}

	@Override
	public int getThreadCoreCount() {
		return 2;
	}

	@Override
	public int getThreadMaxCoreCount() {
		return 2;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public ArrayBlockingQueue<V2User> getCompleteQueue() {
		return completeQueue;
	}

	public void setCompleteQueue(ArrayBlockingQueue<V2User> completeQueue) {
		this.completeQueue = completeQueue;
	}

	public List<V2User> getV2Users() {
		return v2Users;
	}

	public void setV2Users(List<V2User> v2Users) {
		this.v2Users = v2Users;
	}

	public NodeKey getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(NodeKey nodeKey) {
		this.nodeKey = nodeKey;
	}

}
