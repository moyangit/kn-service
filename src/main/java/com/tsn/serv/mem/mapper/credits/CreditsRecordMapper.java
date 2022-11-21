package com.tsn.serv.mem.mapper.credits;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.credits.CreditsRecord;
import com.tsn.serv.mem.entity.credits.CreditsRecordStatistics;

public interface CreditsRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(CreditsRecord record);

    int insertSelective(CreditsRecord record);

    CreditsRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CreditsRecord record);

    int updateByPrimaryKey(CreditsRecord record);

	List<CreditsRecord> selectCreditsRecordByEntity(CreditsRecord creditsRecord);

    CreditsRecordStatistics selectCreditsStatistics(@Param("createTime") String createTime, @Param("userId") String userId);

	List<CreditsRecord> selectCreditsRecordByPage(Page page);
}