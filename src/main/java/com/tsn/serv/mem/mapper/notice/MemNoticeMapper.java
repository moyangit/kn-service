package com.tsn.serv.mem.mapper.notice;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.notice.MemNotice;

public interface MemNoticeMapper {
    int delete(Integer id);

    int insert(MemNotice memNotice);

    int insertDynamic(MemNotice memNotice);

    int updateDynamic(MemNotice memNotice);

    int update(MemNotice memNotice);

    MemNotice selectById(Integer id);

    int deleteMemNoticeById(List<Integer> idList);

    List<MemNotice> getMemNoticeListByPage(Page page);

    List<MemNotice> getMemNoticeByType(String type);
}
