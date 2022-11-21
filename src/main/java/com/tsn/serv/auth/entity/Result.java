package com.tsn.serv.auth.entity;

import com.tsn.common.web.entity.AccessToken;

public class Result {
	
	private AccessToken token;
	
	private Object user;
	
	public Result(AccessToken token, Object user){
		this.token = token;
		this.user = user;
	}

	public AccessToken getToken() {
		return token;
	}

	public void setToken(AccessToken token) {
		this.token = token;
	}

	public Object getUser() {
		return user;
	}

	public void setUser(Object user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Result [token=" + token + ", user=" + user + "]";
	}

}
