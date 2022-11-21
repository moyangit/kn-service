package com.tsn.serv.mem.mapper.charge;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.charge.AliSubject;

public interface AliSubjectMapper{

    int delete(Integer id);

    int insert(AliSubject aliSubject);

    int insertDynamic(AliSubject aliSubject);

    int updateDynamic(AliSubject aliSubject);

    int update(AliSubject aliSubject);

    AliSubject selectById(Integer id);

    AliSubject getAliSubjectOne();

    List<AliSubject> getAliSubjectList(Page page);

    void deleteAliSubjectByIds(List<Integer> idList);
}