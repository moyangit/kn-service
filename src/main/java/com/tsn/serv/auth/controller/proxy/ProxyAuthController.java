package com.tsn.serv.auth.controller.proxy;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.auth.entity.User;
import com.tsn.serv.auth.service.proxy.ProxyAuthService;

/**
 * 代理后台管理系统用户授权实现
 * @author admin
 *
 */
@RestController
@RequestMapping("proxy")
public class ProxyAuthController {

	@Autowired
	private ProxyAuthService proxyService;

	/**
	 * 代理后台管理系统用户登录
	 * @param user
	 * @param br
	 * @return
	 */
	@PostMapping("token")
	public Response<?> token(@Valid @RequestBody User user, BindingResult br){
		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
		}
		AccessToken smsToken = proxyService.authToken(user);

		return Response.ok(smsToken);
	}
	
	/**
	 * 代理后台管理系统用户登录
	 * @param user
	 * @param br
	 * @return
	 */
	@GetMapping("token")
	public Response<?> token(String sign){
		if (StringUtils.isEmpty(sign)) {
			throw new RequestParamValidException("sign can not be null");
		}
		
		AccessToken smsToken = proxyService.authToken(sign);

		return Response.ok(smsToken);
	}
	
	@GetMapping("redirect")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> redirect(){
		
		String memId = JwtLocal.getJwt().getSubject();
		
		String websiteUrl = proxyService.redirect2ProxyWebsite(memId);

		return Response.ok(websiteUrl);
	}
}
