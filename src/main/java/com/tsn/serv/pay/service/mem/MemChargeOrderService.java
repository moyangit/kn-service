package com.tsn.serv.pay.service.mem;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.pay.common.cons.ApiCons;

@Service
public class MemChargeOrderService {
	
	@Autowired
	private IMemFeignService memFeignService;
	
	public void updateChargeOrderClose(String orderNo, String tradeNo, String tradeStatus) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNo", orderNo);
		params.put("tradeNo", tradeNo);
		params.put("tradeStatus", tradeStatus);
		
		Response<?> resp = memFeignService.updateChargeOrderClose(params);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method updateChargeOrderFail fail");
		}
		
	}
	
	public void updateChargeOrderSuccess(String orderNo, String tradeNo, String tradeStatus, String tradePayTime) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNo", orderNo);
		params.put("tradeNo", tradeNo);
		params.put("tradeStatus", tradeStatus);
		params.put("tradePayTime", tradePayTime);
		
		Response<?> resp = memFeignService.updateChargeOrderSuccess(params);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method updateChargeOrderSuccess fail");
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryOrderByNo(String orderNo) {
		
		Response<?> resp = memFeignService.queryOrderByNo(orderNo);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method updateChargeOrderSuccess fail");
		}
		
		return (Map<String, Object>) resp.getData();
	}
	
	
	
	@FeignClient(name = "hb-service")  
	@Deprecated
	public interface IMemFeignService {
		
		@RequestMapping(value="order/" + ApiCons.PRIVATE_PATH + "/charge/close", method=RequestMethod.PUT)
		public Response<?> updateChargeOrderClose(@RequestBody Map<String, Object> params);
		
		@RequestMapping(value="order/" + ApiCons.PRIVATE_PATH + "/charge/success", method=RequestMethod.PUT)
		public Response<?> updateChargeOrderSuccess(@RequestBody Map<String, Object> params);
		
		@RequestMapping(value="order/" + ApiCons.PRIVATE_PATH + "/no/{orderNo}", method=RequestMethod.GET)
		public Response<?> queryOrderByNo(@PathVariable("orderNo") String orderNo);
	}

}