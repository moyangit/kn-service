package com.tsn.serv.mem.service.pay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tsn.common.pay.alipay.face.model.result.AlipayF2FQueryResult;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.common.entity.pay.AliFacePay;

@Service
public class PayService {
	
	@Autowired
	private IPayFeignService payService;
	
	public String sendQcodeTrade(String prodCode, AliFacePay aliFacePay) {
		
		Response<?> resp = payService.sendQcodeTrade(prodCode, aliFacePay);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(resp.getCode(), "remote feign v-pay service, method sendQcodeTrade fail");
		}
		
		return String.valueOf(resp.getData());
		
	}
	
	public AlipayF2FQueryResult queryAliOrderByNo(String prodCode, String outOrderNo) {
		
		Response<AlipayF2FQueryResult> resp = payService.queryAliOrderByNo(prodCode, outOrderNo);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-pay service, method sendQcodeTrade fail");
		}
		
		return (AlipayF2FQueryResult) resp.getData();
		
	}
	
	@FeignClient(name = "hb-service", url="${feign.client.url.addr}")  
	public interface IPayFeignService {
		
/*		@RequestMapping(value="v1/aliface" + ApiCons.PRIVATE_PATH + "/qcode/hb",method=RequestMethod.POST)
		public Response<String> sendQcodeTrade(@RequestBody AliFacePay aliFacePay);
		
		@RequestMapping(value="v1/aliface" + ApiCons.PRIVATE_PATH + "/{outOrderNo}/hb", method=RequestMethod.GET)
		public Response<AlipayF2FQueryResult> queryAliOrderByNo(@PathVariable("outOrderNo") String outOrderNo);*/
		
		@RequestMapping(value="v1/aliface" + ApiCons.PRIVATE_PATH + "/qcode/{prodCode}",method=RequestMethod.POST)
		public Response<String> sendQcodeTrade(@PathVariable("prodCode") String prodCode, @RequestBody AliFacePay aliFacePay);
		
		@RequestMapping(value="v1/aliface" + ApiCons.PRIVATE_PATH + "/{outOrderNo}/{prodCode}", method=RequestMethod.GET)
		public Response<AlipayF2FQueryResult> queryAliOrderByNo(@PathVariable("prodCode") String prodCode, @PathVariable("outOrderNo") String outOrderNo);

	}

}
