package com.tsn.serv.auth.entity;

public class ShareUser extends User{
	
	private String shareUrl;
	
	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	@Override
	public String toString() {
		return "ShareUser [shareUrl=" + shareUrl + "]";
	}


}
