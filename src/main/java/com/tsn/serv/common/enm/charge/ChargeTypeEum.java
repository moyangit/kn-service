package com.tsn.serv.common.enm.charge;

public enum ChargeTypeEum {
	
	month("10", "月卡"), quarter("11", "季卡"), halfYear("12", "半年卡"), year("13", "年卡"), forever("14", "永久");
	
	String type;
	
	private String detail;
	
	ChargeTypeEum(String type, String detail) {
		this.type = type;
		this.detail = detail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static ChargeTypeEum getChargeTypeEnum(String code) {
        for (ChargeTypeEum orderStatusEnum: ChargeTypeEum.values()) {
            if (orderStatusEnum.type.equals(code)) {
                return orderStatusEnum;
            }
        }
        return null;
    }

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}


}
