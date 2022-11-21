package com.tsn.serv.mem.mapper.mem;

import com.tsn.serv.mem.entity.mem.MemInfoConfig;

public interface MemInfoConfigMapper {
    int deleteByPrimaryKey(String memId);

    int insert(MemInfoConfig record);

    int insertSelective(MemInfoConfig record);

    MemInfoConfig selectByPrimaryKey(String memId);
    
    MemInfoConfig selectBySubKey(String subKey);

    int updateByPrimaryKeySelective(MemInfoConfig record);

    int updateByPrimaryKeyWithBLOBs(MemInfoConfig record);
}