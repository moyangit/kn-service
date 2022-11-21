package com.tsn.serv.mem.entity.mem;

public class MemInfoConfig {
    private String memId;
    
    private String subKey;
    
    private Integer isSubKey;

    private String pathArr;

    public String getSubKey() {
		return subKey;
	}

	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}

	public Integer getIsSubKey() {
		return isSubKey;
	}

	public void setIsSubKey(Integer isSubKey) {
		this.isSubKey = isSubKey;
	}

	public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId == null ? null : memId.trim();
    }

    public String getPathArr() {
        return pathArr;
    }

    public void setPathArr(String pathArr) {
        this.pathArr = pathArr == null ? null : pathArr.trim();
    }
}