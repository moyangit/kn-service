package com.tsn.serv.mem.mapper.source;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.source.MemSourceInviter;

public interface MemSourceInviterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MemSourceInviter record);

    int insertSelective(MemSourceInviter record);

    MemSourceInviter selectByPrimaryKey(Integer id);
    
    List<MemSourceInviter> selectAll();

    int updateByPrimaryKeySelective(MemSourceInviter record);

    int updateByPrimaryKey(MemSourceInviter record);

    MemSourceInviter selectByInviterCode(String inviterCode);

    MemSourceInviter getDetailsByPath(String sourcePath);

    List<MemSourceInviter> getListByPage(Page page);
}