package com.tsn.serv.common.enm.credits.task;

/**
 * 任务类型
 * @author work
 *
 */
public enum CreditsTaskEum {
	
	sign("每日签到"), 
	ad_view("视频广告"),
	ad_stimulate("激励广告"),
	url_save("保存网址"), 
	url_fill("填写网址"), 
	app_share("分享到朋友圈"), 
	fq_fill("填写问卷"), 
	friend_invite("邀请好友"), 
	google_evaluate("谷歌商店评价"), 
	ad_down("下载软件广告");

	private String code;
	
	CreditsTaskEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
