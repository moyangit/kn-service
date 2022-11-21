package com.tsn.serv.mem.service.notice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.notice.MemNotice;
import com.tsn.serv.mem.mapper.notice.MemNoticeMapper;

@Service
public class MemNoticeService {

    @Autowired
    private MemNoticeMapper memNoticeMapper;

    public void memNoticeList(Page page) {
        List<MemNotice> memNoticeList = memNoticeMapper.getMemNoticeListByPage(page);
        page.setData(memNoticeList);
    }

    public void addMemNotice(MemNotice memNotice) {
        memNoticeMapper.insert(memNotice);
    }

    public void updateMemNotice(MemNotice memNotice) {
        memNoticeMapper.updateDynamic(memNotice);
    }

    public void deleteMemNotice(List<MemNotice> memNoticeList) {
        List<Integer> idList = new ArrayList<>();
        memNoticeList.stream()
                .forEach(item -> {
                    idList.add(item.getId());
                });
        int i = memNoticeMapper.deleteMemNoticeById(idList);
    }

    public List<MemNotice> getMemNoticeByType(String type) {
        return memNoticeMapper.getMemNoticeByType(type);
    }
}
