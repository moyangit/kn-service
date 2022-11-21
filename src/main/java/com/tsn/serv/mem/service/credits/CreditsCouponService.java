package com.tsn.serv.mem.service.credits;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.serv.mem.entity.credits.CreditsCoupon;
import com.tsn.serv.mem.mapper.credits.CreditsCouponMapper;

@Service
public class CreditsCouponService {

	@Autowired
	private CreditsCouponMapper creditsCouponMapper;

	public List<CreditsCoupon> getCreditsCouponListByType(String couponType) {
		return creditsCouponMapper.getCreditsCouponListByType(couponType);
	}

	public List<CreditsCoupon> getCreditsCouponListByEntity(CreditsCoupon creditsCoupon) {
		// TODO Auto-generated method stub
		return creditsCouponMapper.selectCreditsCouponListByEntity(creditsCoupon);
	}

	public void updateByCouponType(String convertType) {
		creditsCouponMapper.updateByCouponType(convertType);
	}
}
