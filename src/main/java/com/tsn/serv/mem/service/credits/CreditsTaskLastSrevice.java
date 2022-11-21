package com.tsn.serv.mem.service.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.serv.mem.entity.credits.CreditsTaskLast;
import com.tsn.serv.mem.mapper.credits.CreditsTaskLastMapper;

@Service
public class CreditsTaskLastSrevice {

	@Autowired
	private CreditsTaskLastMapper creditsTaskLastMapper;

	public CreditsTaskLast selectCreditsTaskLast(CreditsTaskLast taskLast) {
		// TODO Auto-generated method stub
		return creditsTaskLastMapper.selectCreditsTaskLast(taskLast);
	}

	public int updateByPrimaryKeyWithBLOBs(CreditsTaskLast taskLast) {
		return creditsTaskLastMapper.updateByPrimaryKeyWithBLOBs(taskLast);
	}

	public int updateByTaskType(CreditsTaskLast taskLast) {
		// TODO Auto-generated method stub
		return creditsTaskLastMapper.updateByTaskType(taskLast);
	}

	public void insertByTaskType(CreditsTaskLast taskLast) {
		creditsTaskLastMapper.insertByTaskType(taskLast);
	}

	public void updateTaskLastTime(CreditsTaskLast creditsTaskLast) {
		creditsTaskLastMapper.updateTaskLastTime(creditsTaskLast);
	}
	
	
	
	
	
}
