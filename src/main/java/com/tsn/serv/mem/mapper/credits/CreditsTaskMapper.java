package com.tsn.serv.mem.mapper.credits;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.credits.CreditsTask;

public interface CreditsTaskMapper {
    int deleteByPrimaryKey(String id);

    int insert(CreditsTask record);

    int insertSelective(CreditsTask record);

    CreditsTask selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CreditsTask record);

    int updateByPrimaryKey(CreditsTask record);

	List<CreditsTask> selectCreditsTaskByEntity(CreditsTask creditsTask);

	CreditsTask selectCreditsTaskByTaskType(String taskType);

	CreditsTask getCreditsTaskByTaskType(@Param("taskType") String taskType, @Param("deviceType") String deviceType);

	List<CreditsTask> selectByPage(Page page);
}