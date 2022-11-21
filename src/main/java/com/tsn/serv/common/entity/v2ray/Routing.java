package com.tsn.serv.common.entity.v2ray;

import java.util.Arrays;
import java.util.List;

public class Routing {
	
	private String domainStrategy;
	
	private List<Selector> balancers;
	
	private List<Rule> rules;
	
	public Routing(List<Rule> rules ,String domainStrategy) {
		this.rules = rules;
		this.domainStrategy = domainStrategy;
	}
	
	public Routing(List<Rule> rules ,String domainStrategy, List<Selector> balancers) {
		this.rules = rules;
		this.domainStrategy = domainStrategy;
		this.balancers = balancers;
	}
	
	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public String getDomainStrategy() {
		return domainStrategy;
	}

	public void setDomainStrategy(String domainStrategy) {
		this.domainStrategy = domainStrategy;
	}
	
	public List<Selector> getBalancers() {
		return balancers;
	}

	public void setBalancers(List<Selector> balancers) {
		this.balancers = balancers;
	}

	public static class Selector {
		
		private String tag;
		
		private List<String> selector;
		
		public Selector() {
			
		}
		
		public Selector(String tag, List<String> selector) {
			this.tag = tag;
			this.selector = selector;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public List<String> getSelector() {
			return selector;
		}

		public void setSelector(List<String> selector) {
			this.selector = selector;
		}
		
	}

	public static class Rule {
		
		private String type;
		
		private List<String> ip;
		
		private List<String> domain;
		
		private List<String> inboundTag;
		
		private String outboundTag;
		
		private String network;
		
		private Integer port;
		
		private String balancerTag;
		
		public Rule(String type, String outboundTag) {
			this.type = type;
			this.outboundTag = outboundTag;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<String> getIp() {
			return ip;
		}

		public void setIp(List<String> ip) {
			this.ip = ip;
		}
		
		public void setIp(String ips) {
			this.ip = Arrays.asList(ips.split(","));
		}

		public List<String> getDomain() {
			return domain;
		}

		public void setDomain(List<String> domain) {
			this.domain = domain;
		}
		
		public void setDomain(String domains) {
			this.domain = Arrays.asList(domains.split(","));
		}

		public List<String> getInboundTag() {
			return inboundTag;
		}

		public void setInboundTag(List<String> inboundTag) {
			this.inboundTag = inboundTag;
		}
		
		public void setInboundTag(String inboundTags) {
			this.inboundTag = Arrays.asList(inboundTags.split(","));
		}

		public String getOutboundTag() {
			return outboundTag;
		}

		public void setOutboundTag(String outboundTag) {
			this.outboundTag = outboundTag;
		}

		public String getNetwork() {
			return network;
		}

		public void setNetwork(String network) {
			this.network = network;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getBalancerTag() {
			return balancerTag;
		}

		public void setBalancerTag(String balancerTag) {
			this.balancerTag = balancerTag;
		}
		
	}
}
