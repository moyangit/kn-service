package com.tsn.serv.common.enm.credits.convert;

/**
 * 兑换时长
 * @author work
 *
 */
public enum ConvertDurationEum {
	reg("30"),
	// 渠道邀请码填写
	source(""),
	//邀请获得三天72小时 3 * 24 * 60
	friend_invite("4320"), //分钟
	//谷歌评价获得30天720小时
	google_evaluate("43200"),
	//激励
	ad_stimulate("15"),
	//积分兑换
	credits(""),
	//月卡
	month(""),
	//季卡
	quarter(""),
	//半年卡
	half_year(""),
	
	//年卡
	year(""),
	
	CDK("");
	
	private String code;
	
	ConvertDurationEum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
