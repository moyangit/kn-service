package com.tsn.serv.auth.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.utils.tools.ip.IpUtil;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.web.core.JwtTokenFactory;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.serv.auth.common.enm.GrantTypeEnum;
import com.tsn.serv.auth.entity.User;
import com.tsn.serv.auth.service.UserAuthService;

/**
 * 用户授权实现
 * @author admin
 *
 */
@RestController
@RequestMapping("auth")
public class MemAuthController {

	@Autowired
	private UserAuthService authService;
	
	@Autowired
	private JwtTokenFactory jwtFactory;
	
	/**
	 * 返回授权码
	 * @param clientId 必填
	 * @param reponseType 默认为 code
	 * @param redirectUrl 回调URL
	 */
	@GetMapping("authorize")
	public void authorize(@RequestParam String clientId, @RequestParam String reponseType, @RequestParam String redirectUrl, @RequestParam(required = false) String state){
		//验证clientId 是否存在
		//生成redirectUrl?code=xxxxxx，注意code存放超时时间
		//重定向上面生成的URL
	}

	/**
	 * 返回token
	 * @param userName
	 * @param password
	 * @param grantType
	 */
	@PostMapping("token")
	public Response<?> token(@Valid @RequestBody User cert, BindingResult br, HttpServletRequest request){
		
		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
			
		}
		
		String ip = IpUtil.getIPAddress(request);
		cert.setIp(ip);
		Response<?> tokenJson = authToken(cert);
		return tokenJson;
	}
	
	@PostMapping("v2/token")
	public Response<?> tokenV2(@RequestBody User cert, BindingResult br, HttpServletRequest request){
		String ip = IpUtil.getIPAddress(request);
		cert.setIp(ip);
		Response<?> tokenJson = authTokenV2(cert);
		return tokenJson;
	}
	
	private Response<?> authTokenV2(User cert) {
		
		String grantType = cert.getGrantType();
		
		GrantTypeEnum type = Enum.valueOf(GrantTypeEnum.class, grantType);
		
		switch (type) {
			//密码模式，使用于用户网页登录，app登录，用户将自身的userName和password交由client，client将使用它们来申请access token，整个过程会将用户信息暴露。因此，除非client十分可靠（例如网页登录，APP登录）
			case password:
				
				AccessToken token = authService.valideUserByPswdV2(cert);
				
				return Response.ok(token);
			
			case sms_code:
				
				AccessToken smsToken = authService.validLoginUserBySmsCode(cert.getUserName(), cert.getSmsCode());
				
				return Response.ok(smsToken);
				
			//Client模式，这种模式不常用，该模式下，并不存在对个体用户授权的行为，被授权的主体为client。因此，该模式可用于对某类用户进行集体授权
			case client_credentials:
				
				return null;
			//授权码模式，最常用最严谨的方式，适合第三方应用授权实现access_token调用接口访问	
			case authorization_code:
			
				return null;
		}
		
		return null;
	}
	
	/**
	 * 刷新
	 * @param token
	 * @return
	 */
	@GetMapping("refresh")
	public Response<?> refresh(String reToken){
		Map<String, String> result = authService.refresh(reToken, AuthEnum.bea_us);
		return Response.ok(result);
	}
	
	@RequestMapping("destroy")
	public Response<?> destroy(String token){
		return null;
	}
	
	private Response<?> authToken(User cert) {
		
		String grantType = cert.getGrantType();
		
		GrantTypeEnum type = Enum.valueOf(GrantTypeEnum.class, grantType);
		
		switch (type) {
			//密码模式，使用于用户网页登录，app登录，用户将自身的userName和password交由client，client将使用它们来申请access token，整个过程会将用户信息暴露。因此，除非client十分可靠（例如网页登录，APP登录）
			case password:
				
				AccessToken token = authService.valideUserByPswd(cert);
				
				return Response.ok(token);
			
			case sms_code:
				
				AccessToken smsToken = authService.validLoginUserBySmsCode(cert.getUserPhone(), cert.getSmsCode());
				
				return Response.ok(smsToken);
				
			//Client模式，这种模式不常用，该模式下，并不存在对个体用户授权的行为，被授权的主体为client。因此，该模式可用于对某类用户进行集体授权
			case client_credentials:
				
				return null;
			//授权码模式，最常用最严谨的方式，适合第三方应用授权实现access_token调用接口访问	
			case authorization_code:
			
				return null;
		}
		
		return null;
	}

	/**
	 * 忘记密码(修改密码)
	 * @param cert
	 * @param br
	 * @return
	 */
	@PostMapping("forget")
	public Response<?> forget(@Valid @RequestBody User cert, BindingResult br){
		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());

		}
		authService.forget(cert);
		return Response.ok();
	}
	
	@PostMapping("v2/forget")
	public Response<?> forgetV2(@RequestBody User cert, BindingResult br){
		authService.forgetV2(cert);
		return Response.ok();
	}
}
