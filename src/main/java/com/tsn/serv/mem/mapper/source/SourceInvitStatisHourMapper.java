package com.tsn.serv.mem.mapper.source;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tsn.serv.mem.entity.source.SourceInvitStatisHour;

public interface SourceInvitStatisHourMapper {
    int deleteByPrimaryKey(Integer inviterHourId);

    int insert(SourceInvitStatisHour record);
    
    int insertByIgnore(SourceInvitStatisHour record);

    int insertSelective(SourceInvitStatisHour record);

    SourceInvitStatisHour selectByPrimaryKey(Integer inviterHourId);

    int updateByPrimaryKeySelective(SourceInvitStatisHour record);

    int updateByPrimaryKey(SourceInvitStatisHour record);
    
    List<SourceInvitStatisHour> selectBySourceCodeAndDay(@Param("sourceInvitCode") String sourceCode, @Param("YMDTime") String YMDTime);
    
    List<SourceInvitStatisHour> selectBySourceCodeAndTime(@Param("sourceInvitCode") String sourceCode, @Param("startDayTime") String startDayTime, @Param("endDayTime") String endDayTime);

    List<SourceInvitStatisHour> selectBySourcePathAndTime(@Param("sourcePath") String sourcePath, @Param("startDayTime") String startDayTime, @Param("endDayTime") String endDayTime);
    
    List<String> getInviterChart(@Param("source")String source, @Param("monthDate")String monthDate);

    List<String> getInviterChartTo(@Param("source")String source, @Param("statDate")String statDate, @Param("endDate")String endDate);

    Map<String, Object> getInviterByDayTime(@Param("source")String source, @Param("dayTime")String dayTime);

}