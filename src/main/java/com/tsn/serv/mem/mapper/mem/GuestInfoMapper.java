package com.tsn.serv.mem.mapper.mem;

import com.tsn.serv.mem.entity.mem.GuestInfo;

public interface GuestInfoMapper {
    int deleteByPrimaryKey(String memId);

    int insert(GuestInfo record);
    
    int insertByIgnore(GuestInfo record);

    int insertSelective(GuestInfo record);

    GuestInfo selectByPrimaryKey(String memId);
    
    GuestInfo getGuestByDeviceNo(String deviceNo);

    int updateByPrimaryKeySelective(GuestInfo record);

    int updateByPrimaryKey(GuestInfo record);
}