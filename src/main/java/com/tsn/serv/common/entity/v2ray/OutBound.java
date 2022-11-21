package com.tsn.serv.common.entity.v2ray;

import java.util.List;


public class OutBound {
	
	private String tag;
	
	private String protocol;
	
	private Settings settings;
	
	private StreamSettings streamSettings;
	
	private Mux mux;
	
	public OutBound(String tag, String protocol, Settings settings, StreamSettings streamSettings) {
		this.tag = tag;
		this.protocol = protocol;
		this.settings = settings;
		this.streamSettings = streamSettings;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
	
	public StreamSettings getStreamSettings() {
		return streamSettings;
	}

	public void setStreamSettings(StreamSettings streamSettings) {
		this.streamSettings = streamSettings;
	}
	
	public Mux getMux() {
		return mux;
	}

	public void setMux(Mux mux) {
		this.mux = mux;
	}

	public static class Mux {
		private boolean enabled = false;
		private int concurrency = 8;
		
		public Mux(boolean enabled, int concurrency) {
			this.enabled = enabled;
			this.concurrency = concurrency;
		}
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public int getConcurrency() {
			return concurrency;
		}
		public void setConcurrency(int concurrency) {
			this.concurrency = concurrency;
		}
	}

	public static class Settings {
		
		private List<UserAndServ> vnext;
		
		private String domainStrategy;
		
		public Settings(String domainStrategy) {
			this.domainStrategy = domainStrategy;
		}
		
		public Settings(List<UserAndServ> vnext) {
			this.vnext = vnext;
		}
		
		public List<UserAndServ> getVnext() {
			return vnext;
		}

		public void setVnext(List<UserAndServ> vnext) {
			this.vnext = vnext;
		}

		public String getDomainStrategy() {
			return domainStrategy;
		}

		public void setDomainStrategy(String domainStrategy) {
			this.domainStrategy = domainStrategy;
		}

	}
	
	
	public static class UserAndServ {
		
		private List<V2User> users;
		
		private String address;
		
		private int port;
		
		public UserAndServ(List<V2User> users, String address, int port){
			this.users = users;
			this.address = address;
			this.port = port;
		}

		public List<V2User> getUsers() {
			return users;
		}

		public void setUsers(List<V2User> users) {
			this.users = users;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}
		
	}
	
	public static class StreamSettings {
		
		private String network;
		
		private String security;
		
		private WsSettings wsSettings;
		
		public StreamSettings(String network, String security, WsSettings ssSettings) {
			this.network = network;
			this.security = security;
			this.wsSettings = ssSettings;
		}

		public String getNetwork() {
			return network;
		}

		public void setNetwork(String network) {
			this.network = network;
		}

		public String getSecurity() {
			return security;
		}

		public void setSecurity(String security) {
			this.security = security;
		}

		public WsSettings getWsSettings() {
			return wsSettings;
		}

		public void setWsSettings(WsSettings wsSettings) {
			this.wsSettings = wsSettings;
		}
		
	}
	
	public static class WsSettings {
		
		private String path;
		
		public WsSettings(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}
	
	public static class Server {
		
		private String ip;
		
		private int port;
		
		public Server(){
			
		}
		
		public Server(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}
		
		
		
	}

}
