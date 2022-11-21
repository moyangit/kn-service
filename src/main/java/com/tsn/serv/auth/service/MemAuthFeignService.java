package com.tsn.serv.auth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;

@Service
public class MemAuthFeignService {
	
	@Autowired
	private IMemFeignService memFeignService;
	
	@Autowired
	private RedisHandler redisHandler;
	
	public MemInfo selectUserByPhone(String phone) {
		
		Response<MemInfo> memResult = memFeignService.selectUserByPhone(phone);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectUserByPhone fail");
		}
		
		return (MemInfo) memResult.getData();
		
	}
	
	public MemInfo getMemById(String memId) {
		
		Response<MemInfo> memResult = memFeignService.getMemById(memId);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectUserByPhone fail");
		}
		
		return (MemInfo) memResult.getData();
		
	}
	
	public Map<String, Object> selectUserByUserName(String name) {
		
		Response<?> memResult = memFeignService.selectUserByUserName(name);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectUserByUserName fail");
		}
		
		return (Map<String, Object>) memResult.getData();
	}
	
	public void updateLogintime(String memId) {
		
		Response<?> resp = memFeignService.updateLogintime(memId);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method updateLogintime fail");
		}
		
	}
	
	public void addMem(Map<String, String> mem) {
		
		Response<?> resp = memFeignService.addMem(mem);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method addMem fail");
		}
		
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

	public void updateMem(Map<String, String> mem) {

		Response<?> resp = memFeignService.updateMem(mem);

		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method updateMem fail");
		}

	}

	public Response<?> bindingAccount(Map<String, String> newUserMap) {
		return memFeignService.bindingAccount(newUserMap);

	}

	/**
	 * 根据账号(手机号)查询代理
	 * @param phone
	 * @return
	 */
	public MemInfo selectProxyByPhone(String phone) {

		Response<MemInfo> memResult = memFeignService.selectProxyByPhone(phone);

		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectUserByPhone fail");
		}

		return (MemInfo) memResult.getData();

	}

    /**
     * 根据类型获取轮播公告
     * @param type
     * @return
     */
	public Response<?> getMemNoticeByType(String type) {
		return memFeignService.getMemNoticeByType(type);
	}

	public void addGuest(Map<String, Object> guestInfo) {
		
		Response<?> resp = memFeignService.addGuest(guestInfo);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(resp.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method addGuest fail");
		}
		
	}
	
	public GuestInfo getGuestByDeviceId(String deviceNo) {
		Response<GuestInfo> memResult = memFeignService.getGuestByDeviceId(deviceNo);

		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method getGuestByDeviceId fail");
		}

		return (GuestInfo) memResult.getData();
	}
	
	public MemInfo getMemByDeviceId(String deviceNo) {
		Response<MemInfo> memResult = memFeignService.getMemByDeviceId(deviceNo);

		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method getMemByDeviceId fail");
		}

		return (MemInfo) memResult.getData();
	}
	
	public MemInfo selectUserByEmail(String email) {
		
		Response<MemInfo> memResult = memFeignService.selectUserByEmail(email);
		
		if (!ResultCode.OPERATION_IS_SUCCESS.equals(memResult.getCode())) {
			throw new BusinessException(ResultCode.REMOTE_CALL_ERROR, "remote feign v-mem service, method selectUserByPhone fail");
		}
		
		return (MemInfo) memResult.getData();
		
	}
	

	@FeignClient(name = "hb-service", url="${feign.client.url.addr}")  
	public interface IMemFeignService {
		
		@RequestMapping(value="mem/private/phone/{phone}",method=RequestMethod.GET)
		public Response<MemInfo> selectUserByPhone(@PathVariable("phone") String phone);

		@RequestMapping(value="mem/name/{name}",method=RequestMethod.GET)
		public Response<?> selectUserByUserName(@PathVariable("name") String name);
		
		@RequestMapping(value="mem",method=RequestMethod.POST)
		public Response<?> addMem(@RequestBody Map<String, String> mem);
		
		@RequestMapping(value="mem/private/email/{email}",method=RequestMethod.GET)
		public Response<MemInfo> selectUserByEmail(@PathVariable("email") String email);
		
		@RequestMapping(value="mem/private/logintime/{memId}",method=RequestMethod.PUT)
		public Response<?> updateLogintime(@PathVariable("memId") String memId);
		
		@RequestMapping(value="mem/private/flow/{memId}",method=RequestMethod.GET)
		public Response<?> selectFlowByMemId(@PathVariable("memId") String memId);

		@RequestMapping(value="mem",method=RequestMethod.PUT)
		public Response<?> updateMem(@RequestBody Map<String, String> mem);

		@RequestMapping(value="mem/bindingAccount",method=RequestMethod.POST)
		public Response<?> bindingAccount(@RequestBody Map<String, String> newUserMap);

		@RequestMapping(value="mem/proxy/phone/{phone}",method=RequestMethod.GET)
		public Response<MemInfo> selectProxyByPhone(@PathVariable("phone") String phone);

		@RequestMapping(value="memNotice/type/{type}",method=RequestMethod.GET)
		Response<?> getMemNoticeByType(@PathVariable("type")String type);
		
		@RequestMapping(value="guest/private",method=RequestMethod.POST)
		public Response<?> addGuest(@RequestBody Map<String, Object> guestMap);
		
		@RequestMapping(value="guest/private/{deviceNo}",method=RequestMethod.GET)
		public Response<GuestInfo> getGuestByDeviceId(@PathVariable("deviceNo") String deviceNo);
		
		@RequestMapping(value="mem/private/device/{deviceNo}",method=RequestMethod.GET)
		public Response<MemInfo> getMemByDeviceId(@PathVariable("deviceNo") String deviceNo);
		
		@RequestMapping(value="mem/private/{memId}",method=RequestMethod.GET)
		public Response<MemInfo> getMemById(@PathVariable("memId") String memId);
		
	}

}
