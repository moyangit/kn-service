package com.tsn.serv.mem.service.geo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.utils.tools.geo.GeoManager;
import com.tsn.common.utils.utils.tools.geo.GeoManager.IPEntity;
import com.tsn.common.utils.utils.tools.ip.IpAddrUtils;
import com.tsn.serv.mem.entity.mem.MemSource;
import com.tsn.serv.mem.service.mem.MemSourceService;

@Service
public class GeoService {
	
	@Autowired
	private MemSourceService memSourceService;
	
/*	public boolean reg2saveMemSource(String memId, String ip) {
		if (StringUtils.isEmpty(ip)) {
			return false;
		}
		
		IPEntity city = GeoManager.create().getCompleteCity(ip);
		
		MemSource memSource = new MemSource();
		memSource.setId(memId);
		memSource.setCip(ip);
		memSource.setCid(city.getProvinceCode());
		memSource.setCname(city.getCountryName() + city.getProvinceName() + city.getCityName());
		
		boolean res = memSourceService.updateById(memSource);
	
		if (!res) {
			return memSourceService.save(memSource);
		}
		
		return res;
	}*/
	
	public boolean use2saveMemSource(String memId, String ip) {
		if (StringUtils.isEmpty(ip)) {
			return false;
		}
		
		if (ip.contains("10.0.0")) {
			return false;
		}
		
		IPEntity city = GeoManager.create().getCompleteCity(ip);
		
		if (city == null) {
			return false;
		}
		
		MemSource memSource = new MemSource();
		memSource.setId(memId);
		memSource.setCip(ip);
		memSource.setCid(city.getProvinceCode());
		memSource.setCname(city.getCountryName() + city.getProvinceName() + city.getCityName());
		
		boolean res = memSourceService.updateById(memSource);
		
		if (!res) {
			return memSourceService.save(memSource);
		}
		
		return res;
	}
	
	
	public static void main(String[] args) {
		boolean innerIp = IpAddrUtils.isInner("10.255.0.2");
		
		System.out.println(innerIp);
	}

}
