package com.tsn.serv.mem.controller.credits;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.credits.CreditsCoupon;
import com.tsn.serv.mem.service.credits.CreditsCouponService;

/**
 * 兑换券
 * @author work
 *
 */
@RestController
@RequestMapping("coupon")
public class CreditsCouponController {


	@Autowired
	private CreditsCouponService creditsCouponService;

	/**
	 * 根据type获取所有可用券
	 * @return
	 */
	@GetMapping("/")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getCreditsCouponListByType(String couponType){
		List<CreditsCoupon> couponList = creditsCouponService.getCreditsCouponListByType(couponType);
		return Response.ok(couponList);
	}
	
	
	@GetMapping("entity")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getCreditsCouponListByEntity(CreditsCoupon couponType){
		List<CreditsCoupon> couponList = creditsCouponService.getCreditsCouponListByEntity(couponType);
		return Response.ok(couponList);
	}
	
}
