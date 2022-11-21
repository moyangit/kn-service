package com.tsn.serv.mem.controller.device.v;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.AuthCode;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.service.device.DeviceService;

@RestController
@RequestMapping("v1/device")
public class DeviceV1Controller {

	@Autowired
	private DeviceService deviceService;
	

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
		
		String tokenType = jwtInfo.getTokenType();
		List<Map> olineDevice = new ArrayList<Map>();
		
		olineDevice = deviceService.getOlineDeviceV1(userId, dId, tokenType);
		
		return Response.ok(olineDevice);
	}

	/**
	 * 查询当前在线设备
	 * @return
	 */
	@GetMapping("/deviceDetails")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> deviceDetails() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		String tokenType = jwtInfo.getTokenType();
		
		Map<String, Object> deviceMap = deviceService.deviceDetails(userId,tokenType);

		return Response.ok(deviceMap);
	}


	/**
	 * 查询会员设备信息
	 * @param page
	 * @return
	 */
	@GetMapping("/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deviceList(Page page) {
		deviceService.deviceList(page);
		return Response.ok(page);
	}

	/**
	 * 重置指定用户在线设备信息
	 * @param memId
	 * @return
	 */
	@PutMapping("/reset")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> resetDeviceOline(String memId) {
		deviceService.resetDeviceOline(memId);
		return Response.ok();
	}
}
