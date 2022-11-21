package com.tsn.serv.auth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.tools.ip.IpUtil;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.core.JwtTokenFactory;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.serv.auth.entity.User;
import com.tsn.serv.auth.service.UserAuthService;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.entity.device.Device;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsgProducter;
import com.tsn.serv.common.utils.DeviceUtils;

/**
 * 游客
 * @author work
 *
 */
@RestController
@RequestMapping("guest")
public class GuestAuthController {

	@Autowired
	private UserAuthService authService;
	
	@Autowired
	private JwtTokenFactory jwtFactory;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@PostMapping("v3/auth/token")
	public Response<?> getTokenV3(@RequestParam("deviceNo") String deviceNo, String channelCode, HttpServletRequest request){
		
		if(StringUtils.isEmpty(deviceNo) || "undefined".equals(deviceNo) || "(null)".equals(deviceNo) || "null".equals(deviceNo)) {
			throw new BusinessException(AuthCode.GUEST_LOGIN_ERROR, "程序异常 ,请联系客服");
		}
		
		User user = new User();
		
		Device device = DeviceUtils.getDeviceInfo(deviceNo);
		
		/*String memName = "游客 "+new SimpleDateFormat("yyyyMMdd").format(new Date());
		user.setUserName(memName);
		user.setNickName(memName);*/
		user.setUserType("00");
		user.setTerminalType(device.getDeviceType());
		user.setChannelCode(channelCode);
		//这里还是完整的设备信息
		user.setDeviceNo(deviceNo);
		
		String ip = IpUtil.getIPAddress(request);
		user.setIp(ip);
		
		AccessToken accessToken = authService.regGuestMem(user);
		
		//添加游客注册事件
		ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createGuestPeopleCount(channelCode, deviceNo));
		
		return Response.ok(accessToken);
	}
	
}
