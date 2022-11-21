package com.tsn.serv.mem.common.limit;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.tsn.common.utils.utils.tools.http.HttpGetReq;
import com.tsn.common.utils.utils.tools.http.HttpPostReq;
import com.tsn.common.utils.utils.tools.http.entity.HttpCode;
import com.tsn.common.utils.utils.tools.http.entity.HttpRes;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.utils.env.Env;

public class LimitCacheManager {
	
	private Logger log = LoggerFactory.getLogger(LimitCacheManager.class);
	
	private static LimitCacheManager limitCacheManager = new LimitCacheManager();
	
	private final Cache<String, LimitStatus> userLimitCache;
	
	private Set<String> proxyServers;
	
	private int limitExpireMins = 30 * 2;
	
	private String monitUrl = "https://monit.api.kuaijiasuhouduan.com/";
	
	// 获取节点数据
    private ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
    
    // 获取节点数据
    private ScheduledThreadPoolExecutor limitSchedule = new ScheduledThreadPoolExecutor(1);
	
	private LimitCacheManager () {
		
		String monitConfigUrl = Env.getVal("monit.server.url");
		
		if (!StringUtils.isEmpty(monitConfigUrl)) {
			this.monitUrl = monitConfigUrl;
		}
		
		
		this.userLimitCache = CacheBuilder.newBuilder().expireAfterWrite(limitExpireMins, TimeUnit.MINUTES).removalListener((RemovalListener<Object, LimitStatus>) rn -> 
			{
				String userId = (String) rn.getKey();
				//限速解除，这时调用中间件接口， 也可以不用
				
				//这里中间件默认30分钟后会处理，所以在这里不做处理
			}
		).build();
		
		this.initTask();
	}
	
	public void initTask() {
		
		//每隔10秒重新扫描失败列表是否有“活”的，固定时间
		schedule.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					getRemoteProxyServers();
				} catch (Exception e) {
				}
				
			}
		}, 0, 60, TimeUnit.SECONDS);
		
		limitSchedule.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					sendLimitApi();
				} catch (Exception e) {
				}
				
			}
		}, 2 * 60, 2 * 60, TimeUnit.SECONDS);
		
	}
	
	private void sendLimitApi() {
		
		
		ConcurrentMap<String, LimitStatus> limitUserMap = userLimitCache.asMap();
		
		if (limitUserMap.isEmpty()) {
			return;
		}
		
		limitUserMap.forEach((key, Boolean) -> {
			String userId = key;
			
			Set<String> proxyIps = this.proxyServers;
			
			if (proxyIps.isEmpty()) {
				log.warn("limit proxyIps is empty()");
				return;
			}
			
			for (String proxyId : proxyIps) {
				
				String proxyUrl = "http://" + proxyId + "/proxy/flow/" + userId + "/L4";
				
				try {
					
					HttpRes httpResult = new HttpPostReq(proxyUrl).excuteReturnObj();
					if (!HttpCode.RES_SUCCESS.equals(httpResult.getCode())) {
						return;
					}
					
				} catch (Exception e) {
					
				}
				
			}
		});
		
		
		
	}
	
	private void getRemoteProxyServers() {
		
		String url = monitUrl + "/server/app/list/0";
		
		try {
			
			HttpGetReq req = new HttpGetReq(url);
			
			HttpRes httpRes = req.excuteReturnObj();
			
			if (!HttpCode.RES_SUCCESS.equals(httpRes.getCode())) {
				return;
			}
			
			String data = httpRes.getData();
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = JsonUtils.jsonToPojo(data, Map.class);
			List<Map> proxyList = JsonUtils.jsonToList(JsonUtils.objectToJson(dataMap.get("data")), Map.class);
			
			Set<String> proxyIps = new HashSet<String>();
			for (Map map : proxyList) {
				
				Map<String, Object> serverMap = (Map<String, Object>) map.get("server");
				
				String ip = String.valueOf(serverMap.get("serverIp"));
				
				int port = Integer.valueOf(serverMap.get("appPort") == null ? "20208" : String.valueOf(serverMap.get("appPort")));
				
				proxyIps.add(ip + ":" + port);
				
			}
			
			this.setProxyServers(proxyIps);
			
		} catch (Exception e) {
			log.error("limit thread request proxy server error, url = {}, e = {}", new Object[]{url,e});
		}
	}
	
	public static LimitCacheManager build() {
		return limitCacheManager;
	}
	
	public void put(String userId, LimitStatus status) {
		userLimitCache.put(userId, status);
	}
	
	public boolean isLimit(String userId) {
		
		LimitStatus bl = userLimitCache.getIfPresent(userId);
		
		if (bl == null || bl.isLimit() == false) {
			return false;
		}
		
		return true;
		
	}

	public Set<String> getProxyServers() {
		return proxyServers;
	}

	public void setProxyServers(Set<String> proxyServers) {
		this.proxyServers = proxyServers;
	}

	public Cache<String, LimitStatus> getUserLimitCache() {
		return userLimitCache;
	}
	
	public static class LimitStatus {
		
		private boolean limit;
		
		private String date;
		
		private long currVal;
		
		private long limitVal;
		
		public LimitStatus() {
			
		}
		
		public LimitStatus(long currVal, long limitVal) {
			this.setCurrVal(currVal);
			this.limit = true;
			this.date = DateUtils.getCurrYMD("yyyy-MM-dd hh:mm:ss");
			this.limitVal = limitVal;
		}

		public boolean isLimit() {
			return limit;
		}

		public void setLimit(boolean limit) {
			this.limit = limit;
		}
		
		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public long getLimitVal() {
			return limitVal;
		}

		public void setLimitVal(long limitVal) {
			this.limitVal = limitVal;
		}

		public long getCurrVal() {
			return currVal;
		}

		public void setCurrVal(long currVal) {
			this.currVal = currVal;
		}
		
	}
	
	

}
