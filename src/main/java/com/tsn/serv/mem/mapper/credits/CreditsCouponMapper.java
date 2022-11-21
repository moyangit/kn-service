package com.tsn.serv.mem.mapper.credits;

import java.util.List;

import com.tsn.serv.mem.entity.credits.CreditsCoupon;

public interface CreditsCouponMapper {
    int deleteByPrimaryKey(String id);

    int insert(CreditsCoupon record);

    int insertSelective(CreditsCoupon record);

    CreditsCoupon selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CreditsCoupon record);

    int updateByPrimaryKey(CreditsCoupon record);

    List<CreditsCoupon> getCreditsCouponListByType(String couponType);

	List<CreditsCoupon> selectCreditsCouponListByEntity(CreditsCoupon creditsCoupon);

	void updateByCouponType(String convertType);
}