package com.tsn.serv.mem.mapper.credits;

import com.tsn.serv.mem.entity.credits.Credits;

public interface CreditsMapper {
    int deleteByPrimaryKey(String id);

    int insert(Credits record);

    int insertSelective(Credits record);

    Credits selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Credits record);

    int updateByPrimaryKey(Credits record);

	int updateCreditsByMemId(Credits credits);

	Credits selectCreditsByMemId(String memId);
}