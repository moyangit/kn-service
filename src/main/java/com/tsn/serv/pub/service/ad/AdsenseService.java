package com.tsn.serv.pub.service.ad;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.algo.WeightAlgo;
import com.tsn.common.utils.utils.tools.algo.WeightAlgo.WeightObj;
import com.tsn.serv.pub.entity.ad.Adsense;
import com.tsn.serv.pub.entity.ad.AdsenseRelaAd;
import com.tsn.serv.pub.entity.ad.Advert;
import com.tsn.serv.pub.entity.ad.Advertiser;
import com.tsn.serv.pub.mapper.ad.AdsenseMapper;

@Service
public class AdsenseService extends ServiceImpl<AdsenseMapper, Adsense>{
	
	@Autowired
	private AdService adService;
	
	@Autowired
	private AdsenseRelaAdService adsenseRelaAdService;
	
	@Autowired
	private AdvertiserService advertiserService;
	
	@Autowired
	private AdStatisService adStatisService;
	
	/**
	 * 通过广告位查询广告
	 * @param appType
	 * @param posCode
	 * @return
	 */
	public Advert queryAdvert(String appType, String posCode) {
		
		QueryWrapper<Adsense> queryWrapper = new QueryWrapper<Adsense>();
		queryWrapper.eq("app_type", appType);
		queryWrapper.eq("adsense_code", posCode);
		
		Adsense adsense = super.getOne(queryWrapper);
		
		if (adsense == null) {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "Adsense is not exsits");
		}
		
		//获取广告位Id
		long adsenseId = adsense.getId();
		
		QueryWrapper<AdsenseRelaAd> advertWrapper = new QueryWrapper<AdsenseRelaAd>();
		advertWrapper.eq("adsense_id", adsenseId);
		List<AdsenseRelaAd> adsenseRelaAdList = adsenseRelaAdService.list(advertWrapper);
		
		//获取一个权重比较高的
		String adId = null;
		if ("random".equals(adsense.getMode())) {
			AdsenseRelaAd adsenseRelaAd =  adsenseRelaAdList.get(new Random().nextInt(adsenseRelaAdList.size()));
			adId = String.valueOf(adsenseRelaAd.getAdId());
		}else if ("weight".equals(adsense.getMode())) {
			List<WeightObj> weightObjList = new ArrayList<WeightObj>(); 
			
			for (AdsenseRelaAd ad : adsenseRelaAdList) {
				weightObjList.add(new WeightObj(String.valueOf(ad.getAdId()), ad.getWeight()));
			}
			
			WeightAlgo weightAlgo = new WeightAlgo(weightObjList);
			
			adId = weightAlgo.getWeight();
		}
		
		if (StringUtils.isEmpty(adId)) {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "adId is null");
		}
		
		Advert advert = adService.getById(adId);
		advert.setIntervalTime(adsense.getIntervalTime());
		
		try {
			adStatisService.statisShow(adId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return advert;
	}
	
	/**
	 * 通过广告位查询广告
	 * @param appType
	 * @param posCode
	 * @return
	 */
	public List<Advert> queryAdvertListByPos(String posId) {
		
		QueryWrapper<AdsenseRelaAd> advertWrapper = new QueryWrapper<AdsenseRelaAd>();
		advertWrapper.eq("adsense_id", posId);
		List<AdsenseRelaAd> adsenseRelaAdList = adsenseRelaAdService.list(advertWrapper);
		
		Set<Long> adIds =  adsenseRelaAdList.stream().map(AdsenseRelaAd::getAdId).collect(Collectors.toSet());
		
		List<Advert> advertList = adService.listByIds(adIds);
		
		for (Advert advert : advertList) {
			
			for (AdsenseRelaAd adsenseRelaAd : adsenseRelaAdList) {
				if (advert.getId().equals(adsenseRelaAd.getAdId())) {
					advert.setWeight(adsenseRelaAd.getWeight());
					advert.setRelaId(adsenseRelaAd.getId());
				}
			}
			
		}
		
		Set<Long> customerList = advertList.stream().map(t ->  t.getAdCustomerId()).collect(Collectors.toSet());
		
		List<Advertiser> advertiserList = advertiserService.listByIds(customerList);
		            
		advertList.forEach(module -> {
			advertiserList.forEach(ad -> {
		           if (ad.getId() == module.getAdCustomerId())  {
		               module.setAdCustomerName(ad.getAdvertiserName());
		            }
		      });
		});
		
		return advertList;
	}
    
}