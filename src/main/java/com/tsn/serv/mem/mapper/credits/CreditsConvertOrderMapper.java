package com.tsn.serv.mem.mapper.credits;

import java.util.List;
import java.util.Map;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.credits.CreditsConvertOrder;

public interface CreditsConvertOrderMapper {
    int deleteByPrimaryKey(String id);

    int insert(CreditsConvertOrder record);

    int insertSelective(CreditsConvertOrder record);

    CreditsConvertOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CreditsConvertOrder record);

    int updateByPrimaryKey(CreditsConvertOrder record);

    List<Map<String,Object>> selectCreditsConvertOrderByPage(Page page);
}