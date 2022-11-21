package com.tsn.serv.mem.mapper.node;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.node.NodePath;

public interface NodePathMapper extends IBaseMapper<NodePath> {
    int delete(String pathId);

    int insert(NodePath nodePath);

    int insertDynamic(NodePath nodePath);

    int updateDynamic(NodePath nodePath);
    
    int updateBatchDynamic(@Param("nodePath")NodePath nodePath, @Param("ids")List<String> ids);

    int update(NodePath nodePath);

    NodePath selectByPathId(String pathId);

    List<NodePath> nodePathList(Page page);
    
	List<NodePath> getPathInfoAllUse();
	
	List<NodePath> getPathInfoAllUseByWeight(String pathGrade);
	
	List<NodePath> getPathInfoAllByWeight(String pathGrade);
	
	List<NodePath> getPathInfoAllUseByGrade(String pathGrade);
	
	List<NodePath> getPathInfoAllAndAutoUseByGrade(String pathGrade);
	
	NodePath getPathInfoAllUseByCode(String code);

	NodePath getPathInfoAllUseByCodeV1(@Param("pathCode") String code, @Param("pathGrade") String pathGrade);
	
	List<NodePath> getPathInfoByAutoGroup(@Param("pathGrade") String pathGrade, @Param("autoGroupId") String autoGroupId);
	
	List<NodePath> getPathInfoByAutoGroupByGroupId(String autoGroupId);
	
	List<NodePath> getPathTestInfo(List<String> codes);
}