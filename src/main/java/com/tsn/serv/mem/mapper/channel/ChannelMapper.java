package com.tsn.serv.mem.mapper.channel;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.serv.mem.entity.channel.Channel;


public interface ChannelMapper extends IBaseMapper<Channel> {
    
    int updateForType(@Param("channelCode") String channelCode, @Param("typeName") String typeName);
    
    List<Channel> getChannelAll();
    
}