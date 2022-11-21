package com.tsn.serv.common.enm.comm;

import java.util.ArrayList;
import java.util.List;

public enum PlatTypeEnum {
	
	K("快加速"),HB("黑豹"),TH("第三方"),K_HG("快加速回国版"),XM("熊猫"), KN("快鸟");
	
	String desc;
	
	PlatTypeEnum(String desc) {
		this.desc = desc;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static PlatTypeEnum getEnum(String name) {
		try {
			PlatTypeEnum platTypeEnum = PlatTypeEnum.valueOf(name);
			return platTypeEnum;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static List<String> getPlatList() {
		
		PlatTypeEnum[] enums = PlatTypeEnum.values();
		
		List<String> platEnumsList = new ArrayList<String>();
		
		for (PlatTypeEnum pEnum : enums) {
			platEnumsList.add(pEnum.name());
		}
		
		return platEnumsList;
		
	}
	
	public static String getDescByName(String name) {
		PlatTypeEnum platType = getEnum(name);
		
		if (platType == null) {
			return null;
		}
		
		return platType.getDesc();
	}

}
