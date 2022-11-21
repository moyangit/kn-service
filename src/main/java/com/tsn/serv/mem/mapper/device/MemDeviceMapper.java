package com.tsn.serv.mem.mapper.device;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.device.MemDevice;

public interface MemDeviceMapper {

    int delete(Integer id);

    int insert(MemDevice memDevice);

    int insertDynamic(MemDevice memDevice);

    int updateDynamic(MemDevice memDevice);

    int update(MemDevice memDevice);

    MemDevice selectById(Integer id);

    MemDevice selectByMemIdAndDeviceNo(@Param("memId") String memId, @Param("deviceNo") String deviceNo);

    List<MemDevice> getOnLineDevice(@Param("memId") String memId, @Param("pollingTime") Integer pollingTime);

    List<Map<String,Object>> getDeviceNum(@Param("selectType") String selectType);

    List<MemDevice> getAllUpDevice(@Param("selectType") String selectType);

    int updateByMemIdAndDeviceNo(MemDevice memDevice);

    List<Map<String, Object>> deviceList(Page page);
}