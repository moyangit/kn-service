package com.tsn.serv.pub.controller.ad;

import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsn.common.db.mysql.page.PageResult;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.entity.ad.Adsense;
import com.tsn.serv.pub.entity.ad.AdsenseRelaAd;
import com.tsn.serv.pub.entity.ad.Advert;
import com.tsn.serv.pub.entity.ad.Advertiser;
import com.tsn.serv.pub.entity.ad.param.AdParam;
import com.tsn.serv.pub.service.ad.AdService;
import com.tsn.serv.pub.service.ad.AdStatisService;
import com.tsn.serv.pub.service.ad.AdsenseRelaAdService;
import com.tsn.serv.pub.service.ad.AdsenseService;
import com.tsn.serv.pub.service.ad.AdvertiserService;

@RestController
@RequestMapping("advert")
public class AdController {
	
	@Autowired
	private AdService adService;
	
	@Autowired
	private AdvertiserService advertiserService;
	
	@Autowired
	private AdsenseRelaAdService adsenseRelaAdService;
	
	@Autowired
	private AdsenseService adsenseService;
	
	@Autowired
	private AdStatisService adStatisService;
	
	@GetMapping("/pos/{appType}/{posCode}")
	public Response<?> queryAdByPos(@PathVariable String appType, @PathVariable String posCode) {
		Advert advert = adsenseService.queryAdvert(appType, posCode);
		return Response.ok(advert);
	}
	
	@GetMapping("/list/{posId}")
	public Response<?> queryAdListByPosId(@PathVariable String posId) {
		List<Advert> advertList = adsenseService.queryAdvertListByPos(posId);
		return Response.ok(advertList);
	}
	
	@GetMapping("/listbycustomer/{customerId}")
	public Response<?> queryAdListbycustomerByPosId(@PathVariable String customerId) {
		List<Advert> advertList = adService.queryAdListByCustomerId(customerId);
		return Response.ok(advertList);
	}
	
	@DeleteMapping("/adsense/rela/{relaId}")
	public Response<?> deleteAdsenseRelaByRelaId(@PathVariable String relaId) {
		adsenseRelaAdService.removeById(relaId);
		return Response.ok();
	}
	
	@PostMapping("/adsense")
	public Response<?> addAdsenseRelaByRelaId(@RequestBody AdsenseRelaAd adsenseRelaAd) {
		adsenseRelaAdService.save(adsenseRelaAd);
		return Response.ok();
	}
	
	/**
	 * 获取自定义广告
	 * @return
	 */
	@GetMapping()
	public Response<?> getListOne() {
		/*Advert advert = adService.getAdvertRandom();
		return Response.ok(advert);*/
		return Response.ok();
	}
	
	@GetMapping("/customers")
	public Response<?> getCustomers() {
		List<Advertiser> advertiserList = advertiserService.list();
		return Response.ok(advertiserList);
	}
	
	@GetMapping("page/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	public Response<PageResult<Advert>> getTenantorPage(@PathVariable int page, @PathVariable int rows, AdParam param) {
		
		//构建条件
	    QueryWrapper<Advert> wrapper = new QueryWrapper<Advert>();

	    //获取条件是否为空
	    if (!StringUtils.isEmpty(param.getAdCustomerId())){
	        wrapper.like("ad_customer_id", param.getAdCustomerId());
	    }
	    
	    if (!StringUtils.isEmpty(param.getStatus())){
	        wrapper.like("status", param.getStatus());
	    }

	    Page<Advert> pageObj = new Page<Advert>(page, rows);
	    //重写方法
	    adService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}
	

	/**
	 * 查询广告位
	 * @param page
	 * @param rows
	 * @param param
	 * @return
	 */
	@GetMapping("/pos/list")
	@ApiOperation(value = "广告位",notes = "")
	public Response<List<Adsense>> getAdPosList() {
		
	    //重写方法
		List<Adsense> adsenseList = adsenseService.list();
		
		return Response.ok(adsenseList);
	}
	
	/*@GetMapping("list")
	public Response<?> getList() {
		List<Advert> adverts = adService.getAdList();
		return Response.ok(adverts);
	}*/
	
	@PostMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> add(@RequestBody Advert advert) {
		adService.addAd(advert);
		return Response.ok(advert);
	}
	
	@PutMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> update(@RequestBody Advert advert) {
		adService.updateAd(advert);
		return Response.ok(advert);
	}
	
	@DeleteMapping("/{id}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> update(@PathVariable long id) {
		adService.deleteAd(id);
		return Response.ok();
	}
	
	/**
	 * 查询广告位
	 * @param page
	 * @param rows
	 * @param param
	 * @return
	 */
	@GetMapping("/statis/month/{yyyyMM}/{adId}")
	@ApiOperation(value = "广告位",notes = "")
	public Response<List<Map<String, String>>> getAdStatisData(@PathVariable String adId, @PathVariable String yyyyMM) {
		
	    //重写方法
		List<Map<String, String>> adsenseList = adStatisService.getAdStatisListByAd(adId, yyyyMM);
		
		return Response.ok(adsenseList);
	}
	
}
