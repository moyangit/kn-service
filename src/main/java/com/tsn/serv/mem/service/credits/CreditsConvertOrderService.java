package com.tsn.serv.mem.service.credits;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.credits.CreditsConvertOrder;
import com.tsn.serv.mem.mapper.credits.CreditsConvertOrderMapper;

@Service
public class CreditsConvertOrderService {

	@Autowired
	private CreditsConvertOrderMapper creditsConvertOrderMapper;

	
	public void insert(CreditsConvertOrder creditsConvertOrder) {
		// TODO Auto-generated method stub
		creditsConvertOrderMapper.insert(creditsConvertOrder);
	}

	public void selectCreditsConvertOrderByPage(Page page) {
		List<Map<String, Object>> convertOrderList = creditsConvertOrderMapper.selectCreditsConvertOrderByPage(page);
		page.setData(convertOrderList);
	}
}
