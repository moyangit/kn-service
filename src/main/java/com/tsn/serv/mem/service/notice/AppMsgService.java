package com.tsn.serv.mem.service.notice;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.serv.mem.entity.notice.AppMsg;
import com.tsn.serv.mem.mapper.notice.AppMsgMapper;

@Service
public class AppMsgService extends ServiceImpl<AppMsgMapper, AppMsg>{
	
	public AppMsg getMsgUrgent() {
		
		QueryWrapper<AppMsg> queryWrapper = new QueryWrapper<AppMsg>();
		queryWrapper.eq("urgent", 1);
		AppMsg appMsg = super.getOne(queryWrapper);
		
		return appMsg;
		
	}

}
