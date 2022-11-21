package com.tsn.serv.mem.mapper.account;

import java.util.List;
import java.util.Map;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.account.MemAccountRecord;

public interface MemAccountRecordMapper extends IBaseMapper<MemAccountRecord>{


    List<Map<String,Object>> getPcAccRecordPage(Page page);

    List<MemAccountRecord> getPcAccRecord(Page page);

    List<MemAccountRecord> getRebateOrder(Page page);
    
    List<MemAccountRecord> getAccDetailsPage(Page page);

    Map<String,Object> getAccRecordSum(String userId);
}