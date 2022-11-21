package com.tsn.serv.pub.mapper.access;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tsn.serv.pub.entity.access.AccessHis;

public interface AccessHisMapper {

    int deleteByPrimaryKey(String accessId);

    int insert(AccessHis accessHis);

    int insertSelective(AccessHis accessHis);

    int updateByPrimaryKeySelective(AccessHis accessHis);

    int updateByPrimaryKey(AccessHis accessHis);

    AccessHis selectByAccessId(String accessId);

    List<Map<String,Object>> selectAccessByLastHour();

    List<Map<String,Object>> selectAccessByHour(Date hourDate);
}