package com.tsn.serv.mem.service.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.account.MemAccountRecord;
import com.tsn.serv.mem.mapper.account.MemAccountRecordMapper;

@Service
public class MemAccountRecordService {
	
	@Autowired
	private MemAccountRecordMapper memAccountRecordMapper;
	
	public void addMemAccountRecord(MemAccountRecord accountRecord){
		memAccountRecordMapper.insert(accountRecord);
	}
	
	public void queryMemAccountRecordPage(Page page) {
		List<MemAccountRecord> accountRecordList = memAccountRecordMapper.selectByPage(page);
		page.setData(accountRecordList);
	}

}
