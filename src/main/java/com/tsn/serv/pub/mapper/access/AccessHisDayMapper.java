package com.tsn.serv.pub.mapper.access;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.serv.pub.entity.access.AccessHisDay;

public interface AccessHisDayMapper {

    int deleteByPrimaryKey(String accessDayId);

    int insert(AccessHisDay accessHisDay);

    int insertSelective(AccessHisDay accessHisDay);

    int updateByPrimaryKeySelective(AccessHisDay accessHisDay);

    int updateByPrimaryKey(AccessHisDay accessHisDay);

    AccessHisDay selectByAccessDayId(String accessDayId);

    List<Map<Object,Object>> getAccessChart(@Param("monthDate") String monthDate, @Param("source") String source, @Param("selectType") String selectType, @Param("statDate") String statDate, @Param("endDate") String endDate);

    List<Map<Object, Object>> getAccessType(@Param("dayDate") Date dayDate, @Param("selectType") String selectType, @Param("statDate") String statDate, @Param("endDate") String endDate);

    AccessHisDay selectListByDayHourPath(@Param("dayTo") String day, @Param("hourTo") String hour, @Param("path") String path);
}