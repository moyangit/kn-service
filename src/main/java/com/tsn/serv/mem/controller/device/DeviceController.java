package com.tsn.serv.mem.controller.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.AuthCode;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.entity.device.Device;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.service.device.DeviceService;
import com.tsn.serv.mem.service.mem.MemService;

@RestController
@RequestMapping("device")
public class DeviceController {

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private MemService memService;
	
	@GetMapping("/list")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> getDeviceList(String dId) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		List<Device> deviceList = deviceService.getDeviceListByUserId(userId);
		return Response.ok(deviceList);
	}
	
	@GetMapping("/valid")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> valid(String dId) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		Map<String, Object> res = deviceService.validConnectByDeviceNo(userId, dId);
		
		return Response.ok(res);
	}
	
	/**
	 * 设备轮询
	 * @return	
	 */
	@GetMapping("/getOlineDevice")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> getOlineDevice(String dId) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		List<Map> olineDevice = deviceService.getOlineDevice(userId, dId);

		return Response.ok(olineDevice);
	}

	/**
	 * 查询当前在线设备
	 * @return
	 */
	@GetMapping("/deviceDetails")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deviceDetails() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		Map<String, Object> deviceMap = deviceService.deviceDetails(userId);

		return Response.ok(deviceMap);
	}
}
