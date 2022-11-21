package com.tsn.serv.mem.mapper.mem;

import com.tsn.serv.mem.entity.mem.DurationRecord;

public interface DurationRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(DurationRecord record);

    int insertSelective(DurationRecord record);

    DurationRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DurationRecord record);

    int updateByPrimaryKey(DurationRecord record);
}