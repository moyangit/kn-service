package com.tsn.serv.mem.service.notice;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.serv.mem.entity.notice.AppMsgTempl;
import com.tsn.serv.mem.mapper.notice.AppMsgTemplMapper;

@Service
public class AppMsgTemplService extends ServiceImpl<AppMsgTemplMapper, AppMsgTempl>{
	
	public String getCDKMsgTitleTpl(String msgType, int num) {
		
		QueryWrapper<AppMsgTempl> queryWapperTmp = new QueryWrapper<AppMsgTempl>();
		queryWapperTmp.eq("msg_type", msgType);
		AppMsgTempl tpl = super.getOne(queryWapperTmp);
		
		if (tpl == null) {
			return null;
		}
			
		String title = String.format(tpl.getMsgTitleTpl(),String.valueOf(num));

		return	title;
		
	}

}
