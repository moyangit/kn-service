package com.tsn.serv.common.entity.v2ray;

import java.io.Serializable;

public class V2User implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
    private String id;
    /**
     * "alterId": 32
     */
    private Integer alterId;
    /**
     * 0
     */
    private Integer level;

    private String email;

	public V2User() {
    	
    }
    
    public V2User(String id, int alterId, int level, String email) {
    	this.id = id;
    	this.alterId = alterId;
    	this.level = level;
    	this.email = email;
    }
    
    public V2User(String id, String email) {
    	this.id = id;
    	this.alterId = 0;
    	this.level = 0;
    	this.email = email;
    }

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getAlterId() {
		return alterId;
	}
	public void setAlterId(Integer alterId) {
		this.alterId = alterId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "V2User [id=" + id + ", alterId=" + alterId + ", level=" + level
				+ ", email=" + email + "]";
	}

	
}
