package com.tsn.serv.auth.mapper.auth;

import org.apache.ibatis.annotations.Param;

import com.tsn.serv.auth.entity.auth.AuthUser;

public interface AuthUserMapper {
    int deleteByPrimaryKey(String authCode);

    int insert(AuthUser record);

    int insertSelective(AuthUser record);

    AuthUser selectByPrimaryKey(@Param("authCode") String authCode, @Param("authType") String authType);

    int updateByPrimaryKeySelective(AuthUser record);

    int updateByPrimaryKey(AuthUser record);
    
    
}