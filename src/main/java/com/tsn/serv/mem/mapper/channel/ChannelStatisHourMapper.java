package com.tsn.serv.mem.mapper.channel;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.serv.mem.entity.channel.ChannelStatisHour;

public interface ChannelStatisHourMapper {
    int deleteByPrimaryKey(String channelRecordId);

    int insert(ChannelStatisHour record);
    
    int insertIgnore(ChannelStatisHour record);

    int insertSelective(ChannelStatisHour record);

    ChannelStatisHour selectByPrimaryKey(String channelRecordId);

    int updateByPrimaryKeySelective(ChannelStatisHour record);

    int updateByPrimaryKey(ChannelStatisHour record);
    
    int updateStatisForType(@Param("channelCode") String channelCode, @Param("typeName") String typeName, @Param("dayTime") String dayTime);
    
    List<ChannelStatisHour> getChannelStatisListByDay(@Param("channelCode") String channelCode, @Param("startDay") String startDay, @Param("endDay") String endDay);
    
}