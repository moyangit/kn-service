package com.tsn.serv.mem.service.charge;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
		//memChargeMapper.updateByPrimaryKeySelective(memCharge);
		memChargeMapper.updateById(memCharge);
	}
	
	public List<MemCharge> selectChargeByMemType(String memType){
		
		if ("00".equals(memType)) {
			memType = "01";
		}
		
		QueryWrapper<MemCharge> queryWrapper = new QueryWrapper<MemCharge>();
		queryWrapper.eq("mem_type", memType);
		queryWrapper.eq("status", 0);
		queryWrapper.orderByAsc("charge_id");
		
		List<MemCharge> memCharges =  memChargeMapper.selectList(queryWrapper);
		
		for (MemCharge charge : memCharges) {
			charge.setFinalMoney(charge.getChargeMoney().multiply(new BigDecimal(0.01)).multiply(new BigDecimal(charge.getDiscount())).setScale(2,BigDecimal.ROUND_HALF_UP) );
		}
		
		BigDecimal rate = sysConfigService.getUsdRate();
		
		for (MemCharge memCharge : memCharges) {
			memCharge.setUsdRateVal(rate);
		}
		
		return memCharges;
		
	}
}
