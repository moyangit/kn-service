package com.tsn.serv.mem.service.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.account.MemAccountRecord;
import com.tsn.serv.mem.mapper.account.MemAccountMapper;
import com.tsn.serv.mem.mapper.account.MemAccountRecordMapper;

@Service
public class MemAccountService {
	
	@Autowired
	private MemAccountMapper memAccountMapper;
	
	@Autowired
	private MemAccountRecordMapper memAccountRecordMapper;
	
	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();
	
	public void addMemAccount(MemAccount memAccount) {
		
		if (StringUtils.isEmpty(memAccount.getMemId())) {
			throw new BusinessException(ResultCode.DATABASE_OPERATIOIN_IS_ERROR, "insert memAccount memId is not null");
		}
		
		memAccount.setAccountNo(snowFlakeManager.create(GenIDEnum.ACCOUNT_NO.name()).getIdByPrefix(GenIDEnum.ACCOUNT_NO.getPreFix()));
		memAccount.setAccountMoney(new BigDecimal(0));
		memAccount.setCreateTime(new Date());
		memAccount.setAccountStatus(String.valueOf(EnableStatus.enable.getCode()));
		
		memAccountMapper.insert(memAccount);
		
	}
	
	@Transactional
	public void updateMemAccountByAccount(MemAccount memAccount, MemAccountRecord memAccountRecord) {
		memAccountMapper.updateMoneyByAccountNo(memAccount);
		memAccountRecordMapper.insert(memAccountRecord);
	}
	
	public MemAccount queryMemAccountByMemId(String memId) {
		return memAccountMapper.queryMemAccountByMemId(memId);
	}
	
	public void queryMemAccountRecordPage(Page page) {
		List<MemAccountRecord> accountList = memAccountRecordMapper.selectByPage(page);
		page.setData(accountList);
	}

	public void getPcAccRecordPage(Page page) {
		List<Map<String,Object>> accRecordList = memAccountRecordMapper.getPcAccRecordPage(page);
		page.setData(accRecordList);
	}

	public void getRebateOrder(Page page) {
		List<MemAccountRecord> accountList = memAccountRecordMapper.getRebateOrder(page);
		page.setData(accountList);
	}
	
	public Map<String, Object> getAccRecordSum(String userId) {
		return memAccountRecordMapper.getAccRecordSum(userId);
	}
}
