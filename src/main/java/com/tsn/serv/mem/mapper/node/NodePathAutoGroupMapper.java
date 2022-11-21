package com.tsn.serv.mem.mapper.node;

import java.util.List;

import com.tsn.serv.mem.entity.node.NodePathAutoGroup;

public interface NodePathAutoGroupMapper {
    int deleteByPrimaryKey(String autoGroupId);

    int insert(NodePathAutoGroup record);

    int insertSelective(NodePathAutoGroup record);

    NodePathAutoGroup selectByPrimaryKey(String autoGroupId);

    int updateByPrimaryKeySelective(NodePathAutoGroup record);

    int updateByPrimaryKey(NodePathAutoGroup record);
    
    List<NodePathAutoGroup> selectNodeUserPathGroupList();
    
    List<NodePathAutoGroup> selectAllByType(String grade);
}