package com.tsn.serv.auth.service.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.core.JwtTokenFactory;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.serv.auth.entity.sys.SysUser;
import com.tsn.serv.auth.mapper.sys.SysUserMapper;
import com.tsn.serv.common.code.auth.AuthCode;

@Service
public class SysUserService {

	@Autowired
	private SysUserMapper sysUserMapper;

	@Autowired
	private JwtTokenFactory jwtFactory;

	public AccessToken authToken(SysUser sysUser) {

		//根据登录账号查询用户是否存在
		SysUser user = selectSysUserByUserName(sysUser.getUsername());

		if (user == null) {

			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "用户不存在");

		}
		// 密码加密
		String md5Password = DigestUtils.md5Hex(sysUser.getPassword().getBytes());

		if (!md5Password.equals(user.getPassword())){
			throw new AuthException(AuthCode.AUTH_PWD_ERROR, "密码错误");
		}

		JwtInfo info = new JwtInfo();
		info.setSubject(user.getUserId().toString());
		info.setSubName(user.getUsername());
		//写入 权限和角色    暂时为空
		info.setPermiss("all");
		info.setRoles("all");

		JwtLocal.setJwt(info);
		//拼装accessToken
		AccessToken token = jwtFactory.createAccessJwtToken(AuthEnum.bea_mn);

		token.setData(user);

		return token;
	}

	public SysUser selectSysUserByUserName(String username){
		return sysUserMapper.selectSysUserByUserName(username);
	}

	public void sysUserList(Page page) {
		List<Map> userList = sysUserMapper.sysUserList(page);
		page.setData(userList);
	}

	public void addSysUser(SysUser sysUser) {
		SysUser user = selectSysUserByUserName(sysUser.getUsername());
		if (user != null) {
			throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "账号已存在");
		}
		// 密码加密
		String md5Password = DigestUtils.md5Hex(sysUser.getPassword().getBytes());
		sysUser.setPassword(md5Password);
		sysUser.setCreateBy("admin");
		sysUser.setCreateDate(new Date());
		sysUser.setUpdateBy("admin");
		sysUser.setUpdateDate(new Date());
		sysUserMapper.insert(sysUser);
	}

	public void updateSysUser(SysUser sysUser) {
		if (!StringUtils.isEmpty(sysUser.getPassword())){
			// 密码加密
			String md5Password = DigestUtils.md5Hex(sysUser.getPassword().getBytes());
			sysUser.setPassword(md5Password);
		}
		sysUser.setUpdateDate(new Date());
		sysUserMapper.updateByPrimaryKeySelective(sysUser);
	}

	public void deleteSysUser(List<SysUser> sysUserList) {
		List<Integer> idList = new ArrayList<>();
		sysUserList.stream()
				.forEach(item -> {
					idList.add(item.getUserId());
				});
		int i = sysUserMapper.deleteSysUserById(idList);
	}
}
