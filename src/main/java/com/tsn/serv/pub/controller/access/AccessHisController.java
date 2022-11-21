package com.tsn.serv.pub.controller.access;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.maxmind.geoip2.record.Country;
import com.tsn.common.utils.utils.tools.geo.GeoManager;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.pub.common.utils.IpUtil;
import com.tsn.serv.pub.entity.access.AccessHis;
import com.tsn.serv.pub.service.access.AccessHisService;

@RestController
@RequestMapping("access")
public class AccessHisController {
	
	public static final Logger logger = LoggerFactory.getLogger(AccessHisController.class);
	
	@Autowired
	private AccessHisService accessHisService;
	
	private GeoManager geoManager = GeoManager.create();
	
	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();
	
	@PostMapping("/up")
	public Response<?> access(@RequestBody AccessHis accessHis, HttpServletRequest request) {
		
		logger.debug("post access , accessHis = {}", accessHis.toString());
		
		accessHis.setRemainTime(accessHis.getAccessLastTime().getTime() - accessHis.getAccessTime().getTime());
		
		accessHisService.updateAccessHis(accessHis);
		
		return Response.ok();
	}
	
	/**
	 * 先记录访问记录 ，并返回 ID
	 * @param accessHis
	 * @param request
	 * @return
	 */
	@PostMapping()
	public Response<?> startAccess(@RequestBody AccessHis accessHis, HttpServletRequest request) {
		logger.debug("get access , accessIp ={}, accessHis = {}", accessHis.toString());
		String accessNo = snowFlakeManager.create("ACCESS_NO").getIdByPrefix("99");
		
		if (StringUtils.isEmpty(accessHis.getAccessIp())) {
			String accessIp = IpUtil.getIPAddress(request);
			Country country = geoManager.getCountry(accessIp);
			accessHis.setAccessIp(accessIp);
			if (country != null) {
				accessHis.setSourceArea(country.getIsoCode());
				accessHis.setSourceAreaName(country.getName());
			}
		}
		
		accessHis.setAccessId(accessNo);
		
		accessHis.setCreateTime(new Date());

		accessHis.setIsDown("0");
		
		accessHisService.insertAccessHis(accessHis);
		
		return Response.ok(accessNo);
	}

	@PutMapping("/isDown")
	private Response<?> isDown(@RequestBody AccessHis accessHis, HttpServletRequest request){
		if (StringUtils.isEmpty(accessHis.getAccessId())) {
			String accessNo = snowFlakeManager.create("ACCESS_NO").getIdByPrefix("99");

			if (StringUtils.isEmpty(accessHis.getAccessIp())) {
				String accessIp = IpUtil.getIPAddress(request);
				Country country = geoManager.getCountry(accessIp);
				accessHis.setAccessIp(accessIp);
				if (country != null) {
					accessHis.setSourceArea(country.getIsoCode());
					accessHis.setSourceAreaName(country.getName());
				}
			}
			accessHis.setAccessId(accessNo);
			accessHis.setCreateTime(new Date());
			accessHis.setIsDown("0");

			accessHisService.insertAccessHis(accessHis);
		} else {
			accessHis.setIsDown("1");
			accessHisService.uPAccessHis(accessHis);
		}
		return Response.ok(accessHis.getAccessId());
	}

    /**
     * 自定义开始日期和结束日期统计页面访问量和下载量
     * @param statDate
     * @param endDate
     * @return
     */
	@GetMapping("/statEnd")
    private Response<?> upAccessHisDayByStatEnd(String statDate, String endDate) {
        accessHisService.upAccessHisDayByStatEnd(statDate, endDate);
	    return Response.ok();
    }

    /**
     * 统计指定时间页面访问量和下载量
     * @param date
     * @return
     */
    @GetMapping("/date")
    private Response<?> upAccessHisDayByDate(String date) {
        accessHisService.upAccessHisDayByDate(date);
        return Response.ok();
    }
}
