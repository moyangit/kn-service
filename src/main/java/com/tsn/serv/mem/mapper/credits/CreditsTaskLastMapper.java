package com.tsn.serv.mem.mapper.credits;

import com.tsn.serv.mem.entity.credits.CreditsTaskLast;

public interface CreditsTaskLastMapper {
    int deleteByPrimaryKey(String id);

    int insert(CreditsTaskLast record);

    int insertSelective(CreditsTaskLast record);

    CreditsTaskLast selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CreditsTaskLast record);

    int updateByPrimaryKeyWithBLOBs(CreditsTaskLast record);

    int updateByPrimaryKey(CreditsTaskLast record);

	CreditsTaskLast selectCreditsTaskLast(CreditsTaskLast taskLast);

	int updateByTaskType(CreditsTaskLast taskLast);

	void insertByTaskType(CreditsTaskLast taskLast);

	void updateTaskLastTime(CreditsTaskLast creditsTaskLast);
}