package com.tsn.serv.mem.entity.mem;

public class MemConfigPathInfo {
	
	private String code;
	
	private String node;
	
	public MemConfigPathInfo() {
		
	}
	
	public MemConfigPathInfo(String code, String node) {
		this.code = code;
		this.node = node;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return "MemConfigPathInfo [code=" + code + ", node=" + node + "]";
	}

}
