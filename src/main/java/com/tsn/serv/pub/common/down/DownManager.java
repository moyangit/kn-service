package com.tsn.serv.pub.common.down;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.utils.tools.json.JsonUtils;

public class DownManager {
	
	private static Logger log = LoggerFactory.getLogger(DownManager.class);
	
	private Map<String, DownUrl> knMap = new HashMap<String, DownUrl>();
	
	private static DownManager downManager = new DownManager();
	
	private ExecutorService knExecutor = Executors.newSingleThreadExecutor();
	
	private DelayQueue<QueueObj> knQueue = new DelayQueue<>();
	/*
	 * 安卓 https://kjsdd.lanzoui.com/iv8OWr2ekve
windows电脑 https://kjsdd.lanzoui.com/iIkoJr3d54h
苹果电脑 https://kjsdd.lanzoui.com/i8yuVr3d4ob
	 */
	

	private DownManager() {
		
		init();
		
		knExecutor.execute(new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					
					
					try {
						QueueObj queueObj = knQueue.take();
						
						DownUrl downUrl = knMap.get(queueObj.name);
						downUrl.refresh();
						queueObj.setStartTime(downUrl.getNextTime().getTime());
						knQueue.put(queueObj);
						
						log.info("refresh kjs url success, {}, {} ", queueObj.name, downUrl);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
				
			}
		}));
	}
	
	/**
	 * https://s456s456.com/down/hb/sgwjg —-未加固
		https://s456s456.com/down/hb/sgjg —加固
	 */
	private void init() {
		//https://kjsdd.lanzoue.com
		knMap.put("ad",new DownUrl("https://wwyh.lanzoue.com/iRmNN0hbe72h", "", "46m5"));
		knMap.put("pc",new DownUrl("https://wwyh.lanzoue.com/i5BMO0hbct7c","", "3bvb"));
		knMap.put("mac",new DownUrl("https://wwyh.lanzoue.com/ilBqU0hbd82h","", "8pbz"));
	}
	
	public void initData() {
		
		for (Map.Entry<String, DownUrl> entry : knMap.entrySet()) {
			DownUrl downUrl = entry.getValue();
			downUrl.refresh();
			knQueue.add(new QueueObj(entry.getKey(), downUrl.getNextTime().getTime()));
		}
	}
	
	public static DownManager build() {
		return downManager;
	}
	
	public void refreshDownUrl () {
		
		
		
	}
	
	public class QueueObj implements Delayed{
	    /* 触发时间*/
	    private long startTime;
	    private String name;
	 
	    public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public QueueObj(String name, long time) {
	        this.name = name;
	        this.startTime = time;
	    }
	 
	    @Override
	    public long getDelay(TimeUnit unit) {
	        return startTime - System.currentTimeMillis();
	    }
	 
	    @Override
	    public int compareTo(Delayed o) {
	    	QueueObj item = (QueueObj) o;
	        long diff = this.startTime - item.startTime;
	        if (diff <= 0) {// 改成>=会造成问题
	            return -1;
	        }else {
	            return 1;
	        }
	    }
	 
	    @Override
	    public String toString() {
	        return "Item{" +
	                "time=" + startTime +
	                ", name='" + name + '\'' +
	                '}';
	    }
	}
	
	
	public static class DownUrl {
		
		private String sourceUrl;
		
		private String downUrl;
		
		private String pwd;
		
		private Date lastTime;
		
		private Date nextTime;
		
		public DownUrl(String sourceUrl, String downUrl, String pwd) {
			this.setSourceUrl(sourceUrl);
			this.setDownUrl(downUrl);
			this.pwd = pwd;
			this.lastTime = new Date();
			this.nextTime = DateUtils.addMinutes(this.lastTime, new Random().nextInt(3));
			this.nextTime = DateUtils.addSeconds(this.nextTime, new Random().nextInt(60));
		}
		
		public void refresh() {
			String downUrl = getYunPanUrl(sourceUrl, pwd);
			
			if (!StringUtils.isEmpty(downUrl)) {
				this.downUrl = downUrl;
			}
			
			this.lastTime = new Date();
			this.nextTime = DateUtils.addMinutes(this.lastTime, 2 + new Random().nextInt(3));
			this.nextTime = DateUtils.addSeconds(this.nextTime, new Random().nextInt(60));
		}
		
		public String getYunPanUrl(String reqUrl, String passwd) {
			
			try {
				//String reqUrl = reqUrl;
				//String passwd = passwd;
				
				Connection.Response response = Jsoup.connect(reqUrl).execute();
//				System.out.println(response.cookies());
//				System.out.println(response.body());
				
				Document document = Jsoup.parse(response.body());
				
				//System.out.println(document.html());
				
				String content = document.html();
				
				String[] contentArr = content.split("\n");
				
				String postUrl = "https://wwyh.lanzoue.com/ajaxm.php";
				String htmlData = "";
				for (String html : contentArr) {
					
					if (html.contains("//")) {
						continue;
					}
					
					if (html.contains("data : ")) {
						htmlData = html.substring(html.indexOf("'") + 1, html.lastIndexOf("'"));
						//params.put("signs", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}
				}
				
				Map<String, String> params = new HashMap<String, String>();
				if (!StringUtils.isEmpty(htmlData)) {
					htmlData = htmlData + passwd;
					String[] htmlDataArr = htmlData.split("&");
					for (String param : htmlDataArr) {
						String[] paramArr = param.split("=");
						params.put(paramArr[0], paramArr[1]);
					}
				}
				
				Map<String, String> headerMap = new HashMap<String, String>();
				headerMap.put("origin", "https://wwyh.lanzoue.com");
				headerMap.put("Referer", reqUrl);
				
				Connection.Response responseFn = Jsoup.connect(postUrl).method(Method.POST)
						.data(params)
						.headers(headerMap)
						.cookies(response.cookies()).followRedirects(true)
				        .execute();
				
				Map<String, String> resultMap = JsonUtils.jsonToPojo(responseFn.body(), Map.class);
				
				String dom = resultMap.get("dom");
				String domUrl = resultMap.get("url");
				
				String downUrl = dom + "/file/" + domUrl;
				
				//System.out.println(downUrl);
				return downUrl;
			} catch (Exception e) {
				log.error("get yunpan url error, e ={}", e);
			} 
			
			return null;
		}
		
		public Date getLastTime() {
			return lastTime;
		}

		public void setLastTime(Date lastTime) {
			this.lastTime = lastTime;
		}

		public Date getNextTime() {
			return nextTime;
		}

		public void setNextTime(Date nextTime) {
			this.nextTime = nextTime;
		}

		public String getDownUrl() {
			return downUrl;
		}

		public void setDownUrl(String downUrl) {
			this.downUrl = downUrl;
		}

		public String getSourceUrl() {
			return sourceUrl;
		}

		public void setSourceUrl(String sourceUrl) {
			this.sourceUrl = sourceUrl;
		}

		@Override
		public String toString() {
			return "DownUrl [sourceUrl=" + sourceUrl + ", downUrl=" + downUrl
					+ ", lastTime=" + lastTime + ", nextTime=" + nextTime + "]";
		}
		
	}


	public Map<String, DownUrl> getKnMap() {
		return knMap;
	}

	public void setKnMap(Map<String, DownUrl> knMap) {
		this.knMap = knMap;
	}

	public ExecutorService getKnExecutor() {
		return knExecutor;
	}

	public void setKnExecutor(ExecutorService knExecutor) {
		this.knExecutor = knExecutor;
	}

	public DelayQueue<QueueObj> getKnQueue() {
		return knQueue;
	}

	public void setKnQueue(DelayQueue<QueueObj> knQueue) {
		this.knQueue = knQueue;
	}

	
	
}
