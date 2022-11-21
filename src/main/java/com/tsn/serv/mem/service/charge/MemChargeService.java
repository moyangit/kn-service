package com.tsn.serv.mem.service.charge;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.mem.entity.charge.MemCharge;
import com.tsn.serv.mem.mapper.charge.MemChargeMapper;
import com.tsn.serv.pub.service.sys.SysConfigService;

@Service
public class MemChargeService {
	
	@Autowired
	private MemChargeMapper memChargeMapper;
	
	@Autowired
	private SysConfigService sysConfigService;
	
	public void getMenChargeList(Page page) {
		List<MemCharge> memChargeList = memChargeMapper.getMenChargeList(page);
		page.setData(memChargeList);
	}

	public void updateCustomer(MemCharge memCharge) {
		memChargeMapper.updateByPrimaryKeySelective(memCharge);
	}
	
	public List<MemCharge> selectChargeByMemType(String memType){
		
		if ("00".equals(memType)) {
			memType = "01";
		}
		
		List<MemCharge> memCharges =  memChargeMapper.selectMemChargeByMemType(memType);
		
		BigDecimal rate = sysConfigService.getUsdRate();
		
		for (MemCharge memCharge : memCharges) {
			memCharge.setUsdRateVal(rate);
		}
		
		return memCharges;
		
	}
}
