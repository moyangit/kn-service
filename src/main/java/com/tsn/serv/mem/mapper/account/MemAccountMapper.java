package com.tsn.serv.mem.mapper.account;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.serv.mem.entity.account.MemAccount;

public interface MemAccountMapper extends IBaseMapper<MemAccount>{
	
	int updateMoneyByAccountNo(MemAccount account);
	
	MemAccount queryMemAccountByMemId(String memId);
}