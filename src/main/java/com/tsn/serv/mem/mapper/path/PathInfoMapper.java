/*package com.tsn.serv.mem.mapper.path;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.serv.mem.entity.path.PathInfo;

public interface PathInfoMapper extends IBaseMapper<PathInfo>{
	
	List<PathInfo> getPathInfoAllUse();
	
	List<PathInfo> getPathInfoAllUseByWeight(String pathGrade);
	
	List<PathInfo> getPathInfoAllUseByGrade(String pathGrade);
	
	PathInfo getPathInfoAllUseByCode(String code);

	PathInfo getPathInfoAllUseByCodeV1(@Param("pathCode") String code, @Param("pathGrade") String pathGrade);

}*/