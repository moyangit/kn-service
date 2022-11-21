package com.tsn.serv.pub.service.ad;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.serv.pub.entity.ad.Advert;
import com.tsn.serv.pub.entity.ad.Advertiser;
import com.tsn.serv.pub.mapper.ad.AdvertMapper;

@Service
public class AdService extends ServiceImpl<AdvertMapper, Advert>{
	
	@Autowired
	private AdvertMapper advertMapper;
	
	@Autowired
	private AdvertiserService advertiserService;
	
	@Override
	public <Page extends IPage<Advert>> Page page(Page page, Wrapper<Advert> queryWrapper) {
		// TODO Auto-generated method stub
		super.page(page, queryWrapper);
		List<Advert> adList = page.getRecords();
		
		Set<Long> customerList = adList.stream().map(t ->  t.getAdCustomerId()).collect(Collectors.toSet());
		
		List<Advertiser> advertiserList = advertiserService.listByIds(customerList);
		            
		adList.forEach(module -> {
			advertiserList.forEach(ad -> {
		           if (ad.getId() == module.getAdCustomerId())  {
		               module.setAdCustomerName(ad.getAdvertiserName());
		            }
		      });
		});
		
		return page;
	}

	public void page(Page page) {
		
	}

	public void addAd(Advert advert) {
		advert.setSeq(0);
		advert.setStatus(0);
		super.save(advert);
	}
	
	public void updateAd(Advert advert) {
		advert.setUpdateTime(new Date());
		advertMapper.updateById(advert);
	}
	
	public void deleteAd(long id) {
		advertMapper.deleteById(id);
	}
	
	/**
	 * 随机取一个
	 * @return
	 */
	public Advert getAdvertRandom() {
		
		List<Advert> advertList = advertMapper.selectList(null);
		
		if (advertList == null || advertList.isEmpty()) {
			return null;
		}
		
		return advertList.get(new Random().nextInt(advertList.size()));
		
	}
	
	public List<Advert> queryAdListByCustomerId(String customerId) {
		
		QueryWrapper<Advert> advertWrapper = new QueryWrapper<Advert>();
		advertWrapper.eq("ad_customer_id", customerId);
		
		List<Advert> adList = advertMapper.selectList(advertWrapper);
		return adList;
		
	}
	

}
