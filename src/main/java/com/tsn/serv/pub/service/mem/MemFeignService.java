package com.tsn.serv.pub.service.mem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.mem.entity.mem.MemInfo;

@Service
public class MemFeignService {
	
	@Autowired
	private IMemFeignService memFeignService;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private IAuthFeignService authFeignService;
	
	public MemInfo selectMemById(String memId) {
		Response<MemInfo> memResult = memFeignService.selectMemById(memId);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectMemById fail");
		}
		
		return (MemInfo) memResult.getData();
	}
	
	public MemInfo selectUserByPhone(String phone) {
		
		Response<MemInfo> memResult = memFeignService.selectUserByPhone(phone);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectUserByPhone fail");
		}
		
		return (MemInfo) memResult.getData();
		
	}
	
	public Response<?> queryMemLimit(String memId) {
		
		Response<?> result = (Response<?>) redisHandler.get(RedisKey.MEM_LIMIT_FLOW_ID + memId);
		
		if (result != null) {
			return result;
		}
		
		Response<?> resp = memFeignService.selectFlowByMemId(memId);
		
		redisHandler.set(RedisKey.MEM_LIMIT_FLOW_ID + memId, resp, 60);
		
		return resp;
		
	}

	public Response<?> getSourceInviterByPath(String sourcePath) {
		Response<?> sourceResult = memFeignService.getDetailsByPath(sourcePath);
		return sourceResult;

	}
	
	public String getReToken() {
		Response<?> resp = authFeignService.getReToken();
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign hb-auth service, method getReToken fail, code = " + resp.getCode());
		}
		
		return (String) resp.getData();
	}
	
	public List<?> getChargeList(String mId) {
		Response<?> resp = memFeignService.getChargeList(mId);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-xm service, method getChargeList fail");
		}
		
		return (List<?>) resp.getData();
	}

	@FeignClient(name = "hb-service", url="${feign.client.url.addr}")
	public interface IMemFeignService {
		
		@RequestMapping(value="mem/private/phone/{phone}",method=RequestMethod.GET)
		public Response<MemInfo> selectUserByPhone(@PathVariable("phone") String phone);
		
		@RequestMapping(value="mem/private/flow/{memId}",method=RequestMethod.GET)
		public Response<?> selectFlowByMemId(@PathVariable("memId") String memId);

		@RequestMapping(value="source/private/path/{sourcePath}",method=RequestMethod.GET)
		public Response<?> getDetailsByPath(@PathVariable("sourcePath") String sourcePath);
		
		@RequestMapping(value="charge/private/mId", method=RequestMethod.GET)
		public Response<?> getChargeList(@RequestParam("memId") String memId);
		
		@RequestMapping(value="mem/" + ApiCons.PRIVATE_PATH +"/{memId}",method=RequestMethod.GET)
		public Response<MemInfo> selectMemById(@PathVariable("memId") String memId);
		
	}
	
	@FeignClient(name = "hb-service", url="${feign.client.url.addr}")
	public interface IAuthFeignService {
		
		@RequestMapping(value="reToken", method=RequestMethod.GET)
		public Response<?> getReToken();
		
	}
	

}
