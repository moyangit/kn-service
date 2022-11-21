package com.tsn.serv.mem.mapper.device;


import com.tsn.serv.mem.entity.device.MemDeviceOline;

public interface MemDeviceOlineMapper {

    int delete(Integer id);

    int insert(MemDeviceOline memDeviceOline);

    int insertDynamic(MemDeviceOline memDeviceOline);

    int updateDynamic(MemDeviceOline memDeviceOline);

    int update(MemDeviceOline memDeviceOline);

    MemDeviceOline selectById(Integer id);

    MemDeviceOline selectByMemId(String memId);
}