package com.tsn.serv.mem.mapper.channel;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.serv.mem.entity.channel.ChannelStatisDay;

public interface ChannelStatisDayMapper {
    int deleteByPrimaryKey(String channelRecordId);

    int insert(ChannelStatisDay record);
    
    int insertIgnore(ChannelStatisDay record);

    int insertSelective(ChannelStatisDay record);

    ChannelStatisDay selectByPrimaryKey(String channelRecordId);

    int updateByPrimaryKeySelective(ChannelStatisDay record);

    int updateByPrimaryKey(ChannelStatisDay record);
    
    int updateStatisForType(@Param("channelCode") String channelCode, @Param("typeName") String typeName, @Param("dayTime") String dayTime);
    
    List<ChannelStatisDay> getChannelStatisListByDay(@Param("channelCode") String channelCode, @Param("startDay") String startDay, @Param("endDay") String endDay);
    
}