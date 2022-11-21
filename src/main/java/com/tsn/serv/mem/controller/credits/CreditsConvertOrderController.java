package com.tsn.serv.mem.controller.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.service.credits.CreditsConvertOrderService;

/**
 * 积分兑换记录
 * @author work
 *
 */
@RestController
@RequestMapping("credits/convert/order")
public class CreditsConvertOrderController {
	
	@Autowired
	private CreditsConvertOrderService creditsConvertOrderService;
	
	/**
	 * 积分兑换记录
	 * @return
	 */
	@GetMapping("page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> selectCreditsConvertOrderByPage(Page page){
		creditsConvertOrderService.selectCreditsConvertOrderByPage(page);
		return Response.ok(page);
	}

}
