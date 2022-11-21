package com.tsn.serv.mem.service.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.cons.ResultCode;
import com.tsn.serv.mem.entity.node.NodePath;
import com.tsn.serv.mem.entity.node.NodePathAutoGroup;
import com.tsn.serv.mem.mapper.node.NodePathAutoGroupMapper;
import com.tsn.serv.mem.mapper.node.NodePathMapper;

@Service
public class NodePathService {

    @Autowired
    private NodePathMapper nodePathMapper;
    
    @Autowired
    private NodePathAutoGroupMapper nodePathAutoGroupMapper;
    
    public List<NodePathAutoGroup> getNodePathAutoGroupList(String grade) {
    	List<NodePathAutoGroup> autoGroupList = nodePathAutoGroupMapper.selectAllByType(grade);
    	
    	List<NodePath> nodePathList = nodePathMapper.getPathInfoAllByWeight(grade);
    	
    	for (NodePathAutoGroup group : autoGroupList) {
    		List<NodePath> nodePathTempList = new ArrayList<NodePath>();
    		for (NodePath path : nodePathList) {
    			if (StringUtils.isEmpty(path.getAutoGroupId())) {
    				continue;
    			}
    			if (group.getAutoGroupId().equals(path.getAutoGroupId())) {
    				nodePathTempList.add(path);
    			}
    		}
    		group.setNodePathList(nodePathTempList);
    		
    	}
    	
    	return autoGroupList;
    	
    }

    public void nodePathList(Page page) {
        List<NodePath> nodePathList = nodePathMapper.nodePathList(page);
        page.setData(nodePathList);
    }

    public void addNodePath(NodePath nodePath) {
        nodePath.setPathId(CommUtils.getUuid());
        nodePath.setIsUse(0);
        nodePath.setSort(nodePath.getSort() == null ? 0 : nodePath.getSort());
        
        if ("1".equals(nodePath.getPathGrade())) {
        	nodePath.setPathCode("FR_" + nodePath.getPathCode() + Long.toHexString(new Date().getTime()));
        }else {
        	nodePath.setPathCode(nodePath.getPathCode() + Long.toHexString(new Date().getTime()));
        }
        nodePathMapper.insert(nodePath);
    }

    public void updateNodePath(NodePath nodePath) {
        nodePathMapper.updateDynamic(nodePath);
    }

    public void deleteNodePath(NodePath nodePath) {
        nodePathMapper.delete(nodePath.getPathId());
    }
    
    public void addNodeGroup(NodePathAutoGroup nodePathAutoGroup) {
    	nodePathAutoGroup.setAutoGroupId(CommUtils.getUuid());
    	nodePathAutoGroup.setStatus((byte)0);
    	nodePathAutoGroup.setCreateTime(new Date());
    	nodePathAutoGroup.setUpdateTime(nodePathAutoGroup.getCreateTime());
        nodePathAutoGroupMapper.insert(nodePathAutoGroup);
    }
    
    public void updateNodeGroup(NodePathAutoGroup nodePathAutoGroup) {
    	nodePathAutoGroup.setUpdateTime(new Date());
        nodePathAutoGroupMapper.updateByPrimaryKeySelective(nodePathAutoGroup);
    }
    
    @Transactional
    public void batchNodePath(NodePath nodePath, String pathGrade) {
    	
    	List<NodePath> nodePathList = nodePathMapper.getPathInfoAllAndAutoUseByGrade(pathGrade);
    	
    	
    	//这里不支持直连修改，过滤掉直连线路
    	nodePathList = nodePathList.stream().filter(path -> path.getDirect() == 0).collect(Collectors.toList());
    	
    	if (nodePath.getIsAuto() != null) {//过滤掉直连线路， 直连线路不能批量更改中转
    		nodePathList = nodePathList.stream().filter(path -> nodePath.getIsAuto() == path.getIsAuto()).collect(Collectors.toList());
    	}
    	
    	List<String> userIds = nodePathList.stream().map(NodePath::getPathId).collect(Collectors.toList());
    	nodePathMapper.updateBatchDynamic(nodePath, userIds);
    }
    
    public void deleteNodeGroup(String groupId) {
    	
    	List<NodePath> nodePathList = nodePathMapper.getPathInfoByAutoGroupByGroupId(groupId);
    	
    	if (nodePathList != null && !nodePathList.isEmpty())  {
    		throw new BusinessException(ResultCode.NO_DEL, "can not delete");
    	}
        nodePathAutoGroupMapper.deleteByPrimaryKey(groupId);
    }
}
