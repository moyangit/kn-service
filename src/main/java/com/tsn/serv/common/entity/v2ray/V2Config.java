package com.tsn.serv.common.entity.v2ray;

import java.util.List;

public class V2Config {
	
	private Routing routing;
	
	private List<OutBound> outBounds;
	
	public V2Config() {
		
	}
	
	public V2Config(Routing routing, List<OutBound> outBounds) {
		this.routing = routing;
		this.outBounds = outBounds;
	}

	public Routing getRouting() {
		return routing;
	}

	public void setRouting(Routing routing) {
		this.routing = routing;
	}

	public List<OutBound> getOutBounds() {
		return outBounds;
	}

	public void setOutBounds(List<OutBound> outBounds) {
		this.outBounds = outBounds;
	}

	@Override
	public String toString() {
		return "V2Config [routing=" + routing + ", outBounds=" + outBounds
				+ "]";
	}

}
