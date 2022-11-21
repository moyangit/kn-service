package com.tsn.serv.auth.service.proxy;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.tsn.common.utils.utils.tools.security.Base64Utils;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.core.JwtTokenFactory;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.serv.auth.entity.User;
import com.tsn.serv.auth.service.MemAuthFeignService;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.cons.key.KeyCons;
import com.tsn.serv.mem.entity.mem.MemInfo;

@Service
public class ProxyAuthService {

	@Autowired
	private JwtTokenFactory jwtFactory;

	@Autowired
	private MemAuthFeignService memService;

	public AccessToken authToken(User user) {

		//根据登录账号查询用户是否存在
		MemInfo memInfo = selectProxyByPhone(user.getUserPhone());

		if (memInfo == null) {

			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "用户不存在或者该账户不是代理");

		}
		// 密码加密
		String md5Password = DigestUtils.md5Hex(user.getPassword().getBytes());

		if (!md5Password.equals(memInfo.getMemPwd())){
			throw new AuthException(AuthCode.AUTH_PWD_ERROR, "密码错误");
		}
		JwtInfo info = new JwtInfo();
		info.setSubject(memInfo.getMemId());
		info.setSubName(memInfo.getMemNickName());
		//写入 权限和角色    暂时为空
		info.setPermiss("all");
		info.setRoles("all");

		JwtLocal.setJwt(info);
		//拼装accessToken
		AccessToken token = jwtFactory.createAccessJwtToken(AuthEnum.pxy_us);

		token.setData(memInfo);

		return token;
	}
	
	public AccessToken authToken(String sign) {
		
		//这里包含域名，用户Id， time, sign 
		String reqContent = Base64Utils.decodeToString(sign);
		String[] contentArr = reqContent.split("&");
		String proxyUrl = contentArr[0];
		String memId = contentArr[1];
		String time = contentArr[2];
		String signStr = contentArr[3];
		
		String newSign = proxyUrl + "&" + memId + "&" + time + "&" + KeyCons.PROXY_SING_KEY;
		
		if (!MD5Utils.digest(newSign).equals(signStr)) {
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "授权失败");
		}
		
		MemInfo memInfo = memService.getMemById(memId);

		JwtInfo info = new JwtInfo();
		info.setSubject(memInfo.getMemId());
		info.setSubName(memInfo.getMemNickName());
		//写入 权限和角色    暂时为空
		info.setPermiss("all");
		info.setRoles("all");

		JwtLocal.setJwt(info);
		//拼装accessToken
		AccessToken token = jwtFactory.createAccessJwtToken(AuthEnum.pxy_us);

		token.setData(memInfo);

		return token;
	}
	
	public String redirect2ProxyWebsite(String memId) {
		String proxyUrl = Env.getVal("proxy.website.url");
		proxyUrl = StringUtils.isEmpty(proxyUrl) ? "https://agent.heibaojiasuqi.com" : proxyUrl;
		String time = String.valueOf(new Date().getTime());
		String newSign = proxyUrl + "&" + memId + "&" + time + "&" + KeyCons.PROXY_SING_KEY;
		newSign = proxyUrl + "&" + memId + "&" + time + "&" + MD5Utils.digest(newSign);
		return proxyUrl + "?sign=" + Base64Utils.encodeToString(newSign);
	}

	public MemInfo selectProxyByPhone(String username){
		return memService.selectProxyByPhone(username);
	}
	
}
